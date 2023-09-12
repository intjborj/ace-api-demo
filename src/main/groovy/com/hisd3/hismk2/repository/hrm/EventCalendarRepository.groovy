package com.hisd3.hismk2.repository.hrm

import com.hisd3.hismk2.domain.hrm.EventCalendar
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

import java.time.Instant

interface EventCalendarRepository extends JpaRepository<EventCalendar, UUID> {

    @Query(value = """
        Select e from EventCalendar e
        where
            e.startDate >= :startDate and e.endDate <= :endDate
        order by e.startDate
    """)
    List<EventCalendar>getEventsBetweenTwoDates(@Param("startDate")Instant startDate, @Param("endDate") Instant endDate)
}
