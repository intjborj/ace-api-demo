package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.accounting.Fiscal
import com.hisd3.hismk2.domain.accounting.HeaderLedger
import com.hisd3.hismk2.domain.accounting.JournalType
import com.hisd3.hismk2.domain.accounting.Ledger
import com.hisd3.hismk2.domain.accounting.LedgerDocType
import com.hisd3.hismk2.domain.billing.BillingItem
import com.hisd3.hismk2.domain.bms.Room
import com.hisd3.hismk2.domain.cashiering.PaymentTracker
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import com.sun.org.apache.xpath.internal.operations.Bool
import groovy.transform.Canonical
import groovy.util.logging.Slf4j
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLContext
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.BooleanUtils
import org.apache.commons.lang3.ObjectUtils
import org.apache.commons.lang3.StringUtils
import org.hibernate.query.NativeQuery
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

import javax.persistence.EntityManager
import javax.transaction.Transactional
import java.sql.Timestamp
import java.text.DecimalFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Canonical
class Entry {
    ChartOfAccountGenerate journal
    BigDecimal value
}

@Canonical
class EntryFull {
    ChartOfAccountGenerate journal
    BigDecimal debit
    BigDecimal credit
}

class GeneralLedgerDto{

    String code
    String description
    String accountType
    String normalSide
    BigDecimal beginningDebit = 0.0
    BigDecimal beginningCredit= 0.0
    BigDecimal periodicDebit= 0.0
    BigDecimal periodicCredit= 0.0
    BigDecimal endingDebit= 0.0
    BigDecimal endingCredit= 0.0
}

@Canonical
class GeneralLedgerDtoContainer{
    List<GeneralLedgerDto> payload

    BigDecimal totalbeginningDebit = 0.0
    BigDecimal totalbeginningCredit= 0.0
    BigDecimal totalperiodicDebit= 0.0
    BigDecimal totalperiodicCredit= 0.0
    BigDecimal totalendingDebit= 0.0
    BigDecimal totalendingCredit= 0.0

}

@Canonical
class TransactionJournalDto{


    String referenceNo
    String entityName
    String journalType
    String otherDetail
    Long notApproved
    Long approved

}



@Service
@GraphQLApi
@Slf4j
@Transactional(rollbackOn = Exception.class)
class LedgerServices extends AbstractDaoService<HeaderLedger> {

    @Autowired
    FiscalServices fiscalServices

    @Autowired
    GeneratorService generatorService

    @Autowired
    SubAccountSetupService subAccountSetupService


    @Autowired
    JdbcTemplate jdbcTemplate

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate

    @Autowired
    EntityManager entityManager



    LedgerServices() {
        super(HeaderLedger.class)
    }


    @GraphQLMutation
    @javax.transaction.Transactional
    Boolean approveLedger(
            @GraphQLArgument(name = "ids") List<UUID> ids){

       def login =  SecurityUtils.currentLogin()
        ids.each {
            def header = findOne(it)
            header.approvedBy = login
            header.approvedDatetime = Instant.now()
            save(header)
        }

        true
    }



    List<HeaderLedger> getByPaymentTrackerParent(PaymentTracker paymentTracker){
        createQuery('Select hl from HeaderLedger hl where hl.reapplyPaymentTracker = :pt',
                [pt:paymentTracker.id]
        ).resultList

    }

    @GraphQLMutation
    GraphQLRetVal<Boolean> reverseHeader(@GraphQLArgument(name = "headerId") UUID headerId){
        def header = findOne(headerId)

        reverseEntries(header)

        new GraphQLRetVal<Boolean>(true,true,"OK")
    }

    @GraphQLMutation
    GraphQLRetVal<Boolean> adjustJV(@GraphQLArgument(name = "headerId") UUID headerId,
                                       @GraphQLArgument(name = "entries")  List<Map<String,Object>>  entries) {

        def header = findOne(headerId)


        def coa =  subAccountSetupService.getAllChartOfAccountGenerate("","","","","")
        List<EntryFull> entriesTarget = []

        for (Map<String,Object> entry in entries ){
            String code = entry.get("code")
            String description = entry.get("description")
            BigDecimal debit = new BigDecimal(entry.get("debit") as String)
            BigDecimal credit =  new BigDecimal(entry.get("credit") as String)
            def match =  coa.find {
                it.code == code
            }
            if(!match){
                return   new GraphQLRetVal<Boolean>(false,false,"${code}-${description} is not found in Chart of accounts")
            }

            entriesTarget << new EntryFull(match,debit,credit)
        }


        BigDecimal totalDebit = 0.0
        BigDecimal totalCredit = 0.0


        entriesTarget.each {

            totalDebit += it.debit
            totalCredit += it.credit

        }

        if(Math.abs(totalDebit - totalCredit) > 0.5 ){
           // throw new Exception("Entries not Balanced. Debit [${totalDebit.toPlainString()}] Credit [${totalCredit.toPlainString()}]")
           return new GraphQLRetVal<Boolean>(false,false,"Entries not Balanced. Debit [${totalDebit.toPlainString()}] Credit [${totalCredit.toPlainString()}]")
        }

        def headerLedger  =   createDraftHeaderLedgerFull(entriesTarget)


        headerLedger.ledger.each {
            it.header = header
            header.ledger.add(it)
        }

        save(header)

        new GraphQLRetVal<Boolean>(true,true,"OK")
    }

