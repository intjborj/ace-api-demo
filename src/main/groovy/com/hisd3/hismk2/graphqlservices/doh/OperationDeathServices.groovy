package com.hisd3.hismk2.graphqlservices.doh

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.hisd3.hismk2.domain.doh.DOH_REPORT_TYPE
import com.hisd3.hismk2.domain.doh.DohLogs
import com.hisd3.hismk2.domain.doh.OperationDeaths
import com.hisd3.hismk2.domain.doh.OperationHai
import com.hisd3.hismk2.graphqlservices.doh.dto.ErdConsultation2Dto
import com.hisd3.hismk2.graphqlservices.doh.dto.HospOptDischargesERDTO
import com.hisd3.hismk2.graphqlservices.doh.dto.HospOptMortalityDeathsDTO
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.doh.OperationDeathsRepository
import com.hisd3.hismk2.utils.SOAPConnector
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesNumberDeliveriesResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsDeaths
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsDeathsResponse

import java.time.Instant

@Component
@GraphQLApi
class OperationDeathServices {

    @Autowired
    OperationDeathsRepository operationDeathsRepository

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    DohReportService dohReportService

    @Autowired
    DohAPIService dohAPIService

    @Autowired
    DohLogsServices dohLogsServices

    @Autowired
    HospitalConfigService hospitalConfigService

    @GraphQLQuery(name = "findAllOperationDeaths", description = "Find all classification")
    List<OperationDeaths> findAllOperationDeaths() {
        return operationDeathsRepository.findAllOperationDeaths()
    }

    //==================================Mutation ============
    @GraphQLMutation
    def postOperationDeaths(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    ) {
        if (id) {
            def operationDeaths = operationDeathsRepository.findById(id).get()
            objectMapper.updateValue(operationDeaths, fields)
            operationDeaths.submittedDateTime = Instant.now()

            return operationDeathsRepository.save(operationDeaths)
        } else {

            def operationDeaths = objectMapper.convertValue(fields, OperationDeaths)

            return operationDeathsRepository.save(operationDeaths)
        }
    }

    @GraphQLMutation(name = "submitDeathMortalities")
    GraphQLRetVal<List<HospOptMortalityDeathsDTO>> submitDeathMortalities(@GraphQLArgument(name = "year") Integer year) {

        List<HospOptMortalityDeathsDTO> hospOptMortalityDeathsDTOS = []
        List<Map<String, Object>> top10MortalityCases = dohReportService.top10MortalityCasesV2(year)

        top10MortalityCases.each {
            HospOptMortalityDeathsDTO response = dohAPIService.hospitalOperationsMortalityDeaths(
                   hospitalConfigService.hospitalInfo.hfhudcode ?: '',
                    it['icd10desc'].toString(),
                    it['munder1'].toString(),
                    it['funder1'].toString(),
                    it['m1to4'].toString(),
                    it['f1to4'].toString(),
                    it['m5to9'].toString(),
                    it['f5to9'].toString(),
                    it['m10to14'].toString(),
                    it['f10to14'].toString(),
                    it['m15to19'].toString(),
                    it['f15to19'].toString(),
                    it['m20to24'].toString(),
                    it['f20to24'].toString(),
                    it['m25to29'].toString(),
                    it['f25to29'].toString(),
                    it['m30to34'].toString(),
                    it['f30to34'].toString(),
                    it['m35to39'].toString(),
                    it['f35to39'].toString(),
                    it['m40to44'].toString(),
                    it['f40to44'].toString(),
                    it['m45to49'].toString(),
                    it['f45to49'].toString(),
                    it['m50to54'].toString(),
                    it['f50to54'].toString(),
                    it['m55to59'].toString(),
                    it['f55to59'].toString(),
                    it['m60to64'].toString(),
                    it['f60to64'].toString(),
                    it['m65to69'].toString(),
                    it['f65to69'].toString(),
                    it['m70over'].toString(),
                    it['f70over'].toString(),
                    it['msubtotal'].toString(),
                    it['fsubtotal'].toString(),
                    it['grandtotal'].toString(),
                    it['icd10code'].toString(),
                    it['icd10category'].toString(),
                    year.toString()
            )

            hospOptMortalityDeathsDTOS.push(response)
            if (top10MortalityCases) {
                DohLogs dohLogs = new DohLogs()
                dohLogs.type = DOH_REPORT_TYPE.DISCHARGES_MORTALITY.name()
                dohLogs.submittedReport = new JSONObject(it)
                dohLogs.reportResponse = new Gson().toJson(response)
                dohLogs.reportingYear = year
                dohLogs.status = response.response_desc
                dohLogsServices.save(dohLogs)
            }
        }

        return new GraphQLRetVal<List<HospOptMortalityDeathsDTO>>(hospOptMortalityDeathsDTOS,true, '')


    }

}
