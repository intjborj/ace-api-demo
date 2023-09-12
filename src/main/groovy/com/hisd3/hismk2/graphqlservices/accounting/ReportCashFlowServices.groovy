package com.hisd3.hismk2.graphqlservices.accounting

import com.google.gson.Gson
import groovy.json.JsonSlurper
import groovy.transform.Canonical
import groovy.transform.builder.Builder
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.json.JSONArray
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters


@Canonical
class  PeriodDateRangeChild {
    Integer key
    String label
    LocalDate start
    LocalDate end
}


@Canonical
class  PeriodRangeParent {
    List<ISPeriodDateRangeChild> periodDateRangeChildList
    String start
    String end
}

@Builder
class CashFlowGuarantor {
    String code
    String description
    Map<String,Object> source
}

@Builder
class CashFlowFields {
    String code
    String description
    String source
}

@Component
@GraphQLApi
class ReportCashFlowServices {

    @Autowired
    JdbcTemplate jdbcTemplate

    static String getCalculationType(LocalDate start, LocalDate end){
        LocalDate firstDayOfMonth = start.with(TemporalAdjusters.firstDayOfMonth())
        LocalDate lastDayOfStartMonth = start.with(TemporalAdjusters.lastDayOfMonth())
        LocalDate lastDayOfEndMonth = end.with(TemporalAdjusters.lastDayOfMonth())

        if(((start == firstDayOfMonth) && (lastDayOfStartMonth == end)) && (start.getYear() == end.getYear())){
            return  'monthly'
        }
        else if(((start == firstDayOfMonth) && (lastDayOfEndMonth == end)) && (start.getYear() == end.getYear()))
        {
            return  'yearly'
        }
        else if(start.getYear() != end.getYear())
        {
            return  'custom-yearly'
        }
        else {
            return  'daily'
        }
    }

    static String getPeriodLabel(String calculationType, LocalDate start, LocalDate end){
        def firstFormatter = DateTimeFormatter.ofPattern(" MMM YYYY")
        def secondFormatter = DateTimeFormatter.ofPattern("YYYY")
        def thirdFormatter = DateTimeFormatter.ofPattern("d MMM")
        def fourthFormatter = DateTimeFormatter.ofPattern("d MMM")
        def year = DateTimeFormatter.ofPattern("YYYY")


        switch (calculationType){
            case ('monthly'):
                return "${start.format(firstFormatter)}"
                break;
            case ('yearly'):
                return "${start.format(secondFormatter)}"
                break;
            case ('custom-yearly'):
                return "${start.format(fourthFormatter)}-${end.format(fourthFormatter)} ${end.format(year)}"
                break;
            default:
                return "${start.format(thirdFormatter)}-${end.format(fourthFormatter)} ${end.format(year)}"
                break;
        }

    }

    static LocalDate getDateStartEnd(String calculationType, LocalDate start, LocalDate end, List<ISPeriodDateRangeChild> periodList, String returnType){
        LocalDate monthStart
        LocalDate monthEnd
        Period period = Period.between(start,end)
        Integer diffMonths = Math.abs(period.getMonths())
        Integer diffDays = Math.abs(period.getDays())
        Integer diffYears = Math.abs(period.getYears())

        switch (calculationType){
            case ('monthly') :
                monthStart = periodList[0].start.minusMonths(1)
                monthEnd = monthStart.with(TemporalAdjusters.lastDayOfMonth())
                break;
            case ('yearly') :
                monthStart = periodList[0].start.minusYears(1).with(TemporalAdjusters.firstDayOfMonth())
                monthEnd = periodList[0].end.minusYears(1).with(TemporalAdjusters.lastDayOfMonth())
                break;
            default:
                monthEnd = periodList[0].start.minusDays(1)
                monthStart = monthEnd.minusDays(diffDays)
                if(diffMonths > 0){
                    monthStart =  monthStart.minusMonths(diffMonths)
                }
                if(diffYears > 0){
                    monthStart =  monthStart.minusYears(diffYears)
                }
                break;
        }

        if(returnType.equalsIgnoreCase('start'))
            return monthStart

        return monthEnd

    }


