package com.hisd3.hismk2.repository.billing

import com.hisd3.hismk2.domain.billing.InvestorAttachment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface InvestorAttachmentRepository extends JpaRepository<InvestorAttachment, UUID> {

    @Query(value = """
        Select i from InvestorAttachment i
        left join fetch i.investor
        left join fetch i.dependent
        where i.id = :id
    """)
    Optional<InvestorAttachment> findOneAttachment(@Param("id")UUID id)
}
