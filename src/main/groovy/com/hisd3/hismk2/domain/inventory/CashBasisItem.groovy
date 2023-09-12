package com.hisd3.hismk2.domain.inventory

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.pms.Medication
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*

@Entity
@Table(schema = "inventory", name = "cash_basis_item")
class CashBasisItem extends AbstractAuditingEntity {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@GraphQLQuery
	@ManyToOne(fetch = FetchType.EAGER)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "cash_basis_id", referencedColumnName = "id")
	CashBasis cashBasis
	
	@GraphQLQuery
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "item_id", referencedColumnName = "id")
	Item item

	@GraphQLQuery
	@Column(name = "or_number")
	String orNumber

	@GraphQLQuery
	@Column(name = "billing_item_no")
	String billingItemNo

	@GraphQLQuery
	@Column(name = "sri_number")
	String sriNumber

	@GraphQLQuery
	@Column(name = "quantity")
	BigDecimal quantity

	@GraphQLQuery
	@Column(name = "price")
	BigDecimal price

	@GraphQLQuery
	@Column(name = "type")
	String type
}
