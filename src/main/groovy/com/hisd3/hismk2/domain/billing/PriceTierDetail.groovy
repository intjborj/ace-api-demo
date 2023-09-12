package com.hisd3.hismk2.domain.billing

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.Department
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type
import org.springframework.data.annotation.CreatedDate

import javax.persistence.*
import java.time.Instant

@Entity
@Table(name = "price_tier_details", schema = "billing")
class PriceTierDetail extends AbstractAuditingEntity implements Serializable {
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "tier_code", columnDefinition = "varchar")
	String tierCode
	
	@GraphQLQuery
	@Column(name = "description", columnDefinition = "varchar")
	String description
	
	@GraphQLQuery
	@Column(name = "registry_type", columnDefinition = "varchar")
	String registryType
	
	@GraphQLQuery
	@Column(name = "accommodation_type", columnDefinition = "varchar")
	String accommodationType
	
	@GraphQLQuery
	@Column(name = "room_types", columnDefinition = "varchar")
	String roomTypes
	
	@GraphQLQuery
	@CreatedDate
	@Column(name = "from_datetime", nullable = false)
	Instant fromDatetime
	
	@GraphQLQuery
	@CreatedDate
	@Column(name = "to_datetime", nullable = false)
	Instant toDatetime
	
	@GraphQLQuery
	@Column(name = "is_vatable", columnDefinition = "boolean")
	Boolean isVatable
	
	@GraphQLQuery
	@Column(name = "vat_rate", columnDefinition = "numeric")
	BigDecimal vatRate
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "department", referencedColumnName = "id")
	Department department
	
	@GraphQLQuery
	@Column(name = "for_senior", columnDefinition = "boolean")
	Boolean forSenior

	@GraphQLQuery
	@Column(name = "target_audience", columnDefinition = "varchar")
	String targetAudience

	@GraphQLQuery
	@Column(name = "oct_use", columnDefinition = "boolean")
	Boolean octUse

	@GraphQLQuery
	@Column(name = "departments", columnDefinition = "text")
	String departments



}
