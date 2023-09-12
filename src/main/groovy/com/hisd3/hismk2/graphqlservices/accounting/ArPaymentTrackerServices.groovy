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

import javax.persistence.TypedQuery

@Component
@GraphQLApi
class ArPaymentTrackerServices extends AbstractDaoService<ArPaymentTracker>{

    ArPaymentTrackerServices() {
        super(ArPaymentTracker.class)
    }

    @Autowired
    EntityObjectMapperService entityObjectMapperService

    @Autowired
    GeneratorService generatorService

    @GraphQLQuery(name = "findArPaymentTrackerPerID")
    ArPaymentTracker findArPaymentTrackerPerID(
            @GraphQLArgument(name="id") UUID id
    ){
        if(id){
            return  findOne(id)
        }
        return new ArPaymentTracker()
    }


    @GraphQLQuery(name = "findORIfExistARPT")
    ArPaymentTracker findORIfExistARPT(
            @GraphQLArgument(name="paymentTrackerId") UUID paymentTrackerId
    ){
        if (paymentTrackerId) {
            def query =  createQuery(""" Select a from ArPaymentTracker a where a.paymentTracker.id = :paymentTrackerId """,
                    [
                            paymentTrackerId: paymentTrackerId
                    ] as Map<String, Object>).singleResult
            if(query){
                return  query
            }
            return new ArPaymentTracker()
        }
        else{
            return new ArPaymentTracker()
        }
    }

    @GraphQLMutation
    ArPaymentTracker upsertArPaymentTracker(
            @GraphQLArgument(name="id") UUID id,
            @GraphQLArgument(name="fields") Map<String,Object> fields
    ){
        if(id){
            def update = findOne(id)
            entityObjectMapperService.updateFromMap(update,fields)
            save(update)
        }
        else{
            ArPaymentTracker upsert = new ArPaymentTracker()
            entityObjectMapperService.updateFromMap(upsert,fields)
            save(upsert)
            return upsert
        }
    }

    @GraphQLMutation
    ArPaymentTracker saveArPaymentTracker(
            @GraphQLArgument(name="arPayment") ArPaymentTracker arPayment
    ){
        save(arPayment)
    }

}
