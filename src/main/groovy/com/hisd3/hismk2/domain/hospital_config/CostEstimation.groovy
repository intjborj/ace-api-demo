package com.hisd3.hismk2.domain.hospital_config

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.pms.Patient
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*

@Entity
@Table(schema = "hospital_configuration", name = "cost_estimation")
class CostEstimation extends AbstractAuditingEntity {
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "name", columnDefinition = "varchar")
	String description
	
	@GraphQLQuery
	@Column(name = "diagnosis", columnDefinition = "varchar")
	String diagnosis
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "patient", referencedColumnName = "id")
	Patient patient
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "attending_physician", referencedColumnName = "id")
	Employee attendingPhysician
}
