package com.hisd3.hismk2.graphqlservices.doh

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.hisd3.hismk2.domain.doh.DohConfiguration
import com.hisd3.hismk2.domain.doh.StaffingPatternOthers
import com.hisd3.hismk2.domain.doh.TotalDeathsConfig
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.views.doh.MorbidityByAgeView
import com.hisd3.hismk2.graphqlservices.doh.dto.DeliveriesDto
import com.hisd3.hismk2.graphqlservices.doh.dto.DengueConfirmedCasesDto
import com.hisd3.hismk2.graphqlservices.doh.dto.DohDeathsDto
import com.hisd3.hismk2.graphqlservices.doh.dto.DohDeathsPageDto
import com.hisd3.hismk2.graphqlservices.doh.dto.DohTestingDto
import com.hisd3.hismk2.graphqlservices.doh.dto.EmergencyVisitsDto
import com.hisd3.hismk2.graphqlservices.doh.dto.TotalDeathsDto
import com.hisd3.hismk2.graphqlservices.doh.dto.TransfersCountDto
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.rest.dto.DohIcdCodeDto
import com.hisd3.hismk2.rest.dto.HospitalDischargeMorbidityDto
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.utils.SOAPConnector
import groovy.transform.Canonical
import groovy.transform.builder.Builder
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.hibernate.query.NativeQuery
import org.hibernate.transform.Transformers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Component
import ph.gov.doh.uhmistrn.ahsr.webservice.index.AuthenticationTest
import ph.gov.doh.uhmistrn.ahsr.webservice.index.AuthenticationTestResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.StaffingPatternOthersResponse

import javax.persistence.EntityManager
import java.math.RoundingMode
import java.sql.ResultSet
import java.sql.SQLException
import java.text.DecimalFormat
import java.util.stream.Collectors


@Canonical
class DOHProgressLogs {
    String items
    String description
    Integer success
    Integer key
    String logs
}


@Component
@GraphQLApi
class DohReportService {

    @Autowired
    private JdbcTemplate jdbcTemplate

    @Autowired
    EntityManager entityManager


    @Autowired
    SOAPConnector soapConnector

    @Autowired
    HospitalConfigService hospitalConfigService

    @Autowired
    DohAPIService dohAPIService

    @Autowired
    EmployeeRepository employeeRepository

    @Autowired
    BedCapacityServices bedCapacityServices

    @Autowired
    DohConfigurationService dohConfigurationService

    @GraphQLQuery(name = "count_confirmed_dengue_cases")
    List<DengueConfirmedCasesDto> getAllConfirmedDengue(@GraphQLArgument(name = 'year') Integer year) {
        List<DengueConfirmedCasesDto> qResult = jdbcTemplate.query("""SELECT icdcode, longname, total FROM pms.morbidity_by_age_view where upper(longname) LIKE '%' || 'DENGUE' || '%' and reporting_year = ${year};""", new BeanPropertyRowMapper(DengueConfirmedCasesDto.class))

        return qResult
    }

    @GraphQLQuery(name = "top_10_morbidity_cases")
    List<Map<String, Object>> top10MorbidityCases(@GraphQLArgument(name = 'year') Integer year) {
        def qResult = jdbcTemplate.queryForList("""SELECT * FROM pms.morbidity_by_age_view where reporting_year = ${year} limit 10""")

        return qResult
    }

    @GraphQLQuery(name = "top_10_mortality_cases")
    List<Map<String, Object>> top10MortalityCases(@GraphQLArgument(name = 'year') Integer year) {
        def qResult = jdbcTemplate.queryForList("""SELECT * FROM pms.mortality_by_age_view2 where reporting_year = ${year} limit 10""")

        return qResult
    }


