package com.hisd3.hismk2.graphqlservices.cashiering

import com.hisd3.hismk2.domain.cashiering.PaymentTrackerDetails
import com.hisd3.hismk2.domain.cashiering.PaymentType
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.types.GraphQLResVal
import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service

import javax.transaction.Transactional
import java.time.Instant

@Canonical
class DailySalesDataExtracted{
    String shiftNo
    String startShift
    String endShift
    String terminalId
    String collectionDate
    String docType
    String docNo
    String reference
    String payee
    String paymentDescription
    String userId
    String type
    BigDecimal amount
}


@Service
@GraphQLApi
@Transactional(rollbackOn = Exception.class)
class PaymentTrackerDetailsServices extends  AbstractDaoService<PaymentTrackerDetails> {

    PaymentTrackerDetailsServices(){
        super(PaymentTrackerDetails.class)
    }

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate

    @GraphQLQuery(name = "getCashieringCollectionReport")
    Page<PaymentTrackerDetails> getCashieringCollectionReport(
            @GraphQLArgument(name = "filter") String filter,
            @GraphQLArgument(name = "terminalId") List<UUID> terminalId,
            @GraphQLArgument(name = "collectionStartDate") String collectionStartDate,
            @GraphQLArgument(name = "collectionEndDate") String collectionEndDate,
            @GraphQLArgument(name = "paymentType") List<String> paymentType,
            @GraphQLArgument(name = "transactionCategoryId") UUID transactionCategoryId,
            @GraphQLArgument(name = "filterType") String filterType,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "size") Integer size
    ) {

        if(!collectionStartDate || !collectionEndDate)
            return Page.empty()


        String queryStr = "from PaymentTrackerDetails s where  s.paymentTracker.shift.cdctr is not null "
        Map<String, Object> params = [:]

        if(paymentType.size() > 0){
            params['paymentType'] = paymentType.collect{ PaymentType.valueOf(it)}
            queryStr +=" and s.type in :paymentType "
        }

        if(transactionCategoryId){
            params['transactionCategoryId'] = transactionCategoryId
            queryStr +=" and s.paymentTracker.transactionCategoryId = :transactionCategoryId "
        }

        if(terminalId.size() > 0){
            params['terminalId'] = terminalId.collect{it}
            queryStr +=" and s.paymentTracker.shift.terminal.id in :terminalId "
        }

        if(filter){
            params['filter'] = filter
            if(filterType.equalsIgnoreCase('DOC_NO'))
                queryStr +=" and upper(s.paymentTracker.ornumber) like upper(concat('%',:filter,'%')) "
            else if(filterType.equalsIgnoreCase('PAYEE'))
                queryStr +=" and upper(s.paymentTracker.payorName) like upper(concat('%',:filter,'%')) "
            else
                queryStr +=" and upper(s.paymentTracker.shift.shiftno) like upper(concat('%',:filter,'%')) "
        }

        params['collectionStartDate'] = collectionStartDate
        params['collectionEndDate'] = collectionEndDate
        queryStr +=" and to_date(to_char(s.paymentTracker.createdDate, 'YYYY-MM-DD'),'YYYY-MM-DD') >= to_date(:collectionStartDate,'YYYY-MM-DD') and to_date(to_char(s.paymentTracker.createdDate, 'YYYY-MM-DD'),'YYYY-MM-DD') <= to_date(:collectionEndDate,'YYYY-MM-DD')"


        getPageable("${queryStr} order by s.paymentTracker.createdDate desc",
                "Select count(s) ${queryStr} ",
                page,
                size,
                params
        )
    }

    @GraphQLQuery(name="getDCRPageable")
    Page<DailySalesDataExtracted> getDCRPageable(
        @GraphQLArgument(name = "filter") String filter,
        @GraphQLArgument(name = "terminalId") UUID terminalId,
        @GraphQLArgument(name = "collectionStartDate") String collectionStartDate,
        @GraphQLArgument(name = "collectionEndDate") String collectionEndDate,
        @GraphQLArgument(name = "paymentType") String paymentType,
        @GraphQLArgument(name = "transactionCategory") String transactionCategory,
        @GraphQLArgument(name = "filterType") String filterType,
        @GraphQLArgument(name = "page") Integer page,
        @GraphQLArgument(name = "size") Integer size
    ){
            Map<String,Object> params = [:]
            params['collectionStartDate'] = collectionStartDate
            params['collectionEndDate'] = collectionEndDate
            params['page'] = page * size
            params['size'] = size

            String paramString = ""

            if(filter){
                params['filter'] = filter
                if(filterType.equalsIgnoreCase('DOC_NO'))
                    paramString +=" and pt.ornumber = :filter "
                else if(filterType.equalsIgnoreCase('PAYEE'))
                    paramString +=" and upper(pt.payor_name) like upper(concat('%',:filter,'%')) "
                else
                    paramString +=" and s.shiftno = :filter  "
            }

            if(terminalId){
                params['terminalId'] = terminalId
                paramString +=" and c.id = :terminalId "
            }

            if(transactionCategory){
                params['transactionCategory'] = transactionCategory
                paramString +=" and pt.transaction_category = :transactionCategory "
            }

            if(paymentType){
                params['paymentType'] = paymentType
                paramString +=" and ptd.type = :paymentType "
            }

            List<DailySalesDataExtracted> records = []

            def recordsRaw = namedParameterJdbcTemplate.queryForList(
            """
                SELECT
                    s.shiftno AS "shiftNo",
                    TO_CHAR(DATE(s.startshift + INTERVAL '8 HOUR'), 'YYYY-MM-DD') AS "startShift",
                    TO_CHAR(DATE(s.endshift + INTERVAL '8 HOUR'), 'YYYY-MM-DD') AS "endShift",
                    c.terminal_id AS "terminalId",
                    TO_CHAR(DATE(pt.created_date + INTERVAL '8 HOUR'), 'YYYY-MM-DD') AS "collectionDate",
                    pt.receipt_type AS "docType",
                    pt.ornumber AS "docNo",
                    CASE 
                        WHEN pt.billingid IS NOT NULL 
                        THEN (SELECT b.billing_no FROM billing.billing b WHERE b.id = pt.billingid)
                        WHEN pt.investorid IS NOT NULL 
                        THEN (
                            SELECT CAST(JSONB_AGG(ss.subscription_code) AS TEXT) AS "subscriptions"
                            FROM billing.investor_payment_ledger ipl
                            LEFT JOIN billing.investor_subscriptions ss ON ss.id = ipl."subscription"
                            WHERE ipl.payment_tracker IS NOT NULL AND ipl."subscription" IS NOT NULL 
                            AND ipl.payment_tracker = pt.id
                            GROUP BY ipl.payment_tracker
                        )
                        ELSE pt.reference
                    END AS "reference",
                    CASE 
                        WHEN pt.payor_name IS NOT NULL 
                        THEN pt.payor_name
                        ELSE pt.description
                    END AS "payee",
                    pt.transaction_category AS "paymentDescription",
                    CAST(tu.id AS VARCHAR) AS "userId",
                    ptd."type",
                    ptd.amount
                FROM
                    cashiering.payment_tracker_details ptd  
                LEFT JOIN
                    cashiering.payment_tracker pt ON pt.id = ptd.payment_tracker 
                LEFT JOIN
                    cashiering.shifting s ON s.id = pt.shiftid 
                LEFT JOIN
                    cashiering.cashierterminals c ON c.id = s.cashier 
                LEFT JOIN
                    public.t_user tu ON tu.login = s.created_by 
                WHERE
                    TO_CHAR(DATE(pt.created_date + INTERVAL '8 HOUR'), 'YYYY-MM-DD') >= :collectionStartDate 
                    AND TO_CHAR(DATE(pt.created_date + INTERVAL '8 HOUR'), 'YYYY-MM-DD') <= :collectionEndDate
                    ${paramString}
                LIMIT :size OFFSET :page
            """.toString()
                    ,
                params
            )

            recordsRaw.each {
                records << new DailySalesDataExtracted(
                        it.get("shiftNo","") as String,
                        it.get("startShift","") as String,
                        it.get("endShift","") as String,
                        it.get("terminalId","") as String,
                        it.get("collectionDate") as String,
                        it.get("docType") as String,
                        it.get("docNo") as String,
                        it.get("reference") as String,
                        it.get("payee") as String,
                        it.get("paymentDescription") as String,
                        it.get("userId") as String,
                        it.get("type") as String,
                        it.get("amount") as BigDecimal
                )
            }

            params.remove("page")
            params.remove("size")

            def count =     namedParameterJdbcTemplate.queryForObject("""
                SELECT
                   count(*)
                FROM
                    cashiering.payment_tracker_details ptd  
                LEFT JOIN
                    cashiering.payment_tracker pt ON pt.id = ptd.payment_tracker 
                LEFT JOIN
                    cashiering.shifting s ON s.id = pt.shiftid 
                LEFT JOIN
                    cashiering.cashierterminals c ON c.id = s.cashier 
                LEFT JOIN
                    public.t_user tu ON tu.login = s.created_by 
                WHERE
                    TO_CHAR(DATE(pt.created_date + INTERVAL '8 HOUR'), 'YYYY-MM-DD') >= :collectionStartDate 
                    AND TO_CHAR(DATE(pt.created_date + INTERVAL '8 HOUR'), 'YYYY-MM-DD') <= :collectionEndDate
                    ${paramString} """.toString(),
                    params, Long.class)
            new PageImpl<DailySalesDataExtracted>(records, PageRequest.of(page, size),count)
    }


    @GraphQLQuery(name = "getAllCashieringCollectionReport")
    List<DailySalesDataExtracted> getAllCashieringCollectionReport(
            @GraphQLArgument(name = "filter") String filter,
            @GraphQLArgument(name = "terminalId") UUID terminalId,
            @GraphQLArgument(name = "collectionStartDate") String collectionStartDate,
            @GraphQLArgument(name = "collectionEndDate") String collectionEndDate,
            @GraphQLArgument(name = "paymentType") String paymentType,
            @GraphQLArgument(name = "transactionCategory") String transactionCategory,
            @GraphQLArgument(name = "filterType") String filterType = 'SHIFT_NO'
    ) {
        if(!collectionStartDate || !collectionEndDate)
            return []

        Map<String,Object> params = [:]
        params['collectionStartDate'] = collectionStartDate
        params['collectionEndDate'] = collectionEndDate

        String paramString = ""

        if(filter){
            params['filter'] = filter
            if(filterType.equalsIgnoreCase('DOC_NO'))
                paramString +=" and pt.ornumber = :filter "
            else if(filterType.equalsIgnoreCase('PAYEE'))
                paramString +=" and upper(pt.payor_name) like upper(concat('%',:filter,'%')) "
            else
                paramString +=" and s.shiftno = :filter  "
        }

        if(terminalId){
            params['terminalId'] = terminalId
            paramString +=" and c.id = :terminalId "
        }

        if(transactionCategory){
            params['transactionCategory'] = transactionCategory
            paramString +=" and pt.transaction_category = :transactionCategory "
        }

        if(paymentType){
            params['paymentType'] = paymentType
            paramString +=" and ptd.type = :paymentType "
        }

        List<DailySalesDataExtracted> records = []

        def recordsRaw = namedParameterJdbcTemplate.queryForList(
                """
                SELECT
                    s.shiftno AS "shiftNo",
                    TO_CHAR(DATE(s.startshift + INTERVAL '8 HOUR'), 'YYYY-MM-DD') AS "startShift",
                    TO_CHAR(DATE(s.endshift + INTERVAL '8 HOUR'), 'YYYY-MM-DD') AS "endShift",
                    c.terminal_id AS "terminalId",
                    TO_CHAR(DATE(pt.created_date + INTERVAL '8 HOUR'), 'YYYY-MM-DD') AS "collectionDate",
                    pt.receipt_type AS "docType",
                    pt.ornumber AS "docNo",
                    CASE 
                        WHEN pt.billingid IS NOT NULL 
                        THEN (SELECT b.billing_no FROM billing.billing b WHERE b.id = pt.billingid)
                        WHEN pt.investorid IS NOT NULL 
                        THEN (
                            SELECT CAST(JSONB_AGG(ss.subscription_code) AS TEXT) AS "subscriptions"
                            FROM billing.investor_payment_ledger ipl
                            LEFT JOIN billing.investor_subscriptions ss ON ss.id = ipl."subscription"
                            WHERE ipl.payment_tracker IS NOT NULL AND ipl."subscription" IS NOT NULL 
                            AND ipl.payment_tracker = pt.id
                            GROUP BY ipl.payment_tracker
                        )
                        ELSE pt.reference
                    END AS "reference",
                    CASE 
                        WHEN pt.payor_name IS NOT NULL 
                        THEN pt.payor_name
                        ELSE pt.description
                    END AS "payee",
                    pt.transaction_category AS "paymentDescription",
                    CAST(tu.id AS VARCHAR) AS "userId",
                    ptd."type",
                    ptd.amount
                FROM
                    cashiering.payment_tracker_details ptd  
                LEFT JOIN
                    cashiering.payment_tracker pt ON pt.id = ptd.payment_tracker 
                LEFT JOIN
                    cashiering.shifting s ON s.id = pt.shiftid 
                LEFT JOIN
                    cashiering.cashierterminals c ON c.id = s.cashier 
                LEFT JOIN
                    public.t_user tu ON tu.login = s.created_by 
                WHERE
                    TO_CHAR(DATE(pt.created_date + INTERVAL '8 HOUR'), 'YYYY-MM-DD') >= :collectionStartDate 
                    AND TO_CHAR(DATE(pt.created_date + INTERVAL '8 HOUR'), 'YYYY-MM-DD') <= :collectionEndDate
                    ${paramString}
            """.toString()
                ,
                params
        )
    }


    @GraphQLQuery(name = "getCashieringCollectionByTypePage")
    Page<PaymentTrackerDetails> getCashieringCollectionByTypePage(
            @GraphQLArgument(name = "filter") String filter,
            @GraphQLArgument(name = "depositoryBank") UUID depositoryBank,
            @GraphQLArgument(name = "collectionDate") Instant collectionDate,
            @GraphQLArgument(name = "clearingDate") Instant clearingDate,
            @GraphQLArgument(name = "paymentType") String paymentType,
            @GraphQLArgument(name = "status") String status,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "size") Integer size
    ) {


        String queryStr = "from PaymentTrackerDetails s where  s.paymentTracker.shift.cdctr is not null "
        Map<String, Object> params = [:]

        if(paymentType){
            params['paymentType'] = PaymentType.valueOf(paymentType)
            queryStr +=" and s.type = :paymentType "
        }

        if(depositoryBank){
            params['depositoryBank'] = depositoryBank
            queryStr +=" and s.collectionDetail.bank.id = :depositoryBank "
        }

        if(collectionDate){
            params['collectionDate'] = collectionDate
            queryStr +=" and to_date(to_char(s.createdDate, 'YYYY-MM-DD'),'YYYY-MM-DD') = to_date(:collectionDate,'YYYY-MM-DD') "
        }

        if(clearingDate){
            params['clearingDate'] = clearingDate
            queryStr +=" and to_date(to_char(s.cleareddate, 'YYYY-MM-DD'),'YYYY-MM-DD') = to_date(:clearingDate,'YYYY-MM-DD') "
        }

        if(status){
            params['status'] = status
            if(status == 'Pending')
            queryStr +=" and ( s.status is null or s.status = :status )"
            else
            queryStr +=" and s.status = :status "
        }

        if(filter){
            params['filter'] = filter
            queryStr +=" and upper(s.paymentTracker.shift.shiftno) like upper(concat('%',:filter,'%')) "
        }

        String query = """Select s ${queryStr} order by s.createdDate desc"""
        String countQuery = """Select count(s) ${queryStr} """


        getPageable(query,
                countQuery,
                page,
                size,
                params
        )
    }

    @GraphQLQuery(name = "getCashieringCollectionByTypeOnList")
    List<PaymentTrackerDetails> getCashieringCollectionByTypeOnList(
            @GraphQLArgument(name = "filter") String filter,
            @GraphQLArgument(name = "depositoryBank") UUID depositoryBank,
            @GraphQLArgument(name = "collectionDate") Instant collectionDate,
            @GraphQLArgument(name = "clearingDate") Instant clearingDate,
            @GraphQLArgument(name = "paymentType") String paymentType,
            @GraphQLArgument(name = "status") String status
    ) {

        String queryStr = "from PaymentTrackerDetails s where  s.paymentTracker.shift.cdctr is not null "
        Map<String, Object> params = [:]

        if(paymentType){
            params['paymentType'] = PaymentType.valueOf(paymentType)
            queryStr +=" and s.type = :paymentType "
        }

        if(depositoryBank){
            params['depositoryBank'] = depositoryBank
            queryStr +=" and s.collectionDetail.bank.id = :depositoryBank "
        }

        if(collectionDate){
            params['collectionDate'] = collectionDate
            queryStr +=" and to_date(to_char(s.createdDate, 'YYYY-MM-DD'),'YYYY-MM-DD') = to_date(:collectionDate,'YYYY-MM-DD') "
        }

        if(clearingDate){
            params['clearingDate'] = clearingDate
            queryStr +=" and to_date(to_char(s.cleareddate, 'YYYY-MM-DD'),'YYYY-MM-DD') = to_date(:clearingDate,'YYYY-MM-DD') "
        }

        if(status){
            params['status'] = status
            if(status == 'Pending')
                queryStr +=" and ( s.status is null or s.status = :status )"
            else
                queryStr +=" and s.status = :status "
        }

        if(filter){
            params['filter'] = filter
            queryStr +=" and upper(s.paymentTracker.shift.shiftno) like upper(concat('%',:filter,'%')) "
        }

        String query = """Select s ${queryStr} order by s.createdDate desc"""

        createQuery(query,params).resultList
    }


    @GraphQLMutation(name='updatePaymentTrackerDetails')
    PaymentTrackerDetails updatePaymentTrackerDetails (
                @GraphQLArgument(name = "id") UUID id,
                @GraphQLArgument(name = "fields") Map<String,Object> fields
    ){
       upsertFromMap(id , fields)
    }


    @GraphQLQuery(name='findPaymentTrackerDetailsById')
    PaymentTrackerDetails findPaymentTrackerDetailsById (
            @GraphQLArgument(name = "id") UUID id
    ){
        id ? findOne(id) ?: null : null
    }
}
