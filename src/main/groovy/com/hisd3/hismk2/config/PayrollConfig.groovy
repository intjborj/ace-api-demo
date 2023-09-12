package com.hisd3.hismk2.config

import com.hisd3.hismk2.graphqlservices.payroll.common.AbstractPayrollEmployeeStatusService
import com.hisd3.hismk2.graphqlservices.payroll.enums.PayrollModule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PayrollConfig {

    @Autowired
    List<AbstractPayrollEmployeeStatusService> payrollEmployeeStatusServiceList


    @Bean
    Map<PayrollModule, AbstractPayrollEmployeeStatusService> mapPayrollEmployeeStatusService(){
        Set<PayrollModule> foundModules = new HashSet<>()
        payrollEmployeeStatusServiceList.each {
            if(it.payrollModule == null)
                throw new RuntimeException("'payrollModule' property of ${AbstractPayrollEmployeeStatusService.class} cannot have a value of 'null'")
            if(!foundModules.add(it.payrollModule)){
                throw new RuntimeException("Found duplicate ${AbstractPayrollEmployeeStatusService.class} having a 'payrollModule' property with a value of: ${it.payrollModule.class}")
            }
        }

        return payrollEmployeeStatusServiceList.collectEntries { [it.payrollModule, it] }
    }
}
