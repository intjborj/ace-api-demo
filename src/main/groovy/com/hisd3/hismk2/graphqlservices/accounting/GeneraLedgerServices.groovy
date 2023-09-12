package com.hisd3.hismk2.graphqlservices.accounting

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.accounting.Fiscal
import com.hisd3.hismk2.domain.accounting.JournalType
import com.hisd3.hismk2.domain.accounting.Ledger
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.types.GraphQLResVal
import com.hisd3.hismk2.repository.accounting.LedgerRepository
import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.persistence.EntityManager
import javax.swing.text.html.ListView
import java.time.Instant
import java.time.LocalDateTime
import java.time.Year
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Canonical
class  LedgerViewContainer {
    Page<Ledger> ledgerPage

    BigDecimal totalDebit = 0.0
    BigDecimal totalCredit = 0.0
}


@Canonical
class  LedgerView {
    List<Map<String,Object>> ledgerPage
    BigDecimal totalDebit = 0.0
    BigDecimal totalCredit = 0.0
}

@Canonical
class LedgerDownloadItem{
   String accountCode
   String accountTitle
   String journal
   String docNum
   String dateTime
   String reference
   String entity
   String particulars
   String created
   String approved
   String approvedDatetime
   BigDecimal debit
   BigDecimal credit

}
@Service
@GraphQLApi
class GeneraLedgerServices extends AbstractDaoService<Ledger> {
    GeneraLedgerServices( ) {
        super(Ledger.class)
    }
    @Autowired
    FiscalServices fiscalServices

    @Autowired
    JdbcTemplate jdbcTemplate

    @Autowired
    LedgerRepository ledgerRepository

    @Autowired
    EntityManager entityManager

    List<Ledger> ledgerAllDownload(
            @GraphQLArgument(name ="journalType") String journalType,
            @GraphQLArgument(name = "fromDate")  Instant fromDate,
            @GraphQLArgument(name = "toDate")  Instant  toDate,
            @GraphQLArgument(name = "posted")  Boolean  posted
    ){

        if(posted){

            createQuery("""
      Select l   from Ledger l left join fetch l.header header where  header.transactionDate >= :fromDate and header.transactionDate <= :toDate  and
            ( header.journalType = :journalType or :journalType = 'ALL') and header.approvedBy is not null
         order by header.transactionDate ASC 
         """,
                    [
                            journalType: JournalType.valueOf(journalType),
                            fromDate: fromDate,
                            toDate: toDate
                    ]).resultList
        }
        else {
            createQuery("""
      Select l   from Ledger l left join fetch l.header header where  header.transactionDate >= :fromDate and header.transactionDate <= :toDate  and
            ( header.journalType = :journalType or :journalType = 'ALL')
         order by header.transactionDate ASC 
         """,
                    [
                            journalType: JournalType.valueOf(journalType),
                            fromDate: fromDate,
                            toDate: toDate
                    ]).resultList
        }

    }

    List<Ledger> ledgerViewListForDownload(
            @GraphQLArgument(name ="fiscalId") UUID fiscalId,
            @GraphQLArgument(name = "code")  String code,
            @GraphQLArgument(name = "monthNo")  Integer  monthNo,
            @GraphQLArgument(name = "filter")  String  filter
    ){

        Fiscal fiscal =  fiscalServices.findOne(fiscalId)

        LocalDateTime currentTargetStart = LocalDateTime.of(fiscal.toDate.getYear() ,monthNo,1,0,0,0,0)
        LocalDateTime currentTargetEnd = currentTargetStart.plusMonths(1).minusSeconds(1)



        createQuery("""
         from Ledger l  where function('jsonb_extract_path_text',l.journalAccount,'code') = :code and 
           l.header.transactionDate >= :fromDate and l.header.transactionDate <= :toDate and l.header.approvedDatetime is not null and
           l.header.fiscal = :fiscal and
            (
                lower(l.header.entityName) like lower(concat('%',:filter,'%')) or 
                lower(l.header.docnum) like lower(concat('%',:filter,'%'))  or  
                lower(l.header.particulars) like lower(concat('%',:filter,'%')) or  
                lower(l.header.invoiceSoaReference) like lower(concat('%',:filter,'%'))
           )
         order by l.header.transactionDate DESC 
         """,
                 [
                         fiscal:fiscal,
                         code:code,
                         filter:filter,
                         fromDate: currentTargetStart.toInstant(ZoneOffset.UTC),
                         toDate: currentTargetEnd.toInstant(ZoneOffset.UTC)
                 ]).resultList


    }