    @GraphQLQuery(name = "top_10_mortality_casesV2")
    List<Map<String, Object>> top10MortalityCasesV2(@GraphQLArgument(name = 'year') Integer year) {
        def qResult = jdbcTemplate.queryForList("""
                 WITH patient_age AS (
                    SELECT p_1.id,
                        p_1.gender,
                        date_part('year'::text, age(p_1.dob::timestamp with time zone))::integer AS page
                    FROM pms.patients p_1
                )
                    SELECT 
                ROW_NUMBER() OVER(
                    ORDER BY count(*) desc
                ) as no,
                c.id,
                    d.value ->> 'icdCode'::text AS icd10code,
                    d.value ->> 'icdDesc'::text AS icd10desc,
                    dic.icd10_cat as icd10category,
                    date_part('year'::text, c.discharged_datetime) AS reporting_year,
                    count(*) FILTER (WHERE p.page < 1 AND p.gender::text = 'MALE'::text) AS munder1,
                    count(*) FILTER (WHERE p.page < 1 AND p.gender::text = 'FEMALE'::text) AS funder1,
                    count(*) FILTER (WHERE p.page >= 1 AND p.page <= 4 AND p.gender::text = 'MALE'::text) AS m1to4,
                    count(*) FILTER (WHERE p.page >= 1 AND p.page <= 4 AND p.gender::text = 'FEMALE'::text) AS f1to4,
                    count(*) FILTER (WHERE p.page >= 5 AND p.page <= 9 AND p.gender::text = 'MALE'::text) AS m5to9,
                    count(*) FILTER (WHERE p.page >= 5 AND p.page <= 9 AND p.gender::text = 'FEMALE'::text) AS f5to9,
                    count(*) FILTER (WHERE p.page >= 10 AND p.page <= 14 AND p.gender::text = 'MALE'::text) AS m10to14,
                    count(*) FILTER (WHERE p.page >= 10 AND p.page <= 14 AND p.gender::text = 'FEMALE'::text) AS f10to14,
                    count(*) FILTER (WHERE p.page >= 15 AND p.page <= 19 AND p.gender::text = 'MALE'::text) AS m15to19,
                    count(*) FILTER (WHERE p.page >= 15 AND p.page <= 19 AND p.gender::text = 'FEMALE'::text) AS f15to19,
                    count(*) FILTER (WHERE p.page >= 20 AND p.page <= 24 AND p.gender::text = 'MALE'::text) AS m20to24,
                    count(*) FILTER (WHERE p.page >= 20 AND p.page <= 24 AND p.gender::text = 'FEMALE'::text) AS f20to24,
                    count(*) FILTER (WHERE p.page >= 25 AND p.page <= 29 AND p.gender::text = 'MALE'::text) AS m25to29,
                    count(*) FILTER (WHERE p.page >= 25 AND p.page <= 29 AND p.gender::text = 'FEMALE'::text) AS f25to29,
                    count(*) FILTER (WHERE p.page >= 30 AND p.page <= 34 AND p.gender::text = 'MALE'::text) AS m30to34,
                    count(*) FILTER (WHERE p.page >= 30 AND p.page <= 34 AND p.gender::text = 'FEMALE'::text) AS f30to34,
                    count(*) FILTER (WHERE p.page >= 35 AND p.page <= 39 AND p.gender::text = 'MALE'::text) AS m35to39,
                    count(*) FILTER (WHERE p.page >= 35 AND p.page <= 39 AND p.gender::text = 'FEMALE'::text) AS f35to39,
                    count(*) FILTER (WHERE p.page >= 40 AND p.page <= 44 AND p.gender::text = 'MALE'::text) AS m40to44,
                    count(*) FILTER (WHERE p.page >= 40 AND p.page <= 44 AND p.gender::text = 'FEMALE'::text) AS f40to44,
                    count(*) FILTER (WHERE p.page >= 45 AND p.page <= 49 AND p.gender::text = 'MALE'::text) AS m45to49,
                    count(*) FILTER (WHERE p.page >= 45 AND p.page <= 49 AND p.gender::text = 'FEMALE'::text) AS f45to49,
                    count(*) FILTER (WHERE p.page >= 50 AND p.page <= 54 AND p.gender::text = 'MALE'::text) AS m50to54,
                    count(*) FILTER (WHERE p.page >= 50 AND p.page <= 54 AND p.gender::text = 'FEMALE'::text) AS f50to54,
                    count(*) FILTER (WHERE p.page >= 55 AND p.page <= 59 AND p.gender::text = 'MALE'::text) AS m55to59,
                    count(*) FILTER (WHERE p.page >= 55 AND p.page <= 59 AND p.gender::text = 'FEMALE'::text) AS f55to59,
                    count(*) FILTER (WHERE p.page >= 60 AND p.page <= 64 AND p.gender::text = 'MALE'::text) AS m60to64,
                    count(*) FILTER (WHERE p.page >= 60 AND p.page <= 64 AND p.gender::text = 'FEMALE'::text) AS f60to64,
                    count(*) FILTER (WHERE p.page >= 65 AND p.page <= 69 AND p.gender::text = 'MALE'::text) AS m65to69,
                    count(*) FILTER (WHERE p.page >= 65 AND p.page <= 69 AND p.gender::text = 'FEMALE'::text) AS f65to69,
                    count(*) FILTER (WHERE p.page >= 70 AND p.gender::text = 'MALE'::text) AS m70over,
                    count(*) FILTER (WHERE p.page >= 70 AND p.gender::text = 'FEMALE'::text) AS f70over,
                    count(*) FILTER (WHERE p.gender::text = 'MALE'::text) AS msubtotal,
                    count(*) FILTER (WHERE p.gender::text = 'FEMALE'::text) AS fsubtotal,
                    count(*) AS grandtotal
                FROM pms.cases c,
                    patient_age p,
                LATERAL json_array_elements(
                CASE 
                    WHEN pms.is_json(c.doh_icd_diagnosis ::character varying) AND c.doh_icd_diagnosis IS NOT NULL THEN c.doh_icd_diagnosis::json
                ELSE NULL::json
                    END) d(value)
                LEFT JOIN referential.doh_icd_codes dic on dic.icd10_code = d.value ->> 'icdCode'
                WHERE 
                    d.value ->> 'causeOfDeath' = 'UNDERLYING' 
                and 
             --   (CAST (d.value ->> 'isCardioRespiratoryArrest' as BOOLEAN) is null OR CAST(d.value ->> 'isCardioRespiratoryArrest' as BOOLEAN) is false)
             --   and 
                    d.value ->> 'icdCode' is not null
                and
                    c.patient = p.id
                and
                    c.discharge_condition = 'EXPIRED'
                and
                 date_part('year'::text, c.discharged_datetime + interval '8 hour') = ? ::DOUBLE PRECISION
                 GROUP BY (d.value ->> 'icdCode'::text), (d.value ->> 'icdDesc'::text), (date_part('year'::text, c.discharged_datetime), dic.icd10_cat), c.id
                 ORDER BY (count(*)) desc
                --limit 10
        """, year)

        return qResult
    }


    @GraphQLQuery(name = "count_deaths")
    TotalDeathsDto getTotalDeaths(@GraphQLArgument(name = 'year') Integer year) {
        TotalDeathsDto qResult = jdbcTemplate.queryForObject("""
      SELECT 
 count(*) filter (where pms.double_get_hours(coalesce(admission_datetime, entry_datetime) + '8:00:00'::interval, discharged_datetime  + '8:00:00'::interval ) > 48) as greater_than_48hrs, 
 count(*) filter (where pms.double_get_hours(coalesce(admission_datetime, entry_datetime) + '8:00:00'::interval, discharged_datetime  + '8:00:00'::interval ) < 48) as less_than_48hrs,
    count(*) filter(where discharged_datetime is null) as no_discharge_date,
    count(*) filter(where admission_datetime is null) as no_admission_date,
 count(*) filter(where entry_datetime is null) as no_entry_datetime,
 (
  count(*) filter (where pms.double_get_hours(coalesce(admission_datetime, entry_datetime) + '8:00:00'::interval, time_of_death  + '8:00:00'::interval ) > 48)::int +
     count(*) filter (where pms.double_get_hours(coalesce(admission_datetime, entry_datetime) + '8:00:00'::interval, time_of_death  + '8:00:00'::interval ) < 48)::int
    )as total

FROM pms.cases where discharge_condition = 'EXPIRED' and  date_part('year'::text, discharged_datetime) = ${year}
  """, new BeanPropertyRowMapper(TotalDeathsDto.class))

        return qResult
    }

