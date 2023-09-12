package com.hisd3.hismk2.graphqlservices.pms

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.domain.pms.*
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.inventory.StockRequestItemRepository
import com.hisd3.hismk2.repository.pms.AdministrationRepository
import com.hisd3.hismk2.repository.pms.MedicationRepository
import com.hisd3.hismk2.repository.pms.PatientOwnMedicationAdministrationRepository
import com.hisd3.hismk2.repository.pms.PatientOwnMedicineRepository
import com.hisd3.hismk2.rest.dto.AdministrationDto
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLContext
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@TypeChecked
@Component
@GraphQLApi
class MedicationService {
	
	@Autowired
	private MedicationRepository medicationRepository
	
	@Autowired
	private PatientOwnMedicineRepository patientOwnMedicineRepository
	
	@Autowired
	private AdministrationRepository administrationRepository
	
	@Autowired
	private StockRequestItemRepository stockRequestItemRepository
	
	@Autowired
	private PatientOwnMedicationAdministrationRepository patientOwnMedicationAdministrationRepository
	
	@Autowired
	ObjectMapper objectMapper
	
	//============== All Queries ====================
	
	@GraphQLQuery(name = "medications", description = "Get all Medications")
	List<Medication> findAll() {
		return medicationRepository.findAll().sort { it.entryDateTime }
	}
	
	@GraphQLQuery(name = "medication", description = "Get Medication By Id")
	Medication findById(@GraphQLArgument(name = "id") UUID id) {
		return medicationRepository.findById(id).get()
	}
	
	@GraphQLQuery(name = "patient_own_medications", description = "Get Patient Own Medication By Case ID")
	List<PatientOwnMedicine> findPatientOwnMedicineByCaseId(@GraphQLArgument(name = "caseId") UUID caseId) {
		return patientOwnMedicineRepository.getPatientOwnMedicationsByCase(caseId).sort { it.entry_datetime }
	}
	
	@GraphQLQuery(name = "medicationsByCase", description = "Get all Medications by Case Id")
	List<Medication> getMedicationsByCase(@GraphQLArgument(name = "caseId") UUID caseId) {
		return medicationRepository.getMedicationsByCase(caseId).sort { it.entryDateTime }
	}

	@GraphQLQuery(name = "medicationsByCaseAndType", description = "Get all Medications by Case Id and Type")
	List<Medication> getMedicationsByCaseAndType(@GraphQLArgument(name = "caseId") UUID caseId, @GraphQLArgument(name = "type") String type) {

		List<Medication> list = null

		if (type == "STAT,PRN") {
			list = medicationRepository.getMedicationsByCaseAndTypeStatPrn(caseId).sort { it.entryDateTime }
		} else {
			if (type == "IV") {
				list = medicationRepository.getMedicationsByCaseForIV(caseId).sort { it.entryDateTime }
			} else {
				if (type == "NEBULIZATION") {
					list = medicationRepository.getMedicationsByCaseAndTypeNebulization(caseId).sort { it.entryDateTime }
				} else {
					list = medicationRepository.getMedicationsByCaseAndType(caseId, type).sort { it.entryDateTime }
				}
			}
		}

		for (Medication med in list) {
			Integer srItem = stockRequestItemRepository
					.getSRItemsByCaseIdAndPendingAndItemISum(med.parentCase.id, med.medicine.id)

			if (srItem)
				med.pending = srItem
			else
				med.pending = 0.0

			Integer srItem2 = stockRequestItemRepository.getSRItemsByCaseIdAndClaimableAndItemId(med.parentCase.id, med.medicine.id)
			if (srItem2)
				med.claimable = srItem2
			else
				med.claimable = 0.0

			BigDecimal sriNoStock = stockRequestItemRepository.getSRIItemsByCaseIdAndNoStock(med.parentCase.id, med.medicine.id) ?: BigDecimal.ZERO
			BigDecimal sriCancelled = stockRequestItemRepository.getSRIItemsByCaseIdAndCancelled(med.parentCase.id, med.medicine.id) ?: BigDecimal.ZERO

			if(sriNoStock || sriCancelled)
				med.noStockOrCancel = sriNoStock + sriCancelled
			else
				med.noStockOrCancel = 0.0

			BigDecimal onHandClaimed = stockRequestItemRepository.getSRIItemsByCaseIdAndClaimedOnHand(med.parentCase.id, med.medicine.id) ?: BigDecimal.ZERO
			Long adm = administrationRepository.getMedicationAdm(med.id)
			def onHandQty = onHandClaimed - adm
			if(onHandQty >= 0)
				med.onhand = onHandQty
			else
				med.onhand = 0.0


		}
		return list
	}

