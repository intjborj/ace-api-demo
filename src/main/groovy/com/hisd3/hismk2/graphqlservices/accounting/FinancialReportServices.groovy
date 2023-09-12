package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.accounting.ChartOfAccount
import com.hisd3.hismk2.domain.accounting.FinancialReport
import com.hisd3.hismk2.domain.accounting.LineType
import com.hisd3.hismk2.domain.accounting.SourceLineType
import com.hisd3.hismk2.domain.accounting.SourceMotherAccount
import com.hisd3.hismk2.domain.accounting.SourceSubAccount
import com.hisd3.hismk2.domain.accounting.SourceSubAccountExclude
import com.hisd3.hismk2.domain.accounting.SubAccountSetup
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.apache.poi.sl.usermodel.Line
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

@Service
@GraphQLApi
@Transactional(rollbackOn = Exception.class)
class FinancialReportServices extends AbstractDaoService<FinancialReport> {


    FinancialReportServices( ) {
        super(FinancialReport.class)
    }


    @PersistenceContext
    EntityManager entityManager


    @Autowired
    GeneratorService generatorService





    @GraphQLQuery(name = "getForParentLineTypes")
    List<LineType> getForParentLineTypes(@GraphQLArgument(name = "id") UUID id,
                                         @GraphQLArgument(name = "reqId") UUID reqId)
    {
        def fs = fsById(id)
        List<LineType> result = []
        fs.lineTypes.each {
            if(reqId)
            {
                 if(it.id != reqId)
                     result << it
            }
            else {
                result << it
            }

        }

        result
    }
    @GraphQLQuery(name = "fsById")
    FinancialReport fsById( @GraphQLArgument(name = "id") UUID id){
        findOne(id)
    }
    @GraphQLQuery(name = "financialReports")
    Page<FinancialReport> financialReports(
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "size") Integer size
    ){

        getPageable("""
            from FinancialReport fr order by fr.code
""","""
 Select count(fr) from FinancialReport fr
""",
        page,
        size,
        [:])

    }


    @GraphQLMutation
    Boolean updateExcludeList(  @GraphQLArgument(name = "id") UUID id,
                                @GraphQLArgument(name = "coaId") UUID coaId){



        def srcSubAccount = entityManager.find(SourceSubAccount.class,id)
        def coa = entityManager.find(ChartOfAccount.class,coaId)

        def match = srcSubAccount.excludes.find { it.motherAccount ==  coa}

        if(match)
        srcSubAccount.excludes.remove(match)
        else
        {
            def newItem = new SourceSubAccountExclude()
            newItem.sourceSubAccount = srcSubAccount
            newItem.motherAccount = coa
            srcSubAccount.excludes << newItem
        }


        entityManager.merge(srcSubAccount)

        true
    }


    @GraphQLMutation
    Boolean deleteMotherAccountSource(  @GraphQLArgument(name = "id") UUID id){

        def lineType = entityManager.find(SourceMotherAccount.class,id)
        entityManager.remove(lineType)
        true
    }

    @GraphQLMutation
    SourceMotherAccount upsertMotherAccountSource(
            @GraphQLArgument(name = "lineTypeId") UUID lineTypeId,
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    ) {

        def lineType = entityManager.find(LineType.class,lineTypeId)
        SourceMotherAccount sourceMotherAccount

        if(id){
            sourceMotherAccount = lineType.sourceMotherAccounts.find{ it.id == id}
            updateFromMap(sourceMotherAccount,fields)
        }else {
            sourceMotherAccount = new SourceMotherAccount()
            sourceMotherAccount.lineType = lineType
            sourceMotherAccount.code = generatorService.getNextValue(GeneratorType.FINANCIAL_REPORT_SOURCE_TYPE, {
                return "RST-" + StringUtils.leftPad(it.toString(), 6, "0")
            })

            updateFromMap(sourceMotherAccount,fields)

            lineType.sourceMotherAccounts << sourceMotherAccount
        }

        save(lineType.report)
        sourceMotherAccount
    }


    @GraphQLMutation
    Boolean deleteLineType(  @GraphQLArgument(name = "id") UUID id){

        def lineType = entityManager.find(SourceLineType.class,id)
        entityManager.remove(lineType)
        true
    }

    @GraphQLMutation
    SourceLineType upsertLineTypeSource(
            @GraphQLArgument(name = "lineTypeId") UUID lineTypeId,
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    ) {

        def lineType = entityManager.find(LineType.class,lineTypeId)
        SourceLineType sourceLineType

        if(id){
            sourceLineType = lineType.sourceLineTypes.find{ it.id == id}
            updateFromMap(sourceLineType,fields)
        }else {
            sourceLineType = new SourceLineType()
            sourceLineType.linetypeParent = lineType
            sourceLineType.code = generatorService.getNextValue(GeneratorType.FINANCIAL_REPORT_SOURCE_TYPE, {
                return "RST-" + StringUtils.leftPad(it.toString(), 6, "0")
            })

            updateFromMap(sourceLineType,fields)

            lineType.sourceLineTypes << sourceLineType
        }

        save(lineType.report)
        sourceLineType
    }


    @GraphQLMutation
    Boolean deleteSubAccountSource(  @GraphQLArgument(name = "id") UUID id){

        def lineType = entityManager.find(SourceSubAccount.class,id)
        entityManager.remove(lineType)
        true
    }

    @GraphQLMutation
    SourceSubAccount upsertSubaccountSource(
            @GraphQLArgument(name = "lineTypeId") UUID lineTypeId,
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    ) {


        def lineType = entityManager.find(LineType.class,lineTypeId)
        SourceSubAccount sourceSubAccount

        if(id){
            sourceSubAccount = lineType.sourceSubAccountList.find{ it.id == id}
            updateFromMap(sourceSubAccount,fields)
        }else {
            sourceSubAccount = new SourceSubAccount()
            sourceSubAccount.lineType = lineType
            sourceSubAccount.code = generatorService.getNextValue(GeneratorType.FINANCIAL_REPORT_SOURCE_TYPE, {
                return "RST-" + StringUtils.leftPad(it.toString(), 6, "0")
            })

            updateFromMap(sourceSubAccount,fields)

            lineType.sourceSubAccountList << sourceSubAccount
        }

        save(lineType.report)
        sourceSubAccount
    }


    @GraphQLMutation
    LineType upsertLineTypes(
            @GraphQLArgument(name = "fr") UUID fr,
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    ) {

        def financialReport = findOne(fr)

        LineType lineType

         if(id)
             lineType = financialReport.lineTypes.find { it.id == id}
        else
             {
                 lineType = new LineType()
                 lineType.report = financialReport
                 lineType.code = generatorService.getNextValue(GeneratorType.FINANCIAL_REPORT_LINE_TYPE, {
                     return "LT-" + StringUtils.leftPad(it.toString(), 6, "0")
                 })
                 financialReport.lineTypes << lineType
             }


        updateFromMap(lineType,fields)

        save(financialReport)
        lineType
    }

    @GraphQLMutation
    FinancialReport upsertFinancialReport(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    ) {

        upsertFromMap(id, fields, { FinancialReport entity, boolean forInsert ->
            if (forInsert) {
                entity.code = generatorService.getNextValue(GeneratorType.FINANCIAL_REPORT, {
                    return "FR-" + StringUtils.leftPad(it.toString(), 6, "0")
                })
            }
        })

    }


}
