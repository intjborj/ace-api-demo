package com.hisd3.hismk2.graphqlservices.hospital_config

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.billing.BillingItem
import com.hisd3.hismk2.domain.billing.BillingItemDetailParam
import com.hisd3.hismk2.domain.billing.BillingItemStatus
import com.hisd3.hismk2.domain.billing.BillingItemType
import com.hisd3.hismk2.domain.hospital_config.ComlogikSetting
import com.hisd3.hismk2.domain.inventory.Generic
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.pms.DoctorOrder
import com.hisd3.hismk2.domain.pms.DoctorOrderItem
import com.hisd3.hismk2.domain.pms.PatientPhilhealthData
import com.hisd3.hismk2.graphqlservices.billing.BillingService
import com.hisd3.hismk2.graphqlservices.billing.CompanyAccountServices
import com.hisd3.hismk2.graphqlservices.pms.CaseService
import com.hisd3.hismk2.graphqlservices.pms.VitalSignService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.ancillary.ServiceRepository
import com.hisd3.hismk2.repository.hospital_config.ComlogikSettingRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.inventory.GenericRepository
import com.hisd3.hismk2.repository.inventory.ItemRepository
import com.hisd3.hismk2.repository.pms.*
import com.hisd3.hismk2.rest.dto.ComlogikItemDto
import com.hisd3.hismk2.rest.dto.ComlogikMedicineDto
import com.hisd3.hismk2.security.SecurityUtils
import com.squareup.okhttp.*
import groovy.json.JsonSlurper
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.apache.http.HttpHost
import org.apache.http.NameValuePair
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.fluent.Executor
import org.apache.http.client.fluent.Request
import org.apache.http.entity.ContentType
import org.apache.http.message.BasicNameValuePair
import org.json.JSONArray
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

import java.text.DecimalFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@TypeChecked
@Component
@GraphQLApi
class ComlogikSettingService {
	@Autowired
	ComlogikSettingRepository comlogikSettingRepository

	@Autowired
	GenericRepository genericRepository

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	PasswordEncoder passwordEncoder

	@Autowired
	CaseRepository caseRepository

	@Autowired
	PatientPhilhealthDataRepository patientPhilhealthDataRepository

	@Autowired
	CaseService caseService

	@Autowired
	ItemRepository itemRepository

	@Autowired
	NurseNoteRepository nurseNoteRepository

	@Autowired
	DoctorOrderRepository doctorOrderRepository

	@Autowired
	DoctorOrderItemRepository doctorOrderItemRepository

	@Autowired
	BillingService billingService

	@Autowired
	EmployeeRepository employeeRepository

	@Autowired
	ServiceRepository serviceRepository

	@Autowired
	VitalSignService vitalSignService

	@Autowired
	CompanyAccountServices companyAccountServices

	@Autowired
	MedicationRepository medicationRepository

	@Autowired
	PatientOwnMedicineRepository patientOwnMedicineRepository

	@GraphQLQuery(name = "comlogik_settings", description = "")
	List<ComlogikSetting> getCommlogikSetting() {

		return comlogikSettingRepository.findAll()
	}

	@GraphQLQuery(name = "patient_medications", description = "get all billed medications and outside purchase meds")
	List<ComlogikItemDto> patientMedicatiosn(@GraphQLArgument(name = 'billingId') UUID billingId, @GraphQLArgument(name = "includePatientMeds") Boolean includePatientMeds){
		DateTimeFormatter dateFormat =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
		String pattern = "###0.0#"
		DecimalFormat decimalFormat = new DecimalFormat(pattern)
		decimalFormat.setParseBigDecimal(true)
		def listComlogikDto = new ArrayList<ComlogikItemDto>()
		def listComlogikDto2 = new ArrayList<ComlogikItemDto>()

		if(billingId) {
			def activeBillings = billingService.findOne(billingId)

			def sortedBillingItem = activeBillings?.billingItemList?.toSorted { BillingItem a, BillingItem b -> b.subTotal <=> a.subTotal }
			def patientMeds = patientOwnMedicineRepository.getPatientOwnMedicationsByCase(activeBillings.patientCase.id)

			def subTotal = 0.0

			sortedBillingItem.findAll {
				it.itemType == BillingItemType.MEDICINES &&
						it.status == BillingItemStatus.ACTIVE
			}.each { entry ->
				if (entry.details.containsKey('ITEMID')) {
					def invItem = itemRepository.findById(UUID.fromString(entry.details['ITEMID'])).get()
					ComlogikItemDto itemDto = new ComlogikItemDto()
					subTotal += entry.subTotal

					def pActualUnitPrice = decimalFormat.format(((entry.debit ?: BigDecimal.ZERO) - (entry.credit ?: BigDecimal.ZERO)))

					def exist = listComlogikDto.find {
						it.id == invItem.id && it.philhealthActualUnitPrice.replace("-", "") == pActualUnitPrice.replace("-", "")
					}

					if (!exist) {
						itemDto.id = invItem.id
						itemDto.genericId = invItem.item_generics.id
						itemDto.philhealthDrugCode = invItem.itemCode ?: ""
						itemDto.philhealthPNDFCode = invItem.item_generics.genericCode ?: ""
						itemDto.philhealthGenericName = invItem.item_generics.genericDescription ?: ""
						itemDto.philhealthBrandName = invItem.brand ?: ""
						itemDto.philhealthDrugCodePhic = invItem.item_generics.phicCode ?: ""
						itemDto.philhealthQuantity = entry.qty
						itemDto.isPatientMed = false
						itemDto.philhealthActualUnitPrice = pActualUnitPrice
						itemDto.philhealthPurchaseDate = dateFormat.format(entry.transactionDate)
						itemDto.philhealthPreparation = invItem.unit_of_usage.unitDescription ?: entry.description ?: ""
						listComlogikDto.add(itemDto)
					} else {

						if (entry.credit?:0.compareTo(BigDecimal.ZERO) > 0) {
							exist.philhealthQuantity -= entry.qty
						} else {
							exist.philhealthQuantity += entry.qty
						}
					}

				}

			}

			if (includePatientMeds) {
				patientMeds.each {
					entry ->

						ComlogikItemDto dto = new ComlogikItemDto()

						def exist = listComlogikDto.find {
							it.id == entry.id
						}

						if (!exist) {
							dto.id = entry.id
							dto.refId = entry.id
							dto.philhealthDrugCode = ""
							dto.philhealthPNDFCode = ""
							dto.philhealthGenericName = entry.medicine_name ?: ""
							dto.philhealthBrandName = ""
							dto.philhealthQuantity = entry.qty_onhand.toInteger()
							dto.philhealthActualUnitPrice = 0
							if (entry.entry_datetime != null) {
								dto.philhealthPurchaseDate = dateFormat.format(entry.entry_datetime)
							} else {
								dto.philhealthPurchaseDate = ""
							}
							dto.philhealthPreparation = entry.description
							dto.isPatientMed = true
							listComlogikDto2.add(dto)
						} else {
							exist.philhealthQuantity += entry.qty_onhand.toInteger()
						}


				}

			}
		}

		listComlogikDto.addAll(listComlogikDto2)

		return  listComlogikDto

	}

	@GraphQLQuery(name = "comlogik_medicines", description = "get list of comlogik medicines")
	List<ComlogikMedicineDto> comlogikMedicines(
			@GraphQLArgument(name = "searchStr") String searchStr,
			@GraphQLArgument(name = "page") String page,
			@GraphQLArgument(name = "rows") String rows
	) {

		List<ComlogikSetting> comlogikSetting = comlogikSettingRepository.findAll()

		List<ComlogikMedicineDto> list = new ArrayList<>()

		if (comlogikSetting) {
			OkHttpClient client = new OkHttpClient()
			HttpUrl.Builder httpBuilder = HttpUrl.parse(comlogikSetting[0].host + "/api/searchmedicines").newBuilder()
			httpBuilder.addQueryParameter("searchStr", searchStr)
			httpBuilder.addQueryParameter("page", page)
			httpBuilder.addQueryParameter("rows", rows)
			def request = new com.squareup.okhttp.Request.Builder()
					.url(httpBuilder.build())
					.method("GET", null)
					.addHeader("Content-Type", "application/x-www-form-urlencoded")
					.build()
			Response response = client.newCall(request).execute()

				if(response.successful) {
					def medLists = new JsonSlurper().parseText(response.body().string())

					medLists.each {
						def dto = new ComlogikMedicineDto(
								addedBy: it['AddedBy'] as String,
								customMedicine: it['CustomMedicine'] as Boolean,
								dateAdded: it['DateAdded'] as String,
								dateDeleted: it['DateDeleted'] as String,
								dateUpdated: it['DateUpdated'] as String,
								deletedBy: it['DeletedBy'] as String,
								description: it['Description'] as String,
								drugCode: it['DrugCode'] as String,
								formCode: it['FormCode'] as String,
								formDescription: it['FormDescription'] as String,
								genCode: it['GenCode'] as String,
								genericDescription: it['GenericDescription'] as String,
								id: it['Id'] as String,
								packageCode: it['PackageCode'] as String,
								packageDescription: it['PackageDescription'] as String,
								route: it['Route'] as String,
								saltCode: it['SaltCode'] as String,
								saltDescription: it['SaltDescription'] as String,
								strengthCode: it['StrengthCode'] as String,
								strengthDescription: it['StrengthDescription'] as String,
								unitCode: it['UnitCode'] as String,
								unitDescription: it['UnitDescription'] as String,
								updatedBy: it['UpdatedBy'] as String,

						)

						list.add(dto)
					}
				}


		} else {
			throw new IllegalArgumentException("ComLogik Error! Please contact your system administrator.")
		}

		return  list
	}

	@GraphQLQuery(name = "comlogik_refId")
	Object comlogikRefId(@GraphQLArgument(name = "caseNo") String caseNo) {

		List<ComlogikSetting> comlogikSetting = comlogikSettingRepository.findAll()
		def form = new JSONObject()

		form.put("lastname", "")
		form.put("firstname", "")
		form.put("middlename", "")
		form.put("caseno", caseNo)
		form.put("page", "")
		form.put("rows", 20)

		if (comlogikSetting) {
			def executor = Executor.newInstance()
					.auth(new HttpHost(comlogikSetting[0].host), comlogikSetting[0].login, comlogikSetting[0].password)

			def response = executor.execute(Request.Get(comlogikSetting[0].host + "/api/dataaccess/workingclaims?lastname=&firstname=&middlename=&caseno=" + caseNo + "&page=1&rows=20"))

			return new JsonSlurper().parseText(response.returnContent().asString())
		} else {
			throw new IllegalArgumentException("ComLogik Error! Please contact your system administrator.")
		}
	}

	@GraphQLMutation(name = "comlogik_setting_mutation")
	ComlogikSetting saveComlogikSetting(@GraphQLArgument(name = 'fields') Map<String, Object> fields) {
		ComlogikSetting comlogikSetting = objectMapper.convertValue(fields, ComlogikSetting.class)
		return comlogikSettingRepository.save(comlogikSetting)
	}