    @GraphQLQuery(name = "ledgerViewList", description = "Ledger View Listing")
    LedgerViewContainer ledgerViewList(
            @GraphQLArgument(name="fiscalId") UUID fiscalId,
            @GraphQLArgument(name = "code")  String code,
            @GraphQLArgument(name = "monthNo")  Integer  monthNo,
            @GraphQLArgument(name = "filter")  String  filter,
            @GraphQLArgument(name = "page")  Integer  page,
            @GraphQLArgument(name = "size")  Integer  size
    ){

        Fiscal fiscal =  fiscalServices.findOne(fiscalId)

        LocalDateTime currentTargetStart = LocalDateTime.of(fiscal.toDate.getYear() ,monthNo,1,0,0,0,0)
        LocalDateTime currentTargetEnd = currentTargetStart.plusMonths(1).minusSeconds(1)

        def pageable =   getPageable(
                """ from Ledger l  where function('jsonb_extract_path_text',l.journalAccount,'code') = :code and 
           l.header.transactionDate >= :fromDate and l.header.transactionDate <= :toDate and l.header.approvedDatetime is not null and
           l.header.fiscal = :fiscal and
            (
                lower(l.header.entityName) like lower(concat('%',:filter,'%')) or 
                lower(l.header.docnum) like lower(concat('%',:filter,'%'))  or  
                lower(l.header.particulars) like lower(concat('%',:filter,'%')) or  
                lower(l.header.invoiceSoaReference) like lower(concat('%',:filter,'%'))
           )
         order by l.header.transactionDate DESC 
""",
                """
           Select count(l)  from Ledger l  where function('jsonb_extract_path_text',l.journalAccount,'code') = :code and 
           l.header.transactionDate >= :fromDate and l.header.transactionDate <= :toDate and l.header.approvedDatetime is not null and
           l.header.fiscal = :fiscal and
            (
                lower(l.header.entityName) like lower(concat('%',:filter,'%')) or 
                lower(l.header.docnum) like lower(concat('%',:filter,'%'))  or  
                lower(l.header.particulars) like lower(concat('%',:filter,'%')) or  
                lower(l.header.invoiceSoaReference) like lower(concat('%',:filter,'%'))
           )
""",
                page,
                size,
                [
                        fiscal:fiscal,
                        code:code,
                        filter:filter,
                        fromDate: currentTargetStart.toInstant(ZoneOffset.UTC),
                        toDate: currentTargetEnd.toInstant(ZoneOffset.UTC)
                ]
        )



        BigDecimal totalDebit = 0.0
        BigDecimal totalCredit = 0.0


        def sumTotal= jdbcTemplate.queryForList("""
                select sum(l.debit) as totalDebit, sum(l.credit) as totalCredit from  accounting.ledger l 
                left join accounting.header_ledger hl  on hl.id = l."header" 
                where hl.transaction_date >= ? and  hl.transaction_date <= ? and hl.fiscal = ? and l.journal_account ->>'code' = ? and hl.approved_datetime is not null
                and (
                  hl.entity_name ilike concat('%',?,'%')  or 
                  hl.docnum  ilike concat('%',?,'%')   or 
                  hl.particulars ilike concat('%',?,'%') or 
                  hl.invoice_soa_reference  ilike concat('%',?,'%')
                )
""",                 java.sql.Timestamp.from(currentTargetStart.toInstant(ZoneOffset.UTC)),
                java.sql.Timestamp.from(currentTargetEnd.toInstant(ZoneOffset.UTC)),
                fiscal.id,code,
                filter,
                filter,
                filter,
                filter
        ).find()

        if(sumTotal){

            totalDebit = (BigDecimal)(sumTotal.get("totalDebit")?:0.0)
            totalCredit =  (BigDecimal)(sumTotal.get("totalCredit")?:0.0)

        }


        return new LedgerViewContainer(
                pageable,
                totalDebit,
                totalCredit
        )


    }


