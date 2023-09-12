package com.hisd3.hismk2.repository

import com.hisd3.hismk2.domain.Notification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param

interface NotificationRepository extends JpaRepository<Notification, UUID> {
	List<Notification> findTop10ByToOrderByDatenotifiedDesc(@Param("to") UUID to)
}
