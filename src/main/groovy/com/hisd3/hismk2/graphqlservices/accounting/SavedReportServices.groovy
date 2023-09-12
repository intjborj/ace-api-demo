package com.hisd3.hismk2.graphqlservices.accounting

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.hisd3.hismk2.domain.accounting.SavedReports
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.persistence.EntityManager
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
@GraphQLApi
class SavedReportServices extends AbstractDaoService<SavedReports>{

    SavedReportServices() {
        super(SavedReports.class)
    }


    @Autowired
    ReportService reportService

    @Autowired
    EntityManager entityManager


    static def mapSavedReports(Byte[] byteFile, Class aClass){
        try{
            String encode = Base64.getEncoder().encodeToString(byteFile as byte[])
            byte[] decoded = Base64.getDecoder().decode(encode) as byte[];
            String decodedStr = new String(decoded, StandardCharsets.UTF_8);
            ObjectMapper objectMapper = new ObjectMapper()
            return objectMapper.readValue(decodedStr,aClass)
        }
        catch (e){
            System.out.println(e);
        }
    }

    @GraphQLQuery(name = "getSavedTrialBalanceByDate")
    TrialBalanceDto2[] getSavedTrialBalanceByDate(
            @GraphQLArgument(name = "start") String  start,
            @GraphQLArgument(name = "end") String  end
    ){
        TrialBalanceDto2[] dto2s = []
        TimeZone timeZone = TimeZone.getTimeZone('Asia/Manila')

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
        Date startDate = Date.from(LocalDate.parse(start, formatter).atStartOfDay(timeZone.toZoneId()).toInstant())
        Date endDate = Date.from(LocalDate.parse(end, formatter).atStartOfDay(timeZone.toZoneId()).toInstant())
        try{
            SavedReports savedReports = entityManager.createQuery("""
                Select s from SavedReports s where s.startDate = :start and s.endDate = :end and s.reportType = 'TRIAL-BALANCE'
            """,SavedReports.class)
                .setParameter('start',startDate)
                .setParameter('end',endDate)
                .getSingleResult()
            if(savedReports)
                dto2s = mapSavedReports(savedReports.jsonFile, TrialBalanceDto2[].class) as TrialBalanceDto2[]

            return dto2s
        }
        catch (e){
            System.out.println(e);
            return dto2s
        }
    }


    @GraphQLQuery(name = "getSavedTrialBalance")
    GraphQLRetVal<TrialBalanceDto2[]> getSavedTrialBalance(
            @GraphQLArgument(name = "start") String  start,
            @GraphQLArgument(name = "end") String  end
    ){
        TrialBalanceDto2[] dto2s = getSavedTrialBalanceByDate(start,end)
        if(!dto2s){
            dto2s = savedTrialBalance(start,end)
        }
        return new GraphQLRetVal<TrialBalanceDto2[]>(dto2s,true,'')
    }


    @GraphQLMutation(name="savedTrialBalance")
    TrialBalanceDto2[] savedTrialBalance(
            @GraphQLArgument(name = "start") String  start,
            @GraphQLArgument(name = "end") String  end
    ){
        TrialBalanceDto2[] dto2s = []
        try{
            List<TrialBalanceDto2> dtoList = reportService.getTrialBalanceSummary(start,end) as List<TrialBalanceDto2>

            SavedReports savedReports = new SavedReports()
            savedReports.reportType = 'TRIAL-BALANCE'
            savedReports.startDate = new SimpleDateFormat("yyyy-MM-dd").parse(start);
            savedReports.endDate = new SimpleDateFormat("yyyy-MM-dd").parse(end);
            savedReports.jsonFile = new Gson().toJson(dtoList).getBytes()
            SavedReports newSave = save(savedReports)
            if(newSave)
                dto2s = mapSavedReports(newSave.jsonFile, TrialBalanceDto2[].class) as TrialBalanceDto2[]

            return dto2s
        }
        catch (e){
            System.out.println(e);
            return dto2s
        }
    }

}
