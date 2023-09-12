package com.hisd3.hismk2.graphqlservices.doh

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.hisd3.hismk2.domain.doh.DOH_REPORT_TYPE
import com.hisd3.hismk2.domain.doh.DohLogs
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.utils.SOAPConnector
import io.leangen.graphql.annotations.*
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.json.JSONObject
import org.json.XML
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ph.gov.doh.uhmistrn.ahsr.webservice.index.AuthenticationTest
import ph.gov.doh.uhmistrn.ahsr.webservice.index.AuthenticationTestResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.CreateNEHEHRSVaccount
import ph.gov.doh.uhmistrn.ahsr.webservice.index.CreateNEHEHRSVaccountResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.Expenses
import ph.gov.doh.uhmistrn.ahsr.webservice.index.ExpensesResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.GenInfoBedCapacity
import com.hisd3.hismk2.graphqlservices.doh.dto.*
import ph.gov.doh.uhmistrn.ahsr.webservice.index.GenInfoBedCapacityResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.GenInfoClassification
import ph.gov.doh.uhmistrn.ahsr.webservice.index.GenInfoClassificationResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.GenInfoQualityManagement
import ph.gov.doh.uhmistrn.ahsr.webservice.index.GenInfoQualityManagementResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.GetDataTable
import ph.gov.doh.uhmistrn.ahsr.webservice.index.GetDataTableResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesER
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesERResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesEV
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesEVResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesMorbidity
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesMorbidityResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesNumberDeliveries
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesNumberDeliveriesResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesOPD
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesOPDResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesOPV
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesOPVResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesSpecialty
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesSpecialtyOthers
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesSpecialtyOthersResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesSpecialtyResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesTesting
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesTestingResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptSummaryOfPatients
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptSummaryOfPatientsResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsDeaths
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsDeathsResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsHAI
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsHAIResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsMajorOpt
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsMajorOptResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsMinorOpt
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsMinorOptResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsMortalityDeaths
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsMortalityDeathsResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.Revenues
import ph.gov.doh.uhmistrn.ahsr.webservice.index.RevenuesResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.StaffingPattern
import ph.gov.doh.uhmistrn.ahsr.webservice.index.StaffingPatternOthers
import ph.gov.doh.uhmistrn.ahsr.webservice.index.StaffingPatternOthersResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.StaffingPatternResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.SubmittedReports
import ph.gov.doh.uhmistrn.ahsr.webservice.index.SubmittedReportsResponse


import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Component
@GraphQLApi
class DohAPIService {

    @Value('${doh.url}')
    String dohUrl

    @Autowired
    SOAPConnector soapConnector

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    HospitalConfigService hospitalConfigService

    @Autowired
    DohLogsServices dohLogsServices

    def static XMLObjectMapper(String str, Class aClass) {
        JSONObject xmlJSONObj = XML.toJSONObject(str)
        def jsonData = xmlJSONObj.toString(2);
        JSONObject primaryDxJson = new JSONObject(jsonData)
        JSONObject jsonObj = primaryDxJson.get('ohsrs') as JSONObject
        return new Gson().fromJson(jsonObj.toString(), aClass)
    }


    @GraphQLMutation(name = "authenticationTest")
    GraphQLRetVal<AuthenticationTestResponseDto> authenticationTest(
            @GraphQLArgument(name = "username") String username,
            @GraphQLArgument(name = "password") String password
    ) {
        try {
            AuthenticationTest request = new AuthenticationTest()
            request.username = username
            request.password = password
            AuthenticationTestResponse response =
                    (AuthenticationTestResponse) soapConnector.callWebService(dohUrl, request)
            AuthenticationTestResponseDto result = XMLObjectMapper(response.return, AuthenticationTestResponseDto.class) as AuthenticationTestResponseDto
            return new GraphQLRetVal<AuthenticationTestResponseDto>(result, true, 'Success')
        }
        catch (e) {
            return new GraphQLRetVal<AuthenticationTestResponseDto>(new AuthenticationTestResponseDto(), false, e.message)
        }

    }

    @GraphQLQuery(name = "getDataTable")
    GraphQLRetVal<String> getDataTable(
            @GraphQLArgument(name = "hfhudcode") String hfhudcode,
            @GraphQLArgument(name = "reportingyear") String reportingyear,
            @GraphQLArgument(name = "table") String table
    ) {
        GetDataTable request = new GetDataTable()
        request.hfhudcode = hfhudcode
        request.reportingyear = reportingyear
        request.table = table
        GetDataTableResponse response =
                (GetDataTableResponse) soapConnector.callWebService(dohUrl, request)
        return new GraphQLRetVal<String>(response.return, true, 'Success')
    }

    @GraphQLMutation(name = "genInfoClassification")
    GenInfoClassificationDTO genInfoClassification(
            @GraphQLArgument(name = "hfhudcode") String hfhudcode,
            @GraphQLArgument(name = "servicecapability") String servicecapability,
            @GraphQLArgument(name = "general") String general,
            @GraphQLArgument(name = "specialty") String specialty,
            @GraphQLArgument(name = "specialtyspecify") String specialtyspecify,
            @GraphQLArgument(name = "traumacapability") String traumacapability,
            @GraphQLArgument(name = "natureofownership") String natureofownership,
            @GraphQLArgument(name = "government") String government,
            @GraphQLArgument(name = "national") String national,
            @GraphQLArgument(name = "local") String local,
            @GraphQLArgument(name = "private") String privated,
            @GraphQLArgument(name = "reportingyear") String reportingyear,
            @GraphQLArgument(name = "ownershipothers") String ownershipothers
    ) {
        GenInfoClassification request = new GenInfoClassification()
        request.hfhudcode = hfhudcode
        request.servicecapability = servicecapability
        request.general = general
        request.specialty = specialty
        request.specialtyspecify = specialtyspecify
        request.traumacapability = traumacapability
        request.natureofownership = natureofownership
        request.government = government
        request.national = national
        request.local = local
        request.private = privated
        request.reportingyear = reportingyear
        request.ownershipothers = ownershipothers

        GenInfoClassificationResponse response =
                (GenInfoClassificationResponse) soapConnector.callWebService(dohUrl, request)
        GenInfoClassificationDTO result = XMLObjectMapper(response.return, GenInfoClassificationDTO.class) as GenInfoClassificationDTO
        return result
    }

