package com.hisd3.hismk2.domain.inventory

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.Department
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*

import javax.persistence.*
import java.time.Instant

@javax.persistence.Entity
@javax.persistence.Table(schema = "inventory", name = "physical_count")
@SQLDelete(sql = "UPDATE inventory.physical_count SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class PhysicalCount extends AbstractAuditingEntity {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "ref_no")
	String refNo
	
	@GraphQLQuery
	@Column(name = "date_trans")
	Instant dateTrans
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item", referencedColumnName = "id")
	Item item
	
	@GraphQLQuery
	@Column(name = "expiration_date")
	Instant expiration_date
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "department", referencedColumnName = "id")
	Department department

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "physical_count_transaction", referencedColumnName = "id")
	PhysicalTransaction physicalTransaction
	
	@GraphQLQuery
	@Column(name = "on_hand")
	Integer onHand
	
	@GraphQLQuery
	@Column(name = "quantity")
	Integer quantity
	
	@GraphQLQuery
	@Column(name = "variance")
	Integer variance
	
	@GraphQLQuery
	@Column(name = "unit_cost")
	BigDecimal unitCost
	
	@GraphQLQuery
	@Column(name = "wcost")
	BigDecimal wcost
	
	@GraphQLQuery
	@Column(name = "is_posted")
	Boolean isPosted

	@GraphQLQuery
	@Column(name = "is_cancel")
	Boolean isCancel
	
	@GraphQLQuery
	@Column(name = "ref_ledger_id")
	UUID refLedgerId

	@GraphQLQuery
	@Formula("(Select COALESCE(sum(q.log_count), 0) from inventory.physical_logs_count q where q.physical_count=id)")
	Integer monthlyCount
}
