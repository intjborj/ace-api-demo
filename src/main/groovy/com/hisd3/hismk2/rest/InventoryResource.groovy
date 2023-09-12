package com.hisd3.hismk2.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.hisd3.hismk2.domain.billing.ItemPriceControl
import com.hisd3.hismk2.domain.billing.PriceTierDetail
import com.hisd3.hismk2.domain.hospital_config.AdmissionConfiguration
import com.hisd3.hismk2.domain.hospital_config.PharmacyConfiguration
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.inventory.*
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.pms.Patient
import com.hisd3.hismk2.graphqlservices.billing.BillingItemServices
import com.hisd3.hismk2.graphqlservices.inventory.InventoryPageableService
import com.hisd3.hismk2.graphqlservices.inventory.ItemDto
import com.hisd3.hismk2.graphqlservices.inventory.ItemService
import com.hisd3.hismk2.graphqlservices.inventory.MarkupPageableService
import com.hisd3.hismk2.graphqlservices.inventoryv2.ServicePhysicalCountView
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.billing.ItemPriceControlRepository
import com.hisd3.hismk2.repository.billing.PriceTierDetailRepository
import com.hisd3.hismk2.repository.hospital_config.AdmissionConfigurationRepository
import com.hisd3.hismk2.repository.hospital_config.PharmacyConfigurationRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.inventory.*
import com.hisd3.hismk2.repository.pms.CaseRepository
import com.hisd3.hismk2.repository.pms.PatientRepository
import com.hisd3.hismk2.rest.dto.*
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.services.EntityObjectMapperService
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import com.hisd3.hismk2.services.PrintingService
import io.leangen.graphql.annotations.GraphQLArgument
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.apache.commons.lang3.StringUtils
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.apache.xmlbeans.impl.xb.xsdschema.AnyDocument
import org.json.JSONArray
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import java.lang.reflect.Type
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RestController
class InventoryResource {

	@Autowired
	GeneratorService generatorService

	@Autowired
	SupplierRepository supplierRepository

	@Autowired
	DepartmentRepository departmentRepository

	@Autowired
	PurchaseRequestRepository purchaseRequestRepository

	@Autowired
	ItemRepository itemRepository

	@Autowired
	PurchaseRequestItemRepository purchaseRequestItemRepository

	@Autowired
	SupplierItemRepository supplierItemRepository

	@Autowired
	PurchaseOrderRepository purchaseOrderRepository

	@Autowired
	PurchaseOrderItemRepository purchaseOrderItemRepository

	@Autowired
	PaymentTermRepository paymentTermRepository

	@Autowired
	ReceivingReportRepository receivingReportRepository

	@Autowired
	ReceivingReportItemRepository receivingReportItemRepository

	@Autowired
	InventoryLedgerRepository inventoryLedgerRepository

	@Autowired
	DocumentTypeRepository documentTypeRepository

	@Autowired
    StockRequestItemRepository stockRequestItemRepository

	@Autowired
	DepartmentStockRequestRepository departmentStockRequestRepository

	@Autowired
	DepartmentStockRequestItemRepository departmentStockRequestItemRepository

	@Autowired
	EmployeeRepository employeeRepository

	@Autowired
	DepartmentStockIssueRepository departmentStockIssueRepository

	@Autowired
	DepartmentStockIssueItemRepository departmentStockIssueItemRepository

	@Autowired
	ReturnSupplierRepository returnSupplierRepository

	@Autowired
	ReturnSupplierItemRepository returnSupplierItemRepository

	@Autowired
	StockRequestRepository stockRequestRepository

	@Autowired
	CashBasisRepository cashBasisRepository

	@Autowired
	PatientRepository patientRepository

	@Autowired
	PrintingService printingService

	@Autowired
	PharmacyConfigurationRepository pharmacyConfigurationRepository

	@Autowired
	AdmissionConfigurationRepository admissionConfigurationRepository

	@Autowired
	CaseRepository caseRepository

	@Autowired
	DepartmentItemRepository departmentItemRepository

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	JdbcTemplate jdbcTemplate

	@Autowired
	PhysicalCountViewRepository physicalCountViewRepository

	@Autowired
	ServicePhysicalCountView servicePhysicalCountView

	@Autowired
	PriceTierDetailRepository priceTierDetailRepository

	@Autowired
	ItemPriceControlRepository itemPriceControlRepository

	@Autowired
	ItemService itemService

	@Autowired
	EntityObjectMapperService entityObjectMapperService

	@Autowired
	BillingItemServices billingItemServices

	@Autowired
	MarkupPageableService markupPageableService

	@Autowired
	InventoryPageableService inventoryPageableService

	//purchase Reqest
	@Transactional(rollbackFor = QueryErrorException.class)
	@RequestMapping(method = [RequestMethod.POST], value = ["/api/pr/save"])
	List<PrItems> savePurchaseReq(
			@RequestParam("pr") String pr,
			@RequestParam("prItems") String prItems,
			@RequestParam("prId") String prId
	) {
		String request = pr
		String requestItems = prItems
		Gson gson = new Gson()
		PurchaseRequestDto prDto = gson.fromJson(request, PurchaseRequestDto.class)
		Type collectionType = new TypeToken<Collection<PrItems>>() {}.getType()
		List<PrItems> request_items = (List<PrItems>) new Gson().fromJson(requestItems, collectionType)
		//check if
		try {
			if (prId) {
				//update
				def p_request = purchaseRequestRepository.findById(UUID.fromString(prId)).get()
				p_request.prDateNeeded = Instant.parse(prDto.date_needed)
				if (prDto.supplier) {
					p_request.supplier = supplierRepository.findById(UUID.fromString(prDto.supplier)).get()
				}
				p_request.requestedDepartment = departmentRepository.findById(UUID.fromString(prDto.request_to)).get()
				p_request.prType = prDto.request_type

				//loop items
				request_items.each {
					it ->
						if (!it.prItemId) {
							def pr_items = new PurchaseRequestItem()
							pr_items.item = itemRepository.findById(UUID.fromString(it.itemId)).get()
							pr_items.purchaseRequest = purchaseRequestRepository.findById(UUID.fromString(prId)).get()
							if (it.supplierItemId) {
								pr_items.refSupItemId = UUID.fromString(it.supplierItemId)
							}
							pr_items.requestedQty = it.qty as Integer
							pr_items.unitCost = it.cost as BigDecimal
							pr_items.total = it.total as BigDecimal
							purchaseRequestItemRepository.save(pr_items)
							//
							if (it.supplierItemId) {
								def supItem = supplierItemRepository.findById(UUID.fromString(it.supplierItemId)).get()
								supItem.cost = it.cost as BigDecimal
								supplierItemRepository.save(supItem)
							}

						} else {
							//update
							def pr_item = purchaseRequestItemRepository.findById(UUID.fromString(it.prItemId)).get()
							pr_item.requestedQty = it.qty as Integer
							pr_item.unitCost = it.cost as BigDecimal
							pr_item.total = it.total as BigDecimal
							purchaseRequestItemRepository.save(pr_item)
							//
							if (it.supplierItemId) {
								def supItem = supplierItemRepository.findById(UUID.fromString(it.supplierItemId)).get()
								supItem.cost = it.cost as BigDecimal
								supplierItemRepository.save(supItem)
							}
						}
				}
				purchaseRequestRepository.save(p_request)
			} else {
				//insert
				def p_request = new PurchaseRequest()
				p_request.prNo = generatorService.getNextValue(GeneratorType.PR_NO) { Long no ->
					StringUtils.leftPad(no.toString(), 6, "0")
				}
				p_request.prDateRequested = Instant.now()
				p_request.prDateNeeded = Instant.parse(prDto.date_needed)
				if (prDto.supplier) {
					p_request.supplier = supplierRepository.findById(UUID.fromString(prDto.supplier)).get()
				}
				p_request.userId = UUID.fromString(prDto.user_id)
				p_request.userFullname = prDto.requested_by
				p_request.requestedDepartment = departmentRepository.findById(UUID.fromString(prDto.request_to)).get()
				p_request.requestingDepartment = departmentRepository.findById(UUID.fromString(prDto.requestingDep)).get()
				p_request.prType = prDto.request_type
				p_request.status = "For Approval"
				def afterSave = purchaseRequestRepository.save(p_request)

				//loop items
				request_items.each {
					it ->
						if (!it.prItemId) {
							def pr_items = new PurchaseRequestItem()
							pr_items.item = itemRepository.findById(UUID.fromString(it.itemId)).get()
							pr_items.purchaseRequest = afterSave
							if (it.supplierItemId) {
								pr_items.refSupItemId = UUID.fromString(it.supplierItemId)
							}
							pr_items.requestedQty = it.qty as Integer
							pr_items.unitCost = it.cost as BigDecimal
							pr_items.total = it.total as BigDecimal
							purchaseRequestItemRepository.save(pr_items)
							//
							if (it.supplierItemId) {
								def supItem = supplierItemRepository.findById(UUID.fromString(it.supplierItemId)).get()
								supItem.cost = it.cost as BigDecimal
								supplierItemRepository.save(supItem)
							}
						} else {
							//update
							def pr_item = purchaseRequestItemRepository.findById(UUID.fromString(it.prItemId)).get()
							pr_item.requestedQty = it.qty as Integer
							pr_item.unitCost = it.cost as BigDecimal
							pr_item.total = it.total as BigDecimal
							purchaseRequestItemRepository.save(pr_item)
							//
							if (it.supplierItemId) {
								def supItem = supplierItemRepository.findById(UUID.fromString(it.supplierItemId)).get()
								supItem.cost = it.cost as BigDecimal
								supplierItemRepository.save(supItem)
							}

						}
				}

			}
		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}
		//
		return request_items
	}