	@GraphQLMutation(name = 'savemedicines', description = "save patient medications to comlogik")
	Object savemedicines(
			@GraphQLArgument(name = "caseId") UUID caseId,
			@GraphQLArgument(name = "billingId") UUID billingId,
			@GraphQLArgument(name = "includePatientMeds") Boolean includePatientMeds,
			@GraphQLArgument(name = 'credentials') String crendetials
	) {
		List<ComlogikSetting> comlogikSetting = comlogikSettingRepository.findAll()
		def meds = medicationRepository.getMedicationsByCase(caseId)
		def patientMeds = patientOwnMedicineRepository.getPatientOwnMedicationsByCase(caseId)
		JSONObject form = new JSONObject()
		Case aCase = caseRepository.findById(caseId).get()
		JSONArray formsArry = new JSONArray()
		DateTimeFormatter dateFormat =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
		String pattern = "###0.0#"
		DecimalFormat decimalFormat = new DecimalFormat(pattern)
		decimalFormat.setParseBigDecimal(true)
		if (aCase != null) {
			if (aCase.caseNo) {
				form.put("CaseNo", aCase.caseNo)
			} else {
				throw new IllegalArgumentException("Invalid Case #")
			}

			def activeBillings = billingService.findOne(billingId)

			def sortedBillingItem = activeBillings?.billingItemList?.toSorted { BillingItem a, BillingItem b -> b.subTotal <=> a.subTotal }

			def fillteredMeds = sortedBillingItem.findAll {
				it.itemType == BillingItemType.MEDICINES &&
						it.status == BillingItemStatus.ACTIVE
			}

			def listComlogikDto = new ArrayList<ComlogikItemDto>()
			def listComlogikDto2 = new ArrayList<ComlogikItemDto>()
			def subTotal = 0.0

			fillteredMeds.each { entry ->
				if (entry.details.containsKey('ITEMID')) {
					def invItem = itemRepository.findById(UUID.fromString(entry.details['ITEMID'])).get()
					ComlogikItemDto itemDto = new ComlogikItemDto()
					subTotal += entry.subTotal

					def pActualUnitPrice = decimalFormat.format(entry.subTotal / entry.qty)

					def exist = listComlogikDto.find {
						it.id == invItem.id && it.philhealthActualUnitPrice.replace("-", "") == pActualUnitPrice.replace("-", "")
					}

					if (!exist) {
						itemDto.id = invItem.id
						itemDto.philhealthDrugCode = invItem.itemCode ?: ""
						itemDto.philhealthPNDFCode = invItem.item_generics.genericCode ?: ""
						itemDto.philhealthBrandName = invItem.brand ?: ""
						if(invItem.item_generics.phicCode){
							itemDto.philhealthDrugCodePhic = invItem.item_generics.phicCode ?: ""
						}
						itemDto.philhealthGenericName = invItem.item_generics.genericDescription ?: ""

						itemDto.philhealthQuantity = entry.qty
						itemDto.philhealthActualUnitPrice = pActualUnitPrice
						itemDto.philhealthPurchaseDate = dateFormat.format(entry.transactionDate)
						itemDto.philhealthPreparation = invItem.unit_of_usage.unitDescription ?: entry.description ?: ""
						listComlogikDto.add(itemDto)
					} else {
						if (entry.credit?:0.compareTo(BigDecimal.ZERO) > 0) {
							exist.philhealthQuantity -= entry.qty
						} else {
							exist.philhealthQuantity += entry.qty
						}
					}

				}

			}

			if (includePatientMeds) {
				patientMeds.each {
					entry ->

						ComlogikItemDto dto = new ComlogikItemDto()

						def exist = listComlogikDto.find {
							it.id == entry.id
						}

						if (!exist) {
							dto.id = entry.id
							dto.refId = entry.id
							dto.philhealthDrugCode = ""
							dto.philhealthPNDFCode = ""
							dto.philhealthGenericName = entry.medicine_name ?: ""
							dto.philhealthBrandName = ""
							dto.philhealthQuantity = entry.qty_onhand.toInteger()
							dto.philhealthActualUnitPrice = 0
							if (entry.entry_datetime != null) {
								dto.philhealthPurchaseDate = dateFormat.format(entry.entry_datetime)
							} else {
								dto.philhealthPurchaseDate = ""
							}
							dto.philhealthPreparation = entry.description
							listComlogikDto2.add(dto)
						} else {
							exist.philhealthQuantity += entry.qty_onhand.toInteger()
						}


				}

			}


		listComlogikDto.addAll(listComlogikDto2)

			String comlogikItems = ""

			if(aCase.comlogikRefNo){
				comlogikItems = getAllComlogikItems(aCase.comlogikRefNo)['rows']
			}else {
				def object = comlogikRefId(aCase.caseNo)

				if (object) {

					def rows = object['rows'] as ArrayList
					def refno = rows[0]['refno']?:""

					comlogikItems = getAllComlogikItems(refno.toString())['rows']
				}
			}

			Response deleteResp = deleteAllComlogikItems(comlogikItems, crendetials)

			if(deleteResp && !deleteResp.successful){
				throw new IllegalArgumentException("Error on deleting Items")
			}

			listComlogikDto.findAll { it.philhealthQuantity != 0 }.each { entry ->
				def med = new JSONObject()
				def medDto = meds.find {
					it.medicine.id == entry.id
				}
				if(aCase.comlogikRefNo){
					//comlogikItems = getAllComlogikItems(aCase.comlogikRefNo)['rows']
					med.put("refno", aCase.comlogikRefNo)
				}else {
					def object = comlogikRefId(aCase.caseNo)

					if (object) {

						def rows = object['rows'] as ArrayList
						def refno = rows[0]['refno']?:""

						//comlogikItems = getAllComlogikItems(refno.toString())['rows']
						med.put("refno", refno)
					}
				}

				med.put('id', 0)
//				med.put("pDrugCode", entry.philhealthDrugCode ?: "")
//				med.put("pPNDFCode", "")
//				med.put("pBrandName", entry.philhealthBrandName ?: "")
//				med.put("pPreparation", entry.philhealthPreparation ?: "")
				med.put("drugcode", entry.philhealthDrugCodePhic?:"")
				med.put("medicinedesc", entry.philhealthGenericName ?: "")
//				med.put("pDescription", entry.philhealthGenericName ?: "")

//                                    if(it?.doctorOrderId!=null){
//                                        var docOrders = doctorsOrderRepository?.findOne(UUID.fromString(it?.doctorOrderId))
//                                        meds.put("pRoute", docOrders?.route?:"")
//                                        meds.put("pInstructionFrequency", docOrders?.frequency?:"")
//                                    }


				if(medDto){
					med.put("route", medDto.route?:"")
					med.put("freq", medDto.frequency?:"")
				}else {
					med.put("route", "N/A")
					med.put("freq", "")
				}


				med.put("purchasedate", entry.philhealthPurchaseDate ?: "")
				med.put("qty", entry.philhealthQuantity ?: 0)
				if (entry.philhealthActualUnitPrice) {
					med.put("price", entry.philhealthActualUnitPrice)
				} else {
					med.put("price", "0.00")
				}
				formsArry.put(med)
			}
		}

		String finalResp

		if (formsArry) {

			for(def i in 0..formsArry.length()-1){
				OkHttpClient client = new OkHttpClient()
				def  dto = formsArry.getJSONObject(i)
				def valuePairs = new ArrayList<NameValuePair>(2)
				def keys = dto.keySet()

				keys.each {
					valuePairs.add(new BasicNameValuePair(it.toString(), dto.optString(it.toString())))
				}

				MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded")
				String requestBody = contentBuilder(valuePairs)
				RequestBody body = RequestBody.create(mediaType, requestBody)
				def request = new com.squareup.okhttp.Request.Builder()
						.url(comlogikSetting[0].host + "/api/saveaddmedicine")
						.method("POST", body)
						.addHeader("Cookie", crendetials)
						.build()

				def response = client.newCall(request).execute().body().string()

				if((formsArry.length() - 1) == i){
					finalResp = response
				}
			}

		}

		def loggedUser = SecurityUtils.currentLogin()

		if (loggedUser != null) {
			form.put("UserName", loggedUser)
		}

		if(finalResp){
			return  new JsonSlurper().parseText(finalResp)
		}else{
			throw new IllegalArgumentException('Something went wrong.')
		}
	}

	@GraphQLQuery(name = "get_all_comlogik_items")
	JSONObject getAllComlogikItems(@GraphQLArgument(name = 'refeNo') String refNo) {
		List<ComlogikSetting> comlogikSetting = comlogikSettingRepository.findAll()

		if (comlogikSetting) {
			OkHttpClient client = new OkHttpClient()
			MediaType mediaType = MediaType.parse("application/json; charset=utf-8")
			String requestBody = ""
			RequestBody body = RequestBody.create(mediaType, requestBody)

			HttpUrl.Builder httpBuilder = HttpUrl.parse(comlogikSetting[0].host + "/api/dataaccess/getmeds").newBuilder()
			httpBuilder.addQueryParameter("refno", refNo)
			def request = new com.squareup.okhttp.Request.Builder()
					.url(httpBuilder.build())
					.method("POST", body)
					.addHeader("Content-Type", "application/json; charset=utf-8")
					.build()
			Response response = client.newCall(request).execute()

			return new JSONObject(response.body().string())
		} else {
			throw new IllegalArgumentException("ComLogik Error! Please contact your system administrator.")
		}
	}

	@GraphQLMutation(name = "delete_comlogik_items")
	Response deleteAllComlogikItems(
			@GraphQLArgument(name = "medsArrayString") String medsArrayString,
			@GraphQLArgument(name = 'credentials') String crendetials
	){
		List<ComlogikSetting> comlogikSetting = comlogikSettingRepository.findAll()

		def form = new ArrayList<NameValuePair>(2)
		form.add(new BasicNameValuePair('meds', medsArrayString))

		def client = new OkHttpClient()


		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded")
		String requestBody = contentBuilder(form) ?: ""
		RequestBody body = RequestBody.create(mediaType, requestBody)
		def request = new com.squareup.okhttp.Request.Builder()
				.url(comlogikSetting[0].host + "/api/deletemedicines")
				.method("POST", body)
				.addHeader("Content-Type", "application/x-www-form-urlencoded")
				.addHeader("Cookie", crendetials)
				.build()

		Response response = client.newCall(request).execute()

		String responseString = response.body().string()

		return response
	}

	@GraphQLMutation(name = "saveconfinementdetails", description = "save confinement information to comlogik")
	String saveconfinementdetails(@GraphQLArgument(name = "caseId") UUID caseId) {
		Case aCase = caseRepository.findById(caseId).get()
		def object = comlogikRefId(aCase.caseNo)

		DateTimeFormatter formatter =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())

		DateTimeFormatter dobFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

		DateTimeFormatter formatterWithTime =
				DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a").withZone(ZoneId.systemDefault())

		List<ComlogikSetting> comlogikSetting = comlogikSettingRepository.findAll()

		List<PatientPhilhealthData> philhealthData = patientPhilhealthDataRepository.findByCaseId(caseId).sort {
			a,b -> b.lastModifiedDate <=> a.lastModifiedDate
		}

		def form = new JSONObject()
		if (aCase != null) {

			if (aCase?.caseNo != null) {
				form.put("CaseNo", aCase?.caseNo)
			} else {
				throw new IllegalArgumentException("Case no. is required!")

			}

			if (!philhealthData) {
				throw new IllegalArgumentException("No philhealth member data found.")
			}

			def loggedUser = SecurityUtils.currentLogin()

			if (loggedUser != null) {
				form.put("UserName", loggedUser)

			} else {
				throw new IllegalArgumentException("Invalid user. Please check currently logged in user.")
			}

			if (philhealthData[0].memberRelation == null) {
				throw new IllegalAccessException("Patient's Relation to Member is required!")
			}

			String[] relationValidValue = ['M', 'S', 'C', 'P']
			def isValid = relationValidValue.any {
				it.contains(philhealthData[0].memberRelation ?: "")
			}

			if (!isValid) {
				throw new IllegalAccessException("Patient's Relation to member is not valid.")
			}

			if (philhealthData[0].memberRelation != 'M') {
				if (aCase?.patient?.lastName != null || philhealthData[0].memberLastName != null) {
					form.put("pMemberLastName", philhealthData[0].memberLastName ?: "")
					form.put("pPatientLastName", aCase.patient.lastName ?: "")

				} else {
					throw new IllegalArgumentException("Member's last name is required.")
				}

				if (aCase?.patient?.firstName != null || philhealthData[0].memberFirstName != null) {
					form.put("pPatientFirstName", aCase.patient.firstName ?: "")
					form.put("pMemberFirstName", philhealthData[0].memberFirstName ?: "")
				} else {
					throw new IllegalArgumentException("Member's first name is required.")
				}

				if (aCase?.patient?.middleName != null || philhealthData[0].memberMiddleName != null) {
					form.put("pPatientMiddleName", aCase.patient.middleName ?: "")
					form.put("pMemberMiddleName", philhealthData[0].memberMiddleName ?: "")
				} else {
					throw new IllegalArgumentException("Member's middle name is required.")
				}

				if (philhealthData[0]?.memberSuffix && philhealthData[0].memberSuffix.length() > 5) {
					throw new IllegalArgumentException("Member's Suffix reached character limit should only consist of 5 characters or less.")

				} else {
					form.put("pMemberSuffix", philhealthData[0].memberSuffix ?: "")

				}

				if (aCase.patient?.nameSuffix && aCase.patient.nameSuffix.length() > 5) {
					throw new IllegalArgumentException("Patient's Suffix reached character limit should only consist of 5 characters or less.")

				} else {
					form.put("pPatientSuffix", aCase.patient.nameSuffix ?: "")

				}

				if (aCase?.patient?.dob != null || philhealthData[0].memberDob != null) {
					form.put("pMemberBirthDate", formatter.format(philhealthData[0].memberDob))
					form.put("pPatientBirthDate", dobFormat.format(aCase.patient.dob))

				} else {
					throw new IllegalArgumentException("Member's date of birth is required.")
				}

				form.put("pMailingAddress", aCase?.patient?.fullAddress)

				if (aCase?.patient?.gender != null || philhealthData[0].memberGender != null) {
					form.put("pMemberSex", philhealthData[0].memberGender?.substring(0, 1)?.toUpperCase()?.trim())
					form.put("pPatientSex", aCase?.patient?.gender?.substring(0, 1)?.toUpperCase()?.trim())

				} else {
					throw new IllegalArgumentException("Member's gender is required!")
				}

				form.put("pPatientIs", philhealthData[0].memberRelation ?: "")

				form.put("pPatientPIN", aCase.patient.philHealthId ?: "")
				form.put("pMemberPIN", philhealthData[0].memberPin ?: "")

			} else {
				if (aCase?.patient?.lastName != null) {
					form.put("pMemberLastName", philhealthData[0].memberLastName ?: "")
					form.put("pPatientLastName", philhealthData[0].memberLastName ?: "")

				} else {
					throw new IllegalArgumentException("Member's last name is required.")
				}

				if (aCase?.patient?.firstName != null) {
					form.put("pPatientFirstName", philhealthData[0].memberFirstName ?: "")
					form.put("pMemberFirstName", philhealthData[0].memberFirstName ?: "")
				} else {
					throw new IllegalArgumentException("Member's first name is required.")
				}

				if (aCase?.patient?.middleName != null) {
					form.put("pPatientMiddleName", philhealthData[0].memberMiddleName ?: "")
					form.put("pMemberMiddleName", philhealthData[0].memberMiddleName ?: "")
				} else {
					throw new IllegalArgumentException("Member's middle name is required.")
				}

				if (philhealthData[0]?.memberSuffix && philhealthData[0].memberSuffix.length() > 5) {
					throw new IllegalArgumentException("Member's Suffix reached character limit should only consist of 5 characters or less.")

				} else {
					form.put("pPatientSuffix", philhealthData[0].memberSuffix ?: "")
					form.put("pMemberSuffix", philhealthData[0].memberSuffix ?: "")

				}

				if (aCase?.patient?.dob != null) {
					form.put("pMemberBirthDate", formatter.format(philhealthData[0].memberDob))
					form.put("pPatientBirthDate", formatter.format(philhealthData[0].memberDob))

				} else {
					throw new IllegalArgumentException("Member's date of birth is required.")
				}

				form.put("pMailingAddress", aCase?.patient?.fullAddress)

				if (aCase?.patient?.gender != null) {
					form.put("pMemberSex", philhealthData[0].memberGender?.substring(0, 1)?.toUpperCase()?.trim())
					form.put("pPatientSex", philhealthData[0].memberGender?.substring(0, 1)?.toUpperCase()?.trim())

				} else {
					throw new IllegalArgumentException("Member's gender is required!")
				}

				form.put("pPatientIs", philhealthData[0].memberRelation ?: "")

				form.put("pPatientPIN", philhealthData[0].memberPin ?: "")
				form.put("pMemberPIN", philhealthData[0].memberPin ?: "")

			}

			if (philhealthData[0].memberZipCode != null) {
				form.put("pZipCode", philhealthData[0].memberZipCode ?: "")
			} else {
				throw new IllegalArgumentException("Zipcode is requried.")
			}

			if (aCase.patient.civilStatus) {
				if (aCase.patient.civilStatus.contains("NEW BORN") || aCase.patient.civilStatus.contains("CHILD")) {
					form.put("pCivilStatus", "S")
				} else {
					form.put("pCivilStatus", aCase.patient.civilStatus[0] ?: "")
				}
			} else {
				if (philhealthData[0].memberCivilStatus) {
					if (philhealthData[0].memberCivilStatus.contains("NEW BORN") || philhealthData[0].memberCivilStatus.contains("CHILD")) {
						form.put("pCivilStatus", "S")
					} else {
						form.put("pCivilStatus", philhealthData[0].memberCivilStatus[0] ?: "")
					}
				} else {
					throw new IllegalArgumentException("Invalid Civil Status")
				}
			}

			form.put("pMemberShipType", philhealthData[0].memberType ?: "")

			if (aCase != null) {
				if (aCase.chiefComplaint != null) {
					form.put("pChiefComplaint", aCase.chiefComplaint ?: "")
				} else {
					throw new IllegalArgumentException("Chief Complaints is required.")
				}

			} else {
				throw new IllegalArgumentException("Chief Complaints is required.")
			}

			if (aCase.admissionDatetime != null) {
				form.put("pAdmissionDate", formatterWithTime.format(aCase.admissionDatetime))
			} else {
				form.put("pAdmissionDate", "")
			}

			if (aCase.dischargedDatetime) {
				form.put("pDischargeDate", formatterWithTime.format(aCase.dischargedDatetime))
			} else {
				if (aCase.admissionDatetime != null) {
					form.put("pDischargeDate", formatterWithTime.format(aCase.admissionDatetime))
				} else {
					throw new IllegalArgumentException("No admission date.")
				}

			}

			form.put("pPEN", aCase.companyPen ?: "")
			form.put("pEmailAddress", "")
			form.put("pEmployerName", aCase.occupation ?: "")
			form.put("pRVS", "")
			form.put("pTotalAmountActual", "")
			form.put("pTotalAmountClaimed", "")
			form.put("pIsFinal", "0")

//            form.put("isok", "")
//            form.put("trackno", "")
//            form.put("with3over6", "")
//            form.put("with9over12","")
//            form.put("remainingdays", "")
//            form.put("asof", "")
//            form.put("reason", "")
//            form.put("reason", "")
//            form.put("ReferenceNo", "")
		} else {
			throw new IllegalArgumentException("No philhealth data specified.")
		}

