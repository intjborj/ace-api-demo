package com.hisd3.hismk2.domain.inventory

import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.pms.Patient
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant

@Entity
@Table(schema = "inventory", name = "return_medication")
class ReturnMedication {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "patient", referencedColumnName = "id")
	Patient patient
	
	@GraphQLQuery
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "returned_by", referencedColumnName = "id")
	Employee returnedBy
	
	@GraphQLQuery
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "received_by", referencedColumnName = "id")
	Employee receivedBy
	
	@GraphQLQuery
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "`case`", referencedColumnName = "id")
	Case case_
	
	@GraphQLQuery
	@Column(name = "remarks")
	String remarks
	
	@GraphQLQuery
	@Column(name = "returned_date")
	Instant returnedDate
	
}