        @GraphQLMutation
    GraphQLRetVal<Boolean> addCustomJV(@GraphQLArgument(name = "header")  Map<String,Object>  header,
                                       @GraphQLArgument(name = "entries")  List<Map<String,Object>>  entries,
                                       @GraphQLArgument(name = "transactionDate")  Instant transactionDate) {


       def coa =  subAccountSetupService.getAllChartOfAccountGenerate("","","","","")

        // validate if code is on cOa

        List<EntryFull> entriesTarget = []
      for (Map<String,Object> entry in entries ){
          String code = entry.get("code")
          String description = entry.get("description")
          BigDecimal debit = new BigDecimal(entry.get("debit") as String)
          BigDecimal credit =  new BigDecimal(entry.get("credit") as String)
         def match =  coa.find {
             it.code == code
         }
          if(!match){
            return   new GraphQLRetVal<Boolean>(false,false,"${code}-${description} is not found in Chart of accounts")
          }

          entriesTarget << new EntryFull(match,debit,credit)
      }


        String invoiceSoaReference = header.get("invoiceSoaReference")
        String entityName = header.get("entityName")
        String particulars = header.get("particulars")

        String docType = header.get("docType")
        Boolean beginningBalance =(Boolean) header.get("beginningBalance",false)




        try {

            def headerLedger  =   createDraftHeaderLedgerFull(entriesTarget)

            headerLedger.entityName = entityName
            validateEntries(headerLedger)

            persistHeaderLedger(headerLedger,
                    StringUtils.upperCase(invoiceSoaReference),
                    StringUtils.upperCase(entityName),
                    StringUtils.upperCase(particulars),
                    LedgerDocType.valueOf(docType),
                    JournalType.GENERAL,
                    transactionDate?:Instant.now(),
                    [:],
                    beginningBalance,
                    true
            )
        }catch(Exception e){
            e.printStackTrace()
          return  new GraphQLRetVal<Boolean>(false,false,e.message)
        }





        new GraphQLRetVal<Boolean>(true,true,"OK")
    }

    @GraphQLMutation
    GraphQLRetVal<Boolean> addManualJVDynamic(@GraphQLArgument(name = "header")  Map<String,Object>  header,
                                       @GraphQLArgument(name = "entries")  List<Map<String,Object>>  entries,
                                              @GraphQLArgument(name = "docType")  LedgerDocType docType,
                                              @GraphQLArgument(name = "journalType")  JournalType journalType,
                                              @GraphQLArgument(name = "ledgerDate")  Instant ledgerDate,
                                              @GraphQLArgument(name = "details")  Map<String,String> details) {


        def coa =  subAccountSetupService.getAllChartOfAccountGenerate("","","","","")
        // validate if code is on cOa

        List<EntryFull> entriesTarget = []
        for (Map<String,Object> entry in entries ){
            String code = entry.get("code")
            String description = entry.get("description")
            BigDecimal debit = new BigDecimal(entry.get("debit") as String)
            BigDecimal credit =  new BigDecimal(entry.get("credit") as String)
            def match =  coa.find {
                it.code == code
            }
            if(!match){
                return   new GraphQLRetVal<Boolean>(false,false,"${code}-${description} is not found in Chart of accounts")
            }

            entriesTarget << new EntryFull(match,debit,credit)
        }


        String invoiceSoaReference = header.get("invoiceSoaReference")
        String entityName = header.get("entityName")
        String particulars = header.get("particulars")



        def id = null
        try {

            def headerLedger  =   createDraftHeaderLedgerFull(entriesTarget)

            headerLedger.entityName = entityName
            validateEntries(headerLedger)

            def pHeader = persistHeaderLedger(headerLedger,
                    StringUtils.upperCase(invoiceSoaReference),
                    StringUtils.upperCase(entityName),
                    StringUtils.upperCase(particulars),
                    docType,
                    journalType,
                    ledgerDate,
                    details
            )
            id = pHeader.id
        }catch(Exception e){
            e.printStackTrace()
            return  new GraphQLRetVal<Boolean>(false,false,e.message, id)
        }

        new GraphQLRetVal<Boolean>(true,true,"OK", id)

    }



    @GraphQLQuery(name = "ledgerValue" )
    BigDecimal ledgerValue(@GraphQLContext HeaderLedger headerLedger) {

        BigDecimal value = 0.0

        headerLedger.ledger.each {
            value += it.debit
        }

        if(headerLedger.reversal)
            return value.abs() * -1

        return value

    }




    @GraphQLQuery(name = "transactionJournal2", description = "Transaction Journals")
    Page<TransactionJournalDto> transactionJournal2(
            @GraphQLArgument(name = "journalType")  JournalType  journalType,
            @GraphQLArgument(name = "startDateTime")  Instant  startDateTime,
            @GraphQLArgument(name = "endDateTime")  Instant  endDateTime,
            @GraphQLArgument(name = "filter")  String  filter, // JV No. or particulars
            @GraphQLArgument(name = "page")  Integer  page,
            @GraphQLArgument(name = "size")  Integer  size,
            @GraphQLArgument(name = "beginningBalance")  Boolean  beginningBalance,
            @GraphQLArgument(name = "showAll")  Boolean  showAll
    ){

        String showFilter  = ""

        if(!showAll){
            showFilter = """

    where   transactions.not_approved > 0
       
 """

        }


        List<TransactionJournalDto> records = []

        def recordsRaw= namedParameterJdbcTemplate.queryForList(
                """
          
     
SELECT entity_name,invoice_soa_reference,journal_type,shiftno,not_approved,approved from (
    
            select hl.entity_name , hl.invoice_soa_reference, hl.journal_type  ,ps.shiftno,
            
             (select count(*) from accounting.header_ledger_entityname_and_reference_not_approved hlearna
               where (hlearna.entity_name) = (hl.entity_name) and (hlearna.invoice_soa_reference) = (hl.invoice_soa_reference)) as not_approved,
              
           
              (select count(*) from accounting.header_ledger_entityname_and_reference_approved hlearna
               where (hlearna.entity_name) = (hl.entity_name) and (hlearna.invoice_soa_reference) = (hl.invoice_soa_reference)) as approved
            
             from accounting.header_ledger hl 
                    left join  (
                         select  distinct pt.receipt_type, pt.ornumber, pt.shiftno, pt.ledger_header from cashiering.payment_tracker_shift pt
                       ) ps  on ps.ledger_header=hl.id
             where 
                    (case when (:beginningBalance) then  hl.beginning_balance else  true end  ) = true  and 
                    ( 
                        hl.entity_name ilike concat('%',:filter,'%') or 
                        hl.invoice_soa_reference ilike concat('%',:filter,'%') 
                      
                    )  and 
                    (case when :journalType='ALL' then true else  (hl.journal_type=:journalType)   end)  = true  and 
                    hl.transaction_date >= :startDateTime and hl.transaction_date <= :endDateTime and (hl.reversal is null or hl.reversal = false)
    
                     group by 
                    hl.entity_name , hl.invoice_soa_reference, hl.journal_type  ,ps.shiftno
                     order by 
                                 min(hl.transaction_date) desc
                     limit :size  offset :offset
) as transactions
 
       ${showFilter}
    
    
    

 """
                ,[
                beginningBalance:beginningBalance,
                filter:filter,
                journalType:journalType.name(),
                size:size,
                offset: size * page,
                startDateTime: Timestamp.from(startDateTime),
                endDateTime: Timestamp.from(endDateTime)
        ])

        recordsRaw.each {

            records << new TransactionJournalDto(
                    StringUtils.upperCase( it.get("invoice_soa_reference","") as String),
                    StringUtils.upperCase( it.get("entity_name","") as String),
                    StringUtils.upperCase( it.get("journal_type","") as String),
                    StringUtils.upperCase( it.get("shiftno","") as String),
                    it.get("not_approved",0) as Long,
                    it.get("approved",0) as Long
            )


        }

        def count =     namedParameterJdbcTemplate.queryForObject("""

  select count(*) from
           (
          
                     
                SELECT entity_name,invoice_soa_reference,journal_type,shiftno,not_approved,approved from (
                    
                            select hl.entity_name , hl.invoice_soa_reference, hl.journal_type  ,ps.shiftno,
                            
                             (select count(*) from accounting.header_ledger_entityname_and_reference_not_approved hlearna
                               where (hlearna.entity_name) = (hl.entity_name) and (hlearna.invoice_soa_reference) = (hl.invoice_soa_reference)) as not_approved,
                              
                           
                              (select count(*) from accounting.header_ledger_entityname_and_reference_approved hlearna
                               where (hlearna.entity_name) = (hl.entity_name) and (hlearna.invoice_soa_reference) = (hl.invoice_soa_reference)) as approved
                            
                             from accounting.header_ledger hl 
                                    left join  (
                                         select  distinct pt.receipt_type, pt.ornumber, pt.shiftno, pt.ledger_header from cashiering.payment_tracker_shift pt
                                       ) ps  on ps.ledger_header=hl.id
                             where 
                                    (case when (:beginningBalance) then  hl.beginning_balance else  true end  ) = true  and 
                                    ( 
                                        hl.entity_name ilike concat('%',:filter,'%') or 
                                        hl.invoice_soa_reference ilike concat('%',:filter,'%') 
                                      
                                    )  and 
                                    (case when :journalType='ALL' then true else  (hl.journal_type=:journalType)   end)  = true  and 
                                    hl.transaction_date >= :startDateTime and hl.transaction_date <= :endDateTime and (hl.reversal is null or hl.reversal = false)
                    
                                     group by 
                                    hl.entity_name , hl.invoice_soa_reference, hl.journal_type  ,ps.shiftno
                                    
                ) as transactions
                  ${showFilter}
           )     sub         
           


 """,
                [
                        beginningBalance:beginningBalance,
                        filter:filter,
                        journalType:journalType.name(),
                        startDateTime: Timestamp.from(startDateTime),
                        endDateTime: Timestamp.from(endDateTime)
                ], Long.class)


        new PageImpl<TransactionJournalDto>(records, PageRequest.of(page, size),
                count)
    }

