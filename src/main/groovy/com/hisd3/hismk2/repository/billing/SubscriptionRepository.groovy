package com.hisd3.hismk2.repository.billing

import com.hisd3.hismk2.domain.billing.Investor
import com.hisd3.hismk2.domain.billing.Subscription
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    @Query(value = """
        Select s from Subscription s
        left join fetch s.investor i
        where s.fullPaymentDate is null and i.id = :id 
        order by s.createdDate ASC
    """)
    List<Subscription> getSubscriptionByInvestorId(
            @Param("id")UUID id
    )

//    @Query(value = """
//                    Select s from Subscription s
//                    Where s.investor = :investorId
//l       """)
	
}