    @GraphQLMutation(name = "genInfoQualityManagement")
    GenInfoQualityManagementDTO genInfoQualityManagement(
            @GraphQLArgument(name = "hfhudcode") String hfhudcode,
            @GraphQLArgument(name = "qualitymgmttype") String qualitymgmttype,
            @GraphQLArgument(name = "description") String description,
            @GraphQLArgument(name = "certifyingbody") String certifyingbody,
            @GraphQLArgument(name = "philhealthaccreditation") String philhealthaccreditation,
            @GraphQLArgument(name = "validityfrom") String validityfrom,
            @GraphQLArgument(name = "validityto") String validityto,
            @GraphQLArgument(name = "reportingyear") String reportingyear
    ) {
        GenInfoQualityManagement request = new GenInfoQualityManagement()
        request.hfhudcode = hfhudcode
        request.qualitymgmttype = qualitymgmttype
        request.description = description
        request.certifyingbody = certifyingbody
        request.philhealthaccreditation = philhealthaccreditation
        request.validityfrom = validityfrom
        request.validityto = validityto
        request.reportingyear = reportingyear

        GenInfoQualityManagementResponse response =
                (GenInfoQualityManagementResponse) soapConnector.callWebService(dohUrl, request)
        GenInfoQualityManagementDTO result = XMLObjectMapper(response.return, GenInfoQualityManagementDTO.class) as GenInfoQualityManagementDTO
        return result
    }

    @GraphQLMutation(name = "genInfoBedCapacity")
    GenInfoBedCapacityResponseDto genInfoBedCapacity(
            @GraphQLArgument(name = "hfhudcode") String hfhudcode,
            @GraphQLArgument(name = "abc") String abc,
            @GraphQLArgument(name = "implementingbeds") String implementingbeds,
            @GraphQLArgument(name = "bor") String bor,
            @GraphQLArgument(name = "reportingyear") String reportingyear
    ) {
        GenInfoBedCapacity request = new GenInfoBedCapacity()
        request.hfhudcode = hfhudcode
        request.abc = abc
        request.implementingbeds = implementingbeds
        request.bor = bor
        request.reportingyear = reportingyear
        GenInfoBedCapacityResponse response =
                (GenInfoBedCapacityResponse) soapConnector.callWebService(dohUrl, request)
        GenInfoBedCapacityResponseDto result = XMLObjectMapper(response.return, GenInfoBedCapacityResponseDto.class) as GenInfoBedCapacityResponseDto
        return result
    }

    @GraphQLMutation(name = "hospOptSummaryOfPatients")
    GraphQLRetVal<SummaryOfPatientDTO> hospOptSummaryOfPatients(
            @GraphQLArgument(name = "hfhudcode") String hfhudcode = '',
            @GraphQLArgument(name = "totalinpatients") String totalinpatients,
            @GraphQLArgument(name = "totalnewborn") String totalnewborn,
            @GraphQLArgument(name = "totaldischarges") String totaldischarges,
            @GraphQLArgument(name = "totalpad") String totalpad,
            @GraphQLArgument(name = "totalibd") String totalibd,
            @GraphQLArgument(name = "totalinpatienttransto") String totalinpatienttransto,
            @GraphQLArgument(name = "totalinpatienttransfrom") String totalinpatienttransfrom,
            @GraphQLArgument(name = "totalpatientsremaining") String totalpatientsremaining,
            @GraphQLArgument(name = "reportingyear") String reportingyear
    ) {
        SummaryOfPatientDTO result = new SummaryOfPatientDTO()
        try {
            HospOptSummaryOfPatients request = new HospOptSummaryOfPatients()
            request.hfhudcode = hfhudcode ?: hospitalConfigService.hospitalInfo.hfhudcode
            request.totalinpatients = totalinpatients
            request.totalnewborn = totalnewborn
            request.totaldischarges = totaldischarges
            request.totalpad = totalpad
            request.totalibd = totalibd
            request.totalinpatienttransto = totalinpatienttransto
            request.totalinpatienttransfrom = totalinpatienttransfrom
            request.totalpatientsremaining = totalpatientsremaining
            request.reportingyear = reportingyear

            HospOptSummaryOfPatientsResponse response =
                    (HospOptSummaryOfPatientsResponse) soapConnector.callWebService(dohUrl, request)
            result = XMLObjectMapper(response.return, SummaryOfPatientDTO.class) as SummaryOfPatientDTO
            return new GraphQLRetVal<SummaryOfPatientDTO>(result, true, 'Success')

        } catch (e) {
            result.response_code = '408'
            result.response_datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a"))
            result.response_desc = ' Time Out '
            return new GraphQLRetVal<SummaryOfPatientDTO>(result, false, e.message)
        }

    }


    @GraphQLMutation(name = "hospOptDischargesSpecialty")
    HospOptDischargesSpecialtyDTO hospOptDischargesSpecialty(
            @GraphQLArgument(name = "hfhudcode") String hfhudcode,
            @GraphQLArgument(name = "typeofservice") String typeofservice,
            @GraphQLArgument(name = "nopatients") String nopatients,
            @GraphQLArgument(name = "totallengthstay") String totallengthstay,
            @GraphQLArgument(name = "nppay") String nppay,
            @GraphQLArgument(name = "nphservicecharity") String nphservicecharity,
            @GraphQLArgument(name = "nphtotal") String nphtotal,
            @GraphQLArgument(name = "phpay") String phpay,
            @GraphQLArgument(name = "phservice") String phservice,
            @GraphQLArgument(name = "phtotal") String phtotal,
            @GraphQLArgument(name = "hmo") String hmo,
            @GraphQLArgument(name = "owwa") String owwa,
            @GraphQLArgument(name = "recoveredimproved") String recoveredimproved,
            @GraphQLArgument(name = "transferred") String transferred,
            @GraphQLArgument(name = "hama") String hama,
            @GraphQLArgument(name = "absconded") String absconded,
            @GraphQLArgument(name = "unimproved") String unimproved,
            @GraphQLArgument(name = "deathsbelow48") String deathsbelow48,
            @GraphQLArgument(name = "deathsover48") String deathsover48,
            @GraphQLArgument(name = "totaldeaths") String totaldeaths,
            @GraphQLArgument(name = "totaldischarges") String totaldischarges,
            @GraphQLArgument(name = "remarks") String remarks,
            @GraphQLArgument(name = "reportingyear") String reportingyear
    ) {
        HospOptDischargesSpecialty request = new HospOptDischargesSpecialty()
        request.hfhudcode = hfhudcode
        request.typeofservice = typeofservice
        request.nopatients = nopatients
        request.totallengthstay = totallengthstay
        request.nppay = nppay
        request.nphservicecharity = nphservicecharity
        request.nphtotal = nphtotal
        request.phpay = phpay
        request.phservice = phservice
        request.phtotal = phtotal
        request.hmo = hmo
        request.owwa = owwa
        request.recoveredimproved = recoveredimproved
        request.transferred = transferred
        request.hama = hama
        request.absconded = absconded
        request.unimproved = unimproved
        request.deathsbelow48 = deathsbelow48
        request.deathsover48 = deathsover48
        request.totaldeaths = totaldeaths
        request.totaldischarges = totaldischarges
        request.remarks = remarks
        request.reportingyear = reportingyear

        HospOptDischargesSpecialtyResponse response =
                (HospOptDischargesSpecialtyResponse) soapConnector.callWebService(dohUrl, request)
        HospOptDischargesSpecialtyDTO result = XMLObjectMapper(response.return, HospOptDischargesSpecialtyDTO.class) as HospOptDischargesSpecialtyDTO
        return result
    }

