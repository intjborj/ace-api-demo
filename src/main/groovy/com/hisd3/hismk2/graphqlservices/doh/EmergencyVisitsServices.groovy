package com.hisd3.hismk2.graphqlservices.doh

import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
@GraphQLApi
class EmergencyVisitsServices {


    @Autowired
    JdbcTemplate jdbcTemplate



    @GraphQLQuery(name="getERDTotalNumberOfPatientsVisitsPerYear")
    Long getERDTotalNumberOfPatientsVisitsPerYear(
            @GraphQLArgument(name="year") Integer year
    ){
        return jdbcTemplate.queryForObject("""
                select
                count(*)
                    from
                        (
                           select 
                            c.id
                            from pms.cases c 
                            left join pms.transfers t on t."case" = c.id 
                            left join pms.patients p on p.id = c.patient,
                            LATERAL json_array_elements(
                            CASE 
                                WHEN pms.is_json(c.doh_icd_diagnosis ::character varying) AND c.doh_icd_diagnosis IS NOT NULL THEN c.doh_icd_diagnosis::json
                            ELSE NULL::json
                                END) d(value)
                            LEFT JOIN referential.doh_icd_codes dic on dic.icd10_code = d.value ->> 'icdCode'
                            WHERE 
                            d.value ->> 'icdCode' is not null and 
                            (case 
                            when c.discharge_condition = 'EXPIRED' then 
                            d.value ->> 'causeOfDeath' = 'UNDERLYING' 
                            else 
                            coalesce(cast(d.value ->> 'isFinalDiagnosis' as Boolean),false) = true 
                            end) = true
                            and 
                            t.registry_type = 'ERD'
                            and 
                            date_part('year'::text, c.entry_datetime + interval '8 hour') = ? ::DOUBLE PRECISION
                            and p.dob is not null
                       --     and
                        --    c.urgency = 'EMERGENCY'
                            group by c.id
                       ) as cases
                """,
                Long,
                year
        )
    }

    @GraphQLQuery(name="getERDTotalNumberOfPatientsAdultPerYear")
    Long getERDTotalNumberOfPatientsAdultPerYear(
            @GraphQLArgument(name="year") Integer year
    ){
        return jdbcTemplate.queryForObject("""
                select
                count(*)
                from
                (
                    select
                    c.id
                            from pms.cases c
                    left join pms.patients p on p.id = c.patient
                    left join pms.transfers t on t."case" = c.id,
                    LATERAL json_array_elements(
                    CASE 
                        WHEN pms.is_json(c.doh_icd_diagnosis ::character varying) AND c.doh_icd_diagnosis IS NOT NULL THEN c.doh_icd_diagnosis::json
                    ELSE NULL::json
                        END) d(value)
                    LEFT JOIN referential.doh_icd_codes dic on dic.icd10_code = d.value ->> 'icdCode'
                    WHERE 
                    d.value ->> 'icdCode' is not null and 
                    (case 
                    when c.discharge_condition = 'EXPIRED' then 
                    d.value ->> 'causeOfDeath' = 'UNDERLYING' 
                    else 
                    coalesce(cast(d.value ->> 'isFinalDiagnosis' as Boolean),false) = true 
                    end) = true
                    and
                    t.registry_type = 'ERD'
                    and
                    date_part('year'::text, c.entry_datetime + interval '8 hour') = ?::DOUBLE PRECISION
                 --   and
                  --  c.urgency = 'EMERGENCY'
                    and date_part('year'::text, age(date(c.entry_datetime  + interval '8 hour'),date(p.dob  + interval '8 hour')))::integer >= 19
                    group by c.id
                ) as cases
                """,
                Long,
                year
        )
    }

    @GraphQLQuery(name="getERDTotalNumberOfPatientsPediatricPerYear")
    Long getERDTotalNumberOfPatientsPediatricPerYear(
            @GraphQLArgument(name="year") Integer year
    ){
        return jdbcTemplate.queryForObject("""
                select
                count(*)
                from
                (
                    select
                    c.id
                            from pms.cases c
                    left join pms.patients p on p.id = c.patient
                    left join pms.transfers t on t."case" = c.id,
                    LATERAL json_array_elements(
                    CASE 
                        WHEN pms.is_json(c.doh_icd_diagnosis ::character varying) AND c.doh_icd_diagnosis IS NOT NULL THEN c.doh_icd_diagnosis::json
                    ELSE NULL::json
                        END) d(value)
                    LEFT JOIN referential.doh_icd_codes dic on dic.icd10_code = d.value ->> 'icdCode'
                    WHERE 
                    d.value ->> 'icdCode' is not null and 
                    (case 
                    when c.discharge_condition = 'EXPIRED' then 
                    d.value ->> 'causeOfDeath' = 'UNDERLYING' 
                    else 
                    coalesce(cast(d.value ->> 'isFinalDiagnosis' as Boolean),false) = true 
                    end) = true
                    and
                    t.registry_type = 'ERD'
                    and
                    date_part('year'::text, c.entry_datetime + interval '8 hour') = ?::DOUBLE PRECISION
                  --  and
                  --  c.urgency = 'EMERGENCY'
                    and date_part('year'::text, age(date(c.entry_datetime  + interval '8 hour'),date(p.dob  + interval '8 hour')))::integer < 19
                    group by c.id
                ) as cases
                """,
                Long,
                year
        )
    }

