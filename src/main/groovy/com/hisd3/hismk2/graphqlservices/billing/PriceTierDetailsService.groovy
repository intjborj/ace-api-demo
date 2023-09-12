package com.hisd3.hismk2.graphqlservices.billing

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.billing.PriceTierDetail
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.billing.PriceTierDetailRepository
import com.hisd3.hismk2.repository.pms.CaseRepository
import groovy.transform.TypeChecked
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
import javax.persistence.PersistenceContext
import java.time.Instant

class PriceTierDetailDto {
    UUID id
    String tierCode
    String description
    String registryType
    String accommodationType
    List<String> roomTypes
    Instant fromDatetime
    Instant toDatetime
    Boolean isVatable
    BigDecimal vatRate
    Department department
    Boolean forSenior
}

@TypeChecked
@Component
@GraphQLApi
class PriceTierDetailsService {

    @Autowired
    PriceTierDetailRepository priceTierDetailRepository

    @Autowired
    DepartmentRepository departmentRepository

    @Autowired
    CaseRepository caseRepository

    @Autowired
    ObjectMapper objectMapper

    @PersistenceContext
    EntityManager entityManager

    @GraphQLQuery(name = "getPriceTierDetails", description = "Get all price tier details by title")
    List<PriceTierDetail> getPriceTierDetails(@GraphQLArgument(name = "title") String title) {
        List<PriceTierDetail> detailList = priceTierDetailRepository.getPriceTierDetailsByFilter(title).sort { it -> it.description }
    }

    @GraphQLQuery(name = "getPriceTierDetailsPageable", description = "Get all price tier details by title")
    Page<PriceTierDetail> getPriceTierDetails(
            @GraphQLArgument(name = "title") String title,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "pageSize") Integer pageSize,
            @GraphQLArgument(name = "type") String type
    ) {
        def newPage = 0
        def newPageSize = 25
        def defaultType = "STANDARD"

        if (page)
            newPage = page
        if (pageSize)
            newPageSize = pageSize

        if (type)
            defaultType = type

        return priceTierDetailRepository.getPriceTierDetailsByFilterPageable(title, defaultType, PageRequest.of(newPage, newPageSize, Sort.Direction.ASC, 'createdDate'))
    }

    @GraphQLMutation
    GraphQLRetVal<PriceTierDetail> upsertPriceTierDetail(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields,
            @GraphQLArgument(name = "departments") List<UUID> departments,
            @GraphQLArgument(name = "department") UUID department,
            @GraphQLArgument(name = "roomTypes") List<String> roomTypes
    ) {

//         if (!fields.get("registryType") || !fields.get("accommodationType") || departments.isEmpty())
        if (!fields.get("registryType") || !fields.get("accommodationType"))
            return new GraphQLRetVal<PriceTierDetail>(null, false, "Failed to ${id ? "update" : "create"} price tier detail.")

        String queryString = """
			Select p from PriceTierDetail p
			where 
                p.registryType = :registryType 
                and p.accommodationType = :accommodationType 
		"""

        String deptRoomTypesQuery = " and (p.departments is not null"
        if (roomTypes.size() > 0) deptRoomTypesQuery += " or p.roomTypes is not null)"
        else deptRoomTypesQuery += ")"
        queryString += deptRoomTypesQuery


        Boolean senior = fields.get("forSenior") ?: false
        Boolean isVatable = fields.get("isVatable") ?: false
        BigDecimal vatRate = (fields.get("vatRate") ?: null) as BigDecimal


        if (senior)
            queryString += "and p.forSenior = :forSenior "
        else {
            queryString += "and (p.forSenior is null OR p.forSenior = :forSenior) "
        }

        String departmentQuery = ""
        String roomTypesQuery = ""

        if (departments.size() > 0) {
            departments.eachWithIndex { UUID it, int i ->
                if (i > 0)
                    departmentQuery += " or"
                departmentQuery += " upper(p.departments) LIKE upper(concat('%', :department${i}, '%'))"
            }
            queryString += " AND (${departmentQuery})"
        }


        if (roomTypes.size() > 0) {
            roomTypes.eachWithIndex { String it, int i ->
                if (i > 0) roomTypesQuery += " or"
                roomTypesQuery += " upper(p.roomTypes) LIKE upper(concat('%', :roomType${i}, '%'))"
            }
            queryString += " AND (${roomTypesQuery})"
        } else {
            queryString += " AND (upper(p.roomTypes) LIKE upper(concat('%', '[]', '%')) or p.roomTypes is NULL)"
        }

        if (id) {
            queryString += " AND p.id != :id"
        }

        def query = entityManager.createQuery(queryString)
                .setParameter("registryType", fields.get("registryType"))
                .setParameter("accommodationType", fields.get("accommodationType"))
                .setParameter("forSenior", senior)
//                .setParameter("vatRate", vatRate)

        if (departments.size() > 0) {
            departments.eachWithIndex { UUID it, int i ->
                query.setParameter("department${i}", it)
            }
        }
        if (roomTypes.size() > 0) {
            roomTypes.eachWithIndex { String it, int i ->
                query.setParameter("roomType${i}", it)
            }
        }
        if (id) {
            query.setParameter("id", id)
        }

        List<PriceTierDetail> priceTiers = query.resultList

        if (priceTiers.size() > 0) {
            String redundantPriceTiers = ""
            priceTiers.eachWithIndex { PriceTierDetail entry, int i ->
                redundantPriceTiers += "${entry.tierCode}-${entry.description}"
                if (i > 1) redundantPriceTiers += ","
            }
            return new GraphQLRetVal<PriceTierDetail>(null, false, "Found redundant price tier: " + redundantPriceTiers)
        }

        Department priceTierDepartment
        String stringDepartments = objectMapper.writeValueAsString(departments)
        String stringRoomTypes = objectMapper.writeValueAsString(roomTypes)
        if (department)
            priceTierDepartment = departmentRepository.findById(department).get()

        if (id) {
            PriceTierDetail priceTierDetail = priceTierDetailRepository.findById(id).get()
            priceTierDetail = objectMapper.updateValue(priceTierDetail, fields)
            priceTierDetail.departments = stringDepartments
            priceTierDetail.roomTypes = stringRoomTypes

            if (priceTierDepartment)
                priceTierDetail.department = priceTierDepartment
            else priceTierDetail.department = null
            priceTierDetail = priceTierDetailRepository.save(priceTierDetail)


            return new GraphQLRetVal<PriceTierDetail>(priceTierDetail, true, "Successfully updated price tier detail.")
        } else {

            PriceTierDetail priceTierDetail = objectMapper.convertValue(fields, PriceTierDetail)
            priceTierDetail.departments = stringDepartments
            priceTierDetail.roomTypes = stringRoomTypes
            if (priceTierDepartment)
                priceTierDetail.department = priceTierDepartment
            else priceTierDetail.department = null

            priceTierDetail = priceTierDetailRepository.save(priceTierDetail)

            return new GraphQLRetVal<PriceTierDetail>(priceTierDetail, true, "Successfully created price tier detail.")
        }

    }
}
