package com.hisd3.hismk2.repository.philhealth

import com.hisd3.hismk2.domain.philhealth.PhicGroup
import org.springframework.data.jpa.repository.JpaRepository

interface PhicGroupRepository extends JpaRepository<PhicGroup, UUID> {

}