    @GraphQLQuery(name = "transactionJournalWithPartition", description = "Transaction Journals")
    Page<TransactionJournalDto> transactionJournalWithPartition(
            @GraphQLArgument(name = "journalType")  JournalType  journalType,
            @GraphQLArgument(name = "startDate")  String  startDate,
            @GraphQLArgument(name = "endDate")  String  endDate,
            @GraphQLArgument(name = "filter")  String  filter, // JV No. or particulars
            @GraphQLArgument(name = "page")  Integer  page,
            @GraphQLArgument(name = "size")  Integer  size,
            @GraphQLArgument(name = "beginningBalance")  Boolean  beginningBalance,
            @GraphQLArgument(name = "showAll")  Boolean  showAll
    ){

        String queryString = """
                select
                    hl.entity_name,
                    hl.invoice_soa_reference,
                    hl.journal_type,
                    '' as "shiftno",
                    (
                        SELECT COUNT(*)
                        FROM accounting.header_ledger_entityname_and_reference_not_approved hlearna
                        WHERE hlearna.entity_name = hl.entity_name
                        AND 
                        CASE 
                            WHEN :journalType != 'ALL' 
                            THEN hlearna.journal_type = hl.journal_type
                            ELSE true
                        END
                        AND hlearna.invoice_soa_reference = hl.invoice_soa_reference
                        AND hlearna.transaction_date_only >= cast(:startDate as date) AND hlearna.transaction_date_only <= cast(:endDate as date)
                    ) AS not_approved,
                    (
                        SELECT COUNT(*)
                        FROM accounting.header_ledger_entityname_and_reference_approved hlearna
                        WHERE hlearna.entity_name = hl.entity_name
                        AND 
                        CASE 
                            WHEN :journalType != 'ALL' 
                            THEN hlearna.journal_type = hl.journal_type
                            ELSE true
                        END
                        AND hlearna.invoice_soa_reference = hl.invoice_soa_reference
                        AND hlearna.transaction_date_only >= cast(:startDate as date) AND hlearna.transaction_date_only <= cast(:endDate as date)
                    ) AS approved
                  FROM accounting.header_ledger hl
                  WHERE
                    (
                      CASE WHEN (:beginningBalance) THEN hl.beginning_balance ELSE true END
                    ) = true
                    AND (
                      hl.entity_name ILIKE concat('%', :filter, '%')
                      OR hl.invoice_soa_reference ILIKE concat('%', :filter, '%')
                    )
                    AND (
                      CASE WHEN :journalType = 'ALL' THEN true ELSE hl.journal_type = :journalType END
                    ) = true
                    AND (hl.reversal IS NULL OR hl.reversal = false)
                    and hl.transaction_date_only >= cast(:startDate as date) and hl.transaction_date_only <= cast(:endDate as date) 
                    and (hl.reversal is null or hl.reversal = false)
                  GROUP by
                    hl.entity_name,
                    hl.invoice_soa_reference,
                    hl.journal_type
                  ORDER BY
                    min(hl.transaction_date) DESC"""

        if(journalType == JournalType.RECEIPTS)
            queryString = """
                select
                    hl.entity_name,
                    hl.invoice_soa_reference,
                    hl.journal_type,
                    ps.shiftno,
                    (
                        SELECT COUNT(*)
                        FROM accounting.header_ledger_entityname_and_reference_not_approved hlearna
                        WHERE hlearna.entity_name = hl.entity_name
                        AND 
                        CASE 
                            WHEN :journalType != 'ALL' 
                            THEN hlearna.journal_type = hl.journal_type
                            ELSE true
                        END
                        AND hlearna.invoice_soa_reference = hl.invoice_soa_reference
                        AND hlearna.transaction_date_only >= cast(:startDate as date) AND hlearna.transaction_date_only <= cast(:endDate as date)
                    ) AS not_approved,
                    (
                        SELECT COUNT(*)
                        FROM accounting.header_ledger_entityname_and_reference_approved hlearna
                        WHERE hlearna.entity_name = hl.entity_name
                        AND 
                        CASE 
                            WHEN :journalType != 'ALL' 
                            THEN hlearna.journal_type = hl.journal_type
                            ELSE true
                        END
                        AND hlearna.invoice_soa_reference = hl.invoice_soa_reference
                        AND hlearna.transaction_date_only >= cast(:startDate as date) AND hlearna.transaction_date_only <= cast(:endDate as date)
                    ) AS approved
                  FROM accounting.header_ledger hl
                 LEFT JOIN (
                    SELECT DISTINCT
                    pt.receipt_type,
                    pt.ornumber,
                    pt.shiftno,
                    pt.ledger_header
                    FROM cashiering.payment_tracker_shift pt
                ) ps ON ps.ledger_header = hl.id
                  WHERE
                    (
                      CASE WHEN (:beginningBalance) THEN hl.beginning_balance ELSE true END
                    ) = true
                    AND (
                      hl.entity_name ILIKE concat('%', :filter, '%')
                      OR hl.invoice_soa_reference ILIKE concat('%', :filter, '%')
                    )
                    AND (
                      CASE WHEN :journalType = 'ALL' THEN true ELSE hl.journal_type = :journalType END
                    ) = true
                    AND (hl.reversal IS NULL OR hl.reversal = false)
                    and hl.transaction_date_only >= cast(:startDate as date) and hl.transaction_date_only <= cast(:endDate as date) 
                    and (hl.reversal is null or hl.reversal = false)
                  GROUP by
                    ps.shiftno,
                    hl.entity_name,
                    hl.invoice_soa_reference,
                    hl.journal_type
                  ORDER BY
                    min(hl.transaction_date) DESC
                """

        String showFilter  = ""

        if(!showAll){
            showFilter = """
                where   transactions.not_approved > 0
             """
        }

        List<TransactionJournalDto> records = []

        def recordsRaw= namedParameterJdbcTemplate.queryForList(
    """ 
        Select
         *
        FROM (
        ${queryString}
        LIMIT :size OFFSET :offset
          ) AS transactions
        ${showFilter}
        """
                ,[
                beginningBalance:beginningBalance,
                filter:filter,
                journalType:journalType.name(),
                size:size,
                offset: size * page,
                startDate: startDate,
                endDate: endDate
        ])

        recordsRaw.each {

            records << new TransactionJournalDto(
                    StringUtils.upperCase( it.get("invoice_soa_reference","") as String),
                    StringUtils.upperCase( it.get("entity_name","") as String),
                    StringUtils.upperCase( it.get("journal_type","") as String),
                    StringUtils.upperCase( it.get("shiftno","") as String),
                    it.get("not_approved",0) as Long,
                    it.get("approved",0) as Long
            )


        }

        def count =     namedParameterJdbcTemplate.queryForObject("""
        select 
            count(*) 
        from
        (
            Select
                  entity_name,
                  invoice_soa_reference,
                  journal_type,
                  "shiftno",
                  not_approved,
                  approved
            from
            ( ${queryString} ) AS transactions
            ${showFilter}
        ) sub         
        """,
                [
                        beginningBalance:beginningBalance,
                        filter:filter,
                        journalType:journalType.name(),
                        startDate: startDate,
                        endDate: endDate
                ], Long.class)


        new PageImpl<TransactionJournalDto>(records, PageRequest.of(page, size),
                count)
    }

