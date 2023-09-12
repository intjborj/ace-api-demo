package com.hisd3.hismk2.domain.billing

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import com.hisd3.hismk2.domain.types.Subaccountable
import com.hisd3.hismk2.rest.dto.CoaConfig
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*

enum DiscountType {
	CUSTOM,
	FIXED
}

@Entity
@Table(name = "discounts", schema = "billing")
class Discount extends AbstractAuditingEntity implements Subaccountable, Serializable {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "code", columnDefinition = "varchar")
	String code
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "discount", columnDefinition = "varchar")
	String discount
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "remarks", columnDefinition = "varchar")
	String remarks
	
	@GraphQLQuery
	@Column(name = "value", columnDefinition = "numeric")
	BigDecimal value
	
	@Enumerated(value = EnumType.STRING)
	@Column(name = "type", columnDefinition = "varchar")
	DiscountType type
	
	@GraphQLQuery
	@Column(name = "applyhci", columnDefinition = "bool")
	Boolean applyhci
	
	@GraphQLQuery
	@Column(name = "applypf", columnDefinition = "bool")
	Boolean applypf
	
	@GraphQLQuery
	@Column(name = "from_initial", columnDefinition = "bool")
	Boolean fromInitial


	@GraphQLQuery
	@Column(name = "active", columnDefinition = "bool")
	Boolean active

	@GraphQLQuery
	@Column(name = "include_vat", columnDefinition = "bool")
	Boolean includeVat



	@GraphQLQuery
	@Column(name = "vat", columnDefinition = "bool")
	Boolean vat
	
	@GraphQLQuery
	@Column(name = "validation_source", columnDefinition = "varchar")
	@UpperCase
	String validationSource

	@Override
	String getDescription() {
		return discount
	}

	@Override
	String getDomain() {
		return Discount.class.name
	}

	@Override
	List<UUID> getDepartment() {
		return null
	}

	@Override
	CoaConfig getConfig() {
		new CoaConfig(show: true, showDepartments: true)
	}
}
