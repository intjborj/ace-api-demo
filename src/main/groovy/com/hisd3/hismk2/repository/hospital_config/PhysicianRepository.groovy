package com.hisd3.hismk2.repository.hospital_config


import com.hisd3.hismk2.domain.hospital_config.ClinicRoom
import com.hisd3.hismk2.domain.hospital_config.Physician
import org.springframework.data.jpa.repository.JpaRepository

interface PhysicianRepository extends JpaRepository<Physician, UUID> {

}