    @GraphQLQuery(name = "count_cases_by_registry_type")
    List<EmergencyVisitsDto> totalEmergencyVisits(@GraphQLArgument(name = 'year') Integer year) {
        List<EmergencyVisitsDto> qResult = jdbcTemplate.query("""SELECT registry_type, count(*) as total FROM pms.transfers where date_part('year'::text, entry_datetime) = ${year} group by registry_type ;""", new BeanPropertyRowMapper(EmergencyVisitsDto.class))
        return qResult
    }

    @GraphQLQuery(name = "count_deaths_by_registry")
    List<EmergencyVisitsDto> totalDeathsByRegistryType(@GraphQLArgument(name = 'year') Integer year) {
        List<EmergencyVisitsDto> qResult = jdbcTemplate.query("""SELECT registry_type, count(*) as total FROM pms.cases where discharge_condition = 'EXPIRED' and date_part('year', time_of_death) = ${year} group by registry_type;""", new BeanPropertyRowMapper(EmergencyVisitsDto.class))
        return qResult
    }

    @GraphQLQuery(name = "count_cases_by_discharge_disposition")
    List<TransfersCountDto> totalTransfers(@GraphQLArgument(name = 'year') Integer year) {
        List<EmergencyVisitsDto> qResult = jdbcTemplate.query("""SELECT discharge_disposition, count(*) as total FROM pms.cases where date_part('year', discharged_datetime) = ${year} group by discharge_disposition""", new BeanPropertyRowMapper(TransfersCountDto.class))
        return qResult
    }

    @GraphQLQuery(name = "dohIcd10CustomMortality")
    List<DohIcdCodeDto> dohIcd10Custom(@GraphQLArgument(name = 'year') Integer year) {
        def items = jdbcTemplate.queryForList("SELECT  icdcode, longname FROM pms.mortality_by_age_view where reporting_year = ${year}  limit 10")
        List<DohIcdCodeDto> dtoList = new ArrayList<>()
        items.each {
            DohIcdCodeDto dto = new DohIcdCodeDto()
            dto.icdCode = it.icdcode
            dto.icdDesc = it.longname
            dtoList << dto
        }

        return dtoList
    }

    @GraphQLQuery(name = "hospOptDischargesMortalityV2")
    HospitalDischargeMorbidityDto hospOptDischargesMortalityV2(@GraphQLArgument(name = 'icdRvsCode') String icdRvsCode, @GraphQLArgument(name = "year") Integer year) {
        def parameters = [:]
        int currentYear = Calendar.getInstance().get(Calendar.YEAR)
        List<MorbidityByAgeView> morbidityByAgeViews
        HospitalDischargeMorbidityDto dto = new HospitalDischargeMorbidityDto()

        if (StringUtils.isBlank(icdRvsCode) || icdRvsCode == 'all') {
            parameters.put('year', year ?: currentYear)
            morbidityByAgeViews = jdbcTemplate.queryForList('SELECT * FROM pms.mortality_by_age_view')
        } else {
            def dohIcdCode = jdbcTemplate.query(
                    """SELECT icd10_code, icd10_desc, icd10_cat, id, doh_icd_cat FROM referential.doh_icd_codes where icd10_code = '${
                        icdRvsCode
                    }'"""
                    , new RowMapper<Map<String, Object>>() {
                @Override
                Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
                    def mapper = [:]
                    mapper.put('icd10Category', rs.getString('icd10_cat'))
                    mapper.put('icd10Code', rs.getString('icd10_code'))
                    mapper.put('icd10Desc', rs.getString('icd10_desc'))
                    return mapper
                }
            })

            if (dohIcdCode) {
                dto.diagnosisCategory = dohIcdCode.icd10Category
            }
            parameters.put('icdCode', icdRvsCode)
            parameters.put('year', year ?: currentYear)
            morbidityByAgeViews = jdbcTemplate.queryForList("SELECT * FROM pms.mortality_by_age_view where icdcode = ? and reporting_year = ?", icdRvsCode, year)
        }


        morbidityByAgeViews.each {
            item ->
                if (icdRvsCode != "all" && icdRvsCode != "" && icdRvsCode != null) {
                    dto.icd10Code = item.icdcode
                    dto.icd10Desc = item.longname
                }
                dto.reportingYear = year ?: currentYear
                dto.maleUnder1 += item.munder1 ?: 0
                dto.femaleUnder1 += item.funder1 ?: 0
                dto.male14 += item.m1to4
                dto.female14 += item.f1to4
                dto.male59 += item.m5to9
                dto.female59 += item.f5to9
                dto.male1014 += item.m10to14
                dto.female1014 += item.f10to14
                dto.male1519 += item.m15to19
                dto.female1519 += item.f15to19
                dto.male2024 += item.m20to24
                dto.female2024 += item.f20to24
                dto.male2529 += item.m25to29
                dto.female2529 += item.f25to29
                dto.male3034 += item.m30to34
                dto.female3034 += item.f30to34
                dto.male3539 += item.m35to39
                dto.female3539 += item.f35to39
                dto.male4044 += item.m40to44
                dto.female4044 += item.f40to44
                dto.male4549 += item.m45to49
                dto.female4549 += item.f45to49
                dto.male5054 += item.m50to54
                dto.female5054 += item.f50to54
                dto.male5559 += item.m55to59
                dto.female5559 += item.f55to59
                dto.male6064 += item.m60to64
                dto.female6064 += item.f60to64
                dto.male6569 += item.m65to69
                dto.female6569 += item.f65to69
                dto.male70Over += item.m70over ?: 0
                dto.female70Over += item.f70over ?: 0
                dto.maleSubtotal += item.msubtotal
                dto.femaleSubtotal += item.fsubtotal
        }

        dto.grandTotal = dto.maleSubtotal + dto.femaleSubtotal