		if (comlogikSetting) {

//            def httpclient = HttpClients.custom().build()
//            def post = new HttpPost(comlogikSetting[0].host  + "/api/dataaccess/saveeligibility")
//
//            def auth = comlogikSetting[0].login + ":" + comlogikSetting[0].password
//            def encodedAuth = auth.bytes.encodeBase64().toString()
//            def authHeader = "Basic " + encodedAuth
//            post.addHeader("content-type", "application/x-www-form-urlencoded");
//            post.setHeader(HttpHeaders.AUTHORIZATION, authHeader)
//            post.entity = new StringEntity(form.toString())
//
//
//            def response = httpclient.execute(post)
			def credentials = new UsernamePasswordCredentials(comlogikSetting[0].login, comlogikSetting[0].password)
			def executor = Executor.newInstance().auth(credentials)

			def response = executor.execute(Request.Post(comlogikSetting[0].host + "/api/saveconfinementdetails")
					.bodyString(form.toString(), ContentType.APPLICATION_JSON)).returnContent().asString()

			if (object) {
				def count = object['total'] as String

				if (StringUtils.equals(count, "1")) {
					def rows = object['rows'] as ArrayList
					def refno = rows[0]['refno']
					form.put("ReferenceNo", refno ?: "")

					aCase.comlogikRefNo = refno ?: ""
					caseRepository.save(aCase)
				}
			}

			return response
		} else {
			throw new IllegalArgumentException("ComLogik Error! Please contact your system administrator.")
		}

	}

	@GraphQLMutation(name = "savecourseinthewards", description = "save course in the ward to comlogik")
	String savecourseinthewards(@GraphQLArgument(name = "caseId") UUID caseId) {
		Case aCase = caseRepository.findById(caseId).get()

		DateTimeFormatter formatter =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
		DateTimeFormatter formatterWithTime =
				DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a").withZone(ZoneId.systemDefault())

		List<ComlogikSetting> comlogikSetting = comlogikSettingRepository.findAll()
		def executor = Executor.newInstance()
				.auth(new HttpHost(comlogikSetting[0].host), comlogikSetting[0].login, comlogikSetting[0].password)

		String response = ""

		if (aCase) {
			def form = new JSONObject()
			List<DoctorOrder> doctorOrders = doctorOrderRepository.getDoctorOrdersByCase(caseId)

			if (doctorOrders) {
				doctorOrders.each {
					doctorOrder ->

						List<DoctorOrderItem> doItems = doctorOrderItemRepository.getDoctorOrderItemsByDoctorOrder(doctorOrder.id).sort {
							it.createdDate
						}

						def loggedUser = SecurityUtils.currentLogin()

						if (loggedUser != null) {
							form.put("UserName", loggedUser)

						} else {
							throw new IllegalArgumentException("Invalid user. Please check currently logged in user.")
						}

						form.put('CaseNo', aCase.caseNo)

						form.put('TransDate', formatterWithTime.format(doctorOrder.entryDateTime))

						List<String> orders = new ArrayList<>()

						if (doItems) {

							doItems.each {
								doItem ->
									orders.add(doItem.order)

							}

						}

						if (orders) {
							form.put('DoctorsAction', StringUtils.join(orders, '\n'))
						}else{
							form.put('DoctorsAction', "")
						}

						if (comlogikSetting) {
							response = executor.execute(Request.Post(comlogikSetting[0].host + "/api/savecourseinthewards2")
									.bodyString(form.toString(), ContentType.APPLICATION_JSON)).returnContent().asString()
						}

				}
			} else {
				throw new IllegalArgumentException("No notes are found!")
			}
		} else {
			throw new IllegalArgumentException("Case no. is required!")
		}

		def respObj = new JSONObject(response)

		if (respObj.optString("Status", "-1") == "0") {
			throw new IllegalArgumentException(respObj.optString("Message", ""))
		}

		return response

	}

	@GraphQLMutation(name = "savecaserates")
	String savecaserates(@GraphQLArgument(name = "caseId") UUID caseId, @GraphQLArgument(name = "billingId") UUID billingId) {
		List<ComlogikSetting> comlogikSetting = comlogikSettingRepository.findAll()
		def billing = billingService.findOne(billingId)
		def caseRates = billingService.getCaseRatesFromBilling(billingId)
		def loggedUser = SecurityUtils.currentLogin()
		def form = new JSONObject()
		def aCase = caseRepository.findById(caseId).get()
		if (aCase.caseNo) {
			form.put('CaseNo', aCase.caseNo)
		} else {
			throw new IllegalArgumentException(("Invalid Case #"))
		}

		if (loggedUser != null) {
			form.put("UserName", loggedUser)

		} else {
			throw new IllegalArgumentException("Invalid user. Please check currently logged in user.")
		}

		def totalPfDeductions = 0.0
		def totalPf = 0.0
		def totalPhilhealthBenefitPf = 0.0
		def totalPhilhealthBenefit = 0.0
		def totalDeductions = 0.0
		billing.billingItemList.findAll {
			it.itemType == BillingItemType.DEDUCTIONSPF &&
					it.status == BillingItemStatus.ACTIVE
		}.each {
			if (it.details.containsKey(BillingItemDetailParam.COMPANY_ACCOUNT_ID.name())) {
				def cid = it.details[BillingItemDetailParam.COMPANY_ACCOUNT_ID.name()] as String
				def companyAccount = companyAccountServices.findOne(UUID.fromString(cid))
				if (companyAccount.companyname.contains('PHIC')) {
					totalPhilhealthBenefitPf += it.subTotal

				} else {
					totalPfDeductions += it.subTotal

				}
			} else {
				totalPfDeductions += it.subTotal
			}

		}

		billing.billingItemList.findAll {
			it.itemType == BillingItemType.PF &&
					it.status == BillingItemStatus.ACTIVE
		}.each {
			totalPf += it.subTotal
		}

		billing.billingItemList.findAll {
			it.itemType == BillingItemType.DEDUCTIONS &&
					it.status == BillingItemStatus.ACTIVE
		}.each {
			if (it.details.containsKey(BillingItemDetailParam.COMPANY_ACCOUNT_ID.name())) {
				def cid = it.details[BillingItemDetailParam.COMPANY_ACCOUNT_ID.name()] as String
				def companyAccount = companyAccountServices.findOne(UUID.fromString(cid))
				if (companyAccount.companyname.contains('PHIC')) {
					totalPhilhealthBenefit += it.subTotal

				} else {
					totalDeductions += it.subTotal

				}
			} else {
				totalDeductions += it.subTotal
			}
		}

		if (caseRates) {
			caseRates.each {
				if (StringUtils.equalsAnyIgnoreCase(it.tier, 'PRIMARY')) {
					if (it.type == 'RVS') {
						form.put('firstCaseRateType', 'R')
						form.put("pCaseRateCode1", it.code ?: "")
						form.put("pCaseRateAmount1", ((it.hospitalShare ?: BigDecimal.ZERO).add(it.pfShare ?: BigDecimal.ZERO)).toString())
					}

					if (it.type == 'ICD') {
						form.put('firstCaseRateType', 'I')
						form.put("pCaseRateCode1", it.code ?: "")
						form.put("pCaseRateAmount1", ((it.hospitalShare ?: BigDecimal.ZERO).add(it.pfShare ?: BigDecimal.ZERO)).toString())
					}
				} else {
					form.put('firstCaseRateType', "")
					form.put("pCaseRateCode1", "")
					form.put("pCaseRateAmount1", "")
				}

				if (it.tier == 'SECONDARY') {
					if (it.type == 'RVS') {
						form.put('secondCaseRateType', 'R')
						form.put("pCaseRateCode2", it.code ?: "")
						form.put("pCaseRateAmount2", ((it.hospitalShare ?: BigDecimal.ZERO).add(it.pfShare ?: BigDecimal.ZERO)).toString())

					}

					if (it.type == 'ICD') {
						form.put('secondCaseRateType', 'R')
						form.put("pCaseRateCode2", it.code ?: "")
						form.put("pCaseRateAmount2", ((it.hospitalShare ?: BigDecimal.ZERO).add(it.pfShare ?: BigDecimal.ZERO)).toString())
					}
				} else {
					form.put('secondCaseRateType', "")
					form.put("pCaseRateCode2", "")
					form.put("pCaseRateAmount2", "")
				}
			}
		} else {
			throw new IllegalArgumentException("No Diagnosis found! Please set Patient's Diagnosis")

		}

//        if(aCase.primaryDx){
//            def dx = new JSONObject(aCase.primaryDx)
//
//            if(dx.length() !=0 && !dx.has('primaryDx')){
//                if(dx.getString('__typename') == "ICDCode"){
//                    form.put('firstCaseRateType', 'I')
//                    form.put("pCaseRateCode1", dx.getString("diagnosisCode"))
//                    form.put("pCaseRateAmount1",dx.getDouble("primaryAmount1").toString())
//
//                }else if(dx.getString('__typename') == "RVSCode"){
//                    form.put('firstCaseRateType', 'R')
//                    form.put("pCaseRateCode1", dx.getString("rvsCode"))
//                    form.put("pCaseRateAmount1",dx.getDouble("primaryAmount1").toString())
//
//                }
//            }else {
//                form.put('firstCaseRateType', "")
//                form.put("pCaseRateCode1", "")
//                form.put("pCaseRateAmount1", "")
//            }
//        }else {
//            throw new IllegalArgumentException("No Primary Diagnosis found! Please set your Primary Diagnosis")
//        }
//
//        if(aCase.secondaryDx){
//            def dx = new JSONObject(aCase.secondaryDx)
//            if(dx.length() !=0 && !dx.has('secondaryDx')){
//                if(dx.getString('__typename') == "ICDCode"){
//                    form.put('secondCaseRateType', 'I')
//                    form.put("pCaseRateCode2", dx.getString("diagnosisCode"))
//                    form.put("pCaseRateAmount2",dx.getDouble("primaryAmount1").toString())
//
//
//                }else if(dx.getString('__typename') == "RVSCode"){
//                    form.put('secondCaseRateType', 'R')
//                    form.put("pCaseRateCode2", dx.getString("rvsCode"))
//                    form.put("pCaseRateAmount2",dx.getDouble("primaryAmount1").toString())
//
//                }
//            }else{
//                form.put('secondCaseRateType', "")
//                form.put("pCaseRateCode2", "")
//                form.put("pCaseRateAmount2", "")
//            }
//        }

		def pActualCharges = billing.totals - totalPf
		def pDiscount = pActualCharges + totalDeductions

		form.put("pTotalActualCharges", (pActualCharges ?: 0.0).toString())
		form.put("pDiscount", pDiscount.toString() ?: "0.0")
		form.put("pEnoughBenefits", billing.totals.compareTo(totalPhilhealthBenefit + totalPhilhealthBenefitPf) == -1 ? "Y" : "N")
		form.put("pPhilhealthBenefit", totalPhilhealthBenefit.abs().toString())
		form.put("pTotalActualChargesPf", totalPf.toString())
		def pDiscountPf = totalPf - totalPfDeductions.abs()
		form.put("pDiscountPf", (pDiscountPf ?: 0.0).toString())
		form.put("pPhilhealthBenefitPf", totalPhilhealthBenefitPf.abs().toString())

		if (comlogikSetting) {
			def executor = Executor.newInstance()
					.auth(new HttpHost(comlogikSetting[0].host), comlogikSetting[0].login, comlogikSetting[0].password)

			def response = executor.execute(Request.Post(comlogikSetting[0].host + "/api/savecaserates")
					.bodyString(form.toString(), ContentType.APPLICATION_JSON)).returnContent().asString()

			return response
		} else {
			throw new IllegalArgumentException("ComLogik Error! Please contact your system administrator.")
		}

	}

	@GraphQLMutation(name = "savediagnosis")
	String savediagnosis(@GraphQLArgument(name = "caseId") UUID caseId) {
		List<ComlogikSetting> comlogikSetting = comlogikSettingRepository.findAll()
		def loggedUser = SecurityUtils.currentLogin()
		def form = new JSONObject()
		def aCase = caseRepository.findById(caseId).get()

		def icds = new JSONArray()
		def rvs = new JSONArray()

		if (aCase.caseNo) {
			form.put('CaseNo', aCase.caseNo)
		} else {
			throw new IllegalArgumentException(("Invalid Case #"))
		}

		if (loggedUser != null) {
			form.put("UserName", loggedUser)

		} else {
			throw new IllegalArgumentException("Invalid user. Please check currently logged in user.")
		}

		if (aCase.icdDiagnosis) {
			def dohIcds = new JSONArray(aCase.icdDiagnosis)

			for (Integer i in 0..dohIcds.length() - 1) {
				def dto = new JSONObject()

				dto.put("pICDCode", dohIcds.getJSONObject(i).optString("diagnosisCode", ""))
				icds.put(dto)
			}
		}

		if (aCase.rvsDiagnosis) {
			def dohRvs = new JSONArray(aCase.rvsDiagnosis)
			for (Integer i in 0..dohRvs.length() - 1) {
				def dto = new JSONObject()
				dto.put("pRVSCode", dohRvs.getJSONObject(i).optString("rvsCode", ""))
				dto.put("pRelatedProcedure", "")
				dto.put("pLaterality", "N/A")
				dto.put("pProcedureDate", "")
				rvs.put(dto)
			}
		}

		form.put("Icd", icds ?: "[]")

		form.put("Rvs", rvs ?: "[]")

		form.put("Diagnosis", aCase.admittingDiagnosis ?: "")

		if (comlogikSetting) {
			def executor = Executor.newInstance()
					.auth(new HttpHost(comlogikSetting[0].host), comlogikSetting[0].login, comlogikSetting[0].password)

			def response = executor.execute(Request.Post(comlogikSetting[0].host + "/api/savediagnosis")
					.bodyString(form.toString(), ContentType.APPLICATION_JSON)).returnContent().asString()

			return response
		} else {
			throw new IllegalArgumentException("ComLogik Error! Please contact your system administrator.")
		}

	}

	@GraphQLMutation(name = "savepfcharges")
	String savepfcharges(@GraphQLArgument(name = "caseId") UUID caseId, @GraphQLArgument(name = "billingId") UUID billingId) {
		List<ComlogikSetting> comlogikSetting = comlogikSettingRepository.findAll()
		def billing = billingService.findOne(billingId)
		def loggedUser = SecurityUtils.currentLogin()
		def form = new JSONObject()
		def aCase = caseRepository.findById(caseId).get()

		String pattern = "#,##0.0#"
		DecimalFormat decimalFormat = new DecimalFormat(pattern)
		decimalFormat.setParseBigDecimal(true)

		DateTimeFormatter dateFormat =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())

		def pfcharges = new JSONArray()

		if (aCase) {
			form.put("CaseNo", aCase.caseNo)
		} else {
			throw new IllegalArgumentException("Invalid Case no.")
		}

		if (loggedUser != null) {
			form.put("UserName", loggedUser)

		} else {
			throw new IllegalArgumentException("Invalid user. Please check currently logged in user.")
		}

		def pfDeductions = billing.billingItemList.findAll {
			it.itemType == BillingItemType.DEDUCTIONSPF && it.status == BillingItemStatus.ACTIVE
		}

		billing.billingItemList.findAll {
			it.itemType == BillingItemType.PF &&
					it.status == BillingItemStatus.ACTIVE
		}.each {
			if (it.details.containsKey('PF_EMPLOYEEID')) {
				def deductions = 0.0
				if (pfDeductions) {
					pfDeductions.findAll {
						deduction ->
							deduction.details['PF_EMPLOYEEID'] == deduction.details.get("PF_EMPLOYEEID")
					}.each {
						deduction2 ->
							def pf = (deduction2.debit ?: BigDecimal.ZERO) - (deduction2.credit ?: BigDecimal.ZERO)

							deductions += pf
					}
				}
				def dto = new JSONObject()
				def employee = employeeRepository.findById(UUID.fromString(it.details.get("PF_EMPLOYEEID"))).get()
				def pf = decimalFormat.format(((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO)) - deductions.abs())
				dto.put("pDoctorAccreCode", employee?.prcLicenseNo)
				dto.put("pDoctorLastName", employee?.lastName)
				dto.put("pDoctorFirstName", employee?.firstName)
				dto.put("pDoctorMiddleName", employee?.middleName)
				dto.put("pDoctorSuffix", employee?.nameSuffix)

				if (pf) {
					dto.put("pWithCoPay", "Y")
					dto.put("pDoctorCoPay", pf - deductions)
				} else {
					dto.put("pWithCoPay", "N")
					dto.put("pDoctorCoPay", pf - deductions)
				}

				dto.put("pDoctorSignDate", dateFormat.format(it.entryDate))
				pfcharges.put(dto)
			}
		}

		if (pfcharges) {
			form.put("pfcharges", pfcharges)
		}

		if (comlogikSetting) {
			def executor = Executor.newInstance()
					.auth(new HttpHost(comlogikSetting[0].host), comlogikSetting[0].login, comlogikSetting[0].password)

			def response = executor.execute(Request.Post(comlogikSetting[0].host + "/api/savepfcharges")
					.bodyString(form.toString(), ContentType.APPLICATION_JSON)).returnContent().asString()

			return response
		} else {
			throw new IllegalArgumentException("ComLogik Error! Please contact your system administrator.")
		}
	}

	@GraphQLMutation(name = "saveothers")
	String saveothers(@GraphQLArgument(name = "caseId") UUID caseId, @GraphQLArgument(name = "billingId") UUID billingId) {

		List<ComlogikSetting> comlogikSetting = comlogikSettingRepository.findAll()
		def billing = billingService.findOne(billingId)
		def loggedUser = SecurityUtils.currentLogin()
		def form = new JSONObject()
		def aCase = caseRepository.findById(caseId).get()

		DateTimeFormatter formatter =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())

		if (aCase) {
			form.put("CaseNo", aCase.caseNo)
		} else {
			throw new IllegalArgumentException("Invalid Case no.")
		}

		if (loggedUser != null) {
			form.put("UserName", loggedUser)

		} else {
			throw new IllegalArgumentException("Invalid user. Please check currently logged in user.")
		}

		def others = new JSONArray()

		billing.billingItemList.findAll {
			it.itemType == BillingItemType.DIAGNOSTICS &&
					it.status == BillingItemStatus.ACTIVE
		}.each {
			def service = serviceRepository.findById(UUID.fromString(it.details.get('SERVICEID'))).get()

			def dto = new JSONObject()

			if (service.department.departmentName.contains('LABORATORY')) {
				dto.put('pDiagnosticName', service.description ?: "")
				dto.put('pDiagnosticType', "LABORATORY")
				dto.put('pQuantity', it.qty.toString() ?: "0")
				dto.put('pDiagnosticDate', (formatter.format(it.entryDate)) ?: "")
			} else if (service.department.departmentName.contains('IMAGING')) {
				dto.put('pDiagnosticName', service.description ?: "")
				dto.put('pDiagnosticType', "IMAGING")
				dto.put('pQuantity', it.qty.toString() ?: "0")
				dto.put('pDiagnosticDate', (formatter.format(it.entryDate)) ?: "")
			} else {
				dto.put('pDiagnosticName', service.description ?: "")
				dto.put('pDiagnosticType', "OTHERS")
				dto.put('pQuantity', it.qty.toString() ?: "0")
				dto.put('pDiagnosticDate', (formatter.format(it.entryDate)) ?: "")
			}

			others.put(dto)
		}

		billing.billingItemList.findAll {
			it.itemType == BillingItemType.SUPPLIES &&
					it.status == BillingItemStatus.ACTIVE
		}.each {
			def item = itemRepository.findById(UUID.fromString(it.details.get('ITEMID'))).get()
			def dto = new JSONObject()

			if (it.itemType == BillingItemType.SUPPLIES) {
				dto.put('pDiagnosticName', item.descLong ?: "")
				dto.put('pDiagnosticType', "SUPPLIES")
				dto.put('pQuantity', it.qty.toString() ?: "0")
				dto.put('pDiagnosticDate', (formatter.format(it.entryDate)) ?: "")
			}

			others.put(dto)
		}

		form.put("Others", others ?: "[]")

		if (comlogikSetting) {
			def executor = Executor.newInstance()
					.auth(new HttpHost(comlogikSetting[0].host), comlogikSetting[0].login, comlogikSetting[0].password)

			def response = executor.execute(Request.Post(comlogikSetting[0].host + "/api/saveothers")
					.bodyString(form.toString(), ContentType.APPLICATION_JSON)).returnContent().asString()

			return response
		} else {
			throw new IllegalArgumentException("ComLogik Error! Please contact your system administrator.")
		}
	}

	@GraphQLMutation(name = "savecf4")
	Object savecf4(@GraphQLArgument(name = "caseId") UUID caseId) {
		List<ComlogikSetting> comlogikSetting = comlogikSettingRepository.findAll()
		def aCase = caseRepository.findById(caseId).get()
		def form = new ArrayList<NameValuePair>(2)

		def symptoms = new JSONArray("[]")
		def otherSymptoms = ""

		def heents = new JSONArray("[]")
		def heentOther = ""

		def chests = new JSONArray("[]")
		def chestOther = ""

		def cvs = new JSONArray("[]")
		def cvsOthers = ""

		def abdomen = new JSONArray("[]")
		def abdomenOthers = ""

		def guie = new JSONArray("[]")
		def guieOthers = ""

		def skins = new JSONArray("[]")
		def skinOtehrs = ""

		def nueros = new JSONArray("[]")
		def nueroOthers = ""

		if (aCase) {
			if (aCase.pertinentSymptomsList) {
				def symptomLists = new JSONArray(aCase.pertinentSymptomsList)

				if (symptomLists != null && symptomLists.length() != 0) {
					for (def i in 0..symptomLists.length() - 1) {
						def pertinentSymtom = symptomLists.getJSONObject(i)
						//--SYMPTOMS

						if (pertinentSymtom.optString('field', '') == 'alteredmentalsensorium' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id', 39)
							obj.put('SymptomsId', '1')
							obj.put('SymptomsDescription', 'ALTERED MENTAL SENSORIUM')
							obj.put('Active', true)

							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'abdominalcramppain' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id', 40)
							obj.put('SymptomsId', '2')
							obj.put('SymptomsDescription', 'ABDOMINAL CRAMP/PAIN')
							obj.put('Active', true)

							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'anorexia' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id', 41)
							obj.put('SymptomsId', '3')
							obj.put('SymptomsDescription', 'ANOREXIA')
							obj.put('Active', true)

							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'bleedinggums' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id', 42)
							obj.put('SymptomsId', '4')
							obj.put('SymptomsDescription', 'BLEEDING GUMS')
							obj.put('Active', true)

							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'bodyweakness' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id', 43)
							obj.put('SymptomsId', '5')
							obj.put('SymptomsDescription', 'BODY WEAKNESS')
							obj.put('Active', true)

							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'blurringofvision' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id', 44)
							obj.put('SymptomsId', '6')
							obj.put('SymptomsDescription', 'BLURRING OF VISION')
							obj.put('Active', true)

							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'chestpaindiscomfort' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id', 46)
							obj.put('SymptomsId', '8')
							obj.put('SymptomsDescription', 'CHEST PAIN/DISCOMFORT')
							obj.put('Active', true)

							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'constipation' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id', 45)
							obj.put('SymptomsId', '7')
							obj.put('SymptomsDescription', 'CONSTIPATION')
							obj.put('Active', true)

							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'cough' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id', 47)
							obj.put('SymptomsId', '9')
							obj.put('SymptomsDescription', 'COUGH')
							obj.put('Active', true)

							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'diarrhea' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id', 48)
							obj.put('SymptomsId', '10')
							obj.put('SymptomsDescription', 'DIARRHEA')
							obj.put('Active', true)

							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'dizziness' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id', 49)
							obj.put('SymptomsId', '11')
							obj.put('SymptomsDescription', 'DIZZINESS')
							obj.put('Active', true)

							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'dysphagia' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id', 50)
							obj.put('SymptomsId', '12')
							obj.put('SymptomsDescription', 'DYSPHAGIA')
							obj.put('Active', true)

							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'dyspnea' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id', 51)
							obj.put('SymptomsId', '13')
							obj.put('SymptomsDescription', 'DYSPNEA')
							obj.put('Active', true)
							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'dysuria' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id', 52)
							obj.put('SymptomsId', '14')
							obj.put('SymptomsDescription', 'DYSURIA')
							obj.put('Active', true)

							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'epistaxis' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id', 53)
							obj.put('SymptomsId', '15')
							obj.put('SymptomsDescription', 'EPISTAXIS')
							obj.put('Active', true)
							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'fever' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id', 72)
							obj.put('SymptomsId', '37')
							obj.put('SymptomsDescription', 'FEVER')
							obj.put('Active', true)

							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'frequencyofurination' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id', 54)
							obj.put('SymptomsId', '17')
							obj.put('SymptomsDescription', 'FREQUENCY OF URINATION')
							obj.put('Active', true)

							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'headache' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id', 55)
							obj.put('SymptomsId', '18')
							obj.put('SymptomsDescription', 'HEADACHE')
							obj.put('Active', true)

							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'hematemesis' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id', 56)
							obj.put('SymptomsId', '19')
							obj.put('SymptomsDescription', 'HEMATEMESIS')
							obj.put('Active', true)

							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'hematuria' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id', 57)
							obj.put('SymptomsId', '20')
							obj.put('SymptomsDescription', 'HEMATURIA')
							obj.put('Active', true)

							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'hemoptysis' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id', 58)
							obj.put('SymptomsId', '21')
							obj.put('SymptomsDescription', 'HEMOPTYSIS')
							obj.put('Active', true)
							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'irritability' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id', 59)
							obj.put('SymptomsId', '22')
							obj.put('SymptomsDescription', 'IRRITABILITY')
							obj.put('Active', true)

							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'jaundice' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id', 60)
							obj.put('SymptomsId', '23')
							obj.put('SymptomsDescription', 'JAUNDICE')
							obj.put('Active', true)

							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'lowerextremityedema' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id', 61)
							obj.put('SymptomsId', '25')
							obj.put('SymptomsDescription', 'LOWER EXTREMITY EDEMA')
							obj.put('Active', true)

							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'myalgia' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id', 61)
							obj.put('SymptomsId', '25')
							obj.put('SymptomsDescription', 'LOWER EXTREMITY EDEMA')
							obj.put('Active', true)

							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'orthopnea' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id', 63)
							obj.put('SymptomsId', '27')
							obj.put('SymptomsDescription', 'ORTHOPNEA')
							obj.put('Active', true)

							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'palpitations' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id', 64)
							obj.put('SymptomsId', '28')
							obj.put('SymptomsDescription', 'PALPITATIONS')
							obj.put('Active', true)

							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'seizures' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id', 68)
							obj.put('SymptomsId', '33')
							obj.put('SymptomsDescription', 'SEIZURES')
							obj.put('Active', true)

							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'skinrashes' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id', 65)
							obj.put('SymptomsId', '29')
							obj.put('SymptomsDescription', 'SKIN RASHES')
							obj.put('Active', true)

							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'stoolbloodyblacktarrymucoid' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id', 66)
							obj.put('SymptomsId', '30')
							obj.put('SymptomsDescription', 'STOOL, BLOODY/BLACK TARRY/MUCOID')
							obj.put('Active', true)

							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'sweating' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							//obj.put('Id', 1)
							obj.put('SymptomsId', '32')
							obj.put('SymptomsDescription', 'SWEATING')

							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'urgency' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id', 69)
							obj.put('SymptomsId', '34')
							obj.put('SymptomsDescription', 'URGENCY')

							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'vomiting' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id', 70)
							obj.put('SymptomsId', '36')
							obj.put('SymptomsDescription', 'VOMITING/NAUSEA')
							obj.put('Active', true)

							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'weightloss' && pertinentSymtom.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id', 71)
							obj.put('SymptomsId', '36')
							obj.put('SymptomsDescription', 'WEIGHT LOSS')
							obj.put('Active', true)

							symptoms.put(obj)
						}

						if (pertinentSymtom.optString('field', '') == 'pertinentothers' && pertinentSymtom.optString('value', '')) {
							def obj = new JSONObject()

							obj.put('Id', 74)
							obj.put('SymptomsId', 'X')
							obj.put('SymptomsDescription', 'OTHERS')
							obj.put('Active', true)

							symptoms.put(obj)

							otherSymptoms = pertinentSymtom.optString('value', '')

						}
					}
				}
			}

			if (aCase.physicalExamList) {
				def physicalExamList = new JSONArray(aCase.physicalExamList)

				if (physicalExamList != null && physicalExamList.length() != 0) {
					for (def i in 0..physicalExamList.length() - 1) {
						def physicalExam = physicalExamList.getJSONObject(i)

						//---HEENT

						if (physicalExam.optString('field', "") == 'awakealert' && physicalExam.optBoolean('value', false)) {
							def exist = form.find {
								it.name == "pGenSurveyId"
							}
							if (!exist) {
								form.add(new BasicNameValuePair('pGenSurveyId', "1"))
							}
						}

						if (physicalExam.optString('field', "") == 'alteredsensorium' && physicalExam.optBoolean('value', false)) {
							def exist = form.find {
								it.name == "pGenSurveyId"
							}
							if (!exist) {
								form.add(new BasicNameValuePair('pGenSurveyId', "2"))
							}
						}

						if (physicalExam.optString('field', '') == 'essentiallynormal' && physicalExam.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id1', 1)
							obj.put('Id', '11')
							obj.put('Description', 'Essentially Normal')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							heents.put(obj)
						}

						if (physicalExam.optString('field', '') == 'abnormalpupillaryreaction' && physicalExam.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id1', 2)
							obj.put('Id', '12')
							obj.put('Description', 'Abnormal pupillary reaction')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							heents.put(obj)
						}

						if (physicalExam.optString('field', '') == 'cervicallymphadenopthy' && physicalExam.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id1', 3)
							obj.put('Id', '13')
							obj.put('Description', 'Cervical lympadenopathy')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							heents.put(obj)
						}

						if (physicalExam.optString('field', '') == 'drymucousmembrane' && physicalExam.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id1', 4)
							obj.put('Id', '14')
							obj.put('Description', 'Dry mucous membrane')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							heents.put(obj)
						}

						if (physicalExam.optString('field', '') == 'ictericsclerae' && physicalExam.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id1', 5)
							obj.put('Id', '15')
							obj.put('Description', 'Icteric sclerae')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							heents.put(obj)
						}

						if (physicalExam.optString('field', '') == 'paleconjunctivae' && physicalExam.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id1', 6)
							obj.put('Id', '16')
							obj.put('Description', 'Pale conjunctivae')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							heents.put(obj)
						}

						if (physicalExam.optString('field', '') == 'sunkenfontanelle' && physicalExam.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id1', 7)
							obj.put('Id', '17')
							obj.put('Description', 'Sunken eyeballs')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							heents.put(obj)
						}

						if (physicalExam.optString('field', '') == 'others' && physicalExam.optString('value', '')) {
							def obj = new JSONObject()

							obj.put('Id1', 9)
							obj.put('Id', '99')
							obj.put('Description', 'Others')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							heents.put(obj)

							heentOther = physicalExam.optString('value', '')

						}

						//---CHEST/LUNGS

						if (physicalExam.optString('field', '') == 'essentiallynormal2' && physicalExam.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id1', 7)
							obj.put('Id', '6')
							obj.put('Description', 'Essentially normal')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							chests.put(obj)
						}

						if (physicalExam.optString('field', '') == 'asymmetricalchestexpansion' && physicalExam.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id1', 8)
							obj.put('Id', '7')
							obj.put('Description', 'Asymmetrical chest expansion')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							chests.put(obj)
						}

						if (physicalExam.optString('field', '') == 'decreasedbreathsounds' && physicalExam.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id1', 9)
							obj.put('Id', '8')
							obj.put('Description', 'Decreased breath sounds')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							chests.put(obj)
						}

						if (physicalExam.optString('field', '') == 'wheezes' && physicalExam.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id1', 6)
							obj.put('Id', '5')
							obj.put('Description', 'Wheezes')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							chests.put(obj)
						}

						if (physicalExam.optString('field', '') == 'lumpsoverbreasts' && physicalExam.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id1', 2)
							obj.put('Id', '10')
							obj.put('Description', 'Lumps over breast(s)')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							chests.put(obj)
						}

						if (physicalExam.optString('field', '') == 'ralescracklesrhonchi' && physicalExam.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id1', 5)
							obj.put('Id', '4')
							obj.put('Description', 'Crackles/rales')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							chests.put(obj)
						}

						if (physicalExam.optString('field', '') == 'intercostalribclavicularretraction' && physicalExam.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id1', 4)
							obj.put('Id', '3')
							obj.put('Description', 'Retractions')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							chests.put(obj)
						}

						if (physicalExam.optString('field', '') == 'others2' && physicalExam.optString('value', '')) {
							def obj = new JSONObject()

							obj.put('Id1', 11)
							obj.put('Id', '99')
							obj.put('Description', 'Others')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							chests.put(obj)

							chestOther = physicalExam.optString('value', '')
						}

						//---CVS

						if (physicalExam.optString('field', '') == 'essentiallynormal3' && physicalExam.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id1', 5)
							obj.put('Id', '5')
							obj.put('Description', 'Essentially normal')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							cvs.put(obj)
						}

						if (physicalExam.optString('field', '') == 'displacedapexbeat' && physicalExam.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id1', 6)
							obj.put('Id', '6')
							obj.put('Description', 'Displaced apex beat')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							cvs.put(obj)
						}

						if (physicalExam.optString('field', '') == 'heavesthrills' && physicalExam.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id1', 3)
							obj.put('Id', '3')
							obj.put('Description', 'Heaves/trills')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							cvs.put(obj)
						}

						if (physicalExam.optString('field', '') == 'pericardialbulge' && physicalExam.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id1', 9)
							obj.put('Id', '9')
							obj.put('Description', 'Pericardial bulge')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							cvs.put(obj)
						}

						if (physicalExam.optString('field', '') == 'irregularrhythm' && physicalExam.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id1', 7)
							obj.put('Id', '7')
							obj.put('Description', 'Irregular rhythm"')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							cvs.put(obj)
						}

						if (physicalExam.optString('field', '') == 'muffledheartsounds' && physicalExam.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id1', 8)
							obj.put('Id', '8')
							obj.put('Description', 'Muffled heart sounds')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							cvs.put(obj)
						}

						if (physicalExam.optString('field', '') == 'murmur' && physicalExam.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id1', 4)
							obj.put('Id', '4')
							obj.put('Description', 'Murmurs')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							cvs.put(obj)
						}

						if (physicalExam.optString('field', '') == 'others3' && physicalExam.optString('value', '')) {
							def obj = new JSONObject()

							obj.put('Id1', 10)
							obj.put('Id', '99')
							obj.put('Description', 'Others')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							cvs.put(obj)
							cvsOthers = physicalExam.optString('value', '')
						}

						//---ABDOMEN

						if (physicalExam.optString('field', '') == 'essentiallynormal4' && physicalExam.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id1', 11)
							obj.put('Id', '7')
							obj.put('Description', 'Essentially normal')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							abdomen.put(obj)
						}

						if (physicalExam.optString('field', '') == 'abdominalrigidity' && physicalExam.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id1', 12)
							obj.put('Id', '8')
							obj.put('Description', 'Abdominal rigidity')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							abdomen.put(obj)
						}

						if (physicalExam.optString('field', '') == 'abdomentenderness' && physicalExam.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id1', 13)
							obj.put('Id', '9')
							obj.put('Description', 'Abdominal tenderness')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							abdomen.put(obj)
						}

						if (physicalExam.optString('field', '') == 'hyperactivebowelsounds' && physicalExam.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id1', 2)
							obj.put('Id', '10')
							obj.put('Description', 'Hyperactive bowel sounds')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							abdomen.put(obj)
						}

						if (physicalExam.optString('field', '') == 'palpablemass' && physicalExam.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id1', 3)
							obj.put('Id', '11')
							obj.put('Description', 'Palpable mass(es)')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							abdomen.put(obj)
						}

						if (physicalExam.optString('field', '') == 'uterinecontraction' && physicalExam.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id1', 5)
							obj.put('Id', '13')
							obj.put('Description', 'Uterine contraction')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							abdomen.put(obj)
						}

						if (physicalExam.optString('field', '') == 'others9' && physicalExam.optString('value', '')) {
							def obj = new JSONObject()

							obj.put('Id1', 14)
							obj.put('Id', '99')
							obj.put('Description', 'Others')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							abdomen.put(obj)
							abdomenOthers = physicalExam.optString('value', '')
						}

						//---GU(IE)

						if (physicalExam.optString('field', '') == 'essentiallynormal5' && physicalExam.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id1', 1)
							obj.put('Id', '1')
							obj.put('Description', 'Essentially normal')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							guie.put(obj)
						}

						if (physicalExam.optString('field', '') == 'bloodstainedexamfinger' && physicalExam.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id1', 2)
							obj.put('Id', '2')
							obj.put('Description', 'Blood stained in exam finger')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							guie.put(obj)
						}

						if (physicalExam.optString('field', '') == 'cervicaldilatation' && physicalExam.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id1', 3)
							obj.put('Id', '3')
							obj.put('Description', 'Cervical dilatation')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							guie.put(obj)
						}

						if (physicalExam.optString('field', '') == 'presenceabnormaldischarge' && physicalExam.optBoolean('value', false)) {
							def obj = new JSONObject()

							obj.put('Id1', 4)
							obj.put('Id', '4')
							obj.put('Description', 'Presence of abnormal discharge')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							guie.put(obj)
						}

						if (physicalExam.optString('field', '') == 'others4' && physicalExam.optString('value', '')) {
							def obj = new JSONObject()

							obj.put('Id1', 5)
							obj.put('Id', '99')
							obj.put('Description', 'Others')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							guie.put(obj)
							guieOthers = physicalExam.optString('value', '')
						}

						//---SKIN/EXTREMITIES

						if (physicalExam.optString('field', '') == 'essentiallynormal6' && physicalExam.optString('value', '')) {
							def obj = new JSONObject()

							obj.put('Id1', 5)
							obj.put('Id', '99')
							obj.put('Description', 'Others')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							skins.put(obj)

						}

						if (physicalExam.optString('field', '') == 'clubbing' && physicalExam.optString('value', '')) {
							def obj = new JSONObject()

							obj.put('Id1', 3)
							obj.put('Id', '2')
							obj.put('Description', 'Clubbing')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							skins.put(obj)

						}

						if (physicalExam.optString('field', '') == 'coldclammyskin' && physicalExam.optString('value', '')) {
							def obj = new JSONObject()

							obj.put('Id1', 4)
							obj.put('Id', '3')
							obj.put('Description', 'Cold clammy')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							skins.put(obj)

						}

						if (physicalExam.optString('field', '') == 'cyanosismottledskin' && physicalExam.optString('value', '')) {
							def obj = new JSONObject()

							obj.put('Id1', 5)
							obj.put('Id', '4')
							obj.put('Description', 'Cyanosis/mottled skin')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							skins.put(obj)

						}

						if (physicalExam.optString('field', '') == 'edemaswelling' && physicalExam.optString('value', '')) {
							def obj = new JSONObject()

							obj.put('Id1', 6)
							obj.put('Id', '5')
							obj.put('Description', 'Edema/swelling')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							skins.put(obj)

						}

						if (physicalExam.optString('field', '') == 'decreasedmobility' && physicalExam.optString('value', '')) {
							def obj = new JSONObject()

							obj.put('Id1', 7)
							obj.put('Id', '6')
							obj.put('Description', 'Decreased mobility')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							skins.put(obj)

						}

						if (physicalExam.optString('field', '') == 'palenailbeds' && physicalExam.optString('value', '')) {
							def obj = new JSONObject()

							obj.put('Id1', 8)
							obj.put('Id', '7')
							obj.put('Description', 'Pale nailbeds')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							skins.put(obj)

						}

						if (physicalExam.optString('field', '') == 'poorskinturgor' && physicalExam.optString('value', '')) {
							def obj = new JSONObject()

							obj.put('Id1', 9)
							obj.put('Id', '8')
							obj.put('Description', 'Poor skin turgor')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							skins.put(obj)

						}

						if (physicalExam.optString('field', '') == 'rashespetechiae' && physicalExam.optString('value', '')) {
							def obj = new JSONObject()

							obj.put('Id1', 10)
							obj.put('Id', '9')
							obj.put('Description', 'Rashes/Petechiae')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							skins.put(obj)

						}

						if (physicalExam.optString('field', '') == 'weakpulses' && physicalExam.optString('value', '')) {
							def obj = new JSONObject()

							obj.put('Id1', 2)
							obj.put('Id', '10')
							obj.put('Description', 'Weak pulses')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							skins.put(obj)

						}

						if (physicalExam.optString('field', '') == 'others5' && physicalExam.optString('value', '')) {
							def obj = new JSONObject()

							obj.put('Id1', 11)
							obj.put('Id', '99')
							obj.put('Description', 'Others')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							skins.put(obj)
							skinOtehrs = physicalExam.optString('value', '')
						}

						//--NUERO

						if (physicalExam.optString('field', '') == 'essentiallynormal7' && physicalExam.optString('value', '')) {
							def obj = new JSONObject()

							obj.put('Id1', 10)
							obj.put('Id', '6')
							obj.put('Description', 'Essentially normal')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							nueros.put(obj)

						}

						if (physicalExam.optString('field', '') == 'abnormalgait' && physicalExam.optString('value', '')) {
							def obj = new JSONObject()

							obj.put('Id1', 11)
							obj.put('Id', '7')
							obj.put('Description', 'Abnormal gait')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							nueros.put(obj)

						}

						if (physicalExam.optString('field', '') == 'abnormalpositionsense' && physicalExam.optString('value', '')) {
							def obj = new JSONObject()

							obj.put('Id1', 12)
							obj.put('Id', '8')
							obj.put('Description', 'Abnormal position sense')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							nueros.put(obj)

						}

//                    if(physicalExam.optString('field', '') == 'abnormaldecreasedsense' && physicalExam.optString('value', '')){
//                        def obj = new JSONObject()
//
//                        obj.put('Id1', 2)
//                        obj.put('Id', '10')
//                        obj.put('Description', 'Weak')
//                        obj.put('AddedBy', '')
//                        obj.put('DateAdded', '')
//                        obj.put('UpdatedBy','')
//                        obj.put('DateUpdated','')
//
//                        nueros.put(obj)
//
//                    }

						if (physicalExam.optString('field', '') == 'abnormalreflex' && physicalExam.optString('value', '')) {
							def obj = new JSONObject()

							obj.put('Id1', 2)
							obj.put('Id', '10')
							obj.put('Description', 'Abnormal reflex(es)')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							nueros.put(obj)

						}

						if (physicalExam.optString('field', '') == 'pooralteredmemory' && physicalExam.optString('value', '')) {
							def obj = new JSONObject()

							obj.put('Id1', 3)
							obj.put('Id', '11')
							obj.put('Description', 'Poor/altered memory')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							nueros.put(obj)

						}

						if (physicalExam.optString('field', '') == 'poormuscletone' && physicalExam.optString('value', '')) {
							def obj = new JSONObject()

							obj.put('Id1', 4)
							obj.put('Id', '12')
							obj.put('Description', 'Poor muscle tone/strength')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							nueros.put(obj)

						}

						if (physicalExam.optString('field', '') == 'poorcoordination' && physicalExam.optString('value', '')) {
							def obj = new JSONObject()

							obj.put('Id1', 5)
							obj.put('Id', '13')
							obj.put('Description', 'Poor coordination')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							nueros.put(obj)

						}

						if (physicalExam.optString('field', '') == 'others6' && physicalExam.optString('value', '')) {
							def obj = new JSONObject()

							obj.put('Id1', 14)
							obj.put('Id', '99')
							obj.put('Description', 'Others')
							obj.put('AddedBy', '')
							obj.put('DateAdded', '')
							obj.put('UpdatedBy', '')
							obj.put('DateUpdated', '')

							nueros.put(obj)
							nueroOthers = physicalExam.optString('value', '')
						}

					}
				}
			}

			if (symptoms.length() == 0) {
				throw new IllegalArgumentException("Pertinent findings Symptoms is required")

			}

			if (heents.length() == 0) {
				throw new IllegalArgumentException("Pertinent findings HEENT is required")

			}

			if (chests.length() == 0) {
				throw new IllegalArgumentException("Pertinent findings CHEST is required")

			}

			if (cvs.length() == 0) {
				throw new IllegalArgumentException("Pertinent findings CVS is required")

			}

			if (abdomen.length() == 0) {
				throw new IllegalArgumentException("Pertinent findings ABDOMEN is required")

			}

			if (guie.length() == 0) {
				throw new IllegalArgumentException("Pertinent findings GUIE is required")

			}

			if (skins.length() == 0) {
				throw new IllegalArgumentException("Pertinent findings SKINS is required")

			}

			if (nueros.length() == 0) {
				throw new IllegalArgumentException("Pertinent findings NEUROS is required")
			}

			form.add(new BasicNameValuePair('pChiefComplaint', aCase.chiefComplaint ?: ""))
			form.add(new BasicNameValuePair('pIllnessHistory', aCase.historyPresentIllness ?: ""))
			form.add(new BasicNameValuePair('pSpecificDesc', aCase.pastMedicalHistory ?: ""))
			form.add(new BasicNameValuePair('pLastMensPeriod', ""))
			form.add(new BasicNameValuePair('pPregCnt', (aCase.obgynHistory.gravida ?: 0).toString() ?: ""))
			form.add(new BasicNameValuePair('pDeliveryCnt', (aCase.obgynHistory.parturition ?: 0).toString() ?: ""))
			form.add(new BasicNameValuePair('pOtherComplaint', otherSymptoms ?: ""))
			form.add(new BasicNameValuePair('pGenSurveyRem', ""))
			form.add(new BasicNameValuePair('pSystolic', vitalSignService.getLatest(caseId).systolic ?: ""))
			form.add(new BasicNameValuePair('pDiastolic', vitalSignService.getLatest(caseId).diastolic ?: ""))
			form.add(new BasicNameValuePair('pHr', vitalSignService.getLatest(caseId).pulseRate ?: ""))
			form.add(new BasicNameValuePair('pRr', vitalSignService.getLatest(caseId).respiratoryRate ?: ""))
			form.add(new BasicNameValuePair('pTemp', vitalSignService.getLatest(caseId).temperature ?: ""))
			form.add(new BasicNameValuePair('Symptoms', symptoms.toString()))
			form.add(new BasicNameValuePair('Heent', heents.toString()))
			form.add(new BasicNameValuePair('Chest', chests.toString()))
			form.add(new BasicNameValuePair('Cvs', cvs.toString()))
			form.add(new BasicNameValuePair('Abdomen', abdomen.toString()))
			form.add(new BasicNameValuePair('GuIe', guie.toString()))
			form.add(new BasicNameValuePair('Skin', skins.toString()))
			form.add(new BasicNameValuePair('Neuro', nueros.toString()))
			form.add(new BasicNameValuePair('pHeentRem', heentOther))
			form.add(new BasicNameValuePair('pHeartRem', cvsOthers))
			form.add(new BasicNameValuePair('pAbdomenRem', abdomenOthers))
			form.add(new BasicNameValuePair('pGuRem', guieOthers))
			form.add(new BasicNameValuePair('pSkinRem', skinOtehrs))
			form.add(new BasicNameValuePair('pNeuroRem', nueroOthers))
			form.add(new BasicNameValuePair('pChestRem', chestOther))
			form.add(new BasicNameValuePair('pPainSite', aCase.painScore ?: ""))

			def object = comlogikRefId(aCase.caseNo)

			if (object) {
				def count = object['total'] as String

				if (StringUtils.equals(count, "1")) {
					def rows = object['rows'] as ArrayList
					def refno = rows[0]['refno'] as String

					form.add(new BasicNameValuePair('refno', refno ?: ""))
				}
			} else {
				throw new IllegalAccessException("No reference # found. Please Add this patient to Comlogik.")
			}

		} else {
			throw new IllegalAccessException("No case found!")
		}

//        form.add(new BasicNameValuePair('pChiefComplaint', "COUGH AND DYSPNEASSSSS"))
//        form.add(new BasicNameValuePair('pIllnessHistory', "ASDF"))
//        form.add(new BasicNameValuePair('pSpecificDesc', "ASDF"))
//        form.add(new BasicNameValuePair('pLastMensPeriod', ""))
//        form.add(new BasicNameValuePair('pPregCnt', ""))
//        form.add(new BasicNameValuePair('pDeliveryCnt', ""))
//        form.add(new BasicNameValuePair('pOtherComplaint', "ASDF"))
//        form.add(new BasicNameValuePair('pGenSurveyId', "1"))
//        form.add(new BasicNameValuePair('pGenSurveyRem', ""))
//        form.add(new BasicNameValuePair('pSystolic', "123"))
//        form.add(new BasicNameValuePair('pDiastolic', "123"))
//        form.add(new BasicNameValuePair('pHr', "123"))
//        form.add(new BasicNameValuePair('pRr', "123"))
//        form.add(new BasicNameValuePair('pTemp', "11.0"))
//        form.add(new BasicNameValuePair('Symptoms', "[{\"Id\":40,\"SymptomsId\":\"2\",\"SymptomsDescription\":\"ABDOMINAL CRAMP/PAIN\",\"Active\":true}]"))
//        form.add(new BasicNameValuePair('Heent', "[{\"Id1\":3,\"Id\":\"13\",\"Description\":\"Cervical lympadenopathy\",\"Status\":true,\"AddedBy\":null,\"DateAdded\":null,\"UpdatedBy\":null,\"DateUpdated\":null}]"))
//        form.add(new BasicNameValuePair('Chest', "[{\"Id1\":2,\"Id\":\"10\",\"Description\":\"Lumps over breast(s)\",\"Status\":true,\"AddedBy\":null,\"DateAdded\":null,\"UpdatedBy\":null,\"DateUpdated\":null}]"))
//        form.add(new BasicNameValuePair('Cvs', "[{\"Id1\":6,\"Id\":\"6\",\"Description\":\"Displaced apex beat\",\"Status\":true,\"AddedBy\":null,\"DateAdded\":null,\"UpdatedBy\":null,\"DateUpdated\":null}]"))
//        form.add(new BasicNameValuePair('Abdomen', "[{\"Id1\":12,\"Id\":\"8\",\"Description\":\"Abdominal rigidity\",\"Status\":true,\"AddedBy\":null,\"DateAdded\":null,\"UpdatedBy\":null,\"DateUpdated\":null}]"))
//        form.add(new BasicNameValuePair('GuIe',"[{\"Id1\":4,\"Id\":\"4\",\"Description\":\"Presence of abnormal discharge\",\"Status\":true,\"AddedBy\":null,\"DateAdded\":null,\"UpdatedBy\":null,\"DateUpdated\":null}]"))
//        form.add(new BasicNameValuePair('Skin', "[{\"Id1\":1,\"Id\":\"1\",\"Description\":\"Essentially normal\",\"Status\":true,\"AddedBy\":null,\"DateAdded\":null,\"UpdatedBy\":null,\"DateUpdated\":null}]"))
//        form.add(new BasicNameValuePair('Neuro', "[{\"Id1\":11,\"Id\":\"7\",\"Description\":\"Abnormal gait\",\"Status\":true,\"AddedBy\":null,\"DateAdded\":null,\"UpdatedBy\":null,\"DateUpdated\":null}]"))
//        form.add(new BasicNameValuePair('pHeentRem', ""))
//        form.add(new BasicNameValuePair('pHeartRem', ""))
//        form.add(new BasicNameValuePair('pAbdomenRem', ""))
//        form.add(new BasicNameValuePair('pGuRem', ""))
//        form.add(new BasicNameValuePair('pSkinRem', ""))
//        form.add(new BasicNameValuePair('pNeuroRem', ""))
//        form.add(new BasicNameValuePair('pChestRem', ""))
//        form.add(new BasicNameValuePair('pPainSite', "ASDF"))
//        form.add(new BasicNameValuePair('refno', "e1e313b8-2331-4dac-b177-c15f6b448d7c"))

		if (comlogikSetting) {
//            def executor = Executor.newInstance()
//                    .auth(new HttpHost(comlogikSetting[0].host), comlogikSetting[0].login, comlogikSetting[0].password)
//
//            def response = executor.execute(Request.Post(comlogikSetting[0].host +"/api/savecf4")
//                    .bodyForm(form)).returnContent().asString()
//
//            return response
//
//            def provider = new BasicCredentialsProvider();
//            def credentials = new UsernamePasswordCredentials(comlogikSetting[0].login, comlogikSetting[0].password);
//            provider.setCredentials(AuthScope.ANY, credentials);
//
//            HttpClient client = HttpClients.createDefault()
//
//            def httpPost =  new HttpPost("http://172.16.12.25/ClaimsAssureTest/api/savecf4")
//            httpPost.addHeader('Content-Type', 'application/x-www-form-urlencoded')
//            httpPost.addHeader('Accept', '*/*')
//            httpPost.addHeader('Accept-Encoding', 'gzip, deflate, br')
//            httpPost.addHeader('Connection', 'keep-alive')
//            httpPost.setEntity(new UrlEncodedFormEntity(""))
//            HttpResponse response = client.execute(httpPost)
//            HttpEntity entity = response.getEntity()
//
//            return entity.toString()

			def client = new OkHttpClient()

			def queryString = contentBuilder(form)
			//def queryString = "pChiefComplaint=COUGH AND DYSPNEASSSSS&pIllnessHistory=ASDF&pSpecificDesc=ASDF&pLastMensPeriod=&pPregCnt=&pDeliveryCnt=&pOtherComplaint=ASDF&pGenSurveyId=1&pGenSurveyRem=&pSystolic=123&pDiastolic=123&pHr=123&pRr=123&pTemp=11.0&Symptoms=[{\\\"Id\\\":40,\\\"SymptomsId\\\":\\\"2\\\",\\\"SymptomsDescription\\\":\\\"ABDOMINAL CRAMP/PAIN\\\",\\\"Active\\\":true}]&Heent=[{\\\"Id1\\\":3,\\\"Id\\\":\\\"13\\\",\\\"Description\\\":\\\"Cervical lympadenopathy\\\",\\\"Status\\\":true,\\\"AddedBy\\\":null,\\\"DateAdded\\\":null,\\\"UpdatedBy\\\":null,\\\"DateUpdated\\\":null}]&Chest=[{\\\"Id1\\\":2,\\\"Id\\\":\\\"10\\\",\\\"Description\\\":\\\"Lumps over breast(s)\\\",\\\"Status\\\":true,\\\"AddedBy\\\":null,\\\"DateAdded\\\":null,\\\"UpdatedBy\\\":null,\\\"DateUpdated\\\":null}]&Cvs=[{\\\"Id1\\\":6,\\\"Id\\\":\\\"6\\\",\\\"Description\\\":\\\"Displaced apex beat\\\",\\\"Status\\\":true,\\\"AddedBy\\\":null,\\\"DateAdded\\\":null,\\\"UpdatedBy\\\":null,\\\"DateUpdated\\\":null}]&Abdomen=[{\\\"Id1\\\":12,\\\"Id\\\":\\\"8\\\",\\\"Description\\\":\\\"Abdominal rigidity\\\",\\\"Status\\\":true,\\\"AddedBy\\\":null,\\\"DateAdded\\\":null,\\\"UpdatedBy\\\":null,\\\"DateUpdated\\\":null}]&GuIe=[{\\\"Id1\\\":4,\\\"Id\\\":\\\"4\\\",\\\"Description\\\":\\\"Presence of abnormal discharge\\\",\\\"Status\\\":true,\\\"AddedBy\\\":null,\\\"DateAdded\\\":null,\\\"UpdatedBy\\\":null,\\\"DateUpdated\\\":null}]&Skin=[{\\\"Id1\\\":1,\\\"Id\\\":\\\"1\\\",\\\"Description\\\":\\\"Essentially normal\\\",\\\"Status\\\":true,\\\"AddedBy\\\":null,\\\"DateAdded\\\":null,\\\"UpdatedBy\\\":null,\\\"DateUpdated\\\":null}]&Neuro=[{\\\"Id1\\\":11,\\\"Id\\\":\\\"7\\\",\\\"Description\\\":\\\"Abnormal gait\\\",\\\"Status\\\":true,\\\"AddedBy\\\":null,\\\"DateAdded\\\":null,\\\"UpdatedBy\\\":null,\\\"DateUpdated\\\":null}]&pHeentRem=&pHeartRem=&pAbdomenRem=&pGuRem=&pSkinRem=&pNeuroRem=&pChestRem=&pPainSite=ASDF&refno=e1e313b8-2331-4dac-b177-c15f6b448d7c"

			def mediaType = MediaType.parse("application/x-www-form-urlencoded")
			def body = RequestBody.create(mediaType, queryString)
			def request = new com.squareup.okhttp.Request.Builder()
					.url(comlogikSetting[0].host + "/api/savecf4")
					.method("POST", body)
					.addHeader("Content-Type", "application/x-www-form-urlencoded")
					.build()
			def response = client.newCall(request).execute()
			return new JsonSlurper().parseText(response.body().string())
		} else {
			throw new IllegalArgumentException("ComLogik Error! Please contact your system administrator.")
		}
	}

	@GraphQLMutation(name = "saveconfinementdetailsv2", description = "save confinement information to comlogik")
	Object saveconfinementdetailsv2(@GraphQLArgument(name = "caseId") UUID caseId, @GraphQLArgument(name = 'credentials') String crendetials) {
		Case aCase = caseRepository.findById(caseId).get()
		def object = comlogikRefId(aCase.caseNo)

		DateTimeFormatter formatter =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())

		DateTimeFormatter dobFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

		DateTimeFormatter formatterWithTime =
				DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a").withZone(ZoneId.systemDefault())

		List<ComlogikSetting> comlogikSetting = comlogikSettingRepository.findAll()

		List<PatientPhilhealthData> philhealthData = patientPhilhealthDataRepository.findByCaseId(caseId).sort {
			a,b -> b.lastModifiedDate <=> a.lastModifiedDate
		}

		def form = new ArrayList<NameValuePair>(2)
		if (aCase != null) {

			if (aCase?.caseNo != null) {
				form.add(new BasicNameValuePair("caseno", aCase?.caseNo))
			} else {
				throw new IllegalArgumentException("Case no. is required!")

			}

			if (!philhealthData) {
				throw new IllegalArgumentException("No philhealth data found.")
			}

//            def loggedUser = SecurityUtils.currentLogin()
//
//            if(loggedUser!=null){
//                form.add(new BasicNameValuePair("UserName", loggedUser))
//
//            }else{
//                throw new IllegalArgumentException("Invalid user. Please check currently logged in user.")
//            }

			if (philhealthData[0].memberRelation != 'M') {
				if (aCase?.patient?.lastName != null || philhealthData[0].memberLastName != null) {
					form.add(new BasicNameValuePair("pMemberLastName", philhealthData[0].memberLastName ?: ""))
					form.add(new BasicNameValuePair("pPatientLastName", aCase.patient.lastName ?: ""))

				} else {
					throw new IllegalArgumentException("Member's last name is required.")
				}

				if (aCase?.patient?.firstName != null || philhealthData[0].memberFirstName != null) {
					form.add(new BasicNameValuePair("pPatientFirstName", aCase.patient.firstName ?: ""))

					form.add(new BasicNameValuePair("pMemberFirstName", philhealthData[0].memberFirstName ?: ""))
				} else {
					throw new IllegalArgumentException("Member's first name is required.")
				}

				if (aCase?.patient?.middleName != null || philhealthData[0].memberMiddleName != null) {
					form.add(new BasicNameValuePair("pPatientMiddleName", aCase.patient.middleName ?: ""))
					form.add(new BasicNameValuePair("pMemberMiddleName", philhealthData[0].memberMiddleName ?: ""))
				} else {
					throw new IllegalArgumentException("Member's middle name is required.")
				}

				if (philhealthData[0]?.memberSuffix && philhealthData[0].memberSuffix.length() > 5) {
					throw new IllegalArgumentException("Member's Suffix reached character limit should only consist of 5 characters or less.")

				} else {
					form.add(new BasicNameValuePair("pMemberSuffix", philhealthData[0].memberSuffix ?: ""))

				}

				if (aCase.patient?.nameSuffix && aCase.patient.nameSuffix.length() > 5) {
					throw new IllegalArgumentException("Patient's Suffix reached character limit should only consist of 5 characters or less.")

				} else {
					form.add(new BasicNameValuePair("pPatientSuffix", aCase.patient.nameSuffix ?: ""))

				}

				if (aCase?.patient?.dob != null || philhealthData[0].memberDob != null) {
					form.add(new BasicNameValuePair("pMemberBirthDate", formatter.format(philhealthData[0].memberDob)))
					form.add(new BasicNameValuePair("pPatientBirthDate", dobFormat.format(aCase.patient.dob)))

				} else {
					throw new IllegalArgumentException("Member's date of birth is required.")
				}

				form.add(new BasicNameValuePair("pMailingAddress", aCase?.patient?.fullAddress))

				if (aCase?.patient?.gender != null || philhealthData[0].memberGender != null) {
					form.add(new BasicNameValuePair("pMemberSex", philhealthData[0].memberGender?.substring(0, 1)?.toUpperCase()?.trim()))
					form.add(new BasicNameValuePair("pPatientSex", aCase?.patient?.gender?.substring(0, 1)?.toUpperCase()?.trim()))

				} else {
					throw new IllegalArgumentException("Member's gender is required!")
				}

				form.add(new BasicNameValuePair("pPatientIs", philhealthData[0].memberRelation ?: ""))

				form.add(new BasicNameValuePair("pPatientPIN", aCase.patient.philHealthId ?: ""))
				form.add(new BasicNameValuePair("pMemberPIN", philhealthData[0].memberPin ?: ""))

			} else {
				if (aCase?.patient?.lastName != null) {
					form.add(new BasicNameValuePair("pMemberLastName", philhealthData[0].memberLastName ?: ""))
					form.add(new BasicNameValuePair("pPatientLastName", philhealthData[0].memberLastName ?: ""))

				} else {
					throw new IllegalArgumentException("Member's last name is required.")
				}

				if (aCase?.patient?.firstName != null) {
					form.add(new BasicNameValuePair("pPatientFirstName", philhealthData[0].memberFirstName ?: ""))
					form.add(new BasicNameValuePair("pMemberFirstName", philhealthData[0].memberFirstName ?: ""))
				} else {
					throw new IllegalArgumentException("Member's first name is required.")
				}

				if (aCase?.patient?.middleName != null) {
					form.add(new BasicNameValuePair("pPatientMiddleName", philhealthData[0].memberMiddleName ?: ""))
					form.add(new BasicNameValuePair("pMemberMiddleName", philhealthData[0].memberMiddleName ?: ""))
				} else {
					throw new IllegalArgumentException("Member's middle name is required.")
				}

				if (philhealthData[0]?.memberSuffix && philhealthData[0].memberSuffix.length() > 5) {
					throw new IllegalArgumentException("Member's Suffix reached character limit should only consist of 5 characters or less.")

				} else {
					form.add(new BasicNameValuePair("pPatientSuffix", philhealthData[0].memberSuffix ?: ""))
					form.add(new BasicNameValuePair("pMemberSuffix", philhealthData[0].memberSuffix ?: ""))

				}

				if (aCase?.patient?.dob != null) {
					form.add(new BasicNameValuePair("pMemberBirthDate", formatter.format(philhealthData[0].memberDob)))
					form.add(new BasicNameValuePair("pPatientBirthDate", formatter.format(philhealthData[0].memberDob)))

				} else {
					throw new IllegalArgumentException("Member's date of birth is required.")
				}

				form.add(new BasicNameValuePair("pMailingAddress", aCase?.patient?.fullAddress))

				if (aCase?.patient?.gender != null) {
					form.add(new BasicNameValuePair("pMemberSex", philhealthData[0].memberGender?.substring(0, 1)?.toUpperCase()?.trim()))
					form.add(new BasicNameValuePair("pPatientSex", philhealthData[0].memberGender?.substring(0, 1)?.toUpperCase()?.trim()))

				} else {
					throw new IllegalArgumentException("Member's gender is required!")
				}

				form.add(new BasicNameValuePair("pPatientIs", philhealthData[0].memberRelation ?: ""))

				form.add(new BasicNameValuePair("pPatientPIN", philhealthData[0].memberPin ?: ""))
				form.add(new BasicNameValuePair("pMemberPIN", philhealthData[0].memberPin ?: ""))

			}

			if (philhealthData[0].memberZipCode != null) {
				form.add(new BasicNameValuePair("pZipCode", philhealthData[0].memberZipCode ?: ""))
			} else {
				throw new IllegalArgumentException("Zipcode is requried.")
			}

			if (aCase.patient.civilStatus) {
				if (aCase.patient.civilStatus.contains("NEW BORN") || aCase.patient.civilStatus.contains("CHILD")) {
					form.add(new BasicNameValuePair("pCivilStatus", "S"))
				} else {
					form.add(new BasicNameValuePair("pCivilStatus", aCase.patient.civilStatus[0] ?: ""))
				}
			} else {
				if (philhealthData[0].memberCivilStatus) {
					if (philhealthData[0].memberCivilStatus.contains("NEW BORN") || philhealthData[0].memberCivilStatus.contains("CHILD")) {
						form.add(new BasicNameValuePair("pCivilStatus", "S"))
					} else {
						form.add(new BasicNameValuePair("pCivilStatus", philhealthData[0].memberCivilStatus[0] ?: ""))
					}
				} else {
					throw new IllegalArgumentException("Invalid Civil Status")
				}
			}

			form.add(new BasicNameValuePair("pMemberShipType", philhealthData[0].memberType ?: ""))

			if (aCase != null) {
				if (aCase.chiefComplaint != null) {
					form.add(new BasicNameValuePair("pChiefComplaint", aCase.chiefComplaint ?: ""))
				} else {
					throw new IllegalArgumentException("Chief Complaints is required.")
				}

			} else {
				throw new IllegalArgumentException("Chief Complaints is required.")
			}

			if (aCase.admissionDatetime != null) {
				form.add(new BasicNameValuePair("pAdmissionDate", formatterWithTime.format(aCase.admissionDatetime)))
			} else {
				form.add(new BasicNameValuePair("pAdmissionDate", ""))
			}

			if (aCase.dischargedDatetime) {
				form.add(new BasicNameValuePair("pDischargeDate", formatterWithTime.format(aCase.dischargedDatetime)))
			} else {
				if (aCase.admissionDatetime != null) {
					form.add(new BasicNameValuePair("pDischargeDate", formatterWithTime.format(aCase.admissionDatetime)))
				} else {
					throw new IllegalArgumentException("No admission date.")
				}

			}


				def count = object['total'] as Integer

				if(aCase.comlogikRefNo){
					form.add(new BasicNameValuePair('referenceno', aCase.comlogikRefNo ?: ""))
				} else if (count == 1) {
					if (object) {
						def rows = object['rows'] as ArrayList
						def refno = rows[0]['refno'] as String
						form.add(new BasicNameValuePair('referenceno', refno ?: ""))
					}
				}else {
					form.add(new BasicNameValuePair('referenceno', ""))
				}



			form.add(new BasicNameValuePair("pPEN", aCase.companyPen ?: ""))
			form.add(new BasicNameValuePair("pEmailAddress", ""))
			form.add(new BasicNameValuePair("pEmployerName", aCase.occupation ?: ""))
			form.add(new BasicNameValuePair("pRVS", ""))
			form.add(new BasicNameValuePair("pTotalAmountActual", ""))
			form.add(new BasicNameValuePair("pTotalAmountClaimed", ""))
			form.add(new BasicNameValuePair("pIsFinal", "0"))

			form.add(new BasicNameValuePair("docneeded", ""))
			form.add(new BasicNameValuePair("isok", ""))
			form.add(new BasicNameValuePair("trackno", ""))
			form.add(new BasicNameValuePair("with3over6", ""))
			form.add(new BasicNameValuePair("with9over12", ""))
			form.add(new BasicNameValuePair("remainingdays", ""))
			form.add(new BasicNameValuePair("asof", ""))
			form.add(new BasicNameValuePair("reason", ""))
		} else {
			throw new IllegalArgumentException("No philhealth data specified.")
		}

		if (comlogikSetting) {

//            def httpclient = HttpClients.custom().build()
//            def post = new HttpPost(comlogikSetting[0].host  + "/api/dataaccess/saveeligibility")
//
//            def auth = comlogikSetting[0].login + ":" + comlogikSetting[0].password
//            def encodedAuth = auth.bytes.encodeBase64().toString()
//            def authHeader = "Basic " + encodedAuth
//            post.addHeader("content-type", "application/x-www-form-urlencoded");
//            post.setHeader(HttpHeaders.AUTHORIZATION, authHeader)
//            post.entity = new StringEntity(form.toString())
//
//
//            def response = httpclient.execute(post)
//            def credentials = new UsernamePasswordCredentials(comlogikSetting[0].login, comlogikSetting[0].password);
//            def executor = Executor.newInstance().auth(credentials)
//
//            def response = executor.execute(Request.Post(comlogikSetting[0].host +"/api/saveconfinementdetails")
//                    .bodyString(form.toString(), ContentType.APPLICATION_JSON)).returnContent().asString()

			OkHttpClient client = new OkHttpClient()

			MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded")
			String requestBody = contentBuilder(form) ?: ""
			RequestBody body = RequestBody.create(mediaType, requestBody)

			def request = new com.squareup.okhttp.Request.Builder()
					.url(comlogikSetting[0].host + "/api/dataaccess/saveeligibility")
					.method("POST", body)
					.addHeader("Content-Type", "application/x-www-form-urlencoded")
					.addHeader("Cookie", crendetials)
					.build()
			Response response = client.newCall(request).execute()

			Object respObj = new JsonSlurper().parseText(response.body().string())
			String referenceno = respObj['referenceno']?:""

			if(referenceno){
				if(!aCase.comlogikRefNo){
					aCase.comlogikRefNo = referenceno
					caseRepository.save(aCase)
				}
			}

			return respObj

		} else {
			throw new IllegalArgumentException("ComLogik Error! Please contact your system administrator.")
		}

	}

	@GraphQLMutation(name = 'login')
	String comlogikAuthHeaders(@GraphQLArgument(name = 'credentials') Map<String, String> credentials){
		List<ComlogikSetting> comlogikSetting = comlogikSettingRepository.findAll()
		def form = new ArrayList<NameValuePair>(2)

		form.add(new BasicNameValuePair('UserName', credentials['username']?:""))
		form.add(new BasicNameValuePair('Password', credentials['password']?:""))

		if (comlogikSetting) {
//            def httpclient = HttpClients.custom().build()
//            def post = new HttpPost(comlogikSetting[0].host  + "/api/dataaccess/saveeligibility")
//
//            def auth = comlogikSetting[0].login + ":" + comlogikSetting[0].password
//            def encodedAuth = auth.bytes.encodeBase64().toString()
//            def authHeader = "Basic " + encodedAuth
//            post.addHeader("content-type", "application/x-www-form-urlencoded");
//            post.setHeader(HttpHeaders.AUTHORIZATION, authHeader)
//            post.entity = new StringEntity(form.toString())
//
//
//            def response = httpclient.execute(post)
//            def credentials = new UsernamePasswordCredentials(comlogikSetting[0].login, comlogikSetting[0].password);
//            def executor = Executor.newInstance().auth(credentials)
//
//            def response = executor.execute(Request.Post(comlogikSetting[0].host +"/api/saveconfinementdetails")
//                    .bodyString(form.toString(), ContentType.APPLICATION_JSON)).returnContent().asString()

			OkHttpClient client = new OkHttpClient()

			MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded")
			RequestBody body = RequestBody.create(mediaType, contentBuilder(form) ?: "")
			def request = new com.squareup.okhttp.Request.Builder()
					.url(comlogikSetting[0].host + "/Users/LogIn")
					.method("POST", body)
					.addHeader("Content-Type", "application/x-www-form-urlencoded")
					.build()
			Response response = client.newCall(request).execute()

			if(response.successful){
				List<String> Cookielist = response.headers().values("Set-Cookie")
				String cookies = ""
				Cookielist.each {
					cookies += it.split(';')[0] + "; "
				}
				return cookies
			}else  {
				throw new IllegalAccessException(response.body().string())
			}


		} else {
			throw new IllegalArgumentException("ComLogik Error! Please contact your system administrator.")
		}
	}

	@GraphQLMutation(name = "set_phic_drug_code")
	GraphQLRetVal<Generic> setPhicCode(@GraphQLArgument(name = "id") UUID id, @GraphQLArgument(name="fields") Map<String, Object> fields){
		def item = itemRepository.findById(id).get()
		def generic = genericRepository.findById(item.item_generics.id).get()

		generic.phicCode = fields['drugCode']?:""
		generic.phicDescription = fields['description']?:""

		genericRepository.save(generic)

		return  new GraphQLRetVal<Generic>(generic, true, "Successfully saved!")
	}

	@GraphQLMutation(name = "delete_all_courseinthewards")
	GraphQLRetVal<Object> deleteAllCourseInTheWards(@GraphQLArgument(name = "caseId") UUID caseId, @GraphQLArgument(name = 'credentials') String crendetials){
		Case aCase = caseRepository.findById(caseId).get()
		def object = comlogikRefId(aCase.caseNo)

		DateTimeFormatter formatter =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())

		DateTimeFormatter dobFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

		DateTimeFormatter formatterWithTime =
				DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a").withZone(ZoneId.systemDefault())

		List<ComlogikSetting> comlogikSetting = comlogikSettingRepository.findAll()

		List<PatientPhilhealthData> philhealthData = patientPhilhealthDataRepository.findByCaseId(caseId).sort {
			a,b -> b.lastModifiedDate <=> a.lastModifiedDate
		}
		if (comlogikSetting) {

//            def httpclient = HttpClients.custom().build()
//            def post = new HttpPost(comlogikSetting[0].host  + "/api/dataaccess/saveeligibility")
//
//            def auth = comlogikSetting[0].login + ":" + comlogikSetting[0].password
//            def encodedAuth = auth.bytes.encodeBase64().toString()
//            def authHeader = "Basic " + encodedAuth
//            post.addHeader("content-type", "application/x-www-form-urlencoded");
//            post.setHeader(HttpHeaders.AUTHORIZATION, authHeader)
//            post.entity = new StringEntity(form.toString())
//
//
//            def response = httpclient.execute(post)
//            def credentials = new UsernamePasswordCredentials(comlogikSetting[0].login, comlogikSetting[0].password);
//            def executor = Executor.newInstance().auth(credentials)
//
//            def response = executor.execute(Request.Post(comlogikSetting[0].host +"/api/saveconfinementdetails")
//                    .bodyString(form.toString(), ContentType.APPLICATION_JSON)).returnContent().asString()

			def count = object['total'] as Integer
			def refno_param = ""

			if(aCase.comlogikRefNo){
				aCase.comlogikRefNo ?: ""
			} else if (count == 1) {
				if (object) {
					def rows = object['rows'] as ArrayList
					def refno = rows[0]['refno'] as String
					refno_param = refno
				}
			}

			OkHttpClient client = new OkHttpClient()
			HttpUrl.Builder httpBuilder = HttpUrl.parse(comlogikSetting[0].host + "/api/getcourseintheward").newBuilder()
			httpBuilder.addQueryParameter("refno", refno_param)
			def request = new com.squareup.okhttp.Request.Builder()
					.url(httpBuilder.build())
					.method("GET", null)
					.addHeader("Content-Type", "application/x-www-form-urlencoded")
					.addHeader("Cookie", crendetials)
					.build()
			Response response = client.newCall(request).execute()

			Object respObj = new Object()

			if(response.successful){
				 respObj = new JsonSlurper().parseText(response.body().string())

				def Ids = respObj as ArrayList<Object>
				Ids.each {
					def form = new ArrayList<NameValuePair>(2)
					def Id = it['Id'].toString()
					form.add(new BasicNameValuePair('Id', Id?:""))
					MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded")
					RequestBody body = RequestBody.create(mediaType, contentBuilder(form) ?: "")

					def requestDeleted = new com.squareup.okhttp.Request.Builder()
							.url(comlogikSetting[0].host + "/api/deletecourseinthewards")
							.method("POST", body)
							.addHeader("Content-Type", "application/x-www-form-urlencoded")
							.addHeader("Cookie", crendetials?:"")
							.build()
					Response requestDeletedResp = client.newCall(requestDeleted).execute()

					if(!requestDeletedResp.successful){
						throw new IllegalArgumentException("ComLogik Error! Please contact your system administrator.")
					}
				}
			}else{
				throw new IllegalArgumentException("ComLogik Error! Please contact your system administrator.")

			}
//			String referenceno = respObj['referenceno']?:""
//
//			if(referenceno){
//				if(!aCase.comlogikRefNo){
//					aCase.comlogikRefNo = referenceno
//					caseRepository.save(aCase)
//				}
//			}

			return new GraphQLRetVal(respObj, true, "Successfully deleted!")


		} else {
			throw new IllegalArgumentException("ComLogik Error! Please contact your system administrator.")
		}

	}


	String contentBuilder(ArrayList<NameValuePair> params) {
		String contentBuilder = ""

		params.eachWithIndex { NameValuePair entry, int i ->
			if (i == params.size() - 1) {
				contentBuilder += entry.name + "=" + entry.value
			} else {
				contentBuilder += entry.name + "=" + entry.value + "&"
			}
		}

		return contentBuilder
	}

}
