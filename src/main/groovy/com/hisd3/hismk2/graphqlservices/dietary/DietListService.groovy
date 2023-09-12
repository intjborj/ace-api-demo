package com.hisd3.hismk2.graphqlservices.dietary

import com.hisd3.hismk2.domain.dietary.Diet
import com.hisd3.hismk2.domain.dietary.DietList
import com.hisd3.hismk2.repository.dietary.DietListRepository
import com.hisd3.hismk2.services.DietaryService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.sql.Timestamp
import java.time.Instant

@Component
@GraphQLApi
class DietListService {
    @Autowired
    DietListRepository dietListRepository

    @Autowired
    DietaryService dietaryService

    @GraphQLQuery(name = "dietsList", description = "Get All Diets List")
    List<DietList> findAll(
            @GraphQLArgument(name = "filter") String filter,
            @GraphQLArgument(name = "meal") String meal,
            @GraphQLArgument(name = "recent") String recent,
            @GraphQLArgument(name = "now") String now
    ) {
        Timestamp startTimeS = Timestamp.valueOf(recent)
        Instant startDate = startTimeS.toInstant()
        Timestamp endTimeS = Timestamp.valueOf(now)
        Instant endDate = endTimeS.toInstant()

        return dietListRepository.findDietListByDate(filter,meal,startDate,endDate).sort{it.patientCase?.room?.roomNo}
    }

    @GraphQLQuery(name = "employeeDietsList", description = "Get Employee Diet List")
    List<DietList> employeeDietsList(
            @GraphQLArgument(name = "filter") String filter,
            @GraphQLArgument(name = "meal") String meal,
            @GraphQLArgument(name = "recent") String recent,
            @GraphQLArgument(name = "now") String now
    ) {
        Timestamp startTimeS = Timestamp.valueOf(recent)
        Instant startDate = startTimeS.toInstant()
        Timestamp endTimeS = Timestamp.valueOf(now)
        Instant endDate = endTimeS.toInstant()

        return dietListRepository.findEmployeeDietByDate(filter,meal,startDate,endDate).sort{it.patientCase?.room?.roomNo}
    }

    @GraphQLQuery(name = "companionDietsList", description = "Get Companion Diet List")
    List<DietList> companionDietsList(
            @GraphQLArgument(name = "filter") String filter,
            @GraphQLArgument(name = "meal") String meal,
            @GraphQLArgument(name = "recent") String recent,
            @GraphQLArgument(name = "now") String now
    ) {
        Timestamp startTimeS = Timestamp.valueOf(recent)
        Instant startDate = startTimeS.toInstant()
        Timestamp endTimeS = Timestamp.valueOf(now)
        Instant endDate = endTimeS.toInstant()

        return dietListRepository.findCompanionDietByDate(filter,meal,startDate,endDate).sort{it.patientCase?.room?.roomNo}
    }



    @GraphQLMutation
    List<DietList> generateDietTask(
            @GraphQLArgument(name = "meal") String meal,
            @GraphQLArgument(name = "start") String start,
            @GraphQLArgument(name = "end") String end
    ) {

        return dietaryService.createPatientDietList(meal,start,end)

    }

    @GraphQLMutation
    Boolean addEmployeeMeal(
            @GraphQLArgument(name = "meal") String meal,
            @GraphQLArgument(name = "empId") String empId
    ) {
        println("log here")
        return dietaryService.addEmpMeal(meal,UUID.fromString(empId))

    }
    @GraphQLMutation
    Boolean addCompanionMeal(
            @GraphQLArgument(name = "meal") String meal,
            @GraphQLArgument(name = "patientCase") String pCase,
            @GraphQLArgument(name = "alias") String alias
    ) {
        println("log here")
        return dietaryService.addCompanionMeal(meal,UUID.fromString(pCase),alias)

    }
}
