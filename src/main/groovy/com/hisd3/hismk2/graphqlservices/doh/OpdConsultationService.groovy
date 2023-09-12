package com.hisd3.hismk2.graphqlservices.doh

import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.graphqlservices.doh.dto.OpdConsultation2Dto
import com.hisd3.hismk2.graphqlservices.doh.dto.OpdConsultationDto
import com.hisd3.hismk2.repository.pms.CaseRepository
import com.hisd3.hismk2.rest.dto.StockCard
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate


@Service
@Component
@Transactional
@GraphQLApi
class OpdConsultationService {

    @Autowired
    CaseRepository caseRepository

    @Autowired
    private JdbcTemplate jdbcTemplate


    @GraphQLQuery( name='OpdConsultation', description = 'opd consultation report')
    List<OpdConsultationDto> OpdConsultation(@GraphQLArgument(name = 'year') Integer year){

        List<OpdConsultationDto>  opdConsultation = jdbcTemplate.query("""
                    SELECT (d.value ->> 'icdCode'::text) AS icd_code,
                        (d.value ->> 'icdDesc'::text) AS long_name,
                        date_part('year'::text, c.entry_datetime) AS reporting_year,
                        icd.icd10_cat,
                        count((d.value ->> 'icdCode'::text)) AS total
                        
                       FROM pms.cases c,
                        LATERAL json_array_elements(
                            CASE
                                WHEN (pms.is_json((c.doh_icd_diagnosis)::character varying) AND (c.doh_icd_diagnosis IS NOT NULL)) THEN (c.doh_icd_diagnosis)::json
                                ELSE NULL::json
                            END) d(value)
                        left join referential.doh_icd_codes icd on icd.icd10_code = (d.value ->> 'icdCode'::TEXT)
                      WHERE c.registry_type= 'OPD' and date_part('year'::text, c.entry_datetime) = ${year}   
                      
                      GROUP BY (d.value ->> 'icdCode'::text), (d.value ->> 'icdDesc'::text), (date_part('year'::text, c.entry_datetime)), icd.icd10_cat
                      ORDER BY (count((d.value ->> 'icdCode'::text))) DESC
                      limit 10;
        """, new BeanPropertyRowMapper(OpdConsultationDto.class))
        return opdConsultation
    }

    @GraphQLQuery( name='OpdConsultationV2', description = 'opd consultation report')
    List<OpdConsultation2Dto> OpdConsultationV2(@GraphQLArgument(name = 'year') Integer year){

        List<OpdConsultation2Dto>  opdConsultation = jdbcTemplate.query("""
        select 
            concat(row_number() over(order by count(*) desc,doh.value->>'icdCategory' asc),'. ',doh.value->>'icdDesc' ) as "description",
            doh.value->>'icdCode' as "icdCode",
            doh.value->>'icdCategory' as "icdCategory",
            count(*) as "number"
            from (
                select
                "dohICDs"
                from
                (
                select 
                p.id ,
                jsonb_agg(jsonb_build_object(
                'id',d.value->>'id',
                'index',d.value->>'index',
                'icdCode',d.value->>'icdCode',
                'icdDesc',d.value->>'icdDesc',
                'position',d.value->>'position',
                'causeOfDeath',d.value->>'causeOfDeath',
                'isFinalDiagnosis',d.value->>'isFinalDiagnosis',
                'icdCategory',dic.icd10_cat,
                'catCode',dic.icd10_code 
                )) filter(where date_part('year'::text, c.entry_datetime + interval '8 hour') =   ?::DOUBLE PRECISION ) as "dohICDs", 
                count(c.*)filter(where date_part('year'::text, c.entry_datetime + interval '8 hour') =   ?::DOUBLE PRECISION ) as "countByYear",
                count(c.*) as "caseCount",
                jsonb_agg(date_part('year'::text, c.entry_datetime + interval '8 hour')::varchar order by c.entry_datetime) as "arr_year",
                jsonb_agg(jsonb_build_object('id',c.id,'no',c.case_no,'entry_datetime',c.entry_datetime,'t_year',date_part('year'::text, c.entry_datetime + interval '8 hour')::varchar)) as cases
                from pms.patients p
                left join pms.cases c on c.patient = p.id 
                left join pms.transfers t on t."case"  = c.id,
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
                and t.registry_type = 'OPD'
                and date_part('year'::text, c.entry_datetime + interval '8 hour') <=  ?::DOUBLE PRECISION  
                group by p.id 
                having count(c.*) >= 1
            ) as c
            where c."countByYear" >= 1
            ) as c,
            lateral  json_array_elements ("dohICDs"::json) doh(value)
            group by doh.value->>'icdCode',doh.value->>'icdDesc',doh.value->>'icdCategory',doh.value->>'catCode'
            order by count(*) desc,doh.value->>'icdCategory' asc
                    -- limit 10
        """, new BeanPropertyRowMapper(OpdConsultation2Dto.class),year,year,year)
        return opdConsultation
    }







}
