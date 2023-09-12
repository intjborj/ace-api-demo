package com.hisd3.hismk2.graphqlservices.doh

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.doh.StaffingPattern
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.doh.StaffingPatternRepository
import com.hisd3.hismk2.rest.dto.PatientBasicDto
import com.hisd3.hismk2.utils.SOAPConnector
import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import ph.gov.doh.uhmistrn.ahsr.webservice.index.RevenuesResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.StaffingPatternResponse

import java.time.Instant


@Canonical
class PositionCountDTO {
//    String hfhudcode
//    String parent
    String positiondesc
    String professiondesignation
    Integer specialtyboardcertified = 0
    Integer fulltime40permanent= 0
    Integer fulltime40contractual= 0
    Integer parttimepermanent= 0
    Integer parttimecontractual= 0
    Integer activerotatingaffiliate= 0
    Integer outsourced= 0
    String reportingyear
    Boolean isothers
}

@Component
@GraphQLApi
class StaffingPatternServices {

    @Autowired
    StaffingPatternRepository staffingPatternRepository

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    JdbcTemplate jdbcTemplate

    @Autowired
    SOAPConnector soapConnector

    @Autowired
    HospitalConfigService hospitalConfigService

    @GraphQLQuery(name = "findAllStaffingPattern", description = "Find all Staffing Pattern")
    List<StaffingPattern> findAllStaffingPattern() {
        return staffingPatternRepository.findAllStaffingPattern()
    }

//
//    @GraphQLQuery(name = "countEmpByPositionV2")
//    List<PositionCountDTO> countEmpByPositionV2() {
//
//        List<PositionCountDTO> positionList = new ArrayList<PositionCountDTO>()
//
//        List<PositionCountDTO> allPositions = jdbcTemplate.query("""
//		Select
//p.poscode as professiondesignation,
//p.postdesc as positiondesc,
//
//
//count(e.*) filter(where e.employee_type = 'PERMANENT - FULLTIME') as fulltime40Permanent,
//count(e.*) filter(where e.employee_type = 'CONTRACTUAL - FULLTIME') as fulltime40Contractual,
//count(e.*) filter(where e.employee_type = 'PERMANENT - PARTTIME') as parttimepermanent ,
//count(e.*) filter(where e.employee_type = 'CONTRACTUAL - PARTTIME') as parttimecontractual ,
//count(e.*) filter(where e.employee_type = 'ACTIVE ROTATING') as activerotatingaffiliate ,
//count(e.*) filter(where e.employee_type = 'OUTSOURCED') as outsourced,
//count(e.*) filter(where e.is_specialty_board_certified = true) as specialtyboardcertified
//from referential.doh_positions p
//
//left join  hrm.employees e
//on e.position_code = p.poscode
//and e.is_active = true
//
//where  p.poscode not in (1,2,17,42, 16)
//
//GROUP by p.poscode, p.id
//
//order by p.poscode
//;
//
//        """, new BeanPropertyRowMapper(PositionCountDTO.class))
//
//        List<PositionCountDTO> countParentPositions = jdbcTemplate.query("""
//select
//pos.poscode as professiondesignation,
//pos.postdesc as positiondesc,
//sum (sub.fulltime40Permanent ) as fulltime40Permanent,
//sum (sub.fulltime40Contractual ) as fulltime40Contractual,
//sum (sub.parttimepermanent ) as parttimepermanent ,
//sum (sub.parttimecontractual ) as parttimecontractual ,
//sum (sub.activerotatingaffiliate ) as activerotatingaffiliate ,
//sum (sub.outsourced ) as outsourced,
//sum (sub.is_specialty_board_certified ) as specialtyboardcertified
//
//from(
//Select
//p.* ,
//
//count(e.*) filter(where e.employee_type = 'PERMANENT - FULLTIME') as fulltime40Permanent,
//count(e.*) filter(where e.employee_type = 'CONTRACTUAL - FULLTIME') as fulltime40Contractual,
//count(e.*) filter(where e.employee_type = 'PERMANENT - PARTTIME') as parttimepermanent ,
//count(e.*) filter(where e.employee_type = 'CONTRACTUAL - PARTTIME') as parttimecontractual ,
//count(e.*) filter(where e.employee_type = 'ACTIVE ROTATING') as activerotatingaffiliate ,
//count(e.*) filter(where e.employee_type = 'OUTSOURCED') as outsourced,
//count(e.*) filter(where e.is_specialty_board_certified = true) as is_specialty_board_certified
//from referential.doh_positions p
//
//left join  hrm.employees e
//on e.position_code = p.poscode
//and e.is_active = true
//
//
//
//where p.poscode_parent in (2,17, 16, 42)
//
//
//GROUP by  p.poscode_parent, p.id
//
//order by p.poscode
//
//) as sub
//
//left join referential.doh_positions pos on pos.poscode = sub.poscode_parent
//group by pos.poscode,pos.postdesc
//;
//        """, new BeanPropertyRowMapper(PositionCountDTO.class))
//
//
//        List<PositionCountDTO> consultants = jdbcTemplate.query("""
//select
//'1' as professiondesignation,
//'Consultants' as positiondesc,
//sum (sub.fulltime40Permanent ) as fulltime40Permanent,
//sum (sub.fulltime40Contractual ) as fulltime40Contractual,
//sum (sub.parttimepermanent ) as parttimepermanent ,
//sum (sub.parttimecontractual ) as parttimecontractual ,
//sum (sub.activerotatingaffiliate ) as activerotatingaffiliate ,
//sum (sub.outsourced ) as outsourced,
//sum (sub.is_specialty_board_certified ) as specialtyboardcertified
//
//from(
//Select
//p.* ,
//
//count(e.*) filter(where e.employee_type = 'PERMANENT - FULLTIME') as fulltime40Permanent,
//count(e.*) filter(where e.employee_type = 'CONTRACTUAL - FULLTIME') as fulltime40Contractual,
//count(e.*) filter(where e.employee_type = 'PERMANENT - PARTTIME') as parttimepermanent ,
//count(e.*) filter(where e.employee_type = 'CONTRACTUAL - PARTTIME') as parttimecontractual ,
//count(e.*) filter(where e.employee_type = 'ACTIVE ROTATING') as activerotatingaffiliate ,
//count(e.*) filter(where e.employee_type = 'OUTSOURCED') as outsourced,
//count(e.*) filter(where e.is_specialty_board_certified = true) as is_specialty_board_certified
//from referential.doh_positions p
//
//left join  hrm.employees e
//on e.position_code = p.poscode
//and e.is_active = true
//
//
//
//where p.poscode_parent in (1,2) or e.position_code_others = 10
//
//
//GROUP by  p.poscode_parent, p.id
//
//order by p.poscode
//
//) as sub
//
//left join referential.doh_positions pos on pos.poscode = sub.poscode_parent;
//        """, new BeanPropertyRowMapper(PositionCountDTO.class))
//
//        List<PositionCountDTO> positionOthers = jdbcTemplate.query("""
//Select
//TRUE as isothers,
//p.poscode as professiondesignation,
//p.postdesc as positiondesc,
//count(e.*) filter(where e.employee_type = 'PERMANENT - FULLTIME') as fulltime40Permanent,
//count(e.*) filter(where e.employee_type = 'CONTRACTUAL - FULLTIME') as fulltime40Contractual,
//count(e.*) filter(where e.employee_type = 'PERMANENT - PARTTIME') as parttimepermanent ,
//count(e.*) filter(where e.employee_type = 'CONTRACTUAL - PARTTIME') as parttimecontractual ,
//count(e.*) filter(where e.employee_type = 'ACTIVE ROTATING') as activerotatingaffiliate ,
//count(e.*) filter(where e.employee_type = 'OUTSOURCED') as outsourced,
//count(e.*) filter(where e.is_specialty_board_certified = true) as specialtyboardcertified
//from referential.doh_position_others p
//
//left join  hrm.employees e
//on e.is_active = true
//
//where e.position_code_others = p.poscode and e.position_type = p.postdesc
//
//GROUP by p.poscode, p.id
//
//order by p.poscode
//;
//        """, new BeanPropertyRowMapper(PositionCountDTO.class))
//
//        PositionCountDTO postGradFellow = new PositionCountDTO()
//        postGradFellow.positiondesc = 'Post-Graduate Fellows'
//        postGradFellow.professiondesignation = '16'
//
//
//        PositionCountDTO residentOthers = new PositionCountDTO()
//        PositionCountDTO genSuppStaffOthers = new PositionCountDTO()
//        PositionCountDTO consultantInternalMedOthers = new PositionCountDTO()
//
//        positionOthers.each {
//            if (it.professiondesignation == '17') {
//                postGradFellow.specialtyboardcertified += it.specialtyboardcertified
//                postGradFellow.fulltime40permanent += it.fulltime40permanent
//                postGradFellow.fulltime40contractual += it.fulltime40contractual
//                postGradFellow.parttimepermanent += it.parttimepermanent
//                postGradFellow.parttimecontractual += it.parttimecontractual
//                postGradFellow.activerotatingaffiliate += it.activerotatingaffiliate
//                postGradFellow.outsourced += it.outsourced
//            }
//
//            if (it.professiondesignation == '22') {
//                residentOthers.specialtyboardcertified += it.specialtyboardcertified
//                residentOthers.fulltime40permanent += it.fulltime40permanent
//                residentOthers.fulltime40contractual += it.fulltime40contractual
//                residentOthers.parttimepermanent += it.parttimepermanent
//                residentOthers.parttimecontractual += it.parttimecontractual
//                residentOthers.activerotatingaffiliate += it.activerotatingaffiliate
//                residentOthers.outsourced += it.outsourced
//            }
//
//            if (it.professiondesignation == '46') {
//                genSuppStaffOthers.specialtyboardcertified += it.specialtyboardcertified
//                genSuppStaffOthers.fulltime40permanent += it.fulltime40permanent
//                genSuppStaffOthers.fulltime40contractual += it.fulltime40contractual
//                genSuppStaffOthers.parttimepermanent += it.parttimepermanent
//                genSuppStaffOthers.parttimecontractual += it.parttimecontractual
//                genSuppStaffOthers.activerotatingaffiliate += it.activerotatingaffiliate
//                genSuppStaffOthers.outsourced += it.outsourced
//            }
//
//            if (it.professiondesignation == '10') {
//                consultantInternalMedOthers.specialtyboardcertified += it.specialtyboardcertified
//                consultantInternalMedOthers.fulltime40permanent += it.fulltime40permanent
//                consultantInternalMedOthers.fulltime40contractual += it.fulltime40contractual
//                consultantInternalMedOthers.parttimepermanent += it.parttimepermanent
//                consultantInternalMedOthers.parttimecontractual += it.parttimecontractual
//                consultantInternalMedOthers.activerotatingaffiliate += it.activerotatingaffiliate
//                consultantInternalMedOthers.outsourced += it.outsourced
//
//                consultants[0].specialtyboardcertified += it.specialtyboardcertified
//                consultants[0].fulltime40permanent += it.fulltime40permanent
//                consultants[0].fulltime40contractual += it.fulltime40contractual
//                consultants[0].parttimepermanent += it.parttimepermanent
//                consultants[0].parttimecontractual += it.parttimecontractual
//                consultants[0].activerotatingaffiliate += it.activerotatingaffiliate
//                consultants[0].outsourced += it.outsourced
//            }
//            allPositions.push(it)
//        }
//
//        countParentPositions.each {
//            PositionCountDTO temp = it
//            if (it.professiondesignation == '17') {
//                temp.specialtyboardcertified += residentOthers.specialtyboardcertified
//                temp.fulltime40permanent += residentOthers.fulltime40permanent
//                temp.fulltime40contractual += residentOthers.fulltime40contractual
//                temp.parttimepermanent += residentOthers.parttimepermanent
//                temp.parttimecontractual += residentOthers.parttimecontractual
//                temp.activerotatingaffiliate += residentOthers.activerotatingaffiliate
//                temp.outsourced += residentOthers.outsourced
//                allPositions.push(it)
//            } else if (it.professiondesignation == '42') {
//                temp.specialtyboardcertified += genSuppStaffOthers.specialtyboardcertified
//                temp.fulltime40permanent += genSuppStaffOthers.fulltime40permanent
//                temp.fulltime40contractual += genSuppStaffOthers.fulltime40contractual
//                temp.parttimepermanent += genSuppStaffOthers.parttimepermanent
//                temp.parttimecontractual += genSuppStaffOthers.parttimecontractual
//                temp.activerotatingaffiliate += genSuppStaffOthers.activerotatingaffiliate
//                temp.outsourced += genSuppStaffOthers.outsourced
//                allPositions.push(it)
//            } else if ( it.professiondesignation == '2') {
//                temp.specialtyboardcertified += consultantInternalMedOthers.specialtyboardcertified
//                temp.fulltime40permanent += consultantInternalMedOthers.fulltime40permanent
//                temp.fulltime40contractual += consultantInternalMedOthers.fulltime40contractual
//                temp.parttimepermanent += consultantInternalMedOthers.parttimepermanent
//                temp.parttimecontractual += consultantInternalMedOthers.parttimecontractual
//                temp.activerotatingaffiliate += consultantInternalMedOthers.activerotatingaffiliate
//                temp.outsourced += consultantInternalMedOthers.outsourced
//                allPositions.push(it)
//            } else {
//                allPositions.push(it)
//            }
//        }
//
//        allPositions.push(postGradFellow)
//        allPositions.push(consultants[0])
//
////allPositions.push(consultant)
//
//
//        return allPositions.sort({ it.professiondesignation as Integer })
//
//    }


