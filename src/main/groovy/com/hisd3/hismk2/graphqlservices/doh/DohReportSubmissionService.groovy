package com.hisd3.hismk2.graphqlservices.doh

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.hisd3.hismk2.domain.doh.DOH_REPORT_TYPE
import com.hisd3.hismk2.domain.doh.DischargeMobidity
import com.hisd3.hismk2.domain.doh.DohLogs
import com.hisd3.hismk2.domain.hospital_config.HospitalInfo
import com.hisd3.hismk2.graphqlservices.doh.dto.DohDeathsDto
import com.hisd3.hismk2.graphqlservices.doh.dto.DohDeathsPageDto
import com.hisd3.hismk2.graphqlservices.doh.dto.DohTestingDetailsDTo
import com.hisd3.hismk2.graphqlservices.doh.dto.DohTestingDto
import com.hisd3.hismk2.graphqlservices.doh.dto.GenInfoBedCapacity
import com.hisd3.hismk2.graphqlservices.doh.dto.GenInfoBedCapacityResponseDto
import com.hisd3.hismk2.graphqlservices.doh.dto.GenInfoClassificationDTO
import com.hisd3.hismk2.graphqlservices.doh.dto.GenInfoQualityManagementDTO
import com.hisd3.hismk2.graphqlservices.doh.dto.HospOptDischargesEVDTO
import com.hisd3.hismk2.graphqlservices.doh.dto.HospOptDischargesMorbidityDTO
import com.hisd3.hismk2.graphqlservices.doh.dto.HospOptDischargesOPVDTO
import com.hisd3.hismk2.graphqlservices.doh.dto.HospOptDischargesSpecialtyDTO
import com.hisd3.hismk2.graphqlservices.doh.dto.HospOptDischargesTestingDTO
import com.hisd3.hismk2.graphqlservices.doh.dto.HospOtpDischargeEVDto
import com.hisd3.hismk2.graphqlservices.doh.dto.HospitalOperationsDeathsDTO
import com.hisd3.hismk2.graphqlservices.doh.dto.HospitalOperationsHAIDTO
import com.hisd3.hismk2.graphqlservices.doh.dto.HospitalOperationsMajorOptDTO
import com.hisd3.hismk2.graphqlservices.doh.dto.HospitalOperationsMinorOptDTO
import com.hisd3.hismk2.graphqlservices.doh.dto.MajorOpDto
import com.hisd3.hismk2.graphqlservices.doh.dto.OpdMinorOpDto
import com.hisd3.hismk2.graphqlservices.doh.dto.StaffingPatternDTO
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.doh.DischargeMobidityRepository
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

import java.text.SimpleDateFormat
import java.time.Instant

@Component
@GraphQLApi
class DohReportSubmissionService {

    @Autowired
    DischargeMobidityRepository dischargeMobidityRepository

    @Autowired
    DohReportService dohReportService

    @Autowired
    StaffingPatternServices staffingPatternServices

    @Autowired
    DohAPIService dohAPIService

    @Autowired
    DohLogsServices dohLogsServices

    @Autowired
    TestingProcedureServices testingProcedureServices

    @Autowired
    OperationMajorOptServices operationMajorOptServices

    @Autowired
    OperationMinorOptServices operationMinorOptServices

    @Autowired
    BedCapacityServices bedCapacityServices

    @Autowired
    OperationHaiServices operationHaiServices

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    HospitalConfigService hospitalConfigService

    @Autowired
    EmergencyVisitsServices emergencyVisitsServices

    @Autowired
    DischargeSpecialtyServices dischargeSpecialtyServices

    @Autowired
    OpdVisitsService opdVisitsService

    @Autowired
    JdbcTemplate jdbcTemplate


    //==================================Mutation ============

    @GraphQLMutation(name = "submitHospOptDischargesTesting")
    GraphQLRetVal<List<HospOptDischargesTestingDTO>> submitHospOptDischargesTesting(@GraphQLArgument(name = "year") Integer year) {
        List<HospOptDischargesTestingDTO> hospOptDischargesTestingDTOArrayList = []

        DohTestingDto testingDto = testingProcedureServices.dohTestingPerYear(year.toString())

        testingDto.imaging.each {
            HospOptDischargesTestingDTO response = dohAPIService.hospOptDischargesTesting(
                    hospitalConfigService.hospitalInfo.hfhudcode ?: '',
                    it.groupCode,
                    it.code,
                    it.number.toString(),
                    year.toString()
            )
            Map<String, Object> fields = [:]
            fields['testinggroup'] = it.groupCode
            fields['testing'] = it.code
            fields['description'] = it.description
            fields['number'] = it.number.toString()
            fields['year'] = year.toString()

            response.description = it.description
            response.testinggroup = it.groupCode
            response.number = it.number.toString()
            hospOptDischargesTestingDTOArrayList.push(response)

            DohLogs dohLogs = new DohLogs()
            dohLogs.type = DOH_REPORT_TYPE.TESTING.name()
            dohLogs.submittedReport = new JSONObject(fields)
            dohLogs.reportResponse = new Gson().toJson(response)
            dohLogs.reportingYear = year
            dohLogs.status = response.response_desc
            dohLogsServices.save(dohLogs)
        }

        testingDto.laboratoryAndDiagnostic.each {
            HospOptDischargesTestingDTO response = dohAPIService.hospOptDischargesTesting(
                    hospitalConfigService.hospitalInfo.hfhudcode ?: '',
                    it.groupCode,
                    it.code,
                    it.number as String,
                    year.toString()
            )
            Map<String, Object> fields = [:]
            fields['testinggroup'] = it.groupCode
            fields['testing'] = it.code
            fields['description'] = it.description
            fields['number'] = it.number.toString()
            fields['year'] = year.toString()

            response.description = it.description
            response.testinggroup = it.groupCode
            response.number = it.number.toString()
            hospOptDischargesTestingDTOArrayList.push(response)

            DohLogs dohLogs = new DohLogs()
            dohLogs.type = DOH_REPORT_TYPE.TESTING.name()
            dohLogs.submittedReport = new JSONObject(fields)
            dohLogs.reportResponse = new Gson().toJson(response)
            dohLogs.reportingYear = year
            dohLogs.status = response.response_desc
            dohLogsServices.save(dohLogs)
        }

        return new GraphQLRetVal<List<HospOptDischargesTestingDTO>>(hospOptDischargesTestingDTOArrayList, true, '')
    }