    @GraphQLMutation(name = "hospOptDischargesSpecialtyOthers")
    HospOptDischargesSpecialtyDTO hospOptDischargesSpecialtyOthers(
            @GraphQLArgument(name = "hfhudcode") String hfhudcode,
            @GraphQLArgument(name = "othertypeofservicespecify") String othertypeofservicespecify,
            @GraphQLArgument(name = "nopatients") String nopatients,
            @GraphQLArgument(name = "totallengthstay") String totallengthstay,
            @GraphQLArgument(name = "nppay") String nppay,
            @GraphQLArgument(name = "nphservicecharity") String nphservicecharity,
            @GraphQLArgument(name = "nphtotal") String nphtotal,
            @GraphQLArgument(name = "phpay") String phpay,
            @GraphQLArgument(name = "phservice") String phservice,
            @GraphQLArgument(name = "phtotal") String phtotal,
            @GraphQLArgument(name = "hmo") String hmo,
            @GraphQLArgument(name = "owwa") String owwa,
            @GraphQLArgument(name = "recoveredimproved") String recoveredimproved,
            @GraphQLArgument(name = "transferred") String transferred,
            @GraphQLArgument(name = "hama") String hama,
            @GraphQLArgument(name = "absconded") String absconded,
            @GraphQLArgument(name = "unimproved") String unimproved,
            @GraphQLArgument(name = "deathsbelow48") String deathsbelow48,
            @GraphQLArgument(name = "deathsover48") String deathsover48,
            @GraphQLArgument(name = "totaldeaths") String totaldeaths,
            @GraphQLArgument(name = "totaldischarges") String totaldischarges,
            @GraphQLArgument(name = "remarks") String remarks,
            @GraphQLArgument(name = "reportingyear") String reportingyear
    ) {
        HospOptDischargesSpecialtyOthers request = new HospOptDischargesSpecialtyOthers()
        request.hfhudcode = hfhudcode
        request.othertypeofservicespecify = othertypeofservicespecify
        request.nopatients = nopatients
        request.totallengthstay = totallengthstay
        request.nppay = nppay
        request.nphservicecharity = nphservicecharity
        request.nphtotal = nphtotal
        request.phpay = phpay
        request.phservice = phservice
        request.phtotal = phtotal
        request.hmo = hmo
        request.owwa = owwa
        request.recoveredimproved = recoveredimproved
        request.transferred = transferred
        request.hama = hama
        request.absconded = absconded
        request.unimproved = unimproved
        request.deathsbelow48 = deathsbelow48
        request.deathsover48 = deathsover48
        request.totaldeaths = totaldeaths
        request.totaldischarges = totaldischarges
        request.remarks = remarks
        request.reportingyear = reportingyear

        HospOptDischargesSpecialtyOthersResponse response =
                (HospOptDischargesSpecialtyOthersResponse) soapConnector.callWebService(dohUrl, request)
        HospOptDischargesSpecialtyDTO result = XMLObjectMapper(response.return, HospOptDischargesSpecialtyDTO.class) as HospOptDischargesSpecialtyDTO
        return result
    }


    @GraphQLMutation(name = "hospOptDischargesMorbidity")
    HospOptDischargesMorbidityDTO hospOptDischargesMorbidity(
            @GraphQLArgument(name = "hfhudcode") String hfhudcode,
            @GraphQLArgument(name = "icd10desc") String icd10desc,
            @GraphQLArgument(name = "munder1") String munder1,
            @GraphQLArgument(name = "funder1") String funder1,
            @GraphQLArgument(name = "m1to4") String m1to4,
            @GraphQLArgument(name = "f1to4") String f1to4,
            @GraphQLArgument(name = "m5to9") String m5to9,
            @GraphQLArgument(name = "f5to9") String f5to9,
            @GraphQLArgument(name = "m10to14") String m10to14,
            @GraphQLArgument(name = "f10to14") String f10to14,
            @GraphQLArgument(name = "m15to19") String m15to19,
            @GraphQLArgument(name = "f15to19") String f15to19,
            @GraphQLArgument(name = "m20to24") String m20to24,
            @GraphQLArgument(name = "f20to24") String f20to24,
            @GraphQLArgument(name = "m25to29") String m25to29,
            @GraphQLArgument(name = "f25to29") String f25to29,
            @GraphQLArgument(name = "m30to34") String m30to34,
            @GraphQLArgument(name = "f30to34") String f30to34,
            @GraphQLArgument(name = "m35to39") String m35to39,
            @GraphQLArgument(name = "f35to39") String f35to39,
            @GraphQLArgument(name = "m40to44") String m40to44,
            @GraphQLArgument(name = "f40to44") String f40to44,
            @GraphQLArgument(name = "m45to49") String m45to49,
            @GraphQLArgument(name = "f45to49") String f45to49,
            @GraphQLArgument(name = "m50to54") String m50to54,
            @GraphQLArgument(name = "f50to54") String f50to54,
            @GraphQLArgument(name = "m55to59") String m55to59,
            @GraphQLArgument(name = "f55to59") String f55to59,
            @GraphQLArgument(name = "m60to64") String m60to64,
            @GraphQLArgument(name = "f60to64") String f60to64,
            @GraphQLArgument(name = "m65to69") String m65to69,
            @GraphQLArgument(name = "f65to69") String f65to69,
            @GraphQLArgument(name = "m70over") String m70over,
            @GraphQLArgument(name = "f70over") String f70over,
            @GraphQLArgument(name = "msubtotal") String msubtotal,
            @GraphQLArgument(name = "fsubtotal") String fsubtotal,
            @GraphQLArgument(name = "grandtotal") String grandtotal,
            @GraphQLArgument(name = "icd10code") String icd10code,
            @GraphQLArgument(name = "icd10category") String icd10category,
            @GraphQLArgument(name = "reportingyear") String reportingyear
    ) {
        HospOptDischargesMorbidity request = new HospOptDischargesMorbidity()
        request.hfhudcode = hfhudcode
        request.icd10Desc = icd10desc
        request.munder1 = munder1
        request.funder1 = funder1
        request.m1To4 = m1to4
        request.f1To4 = f1to4
        request.m5To9 = m5to9
        request.f5To9 = f5to9
        request.m10To14 = m10to14
        request.f10To14 = f10to14
        request.m15To19 = m15to19
        request.f15To19 = f15to19
        request.m20To24 = m20to24
        request.f20To24 = f20to24
        request.m25To29 = m25to29
        request.f25To29 = f25to29
        request.m30To34 = m30to34
        request.f30To34 = f30to34
        request.m35To39 = m35to39
        request.f35To39 = f35to39
        request.m40To44 = m40to44
        request.f40To44 = f40to44
        request.m45To49 = m45to49
        request.f45To49 = f45to49
        request.m50To54 = m50to54
        request.f50To54 = f50to54
        request.m55To59 = m55to59
        request.f55To59 = f55to59
        request.m60To64 = m60to64
        request.f60To64 = f60to64
        request.m65To69 = m65to69
        request.f65To69 = f65to69
        request.m70Over = m70over
        request.f70Over = f70over
        request.msubtotal = msubtotal
        request.fsubtotal = fsubtotal
        request.grandtotal = grandtotal
        request.icd10Code = icd10code
        request.icd10Category = icd10category
        request.reportingyear = reportingyear

        HospOptDischargesMorbidityResponse response =
                (HospOptDischargesMorbidityResponse) soapConnector.callWebService(dohUrl, request)
        HospOptDischargesMorbidityDTO result = XMLObjectMapper(response.return, HospOptDischargesMorbidityDTO.class) as HospOptDischargesMorbidityDTO
        result.icd10desc = icd10desc
        result.icd10code = icd10code

        return result
    }


