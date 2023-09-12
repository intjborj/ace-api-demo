package com.hisd3.hismk2.repository.hospital_config

import com.hisd3.hismk2.domain.hospital_config.ClinicDoctor
import com.hisd3.hismk2.domain.hospital_config.ClinicRoom
import org.springframework.data.jpa.repository.JpaRepository

interface ClinicDoctorRepository extends JpaRepository<ClinicDoctor, UUID> {

}