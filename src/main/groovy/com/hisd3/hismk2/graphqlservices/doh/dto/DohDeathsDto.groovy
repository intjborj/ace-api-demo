package com.hisd3.hismk2.graphqlservices.doh.dto

import groovy.transform.builder.Builder;

@Builder
class DohDeathsDto {
     Integer totalDeaths
     Integer totalDeathLessThan48
     Integer totalDeathGreaterThanEqualTo48
     Integer emergencyRoomDeaths
     Integer deadOnArrivalDeaths
     Integer neonatalDeaths
     Integer stillbirthsDeaths
     Integer maternalDeaths
     Integer no_entry_datetime
     Integer no_admission_date
     Integer no_discharge_date

}

class DohDeathsPageDto {
     BigInteger totalDischarged
     BigDecimal netDeathRate
     BigDecimal grossDeathRate
     DohDeathsDto deathsDto
}