    @GraphQLMutation(name = "hospOptDischargesNumberDeliveries")
    GraphQLRetVal<HospOptDischargesNumberDeliveriesDto> hospOptDischargesNumberDeliveries(
            @GraphQLArgument(name = "totalifdelivery") String totalifdelivery,
            @GraphQLArgument(name = "totallbvdelivery") String totallbvdelivery,
            @GraphQLArgument(name = "totallbcdelivery") String totallbcdelivery,
            @GraphQLArgument(name = "totalotherdelivery") String totalotherdelivery,
            @GraphQLArgument(name = "reportingyear") String reportingyear,
            @GraphQLArgument(name = "hfhudcode") String hfhudcode = ''
    ) {
        HospOptDischargesNumberDeliveriesDto result = new HospOptDischargesNumberDeliveriesDto()
        try {
            HospOptDischargesNumberDeliveries request = new HospOptDischargesNumberDeliveries()
            request.hfhudcode = hfhudcode ?: hospitalConfigService.hospitalInfo.hfhudcode
            request.totalifdelivery = totalifdelivery
            request.totallbvdelivery = totallbvdelivery
            request.totallbcdelivery = totallbcdelivery
            request.totalotherdelivery = totalotherdelivery
            request.reportingyear = reportingyear

            HospOptDischargesNumberDeliveriesResponse response =
                    (HospOptDischargesNumberDeliveriesResponse) soapConnector.callWebService(dohUrl, request)
            result = XMLObjectMapper(response.return, HospOptDischargesNumberDeliveriesDto.class) as HospOptDischargesNumberDeliveriesDto
            return new GraphQLRetVal<HospOptDischargesNumberDeliveriesDto>(result, true, 'Successfully Submitted')
        } catch (e) {

            result.response_code = '408'
            result.response_datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a"))
            result.response_desc = ' Time Out '
            return new GraphQLRetVal<HospOptDischargesNumberDeliveriesDto>(result, false, e.message)
        }
    }

    @GraphQLMutation(name = "hospOptDischargesOPV")
    GraphQLRetVal<HospOptDischargesOPVDTO> hospOptDischargesOPV(
            @GraphQLArgument(name = "hfhudcode") String hfhudcode = '',
            @GraphQLArgument(name = "newpatient") String newpatient,
            @GraphQLArgument(name = "revisit") String revisit,
            @GraphQLArgument(name = "adult") String adult,
            @GraphQLArgument(name = "pediatric") String pediatric,
            @GraphQLArgument(name = "adultgeneralmedicine") String adultgeneralmedicine,
            @GraphQLArgument(name = "specialtynonsurgical") String specialtynonsurgical,
            @GraphQLArgument(name = "surgical") String surgical,
            @GraphQLArgument(name = "antenatal") String antenatal,
            @GraphQLArgument(name = "postnatal") String postnatal,
            @GraphQLArgument(name = "reportingyear") String reportingyear
    ) {

        HospOptDischargesOPVDTO result = new HospOptDischargesOPVDTO()

        try {
            HospOptDischargesOPV request = new HospOptDischargesOPV()
            request.hfhudcode = hfhudcode ?: hospitalConfigService.hospitalInfo.hfhudcode
            request.newpatient = newpatient
            request.revisit = revisit
            request.adult = adult
            request.pediatric = pediatric
            request.adultgeneralmedicine = adultgeneralmedicine
            request.specialtynonsurgical = specialtynonsurgical
            request.surgical = surgical
            request.antenatal = antenatal
            request.postnatal = postnatal
            request.reportingyear = reportingyear

            HospOptDischargesOPVResponse response =
                    (HospOptDischargesOPVResponse) soapConnector.callWebService(dohUrl, request)
            result = XMLObjectMapper(response.return, HospOptDischargesOPVDTO.class) as HospOptDischargesOPVDTO
            return new GraphQLRetVal<HospOptDischargesOPVDTO>(result, true, 'Success')

        } catch (e) {
            result.response_code = '408'
            result.response_datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a"))
            result.response_desc = ' Time Out '
            return new GraphQLRetVal<HospOptDischargesOPVDTO>(result, false, e.message)
        }
    }

    @GraphQLMutation(name = "hospOptDischargesOPD")
    GraphQLRetVal<HospOptDischargesOPDDTO> hospOptDischargesOPD(
            @GraphQLArgument(name = "hfhudcode") String hfhudcode = '',
            @GraphQLArgument(name = "opdconsultations") String opdconsultations,
            @GraphQLArgument(name = "number") String number,
            @GraphQLArgument(name = "icd10code") String icd10code,
            @GraphQLArgument(name = "icd10category") String icd10category,
            @GraphQLArgument(name = "reportingyear") String reportingyear
    ) {
        HospOptDischargesOPDDTO result = new HospOptDischargesOPDDTO()
        try {
            HospOptDischargesOPD request = new HospOptDischargesOPD()
//        request.hfhudcode = hfhudcode
            request.hfhudcode = hfhudcode ?: hospitalConfigService.hospitalInfo.hfhudcode
            request.opdconsultations = opdconsultations
            request.number = number
            request.icd10Code = icd10code
            request.icd10Category = icd10category
            request.reportingyear = reportingyear

            HospOptDischargesOPDResponse response =
                    (HospOptDischargesOPDResponse) soapConnector.callWebService(dohUrl, request)
            result = XMLObjectMapper(response.return, HospOptDischargesOPDDTO.class) as HospOptDischargesOPDDTO
            return new GraphQLRetVal<HospOptDischargesOPDDTO>(result, true, 'success')

        } catch (e) {
            result.response_code = '408'
            result.response_datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a"))
            result.response_desc = 'Time Out'
            return new GraphQLRetVal<HospOptDischargesOPDDTO>(result, false, e.message)
        }

    }

