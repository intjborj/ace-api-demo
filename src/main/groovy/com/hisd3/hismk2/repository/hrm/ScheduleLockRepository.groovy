package com.hisd3.hismk2.repository.hrm

import com.hisd3.hismk2.domain.hrm.AddOn
import com.hisd3.hismk2.domain.hrm.ScheduleLock
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ScheduleLockRepository extends JpaRepository<ScheduleLock, UUID> {

}