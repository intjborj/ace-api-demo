package com.hisd3.hismk2.domain.inventory

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.types.AutoIntegrateable
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant

@Entity
@Table(schema = "inventory", name = "inventory_ledger")
class InventoryLedger extends AbstractAuditingEntity implements AutoIntegrateable{
	
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
	@JoinColumn(name = "source_dep", referencedColumnName = "id")
	Department sourceDep
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "destination_dep", referencedColumnName = "id")
	Department destinationDep
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "document_types", referencedColumnName = "id")
	DocumentTypes documentTypes // for expense 0f3c2b76-445a-4f78-a256-21656bd62872
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item", referencedColumnName = "id")
	Item item
	
	@GraphQLQuery
	@Column(name = "reference_no")
	String referenceNo
	
	@GraphQLQuery
	@Column(name = "ledger_date")
	Instant ledgerDate
	
	@GraphQLQuery
	@Column(name = "ledger_qty_in")
	Integer ledgerQtyIn
	
	@GraphQLQuery
	@Column(name = "ledger_qty_out")
	Integer ledgerQtyOut
	
	@GraphQLQuery
	@Column(name = "ledger_physical")
	Integer ledgerPhysical
	
	@GraphQLQuery
	@Column(name = "ledger_unit_cost")
	BigDecimal ledgerUnitCost

	@GraphQLQuery
	@Column(name = "is_include")
	Boolean isInclude

	@GraphQLQuery
	@Column(name = "posted_ledger", columnDefinition = "uuid")
	UUID postedLedger

	@GraphQLQuery
	@Column(name = "canceled_ref", columnDefinition = "uuid")
	UUID canceledref

	@GraphQLQuery
	@Column(name = "billing", columnDefinition = "uuid")
	UUID billing

	@GraphQLQuery
	@Column(name = "billing_item", columnDefinition = "uuid")
	UUID billingItem

	@Override
	String getDomain() {
		return InventoryLedger.class.name
	}

	@Override
	Map<String, String> getDetails() {
		return [:]
	}
	@Transient
	String flagValue
}