    @GraphQLMutation(name = 'dohOptDischargeEV')
    GraphQLRetVal<List<HospOptDischargesEVDTO>> dohOptDischargeEV(@GraphQLArgument(name = 'year') Integer year) {
        List<HospOptDischargesEVDTO> hospOtpDischargeEVDtoList = []

        String emergencyVisit = emergencyVisitsServices.getERDTotalNumberOfPatientsVisitsPerYear(year)
        String emergencyVisitAdult = emergencyVisitsServices.getERDTotalNumberOfPatientsAdultPerYear(year)
        String emergencyVisitPediatric = emergencyVisitsServices.getERDTotalNumberOfPatientsPediatricPerYear(year)
        String evFromFacilityToAnother = emergencyVisitsServices.getERDTotalNumOfPatientsTransferredToOtherPerYear(year)
        String evToFacilityFromAnother = emergencyVisitsServices.getERDTotalNumOfPatientsTransferredFromOtherPerYear(year)

        GraphQLRetVal<HospOptDischargesEVDTO> retVal = dohAPIService.hospOptDischargesEV(
                emergencyVisit,
                emergencyVisitAdult,
                emergencyVisitPediatric,
                evToFacilityFromAnother,
                evFromFacilityToAnother,
                year.toString()
        )

        hospOtpDischargeEVDtoList.push(retVal.payload)
        Map<String, Object> fields = [:]

        fields['Total number of emergency department visits'] = emergencyVisit
        fields['Total number of emergency department visits, adult'] = emergencyVisitAdult
        fields['Total number of emergency department visits, pediatric'] = emergencyVisitPediatric
        fields['Total number of patients transported FROM THIS FACILITY\'S EMERGENCY DEPARTMENT to another facility for inpatient care'] = evFromFacilityToAnother

        if (retVal) {
            DohLogs dohLogs = new DohLogs()
            dohLogs.type = DOH_REPORT_TYPE.DISCHARGES_EV.name()
            dohLogs.submittedReport = new JSONObject(fields)
            dohLogs.reportResponse = new Gson().toJson(retVal.payload)
            dohLogs.reportingYear = year
            dohLogs.status = retVal.payload.response_desc
            dohLogsServices.save(dohLogs)
        }

        return new GraphQLRetVal<List<HospOptDischargesEVDTO>>(hospOtpDischargeEVDtoList, true, '')
    }


    @GraphQLMutation(name = "submitStaffingPattern")
    GraphQLRetVal<List<StaffingPatternDTO>> submitStaffingPattern(@GraphQLArgument(name = "year") Integer year) {

        List<StaffingPatternDTO> staffingPatternDTOArrayList = []

        List<Map<String, Object>> employeeCountByPosition = dohReportService.countEmpByPosition()
        employeeCountByPosition.each {
            StaffingPatternDTO response = dohAPIService.staffingPattern(
                    hospitalConfigService.hospitalInfo.hfhudcode ?: '',
                    it['professiondesignation'].toString(),
                    it['specialtyboardcertified'].toString(),
                    it['fulltime40Permanent'].toString(),
                    it['fulltime40Contractual'].toString(),
                    it['parttimepermanent'].toString(),
                    it['parttimecontractual'].toString(),
                    it['activerotatingaffiliate'].toString(),
                    it['outsourced'].toString(),
                    year.toString()
            )
            response.positionDescription = it['positiondesc']
            staffingPatternDTOArrayList.push(response)

            DohLogs dohLogs = new DohLogs()
            dohLogs.type = DOH_REPORT_TYPE.STAFFING_PATTERN.name()
            dohLogs.submittedReport = new JSONObject(it)
            dohLogs.reportResponse = new Gson().toJson(response)
            dohLogs.reportingYear = year
            dohLogs.status = response.response_desc
            dohLogsServices.save(dohLogs)
        }


        List<Map<String, Object>> employeeCountByPositionOthers = dohReportService.countEmpByPositionOthers()
        employeeCountByPositionOthers.each {
            StaffingPatternDTO response = dohAPIService.staffingPatternOthers(
                    hospitalConfigService.hospitalInfo.hfhudcode ?: '',
                    it['professiondesignation'].toString(),
                    it['positiondesc'].toString(),
                    it['specialtyboardcertified'].toString(),
                    it['fulltime40Permanent'].toString(),
                    it['fulltime40Contractual'].toString(),
                    it['parttimepermanent'].toString(),
                    it['parttimecontractual'].toString(),
                    it['activerotatingaffiliate'].toString(),
                    it['outsourced'].toString(),
                    year.toString()
            )

            response.positionDescription = it['positiondesc']
            staffingPatternDTOArrayList.push(response)

            DohLogs dohLogs = new DohLogs()
            dohLogs.type = DOH_REPORT_TYPE.STAFFING_PATTERN_OTHERS.name()
            dohLogs.submittedReport = new JSONObject(it)
            dohLogs.reportResponse = new Gson().toJson(response)
            dohLogs.reportingYear = year
            dohLogs.status = response.response_desc
            dohLogsServices.save(dohLogs)
        }
        return new GraphQLRetVal<List<StaffingPatternDTO>>(staffingPatternDTOArrayList, true, '')
    }


