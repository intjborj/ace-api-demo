package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.accounting.ArPaymentTracker
import com.hisd3.hismk2.domain.accounting.ArPaymentTrackerTransaction
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.services.EntityObjectMapperService
import com.hisd3.hismk2.services.GeneratorService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@GraphQLApi
class ArPaymentTrackerTransServices extends AbstractDaoService<ArPaymentTrackerTransaction>{

    ArPaymentTrackerTransServices() {
        super(ArPaymentTrackerTransaction.class)
    }

    @Autowired
    EntityObjectMapperService entityObjectMapperService

    @Autowired
    GeneratorService generatorService

    @GraphQLMutation
    ArPaymentTrackerTransaction saveArPaymentTrackerTrans(
            @GraphQLArgument(name="arPaymentTrans") ArPaymentTrackerTransaction arPaymentTrans
    ){
        save(arPaymentTrans)
    }

    @GraphQLQuery(name = "findArPTTByTransId")
    ArPaymentTrackerTransaction findArPTTByTransId(
            @GraphQLArgument(name="arTransID") UUID arTransID
    ){
        if (arTransID) {
            def query =  createQuery(""" Select a from ArPaymentTrackerTransaction a where a.arTransaction.id = :arTransID """,
                    [
                            arTransID: arTransID
                    ] as Map<String, Object>).singleResult
            if(query){
                return  query
            }
            return new ArPaymentTrackerTransaction()
        }
        else{
            return new ArPaymentTrackerTransaction()
        }
    }

    @GraphQLQuery(name = "getAllPaymentsPerAR")
    List<ArPaymentTrackerTransaction> getAllPaymentsPerAR(
            @GraphQLArgument(name="arId") UUID arId
    ){
        if (arId) {
            def query =  createQuery(""" Select a from ArPaymentTrackerTransaction a where a.accountReceivable.id = :arId and a.status = 'active'""",
                    [
                            arId: arId
                    ] as Map<String, Object>).resultList
            if(query){
                return  query
            }
            return  new ArrayList<ArPaymentTrackerTransaction>()
        }
        else{
            return new ArrayList<ArPaymentTrackerTransaction>()
        }
    }
}