    @GraphQLQuery(name = "ledgerViewListV2", description = "Ledger View Listing")
    LedgerView ledgerViewListV2(
            @GraphQLArgument(name="fiscalId") UUID fiscalId,
            @GraphQLArgument(name = "code")  String code,
            @GraphQLArgument(name = "monthNo")  Integer  monthNo,
            @GraphQLArgument(name = "filter")  String  filter,
            @GraphQLArgument(name = "page")  Integer  page,
            @GraphQLArgument(name = "debitBegBal")  BigDecimal  debitBegBal,
            @GraphQLArgument(name = "creditBegBal")  BigDecimal  creditBegBal
    ) {
        Fiscal fiscal = fiscalServices.findOne(fiscalId)
        LocalDateTime currentTargetStart = LocalDateTime.of(fiscal.toDate.getYear(), monthNo, 1, 0, 0, 0, 0)
        LocalDateTime currentTargetEnd = currentTargetStart.plusMonths(1).minusSeconds(1)
        String strStart = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(currentTargetStart)
        String strEnd = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(currentTargetEnd)

        def list = jdbcTemplate.queryForList("""
                            select 
                                "rowNum",
                                "transactionDate",
                                "journalType",
                                "docType",
                                "invoiceSoaReference",
                                "entityName",
                                particulars,
                                debit,
                                credit,
                                balance
                            from 
                            (
                               select 
                                   "rowNum",
                                   "journalType",
                                   "docType",
                                   "invoiceSoaReference",
                                   "entityName",
                                   particulars,
                                   cast(? as date) as "transactionDate",
                                   debit,
                                   credit,
                                   sum(debit-credit) over (partition by code order by "rowNum") as balance
                               from 
                               (
                                   select 
                                   1 as "rowNum",
                                   '' as "journalType",
                                   '' as "docType",
                                   cast(? as date) as "transactionDate",
                                   '' as "invoiceSoaReference",
                                   'BEGINNING BALANCE' as "entityName",
                                   '' as particulars,
                                   cast(?::numeric as numeric) as "debit" , cast(?::numeric as numeric) as credit , ?::varchar as code 
                                   union all
                                   select 
                                    *
                                    from 
                                   (
                                       select 
                                           1+ row_number() over (order by hl.transaction_date asc) as "rowNum",
                                           hl.journal_type as "journalType",
                                           concat(hl.doctype,'-',hl.docnum) as "docType",
                                           hl.transaction_date::date as "transactionDate",
                                           hl.invoice_soa_reference  as "invoiceSoaReference",
                                           hl.entity_name as "entityName",
                                           hl.particulars as "particulars",
                                           l.debit ,
                                           l.credit ,
                                           l.journal_account->>'code' as code
                                       from accounting.ledger l 
                                       left join accounting.header_ledger hl on hl.id  = l."header" 
                                       where 
                                        l.journal_account->>'code' =  ?::varchar
                                       and 
                                        to_char(date(hl.transaction_date),'YYYY-MM-DD') between 
                                            cast(? as varchar)
                                        and 
                                            cast(? as varchar) 
                                        and
                                        hl.approved_datetime  is not null
                                        and
                                        hl.fiscal = ?
                                       order by hl.transaction_date
                                   ) as ledger_details
                               ) as ledger
                                union 
                               select 
                                   999999 as "rowNum",
                                   '' as "journalType",
                                   '' as "docType",
                                   '' as "invoiceSoaReference",
                                   'ENDING BALANCE' as "entityName",
                                   '' as particulars,
                                   cast(? as date) as transaction_date,
                                   '0' as debit,
                                   '0' as credit,
                                   (?::numeric-?::numeric)+sum(debit-credit) as balance
                               from accounting.ledger l 
                               left join accounting.header_ledger hl on hl.id  = l."header" 
                               where 
                                l.journal_account->>'code' = ?::varchar
                               and 
                               to_char(date(hl.transaction_date),'YYYY-MM-DD') between 
                                    cast(? as varchar)
                               and 
                                    cast(? as varchar)
                               and
                                hl.approved_datetime  is not null
                                and
                                hl.fiscal = ?
                               group by l.journal_account->>'code'
                            ) as sample
                            order by "rowNum" 
        """,
                strStart,
                strStart,
                debitBegBal,
                creditBegBal,
                code,
                code,
                strStart,
                strEnd,
                fiscalId,
                strEnd,
                debitBegBal,
                creditBegBal,
                code,
                strStart,
                strEnd,
                fiscalId)

        BigDecimal totalDebit = debitBegBal ?: 0.0
        BigDecimal totalCredit = creditBegBal ?: 0.0

        def sumTotal= jdbcTemplate.queryForList("""
                select sum(l.debit) as totalDebit, sum(l.credit) as totalCredit from  accounting.ledger l
                left join accounting.header_ledger hl  on hl.id = l."header"
                where hl.transaction_date >= ? and  hl.transaction_date <= ? and hl.fiscal = ? and l.journal_account ->>'code' = ? and hl.approved_datetime is not null
                and (
                  hl.entity_name ilike concat('%',?,'%')  or
                  hl.docnum  ilike concat('%',?,'%')   or
                  hl.particulars ilike concat('%',?,'%') or
                  hl.invoice_soa_reference  ilike concat('%',?,'%')
                )
            """,                 java.sql.Timestamp.from(currentTargetStart.toInstant(ZoneOffset.UTC)),
                java.sql.Timestamp.from(currentTargetEnd.toInstant(ZoneOffset.UTC)),
                fiscal.id,code,
                filter,
                filter,
                filter,
                filter
        ).find()

        if(sumTotal){
            totalDebit += (BigDecimal)(sumTotal.get("totalDebit")?:0.0)
            totalCredit +=  (BigDecimal)(sumTotal.get("totalCredit")?:0.0)
        }

        return new LedgerView(
                list,
                totalDebit,
                totalCredit
        )


    }

    @GraphQLQuery(name="getLedgerByHeaderId")
    List<Ledger> getLedgerByHeaderId(
            @GraphQLArgument(name="id") UUID id
    ){
        createQuery("""select l from Ledger l where l.header.id = :id """)
                .setParameter("id",id).resultList.sort{it.journalAccount.code}
    }



}
