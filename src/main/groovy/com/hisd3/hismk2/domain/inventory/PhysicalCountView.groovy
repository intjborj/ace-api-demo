package com.hisd3.hismk2.domain.inventory

import com.hisd3.hismk2.domain.Department
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant

@Entity
@Table(schema = "inventory", name = "view_physical_count")
class PhysicalCountView implements Serializable{
	
	@GraphQLQuery
	@Id
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "date_trans")
	Instant dateTrans
	
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
	@Column(name = "item")
	UUID itemId

	@GraphQLQuery
	@Column(name = "sku")
	String sku
	
	@GraphQLQuery
	@Column(name = "desc_long")
	String descLong
	
	@GraphQLQuery
	@Column(name = "unit_of_purchase")
	String unit_of_purchase
	
	@GraphQLQuery
	@Column(name = "unit_of_usage")
	String unit_of_usage
	
	@GraphQLQuery
	@Column(name = "category_description")
	String category_description
	
	@GraphQLQuery
	@Column(name = "expiration_date")
	Instant expiration_date
	
	@GraphQLQuery
	@Column(name = "on_hand")
	Integer onHand
	
	@GraphQLQuery
	@Column(name = "monthlyCount")
	Integer monthlyCount
	
	@GraphQLQuery
	@Column(name = "variance")
	Integer variance
	
	@GraphQLQuery
	@Column(name = "unit_cost")
	BigDecimal unitCost

	@GraphQLQuery
	@Column(name = "is_posted")
	Boolean isPosted

	@GraphQLQuery
	@Column(name = "is_cancel")
	Boolean isCancel
	
}
