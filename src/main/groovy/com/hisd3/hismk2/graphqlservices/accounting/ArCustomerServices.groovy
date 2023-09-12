package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.accounting.CustomerType
import com.hisd3.hismk2.domain.billing.BillingItemType
import com.hisd3.hismk2.graphqlservices.billing.CompanyAccountServices
import com.hisd3.hismk2.graphqlservices.inventory.SupplierService
import com.hisd3.hismk2.graphqlservices.pms.PatientService
import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLQuery
import org.apache.commons.lang3.StringUtils
import com.hisd3.hismk2.domain.accounting.ArCustomers
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.types.GraphQLResVal
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

import javax.transaction.Transactional


@Canonical
class OptionsDto {
    String value
    String label
}

@Service
@GraphQLApi
@Transactional(rollbackOn = Exception.class)
class ArCustomerServices extends  AbstractDaoService<ArCustomers> {
    ArCustomerServices (){
        super(ArCustomers.class)
    }

    @Autowired
    GeneratorService generatorService

    @Autowired
    CompanyAccountServices companyAccountServices

    @Autowired
    SupplierService supplierService

    @Autowired
    PatientService patientService


    // LOCAL FUNCTIONS --------------------------------------
    static List<OptionsDto> convertPageableToOptions(def data, String label, String value){
        List<OptionsDto> options = []
        data.each { it ->
            OptionsDto opt = [:]
            opt['label'] = it[label]
            opt['value'] = it[value]
            options.push(opt)
        }
        return options
    }


    // END OF LOCAL FUNCTIONS

    @GraphQLMutation(name = "createARCustomer")
    GraphQLResVal<ArCustomers> createARCustomer(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String,Object> fields
    ){
        try{
           def customer = upsertFromMap(id, fields, { ArCustomers entity, boolean forInsert ->
                if (forInsert) {
                    if(!entity.accountNo)
                    entity.accountNo = generatorService.getNextValue(GeneratorType.AR_CUSTOMER, {
                        return StringUtils.leftPad(it.toString(), 6, "0")
                    })
                    return entity
                }
                else
                    return entity
            })

            return new GraphQLResVal<ArCustomers>(customer, true, 'Customer data has been successfully saved. ')
        }
        catch (ignore){
            return new GraphQLResVal<ArCustomers>(null, false, 'Unable to save customer data. Please contact support for assistance.')
        }
    }

    @GraphQLQuery(name="findOneCustomer")
    ArCustomers findOneCustomer(
            @GraphQLArgument(name = "id") UUID id
    ){
        if(id)
            return findOne(id)
        return null
    }

    @GraphQLQuery(name="findAllCustomers")
    Page<ArCustomers> findAllCustomers(
            @GraphQLArgument(name = "type") String type,
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "size") Integer size
    ){
        String queryStr = """ from ArCustomers c where (
						lower(c.accountNo) like lower(concat('%',:search,'%')) or 
						lower(c.customerName) like lower(concat('%',:search,'%'))
              		) """
        Map<String,Object> params = [:]
        params['search'] = search

        if(type) {
            queryStr += """ and c.customerType = :type"""
            params['type'] = CustomerType.valueOf(type)
        }

        getPageable(
                """ Select c ${queryStr} order by c.accountNo""",
                """ Select count(c) ${queryStr} """,
                page,
                size,
                params
        )
    }







    @GraphQLQuery(name="findAllCustomerReference")
    List<OptionsDto> findAllCustomerReference(
            @GraphQLArgument(name = "type") String type,
            @GraphQLArgument(name = "search") String search

    ){
        try{
            switch (type){
                case 'HMO':
                    def company = companyAccountServices.companyAccounts(search,0,10)
                    if(company.content.size() > 0) {
                        return convertPageableToOptions(company.content,'companyname','id')
                    }
                    return []
                break;
                case 'CORPORATE':
                    def companies = companyAccountServices.companyAccounts(search,0,10)
                    if(companies.content.size() > 0) {
                        return convertPageableToOptions(companies.content,'companyname','id')
                    }
                    return []
                break;
                case 'PERSONAL':
                    def suppliers = supplierService.allSupplierPageable(search,50,0)
                    if(suppliers.content.size() > 0) {
                        return convertPageableToOptions(suppliers.content,'supplierFullname','id')
                    }
                    return []
                break;
                case 'PROMISSORY_NOTE':
                    def companies = companyAccountServices.companyAccounts(search,0,10)
                    if(companies.content.size() > 0) {
                        return convertPageableToOptions(companies.content,'companyname','id')
                    }
                    return []
                break;
                default:
                    return []
                break;
            }
        }
        catch (ignore){
            return []
        }
    }



}