    @GraphQLQuery(name = "countEmpByPositionV2")
    List<PositionCountDTO> countEmpByPositionV2() {

        List<PositionCountDTO> positionList = new ArrayList<PositionCountDTO>()

        List<PositionCountDTO> allPositions = jdbcTemplate.query("""
		Select 
p.poscode as professiondesignation,
p.postdesc as positiondesc,


count(e.*) filter(where e.employee_type = 'PERMANENT - FULLTIME') as fulltime40Permanent,
count(e.*) filter(where e.employee_type = 'CONTRACTUAL - FULLTIME') as fulltime40Contractual,
count(e.*) filter(where e.employee_type = 'PERMANENT - PARTTIME') as parttimepermanent ,
count(e.*) filter(where e.employee_type = 'CONTRACTUAL - PARTTIME') as parttimecontractual ,
count(e.*) filter(where e.employee_type = 'ACTIVE ROTATING') as activerotatingaffiliate ,
count(e.*) filter(where e.employee_type = 'OUTSOURCED') as outsourced,
count(e.*) filter(where e.is_specialty_board_certified = true) as specialtyboardcertified
from referential.doh_positions p

left join  hrm.employees e  
on e.position_code = p.poscode 
and e.is_active = true 

where  p.poscode not in (1,2,17,42, 16) 
and p.is_others is not true

GROUP by p.poscode, p.id

order by p.poscode
;

        """, new BeanPropertyRowMapper(PositionCountDTO.class))

        List<PositionCountDTO> countParentPositions = jdbcTemplate.query("""
select 
pos.poscode as professiondesignation,
pos.postdesc as positiondesc,
sum (sub.fulltime40Permanent ) as fulltime40Permanent,
sum (sub.fulltime40Contractual ) as fulltime40Contractual,
sum (sub.parttimepermanent ) as parttimepermanent ,
sum (sub.parttimecontractual ) as parttimecontractual ,
sum (sub.activerotatingaffiliate ) as activerotatingaffiliate ,
sum (sub.outsourced ) as outsourced,
sum (sub.is_specialty_board_certified ) as specialtyboardcertified

from(
Select 
p.* ,

count(e.*) filter(where e.employee_type = 'PERMANENT - FULLTIME') as fulltime40Permanent,
count(e.*) filter(where e.employee_type = 'CONTRACTUAL - FULLTIME') as fulltime40Contractual,
count(e.*) filter(where e.employee_type = 'PERMANENT - PARTTIME') as parttimepermanent ,
count(e.*) filter(where e.employee_type = 'CONTRACTUAL - PARTTIME') as parttimecontractual ,
count(e.*) filter(where e.employee_type = 'ACTIVE ROTATING') as activerotatingaffiliate ,
count(e.*) filter(where e.employee_type = 'OUTSOURCED') as outsourced,
count(e.*) filter(where e.is_specialty_board_certified = true) as is_specialty_board_certified
from referential.doh_positions p

left join  hrm.employees e  
on e.position_code = p.poscode 
and e.is_active = true 



where p.poscode_parent in (2,17, 16, 42) 
and p.is_others is not true


GROUP by  p.poscode_parent, p.id

order by p.poscode

) as sub

left join referential.doh_positions pos on pos.poscode = sub.poscode_parent
group by pos.poscode,pos.postdesc
;
        """, new BeanPropertyRowMapper(PositionCountDTO.class))


        List<PositionCountDTO> consultants = jdbcTemplate.query("""
select 
'1' as professiondesignation,
'Consultants' as positiondesc,
sum (sub.fulltime40Permanent ) as fulltime40Permanent,
sum (sub.fulltime40Contractual ) as fulltime40Contractual,
sum (sub.parttimepermanent ) as parttimepermanent ,
sum (sub.parttimecontractual ) as parttimecontractual ,
sum (sub.activerotatingaffiliate ) as activerotatingaffiliate ,
sum (sub.outsourced ) as outsourced,
sum (sub.is_specialty_board_certified ) as specialtyboardcertified

from(
Select 
p.* ,

count(e.*) filter(where e.employee_type = 'PERMANENT - FULLTIME') as fulltime40Permanent,
count(e.*) filter(where e.employee_type = 'CONTRACTUAL - FULLTIME') as fulltime40Contractual,
count(e.*) filter(where e.employee_type = 'PERMANENT - PARTTIME') as parttimepermanent ,
count(e.*) filter(where e.employee_type = 'CONTRACTUAL - PARTTIME') as parttimecontractual ,
count(e.*) filter(where e.employee_type = 'ACTIVE ROTATING') as activerotatingaffiliate ,
count(e.*) filter(where e.employee_type = 'OUTSOURCED') as outsourced,
count(e.*) filter(where e.is_specialty_board_certified = true) as is_specialty_board_certified
from referential.doh_positions p

left join  hrm.employees e  
on e.position_code = p.poscode 
and e.is_active = true 



where p.poscode_parent in (1,2) or e.position_code_others = 10 
and p.is_others is not true

GROUP by  p.poscode_parent, p.id

order by p.poscode

) as sub

left join referential.doh_positions pos on pos.poscode = sub.poscode_parent;
        """, new BeanPropertyRowMapper(PositionCountDTO.class))

        List<PositionCountDTO> positionOthers = jdbcTemplate.query("""
Select 
TRUE as isothers,
p.poscode as professiondesignation,
p.postdesc as positiondesc, 
count(e.*) filter(where e.employee_type = 'PERMANENT - FULLTIME') as fulltime40Permanent,
count(e.*) filter(where e.employee_type = 'CONTRACTUAL - FULLTIME') as fulltime40Contractual,
count(e.*) filter(where e.employee_type = 'PERMANENT - PARTTIME') as parttimepermanent ,
count(e.*) filter(where e.employee_type = 'CONTRACTUAL - PARTTIME') as parttimecontractual ,
count(e.*) filter(where e.employee_type = 'ACTIVE ROTATING') as activerotatingaffiliate ,
count(e.*) filter(where e.employee_type = 'OUTSOURCED') as outsourced,
count(e.*) filter(where e.is_specialty_board_certified = true) as specialtyboardcertified
from referential.doh_positions p

left join  hrm.employees e  
on e.is_active = true 

where e.position_code_others = p.poscode and e.position_type = p.postdesc
and p.is_others is true
GROUP by p.poscode, p.id

order by p.poscode
;
        """, new BeanPropertyRowMapper(PositionCountDTO.class))

        PositionCountDTO postGradFellow = new PositionCountDTO()
        postGradFellow.positiondesc = 'Post-Graduate Fellows'
        postGradFellow.professiondesignation = '16'


        PositionCountDTO residentOthers = new PositionCountDTO()
        residentOthers.positiondesc = 'Resident Others'
        residentOthers.professiondesignation = '22'
        residentOthers.isothers = false


        PositionCountDTO alliedMedicalOthers = new PositionCountDTO()
        alliedMedicalOthers.positiondesc = 'Allied Medical (others)'
        alliedMedicalOthers.professiondesignation = '35'
        alliedMedicalOthers.isothers = false

        PositionCountDTO nonMedicalOthers = new PositionCountDTO()
        nonMedicalOthers.positiondesc = 'Non-medical (others)'
        nonMedicalOthers.professiondesignation = '42'
        nonMedicalOthers.isothers = false

        PositionCountDTO genSuppStaffOthers = new PositionCountDTO()
        genSuppStaffOthers.positiondesc = 'General Support Staff (others)'
        genSuppStaffOthers.professiondesignation = '46'
        genSuppStaffOthers.isothers = false

        PositionCountDTO consultantInternalMedOthers = new PositionCountDTO()
        consultantInternalMedOthers.positiondesc = 'Internal Medicine (others)'
        consultantInternalMedOthers.professiondesignation = '10'
        consultantInternalMedOthers.isothers = false


        positionOthers.each {
            if (it.professiondesignation == '17') {//add to postGradFellow
                postGradFellow.specialtyboardcertified += it.specialtyboardcertified
                postGradFellow.fulltime40permanent += it.fulltime40permanent
                postGradFellow.fulltime40contractual += it.fulltime40contractual
                postGradFellow.parttimepermanent += it.parttimepermanent
                postGradFellow.parttimecontractual += it.parttimecontractual
                postGradFellow.activerotatingaffiliate += it.activerotatingaffiliate
                postGradFellow.outsourced += it.outsourced

            }

            if (it.professiondesignation == '22') {//add to residentOthers
                residentOthers.specialtyboardcertified += it.specialtyboardcertified
                residentOthers.fulltime40permanent += it.fulltime40permanent
                residentOthers.fulltime40contractual += it.fulltime40contractual
                residentOthers.parttimepermanent += it.parttimepermanent
                residentOthers.parttimecontractual += it.parttimecontractual
                residentOthers.activerotatingaffiliate += it.activerotatingaffiliate
                residentOthers.outsourced += it.outsourced
                residentOthers.isothers = true

            }

            if (it.professiondesignation == '35') {//add to alliedMedicalOthers
                alliedMedicalOthers.specialtyboardcertified += it.specialtyboardcertified
                alliedMedicalOthers.fulltime40permanent += it.fulltime40permanent
                alliedMedicalOthers.fulltime40contractual += it.fulltime40contractual
                alliedMedicalOthers.parttimepermanent += it.parttimepermanent
                alliedMedicalOthers.parttimecontractual += it.parttimecontractual
                alliedMedicalOthers.activerotatingaffiliate += it.activerotatingaffiliate
                alliedMedicalOthers.outsourced += it.outsourced
                alliedMedicalOthers.isothers = true

            }

            if (it.professiondesignation == '42') {//add to nonMedicalOthers
                nonMedicalOthers.specialtyboardcertified += it.specialtyboardcertified
                nonMedicalOthers.fulltime40permanent += it.fulltime40permanent
                nonMedicalOthers.fulltime40contractual += it.fulltime40contractual
                nonMedicalOthers.parttimepermanent += it.parttimepermanent
                nonMedicalOthers.parttimecontractual += it.parttimecontractual
                nonMedicalOthers.activerotatingaffiliate += it.activerotatingaffiliate
                nonMedicalOthers.outsourced += it.outsourced
                nonMedicalOthers.isothers = true

            }

            if (it.professiondesignation == '46') {//add to genSuppStaffOthers
                genSuppStaffOthers.specialtyboardcertified += it.specialtyboardcertified
                genSuppStaffOthers.fulltime40permanent += it.fulltime40permanent
                genSuppStaffOthers.fulltime40contractual += it.fulltime40contractual
                genSuppStaffOthers.parttimepermanent += it.parttimepermanent
                genSuppStaffOthers.parttimecontractual += it.parttimecontractual
                genSuppStaffOthers.activerotatingaffiliate += it.activerotatingaffiliate
                genSuppStaffOthers.outsourced += it.outsourced
                genSuppStaffOthers.isothers = true

            }

            if (it.professiondesignation == '10') {//add to consultantInternalMedOthers and consultants[0]
                consultantInternalMedOthers.specialtyboardcertified += it.specialtyboardcertified
                consultantInternalMedOthers.fulltime40permanent += it.fulltime40permanent
                consultantInternalMedOthers.fulltime40contractual += it.fulltime40contractual
                consultantInternalMedOthers.parttimepermanent += it.parttimepermanent
                consultantInternalMedOthers.parttimecontractual += it.parttimecontractual
                consultantInternalMedOthers.activerotatingaffiliate += it.activerotatingaffiliate
                consultantInternalMedOthers.outsourced += it.outsourced
                consultantInternalMedOthers.isothers = true

                consultants[0].specialtyboardcertified += it.specialtyboardcertified
                consultants[0].fulltime40permanent += it.fulltime40permanent
                consultants[0].fulltime40contractual += it.fulltime40contractual
                consultants[0].parttimepermanent += it.parttimepermanent
                consultants[0].parttimecontractual += it.parttimecontractual
                consultants[0].activerotatingaffiliate += it.activerotatingaffiliate
                consultants[0].outsourced += it.outsourced
            }
//            allPositions.push(it)
        }

        countParentPositions.each {
            PositionCountDTO temp = it
            if (it.professiondesignation == '17') {
                temp.specialtyboardcertified += residentOthers.specialtyboardcertified
                temp.fulltime40permanent += residentOthers.fulltime40permanent
                temp.fulltime40contractual += residentOthers.fulltime40contractual
                temp.parttimepermanent += residentOthers.parttimepermanent
                temp.parttimecontractual += residentOthers.parttimecontractual
                temp.activerotatingaffiliate += residentOthers.activerotatingaffiliate
                temp.outsourced += residentOthers.outsourced
                allPositions.push(it)
            } else if (it.professiondesignation == '42') {
                temp.specialtyboardcertified += genSuppStaffOthers.specialtyboardcertified
                temp.fulltime40permanent += genSuppStaffOthers.fulltime40permanent
                temp.fulltime40contractual += genSuppStaffOthers.fulltime40contractual
                temp.parttimepermanent += genSuppStaffOthers.parttimepermanent
                temp.parttimecontractual += genSuppStaffOthers.parttimecontractual
                temp.activerotatingaffiliate += genSuppStaffOthers.activerotatingaffiliate
                temp.outsourced += genSuppStaffOthers.outsourced
                allPositions.push(it)
            } else if (it.professiondesignation == '2') {
                temp.specialtyboardcertified += consultantInternalMedOthers.specialtyboardcertified
                temp.fulltime40permanent += consultantInternalMedOthers.fulltime40permanent
                temp.fulltime40contractual += consultantInternalMedOthers.fulltime40contractual
                temp.parttimepermanent += consultantInternalMedOthers.parttimepermanent
                temp.parttimecontractual += consultantInternalMedOthers.parttimecontractual
                temp.activerotatingaffiliate += consultantInternalMedOthers.activerotatingaffiliate
                temp.outsourced += consultantInternalMedOthers.outsourced
                allPositions.push(it)
            } else {
                allPositions.push(it)
            }
        }

        allPositions.push(postGradFellow)
        allPositions.push(consultants[0])


        if (residentOthers.isothers) allPositions.push(residentOthers)
        if (genSuppStaffOthers.isothers) allPositions.push(genSuppStaffOthers)
        if (consultantInternalMedOthers.isothers) allPositions.push(consultantInternalMedOthers)
        if (alliedMedicalOthers.isothers) allPositions.push(alliedMedicalOthers)
        if (nonMedicalOthers.isothers) allPositions.push(nonMedicalOthers)


//allPositions.push(consultant)


        return allPositions.sort({ it.professiondesignation as Integer })

    }

