package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import com.hisd3.hismk2.domain.inventory.Supplier
import com.hisd3.hismk2.domain.inventory.SupplierType
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
@javax.persistence.Table(name = "ap_ledger", schema = "accounting")
@SQLDelete(sql = "UPDATE accounting.ap_ledger SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class ApLedger extends AbstractAuditingEntity implements Serializable {

	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "supplier", referencedColumnName = "id")
	Supplier supplier

	@GraphQLQuery
	@Column(name = "ledger_type", columnDefinition = "varchar")
	@UpperCase
	String ledgerType

	@GraphQLQuery
	@Column(name = "ledger_date", columnDefinition = "date")
	Instant ledgerDate

	@GraphQLQuery
	@Column(name = "ref_no", columnDefinition = "varchar")
	String refNo

	@GraphQLQuery
	@Column(name = "ref_id", columnDefinition = "uuid")
	UUID refId

	@GraphQLQuery
	@Column(name = "debit", columnDefinition = "numeric")
	BigDecimal debit

	@GraphQLQuery
	@Column(name = "credit", columnDefinition = "numeric")
	BigDecimal credit

	@GraphQLQuery
	@Column(name = "is_include", columnDefinition = "bool")
	Boolean isInclude

	
}

