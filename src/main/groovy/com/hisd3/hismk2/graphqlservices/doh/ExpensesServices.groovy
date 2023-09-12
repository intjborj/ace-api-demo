package com.hisd3.hismk2.graphqlservices.doh

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.doh.DohExpenses
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.doh.ExpensesRepository
import com.hisd3.hismk2.utils.SOAPConnector
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ph.gov.doh.uhmistrn.ahsr.webservice.index.Expenses
import ph.gov.doh.uhmistrn.ahsr.webservice.index.ExpensesResponse

import java.time.Instant

@Component
@GraphQLApi
class ExpensesServices {

	@Autowired
	SOAPConnector soapConnector

	@Autowired
	ExpensesRepository expensesRepository

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	HospitalConfigService hospitalConfigService

	@GraphQLQuery(name = "findAllExpenses", description = "Find all Expenses")
	List<DohExpenses> findAllExpenses() {
		return expensesRepository.findAllExpenses()
	}

	//==================================Mutation ============
	@GraphQLMutation
	def postExpenses(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		if (id) {
			def expenses = expensesRepository.findById(id).get()
			objectMapper.updateValue(expenses, fields)
			expenses.submittedDateTime = Instant.now()

			return expensesRepository.save(expenses)
		} else {

			def expenses = objectMapper.convertValue(fields, DohExpenses)
			expenses.submittedDateTime = Instant.now()
			return expensesRepository.save(expenses)
		}
	}
	@GraphQLMutation(name = "sendExpenses")
	GraphQLRetVal<String>  sendExpenses(@GraphQLArgument(name = "fields") Map<String, Object> fields){

		try {
			Expenses request = new Expenses()
			request.hfhudcode = hospitalConfigService.hospitalInfo.hfhudcode?:""
			request.salarieswages = fields.get("salariesWages") as String
			request.employeebenefits = fields.get("employeeBenefits") as String
			request.allowances = fields.get("allowances") as String
			request.totalps = fields.get("totalps") as String
			request.totalamountmedicine = fields.get("totalAmountMedicine") as String
			request.totalamountmedicalsupplies = fields.get("totalAmountMedicalSupplies") as String
			request.totalamountutilities = fields.get("totalAmountUtilities") as Double
			request.totalamountnonmedicalservice = fields.get("totalAmountNonMedicalService") as String
			request.totalmooe = fields.get("totalMooe") as String
			request.amountinfrastructure = fields.get("amountInfrastructure") as String
			request.amountequipment = fields.get("amountEquipment") as String
			request.totalco = fields.get("totalCo") as String
			request.grandtotal = fields.get("grandTotal") as String
			request.reportingyear = fields.get("reportingYear") as String

			ExpensesResponse response =
					(ExpensesResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/expenses", request)
			return new GraphQLRetVal<String>(response.return, true)
		} catch(Exception e) {
			return new GraphQLRetVal<String>(e.message, false)
		}
	}
}
