package com.hisd3.hismk2.domain.cashiering

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.accounting.Bank
import com.hisd3.hismk2.domain.annotations.UpperCase
import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant

enum PaymentType {
	CASH,
	CHECK,
	CARD, //CREDIT/DEBIT
	BANKDEPOSIT,
	EWALLET
}

@Canonical
class OtherDetails implements Serializable{
	String invoiceNo
	String traceNo
}


@Entity
@Table(name = "payment_tracker_details", schema = "cashiering")
class PaymentTrackerDetails extends AbstractAuditingEntity implements Serializable {
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_tracker", referencedColumnName = "id")
	PaymentTracker paymentTracker
	
	@GraphQLQuery
	@Column(name = "amount", columnDefinition = "numeric")
	BigDecimal amount
	
	@GraphQLQuery
	@Enumerated(EnumType.STRING)
	@Column(name = "[type]", columnDefinition = "varchar")
	PaymentType type
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "reference", columnDefinition = "varchar")
	String reference
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "check_date", columnDefinition = "varchar")
	String checkdate // or expiry
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "bank", columnDefinition = "varchar")
	String bank
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "name_of_card", columnDefinition = "varchar")
	String nameOfCard
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "card_type", columnDefinition = "varchar")
	String cardType
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "approval_code", columnDefinition = "varchar")
	String approvalCode
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "pos_terminal_id", columnDefinition = "varchar")
	String posTerminalId
	
	@GraphQLQuery
	@Column(name = "cleared", columnDefinition = "boolean")
	Boolean cleared
	
	@GraphQLQuery
	@Column(name = "denied", columnDefinition = "boolean")
	Boolean denied
	
	@GraphQLQuery
	@Column(name = "cleareddate", columnDefinition = "timestamp")
	Instant cleareddate
	
	@GraphQLQuery
	@Column(name = "denieddate", columnDefinition = "timestamp")
	Instant denieddate
	
	@GraphQLQuery
	@Column(name = "reconcile_id", columnDefinition = "uuid")
	UUID reconcileId
	
	@GraphQLQuery
	@Column(name = "reconcile_date", columnDefinition = "timestamp")
	Instant reconcileDate
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bank_id", referencedColumnName = "id")
	Bank bankEntity

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "collection_detail", referencedColumnName = "id")
	CollectionDetail collectionDetail

	@GraphQLQuery
	@Column(name = "ignored", columnDefinition = "boolean")
	Boolean ignored

	@GraphQLQuery
	@Type(type = "jsonb")
	@Column(name="other_details",columnDefinition = "jsonb")
	OtherDetails otherDetails

	@GraphQLQuery
	@Column(name = "deposit_date", columnDefinition = "date")
	Instant depositDate

	@GraphQLQuery
	@Column(name = "status", columnDefinition = "varchar")
	String status

}



