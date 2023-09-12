package com.hisd3.hismk2.rest

import groovy.transform.TypeChecked
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RestController

@TypeChecked
@RestController
@Service
class ResultReceiverResource {

//    @Autowired
//    PatientRepository patientRepository
//
//    @Autowired
//    ServiceRepository serviceRepository
//
//    @Autowired
//    OrderSlipItemRepository orderSlipItemRepository
//
//    @Autowired
//    OrderslipResource orderslipResource
//
//    @Autowired
//    DiagnosticsResultRepository diagnosticsResultRepository
//
//    @RequestMapping(method = [RequestMethod.POST], value =  "/api/result/receiver")
//    ResponseEntity<String> receiver(@RequestBody String payload) {
//        HttpHeaders responseHeaders = new HttpHeaders()
//
//        Gson gson = new Gson()
//        Msgformat data = gson.fromJson(payload, Msgformat.class)
//
//        def attachment
//        if (StringUtils.isNotEmpty(data.attachment)){
//            attachment = Base64.getDecoder().decode(data.attachment)
//        }
//        String sender = data.senderIp
//
//        //def empDoc =  employeeRepository?.findByEmployeeId(data.docEmpId) }
//        Patient patient = patientRepository.getPatientByPatientNo(data.pId).first()
//        HisService  service = serviceRepository.serviceByProcessCode(data.processCode).first()
//        List<OrderSlipItem> listItems = orderSlipItemRepository.findByAccession(data.bacthnum)
//        DiagnosticResult result = new DiagnosticResult()
//
//        if(listItems.size() > 0){
//            listItems.each {
//                if(service == it.service){
//                    result.orderSlipItem = it
//                    result.service = service
//                    result.patient = patient
//                    result.data = data.jsonList
//                    result.accession = data.bacthnum
//                    result.name = data.processCode
//                    if(attachment){
//                        try {
//                            /*** ready for NAS***/
//                            String origin = patient.patientNo+"-"+service.serviceCode
//                            def mime = new Tika().detect(attachment)
//                            String idfname = StringUtils.trim(origin)+".pdf"
//                            result.file_name = origin+".pdf"
//                            result.url_path = orderslipResource.resultWitterOnSmb(it, attachment, idfname)
//                            result.mimetype = mime.toString()
//                        } catch (Exception e) {
//                            e.printStackTrace()
//                            throw e
//                        }
//                    }
//
//                    diagnosticsResultRepository.save(result)
//                }
//                it.status = "COMPLETED"
//                orderSlipItemRepository.save(it)
//            }
//        }else {
//
//            result.patient = patient
//            result.accession = data.bacthnum
//            result.data = data.jsonList
//            diagnosticsResultRepository.save(result)
//        }
//
//        return new ResponseEntity(responseHeaders, HttpStatus.OK)
//
//    }
}
