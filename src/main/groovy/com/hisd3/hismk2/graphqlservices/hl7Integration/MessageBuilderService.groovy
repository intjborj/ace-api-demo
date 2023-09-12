package com.hisd3.hismk2.graphqlservices.hl7Integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.ancillary.OrderSlipItem
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.ancillary.OrderSlipItemRepository
import com.hisd3.hismk2.repository.pms.CaseRepository
import com.hisd3.hismk2.rest.MessageBuilderResource
import com.hisd3.hismk2.rest.dto.OrmDto
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.socket.Message
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class MessageBuilder {

    @Autowired
    GeneratorService generatorService

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    MessageBuilderResource messageBuilderResource

    @Autowired
    CaseRepository caseRepository
    @Autowired
    DepartmentRepository departmentRepository

    @Autowired
    OrderSlipItemRepository orderSlipItemRepository

    //============== All Mutations ====================

    def sendToMiddleware(Department parentDepartment, Case activeCase, List<OrderSlipItem> items, Message msg, String device) {
        OrmDto buildMsg = messageBuilderResource.buildMessage(parentDepartment, activeCase, items, device)

        try {
            String res = messageBuilderResource.sendToMiddleWare(buildMsg)
            items.each {
                it.transmitted = true
                it.status = "PROCESSING"
                orderSlipItemRepository.save(it)
            }
            msg.message = res
        }
        catch (Exception e) {
            throw e
        }
    }

    @GraphQLMutation
    Message createOrm(
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    ) {
        //StockRequest stockRequest = objectMapper.convertValue(fields.get("stockRequest"), StockRequest)
        def patientCase = objectMapper.convertValue(fields.get("patientCase"), Case) as Case

        Case activeCase = caseRepository.findById(patientCase.id).get()

        def serviceParentDepartment = objectMapper.convertValue(fields.get("department"), Department)

        Department parentDepartment = departmentRepository.findById(serviceParentDepartment.id).get()
        List<OrderSlipItem> items = []

        List<OrderSlipItem> ordersItems = fields.get("items") as List<OrderSlipItem>
        ordersItems.each {
            it ->
                def order = objectMapper.convertValue(it, OrderSlipItem)
                OrderSlipItem o = orderSlipItemRepository.findById(order.id).get()
                items.add(o)
        }

        def msg = new Message()

        if (parentDepartment.ancillaryConfig) {

            if (parentDepartment.ancillaryConfig.entityName == "RIS") {

                items.each {
                    List<OrderSlipItem> dic_order = []
                    dic_order.add(it)
                    OrmDto buildMsg = messageBuilderResource.buildMessage(parentDepartment, activeCase, dic_order, '')
                    try {
                        String res = messageBuilderResource.sendToMiddleWare(buildMsg)
                        it.transmitted = true
                        it.status = "PROCESSING"
                        orderSlipItemRepository.save(it)
                        msg.message = res
                    } catch (Exception e) {
                        throw e
                    }
                }

            } else if (parentDepartment.ancillaryConfig.entityName == "LIS") {
                List<OrderSlipItem> ordersForAbbott = []
                List<OrderSlipItem> ordersForZafire = []

                items.each {
                    if (it.service.device != null) {
                        if (it.service.device == 'ABBOTT') {
                            ordersForAbbott.add(it)
                        } else {
                            ordersForZafire.add(it)
                        }
                    } else {
                        ordersForZafire.add(it)
                    }

                }

                if (ordersForAbbott.size() > 0)
                    sendToMiddleware(parentDepartment, activeCase, ordersForAbbott, msg, 'ABBOTT')

                if (ordersForZafire.size() > 0)
                    sendToMiddleware(parentDepartment, activeCase, ordersForZafire, msg, 'ZAFIRE')

            } else {
                msg.message = "Integration is not Configured"
            }
        } else {
            msg.message = "Integration is not Configured"
        }

        return msg
    }
}