    @GraphQLMutation(name = "submitStaffingPatternV2")
    GraphQLRetVal<List<StaffingPatternDTO>> submitStaffingPatternV2(@GraphQLArgument(name = "year") Integer year) {

        List<StaffingPatternDTO> staffingPatternDTOArrayList = []

        List<PositionCountDTO> employeeCountByPosition = staffingPatternServices.countEmpByPositionV2()
        employeeCountByPosition.each {
            if (it.isothers) {
                StaffingPatternDTO response = dohAPIService.staffingPatternOthers(
                        hospitalConfigService.hospitalInfo.hfhudcode ?: '',
                        it.professiondesignation.toString(),
                        it.positiondesc.toString(),
                        it.specialtyboardcertified.toString(),
                        it.fulltime40permanent.toString(),
                        it.fulltime40contractual.toString(),
                        it.parttimepermanent.toString(),
                        it.parttimecontractual.toString(),
                        it.activerotatingaffiliate.toString(),
                        it.outsourced.toString(),
                        year.toString()
                )

                response.positionDescription = it['positiondesc']
                staffingPatternDTOArrayList.push(response)

                Map<String, Object> fields = [:]
                fields['professiondesignation'] = it.professiondesignation
                fields['positiondesc'] = it.positiondesc
                fields['specialtyboardcertified'] = it.specialtyboardcertified
                fields['fulltime40permanent'] = it.fulltime40permanent
                fields['fulltime40contractual'] = it.fulltime40contractual
                fields['parttimepermanent'] = it.parttimepermanent
                fields['parttimecontractual'] = it.parttimecontractual
                fields['activerotatingaffiliate'] = it.activerotatingaffiliate
                fields['outsourced'] = it.outsourced
                fields['reportingyear'] = year.toString()

                DohLogs dohLogs = new DohLogs()
                dohLogs.type = DOH_REPORT_TYPE.STAFFING_PATTERN_OTHERS.name()
                dohLogs.submittedReport = new JSONObject(fields)
                dohLogs.reportResponse = new Gson().toJson(response)
                dohLogs.reportingYear = year
                dohLogs.status = response.response_desc
                dohLogsServices.save(dohLogs)
            } else {
                StaffingPatternDTO response = dohAPIService.staffingPattern(
                        hospitalConfigService.hospitalInfo.hfhudcode ?: '',
                        it.professiondesignation.toString(),
                        it.specialtyboardcertified.toString() ?: '0',
                        it.fulltime40permanent.toString(),
                        it.fulltime40contractual.toString(),
                        it.parttimepermanent.toString(),
                        it.parttimecontractual.toString(),
                        it.activerotatingaffiliate.toString(),
                        it.outsourced.toString(),
                        year.toString()
                )
                response.positionDescription = it.positiondesc
                staffingPatternDTOArrayList.push(response)

                Map<String, Object> fields = [:]
                fields['professiondesignation'] = it.professiondesignation
                fields['specialtyboardcertified'] = it.specialtyboardcertified
                fields['fulltime40permanent'] = it.fulltime40permanent
                fields['fulltime40contractual'] = it.fulltime40contractual
                fields['parttimepermanent'] = it.parttimepermanent
                fields['parttimecontractual'] = it.parttimecontractual
                fields['activerotatingaffiliate'] = it.activerotatingaffiliate
                fields['outsourced'] = it.outsourced
                fields['reportingyear'] = year.toString()

                DohLogs dohLogs = new DohLogs()
                dohLogs.type = DOH_REPORT_TYPE.STAFFING_PATTERN.name()
                dohLogs.submittedReport = new JSONObject(fields)
                dohLogs.reportResponse = new Gson().toJson(response)
                dohLogs.reportingYear = year
                dohLogs.status = response.response_desc
                dohLogsServices.save(dohLogs)
            }

        }


        return new GraphQLRetVal<List<StaffingPatternDTO>>(staffingPatternDTOArrayList, true, '')
    }

