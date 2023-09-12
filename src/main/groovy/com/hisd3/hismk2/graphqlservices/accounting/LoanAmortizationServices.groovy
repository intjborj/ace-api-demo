package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.accounting.AccReceivableGroupParam
import com.hisd3.hismk2.domain.accounting.JournalType
import com.hisd3.hismk2.domain.accounting.LOAN_AMORTIZATION_INTEGRATION
import com.hisd3.hismk2.domain.accounting.LOAN_INTEGRATION
import com.hisd3.hismk2.domain.accounting.LedgerDocType
import com.hisd3.hismk2.domain.accounting.Loan
import com.hisd3.hismk2.domain.accounting.LoanAccounts
import com.hisd3.hismk2.domain.accounting.LoanAmortization
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.services.EntityObjectMapperService
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.time.ZoneId
import java.time.format.DateTimeFormatter


@GraphQLApi
@Service
@Component
class LoanAmortizationServices extends AbstractDaoService<LoanAmortization>{
    LoanAmortizationServices(){
        super(LoanAmortization.class)
    }

    @Autowired
    EntityObjectMapperService entityObjectMapperService

    @Autowired
    GeneratorService generatorService

    @Autowired
    IntegrationServices integrationServices

    @Autowired
    LedgerServices ledgerServices

    @Autowired
    AccountReceivableServices accountReceivableServices

    @Transactional(rollbackFor = Exception.class)
    @Async
    GraphQLRetVal<Boolean> addLoanAmortization(Loan loan, List<Map<String,Object>> loanSchedule){

        try{
            if(loanSchedule){
                Integer count = 1
                loanSchedule.each {
                    it->
                        LoanAmortization details = new LoanAmortization()
                        entityObjectMapperService.updateFromMap(details,it)
                        details.recordNo = generatorService.getNextValue(GeneratorType.LOAN_RECORD, {
                            return StringUtils.leftPad(it.toString(), 6, "0")
                        })
                        details.orderNo = count++
                        details.loan = loan
                        save(details)
                }
            }
            return new GraphQLRetVal<Boolean>(true,true,'Success')
        }
        catch (e){
            return new GraphQLRetVal<Boolean>(false,false,e.message)
        }

    }

    @GraphQLQuery(name="getLoanScheduleById")
    Page<LoanAmortization> getLoanScheduleById(
            @GraphQLArgument(name="id") UUID id,
            @GraphQLArgument(name="filter") String filter,
                @GraphQLArgument(name="page") Integer page,
                @GraphQLArgument(name="size") Integer size
    ){
        Page<LoanAmortization> accountList = Page.empty()
        Map<String,Object> param = [:]
        param.put("id",id)

        String queryStr = "Select b from LoanAmortization b where b.loan.id = :id and "
        String countQueryStr = "Select count(b) from LoanAmortization b where b.loan.id = :id and "

        queryStr += "(b.referenceNo like lower(concat('%',:filter,'%')) or b.recordNo like lower(concat('%',:filter,'%'))) order by b.recordNo"
        countQueryStr += "(b.referenceNo like lower(concat('%',:filter,'%')) or b.recordNo like lower(concat('%',:filter,'%')))"

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

    @GraphQLQuery(name="loanMViewPaidLoan")
    GraphQLRetVal<List<Map<String,Object>>> loanMViewPaidLoan(@GraphQLArgument(name="id") UUID id){
        try{
            if(id) {
                def loanAmortization = findOne(id)
                if(loanAmortization) {
                    List<Map<String,Object>> entry = []

                    def headerLedger =	integrationServices.generateAutoEntries(loanAmortization){it, nul ->
                        it.flagValue = LOAN_AMORTIZATION_INTEGRATION.LOANM_PAYMENT.name()
                        it.bank = it.loan.bankAccount.bank
                    }

                    if(headerLedger) {
                        headerLedger.ledger.each {
                            it ->
                                Map<String, Object> rows = [:]
                                rows["description"] = it['journalAccount']["description"]
                                rows["debit"] = it['debit']
                                rows["credit"] = it['credit']
                                entry.push(rows)
                        }
                        entry.sort{it['debit']}.reverse(true)
                        return new GraphQLRetVal<List<Map<String,Object>>>(entry, true, 'Success.')
                    }
                    return  new GraphQLRetVal<List<Map<String,Object>>>([],false,'No records found.')
                }
            }
            return  new GraphQLRetVal<List<Map<String,Object>>>([],false,'No records found.')
        }
        catch (e){
            return  new GraphQLRetVal<List<Map<String,Object>>>([],false,e.message)
        }
    }

    @GraphQLMutation(name="loanMPaidLoan")
    GraphQLRetVal<Boolean> loanMPaidLoan(@GraphQLArgument(name="id") UUID id){
        try {

            def loanAmortization = findOne(id)
            if (loanAmortization) {
                def yearFormat = DateTimeFormatter.ofPattern("yyyy")

                def headerLedger =	integrationServices.generateAutoEntries(loanAmortization){it, nul ->
                    it.flagValue = LOAN_AMORTIZATION_INTEGRATION.LOANM_PAYMENT.name()
                    it.bank = it.loan.bankAccount.bank
                }

                Map<String,String> details = [:]
                loanAmortization.details.each { k,v ->
                    details[k] = v
                }

                def convertedStartDate = accountReceivableServices.dateToInstantConverter(loanAmortization.loan.startDate)
                def convertedPaymentDate = accountReceivableServices.dateToInstantConverter(loanAmortization.loan.startDate)

                details["LOAN_AMORTIZATION_ID"] = loanAmortization.id.toString()
                def pHeader =	ledgerServices.persistHeaderLedger(headerLedger,
                        "LOAN ${convertedStartDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${loanAmortization.loan.loanNo}",
                        "LOAN ${loanAmortization.loan.loanNo} - ${loanAmortization.loan.bankAccount.accountNo}",
                        "PAID ${loanAmortization.payment} FOR PAYMENT NO: ${loanAmortization.orderNo}",
                        LedgerDocType.JV,
                        JournalType.GENERAL,
                        convertedPaymentDate,
                        details)

                loanAmortization.postedLedger = pHeader.id
                save(loanAmortization)
                return  new GraphQLRetVal<Boolean>(true,true,'Payment Successful.')
            }
            return new GraphQLRetVal<Boolean>(false, false, 'No records found.')
        }
        catch (e){
            return  new GraphQLRetVal<Boolean>(false,false,e.message)
        }
    }

    @GraphQLMutation(name="loanMVoidPaidLoan")
    GraphQLRetVal<Boolean> loanMVoidPaidLoan(
            @GraphQLArgument(name="id") UUID id
    ){
        try{
            if(id) {
                def loanSchedule = findOne(id)
                if (loanSchedule){
                    if(loanSchedule.postedLedger) {
                        def header = ledgerServices.findOne(loanSchedule.postedLedger)
                        if(header) {
                            ledgerServices.reverseEntries(header)
                            loanSchedule.postedLedger = null
                            save(loanSchedule)
                            return new GraphQLRetVal<Boolean>(true, true, 'Cancel Successful.')
                        }
                        return  new GraphQLRetVal<Boolean>(false,false,'No records found.')
                    }
                    return new GraphQLRetVal<Boolean>(false, false, 'Invalid transaction.')
                }
                return  new GraphQLRetVal<Boolean>(false,false,'No records found.')
            }
            return  new GraphQLRetVal<Boolean>(false,false,"Invalid transaction.")
        }
        catch (e){
            return new GraphQLRetVal<Boolean>(false,false,e.message)
        }
    }

}
