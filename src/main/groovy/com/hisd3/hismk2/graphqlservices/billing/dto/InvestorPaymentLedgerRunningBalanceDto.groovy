package com.hisd3.hismk2.graphqlservices.billing.dto


import groovy.transform.Canonical

@Canonical
class InvestorPaymentLedgerRunningBalanceDto {
//    "createdDate",
//    "type",
//    "modeOfPayment",
//    "particular",
//    "debit",
//    "credit",
//    "balance",
//    "remarks"
    String createdDate
    String type
    String modeOfPayment
    String particular
    BigDecimal debit
    BigDecimal credit
    BigDecimal balance
    String remarks

}
