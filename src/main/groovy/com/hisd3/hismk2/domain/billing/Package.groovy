package com.hisd3.hismk2.domain.billing

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*

@Entity
@Table(name = "package", schema = "billing")
class Package extends AbstractAuditingEntity implements Serializable {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "description", columnDefinition = "varchar")
	String description
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "code", columnDefinition = "varchar")
	String code
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "remarks", columnDefinition = "varchar")
	String remarks
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "package_price", columnDefinition = "numeric")
	BigDecimal packagePrice

	@GraphQLQuery
	@UpperCase
	@Column(name = "is_active", columnDefinition = "numeric")
	Boolean isActive = true
	
	/*@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company_account_discount", referencedColumnName = "id")
	CompanyAccount companyAccountDiscount*/
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "discount_target", referencedColumnName = "id")
	Discount discountTarget
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company_account_subsidy", referencedColumnName = "id")
	CompanyAccount companyAccountSubsidy
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = [CascadeType.ALL], orphanRemoval = true)
	List<PackageItem> items
	
}
