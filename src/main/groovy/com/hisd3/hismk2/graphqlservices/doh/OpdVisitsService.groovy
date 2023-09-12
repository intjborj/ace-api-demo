package com.hisd3.hismk2.graphqlservices.doh

import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Component
@Transactional
@GraphQLApi
class OpdVisitsService {

    @Autowired
    JdbcTemplate jdbcTemplate


    @GraphQLQuery(name="getNumberOfOPDNewVisits")
    Long getNumberOfOPDNewVisits(@GraphQLArgument(name="year") Integer year){
        return jdbcTemplate.queryForObject("""
            select
            coalesce(count(*),0)
            from (
                SELECT
                c.patient ,
                count(c.*) as "caseCount",
                jsonb_agg(years) as "arr_year",
                jsonb_agg(jsonb_build_object('id',c.id,'years',years))
                from 
                (
                select 
               c.id,
               c.patient,
                date_part('year'::text, c.entry_datetime + interval '8 hour') as years
                from pms.patients p
                left join pms.cases c on c.patient = p.id 
                left join pms.transfers t on t."case"  = c.id 
                where t.registry_type = 'OPD'
                and date_part('year'::text, c.entry_datetime + interval '8 hour') <= ?::DOUBLE PRECISION 
                GROUP by c.id,date_part('year'::text, c.entry_datetime + interval '8 hour'), c.patient
                ) as c
                group by c.patient
            ) as c
            where 
            cast("arr_year"->>0 as int) = ?::DOUBLE PRECISION 
        """,
                Long,
                year,
                year,
        )
    }

    @GraphQLQuery(name="getNumberOfOPDNewVisitsAdult")
    Long getNumberOfOPDNewVisitsAdult(@GraphQLArgument(name="year") Integer year){
        return jdbcTemplate.queryForObject("""
            select
            coalesce(count(*),0)
                from 
                (
                select 
                p.id,
                jsonb_agg(date_part('year'::text, c.entry_datetime + interval '8 hour')::varchar order by c.entry_datetime) as "arr_year",
                count(c.*)
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
                and date_part('year'::text, age(date(c.entry_datetime  + interval '8 hour'),date(p.dob  + interval '8 hour')))::integer >= 19
                and date_part('year'::text, c.entry_datetime + interval '8 hour') <= ?::DOUBLE PRECISION 
                group by p.id 
                having count(c.*) >= 1
                ) as c
            where 
            cast("arr_year"->>0 as int) = ?::DOUBLE PRECISION 
        """,
                Long,
                year,
                year,
        )
    }

    @GraphQLQuery(name="getNumberOfOPDNewVisitsPediatric")
    Long getNumberOfOPDNewVisitsPediatric(@GraphQLArgument(name="year") Integer year){
        return jdbcTemplate.queryForObject("""
            select
            coalesce(count(*),0)
                from 
                (
                select 
                p.id,
                jsonb_agg(date_part('year'::text, c.entry_datetime + interval '8 hour')::varchar order by c.entry_datetime) as "arr_year",
                count(c.*)
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
                and date_part('year'::text, age(date(c.entry_datetime  + interval '8 hour'),date(p.dob  + interval '8 hour')))::integer < 19
                and date_part('year'::text, c.entry_datetime + interval '8 hour') <= ?::DOUBLE PRECISION 
                group by p.id 
                having count(c.*) >= 1
                ) as c
            where 
            cast("arr_year"->>0 as int) = ?::DOUBLE PRECISION 
        """,
                Long,
                year,
                year,
        )
    }

