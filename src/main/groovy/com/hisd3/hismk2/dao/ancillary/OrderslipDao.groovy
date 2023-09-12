package com.hisd3.hismk2.dao.ancillary


import com.hisd3.hismk2.domain.ancillary.OrderSlipItem
import com.hisd3.hismk2.domain.ancillary.Orderslip
import com.hisd3.hismk2.graphqlservices.ancillary.OrderSlipItemPackageContentService
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.ancillary.OrderSlipItemRepository
import com.hisd3.hismk2.repository.ancillary.OrderslipRepository
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import com.hisd3.hismk2.services.NotificationService
import groovy.transform.TypeChecked
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@TypeChecked
@Service
@Transactional
class OrderslipDao {

    @Autowired
    private OrderslipRepository orderslipRepository

    @Autowired
    GeneratorService generatorService

    @Autowired
    OrderSlipItemRepository orderSlipItemRepository

    @Autowired
    NotificationService notificationService

    @Autowired
    DepartmentRepository departmentRepository

    @Autowired
    OrderSlipItemPackageContentService orderSlipItemPackageContentService

    List<Orderslip> addOrderslip(List<Orderslip> orderslips) {
        List<Orderslip> res = []
        orderslips.each {
            it ->
                it.orderSlipNo = generatorService?.getNextValue(GeneratorType.OrderSlip_NO, { i ->
                    StringUtils.leftPad(i.toString(), 6, "0")
                })
                it.status = "NEW"
                it.deleted = false
                res.add(orderslipRepository.save(it))
        }
        return res
    }

    List<OrderSlipItem> insertOrderTransaction(Orderslip orderSlip, List<OrderSlipItem> orderItems) {

        List<OrderSlipItem> res = []
        Orderslip ret

        if (orderSlip.id == null) {
            orderSlip.status = "NEW"
            orderSlip.deleted = false
            orderSlip.orderSlipNo = generatorService?.getNextValue(GeneratorType.OrderSlip_NO, { i -> StringUtils.leftPad(i.toString(), 6, "0") })
            ret = orderslipRepository.save(orderSlip)
        } else {
            ret = orderSlip
        }

        orderItems.each {
            it ->
                it.orderslip = ret
                it.itemNo = generatorService?.getNextValue(GeneratorType.OrderSlipItem_NO, { i -> StringUtils.leftPad(i.toString(), 6, "0") })
                def orderItem = orderSlipItemRepository.save(it)

                //save package Item
                String type = orderItem.service.serviceType?: ""
                println("type" + type)
                println("it.packageItems" + it.packageItems)
                if(type.equalsIgnoreCase("PACKAGE")){
                    it.packageItems.each {
                        orderSlipItemPackageContentService.upsertOrderSlipItemPackage(
                                orderItem,
                                orderItem.service.department,
                                it.itemId,
                                it.qty
                        )
                    }
                }

                orderItem.tap {
                    ot ->
                        res.add(ot)
                }

                def department = departmentRepository.findById(it.service.department.id).get()
                try {
                    notificationService.notifyUsersOfDepartment(department.parentDepartment.id, "New Service Request", "(" + it.service.category + ") " + it.service.serviceName, "")
                }
                catch (Exception e) {
                    println(e)
                }

        }

        return res
    }

    List<OrderSlipItem> orderSlipsByDepartment(String caseId, String departmentId) {

        Set<Orderslip> uniqOrderSlip = []
        def results =orderSlipItemRepository.orderslipsByDepartmentV2(caseId,departmentId).sort{it.createdDate}
        results.reverse(true)

        return results
    }

    List<Orderslip> orderSlipsByDepartment_old(String caseId, String department) {

        Set<Orderslip> uniqOrderSlip = []
        if (department == "") {
             def results = orderSlipItemRepository.findByCase(UUID.fromString(caseId)).sort {
                it.createdDate
            }
            results.reverse(true)
            for (def item : results) {
                uniqOrderSlip.add(item.orderslip)
            }
        } else {

            def results = orderSlipItemRepository.orderslipsByDepartment(UUID.fromString(caseId), UUID.fromString(department)).sort {
                it.createdDate
            }
            results.reverse(true)
            for (def item : results) {
                uniqOrderSlip.add(item.orderslip)
            }
        }

        return uniqOrderSlip as List<Orderslip>
    }
}
