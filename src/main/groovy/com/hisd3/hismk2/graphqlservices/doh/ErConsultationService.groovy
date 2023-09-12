package com.hisd3.hismk2.graphqlservices.doh

import com.hisd3.hismk2.graphqlservices.doh.dto.ErConsultationDto
import com.hisd3.hismk2.graphqlservices.doh.dto.ErdConsultation2Dto
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class ErConsultationService {


    @Autowired
    private JdbcTemplate jdbcTemplate


    @GraphQLQuery(name= "ErConsultation", description = "get ER consultation")
    List<ErConsultationDto>ErConsultation(@GraphQLArgument(name="year") Integer year){

        List<ErConsultationDto> erConsultation = jdbcTemplate.query("""
            SELECT (d.value ->> 'icdCode'::text) AS icdCode,
                    (d.value ->> 'icdDesc'::text) AS longName,
                    date_part('year'::text, c.entry_datetime) AS reportingYear,
                    icd.icd10_cat AS icd10Cat,
                    count((d.value ->> 'icdCode'::text)) AS total

                   FROM pms.cases c,
                    LATERAL json_array_elements(
                        CASE
                            WHEN (pms.is_json((c.doh_icd_diagnosis)::character varying) AND (c.doh_icd_diagnosis IS NOT NULL)) THEN (c.doh_icd_diagnosis)::json
                            ELSE NULL::json
                        END) d(value)
                  LEFT JOIN referential.doh_icd_codes icd on icd.icd10_code = (d.value ->> 'icdCode'::TEXT)  
            
                  
                  WHERE c.registry_type= 'ERD' and date_part('year'::text, c.entry_datetime) = ${year}  
                  GROUP BY (d.value ->> 'icdCode'::text), (d.value ->> 'icdDesc'::text), (date_part('year'::text, c.entry_datetime)), icd.icd10_cat

              
                  ORDER BY (count((d.value ->> 'icdCode'::text))) DESC
                  limit 10 ;
       """, new BeanPropertyRowMapper(ErConsultationDto.class))
        return erConsultation
    }


    @GraphQLQuery( name='ErdConsultationV2', description = 'opd consultation report')
    List<ErdConsultation2Dto> ErdConsultationV2(@GraphQLArgument(name = 'year') Integer year){

        List<ErdConsultation2Dto>  opdConsultation = jdbcTemplate.query("""
            select
            concat(row_number() over(order by count(*) desc,"dohICDs"->>'icdCategory' asc),'. ',"dohICDs"->>'icdDesc' ) as "description",
            "dohICDs"->>'icdCode' as "icdCode",
            "dohICDs"->>'icdCategory' as "icdCategory",
            count(*) as "number"
                from
                    (
                       select 
                       c.id,
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
                            ))->0 as "dohICDs"
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
                date_part('year'::text, c.entry_datetime + interval '8 hour') =  ?::DOUBLE PRECISION
                    and p.dob is not null
               --     and
            --    c.urgency = 'EMERGENCY'
                    group by c.id
               ) as cases
               group by "dohICDs"
                    -- limit 10
        """, new BeanPropertyRowMapper(ErdConsultation2Dto.class),year)
        return opdConsultation
    }
}
