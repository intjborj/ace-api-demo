package com.hisd3.hismk2.domain.inventory

import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*

@Entity
@Table(schema = "inventory", name = "return_medication_items")
class ReturnMedicationItem {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "return_medication", referencedColumnName = "id")
	ReturnMedication returnMedication
	
	@GraphQLQuery
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "medicine", referencedColumnName = "id")
	Item medicine
	
	@GraphQLQuery
	@Column(name = "quantity_returned")
	Integer quantity_returned
	
}
