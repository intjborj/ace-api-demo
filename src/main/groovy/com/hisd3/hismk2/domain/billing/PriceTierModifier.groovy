package com.hisd3.hismk2.domain.billing

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.hrm.Employee
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*

@Entity
@Table(name = "price_tier_modifiers", schema = "billing")
class PriceTierModifier extends AbstractAuditingEntity {
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "price_tier_detail", referencedColumnName = "id")
	PriceTierDetail priceTierDetail
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee", referencedColumnName = "id")
	Employee employee
	
	@GraphQLQuery
	@Column(name = "category_type", columnDefinition = "varchar")
	String categoryType
	
	@GraphQLQuery
	@Column(name = "from_cost", columnDefinition = "numeric")
	BigDecimal fromCost
	
	@GraphQLQuery
	@Column(name = "to_cost", columnDefinition = "numeric")
	BigDecimal toCost
	
	@GraphQLQuery
	@Column(name = "percentage_value", columnDefinition = "numeric")
	BigDecimal percentageValue
	
	@GraphQLQuery
	@Column(name = "test_amount", columnDefinition = "numeric")
	BigDecimal testAmount
}
