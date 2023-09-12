package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.annotations.UpperCase
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.domain.types.AutoIntegrateable
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant

@javax.persistence.Entity
@javax.persistence.Table(name = "petty_cash_purchases", schema = "accounting")
class PettyCashItem extends AbstractAuditingEntity implements Serializable {

	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "item", referencedColumnName = "id")
	Item item

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "department", referencedColumnName = "id")
	Department department

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "petty_cash", referencedColumnName = "id")
	PettyCash pettyCash

	@GraphQLQuery
	@Column(name = "qty", columnDefinition = "int")
	Integer qty

	@GraphQLQuery
	@Column(name = "unit_cost", columnDefinition = "numeric")
	BigDecimal unitCost

	@GraphQLQuery
	@Column(name = "disc_rate", columnDefinition = "numeric")
	BigDecimal discRate

	@GraphQLQuery
	@Column(name = "disc_amount", columnDefinition = "numeric")
	BigDecimal discAmount

	@GraphQLQuery
	@Column(name = "net_amount", columnDefinition = "numeric")
	BigDecimal netAmount

	@GraphQLQuery
	@Column(name = "is_vat", columnDefinition = "bool")
	Boolean isVat

	@GraphQLQuery
	@Column(name = "vat_amount", columnDefinition = "numeric")
	BigDecimal vatAmount

}

