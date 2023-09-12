package com.hisd3.hismk2.domain.pms

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.pms.Case
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*

import javax.persistence.*
import java.time.Instant

@javax.persistence.Entity
@javax.persistence.Table(schema = "pms", name = "patient_philhealth_data")
class PatientPhilhealthData extends AbstractAuditingEntity {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "`case`", referencedColumnName = "id")
	Case parentCase
	
	@GraphQLQuery
	@Column(name = "member_lastname", columnDefinition = "varchar")
	String memberLastName
	
	@GraphQLQuery
	@Column(name = "member_firstname", columnDefinition = "varchar")
	String memberFirstName
	
	@GraphQLQuery
	@Column(name = "member_middlename", columnDefinition = "varchar")
	String memberMiddleName
	
	@GraphQLQuery
	@Column(name = "member_suffix", columnDefinition = "varchar")
	String memberSuffix
	
	@GraphQLQuery
	@Column(name = "member_gender", columnDefinition = "varchar")
	String memberGender
	
	@GraphQLQuery
	@Column(name = "member_relation", columnDefinition = "varchar")
	String memberRelation
	
	@GraphQLQuery
	@Column(name = "member_dob", columnDefinition = "date")
	Instant memberDob
	
	@GraphQLQuery
	@Column(name = "member_address", columnDefinition = "varchar")
	String memberAddress
	
	@GraphQLQuery
	@Column(name = "member_country", columnDefinition = "varchar")
	String memberCountry
	
	@GraphQLQuery
	@Column(name = "member_state_province", columnDefinition = "varchar")
	String memberStateProvince
	
	@GraphQLQuery
	@Column(name = "member_city_municipality", columnDefinition = "varchar")
	String memberCityMunicipality
	
	@GraphQLQuery
	@Column(name = "member_barangay", columnDefinition = "varchar")
	String memberBarangay
	
	@GraphQLQuery
	@Column(name = "member_zip_code", columnDefinition = "varchar")
	String memberZipCode
	
	@GraphQLQuery
	@Column(name = "member_pin", columnDefinition = "varchar")
	String memberPin
	
	@GraphQLQuery
	@Column(name = "member_type", columnDefinition = "varchar")
	String memberType
	
	@GraphQLQuery
	@Column(name = "member_civil_status", columnDefinition = "varchar")
	String memberCivilStatus
	
	@GraphQLQuery
	@Formula("concat(member_lastname , coalesce(', ' || nullif(member_firstname,'') , ''), coalesce(' ' || nullif(member_middlename,'') , ''), coalesce(' ' || nullif(member_suffix,'') , ''))")
	String fullName
	
}