    @GraphQLQuery(name = "transactionJournalReferenceEntity", description = "Transaction Journals")
    Page<HeaderLedger> transactionJournalReferenceEntity(
            @GraphQLArgument(name = "referenceNo")  String  referenceNo,
            @GraphQLArgument(name = "entityName")  String  entityName,
            @GraphQLArgument(name = "startDateTime")  Instant  startDateTime,
            @GraphQLArgument(name = "endDateTime")  Instant  endDateTime,
            @GraphQLArgument(name = "filter")  String  filter, // JV No. or particulars
            @GraphQLArgument(name = "page")  Integer  page,
            @GraphQLArgument(name = "size")  Integer  size,
            @GraphQLArgument(name = "beginningBalance")  Boolean  beginningBalance,
            @GraphQLArgument(name = "fiscalId")  UUID fiscalId // override from GL
    ){


         if(fiscalId){

             def fiscal = fiscalServices.findOne(fiscalId)

             int fiscalYear= fiscal.fromDate.getYear()
             startDateTime = startDateTime.atZone(ZoneId.systemDefault()).withYear(fiscalYear).toInstant()
             endDateTime = endDateTime.atZone(ZoneId.systemDefault()).withYear(fiscalYear).toInstant()

         }


        if(beginningBalance){

            getPageable("""from HeaderLedger hl where hl.transactionDate >= :startDateTime and hl.transactionDate <= :endDateTime 
  and (
                 lower(hl.docnum) like lower(concat('%',:filter,'%'))  or  
                 lower(hl.particulars) like lower(concat('%',:filter,'%'))
              )
      and
      hl.beginningBalance = true   and lower(hl.entityName)=lower(:entityName) and lower(hl.invoiceSoaReference)=lower(:referenceNo)
 order by hl.transactionDate DESC
""",
                    """ Select count(hl) 
from HeaderLedger hl where hl.transactionDate >= :startDateTime and hl.transactionDate <= :endDateTime   
  and (
            lower(hl.docnum) like lower(concat('%',:filter,'%'))  or  
            lower(hl.particulars) like lower(concat('%',:filter,'%'))
            )
   and
   hl.beginningBalance = true  and lower(hl.entityName)=lower(:entityName) and lower(hl.invoiceSoaReference)=lower(:referenceNo)
""",page,size,[
                    startDateTime:startDateTime,
                    endDateTime:endDateTime,
                    filter:filter,
                   // fiscal:fiscalServices.findFiscalActive(), not required in transaction journal only on Reports and GL
                    referenceNo:referenceNo,
                    entityName:entityName

            ])


        }
        else {
           def values=      getPageable("""from HeaderLedger hl where 
          hl.transactionDate >= :startDateTime and hl.transactionDate <= :endDateTime   and 
          ( 
            lower(hl.docnum) like lower(concat('%',:filter,'%'))  or  
            lower(hl.particulars) like lower(concat('%',:filter,'%'))  
          ) 
             and lower(hl.entityName)=lower(:entityName) and lower(hl.invoiceSoaReference)=lower(:referenceNo)
order by hl.transactionDate DESC
""",
                        """ Select count(hl) 
from HeaderLedger hl where 
      hl.transactionDate >= :startDateTime and hl.transactionDate <= :endDateTime   and
       ( 
        lower(hl.docnum) like lower(concat('%',:filter,'%'))  or  
        lower(hl.particulars) like lower(concat('%',:filter,'%')) 
        )
   and lower(hl.entityName)=lower(:entityName) and lower(hl.invoiceSoaReference)=lower(:referenceNo)
""",page,size,[
               startDateTime:startDateTime,
               endDateTime:endDateTime,
               filter:filter,
             //  fiscal:fiscalServices.findFiscalActive(), not required in transaction journal only on Reports and GL
               referenceNo:referenceNo,
               entityName:entityName
                ])

            return values
            }

 }


