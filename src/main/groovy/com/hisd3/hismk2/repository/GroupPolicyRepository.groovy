package com.hisd3.hismk2.repository

import com.hisd3.hismk2.domain.GroupPolicy
import org.springframework.data.jpa.repository.JpaRepository

interface GroupPolicyRepository extends JpaRepository<GroupPolicy, UUID> {
    List<GroupPolicy> findByIdIn(List<UUID> id)
}