	@RequestMapping(method = [RequestMethod.POST], value = ["/api/get/ledger"])
	List<StockCard> getLedger(
			@RequestParam("itemId") String itemId,
			@RequestParam("department") String depId
	) {

		String sql = "SELECT a.id as id ,a.source_dep,b.department_name as source_department,a.destination_dep,c.department_name as dest_department,a.document_types,d.document_code,d.document_desc,a.item,e.sku,e.item_code,e.desc_long,a.reference_no,(a.ledger_date + interval '8 hours' ) as ledger_date, " +
				"CASE WHEN a.document_types = '4f88d8d7-ecce-4538-a97b-88884b1e106e' OR a.document_types = '37683c86-3038-4207-baf0-b51456fd7037' THEN 0 ELSE a.ledger_qty_in END as ledger_qtyin, " +
				"a.ledger_qty_out, " +
				"CASE WHEN a.document_types = '4f88d8d7-ecce-4538-a97b-88884b1e106e' OR a.document_types = '37683c86-3038-4207-baf0-b51456fd7037' THEN a.ledger_qty_in ELSE 0 END as adjustment, " +
				"a.ledger_unit_cost as Unitcost, " +
				"sum(a.ledger_qty_in - a.ledger_qty_out) OVER (ORDER BY a.ledger_date) as RunningQty, " +
				"CASE WHEN a.document_types = 'd12f0de2-cb65-42ab-bcdb-881ebce57045' THEN a.ledger_unit_cost WHEN a.document_types = '4f88d8d7-ecce-4538-a97b-88884b1e106e' THEN abs(a.ledger_unit_cost) ELSE  COALESCE(round((sum(a.ledger_unit_cost * (sum(a.ledger_qty_in - a.ledger_qty_out))) OVER (ORDER BY a.ledger_date)) / NULLIF((sum(a.ledger_qty_in - a.ledger_qty_out) OVER (ORDER BY a.ledger_date)),0),2),a.ledger_unit_cost) END as wcost, " +
				"sum(abs(a.ledger_unit_cost) * (sum(a.ledger_qty_in - a.ledger_qty_out))) OVER (ORDER BY a.ledger_date) as RunningBalance " +
				"from inventory.inventory_ledger a " +
				"inner join public.departments b on a.source_dep = b.id " +
				"inner join public.departments c on a.destination_dep = c.id " +
				"inner join inventory.document_types d on a.document_types = d.id " +
				"inner join inventory.item e on a.item = e.id " +
				"where a.is_include = true and a.source_dep = " + "'" + depId + "'" + " and a.item = " + "'" + itemId + "'" +
				"group BY a.id, a.source_dep, b.department_name, a.destination_dep, c.department_name, a.document_types, d.document_code, d.document_desc, a.item, e.sku, e.item_code, e.desc_long, a.reference_no, a.ledger_date, a.ledger_qty_in, a.ledger_qty_out, a.ledger_unit_cost"
		List<StockCard> items = jdbcTemplate.query(sql, new BeanPropertyRowMapper(StockCard.class))
		return items
	}

	//@RequestMapping(method = [RequestMethod.POST], value = ["/api/get/ledger_all"])
	List<StockCard> getLedgerAll(
			String itemId
	){

		String sql = "SELECT * from inventory.stock_card_all("+"'"+itemId+"'"+");"
		List<StockCard> items = jdbcTemplate.query(sql, new BeanPropertyRowMapper(StockCard.class))
		return items
	}

	//@RequestMapping(method = [RequestMethod.POST], value = ["/api/get/last_wcost_id"])
	BigDecimal getLedgerWcostAll(
			String itemId,
			String id
	) {

		String sql = "SELECT inventory.last_wcost_by_id("+"'"+itemId+"',"+"'"+id+"'"+");"
		BigDecimal lwcost = jdbcTemplate.queryForObject(sql, BigDecimal) as BigDecimal
		return lwcost
	}

	Integer getOnhandByDepartment(
			UUID depId,
			UUID itemId
	) {
		//inventory.onhand(depid uuid, itemid uuid)
		String sql = "SELECT inventory.onhand('${depId}', '${itemId}');"
		Integer onhand = jdbcTemplate.queryForObject(sql, Integer) as Integer
		return onhand
	}

