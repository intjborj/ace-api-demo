package com.hisd3.hismk2.repository.accounting


import com.hisd3.hismk2.domain.accounting.ArTransaction
import com.hisd3.hismk2.domain.accounting.ArTransactionType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ArTransactionTypeRepository extends JpaRepository<ArTransactionType, UUID> {

    @Query(value = '''Select a from ArTransactionType a''')
    List<ArTransactionType> getARTransactionTypeByType()
}