    @GraphQLQuery(name="getNumberOfOPDReVisits")
    Long getNumberOfOPDReVisits(@GraphQLArgument(name="year") Integer year){
        return jdbcTemplate.queryForObject("""
            select 
            coalesce(sum(total))
            from
            (select
            case
            when cast(arr_year->>0 as Int) = ?::DOUBLE PRECISION  then
                c."countByYear" - 1
            else 
                c."countByYear"
            end as "total"
            from
            (
                select 
                p.id ,
                count(c.*)filter(where date_part('year'::text, c.entry_datetime + interval '8 hour') =  ?::DOUBLE PRECISION) as "countByYear",
                count(c.*) as "caseCount",
                jsonb_agg(date_part('year'::text, c.entry_datetime + interval '8 hour')::varchar order by c.entry_datetime) as "arr_year",
                jsonb_agg(jsonb_build_object('id',c.id,'no',c.case_no,'entry_datetime',c.entry_datetime,'t_year',date_part('year'::text, c.entry_datetime + interval '8 hour')::varchar)) as cases
                from pms.patients p
                left join pms.cases c on c.patient = p.id 
                left join pms.transfers t on t."case"  = c.id 
                where t.registry_type = 'OPD'
                and date_part('year'::text, c.entry_datetime + interval '8 hour') <= ?::DOUBLE PRECISION 
                group by p.id 
                having count(c.*) > 1
            ) as c
            where c."countByYear" >= 1) as c
        """,
                    Long,
                year,
                year,
                year,
        )
    }

    @GraphQLQuery(name="getNumberOfOPDReVisitsPediatric")
    Long getNumberOfOPDReVisitsPediatric(@GraphQLArgument(name="year") Integer year){
        return jdbcTemplate.queryForObject("""
            select 
            coalesce(sum(total),0)
            from
            (select
            case
            when cast(arr_year->>0 as Int) = ?::DOUBLE PRECISION  then
                c."countByYear" - 1
            else 
                c."countByYear"
            end as "total"
            from
            (
                select 
                p.id ,
                count(c.*)filter(where date_part('year'::text, c.entry_datetime + interval '8 hour') =  ?::DOUBLE PRECISION) as "countByYear",
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
                and date_part('year'::text, age(date(c.entry_datetime  + interval '8 hour'),date(p.dob  + interval '8 hour')))::integer < 19
                and date_part('year'::text, c.entry_datetime + interval '8 hour') <= ?::DOUBLE PRECISION 
                group by p.id 
                having count(c.*) > 1
            ) as c
            where c."countByYear" >= 1) as c
        """,
                Long,
                year,
                year,
                year,
        )
    }

    @GraphQLQuery(name="getNumberOfOPDReVisitsAdult")
    Long getNumberOfOPDReVisitsAdult(@GraphQLArgument(name="year") Integer year){
        return jdbcTemplate.queryForObject("""
            select 
            coalesce(sum(total))
            from
            (select
            case
            when cast(arr_year->>0 as Int) = ?::DOUBLE PRECISION  then
                c."countByYear" - 1
            else 
                c."countByYear"
            end as "total"
            from
            (
                select 
                p.id ,
                count(c.*)filter(where date_part('year'::text, c.entry_datetime + interval '8 hour') =  ?::DOUBLE PRECISION) as "countByYear",
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
                and date_part('year'::text, age(date(c.entry_datetime  + interval '8 hour'),date(p.dob  + interval '8 hour')))::integer >= 19
                and date_part('year'::text, c.entry_datetime + interval '8 hour') <= ?::DOUBLE PRECISION 
                group by p.id 
                having count(c.*) > 1
            ) as c
            where c."countByYear" >= 1) as c
        """,
                Long,
                year,
                year,
                year,
        )
    }