	@GraphQLQuery(name = "fetchPatientMedications", description = "Get all Medications by Case Id and Type")
	List<Medication> fetchPatientMedications(@GraphQLArgument(name = "caseId") UUID caseId, @GraphQLArgument(name = "type") String type) {

		List<Medication> list = null

		if (type == "STAT,PRN") {
			list = medicationRepository.getMedicationsByCaseAndTypeStatPrn(caseId).sort { it.entryDateTime }
		} else {
			if (type == "IV") {
				list = medicationRepository.getMedicationsByCaseForIV(caseId).sort { it.entryDateTime }
			} else {
				if (type == "NEBULIZATION") {
					list = medicationRepository.getMedicationsByCaseAndTypeNebulization(caseId).sort { it.entryDateTime }
				} else {
					list = medicationRepository.getMedicationsByCaseAndType(caseId, type).sort { it.entryDateTime }
				}
			}
		}

		return list
	}

	@GraphQLQuery(name = "fetchPatientMedicationStock", description = "Fetch patient medication stock")
	Medication fetchPatientMedicationStock(@GraphQLArgument(name = "medId") UUID medId) {
		def med = medicationRepository.getMedicationByItemId(medId)

		Integer srItem = stockRequestItemRepository
				.getSRItemsByCaseIdAndPendingAndItemISum(med.parentCase.id, med.medicine.id)

		if (srItem)
			med.pending = srItem
		else
			med.pending = 0.0

		Integer srItem2 = stockRequestItemRepository.getSRItemsByCaseIdAndClaimableAndItemId(med.parentCase.id, med.medicine.id)
		if (srItem2)
			med.claimable = srItem2
		else
			med.claimable = 0.0

		BigDecimal sriNoStock = stockRequestItemRepository.getSRIItemsByCaseIdAndNoStock(med.parentCase.id, med.medicine.id) ?: BigDecimal.ZERO
		BigDecimal sriCancelled = stockRequestItemRepository.getSRIItemsByCaseIdAndCancelled(med.parentCase.id, med.medicine.id) ?: BigDecimal.ZERO

		if(sriNoStock || sriCancelled)
			med.noStockOrCancel = sriNoStock + sriCancelled
		else
			med.noStockOrCancel = 0.0

		BigDecimal onHandClaimed = stockRequestItemRepository.getSRIItemsByCaseIdAndClaimedOnHand(med.parentCase.id, med.medicine.id) ?: BigDecimal.ZERO
		Long adm = administrationRepository.getMedicationAdm(med.id)
		def onHandQty = onHandClaimed - adm
		if(onHandQty >= 0)
			med.onhand = onHandQty
		else
			med.onhand = 0.0

		return med;
	}
	
	@GraphQLQuery(name = "checkMedicationIfExist", description = "Get medication if it exists")
	Boolean checkMedicationIfExist(@GraphQLArgument(name = "caseId") UUID caseId,
	                               @GraphQLArgument(name = "medicineId") String medicineId,
	                               @GraphQLArgument(name = "medType") String medType
	) {
		List<Medication> medications = medicationRepository.getMedicationsByCase(caseId)
		Medication medication = medicationRepository.getMedicationsByItemId(UUID.fromString(medicineId)).find()
		
		def flag = false
		
		if (medication)
			medications.each {
				it ->
					if (it.medicine.id == medication.medicine.id) {
						flag = medication.type == medType
					}
			}
		
		return flag
	}
	
