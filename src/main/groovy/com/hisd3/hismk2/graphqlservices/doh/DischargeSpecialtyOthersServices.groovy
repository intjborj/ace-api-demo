package com.hisd3.hismk2.graphqlservices.doh

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.doh.BedCapacity
import com.hisd3.hismk2.domain.doh.HospOptDischargeSpecialtyOthers
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.doh.DischargeSpecialtyOthersRepository
import com.hisd3.hismk2.utils.SOAPConnector
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesSpecialtyOthers
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesSpecialtyOthersResponse

import java.time.Instant


@Component
@GraphQLApi
class DischargeSpecialtyOthersServices {

    @Autowired
    SOAPConnector soapConnector

    @Autowired
    DischargeSpecialtyOthersRepository dischargeSpecialtyOthersRepository

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    HospitalConfigService hospitalConfigService

    @GraphQLQuery(name = "findAllDischargeSpecialtyOthers", description = "Find all discharges Specialty others")
    List<HospOptDischargeSpecialtyOthers> findAllDischargeSpecialtyOthers() {
        return dischargeSpecialtyOthersRepository.findAllDischargeSpecialtyOthers()
    }

    @GraphQLMutation
    def postDischargeSpecialtyOthers(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    ) {
        if (id) {
            def dischargeSpecialtyOthers = dischargeSpecialtyOthersRepository.findById(id).get()
            objectMapper.updateValue(dischargeSpecialtyOthers, fields)
            dischargeSpecialtyOthers.submittedDateTime = Instant.now()

            return dischargeSpecialtyOthersRepository.save(dischargeSpecialtyOthers)
        } else {

            def dischargeSpecialtyOthers = objectMapper.convertValue(fields, HospOptDischargeSpecialtyOthers)

            return dischargeSpecialtyOthersRepository.save(dischargeSpecialtyOthers)
        }
    }
    @GraphQLMutation(name = "sendSpecialtyOthers")
    GraphQLRetVal<String> sendSpecialtyOthers(@GraphQLArgument(name = "fields") Map<String, Object> fields){

        try {
            HospOptDischargesSpecialtyOthers request = new HospOptDischargesSpecialtyOthers()
            request.hfhudcode = hospitalConfigService.hospitalInfo.hfhudcode?:""
            request.othertypeofservicespecify = fields.get("otherTypeServicesSpecify") as String
            request.nopatients = fields.get("noPatients") as Integer
            request.totallengthstay = fields.get("totalLengthStay") as Integer
            request.nppay = fields.get("nonPhilHealthPay") as Integer
            request.nphservicecharity = fields.get("nphServiceCharity") as Integer
            request.nphtotal = fields.get("totalNonPhilHealth") as Integer
            request.phpay = fields.get("philHealthPay") as Integer
            request.phservice = fields.get("philHealthServices") as Integer
            request.phtotal = fields.get("totalPhilHealth") as Integer
            request.hmo = fields.get("hmo") as Integer
            request.owwa = fields.get("owwa") as Integer
            request.recoveredimproved = fields.get("recoveredImproved")  as Integer
            request.transferred = fields.get("transferred") as Integer
            request.hama = fields.get("hama") as Integer
            request.absconded = fields.get("absconded") as Integer
            request.unimproved = fields.get("unImproved") as Integer
            request.deathsbelow48 = fields.get("deathBelow48Hours") as Integer
            request.deathsover48 = fields.get("deathOver48") as Integer
            request.totaldeaths = fields.get("totalDeaths") as Integer
            request.totaldischarges = fields.get("totalDischarge") as Integer
            request.remarks = fields.get("remarks") as String
            request.reportingyear = fields.get("reportingYear") as Integer

            HospOptDischargesSpecialtyOthersResponse response =
                    (HospOptDischargesSpecialtyOthersResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/hospOptDischargesSpecialtyOthers", request)
            return new GraphQLRetVal<String>(response.return, true)
        } catch (Exception e) {
            return new GraphQLRetVal<String>(e.message, false)
        }
    }
}