    @GraphQLMutation(name = "hospOptDischargesER")
    GraphQLRetVal<HospOptDischargesERDTO> hospOptDischargesER(
            @GraphQLArgument(name = "hfhudcode") String hfhudcode = '',
            @GraphQLArgument(name = "erconsultations") String erconsultations,
            @GraphQLArgument(name = "number") String number,
            @GraphQLArgument(name = "icd10code") String icd10code,
            @GraphQLArgument(name = "icd10category") String icd10category,
            @GraphQLArgument(name = "reportingyear") String reportingyear
    ) {

        HospOptDischargesERDTO result = new HospOptDischargesERDTO()
        try {
            HospOptDischargesER request = new HospOptDischargesER()
//            request.hfhudcode = hfhudcode
            request.hfhudcode = hfhudcode ?: hospitalConfigService.hospitalInfo.hfhudcode
            request.erconsultations = erconsultations
            request.number = number
            request.icd10Code = icd10code
            request.icd10Category = icd10category
            request.reportingyear = reportingyear

            HospOptDischargesERResponse response =
                    (HospOptDischargesERResponse) soapConnector.callWebService(dohUrl, request)
            result = XMLObjectMapper(response.return, HospOptDischargesERDTO.class) as HospOptDischargesERDTO
            return new GraphQLRetVal<HospOptDischargesERDTO>(result, true, 'Success')

        } catch (e) {
            result.response_code = '408'
            result.response_datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a"))
            result.response_desc = ' Time Out '
            return new GraphQLRetVal<HospOptDischargesERDTO>(result, false, e.message)
        }
    }

    @GraphQLMutation(name = "hospOptDischargesTesting")
    HospOptDischargesTestingDTO hospOptDischargesTesting(
            @GraphQLArgument(name = "hfhudcode") String hfhudcode,
            @GraphQLArgument(name = "testinggroup") String testinggroup,
            @GraphQLArgument(name = "testing") String testing,
            @GraphQLArgument(name = "number") String number,
            @GraphQLArgument(name = "reportingyear") String reportingyear
    ) {
        HospOptDischargesTesting request = new HospOptDischargesTesting()
        request.hfhudcode = hfhudcode
        request.testinggroup = testinggroup
        request.testing = testing
        request.number = number
        request.reportingyear = reportingyear

        HospOptDischargesTestingResponse response =
                (HospOptDischargesTestingResponse) soapConnector.callWebService(dohUrl, request)
        HospOptDischargesTestingDTO result = XMLObjectMapper(response.return, HospOptDischargesTestingDTO.class) as HospOptDischargesTestingDTO
        return result
    }

    @GraphQLMutation(name = "hospOptDischargesEV")
    GraphQLRetVal<HospOptDischargesEVDTO> hospOptDischargesEV(
            @GraphQLArgument(name = "hfhudcode") String hfhudcode = '',
            @GraphQLArgument(name = "emergencyvisits") String emergencyvisits,
            @GraphQLArgument(name = "emergencyvisitsadult") String emergencyvisitsadult,
            @GraphQLArgument(name = "emergencyvisitspediatric") String emergencyvisitspediatric,
            @GraphQLArgument(name = "evtofacilityfromanother") String evtofacilityfromanother,
            @GraphQLArgument(name = "evfromfacilitytoanother") String evfromfacilitytoanother,

            @GraphQLArgument(name = "reportingyear") String reportingyear
    ) {

        HospOptDischargesEVDTO result = new HospOptDischargesEVDTO()

        try {
            HospOptDischargesEV request = new HospOptDischargesEV()
            request.hfhudcode = hfhudcode ?: hospitalConfigService.hospitalInfo.hfhudcode
            request.emergencyvisits = emergencyvisits
            request.emergencyvisitsadult = emergencyvisitsadult
            request.emergencyvisitspediatric = emergencyvisitspediatric
            request.evtofacilityfromanother = evtofacilityfromanother
            request.evfromfacilitytoanother = evfromfacilitytoanother
            request.reportingyear = reportingyear

            HospOptDischargesEVResponse response =
                    (HospOptDischargesEVResponse) soapConnector.callWebService(dohUrl, request)
            result = XMLObjectMapper(response.return, HospOptDischargesEVDTO.class) as HospOptDischargesEVDTO
            return new GraphQLRetVal<HospOptDischargesEVDTO>(result, true, 'Success')

        } catch (e) {
            result.response_code = '408'
            result.response_datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a"))
            result.response_desc = ' Time Out '
            return new GraphQLRetVal<HospOptDischargesEVDTO>(result, false, e.message)
        }
    }

    ///

    @GraphQLMutation(name = "hospitalOperationsDeaths")
    HospitalOperationsDeathsDTO hospitalOperationsDeaths(
            @GraphQLArgument(name = "hfhudcode") String hfhudcode,
            @GraphQLArgument(name = "totaldeaths") String totaldeaths,
            @GraphQLArgument(name = "totaldeaths48down") String totaldeaths48down,
            @GraphQLArgument(name = "totaldeaths48up") String totaldeaths48up,
            @GraphQLArgument(name = "totalerdeaths") String totalerdeaths,
            @GraphQLArgument(name = "totaldoa") String totaldoa,
            @GraphQLArgument(name = "totalstillbirths") String totalstillbirths,
            @GraphQLArgument(name = "totalneonataldeaths") String totalneonataldeaths,
            @GraphQLArgument(name = "totalmaternaldeaths") String totalmaternaldeaths,
            @GraphQLArgument(name = "totaldeathsnewborn") String totaldeathsnewborn,
            @GraphQLArgument(name = "totaldischargedeaths") String totaldischargedeaths,
            @GraphQLArgument(name = "grossdeathrate") String grossdeathrate,
            @GraphQLArgument(name = "ndrnumerator") String ndrnumerator,
            @GraphQLArgument(name = "ndrdenominator") String ndrdenominator,
            @GraphQLArgument(name = "netdeathrate") String netdeathrate,
            @GraphQLArgument(name = "reportingyear") String reportingyear
    ) {
        HospitalOperationsDeaths request = new HospitalOperationsDeaths()
        request.hfhudcode = hfhudcode
        request.totaldeaths = totaldeaths
        request.totaldeaths48Down = totaldeaths48down
        request.totaldeaths48Up = totaldeaths48up
        request.totalerdeaths = totalerdeaths
        request.totaldoa = totaldoa
        request.totalstillbirths = totalstillbirths
        request.totalneonataldeaths = totalneonataldeaths
        request.totalmaternaldeaths = totalmaternaldeaths
        request.totaldeathsnewborn = totaldeathsnewborn
        request.totaldischargedeaths = totaldischargedeaths
        request.grossdeathrate = grossdeathrate
        request.ndrnumerator = ndrdenominator
        request.ndrdenominator = ndrnumerator
        request.netdeathrate = netdeathrate
        request.reportingyear = reportingyear

        HospitalOperationsDeathsResponse response =
                (HospitalOperationsDeathsResponse) soapConnector.callWebService(dohUrl, request)
        HospitalOperationsDeathsDTO result = XMLObjectMapper(response.return, HospitalOperationsDeathsDTO.class) as HospitalOperationsDeathsDTO
        return result
    }