	@RequestMapping(method = [RequestMethod.GET], value = ["/api/get/onHand"])
	InventoryDto getOnHandLastWcost(
			@RequestParam("itemId") UUID itemId,
			@RequestParam("id") UUID id,
			@RequestParam("date") String date
	) {

		String sql = "select * from inventory.on_hand_last_wcost("+"'"+id+"',"+"'"+itemId+"',"+"'"+date+"'"+");"
		InventoryDto items = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper(InventoryDto.class))
		return items
	}

	InventoryDto getOnHandLastWcostPhy(
			@RequestParam("itemId") UUID itemId,
			@RequestParam("id") UUID id,
			@RequestParam("date") String date,
			@RequestParam("phy") UUID phy
	) {

		String sql = "select * from inventory.on_hand_last_wcost_phy_id("+"'"+id+"',"+"'"+itemId+"',"+"'"+date+"',"+"'"+phy+"'"+");"
		InventoryDto items = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper(InventoryDto.class))
		return items
	}

	@RequestMapping(method = [RequestMethod.POST], value = ["/api/get/on_hand_report"])
	List<OnHandReport> getOnHandReport(
			@RequestParam("department") String depId,
			@RequestParam("filter_date") String filter_date,
			@RequestParam("filter") String filter
	) {

		String sql = "SELECT a.id, a.item, b.desc_long, c.unit_description as unit_of_purchase, d.unit_description as unit_of_usage, e.category_description , a.department , f.department_name,\n" +
				"COALESCE((SELECT inventory.onhand_by_date(a.department, a.item, " + "'" + filter_date + "'" + ")),0) as onhand, " +
				"COALESCE((SELECT inventory.last_unit_price_by_date(a.item, " + "'" + filter_date + "'" + ")),0) as last_unit_cost, " +
				"COALESCE((SELECT inventory.last_wcost_by_date(a.item, " + "'" + filter_date + "'" + ")),0) as last_wcost, " +
				"COALESCE((SELECT inventory.expiry_by_date(a.item, " + "'" + filter_date + "'" + ")),NULL) as expiration_date " +
				"FROM " +
				"inventory.department_item as a, " +
				"inventory.item as b, " +
				"inventory.unit_measurements as c, " +
				"inventory.unit_measurements as d, " +
				"inventory.item_categories as e, " +
				"public.departments as f " +
				"where " +
				"a.item = b.id AND " +
				"b.unit_of_purchase = c.id AND " +
				"b.unit_of_usage = d.id AND " +
				"b.item_category = e.id AND " +
				"a.department  = f.id AND " +
				"lower(b.desc_long)  like lower(" + "'%" + filter + "%'" + ") AND " +
				"department = " + "'" + depId + "'" + " and a.is_assign = true and (b.consignment = false or b.consignment is null) order by b.desc_long"
		List<OnHandReport> items = jdbcTemplate.query(sql, new BeanPropertyRowMapper(OnHandReport.class))
		return items
	}

	List<OnHandReport> getOnHandReport(
			@RequestParam("department") UUID depId,
			@RequestParam("filter_date") String filter_date,
			@RequestParam("filter") String filter,
			@RequestParam("status") String status
	) {

		String sql = "SELECT "+
			"a.id, "+
			"a.item, "+
			"b.desc_long, "+
			"c.unit_description as unit_of_purchase, "+
			"d.unit_description as unit_of_usage, "+
			"e.category_description, "+
			"a.department, "+
			"f.department_name, "+
			"COALESCE(h.onhand,0) as onhand, "+
			"COALESCE(i.unitcost,0) as last_unit_cost, "+
			"COALESCE((SELECT inventory.last_wcost_by_date(a.item, '${filter_date}')),0) as last_wcost, "+
			"COALESCE(g.expiration_date,NULL) as expiration_date "+
			"FROM "+
			"inventory.department_item a "+
			"LEFT join inventory.item b on a.item = b.id and (b.fix_asset is null or b.fix_asset = false) "+
			"LEFT join inventory.unit_measurements c on b.unit_of_purchase = c.id "+
			"LEFT join inventory.unit_measurements d on b.unit_of_usage = d.id "+
			"LEFT join inventory.item_categories e on b.item_category = e.id "+
			"LEFT join public.departments f on a.department  = f.id "+
			"LEFT join inventory.expiry_ref('${filter_date}') g on g.item  = a.item "+
			"LEFT join inventory.onhand_ref('${filter_date}') h on h.item = a.item AND h.source_dep = a.department "+
			"LEFT join inventory.unitcost_ref('${filter_date}') i on i.item  = a.item "+
			"where lower(b.desc_long)  like lower(concat('%','${filter}','%')) AND "+
			"a.department = '${depId}' and a.is_assign = true "
		if(status.equalsIgnoreCase("ACTIVE")){
			sql += "and b.active = true "
		}else if(status.equalsIgnoreCase("INACTIVE")){
			sql += "and (b.active = false or b.active is null) "
		}
		sql += "and (b.consignment = false or b.consignment is null) order by b.desc_long;"
		List<OnHandReport> items = jdbcTemplate.query(sql, new BeanPropertyRowMapper(OnHandReport.class))
		return items
	}

	List<OnHandReport> getOnhandReportByDate(
			@RequestParam("department") UUID depId,
			@RequestParam("filter_date") String filter_date,
			@RequestParam("filter") String filter
	) {

		String sql = "SELECT * from inventory.onhand_report('${filter}','${depId}','${filter_date}');"
		List<OnHandReport> items = jdbcTemplate.query(sql, new BeanPropertyRowMapper(OnHandReport.class))
		return items
	}

	//lastUnitPrice
	@RequestMapping(method = [RequestMethod.POST], value = ["/api/get/lastUnitPrice"])
	BigDecimal getLastUnitPrice(
			@RequestParam("itemId") String itemId
	) {

		String sql = "SELECT coalesce(inventory.last_wcost('${itemId}'),0);"
		BigDecimal unitCost = jdbcTemplate.queryForObject(sql, BigDecimal) as BigDecimal
		return unitCost
	}

	//getCountOnSpecificDate
	@RequestMapping(method = [RequestMethod.POST], value = ["/api/get/getCountByDate"])
	Integer getCountOnSpecificDate(
			@RequestParam("itemId") String itemId,
			@RequestParam("department") String depId,
			@RequestParam("date") String date
	) {

		String sql = "WITH stockcard as(" +
				"SELECT a.id as id ,a.source_dep,b.department_name as source_department,a.destination_dep,c.department_name as dest_department,a.document_types,d.document_code,d.document_desc,a.item,e.sku,e.item_code,e.desc_long,a.reference_no,a.ledger_date, " +
				"CASE WHEN a.document_types = '4f88d8d7-ecce-4538-a97b-88884b1e106e' OR a.document_types = '37683c86-3038-4207-baf0-b51456fd7037' THEN 0 ELSE a.ledger_qty_in END as ledger_qtyin, " +
				"a.ledger_qty_out, " +
				"CASE WHEN a.document_types = '4f88d8d7-ecce-4538-a97b-88884b1e106e' OR a.document_types = '37683c86-3038-4207-baf0-b51456fd7037' THEN a.ledger_qty_in ELSE 0 END as adjustment, " +
				"a.ledger_unit_cost as Unitcost, " +
				"sum(a.ledger_qty_in - a.ledger_qty_out) OVER (ORDER BY a.ledger_date) as RunningQty, " +
				"CASE WHEN a.document_types = 'd12f0de2-cb65-42ab-bcdb-881ebce57045' THEN a.ledger_unit_cost WHEN a.document_types = '4f88d8d7-ecce-4538-a97b-88884b1e106e' THEN abs(a.ledger_unit_cost) ELSE  COALESCE(round((sum(a.ledger_unit_cost * (sum(a.ledger_qty_in - a.ledger_qty_out))) OVER (ORDER BY a.ledger_date)) / NULLIF((sum(a.ledger_qty_in - a.ledger_qty_out) OVER (ORDER BY a.ledger_date)),0),2),a.ledger_unit_cost) END as wcost, " +
				"sum(abs(a.ledger_unit_cost) * (sum(a.ledger_qty_in - a.ledger_qty_out))) OVER (ORDER BY a.ledger_date) as RunningBalance " +
				"from inventory.inventory_ledger a " +
				"inner join public.departments b on a.source_dep = b.id " +
				"inner join public.departments c on a.destination_dep = c.id " +
				"inner join inventory.document_types d on a.document_types = d.id " +
				"inner join inventory.item e on a.item = e.id " +
				"where a.source_dep = " + "'" + depId + "'" + " and a.item = " + "'" + itemId + "'" + " and a.is_include = true " +
				"group BY a.id, a.source_dep, b.department_name, a.destination_dep, c.department_name, a.document_types, d.document_code, d.document_desc, a.item, e.sku, e.item_code, e.desc_long, a.reference_no, a.ledger_date, a.ledger_qty_in, a.ledger_qty_out, a.ledger_unit_cost" +
				")SELECT COALESCE ((SELECT RunningQty FROM stockcard WHERE DATE(ledger_date) <= " + "'" + date + "'" + " ORDER BY ledger_date DESC LIMIT 1),0);"
		Integer onhand = jdbcTemplate.queryForObject(sql, Integer)
		return onhand
	}


	@RequestMapping("/api/generateMSRTag")
	String generateMSRTag(@RequestParam("id") UUID id) {
		StockRequest msr = stockRequestRepository.getById(id)
		PharmacyConfiguration configuration = pharmacyConfigurationRepository.findAll().first()
		Employee e = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()
		String preparedBy = msr.preparedBy!=null?msr.preparedBy.fullnameWithTitle : e.fullnameWithTitle
		String preparedDate = msr.preparedByDatetime!=null?msr.preparedByDatetime.toString(): LocalDateTime.now().toString()
		String fullname = msr?.patient?.getFullName()
		String msrno = msr.stockRequestNo + " | "+msr?.patientCase?.getRoom()?.roomBedNo
		String stickerCode =
				$/CT~~CD,~CC^~CT~
                ^XA~TA000~JSN^LT0^MNW^MTT^PON^PMN^LH0,0^JMA^PR2,2~SD20^JUS^LRN^CI0^XZ
                ^XA
                ^MMT
                ^PW468
                ^LL0223
                ^LS0
                ^FT49,89^ABN,11,7^FH\^FDPREP BY^FS
                ^FT49,105^ABN,11,7^FH\^FDPREP DT^FS
                ^FT49,74^ABN,11,7^FH\^FDREQ DT^FS
                ^FT49,59^ABN,11,7^FH\^FDREQ BY  ^FS
                ^FT49,45^ABN,11,7^FH\^FDMSR #  ^FS
                ^FT129,89^ABN,11,7^FH\^FD:^FS
                ^FT129,105^ABN,11,7^FH\^FD:^FS
                ^FT129,74^ABN,11,7^FH\^FD:^FS
                ^FT129,59^ABN,11,7^FH\^FD:^FS
                ^FT129,44^ABN,11,7^FH\^FD:^FS
                ^FT129,30^ABN,11,7^FH\^FD:^FS
                ^FT145,105^ABN,11,7^FH\^FD${preparedDate.toString()}H^FS
                ^FT49,30^ABN,11,7^FH\^FDPATIENT ^FS
                ^FT146,45^ABN,11,7^FH\^FD${msr.stockRequestNo}^FS
                ^FT145,74^ABN,11,7^FH\^FD${msr.requestedByDatetime.toString()}H^FS
                ^BY3,3,84^FT222,204^BCN,,N,N
                ^FD>:${msr.stockRequestNo}^FS
                ^FT145,30^ABN,11,7^FH\^FD${fullname?.replace("Ñ", "N")}^FS
                ^FT145,90^ABN,11,7^FH\^FD${preparedBy}^FS
                ^FT145,59^ABN,11,7^FH\^FD${msr?.requestedBy?.fullnameWithTitle}E^FS
                ^FT47,216^BQN,2,4
                ^FH\^FDMA,${msrno}^FS
                ^PQ1,0,1,Y^XZ/$

		print(configuration.stickerPinterLocation, stickerCode)

		return id.toString()
	}

	@RequestMapping("/api/markAsForCashPayment")
	String markAsForCashPayment(@RequestParam("id") UUID id) {
		StockRequest msr = stockRequestRepository.getById(id)
		List<CashBasis> cashBasusis = cashBasisRepository.getPendingCashBasisByCase(msr.patientCase.id)
		CashBasis cashBasus = null;
		if(!cashBasusis.isEmpty())
		{
			cashBasus = cashBasusis.first()
		}
		if(cashBasus==null)
		{
			cashBasus = new CashBasis()
			cashBasus.patientCase = msr.patientCase
			cashBasus.department = msr.requestedDepartment
			cashBasus.cashBasisNo = generatorService.getNextValue(GeneratorType.CASH_BASIS_NO) { Long no ->
				'CB-' + StringUtils.leftPad(no.toString(), 6, "0")
			}
			cashBasus.status = "Pending"
			cashBasus.patient = msr.patient
			cashBasus = cashBasisRepository.save(cashBasus)
		}

        List<StockRequestItem> items = stockRequestItemRepository.getSRItemsBySRId(id)
        for(StockRequestItem item in items)
        {
            item.forCashPayment = true
            stockRequestItemRepository.save(item)
        }
		return id.toString()
	}

	@RequestMapping("/api/generateWristbandCode")
	String generateWristbandCode(@RequestParam("id") UUID id, @RequestParam("type") String type) {
		Case caze = caseRepository.getOne(id)
		Patient patient = caze.getPatient()
		List<AdmissionConfiguration> configs = admissionConfigurationRepository.findAll()

		if (configs.size() == 0)
			return "Wristband printer not configured."
		AdmissionConfiguration configuration = configs.first()

		Integer AGE = 0
		String DATEADMITTED = caze.admissionDatetime.toString()
		String DOCTOR = caze?.attendingPhysician?.fullName
		String PATIENTNO = patient.patientNo
		String CASENO = caze?.caseNo
		List<String> allergies = patient.allergies != null ? patient.allergies.split(",").toList() : new ArrayList<String>()
		Integer x = 1
		String allergiesString1 = ""
		String allergiesString2 = ""
		String allergiesString3 = ""
		String myAllergisAll = ""
		Integer last = 3
		if (patient.allergies != null) {
			def allergiez = new JSONObject(patient.allergies)

			String food = allergiez.optString("food")
			def drug = allergiez.optString("drug", "")

			JSONArray foodArray = new JSONArray()
			if (food)
				foodArray = new JSONArray(food)

			if (foodArray.length() > 0) {
				for (i in 0..foodArray.length() - 1) {
					if (myAllergisAll != "") {
						myAllergisAll += ","
					}
					myAllergisAll += foodArray.get(i)

					if (last == 3) {
						if (allergiesString1 != "") {
							allergiesString1 += ","
						}
						last = 1
						allergiesString1 += foodArray.get(i)
					} else if (last == 1) {
						if (allergiesString2 != "") {
							allergiesString2 += ","
						}
						last = 2
						allergiesString2 += foodArray.get(i)
					} else if (last == 2) {
						if (allergiesString3 != "") {
							allergiesString3 += ","
						}
						last = 3
						allergiesString3 += foodArray.get(i)
					}
				}
			}
		}

		/*for(String allergy in myAllergisAll)
		{
				switch (x)
				{
						case 1 :
								if(allergiesString1.size()==0)
										allergiesString1 += allergy
								else
										allergiesString1 += ", " + allergy
								x = 2
						case 2 :
								if(allergiesString2.size()==0)
										allergiesString2 += allergy
								else
										allergiesString2 += ", " + allergy
								x = 3
						case 3 :
								if(allergiesString2.size()==0)
										allergiesString2 += allergy
								else
										allergiesString2 += ", " + allergy
								x = 1
				}
		}*/

		if (myAllergisAll.size() > 0) {
			myAllergisAll = "ALLERGIES : " + myAllergisAll
		}

		def dob = patient.dob
		if (dob != null) {
			// using java.time api and not using Joda
			// c/o albert
			java.time.Period diff = java.time.Period.between(dob, java.time.LocalDate.now())

			//	def period = new Period(new org.joda.time.LocalDateTime(dob), date, PeriodType.yearMonthDay())
			AGE = diff.years
		}
		//Wristband Code for Adult
		String stickerCode =
				$/CT~~CD,~CC^~CT~
            ^XA~TA000~JSN^LT0^MNM^MTT^PON^PMN^LH0,0^JMA^PR2,2~SD2^JUS^LRN^CI0^XZ
            ~DG000.GRF,08064,028,
            ,:::::::::::::::gI07FVF0,::::::::::::::::::::::::::::::::::::::::::::::gG0gSFC0W01FgUFC0V07FgVFC0T0hFC0S0hGFC0R07FhFC0Q0hIFC0P07FhHFC0O01FhIFC0O0hKFC0N07FhJFC0N0hLFC0M01FhKFC0L01FhLFC0:L07FhLFC0K03FhMFC0:K07FhMFC0J01FhNFC0J03FhNFC0J07FhNFC0J0hPFC0I03FhOFC0I07FhOFC0:I0hQFC0H01FhPFC0:H03FhPFC0H07FhPFC0H0hRFC0:::01FhQFC0:03FhQFC0::::::::::::::::::::::::::::::::::03FVFE0H07FVF0,03FTFE0J07FVF0,03FRFC0L07FVF0,03FQFE0M07FVF0,03FQFO07FVF0,03FOFE0O07FVF0,03FOF80O07FVF0,03FOFC0O07FVF0,03FOFE0O07FVF0,03FPFC0N07FVF0,03FQFO07FVF0,03FQF80M07FVF0,03FRFN07FVF0,03FRF80L07FVF0,03FRFE0L07FVF0,03FSF80K07FVF0,03FTFL07FVF0,03FTFC0J07FVF0,03FTFE0J07FVF0,03FUFE0I07FVF0,03FVFJ07FVF0,03FVFC0H07FVF0,03FWF8007FVF0,03FWFE007FVF0,03FXFH07FVF0,03FXFE07FVF0,03FYFC7FVF0,::::::::::::::::::01FYFC7FVF0,:H0gFC7FVF0,:::H07FXFC7FVF0,:H01FXFC0,:I0YFC0W0780,I07FgXF8,I03FgYF,I01FgYFC0,J0hGF8,J07FhF80,J03FhGF8,J01FhGFC,K07FhHF0,K03FhHFE,K03FhIF80,L07FhIFE,L03FhJF80,L01FhKF0,L01FhKFC,M03FhKFC0N0hLFC0:N01FhJFC0O0hKFC0O07FhIFC0O03FhIFC0P07FhHFC0P03FhHFC0Q0hIFC0Q03FhGFC0R0hHFC0R03FhFC0S0hGFC0S03FgYFC0T0hFC0T07FgXFC0U07FgWFC0U03FgWFC0V0gXFC0V01FgVFC0W07FgUFC0W03FgUFC0X0gVFC0X01FgTFC0Y07FgSFC0Y01FgSFC0g03FgRFC0gG0gSFC0gG03FgQFC0gH07FgPFC0gH01FgPFC0gI03FgOFC0gI01FgOFC0gJ01FgNFC0gK07FgMFC0gK01FgMFC0gL01FgLFC0gM07FgKFC0gM01FgKFC0gN07FgJFC0gO03FgIFC0gO01FgIFC0gP03FgHFC0gQ03FgGFC0gQ01FgGFC0gR03FgFC0gS03FYFC0gT07FXFC0gT03FXFC0gU07FWFC0gV07FVFC0gV01FVFC0gW03FUFC0gX03FTFC0gY0UFC0gY01FSFC0h07FRFC0hG03FQFC0hH07FPFC0hH03FPFC0hI01FOFC0hJ03FNFC0hK0OFC0hL07FLFC0hM0MFC0hM03FKFC0hN07FJFC0hO03FIFC0hP0JFC0hP01FHFC0hR0HFC0hR01FC0hS07C0hT0C0,:::::::^XA
            ^MMT
            ^PW300
            ^LL3300
            ^LS0
            ^FT64,2784^XG000.GRF,1,1^FS
            ^FT226,2038^ACB,36,20^FH\^FD$AGE^FS
            ^FT166,2159^ACB,36,20^FH\^FDDOB :^FS
            ^FT226,2158^ACB,36,20^FH\^FDAGE :^FS
            ^FT108,2162^ACB,54,20^FH\^FH^FD${patient.fullName.replace("Ñ", "N")+" / PN : "+PATIENTNO}^FS
            ^FT166,2044^ACB,36,20^FH\^FD$dob^FS
            ^FT287,800^ACB,36,20^FH\^FD$DATEADMITTED^FS
            ^FT235,800^ACB,36,20^FH\^FD- $allergiesString1^FS
            ^FT188,800^ACB,36,20^FH\^FD- $allergiesString2^FS
            ^FT143,800^ACB,36,20^FH\^FD- $allergiesString3^FS
            ^FT96,800^ACB,36,20^FH\^FDALLERGIES^FS
            ^FT59,2470^BQN,2,11
            ^FH\^FDMA,$CASENO^FS
            ^FO61,2474^GB223,0,3^FS
            ^PQ1,0,1,Y^XZ
            ^XA^ID000.GRF^FS^XZ/$

		//Wristband Code for Child
		String stickerCodeChild =
				$/
                \u0010CT~~CD,~CC^~CT~
					^XA~TA000~JSN^LT0^MNM^MTD^PON^PMN^LH0,0^JMA^PR2,2~SD21^JUS^LRN^CI0^XZ
					^XA
					^MMT
					^PW300
					^LL2400
					^LS0
					^FO64,1216^GFA,07168,07168,00028,:Z64:
					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:DC07
					^FT277,975^A0B,33,33^FH\^FD$myAllergisAll^FS
					^FT237,972^A0B,33,33^FH\^FD$DOCTOR ($DATEADMITTED)^FS
					^FT193,971^A0B,33,33^FH\^FDAGE : $AGE^FS
					^FT152,971^A0B,33,33^FH\^FDDOB : $dob^FS
					^FT100,973^A0B,42,40^FH\^FD${patient.fullName.replace("Ñ", "N")+" / PN : "+PATIENTNO}^FS
					^FT63,1241^BQN,2,10
					^FH\^FDMA,$CASENO^FS
					^PQ1,0,1,Y^XZ
				/$

		if (type != "child")
			print(configuration.wristbandPrinterLocation, stickerCode)
		else
			print(configuration.wristbandPrinterLocation, stickerCodeChild)
		return id.toString()
	}

	void print(String link, String data) {
		def httpclient = HttpClients.custom().build()
		HttpPost post = new HttpPost(link)
		ArrayList params = new ArrayList(2)
		String wristband3 = data
		params.add(new BasicNameValuePair("wristband_data", wristband3))
		post.entity = new StringEntity(wristband3)
		def response = httpclient.execute(post)
		HttpHeaders responseHeaders = new HttpHeaders()
		try {
			responseHeaders.set(response.entity.contentType.name, response.entity.contentType.value)
		} catch (Exception e) {
			e.printStackTrace()
		}
	}

	//insert to inventory viewable
	void insertIntoDepItem(String item, String dep) {
		//add to viewable by department
		def depItemObj = departmentItemRepository.findByItemDep(UUID.fromString(item), UUID.fromString(dep))
		if (!depItemObj) {
			DepartmentItem deptItem = new DepartmentItem()
			deptItem.item = itemRepository.findById(UUID.fromString(item)).get()
			deptItem.department = departmentRepository.findById(UUID.fromString(dep)).get()
			deptItem.allow_trade = 0
			deptItem.is_assign = true
			deptItem.reorder_quantity = BigDecimal.ZERO
			departmentItemRepository.save(deptItem)
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = ["/api/masterfile/report"])
	ResponseEntity<AnyDocument.Any> downloadMasterfileItems(
			@RequestParam String category,
			@RequestParam UUID group
	) {

		List<Item> itemList = itemRepository.findAll().sort { it.descLong }

		if (group && !category) {
			itemList = itemRepository.itemsFilterByGroup(group).sort { it.descLong }
		} else if (group && category) {
			String[] el = category.split(",")
			List<UUID> cat = new ArrayList<UUID>()
			el.each {
				it ->
					cat.add(UUID.fromString(it))
			}
			itemList = itemRepository.itemsFilterByCategory(group, cat).sort { it.descLong }
		}

		StringBuffer buffer = new StringBuffer()

		DateTimeFormatter formatter =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
		CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV
				.withHeader("SKU/BARCODE","ITEM CODE", "DESCRIPTION", "UNIT OF PURCHASE", "UNIT OF USAGE", "ITEM CATEGORY", "CONVERSION"))

		try {
			itemList.each {
				item ->
					if (item.active) {
						csvPrinter.printRecord(
								item.sku,
								item.itemCode,
								item.descLong,
								item.unit_of_purchase.unitDescription,
								item.unit_of_usage.unitDescription,
								item.item_category.categoryDescription,
								item.item_conversion

						)
					}
			}

			LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
			extHeaders.add("Content-Disposition", "inline;filename=MasterFile-ItemList-Report.csv")

			return new ResponseEntity(String.valueOf(buffer).getBytes(), extHeaders, HttpStatus.OK)
		}
		catch (e) {
			throw e
		}

	}

	@RequestMapping(method = RequestMethod.GET, value = ["/api/onhand/report"])
	ResponseEntity<AnyDocument.Any> downloadOnhandReport(
			@RequestParam String date,
			@RequestParam UUID departmentid
	) {
		def itemList = this.getOnHandReport(departmentid.toString(), date, '')


		StringBuffer buffer = new StringBuffer()

		CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV
				.withHeader("ITEM DESCRIPTION", "UNIT OF PURCHASE", "UNIT OF USAGE", "ITEM CATEGORY", "EXPIRY DATE", "UNIT COST", "ON HAND QTY", "TOTAL COST"))

		try {
			itemList.each {
				item ->
					csvPrinter.printRecord(
							item.desc_long,
							item.unit_of_purchase,
							item.unit_of_usage,
							item.category_description,
							item.expiration_date,
							item.last_unit_cost,
							item.onhand,
							item.last_unit_cost * item.onhand
					)
			}

			LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
			extHeaders.add("Content-Disposition", "inline;filename=MaterialM-onHand-Report.csv")

			return new ResponseEntity(String.valueOf(buffer).getBytes(), extHeaders, HttpStatus.OK)
		}
		catch (e) {
			throw e
		}

	}

	@RequestMapping(method = RequestMethod.GET, value = ["/api/onhand_status/report"])
	ResponseEntity<AnyDocument.Any> downloadOnhandReportStatus(
			@RequestParam String date,
			@RequestParam UUID departmentid,
			@RequestParam Boolean status
	) {
		def itemList = this.getOnHandReport(departmentid.toString(), date, '', status)

		StringBuffer buffer = new StringBuffer()

		CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV
				.withHeader("ITEM DESCRIPTION", "UNIT OF PURCHASE", "UNIT OF USAGE", "ITEM CATEGORY", "EXPIRY DATE", "UNIT COST", "ON HAND QTY", "TOTAL COST"))

		try {
			itemList.each {
				item ->
					csvPrinter.printRecord(
							item.desc_long,
							item.unit_of_purchase,
							item.unit_of_usage,
							item.category_description,
							item.expiration_date,
							item.last_unit_cost,
							item.onhand,
							item.last_unit_cost * item.onhand
					)
			}

			LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
			extHeaders.add("Content-Disposition", "inline;filename=MaterialM-onHand-Report.csv")

			return new ResponseEntity(String.valueOf(buffer).getBytes(), extHeaders, HttpStatus.OK)
		}
		catch (e) {
			throw e
		}

	}

	@RequestMapping(method = RequestMethod.GET, value = ["/api/expense/report"])
	ResponseEntity<AnyDocument.Any> downloadOnhandReport(
			@RequestParam Instant start,
			@RequestParam Instant end,
			@RequestParam UUID expenseFrom
	) {
		Instant fromDate = start.atZone(ZoneId.systemDefault()).toInstant()
		Instant toDate = end.atZone(ZoneId.systemDefault()).toInstant()
		def itemList = departmentStockIssueItemRepository.getItemExpense(fromDate, toDate, 'Expense', '', expenseFrom)
		StringBuffer buffer = new StringBuffer()

		DateTimeFormatter formatter =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
		CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV
				.withHeader("Date", "EXPENSE NO", "EXPENSE TO", "ITEM", "ITEM CATEGORY", "QTY", "UNIT COST", "TOTAL", "UNIT", "ISSUING DEPT.", "ISSUED BY"))

		try {
			itemList.each {
				item ->
					csvPrinter.printRecord(
							formatter.format(item.stockIssue.issueDate),
							item.stockIssue.issueNo,
							item.stockIssue.issueTo.departmentName,
							item.item.descLong,
							item.item.item_category.categoryDescription,
							item.issueQty,
							item.unitCost,
							item.unitCost * item.issueQty,
							item.item.unit_of_purchase.unitDescription,
							item.stockIssue.issueFrom.departmentName,
							item.stockIssue.issued_by.fullName,

					)
			}

			LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
			extHeaders.add("Content-Disposition", "inline;filename=Expense-Report.csv")

			return new ResponseEntity(String.valueOf(buffer).getBytes(), extHeaders, HttpStatus.OK)
		}
		catch (e) {
			throw e
		}

	}

	@RequestMapping(method = RequestMethod.GET, value = ["/api/physicalViewCount/report"])
	ResponseEntity<AnyDocument.Any> physicalCountViewReport(
			@RequestParam String transDate,
			@RequestParam UUID departmentId
	) {
		LocalDate dateC = LocalDate.parse(transDate)
		Instant instant = dateC.atStartOfDay(ZoneId.of("UTC")).toInstant()
//        Instant dateTrans = transDate.atZone(ZoneId.systemDefault()).toInstant()
		def itemList = physicalCountViewRepository.getPhysicalCountViewByDept(departmentId, instant)
		StringBuffer buffer = new StringBuffer()

		DateTimeFormatter formatter =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
		CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV
				.withHeader("DATE", "DEPARTMENT", "ITEM", "UNIT OF PURCHASE", "UNIT OF USAGE", "CATEGORY", "EXPIRATION DATE", "ONHAND", "MONTHLY COUNT", "VARIANCE", "UNIT COST", "TOTAL COST"))

		try {
			itemList.each {
				item ->
					csvPrinter.printRecord(
							formatter.format(item.dateTrans),
							item.department.departmentName,
							item.descLong,
							item.unit_of_purchase,
							item.unit_of_usage,
							item.category_description,
							item.expiration_date,
							item.onHand,
							item.monthlyCount,
							item.variance,
							item.unitCost?:BigDecimal.ZERO,
							item.unitCost?:BigDecimal.ZERO * item.monthlyCount

					)
			}

			LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
			extHeaders.add("Content-Disposition", "inline;filename=Physical-count-Report.csv")

			return new ResponseEntity(String.valueOf(buffer).getBytes(), extHeaders, HttpStatus.OK)
		}
		catch (e) {
			throw e
		}

	}

	@RequestMapping(method = RequestMethod.GET, value = ["/api/physicalViewCountById/report"])
	ResponseEntity<AnyDocument.Any> physicalCountViewReportById(
			@RequestParam UUID id
	) {

		def itemList = servicePhysicalCountView.getPhysicalItemById(id)
		StringBuffer buffer = new StringBuffer()

		DateTimeFormatter formatter =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
		CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV
				.withHeader("DATE", "DEPARTMENT", "ITEM", "UNIT OF PURCHASE", "UNIT OF USAGE", "CATEGORY", "EXPIRATION DATE", "ONHAND", "MONTHLY COUNT", "VARIANCE", "UNIT COST", "TOTAL COST"))

		try {
			itemList.each {
				item ->
					csvPrinter.printRecord(
							formatter.format(item.dateTrans),
							item.department.departmentName,
							item.descLong,
							item.unit_of_purchase,
							item.unit_of_usage,
							item.category_description,
							item.expiration_date,
							item.onHand,
							item.monthlyCount,
							item.variance,
							item.unitCost?:BigDecimal.ZERO,
							item.unitCost?:BigDecimal.ZERO * item.monthlyCount

					)
			}

			LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
			extHeaders.add("Content-Disposition", "inline;filename=Physical-count-Report.csv")

			return new ResponseEntity(String.valueOf(buffer).getBytes(), extHeaders, HttpStatus.OK)
		}
		catch (e) {
			throw e
		}

	}

	@RequestMapping(method = RequestMethod.GET, value = ["/api/stockCard/report"])
	ResponseEntity<AnyDocument.Any> stockCard(
			@RequestParam String itemId,
			@RequestParam String dep
	) {
		def stockcard = this.getLedger(itemId, dep)
		StringBuffer buffer = new StringBuffer()

		DateTimeFormatter formatter =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
		CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV
				.withHeader("DATE","REF.# ","TRANS. TYPE","ITEM","SOURCE","DESTINATION","QTY IN","QTY OUT","ADJ.","UNIT COST","TOTAL AMOUNT","QTY","W. COST","AMOUNT"))

		try {
			if (stockcard) {
				stockcard.each {
					it ->
						csvPrinter.printRecord(
								it.ledger_date.substring(0,10),
								it.reference_no,
								it.document_desc,
								it.desc_long,
								it.source_department,
								it.dest_department,
								it.ledger_qtyin,
								it.ledger_qty_out,
								it.adjustment,
								it.unitcost,
								(it.ledger_qtyin + it.ledger_qty_out + it.adjustment) * it.unitcost,
								it.runningqty,
								it.wcost,
								it.runningbalance
						)
				}
			}

			LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
			extHeaders.add("Content-Disposition", "inline;filename=Stock-card-Report.csv")

			return new ResponseEntity(String.valueOf(buffer).getBytes(), extHeaders, HttpStatus.OK)
		}
		catch (e) {
			throw e
		}

	}

	//stock card all
	@RequestMapping(method = RequestMethod.GET, value = ["/api/stockCardAll/report"])
	ResponseEntity<AnyDocument.Any> stockCardAll(
			@RequestParam String itemId
	) {

		def stockcard = this.getLedgerAll(itemId)
		StringBuffer buffer = new StringBuffer()

		DateTimeFormatter formatter =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
		CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV
				.withHeader("DATE","REF.# ","TRANS. TYPE","ITEM","SOURCE","DESTINATION","QTY IN","QTY OUT","ADJ.","UNIT COST","TOTAL AMOUNT","QTY","W. COST","AMOUNT"))

		try {
			if (stockcard) {
				stockcard.each {
					it ->
						csvPrinter.printRecord(
								it.ledger_date.substring(0,10),
								it.reference_no,
								it.document_desc,
								it.desc_long,
								it.source_department,
								it.dest_department,
								it.ledger_qtyin,
								it.ledger_qty_out,
								it.adjustment,
								it.unitcost,
								(it.ledger_qtyin + it.ledger_qty_out + it.adjustment) * it.unitcost,
								it.runningqty,
								it.wcost,
								it.runningbalance
						)
				}
			}

			LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
			extHeaders.add("Content-Disposition", "inline;filename=Stock-card-Report.csv")

			return new ResponseEntity(String.valueOf(buffer).getBytes(), extHeaders, HttpStatus.OK)
		}
		catch (e) {
			throw e
		}

	}

