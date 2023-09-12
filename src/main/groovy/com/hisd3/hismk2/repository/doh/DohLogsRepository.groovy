package com.hisd3.hismk2.repository.doh

import com.hisd3.hismk2.domain.doh.BedCapacity
import com.hisd3.hismk2.domain.doh.DohLogs
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface DohLogsRepository extends JpaRepository<DohLogs, UUID> {

}