    @GraphQLMutation(name = "hospitalOperationsMortalityDeaths")
    HospOptMortalityDeathsDTO hospitalOperationsMortalityDeaths(
            @GraphQLArgument(name = "hfhudcode") String hfhudcode,
            @GraphQLArgument(name = "icd10desc") String icd10desc,
            @GraphQLArgument(name = "munder1") String munder1,
            @GraphQLArgument(name = "funder1") String funder1,
            @GraphQLArgument(name = "m1to4") String m1to4,
            @GraphQLArgument(name = "f1to4") String f1to4,
            @GraphQLArgument(name = "m5to9") String m5to9,
            @GraphQLArgument(name = "f5to9") String f5to9,
            @GraphQLArgument(name = "m10to14") String m10to14,
            @GraphQLArgument(name = "f10to14") String f10to14,
            @GraphQLArgument(name = "m15to19") String m15to19,
            @GraphQLArgument(name = "f15to19") String f15to19,
            @GraphQLArgument(name = "m20to24") String m20to24,
            @GraphQLArgument(name = "f20to24") String f20to24,
            @GraphQLArgument(name = "m25to29") String m25to29,
            @GraphQLArgument(name = "f25to29") String f25to29,
            @GraphQLArgument(name = "m30to34") String m30to34,
            @GraphQLArgument(name = "f30to34") String f30to34,
            @GraphQLArgument(name = "m35to39") String m35to39,
            @GraphQLArgument(name = "f35to39") String f35to39,
            @GraphQLArgument(name = "m40to44") String m40to44,
            @GraphQLArgument(name = "f40to44") String f40to44,
            @GraphQLArgument(name = "m45to49") String m45to49,
            @GraphQLArgument(name = "f45to49") String f45to49,
            @GraphQLArgument(name = "m50to54") String m50to54,
            @GraphQLArgument(name = "f50to54") String f50to54,
            @GraphQLArgument(name = "m55to59") String m55to59,
            @GraphQLArgument(name = "f55to59") String f55to59,
            @GraphQLArgument(name = "m60to64") String m60to64,
            @GraphQLArgument(name = "f60to64") String f60to64,
            @GraphQLArgument(name = "m65to69") String m65to69,
            @GraphQLArgument(name = "f65to69") String f65to69,
            @GraphQLArgument(name = "m70over") String m70over,
            @GraphQLArgument(name = "f70over") String f70over,
            @GraphQLArgument(name = "msubtotal") String msubtotal,
            @GraphQLArgument(name = "fsubtotal") String fsubtotal,
            @GraphQLArgument(name = "grandtotal") String grandtotal,
            @GraphQLArgument(name = "icd10code") String icd10code,
            @GraphQLArgument(name = "icd10category") String icd10category,
            @GraphQLArgument(name = "reportingyear") String reportingyear
    ) {
        HospitalOperationsMortalityDeaths request = new HospitalOperationsMortalityDeaths()
        request.hfhudcode = hfhudcode
        request.icd10Desc = icd10desc
        request.munder1 = munder1
        request.funder1 = funder1
        request.m1To4 = m1to4
        request.f1To4 = f1to4
        request.m5To9 = m5to9
        request.f5To9 = f5to9
        request.m10To14 = m10to14
        request.f10To14 = f10to14
        request.m15To19 = m15to19
        request.f15To19 = f15to19
        request.m20To24 = m20to24
        request.f20To24 = f20to24
        request.m25To29 = m25to29
        request.f25To29 = f25to29
        request.m30To34 = m30to34
        request.f30To34 = f30to34
        request.m35To39 = m35to39
        request.f35To39 = f35to39
        request.m40To44 = m40to44
        request.f40To44 = f40to44
        request.m45To49 = m45to49
        request.f45To49 = f45to49
        request.m50To54 = m50to54
        request.f50To54 = f50to54
        request.m55To59 = m55to59
        request.f55To59 = f55to59
        request.m60To64 = m60to64
        request.f60To64 = f60to64
        request.m65To69 = m65to69
        request.f65To69 = f65to69
        request.m70Over = m70over
        request.f70Over = f70over
        request.msubtotal = msubtotal
        request.fsubtotal = fsubtotal
        request.grandtotal = grandtotal
        request.icd10Code = icd10code
        request.icd10Category = icd10category
        request.reportingyear = reportingyear

        HospitalOperationsMortalityDeathsResponse response =
                (HospitalOperationsMortalityDeathsResponse) soapConnector.callWebService(dohUrl, request)
        HospOptMortalityDeathsDTO result = XMLObjectMapper(response.return, HospOptMortalityDeathsDTO.class) as HospOptMortalityDeathsDTO
        result.icd10desc = icd10desc
        result.icd10code = icd10code
        return result

    }

    ///

    @GraphQLMutation(name = "hospitalOperationsHAI")
    HospitalOperationsHAIDTO hospitalOperationsHAI(
            @GraphQLArgument(name = "hfhudcode") String hfhudcode,
            @GraphQLArgument(name = "numhai") String numhai,
            @GraphQLArgument(name = "numdischarges") String numdischarges,
            @GraphQLArgument(name = "infectionrate") String infectionrate,
            @GraphQLArgument(name = "patientnumvap") String patientnumvap,
            @GraphQLArgument(name = "totalventilatordays") String totalventilatordays,
            @GraphQLArgument(name = "resultvap") String resultvap,
            @GraphQLArgument(name = "patientnumbsi") String patientnumbsi,
            @GraphQLArgument(name = "totalnumcentralline") String totalnumcentralline,
            @GraphQLArgument(name = "resultbsi") String resultbsi,
            @GraphQLArgument(name = "patientnumuti") String patientnumuti,
            @GraphQLArgument(name = "totalcatheterdays") String totalcatheterdays,
            @GraphQLArgument(name = "resultuti") String resultuti,
            @GraphQLArgument(name = "numssi") String numssi,
            @GraphQLArgument(name = "totalproceduresdone") String totalproceduresdone,
            @GraphQLArgument(name = "resultssi") String resultssi,
            @GraphQLArgument(name = "reportingyear") String reportingyear
    ) {
        HospitalOperationsHAI request = new HospitalOperationsHAI()
        request.hfhudcode = hfhudcode
        request.numhai = numhai
        request.numdischarges = numdischarges
        request.infectionrate = infectionrate
        request.patientnumvap = patientnumvap
        request.totalventilatordays = totalventilatordays
        request.resultvap = resultvap
        request.patientnumbsi = patientnumbsi
        request.totalnumcentralline = totalnumcentralline
        request.resultbsi = resultbsi
        request.patientnumuti = patientnumuti
        request.totalcatheterdays = totalcatheterdays
        request.resultuti = resultuti
        request.numssi = numssi
        request.totalproceduresdone = totalproceduresdone
        request.resultssi = resultssi
        request.reportingyear = reportingyear

        HospitalOperationsHAIResponse response =
                (HospitalOperationsHAIResponse) soapConnector.callWebService(dohUrl, request)
        HospitalOperationsHAIDTO result = XMLObjectMapper(response.return, HospitalOperationsHAIDTO.class) as HospitalOperationsHAIDTO
        return result
    }

    ///


