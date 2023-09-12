package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.accounting.ChartOfAccount
import com.hisd3.hismk2.domain.accounting.MotherAccount
import com.hisd3.hismk2.domain.accounting.SubAccountSetup
import com.hisd3.hismk2.domain.accounting.SubAccountType
import com.hisd3.hismk2.domain.billing.Discount
import com.hisd3.hismk2.domain.types.AutoIntegrateable
import com.hisd3.hismk2.domain.types.Subaccountable
import com.hisd3.hismk2.graphqlservices.DepartmentService
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.memoization.Memoize
import com.hisd3.hismk2.services.requestscope.ChartofAccountGenerator
import com.sun.org.apache.xpath.internal.operations.Bool
import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLContext
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.BooleanUtils
import org.apache.commons.lang3.StringUtils
import org.checkerframework.checker.units.qual.A
import org.reflections.Reflections
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.persistence.EntityManager

@Canonical
class CoaComponentContainer implements Serializable{

    @GraphQLQuery
    String code

    @GraphQLQuery
    UUID id

    @GraphQLQuery
    String description

    @GraphQLQuery
    String domain

    @GraphQLQuery
    String normalSide // DEBIT/CREDIT applicable to Mother account only
}

@Canonical
class ChartOfAccountGenerate implements Serializable{

    CoaComponentContainer motherAccount
    CoaComponentContainer subAccount
    CoaComponentContainer subSubAccount

    String accountType

    Boolean fromGenerator

    String code
    String getCode(){

        String concat = ""
            concat = StringUtils.defaultIfEmpty(motherAccount?.code,"0000")
            concat += "-" + StringUtils.defaultIfEmpty(subAccount?.code,"0000")
            concat += "-" + StringUtils.defaultIfEmpty(subSubAccount?.code,"0000")
        return concat
    }

    String description
    String getDescription(){

        String concat = ""
        concat = StringUtils.defaultIfEmpty(motherAccount?.description,"")

        if(subAccount?.description)
        concat += "-" + subAccount?.description?:""

        if(subSubAccount?.description)
        concat += "-" + subSubAccount?.description?:""

        return concat
    }



}

@Canonical
class ChartOfAccountGenerateGl extends ChartOfAccountGenerate{


}

@Service
@GraphQLApi
class SubAccountSetupService extends AbstractDaoService<SubAccountSetup> {

    @Autowired
    ChartOfAccountServices chartOfAccountServices

    @Autowired
    EntityManager entityManager

    @Autowired
    DepartmentService departmentService



    @Autowired
    ChartofAccountGenerator chartofAccountGenerator


    SubAccountSetupService( ) {
        super(SubAccountSetup.class)

    }



    @GraphQLQuery(name = "getAllChartOfAccountGenerate")
    List<ChartOfAccountGenerate> getAllChartOfAccountGenerate(
            @GraphQLArgument(name = "accountType")    String accountType,
            @GraphQLArgument(name = "motherAccountCode")  String motherAccountCode,
            @GraphQLArgument(name = "description") String description,
            @GraphQLArgument(name = "subaccountType") String subaccountType,
            @GraphQLArgument(name = "department") String department,
            @GraphQLArgument(name = "excludeMotherAccount") Boolean excludeMotherAccount=false
    ) { // department flatten code

       def a =  chartofAccountGenerator.getAllChartOfAccountGenerate(accountType,
        motherAccountCode,
        description,
        subaccountType,
        department,
        excludeMotherAccount)

        /*
         Yaw lang kay sagbot sa logs hehehe
        a =  chartofAccountGenerator.getAllChartOfAccountGenerate(accountType,
                motherAccountCode,
                description,
                subaccountType,
                department,
                excludeMotherAccount)

        a =  chartofAccountGenerator.getAllChartOfAccountGenerate(accountType,
                motherAccountCode,
                description,
                subaccountType,
                department,
                excludeMotherAccount)

        a =  chartofAccountGenerator.getAllChartOfAccountGenerate(accountType,
                motherAccountCode,
                description,
                subaccountType,
                department,
                excludeMotherAccount)
        */
        a

    }


    @GraphQLQuery(name = "motherAccountsListWithNoSetup")
    List<ChartOfAccount> motherAccountsListWithNoSetup(){
        List<ChartOfAccount> result = []
        Set<String> usedMotherAccount = []
        def subaccountSetups = getSetupBySubAccountTypeAll()

        subaccountSetups.each {

            it.motherAccounts.each {
                usedMotherAccount.add(it.chartOfAccount.accountCode)
            }
        }




        chartOfAccountServices.findAll().findAll {BooleanUtils.isNotTrue(it.deprecated)  }.each {
            if(!usedMotherAccount.contains(it.accountCode))
                result << it
        }

        result
    }

    @GraphQLQuery(name = "motherAccountsList")
    List<String> motherAccountsList(
            @GraphQLContext  SubAccountSetup subAccountSetup
    ) {

        List<String> ms = []

        subAccountSetup.motherAccounts.each {
            ms << it.chartOfAccount.accountCode + "-" + it.chartOfAccount.description
        }

        ms
    }


    SubAccountSetup getSetupBySubAccountByCode(
            String code
    ) {

        createQuery("Select sub from SubAccountSetup sub  where sub.subaccountCode=:code order by sub.subaccountCode, sub.createdDate",
                [code: code])
                .resultList.find()
    }

