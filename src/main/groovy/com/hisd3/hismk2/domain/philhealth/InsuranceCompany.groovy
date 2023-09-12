package com.hisd3.hismk2.domain.philhealth

import com.hisd3.hismk2.domain.types.JaversResolvable
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*

@Entity
@Table(schema = "philhealth", name = "insurance_companies")
class InsuranceCompany implements Serializable , JaversResolvable {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "company_name", columnDefinition = "varchar")
	String companyName
	
	@GraphQLQuery
	@Column(name = "company_address", columnDefinition = "varchar")
	String companyAddress
	
	@GraphQLQuery
	@Column(name = "company_contact_name", columnDefinition = "varchar")
	String companyContactName
	
	@GraphQLQuery
	@Column(name = "company_contact_no", columnDefinition = "varchar")
	String companyContactNo
	
	@GraphQLQuery
	@Column(name = "company_email", columnDefinition = "varchar")
	String companyEmail
	
	@GraphQLQuery
	@Column(name = "company_type", columnDefinition = "varchar")
	String companyType
	
	@GraphQLQuery
	@Column(name = "is_active", columnDefinition = "boolean")
	Boolean isActive

	@Override
	String resolveEntityForJavers() {
		return companyContactName
	}
}