    @GraphQLQuery(name = "transactionJournalReferenceEntityWithPartition", description = "Transaction Journals")
    Page<HeaderLedger> transactionJournalReferenceEntityWithPartition(
            @GraphQLArgument(name = "referenceNo") String referenceNo,
            @GraphQLArgument(name = "entityName") String entityName,
            @GraphQLArgument(name = "startDate") String startDate,
            @GraphQLArgument(name = "endDate") String endDate,
            @GraphQLArgument(name = "filter") String filter, // JV No. or particulars
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "size") Integer size,
            @GraphQLArgument(name = "beginningBalance") Boolean beginningBalance,
            @GraphQLArgument(name = "fiscalId") UUID fiscalId // override from GL
    ) {

        if (fiscalId) {

            def fiscal = fiscalServices.findOne(fiscalId)
            int fiscalYear = fiscal.fromDate.getYear()
//            startDateTime = startDateTime.atZone(ZoneId.systemDefault()).withYear(fiscalYear).toInstant()
//            endDateTime = endDateTime.atZone(ZoneId.systemDefault()).withYear(fiscalYear).toInstant()

        }

        def dateFormat = "yyyy-MM-dd"
        // Create a DateTimeFormatter with the desired format
        def formatter = DateTimeFormatter.ofPattern(dateFormat)
        // Parse the String and convert it to LocalDate

        LocalDate startDateLocal = LocalDate.parse(startDate, formatter)
        LocalDate endDateLocal = LocalDate.parse(endDate, formatter)

        if (beginningBalance) {

            getPageable(
                    """from HeaderLedger hl where hl.transactionDateOnly >= :startDateLocal and hl.transactionDateOnly <= :endDateLocal 
            and (
                lower(hl.docnum) like lower(concat('%',:filter,'%'))  or  
                lower(hl.particulars) like lower(concat('%',:filter,'%'))
            )
            and
            hl.beginningBalance = true   and lower(hl.entityName)=lower(:entityName) and lower(hl.invoiceSoaReference)=lower(:referenceNo)
            order by hl.transactionDate DESC
            """,
                    """ Select count(hl) 
            from HeaderLedger hl where hl.transactionDateOnly >= :startDateLocal and hl.transactionDateOnly <= :endDateLocal   
            and (
                lower(hl.docnum) like lower(concat('%',:filter,'%'))  or  
                lower(hl.particulars) like lower(concat('%',:filter,'%'))
            )
            and
            hl.beginningBalance = true  and lower(hl.entityName)=lower(:entityName) and lower(hl.invoiceSoaReference)=lower(:referenceNo)
            """,
                    page,
                    size,
                    [
                            startDateLocal: startDateLocal,
                            endDateLocal: endDateLocal,
                            filter: filter,
                            // fiscal:fiscalServices.findFiscalActive(), not required in transaction journal only on Reports and GL
                            referenceNo: referenceNo,
                            entityName: entityName
                    ]
            )

        } else {
            def values = getPageable(
                    """from HeaderLedger hl where 
            hl.transactionDateOnly >= :startDateLocal and hl.transactionDateOnly <= :endDateLocal   and 
            ( 
                lower(hl.docnum) like lower(concat('%',:filter,'%'))  or  
                lower(hl.particulars) like lower(concat('%',:filter,'%'))  
            ) 
            and lower(hl.entityName)=lower(:entityName) and lower(hl.invoiceSoaReference)=lower(:referenceNo)
            order by hl.transactionDate DESC
            """,
                    """ Select count(hl) 
            from HeaderLedger hl where 
            hl.transactionDateOnly >= :startDateLocal and hl.transactionDateOnly <= :endDateLocal   and
            ( 
                lower(hl.docnum) like lower(concat('%',:filter,'%'))  or  
                lower(hl.particulars) like lower(concat('%',:filter,'%')) 
            )
            and lower(hl.entityName)=lower(:entityName) and lower(hl.invoiceSoaReference)=lower(:referenceNo)
            """,
                    page,
                    size,
                    [
                            startDateLocal: startDateLocal,
                            endDateLocal: endDateLocal,
                            filter: filter,
                            // fiscal:fiscalServices.findFiscalActive(), not required in transaction journal only on Reports and GL
                            referenceNo: referenceNo,
                            entityName: entityName
                    ]
            )

            return values
        }
    }





