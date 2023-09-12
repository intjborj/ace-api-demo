package com.hisd3.hismk2.repository.referential

import com.hisd3.hismk2.domain.referential.RegistryType
import groovy.transform.TypeChecked
import org.springframework.data.jpa.repository.JpaRepository

@TypeChecked
interface RegistryTypeRepository extends JpaRepository<RegistryType, UUID> {

}