    @GraphQLMutation(name = "submitHospitalOperationsMajorOpt")
    GraphQLRetVal<List<HospitalOperationsMajorOptDTO>> submitHospitalOperationsMajorOpt(@GraphQLArgument(name = "year") Integer year) {
        List<HospitalOperationsMajorOptDTO> majorOperationDTOs = []

        GraphQLRetVal<List<MajorOpDto>> majorOperations = operationMajorOptServices.findTop10MajorOperations(year)

        majorOperations.payload.each {
            HospitalOperationsMajorOptDTO response = dohAPIService.hospitalOperationsMajorOpt(
                    hospitalConfigService.hospitalInfo.hfhudcode ?: '',
                    it.proccode,
                    it.longName,
                    it.total.toString(),
                    year.toString()
            )

            Map<String, Object> fields = [:]
            fields['operationcode'] = it.proccode
            fields['surgicaloperation'] = it.longName
            fields['number'] = it.total.toString()
            fields['reportingyear'] = year.toString()


            response.operationcode = it.proccode
            response.surgicaloperation = it.longName
            response.number = it.total.toString()

            majorOperationDTOs.push(response)

            DohLogs dohLogs = new DohLogs()
            dohLogs.type = DOH_REPORT_TYPE.MAJOR_OPERATIONS.name()
            dohLogs.submittedReport = new JSONObject(fields)
            dohLogs.reportResponse = new Gson().toJson(response)
            dohLogs.reportingYear = year
            dohLogs.status = response.response_desc
            dohLogsServices.save(dohLogs)
        }

        return new GraphQLRetVal<List<HospitalOperationsMajorOptDTO>>(majorOperationDTOs, true, '')
    }


    @GraphQLMutation(name = "submitHospitalOperationsMinorOpt")
    GraphQLRetVal<List<HospitalOperationsMinorOptDTO>> submitHospitalOperationsMinorOpt(@GraphQLArgument(name = "year") Integer year) {
        List<HospitalOperationsMinorOptDTO> minorOperationDTOs = []

        GraphQLRetVal<List<OpdMinorOpDto>> minorOperations = operationMinorOptServices.findTop10MinorOperations(year)

        minorOperations.payload.each {
            HospitalOperationsMinorOptDTO response = dohAPIService.hospitalOperationsMinorOpt(
                    hospitalConfigService.hospitalInfo.hfhudcode ?: '',
                    it.proccode,
                    it.longName,
                    it.total.toString(),
                    year.toString()
            )

            Map<String, Object> fields = [:]
            fields['operationcode'] = it.proccode
            fields['surgicaloperation'] = it.longName
            fields['number'] = it.total.toString()
            fields['reportingyear'] = year.toString()


            response.operationcode = it.proccode
            response.surgicaloperation = it.longName
            response.number = it.total.toString()

            minorOperationDTOs.push(response)

            DohLogs dohLogs = new DohLogs()
            dohLogs.type = DOH_REPORT_TYPE.MINOR_OPERATIONS.name()
            dohLogs.submittedReport = new JSONObject(fields)
            dohLogs.reportResponse = new Gson().toJson(response)
            dohLogs.reportingYear = year
            dohLogs.status = response.response_desc
            dohLogsServices.save(dohLogs)
        }

        return new GraphQLRetVal<List<HospitalOperationsMinorOptDTO>>(minorOperationDTOs, true, '')
    }

    @GraphQLMutation(name = "submitGenInfoBedCapacity")
    GraphQLRetVal<GenInfoBedCapacityResponseDto> submitGenInfoBedCapacity(@GraphQLArgument(name = "year") Integer year) {
        def abc = bedCapacityServices.getAuthorizedBedCapacity().toString()
        def implementingbeds = bedCapacityServices.getImplementedBeds().toString()
        def bor = bedCapacityServices.getBasedAuthorizedBeds(year).toString()

        GenInfoBedCapacityResponseDto response = dohAPIService.genInfoBedCapacity(
                hospitalConfigService.hospitalInfo.hfhudcode ?: '',
                abc,
                implementingbeds,
                bor,
                year.toString()
        )
        Map<String, Object> fields = [:]
        fields['abc'] = abc
        fields['implementingbeds'] = implementingbeds
        fields['bor'] = bor
        fields['year'] = year.toString()

        DohLogs dohLogs = new DohLogs()
        dohLogs.type = DOH_REPORT_TYPE.BED_CAPACITY.name()
        dohLogs.submittedReport = new JSONObject(fields)
        dohLogs.reportResponse = new Gson().toJson(response)
        dohLogs.reportingYear = year
        dohLogs.status = response.response_desc
        dohLogsServices.save(dohLogs)

        return new GraphQLRetVal<GenInfoBedCapacityResponseDto>(response, true, '')
    }


