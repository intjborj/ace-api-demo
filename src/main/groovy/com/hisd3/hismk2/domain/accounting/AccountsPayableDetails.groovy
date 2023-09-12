package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.annotations.UpperCase
import com.hisd3.hismk2.domain.inventory.PaymentTerm
import com.hisd3.hismk2.domain.inventory.ReceivingReport
import com.hisd3.hismk2.domain.inventory.Supplier
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where

import javax.persistence.*
import java.time.Instant

@javax.persistence.Entity
@javax.persistence.Table(name = "payables_detials", schema = "accounting")
@SQLDelete(sql = "UPDATE accounting.payables_detials SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class AccountsPayableDetails extends AbstractAuditingEntity implements Serializable {

	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "payables", referencedColumnName = "id")
	AccountsPayable accountsPayable

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "trans_type", referencedColumnName = "id")
	ApTransaction transType

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "department", referencedColumnName = "id")
	Department department
	
	@GraphQLQuery
	@Column(name = "amount", columnDefinition = "numeric")
	@UpperCase
	BigDecimal amount

	@GraphQLQuery
	@Column(name = "disc_rate", columnDefinition = "numeric")
	@UpperCase
	BigDecimal discRate

	@GraphQLQuery
	@Column(name = "disc_amount", columnDefinition = "numeric")
	@UpperCase
	BigDecimal discAmount

	@GraphQLQuery
	@Column(name = "vat_inclusive", columnDefinition = "bool")
	@UpperCase
	Boolean vatInclusive

	@GraphQLQuery
	@Column(name = "vat_amount", columnDefinition = "numeric")
	@UpperCase
	BigDecimal vatAmount

	@GraphQLQuery
	@Column(name = "tax_description", columnDefinition = "varchar")
	@UpperCase
	String taxDesc

	@GraphQLQuery
	@Column(name = "ewt_rate", columnDefinition = "numeric")
	@UpperCase
	BigDecimal ewtRate

	@GraphQLQuery
	@Column(name = "ewt_amount", columnDefinition = "numeric")
	@UpperCase
	BigDecimal ewtAmount

	@GraphQLQuery
	@Column(name = "net_amount", columnDefinition = "numeric")
	@UpperCase
	BigDecimal netAmount

	@GraphQLQuery
	@Column(name = "remarks_notes", columnDefinition = "varchar")
	@UpperCase
	String remarksNotes

	@GraphQLQuery
	@Column(name = "ref_id", columnDefinition = "uuid")
	UUID refId

	@GraphQLQuery 	
	@Column(name = "ref_no", columnDefinition = "varchar")
	@UpperCase
	String refNo

	@GraphQLQuery
	@Column(name = "source", columnDefinition = "varchar")
	String source
	
}

