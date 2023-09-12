package com.hisd3.hismk2.repository.hospital_config

import com.hisd3.hismk2.domain.hospital_config.AdmissionConfiguration
import org.springframework.data.jpa.repository.JpaRepository

interface AdmissionConfigurationRepository extends JpaRepository<AdmissionConfiguration, UUID> {

}
