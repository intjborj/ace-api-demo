package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.IntegrationTemplate
import com.hisd3.hismk2.domain.SubAccountHolder
import com.hisd3.hismk2.domain.accounting.*
import com.hisd3.hismk2.domain.types.AutoIntegrateable
import com.hisd3.hismk2.domain.types.Subaccountable
import com.hisd3.hismk2.graphqlservices.DepartmentService
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.services.EntityObjectMapperService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.BooleanUtils
import org.apache.commons.lang3.StringUtils
import org.reflections.ReflectionUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import java.lang.reflect.Field
import java.time.Instant

interface AutoIntegrateableInitilizer<T extends AutoIntegrateable> {

    def void init(T autoIntegrateable,List<List<T>> multiple)

}
@Service
@GraphQLApi
class IntegrationServices extends AbstractDaoService<Integration> {


    IntegrationServices() {
        super(Integration.class)
    }


    @Autowired
    SubAccountSetupService subAccountSetupService

    @Autowired
    UserRepository userRepository

    @Autowired
    EmployeeRepository employeeRepository


    @Autowired
    DepartmentService departmentService

    @Autowired
    EntityObjectMapperService entityObjectMapperService

    @PersistenceContext
    EntityManager entityManager




    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name = "testFromJsonGenerateAutoEntries", description = "insert Integrations from JSON")
    HeaderLedger testFromJsonGenerateAutoEntries(
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    ) {

        IntegrationTemplate integrationTemplate = new IntegrationTemplate()
        entityObjectMapperService.updateFromMap(integrationTemplate,fields)

        generateAutoEntries(integrationTemplate){it, multipleData->


        }
    }


    /*
     Multiple flag is ignored
     */
   def  <T extends AutoIntegrateable>  HeaderLedger generateAutoEntries(T autoIntegrateable,AutoIntegrateableInitilizer<T> init){


        List<List<T>> multipleData= []
        init.init(autoIntegrateable,multipleData)
        def username = SecurityUtils.currentLogin()
        def user = userRepository.findOneByLogin(username)
        def emp = employeeRepository.findOneByUser(user)
        def department = emp.departmentOfDuty


        String tagValue = autoIntegrateable.flagValue


        if(StringUtils.isBlank(tagValue))
            throw  new Exception("TagValue is not found")


//        List<Integration> matchList = getIntegrationByDomainAndTagValueList(autoIntegrateable.domain,autoIntegrateable.flagValue)
       Integration match = getIntegrationByDomainAndTagValue(autoIntegrateable.domain,autoIntegrateable.flagValue)
        if(!match)
            throw  new Exception("No Integration Rules for ${autoIntegrateable.domain} and ${autoIntegrateable.flagValue}")


        //validating entries

        match.integrationItems.findAll { BooleanUtils.isNotTrue(it.multiple) }. each { item->


            // validate sourceColumn
            if(StringUtils.isBlank(item.sourceColumn)){
                throw new Exception("Source Column not specified for ${item.journalAccount.subAccountName}")
            }


            // validate if source column is not null
            Object srcColValue = autoIntegrateable[item.sourceColumn]
            if(srcColValue==null){
                throw new Exception("Source Column Value for ${item.journalAccount.subAccountName} is null")
            }


            String subAccountCode =  item.journalAccount?.subAccount?.code
             if(StringUtils.equalsIgnoreCase(subAccountCode,"####")){
                 // needs parameter
                 String domain = item.journalAccount.subAccount.domain

                 if(!StringUtils.equalsIgnoreCase(domain,Department.class.name)){
                     // its not a department will look for a parameter
                     String param = item.details[domain]
                     if(StringUtils.isBlank(param))
                     {
                         throw new Exception("Parameter required for ${domain}")
                     }

                     Object paramValue = autoIntegrateable[param]

                     if(!paramValue){
                         throw new Exception("Parameter ${param} needs a Value")
                     }
                 }else {
                       String param = item.details[domain]
                       if(param){
                         Object paramValue = autoIntegrateable[param]

                         if(!paramValue){
                             throw new Exception("Parameter ${param} needs a Value")
                         }
                     }
                 }
             }




            String subSubAccountCode = item.journalAccount?.subSubAccount?.code
            if(StringUtils.equalsIgnoreCase(subSubAccountCode,"####")){
                // needs parameter
                String domain = item.journalAccount.subSubAccount.domain

                if(!StringUtils.equalsIgnoreCase(domain,Department.class.name)){
                    // its not a department will look for a parameter
                    String param = item.details[domain]
                    if(StringUtils.isBlank(param))
                    {
                        throw new Exception("Parameter required for ${domain}")
                    }

                    Object paramValue = autoIntegrateable[param]

                    if(!paramValue){
                        throw new Exception("Parameter ${param} needs a Value")
                    }
                }else {
                    String param = item.details[domain]
                    if(param){
                        Object paramValue = autoIntegrateable[param]

                        if(!paramValue){
                            throw new Exception("Parameter ${param} needs a Value")
                        }
                    }
                }
            }

        }


        // Generate the entries

        HeaderLedger header = new HeaderLedger()
        header.transactionDate = Instant.now()

        // Values correspond to Temporary Entry
        header.docType = LedgerDocType.XX
        header.journalType = JournalType.XXXX
        header.docnum = "AUTO"

        autoIntegrateable.details.each { k,v ->
            header.details.put(k,v)
        }


        match.integrationItems.findAll { BooleanUtils.isNotTrue(it.multiple) }.each { item ->
            Ledger ledger = new Ledger()

            def coa =   createCoaFromItem(autoIntegrateable,item,department)

            ledger.debit = 0.0
            ledger.credit = 0.0

            String normalSide = item.journalAccount.motherAccount.normalSide
            BigDecimal value = (BigDecimal)autoIntegrateable[item.sourceColumn]
            if(normalSide == "DEBIT"){
                 if(value >= 0){
                     ledger.debit = value
                 }
                else {
                     ledger.credit = value.abs()
                 }
            }
            else {
                if(value >= 0){
                    ledger.credit = value
                }
                else {
                    ledger.debit = value.abs()
                }
            }

            ledger.journalAccount = coa

            ledger.header = header

            if(!(ledger.debit == 0.0 && ledger.credit == 0.0))
            header.ledger << ledger
        }



       match.integrationItems.findAll { BooleanUtils.isTrue(it.multiple) }.eachWithIndex { IntegrationItem entry, int i ->
           // def iii = entry
           //println( "entry => " + entry.sourceColumn)
           //println( "condition => " + "${i < multipleData.size()}")
           //println( "size => " + multipleData.size())
           if(i < multipleData.size()){

                 def dataForItem = multipleData.get(i)


                 dataForItem.each {tmpAutoIntegrateable ->

                     Ledger ledger = new Ledger()

                     def coa =   createCoaFromItem(tmpAutoIntegrateable,entry,department)

                     ledger.debit = 0.0
                     ledger.credit = 0.0

                     String normalSide = entry.journalAccount.motherAccount.normalSide
                     BigDecimal value = (BigDecimal)tmpAutoIntegrateable[entry.sourceColumn]
                     //println(coa.code + " : => " + value.toPlainString() + " : " + normalSide + " - " + entry.sourceColumn)
                     if(normalSide == "DEBIT"){
                         if(value >= 0){
                             ledger.debit = value
                         }
                         else {
                             ledger.credit = value.abs()
                         }
                     }
                     else {
                         if(value >= 0){
                             ledger.credit = value
                         }
                         else {
                             ledger.debit = value.abs()
                         }
                     }

                     ledger.journalAccount = coa

                     ledger.header = header

                     if(!(ledger.debit == 0.0 && ledger.credit == 0.0))
                         header.ledger << ledger

                 }



             }
       }

        return  header
    }



      ChartOfAccountGenerate  createCoaFromItem(AutoIntegrateable autoIntegrateable,IntegrationItem item,Department department) {


        ChartOfAccountGenerate coa = new ChartOfAccountGenerate()
        coa.motherAccount = item.journalAccount.motherAccount

        // Testing for SubAccount

        String subAccountCode =  item.journalAccount?.subAccount?.code

        if(StringUtils.equalsIgnoreCase(subAccountCode,"####")){
            // needs parameter
            String domain = item.journalAccount.subAccount.domain

            if(!StringUtils.equalsIgnoreCase(domain,Department.class.name)){
                // its not a department will look for a parameter
                String param = item.details[domain]
                Subaccountable paramValue = (Subaccountable) autoIntegrateable[param]


                if(paramValue instanceof SubAccountHolder)
                {
                    // this is from a subaccountHolder

                    String targetDomain = domain
                    UUID targetId = paramValue.id

                    if(!targetId)
                        throw new Exception("Subaccount holder id not found")

                    Subaccountable realValue = entityManager.find(Class.forName(targetDomain),targetId)

                    if(!realValue)
                        throw new Exception("Subaccount holder instance not found ${targetId.toString()} - ${targetDomain}")

                    coa.subAccount = new CoaComponentContainer(realValue.code,
                            realValue.id,
                            realValue.description,
                            realValue.domain,
                            ""
                    )

                }else {
                    coa.subAccount = new CoaComponentContainer(paramValue.code,
                            paramValue.id,
                            paramValue.description,
                            paramValue.domain,
                            ""
                    )
                }


            }
            else {

                // is a department

                Department target = department

                // check if it has an override ... if override is null
                String param = item.details[domain]
                if(param){
                    Object paramValue = autoIntegrateable[param]

                    if(!paramValue){
                        throw new Exception("Parameter ${param} needs a Value")
                    }
                    target = (Department) paramValue
                }



                coa.subAccount = new CoaComponentContainer( departmentService.generatePrefixParentDepartment(target),
                        target.id,
                        target.description,
                        target.class.name,
                        ""
                )

            }
        }
        else {

            // just copy
            coa.subAccount = new CoaComponentContainer( item.journalAccount.subAccount.code,
                    item.journalAccount.subAccount.id,
                    item.journalAccount.subAccount.description,
                    item.journalAccount.subAccount.domain,
                    ""
            )
        }



        String subSubAccountCode =  item.journalAccount?.subSubAccount?.code

        if(StringUtils.equalsIgnoreCase(subSubAccountCode,"####")){
            // needs parameter
            String domain = item.journalAccount.subSubAccount.domain

            if(!StringUtils.equalsIgnoreCase(domain,Department.class.name)){
                // its not a department will look for a parameter
                String param = item.details[domain]
                Subaccountable paramValue = (Subaccountable) autoIntegrateable[param]

                if(paramValue instanceof SubAccountHolder)
                {
                    // this is from a subaccountHolder

                    String targetDomain = domain
                    UUID targetId = paramValue.id

                    if(!targetId)
                        throw new Exception("Subaccount holder id not found")

                    Subaccountable realValue = entityManager.find(Class.forName(targetDomain),targetId)

                    if(!realValue)
                        throw new Exception("Subaccount holder instance not found ${targetId.toString()} - ${targetDomain}")

                    coa.subSubAccount = new CoaComponentContainer(realValue.code,
                            realValue.id,
                            realValue.description,
                            realValue.domain,
                            ""
                    )

                }else {
                    coa.subSubAccount = new CoaComponentContainer(paramValue.code,
                            paramValue.id,
                            paramValue.description,
                            paramValue.domain,
                            ""
                    )
                }

            }
            else {

                // is a department

                Department target = department

                // check if it has an override ... if override is null
                String param = item.details[domain]
                if(param){
                    Object paramValue = autoIntegrateable[param]

                    if(!paramValue){
                        throw new Exception("Parameter ${param} needs a Value")
                    }
                    target = (Department) paramValue
                }



                coa.subSubAccount = new CoaComponentContainer( departmentService.generatePrefixParentDepartment(target),
                        target.id,
                        target.description,
                        target.class.name,
                        ""
                )

            }
        }else {

            // just copy
            coa.subSubAccount = new CoaComponentContainer( item.journalAccount.subSubAccount.code,
                    item.journalAccount.subSubAccount.id,
                    item.journalAccount.subSubAccount.description,
                    item.journalAccount.subSubAccount.domain,
                    ""
            )
        }
        return coa
    }



    Integration getIntegrationByDomainAndTagValue(String domain,String tagValue){
        createQuery("from Integration i where i.domain=:domain and i.flagValue=:flagValue order by i.orderPriority ",
        [flagValue:tagValue,
         domain:domain])
        .setMaxResults(1)
        .resultList.find()
    }

    List<Integration> getIntegrationByDomainAndTagValueList(String domain,String tagValue){
        createQuery("from Integration i where i.domain=:domain and i.flagValue=:flagValue order by i.orderPriority ",
                [flagValue:tagValue,
                 domain:domain])
                .resultList
    }

    @GraphQLQuery(name = "getStringFieldsFromDomain")
    List<String> getStringFieldsFromDomain(
            @GraphQLArgument(name = "domain") String domain
    ) {
        def classType = Class.forName(domain)
        Set<Field> fields = ReflectionUtils.getAllFields(classType,ReflectionUtils.withTypeAssignableTo(String.class))

        fields.collect {
              it.name
        }.toSorted {
            a,b ->
                a <=> b
        }

    }

    @GraphQLQuery(name = "getBigDecimalFieldsFromDomain")
    List<String> getBigDecimalFieldsFromDomain(
            @GraphQLArgument(name = "domain") String domain
    ) {
        def classType = Class.forName(domain)
        Set<Field> fields = ReflectionUtils.getAllFields(classType,ReflectionUtils.withTypeAssignableTo(BigDecimal.class))

        fields.collect {
            it.name
        }.toSorted {
            a,b ->
                a <=> b
        }

    }


    @GraphQLQuery(name = "getSpecificFieldsFromDomain")
    List<String> getSpecificFieldsFromDomain(
            @GraphQLArgument(name = "domain") String domain,
            @GraphQLArgument(name = "target") String target
    ) {
        def classType = Class.forName(domain)

        if(StringUtils.equalsIgnoreCase(target,Department.class.name)){
            def targetType = Class.forName(target)
            Set<Field> fields = ReflectionUtils.getAllFields(classType,ReflectionUtils.withTypeAssignableTo(targetType))

            fields.collect {
                it.name
            }.toSorted {
                a,b ->
                    a <=> b
            }
        }
        else {
            Set<Field> fields = ReflectionUtils.getAllFields(classType,ReflectionUtils.withTypeAssignableTo(Subaccountable))
            fields.collect {
                it.name
            }.toSorted {
                a,b ->
                    a <=> b
            }
        }


    }



    @GraphQLQuery(name = "integrationList", description = "Integration List")
    List<Integration> integrationList() {
        findAll().sort { it.orderPriority }
    }

    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name = "addSubAccountToIntegration")
    Boolean addSubAccountToIntegration(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "subAccountId") UUID subAccountId

    ) {

        def subAccountSetup = subAccountSetupService.findOne(subAccountId)
        def integration =  findOne(id)

        subAccountSetup.motherAccounts.each {

            CoaPattern pattern = new CoaPattern()
            pattern.subAccountSetupId = subAccountSetup.id
            pattern.subAccountName = subAccountSetup.description
            pattern.motherAccount.normalSide = it.chartOfAccount.normalSide.name()
            pattern.motherAccount.id = it.id
            pattern.motherAccount.code= it.chartOfAccount.accountCode
            pattern.motherAccount.description =it.chartOfAccount.description
            pattern.motherAccount.domain = it.class.name

            IntegrationItem integrationItem = new IntegrationItem()
            integrationItem.integration= integration


            if(StringUtils.isNotBlank(subAccountSetup.sourceDomain)){

                if(!(subAccountSetup.subaccountParent || subAccountSetup.includeDepartment)) {
                    pattern.subAccount.id = UUID.randomUUID()
                    pattern.subAccount.code = "####"
                    pattern.subAccount.description = subAccountSetup.description
                    pattern.subAccount.domain = subAccountSetup.sourceDomain
                }
                else {
                    if(subAccountSetup.subaccountParent){

                        pattern.subAccount.id = subAccountSetup.subaccountParent.id
                        pattern.subAccount.code= subAccountSetup.subaccountParent.subaccountCode
                        pattern.subAccount.description =subAccountSetup.subaccountParent.description
                        pattern.subAccount.domain = subAccountSetup.subaccountParent.class.name

                        pattern.subSubAccount.id = UUID.randomUUID()
                        pattern.subSubAccount.code = "####"
                        pattern.subSubAccount.description = subAccountSetup.description
                        pattern.subSubAccount.domain = subAccountSetup.sourceDomain


                    } else if(subAccountSetup.includeDepartment){

                        pattern.subAccount.id = UUID.randomUUID()
                        pattern.subAccount.code= "####"
                        pattern.subAccount.description = "Department"
                        pattern.subAccount.domain = Department.class.name

                        pattern.subSubAccount.id = UUID.randomUUID()
                        pattern.subSubAccount.code = "####"
                        pattern.subSubAccount.description = subAccountSetup.description
                        pattern.subSubAccount.domain = subAccountSetup.sourceDomain
                    }
                }

            }
            else {
                if(!(subAccountSetup.subaccountParent || subAccountSetup.includeDepartment)){
                    // not 3rd level
                    pattern.subAccount.id = subAccountSetup.id
                    pattern.subAccount.code= subAccountSetup.subaccountCode
                    pattern.subAccount.description =subAccountSetup.description
                    pattern.subAccount.domain = subAccountSetup.class.name

                }

                else {

                     if(subAccountSetup.subaccountParent){
                         pattern.subAccount.id = subAccountSetup.subaccountParent.id
                         pattern.subAccount.code= subAccountSetup.subaccountParent.subaccountCode
                         pattern.subAccount.description =subAccountSetup.subaccountParent.description
                         pattern.subAccount.domain = subAccountSetup.subaccountParent.class.name


                         pattern.subSubAccount.id = subAccountSetup.id
                         pattern.subSubAccount.code= subAccountSetup.subaccountCode
                         pattern.subSubAccount.description =subAccountSetup.description
                         pattern.subSubAccount.domain = subAccountSetup.class.name
                     }
                    else if(subAccountSetup.includeDepartment){
                         pattern.subAccount.id = UUID.randomUUID()
                         pattern.subAccount.code= "####"
                         pattern.subAccount.description = "Department"
                         pattern.subAccount.domain = Department.class.name

                         pattern.subSubAccount.id = subAccountSetup.id
                         pattern.subSubAccount.code= subAccountSetup.subaccountCode
                         pattern.subSubAccount.description =subAccountSetup.description
                         pattern.subSubAccount.domain = subAccountSetup.class.name
                     }

                }
            }

            integrationItem.journalAccount = pattern
            integration.integrationItems << integrationItem
        }

        save(integration)
        true
    }

    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name = "upsertIntegration", description = "insert Integrations")
    Boolean upsertIntegration(
            @GraphQLArgument(name = "fields") Map<String, Object> fields,
            @GraphQLArgument(name = "id") UUID id
    ) {
        upsertFromMap(id, fields, { Integration entity, boolean forInsert ->

            if(forInsert)
                entity.orderPriority = 0

        })

        if(fields.containsKey("reload"))
            return true


        return false
    }

    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name = "deleteIntegration", description = "insert Integrations")
    Boolean deleteIntegration(  @GraphQLArgument(name = "integrationId") UUID integrationId){
        def integration = findOne(integrationId)
        delete(integration)
        true
    }

    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name = "deleteIntegrationItem", description = "insert Integrations")
    Boolean deleteIntegrationItem(
            @GraphQLArgument(name = "integrationId") UUID integrationId,
            @GraphQLArgument(name = "integrationItemId") UUID integrationItemId
    ) {
        def integration = findOne(integrationId)

       integration.integrationItems.removeAll { IntegrationItem item->
            item.id == integrationItemId
        }

        save(integration)
         return true
    }
    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name = "updateIntegrationItem", description = "insert Integrations")
    Boolean upsertIntegrationItem(
            @GraphQLArgument(name = "fields") Map<String, Object> fields,
            @GraphQLArgument(name = "integrationId") UUID integrationId,
            @GraphQLArgument(name = "integrationItemId") UUID integrationItemId
    ) {

        def integration = findOne(integrationId)

        def item = integration.integrationItems.find { IntegrationItem item->
             item.id == integrationItemId
        }

        if(item){
            Map<String,String> tmp = [:]

            item.details.each {k,v ->
                tmp.put(k,v)
            }
            updateFromMap(item,fields)

            tmp.each { k,v ->
                     if(!item.details.containsKey(k))
                         item.details[k] = v
             }

        }

        save(integration)

        if(fields.containsKey("reload"))
            return true


        return false
    }


    @GraphQLQuery(name="integrationById")
    Integration integrationById(
            @GraphQLArgument(name="id") UUID id
    ){
        try{
            findOne(id)
        }
        catch (ignored){
            return null
        }
    }

    @GraphQLMutation(name="transferIntegration")
    Boolean transferIntegration(
            @GraphQLArgument(name="id") UUID id,
            @GraphQLArgument(name="fields") Map<String,Object> fields
    ){
        if(id && fields){
            def upsert = upsertFromObjectMapper(id,fields){
                it,bool ->
                        return true
            }
            return upsert
        }
        return false
    }

}
