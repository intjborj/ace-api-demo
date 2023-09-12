package com.hisd3.hismk2.graphqlservices.inventory


import com.hisd3.hismk2.domain.inventory.*
import com.hisd3.hismk2.graphqlservices.inventoryv2.ServicePoItemMonitoring
import com.hisd3.hismk2.repository.inventory.*
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLContext
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


@Component
@GraphQLApi
@TypeChecked
class PurchaseOrderItemService {

    @Autowired
    PurchaseOrderItemRepository purchaseOrderItemRepository

    @Autowired
    ServicePoItemMonitoring servicePoItemMonitoring

    @GraphQLQuery(name = "delQty")
    Integer deliveredQty(@GraphQLContext PurchaseOrderItems poItem) {
        return servicePoItemMonitoring.getPOByDel(poItem.id).delQty
    }

    @GraphQLQuery(name = "delBalance")
    Integer deliveryBalance(@GraphQLContext PurchaseOrderItems poItem) {
        return servicePoItemMonitoring.getPOByDel(poItem.id).delBalance
    }

    @GraphQLQuery(name = "getPOItemsReport")
    List<PurchaseOrderItems> getPOItemsReport(@GraphQLArgument(name = "start") Instant start,
                                               @GraphQLArgument(name = "end") Instant end,
                                               @GraphQLArgument(name = "filter") String filter,
                                               @GraphQLArgument(name = "supplier") UUID supplier) {

        LocalDateTime fromDate = start.atZone(ZoneId.systemDefault()).toLocalDateTime()
        LocalDateTime toDate = end.atZone(ZoneId.systemDefault()).toLocalDateTime()

        if(supplier){
            return purchaseOrderItemRepository.getPOItemByDateRangeSupplier(fromDate,toDate,filter,supplier)
        }else{
            return purchaseOrderItemRepository.getPOItemByDateRange(fromDate,toDate,filter)
        }
    }

}
