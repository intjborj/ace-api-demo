package com.hisd3.hismk2.socket

import com.hisd3.hismk2.domain.hrm.Payroll
import groovy.transform.Canonical

@Canonical
class PayrollCalculatingMessage {
    Payroll payroll
    Integer progress
    Integer total
    HISD3MessageType type
}