    @GraphQLQuery(name="getNumberOfOPDAdult")
    Long getNumberOfOPDAdult(@GraphQLArgument(name="year") Integer year){
        return jdbcTemplate.queryForObject("""
            select
                coalesce (count(*),0)
            from
            (
                select 
                    patient,
                    jsonb_agg(jsonb_build_object('case_id',id,'registry_type',registry_type) order by case_no) ,
                    count(id) as visits
                from
                (
                    select
                        distinct ON (c.id) 
                        t.registry_type ,c.id,c.patient,c.entry_datetime,c.case_no
                    from (
                        select 
                        c.id,c.case_no,c.patient,c.entry_datetime
                        from pms.cases c 
                        left join pms.patients p on p.id = c.patient
                        left join pms.transfers t on t."case" = c.id
                        where t.registry_type = 'OPD'
                        and date_part('year'::text, age(date(c.entry_datetime  + interval '8 hour'),date(p.dob  + interval '8 hour')))::integer >= 19
                        and date_part('year'::text, c.entry_datetime + interval '8 hour') = ?::DOUBLE PRECISION 
                        group by c.id,c.case_no 
                        order by c.case_no
                    ) c
                    left join pms.transfers t on t."case" = c.id
                    order by c.id, t.entry_datetime 
                ) c
            group  by patient
            ) as c 
        """,
                Long,
                    year
        )
    }

    @GraphQLQuery(name="getNumberOfOPDPediatric")
    Long getNumberOfOPDPediatric(
            @GraphQLArgument(name="year") Integer year){
        return jdbcTemplate.queryForObject("""
            select
                coalesce (count(*),0)
            from
            ( 
                select 
                    patient,
                    jsonb_agg(jsonb_build_object('case_id',id,'registry_type',registry_type) order by case_no) ,
                    count(id) as visits
                from
                (
                    select
                        distinct ON (c.id) 
                        t.registry_type ,c.id,c.patient,c.entry_datetime,c.case_no
                    from (
                        select 
                        c.id,c.case_no,c.patient,c.entry_datetime
                        from pms.cases c 
                        left join pms.patients p on p.id = c.patient
                        left join pms.transfers t on t."case" = c.id
                        where t.registry_type = 'OPD'
                        and date_part('year'::text, age(date(c.entry_datetime  + interval '8 hour'),date(p.dob  + interval '8 hour')))::integer < 19
                        and date_part('year'::text, c.entry_datetime + interval '8 hour') = ?::DOUBLE PRECISION 
                        group by c.id,c.case_no 
                        order by c.case_no
                    ) c
                    left join pms.transfers t on t."case" = c.id
                    order by c.id, t.entry_datetime 
                ) c
            group  by patient
            ) as c 
        """,
                Long,
                year
        )
    }

    @GraphQLQuery(name="getNumberOfOPDAdultGeneralMedicine")
    Long getNumberOfOPDAdultGeneralMedicine(
            @GraphQLArgument(name="year") Integer year){
        return jdbcTemplate.queryForObject("""
            select
                coalesce (count(*),0)
            from
            (
                select 
                    patient,
                    jsonb_agg(jsonb_build_object('case_id',id,'registry_type',registry_type) order by case_no) ,
                    count(id) as visits
                from
                (
                    select
                        distinct ON (c.id) 
                        t.registry_type ,c.id,c.patient,c.entry_datetime,c.case_no
                    from (
                        select 
                        c.id,c.case_no,c.patient,c.entry_datetime
                        from pms.cases c 
                        left join pms.patients p on p.id = c.patient
                        left join pms.transfers t on t."case" = c.id
                        left join hospital_configuration.physicians p2 on p2.id = c.opd_physician and p2.general_practitioner is true ,
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
                        and date_part('year'::text, age(date(c.entry_datetime  + interval '8 hour'),date(p.dob  + interval '8 hour')))::integer >= 19
                        and p2.id  is not null
                        and date_part('year'::text, c.entry_datetime + interval '8 hour') = ?::DOUBLE PRECISION 
                        group by c.id,c.case_no 
                        order by c.case_no
                    ) c
                    left join pms.transfers t on t."case" = c.id
                    order by c.id, t.entry_datetime 
                ) c
            group  by patient
            ) as c 
        """,
                Long,
                year
        )
    }

