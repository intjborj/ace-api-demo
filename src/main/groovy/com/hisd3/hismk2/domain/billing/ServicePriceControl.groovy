package com.hisd3.hismk2.domain.billing

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.ancillary.Service
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*

@Entity
@Table(name = "service_price_controls", schema = "billing")
class ServicePriceControl extends AbstractAuditingEntity {
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "service", referencedColumnName = "id")
	Service service
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "price_tier_detail", referencedColumnName = "id")
	PriceTierDetail priceTierDetail
	
	@GraphQLQuery
	@Column(name = "amount_value", columnDefinition = "numeric")
	BigDecimal amountValue

	@GraphQLQuery
	@Column(name = "percentage_value", columnDefinition = "numeric")
	BigDecimal percentageValue
	
	@GraphQLQuery
	@Column(name = "locked", columnDefinition = "bool")
	Boolean locked
}
