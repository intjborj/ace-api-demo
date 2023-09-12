package com.hisd3.hismk2.domain.cashiering

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.accounting.dto.CompanyDiscountAndPenalties
import com.hisd3.hismk2.domain.annotations.UpperCase
import com.hisd3.hismk2.domain.types.Subaccountable
import com.hisd3.hismk2.graphqlservices.accounting.ChartOfAccountGenerate
import com.hisd3.hismk2.rest.dto.CoaConfig
import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*

enum PaymentTransactionTypeStatus {
	ACTIVE,
	INACTIVE
}

enum MiscType {
	AR,
	OR
}

enum PayorType {
	PATIENT,
	HMO,
	CORPORATE,
	DOCTORS,
	CLINICS,
	EMPLOYEE,
	OTHERS
}

@Canonical
class PaymentTypeAccount implements Serializable{
	String accountType
	String code
	String description
}

@Canonical
class PaymentTypeAccounts {
	PaymentTypeAccount account
	BigDecimal amount
}



@Entity
@Table(name = "payment_transaction_type", schema = "cashiering")
class PaymentTransactionType extends AbstractAuditingEntity implements Serializable{
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@GraphQLQuery
	@UpperCase
	@Column(name = "type_name", columnDefinition = "varchar")
	String typeName
	
	@GraphQLQuery
	@Column(name = "description", columnDefinition = "varchar")
	String description

	@GraphQLQuery
	@Type(type = "jsonb")
	@Column(name="account",columnDefinition = "jsonb")
	List<PaymentTypeAccounts> accounts

	@GraphQLQuery
	@UpperCase
	@Enumerated(EnumType.STRING)
	@Column(name = "payor_type", columnDefinition = "varchar")
	PayorType payorType

	@GraphQLQuery
	@UpperCase
	@Enumerated(EnumType.STRING)
	@Column(name = "misc_type", columnDefinition = "varchar")
	MiscType miscType

	@GraphQLQuery
	@UpperCase
	@Enumerated(EnumType.STRING)
	@Column(name = "status", columnDefinition = "varchar")
	PaymentTransactionTypeStatus status


}
