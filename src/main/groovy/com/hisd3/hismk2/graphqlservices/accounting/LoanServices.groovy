package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.accounting.CompoundType
import com.hisd3.hismk2.domain.accounting.JournalType
import com.hisd3.hismk2.domain.accounting.LOAN_AMORTIZATION_INTEGRATION
import com.hisd3.hismk2.domain.accounting.LOAN_INTEGRATION
import com.hisd3.hismk2.domain.accounting.LedgerDocType
import com.hisd3.hismk2.domain.accounting.Loan
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
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.math.RoundingMode
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Service
@GraphQLApi
class LoanServices extends AbstractDaoService<Loan> {

    LoanServices(){
        super(Loan.class)
    }

    @Autowired
    EntityObjectMapperService entityObjectMapperService

    @Autowired
    LoanAmortizationServices loanManagementDetailsServices

    @Autowired
    GeneratorService generatorService

    @Autowired
    AccountReceivableServices accountReceivableServices

    @Autowired
    IntegrationServices integrationServices

    @Autowired
    LedgerServices ledgerServices

    @GraphQLQuery(name="loanMPMT" , description = "One of the financial functions, calculates the payment for a loan based on constant payments and a constant interest rate.")
     Map<String,BigDecimal> loanMPMT(
            @GraphQLArgument(name="rate" , description = "The interest rate for the loan")  BigDecimal rate ,
            @GraphQLArgument(name="nPer" , description = "The total number of payments for the loan") Integer nPer ,
            @GraphQLArgument(name="pv" , description = "The present value, or the total amount that a series of future payments is worth now; also known as the principal")  BigDecimal pv
    ){
            Map<String,BigDecimal> result = [:]
            BigDecimal pmt = (pv*rate)/(1-(1/Math.pow((rate+1),nPer)))
            result['roundOff'] = pmt.setScale(2, RoundingMode.HALF_EVEN)
            result['normal'] = pmt
            return result
    }

    @GraphQLQuery(name="loanMFV")
    BigDecimal loanMFV(
            @GraphQLArgument(name="rate" , description = "The interest rate for the loan")  BigDecimal rate ,
            @GraphQLArgument(name="nPer" , description = "The total number of payments for the loan") Integer nPer ,
            @GraphQLArgument(name="pmt" , description = "The payment made each period; it cannot change over the life of the annuity.") BigDecimal pmt ,
            @GraphQLArgument(name="pv" , description = "(Optional) The present value, or the total amount that a series of future payments is worth now; also known as the principal")  BigDecimal pv
    ){
        return -( (pmt * (Math.pow(1 + rate, nPer) - 1)) / rate + pv * Math.pow(1 + rate, nPer))
    }

    @GraphQLQuery(name="loanMIPMT")
    BigDecimal loanMIPMT(
            @GraphQLArgument(name="rate" , description = "The interest rate for the loan")  BigDecimal rate ,
            @GraphQLArgument(name="per" , description = "The period for which you want to find the interest and must be in the range 1 to nper") Integer per ,
            @GraphQLArgument(name="nPer" , description = "The total number of payments for the loan") Integer nPer ,
            @GraphQLArgument(name="pv" , description = "(Optional) The present value, or the total amount that a series of future payments is worth now; also known as the principal")  BigDecimal pv,
            @GraphQLArgument(name="type", description = "(Optional). The number 0 or 1 and indicates when payments are due. If type is omitted, it is assumed to be 0")  String type = 0
    ){
        BigDecimal monthlyPayment = loanMPMT(rate, nPer, pv)['normal']
        BigDecimal IPMT =  loanMFV(rate, per - 1, -monthlyPayment, pv) * rate
        if (type == 1) IPMT /= 1 + rate
        return IPMT
    }

    @GraphQLQuery(name="loanMNumberOfPayments")
    Integer loanMNumberOfPayments(
            @GraphQLArgument(name="type", description = "Compound type of loan")  String type,
            @GraphQLArgument(name="numOFYears" , description = "Loan period in years") Integer numOFYears
    ){
        if(numOFYears) {
            if (CompoundType.annually.name().equalsIgnoreCase(type))
                return numOFYears

            if (CompoundType.monthly.name().equalsIgnoreCase(type))
                return numOFYears * 12
        }
        return  0.00
    }

    @GraphQLQuery(name="loanMInterestRate")
    BigDecimal loanMInterestRate(
            @GraphQLArgument(name="type", description = "Compound type of loan")  String type,
            @GraphQLArgument(name="annualInterest") BigDecimal annualInterest
    ){
        if(annualInterest) {
            if(CompoundType.annually.name().equalsIgnoreCase(type))
                return annualInterest
            if(CompoundType.monthly.name().equalsIgnoreCase(type))
                return annualInterest/12
        }
        return  0.00
    }

