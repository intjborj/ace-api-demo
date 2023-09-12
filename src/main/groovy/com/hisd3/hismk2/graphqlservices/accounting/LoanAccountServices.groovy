package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.accounting.Bank
import com.hisd3.hismk2.domain.accounting.JournalType
import com.hisd3.hismk2.domain.accounting.LOAN_AMORTIZATION_INTEGRATION
import com.hisd3.hismk2.domain.accounting.LedgerDocType
import com.hisd3.hismk2.domain.accounting.Loan
import com.hisd3.hismk2.domain.accounting.LoanAccounts
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.services.EntityObjectMapperService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.time.ZoneId
import java.time.format.DateTimeFormatter


@Transactional
@GraphQLApi
@Service
@Component
class LoanAccountServices extends AbstractDaoService<LoanAccounts>{

    LoanAccountServices(){
        super(LoanAccounts.class)
    }


    @Autowired
    EntityObjectMapperService entityObjectMapperService


    @GraphQLQuery(name = "loanAccountById")
    GraphQLRetVal<LoanAccounts> loanAccountById(
            @GraphQLArgument(name = "id") UUID id
    ) {
        try{
            if(id) {
                def result = findOne(id)
                return new GraphQLRetVal<LoanAccounts>(result,true,'Records found')
            }
            return new GraphQLRetVal<LoanAccounts>(new LoanAccounts(),false,'No records found.')
        }
        catch (e){
            return new GraphQLRetVal<LoanAccounts>(new LoanAccounts(),false,e.message)
        }

    }



    @GraphQLQuery(name="loanAccounts")
    Page<LoanAccounts> loanAccounts(
            @GraphQLArgument(name="filter") String filter,
            @GraphQLArgument(name="banks") List<String> banks = [],
            @GraphQLArgument(name="page") Integer page,
            @GraphQLArgument(name="size") Integer size
    ){
        Page<LoanAccounts> accountList = Page.empty()
        Map<String,Object> param = [:]

        String queryStr = "Select b from LoanAccounts b where  "
        String countQueryStr = "Select count(b) from LoanAccounts b where  "

        if(banks) {
            queryStr += "b.bank.bankname in (:banks) and"
            countQueryStr += "b.bank.bankname in (:banks) and"

            param.put("banks",banks)
        }

        queryStr += "(b.accountNo like concat('%',:filter,'%') or lower(b.accountName) like lower(concat('%',:filter,'%')))"
        countQueryStr += "(b.accountNo like concat('%',:filter,'%') or lower(b.accountName) like lower(concat('%',:filter,'%')))"

        param.put("filter",filter)

        def result = getPageable(
                queryStr,
                countQueryStr,
                page,
                size,
                param)

        if(result)
            accountList = result



        return accountList
    }


    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name = "upsertLoanAccounts")
    GraphQLRetVal<LoanAccounts> upsertLoanAccounts(
            @GraphQLArgument(name="fields") Map<String,Object> fields,
            @GraphQLArgument(name="id") UUID id
    ){

        try{
            if(id){
                def existing = findOne(id)
                if(existing) {
                    entityObjectMapperService.updateFromMap(existing, fields)
                    save(existing)
                    return new GraphQLRetVal<LoanAccounts>(existing, true, 'Successfully updated.')
                }
                return new GraphQLRetVal<LoanAccounts>(new LoanAccounts(), false, 'Record not found.')
            }
            else {
                LoanAccounts bankAccount = new LoanAccounts()
                entityObjectMapperService.updateFromMap(bankAccount,fields)
                save(bankAccount)
                return new GraphQLRetVal<LoanAccounts>(bankAccount, true, 'Successfully saved.')
            }
        }
       catch (e){
           return  new GraphQLRetVal<LoanAccounts>(new LoanAccounts(),false,e.message)
       }

    }


}
