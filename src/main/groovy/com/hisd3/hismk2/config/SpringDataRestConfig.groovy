package com.hisd3.hismk2.config

import com.hisd3.hismk2.domain.accounting.Fiscal
import com.hisd3.hismk2.domain.hospital_config.ConstantType
import com.hisd3.hismk2.domain.hospital_config.HospitalInfo
import com.hisd3.hismk2.domain.hospital_config.LoginConfiguration
import com.hisd3.hismk2.domain.hospital_config.RangedConstant
import com.hisd3.hismk2.domain.hrm.EmployeeRequest
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.domain.pms.Patient
import com.hisd3.hismk2.domain.pms.Transfer
import com.hisd3.hismk2.domain.sales.SalesTransaction
import com.hisd3.hismk2.repository.eventhandlers.EventHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer

@Configuration
class SpringDataRestConfig implements RepositoryRestConfigurer {
	
	@Override
	void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
		config.exposeIdsFor(
				Patient.class,
				SalesTransaction.class,
				ConstantType.class,
				HospitalInfo.class,
				Transfer.class,
				EmployeeRequest.class,
				Item.class,
				Fiscal.class,
				LoginConfiguration.class,
				RangedConstant.class
		)
	}
	
	@Bean
	EventHandler eventHandler() {
		return new EventHandler()
	}
}
