package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.repository.accounting.AccountReceivableCompanyRepository
import com.hisd3.hismk2.repository.accounting.AccountReceivableRepository
import com.hisd3.hismk2.services.EntityObjectMapperService
import com.hisd3.hismk2.services.GeneratorService
import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.hibernate.query.NativeQuery
import org.hibernate.transform.Transformers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

import javax.persistence.EntityManager
import javax.transaction.Transactional
import java.time.LocalDate
import java.time.Period
import java.time.chrono.ChronoPeriod
import java.time.temporal.ChronoUnit


@Canonical
class ArDetailedListReport {
	Date discharged_date
	String soa_no
	String folio
	String patient
	List<String> icd
	List<String> rvs
	BigDecimal hci
	BigDecimal pf
	BigDecimal total
}

@Canonical
class ArReportPageLayout {
	List<ArDetailedListReport> content
	Integer page
	Integer size
	BigInteger totalSize
	BigDecimal totalHCI
	BigDecimal totalPF
	BigDecimal grandTotal
}

@Canonical
class ReceivableDetailedList {
	Integer rowNum
	String dischargeDate
	String patient
	String finalSoa
	String billingNo
	String icd
	String rvs
	BigDecimal hci
	BigDecimal pf
	BigDecimal balance
	Integer fullCount
	Integer change
}

@Canonical
class ReceivableDetailedReport {
	List<ReceivableDetailedList> content
	Integer page
	Integer size
	BigInteger totalSize
}
@Canonical
class ReceivableLedgerWithAgingReport {
	List<ReceivableLedgerWithAging> receivableLedgerWithAgings
	BigDecimal debitTotal = BigDecimal.ZERO
	BigDecimal creditTotal = BigDecimal.ZERO
	BigDecimal netAmount = BigDecimal.ZERO
	BigDecimal openItemTotal = BigDecimal.ZERO
	BigDecimal notDueTotal = BigDecimal.ZERO
	BigDecimal day1To30Total = BigDecimal.ZERO
	BigDecimal day31To60Total = BigDecimal.ZERO
	BigDecimal day61To90Total = BigDecimal.ZERO
	BigDecimal day91To120Total = BigDecimal.ZERO
	BigDecimal dayOlderTotal = BigDecimal.ZERO
}

@Canonical
class ReceivableLedgerWithAging {
	String docDate
	String docType
	String docNo
	String dueDate
	String refDocType
	String refNo
	String patientNo
	String patientId
	String patientName
	BigDecimal debit = BigDecimal.ZERO
	BigDecimal credit = BigDecimal.ZERO
	BigDecimal netAmount = BigDecimal.ZERO
	BigDecimal runningBalance = BigDecimal.ZERO
	BigDecimal openItem = BigDecimal.ZERO
	BigDecimal notDue = BigDecimal.ZERO
	BigDecimal day1To30 = BigDecimal.ZERO
	BigDecimal day31To60 = BigDecimal.ZERO
	BigDecimal day61To90 = BigDecimal.ZERO
	BigDecimal day91To120 = BigDecimal.ZERO
	BigDecimal dayOver120 = BigDecimal.ZERO
}

@Canonical
class BillingDeductionSummary{
	Integer recordNo
	String dischargedDate
	String soa
	String billingNo
	String patient
	String icd
	String rvs
	BigDecimal hci
	BigDecimal pf
	BigDecimal total
	Integer totalSize
}

@Canonical
class BillingDeductionSummaryPage {
	List<BillingDeductionSummary> content
	Integer page
	Integer size
	Integer totalSize
}


@Transactional(rollbackOn = [Exception.class])
@Component
@GraphQLApi
class AccountReceivableReportsService {

	@Autowired
	EntityManager entityManager