//	code ni donss
	@RequestMapping(method = RequestMethod.GET, value = ["/api/issued/report"])
	ResponseEntity<AnyDocument.Any> downloadIssuedReport(
			@RequestParam Instant start,
			@RequestParam Instant end,
			@RequestParam UUID issueFrom
	) {
		Instant fromDate = start.atZone(ZoneId.systemDefault()).toInstant()
		Instant toDate = end.atZone(ZoneId.systemDefault()).toInstant()
		def itemList = departmentStockIssueItemRepository.getItemExpense(fromDate, toDate, 'Stock', '', issueFrom)
		StringBuffer buffer = new StringBuffer()

		DateTimeFormatter formatter =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
		CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV
				.withHeader("Date", "ISSUANCE NO", "ISSUED TO", "ITEM", "ITEM CATEGORY", "QTY", "UNIT COST", "TOTAL", "UNIT", "ISSUING DEPT.", "ISSUED BY"))

		try {
			itemList.each {
				item ->
					csvPrinter.printRecord(
							formatter.format(item.stockIssue.issueDate),
							item.stockIssue.issueNo,
							item.stockIssue.issueTo.departmentName,
							item.item.descLong,
							item.item.item_category.categoryDescription,
							item.issueQty,
							item.unitCost,
							item.unitCost * item.issueQty,
							item.item.unit_of_purchase.unitDescription,
							item.stockIssue.issueFrom.departmentName,
							item.stockIssue.issued_by.fullName,

					)
			}

			LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
			extHeaders.add("Content-Disposition", "inline;filename=Issuance-Report.csv")

			return new ResponseEntity(String.valueOf(buffer).getBytes(), extHeaders, HttpStatus.OK)
		}
		catch (e) {
			throw e
		}

	}