    @GraphQLQuery(name="getERDTotalNumOfPatientsTransferredToOtherPerYear")
    Long getERDTotalNumOfPatientsTransferredToOtherPerYear(
            @GraphQLArgument(name="year") Integer year
    ){
        return jdbcTemplate.queryForObject("""
                select
                count(*)
                from
                (
                    select
                    c.id
                    from pms.cases c
                    left join pms.transfers t on t."case" = c.id,
                    LATERAL json_array_elements(
                    CASE 
                        WHEN pms.is_json(c.doh_icd_diagnosis ::character varying) AND c.doh_icd_diagnosis IS NOT NULL THEN c.doh_icd_diagnosis::json
                    ELSE NULL::json
                        END) d(value)
                    LEFT JOIN referential.doh_icd_codes dic on dic.icd10_code = d.value ->> 'icdCode'
                    WHERE 
                    d.value ->> 'icdCode' is not null and 
                    (case 
                    when c.discharge_condition = 'EXPIRED' then 
                    d.value ->> 'causeOfDeath' = 'UNDERLYING' 
                    else 
                    coalesce(cast(d.value ->> 'isFinalDiagnosis' as Boolean),false) = true 
                    end) = true
                    and
                    t.registry_type = 'ERD'
                    and
                 --   c.urgency = 'EMERGENCY'
                  --  and
                    c.discharge_disposition = 'TRANSFERRED'
                    and
                    date_part('year'::text, c.entry_datetime + interval '8 hour') = ?::DOUBLE PRECISION
                    group by c.id
                ) as cases
                """,
                Long,
                year
        )
    }


    @GraphQLQuery(name="getERDTotalNumOfPatientsTransferredFromOtherPerYear")
    Long getERDTotalNumOfPatientsTransferredFromOtherPerYear(
            @GraphQLArgument(name="year") Integer year
    ){
        return jdbcTemplate.queryForObject("""
                select
                    count(*)
                from pms.cases c,
                LATERAL json_array_elements(
                CASE 
                    WHEN pms.is_json(c.doh_icd_diagnosis ::character varying) AND c.doh_icd_diagnosis IS NOT NULL THEN c.doh_icd_diagnosis::json
                ELSE NULL::json
                    END) d(value)
                LEFT JOIN referential.doh_icd_codes dic on dic.icd10_code = d.value ->> 'icdCode'
                WHERE 
                d.value ->> 'icdCode' is not null and 
                (case 
                when c.discharge_condition = 'EXPIRED' then 
                d.value ->> 'causeOfDeath' = 'UNDERLYING' 
                else 
                coalesce(cast(d.value ->> 'isFinalDiagnosis' as Boolean),false) = true 
                end) = true
                and
                    c.patient_transfer is true
                    and
                    c.reason_for_transfer_in is not null
                    and
                    trim(c.reason_for_transfer_in) != ''
                    and
                    c.originating_hci is not null
                    and
                    date_part('year'::text, c.entry_datetime + interval '8 hour') = ?::DOUBLE PRECISION
                """,
                Long,
                year
        )
    }

    @GraphQLQuery(name="getERDVisitsReport")
    List<Map<String,Object>> getERDVisitsReport(@GraphQLArgument(name = "year") Integer year){
        List<Map<String,Object>> report = []

        Map<String,Object> visits = [:]
        visits['key'] = 0
        visits['label'] = "Total number of emergency department visits"
        visits['value'] = getERDTotalNumberOfPatientsVisitsPerYear(year)
        report.push(visits)

        Map<String,Object> reVisits = [:]
        reVisits['key'] = 1
        reVisits['label'] = "Total number of emergency department visits, adult"
        reVisits['value'] = getERDTotalNumberOfPatientsAdultPerYear(year)
        report.push(reVisits)

        Map<String,Object> adult = [:]
        adult['key'] = 2
        adult['label'] = "Total number of emergency department visits, pediatric"
        adult['value'] = getERDTotalNumberOfPatientsPediatricPerYear(year)
        report.push(adult)

        Map<String,Object> pediatric = [:]
        pediatric['key'] = 3
        pediatric['label'] = "Total number of patients transported TO THIS FACILITY'S EMERGENCY DEPARTMENT from other health facilities"
        pediatric['value'] = getERDTotalNumOfPatientsTransferredFromOtherPerYear(year)
        report.push(pediatric)

        Map<String,Object> generalMedicine = [:]
        generalMedicine['key'] = 4
        generalMedicine['label'] = "Total number of patients transported FROM THIS FACILITY'S EMERGENCY DEPARTMENT to another facility for inpatient care"
        generalMedicine['value'] = getERDTotalNumOfPatientsTransferredToOtherPerYear(year)
        report.push(generalMedicine)

        return  report.sort{it['key']}
    }

}
