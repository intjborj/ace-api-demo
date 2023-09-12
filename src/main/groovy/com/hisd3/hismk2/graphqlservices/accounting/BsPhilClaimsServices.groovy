package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.accounting.AccountReceivable
import com.hisd3.hismk2.domain.accounting.AccountReceivableItems
import com.hisd3.hismk2.domain.accounting.BillingSchedule
import com.hisd3.hismk2.domain.accounting.BillingScheduleItems
import com.hisd3.hismk2.domain.accounting.BsPhilClaims
import com.hisd3.hismk2.domain.accounting.BsPhilClaimsItems
import com.hisd3.hismk2.domain.billing.BillingItem
import com.hisd3.hismk2.domain.hospital_config.ComlogikSetting
import com.hisd3.hismk2.domain.hrm.Payroll
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.billing.BillingItemServices
import com.hisd3.hismk2.graphqlservices.billing.CompanyAccountServices
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.accounting.BillingScheduleItemsRepository
import com.hisd3.hismk2.repository.accounting.BillingScheduleRepository
import com.hisd3.hismk2.repository.accounting.BsPhilClaimsRepository
import com.hisd3.hismk2.repository.hospital_config.ComlogikSettingRepository
import com.hisd3.hismk2.services.EntityObjectMapperService
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import groovy.json.JsonSlurper
import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.apache.http.HttpHost
import org.apache.http.client.fluent.Executor
import org.apache.http.client.fluent.Request
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.xmlsoap.schemas.soap.encoding.Date

import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Objects

import javax.transaction.Transactional

@Canonical
class BsPhilClaimsTemp {
	UUID id
	UUID billingSchedule
	String caseNo
	String claimNumber
	String claimSeriesLhio
	BigDecimal claimAmount
	Date voucherDate
	String voucherNo
	String processStage
	String status
}


@Canonical
class UnClaimedEClaims {
	UUID billingId
	String claimNumber
	UUID id
}

@Component
@GraphQLApi
class BsPhilClaimsServices extends AbstractDaoService<BsPhilClaims> {

	BsPhilClaimsServices() {
		super(BsPhilClaims.class)
	}

	@Autowired
	EntityObjectMapperService entityObjectMapperService

	@Autowired
	BsPhilClaimsItemsServices bsPhilClaimsItemsServices

	@Autowired
	AccountReceivableItemsServices accountReceivableItemsServices

	@Autowired
	AccountReceivableServices accountReceivableServices

	@Autowired
	BillingItemServices billingItemServices

	@Autowired
	BillingScheduleItemsServices billingScheduleItemsServices

	@Autowired
	ComlogikSettingRepository comlogikSettingRepository

	@Autowired
	GeneratorService generatorService


	@GraphQLQuery(name = "getAllBsPhilClaimsBy")
	Page<BsPhilClaims> getAllBsPhilClaims(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "billingSchedule") UUID billingSchedule,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		getPageable(
				"""
              	Select c from BsPhilClaims c  where c.billingSchedule.id = :billingSchedule and (lower(c.billing.patient.fullName) like lower(concat('%',:filter,'%')) )  order by c.caseNo
				""",
				"""
			 	Select count(c) from BsPhilClaims c  where c.billingSchedule.id = :billingSchedule and (lower(c.billing.patient.fullName) like lower(concat('%',:filter,'%')) )
				""",
				page,
				size,
				[
						billingSchedule: billingSchedule,
						filter         : filter,
				]
		)

	}

	@GraphQLQuery(name = "getEClaimsVoucher")
	Object getEClaimsVoucher(@GraphQLArgument(name = "caseNo") String caseNo) {

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
			def response = executor.execute(Request.Get(comlogikSetting[0].host + "/api/dataaccess/submittedclaims?lastname=&firstname=&middlename=&caseno=" + caseNo + "&bydate=false&datefrom=02%2F24%2F2021&dateto=02%2F24%2F2021&page=1&rows=20"))

			return new JsonSlurper().parseText(response.returnContent().asString())
		} else {
			throw new IllegalArgumentException("ComLogik Error! Please contact your system administrator.")
		}
	}

	@GraphQLQuery(name = "getBsPhilClaimsByCaseNo")
	BsPhilClaims getBsPhilClaimsByCaseNo(@GraphQLArgument(name = "caseNo") String caseNo,
										 @GraphQLArgument(name = "billingScheduleId") UUID billingScheduleId

	) {
		createQuery("""
                    select b from BsPhilClaims b where b.billing.patientCase.caseNo = :caseNo and b.billingSchedule.id = :billingScheduleId
            """,
				[
						caseNo: caseNo,
						billingScheduleId: billingScheduleId,
				] as Map<String, Object>).singleResult
	}

	@GraphQLQuery(name = "checkClaimsExist")
	BsPhilClaims checkClaimsExist(@GraphQLArgument(name = "claimSeriesLhio") String claimSeriesLhio
	) {

		List<BsPhilClaims> claims =  createQuery("""
                    select b from BsPhilClaims b where b.claimSeriesLhio = :claimSeriesLhio
            """,
				[
						claimSeriesLhio: claimSeriesLhio
				] as Map<String, Object>).setMaxResults(1).resultList
		return claims.size > 0 ? claims[0] : null

	}

	@GraphQLQuery(name = "getUnProcessClaims")
	List<BsPhilClaims> getUnProcessClaims() {
		createQuery("""
                    select b from BsPhilClaims b where b.status in ('IN PROCESS','RETURN','DENIED')
            """).resultList
	}

