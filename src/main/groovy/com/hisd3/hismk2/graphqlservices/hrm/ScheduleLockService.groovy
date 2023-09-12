package com.hisd3.hismk2.graphqlservices.hrm

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.hrm.ScheduleLock
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.hrm.ScheduleLockRepository
import com.vladmihalcea.hibernate.type.util.MapResultTransformer
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.hibernate.query.Query
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.validation.constraints.NotNull
import java.awt.font.GraphicAttribute
import java.time.Instant

@TypeChecked
@Component
@GraphQLApi
class ScheduleLockService {

    @Autowired
    ScheduleLockRepository scheduleLockRepository

    @Autowired
    ObjectMapper objectMapper

    @PersistenceContext
    private EntityManager entityManager

    //==================================Mutation==================================\\

    @GraphQLQuery(name = "getScheduleLock")
    Map<String, ScheduleLock> getScheduleLock(
            @GraphQLArgument(name = "startDate") Instant startDate,
            @GraphQLArgument(name = "endDate") Instant endDate
    ) {
        Map<String, ScheduleLock> scheduleLock = entityManager.createQuery("""
		select 
		    to_char(s.date + '8h', 'MM_DD_YYYY') as map_key, 
		    s as map_value 
		from ScheduleLock s
		where 
		    s.date >= :startDate and s.date <= :endDate
		order by s.date
        """).unwrap(Query.class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setResultTransformer(new MapResultTransformer<String, ScheduleLock>())
                .getSingleResult() as Map<String, ScheduleLock>

        return scheduleLock

    }


    //==================================Mutation==================================\\

    //==================================Mutation==================================\\

    @GraphQLMutation(name = "upsertScheduleLock")
    GraphQLRetVal<ScheduleLock> upsertScheduleLock(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") @NotNull Map<String, Object> fields
    ) {

        if (id) {
            ScheduleLock scheduleLock = scheduleLockRepository.findById(id).get()
            if (!scheduleLock) return new GraphQLRetVal<ScheduleLock>(null, false, "Failed to save schedule lock.")
            scheduleLock = objectMapper.updateValue(scheduleLock, fields)
            scheduleLockRepository.save(scheduleLock)

            return new GraphQLRetVal<ScheduleLock>(scheduleLock, true, "Successfully saved schedule lock.")

        } else {
            ScheduleLock scheduleLock = objectMapper.convertValue(fields, ScheduleLock)
            scheduleLockRepository.save(scheduleLock)

            return new GraphQLRetVal<ScheduleLock>(scheduleLock, true, "Successfully saved schedule lock.")

        }

    }

    //==================================Mutation==================================\\


}
