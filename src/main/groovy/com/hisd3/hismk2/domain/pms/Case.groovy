package com.hisd3.hismk2.domain.pms

import com.fasterxml.jackson.annotation.JsonBackReference
import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.User
import com.hisd3.hismk2.domain.billing.PriceTierDetail
import com.hisd3.hismk2.domain.bms.Room
import com.hisd3.hismk2.domain.dietary.Diet
import com.hisd3.hismk2.domain.hospital_config.ClinicDoctor
import com.hisd3.hismk2.domain.hospital_config.Physician
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.philhealth.InsuranceCompany
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type
import org.javers.core.metamodel.annotation.DiffIgnore
import org.javers.core.metamodel.annotation.ShallowReference

import javax.persistence.*
import java.time.Instant

enum CaseServiceType {
	MEDICINE(1, 'MEDICINE'),
	OBSTETRICS(2, 'OBSTETRICS'),
	GYNECOLOGY(3, 'GYNECOLOGY'),
	PEDIATRICS(4, 'PEDIATRICS'),
	SURGICAL_PEDIA(5, 'SURGICAL-PEDIA'),
	SURGICAL_ADULT(6, 'SURGICAL-ADULT'),
	OTHERS(7, 'OTHERS'),
	PATHOLOGIC(8, 'PATHOLOGIC'),
	NON_PATHOLOGIC_WELL_BABY(9, 'NON-PATHOLOGIC/WELL-BABY')
	
	private final Integer value
	private final String status
	
	private CaseServiceType(Integer value, String status) {
		this.value = value
		this.status = status
	}
	
	Integer getValue() {
		return value
	}
	
	String getStatus() {
		return status
	}
}