/*    @GraphQLQuery(name = "transactionJournal", description = "Transaction Journals")
    Page<HeaderLedger> transactionJournal(
            @GraphQLArgument(name = "journalType")  JournalType  journalType,
            @GraphQLArgument(name = "startDateTime")  Instant  startDateTime,
            @GraphQLArgument(name = "endDateTime")  Instant  endDateTime,
            @GraphQLArgument(name = "filter")  String  filter, // JV No. or particulars
            @GraphQLArgument(name = "page")  Integer  page,
            @GraphQLArgument(name = "size")  Integer  size,
            @GraphQLArgument(name = "beginningBalance")  Boolean  beginningBalance
            ){



        if(beginningBalance){

            getPageable("""from HeaderLedger hl where hl.transactionDate >= :startDateTime and hl.transactionDate <= :endDateTime 
 and hl.fiscal = :fiscal and (lower(hl.entityName) like lower(concat('%',:filter,'%')) or lower(hl.docnum) like lower(concat('%',:filter,'%'))  or  lower(hl.particulars) like lower(concat('%',:filter,'%'))
 or  lower(hl.invoiceSoaReference) like lower(concat('%',:filter,'%'))
   )
   and
   hl.beginningBalance = true
 order by hl.transactionDate DESC
""",""" Select count(hl) 
from HeaderLedger hl where hl.transactionDate >= :startDateTime and hl.transactionDate <= :endDateTime   
and hl.fiscal = :fiscal and (lower(hl.entityName) like lower(concat('%',:filter,'%')) or lower(hl.docnum) like lower(concat('%',:filter,'%'))  or  lower(hl.particulars) like lower(concat('%',:filter,'%'))
or  lower(hl.invoiceSoaReference) like lower(concat('%',:filter,'%'))
  )
  and
   hl.beginningBalance = true
""",page,size,[
                    startDateTime:startDateTime,
                    endDateTime:endDateTime,
                    filter:filter,
                    fiscal:fiscalServices.findFiscalActive()
            ])


        }
        else {
            if(journalType == JournalType.ALL){

                getPageable("""from HeaderLedger hl where hl.transactionDate >= :startDateTime and hl.transactionDate <= :endDateTime 
 and hl.fiscal = :fiscal and (lower(hl.entityName) like lower(concat('%',:filter,'%')) or lower(hl.docnum) like lower(concat('%',:filter,'%'))  or  lower(hl.particulars) like lower(concat('%',:filter,'%'))
 or  lower(hl.invoiceSoaReference) like lower(concat('%',:filter,'%'))
   )
 order by hl.transactionDate DESC
""",""" Select count(hl) 
from HeaderLedger hl where hl.transactionDate >= :startDateTime and hl.transactionDate <= :endDateTime   
and hl.fiscal = :fiscal and (lower(hl.entityName) like lower(concat('%',:filter,'%')) or lower(hl.docnum) like lower(concat('%',:filter,'%'))  or  lower(hl.particulars) like lower(concat('%',:filter,'%'))
or  lower(hl.invoiceSoaReference) like lower(concat('%',:filter,'%'))
  )

""",page,size,[
                        startDateTime:startDateTime,
                        endDateTime:endDateTime,
                        filter:filter,
                        fiscal:fiscalServices.findFiscalActive()
                ])
            }
            else {
                getPageable("""from HeaderLedger hl where hl.transactionDate >= :startDateTime and hl.transactionDate <= :endDateTime and
hl.journalType = :journalType and hl.fiscal = :fiscal and (lower(hl.entityName) like lower(concat('%',:filter,'%')) or lower(hl.docnum) like lower(concat('%',:filter,'%'))  or  lower(hl.particulars) like lower(concat('%',:filter,'%')) 
or  lower(hl.invoiceSoaReference) like lower(concat('%',:filter,'%'))
 )
order by hl.transactionDate DESC
""",""" Select count(hl) 
from HeaderLedger hl where hl.transactionDate >= :startDateTime and hl.transactionDate <= :endDateTime and
hl.journalType = :journalType and hl.fiscal = :fiscal and (lower(hl.entityName) like lower(concat('%',:filter,'%')) or lower(hl.docnum) like lower(concat('%',:filter,'%'))  or  lower(hl.particulars) like lower(concat('%',:filter,'%'))
or  lower(hl.invoiceSoaReference) like lower(concat('%',:filter,'%'))
  )

""",page,size,[journalType:journalType,
               startDateTime:startDateTime,
               endDateTime:endDateTime,
               filter:filter,
               fiscal:fiscalServices.findFiscalActive()
                ])
            }
        }




    }*/


    HeaderLedger createDraftHeaderLedger(List<Entry> entries){
        HeaderLedger header = new HeaderLedger()
        entries.each { entry->

            // this will only accept from getAllChartOfAccountGenerate

            if(!entry.journal.fromGenerator)
                throw  new Exception("Method: createDraftHeaderLedger will only accept journal record from getAllChartOfAccountGenerate")


            Ledger ledger = new Ledger()
            ledger.journalAccount = entry.journal
            ledger.debit = 0.0
            ledger.credit = 0.0
            if(entry.journal.motherAccount.normalSide == "DEBIT"){
                if(entry.value >=0)
                    ledger.debit = entry.value.abs()
                else
                    ledger.credit = entry.value.abs()
            }  else {

                if(entry.value >=0)
                    ledger.credit = entry.value.abs()
                else
                    ledger.debit = entry.value.abs()
            }

            ledger.header = header
            header.ledger.add(ledger)
        }
        header
    }


    HeaderLedger createDraftHeaderLedgerFull(List<EntryFull> entries){
        HeaderLedger header = new HeaderLedger()
        entries.each { entry->

            // this will only accept from getAllChartOfAccountGenerate

            if(!entry.journal.fromGenerator)
                throw  new Exception("Method: createDraftHeaderLedger will only accept journal record from getAllChartOfAccountGenerate")
            Ledger ledger = new Ledger()
            ledger.journalAccount = entry.journal
            ledger.debit = entry.debit
            ledger.credit = entry.credit
            ledger.header = header
            header.ledger.add(ledger)
        }
        header
    }


    HeaderLedger postJournalEntries(String refNo,
                                    String entity,
                                    String particulars,
                                    LedgerDocType ledgerDocType,
                                    JournalType journalType,
                                    Instant transactionDatetime,
                                    Map<String,String> details,
                                    List<Entry> entries
    ){

        HeaderLedger header = createDraftHeaderLedger(entries)
        persistHeaderLedger(header,refNo,entity,particulars,ledgerDocType,journalType,transactionDatetime,details)
    }

