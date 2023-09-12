package com.hisd3.hismk2.graphqlservices.doh.dto

import com.hisd3.hismk2.rest.dto.HospOptDischargesSpecialtyOthers
import groovy.transform.Canonical

@Canonical
class AuthenticationTest {
    String authentication
}

@Canonical
class AuthenticationTestResponseDto {
    String response_code
    String response_desc
    String response_date
    AuthenticationTest response_value
}

//deafult response
@Canonical
class DefaultDOHResponseDetails {
    String hfhudcode
    String reportingyear
    String status
}

@Canonical
class GenInfoBedCapacityReport {
    DefaultDOHResponseDetails report
    String insert
    String update
}

@Canonical
class GenInfoBedCapacity {
    GenInfoBedCapacityReport genInfoBedCapacity
}

@Canonical
class GenInfoBedCapacityResponseDto {
    String response_code
    String response_desc
    String response_datetime
    GenInfoBedCapacity response_value
}

// Hospital Number Deliveries DTO
@Canonical
class HospOptDischargesNumberDeliveriesReport {
    DefaultDOHResponseDetails report
    String insert
    String update
}

@Canonical
class HospOptDischargesNumberDeliveries {
    HospOptDischargesNumberDeliveriesReport hospOptDischargesNumberDeliveries
}

@Canonical
class  HospOptDischargesNumberDeliveriesDto{
    String response_code
    String response_desc
    String response_datetime
    HospOptDischargesNumberDeliveries response_value
}


//Emergency Visit DTO
@Canonical
class HospOptDischargesERDOH{
    DefaultDOHResponseDetails report
    String insert
    String update

}

@Canonical
class HospOptDischargesER{
   HospOptDischargesERDOH hospOptDischargesER
}

@Canonical
class HospOptDischargesERDTO{
    String response_code
    String response_desc
    String response_datetime
    HospOptDischargesER response_value
}


// OPD Consultation
@Canonical
class HospOptDischargesOPDDOH {
    DefaultDOHResponseDetails report
    String insert
    String update

}

@Canonical
class HospOptDischargesOPD{
    HospOptDischargesOPDDOH hospOptDischargesOPD
}

@Canonical
class HospOptDischargesOPDDTO{
    String response_code
    String response_desc
    String response_datetime
    HospOptDischargesOPD response_value
}

// Expenses DTO
@Canonical
class ExpensesDOH {
    DefaultDOHResponseDetails report
    String insert
    String update
}

@Canonical
class Expenses{
    ExpensesDOH expenses
}

@Canonical
class ExpensesDTO{
    String response_code
    String response_desc
    String response_datetime
    Expenses response_value
}

// Revenues DTO
@Canonical
class RevenuesDOH {
    DefaultDOHResponseDetails report
    String insert
}

@Canonical
class RevenuesObj{
    RevenuesDOH revenues
}

@Canonical
class RevenuesDTO{
    String response_code
    String response_desc
    String response_datetime
    RevenuesObj response_value
}


// Summary of Patient
@Canonical
class SummaryPatients{
    DefaultDOHResponseDetails report
    String insert
}

@Canonical
class SummaryPatient{
    SummaryPatients hospOptSummaryOfPatients
}

@Canonical
class SummaryOfPatientDTO{
    String response_code
    String response_desc
    String response_datetime
    SummaryPatient response_value
}

// HospitalOperationMortalityDeathsDTO
@Canonical
class HospOptMortalityDeathsDOH {
    DefaultDOHResponseDetails report
    String insert
    String update
}

@Canonical
class HospOptMortalityDeathsObj{
    HospOptMortalityDeathsDOH hospitalOperationsMortalityDeaths
}

@Canonical
class HospOptMortalityDeathsDTO{
    String icd10desc
    String icd10code

    String response_code
    String response_desc
    String response_datetime
    HospOptMortalityDeathsObj response_value
}

// HospOptDischargesMorbidityDTO
@Canonical
class HospOptDischargesMorbidityDOH {
    DefaultDOHResponseDetails report
    String insert
    String update
}

@Canonical
class HospOptDischargesMorbidityObj{
    HospOptDischargesMorbidityDOH hospOptDischargesMorbidity
}

@Canonical
class HospOptDischargesMorbidityDTO{
    String icd10desc
    String icd10code

    String response_code
    String response_desc
    String response_datetime
    HospOptDischargesMorbidityObj response_value
}

// HospOptDischargesTesting DTO
@Canonical
class HospOptDischargesTestingDOH {
    DefaultDOHResponseDetails report
    String insert
    String update
}

@Canonical
class HospOptDischargesTestingObj{
    HospOptDischargesTestingDOH hospOptDischargesTesting
}

@Canonical
class HospOptDischargesTestingDTO{
    String description
    String testinggroup
    String number


    String response_code
    String response_desc
    String response_datetime
    HospOptDischargesTestingObj response_value
}


