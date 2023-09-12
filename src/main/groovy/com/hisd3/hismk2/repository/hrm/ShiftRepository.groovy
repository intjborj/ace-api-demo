package com.hisd3.hismk2.repository.hrm

import com.hisd3.hismk2.domain.hrm.Shift
import org.springframework.data.jpa.repository.JpaRepository

interface ShiftRepository extends JpaRepository<Shift, UUID> {

}
