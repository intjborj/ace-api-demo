package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.accounting.AccountReceivableRepository
import com.hisd3.hismk2.security.SecurityUtils
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.http.conn.ConnectTimeoutException
import org.hibernate.query.NativeQuery
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

import javax.persistence.EntityManager
import javax.transaction.Transactional
import java.time.Instant

@Service
@GraphQLApi
class AccountingServices {

    @Autowired
    LedgerServices ledgerServices

    @Autowired
    AccountReceivableRepository accountReceivableRepository

    @Autowired
    EntityManager entityManager

    @Transactional(dontRollbackOn = [ConnectTimeoutException.class])
    @GraphQLMutation(name="approveAllToLedger")
    GraphQLRetVal<Long> approveAllToLedger(){
        try{
            def login =  SecurityUtils.currentLogin()
            Long updated = entityManager.createNativeQuery("""
                Update accounting.header_ledger  
                set 
                    approved_by = :approvedBy, 
                    approved_datetime = :approvedDatetime 
                where approved_by is null""")
                    .setParameter('approvedBy',login)
                    .setParameter('approvedDatetime',Instant.now())
                    .executeUpdate();
            return new GraphQLRetVal<Long>(updated,true,'Posting entries has been started.')
        }
        catch (e){
            return new GraphQLRetVal<Long>(0,false,e.message)
        }
    }

    @Transactional(dontRollbackOn = [ConnectTimeoutException.class])
    @GraphQLMutation(name="approveAllLedgerByTypeAndDate")
    GraphQLRetVal<Long> approveAllLedgerByTypeAndDate(
            @GraphQLArgument(name='journalType') String journalType,
            @GraphQLArgument(name='startDate') String startDate,
            @GraphQLArgument(name='endDate') String endDate
    ){
        try{
            def login =  SecurityUtils.currentLogin()
            Long updated = entityManager.createNativeQuery("""
                update accounting.header_ledger  
                set 
                    approved_by = :approvedBy, 
                    approved_datetime = :approvedDatetime 
                where 
                approved_by is null
                and 
                case 
                    when :journalType != 'ALL'
                    then journal_type = :journalType
                    else true
                end
                and transaction_date_only >= cast(:startDate as date) and transaction_date_only <= cast(:endDate as date)
            """)
                    .setParameter('journalType',journalType)
                    .setParameter('startDate',startDate)
                    .setParameter('endDate',endDate)
                    .setParameter('approvedBy',login)
                    .setParameter('approvedDatetime',Instant.now())
                    .executeUpdate();
            return new GraphQLRetVal<Long>(updated,true,'Posting entries has been started.')
        }
        catch (e){
            return new GraphQLRetVal<Long>(0,false,e.message)
        }
    }


}
