package com.hisd3.hismk2.repository.hrm

import com.hisd3.hismk2.domain.hrm.AllowanceTemplateItem
import com.hisd3.hismk2.domain.hrm.AllowanceTemplateItemsId
import org.springframework.data.jpa.repository.JpaRepository

interface AllowanceTemplateItemsRepository extends JpaRepository<AllowanceTemplateItem, AllowanceTemplateItemsId> {

}