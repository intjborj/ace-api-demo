package com.hisd3.hismk2.graphqlservices.hrm

import com.hisd3.hismk2.domain.hrm.Allowance
import com.hisd3.hismk2.domain.hrm.AllowanceTemplateItem
import com.hisd3.hismk2.domain.hrm.AllowanceTemplateItemsId
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.hrm.EmployeeAllowance
import com.hisd3.hismk2.domain.payroll.Timekeeping
import com.hisd3.hismk2.graphqlservices.types.GraphQLResVal
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.hrm.AllowanceTemplateItemsRepository
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

import javax.persistence.EntityManager

@Component
@GraphQLApi
class AllowanceTemplateItemsService {

    @Autowired
    AllowanceTemplateItemsRepository allowanceTemplateItemsRepository

    @Autowired
    EntityManager entityManager

    //================================= QUERIES ==================================
    @GraphQLQuery(name = "getAllowanceTemplateItemsByTemplateId", description = "get allowance template items  by their template id")
    List<AllowanceTemplateItem> getAllowanceTemplateItemsByTemplateId(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "showActive") Boolean showActive
    ) {
        String queryString = """ Select a from AllowanceTemplateItem a where a.template.id = :id """
        if (showActive)queryString += """ and a.active = true"""

        List<AllowanceTemplateItem> templateItems = entityManager.createQuery(
                    queryString
        ).setParameter("id", id).resultList
        return templateItems
    }


    //================================= MUTATIONS ==================================


    @GraphQLMutation(name = "deleteAllowanceTemplateItem")
    GraphQLResVal<Boolean> deleteAllowanceTemplateItem(
            @GraphQLArgument(name = "allowanceId") UUID allowanceId,
            @GraphQLArgument(name = "templateId") UUID templateId

    ) {
        try {
            if (allowanceId && templateId) {
                AllowanceTemplateItemsId id = new AllowanceTemplateItemsId(templateId, allowanceId)
                allowanceTemplateItemsRepository.deleteById(id)
                return new GraphQLResVal<Boolean>(true, true, 'Success')
            }
            return new GraphQLResVal<Boolean>(false, false, 'No parameter')
        } catch (e) {
            return new GraphQLResVal<Boolean>(false, false, e.message)
        }
    }


    @GraphQLMutation(name = "updateAllowanceTemplateItemStatus")
    GraphQLResVal<Boolean> updateAllowanceTemplateItemStatus(
            @GraphQLArgument(name = "allowanceId") UUID allowanceId,
            @GraphQLArgument(name = "templateId") UUID templateId

    ) {
        try {
            if (allowanceId && templateId) {
                AllowanceTemplateItemsId id = new AllowanceTemplateItemsId(templateId, allowanceId)
                AllowanceTemplateItem item = allowanceTemplateItemsRepository.findById(id).get()
                item.active = !item.active
                allowanceTemplateItemsRepository.save(item)
                return new GraphQLResVal<Boolean>(true, true, 'Success')
            }
            return new GraphQLResVal<Boolean>(false, false, 'No parameter')
        } catch (e) {
            return new GraphQLResVal<Boolean>(false, false, e.message)
        }
    }
}