// Emergency Visit

@Canonical
class HospitalDischargeEVisit{
    DefaultDOHResponseDetails report
    String insert
}

@Canonical
class HospOptDischargesEV{
    HospitalDischargeEVisit hospOptDischargesEV
}

@Canonical
class  HospOptDischargesEVDTO{
    String response_code
    String response_desc
    String response_datetime
    HospOptDischargesEV response_value
}


// StaffingPattern DTO
@Canonical
class StaffingPatternDOH {
    DefaultDOHResponseDetails report
    String insert
    String update
}

@Canonical
class StaffingPatternObj{
    StaffingPatternDOH staffingPattern
    StaffingPatternDOH staffingPatternOthers
}

@Canonical
class StaffingPatternDTO{
    String positionDescription

    String response_code
    String response_desc
    String response_datetime
    StaffingPatternObj response_value
}



// Out-Patient Visit DTO

@Canonical
class HospOptDischargesOPVDoh{
    String insert
    DefaultDOHResponseDetails report
}

@Canonical
class HospOptDischargesOPV {
    HospOptDischargesOPVDoh hospOptDischargesOPV
}

@Canonical
class HospOptDischargesOPVDTO{
    String response_code
    String response_desc
    String response_datetime
    HospOptDischargesOPV response_value
}



// HospitalOperationsMajorOpt DTO
@Canonical
class HospitalOperationsMajorOptDOH {
    DefaultDOHResponseDetails report
    String insert
    String update
}

@Canonical
class HospitalOperationsMajorOptObj{
    HospitalOperationsMajorOptDOH hospitalOperationsMajorOpt
}

@Canonical
class HospitalOperationsMajorOptDTO{
    String operationcode
    String surgicaloperation
    String number

    String response_code
    String response_desc
    String response_datetime
    HospitalOperationsMajorOptObj response_value
}



// HospitalOperationsMinorOpt DTO
@Canonical
class HospitalOperationsMinorOptDOH {
    DefaultDOHResponseDetails report
    String insert
    String update
}

@Canonical
class HospitalOperationsMinorOptObj{
    HospitalOperationsMinorOptDOH hospitalOperationsMinorOpt
}

@Canonical
class HospitalOperationsMinorOptDTO {
    String operationcode
    String surgicaloperation
    String number

    String response_code
    String response_desc
    String response_datetime
    HospitalOperationsMinorOptObj response_value
}

//HospitalOperationsHAI DTO
@Canonical
class HospitalOperationsHAIDOH {
    DefaultDOHResponseDetails report
    String insert
    String update
}

@Canonical
class HospitalOperationsHAIObj{
    HospitalOperationsHAIDOH hospitalOperationsHAI
}

@Canonical
class HospitalOperationsHAIDTO{

    String response_code
    String response_desc
    String response_datetime
    HospitalOperationsHAIObj response_value
}

//HospitalOperationsDeaths DTO
@Canonical
class HospitalOperationsDeathsDOH {
    DefaultDOHResponseDetails report
    String insert
    String update
}

@Canonical
class HospitalOperationsDeathsObj{
    HospitalOperationsDeathsDOH hospitalOperationsDeaths
}

@Canonical
class HospitalOperationsDeathsDTO{

    String response_code
    String response_desc
    String response_datetime
    HospitalOperationsDeathsObj response_value
}

//HospOptDischargesSpecialty DTO
@Canonical
class HospOptDischargesSpecialtyDOH {
    DefaultDOHResponseDetails report
    String insert
    String update
}

@Canonical
class HospOptDischargesSpecialtyObj{
    HospOptDischargesSpecialtyDOH hospOptDischargesSpecialty
    HospOptDischargesSpecialtyDOH hospOptDischargesSpecialtyOthers

}

@Canonical
class HospOptDischargesSpecialtyDTO{
    String serviceDescription

    String response_code
    String response_desc
    String response_datetime
    HospOptDischargesSpecialtyObj response_value
}


//GenInfoClassification DTO
@Canonical
class GenInfoClassificationDOH {
    DefaultDOHResponseDetails report
    String insert
    String update
}

@Canonical
class GenInfoClassificationObj{
    GenInfoClassificationDOH genInfoClassification

}

@Canonical
class GenInfoClassificationDTO{
    String serviceDescription

    String response_code
    String response_desc
    String response_datetime
    GenInfoClassificationObj response_value
}

//GenInfoQualityManagement DTO
@Canonical
class GenInfoQualityManagementDOH {
    DefaultDOHResponseDetails report
    String insert
    String update
}

@Canonical
class GenInfoQualityManagementObj{
    GenInfoQualityManagementDOH genInfoQualityManagement

}

@Canonical
class GenInfoQualityManagementDTO{
    String qualityManagementType

    String response_code
    String response_desc
    String response_datetime
    GenInfoQualityManagementObj response_value
}