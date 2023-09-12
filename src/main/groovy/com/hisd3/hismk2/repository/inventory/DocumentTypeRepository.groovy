package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.DocumentTypes
import org.springframework.data.jpa.repository.JpaRepository

interface DocumentTypeRepository extends JpaRepository<DocumentTypes, UUID> {

}
