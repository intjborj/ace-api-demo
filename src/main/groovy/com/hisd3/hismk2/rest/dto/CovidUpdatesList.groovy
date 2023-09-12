package com.hisd3.hismk2.rest.dto

import com.hisd3.hismk2.domain.appointment.AppointmentScheduleTime
import groovy.transform.Canonical
import groovy.transform.TupleConstructor

import java.time.Instant
import java.time.LocalDate

@Canonical
class CovidUpdatesList{
	String label
	String value
}

@Canonical
class RestAppointment{
	UUID id
	String appNo
	String purposeOfTesting
	String reasonOfTesting
	Instant dateValidity
	String transportation
	String airlineSeaVessel
	String flightVesselNo
	String countryDestination
	String positiveCovidBefore
	String informant
	String relationInformant
	String informantContact
	String numberOfTest
	String covidUpdates
	ArrayList<CovidUpdatesList> covidUpdatesList
	String outcomeCondition
	LocalDate dod
	String immediateCause
	String antecedentCause
	String underlyingCause
	String contributoryConditions
	LocalDate dor
	PatientInfo patient
	ScheduleAppTime scheduleTime
	ScheduleAppDate schedule
	String orderStatus
	Boolean status
}

@Canonical
class ScheduleAppTime{
	UUID id
	UUID timeId
	String formattedTime
}

@Canonical
class ScheduleAppDate{
	UUID id
	Instant scheduleDate
	String formattedScheduleDate
}

@Canonical
class PatientInfo{
	UUID id
	String fullName
}