    @GraphQLQuery(name="loanMCostOFLoan")
    BigDecimal loanMCostOFLoan(
            @GraphQLArgument(name="payment") BigDecimal payment,
            @GraphQLArgument(name="numOfPayments") BigDecimal numOfPayments
    ){
        if(payment && numOfPayments){
            return payment * numOfPayments
        }

        return 0.00
    }

    @GraphQLQuery(name="loanMLoanAmortization")
    List<Map<String,Object>> loanMLoanAmortization(
            @GraphQLArgument(name="startDate") String startDate,
            @GraphQLArgument(name="principalAmount") BigDecimal principalAmount,
            @GraphQLArgument(name="numOfPayments") Integer numOfPayments,
            @GraphQLArgument(name="annualInterest") BigDecimal annualInterest,
            @GraphQLArgument(name="compoundType") String compoundType
    ){
        List<Map<String,Object>> amortize =  []
        BigDecimal interestRate = loanMInterestRate(compoundType, annualInterest) / 100
        Map<String,BigDecimal> monthlyPayment = loanMPMT(interestRate, numOfPayments, principalAmount)

        for(int i=1; i<=numOfPayments; i++){
            Map<String,Object> row = [:]
            BigDecimal beginning = (loanMFV(interestRate,i-1,-monthlyPayment['normal'],principalAmount) * -1).setScale(2, RoundingMode.HALF_EVEN)
            BigDecimal interest = loanMIPMT(interestRate,i,numOfPayments,principalAmount).setScale(2, RoundingMode.HALF_EVEN)


            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDate localDate = LocalDate.parse(startDate, formatter);

            row['no'] = i
            row['beginningBalance'] = beginning
            row['paymentDate'] =  CompoundType.annually.name().equalsIgnoreCase(compoundType) ? localDate.plusYears(i) : localDate.plusMonths(i)
//            row['paymentDate'] = localDate.plusMonths(i)
            row['payment'] = monthlyPayment['roundOff']
            row['principal'] = (monthlyPayment['roundOff'] + interest).setScale(2, RoundingMode.HALF_EVEN)
            row['interest'] = (interest * -1)
            row['endingBalance'] = (loanMFV(interestRate,i,-monthlyPayment['normal'],principalAmount) * -1).setScale(2, RoundingMode.HALF_EVEN)
            amortize.push(row)
        }

        return amortize.sort{it['no'] }
    }