    @GraphQLQuery(name="getNumberOfOPDVisitsNonSurgical")
    Long getNumberOfOPDVisitsNonSurgical(@GraphQLArgument(name="year") Integer year){
        return jdbcTemplate.queryForObject("""
            select
                coalesce (count(*),0)
            from
            (
                select 
                    patient,
                    jsonb_agg(jsonb_build_object('case_id',id,'registry_type',registry_type) order by case_no) ,
                    count(id) as visits
                from
                (
                    select
                        distinct ON (c.id) 
                        t.registry_type ,c.id,c.patient,c.entry_datetime,c.case_no
                    from (
                        select 
                        c.id,c.case_no,c.patient,c.entry_datetime
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
                        t.registry_type = 'OPD'
                        and 
                        c.consultation = 'NON-SURGICAL'
                        and date_part('year'::text, c.entry_datetime + interval '8 hour') = ?::DOUBLE PRECISION  
                        group by c.id,c.case_no 
                        order by c.case_no
                    ) c
                    left join pms.transfers t on t."case" = c.id
                    order by c.id, t.entry_datetime 
                ) c
            group  by patient
            ) as c 
        """,
                Long,
                    year
        )
    }

    @GraphQLQuery(name="getNumberOfOPDVisitsSurgical")
    Long getNumberOfOPDVisitsSurgical(@GraphQLArgument(name="year") Integer year){
        return jdbcTemplate.queryForObject("""
            select
                coalesce (count(*),0)
            from
            (
                select 
                    patient,
                    jsonb_agg(jsonb_build_object('case_id',id,'registry_type',registry_type) order by case_no) ,
                    count(id) as visits
                from
                (
                    select
                        distinct ON (c.id) 
                        t.registry_type ,c.id,c.patient,c.entry_datetime,c.case_no
                    from (
                        select 
                        c.id,c.case_no,c.patient,c.entry_datetime
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
                        t.registry_type = 'OPD'
                        and 
                        c.consultation = 'SURGICAL'
                        and date_part('year'::text, c.entry_datetime + interval '8 hour') = ?::DOUBLE PRECISION  
                        group by c.id,c.case_no 
                        order by c.case_no
                    ) c
                    left join pms.transfers t on t."case" = c.id
                    order by c.id, t.entry_datetime 
                ) c
            group  by patient
            ) as c 
        """,
                Long,
                    year
        )
    }

    @GraphQLQuery(name="getNumberOfOPDVisitsAntenatalPrenatal")
    Long getNumberOfOPDVisitsAntenatalPrenatal(@GraphQLArgument(name="year") Integer year){
        return jdbcTemplate.queryForObject("""
            select
                coalesce (count(*),0)
            from
            (
                select 
                    patient,
                    jsonb_agg(jsonb_build_object('case_id',id,'registry_type',registry_type) order by case_no) ,
                    count(id) as visits
                from
                (
                    select
                        distinct ON (c.id) 
                        t.registry_type ,c.id,c.patient,c.entry_datetime,c.case_no
                    from (
                        select 
                        c.id,c.case_no,c.patient,c.entry_datetime
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
                        t.registry_type = 'OPD'
                        and 
                        c.consultation = 'PRENATAL/ANTENATAL'
                        and date_part('year'::text, c.entry_datetime + interval '8 hour') = ?::DOUBLE PRECISION  
                        group by c.id,c.case_no 
                        order by c.case_no
                    ) c
                    left join pms.transfers t on t."case" = c.id
                    order by c.id, t.entry_datetime 
                ) c
            group  by patient
            ) as c 
        """,
                Long,
                    year
        )
    }

