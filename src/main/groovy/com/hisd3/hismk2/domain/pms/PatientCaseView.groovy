package com.hisd3.hismk2.domain.pms

import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.bms.Room
import com.hisd3.hismk2.domain.dietary.Diet
import com.hisd3.hismk2.domain.hrm.Employee
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*

import javax.persistence.*
import java.time.Instant
import java.time.LocalDate

@javax.persistence.Entity
@javax.persistence.Table(schema = "pms", name = "patient_case_view")
class PatientCaseView implements Serializable {
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "patient_no", columnDefinition = "varchar")
	String patientNo
	
	@GraphQLQuery
	@Column(name = "patient_firstname", columnDefinition = "varchar")
	String firstName
	
	@GraphQLQuery
	@Column(name = "patient_lastname", columnDefinition = "varchar")
	String lastName
	
	@GraphQLQuery
	@Column(name = "patient_middlename", columnDefinition = "varchar")
	String middleName

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "caseid", referencedColumnName = "id")
	Case parentCase
	
	@GraphQLQuery
	@Column(name = "patient_suffix", columnDefinition = "varchar")
	String nameSuffix
	
	@GraphQLQuery
	@Formula("concat(patient_lastname , coalesce(', ' || nullif(patient_firstname,'') , ''), coalesce(' ' || nullif(patient_middlename,'') , ''), coalesce(' ' || nullif(patient_suffix,'') , ''))")
	String fullName
	
	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "primaryphysician", referencedColumnName = "id")
	Employee primaryphysician
	
	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "room", referencedColumnName = "id")
	Room room
	
	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "diet", referencedColumnName = "id")
	Diet diet
	
	@GraphQLQuery
	@Column(name = "discharge_disposition", columnDefinition = "varchar")
	String dischargeDisposition
	
	@GraphQLQuery
	@Column(name = "discharge_condition", columnDefinition = "varchar")
	String dischargeCondition
	
	@GraphQLQuery
	@Column(name = "status", columnDefinition = "varchar")
	String status
	
	@GraphQLQuery
	@Column(name = "registry_type", columnDefinition = "varchar")
	String registryType
	
	@GraphQLQuery
	@Column(name = "service_type", columnDefinition = "varchar")
	String serviceType
	
	@GraphQLQuery
	@Column(name = "transfer_hci", columnDefinition = "varchar")
	String transferHci
	
	@GraphQLQuery
	@Column(name = "originating_hci", columnDefinition = "varchar")
	String originatingHci
	
	@GraphQLQuery
	@Column(name = "transferred_in", columnDefinition = "bool")
	Boolean transferredIn
	
	@GraphQLQuery
	@Column(name = "transferred_out", columnDefinition = "bool")
	Boolean transferredOut
	
	@GraphQLQuery
	@Column(name = "may_go_home_datetime", columnDefinition = "timestamp")
	Instant mayGoHomeDatetime
	
	@GraphQLQuery
	@Column(name = "admission_datetime", columnDefinition = "timestamp")
	Instant admissionDatetime

	@GraphQLQuery
	@Column(name = "entry_datetime", columnDefinition = "timestamp")
	Instant entryDatetime
	
	@GraphQLQuery
	@Column(name = "discharged_datetime", columnDefinition = "timestamp")
	Instant dischargedDatetime
	
	@GraphQLQuery
	@Column(name = "managing_staffs", columnDefinition = "varchar")
	String managingStaffs
	
	@GraphQLQuery
	@Column(name = "comanaging_physicians", columnDefinition = "varchar")
	String comanagingPhysician

	@GraphQLQuery
	@Column(name = "admitting_diagnosis", columnDefinition = "varchar")
	String admittingDiagnosis

	@GraphQLQuery
	@Column(name = "dob", columnDefinition = "date")
	LocalDate dob

	@GraphQLQuery
	@Column(name = "gender", columnDefinition = "varchar")
	String gender

	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "department", referencedColumnName = "id")
	Department department

}