	@GraphQLQuery(name = "receivableDetailedQuery")
	List<ReceivableDetailedList> receivableDetailedQuery(
			@GraphQLArgument(name = "accountId") String accountId,
			@GraphQLArgument(name = "searchFilter") String searchFilter,
			@GraphQLArgument(name = "startDate") String startDate,
			@GraphQLArgument(name = "endDate") String endDate,
			@GraphQLArgument(name = "status") String status,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "pageSize") Integer pageSize){
		return  entityManager.createNativeQuery("""
			select  
				cast("rowNum" as int) as "rowNum" ,
				"dischargeDate",
				patient,
				"finalSoa",
				"billingNo",
				icd,
				rvs,
				hci,
				pf,
				balance,
				cast("fullCount" as int)  as "fullCount",
				cast("change" as int)  as change
				from
					(select
						ROW_NUMBER () OVER (ORDER BY "dischargeDate" desc) as "rowNum",
						"dischargeDate",
						patient ,
						"finalSoa" ,
						"billingNo",
						icd,
						rvs,
						sum(hci) as "hci" ,
						sum(pf) as "pf" ,
						sum(hci) + sum(pf) as "balance",
						count(*) OVER() AS "fullCount",
						"change"
					from
						(select
							(
							case when to_char(date(c.discharged_datetime + interval '8 hour'),'YYYY-MM-DD') is null
							then
							to_char(date(c.created_date + interval '8 hour'),'YYYY-MM-DD')
							else
							to_char(date(c.discharged_datetime + interval '8 hour'),'YYYY-MM-DD')
							end
							) as "dischargeDate",
							date(c.discharged_datetime  + interval '8 hour') - date(c.discharged_datetime  AT TIME ZONE 'Asia/Manila') as "change",
							concat(p.last_name,', ',p.first_name,' ',p.middle_name) as "patient",
							concat(to_char(date(b.entry_datetime + interval '8 hour'),'YYYY'),'-',(case when b.final_soa is null then b.billing_no else b.final_soa end)) as "finalSoa",
							b.billing_no as "billingNo",
							(case when bi.item_type = 'DEDUCTIONS' then bi.credit else 0 end) as hci,
							(case when bi.item_type = 'DEDUCTIONSPF' then bi.credit else 0 end) as pf,
							case when c2.phil_health then c.icd_diagnosis  else null end as "icd",
							case when c2.phil_health then c.rvs_diagnosis  else null end as "rvs"
						from billing.billing b
							left join billing.billing_item bi on bi.billing = b.id
							left join billing.billing_item_details bid on bid.billingitem = bi.id and bid.field_name = 'COMPANY_ACCOUNT_ID'
							left join pms.patients p  on p.id = b.patient
							left join pms.cases c  on c.id = b.patient_case
							left join billing.companyaccounts c2 on c2.id  = cast(bid.field_value as uuid)
							left join accounting.billing_schedule_items bsi on bi.id = bsi.billing_item_id 
						where
							bi.item_type in ('DEDUCTIONS', 'DEDUCTIONSPF')
							and
							cast(bid.field_value as uuid) = cast(:accountId as uuid)
							and
							bi.status  = 'ACTIVE'
							and
							case when :status = 'UNBILLED' then bsi.id is null when :status = 'BILLED' then bsi.id is not null else bsi.id is not null or bsi.id is null end
						) as detailed_subsidiary
					where 
						((upper(patient) like upper(:searchFilter))  or 
						(upper("billingNo") like upper(:searchFilter))  or 
						(upper("finalSoa") like upper(:searchFilter)) or 
						(upper(icd) like upper(:searchFilter)) or 
						(upper(rvs) like upper(:searchFilter)))  and
						"dischargeDate" between  to_char(cast(:startDate as date),'YYYY-MM-DD')  and to_char(cast(:endDate as date),'YYYY-MM-DD')
						group by patient,"dischargeDate","finalSoa","billingNo",icd,rvs,"change"
						order by "dischargeDate" desc,"billingNo" desc,patient asc) as paginate_table
				where 
				case when :pageSize > 0 then  "rowNum" > :page  and "rowNum" <= (:page+:pageSize) else "rowNum" <= "fullCount" end
			""")
				.setParameter('startDate',startDate)
				.setParameter('endDate',endDate)
				.setParameter('accountId',accountId)
				.setParameter('searchFilter',searchFilter)
				.setParameter('status',status)
				.setParameter('page',page)
				.setParameter('pageSize',pageSize)
				.unwrap(NativeQuery.class)
				.setResultTransformer(Transformers.aliasToBean(ReceivableDetailedList.class))
				.getResultList();
	}


	@GraphQLQuery(name = "getReceivableDetailedReport")
	ReceivableDetailedReport getReceivableDetailedReport(
			@GraphQLArgument(name = "accountId") String accountId,
			@GraphQLArgument(name = "searchFilter") String searchFilter,
			@GraphQLArgument(name = "startDate") String startDate,
			@GraphQLArgument(name = "endDate") String endDate,
			@GraphQLArgument(name = "status") String status,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "pageSize") Integer pageSize){
		ReceivableDetailedReport receivableDetailedReport = new ReceivableDetailedReport()
		List<ReceivableDetailedList> reportInfList = []
		if(accountId){
			reportInfList = receivableDetailedQuery(accountId,searchFilter,startDate,endDate,status,page,pageSize)
		}

		receivableDetailedReport.content = reportInfList
		receivableDetailedReport.page = page
		receivableDetailedReport.size = pageSize
		if(reportInfList)
			receivableDetailedReport.totalSize = reportInfList[0]['fullCount'] as Integer
		return  receivableDetailedReport

	}

	List<ReceivableLedgerWithAging> getReceivableLedger(
			String account,
			String billType,
			String filterDate,
			String filterSearch){

		return  entityManager.createNativeQuery("""
			select 
				cast(ledger.doc_date as varchar) as "docDate",
				ledger.doc_type as "docType",
				cast(ledger.doc_number as varchar) as "docNo",
				cast(ledger.due_date as varchar) as "dueDate",
				cast(ledger.ref_no as varchar) as "refNo",
				cast(ledger.patient_no as varchar) as "patientNo",
				ledger.patient_name as "patientName",
				ledger.debit as "debit",
				ledger.credit as "credit",
				ledger.net_amount as "netAmount",
				(sum(ledger.debit-ledger.credit) over (partition by ledger.company_id order by ledger.order_date,ledger.doc_number,ledger.patient_name,debit desc))  as "runningBalance",
				ledger.open_item as "openItem"
				FROM
					(select   
				cast(bs.transaction_date as timestamp) as order_date,
				to_char(date(bs.transaction_date + interval '8 hour'),'YYYY-MM-DD') as doc_date,
				'SOA' as doc_type,
				substring(bs.billing_schedule_no,5,6) as doc_number,
				bs.due_date as due_date,
				'LOA' as ref_doc_type,
				ari.ref_code as ref_no,
				b.billing_no as patient_no,
				concat(p.last_name,', ',p.first_name,' ',substring(p.middle_name,1,1),'.') as patient_name,
				(ari.amount) as debit,
				0 as credit,
				(ari.amount) as net_amount,
				(ari.debit - ari.credit) as open_item,
				arc.field_value as company_id
					from accounting.account_receivable_items ari 
				left join accounting.ar_group ag on ag.account_receivable = ari.account_receivable_id and 
					ag.field_name = 'BILLING_SCHEDULE_ID' 
				left join accounting.billing_schedule bs on bs.id = ag.field_value 
				left join accounting.account_receivable_items_details arid on 
					arid.account_receivable_items = ari.id and 
					arid.field_name =  'PATIENT_ID'
				left join pms.patients p on p.id = cast(arid.field_value as uuid)
				left join accounting.account_receivable_company arc  on 
					arc.account_receivable = ari.account_receivable_id  and
					arc.field_name = 'COMPANY_ACCOUNT_ID' and
					arc.field_value = cast(:account as uuid)
				left join accounting.account_receivable ar on ar.id =  ari.account_receivable_id 
				left join accounting.account_receivable_items_details arid2 on 
					arid2.account_receivable_items = ari.id and 
					arid2.field_name =  'BILLING_ITEM_ID'
				left join billing.billing_item bi on bi.id  = cast(arid2.field_value as uuid)
				left join billing.billing b on b.id = bi.billing 
				where 
					ar.status  = 'active'  
					and  
					ag.field_name is not null  
					and 
					ari."type" = cast(:billType as varchar)
					and
					arc.field_value  is not  null
					and 
					to_char(date(bs.transaction_date + interval '8 hour'),'YYYY-MM-DD') <= cast(:filterDate as varchar)
					and 
					(concat(p.last_name,', ',p.first_name,' ',substring(p.middle_name,1,1),'.') like :filterSearch)
					union all
					select 
				at2.transaction_date as order_date,
				to_char(date(at2.transaction_date + interval '8 hour'),'YYYY-MM-DD') as doc_date,
				case 
					when atd."type" = 'transfer' then
				'TRANSFER'
					else  
				'OR'
				end as doc_type,
				case 
					when atd."type" = 'transfer' then
				substring(at2.tracking_code,7,6)
					else  
				pt.ornumber
				end as doc_number,
				bs.due_date as due_date,
				'SOA' as ref_doc_type,
				substring(bs.billing_schedule_no,5,6) as ref_no,
				b.billing_no as patient_no,
				concat(p.last_name,', ',p.first_name,' ',substring(p.middle_name,1,1),'.') as patient_name,
				0 as  debit,
				atd.amount as credit,
				(-atd.amount) as net_amount,
				0 as open_item,
				arc.field_value as company_id
					from accounting.ar_transaction_details atd 
				left join accounting.account_receivable_items_details arid on 
					arid.account_receivable_items = atd.account_receivable_item_id and 
					arid.field_name =  'PATIENT_ID'
				left join pms.patients p on p.id = cast(arid.field_value as uuid)
				left join accounting.ar_transaction at2 on at2.id = atd.ar_transaction_id
				left join  accounting.account_receivable_items ari on ari.id  = atd.account_receivable_item_id 
				left join accounting.account_receivable_company arc  on 
					arc.account_receivable = ari.account_receivable_id  and
					arc.field_name = 'COMPANY_ACCOUNT_ID' and 
					arc.field_value = cast(:account as uuid)
				left join accounting.ar_group ag on ag.account_receivable = ari.account_receivable_id and 
					ag.field_name = 'BILLING_SCHEDULE_ID'
				left join accounting.billing_schedule bs on bs.id = ag.field_value 
				left join cashiering.payment_tracker pt on pt.id = at2.payment_tracker_id 
				left join accounting.account_receivable_items_details arid2 on 
					arid2.account_receivable_items = ari.id and 
					arid2.field_name =  'BILLING_ITEM_ID'
				left join billing.billing_item bi on bi.id  = cast(arid2.field_value as uuid)
				left join billing.billing b on b.id = bi.billing 
				where 
				ari."type" = cast(:billType as varchar)
				and 
				arc.field_value is not null
				and 
				to_char(date(at2.transaction_date + interval '8 hour'),'YYYY-MM-DD') <= cast(:filterDate as varchar)
				and 
				(concat(p.last_name,', ',p.first_name,' ',substring(p.middle_name,1,1),'.') like :filterSearch)
					) as ledger 
				order by ledger.order_date,ledger.doc_number,ledger.patient_name,debit desc
			""")
				.setParameter('account',account)
				.setParameter('filterDate',filterDate)
				.setParameter('billType',billType)
				.setParameter('filterSearch',filterSearch)
				.unwrap(NativeQuery.class)
				.setResultTransformer(Transformers.aliasToBean(ReceivableLedgerWithAging.class))
				.getResultList();
	}

	@GraphQLQuery(name = "getReceivableLedgerReport")
	ReceivableLedgerWithAgingReport getReceivableLedgerReport(
			@GraphQLArgument(name = "account") String account,
			@GraphQLArgument(name = "billType") String billType,
			@GraphQLArgument(name = "filterDate") String filterDate,
			@GraphQLArgument(name = "filterSearch") String filterSearch
	){
		ReceivableLedgerWithAgingReport receivableLedgerWithAgingReport = new ReceivableLedgerWithAgingReport()
		List<ReceivableLedgerWithAging> receivableLedgerWithAging = getReceivableLedger(account,billType,filterDate,filterSearch)
		if(receivableLedgerWithAging){

			receivableLedgerWithAging.each {
				LocalDate transDate = LocalDate.parse(filterDate)
				LocalDate dueDate = LocalDate.parse(it.dueDate)
				Period period = Period.between(dueDate, transDate);

				Long days = ChronoUnit.DAYS.between(dueDate,transDate)
				if(days > 0 && days < 31) {
					it.day1To30 = it.netAmount
				}else if(days > 30 && days < 61) {
					it.day31To60 = it.netAmount
				}else if(days > 60 && days < 91) {
					it.day61To90 = it.netAmount
				}else if(days > 90 && days < 121) {
					it.day91To120 = it.netAmount
				}else if(days >  121) {
					it.dayOver120 = it.netAmount
				}else {
					it.notDue = it.netAmount
				}

				receivableLedgerWithAgingReport.debitTotal = receivableLedgerWithAgingReport.debitTotal + it.debit
				receivableLedgerWithAgingReport.creditTotal = receivableLedgerWithAgingReport.creditTotal + it.credit
				receivableLedgerWithAgingReport.netAmount = receivableLedgerWithAgingReport.netAmount + it.netAmount
				receivableLedgerWithAgingReport.openItemTotal = receivableLedgerWithAgingReport.openItemTotal + it.openItem
				receivableLedgerWithAgingReport.notDueTotal = receivableLedgerWithAgingReport.notDueTotal + it.notDue
				receivableLedgerWithAgingReport.day1To30Total = receivableLedgerWithAgingReport.day1To30Total + it.day1To30
				receivableLedgerWithAgingReport.day31To60Total = receivableLedgerWithAgingReport.day31To60Total + it.day31To60
				receivableLedgerWithAgingReport.day61To90Total = receivableLedgerWithAgingReport.day61To90Total + it.day61To90
				receivableLedgerWithAgingReport.day91To120Total = receivableLedgerWithAgingReport.day91To120Total + it.day91To120
				receivableLedgerWithAgingReport.dayOlderTotal = receivableLedgerWithAgingReport.dayOlderTotal + it.dayOver120


				return  it

			}
			receivableLedgerWithAgingReport.receivableLedgerWithAgings =  receivableLedgerWithAging
		}

//		BigDecimal notDue
//		BigDecimal day1To30
//		BigDecimal day31To60
//		BigDecimal day61To90
//		BigDecimal day91To120
//		BigDecimal dayOver120

		return receivableLedgerWithAgingReport
	}


	@GraphQLQuery(name='billingDeductionsNativeQuery')
	List<BillingDeductionSummary> billingDeductionsNativeQuery(
			@GraphQLArgument(name = "startDate") String startDate,
			@GraphQLArgument(name = "endDate") String endDate,
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "accountId") String accountId,
			@GraphQLArgument(name = "status") String status,
			@GraphQLArgument(name = "pageSize") Integer pageSize,
			@GraphQLArgument(name = "page") Integer page
	){
		return  entityManager.createNativeQuery("""
            select 
            *
            from (
            select 
            cast(ROW_NUMBER () OVER (ORDER BY discharged_date desc) as int) as "recordNo",
            cast(discharged_date as varchar) as "dischargedDate",
            cast(soa as varchar),
            billing_no as "billingNo",
            cast(patient as varchar),
            cast(icd as varchar),
            cast(rvs as varchar),
            sum(hci) as hci,
            sum(pf) as pf,
            sum(total) as total,
            cast(count(*) OVER() as int) AS "totalSize"
            from 
            (
             select
             to_char(date(
             case 
              when c.discharged_datetime is null then 
              c.created_date else
              c.discharged_datetime 
             end + interval '8 hour'
             ),'YYYY-MM-DD') as "discharged_date",
             case when b.final_soa is not null then
             concat(date_part('YEAR',b.entry_datetime),'-',b.final_soa)
             else 
             concat(date_part('YEAR',b.entry_datetime),'-',c.registry_type,'-',b.billing_no)  end  as "soa",
             b.billing_no,
             concat(p.last_name,', ',p.first_name,' ',p.middle_name) as "patient",
             case when c2.phil_health then c.icd_diagnosis  else null end as "icd",
			 case when c2.phil_health then c.rvs_diagnosis  else null end as "rvs",
             case 
              when bi.item_type = 'DEDUCTIONS' then 
              bi.credit
              else
              0
             end as "hci",
             case 
              when bi.item_type = 'DEDUCTIONSPF' then 
              bi.credit 
              else
              0
             end as "pf",
             bi.credit as "total"
             from billing.billing_item_details bid
             left join billing.companyaccounts c2 on c2.id  = cast( bid.field_value as uuid) 
             left join billing.billing_item bi  on bi.id  = bid.billingitem 
             left join billing.billing b on b.id  = bi.billing 
             left join pms.cases c on c.id  = b.patient_case
             left join pms.patients p on p.id = c.patient
             where bid.field_name = 'COMPANY_ACCOUNT_ID'
             and bid.field_value = :accountId
             and bi.status  = 'ACTIVE'
        	 and case when :status = 'UNBILLED' then (bi.is_billed_ar is null or bi.is_billed_ar is false) when :status = 'BILLED' then bi.is_billed_ar is true else true end
            ) as billing_insurance
            where
            discharged_date between to_char(cast(:startDate as date),'YYYY-MM-DD')  and to_char(cast(:endDate as date),'YYYY-MM-DD')
            and 
            (
                upper(patient) like concat('%',upper(:filter),'%') or 
                upper(billing_no) like concat('%',upper(:filter),'%')
            )
            group by 
            discharged_date,
            soa,
            billing_no,
            patient,
            icd,
            rvs
            ) as "billing_insurance_pageable"
            where 
            case when :pageSize > 0 then  "recordNo" > :page  and "recordNo" <= (:page+:pageSize) else "recordNo" <= "totalSize" end
        """)
				.setParameter('startDate', startDate)
				.setParameter('endDate', endDate)
				.setParameter('accountId', accountId)
				.setParameter('filter', filter)
				.setParameter('status', status)
				.setParameter('page', page)
				.setParameter('pageSize', pageSize)
				.unwrap(NativeQuery.class)
				.setResultTransformer(Transformers.aliasToBean(BillingDeductionSummary.class))
				.getResultList();
	}


	@GraphQLQuery(name="billingDeductions")
	BillingDeductionSummaryPage billingDeductions(
			@GraphQLArgument(name = "startDate") String startDate,
			@GraphQLArgument(name = "endDate") String endDate,
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "accountId") String accountId,
			@GraphQLArgument(name = "status") String status,
			@GraphQLArgument(name = "pageSize") Integer pageSize,
			@GraphQLArgument(name = "page") Integer page
	){
		BillingDeductionSummaryPage pageable = new BillingDeductionSummaryPage()
		pageable.content = billingDeductionsNativeQuery(startDate,endDate,filter,accountId,status,pageSize,page)
		if(pageable.content.size() > 0){
			pageable.size = pageSize
			pageable.page = page
			pageable.totalSize = pageable.content[0].totalSize
		}
		return pageable
	}


}