// ====================end of manual====================
    boolean validateEntries(HeaderLedger headerLedger){

        // Validate Journal References

        BigDecimal totalDebit = 0.0
        BigDecimal totalCredit = 0.0
        headerLedger.ledger.each {

            if(StringUtils.isNotBlank(it.journalAccount?.subAccount?.domain)){
                // validate Code Subaccount
                if(   StringUtils.isBlank(it.journalAccount.subAccount.code) ||
                      StringUtils.equalsIgnoreCase(it.journalAccount.subAccount.code,"0000") ||
                      StringUtils.equalsIgnoreCase(it.journalAccount.subAccount.code,"####")
                ){
                    throw new Exception("Invalid SubAccount [${it.journalAccount.subAccount.code}] for domain [${it.journalAccount.subAccount.domain}]")
                }
            }


            if(StringUtils.isNotBlank(it.journalAccount?.subSubAccount?.domain)){
                // validate Code Subaccount
                if(   StringUtils.isBlank(it.journalAccount.subSubAccount.code) ||
                        StringUtils.equalsIgnoreCase(it.journalAccount.subSubAccount.code,"0000") ||
                        StringUtils.equalsIgnoreCase(it.journalAccount.subSubAccount.code,"####")
                ){
                    throw new Exception("Invalid SubSubAccount [${it.journalAccount.subSubAccount.code}] for domain [${it.journalAccount.subSubAccount.domain}]")
                }
            }

            totalDebit += it.debit
            totalCredit += it.credit
        }





        if(StringUtils.isBlank(headerLedger.entityName)){
            throw new Exception("Entity name is blank")
        }


        // validate balance entries
        if(Math.abs(totalDebit - totalCredit) > 0.5 ){

            headerLedger.ledger.each {
                log.info( String.format("%-100.100s  Debit:%s     Credit: %s", it.journalAccount.description,
                        new DecimalFormat("#,##0.00").format(it.debit),
                        new DecimalFormat("#,##0.00").format(it.credit),
                ))
            }

            throw new Exception("Entries not Balanced. Debit [${totalDebit.toPlainString()}] Credit [${totalCredit.toPlainString()}]")
        }

        true
    }


    boolean  validateTransactionDateFromFiscal(Fiscal fiscal,Instant transactionDate){

        int  month = transactionDate.atZone(ZoneId.systemDefault()).getMonth().value
        switch (month){
            case 1:
                return !BooleanUtils.isTrue(fiscal.lockJanuary)
                break
            case 2:
                return !BooleanUtils.isTrue(fiscal.lockFebruary)
                break
            case 3:
                return !BooleanUtils.isTrue(fiscal.lockMarch)
                break
            case 4:
                return !BooleanUtils.isTrue(fiscal.lockApril)
                break
            case 5:
                return !BooleanUtils.isTrue(fiscal.lockMay)
                break
            case 6:
                return !BooleanUtils.isTrue(fiscal.lockJune)
                break
            case 7:
                return !BooleanUtils.isTrue(fiscal.lockJuly)
                break
            case 8:
                return !BooleanUtils.isTrue(fiscal.lockAugust)
                break
            case 9:
                return !BooleanUtils.isTrue(fiscal.lockSeptember)
                break
            case 10:
                return !BooleanUtils.isTrue(fiscal.lockOctober)
                break
            case 11:
                return !BooleanUtils.isTrue(fiscal.lockNovember)
                break
            case 12:
                return !BooleanUtils.isTrue(fiscal.lockDecember)
                break
        }
        false
    }

    HeaderLedger persistHeaderLedger(   HeaderLedger headerLedger,
                                        String refNo,
                                        String entity,
                                         String particulars,
                                        LedgerDocType ledgerDocType,
                                        JournalType journalType,
                                        Instant transactionDatetime,
                                        Map<String,String> details,
                                        Boolean begBalance=false,
                                        Boolean custom=false){




        //todo: this should be auto detect with accounting year the entries belong to...
        // and it is automatic
        // if not match will return exception

        headerLedger.fiscal = fiscalServices.findFiscalForTransactionDate(transactionDatetime)


        if(!headerLedger.fiscal)
            throw new Exception("Fiscal Year not setup or not found to this date of Transaction")

        // validate if Month of Transaction Date is locked


        if(!validateTransactionDateFromFiscal(headerLedger.fiscal,transactionDatetime))
            throw new Exception("Cannot insert entries. Target Month was already locked")



        headerLedger.entityName = entity
        headerLedger.invoiceSoaReference = refNo
        headerLedger.particulars = particulars
        headerLedger.docType = ledgerDocType
        headerLedger.journalType = journalType
        headerLedger.transactionDate = transactionDatetime
        headerLedger.beginningBalance = begBalance
        headerLedger.custom = custom
        headerLedger.docnum = generatorService.getNextValue( GeneratorType.JOURNAL_VOUCHER){
             StringUtils.leftPad(it.toString(),5,"0")
        }

        if(details)
            details.each {k,v->
                headerLedger.details[k] = v
            }


        validateEntries(headerLedger)

        save(headerLedger)
    }




    // Utilities


    HeaderLedger reverseEntries(HeaderLedger source){

        HeaderLedger reversal = new HeaderLedger()
        reversal.reversal = true
        reversal.invoiceSoaReference = source.invoiceSoaReference
        reversal.fiscal = source.fiscal
        reversal.particulars = source.particulars
        reversal.transactionDate = Instant.now()
        reversal.particulars = "[REVERSAL (${source.docType}-${source.docnum})]-${source.particulars}"
        reversal.entityName = source.entityName
        reversal.docType = source.docType
        reversal.journalType = source.journalType
        reversal.custom = source.custom
        reversal.parentLedger = source.parentLedger
        reversal.beginningBalance = source.beginningBalance


        reversal.docnum = generatorService.getNextValue( GeneratorType.JOURNAL_VOUCHER){
            StringUtils.leftPad(it.toString(),5,"0")
        }

        source.details.each { k,v ->
            reversal.details[k] = v
        }

        // reverse ledger

        source.ledger.each {

            Ledger l = new Ledger()
            l.header = reversal

            l.particulars = it.particulars
            l.journalAccount = it.journalAccount
            l.debit = it.credit
            l.credit = it.debit

            reversal.ledger << l
        }

       save(reversal)

    }

    HeaderLedger reverseEntriesCustom(HeaderLedger source, Instant date){

        HeaderLedger reversal = new HeaderLedger()
        reversal.reversal = true
        reversal.invoiceSoaReference = source.invoiceSoaReference
        reversal.fiscal = source.fiscal
        reversal.particulars = source.particulars
        reversal.transactionDate = date
        reversal.particulars = "[REVERSAL]-${source.particulars}"
        reversal.entityName = source.entityName
        reversal.docType = source.docType
        reversal.journalType = source.journalType
        reversal.custom = source.custom
        reversal.parentLedger = source.parentLedger
        reversal.beginningBalance = source.beginningBalance


        reversal.docnum = generatorService.getNextValue( GeneratorType.JOURNAL_VOUCHER){
            StringUtils.leftPad(it.toString(),5,"0")
        }

        source.details.each { k,v ->
            reversal.details[k] = v
        }

        // reverse ledger

        source.ledger.each {

            Ledger l = new Ledger()
            l.header = reversal

            l.particulars = it.particulars
            l.journalAccount = it.journalAccount
            l.debit = it.credit
            l.credit = it.debit

            reversal.ledger << l
        }

        save(reversal)

    }





    @GraphQLQuery(name = "getGeneralLedger")
    GeneralLedgerDtoContainer getGeneralLedger(
            @GraphQLArgument(name = "fiscalId")    UUID fiscalId,
            @GraphQLArgument(name = "accountType")    String accountType,
            @GraphQLArgument(name = "motherAccountCode")  String motherAccountCode,
            @GraphQLArgument(name = "description") String description,
            @GraphQLArgument(name = "subaccountType") String subaccountType,
            @GraphQLArgument(name = "department") String department,
            @GraphQLArgument(name = "monthNo") Integer monthNo
    ) {


        Fiscal fiscal =  fiscalServices.findOne(fiscalId)



        LocalDateTime currentTargetStart = LocalDateTime.of(fiscal.toDate.getYear() ,monthNo,1,0,0,0,0)
        LocalDateTime currentTargetEnd = currentTargetStart.plusMonths(1).minusSeconds(1)








        List<GeneralLedgerDto> results = []
       def coaList = subAccountSetupService.getAllChartOfAccountGenerate(
                accountType,
                motherAccountCode,
                description,
                subaccountType,
                department
        )


        coaList.each {coa ->
            results << new GeneralLedgerDto().tap {
                it.code = coa.code
                it.description = coa.description
                it.accountType = coa.accountType
                it.normalSide = coa.motherAccount.normalSide
            }

        }



            List<String> accountCode=[]

            coaList.each {
                accountCode << it.code
            }




            def query = """


                            SELECT 
                            t.code,
                            bd.total_debit as priordebit,
                            bd.total_credit as priorcredit,
                            ld.total_debit as currentdebit,
                            ld.total_credit as currentcredit
                            FROM 
                            
                            ${"(VALUES ('" + accountCode.join("'),('") + "')) as t(code)"}
                            
                            left join (select journal_account->> 'code' as code , sum(debit) as total_debit, sum(credit) as total_credit from accounting.ledger_date 
                            where transaction_date >= ? and transaction_date <= ? and fiscal=? and approved_datetime is not null
                            group by journal_account->> 'code' ) ld on ld.code=t.code 
                            
                        
                            left join (select journal_account->> 'code' as code , sum(debit) as total_debit, sum(credit) as total_credit from accounting.ledger_date 
                            where transaction_date <= ?  and fiscal=? and approved_datetime is not null
       
                            group by journal_account->> 'code' ) bd on bd.code=t.code 


            
"""

            def records=  jdbcTemplate.queryForList(query,
            Timestamp.from(currentTargetStart.toInstant(ZoneOffset.UTC)),
            Timestamp.from(currentTargetEnd.toInstant(ZoneOffset.UTC)),
            fiscal.id,
            Timestamp.from(currentTargetStart.toInstant(ZoneOffset.UTC)),
            fiscal.id
            )


            Map<String,Map<String,Object>> tmp = [:]

        records.each {
             tmp[(String)it.get("code")] = it
        }



        results.each {dto->

            def match = tmp[dto.code]

            if(match){

               BigDecimal priordebit    = ((BigDecimal) match.get("priordebit",0.0))?:0.0
               BigDecimal priorcredit   = ((BigDecimal) match.get("priorcredit",0.0))?:0.0
               BigDecimal currentdebit  = ((BigDecimal) match.get("currentdebit",0.0))?:0.0
               BigDecimal currentcredit = ((BigDecimal) match.get("currentcredit",0.0))?:0.0



                if(dto.normalSide=="DEBIT"){
                    def balance = (priordebit?:0.0)  - (priorcredit?:0.0)

                    if(balance > 0)
                        dto.beginningDebit = balance
                    else
                        dto.beginningCredit = balance.abs()
                }else {
                    def balance =  (priorcredit?:0.0) - (priordebit?:0.0)

                    if(balance > 0)
                        dto.beginningCredit = balance
                    else
                        dto.beginningDebit = balance.abs()
                }
                dto.periodicDebit = currentdebit
                dto.periodicCredit = currentcredit




                if(dto.normalSide=="DEBIT"){
                    def ending = (dto.beginningDebit + dto.periodicDebit) - (dto.beginningCredit + dto.periodicCredit)
                    if(ending > 0)
                        dto.endingDebit = ending
                    else
                        dto.endingCredit = ending.abs()

                }
                else {

                    def ending =    (dto.beginningCredit + dto.periodicCredit) - (dto.beginningDebit + dto.periodicDebit)

                    if(ending > 0)
                        dto.endingCredit = ending
                    else
                        dto.endingDebit = ending.abs()
                }


            }

        }


        BigDecimal totalbeginningDebit = 0.0
        BigDecimal totalbeginningCredit= 0.0
        BigDecimal totalperiodicDebit= 0.0
        BigDecimal totalperiodicCredit= 0.0
        BigDecimal totalendingDebit= 0.0
        BigDecimal totalendingCredit= 0.0
      def finalList=  results.findAll {

              totalbeginningDebit +=  it.beginningDebit
              totalbeginningCredit+= it.beginningCredit
              totalperiodicDebit+= it.periodicDebit
              totalperiodicCredit+= it.periodicCredit
              totalendingDebit+= it.endingDebit
              totalendingCredit+= it.endingCredit

            (it.beginningDebit>0) || (it.beginningCredit>0) || (it.periodicDebit>0) || (it.periodicCredit>0) || (it.endingDebit>0) || (it.endingCredit>0)


        }

        new GeneralLedgerDtoContainer(finalList,
                totalbeginningDebit,
                totalbeginningCredit,
                totalperiodicDebit,
                totalperiodicCredit,
                totalendingDebit,
                totalendingCredit
        )
    }





}