    @GraphQLMutation(name = "postHospOptDischargesOPV")
    GraphQLRetVal<List<HospOptDischargesOPVDTO>> postHospOptDischargesOPV(@GraphQLArgument(name = "year") Integer year) {

        List<HospOptDischargesOPVDTO> hospOptDischargesOPVDTOList = []

        Long newVisitsAdult = opdVisitsService.getNumberOfOPDNewVisitsAdult(year)
        Long revisitsAdult = opdVisitsService.getNumberOfOPDReVisitsAdult(year)

        Long newVisitsPediatric = opdVisitsService.getNumberOfOPDNewVisitsPediatric(year)
        Long revisitsPediatric = opdVisitsService.getNumberOfOPDReVisitsPediatric(year)

        if (!newVisitsAdult) newVisitsAdult = 0
        if (!revisitsAdult) revisitsAdult = 0
        if (!newVisitsPediatric) newVisitsPediatric = 0
        if (!revisitsPediatric) revisitsPediatric = 0

        String newpatient = newVisitsPediatric + newVisitsAdult
        String revisit = revisitsAdult + revisitsPediatric
        String adult = newVisitsAdult + revisitsAdult
        String pediatric = newVisitsPediatric + revisitsPediatric
        String adultgeneralmedicine = opdVisitsService.getNumberOfOPDAdultGeneralMedicine(year) ?: 0
        String specialtynonsurgical = opdVisitsService.getNumberOfOPDVisitsNonSurgical(year) ?: 0
        String surgical = opdVisitsService.getNumberOfOPDVisitsSurgical(year) ?: 0
        String antenatal = opdVisitsService.getNumberOfOPDVisitsAntenatalPrenatal(year) ?: 0
        String postnatal = opdVisitsService.getNumberOfOPDVisitsAntenatalPostnatal(year) ?: 0

        GraphQLRetVal<HospOptDischargesOPVDTO> retVal = dohAPIService.hospOptDischargesOPV(
                hospitalConfigService.hospitalInfo.hfhudcode ?: '',
                newpatient,
                revisit,
                adult,
                pediatric,
                adultgeneralmedicine,
                specialtynonsurgical,
                surgical,
                antenatal,
                postnatal,
                year.toString()
        )

        hospOptDischargesOPVDTOList.push(retVal.payload)
        Map<String, Object> fields = [:]

        fields['Number of outpatient visits, new patient'] = newpatient
        fields['Number of outpatient visits, re-visit'] = revisit
        fields['Number of outpatient visits, adult'] = adult
        fields['Number of outpatient visits, pediatric'] = pediatric
        fields['Number of adult general medicine outpatient visits'] = adultgeneralmedicine
        fields['Number of specialty (non-surgical) outpatient visits'] = specialtynonsurgical
        fields['Number of surgical outpatient visits'] = surgical
        fields['Number of antenatal/prenatal care visits'] = antenatal
        fields['Number of postnatal care visits'] = postnatal

        if (retVal) {

            DohLogs dohLogs = new DohLogs()
            dohLogs.type = DOH_REPORT_TYPE.DISCHARGE_OPV.name()
            dohLogs.submittedReport = new JSONObject(fields)
            dohLogs.reportResponse = new Gson().toJson(retVal.payload)
            dohLogs.reportingYear = year
            dohLogs.status = retVal.payload.response_desc
            dohLogsServices.save(dohLogs)
        }

        return new GraphQLRetVal<List<HospOptDischargesOPVDTO>>(hospOptDischargesOPVDTOList, true, '')
    }


