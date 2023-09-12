package com.hisd3.hismk2.rest.reports.pms

import com.google.gson.GsonBuilder
import com.hisd3.hismk2.repository.pms.MedicationRepository
import com.hisd3.hismk2.repository.pms.ObgynHistoryRepository
import com.hisd3.hismk2.rest.dto.DischargeSummaryAbstractDto
import groovy.json.JsonSlurper
import groovy.transform.Canonical
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import org.springframework.context.ApplicationContext
import com.hisd3.hismk2.repository.hospital_config.ConstantRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.pms.CaseRepository
import com.hisd3.hismk2.rest.dto.DischargeInstructionDto
import com.hisd3.hismk2.security.SecurityUtils
import net.sf.jasperreports.engine.JRException
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.export.JRPdfExporter
import net.sf.jasperreports.export.SimpleExporterInput
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput
import net.sf.jasperreports.export.SimplePdfExporterConfiguration
import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import  com.hisd3.hismk2.rest.reports.pms.constants.HistoryConstant

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@Canonical
class TakeHomeMedicinesDetails {
    String id
    String descLong
}

@Canonical
class TakeHomeMedicines {
    TakeHomeMedicinesDetails medicines
}

@Canonical
class  HistoryDto {
    String type

}

@RestController
@RequestMapping(value = ['/reports/print'])
class DischargeSummaryResource {


    @Autowired
    CaseRepository caseRepository

    @PersistenceContext
    EntityManager entityManager

    @Autowired
    ApplicationContext applicationContext

    @Autowired
    EmployeeRepository employeeRepository

    @Autowired
    ConstantRepository constantRepository

    @Autowired
    MedicationRepository medicationRepository

    @Autowired
    ObgynHistoryRepository obgynHistoryRepository



    //--------------this is for Discharge summary report---------------\\


    static Map<String,Integer> getNumberOfRows(Long numerator, Long denominator){
        Map<String,Integer> result = [:]
        result['size'] = Math.floor(numerator / denominator) as Integer
        result['reminder'] = numerator % denominator as Integer
        return  result
    }