    @GraphQLQuery(name = "countEmpByPositionV2_OLD")
    List<PositionCountDTO> countEmpByPositionV2_OLD() {

        List<PositionCountDTO> positionList = new ArrayList<PositionCountDTO>()

        List<PositionCountDTO> allPositions = jdbcTemplate.query("""
		Select 
p.poscode as professiondesignation,
p.postdesc as positiondesc,


count(e.*) filter(where e.employee_type = 'PERMANENT - FULLTIME') as fulltime40Permanent,
count(e.*) filter(where e.employee_type = 'CONTRACTUAL - FULLTIME') as fulltime40Contractual,
count(e.*) filter(where e.employee_type = 'PERMANENT - PARTTIME') as parttimepermanent ,
count(e.*) filter(where e.employee_type = 'CONTRACTUAL - PARTTIME') as parttimecontractual ,
count(e.*) filter(where e.employee_type = 'ACTIVE ROTATING') as activerotatingaffiliate ,
count(e.*) filter(where e.employee_type = 'OUTSOURCED') as outsourced,
count(e.*) filter(where e.is_specialty_board_certified = true) as specialtyboardcertified
from referential.doh_positions p

left join  hrm.employees e  
on e.position_code = p.poscode 
and e.is_active = true 

where  p.poscode not in (1,2,17,42, 16)

GROUP by p.poscode, p.id

order by p.poscode
;

        """, new BeanPropertyRowMapper(PositionCountDTO.class))

        List<PositionCountDTO> countParentPositions = jdbcTemplate.query("""
select 
pos.poscode as professiondesignation,
pos.postdesc as positiondesc,
sum (sub.fulltime40Permanent ) as fulltime40Permanent,
sum (sub.fulltime40Contractual ) as fulltime40Contractual,
sum (sub.parttimepermanent ) as parttimepermanent ,
sum (sub.parttimecontractual ) as parttimecontractual ,
sum (sub.activerotatingaffiliate ) as activerotatingaffiliate ,
sum (sub.outsourced ) as outsourced,
sum (sub.is_specialty_board_certified ) as specialtyboardcertified

from(
Select 
p.* ,

count(e.*) filter(where e.employee_type = 'PERMANENT - FULLTIME') as fulltime40Permanent,
count(e.*) filter(where e.employee_type = 'CONTRACTUAL - FULLTIME') as fulltime40Contractual,
count(e.*) filter(where e.employee_type = 'PERMANENT - PARTTIME') as parttimepermanent ,
count(e.*) filter(where e.employee_type = 'CONTRACTUAL - PARTTIME') as parttimecontractual ,
count(e.*) filter(where e.employee_type = 'ACTIVE ROTATING') as activerotatingaffiliate ,
count(e.*) filter(where e.employee_type = 'OUTSOURCED') as outsourced,
count(e.*) filter(where e.is_specialty_board_certified = true) as is_specialty_board_certified
from referential.doh_positions p

left join  hrm.employees e  
on e.position_code = p.poscode 
and e.is_active = true 



where p.poscode_parent in (2,17, 16, 42)


GROUP by  p.poscode_parent, p.id

order by p.poscode

) as sub

left join referential.doh_positions pos on pos.poscode = sub.poscode_parent
group by pos.poscode,pos.postdesc
;
        """, new BeanPropertyRowMapper(PositionCountDTO.class))


        List<PositionCountDTO> consultants = jdbcTemplate.query("""
select 
'1' as professiondesignation,
'Consultants' as positiondesc,
sum (sub.fulltime40Permanent ) as fulltime40Permanent,
sum (sub.fulltime40Contractual ) as fulltime40Contractual,
sum (sub.parttimepermanent ) as parttimepermanent ,
sum (sub.parttimecontractual ) as parttimecontractual ,
sum (sub.activerotatingaffiliate ) as activerotatingaffiliate ,
sum (sub.outsourced ) as outsourced,
sum (sub.is_specialty_board_certified ) as specialtyboardcertified

from(
Select 
p.* ,

count(e.*) filter(where e.employee_type = 'PERMANENT - FULLTIME') as fulltime40Permanent,
count(e.*) filter(where e.employee_type = 'CONTRACTUAL - FULLTIME') as fulltime40Contractual,
count(e.*) filter(where e.employee_type = 'PERMANENT - PARTTIME') as parttimepermanent ,
count(e.*) filter(where e.employee_type = 'CONTRACTUAL - PARTTIME') as parttimecontractual ,
count(e.*) filter(where e.employee_type = 'ACTIVE ROTATING') as activerotatingaffiliate ,
count(e.*) filter(where e.employee_type = 'OUTSOURCED') as outsourced,
count(e.*) filter(where e.is_specialty_board_certified = true) as is_specialty_board_certified
from referential.doh_positions p

left join  hrm.employees e  
on e.position_code = p.poscode 
and e.is_active = true 



where p.poscode_parent in (1,2) or e.position_code_others = 10


GROUP by  p.poscode_parent, p.id

order by p.poscode

) as sub

left join referential.doh_positions pos on pos.poscode = sub.poscode_parent;
        """, new BeanPropertyRowMapper(PositionCountDTO.class))

        List<PositionCountDTO> positionOthers = jdbcTemplate.query("""
Select 
TRUE as isothers,
p.poscode as professiondesignation,
p.postdesc as positiondesc, 
count(e.*) filter(where e.employee_type = 'PERMANENT - FULLTIME') as fulltime40Permanent,
count(e.*) filter(where e.employee_type = 'CONTRACTUAL - FULLTIME') as fulltime40Contractual,
count(e.*) filter(where e.employee_type = 'PERMANENT - PARTTIME') as parttimepermanent ,
count(e.*) filter(where e.employee_type = 'CONTRACTUAL - PARTTIME') as parttimecontractual ,
count(e.*) filter(where e.employee_type = 'ACTIVE ROTATING') as activerotatingaffiliate ,
count(e.*) filter(where e.employee_type = 'OUTSOURCED') as outsourced,
count(e.*) filter(where e.is_specialty_board_certified = true) as specialtyboardcertified
from referential.doh_position_others p

left join  hrm.employees e  
on e.is_active = true 

where e.position_code_others = p.poscode and e.position_type = p.postdesc

GROUP by p.poscode, p.id

order by p.poscode
;
        """, new BeanPropertyRowMapper(PositionCountDTO.class))

        PositionCountDTO postGradFellow = new PositionCountDTO()
        postGradFellow.positiondesc = 'Post-Graduate Fellows'
        postGradFellow.professiondesignation = '16'


        PositionCountDTO residentOthers = new PositionCountDTO()
        PositionCountDTO genSuppStaffOthers = new PositionCountDTO()
        PositionCountDTO consultantInternalMedOthers = new PositionCountDTO()

        positionOthers.each {
            if (it.professiondesignation == '17') {//add to postGradFellow

                postGradFellow.specialtyboardcertified += it.specialtyboardcertified
                postGradFellow.fulltime40permanent += it.fulltime40permanent
                postGradFellow.fulltime40contractual += it.fulltime40contractual
                postGradFellow.parttimepermanent += it.parttimepermanent
                postGradFellow.parttimecontractual += it.parttimecontractual
                postGradFellow.activerotatingaffiliate += it.activerotatingaffiliate
                postGradFellow.outsourced += it.outsourced
            }

            if (it.professiondesignation == '22') {//add to residentOthers
                residentOthers.specialtyboardcertified += it.specialtyboardcertified
                residentOthers.fulltime40permanent += it.fulltime40permanent
                residentOthers.fulltime40contractual += it.fulltime40contractual
                residentOthers.parttimepermanent += it.parttimepermanent
                residentOthers.parttimecontractual += it.parttimecontractual
                residentOthers.activerotatingaffiliate += it.activerotatingaffiliate
                residentOthers.outsourced += it.outsourced
            }

            if (it.professiondesignation == '46') {//add to genSuppStaffOthers
                genSuppStaffOthers.specialtyboardcertified += it.specialtyboardcertified
                genSuppStaffOthers.fulltime40permanent += it.fulltime40permanent
                genSuppStaffOthers.fulltime40contractual += it.fulltime40contractual
                genSuppStaffOthers.parttimepermanent += it.parttimepermanent
                genSuppStaffOthers.parttimecontractual += it.parttimecontractual
                genSuppStaffOthers.activerotatingaffiliate += it.activerotatingaffiliate
                genSuppStaffOthers.outsourced += it.outsourced
            }

            if (it.professiondesignation == '10') {//add to consultantInternalMedOthers
                consultantInternalMedOthers.specialtyboardcertified += it.specialtyboardcertified
                consultantInternalMedOthers.fulltime40permanent += it.fulltime40permanent
                consultantInternalMedOthers.fulltime40contractual += it.fulltime40contractual
                consultantInternalMedOthers.parttimepermanent += it.parttimepermanent
                consultantInternalMedOthers.parttimecontractual += it.parttimecontractual
                consultantInternalMedOthers.activerotatingaffiliate += it.activerotatingaffiliate
                consultantInternalMedOthers.outsourced += it.outsourced

                consultants[0].specialtyboardcertified += it.specialtyboardcertified
                consultants[0].fulltime40permanent += it.fulltime40permanent
                consultants[0].fulltime40contractual += it.fulltime40contractual
                consultants[0].parttimepermanent += it.parttimepermanent
                consultants[0].parttimecontractual += it.parttimecontractual
                consultants[0].activerotatingaffiliate += it.activerotatingaffiliate
                consultants[0].outsourced += it.outsourced
            }
            allPositions.push(it)
        }

        countParentPositions.each {
            PositionCountDTO temp = it
            if (it.professiondesignation == '17') {
                temp.specialtyboardcertified += residentOthers.specialtyboardcertified
                temp.fulltime40permanent += residentOthers.fulltime40permanent
                temp.fulltime40contractual += residentOthers.fulltime40contractual
                temp.parttimepermanent += residentOthers.parttimepermanent
                temp.parttimecontractual += residentOthers.parttimecontractual
                temp.activerotatingaffiliate += residentOthers.activerotatingaffiliate
                temp.outsourced += residentOthers.outsourced
                allPositions.push(it)
            } else if (it.professiondesignation == '42') {
                temp.specialtyboardcertified += genSuppStaffOthers.specialtyboardcertified
                temp.fulltime40permanent += genSuppStaffOthers.fulltime40permanent
                temp.fulltime40contractual += genSuppStaffOthers.fulltime40contractual
                temp.parttimepermanent += genSuppStaffOthers.parttimepermanent
                temp.parttimecontractual += genSuppStaffOthers.parttimecontractual
                temp.activerotatingaffiliate += genSuppStaffOthers.activerotatingaffiliate
                temp.outsourced += genSuppStaffOthers.outsourced
                allPositions.push(it)
            } else if (it.professiondesignation == '2') {
                temp.specialtyboardcertified += consultantInternalMedOthers.specialtyboardcertified
                temp.fulltime40permanent += consultantInternalMedOthers.fulltime40permanent
                temp.fulltime40contractual += consultantInternalMedOthers.fulltime40contractual
                temp.parttimepermanent += consultantInternalMedOthers.parttimepermanent
                temp.parttimecontractual += consultantInternalMedOthers.parttimecontractual
                temp.activerotatingaffiliate += consultantInternalMedOthers.activerotatingaffiliate
                temp.outsourced += consultantInternalMedOthers.outsourced
                allPositions.push(it)
            } else {
                allPositions.push(it)
            }
        }

        allPositions.push(postGradFellow)
        allPositions.push(consultants[0])

//allPositions.push(consultant)


        return allPositions.sort({ it.professiondesignation as Integer })

    }