    @GraphQLMutation(name = "submitHospitalOperationsHAI")
    GraphQLRetVal<HospitalOperationsHAIDTO> submitHospitalOperationsHAI(@GraphQLArgument(name = "year") Integer year) {


        Long patientNumVap = operationHaiServices.getTotalPatientsWithVap(year)
        Long totalVentilatorDays = operationHaiServices.getTotalVentilatorDays(year)
        BigDecimal resultVap = operationHaiServices.getVAP(year)
        Long patientNumBsi = operationHaiServices.getTotalPatientsWithBsi(year)
        Long totalNumCentralLine = operationHaiServices.getTotalCentralLineDays(year)
        BigDecimal resultBsi = operationHaiServices.getBSI(year)
        Long patientNumUti = operationHaiServices.getTotalPatientsWithUti(year)
        Long totalCatheterDays = operationHaiServices.getTotalCatheterDays(year)
        BigDecimal resultUti = operationHaiServices.getUTI(year)
        Long numSsi = operationHaiServices.getTotalSurgicalSiteInfections(year)
        Long totalProceduresDone = operationHaiServices.getTotalProceduresDone(year)
        BigDecimal resultSsi = operationHaiServices.getSSI(year)

        def dischargedResult = jdbcTemplate.queryForList("""select count(*) from pms.cases c  where date_part('year', c.discharged_datetime) = ?::DOUBLE PRECISION""",
                year)
        Long numHai = patientNumVap + patientNumBsi + patientNumUti + numSsi
        BigInteger numDischarges = dischargedResult[0]['count'] as BigInteger
        BigDecimal infectionRate = (numHai / numDischarges) * 100

        HospitalOperationsHAIDTO response = dohAPIService.hospitalOperationsHAI(
                hospitalConfigService.hospitalInfo.hfhudcode ?: '',
                numHai.toString(),
                numDischarges.toString(),
                infectionRate.toString(),
                patientNumVap.toString(),
                totalVentilatorDays.toString(),
                resultVap.toString(),
                patientNumBsi.toString(),
                totalNumCentralLine.toString(),
                resultBsi.toString(),
                patientNumUti.toString(),
                totalCatheterDays.toString(),
                resultUti.toString(),
                numSsi.toString(),
                totalProceduresDone.toString(),
                resultSsi.toString(),
                year.toString()
        )

        Map<String, Object> fields = [:]
        fields['numHai'] = numHai
        fields['numDischarges'] = numDischarges
        fields['infectionRate'] = infectionRate
        fields['patientNumVap'] = patientNumVap
        fields['totalVentilatorDays'] = totalVentilatorDays
        fields['resultVap'] = resultVap
        fields['patientNumBsi'] = patientNumBsi
        fields['totalNumCentralLine'] = totalNumCentralLine
        fields['resultBsi'] = resultBsi
        fields['patientNumUti'] = patientNumUti
        fields['totalCatheterDays'] = totalCatheterDays
        fields['resultUti'] = resultUti
        fields['numSsi'] = numSsi
        fields['totalProceduresDone'] = totalProceduresDone
        fields['resultSsi'] = resultSsi
        fields['year'] = year.toString()


        DohLogs dohLogs = new DohLogs()
        dohLogs.type = DOH_REPORT_TYPE.INFECTIONS.name()
        dohLogs.submittedReport = new JSONObject(fields)
        dohLogs.reportResponse = new Gson().toJson(response)
        dohLogs.reportingYear = year
        dohLogs.status = response.response_desc
        dohLogsServices.save(dohLogs)

        return new GraphQLRetVal<HospitalOperationsHAIDTO>(response, true, '')
    }


    @GraphQLMutation(name = "submitHospitalOperationsDeaths")
    GraphQLRetVal<HospitalOperationsDeathsDTO> submitHospitalOperationsDeaths(@GraphQLArgument(name = "year") Integer year) {
        DohDeathsPageDto deaths = dohReportService.countDeathV3(year)
        String totalDeaths = deaths.deathsDto.totalDeaths.toString()
        String totalDeaths48down = deaths.deathsDto.totalDeathLessThan48.toString()
        String totalDeaths48up = deaths.deathsDto.totalDeathGreaterThanEqualTo48.toString()
        String totalErDeaths = deaths.deathsDto.emergencyRoomDeaths.toString()
        String totalDoa = deaths.deathsDto.deadOnArrivalDeaths.toString()
        String totalStillbirths = deaths.deathsDto.stillbirthsDeaths.toString()
        String totalNeonatalDeaths = deaths.deathsDto.neonatalDeaths.toString()
        String totalMaternalDeaths = deaths.deathsDto.maternalDeaths.toString()
        String totalDeathsNewborn = deaths.deathsDto.totalDeaths.toString()
        String totalDischargeDeaths = deaths.totalDischarged.toString()
        String grossDeathRate = deaths.grossDeathRate.toString()
        def ndrDenominator = deaths.deathsDto.totalDeaths - deaths.deathsDto.totalDeathLessThan48
        def ndrNumerator = deaths.totalDischarged - deaths.deathsDto.totalDeathLessThan48

        String netDeathRate = deaths.netDeathRate.toString()

        HospitalOperationsDeathsDTO response = dohAPIService.hospitalOperationsDeaths(
                hospitalConfigService.hospitalInfo.hfhudcode ?: '',
                totalDeaths,
                totalDeaths48down,
                totalDeaths48up,
                totalErDeaths,
                totalDoa,
                totalStillbirths,
                totalNeonatalDeaths,
                totalMaternalDeaths,
                totalDeathsNewborn,
                totalDischargeDeaths,
                grossDeathRate,
                ndrNumerator.toString(),
                ndrDenominator.toString(),
                netDeathRate,
                year.toString()
        )
        Map<String, Object> fields = [:]
        fields['totalDeaths'] = totalDeaths
        fields['totalDeaths48down'] = totalDeaths48down
        fields['totalDeaths48up'] = totalDeaths48up
        fields['totalErDeaths'] = totalErDeaths
        fields['totalDoa'] = totalDoa
        fields['totalStillbirths'] = totalStillbirths
        fields['totalNeonatalDeaths'] = totalNeonatalDeaths
        fields['totalMaternalDeaths'] = totalMaternalDeaths
        fields['totalDeathsNewborn'] = totalDeathsNewborn
        fields['totalDischargeDeaths'] = totalDischargeDeaths
        fields['grossDeathRate'] = grossDeathRate
        fields['ndrNumerator'] = ndrNumerator
        fields['ndrDenominator'] = ndrDenominator
        fields['netDeathRate'] = netDeathRate
        fields['year'] = year


        DohLogs dohLogs = new DohLogs()
        dohLogs.type = DOH_REPORT_TYPE.DEATHS.name()
        dohLogs.submittedReport = new JSONObject(fields)
        dohLogs.reportResponse = new Gson().toJson(response)
        dohLogs.reportingYear = year
        dohLogs.status = response.response_desc
        dohLogsServices.save(dohLogs)

        return new GraphQLRetVal<HospitalOperationsDeathsDTO>(response, true, '')
    }