@TypeChecked
@Entity
@Table(schema = "pms", name = "cases")
class Case extends AbstractAuditingEntity implements Serializable {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	// Ignore in Versioning because patient will never change anyway
	@DiffIgnore
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "patient", referencedColumnName = "id")
	Patient patient
	
	@DiffIgnore
	@GraphQLQuery
	@Column(name = "case_no", columnDefinition = "varchar")
	String caseNo
	
	@ShallowReference
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "attending_physician", referencedColumnName = "id")
	Employee attendingPhysician
	
	@ShallowReference
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "admitting_physician", referencedColumnName = "id")
	Employee admittingPhysician
	
	@ShallowReference
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "admitting_officer", referencedColumnName = "id")
	Employee admittingOfficer
	
	@ShallowReference
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "room", referencedColumnName = "id")
	Room room
	
	@ShallowReference
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "department", referencedColumnName = "id")
	Department department

	@ShallowReference
	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "consulting_physician", referencedColumnName = "id")
	Employee consultingPhysician

	@ShallowReference
	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "consulting_physician_trs", referencedColumnName = "id")
	Employee consultingPhysicianTransfer

	@ShallowReference
	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "price_tier_detail", referencedColumnName = "id")
	PriceTierDetail priceTierDetail
	
	@ShallowReference
	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "diet", referencedColumnName = "id")
	Diet diet
	
	@ShallowReference
	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hmo_company", referencedColumnName = "id")
	InsuranceCompany hmoCompany
	
	@ShallowReference
	@GraphQLQuery
	@OneToOne(mappedBy = "aCase")
	@JsonBackReference
	ObgynHistory obgynHistory
	
	@GraphQLQuery
	@Column(name = "status", columnDefinition = "varchar")
	String status = "ACTIVE"
	
	/*@PrePersist
	void preInsert() {
		if (this.status == null)
			this.status = 'ACTIVE'
	}*/
	
	@GraphQLQuery
	@Column(name = "service_type", columnDefinition = "varchar")
	String serviceType
	
	@GraphQLQuery
	@Column(name = "service_code", columnDefinition = "int")
	Integer serviceCode
	
	@GraphQLQuery
	@Column(name = "accommodation_type", columnDefinition = "varchar")
	String accommodationType
	
	@GraphQLQuery
	@Column(name = "registry_type", columnDefinition = "varchar")
	String registryType
	
	@GraphQLQuery
	@Column(name = "entry_datetime", columnDefinition = "timestamp")
	Instant entryDateTime
	
	@GraphQLQuery
	@Column(name = "admission_datetime", columnDefinition = "timestamp")
	Instant admissionDatetime
	
	@GraphQLQuery
	@Column(name = "discharged_datetime", columnDefinition = "timestamp")
	Instant dischargedDatetime
	
	@GraphQLQuery
	@Column(name = "may_go_home_datetime", columnDefinition = "timestamp")
	Instant mayGoHomeDatetime
	
	@GraphQLQuery
	@Column(name = "chief_complaint", columnDefinition = "varchar")
	String chiefComplaint
	
	@GraphQLQuery
	@Column(name = "admitting_diagnosis", columnDefinition = "varchar")
	String admittingDiagnosis
	
	@GraphQLQuery
	@Column(name = "discharge_diagnosis", columnDefinition = "varchar")
	String dischargeDiagnosis
	
	@GraphQLQuery
	@Column(name = "history_present_illness", columnDefinition = "varchar")
	String historyPresentIllness
	
	@GraphQLQuery
	@Column(name = "past_medical_history", columnDefinition = "varchar")
	String pastMedicalHistory
	
	@GraphQLQuery
	@Column(name = "pre_op_diagnosis", columnDefinition = "varchar")
	String preOpDiagnosis
	
	@GraphQLQuery
	@Column(name = "post_op_diagnosis", columnDefinition = "varchar")
	String postOpDiagnosis
	
	@GraphQLQuery
	@Column(name = "surgical_procedure", columnDefinition = "varchar")
	String proceduresPerformed
	
	@GraphQLQuery
	@Column(name = "informant", columnDefinition = "varchar")
	String informant
	
	@GraphQLQuery
	@Column(name = "informant_address", columnDefinition = "varchar")
	String informantAddress
	
	@GraphQLQuery
	@Column(name = "informant_relation", columnDefinition = "varchar")
	String informantRelation
	
	@GraphQLQuery
	@Column(name = "informant_contact", columnDefinition = "varchar")
	String informantContact
	
	@GraphQLQuery
	@Column(name = "occupation", columnDefinition = "varchar")
	String occupation
	
	@GraphQLQuery
	@Column(name = "company_name", columnDefinition = "varchar")
	String companyName
	
	@GraphQLQuery
	@Column(name = "company_address", columnDefinition = "varchar")
	String companyAddress
	
	@GraphQLQuery
	@Column(name = "company_contact", columnDefinition = "varchar")
	String companyContact
	
	@GraphQLQuery
	@Column(name = "company_pen", columnDefinition = "varchar")
	String companyPen
	
	@GraphQLQuery
	@Column(name = "emergency_contact_name", columnDefinition = "varchar")
	String emergencyContactName
	
	@GraphQLQuery
	@Column(name = "emergency_contact_address", columnDefinition = "varchar")
	String emergencyContactAddress
	
	@GraphQLQuery
	@Column(name = "emergency_contact_relation", columnDefinition = "varchar")
	String emergencyContactRelation
	
	@GraphQLQuery
	@Column(name = "emergency_contact", columnDefinition = "varchar")
	String emergencyContact
	
	@GraphQLQuery
	@Column(name = "guarantor_name", columnDefinition = "varchar")
	String guarantorName
	
	@GraphQLQuery
	@Column(name = "guarantor_address", columnDefinition = "varchar")
	String guarantorAddress
	
	@GraphQLQuery
	@Column(name = "guarantor_relation", columnDefinition = "varchar")
	String guarantorRelation
	
	@GraphQLQuery
	@Column(name = "guarantor_contact", columnDefinition = "varchar")
	String guarantorContact
	
	@GraphQLQuery
	@Column(name = "history_input_datetime", columnDefinition = "timestamp")
	Instant historyInputDatetime
	
	@GraphQLQuery
	@Column(name = "triage", columnDefinition = "varchar")
	String triage
	
	@GraphQLQuery
	@Column(name = "height", columnDefinition = "varchar")
	String height
	
	@GraphQLQuery
	@Column(name = "weight", columnDefinition = "varchar")
	String weight
	
	@GraphQLQuery
	@Column(name = "initial_bp", columnDefinition = "varchar")
	String initialBp
	
	@GraphQLQuery
	@Column(name = "initial_temperature", columnDefinition = "varchar")
	String initialTemperature
	
	@GraphQLQuery
	@Column(name = "initial_pulse", columnDefinition = "varchar")
	String initialPulse
	
	@GraphQLQuery
	@Column(name = "initial_resp", columnDefinition = "varchar")
	String initialResp
	
	@GraphQLQuery
	@Column(name = "initial_o2sat", columnDefinition = "varchar")
	String initialO2sat
	
	@GraphQLQuery
	@Column(name = "initial_cbs", columnDefinition = "varchar")
	String initialCBS
	
	@GraphQLQuery
	@Column(name = "pain_score", columnDefinition = "varchar")
	String painScore
	
	@GraphQLQuery
	@Column(name = "fall_assessment", columnDefinition = "varchar")
	String fallAssessment
	
	@GraphQLQuery
	@Column(name = "followup_datetime", columnDefinition = "timestamp")
	Instant followupDatetime
	
	@GraphQLQuery
	@Column(name = "home_medication", columnDefinition = "varchar")
	String homeMedication
	
	@GraphQLQuery
	@Column(name = "special_instructions", columnDefinition = "varchar")
	String specialInstructions
	
	@GraphQLQuery
	@Column(name = "lacerated_wound", columnDefinition = "varchar")
	String laceratedWound
	
	@GraphQLQuery
	@Column(name = "head_injury", columnDefinition = "varchar")
	String headInjury
	
	@GraphQLQuery
	@Column(name = "pertinent_past_medical_history", columnDefinition = "varchar")
	String pertinentPastMedicalHistory
	
	@GraphQLQuery
	@Column(name = "previous_admission", columnDefinition = "varchar")
	String previousAdmission
	
	@GraphQLQuery
	@Column(name = "how_taken_to_room", columnDefinition = "varchar")
	String howTakenToRoom
	
	@GraphQLQuery
	@Column(name = "transferred_in", columnDefinition = "boolean")
	Boolean transferredIn

	@GraphQLQuery
	@Column(name = "other_admission_date", columnDefinition = "timestamp")
	Instant otherAdmissionDate

	@GraphQLQuery
	@Column(name = "reason_for_transfer_in", columnDefinition = "varchar")
	String reasonForTransferIn

	// In frontend this field is use in Previous HCI
	@GraphQLQuery
	@Column(name = "originating_hci", columnDefinition = "varchar")
	String originatingHci

	// In frontend this field is use in Originating HCI
	@GraphQLQuery
	@Column(name = "previous_hci", columnDefinition = "varchar")
	String previousHCI

	@GraphQLQuery
	@Column(name = "patient_transfer", columnDefinition = "boolean")
	Boolean patientTransfer


	@GraphQLQuery
	@Column(name = "transferred_out", columnDefinition = "boolean")
	Boolean transferredOut
	
	@GraphQLQuery
	@Column(name = "reason_for_transfer_out", columnDefinition = "varchar")
	String reasonForTransferOut
	
	@GraphQLQuery
	@Column(name = "transfer_hci", columnDefinition = "varchar")
	String transferHci
	
	@GraphQLQuery
	@Column(name = "icd_diagnosis", columnDefinition = "varchar")
	String icdDiagnosis
	
	@GraphQLQuery
	@Column(name = "rvs_diagnosis", columnDefinition = "varchar")
	String rvsDiagnosis
	
	@GraphQLQuery
	@Column(name = "primary_dx", columnDefinition = "varchar")
	String primaryDx
	
	@GraphQLQuery
	@Column(name = "secondary_dx", columnDefinition = "varchar")
	String secondaryDx
	
	@GraphQLQuery
	@Column(name = "doh_icd_diagnosis", columnDefinition = "varchar")
	String dohICDDiagnosis
	
	@GraphQLQuery
	@Column(name = "doh_surgical_diagnosis", columnDefinition = "varchar")
	String dohSurgicalDiagnosis
	
	@GraphQLQuery
	@Column(name = "take_home_medications", columnDefinition = "text")
	String takeHomeMedications
	
	@GraphQLQuery
	@Column(name = "physical_exam_list", columnDefinition = "text")
	String physicalExamList
	
	@GraphQLQuery
	@Column(name = "pertinent_symptoms_list", columnDefinition = "text")
	String pertinentSymptomsList
	
	@GraphQLQuery
	@Column(name = "room_in", columnDefinition = "bool")
	Boolean roomIn
	
	@GraphQLQuery
	@Column(name = "is_infacility_delivery", columnDefinition = "boolean")
	Boolean isInfacilityDelivery
	
	@GraphQLQuery
	@Column(name = "delivery_type", columnDefinition = "varchar")
	String deliveryType
	
	@GraphQLQuery
	@Column(name = "is_antenatal", columnDefinition = "boolean")
	Boolean isAntenatal
	
	@GraphQLQuery
	@Column(name = "is_postnatal", columnDefinition = "boolean")
	Boolean isPostnatal
	
	@GraphQLQuery
	@Column(name = "discharge_condition", columnDefinition = "varchar")
	String dischargeCondition
	
	//TODO don't comment this field is required in discharging patient and required by DOH reports
	@GraphQLQuery
	@Column(name = "discharge_disposition", columnDefinition = "varchar")
	String dischargeDisposition
	
	@GraphQLQuery
	@Column(name = "time_of_birth", columnDefinition = "timestamp")
	Instant timeOfBirth
	
	@GraphQLQuery
	@Column(name = "time_of_death", columnDefinition = "timestamp")
	Instant timeOfDeath
	
	@GraphQLQuery
	@Column(name = "is_dead_on_arrival", columnDefinition = "boolean")
	Boolean isDeadOnArrival
	
	@GraphQLQuery
	@Column(name = "death_type", columnDefinition = "varchar")
	String deathType
	
	@GraphQLQuery
	@Column(name = "operation_code", columnDefinition = "text")
	String operationCode
	
	@GraphQLQuery
	@Column(name = "course_in_the_ward", columnDefinition = "text")
	String courseInTheWard
	
	@GraphQLQuery
	@Column(name = "credit_limit", columnDefinition = "numeric")
	BigDecimal creditLimit
	
	@GraphQLQuery
	@Column(name = "rcid", columnDefinition = "text")
	String rIcd
	
	@GraphQLQuery
	@Column(name = "nurse_note", columnDefinition = "text")
	String nurseNote
	
	@GraphQLQuery
	@Column(name = "price_accommodation_type", columnDefinition = "varchar")
	String priceAccommodationType
	
	@GraphQLQuery
	@Column(name = "comlogik_refno", columnDefinition = "varchar")
	String comlogikRefNo
	
	@GraphQLQuery
	@Column(name = "investor", columnDefinition = "boolean")
	Boolean investor
	
	@GraphQLQuery
	@Column(name = "investor_id", columnDefinition = "varchar")
	String investorId
	
	@GraphQLQuery
	@Column(name = "refusal_of_admission", columnDefinition = "varchar")
	String refusalOfAdmission
	
	@GraphQLQuery
	@Column(name = "death_expiration", columnDefinition = "varchar")
	String deathExpiration

	@GraphQLQuery
	@Column(name = "last_meal", columnDefinition = "timestamp")
	Instant lastMeal

	@GraphQLQuery
	@Column(name = "locked", columnDefinition = "boolean")
	Boolean locked

	@GraphQLQuery
	@Column(name = "refused_kitbag", columnDefinition = "boolean")
	Boolean refusedKitBag

	@GraphQLQuery
	@Column(name = "family_history", columnDefinition = "text")
	String familyHistory

	@GraphQLQuery
	@Column(name = "personal_social_history", columnDefinition = "text")
	String personalSocialHistory

	@GraphQLQuery
	@Column(name = "vap_infection", columnDefinition = "boolean")
	Boolean vapInfection

	@GraphQLQuery
	@Column(name = "vap_infected_date", columnDefinition = "timestamp")
	Instant vapInfectedDate

	@GraphQLQuery
	@Column(name = "bsi_infection", columnDefinition = "boolean")
	Boolean bsiInfection

	@GraphQLQuery
	@Column(name = "bsi_infected_date", columnDefinition = "timestamp")
	Instant bsiInfectedDate

	@GraphQLQuery
	@Column(name = "uti_infection", columnDefinition = "boolean")
	Boolean utiInfection

	@GraphQLQuery
	@Column(name = "uti_infected_date", columnDefinition = "timestamp")
	Instant utiInfectedDate

	@ShallowReference
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vap_infection_by", referencedColumnName = "id")
	Employee vapInfectionBy

	@ShallowReference
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bsi_infection_by", referencedColumnName = "id")
	Employee bsiInfectionBy

	@ShallowReference
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "uti_infection_by", referencedColumnName = "id")
	Employee utiInfectionBy

	@ShallowReference
	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "opd_physician", referencedColumnName = "id")
	Physician opdPhysician

	@GraphQLQuery
	@Column(name = "consultation", columnDefinition = "varchar")
	String consultation

	@GraphQLQuery
	@Column(name = "urgency", columnDefinition = "varchar")
	String urgency

  @GraphQLQuery
	@Column(name = "adult_kit_bag", columnDefinition = "boolean")
	Boolean adultKitBag

	@GraphQLQuery
	@Column(name = "new_born_kit_bag", columnDefinition = "boolean")
	Boolean newBornKitBag

	@GraphQLQuery
	@Column(name = "admission_kit", columnDefinition = "varchar")
	String admissionKit

	@GraphQLQuery
	@Column(name = "is_consultation", columnDefinition = "bool")
	Boolean isConsultation

}