    @RequestMapping(value = ['/discharge_summary'], produces = ['application/pdf'])
    ResponseEntity<byte[]> printDischargeSummary(@RequestParam("caseId") UUID caseId) {

        def caseDto = caseRepository.findById(caseId).get()
        def dto = new DischargeSummaryAbstractDto()
        def res = applicationContext.getResource("classpath:/reports/pms/discharge_summary.jasper")
        def logo = applicationContext?.getResource("classpath:/reports/logo.png")
        def parameters = [:] as Map<String, Object>
        def gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()
        def chkchecked = applicationContext.getResource("classpath:/reports/check.png")
        def chkunchecked = applicationContext.getResource("classpath:/reports/uncheck.png")
        def formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
        def dobFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        def age = Period.between(caseDto.patient.dob, LocalDate.now()).years
        def emp = employeeRepository.findByUsername(SecurityUtils.currentLogin())


        dto.patientFullName = caseDto?.patient?.fullName
        dto.address = caseDto?.patient?.fullAddress
        dto.date = formatter.format(Instant.now())
        dto.pin = caseDto?.patient?.patientNo
        dto.caseNo = caseDto?.caseNo
        dto.roomNo = caseDto?.room?.roomNo
        dto.age = age
        dto.gender = caseDto?.patient?.gender
        dto.civilStatus = caseDto?.patient?.civilStatus
        dto.dob = dobFormat.format(caseDto?.patient?.dob)
        dto.attendingPhysician = " ${caseDto?.attendingPhysician?.fullName  ? caseDto?.attendingPhysician?.fullName + 'M.D' : '--'} "
//        dto.attendingPhysician = caseDto?.attendingPhysician?.fullName + " M.D."
        dto.licenseNo = caseDto?.attendingPhysician?.prcLicenseNo

        dto.dateAdmitted = caseDto?.admissionDatetime ? formatter.format(caseDto?.admissionDatetime) : '--'
        dto.dateDischarged = caseDto?.dischargedDatetime ? formatter.format(caseDto?.dischargedDatetime) : '--'
        dto.followUpDate = caseDto?.followupDatetime ? formatter.format(caseDto?.followupDatetime) : '--'
        dto.specialInstructions = caseDto?.specialInstructions ? caseDto?.specialInstructions : '--';

        dto.admittingDiagnosis = caseDto?.admittingDiagnosis ? caseDto?.admittingDiagnosis : '--';
        dto.procedurePerformed = caseDto?.proceduresPerformed ? caseDto?.proceduresPerformed : '--';
        dto.dischargeDiagnosis = caseDto?.dischargeDiagnosis ? caseDto?.dischargeDiagnosis : '--';
        dto.courseInTheWard = caseDto?.courseInTheWard ? caseDto?.courseInTheWard : '--';
        dto.aog = "  ${caseDto?.obgynHistory?.ageOfGestation2 ? caseDto?.obgynHistory?.ageOfGestation2 + ' weeks ' : '--'}  "
        dto.p = caseDto?.obgynHistory?.parturition ? caseDto?.obgynHistory?.parturition : '--'
        dto.g = caseDto?.obgynHistory?.gravida ? caseDto?.obgynHistory?.gravida : '--'
        dto.abort = caseDto?.obgynHistory?.abortion ? caseDto?.obgynHistory?.abortion : '--'
        dto.chiefComplaint = caseDto?.chiefComplaint ? caseDto?.chiefComplaint : '--'


        if (emp) {
            def employee = emp.first()
            dto.nurseName = employee.fullName

        }

        def dt = DateTimeFormatter.ofPattern("MM/dd/yyyy").format(LocalDateTime.now())

        String date = "${dt}"

        parameters.put('date', date ?: '--')
        parameters.put('edc', caseDto?.obgynHistory?.dueDate ? formatter.format(caseDto?.obgynHistory?.dueDate) : '--')
        parameters.put('lmp', caseDto?.obgynHistory?.lastMenstrualPeriod ? formatter.format(caseDto?.obgynHistory?.lastMenstrualPeriod) : '--')

        parameters.put("bp", caseDto.initialBp ?: '--');
        parameters.put("temperature", caseDto.initialTemperature ?: '--');
        parameters.put("o2sat", caseDto.initialO2sat ?: '--');
        parameters.put("rr", caseDto.initialResp ?: '--');
        parameters.put("hr", caseDto.initialPulse ?: '--');

        parameters.put("admittingDiagnosis", caseDto.admittingDiagnosis ?: '--');
        parameters.put("attendingPhysician", caseDto.attendingPhysician ?: '--');
        parameters.put("courseInTheWard", caseDto.courseInTheWard ?: '--');
        parameters.put("disposition", caseDto.dischargeDisposition ?: '--');
        parameters.put("specialInstructions", caseDto.specialInstructions ?: '--');
        parameters.put("medication", caseDto.takeHomeMedications ?: '--');
        parameters.put("followUpSched", caseDto.followupDatetime ? dt : '--')
        parameters.put("logo", logo.inputStream)


        def bytearray = new ByteArrayInputStream()
        def os = new ByteArrayOutputStream()

        def physicalExam = HistoryConstant.physicalExam
        def physicalData = caseDto?.physicalExamList
        def dataList = physicalData ? new JsonSlurper().parseText(physicalData) : ''

        def pertinentExamData = HistoryConstant.pertinentExam
        def pertData = caseDto?.pertinentSymptomsList
        def pertinentData = pertData ? new JsonSlurper().parseText(pertData) : ''

        Map<String,String> physicalExamsCategories = [:]
        dataList.each {
            it ->
                String fields = it['field']
                if(physicalExam[fields]) {
                    String value = physicalExam[fields].description
                    if(value == 'Others')
                        value = it['value']

                    if(!physicalExamsCategories[physicalExam[fields].category])
                        physicalExamsCategories[physicalExam[fields].category] = value
                    else
                        physicalExamsCategories[physicalExam[fields].category] = "${physicalExamsCategories[physicalExam[fields].category]}, ${value}".toString()
                }
        }

        Map<String, String> pertinentSymptoms = [:]
        pertinentData.each {
            it ->
                String fields = it['field']
                if(pertinentExamData[fields]){
                    String value = pertinentExamData[fields].description
                    if(value == 'Others')
                        value = it['value']

                    if(!pertinentSymptoms[pertinentExamData[fields].category])
                        pertinentSymptoms[pertinentExamData[fields].category] = value
                    else
                        pertinentSymptoms[pertinentExamData[fields].category] = "${pertinentSymptoms[pertinentExamData[fields].category]}, ${value}".toString()
                }
        }

        parameters.put("generalSurvery", physicalExamsCategories['General Survey'] ?: "--")
        parameters.put("heent", physicalExamsCategories['HEENT'] ?: "--")
        parameters.put("chestLungs", physicalExamsCategories['chestLungs'] ?: "--")
        parameters.put("cvs", physicalExamsCategories['CVS'] ?: "--")
        parameters.put("abdomen", physicalExamsCategories['abdomen'] ?: "--")
        parameters.put("gu", physicalExamsCategories['gu'] ?: "--")
        parameters.put("skin", physicalExamsCategories['skin'] ?: "--")
        parameters.put("neuroExam", physicalExamsCategories['neuro'] ?: "--")
        parameters.put("pertinentSymptoms", pertinentSymptoms['pertinentSymptoms'] ?: "--")


        List<DischargeInstructionDto> ds = []
        ds << dto

        try {
            def jrprint = JasperFillManager.fillReport(res.inputStream,parameters, new JRBeanCollectionDataSource(ds))

            def pdfExporter = new JRPdfExporter()

            def outputStreamExporterOutput = new SimpleOutputStreamExporterOutput(os)

            pdfExporter.setExporterInput(new SimpleExporterInput(jrprint))
            pdfExporter.setExporterOutput(outputStreamExporterOutput)
            def configuration = new SimplePdfExporterConfiguration()
            pdfExporter.setConfiguration(configuration)
            pdfExporter.exportReport()

        } catch (JRException e) {
            e.printStackTrace()
        } catch (IOException e) {
            e.printStackTrace()
        }

        if (bytearray != null)
            IOUtils.closeQuietly(bytearray)

        def data = os.toByteArray()
        def params = new LinkedMultiValueMap<String, String>()
        params.add("Content-Disposition", "inline;filename=Discharge-Instruction-of-\"" + caseDto?.patient?.fullName + "\".pdf")
        return new ResponseEntity(data, params, HttpStatus.OK)
    }

}