    //==================================Mutation ============
    @GraphQLMutation
    def postStaffingPattern(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    ) {
        if (id) {
            def staffingCapacity = staffingPatternRepository.findById(id).get()
            objectMapper.updateValue(staffingCapacity, fields)
            staffingCapacity.submittedDateTime = Instant.now()

            return staffingPatternRepository.save(staffingCapacity)
        } else {

            def staffingCapacity = objectMapper.convertValue(fields, StaffingPattern)

            return staffingPatternRepository.save(staffingCapacity)
        }
    }


    @GraphQLMutation(name = "postStaffingPatternDoh")
    GraphQLRetVal<String> postStaffingPatternDoh(@GraphQLArgument(name = 'fields') Map<String, Object> fields) {

        try {
            ph.gov.doh.uhmistrn.ahsr.webservice.index.StaffingPattern request = new ph.gov.doh.uhmistrn.ahsr.webservice.index.StaffingPattern()

            request.hfhudcode = hospitalConfigService.hospitalInfo.hfhudcode ?: ""
            request.professiondesignation = fields.get('operationCode') as String
            request.specialtyboardcertified = fields.get('surgicalOperation') as String
            request.fulltime40Permanent = fields.get('number') as String
            request.fulltime40Contractual = fields.get('number') as String
            request.parttimepermanent = fields.get('number') as String
            request.parttimecontractual = fields.get('number') as String
            request.activerotatingaffiliate = fields.get('number') as String
            request.outsourced = fields.get('number') as String
            request.reportingyear = fields.get("reportingYear") as Integer


            StaffingPatternResponse response =
                    (StaffingPatternResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/staffingPattern", request)

            UUID id = UUID.fromString(fields.get('id') as String)
            StaffingPattern dto = staffingPatternRepository.getOne(id)
            dto.dohResponse = response.return

            staffingPatternRepository.save(dto)

            return new GraphQLRetVal<String>(response.return, true)

        } catch (Exception e) {
            return new GraphQLRetVal<String>(e.message, false)
        }
    }
}