    @GraphQLQuery(name="getReportPeriodRange")
    PeriodRangeParent getReportPeriodRange(
            @GraphQLArgument(name='start') String start,
            @GraphQLArgument(name='end') String end,
            @GraphQLArgument(name='numPeriod') Integer numPeriod) {

        LocalDate localStart = LocalDate.parse(start)
        LocalDate localEnd = LocalDate.parse(end)

        PeriodRangeParent periodRangeList = new PeriodRangeParent()
        String calculationType = getCalculationType(localStart,localEnd)
        Integer i = 0

        List<ISPeriodDateRangeChild> periodDateRangeChildList = new ArrayList<ISPeriodDateRangeChild>()

        ISPeriodDateRangeChild isPeriodDateRangeChild = new ISPeriodDateRangeChild()
        isPeriodDateRangeChild.key = i++
        isPeriodDateRangeChild.label = getPeriodLabel(calculationType,localStart,localEnd)
        isPeriodDateRangeChild.start = localStart
        isPeriodDateRangeChild.end = localEnd
        periodDateRangeChildList.push(isPeriodDateRangeChild)

        while (numPeriod >= i){
            ISPeriodDateRangeChild isPeriodDateRangeChildWhile = new ISPeriodDateRangeChild()
            isPeriodDateRangeChildWhile.start = getDateStartEnd(calculationType, localStart, localEnd, periodDateRangeChildList, 'start')
            isPeriodDateRangeChildWhile.end = getDateStartEnd(calculationType, localStart, localEnd, periodDateRangeChildList, 'end')
            isPeriodDateRangeChildWhile.label = getPeriodLabel(calculationType,isPeriodDateRangeChildWhile.start,isPeriodDateRangeChildWhile.end)
            isPeriodDateRangeChildWhile.key = i++
            periodDateRangeChildList.push(isPeriodDateRangeChildWhile)
        }

        periodRangeList.periodDateRangeChildList = periodDateRangeChildList.sort{it.key }
        periodRangeList.start = periodDateRangeChildList[0].start
        periodRangeList.end = periodDateRangeChildList[i-1].end
        return  periodRangeList
    }




    @GraphQLQuery(name="getCashFlowGuarantorList")
    List<CashFlowGuarantor> getCashFlowGuarantorList(){
        List<CashFlowGuarantor> result = jdbcTemplate.query("""
            select
            c.companyaccountid as code ,concat('Cash Received from ',c.companyname) as description,
            from billing.companyaccounts c
            group by c.companyname,c.companyaccountid
            order by c.companyname
        """, new BeanPropertyRowMapper(CashFlowGuarantor.class))

        if(result)
            return result.sort{it.code}
        return []
    }

    @GraphQLQuery(name="getGuarantorPerPeriod")
    List<CashFlowFields> getGuarantorPerPeriod(
            @GraphQLArgument(name="start") String start,
            @GraphQLArgument(name="end") String end,
            @GraphQLArgument(name="period") Integer period){

        PeriodRangeParent periodRange = getReportPeriodRange(start,end,period)
        String caseSyntax = ""
        String whereSyntax = ""

        Integer count = 0
        if(periodRange) {
            periodRange.periodDateRangeChildList.each {
                caseSyntax += """ when to_char(date(at2.transaction_date+ interval '8 hour'),'YYYY-MM-DD') between '${it.start.toString()}' and '${it.end.toString()}' then ${it.key} """
                if(count == 0)
                    whereSyntax += """ (to_char(date(at2.transaction_date+ interval '8 hour'),'YYYY-MM-DD') between '${it.start.toString()}' and '${it.end.toString()}' ) """
                else
                    whereSyntax += """or (to_char(date(at2.transaction_date+ interval '8 hour'),'YYYY-MM-DD') between '${it.start.toString()}' and '${it.end.toString()}' ) """
                count++
            }

            List<CashFlowFields> result = jdbcTemplate.query( """
                select 
                    "code",
                    "description",
                    jsonb_object_agg("period","amount" order by "period") as "source"
                from
                (
                    select 
                        "period",
                        "code",
                        "description",
                        sum(amount) as amount
                    from
                    (
                        select 
                            to_char(date(at2.transaction_date+ interval '8 hour'),'YYYY-MM-DD') as "date",
                            case
                            ${caseSyntax}
                            else -1
                            end as "period",
                            c.companyaccountid as code,
                            concat('Cash Received from ',c.companyname) as description,
                            coalesce (sum(atd.amount),0) as amount
                        from 
                        billing.companyaccounts c
                        left join accounting.ar_transaction at2 on at2.account_id = c.id 
                        and 
                        c.id != '18bc3e6a-0e42-4b65-99f3-27ca00171d9d'
                        and
                        (${whereSyntax})
                        left join accounting.ar_transaction_details atd on atd.ar_transaction_id = at2.id
                        group by c.companyname,c.companyaccountid ,to_char(date(at2.transaction_date+ interval '8 hour'),'YYYY-MM-DD')
                        order by to_char(date(at2.transaction_date+ interval '8 hour'),'YYYY-MM-DD') asc
                    ) as periods
                    group by 
                        "period",
                        "code",
                        "description"
                )as periods
                group by "code","description"
            """,
                new BeanPropertyRowMapper(CashFlowFields.class)
            )

            if(result)
                return result
            return []
        }
    }

