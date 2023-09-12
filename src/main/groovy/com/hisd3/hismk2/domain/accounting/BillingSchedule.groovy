package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.billing.CompanyAccount
import com.hisd3.hismk2.domain.types.AutoIntegrateable
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type
import org.springframework.data.annotation.CreatedDate

import javax.persistence.*
import java.sql.Date
import java.time.Instant

@Entity
@Table(schema = "accounting", name = "billing_schedule")
class BillingSchedule extends AbstractAuditingEntity implements Serializable, AutoIntegrateable{

	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@GraphQLQuery
	@Column(name = "billing_schedule_no")
	String billingScheduleNo

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id", referencedColumnName = "id")
	CompanyAccount companyAccount

	@GraphQLQuery
	@Column(name = "total_receivable_amount")
	BigDecimal totalReceivableAmount

	@GraphQLQuery
	@Column(name = "patient_type")
	String patientType

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
	@Column(name = "is_voided")
	Boolean isVoided

	@GraphQLQuery
	@OneToMany(mappedBy="billingSchedule", fetch = FetchType.LAZY)
	Set<BillingScheduleItems> billingScheduleItems


	@GraphQLQuery
	@Formula("(Select sum(COALESCE(b.amount,0)) from accounting.billing_schedule_items b where b.type='HCI' and (b.is_voided is null or b.is_voided = false) and b.billing_schedule_id=id)")
	BigDecimal hci

	@GraphQLQuery
	@Formula("(Select sum(COALESCE(b.debit_adjustment,0)) from accounting.billing_schedule_items b where b.type='HCI' and (b.is_voided is null or b.is_voided = false) and b.billing_schedule_id=id)")
	BigDecimal hciWithDebit

	@GraphQLQuery
	@Formula("(Select sum(COALESCE(b.amount,0)) from accounting.billing_schedule_items b where b.type='PF' and (b.is_voided is null or b.is_voided = false) and b.billing_schedule_id=id)")
	BigDecimal pf

	@GraphQLQuery
	@Formula("(Select sum(COALESCE(b.debit_adjustment,0)) from accounting.billing_schedule_items b where b.type='PF' and (b.is_voided is null or b.is_voided = false) and b.billing_schedule_id=id)")
	BigDecimal pfWithDebit

	@GraphQLQuery
	@Formula("(Select sum(COALESCE(b.debit_adjustment,0)) from accounting.billing_schedule_items b where (b.is_voided is null or b.is_voided = false) and b.billing_schedule_id=id)")
	BigDecimal debitAdjustment

//	@GraphQLQuery
//	@Formula("(Select b.ar_no from accounting.account_receivable b INNER JOIN accounting.ar_group a ON b.id = a.account_receivable and a.field_name = 'BILLING_SCHEDULE_ID' where a.field_value=id)")
//	String arNo

	@Transient
	BigDecimal totalAmount
	BigDecimal getTotalAmount() {
		def hospital = 0
		def doctors = 0
		if(hci){
			hospital = hci
		}
		if(pf){
			doctors = pf
		}

		return hospital + doctors
	}

	@Transient
	BigDecimal totalHCI
	BigDecimal getTotalHCI() {
		def hospital = 0
		def debit = 0
		if(hci){
			hospital = hci
		}

		if(hciWithDebit){
			debit = hciWithDebit
		}

		return hospital + debit
	}

	@Transient
	BigDecimal totalPf
	BigDecimal getTotalPf() {
		def doctors = 0
		def debit = 0
		if(pf){
			doctors = pf
		}

		if(pfWithDebit){
			debit = pfWithDebit
		}

		return doctors + debit
	}


	@Transient
	BigDecimal totalAmountWithDebit
	BigDecimal getTotalAmountWithDebit() {
		def hospital = 0
		def doctors = 0

		if(hci){
			hospital = hci
		}

		if(pf){
			doctors = pf
		}

		if(hciWithDebit){
			hospital = hospital + hciWithDebit
		}
		if(pfWithDebit){
			doctors = doctors + pfWithDebit
		}

		return hospital + doctors
	}



	@Override
	String getDomain() {
		return BillingSchedule.class.name
	}

	@Override
	Map<String, String> getDetails() {
		return [:]
	}

	@Transient
	String flagValue


	@Transient
	BigDecimal varA,varB,varC,negativeHciWithDebit,totalHciWithDebit

}
