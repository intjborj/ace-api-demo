package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.ReturnMedicationItem
import org.springframework.data.jpa.repository.JpaRepository

interface ReturnMedicationItemRepository extends JpaRepository<ReturnMedicationItem, UUID> {

}