	@GraphQLQuery(name = "medicationAdministrations", description = "Get all Medication Administrations")
	List<Administration> getAdministrations(@GraphQLContext Medication medication) {
		return administrationRepository.getMedicationAdministrations(medication.id).sort { it.entryDateTime }
	}
	
	@GraphQLQuery(name = "combinedMedicationAdministrations", description = "Get combined Medication Administrations")
	List<AdministrationDto> getCombinedMedicationAdministrations(@GraphQLArgument(name = "caseId") UUID caseId) {
		
		List<Administration> adms = administrationRepository.getMedicationAdministrationsByCase(caseId).sort {
			it.entryDateTime
		}
		List<PatientOwnMedicineAdministration> ptadms = patientOwnMedicationAdministrationRepository.getMedicationAdministrationsByCaseId(caseId).sort {
			it.entryDateTime
		}
		DateTimeFormatter formatterWithTime =
				DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a").withZone(ZoneId.systemDefault())
		List<AdministrationDto> forReturn = new ArrayList<>()
		for (Administration adm in adms) {
			AdministrationDto admDto = new AdministrationDto(adm.id, adm.medication.medicine.descLong, adm.action, adm.dose, adm.remarks, adm.entryDateTime, LocalDateTime.ofInstant(adm.entryDateTime, ZoneId.of("GMT+08:00")).format(formatterWithTime) , adm.employee, false)
			forReturn.add(admDto)
		}
		
		return forReturn.sort { it.entryDateTime }
	}
	
	@GraphQLQuery(name = "ownMedicationAdministrations", description = "Get combined Medication Administrations")
	List<AdministrationDto> getOwnMedicationAdministrations(@GraphQLArgument(name = "caseId") UUID caseId) {
		
		List<PatientOwnMedicineAdministration> ptadms = patientOwnMedicationAdministrationRepository.getMedicationAdministrationsByCaseId(caseId).sort {
			it.entryDateTime
		}
		List<AdministrationDto> forReturn = new ArrayList<>()
		
		for (PatientOwnMedicineAdministration adm in ptadms) {
			AdministrationDto admDto = new AdministrationDto(adm.id, adm.patientOwnMedicine.medicine_name, adm.action, adm.dose, adm.remarks, adm.entryDateTime, adm.employee, true)
			forReturn.add(admDto)
		}
		
		return forReturn.sort { it.entryDateTime }
	}
	
	@GraphQLQuery(name = "patientOwnMedicationAdministrations", description = "Get all patientOwnMedicationAdministrations by Medication Id")
	List<PatientOwnMedicineAdministration> getPatientOwnMedicationAdministrations(@GraphQLContext PatientOwnMedicine patientOwnMedicine) {
		return patientOwnMedicationAdministrationRepository.getMedicationAdministrations(patientOwnMedicine.id).sort {
			it.entryDateTime
		}
		//return administrationRepository.getMedicationAdministrations(medication).sort { it.entryDateTime }
	}
	
	@GraphQLMutation
	Medication addMedicationForFlutter(
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		return medicationRepository.save(
				new Medication().tap {
					entryDateTime = Timestamp.valueOf(fields.get("entryDateTime") as String).toInstant()
					medicine = objectMapper.convertValue(fields.get('medicine'), Item)
					type = fields.get('type')
					additive = objectMapper.convertValue(fields.get('additive'), Item)
					frequency = fields.get('frequency')
					dose = fields.get('dose')
					route = fields.get('route')
					volume = fields.get('volume')
					flowRate = fields.get('flowRate')
					doctorsOrderItemId = UUID.fromString(fields.get('doctorsOrderItemId') as String)
					orderingPhysician = objectMapper.convertValue(fields.get("orderingPhysician"), Employee)
					parentCase = objectMapper.convertValue(fields.get("parentCase"), Case)
				}
		)
	}

	@GraphQLMutation
	GraphQLRetVal<String> updateMedication(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	){

			Medication medication1 = medicationRepository.findById(id).get()
			objectMapper.updateValue(medication1, fields)
			medicationRepository.save(medication1)
			return new GraphQLRetVal<String>("Ok", true, "Successfully Medication Updated")

	}

}
