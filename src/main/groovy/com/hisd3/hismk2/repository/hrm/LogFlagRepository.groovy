package com.hisd3.hismk2.repository.hrm

import com.hisd3.hismk2.domain.hrm.LogFlag
import org.springframework.data.jpa.repository.JpaRepository

interface LogFlagRepository extends JpaRepository<LogFlag, UUID> {

}
