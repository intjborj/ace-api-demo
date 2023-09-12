package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.User
import com.hisd3.hismk2.domain.billing.CompanyAccount
import com.hisd3.hismk2.domain.billing.PriceTierDetail
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.types.AutoIntegrateable
import com.hisd3.hismk2.rest.dto.JournalEntryViewDto
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type
//import org.joda.time.DateTime
import org.springframework.data.annotation.CreatedDate

import javax.persistence.*
import java.sql.Date
import java.time.Instant



enum AccReceivableGroupParam {
	COMPANY_ACCOUNT_ID,
	PERSONAL_ACCOUNT_ID,
	BILLING_SCHEDULE_ID,
	TRANSACTION_ID,
	CREDIT_MEMO_ID
}



@Entity
@Table(schema = "accounting", name = "account_receivable")
class AccountReceivable extends AbstractAuditingEntity implements Serializable, AutoIntegrateable{

	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@GraphQLQuery
	@Column(name = "ar_no")
	String arNo

	@GraphQLQuery
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(schema = "accounting", name = "ar_group",
			joinColumns = [@JoinColumn(name = "account_receivable")])
	@MapKeyColumn(name = "field_name")
	@Column(name = "field_value")
	@BatchSize(size = 20)
	Map<String, UUID> groups = [:]

	@GraphQLQuery
	@Column(name = "posted_ledger", columnDefinition = "uuid")
	UUID postedLedger

	@GraphQLQuery
	@Column(name = "reference_no")
	String referenceNo

	@GraphQLQuery
	@Column(name = "status")
	String status

	@GraphQLQuery
	@Column(name = "remarks")
	String remarks

	@GraphQLQuery
	@Column(name = "transaction_date")
	Date transactionDate

	@GraphQLQuery
	@Column(name = "due_date")
	Date dueDate

	@GraphQLQuery
	@Formula("(Select sum(COALESCE(b.debit,0)) from accounting.account_receivable_items b where b.type='HCI' and b.account_receivable_id=id)")
	BigDecimal hci

	@GraphQLQuery
	@Formula("(Select sum(COALESCE(b.debit,0)) from accounting.account_receivable_items b where b.type='PF' and b.account_receivable_id=id)")
	BigDecimal pf

	@GraphQLQuery
	@Formula("(Select sum(COALESCE(b.debit,0)) from accounting.account_receivable_items b where b.type='OTHER' and b.account_receivable_id=id)")
	BigDecimal other

	@GraphQLQuery
	@Formula("(Select sum(COALESCE(b.debit,0)) from accounting.account_receivable_items b where b.account_receivable_id=id)")
	BigDecimal totals

	@GraphQLQuery
	@Formula("(Select sum(COALESCE(b.debit,0) - COALESCE(b.credit,0)) from accounting.account_receivable_items b where b.account_receivable_id=id)")
	BigDecimal balance

	@GraphQLQuery
	@Formula("(Select sum(COALESCE(b.credit,0)) from accounting.account_receivable_items b where b.account_receivable_id=id)")
	BigDecimal credit

	@GraphQLQuery
	@Formula("(Select b.billing_schedule_no from accounting.billing_schedule b INNER JOIN accounting.ar_group a ON b.id = a.field_value and a.field_name = 'BILLING_SCHEDULE_ID' and a.account_receivable=id)")
	String billingScheduleNo

	@GraphQLQuery
	@Formula("(Select b.companyname from billing.companyaccounts b INNER JOIN accounting.ar_group a ON b.id = a.field_value and a.field_name = 'COMPANY_ACCOUNT_ID' and a.account_receivable=id)")
	String companyName

	@GraphQLQuery
	@Formula("(Select b.supplier_fullname from inventory.supplier b INNER JOIN accounting.ar_group a ON b.id = a.field_value and a.field_name = 'PERSONAL_ACCOUNT_ID' and a.account_receivable=id)")
	String contactNamePA

	@GraphQLQuery
	@Formula("(Select sum(COALESCE(b.amount,0)) from accounting.ar_transaction b where b.type='payments' and b.status = 'ACTIVE' and b.account_receivable_id=id)")
	BigDecimal payment

	@GraphQLQuery
	@Formula("(Select sum(COALESCE(b.amount,0)) from accounting.ar_transaction b where b.type='memo' and b.status = 'ACTIVE' and b.account_receivable_id=id)")
	BigDecimal memo

	@GraphQLQuery
	@OneToMany(mappedBy="accountReceivable")
	@OrderBy("recordNo")
	Set<AccountReceivableItems> accountReceivableItems


	@GraphQLQuery
	@OneToMany(mappedBy="accountReceivable")
	@OrderBy("createdDate")
	Set<ArTransaction> arTransactions


	@Override
	String getDomain() {
		return AccountReceivable.class.name
	}

	@Override
	Map<String, String> getDetails() {
		return [:]
	}

	@Transient
	String flagValue

	@Transient
	BigDecimal negativeTotalAmount
	BigDecimal getNegativeTotalAmount() {
		def b = -totals
		return b
	}

	@Transient
	BigDecimal negativeHciAmount
	BigDecimal getNegativeHciAmount() {
		def b = 0
		if(hci){
			b = -hci
		}
		return b
	}

	@Transient
	BigDecimal negativePfAmount
	BigDecimal getNegativePfAmount() {
		def b = -pf
		return b
	}

	@Transient
	BigDecimal varA,varB,varC

	@Transient
	BigDecimal electricity,rental,taxAmount,receivableAmt,affiliationFee

	@Transient
	CompanyAccount companyAccount

	@Transient
	String yearList


}