    @GraphQLMutation(name = "hospitalOperationsMajorOpt")
    HospitalOperationsMajorOptDTO hospitalOperationsMajorOpt(
            @GraphQLArgument(name = "hfhudcode") String hfhudcode,
            @GraphQLArgument(name = "operationcode") String operationcode,
            @GraphQLArgument(name = "surgicaloperation") String surgicaloperation,
            @GraphQLArgument(name = "number") String number,
            @GraphQLArgument(name = "reportingyear") String reportingyear
    ) {
        HospitalOperationsMajorOpt request = new HospitalOperationsMajorOpt()
        request.hfhudcode = hfhudcode
        request.operationcode = operationcode
        request.surgicaloperation = surgicaloperation
        request.number = number
        request.reportingyear = reportingyear

        HospitalOperationsMajorOptResponse response =
                (HospitalOperationsMajorOptResponse) soapConnector.callWebService(dohUrl, request)
        HospitalOperationsMajorOptDTO result = XMLObjectMapper(response.return, HospitalOperationsMajorOptDTO.class) as HospitalOperationsMajorOptDTO
        return result
    }


    ///

    @GraphQLMutation(name = "hospitalOperationsMinorOpt")
    HospitalOperationsMinorOptDTO hospitalOperationsMinorOpt(
            @GraphQLArgument(name = "hfhudcode") String hfhudcode,
            @GraphQLArgument(name = "operationcode") String operationcode,
            @GraphQLArgument(name = "surgicaloperation") String surgicaloperation,
            @GraphQLArgument(name = "number") String number,
            @GraphQLArgument(name = "reportingyear") String reportingyear
    ) {
        HospitalOperationsMinorOpt request = new HospitalOperationsMinorOpt()
        request.hfhudcode = hfhudcode
        request.operationcode = operationcode
        request.surgicaloperation = surgicaloperation
        request.number = number
        request.reportingyear = reportingyear

        HospitalOperationsMinorOptResponse response =
                (HospitalOperationsMinorOptResponse) soapConnector.callWebService(dohUrl, request)
        HospitalOperationsMinorOptDTO result = XMLObjectMapper(response.return, HospitalOperationsMinorOptDTO.class) as HospitalOperationsMinorOptDTO
        return result
    }

//    @GraphQLMutation(name = "staffingPattern")
//    GraphQLRetVal<String> staffingPattern(
//            @GraphQLArgument(name = "staffingPattern") List<Map<String, Object>> staffingPattern,
//            @GraphQLArgument(name = "staffingPatternOthers") List<Map<String, Object>> staffingPatternOthers,
//            @GraphQLArgument(name = "reportingyear") String reportingyear
//    ) {
//        def responseList = new ArrayList<String>()
//        staffingPattern.each {
//            StaffingPattern request = objectMapper.convertValue(it, StaffingPattern)
//            request.hfhudcode = hospitalConfigService.hospitalInfo.hfhudcode ?: 'DOH000000000000000'
//            request.reportingyear = reportingyear
//            request.fulltime40Permanent = it.get("fulltime40permanent") as String
//            request.fulltime40Contractual = it.get("fulltime40contractual") as String
//            StaffingPatternResponse response =
//                    (StaffingPatternResponse) soapConnector.callWebService(dohUrl, request)
//            responseList.add(response.return)
//        }
//
//        staffingPatternOthers.each {
//            StaffingPatternOthers request = objectMapper.convertValue(it, StaffingPatternOthers)
//            request.hfhudcode = hospitalConfigService.hospitalInfo.hfhudcode ?: 'DOH000000000000000'
//            request.reportingyear = reportingyear
//            request.fulltime40Permanent = it.get("fulltime40permanent") as String
//            request.fulltime40Contractual = it.get("fulltime40contractual") as String
//            request.parent = it.get("professiondesignation") as String
//            request.professiondesignation = it.get("positiondesc") as String
//            StaffingPatternOthersResponse response =
//                    (StaffingPatternOthersResponse) soapConnector.callWebService(dohUrl, request)
//            responseList.add(response.return)
//        }
//        return new GraphQLRetVal<String>(responseList, true, 'Success')
//    }

    @GraphQLMutation(name = "staffingPattern")
    StaffingPatternDTO staffingPattern(
            @GraphQLArgument(name = "hfhudcode") String hfhudcode,
            @GraphQLArgument(name = "professiondesignation") String professiondesignation,
            @GraphQLArgument(name = "specialtyboardcertified") String specialtyboardcertified,
            @GraphQLArgument(name = "fulltime40permanent") String fulltime40permanent,
            @GraphQLArgument(name = "fulltime40contractual") String fulltime40contractual,
            @GraphQLArgument(name = "parttimepermanent") String parttimepermanent,
            @GraphQLArgument(name = "parttimecontractual") String parttimecontractual,
            @GraphQLArgument(name = "activerotatingaffiliate") String activerotatingaffiliate,
            @GraphQLArgument(name = "outsourced") String outsourced,
            @GraphQLArgument(name = "reportingyear") String reportingyear
    ) {
        StaffingPattern request = new StaffingPattern()
        request.hfhudcode = hfhudcode
        request.professiondesignation = professiondesignation
        request.specialtyboardcertified = specialtyboardcertified == '0' ? '00' : specialtyboardcertified
        request.fulltime40Permanent = fulltime40permanent == '0' ? '00' : fulltime40permanent
        request.fulltime40Contractual = fulltime40contractual == '0' ? '00' : fulltime40contractual
        request.parttimepermanent = parttimepermanent == '0' ? '00' : parttimepermanent
        request.parttimecontractual = parttimecontractual == '0' ? '00' : parttimecontractual
        request.activerotatingaffiliate = activerotatingaffiliate == '0' ? '00' : activerotatingaffiliate
        request.outsourced = outsourced == '0' ? '00' : outsourced
        request.reportingyear = reportingyear

        StaffingPatternResponse response =
                (StaffingPatternResponse) soapConnector.callWebService(dohUrl, request)
        StaffingPatternDTO result = XMLObjectMapper(response.return, StaffingPatternDTO.class) as StaffingPatternDTO

        return result
    }