//	code ni dons
	@RequestMapping(method = RequestMethod.GET, value = ["/api/srr/report"])
	ResponseEntity<AnyDocument.Any> downloadSrrReport(
			@RequestParam Instant start,
			@RequestParam Instant end
	) {

		Instant fromDate = start.atZone(ZoneId.systemDefault()).toInstant()
		Instant toDate = end.atZone(ZoneId.systemDefault()).toInstant()
		def itemList = receivingReportRepository.getSrrByDateRange(fromDate,toDate,'')
		StringBuffer buffer = new StringBuffer()

		DateTimeFormatter formatter =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
		CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV
				.withHeader("SRR NO","RECEIVING DATE","PO NUMBER","REFERENCE NUMBER","REFERENCE DATE","RECEIVING DEPARTMENT","SUPPLIER","PAYMENT Terms","GROSS AMOUNT","TOTAL DISCOUNT","NET OF DISCOUNT","AMOUNT","INPUT TAX","NET AMOUNT","RECEIVE BY"))

		try {
			itemList.each {
				item ->
					csvPrinter.printRecord(
							item.rrNo,
							formatter.format(item.receiveDate),
							item.purchaseOrder?.poNumber,
							item.receivedRefNo,
							formatter.format(item.receivedRefDate),
							item.receiveDepartment?.departmentName,
							item.supplier?.supplierFullname,
							item.paymentTerms?.paymentDesc,
							item.grossAmount,
							item.totalDiscount,
							item.netDiscount,
							item.amount,
							item.inputTax,
							item.netAmount,
							item.userFullname,

					)
			}

			LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
			extHeaders.add("Content-Disposition", "inline;filename=SRR-Report.csv")

			return new ResponseEntity(String.valueOf(buffer).getBytes(), extHeaders, HttpStatus.OK)
		}
		catch (e) {
			throw e
		}

	}

	//	code ni dons
	@RequestMapping(method = RequestMethod.GET, value = ["/api/srrItem/report"])
	ResponseEntity<AnyDocument.Any> downloadSrrItemReport(
			@RequestParam Instant start,
			@RequestParam Instant end
	) {

		Instant fromDate = start.atZone(ZoneId.systemDefault()).toInstant()
		Instant toDate = end.atZone(ZoneId.systemDefault()).toInstant()
		def itemList = receivingReportItemRepository.getSrrItemByDateRange(fromDate,toDate,'')
		StringBuffer buffer = new StringBuffer()

		DateTimeFormatter formatter =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
		CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV
				.withHeader("SRR NO","RECEIVING DATE","PO NUMBER","REFERENCE NUMBER","REFERENCE DATE","RECEIVING DEPARTMENT","SUPPLIER","ITEM","ITEM CATEGORY","UNIT COST","DISCOUNT","QUANTITY","TAX","TOTAL AMOUNT","NET AMOUNT"))

		try {
			itemList.each {
				item ->
					csvPrinter.printRecord(
							item.receivingReport?.rrNo,
							formatter.format(item.receivingReport?.receiveDate),
							item.receivingReport?.purchaseOrder?.poNumber,
							item.receivingReport?.receivedRefNo,
							formatter.format(item.receivingReport?.receivedRefDate),
							item.receivingReport?.receiveDepartment?.departmentName,
							item.receivingReport?.supplier?.supplierFullname,
							item.item.descLong,
							item.item?.item_category?.categoryDescription,
							item.receiveUnitCost,
							item.receiveDiscountCost,
							item.receiveQty,
							item.inputTax,
							item.totalAmount,
							item.netAmount
					)
			}

			LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
			extHeaders.add("Content-Disposition", "inline;filename=SRR-Item-Report.csv")

			return new ResponseEntity(String.valueOf(buffer).getBytes(), extHeaders, HttpStatus.OK)
		}
		catch (e) {
			throw e
		}

	}

	@RequestMapping(method = RequestMethod.GET, value = ["/api/item-price-control/csv-download"])
	ResponseEntity<AnyDocument.Any> downloadItemPriceControl(
			@RequestParam String tierId,
			@RequestParam String group,
			@RequestParam String costGroup,
			@RequestParam String filter
	) {
		try {

			List<ItemDto> allItems
			StringBuffer buffer = new StringBuffer()
			DateTimeFormatter formatter =
					DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
			CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV
					.withHeader("Desc long","Actual unit cost","Base price","Percentage value","Margin","Total markup","Amount value"))

			if (costGroup && costGroup.toUpperCase() != "ALL") {
				def cg = costGroup.split("-")

				def from = cg[0]
				def to = cg[1]

				if (to.toLowerCase() == "up") {
					to = 99999999.0
				}

				from = from as BigDecimal
				to = to as BigDecimal

				if (group == "MEDICINES")
					allItems = itemService.itemsByCostRange(from, to, true, filter)
				else if (group == "SUPPLIES")
					allItems = itemService.itemsByCostRange(from, to, false, filter)
				else if (group == "MEDICAL SUPPLIES")
					allItems = itemService.itemsByCostRangeMedicalSupplies(from, to, filter)
				else
					allItems = itemService.itemsByCostRange(from, to, filter)
			} else {
				if (group == "MEDICINES")
					allItems = itemService.allItemsByType(true, filter)
				else if (group == "SUPPLIES")
					allItems = itemService.allItemsByType(false, filter)
				else if (group == "MEDICAL SUPPLIES")
					allItems = itemService.allItemsMedicalSuppliesByType(filter)
				else
					allItems = itemService.allItemsNativeQuery(filter)
			}

			List<ItemPriceControlDto> allItemPrice = []

			if (tierId) {
				PriceTierDetail tier = priceTierDetailRepository.findById(UUID.fromString(tierId)).get()

				allItems.each {
					it ->
						ItemPriceControl item = itemPriceControlRepository.getItemByIdAndTier(
								UUID.fromString(tierId),
								UUID.fromString(it.id)
						)

						if (it.active) {
							if (item) {
								ItemPriceControlDto ipcDto = new ItemPriceControlDto()
								Item i = new Item()
								i.id = UUID.fromString(it.id)
								i.descLong = it.descLong
								i.actualUnitCost = it.actualUnitCost
								i.item_markup = it.item_markup
								i.vatable = it.vatable

								ipcDto.item = i

								def unitCost = it.actualUnitCost ?: 0.0
								def basePrice = it.item_markup ? unitCost + (unitCost * (it.item_markup / 100)) : unitCost
								ipcDto.basePrice = basePrice ?: 0.0
								def sellPrice = 0.0

								if (item.amountValue > 0 && basePrice != 0.0) {
									if (tier.isVatable && (it.vatable && tier.vatRate)) {
										sellPrice = item.amountValue + (item.amountValue * (tier.vatRate / 100)) ?: 0.0
										ipcDto.percentageValue = item.percentageValue ?: 0.0
										ipcDto.amountValue = sellPrice
									} else {
										sellPrice = item.amountValue ?: 0.0
										ipcDto.percentageValue = item.percentageValue ?: 0.0
										ipcDto.amountValue = sellPrice
									}

									ipcDto.addon = ((sellPrice - basePrice) / basePrice) * 100
									ipcDto.margin = sellPrice - it.actualUnitCost
									ipcDto.totalMarkup = ((sellPrice - it.actualUnitCost) / it.actualUnitCost) * 100
									ipcDto.locked = item.locked
								} else {
									ipcDto.addon = 0.0
									ipcDto.percentageValue = 0.0
									ipcDto.amountValue = 0.0
									ipcDto.margin = 0.0
									ipcDto.totalMarkup = 0.0
									ipcDto.locked = item.locked
								}

								ipcDto.tierDetail = tier

								csvPrinter.printRecord(
										it.descLong,
										it.actualUnitCost,
										ipcDto.basePrice,
										ipcDto.percentageValue,
										ipcDto.margin,
										ipcDto.totalMarkup,
										ipcDto.amountValue
								)
							} else {
								ItemPriceControlDto ipcDto = new ItemPriceControlDto()
								Item i = new Item()
								i.id = UUID.fromString(it.id)
								i.descLong = it.descLong
								i.actualUnitCost = it.actualUnitCost
								i.item_markup = it.item_markup
								i.vatable = it.vatable

								ipcDto.item = i

								def unitCost = it.actualUnitCost ?: 0.0
								def basePrice = it.item_markup ? unitCost + (unitCost * (it.item_markup / 100)) : unitCost

								ipcDto.basePrice = basePrice
								ipcDto.percentageValue = 0.0
								ipcDto.amountValue = basePrice
								ipcDto.addon = 0.0
								ipcDto.margin = 0.0
								ipcDto.totalMarkup = 0.0
								ipcDto.locked = false

								ipcDto.tierDetail = null

								csvPrinter.printRecord(
										it.descLong,
										it.actualUnitCost,
										ipcDto.basePrice,
										ipcDto.percentageValue,
										ipcDto.margin,
										ipcDto.totalMarkup,
										ipcDto.amountValue
								)
							}
						}
				}
			}


			LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
			extHeaders.add("Content-Disposition", "inline;filename=Item-Price-Control.csv")

			return new ResponseEntity(String.valueOf(buffer).getBytes(), extHeaders, HttpStatus.OK)
		}
		catch (e) {
			throw e
		}

	}

	//download markup
	@RequestMapping(method = RequestMethod.GET, value = ["/api/markup/report"])
	ResponseEntity<AnyDocument.Any> downloadMarkup(
			@RequestParam String category,
			@RequestParam UUID group
	) {
		List<UUID> cat = new ArrayList<UUID>()
		if(category){
			String[] el = category.split(",")
			el.each {
				it ->
					cat.add(UUID.fromString(it))
			}
		}

		List<Markup> itemList = markupPageableService.markupItemList('', group, cat)

		StringBuffer buffer = new StringBuffer()

		DateTimeFormatter formatter =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
		CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV
				.withHeader("SKU/BARCODE","ITEM CODE", "DESCRIPTION", "UNIT OF PURCHASE", "UNIT OF USAGE","ITEM GROUP", "ITEM CATEGORY", "LAST UNIT COST", "ACTUAL UNIT COST", "MARKUP (%)", "BASE PRICE"))

		try {
			itemList.each {
				item ->
					if (item.active) {
						def basePrice = item.actualUnitCost?:BigDecimal.ZERO + (item.actualUnitCost?:BigDecimal.ZERO * (item.item_markup?:BigDecimal.ZERO / 100))
						csvPrinter.printRecord(
								item.sku,
								item.itemCode,
								item.descLong,
								item.unit_of_purchase.unitDescription,
								item.unit_of_usage.unitDescription,
								item.item_group.itemDescription,
								item.item_category.categoryDescription,
								item.lastUnitCost?:BigDecimal.ZERO,
								item.actualUnitCost?:BigDecimal.ZERO,
								item.item_markup?:BigDecimal.ZERO,
								basePrice
						)
					}
			}

			LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
			extHeaders.add("Content-Disposition", "inline;filename=Markup-Price-Control.csv")

			return new ResponseEntity(String.valueOf(buffer).getBytes(), extHeaders, HttpStatus.OK)
		}
		catch (e) {
			throw e
		}

	}

	@RequestMapping(method = RequestMethod.GET, value = ["/api/detailed-sales/csv"])
	ResponseEntity<AnyDocument.Any> downloadSalesDetailed(
			@RequestParam String type,
			@RequestParam String start,
			@RequestParam String end
	) {
		def itemList = billingItemServices.listSalesReportDetailed(type, start, end)

		StringBuffer buffer = new StringBuffer()

		DateTimeFormatter formatter =
				DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss").withZone(ZoneId.systemDefault())
		CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV
				.withHeader("Transaction Date",
						"Bill No",
						"Billing Item Reference No",
						"Barcode", "Item Code",
						"Service Code",
						"Description",
						"Department",
						"Qty", "Amount", "Total Amount"))
		try {
			itemList.each {
				item ->
					csvPrinter.printRecord(
							item.transaction_date,
							item.folio_no,
							item.billing_reference_no,
							item.barcode,
							item.item_code,
							item.service_code,
							item.description,
							item.department,
							item.qty,
							item.amount,
							item.total_amount
					)
			}

			LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
			extHeaders.add("Content-Disposition", "inline;filename=salesReportDetailed.csv")

			return new ResponseEntity(String.valueOf(buffer).getBytes(), extHeaders, HttpStatus.OK)
		} catch (e) {
			throw e
		}

	}

	//code ni wilson
	@RequestMapping(method = RequestMethod.GET, value = ["/api/current/inventory"])
	ResponseEntity<AnyDocument.Any> downloadCurrentInventory(
			@RequestParam String dep,
			@RequestParam(required = false) String group,
			@RequestParam(required = false) String category,
			@RequestParam(required = false) String status,
			@RequestParam(required = false) Boolean consignment
	) {
		def groupId = group ? UUID.fromString(group) : null
		def active = status ? status : null
		List<UUID> cat = new ArrayList<UUID>()
		if(category){
			String[] el = category.split(",")
			el.each {
				it ->
					cat.add(UUID.fromString(it))
			}
		}
		List<Inventory> itemList = inventoryPageableService.inventoryListByDepStatus("", UUID.fromString(dep), groupId, cat, active, consignment)

		StringBuffer buffer = new StringBuffer()

		CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV
				.withHeader("ID", "SKU/BARCODE","ITEM CODE", "DESCRIPTION", "UNIT OF PURCHASE", "UNIT OF USAGE", "ITEM CATEGORY", "ITEM BRAND", "ITEM DFS", "ITEM GENERIC NAME", "CONVERSION", "LAST UNITCOST","CURRENT ONHAND QTY", "STATUS"))

		try {
			itemList.each {
				item ->
					if (item.active) {
						csvPrinter.printRecord(
								item.id,
								item.sku,
								item.itemCode,
								item.descLong,
								item.item.unit_of_purchase.unitDescription,
								item.item.unit_of_usage.unitDescription,
								item.item.item_category.categoryDescription,
								item.item.brand,
								item.item.item_dfs,
								item.item.item_generics.genericDescription,
								item.item.item_conversion,
								item.lastUnitCost,
								item.onHand,
								item.status

						)
					}
			}

			LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
			extHeaders.add("Content-Disposition", "inline;filename=Current-Inventory-Report.csv")

			return new ResponseEntity(String.valueOf(buffer).getBytes(), extHeaders, HttpStatus.OK)
		}
		catch (e) {
			throw e
		}

	}

	//supplier barcode
	SupplierBarcodeItemDto supplierBarcode(
			@RequestParam("supplier") UUID supplier,
			@RequestParam("sku") String sku
	) {

		String sql = "select * from inventory.barcode_supplier_item('${supplier}', '${sku}');"
		SupplierBarcodeDto item = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper(SupplierBarcodeDto.class))
		if(item){
			def returnItem = new SupplierBarcodeItemDto(
					id: item.id,
					item: itemRepository.findById(item.item).get(),
					unitCost: item.unitCost
			)
			return returnItem
		}else{
			return null
		}
	}

	IssuanceBarcodeItemDto issuanceBarcode(
			@RequestParam("department") UUID department,
			@RequestParam("sku") String sku
	) {

		String sql = "select * from inventory.barcode_issuance_item('${department}', '${sku}');"
		IssuanceBarcodeDto item = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper(IssuanceBarcodeDto.class))
		if(item){
			def e = itemRepository.findById(item.item).get()
			def returnItem = new IssuanceBarcodeItemDto(
					id: item.id,
					item: e,
					wcost: item.wcost,
					itemCategory: e.item_category.categoryDescription
			)
			return returnItem
		}else{
			return null
		}
	}

	//download PR Items
	@RequestMapping(method = RequestMethod.GET, value = ["/api/prItem/csv"])
	ResponseEntity<AnyDocument.Any> downloadPRtemReport(
			@RequestParam Instant start,
			@RequestParam Instant end
	) {

		Instant fromDate = start.atZone(ZoneId.systemDefault()).toInstant()
		Instant toDate = end.atZone(ZoneId.systemDefault()).toInstant()
		def itemList = purchaseRequestItemRepository.getPRItemByDateRange(fromDate,toDate,'')
		StringBuffer buffer = new StringBuffer()

		DateTimeFormatter formatter =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
		CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV
				.withHeader("PR NO","PR DATE","DEPARTMENT","SUPPLIER","ITEM","ITEM CATEGORY","QUANTITY","UNIT OF PURCHASE"))

		try {
			itemList.each {
				item ->
					csvPrinter.printRecord(
							item.purchaseRequest?.prNo,
							formatter.format(item.purchaseRequest?.prDateRequested),
							item.purchaseRequest?.requestingDepartment?.departmentName,
							item.purchaseRequest?.supplier?.supplierFullname,
							item.item.descLong,
							item.item?.item_category?.categoryDescription,
							item.requestedQty,
							item.item?.unit_of_purchase?.unitDescription,
					)
			}

			LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
			extHeaders.add("Content-Disposition", "inline;filename=PR-Item-Report.csv")

			return new ResponseEntity(String.valueOf(buffer).getBytes(), extHeaders, HttpStatus.OK)
		}
		catch (e) {
			throw e
		}

	}

	@RequestMapping(method = RequestMethod.GET, value = ["/api/poItem/csv"])
	ResponseEntity<AnyDocument.Any> downloadPOtemReport(
			@RequestParam Instant start,
			@RequestParam Instant end
	) {

		LocalDateTime fromDate = start.atZone(ZoneId.systemDefault()).toLocalDateTime()
		LocalDateTime toDate = end.atZone(ZoneId.systemDefault()).toLocalDateTime()
		def itemList = purchaseOrderItemRepository.getPOItemByDateRange(fromDate,toDate,'')
		StringBuffer buffer = new StringBuffer()

		DateTimeFormatter formatter =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
		CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV
				.withHeader("PO NO","PO DATE","DEPARTMENT","SUPPLIER","ITEM","ITEM CATEGORY","BRAND","QUANTITY","UNIT OF PURCHASE", "UNIT COST"))

		try {
			itemList.each {
				item ->
					csvPrinter.printRecord(
							item.purchaseOrder?.poNumber,
							formatter.format(item.purchaseOrder?.preparedDate),
							item.purchaseOrder?.departmentFrom?.departmentName,
							item.purchaseOrder?.supplier?.supplierFullname,
							item.item.descLong,
							item.item?.item_category?.categoryDescription,
							item.item?.brand,
							item.quantity,
							item.item?.unit_of_purchase?.unitDescription,
							item.quantity * item.supplierLastPrice,
					)
			}

			LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
			extHeaders.add("Content-Disposition", "inline;filename=PR-Item-Report.csv")

			return new ResponseEntity(String.valueOf(buffer).getBytes(), extHeaders, HttpStatus.OK)
		}
		catch (e) {
			throw e
		}

	}
}
