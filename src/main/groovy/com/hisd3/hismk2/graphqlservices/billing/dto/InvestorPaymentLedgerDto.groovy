package com.hisd3.hismk2.graphqlservices.billing.dto

import com.hisd3.hismk2.domain.billing.enums.InvestorLedgerTransactionType
import com.sun.org.apache.xpath.internal.operations.Bool
import groovy.transform.Canonical
//import org.apache.logging.log4j.core.time.Instant

@Canonical
class InvestorPaymentLedgerDto {
//    "dateReceived"
//    "dateDeposited"
//    "dateCleared"
//    "depositoryBank"
//    "remarks"
    String id
    String investorId
    String type
    String ptdCreatedDate
    String modeOfPayment
    String checkDate
    String particular
    BigDecimal debit
    BigDecimal credit
    String acquiringBank
    String shiftNumber
    String shiftId
    String subscriptionNumber
    String referenceNumber
    String receiptNumber
    String receiptType
    String subscriptionCode
    String subscriptionCreatedDate
    String subscriptionId
    Boolean isVoided
    String createdDate
}