    @GraphQLMutation(name = "submitHospOptDischargesSpecialty")
    GraphQLRetVal<List<HospOptDischargesSpecialtyDTO>> submitHospOptDischargesSpecialty(@GraphQLArgument(name = "year") Integer year) {
        List<Map<String, Object>> dischargeSpecialties = dischargeSpecialtyServices.getDischargeSpecialty(year)

        List<HospOptDischargesSpecialtyDTO> dischargesSpecialtyDTOS = []

        HospOptDischargesSpecialtyDTO response

        dischargeSpecialties.each {
            if (it['serviceCode'].toString() != '7') {
                response = dohAPIService.hospOptDischargesSpecialty(
                        hospitalConfigService.hospitalInfo.hfhudcode ?: '',
                        it['serviceCode'].toString(),//typeofservice
                        it['patients'].toString(),//nopatients
                        it['totalDays'].toString(),//totallengthstay
                        it['nonPhicPay'].toString(),//nppay
                        it['nonPhicSerivceCharity'].toString(),//nphservicecharity
                        it['nonPhicTotal'].toString(),//nphtotal
                        it['phicPay'].toString(),//phpay
                        it['phicSerivceCharity'].toString(),//phservice
                        it['phicTotal'].toString(),//phtotal
                        it['hmo'].toString(),//hmo
                        it['owwa'].toString(),//owwa
                        it['ri'].toString(),//recoveredimproved
                        it['tranferred'].toString(),//transferred
                        it['hama'].toString(),//hama
                        it['absconded'].toString(),//absconded
                        it['unimproved'].toString(),//unimproved
                        it['less_than_48hrs'].toString(),//deathsbelow48
                        it['greater_than_48hrs'].toString(),//deathsover48
                        it['total_deaths'].toString(),
                        it['total_discharges'].toString(),//totaldischarges
                        '' as String,//remarks
                        year.toString()
                )
            } else {
                response = dohAPIService.hospOptDischargesSpecialtyOthers(
                        hospitalConfigService.hospitalInfo.hfhudcode ?: '',
                        'Others' as String,
                        it['patients'].toString(),
                        it['totalDays'].toString(),
                        it['nonPhicPay'].toString(),
                        it['nonPhicSerivceCharity'].toString(),
                        it['nonPhicTotal'].toString(),
                        it['phicPay'].toString(),
                        it['phicSerivceCharity'].toString(),
                        it['nonPhicTotal'].toString(),
                        it['hmo'].toString(),
                        it['owwa'].toString(),
                        it['ri'].toString(),
                        it['tranferred'].toString(),
                        it['hama'].toString(),
                        it['absconded'].toString(),
                        it['unimproved'].toString(),
                        it['less_than_48hrs'].toString(),
                        it['greater_than_48hrs'].toString(),
                        it['total_deaths'].toString(),
                        it['total_discharges'].toString(),
                        '' as String,
                        year.toString()
                )
            }


            response.serviceDescription = it['serviceType'].toString()
            dischargesSpecialtyDTOS.push(response)

            DohLogs dohLogs = new DohLogs()
            if (it['serviceCode'].toString() != '7') {
                dohLogs.type = DOH_REPORT_TYPE.DISCHARGES_SPECIALTY.name()
            } else {
                dohLogs.type = DOH_REPORT_TYPE.DISCHARGES_SPECIALTY_OTHERS.name()
            }
            dohLogs.submittedReport = new JSONObject(it)
            dohLogs.reportResponse = new Gson().toJson(response)
            dohLogs.reportingYear = year
            dohLogs.status = response.response_desc
            dohLogsServices.save(dohLogs)
        }

        return new GraphQLRetVal<List<HospOptDischargesSpecialtyDTO>>(dischargesSpecialtyDTOS, true, '')
    }