    @GraphQLQuery(name="getNumberOfOPDVisitsAntenatalPostnatal")
    Long getNumberOfOPDVisitsAntenatalPostnatal(@GraphQLArgument(name="year") Integer year){
        return jdbcTemplate.queryForObject("""
            select
                coalesce (count(*),0)
            from
            (
                select 
                    patient,
                    jsonb_agg(jsonb_build_object('case_id',id,'registry_type',registry_type) order by case_no) ,
                    count(id) as visits
                from
                (
                    select
                        distinct ON (c.id) 
                        t.registry_type ,c.id,c.patient,c.entry_datetime,c.case_no
                    from (
                        select 
                            c.id,c.case_no,c.patient,c.entry_datetime
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
                        t.registry_type = 'OPD'
                        and 
                        c.consultation = 'POSTNATAL'
                        and date_part('year'::text, c.entry_datetime + interval '8 hour') = ?::DOUBLE PRECISION  
                        group by c.id,c.case_no 
                        order by c.case_no
                    ) c
                    left join pms.transfers t on t."case" = c.id
                    order by c.id, t.entry_datetime 
                ) c
            group  by patient
            ) as c 
        """,
                Long,
                    year
        )
    }


    @GraphQLQuery(name="getOPDVisitsReport")
    List<Map<String,Object>> getOPDVisitsReport(@GraphQLArgument(name = "year") Integer year){
        List<Map<String,Object>> report = []

        Long newVisitsValue = getNumberOfOPDNewVisits(year)
        Long reVisitsValue = getNumberOfOPDReVisits(year)
        Long newVisitsAdult = getNumberOfOPDNewVisitsAdult(year)
        Long revisitsAdult = getNumberOfOPDReVisitsAdult(year)

        Long newVisitsPediatric = getNumberOfOPDNewVisitsPediatric(year)
        Long revisitsPediatric = getNumberOfOPDReVisitsPediatric(year)

        if(!newVisitsAdult)  newVisitsAdult = 0
        if(!revisitsAdult)  revisitsAdult = 0
        if(!newVisitsPediatric)  newVisitsPediatric = 0
        if(!revisitsPediatric)  revisitsPediatric = 0

        Map<String,Object> visits = [:]
        visits['key'] = 0
        visits['label'] = "Number of outpatient visits, new patient"
        visits['value'] = newVisitsPediatric + newVisitsAdult
        report.push(visits)

        Map<String,Object> reVisits = [:]
        reVisits['key'] = 1
        reVisits['label'] = "Number of outpatient visits, re-visit"
        reVisits['value'] = revisitsAdult + revisitsPediatric
        report.push(reVisits)

        Map<String,Object> adult = [:]
        adult['key'] = 2
        adult['label'] = "Number of outpatient visits, adult"
        adult['value'] = newVisitsAdult + revisitsAdult
        report.push(adult)

        Map<String,Object> pediatric = [:]
        pediatric['key'] = 3
        pediatric['label'] = "Number of outpatient visits, pediatric"
        pediatric['value'] = newVisitsPediatric + revisitsPediatric
        report.push(pediatric)

        Map<String,Object> generalMedicine = [:]
        generalMedicine['key'] = 4
        generalMedicine['label'] = "Number of adult general medicine outpatient visits"
        generalMedicine['value'] = getNumberOfOPDAdultGeneralMedicine(year)
        report.push(generalMedicine)

        Map<String,Object> nonSurgical = [:]
        nonSurgical['key'] = 5
        nonSurgical['label'] = "Number of specialty (non-surgical) outpatient visits"
        nonSurgical['value'] = getNumberOfOPDVisitsNonSurgical(year)
        report.push(nonSurgical)

        Map<String,Object> surgical = [:]
        surgical['key'] = 6
        surgical['label'] = "Number of surgical outpatient visits"
        surgical['value'] = getNumberOfOPDVisitsSurgical(year)
        report.push(surgical)

        Map<String,Object> prenatal = [:]
        prenatal['key'] = 7
        prenatal['label'] = "Number of antenatal/prenatal care visits"
        prenatal['value'] = getNumberOfOPDVisitsAntenatalPrenatal(year)
        report.push(prenatal)

        Map<String,Object> postnatal = [:]
        postnatal['key'] = 8
        postnatal['label'] = "Number of postnatal care visits"
        postnatal['value'] = getNumberOfOPDVisitsAntenatalPostnatal(year)
        report.push(postnatal)

        return  report.sort{it['key']}

    }


}
