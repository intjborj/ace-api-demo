package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.accounting.ArCreditNote
import com.hisd3.hismk2.domain.accounting.ArInvoice
import com.hisd3.hismk2.domain.accounting.ArTransactionLedger
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.persistence.EntityManager
import javax.transaction.Transactional
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset

@Canonical
class ArTransactionLedgerRemainingBalanceDto {
    BigDecimal remainingHCIBalanceSum
    BigDecimal remainingPFBalanceSum
    BigDecimal remainingBalanceSum
}

@Service
@GraphQLApi
@Transactional(rollbackOn = Exception.class)
class ArTransactionLedgerServices extends  AbstractDaoService<ArTransactionLedger> {

    ArTransactionLedgerServices(){
        super(ArTransactionLedger.class)
    }

    @Autowired
    EntityManager entityManager

    @Autowired
    GeneratorService generatorService

    @GraphQLQuery(name="customerRemainingBalance")
    ArTransactionLedgerRemainingBalanceDto customerRemainingBalance(
            @GraphQLArgument(name = "customerId") UUID customerId
    ){
        try{
            ArTransactionLedger ledger = entityManager.createQuery(""" 
                        Select 
                            i
                            from ArTransactionLedger i 
                            where i.arCustomer.id = :customerId
                            ORDER BY i.recordNo DESC
            """,ArTransactionLedger.class)
                    .setParameter('customerId',customerId)
                    .setMaxResults(1)
                    .getSingleResult()
            if(!ledger)
                return new ArTransactionLedgerRemainingBalanceDto(
                        0.00,
                        0.00,
                        0.00,
                )

            return new ArTransactionLedgerRemainingBalanceDto(
                    ledger.remainingHCIBalance,
                    ledger.remainingPFBalance,
                    ledger.remainingBalance
            )
        }
        catch (ignored) {
            return new ArTransactionLedgerRemainingBalanceDto(
                    0.00,
                    0.00,
                    0.00,
            )
        }
    }


    Boolean insertArInvoiceTransactionLedger(ArInvoice invoice){

        ArTransactionLedgerRemainingBalanceDto balanceDto = customerRemainingBalance(invoice.arCustomer.id)
        ArTransactionLedger ledger = new ArTransactionLedger()
        ledger.recordNo = generatorService.getNextValue(GeneratorType.AR_TRANS_LEDGER, {
            return StringUtils.leftPad(it.toString(), 6, "0")
        })
        ledger.docNo = invoice.invoiceNo
        ledger.docType = 'INVOICE'
        ledger.arCustomer = invoice.arCustomer
        ledger.arInvoice = invoice
        ledger.ledgerDate = invoice.createdDate
        ledger.docDate = invoice.invoiceDate
        ledger.totalHCIAmount = invoice.totalHCIAmount?:0.00
        ledger.totalPFAmount = invoice.totalPFAmount?:0.00
        ledger.totalCwtAmount = invoice.cwtAmount?:0.00
        ledger.totalVatAmount = invoice.vatAmount?:0.00
        ledger.totalAmountDue = invoice.totalAmountDue?:0.00

        ledger.remainingHCIBalance = (balanceDto?.remainingHCIBalanceSum?:0.00) + ledger.totalHCIAmount
        ledger.remainingPFBalance = (balanceDto?.remainingPFBalanceSum?:0.00) + ledger.totalPFAmount
        ledger.remainingBalance = (balanceDto?.remainingBalanceSum?:0.00) + ledger.totalAmountDue
        save(ledger)
    }


    Boolean insertArCreditNoteTransactionLedger(ArCreditNote creditNote){

        ArTransactionLedgerRemainingBalanceDto balanceDto = customerRemainingBalance(creditNote.arCustomer.id)
        ArTransactionLedger ledger = new ArTransactionLedger()
        ledger.recordNo = generatorService.getNextValue(GeneratorType.AR_TRANS_LEDGER, {
            return StringUtils.leftPad(it.toString(), 6, "0")
        })
        ledger.docNo = creditNote.creditNoteNo
        ledger.docType = 'CN'
        ledger.arCustomer = creditNote.arCustomer
        ledger.arCreditNote = creditNote
        ledger.ledgerDate = creditNote.createdDate
        ledger.docDate = creditNote.creditNoteDate
        ledger.totalHCIAmount = creditNote.totalHCIAmount?:0.00
        ledger.totalPFAmount = creditNote.totalPFAmount?:0.00
        ledger.totalCwtAmount = creditNote.cwtAmount?:0.00
        ledger.totalVatAmount = creditNote.vatAmount?:0.00
        ledger.totalAmountDue = creditNote.totalAmountDue?:0.00

        ledger.remainingHCIBalance = (balanceDto?.remainingHCIBalanceSum?:0.00) - ledger.totalHCIAmount
        ledger.remainingPFBalance = (balanceDto?.remainingPFBalanceSum?:0.00) - ledger.totalPFAmount
        ledger.remainingBalance = (balanceDto?.remainingBalanceSum?:0.00) - ledger.totalAmountDue
        save(ledger)
    }



}
