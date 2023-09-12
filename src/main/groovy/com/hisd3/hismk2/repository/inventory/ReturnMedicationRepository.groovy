package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.ReturnMedication
import org.springframework.data.jpa.repository.JpaRepository

interface ReturnMedicationRepository extends JpaRepository<ReturnMedication, UUID> {

}
