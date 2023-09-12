package com.hisd3.hismk2.domain.hospital_config

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import org.xmlsoap.schemas.soap.encoding.Int

import javax.persistence.*

@Canonical
class DohClassification {
	Integer servicecapability
	Integer general
	Integer specialty
	String 	specialtyspecify
	Integer traumacapability
	Integer natureofownership
	Integer government
	Integer national
	Integer local
	Integer privateClassification
	String ownershipothers
}


@Canonical
class DohQualityManagement{
	Integer qualitymgmttype
	String description
	String certifyingbody
	Integer philhealthaccreditation
	Date validityfrom
	Date validityto
}


@Entity
@Table(schema = "hospital_configuration", name = "hospital_info")
class HospitalInfo extends AbstractAuditingEntity {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "hospital_name", columnDefinition = "varchar")
	String hospitalName
	
	@GraphQLQuery
	@Column(name = "address", columnDefinition = "varchar")
	String address
	
	@GraphQLQuery
	@Column(name = "address_line2", columnDefinition = "varchar")
	String addressLine2
	
	@GraphQLQuery
	@Column(name = "city", columnDefinition = "varchar")
	String city
	
	@GraphQLQuery
	@Column(name = "street", columnDefinition = "varchar")
	String street
	
	@GraphQLQuery
	@Column(name = "zip", columnDefinition = "varchar")
	String zip
	
	@GraphQLQuery
	@Column(name = "country", columnDefinition = "varchar")
	String country
	
	@GraphQLQuery
	@Column(name = "tel_no", columnDefinition = "varchar")
	String telNo
	
	@GraphQLQuery
	@Column(name = "mobile", columnDefinition = "varchar")
	String mobile
	
	@GraphQLQuery
	@Column(name = "other_no", columnDefinition = "varchar")
	String otherNo
	
	@GraphQLQuery
	@Column(name = "fax", columnDefinition = "varchar")
	String fax
	
	@GraphQLQuery
	@Column(name = "email", columnDefinition = "varchar")
	String email
	
	@GraphQLQuery
	@Column(name = "service_level", columnDefinition = "varchar")
	String serviceLevel
	
	@GraphQLQuery
	@Column(name = "trauma_capable", columnDefinition = "bool")
	Boolean traumaCapable
	
	@GraphQLQuery
	@Column(name = "trauma_receiving", columnDefinition = "bool")
	Boolean traumaReceiving
	
	@GraphQLQuery
	@Column(name = "bed_capacity", columnDefinition = "numeric")
	Integer bedCapacity
	
	@GraphQLQuery
	@Column(name = "implemented_bed", columnDefinition = "numeric")
	Integer implementedBed
	
	@GraphQLQuery
	@Column(name = "iso_validity", columnDefinition = "varchar")
	String isoValidity
	
	@GraphQLQuery
	@Column(name = "international_accreditation", columnDefinition = "varchar")
	String internationalAccreditation
	
	@GraphQLQuery
	@Column(name = "philhealth_accreditation", columnDefinition = "varchar")
	String philhealthAccreditation
	
	@GraphQLQuery
	@Column(name = "philhealth_accreditation_validity", columnDefinition = "varchar")
	String philhealthAccreditationValidity
	
	//--------------------------------------------------
	//--------------------------------------------------
	
	@GraphQLQuery
	@Column(name = "service_capability", columnDefinition = "numeric")
	Integer serviceCapability
	
	@GraphQLQuery
	@Column(name = "ic_general", columnDefinition = "numeric")
	Integer icGeneral
	
	@GraphQLQuery
	@Column(name = "specialty", columnDefinition = "numeric")
	Integer specialty
	
	@GraphQLQuery
	@Column(name = "specialty_specify", columnDefinition = "numeric")
	Integer specialtySpecify
	
	@GraphQLQuery
	@Column(name = "trauma_capability", columnDefinition = "numeric")
	Integer traumaCapability
	
	@GraphQLQuery
	@Column(name = "nature_of_ownership", columnDefinition = "numeric")
	Integer natureOfOwnership
	
	@GraphQLQuery
	@Column(name = "government", columnDefinition = "numeric")
	Integer government
	
	@GraphQLQuery
	@Column(name = "national", columnDefinition = "numeric")
	Integer national
	
	@GraphQLQuery
	@Column(name = "local", columnDefinition = "numeric")
	Integer local
	
	@GraphQLQuery
	@Column(name = "private", columnDefinition = "numeric")
	Integer icPrivate
	
	@GraphQLQuery
	@Column(name = "abc", columnDefinition = "numeric")
	Integer abc
	
	@GraphQLQuery
	@Column(name = "implementing_beds", columnDefinition = "numeric")
	Integer implementingBeds
	
	@GraphQLQuery
	@Column(name = "bor", columnDefinition = "numeric")
	Integer bor
	
	@GraphQLQuery
	@Column(name = "quality_mgmt_type", columnDefinition = "numeric")
	Integer qualityMgmtType
	
	@GraphQLQuery
	@Column(name = "qm_description", columnDefinition = "varchar")
	String qmDescription
	
	@GraphQLQuery
	@Column(name = "certifying_body", columnDefinition = "varchar")
	String certifyingBody
	
	@GraphQLQuery
	@Column(name = "qm_philhealth_accreditation", columnDefinition = "numeric")
	Integer qmPhilhealthAccreditation
	
	@GraphQLQuery
	@Column(name = "validity_from", columnDefinition = "date")
	Date validityFrom
	
	@GraphQLQuery
	@Column(name = "validity_to", columnDefinition = "date")
	Date validityTo

	@GraphQLQuery
	@Column(name = "hfhudcode", columnDefinition = "varchar")
	String hfhudcode

	@GraphQLQuery
	@Type(type = "jsonb")
	@Column(name="doh_classification",columnDefinition = "jsonb")
	DohClassification dohClassification

	@GraphQLQuery
	@Type(type = "jsonb")
	@Column(name="doh_quality_management",columnDefinition = "jsonb")
	Map<String,DohQualityManagement> dohQualityManagement
}