    @GraphQLQuery(name="getTotalGuarantorPerPeriod")
    CashFlowFields getTotalGuarantorPerPeriod(
            @GraphQLArgument(name="start") String start,
            @GraphQLArgument(name="end") String end,
            @GraphQLArgument(name="period") Integer period){

        PeriodRangeParent periodRange = getReportPeriodRange(start,end,period)
        String caseSyntax = ""
        String whereSyntax = ""

        Integer count = 0
        if(periodRange) {
            periodRange.periodDateRangeChildList.each {
                caseSyntax += """ when to_char(date(at2.transaction_date+ interval '8 hour'),'YYYY-MM-DD') between '${it.start.toString()}' and '${it.end.toString()}' then ${it.key} """
                if(count == 0)
                    whereSyntax += """ (to_char(date(at2.transaction_date+ interval '8 hour'),'YYYY-MM-DD') between '${it.start.toString()}' and '${it.end.toString()}' ) """
                else
                    whereSyntax += """or (to_char(date(at2.transaction_date+ interval '8 hour'),'YYYY-MM-DD') between '${it.start.toString()}' and '${it.end.toString()}' ) """
                count++
            }

            def query = """
                select 
                '' as code,
                '' as description,
                jsonb_object_agg("period",round("amount",2) order by "period") as "source"
                from
                (
                    select 
                        "period",
                        sum(amount) as amount
                    from
                    (
                        select 
                            to_char(date(at2.transaction_date+ interval '8 hour'),'YYYY-MM-DD') as "date",
                            case
                            ${caseSyntax}
                            else -1
                            end as "period",
                            c.companyaccountid as code,
                            concat('Cash Received from ',c.companyname) as description,
                            coalesce (sum(atd.amount),0) as amount
                        from 
                        billing.companyaccounts c
                        left join accounting.ar_transaction at2 on at2.account_id = c.id 
                        and 
                        c.id != '18bc3e6a-0e42-4b65-99f3-27ca00171d9d'
                        and
                        (${whereSyntax})
                        left join accounting.ar_transaction_details atd on atd.ar_transaction_id = at2.id
                        group by c.companyname,c.companyaccountid ,to_char(date(at2.transaction_date+ interval '8 hour'),'YYYY-MM-DD')
                        order by to_char(date(at2.transaction_date+ interval '8 hour'),'YYYY-MM-DD') asc
                    ) as periods
                    where "period" != '-1'
                    group by 
                        "period"
                )as periods
            """

            CashFlowFields result = jdbcTemplate.queryForObject( query,
                    new BeanPropertyRowMapper(CashFlowFields.class)
            )

            if(result)
                return result
            return [:]
        }
    }

    @GraphQLQuery(name="getPatientPerPeriod")
    CashFlowFields getPatientPerPeriod(
            @GraphQLArgument(name="start") String start,
            @GraphQLArgument(name="end") String end,
            @GraphQLArgument(name="period") Integer period)
    {
        PeriodRangeParent periodRange = getReportPeriodRange(start,end,period)
        String caseSyntax = ""
        String whereSyntax = ""

        Integer count = 0
        if(periodRange) {
            periodRange.periodDateRangeChildList.each {
                caseSyntax += """ when to_char(date(pt.created_date+ interval '8 hour'),'YYYY-MM-DD') between '${it.start.toString()}' and '${it.end.toString()}' then ${it.key} """
                if(count == 0)
                    whereSyntax += """ (to_char(date(pt.created_date+ interval '8 hour'),'YYYY-MM-DD') between '${it.start.toString()}' and '${it.end.toString()}' ) """
                else
                    whereSyntax += """or (to_char(date(pt.created_date+ interval '8 hour'),'YYYY-MM-DD') between '${it.start.toString()}' and '${it.end.toString()}' ) """
                count++
            }

            CashFlowFields result = jdbcTemplate.queryForObject( """
                select 
                code,
                description,
                jsonb_object_agg("period","amount" order by "period") as "source"
                from
                (
                select
                '' as "code",
                'Cash Received from PATIENTS' as "description",
                "period",
                sum(amount) as "amount"
                from 
                (   
                    select 
                    case
                    ${caseSyntax}
                    else -1
                    end as "period",
                    sum(total_cash+total_check) as amount
                    from cashiering.payment_tracker pt  
                    where ledger_header is not null
                    and 
                    (
                    total_cash  > 0 
                    or total_check  > 0
                    )
                    and 
                    billingid is not null
                    and 
                    voided is null
                    and
                    (${whereSyntax})
                    group by to_char(date(pt.created_date+ interval '8 hour'),'YYYY-MM-DD')
                ) c
                group by "code","description","period") c 
                group by "code","description"
            """,
                    new BeanPropertyRowMapper(CashFlowFields.class)
            )

            if(result)
                return result
            return [:]
        }
    }

