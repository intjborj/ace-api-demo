package com.hisd3.hismk2.domain.cashiering.dto

import groovy.transform.Canonical

@Canonical
class CollectionReportCsvDownloadDto{
    String shiftno
    String sftStart
    String sftEnd
    String sftStatus
    String terminalId
    String ornumber
    String payee
    String description
    String type
    String reference
    String amount
}
