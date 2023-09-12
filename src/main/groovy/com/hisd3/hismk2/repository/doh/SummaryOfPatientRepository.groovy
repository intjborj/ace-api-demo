package com.hisd3.hismk2.repository.doh

import com.hisd3.hismk2.domain.doh.SummaryOfPatient
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SummaryOfPatientRepository  extends JpaRepository<SummaryOfPatient, UUID> {
    @Query(value = "select c from SummaryOfPatient c")
    List<SummaryOfPatient> findAllSummaryOfPatient()
}