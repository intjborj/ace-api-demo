package com.hisd3.hismk2.rest

import com.hisd3.hismk2.domain.ancillary.RfFees
import com.hisd3.hismk2.domain.ancillary.Service as HisService
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.repository.ancillary.RfFeesRepository
import com.hisd3.hismk2.repository.ancillary.ServiceRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@TypeChecked
@RestController
@Service
class HisServiceResource {

    @Autowired
    ServiceRepository serviceRepository

    @Autowired
    EmployeeRepository employeeRepository

    @Autowired
    RfFeesRepository  rfFeesRepository



    @RequestMapping(method = RequestMethod.POST, value = "/api/generaterf")
    ResponseEntity<String> generateRf(
            @RequestParam("empId") String empId,
            @RequestParam("category") String category,
            @RequestParam("department") String department,
            @RequestParam("filter") String filter,
            @RequestParam("percentage") BigDecimal percentage

    ) {
        if(buildRfTable(empId,department,filter,percentage)){

            return new ResponseEntity<>(
                    "Success Creating RfTables",
                    HttpStatus.OK)
        }
        else {
            return new ResponseEntity<>(
                    "No Records Found",
                    HttpStatus.BAD_REQUEST)
        }
    }

    Boolean buildRfTable(String empId,String department,String filter, BigDecimal percentage ){

        List<HisService> servicesList = new ArrayList<>()
        servicesList = serviceRepository.searchlistByDepartmentAndCategory(UUID.fromString(department), filter)
        Employee emp = employeeRepository.findById(UUID.fromString(empId)).get()

        if (servicesList.size() > 0) {
            servicesList.each { s ->

                List<RfFees> matchRf = rfFeesRepository.searchMatch(s.id, UUID.fromString(empId))
                if (matchRf.size() > 0) {
                    matchRf[0].rfPercentage = percentage
                    matchRf[0].fixedValue = s.basePrice * (percentage / 100)
                    rfFeesRepository.save(matchRf[0])
                } else {
                    RfFees rfFees = new RfFees()
                    rfFees.doctor = emp
                    rfFees.rfPercentage = percentage
                    rfFees.service = s
                    rfFees.useFixedValue = false
                    rfFees.fixedValue = s.basePrice * (percentage / 100)

                    rfFeesRepository.save(rfFees)
                }
            }
            return true
        }
        else {
            return false
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/updaterf")
    ResponseEntity<String> updaterfResource(
            @RequestParam("rfId") String rfId,
            @RequestParam("percentage") String percentage
    ) {
        RfFees rfsetting = rfFeesRepository.findById(UUID.fromString(rfId)).get()
        BigDecimal percVal = new BigDecimal(percentage)
        if(rfsetting){
          try {
              rfsetting.rfPercentage = percVal
              rfsetting.fixedValue = rfsetting.service.basePrice * (percVal / 100)
              rfFeesRepository.save(rfsetting)
              return new ResponseEntity<>(
                      "Success Creating RfTables",
                      HttpStatus.OK)
          }catch(Exception e){
              return new ResponseEntity<>(
                      e.toString(),
                      HttpStatus.INTERNAL_SERVER_ERROR)
          }
        }
        else {
            return new ResponseEntity<>(
                    "No Records Found",
                    HttpStatus.BAD_REQUEST)
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/updateRfFixedPrice")
    ResponseEntity<String> updateRfFixedPrice(
            @RequestParam("rfId") String rfId,
            @RequestParam("fixedPrice") String fixedPrice
    ) {
        RfFees rfsetting = rfFeesRepository.findById(UUID.fromString(rfId)).get()
        BigDecimal fixedVal =new BigDecimal(fixedPrice)
        if(rfsetting){
            try {
                rfsetting.rfPercentage = (fixedVal / rfsetting.service.basePrice) * 100
                rfsetting.fixedValue = fixedVal
                rfFeesRepository.save(rfsetting)
                return new ResponseEntity<>(
                        "Success Creating RfTables",
                        HttpStatus.OK)
            }catch(Exception e){
                return new ResponseEntity<>(
                        e.toString(),
                        HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }
        else {
            return new ResponseEntity<>(
                    "No Records Found",
                    HttpStatus.BAD_REQUEST)
        }
    }
}