    @GraphQLQuery(name = "getSetupBySubAccountType")
    List<SubAccountSetup> getSetupBySubAccountType(
            @GraphQLArgument(name = "subaccountType") String subaccountType,
            @GraphQLArgument(name = "filter") String filter = ''

    ) {

        createQuery("Select sub from SubAccountSetup sub  where sub.subaccountType=:subaccountType and (lower(sub.description) like lower(concat('%',:filter,'%'))) order by sub.subaccountCode, sub.createdDate",
        [
                subaccountType: SubAccountType.valueOf(subaccountType),
                filter: filter
        ]).resultList
    }

    @GraphQLQuery(name = "getSetupBySubAccountTypeAll")
    List<SubAccountSetup> getSetupBySubAccountTypeAll() {
        createQuery("Select sub from SubAccountSetup sub  where coalesce(sub.attrInactive,false) = false   order by sub.description, sub.createdDate",
                [:])
                .resultList
    }

    @GraphQLQuery(name = "subaccountTypeAll")
    List<Map<String,String>> subaccountTypeAll() {
       return SubAccountType.values().collect {
             ["name":getDescSubaccountType(it),
              "value":it.name()]
       }
    }

    String getDescSubaccountType(SubAccountType subAccountType) {

        switch (subAccountType) {
            case SubAccountType.INCOME:
                return "A/R Income Transaction Types"
                break
            case SubAccountType.EXPENSE:
                return "AP Expense Transaction Types"
                break
            case SubAccountType.ADJUSTMENTS:
                return "Debit and Credit Adjustments"
                break
            case SubAccountType.REVENUEITEMS:
                return "Revenue Items"
                break
            case SubAccountType.OTHERPAYMENTS:
                return "Other Payment Types"
                break
            case SubAccountType.PETTYCASH:
                return "Petty Cash Transaction Types"
                break
            case SubAccountType.QUANTITYADJUSTMENTS:
                return "Quantity Adjustment Types"
                break
            case SubAccountType.ASSETCLASS:
                return "Asset Classification"
                break
            case SubAccountType.OTHERENTITIES:
                return "Other Entities"
                break
                ""
        }
    }

    @GraphQLQuery(name = "subaccountTypeDesc")
    String subaccountTypeDesc(@GraphQLContext SubAccountSetup subAccountSetup ) {
        return getDescSubaccountType(subAccountSetup.subaccountType)
    }

    @GraphQLQuery(name = "getSubaccountForParent")
    List<SubAccountSetup> getSubaccountForParent(

    ) {
        createQuery("Select sub from SubAccountSetup sub  where (sub.sourceDomain is null or length(trim(sub.sourceDomain)) = 0 )  and subaccountType = :subaccountType order by sub.subaccountCode, sub.createdDate",
                [subaccountType:SubAccountType.OTHERENTITIES])
                .resultList


    }


    List<SubAccountSetup> getActiveSubAccount( ) {


        createQuery("Select sub from SubAccountSetup sub  where (sub.attrInactive is null or sub.attrInactive=false)  order by sub.subaccountCode, sub.createdDate",[:])
                .resultList

    }


    @GraphQLQuery(name = "getAutoIntegrateableFromDomain")
    List<String> getAutoIntegrateableFromDomain(

    ) {

        Reflections reflections = new Reflections("com.hisd3.hismk2.domain")
        Set<Class<? extends AutoIntegrateable>> subTypes = reflections.getSubTypesOf(AutoIntegrateable.class)

        subTypes.collect {
            it.name
        }

    }


    @GraphQLQuery(name = "getSubaccountableFromDomain")
    List<String> getSubaccountableFromDomain(

    ) {

        Reflections reflections = new Reflections("com.hisd3.hismk2.domain")
        Set<Class<? extends Subaccountable>> subTypes = reflections.getSubTypesOf(Subaccountable.class)

        subTypes.collect {
            it.name
        }

    }

    @GraphQLQuery(name = "getFlattenDepartment")
    List<Subaccountable> getFlattenDepartment(){
          departmentService.findAllSortedByCodeAndFlatten(null)
    }




    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name = "upsertSubAccount")
    SubAccountSetup upsertSubAccount(
            @GraphQLArgument(name = "fields") Map<String, Object> fields,
            @GraphQLArgument(name = "id") UUID id
    ) {
       def entity = upsertFromMap(id, fields, { SubAccountSetup entity, boolean forInsert ->
            if (forInsert) {

                entity.description = entity.description.toUpperCase()
                entity.subaccountCode = entity.subaccountCode.toUpperCase()
            }
        })

        def coaList = chartOfAccountServices.findAll().findAll {
            BooleanUtils.isNotTrue(it.deprecated)
        }.toSorted {a,b ->
            a.accountCode <=> b.accountCode
        }

        def _motherAccounts = fields.get("_motherAccounts",null) as List<String>

        if(_motherAccounts ){

            entity.motherAccounts.clear()
            _motherAccounts.each {code->
                def matchMotherAccounts = coaList.findAll {
                    it.accountCode == code
                }

                matchMotherAccounts.each {coa ->
                    def mo = new MotherAccount()
                    mo.subAccount = entity
                    mo.chartOfAccount =coa

                            entity.motherAccounts << mo
                }


            }

        }
        else {
            entity.motherAccounts.clear()
        }

        save(entity)

    }
}
