package com.hisd3.hismk2.domain.pms

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.philhealth.InsuranceCompany
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*

@TypeChecked
@Entity
@Table(schema = "pms", name = "case_insurances")
class CaseInsurance extends AbstractAuditingEntity {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_case", referencedColumnName = "id")
	Case parentCase
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "insurance_company", referencedColumnName = "id")
	InsuranceCompany insuranceCompany
	
	@GraphQLQuery
	@Column(name = "reference_no", columnDefinition = "varchar")
	String referenceNo
	
	@GraphQLQuery
	@Column(name = "covered_amount", columnDefinition = "numeric")
	BigDecimal coveredAmount
}
