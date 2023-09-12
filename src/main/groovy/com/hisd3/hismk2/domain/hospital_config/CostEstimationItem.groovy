package com.hisd3.hismk2.domain.hospital_config

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*

@Entity
@Table(schema = "hospital_configuration", name = "cost_estimation_item")
class CostEstimationItem extends AbstractAuditingEntity {
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cost_estimation", referencedColumnName = "id")
	CostEstimation costEstimation
	
	@GraphQLQuery
	@Column(name = "type", columnDefinition = "varchar")
	String type
	
	@GraphQLQuery
	@Column(name = "ref_id", columnDefinition = "varchar")
	String refId
	
	@GraphQLQuery
	@Column(name = "amount", columnDefinition = "numeric")
	BigDecimal amount
}