    @GraphQLQuery(name="getTotalCashFlowPatientPerPeriod")
    CashFlowFields getTotalCashFlowPatientPerPeriod(
            @GraphQLArgument(name="start") String start,
            @GraphQLArgument(name="end") String end,
            @GraphQLArgument(name="period") Integer period)
    {
        PeriodRangeParent periodRange = getReportPeriodRange(start,end,period)
        String caseSyntax = ""
        String whereSyntax = ""

        Integer count = 0
        if(periodRange) {
            periodRange.periodDateRangeChildList.each {
                caseSyntax += """ when to_char(date(pt.created_date+ interval '8 hour'),'YYYY-MM-DD') between '${it.start.toString()}' and '${it.end.toString()}' then ${it.key} """
                if(count == 0)
                    whereSyntax += """ (to_char(date(pt.created_date+ interval '8 hour'),'YYYY-MM-DD') between '${it.start.toString()}' and '${it.end.toString()}' ) """
                else
                    whereSyntax += """or (to_char(date(pt.created_date+ interval '8 hour'),'YYYY-MM-DD') between '${it.start.toString()}' and '${it.end.toString()}' ) """
                count++
            }

            CashFlowFields result = jdbcTemplate.queryForObject( """
                select 
                '' code,
                '' description,
                jsonb_object_agg("period","amount" order by "period") as "source"
                from
                (
                select
                "period",
                sum(amount) as "amount"
                from 
                (   
                    select 
                    case
                    ${caseSyntax}
                    else -1
                    end as "period",
                    sum(total_cash+total_check) as amount
                    from cashiering.payment_tracker pt  
                    where ledger_header is not null
                    and 
                    (
                    total_cash  > 0 
                    or total_check  > 0
                    )
                    and 
                    billingid is not null
                    and 
                    voided is null
                    and
                    (${whereSyntax})
                    group by to_char(date(pt.created_date+ interval '8 hour'),'YYYY-MM-DD')
                ) c
                where "period" != '-1'
                group by "period") c 
            """,
                    new BeanPropertyRowMapper(CashFlowFields.class)
            )

            if(result)
                return result
            return [:]
        }
    }

    @GraphQLQuery(name="getPaidToDoctorsPerPeriod")
    List<CashFlowFields> getPaidToDoctorsPerPeriod(
            @GraphQLArgument(name="start") String start,
            @GraphQLArgument(name="end") String end,
            @GraphQLArgument(name="period") Integer period) {
        PeriodRangeParent periodRange = getReportPeriodRange(start, end, period)
        String caseSyntax = ""
        String whereSyntax = ""

        Integer count = 0
        if (periodRange) {
            periodRange.periodDateRangeChildList.each {
                caseSyntax += """ when transaction_date between '${it.start.toString()}' and '${it.end.toString()}' then ${it.key} """
                if (count == 0)
                    whereSyntax += """ (transaction_date between '${it.start.toString()}' and '${it.end.toString()}' ) """
                else
                    whereSyntax += """or (transaction_date between '${it.start.toString()}' and '${it.end.toString()}' ) """
                count++
            }

            List<CashFlowFields> result = jdbcTemplate.query("""
                select 
                    '' code,
                    description,
                    jsonb_object_agg("period",amount) source
                from
                    (
                        select
                        '' code,
                        description,
                        "period",
                        sum(amount) amount
                        from
                        (   
                            select 
                                transaction_date,
                                case
                                    ${caseSyntax}
                                else -1
                                end as "period",
                                description,
                                sum(amount) amount
                            from
                                (
                                    select
                                        to_char(date(transaction_date),'YYYY-MM-DD') as transaction_date ,
                                        description,
                                        debit as amount
                                    from
                                        json_to_recordset('
                                        [
                                            {"code":"200010-2050-0000","description":"Cash Paid for DUE TO DOCTORS"},
                                            {"code":"500020-1090-0000","description":"Cash Paid for DIRECT LABOR-PROFESSIONAL FEES-RODS"},
                                            {"code":"500020-1070-0000","description":"Cash Paid for DIRECT LABOR-READERS FEE"},
                                            {"code":"200020-1050-0000","description":"Cash Paid for ACCOUNTS PAYABLE -DOCTORS FEE LIABILITY"}
                                        ]') as x(code text, description text)
                                        left join accounting.ledger_date ld on ld.journal_account->>'code' = code
                                        and debit > 0 and (${whereSyntax})
                                ) c
                            group by transaction_date,description
                            order by transaction_date
                    ) c group by description,"period"
                ) c group by description
            """,
                    new BeanPropertyRowMapper(CashFlowFields.class)
            )

            if (result)
                return result
            return []
        }
    }