    @GraphQLMutation(name = "submitGenInfoClassification")
    GraphQLRetVal<GenInfoClassificationDTO> submitGenInfoClassification(@GraphQLArgument(name = "year") Integer year) {
        HospitalInfo hospitalInfo = hospitalConfigService.getHospitalInfo()

        if (!hospitalInfo.dohClassification) {
            return new GraphQLRetVal<GenInfoClassificationDTO>(null, false, 'No data for General Information: Classification')
        }


        GenInfoClassificationDTO response = dohAPIService.genInfoClassification(
                hospitalConfigService.hospitalInfo.hfhudcode ?: '',
                hospitalInfo.dohClassification.servicecapability as String,
                hospitalInfo.dohClassification.general as String,
                hospitalInfo.dohClassification.specialty as String,
                hospitalInfo.dohClassification.specialtyspecify as String,
                hospitalInfo.dohClassification.traumacapability as String,
                hospitalInfo.dohClassification.natureofownership as String,
                hospitalInfo.dohClassification.government as String,
                hospitalInfo.dohClassification.national as String,
                hospitalInfo.dohClassification.local as String,
                hospitalInfo.dohClassification.privateClassification as String,
                year.toString() ?: '',
                hospitalInfo.dohClassification.ownershipothers as String ?: ''
        )

        Map<String, Object> fields = [:]
        fields['servicecapability'] = hospitalInfo.dohClassification.servicecapability.toString()
        fields['general'] = hospitalInfo.dohClassification.general.toString()
        fields['specialty'] = hospitalInfo.dohClassification.specialty.toString()
        fields['specialtyspecify'] = hospitalInfo.dohClassification.specialtyspecify.toString()
        fields['traumacapability'] = hospitalInfo.dohClassification.traumacapability.toString()
        fields['natureofownership'] = hospitalInfo.dohClassification.natureofownership.toString()
        fields['government'] = hospitalInfo.dohClassification.government.toString()
        fields['national'] = hospitalInfo.dohClassification.national.toString()
        fields['local'] = hospitalInfo.dohClassification.local.toString()
        fields['privateClassification'] = hospitalInfo.dohClassification.privateClassification.toString()
        fields['ownershipothers'] = hospitalInfo.dohClassification.ownershipothers.toString()
        fields['year'] = year.toString()

        DohLogs dohLogs = new DohLogs()
        dohLogs.type = DOH_REPORT_TYPE.GEN_INFO_CLASSIFICATION.name()
        dohLogs.submittedReport = new JSONObject(fields)
        dohLogs.reportResponse = new Gson().toJson(response)
        dohLogs.reportingYear = year
        dohLogs.status = response.response_desc
        dohLogsServices.save(dohLogs)

        return new GraphQLRetVal<GenInfoClassificationDTO>(response, true, '')
    }

    @GraphQLMutation(name = "submitGenInfoQualityManagement")
    GraphQLRetVal<List<GenInfoQualityManagementDTO>> submitGenInfoQualityManagement(@GraphQLArgument(name = "year") Integer year) {
        HospitalInfo hospitalInfo = hospitalConfigService.getHospitalInfo()

        if (!hospitalInfo.dohQualityManagement) {
            return new GraphQLRetVal<GenInfoQualityManagementDTO>(null, false, 'No data for General Information: Quality Management')
        }


        List<GenInfoQualityManagementDTO> genInfoQualityManagementDTOList = []
        List<String> keys = ['pcaho', 'isocertified', 'philhealthaccreditation', 'internationalaccreditation']

        keys.each {

            if (hospitalInfo.dohQualityManagement[it]) {
                String validityFrom = new SimpleDateFormat("yyyy-MM-dd").format(hospitalInfo.dohQualityManagement[it]['validityfrom']);
                String validityTo = new SimpleDateFormat("yyyy-MM-dd").format(hospitalInfo.dohQualityManagement[it]['validityto']);

                GenInfoQualityManagementDTO response = dohAPIService.genInfoQualityManagement(
                        hospitalConfigService.hospitalInfo.hfhudcode ?: '',
                        hospitalInfo.dohQualityManagement[it]['qualitymgmttype'] as String,
                        hospitalInfo.dohQualityManagement[it]['description'] as String,
                        hospitalInfo.dohQualityManagement[it]['certifyingbody'] as String,
                        hospitalInfo.dohQualityManagement[it]['philhealthaccreditation'] as String,
                        validityFrom,
                        validityTo,
                        year.toString() ?: '',
                )

                String qualityManagementType = null
                switch (it) {
                    case 'pcaho':
                        qualityManagementType = 'PCAHO'
                        break
                    case 'isocertified':
                        qualityManagementType = 'ISO Certified'
                        break
                    case 'philhealthaccreditation':
                        qualityManagementType = 'PhilHealth Accreditation'
                        break
                    case 'internationalaccreditation':
                        qualityManagementType = 'International Accreditation'
                        break
                }
                response.qualityManagementType = qualityManagementType
                genInfoQualityManagementDTOList.push(response)

                Map<String, Object> fields = [:]
                fields['qualitymgmttype'] = hospitalInfo.dohQualityManagement.qualitymgmttype as String
                fields['description'] = hospitalInfo.dohQualityManagement.description as String
                fields['certifyingbody'] = hospitalInfo.dohQualityManagement.certifyingbody as String
                fields['philhealthaccreditation'] = hospitalInfo.dohQualityManagement.philhealthaccreditation as String
                fields['validityfrom'] = hospitalInfo.dohQualityManagement.validityfrom as String
                fields['validityto'] = hospitalInfo.dohQualityManagement.validityto as String
                fields['year'] = year.toString()

                DohLogs dohLogs = new DohLogs()
                dohLogs.type = DOH_REPORT_TYPE.GEN_INFO_QUALITY_MANAGEMENT.name()
                dohLogs.submittedReport = new JSONObject(fields)
                dohLogs.reportResponse = new Gson().toJson(response)
                dohLogs.reportingYear = year
                dohLogs.status = response.response_desc
                dohLogsServices.save(dohLogs)
            }
        }


        return new GraphQLRetVal<List<GenInfoQualityManagementDTO>>(genInfoQualityManagementDTOList, true, '')
    }

}


