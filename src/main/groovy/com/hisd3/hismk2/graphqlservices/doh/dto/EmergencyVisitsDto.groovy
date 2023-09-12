package com.hisd3.hismk2.graphqlservices.doh.dto

import groovy.transform.Canonical

class EmergencyVisitsDto {
    String registry_type, discharge_disposition
    Integer total
}

@Canonical
class HospOtpDischargeEVDto{
    String emergencyvisits
    String emergencyvisitsadult
    String emergencyvisitspediatric
    String evfromfacilitytoanother
}