    @GraphQLMutation(name = "staffingPatternOthers")
    StaffingPatternDTO staffingPatternOthers(
            @GraphQLArgument(name = "hfhudcode") String hfhudcode,
            @GraphQLArgument(name = "parent") String parent,
            @GraphQLArgument(name = "professiondesignation") String professiondesignation,
            @GraphQLArgument(name = "specialtyboardcertified") String specialtyboardcertified,
            @GraphQLArgument(name = "fulltime40permanent") String fulltime40permanent,
            @GraphQLArgument(name = "fulltime40contractual") String fulltime40contractual,
            @GraphQLArgument(name = "parttimepermanent") String parttimepermanent,
            @GraphQLArgument(name = "parttimecontractual") String parttimecontractual,
            @GraphQLArgument(name = "activerotatingaffiliate") String activerotatingaffiliate,
            @GraphQLArgument(name = "outsourced") String outsourced,
            @GraphQLArgument(name = "reportingyear") String reportingyear
    ) {
//        StaffingPatternOthers request = new StaffingPatternOthers()
//        request.hfhudcode = hfhudcode
//        request.parent = parent
//        request.professiondesignation = professiondesignation ?: '0'
//        request.specialtyboardcertified = specialtyboardcertified ?: '0'
//        request.fulltime40Permanent = fulltime40permanent ?: '0'
//        request.fulltime40Contractual = fulltime40contractual ?: '0'
//        request.parttimepermanent = parttimepermanent ?: '0'
//        request.parttimecontractual = parttimecontractual ?: '0'
//        request.activerotatingaffiliate = activerotatingaffiliate ?: '0'
//        request.outsourced = outsourced ?: '0'
//        request.reportingyear = reportingyear

//        StaffingPatternOthers request = new StaffingPatternOthers()
        StaffingPatternOthers request = new StaffingPatternOthers()
        request.hfhudcode = hfhudcode
        request.parent = parent
        request.professiondesignation = professiondesignation
        request.specialtyboardcertified = specialtyboardcertified == '0' ? '00' : specialtyboardcertified
        request.fulltime40Permanent = fulltime40permanent == '0' ? '00' : fulltime40permanent
        request.fulltime40Contractual = fulltime40contractual == '0' ? '00' : fulltime40contractual
        request.parttimepermanent = parttimepermanent == '0' ? '00' : parttimepermanent
        request.parttimecontractual = parttimecontractual == '0' ? '00' : parttimecontractual
        request.activerotatingaffiliate = activerotatingaffiliate == '0' ? '00' : activerotatingaffiliate
        request.outsourced = outsourced == '0' ? '00' : outsourced
        request.reportingyear = reportingyear

        StaffingPatternOthersResponse response =
                (StaffingPatternOthersResponse) soapConnector.callWebService(dohUrl, request)
        StaffingPatternDTO result = XMLObjectMapper(response.return, StaffingPatternDTO.class) as StaffingPatternDTO

        return result
    }


    @GraphQLMutation(name = "expenses")
    GraphQLRetVal<ExpensesDTO> expenses(
//    GraphQLRetVal<String> expenses(
@GraphQLArgument(name = "fields") Map<String, Object> fields
    ) {
        Expenses request = objectMapper.convertValue(fields, Expenses)
        request.hfhudcode = hospitalConfigService.hospitalInfo.hfhudcode ?: ''
        ExpensesResponse response =
                (ExpensesResponse) soapConnector.callWebService(dohUrl, request)
        ExpensesDTO result = XMLObjectMapper(response.return, ExpensesDTO.class) as ExpensesDTO


        if (response) {
            DohLogs dohLogs = new DohLogs()
            dohLogs.type = DOH_REPORT_TYPE.EXPENSES.name()
            dohLogs.submittedReport = new JSONObject(fields)
            dohLogs.reportResponse = new Gson().toJson(result)
            dohLogs.reportingYear = fields['reportingyear'] as Integer
            dohLogs.status = result.response_desc
            dohLogsServices.save(dohLogs)
        }
        return new GraphQLRetVal<ExpensesDTO>(result, true, 'Success')
//        return new GraphQLRetVal<String>(response.return, true, 'Success')

    }


    @GraphQLMutation(name = "revenues")
    GraphQLRetVal<RevenuesDTO> revenues(
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    ) {
        Revenues request = objectMapper.convertValue(fields, Revenues)
        request.hfhudcode = hospitalConfigService.hospitalInfo.hfhudcode ?: ''
        RevenuesResponse response =
                (RevenuesResponse) soapConnector.callWebService(dohUrl, request)
        RevenuesDTO result = XMLObjectMapper(response.return, RevenuesDTO.class) as RevenuesDTO

        if (response) {
            DohLogs dohLogs = new DohLogs()
            dohLogs.type = DOH_REPORT_TYPE.REVENUES.name()
            dohLogs.submittedReport = new JSONObject(fields)
            dohLogs.reportResponse = new Gson().toJson(result)
            dohLogs.reportingYear = fields['reportingyear'] as Integer
            dohLogs.status = result.response_desc
            dohLogsServices.save(dohLogs)
        }
        return new GraphQLRetVal<RevenuesDTO>(result, true, 'Success')
//        return new GraphQLRetVal<String>(response.return, true, 'Success')
    }

    @GraphQLMutation(name = "submittedReports")
    GraphQLRetVal<String> submittedReports(
            @GraphQLArgument(name = "reportingstatus") String reportingstatus,
            @GraphQLArgument(name = "reportedby") String reportedby,
            @GraphQLArgument(name = "designation") String designation,
            @GraphQLArgument(name = "section") String section,
            @GraphQLArgument(name = "department") String department,
            @GraphQLArgument(name = "reportingyear") String reportingyear,
            @GraphQLArgument(name = "hfhudcode") String hfhudcode = ''
    ) {

        SubmittedReports request = new SubmittedReports()
        request.hfhudcode = hfhudcode ?: hospitalConfigService.hospitalInfo.hfhudcode
        request.reportingstatus = reportingstatus
        request.reportedby = reportedby
        request.designation = designation
        request.section = section
        request.reportingyear = reportingyear
        request.department = department
        request.datereported = LocalDateTime.now()

        SubmittedReportsResponse response =
                (SubmittedReportsResponse) soapConnector.callWebService(dohUrl, request)
        return new GraphQLRetVal<String>(response.return, true, 'Success')
    }

    @GraphQLMutation(name = "createNEHEHRSVaccount")
    GraphQLRetVal<String> createNEHEHRSVaccount(
            @GraphQLArgument(name = "hfhudcode") String hfhudcode,
            @GraphQLArgument(name = "hfhudname") String hfhudname,
            @GraphQLArgument(name = "fhudaddress") String fhudaddress,
            @GraphQLArgument(name = "regcode") String regcode,
            @GraphQLArgument(name = "provcode") String provcode,
            @GraphQLArgument(name = "ctymuncode") String ctymuncode,
            @GraphQLArgument(name = "bgycode") String bgycode,
            @GraphQLArgument(name = "fhudtelno1") String fhudtelno1,
            @GraphQLArgument(name = "fhudtelno2") String fhudtelno2,
            @GraphQLArgument(name = "fhudfaxno") String fhudfaxno,
            @GraphQLArgument(name = "fhudemail") String fhudemail,
            @GraphQLArgument(name = "headlname") String headlname,
            @GraphQLArgument(name = "headfname") String headfname,
            @GraphQLArgument(name = "headmname") String headmname,
            @GraphQLArgument(name = "accessKey") String accessKey
    ) {
        CreateNEHEHRSVaccount request = new CreateNEHEHRSVaccount()
        request.hfhudcode = hfhudcode
        request.hfhudname = hfhudname
        request.fhudaddress = fhudaddress
        request.regcode = regcode
        request.provcode = provcode
        request.ctymuncode = ctymuncode
        request.bgycode = bgycode
        request.fhudtelno1 = fhudtelno1
        request.fhudtelno2 = fhudtelno2
        request.fhudfaxno = fhudfaxno
        request.fhudemail = fhudemail
        request.headlname = headlname
        request.headfname = headfname
        request.headmname = headmname
        request.accessKey = accessKey

        CreateNEHEHRSVaccountResponse response =
                (CreateNEHEHRSVaccountResponse) soapConnector.callWebService(dohUrl, request)
        return new GraphQLRetVal<String>(response.return, true, 'Success')
    }

}