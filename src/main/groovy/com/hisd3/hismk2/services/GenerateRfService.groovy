package com.hisd3.hismk2.services


import com.hisd3.hismk2.rest.HisServiceResource
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@TypeChecked
class GenerateRfService {

    @Autowired
    HisServiceResource hisServiceResource


    Boolean generateRfService(String empId,String department,String filter, BigDecimal percentage) {
        hisServiceResource.buildRfTable(empId,department,filter,percentage)
    }
}
