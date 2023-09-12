package com.hisd3.hismk2.graphqlservices.doh

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.doh.BedCapacity
import com.hisd3.hismk2.domain.doh.HospOptDischargeSpecialty
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.doh.DischargeSpecialtyRepository
import com.hisd3.hismk2.utils.SOAPConnector
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesSpecialty
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesSpecialtyResponse

import java.time.Instant


@Component
@GraphQLApi
class DischargeSpecialtyServices {

    @Autowired
    SOAPConnector soapConnector

    @Autowired
    DischargeSpecialtyRepository dischargeSpecialtyRepository

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    HospitalConfigService hospitalConfigService

    @Autowired
    JdbcTemplate jdbcTemplate

    @GraphQLQuery(name = "findAllDischargeSpecialty", description = "Find all Discharge Specialty")
    List<HospOptDischargeSpecialty> findAllDischargeSpecialty() {
        return dischargeSpecialtyRepository.findAllDischargeSpecialty()
    }

    //==================================Mutation ============
    @GraphQLMutation
    def postDischargeSpecialty(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    ) {
        if (id) {
            def dischargeSpecialty = dischargeSpecialtyRepository.findById(id).get()
            objectMapper.updateValue(dischargeSpecialty, fields)
            dischargeSpecialty.submittedDateTime = Instant.now()

            return dischargeSpecialtyRepository.save(dischargeSpecialty)
        } else {

            def dischargeSpecialty = objectMapper.convertValue(fields, HospOptDischargeSpecialty)

            return dischargeSpecialtyRepository.save(dischargeSpecialty)
        }
    }
    @GraphQLMutation(name = "sendDischargeSpecialty")
    GraphQLRetVal<String> sendDischargeSpecialty(@GraphQLArgument(name = "fields") Map<String, Object> fields){

        try {
            HospOptDischargesSpecialty request = new HospOptDischargesSpecialty()
            request.hfhudcode = hospitalConfigService.hospitalInfo.hfhudcode?:""
            request.typeofservice = fields.get("typeOfService") as Integer
            request.nopatients = fields.get("noPatient") as Integer
            request.totallengthstay = fields.get("totalLengthStay") as Integer
            request.nppay = fields.get("nonPhilHealthStay") as Integer
            request.nphservicecharity = fields.get("nhpServiceCharity") as Integer
            request.nphtotal = fields.get("totalNonPhilHealth") as Integer
            request.phpay = fields.get("philHealthPay") as Integer
            request.phservice = fields.get("philHealthService") as Integer
            request.phtotal = fields.get("totalPhilHealth") as Integer
            request.hmo = fields.get("hmo") as Integer
            request.owwa = fields.get("owwa") as Integer
            request.recoveredimproved = fields.get("recoveredImproved") as Integer
            request.transferred = fields.get("transferred") as Integer
            request.hama = fields.get("hama") as Integer
            request.absconded = fields.get("absconded") as Integer
            request.unimproved = fields.get("unImproved") as Integer
            request.deathsbelow48 = fields.get("deathsBelow48Hours") as Integer
            request.deathsover48 = fields.get("deathsOver48") as Integer
            request.totaldeaths = fields.get("totalDeaths") as Integer
            request.totaldischarges = fields.get("totalDischarge") as Integer
            request.remarks = fields.get("remarks") as String
            request.reportingyear = fields.get("reportingYear") as Integer

            HospOptDischargesSpecialtyResponse response =
                    (HospOptDischargesSpecialtyResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/hospOptDischargesSpecialty", request)
            return new GraphQLRetVal<String>(response.return, true)
        } catch (Exception e) {
            return new GraphQLRetVal<String>(e.message, false)
        }
    }

    @GraphQLQuery(name = "getDischargeSpecialty")
    List<Map<String, Object>> getDischargeSpecialty(@GraphQLArgument(name = 'year') Integer year){
        return jdbcTemplate.queryForList("""
               select 
upper(s.tsdesc) as "serviceType", 
                s.tscode as "serviceCode", 
                count(c.*) as patients,
                coalesce(sum(to_char((discharged_datetime + INTERVAL '8h'), 'YYYY-MM-DD')::date - (admission_datetime + INTERVAL '8h')::date),0) as "totalDays",
                count(c.*) filter(where (accommodation_type = 'SELF' and (p.philhealth_id is null or trim(p.philhealth_id) = '') and (r.is_ward is null or r.is_ward is false))) as "nonPhicPay",
                count(c.*) filter(where (accommodation_type = 'SERVICE CHARITY' and (p.philhealth_id is null or trim(p.philhealth_id) = '') and r.is_ward is true)) as "nonPhicSerivceCharity",
                count(c.*) filter(where (accommodation_type = 'SERVICE CHARITY' and (p.philhealth_id is null or trim(p.philhealth_id) = '') and r.is_ward is true) or (accommodation_type = 'SELF' and (p.philhealth_id is null or trim(p.philhealth_id) = '') and (r.is_ward is null or r.is_ward is false))) as "nonPhicTotal",
                count(c.*) filter(where accommodation_type = 'SELF' and (p.philhealth_id is not null and trim(p.philhealth_id) != '') and (r.is_ward is null or r.is_ward is false)) as "phicPay",
                count(c.*) filter(where accommodation_type = 'SERVICE CHARITY' and (p.philhealth_id is not null and trim(p.philhealth_id) != '') and r.is_ward is true) as "phicSerivceCharity",
                count(c.*) filter(where (accommodation_type = 'SERVICE CHARITY' and (p.philhealth_id is not null and trim(p.philhealth_id) != '') and r.is_ward is true) or (accommodation_type = 'SELF' and (p.philhealth_id is not null and trim(p.philhealth_id) != '') and (r.is_ward is null or r.is_ward is false)) ) as "phicTotal",
                count(c.*) filter(where c.accommodation_type = 'HMO') as hmo,
                count(c.*) filter(where c.accommodation_type = 'OWWA' ) as owwa,
                count(c.*) filter(where (discharge_condition = 'IMPROVED' or discharge_condition = 'RECOVERED') and c.discharge_disposition = 'DISCHARGED')  as ri,
                count(c.*) filter (where discharge_disposition = 'TRANSFERRED' and c.discharge_condition != 'EXPIRED') as tranferred,
                count(c.*) filter (where discharge_disposition = 'DAMA/HAMA' and c.discharge_condition != 'EXPIRED') as hama,
                count(c.*) filter (where discharge_disposition = 'ABSCONDED' and c.discharge_condition != 'EXPIRED') as absconded,
                count(c.*) filter (where discharge_condition = 'UNIMPROVED' and c.discharge_disposition = 'DISCHARGED') as unimproved,
                count(c.*) filter (where pms.double_get_hours(coalesce(admission_datetime, entry_datetime) + '8:00:00'::interval, time_of_death  + '8:00:00'::interval ) >= 48 
                and c.discharge_condition = 'EXPIRED' and time_of_death is not null 
                ) as greater_than_48hrs,
                count(c.*) filter (where pms.double_get_hours(coalesce(admission_datetime, entry_datetime) + '8:00:00'::interval, time_of_death  + '8:00:00'::interval ) < 48 
                and c.discharge_condition = 'EXPIRED' and time_of_death is not null 
                ) as less_than_48hrs,
                count(c.*) filter (where c.discharge_condition = 'EXPIRED' and time_of_death is not null) as total_deaths,
                count(c.*) as total_discharges
                from referential.doh_service_types s 
                left join pms.cases c 
                on upper(s.tsdesc) = upper(c.service_type)
                and date_part('year', discharged_datetime + INTERVAL '8h') = ${year} 
                and (discharge_disposition in ('DAMA/HAMA','TRANSFERRED','ABSCONDED','DISCHARGED')) 
                and c.registry_type in ('IPD')
                and c.may_go_home_datetime is not null
                left join pms.patients p on p.id  = c.patient 
                left join bms.rooms r on r.id = c.room 
--                ,
--                LATERAL json_array_elements(
--                CASE 
--                    WHEN pms.is_json(c.doh_icd_diagnosis ::character varying) AND c.doh_icd_diagnosis IS NOT NULL THEN c.doh_icd_diagnosis::json
--                ELSE NULL::json
--                    END) d(value) 
                    --  s.tscode != 7 and 
                group by upper(s.tsdesc), s.tscode
                order by s.tscode asc;
            """)
    }
}
