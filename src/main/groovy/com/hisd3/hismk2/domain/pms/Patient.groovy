package com.hisd3.hismk2.domain.pms

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.apache.commons.lang3.StringUtils
import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import org.javers.core.metamodel.annotation.DiffIgnore

import javax.persistence.*
import java.time.LocalDate


@Entity
@Table(schema = "pms", name = "patients")
class Patient extends AbstractAuditingEntity {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@DiffIgnore
	@GraphQLQuery
	@Column(name = "patient_no", columnDefinition = "varchar")
	String patientNo
	
	@GraphQLQuery
	@Column(name = "first_name", columnDefinition = "varchar")
	String firstName
	
	@GraphQLQuery
	@Column(name = "last_name", columnDefinition = "varchar")
	String lastName
	
	@GraphQLQuery
	@Column(name = "middle_name", columnDefinition = "varchar")
	String middleName
	
	@GraphQLQuery
	@Column(name = "name_suffix", columnDefinition = "varchar")
	String nameSuffix
	
	@GraphQLQuery
	@Column(name = "address", columnDefinition = "varchar")
	String address
	
	@GraphQLQuery
	@Column(name = "country", columnDefinition = "varchar")
	String country
	
	@GraphQLQuery
	@Column(name = "state_province", columnDefinition = "varchar")
	String stateProvince
	
	@GraphQLQuery
	@Column(name = "city_municipality", columnDefinition = "varchar")
	String cityMunicipality
	
	@GraphQLQuery
	@Column(name = "barangay", columnDefinition = "varchar")
	String barangay
	
	@GraphQLQuery
	@Column(name = "zip_code", columnDefinition = "varchar")
	String zipCode
	
	@GraphQLQuery
	@Column(name = "pob", columnDefinition = "varchar")
	String pob
	
	@Transient
	String getFullAddress() {
		return StringUtils.defaultString(address) + ", " + StringUtils.defaultString(barangay) + " " +
				StringUtils.defaultString(cityMunicipality) + " " + StringUtils.defaultString(stateProvince) +
				" " + StringUtils.defaultString(country) + ", " + StringUtils.defaultString(zipCode)
		
	}
	@GraphQLQuery
	@Column(name = "gender", columnDefinition = "varchar")
	String gender
	
	@GraphQLQuery
	@Column(name = "dob", columnDefinition = "date")
	LocalDate dob
	
	@GraphQLQuery
	@Column(name = "allergies", columnDefinition = "varchar")
	String allergies
	
	@GraphQLQuery
	@Column(name = "father", columnDefinition = "varchar")
	String father
	
	@GraphQLQuery
	@Column(name = "mother", columnDefinition = "varchar")
	String mother
	
	@GraphQLQuery
	@Column(name = "father_occupation", columnDefinition = "varchar")
	String fatherOccupation
	
	@GraphQLQuery
	@Column(name = "mother_occupation", columnDefinition = "varchar")
	String motherOccupation
	
	@GraphQLQuery
	@Column(name = "civil_status", columnDefinition = "varchar")
	String civilStatus
	
	@GraphQLQuery
	@Column(name = "citizenship", columnDefinition = "varchar")
	String citizenship
	
	@GraphQLQuery
	@Column(name = "nationality", columnDefinition = "varchar")
	String nationality
	
	@GraphQLQuery
	@Column(name = "email_address", columnDefinition = "varchar")
	String emailAddress
	
	@GraphQLQuery
	@Column(name = "name_of_spouse", columnDefinition = "varchar")
	String nameOfSpouse
	
	@GraphQLQuery
	@Column(name = "contact_no", columnDefinition = "varchar")
	String contactNo
	
	@GraphQLQuery
	@Column(name = "other_contact", columnDefinition = "varchar")
	String otherContact
	
	@GraphQLQuery
	@Column(name = "osca_id", columnDefinition = "varchar")
	String oscaId

	@GraphQLQuery
	@Column(name = "owwa_id", columnDefinition = "varchar")
	String owwaId
	
	@GraphQLQuery
	@Column(name = "philhealth_id", columnDefinition = "varchar")
	String philHealthId

	@GraphQLQuery
	@Formula("concat(last_name , coalesce(', ' || nullif(first_name,'') , ''), coalesce(' ' || nullif(middle_name,'') , ''), coalesce(' ' || nullif(name_suffix,'') , ''))")
	String fullName
	
	@GraphQLQuery
	@Column(name = "age", columnDefinition = "integer")
	Integer age
	
	@GraphQLQuery
	@Column(name = "religion", columnDefinition = "varchar")
	String religion

	@GraphQLQuery
	@Column(name = "spouse_occupation", columnDefinition = "varchar")
	String spouseOccupation

	@GraphQLQuery
	@Column(name = "family_history", columnDefinition = "text")
	String familyHistory

	@GraphQLQuery
	@Column(name = "personal_social_history", columnDefinition = "text")
	String personalSocialHistory
}
