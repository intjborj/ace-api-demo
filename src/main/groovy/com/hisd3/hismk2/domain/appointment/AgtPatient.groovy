package com.hisd3.hismk2.domain.appointment

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import com.hisd3.hismk2.graphqlservices.accounting.CoaComponentContainer
import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLQuery
import org.apache.commons.lang3.StringUtils
import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import org.javers.core.metamodel.annotation.DiffIgnore

import javax.persistence.*
import java.time.Instant
import java.time.LocalDate


@Entity
@Table(schema = "appointment", name = "patients")
class AgtPatient extends AbstractAuditingEntity implements Serializable {
	
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
	@Column(name = "his_patient", columnDefinition = "uuid")
	UUID hisPatient


	@GraphQLQuery
	@UpperCase
	@Column(name = "passport", columnDefinition = "varchar")
	String passport

	@GraphQLQuery
	@UpperCase
	@Column(name = "occupation", columnDefinition = "varchar")
	String occupation

	@GraphQLQuery
	@UpperCase
	@Column(name = "work_settings", columnDefinition = "varchar")
	String workSettings
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "first_name", columnDefinition = "varchar")
	String firstName
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "last_name", columnDefinition = "varchar")
	String lastName
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "middle_name", columnDefinition = "varchar")
	String middleName
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "name_suffix", columnDefinition = "varchar")
	String nameSuffix

	@GraphQLQuery
	@UpperCase
	@Column(name = "house_no", columnDefinition = "varchar")
	String houseNo
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "address", columnDefinition = "varchar")
	String address
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "country", columnDefinition = "varchar")
	String country
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "state_province", columnDefinition = "varchar")
	String stateProvince
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "city_municipality", columnDefinition = "varchar")
	String cityMunicipality
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "barangay", columnDefinition = "varchar")
	String barangay
	
	@GraphQLQuery
	@Column(name = "zip_code", columnDefinition = "varchar")
	String zipCode

	@GraphQLQuery
	@UpperCase
	@Column(name = "area_code", columnDefinition = "varchar")
	String areaCode

	@GraphQLQuery
	@UpperCase
	@Column(name = "home_phone", columnDefinition = "varchar")
	String homePhone

	//permanent address

	@GraphQLQuery
	@UpperCase
	@Column(name = "permanent_house_no", columnDefinition = "varchar")
	String permanentHouseNo

	@GraphQLQuery
	@UpperCase
	@Column(name = "permanent_address", columnDefinition = "varchar")
	String permanentAddress

	@GraphQLQuery
	@UpperCase
	@Column(name = "permanent_country", columnDefinition = "varchar")
	String permanentCountry

	@GraphQLQuery
	@UpperCase
	@Column(name = "permanent_state_province", columnDefinition = "varchar")
	String permanentStateProvince

	@GraphQLQuery
	@UpperCase
	@Column(name = "permanent_city_municipality", columnDefinition = "varchar")
	String permanentCityMunicipality

	@GraphQLQuery
	@UpperCase
	@Column(name = "permanent_barangay", columnDefinition = "varchar")
	String permanentBarangay

	@GraphQLQuery
	@Column(name = "permanent_zip_code", columnDefinition = "varchar")
	String permanentZipCode

	@GraphQLQuery
	@UpperCase
	@Column(name = "permanent_area_code", columnDefinition = "varchar")
	String permanentAreaCode

	@GraphQLQuery
	@UpperCase
	@Column(name = "permanent_home_phone", columnDefinition = "varchar")
	String permanentHomePhone
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "pob", columnDefinition = "varchar")
	String pob
	
	@Transient
	String getFullAddress() {
		return StringUtils.defaultString(houseNo)+ ", "+ StringUtils.defaultString(address) + ", " + StringUtils.defaultString(barangay) + " " +
				StringUtils.defaultString(cityMunicipality) + " " + StringUtils.defaultString(stateProvince) +
				" " + StringUtils.defaultString(country) + ", " + StringUtils.defaultString(zipCode)
		
	}
	@GraphQLQuery
	@UpperCase
	@Column(name = "gender", columnDefinition = "varchar")
	String gender
	
	@GraphQLQuery
	@Column(name = "dob", columnDefinition = "date")
	LocalDate dob
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "father", columnDefinition = "varchar")
	String father
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "mother", columnDefinition = "varchar")
	String mother
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "father_occupation", columnDefinition = "varchar")
	String fatherOccupation
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "mother_occupation", columnDefinition = "varchar")
	String motherOccupation
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "civil_status", columnDefinition = "varchar")
	String civilStatus
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "citizenship", columnDefinition = "varchar")
	String citizenship
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "nationality", columnDefinition = "varchar")
	String nationality
	
	@GraphQLQuery
	@Column(name = "email_address", columnDefinition = "varchar")
	String emailAddress

	@GraphQLQuery
	@Column(name = "secret_key", columnDefinition = "varchar")
	String secretKey
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "name_of_spouse", columnDefinition = "varchar")
	String nameOfSpouse
	
	@GraphQLQuery
	@Column(name = "contact_no", columnDefinition = "varchar")
	String contactNo
	
	@GraphQLQuery
	@Column(name = "other_contact", columnDefinition = "varchar")
	String otherContact

	@GraphQLQuery
	@UpperCase
	@Formula("concat(last_name , coalesce(', ' || nullif(first_name,'') , ''), coalesce(' ' || nullif(middle_name,'') , ''), coalesce(' ' || nullif(name_suffix,'') , ''))")
	String fullName
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "religion", columnDefinition = "varchar")
	String religion

	@GraphQLQuery
	@UpperCase
	@Column(name = "spouse_occupation", columnDefinition = "varchar")
	String spouseOccupation

	//work address

	@GraphQLQuery
	@UpperCase
	@Column(name = "work_house_no", columnDefinition = "varchar")
	String workHouseNo

	@GraphQLQuery
	@UpperCase
	@Column(name = "work_address", columnDefinition = "varchar")
	String workAddress

	@GraphQLQuery
	@UpperCase
	@Column(name = "work_country", columnDefinition = "varchar")
	String workCountry

	@GraphQLQuery
	@UpperCase
	@Column(name = "work_state_province", columnDefinition = "varchar")
	String workStateProvince

	@GraphQLQuery
	@UpperCase
	@Column(name = "work_city_municipality", columnDefinition = "varchar")
	String workCityMunicipality

	@GraphQLQuery
	@UpperCase
	@Column(name = "work_barangay", columnDefinition = "varchar")
	String workBarangay

	@GraphQLQuery
	@UpperCase
	@Column(name = "work_place_name", columnDefinition = "varchar")
	String workPlaceName

	@GraphQLQuery
	@Column(name = "work_contact_number", columnDefinition = "varchar")
	String workContactNumber

	@GraphQLQuery
	@Column(name = "work_email_address", columnDefinition = "varchar")
	String workEmailAddress

}