    @GraphQLQuery(name="loanMLoanPayments")
    Map<String,Object> loanMLoanPayments(
            @GraphQLArgument(name="startDate") String startDate,
            @GraphQLArgument(name="annualInterest") BigDecimal annualInterest,
            @GraphQLArgument(name="compoundType") String compoundType,
            @GraphQLArgument(name="principalAmount") BigDecimal principalAmount,
            @GraphQLArgument(name="numberOfPeriod") Integer numberOfPeriod
    ){
        Map<String,Object> result = [:]
        Map<String,BigDecimal> monthlyPayment = [:]
        BigDecimal costOfLoan = 0.00
        List<Map<String,Object>> amortize = []
        Integer numOfPayments = 0

        if(compoundType && annualInterest > 0 && numberOfPeriod > 0 && principalAmount > 0) {
            BigDecimal interestRate = loanMInterestRate(compoundType, annualInterest) / 100
            numOfPayments = loanMNumberOfPayments(compoundType, numberOfPeriod)
            monthlyPayment = loanMPMT(interestRate, numOfPayments, principalAmount)
            costOfLoan = (numOfPayments * monthlyPayment['normal']).setScale(2, RoundingMode.HALF_EVEN)
            amortize = loanMLoanAmortization(startDate,principalAmount,numOfPayments,annualInterest,compoundType)
        }

        result['monthlyPayment'] = monthlyPayment['roundOff']
        result['numberOfPayments'] = numOfPayments
        result['costOfLoan'] = costOfLoan
        result['totalInterest'] = (costOfLoan - principalAmount)
        result['amortize'] = amortize

        return result
    }

    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name="loanMAddLoan")
    GraphQLRetVal<Loan> loanMAddLoan(
            @GraphQLArgument(name="fields") Map<String,Object> fields
    ){
        try{
            Loan loanManagement = new Loan()
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

            LocalDate localDate = LocalDate.parse(fields["startDate"].toString(), formatter);
            fields["startDate"] = localDate
            entityObjectMapperService.updateFromMap(loanManagement,fields)
            def loanCalculation = loanMLoanPayments(localDate.format(formatter),loanManagement.interestRate,loanManagement.compoundType,loanManagement.loanAmount,loanManagement.loanPeriod)

            loanManagement.loanPayment = loanCalculation['monthlyPayment'] as BigDecimal
            loanManagement.numberOfPayments = loanCalculation['numberOfPayments'] as Integer
            loanManagement.totalCostOfLoan = loanCalculation['costOfLoan'] as BigDecimal
            loanManagement.totalInterest = loanCalculation['totalInterest'] as BigDecimal

            loanManagement.loanNo = generatorService.getNextValue(GeneratorType.LOAN_NO, {
                return StringUtils.leftPad(it.toString(), 6, "0")
            })

            def newSave = save(loanManagement)

            if(newSave) {
                loanManagementDetailsServices.addLoanAmortization(newSave, loanCalculation['amortize'] as List<Map<String, Object>>)
            }

            return new GraphQLRetVal<Loan>(loanManagement,true,'Successfully saved.')
        }
        catch (e){
            return new GraphQLRetVal<Loan>(new Loan(),false,e.message)
        }

    }

    @GraphQLQuery(name="loanManagements")
    Page<Loan> loanManagements(
            @GraphQLArgument(name="filter") String filter,
            @GraphQLArgument(name="accountNo") List<String> accountNo = [],
            @GraphQLArgument(name="page") Integer page,
            @GraphQLArgument(name="size") Integer size
    ){
        Page<Loan> loanList = Page.empty()
        Map<String,Object> param = [:]

        String queryStr = "Select b from Loan b where  "
        String countQueryStr = "Select count(b) from Loan b where  "

        if(accountNo) {
            queryStr += "b.bankAccount.accountNo in (:accountNo) and"
            countQueryStr += "b.bankAccount.accountNo in (:accountNo) and"

            param.put("accountNo",accountNo)
        }

        queryStr += "(lower(b.bankAccount.bank.bankname) like lower(concat('%',:filter,'%')) or b.loanNo like lower(concat('%',:filter,'%')) or b.referenceNo like lower(concat('%',:filter,'%'))) order by b.loanNo desc"
        countQueryStr += "(lower(b.bankAccount.bank.bankname) like lower(concat('%',:filter,'%')) or b.loanNo like lower(concat('%',:filter,'%')) or b.referenceNo like lower(concat('%',:filter,'%')))"

        param.put("filter",filter)

        def result = getPageable(
                queryStr,
                countQueryStr,
                page,
                size,
                param)

        if(result)
            loanList = result


        return loanList
    }


    @GraphQLQuery(name="loanManagementById")
    GraphQLRetVal<Loan> loanManagementById(
            @GraphQLArgument(name="id") UUID id
    ){
        if(id) {
            def result = findOne(id)
            if (result)
                return new GraphQLRetVal<Loan>(result, true, 'Record found.')
            else
                return new GraphQLRetVal<Loan>(new Loan(), false, 'No record found.')
        }
        else
            return  new GraphQLRetVal<Loan>(new Loan(),false,'No parameter found.')
    }

    @GraphQLQuery(name="loanMViewStartingEntry")
    GraphQLRetVal<List<Map<String,Object>>> loanMViewStartingEntry(@GraphQLArgument(name="id") UUID id){
        try{
            if(id) {
                def loan = findOne(id)
                if(loan) {
                    List<Map<String,Object>> entry = []

                    def headerLedger = integrationServices.generateAutoEntries(loan) { it, nul ->
                        it.flagValue = LOAN_INTEGRATION.LOAN_ENTRY.name()
                        it.bank = it.bankAccount.bank
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

    @GraphQLMutation(name="loanMStartingEntry")
    GraphQLRetVal<Boolean> loanMStartingEntry(@GraphQLArgument(name="id") UUID id){
        try {

            def loan = findOne(id)
            if (loan) {
                def yearFormat = DateTimeFormatter.ofPattern("yyyy")

                def headerLedger = integrationServices.generateAutoEntries(loan) { it, nul ->
                    it.flagValue = LOAN_INTEGRATION.LOAN_ENTRY.name()
                    it.bank = it.bankAccount.bank
                }

                Map<String, String> details = [:]
                loan.details.each { k, v ->
                    details[k] = v
                }

                def convertedDate = accountReceivableServices.dateToInstantConverter(loan.startDate)

                details["LOAN_ID"] = loan.id.toString()
                def pHeader = ledgerServices.persistHeaderLedger(headerLedger,
                        "LOAN ${convertedDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${loan.loanNo}",
                        "LOAN ${loan.loanNo} - ${loan.bankAccount.accountNo}",
                        "NEW LOAN ACQUIRED FROM ${loan.bankAccount.bank.bankname}",
                        LedgerDocType.JV,
                        JournalType.GENERAL,
                        convertedDate,
                        details)

                loan.postedLedger = pHeader.id
                save(loan)
                return new GraphQLRetVal<Boolean>(true, true, 'Successfully saved.')
            }
            return new GraphQLRetVal<Boolean>(false, false, 'No records found.')
        }
        catch (e){
            return  new GraphQLRetVal<Boolean>(false,false,e.message)
        }
    }
}