//	@Transactional
//	@GraphQLMutation
//	String updatePhilData(
//			@GraphQLArgument(name = "billScheduleId") UUID billScheduleId
//	) {
//		def billingSchedule = billingScheduleServices.findOne(billScheduleId)
//		billingSchedule.billingScheduleItems.each {
//			it->
//				if(!it.isVoided){
//					def claims = getEClaimsVoucher(it.billing.patientCase.caseNo)
//					if(claims["rows"]){
//						def existingClaims = bsPhilClaimsRepository.getClaimsPerSchedule(it.billing.patientCase.caseNo)
//						if(!existingClaims){
//							def newPhilClaims = new BsPhilClaims()
//							if(!Objects.isNull(claims["rows"][0]["pClaimNumber"])){
//								newPhilClaims.claimNumber = claims["rows"][0]["pClaimNumber"]
//							}
//							if(!Objects.isNull(claims["rows"][0]["pClaimSeriesLhio"])){
//								newPhilClaims.claimSeriesLhio = claims["rows"][0]["pClaimSeriesLhio"]
//							}
//							if(!Objects.isNull(claims["rows"][0]["pClaimAmount"])){
//								newPhilClaims.claimAmount = claims["rows"][0]["pClaimAmount"] ? Double.parseDouble(claims["rows"][0]["pClaimAmount"].toString()) : 0
//							}
//							if(!Objects.isNull(claims["rows"][0]["pVoucherNo"])){
//								newPhilClaims.voucherNo = claims["rows"][0]["pVoucherNo"]
//							}
//							if(!Objects.isNull(claims["rows"][0]["pProcessStage"])){
//								newPhilClaims.processStage = claims["rows"][0]["pProcessStage"]
//							}
//							if(!Objects.isNull(claims["rows"][0]["pStatus"])){
//								newPhilClaims.status = claims["rows"][0]["pStatus"]
//							}
//							if(!Objects.isNull(claims["rows"][0]["pVoucherDate"]) && (claims["rows"][0]["pVoucherDate"])){
//								SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
//								newPhilClaims.voucherDate = formatter.parse(claims["rows"][0]["pVoucherDate"].toString())
//							}
//
//							save(newPhilClaims)
//						}
//						else{
//							def updateClaims = findOne(existingClaims[0].id)
//							updateClaims.claimNumber = claims["rows"][0]["pClaimNumber"]
//							updateClaims.claimSeriesLhio = claims["rows"][0]["pClaimSeriesLhio"]
//							updateClaims.claimAmount = claims["rows"][0]["pClaimAmount"] ? Double.parseDouble(claims["rows"][0]["pClaimAmount"].toString()) : 0
//							updateClaims.voucherNo = claims["rows"][0]["pVoucherNo"]
//							if(!Objects.isNull(claims["rows"][0]["pVoucherDate"]) && (claims["rows"][0]["pVoucherDate"])){
//								SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
//								updateClaims.voucherDate = formatter.parse(claims["rows"][0]["pVoucherDate"].toString())
//							}
//							updateClaims.processStage = claims["rows"][0]["pProcessDate"]
//							updateClaims.status = claims["rows"][0]["pStatus"]
//							save(updateClaims)
//						}
//					}
//				}
//		}
//		return  "OK"
//
//
//	}
	@GraphQLQuery(name = "bsPhilClaimsAll")
	List<BsPhilClaims> bsPhilClaimsAll(
			@GraphQLArgument(name = "dateType") String dateType,
			@GraphQLArgument(name = "from") String from,
			@GraphQLArgument(name = "to") String to,
			@GraphQLArgument(name = "sort") String sort,
			@GraphQLArgument(name = "status") ArrayList<String> status
	) {
		createQuery("""
                    Select c from BsPhilClaims c  
              	where 
				 	to_char(${dateType}, 'YYYY-MM-DD') between :from and :to
					and c.status in (:status)
					order by ${sort}
            """,
				[
						from: from,
						to: to,
						status : status
				] as Map<String, Object>).resultList
	}

	@GraphQLQuery(name = "bsPhilClaimsPageable")
	Page<BsPhilClaims> bsPhilClaimsPageable(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "dateType") String dateType,
			@GraphQLArgument(name = "from") String from,
			@GraphQLArgument(name = "to") String to,
			@GraphQLArgument(name = "sort") String sort,
			@GraphQLArgument(name = "status") ArrayList<String> status,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		String queryStr = """
              	Select c from BsPhilClaims c  
              	where 
					(
						lower(c.patient.fullName) like lower(concat('%',:search,'%')) 
						or
						lower(c.arNo) like lower(concat('%',:search,'%')) 
						or
						lower(c.patientCase.caseNo) like lower(concat('%',:search,'%')) 
						or
						lower(c.claimSeriesLhio) like lower(concat('%',:search,'%')) 
						or
						lower(c.claimNumber) like lower(concat('%',:search,'%')) 

					)  
					and c.status in (:status)
		"""

		String countQueryStr = """
              	Select count(c) from BsPhilClaims c  
              	where 
					(
						lower(c.patient.fullName) like lower(concat('%',:search,'%')) 
						or
						lower(c.arNo) like lower(concat('%',:search,'%')) 
						or
						lower(c.patientCase.caseNo) like lower(concat('%',:search,'%')) 
						or
						lower(c.claimSeriesLhio) like lower(concat('%',:search,'%')) 
						or
						lower(c.claimNumber) like lower(concat('%',:search,'%')) 

					)   
					and c.status in (:status)
		"""

		String dateFilter = """
				and to_char(c.${dateType}, 'YYYY-MM-DD') between :from and :to
		"""

		queryStr += dateFilter
		countQueryStr += dateFilter

		queryStr += """
				order by c.${sort}
		"""

		Map<String, Object> params = new HashMap<>()
		params.put('search',search)
		params.put('status',status)
		params.put('from',from)
		params.put('to',to)

		getPageable(
				queryStr,
				countQueryStr,
				page,
				size,
				params
		)
	}

	@GraphQLQuery(name = "unTrackedClaims")
	List<AccountReceivableItems> unTrackedClaims(
			@GraphQLArgument(name = "companyAccount") UUID companyAccount
	) {
		accountReceivableItemsServices.createQuery("""
                    select  
					bsi
					from AccountReceivableItems bsi
					LEFT JOIN BsPhilClaimsItems bpci on bpci.accountReceivableItems.id = bsi.id 
					LEFT JOIN AccountReceivable ar on ar.id = bsi.accountReceivable.id
					where
					bpci.id is null
					and
					ar.status = 'active'
					and
					ar.groups['COMPANY_ACCOUNT_ID'] = :companyAccount
            """,
				[
						companyAccount: companyAccount
				] as Map<String, Object>).resultList

	}

	@GraphQLMutation
	GraphQLRetVal<String> initiatePhilClaimsUpdate() {
		checkUnTrackClaims()
		return new GraphQLRetVal<String>("Ok", true, "Calculation has started. This may take several minutes, please wait.")
	}

	@GraphQLQuery(name = "sampleConvert")
	Instant convertStrToInstant(
			@GraphQLArgument(name = "dateStr") String dateStr,
			@GraphQLArgument(name = "regexSplitter") String regexSplitter
	) {
		String[] splitStr = dateStr.split(regexSplitter)
		Calendar cl = Calendar.getInstance()
		cl.set(Integer.parseInt(splitStr[2]),Integer.parseInt(splitStr[0])-1,Integer.parseInt(splitStr[1]))
		return cl.toInstant()
	}

	@Async
	def checkUnTrackClaims (){
		List<AccountReceivableItems> accRecItems = unTrackedClaims(UUID.fromString("179c6365-4fd6-4963-b54a-9de01598619b"))
		accRecItems.each {
			it ->
				BillingItem billingItem = billingItemServices.billingItemById(UUID.fromString(it.details['BILLING_ITEM_ID']))
				if(billingItem) {
					def claims = getEClaimsVoucher(billingItem.billing.patientCase.caseNo)
					if(claims["rows"]) {
						def pClaimSeries = claims["rows"][0]["pClaimSeriesLhio"]
						if (pClaimSeries) {
							BsPhilClaims existingClaims = checkClaimsExist(claims["rows"][0]["pClaimSeriesLhio"] as String)
							if (!existingClaims) {
								def newPhilClaims = new BsPhilClaims()
								newPhilClaims.billing = billingItem.billing
								newPhilClaims.patient  = billingItem.billing.patient
								newPhilClaims.patientCase  = billingItem.billing.patientCase

								if (!Objects.isNull(claims["rows"][0]["pClaimNumber"])) {
									newPhilClaims.claimNumber = claims["rows"][0]["pClaimNumber"]
								}
								if (!Objects.isNull(claims["rows"][0]["pClaimSeriesLhio"])) {
									newPhilClaims.claimSeriesLhio = claims["rows"][0]["pClaimSeriesLhio"]
								}
								if (!Objects.isNull(claims["rows"][0]["pClaimAmount"])) {
									newPhilClaims.claimAmount = claims["rows"][0]["pClaimAmount"] ? Double.parseDouble(claims["rows"][0]["pClaimAmount"].toString()) : 0
								}
								if (!Objects.isNull(claims["rows"][0]["pVoucherNo"])) {
									newPhilClaims.voucherNo = claims["rows"][0]["pVoucherNo"]
								}
								if (!Objects.isNull(claims["rows"][0]["pProcessStage"])) {
									newPhilClaims.processStage = claims["rows"][0]["pProcessStage"]
								}

								if (!Objects.isNull(claims["rows"][0]["pProcessDate"])) {
									String[] splitProcessDate = claims["rows"][0]["pProcessDate"].toString().split(" ")
									if(splitProcessDate[0]){
										newPhilClaims.processDate = convertStrToInstant(splitProcessDate[0],"-")
									}
								}

								if (!Objects.isNull(claims["rows"][0]["DateCreatedStr"])) {
									String[] splitCreatedDate = claims["rows"][0]["DateCreatedStr"].toString().split(" ")
									if(splitCreatedDate[0]){
										newPhilClaims.claimDateCreated = convertStrToInstant(splitCreatedDate[0],"/")
									}
								}

								if (!Objects.isNull(claims["rows"][0]["CreatedBy"])) {
									newPhilClaims.claimCreator = claims["rows"][0]["CreatedBy"]
								}

								if (!Objects.isNull(claims["rows"][0]["pStatus"])) {
									newPhilClaims.status = claims["rows"][0]["pStatus"]
								}

								if (!Objects.isNull(claims["rows"][0]["pVoucherDate"]) && (claims["rows"][0]["pVoucherDate"])) {
									SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
									newPhilClaims.voucherDate = formatter.parse(claims["rows"][0]["pVoucherDate"].toString())
								}

								def newSave = save(newPhilClaims)

								BsPhilClaimsItems philClaimsItem = new BsPhilClaimsItems()
								philClaimsItem.billing = billingItem.billing.id
								philClaimsItem.billingItem = billingItem
								philClaimsItem.patient = billingItem.billing.patient.id
								philClaimsItem.patientCase = billingItem.billing.patientCase.id
								philClaimsItem.accountReceivable = it.accountReceivable
								philClaimsItem.accountReceivableItems = it
								philClaimsItem.bsPhilClaims = newSave
								philClaimsItem.type = it.type
								philClaimsItem.amount = billingItem.credit
								bsPhilClaimsItemsServices.save(philClaimsItem)

							} else {
								existingClaims.billing  = billingItem.billing
								existingClaims.patient  = billingItem.billing.patient
								existingClaims.patientCase  = billingItem.billing.patientCase
								existingClaims.claimNumber = claims["rows"][0]["pClaimNumber"]
								existingClaims.claimSeriesLhio = claims["rows"][0]["pClaimSeriesLhio"]
								existingClaims.claimAmount = claims["rows"][0]["pClaimAmount"] ? Double.parseDouble(claims["rows"][0]["pClaimAmount"].toString()) : 0
								existingClaims.voucherNo = claims["rows"][0]["pVoucherNo"]
								if (!Objects.isNull(claims["rows"][0]["pVoucherDate"]) && (claims["rows"][0]["pVoucherDate"])) {
									SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
									existingClaims.voucherDate = formatter.parse(claims["rows"][0]["pVoucherDate"].toString())
								}
								existingClaims.processStage = claims["rows"][0]["pProcessStage"]
								if (!Objects.isNull(claims["rows"][0]["pProcessDate"])) {
									String[] splitProcessDate = claims["rows"][0]["pProcessDate"].toString().split(" ")
									if(splitProcessDate[0]) {
										existingClaims.processDate = convertStrToInstant(splitProcessDate[0], "-")
									}
								}

								if (!Objects.isNull(claims["rows"][0]["DateCreatedStr"])) {
									String[] splitCreatedDate = claims["rows"][0]["DateCreatedStr"].toString().split(" ")
									if(splitCreatedDate[0]) {
										existingClaims.claimDateCreated = convertStrToInstant(splitCreatedDate[0], "/")
									}
								}
								existingClaims.status = claims["rows"][0]["pStatus"]
								existingClaims.claimCreator = claims["rows"][0]["CreatedBy"]
								save(existingClaims)


								BsPhilClaimsItems philClaimsItem = new BsPhilClaimsItems()
								philClaimsItem.billing = billingItem.billing.id
								philClaimsItem.billingItem = billingItem
								philClaimsItem.accountReceivable = it.accountReceivable
								philClaimsItem.accountReceivableItems = it
								philClaimsItem.patient = billingItem.billing.patient.id
								philClaimsItem.patientCase = billingItem.billing.patientCase.id
								philClaimsItem.bsPhilClaims = existingClaims
								philClaimsItem.type = it.type
								philClaimsItem.amount = billingItem.credit
								bsPhilClaimsItemsServices.save(philClaimsItem)


							}
						}
					}
				}
		}

		updateUnProcessPhilClaimsData()
	}


	@Transactional
	def updateUnProcessPhilClaimsData() {
		List<BsPhilClaims> bsClaims =  getUnProcessClaims()
		bsClaims.each {
			it ->
				def claims = getEClaimsVoucher(it.caseNo)
				if (claims["rows"]) {
					it.claimNumber = claims["rows"][0]["pClaimNumber"]
					it.claimSeriesLhio = claims["rows"][0]["pClaimSeriesLhio"]
					it.claimAmount = claims["rows"][0]["pClaimAmount"] ? Double.parseDouble(claims["rows"][0]["pClaimAmount"].toString()) : 0
					it.voucherNo = claims["rows"][0]["pVoucherNo"]
					if (!Objects.isNull(claims["rows"][0]["pVoucherDate"]) && (claims["rows"][0]["pVoucherDate"])) {
						SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
						it.voucherDate = formatter.parse(claims["rows"][0]["pVoucherDate"].toString())
					}
					it.processStage = claims["rows"][0]["pProcessStage"]
					if (!Objects.isNull(claims["rows"][0]["pProcessDate"])) {
						String[] splitProcessDate = claims["rows"][0]["pProcessDate"].toString().split(" ")
						if(splitProcessDate[0]){
							it.processDate = convertStrToInstant(splitProcessDate[0],"-")
						}
					}

					if (!Objects.isNull(claims["rows"][0]["DateCreatedStr"])) {
						String[] splitCreatedDate = claims["rows"][0]["DateCreatedStr"].toString().split(" ")
						if(splitCreatedDate[0]) {
							it.claimDateCreated = convertStrToInstant(splitCreatedDate[0], "/")
						}
					}
					it.claimCreator = claims["rows"][0]["CreatedBy"]
					it.status = claims["rows"][0]["pStatus"]
					save(it)
				}
		}
	}
}

