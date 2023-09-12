package com.hisd3.hismk2.graphqlservices.ancillary

import com.hisd3.hismk2.domain.ancillary.Orderslip
import com.hisd3.hismk2.domain.ancillary.RfFees
import com.hisd3.hismk2.repository.ancillary.RfFeesRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class RfTablesService {

    @Autowired
    RfFeesRepository rfFeesRepository
    //============== All Queries ====================
    @GraphQLQuery(name = "TestString", description = "Get All Orderslips")
    String testString() {
        return "Success"
    }

    @GraphQLQuery(name = "rffees", description = "Get All Rf Rees")
    Page<RfFees> searchRfPageable(
            @GraphQLArgument(name = "empId") String empId ,
            @GraphQLArgument(name = "category") String category,
            @GraphQLArgument(name = "department") String department,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "pageSize") Integer pageSize
    ) {

        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, 'createdDate')
//        if(empId == null){
//            empId = ""
//        }
//        if (department == null){
//            department = ""
//        }
//
//        if( empId || department){
//            return  rfFeesRepository.searchFilteredRfPageable(category,empId,department,pageable)
//        }
//            return  rfFeesRepository.searchRfPageable(category,pageable)
        return  rfFeesRepository.searchFilteredRfPageable(category,empId?:"",department?:"",pageable)
    }
}
