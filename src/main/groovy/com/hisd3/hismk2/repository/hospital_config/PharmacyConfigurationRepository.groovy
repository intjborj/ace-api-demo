package com.hisd3.hismk2.repository.hospital_config

import com.hisd3.hismk2.domain.hospital_config.PharmacyConfiguration
import org.springframework.data.jpa.repository.JpaRepository

interface PharmacyConfigurationRepository extends JpaRepository<PharmacyConfiguration, UUID> {

}
