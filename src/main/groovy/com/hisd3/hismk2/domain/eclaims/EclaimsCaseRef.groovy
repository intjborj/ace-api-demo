package com.hisd3.hismk2.domain.eclaims

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.pms.Case
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*

@Entity
@Table(schema = "eclaims", name = "case_reference")
class EclaimsCaseRef extends AbstractAuditingEntity implements Serializable {
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pt_case", referencedColumnName = "id")
	Case ptCase
	
	@GraphQLQuery
	@Column(name = "reference_id", columnDefinition = "varchar")
	String referenceId

	@GraphQLQuery
	@Column(name = "ext_eligibility", columnDefinition = "uuid")
	UUID eligibility


	@GraphQLQuery
	@Column(name = "ext_claims_id", columnDefinition = "uuid")
	UUID claimsId

	@GraphQLQuery
	@Column(name = "claims_details", columnDefinition = "string")
	String claimsDetails

	@GraphQLQuery
	@Column(name = "ext_cf4_id", columnDefinition = "uuid")
	UUID cf4Id

	@GraphQLQuery
	@Column(name = "cf4_details", columnDefinition = "string")
	String cf4Details

	@GraphQLQuery
	@Column(name = "eligibility_details", columnDefinition = "string")
	String eligibilityDetails


}
