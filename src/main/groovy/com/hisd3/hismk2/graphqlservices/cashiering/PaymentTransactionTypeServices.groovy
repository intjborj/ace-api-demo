package com.hisd3.hismk2.graphqlservices.cashiering

import com.hisd3.hismk2.domain.accounting.ArInvoice
import com.hisd3.hismk2.domain.cashiering.PaymentTransactionType
import com.hisd3.hismk2.domain.cashiering.PaymentTransactionTypeStatus
import com.hisd3.hismk2.domain.cashiering.PaymentTypeAccounts
import com.hisd3.hismk2.domain.cashiering.PayorType
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.types.GraphQLResVal
import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

import javax.transaction.Transactional
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Canonical
class PaymentTransactionTypeOpt {
    String label
    String value
}

@Service
@GraphQLApi
@Transactional(rollbackOn = Exception.class)
class PaymentTransactionTypeServices extends AbstractDaoService<PaymentTransactionType>{

    PaymentTransactionTypeServices(){
        super(PaymentTransactionType.class)
    }

    @GraphQLMutation(name="upsertPaymentTransactionType")
    GraphQLResVal<PaymentTransactionType> upsertPaymentTransactionType(
            @GraphQLArgument(name="id") UUID id,
            @GraphQLArgument(name="fields") Map<String,Object> fields
    ){
        String message = ""
        def transactionType = upsertFromObjectMapper(id, fields , {  PaymentTransactionType entity, boolean forInsert ->
            if (forInsert) {
                entity.status  = PaymentTransactionTypeStatus.ACTIVE
                message = "Payment Transaction Type successfully created."
                return entity
            }
            else {
                message = "Payment Transaction Type successfully updated."
                return entity
            }
        })
        return new GraphQLResVal<PaymentTransactionType>(transactionType, true, message)
    }

    @GraphQLMutation(name="deletePaymentTransactionType")
    GraphQLResVal<Boolean> deletePaymentTransactionType(
            @GraphQLArgument(name="id") UUID id
    ){
        def transactionType = findOne(id)
        if(transactionType)
            deleteById(id)
        return new GraphQLResVal<Boolean>(true, true, "Payment Transaction Type successfully deleted.")
    }

    @GraphQLQuery(name="findOnePaymentTransactionType")
    PaymentTransactionType findOnePaymentTransactionType(
            @GraphQLArgument(name="id") UUID id
    ){
        id ? findOne(id) : null
    }

    @GraphQLQuery(name="listPageablePaymentTransactionTypes")
    Page<PaymentTransactionType> listPageablePaymentTransactionTypes(
            @GraphQLArgument(name="filter") String filter,
            @GraphQLArgument(name="pageNo") Integer pageNo,
            @GraphQLArgument(name="pageSize") Integer pageSize
    ){
        getPageable(
                """ Select p from PaymentTransactionType p 
                    where  lower(p.typeName) like lower(concat('%',:filter,'%'))
                    order by p.createdDate desc
                """,
                """ Select count(p) from PaymentTransactionType p 
                    where  lower(p.typeName) like lower(concat('%',:filter,'%'))
                """,
                pageNo,
                pageSize,
                [
                        filter:filter
                ] as Map<String,Object>
        )
    }

    @GraphQLQuery(name="findAllPaymentTransactionTypes")
    List<PaymentTransactionType> findAllPaymentTransactionTypes(
            @GraphQLArgument(name="miscType") String miscType
    ){
       def paymentList = findAll().findAll { it-> it.miscType.name() == miscType} ?: []
       return paymentList ?: []
    }

}
