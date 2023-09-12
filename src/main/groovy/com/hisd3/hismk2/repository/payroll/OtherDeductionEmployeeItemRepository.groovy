package com.hisd3.hismk2.repository.payroll

import com.hisd3.hismk2.domain.payroll.OtherDeductionEmployee
import com.hisd3.hismk2.domain.payroll.OtherDeductionEmployeeItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface OtherDeductionEmployeeItemRepository extends JpaRepository<OtherDeductionEmployeeItem, UUID> {

    List<OtherDeductionEmployeeItem> findByOtherDeductionEmployeeIn(List<OtherDeductionEmployee> otherDeductionEmployees)

}