        return dto
    }

    @GraphQLQuery(name = "count_all_testing")
    List<DohTestingDto> getAllTesting(@GraphQLArgument(name = "year") Integer year) {
        def resultList = jdbcTemplate.queryForList(""" SELECT d.parent_department AS id,
    text_get_department_desc(d.parent_department) AS department,
    count(*) AS total
   FROM ancillary.orderslip_item o
     LEFT JOIN ancillary.services s ON o.service = s.id
     JOIN departments d ON s.department = d.id
  WHERE o.status::text = 'COMPLETED'::text and date_part('year', o.last_modified_date) = ${year} and d.parent_department is not null
  GROUP BY d.parent_department order by total desc;""")
        def list = new ArrayList<DohTestingDto>()

        if (resultList) {
            list = resultList.stream().map {
                it ->
                    DohTestingDto dto = new DohTestingDto()
                    dto.id = it.id
                    dto.department_desc = it.department
                    dto.total = it.total
                    return dto
            }.collect(Collectors.toList())

        }

        return list
    }

    @GraphQLQuery(name = "count_all_surgical_procedure")
    List<Map<String, Object>> getAllSurgicalProcedure(@GraphQLArgument(name = "year") Integer year) {
        def resultList = jdbcTemplate.queryForList("""SELECT * FROM pms.surgical_procedure_by_age_view where reporting_year = ${year}""")
        //def list = new ArrayList<Map<String, Object>>()

//        if(resultList){
//            list = resultList.stream().map {
//                it ->
//                    def dto = [:]
//                    dto.icdcode = it.icdcode
//                    dto.longname = it.longname
//                    dto.total = it.total
//                    return dto
//            }.collect(Collectors.toList())
//
//        }

        return resultList
    }

    @GraphQLQuery(name = "count_all_death_by_type")
    List<Map<String, Object>> getAllDeathByType(@GraphQLArgument(name = "year") Integer year) {
        def resultList = jdbcTemplate.queryForList("""SELECT death_type, count(*) as total FROM pms.cases where death_type is not null and date_part('year', discharged_datetime) = ${year} group by death_type;""")

        return resultList
    }

    @GraphQLQuery(name = "count_doa")
    Integer countDoa() {
        def resultList = jdbcTemplate.queryForObject("""SELECT count(*) as total FROM pms.cases where is_dead_on_arrival = true ;""", Integer)

        return resultList
    }


    @GraphQLQuery(name = "countEmpByPosition")
    List<Map<String, Object>> countEmpByPosition() {
        return jdbcTemplate.queryForList("""SELECT upper(position_type) as positiondesc,
                position_code as professiondesignation,
                
                count(*) filter(where employee_type = 'PERMANENT - FULLTIME') as fulltime40Permanent,
                count(*) filter(where employee_type = 'CONTRACTUAL - FULLTIME') as fulltime40Contractual,
                count(*) filter(where employee_type = 'PERMANENT - PARTTIME') as parttimepermanent ,
                count(*) filter(where employee_type = 'CONTRACTUAL - PARTTIME') as parttimecontractual ,
                count(*) filter(where employee_type = 'ACTIVE ROTATING') as activerotatingaffiliate ,
                count(*) filter(where employee_type = 'OUTSOURCED') as outsourced,
                count(*) filter(where is_specialty_board_certified = true) as specialtyboardcertified 
                FROM hrm.employees where is_active = true and position_code is not null and position_type is not null group by professiondesignation, positiondesc order by fulltime40permanent desc;""")

    }

    @GraphQLQuery(name = "countEmpByPositionOthers")
    List<Map<String, Object>> countEmpByPositionOthers() {
        return jdbcTemplate.queryForList("""SELECT upper(position_type) as positiondesc,
                position_code_others as professiondesignation,
                
                count(*) filter(where employee_type = 'PERMANENT - FULLTIME') as fulltime40Permanent,
                count(*) filter(where employee_type = 'CONTRACTUAL - FULLTIME') as fulltime40Contractual,
                count(*) filter(where employee_type = 'PERMANENT - PARTTIME') as parttimepermanent ,
                count(*) filter(where employee_type = 'CONTRACTUAL - PARTTIME') as parttimecontractual ,
                count(*) filter(where employee_type = 'ACTIVE ROTATING') as activerotatingaffiliate ,
                count(*) filter(where employee_type = 'OUTSOURCED') as outsourced,
                count(*) filter(where is_specialty_board_certified = true) as specialtyboardcertified 
                FROM hrm.employees where is_active = true and position_code_others is not NULL  group by professiondesignation, positiondesc order by fulltime40permanent desc""")
    }


    @GraphQLQuery(name = "count_patient_reports")
    List<Map<String, Object>> countOutpatientReporst(@GraphQLArgument(name = 'registryType') String registryType, @GraphQLArgument(name = 'year') Integer year) {
        return jdbcTemplate.queryForList("""WITH count_cases as (
 SELECT  patient, count(*) from pms.cases group by patient
), 
patient_age AS (
         SELECT p_1.id,
            p_1.gender,
            date_part('year'::text, age(p_1.dob::timestamp with time zone))::integer AS page
           FROM pms.patients p_1
        )
SELECT  
count(*) filter (where p.count = 1) as new_patient ,
count(*) filter (where p.count > 1) as revisits,
count(*) filter (where pAge > 18) as adults,
count(*) filter (where pAge <= 18) as pediatrics,
count(*) filter (where c.icd_diagnosis is not null and pAge > 18 ) as gnrl_meds,
count(*) filter (where c.rvs_diagnosis is null ) as non_surgical,
count(*) filter (where c.rvs_diagnosis is not null ) as surgical,
count(*) filter (where c.is_antenatal = true) as antenatal,
count(*) filter (where c.is_postnatal = true) as postnatal,
count(*) filter (where c.discharge_disposition = 'TRANSFERRED') as transferred,
count(*) as total
from pms.cases c left join count_cases p on c.patient = p.patient left join patient_age pAge on c.patient = pAge.id
where c.registry_type = '${registryType}' and date_part('year', c.entry_datetime) = ${year}""")

    }

    @GraphQLQuery(name = "count_deliveries")
    List<Map<String, Object>> countDeliveries(@GraphQLArgument(name = 'year') Integer year) {
        return jdbcTemplate.queryForList("""SELECT d.*, (count_normal_delivery + count_cesarean_delivery) as total_deliveries FROM (
SELECT 
count(*) filter (where c.is_infacility_delivery = true) as count_in_patient_delivery, 
count(*) filter (where upper(diagnosis ->> 'rvsCode') = '59409') as count_normal_delivery,
count(*) filter (where upper(diagnosis ->> 'rvsCode') = '59514') as count_cesarean_delivery
FROM pms.cases c,
	LATERAL json_array_elements(
        CASE
            WHEN pms.is_json(c.rvs_diagnosis::character varying) AND c.rvs_diagnosis IS NOT NULL THEN c.rvs_diagnosis::json
            ELSE NULL::json
        END) as diagnosis
	where date_part('year', entry_datetime) = ${year}
	
	
) as d""")

    }


    @GraphQLQuery(name = "getCountDeliveries")

    DeliveriesDto getCountDeliveries(@GraphQLArgument(name = 'year') Integer year) {
        DeliveriesDto dResult = jdbcTemplate.queryForObject(""" SELECT 
            count(*) FILTER (WHERE d.value ->>'deliveryType' in ('CAESARIAN','OTHER','VAGINAL')) AS Infacility,
            count(*) FILTER (WHERE d.value ->>'deliveryType' = 'CAESARIAN') AS Caesarian,
            count(*) FILTER (WHERE d.value ->>'deliveryType' = 'OTHER') AS Other,
            count(*) FILTER (WHERE d.value ->>'deliveryType' = 'VAGINAL') AS Vaginal
            FROM pms.cases c,
            LATERAL json_array_elements(
                CASE
                    WHEN (pms.is_json((c.doh_icd_diagnosis)::character varying) AND (c.doh_icd_diagnosis IS NOT NULL)) THEN (c.doh_icd_diagnosis)::json
                    ELSE NULL::json
                END) d(value)
           WHERE d.value ->>'deliveryType' is not null and date_part('year'::text, c.entry_datetime) = ${year} 
        """, new BeanPropertyRowMapper(DeliveriesDto.class))

        return dResult
    }


    @GraphQLQuery(name = "count_deaths_v2")
    List<Map<String, Object>> countDeathV2(@GraphQLArgument(name = 'year') Integer year) {
        return jdbcTemplate.queryForList("""WITH patient_age AS (
         SELECT p_1.id,
            p_1.gender,
            date_part('year'::text, age(p_1.dob::timestamp with time zone))::integer AS page
           FROM pms.patients p_1
        )
  
select  
count(*) filter (where pms.double_get_hours(coalesce(c.admission_datetime, c.entry_datetime) + '8:00:00'::interval, c.time_of_death  + '8:00:00'::interval ) > 48) as greater_than_48hrs,
count(*) filter (where pms.double_get_hours(coalesce(c.admission_datetime, c.entry_datetime) + '8:00:00'::interval, c.time_of_death  + '8:00:00'::interval ) < 48) as less_than_48hrs,
count(*) filter(where c.discharged_datetime is null) as no_discharge_date,
count(*) filter(where c.admission_datetime is null) as no_admission_date,
count(*) filter(where c.entry_datetime is null) as no_entry_datetime,
count(*) filter (where c.registry_type = 'ERD') as er_deaths,
count(*) filter (where c.is_dead_on_arrival = true) as dod,
count(*) filter (where c.death_type = 'NEONATAL') as neonatal,
count(*) filter (where c.death_type = 'MATERNAL') as maternal,
count(*) as total
from pms.cases c left join patient_age p on c.patient = p.id where date_part('year', c.time_of_death) = ${year}""")
    }


    static BigDecimal getDOHNetDeathRate(BigInteger totalDeaths, BigInteger less48Hours, BigInteger totalDischarged) {
        DecimalFormat roundOff = new DecimalFormat("0.00")
        BigDecimal dohNetDeathRate = 0.00

        BigInteger subtractDeathsAndLessHours = totalDeaths - less48Hours
        BigInteger subtractDischargedAndLessHours = totalDischarged - less48Hours
        if (subtractDeathsAndLessHours > 0 && subtractDischargedAndLessHours > 0) {
            BigDecimal divide = subtractDeathsAndLessHours / subtractDischargedAndLessHours
            dohNetDeathRate = roundOff.format(divide * 100) as BigDecimal
        }
        return dohNetDeathRate

    }

    static BigDecimal getDOHGrossDeathRate(BigInteger totalDeaths, BigInteger totalDischarged) {
        DecimalFormat roundOff = new DecimalFormat("0.00")
        BigDecimal dohGrossDeathRate = 0.00

        if (totalDeaths > 0 && totalDischarged > 0)
            dohGrossDeathRate = roundOff.format((totalDeaths / totalDischarged) * 100) as BigDecimal

        return dohGrossDeathRate
    }


    @GraphQLQuery(name = "count_deaths_v3")
    DohDeathsPageDto countDeathV3(@GraphQLArgument(name = 'year') Integer year) {
        DohDeathsPageDto deathsPageDto = new DohDeathsPageDto()
        def result = jdbcTemplate.query("""
         WITH patient_age AS (  
                    SELECT p_1.id,
            p_1.gender,
            date_part('year'::text, age(p_1.dob::timestamp with time zone))::integer AS page
           FROM pms.patients p_1
        )
        select 
        count(*) filter 
                (
                where 
                pms.double_get_hours(coalesce(c.admission_datetime, c.entry_datetime) + '8:00:00'::interval, c.time_of_death  + '8:00:00'::interval ) >= 48 
                and c.registry_type = 'IPD' 
                and c.time_of_death is not null
        ) as totalDeathGreaterThanEqualTo48,
                count(*) filter 
                (
                where 
                pms.double_get_hours(coalesce(c.admission_datetime, c.entry_datetime) + '8:00:00'::interval, c.time_of_death  + '8:00:00'::interval ) < 48 
                and c.registry_type = 'IPD' 
                and c.time_of_death is not null
        ) as totalDeathLessThan48,
                count(*) filter(where c.discharged_datetime is null) as no_discharge_date,
                count(*) filter(where c.admission_datetime is null) as no_admission_date,
                count(*) filter(where c.entry_datetime is null) as no_entry_datetime,
                count(*) filter (where c.death_expiration = 'ER DEATH' and c.registry_type != 'IPD') as emergencyRoomDeaths,
                count(*) filter (where c.death_expiration = 'DEAD ON ARRIVAL' and c.registry_type  != 'IPD') as deadOnArrivalDeaths,
                count(*) filter (where c.death_type = 'NEONATAL') as neonatalDeaths,
                count(*) filter (where c.death_type = 'MATERNAL') as maternalDeaths,
                count(*) filter (where c.death_type = 'STILLBIRTH') as stillbirthsDeaths,
                 count(*) as totalDeaths
        from pms.cases c
        left join patient_age p on c.patient = p.id 
        where 
        c.doh_icd_diagnosis is not null
        and 
         date_part('year', c.discharged_datetime + INTERVAL '8h') = ? ::DOUBLE PRECISION
        and c.discharge_condition = 'EXPIRED'
        and c.registry_type in ('IPD', 'ERD')
        and c.service_type is not null
        and c.may_go_home_datetime is not null
        and c.death_expiration is not null
        and (discharge_disposition in ('DAMA/HAMA','TRANSFERRED','ABSCONDED','DISCHARGED')) 
        ;""", new BeanPropertyRowMapper(DohDeathsDto.class),
                year
        )

        if (result) {
            deathsPageDto.deathsDto = result[0] as DohDeathsDto
            BigInteger totalDischarged = bedCapacityServices.getTotalDischarged(year)

            TotalDeathsConfig totalDeathsConfig = dohConfigurationService.getDohConfig().totalDeathsConfig
            if (totalDeathsConfig.includeStillBirths) deathsPageDto.deathsDto.totalDeaths += deathsPageDto.deathsDto.stillbirthsDeaths
            if (totalDeathsConfig.includeNeonatal) deathsPageDto.deathsDto.totalDeaths += deathsPageDto.deathsDto.neonatalDeaths
            if (totalDeathsConfig.includeMaternal) deathsPageDto.deathsDto.totalDeaths += deathsPageDto.deathsDto.maternalDeaths

            BigInteger totalDeaths = deathsPageDto.deathsDto.totalDeaths

            BigInteger less48Hours = deathsPageDto.deathsDto.totalDeathLessThan48

            deathsPageDto.totalDischarged = totalDischarged
            deathsPageDto.netDeathRate = getDOHNetDeathRate(totalDeaths, less48Hours, totalDischarged)
            deathsPageDto.grossDeathRate = getDOHGrossDeathRate(totalDeaths, totalDischarged)

        }

        return deathsPageDto
    }


    @GraphQLQuery(name = "count_inpt_care")
    List<Map<String, Object>> countInptCare(@GraphQLArgument(name = 'year') Integer year) {
        return jdbcTemplate.queryForList("""
                    SELECT 
                    count(*) as inpatient, 
                    count(*) filter (where discharge_disposition = 'DISCHARGED' and discharge_condition != 'EXPIRED') as discharges_alive,
                    count(*) filter (where to_char(admission_datetime + INTERVAL '8h', 'MMDDYYYY') = to_char(discharged_datetime + INTERVAL '8h', 'MMDDYYYY')) as same_day_dc,
                    count(*) filter (where discharge_disposition = 'TRANSFERRED') as inpt_transferred,
                    count(*) filter (where previous_admission = 'OTHER/TRANSFEREE') as inpt_trasferee,
                    count(*) filter (where date_part('year', discharged_datetime + INTERVAL '8h') = ${year + 1}) as remaining
                    FROM pms.cases where registry_type = 'IPD' and date_part('year', admission_datetime + INTERVAL '8h') = ${year};
  
        """)
    }

    @GraphQLQuery(name = "count_discharges")
    List<Map<String, Object>> countDischarges(@GraphQLArgument(name = 'year') Integer year) {
        return jdbcTemplate.queryForList("""
                                    select 
                                    upper(service_type) as service_type, 
                                    count(*) as patients,
                                    sum(to_char((discharged_datetime + INTERVAL '8h'), 'YYYY-MM-DD')::date - (admission_datetime + INTERVAL '8h')::date) as total_days,
                                    count(*) filter(where accommodation_type = 'SELF') as non_phic_self,
                                    count(*) filter(where accommodation_type = 'SERVICE CHARITY') as non_phic_charity,
                                    count(*) filter(where accommodation_type = 'NHIP/MEMBER') as nhip_member,
                                    count(*) filter(where accommodation_type = 'NHIP/DEPENDENT') as nhip_dependent,
                                    count(*) filter(where accommodation_type = 'NHIP/DEPENDENT' OR accommodation_type = 'NHIP/MEMBER') as nhip,
                                    count(*) filter(where accommodation_type = 'NHIP/DEPENDENT' OR accommodation_type = 'NHIP/MEMBER'  OR accommodation_type = 'INDIGENT') as nhip_total,
                                    count(*) filter(where accommodation_type = 'OWWA') as owwa,
                                    count(*) filter(where accommodation_type = 'INDIGENT') as indigent,
                                    count(*) filter(where accommodation_type = 'HMO' or accommodation_type = 'CORPORATE') as hmo,
                                    count(*) filter(where accommodation_type = 'CORPORATE') as corporate,
                                    count(*) filter(where discharge_condition = 'IMPROVED' or discharge_condition = 'RECOVERED') as ri,
                                    count(*) filter (where discharge_disposition = 'TRANSFERRED') as tranferred,
                                    count(*) filter (where discharge_disposition = 'DAMA/HAMA') as hama,
                                    count(*) filter (where discharge_disposition = 'ABSCONDED') as absconded,
                                    count(*) filter (where discharge_condition = 'UNIMPROVED') as unimproved,
                                    count(*) filter (where pms.double_get_hours(coalesce(admission_datetime, entry_datetime) + '8:00:00'::interval, time_of_death  + '8:00:00'::interval ) > 48 and discharge_condition = 'EXPIRED') as greater_than_48hrs,
                                    count(*) filter (where pms.double_get_hours(coalesce(admission_datetime, entry_datetime) + '8:00:00'::interval, time_of_death  + '8:00:00'::interval ) < 48  and discharge_condition = 'EXPIRED') as less_than_48hrs,
                                    count(*) filter (where discharge_disposition = 'DISCHARGED' or discharge_disposition = 'DAMA/HAMA' or discharge_disposition = 'TRANSFERRED' or discharged_datetime <> null) as total_discharges
                                    from pms.cases 
                                    where date_part('year', discharged_datetime + INTERVAL '8h') = ${year} 
                                    group by upper(service_type)
                                    order by upper(service_type) asc
                                """)
    }

    @GraphQLQuery(name = "dohProgressLogs")
    List<DOHProgressLogs> dohProgressLogs(
            @GraphQLArgument(name = 'year') Integer year
    ) {
        return entityManager.createNativeQuery("""
            with doh_reports As 
                (
                select * from 
                json_to_recordset('
                [
        {
          "no": 1,
          "description": "Classification",
          "code": "GEN_INFO_CLASSIFICATION",
          "group": "I. General Information",
          "group_no": 1
        },
        {
          "no": 2,
          "description": "Quality Management",
          "code": "GEN_INFO_QUALITY_MANAGEMENT",
          "group": "I. General Information",
          "group_no": 1
        },
        {
          "no": 3,
          "description": "Bed Capacity / Occupancy",
          "code": "BED_CAPACITY",
          "group": "I. General Information",
          "group_no": 1
        },
        {
          "no": 1,
          "description": "Summary of Patients in the Hospital",
          "code": "SUMMARY_PATIENT",
          "group": "II. Hospital Operations",
          "group_no": 2
        },
        {
          "no": 2,
          "description": "Type of Service and Total Discharges According to Specialty",
          "code": "DISCHARGES_SPECIALTY",
          "group": "II. Hospital Operations",
          "group_no": 2
        },
        {
          "no": 3,
          "description": "Total Number of Deliveries",
          "code": "DELIVERIES",
          "group": "II. Hospital Operations",
          "group_no": 2
        },
        {
          "no": 4,
          "description": "Outpatient Visits",
          "code": "DISCHARGE_OPV",
          "group": "II. Hospital Operations",
          "group_no": 2
        },
        {
          "no": 5,
          "description": "Ten Leading OPD Consultations",
          "code": "DISCHARGES_OPD",
          "group": "II. Hospital Operations",
          "group_no": 2
        },
        {
          "no": 6,
          "description": "Ten Leading ER Consultations",
          "code": "DISCHARGES_ER",
          "group": "II. Hospital Operations",
          "group_no": 2
        },
        {
          "no": 7,
          "description": "Testing and Other Services",
          "code": "TESTING",
          "group": "II. Hospital Operations",
          "group_no": 2
        },
        {
          "no": 8,
          "description": "Emergency Visits",
          "code": "DISCHARGES_EV",
          "group": "II. Hospital Operations",
          "group_no": 2
        },
        {
          "no": 9,
          "description": "Total Number of Deaths",
          "code": "DEATHS",
          "group": "II. Hospital Operations",
          "group_no": 2
        },
        {
          "no": 10,
          "description": "Ten Leading causes of Mortality/Deaths Disaggregated as to Age and Sex",
          "code": "DISCHARGES_MORTALITY",
          "group": "II. Hospital Operations",
          "group_no": 2
        },
        {
          "no": 11,
          "description": "Healthcare Associated Infections (HAI)",
          "code": "INFECTIONS",
          "group": "II. Hospital Operations",
          "group_no": 2
        },
        {
          "no": 12,
          "description": "Ten Leading Major Operations (Not Applicable for Infirmary)",
          "code": "MAJOR_OPERATIONS",
          "group": "II. Hospital Operations",
          "group_no": 2
        },
        {
          "no": 13,
          "description": "Ten Leading Minor Operations",
          "code": "MINOR_OPERATIONS",
          "group": "II. Hospital Operations",
          "group_no": 2
        },
        {
          "no": 1,
          "description": "Staffing Pattern",
          "code": "STAFFING_PATTERN",
          "group": "III. Staffing Pattern",
          "group_no": 3
        },
        {
          "no": 1,
          "description": "Expenses",
          "code": "EXPENSES",
          "group": "IV. Expenses",
          "group_no": 4
        },
        {
          "no": 1,
          "description": "Revenues",
          "code": "REVENUES",
          "group": "V. Revenues",
          "group_no": 5
        }
    ]') as x(no int,description varchar, code varchar, "group" varchar, "group_no" int)
                )
               select 
               "group" as "description",
               "groupNo" as "key",
               cast(
               case 
                 when "groupNo" not in ('3','4','5') then
                   jsonb_agg(jsonb_build_object('no',"no",'description',"description",'success',success,'logs',logs)) 
               else
                 '[]'
               end
               as 
               text
               ) as items,
               cast(
               case 
                 when "groupNo" in ('3','4','5') then
               cast(jsonb_agg(success)->>0 as int)
                 else 
                 0
                end as int) as success,
               cast(
               case 
                 when "groupNo" in ('3','4','5') then
                 jsonb_agg(logs)->>0
                 else 
                 '[]'
                end
                 as
                 text) as logs
               from
               (
               select 
                dr.description,dr."no",dr.code,dr."group",dr.group_no as "groupNo",
                cast(count(dl.id) filter (where dl.status = 'Success') as varchar) as success,
                cast(case 
                when dl."type" is not null then jsonb_agg(jsonb_build_object('id',dl.id,'description',concat('Submitted on ',to_char(dl.created_date + interval '8hr' ,'YYYY-MM-DD HH:MI:ss AM')),'status',dl.status) order by dl.created_date desc)
                else  '[]'
                end as text) as logs 
                from doh_reports dr
                left join doh.doh_logs dl on dl."type" = dr.code and dl.reporting_year = :year
                group by dl."type",dr.description,dr."no",dr.code,dr."group",dr.group_no
                order by dr.group_no,dr.no
                ) as doh_reports
                group by "group","groupNo"
                order by "groupNo"
        """)
                .setParameter('year', year)
                .unwrap(NativeQuery.class)
                .setResultTransformer(Transformers.aliasToBean(DOHProgressLogs.class))
                .getResultList();
    }


    @GraphQLQuery(name = "getDOHProgressRate")
    Map<String, Object> getDOHProgressRate(
            @GraphQLArgument(name = "year") Integer year
    ) {
        Map<String, Object> result = [:]
        Long submittedReports = jdbcTemplate.queryForObject("""
           with doh_reports As 
                (
                select * from 
                json_to_recordset('
                [
        {
          "no": 1,
          "description": "Classification",
          "code": "GEN_INFO_CLASSIFICATION",
          "group": "I. General Information",
          "group_no": 1
        },
        {
          "no": 2,
          "description": "Quality Management",
          "code": "GEN_INFO_QUALITY_MANAGEMENT",
          "group": "I. General Information",
          "group_no": 1
        },
        {
          "no": 3,
          "description": "Bed Capacity / Occupancy",
          "code": "BED_CAPACITY",
          "group": "I. General Information",
          "group_no": 1
        },
        {
          "no": 1,
          "description": "Summary of Patients in the Hospital",
          "code": "SUMMARY_PATIENT",
          "group": "II. Hospital Operations",
          "group_no": 2
        },
        {
          "no": 2,
          "description": "Type of Service and Total Discharges According to Specialty",
          "code": "DISCHARGES_SPECIALTY",
          "group": "II. Hospital Operations",
          "group_no": 2
        },
        {
          "no":3,
          "description":"Ten Leading causes of Morbidity/Diseases Disaggregated as to Age and Sex",
          "code": "DISCHARGES_MORBIDITY",
          "group": "II. Hospital Operations",
          "group_no": 2
        },
        {
          "no":4,
          "description": "Total Number of Deliveries",
          "code": "DELIVERIES",
          "group": "II. Hospital Operations",
          "group_no": 2
        },
        {
          "no": 5,
          "description": "Outpatient Visits",
          "code": "DISCHARGE_OPV",
          "group": "II. Hospital Operations",
          "group_no": 2
        },
        {
          "no": 6,
          "description": "Ten Leading OPD Consultations",
          "code": "DISCHARGES_OPD",
          "group": "II. Hospital Operations",
          "group_no": 2
        },
        {
          "no": 7,
          "description": "Ten Leading ER Consultations",
          "code": "DISCHARGES_ER",
          "group": "II. Hospital Operations",
          "group_no": 2
        },
        {
          "no": 8,
          "description": "Testing and Other Services",
          "code": "TESTING",
          "group": "II. Hospital Operations",
          "group_no": 2
        },
        {
          "no": 9,
          "description": "Emergency Visits",
          "code": "DISCHARGES_EV",
          "group": "II. Hospital Operations",
          "group_no": 2
        },
        {
          "no": 10,
          "description": "Total Number of Deaths",
          "code": "DEATHS",
          "group": "II. Hospital Operations",
          "group_no": 2
        },
        {
          "no": 11,
          "description": "Ten Leading causes of Mortality/Deaths Disaggregated as to Age and Sex",
          "code": "DISCHARGES_MORTALITY",
          "group": "II. Hospital Operations",
          "group_no": 2
        },
        {
          "no": 12,
          "description": "Healthcare Associated Infections (HAI)",
          "code": "INFECTIONS",
          "group": "II. Hospital Operations",
          "group_no": 2
        },
        {
          "no": 13,
          "description": "Ten Leading Major Operations (Not Applicable for Infirmary)",
          "code": "MAJOR_OPERATIONS",
          "group": "II. Hospital Operations",
          "group_no": 2
        },
        {
          "no": 14,
          "description": "Ten Leading Minor Operations",
          "code": "MINOR_OPERATIONS",
          "group": "II. Hospital Operations",
          "group_no": 2
        },
        {
          "no": 1,
          "description": "Staffing Pattern",
          "code": "STAFFING_PATTERN",
          "group": "III. Staffing Pattern",
          "group_no": 3
        },
        {
          "no": 1,
          "description": "Expenses",
          "code": "EXPENSES",
          "group": "IV. Expenses",
          "group_no": 4
        },
        {
          "no": 1,
          "description": "Revenues",
          "code": "REVENUES",
          "group": "V. Revenues",
          "group_no": 5
        }
    ]') as x(no int,description varchar, code varchar, "group" varchar, "group_no" int)
                )
               select 
               count(*)
               from
               (
               select 
                count(dl.id) filter (where dl.status = 'Success') as success
                from doh_reports dr
                left join doh.doh_logs dl on dl."type" = dr.code and dl.reporting_year = ? ::DOUBLE PRECISION
                group by dl."type",dr.description,dr."no",dr.code,dr."group",dr.group_no
                order by dr.group_no,dr.no
                ) as doh_reports
                where success > 0
        """, Long, year)

        Long totalNumberOfReports = 19
        BigDecimal quotient = (submittedReports / totalNumberOfReports).setScale(2, RoundingMode.HALF_EVEN)
        BigDecimal percentage = quotient * 100
        result['percentage'] = percentage
        result['submittedReports'] = submittedReports
        result['totalNumberOfReports'] = totalNumberOfReports

        return result
    }


    @GraphQLMutation(name = "postDOHSubmittedReport")
    GraphQLRetVal<Boolean> postDOHSubmittedReport(@GraphQLArgument(name = 'year') Integer year) {

        try {
            def reportedBy = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()
            def result = dohAPIService.submittedReports(

                    'S',
                    reportedBy.fullName,
                    reportedBy.positionDesignation ? reportedBy.positionDesignation : 'n/a',
                    'n/a',
                    reportedBy.departmentOfDuty.departmentName ? reportedBy.departmentOfDuty.departmentName : 'n/a',
                    year.toString(),
            )

            return new GraphQLRetVal<Boolean>(true, true, 'Success')
        } catch (Exception e) {
            return new GraphQLRetVal<Boolean>(false, false, e.message)
        }
    }


}
