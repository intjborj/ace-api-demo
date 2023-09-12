package com.hisd3.hismk2.repository.hospital_config

import com.hisd3.hismk2.domain.hospital_config.DefaultAdmissionItems
import com.hisd3.hismk2.domain.hospital_config.HospitalInfo
import org.springframework.data.jpa.repository.JpaRepository

interface DefaultAdmissionItemsRepository extends JpaRepository<DefaultAdmissionItems, UUID> {

}
