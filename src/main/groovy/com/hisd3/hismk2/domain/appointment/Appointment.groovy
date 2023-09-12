package com.hisd3.hismk2.domain.appointment

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import com.hisd3.hismk2.rest.dto.CovidUpdatesList
import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLQuery
import org.apache.commons.lang3.StringUtils
import org.hibernate.annotations.*

import javax.persistence.*
import java.time.Instant
import java.time.LocalDate

@javax.persistence.Entity
@javax.persistence.Table(schema = "appointment", name = "appointment")
class Appointment extends AbstractAuditingEntity implements Serializable {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@GraphQLQuery
	@Column(name = "app_no", columnDefinition = "varchar")
	String appNo

	//purpose
	@GraphQLQuery
	@UpperCase
	@Column(name = "purpose_of_testing", columnDefinition = "varchar")
	String purposeOfTesting

	@GraphQLQuery
	@UpperCase
	@Column(name = "reason_of_testing", columnDefinition = "varchar")
	String reasonOfTesting

	@GraphQLQuery
	@Column(name = "date_validity", columnDefinition = "timestamp")
	Instant dateValidity

	@GraphQLQuery
	@UpperCase
	@Column(name = "transportation", columnDefinition = "varchar")
	String transportation
	//

	//travel
	@GraphQLQuery
	@UpperCase
	@Column(name = "airline_sea_vessel", columnDefinition = "varchar")
	String airlineSeaVessel

	@GraphQLQuery
	@UpperCase
	@Column(name = "flight_vessel_no", columnDefinition = "varchar")
	String flightVesselNo

	@GraphQLQuery
	@UpperCase
	@Column(name = "country_destination", columnDefinition = "varchar")
	String countryDestination
	//

	//others details

	@GraphQLQuery
	@UpperCase
	@Column(name = "positive_covid_before", columnDefinition = "varchar")
	String positiveCovidBefore //yes or no

	@GraphQLQuery
	@UpperCase
	@Column(name = "informant", columnDefinition = "varchar")
	String informant

	@GraphQLQuery
	@UpperCase
	@Column(name = "realtion_informant", columnDefinition = "varchar")
	String relationInformant

	@GraphQLQuery
	@UpperCase
	@Column(name = "informant_contact", columnDefinition = "varchar")
	String informantContact

	@GraphQLQuery
	@UpperCase
	@Column(name = "number_of_Test", columnDefinition = "varchar")
	String numberOfTest

	@GraphQLQuery
	@UpperCase
	@Column(name = "covid_updates", columnDefinition = "varchar")
	String covidUpdates

	@Transient
	ArrayList<CovidUpdatesList> getCovidUpdatesList() {
		def arr = StringUtils.defaultString(covidUpdates).split(",")
		def finalArr = [];
		arr.each {
			finalArr.push(new CovidUpdatesList(label: it, value: it))
		}
		return finalArr
	}

	@GraphQLQuery
	@UpperCase
	@Column(name = "outcome_condition", columnDefinition = "varchar")
	String outcomeCondition

	@GraphQLQuery
	@Column(name = "dod", columnDefinition = "date")
	LocalDate dod

	@GraphQLQuery
	@UpperCase
	@Column(name = "immediate_cause", columnDefinition = "varchar")
	String immediateCause

	@GraphQLQuery
	@UpperCase
	@Column(name = "antecedent_cause", columnDefinition = "varchar")
	String antecedentCause

	@GraphQLQuery
	@UpperCase
	@Column(name = "underlying_cause", columnDefinition = "varchar")
	String underlyingCause

	@GraphQLQuery
	@UpperCase
	@Column(name = "contributory_conditions", columnDefinition = "varchar")
	String contributoryConditions

	@GraphQLQuery
	@Column(name = "dor", columnDefinition = "date")
	LocalDate dor
	//

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "patient", referencedColumnName = "id")
	AgtPatient patient

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schedule", referencedColumnName = "id")
	AppointmentSchedule schedule

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schedule_time", referencedColumnName = "id")
	AppointmentScheduleTime scheduleTime
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "order_status", columnDefinition = "varchar")
	String orderStatus

	@GraphQLQuery
	@UpperCase
	@Column(name = "status", columnDefinition = "bool")
	Boolean status

}