        @GraphQLQuery(name="getTotalPaidToDoctorsPerPeriod")
        CashFlowFields getTotalPaidToDoctorsPerPeriod(
                @GraphQLArgument(name="start") String start,
                @GraphQLArgument(name="end") String end,
                @GraphQLArgument(name="period") Integer period)
        {
            PeriodRangeParent periodRange = getReportPeriodRange(start, end, period)
            String caseSyntax = ""
            String whereSyntax = ""

            Integer count = 0
            if (periodRange) {
                periodRange.periodDateRangeChildList.each {
                    caseSyntax += """ when transaction_date between '${it.start.toString()}' and '${it.end.toString()}' then ${it.key} """
                    if (count == 0)
                        whereSyntax += """ (transaction_date between '${it.start.toString()}' and '${it.end.toString()}' ) """
                    else
                        whereSyntax += """or (transaction_date between '${it.start.toString()}' and '${it.end.toString()}' ) """
                    count++
                }

            CashFlowFields result = jdbcTemplate.queryForObject("""
            select 
                '' code,
                '' description,
                jsonb_object_agg("period",amount) source
            from
                (
                    select
                    "period",
                    sum(amount) amount
                    from
                    (   
                        select 
                            transaction_date,
                            case
                                ${caseSyntax}
                            else -1
                            end as "period",
                            description,
                            sum(amount) amount
                        from
                            (
                                select
                                    to_char(date(transaction_date),'YYYY-MM-DD') as transaction_date ,
                                    description,
                                    debit as amount
                                from
                                    json_to_recordset('
                                    [
                                        {"code":"200010-2050-0000","description":"Cash Paid for DUE TO DOCTORS"},
                                        {"code":"500020-1090-0000","description":"Cash Paid for DIRECT LABOR-PROFESSIONAL FEES-RODS"},
                                        {"code":"500020-1070-0000","description":"Cash Paid for DIRECT LABOR-READERS FEE"},
                                        {"code":"200020-1050-0000","description":"Cash Paid for ACCOUNTS PAYABLE -DOCTORS FEE LIABILITY"}
                                    ]') as x(code text, description text)
                                    left join accounting.ledger_date ld on ld.journal_account->>'code' = code
                                    and debit > 0 and (${whereSyntax})
                            ) c
                        group by transaction_date,description
                        order by transaction_date
                ) c where "period" != '-1' group by "period"
            ) c 
        """,
                        new BeanPropertyRowMapper(CashFlowFields.class)
                )

                if (result)
                    return result
                return [:]
            }
        }


    @GraphQLQuery(name="getCashProvidedByOperatingActivities")
    List<CashFlowFields> getCashProvidedByOperatingActivities(
            @GraphQLArgument(name="start") String start,
            @GraphQLArgument(name="end") String end,
            @GraphQLArgument(name="period") Integer period){
        List<CashFlowFields> cashFlowFields = []

        CashFlowFields guarantor = getTotalGuarantorPerPeriod(start,end,period)
        CashFlowFields doctors = getTotalPaidToDoctorsPerPeriod(start,end,period)
        CashFlowFields patients = getTotalCashFlowPatientPerPeriod(start,end,period)

        cashFlowFields.push(guarantor)
        cashFlowFields.push(doctors)
        cashFlowFields.push(patients)

        return cashFlowFields

    }

}
