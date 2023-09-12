package com.hisd3.hismk2.rest

import com.google.gson.Gson
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.inventory.*
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.inventory.InventoryLedgerService
import com.hisd3.hismk2.graphqlservices.inventory.SignatureService
import com.hisd3.hismk2.graphqlservices.inventoryv2.ServicePhysicalCountView
import com.hisd3.hismk2.graphqlservices.inventoryv2.ServicePhysicalTransaction
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.hospital_config.HospitalInfoRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.inventory.*
import com.hisd3.hismk2.rest.dto.*
import com.hisd3.hismk2.security.SecurityUtils
import groovy.transform.TypeChecked
import net.sf.jasperreports.engine.JRException
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import net.sf.jasperreports.engine.data.JsonDataSource
import net.sf.jasperreports.engine.export.JRPdfExporter
import net.sf.jasperreports.export.SimpleExporterInput
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput
import net.sf.jasperreports.export.SimplePdfExporterConfiguration
import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@TypeChecked
@RestController
@RequestMapping('/reports/inventory/print')
class InventoryReportResource {
	
	@Autowired
	ApplicationContext applicationContext
	
	@Autowired
	PurchaseOrderRepository purchaseOrderRepository
	
	@Autowired
	PurchaseRequestRepository purchaseRequestRepository
	
	@Autowired
	PurchaseOrderItemRepository purchaseOrderItemRepository
	
	@Autowired
	PurchaseRequestItemRepository purchaseRequestItemRepository
	
	@Autowired
	ReceivingReportRepository receivingReportRepository
	
	@Autowired
	ReceivingReportItemRepository receivingReportItemRepository
	
	@Autowired
	EmployeeRepository employeeRepository
	
	@Autowired
	InventoryLedgerRepository inventoryLedgerRepository
	
	@Autowired
	HospitalInfoRepository hospitalInfoRepository
	
	@Autowired
	InventoryLedgerService inventoryLedgerService
	
	@Autowired
	ItemRepository itemRepository
	
	@Autowired
	InventoryResource inventoryResource
	
	@Autowired
	DepartmentRepository departmentRepository
	
	@Autowired
	PhysicalCountRepository physicalCountRepository

	@Autowired
	ServicePhysicalCountView servicePhysicalCountView

	@Autowired
	ServicePhysicalTransaction servicePhysicalTransaction
	
	@Autowired
	ReturnSupplierRepository returnSupplierRepository
	
	@Autowired
	ReturnSupplierItemRepository returnSupplierItemRepository

	@Autowired
	SignatureService signatureService

	@Autowired
	SignatureRepository signatureRepository

	@Autowired
	HospitalConfigService hospitalConfigService

	@Autowired
	DepartmentStockIssueRepository departmentStockIssueRepository

	@Autowired
	DepartmentStockIssueItemRepository departmentStockIssueItemRepository

	
	@RequestMapping(value = ['/po_report/{id}'], produces = ['application/pdf'])
	ResponseEntity<byte[]> poReport(@PathVariable('id') UUID id) {
		
		PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id).get()
		List<PurchaseOrderItems> purchaseOrderItems = purchaseOrderItemRepository.findByPurchaseOrderId(id).sort {
			it.item.descLong
		}
		Employee emp = employeeRepository.findByUsername(purchaseOrder?.createdBy).first()
		Employee currentEmp = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()

		def res = applicationContext?.getResource("classpath:/reports/inventory/po_report.jasper")
		def bytearray = new ByteArrayInputStream()
		def os = new ByteArrayOutputStream()
		def parameters = [:] as Map<String, Object>
		def logo = applicationContext?.getResource("classpath:/reports/logo.png")
		def itemsDto = new ArrayList<POItemReportDto>()
		
		DateTimeFormatter dateFormat =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
		
		def dto = new POReportDto(
				date: dateFormat.format(purchaseOrder?.preparedDate),
				poNum: purchaseOrder?.poNumber,
				prNum: purchaseOrder?.prNos,
				supplier: purchaseOrder?.supplier?.supplierFullname,
				department: purchaseOrder?.departmentFrom?.departmentName,
				terms: purchaseOrder?.paymentTerms?.paymentDesc,
				fullname: emp?.fullName,
				title: purchaseOrder?.consignment ? "CONSIGNMENT PURCHASE ORDER" : "PURCHASE ORDER"
		
		)
		def gson = new Gson()
		def dataSourceByteArray = new ByteArrayInputStream(gson.toJson(dto).bytes)
		def dataSource = new JsonDataSource(dataSourceByteArray)
		
		if (purchaseOrderItems) {
			def counter = 1
			purchaseOrderItems.each {
				it ->

					def discount = 0.00
					def deals = ''
					def total = 0.00
					if(it.type)
					{
						if(it.type.equalsIgnoreCase('discountRate')) {
							def discount_rate = (it.type_text as BigDecimal) / 100
							discount = (it.supplierLastPrice * it.quantity) * discount_rate

							if (discount_rate > 0) {
								deals = it.type_text + ' %'
								total = (it.supplierLastPrice * it.quantity) - discount
							} else {
								deals = '-------'
								total = it.supplierLastPrice * it.quantity
							}
						}
						else if(it.type.equalsIgnoreCase('discountAmount')){
							deals = '-------'
							discount = it.type_text
							total = (it.supplierLastPrice * it.quantity) - (it.type_text as BigDecimal)
						}
						else if(it.type.equalsIgnoreCase('package')) {
							deals = it.type_text
							discount = 0.00
							total = (it.supplierLastPrice * it.quantity)
						}
						else {
							deals = '-------'
							discount = 0.00
							total = (it.supplierLastPrice * it.quantity)
						}
					}else{
						discount = 0.00
						deals = '-------'
						total = it.supplierLastPrice * it.quantity
					}

					def itemDto = new POItemReportDto(
							description: it.item.descLong,
							uom: it.item.unit_of_purchase.unitDescription + '(' + it.item.item_conversion + ')',
							deals: deals,
							discount: discount as BigDecimal,
							no: counter,
							request_qty: it.quantity,
							unit_cost: it.supplierLastPrice,
							total: total,

					)
					itemsDto.add(itemDto)
					counter = counter + 1
			}
		}

		List<Signature> signList = signatureService.signatureList("PO").sort({it.sequence})
		def signColumn1 = new ArrayList<SignatureReportDto>()
		def signColumn2 = new ArrayList<SignatureReportDto>()

		if (signList) {
			Integer count = 1
			signList.each {
				it ->
					def signData

					if(it.currentUsers){
						signData = new SignatureReportDto(
								signatureHeader: it.signatureHeader,
								signaturies: currentEmp.fullName,
								position: it.signaturePosition,
						)
					}
					else{
						signData = new SignatureReportDto(
								signatureHeader: it.signatureHeader,
								signaturies: (it.signaturePerson != null) ? it.signaturePerson : "",
								position: it.signaturePosition,
						)
					}

					if(count == 1)
					{
						signColumn1.add(signData)
					} else if(count == 2)
					{
						signColumn2.add(signData)
						count = 0
					}

					count++
			}
		}

		if (signColumn1) {
			parameters.put('sign_column1', new JRBeanCollectionDataSource(signColumn1))
		}
		if (signColumn2) {
			parameters.put('sign_column2', new JRBeanCollectionDataSource(signColumn2))
		}

		if (logo.exists()) {
			parameters.put("logo", logo?.getURL())
		}
		
		if (itemsDto) {
			parameters.put('items', new JRBeanCollectionDataSource(itemsDto))
		}

		if(hospitalConfigService?.hospitalInfo){
			String nameWithAddress = ""
			String address = ""
			String address2 = ""

			address +=  hospitalConfigService?.hospitalInfo?.address
			address +=  hospitalConfigService?.hospitalInfo?.addressLine2

			if(hospitalConfigService?.hospitalInfo?.hospitalName) nameWithAddress += hospitalConfigService?.hospitalInfo?.hospitalName + " "
			if(hospitalConfigService?.hospitalInfo?.city) address2 += hospitalConfigService?.hospitalInfo?.city + ", "
			if(hospitalConfigService?.hospitalInfo?.street) address2 += hospitalConfigService?.hospitalInfo?.street + ", "
			if(hospitalConfigService?.hospitalInfo?.zip) address2 += hospitalConfigService?.hospitalInfo?.zip

			nameWithAddress += address

			parameters.put('hospitalAddress', address)
			parameters.put('hospitalAddress2', address2)
			parameters.put('hospitalEmail', hospitalConfigService?.hospitalInfo?.email)
			parameters.put('hospitalName', nameWithAddress?:"")
			parameters.put('hospitalContactNo', hospitalConfigService?.hospitalInfo?.telNo?:"")

		}
		
		//printing
		try {
			def jrprint = JasperFillManager.fillReport(res.inputStream, parameters, dataSource)
			
			def pdfExporter = new JRPdfExporter()
			
			def outputStreamExporterOutput = new SimpleOutputStreamExporterOutput(os)
			
			pdfExporter.setExporterInput(new SimpleExporterInput(jrprint))
			pdfExporter.setExporterOutput(outputStreamExporterOutput)
			def configuration = new SimplePdfExporterConfiguration()
			pdfExporter.setConfiguration(configuration)
			pdfExporter.exportReport()
			
		} catch (JRException e) {
			e.printStackTrace()
		} catch (IOException e) {
			e.printStackTrace()
		}
		
		if (bytearray != null)
			IOUtils.closeQuietly(bytearray)
		//end
		
		def data = os.toByteArray()
		def params = new LinkedMultiValueMap<String, String>()
		//params.add("Content-Disposition", "inline;filename=Discharge-Instruction-of-\"" + caseDto?.patient?.fullName + "\".pdf")
		params.add("Content-Disposition", "inline;filename=PO-Report-of-\"" + purchaseOrder?.poNumber + "\".pdf")
		return new ResponseEntity(data, params, HttpStatus.OK)
	}
	
	//pr print
	@RequestMapping(value = ['/pr_report/{id}'], produces = ['application/pdf'])
	ResponseEntity<byte[]> prReport(@PathVariable('id') UUID id) {
		PurchaseRequest purchaseRequest = purchaseRequestRepository.findById(id).get()
		List<PurchaseRequestItem> purchaseRequestItems = purchaseRequestItemRepository.getByPrId(id).sort {
			it.item.descLong
		}
		Employee emp = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()

		def res = applicationContext?.getResource("classpath:/reports/inventory/pr_report.jasper")
		def bytearray = new ByteArrayInputStream()
		def os = new ByteArrayOutputStream()
		def parameters = [:] as Map<String, Object>
		def logo = applicationContext?.getResource("classpath:/reports/logo.png")
		def itemsDto = new ArrayList<PRItemReportDto>()


		DateTimeFormatter dateFormat =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
		def dto = new PRReportDto(
				prNo: purchaseRequest?.prNo,
				date: dateFormat.format(purchaseRequest?.prDateRequested),
				supplier: purchaseRequest?.supplier?.supplierFullname ? purchaseRequest?.supplier?.supplierFullname : '',
				fullname: purchaseRequest?.userFullname,
				title: purchaseRequest.consignment ? "CONSIGNMENT PURCHASE REQUEST FORM" : "PURCHASE REQUEST FORM"
		
		)
		def gson = new Gson()
		def dataSourceByteArray = new ByteArrayInputStream(gson.toJson(dto).bytes)
		def dataSource = new JsonDataSource(dataSourceByteArray)
		
		if (purchaseRequestItems) {
			purchaseRequestItems.each {
				it ->
					Inventory inv = inventoryLedgerRepository.getOnHandByItem(emp.departmentOfDuty.id, it.item.id)
					def itemDto = new PRItemReportDto(
							description: it.item.descLong,
							brand: it.item.brand,
							uop: it.item.unit_of_purchase.unitDescription,
							uou: it.item.unit_of_usage.unitDescription,
							content_ratio: it.item.item_conversion,
							qty_uop: it.requestedQty,
							qty_uou: it.requestedQty * it.item.item_conversion,
							onhand: it.onHandQty ? it.onHandQty : inv?.onHand,
							reorder: inv?.reOrderQty ? inv?.reOrderQty : 0
					)
					itemsDto.add(itemDto)
			}
		}

		List<Signature> signList = signatureService.signatureList("PR").sort({it.sequence})
		def signColumn1 = new ArrayList<SignatureReportDto>()
		def signColumn2 = new ArrayList<SignatureReportDto>()
		def signColumn3 = new ArrayList<SignatureReportDto>()

		if (signList) {
			Integer count = 1
			signList.each {
				it ->
					def signData
					if(it.currentUsers){
						signData = new SignatureReportDto(
								signatureHeader: it.signatureHeader,
								signaturies: emp.fullName,
								position: it.signaturePosition,
						)
					}
					else{
						signData = new SignatureReportDto(
								signatureHeader: it.signatureHeader,
								signaturies: (it.signaturePerson != null) ? it.signaturePerson : "",
								position: it.signaturePosition,
						)
					}

					if(count == 1)
					{
						signColumn1.add(signData)
					} else if(count == 2)
					{
						signColumn2.add(signData)
					} else if(count == 3)
					{
						signColumn3.add(signData)
						count = 0

					}
					count++
			}
		}

		if (signColumn1) {
			parameters.put('sign_column1', new JRBeanCollectionDataSource(signColumn1))
		}
		if (signColumn2) {
			parameters.put('sign_column2', new JRBeanCollectionDataSource(signColumn2))
		}
		if (signColumn3) {
			parameters.put('sign_column3', new JRBeanCollectionDataSource(signColumn3))
		}

		if (logo.exists()) {
			parameters.put("logo", logo?.getURL())
		}
		
		if (itemsDto) {
			parameters.put('items', new JRBeanCollectionDataSource(itemsDto))
		}

		if(hospitalConfigService?.hospitalInfo){
			String nameWithAddress = ""
			String address = ""
			String address2 = ""

			address +=  hospitalConfigService?.hospitalInfo?.address
			address +=  hospitalConfigService?.hospitalInfo?.addressLine2

			if(hospitalConfigService?.hospitalInfo?.hospitalName) nameWithAddress += hospitalConfigService?.hospitalInfo?.hospitalName + " "
			if(hospitalConfigService?.hospitalInfo?.city) address2 += hospitalConfigService?.hospitalInfo?.city + ", "
			if(hospitalConfigService?.hospitalInfo?.street) address2 += hospitalConfigService?.hospitalInfo?.street + ", "
			if(hospitalConfigService?.hospitalInfo?.zip) address2 += hospitalConfigService?.hospitalInfo?.zip

			nameWithAddress += address

			parameters.put('hospitalAddress', address)
			parameters.put('hospitalAddress2', address2)
			parameters.put('hospitalEmail', hospitalConfigService?.hospitalInfo?.email)
			parameters.put('hospitalName', nameWithAddress?:"")
			parameters.put('hospitalContactNo', hospitalConfigService?.hospitalInfo?.telNo?:"")

		}

		//printing
		try {
			def jrprint = JasperFillManager.fillReport(res.inputStream, parameters, dataSource)
			
			def pdfExporter = new JRPdfExporter()
			
			def outputStreamExporterOutput = new SimpleOutputStreamExporterOutput(os)
			
			pdfExporter.setExporterInput(new SimpleExporterInput(jrprint))
			pdfExporter.setExporterOutput(outputStreamExporterOutput)
			def configuration = new SimplePdfExporterConfiguration()
			pdfExporter.setConfiguration(configuration)
			pdfExporter.exportReport()
			
		} catch (JRException e) {
			e.printStackTrace()
		} catch (IOException e) {
			e.printStackTrace()
		}
		
		if (bytearray != null)
			IOUtils.closeQuietly(bytearray)
		//end
		
		def data = os.toByteArray()
		def params = new LinkedMultiValueMap<String, String>()
		//params.add("Content-Disposition", "inline;filename=Discharge-Instruction-of-\"" + caseDto?.patient?.fullName + "\".pdf")
		params.add("Content-Disposition", "inline;filename=PR-Report-of-\"" + purchaseRequest?.prNo + "\".pdf")
		return new ResponseEntity(data, params, HttpStatus.OK)
	}
	//wilson report
	@RequestMapping(value = ['/receiving_report/{id}'], produces = ['application/pdf'])
	ResponseEntity<byte[]> reReport(@PathVariable('id') UUID id) {
		//query
		def receiving = receivingReportRepository.findById(id).get()
		def receivingItem = receivingReportItemRepository.findItemsByReceivingReportId(id).sort { it.item.descLong }
		
		def res = applicationContext?.getResource("classpath:/reports/inventory/receiving_report.jasper")
		def bytearray = new ByteArrayInputStream()
		def os = new ByteArrayOutputStream()
		def parameters = [:] as Map<String, Object>
		def logo = applicationContext?.getResource("classpath:/reports/logo.png")
		def itemsDto = new ArrayList<ReceivingReportItemDto>()
		
		DateTimeFormatter dateFormat =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
		def dto = new ReceivingReportDto(
				srrNo: receiving?.rrNo,
				date: dateFormat.format(receiving?.receiveDate),
				poNo: receiving?.purchaseOrder?.poNumber ? receiving?.purchaseOrder?.poNumber : '',
				refNo: receiving.receivedRefNo,
				supplier: receiving?.supplier?.supplierFullname,
				remarks: receiving?.receivedRemarks ? receiving?.receivedRemarks : '',
				title: receiving?.consignment ? "CONSIGNMENT STOCK RECEIVING REPORT" : "STOCK RECEIVING REPORT"
		)
		def gson = new Gson()
		def dataSourceByteArray = new ByteArrayInputStream(gson.toJson(dto).bytes)
		def dataSource = new JsonDataSource(dataSourceByteArray)
		
		if (receivingItem) {
			receivingItem.each {

				it ->
					def total = it.totalAmount - it.receiveDiscountCost
					def itemDto = new ReceivingReportItemDto(
							item_code: it.item?.itemCode,
							uou_qty: it.receiveQty,
							uou_unit: it.item?.unit_of_usage?.unitDescription,
							uop_qty: it.receiveQty / it.item?.item_conversion,
							uop_unit: it.item?.unit_of_purchase?.unitDescription,
							item_description: it.item?.descLong,
							expiry: it?.expirationDate ? dateFormat.format(it?.expirationDate) : '',
							unit_cost: it.recInventoryCost,
							input_tax: it.inputTax,
							inventory: receiving?.vatInclusive ? it.netAmount : total,
							total: receiving?.vatInclusive ? total : it.netAmount
					)
					itemsDto.add(itemDto)
			}
		}

		Employee emp = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()
		List<Signature> signList = signatureService.signatureList("DR").sort({it.sequence})
		def signColumn1 = new ArrayList<SignatureReportDto>()
		def signColumn2 = new ArrayList<SignatureReportDto>()

		if (signList) {
			Integer count = 1
			signList.each {
				it ->
					def signData
					if(it.currentUsers){
						signData = new SignatureReportDto(
								signatureHeader: it.signatureHeader,
								signaturies: emp.fullName,
								position: it.signaturePosition,
						)
					}
					else{
						signData = new SignatureReportDto(
								signatureHeader: it.signatureHeader,
								signaturies: (it.signaturePerson != null) ? it.signaturePerson : "",
								position: it.signaturePosition,
						)
					}

					if(count == 1)
					{
						signColumn1.add(signData)
					} else if(count == 2)
					{
						signColumn2.add(signData)
						count = 0
					}
					count++
			}
		}


		if (signColumn1) {
			parameters.put('sign_column1', new JRBeanCollectionDataSource(signColumn1))
		}
		if (signColumn2) {
			parameters.put('sign_column2', new JRBeanCollectionDataSource(signColumn2))
		}

		if (logo.exists()) {
			parameters.put("logo", logo?.getURL())
		}
		
		if (itemsDto) {
			parameters.put('items', new JRBeanCollectionDataSource(itemsDto))
		}

		if(hospitalConfigService?.hospitalInfo){
			String nameWithAddress = ""
			String address = ""
			String address2 = ""

			address +=  hospitalConfigService?.hospitalInfo?.address
			address +=  hospitalConfigService?.hospitalInfo?.addressLine2

			if(hospitalConfigService?.hospitalInfo?.hospitalName) nameWithAddress += hospitalConfigService?.hospitalInfo?.hospitalName + " "
			if(hospitalConfigService?.hospitalInfo?.city) address2 += hospitalConfigService?.hospitalInfo?.city + ", "
			if(hospitalConfigService?.hospitalInfo?.street) address2 += hospitalConfigService?.hospitalInfo?.street + ", "
			if(hospitalConfigService?.hospitalInfo?.zip) address2 += hospitalConfigService?.hospitalInfo?.zip

			nameWithAddress += address

			parameters.put('hospitalAddress', address)
			parameters.put('hospitalAddress2', address2)
			parameters.put('hospitalEmail', hospitalConfigService?.hospitalInfo?.email)
			parameters.put('hospitalName', nameWithAddress?:"")
			parameters.put('hospitalContactNo', hospitalConfigService?.hospitalInfo?.telNo?:"")

		}
		
		//printing
		try {
			def jrprint = JasperFillManager.fillReport(res.inputStream, parameters, dataSource)
			
			def pdfExporter = new JRPdfExporter()
			
			def outputStreamExporterOutput = new SimpleOutputStreamExporterOutput(os)
			
			pdfExporter.setExporterInput(new SimpleExporterInput(jrprint))
			pdfExporter.setExporterOutput(outputStreamExporterOutput)
			def configuration = new SimplePdfExporterConfiguration()
			pdfExporter.setConfiguration(configuration)
			pdfExporter.exportReport()
			
		} catch (JRException e) {
			e.printStackTrace()
		} catch (IOException e) {
			e.printStackTrace()
		}
		
		if (bytearray != null)
			IOUtils.closeQuietly(bytearray)
		//end
		
		def data = os.toByteArray()
		def params = new LinkedMultiValueMap<String, String>()
		//params.add("Content-Disposition", "inline;filename=Discharge-Instruction-of-\"" + caseDto?.patient?.fullName + "\".pdf")
		params.add("Content-Disposition", "inline;filename=Receiving-Report-of-\"" + receiving?.rrNo + "\".pdf")
		return new ResponseEntity(data, params, HttpStatus.OK)
	}
	
	@RequestMapping(value = ['/stockcard_report/{id}/{dep}'], produces = ['application/pdf'])
	ResponseEntity<byte[]> stockcard_report(@PathVariable('id') String itemId, @PathVariable('dep') String dep) {
		//query
		def stockcard = inventoryLedgerService.getStockCard(itemId, dep)
		def item = itemRepository.findById(UUID.fromString(itemId)).get()
		
		def res = applicationContext?.getResource("classpath:/reports/inventory/stockcard_report.jasper")
		def bytearray = new ByteArrayInputStream()
		def os = new ByteArrayOutputStream()
		def parameters = [:] as Map<String, Object>
		def logo = applicationContext?.getResource("classpath:/reports/logo.png")
		def itemsDto = new ArrayList<StockCardPrint>()
		
		DateTimeFormatter dateFormat =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
		def dto = new HeaderDtoPrint(
				descLong: item.descLong,
		)
		def gson = new Gson()
		def dataSourceByteArray = new ByteArrayInputStream(gson.toJson(dto).bytes)
		def dataSource = new JsonDataSource(dataSourceByteArray)
		
		if (stockcard) {
			stockcard.each {
				it ->
					def itemDto = new StockCardPrint(
							ledger_date: it.ledger_date,
							reference_no: it.reference_no,
							document_desc: it.document_desc,
							source_department: it.source_department,
							dest_department: it.dest_department,
							ledger_qtyin: it.ledger_qtyin,
							ledger_qty_out: it.ledger_qty_out,
							adjustment: it.adjustment,
							unitcost: it.unitcost,
							totalCost: (it.ledger_qtyin + it.ledger_qty_out + it.adjustment) * it.unitcost,
							runningqty: it.runningqty,
							wcost: it.wcost,
							runningbalance: it.runningbalance
					)
					itemsDto.add(itemDto)
			}
		}
		
		if (logo.exists()) {
			parameters.put("logo", logo?.getURL())
		}
		
		if (itemsDto) {
			parameters.put('items', new JRBeanCollectionDataSource(itemsDto))
		}

		if(hospitalConfigService?.hospitalInfo){
			String nameWithAddress = ""
			String address = ""
			String address2 = ""

			address +=  hospitalConfigService?.hospitalInfo?.address
			address +=  hospitalConfigService?.hospitalInfo?.addressLine2

			if(hospitalConfigService?.hospitalInfo?.hospitalName) nameWithAddress += hospitalConfigService?.hospitalInfo?.hospitalName + " "
			if(hospitalConfigService?.hospitalInfo?.city) address2 += hospitalConfigService?.hospitalInfo?.city + ", "
			if(hospitalConfigService?.hospitalInfo?.street) address2 += hospitalConfigService?.hospitalInfo?.street + ", "
			if(hospitalConfigService?.hospitalInfo?.zip) address2 += hospitalConfigService?.hospitalInfo?.zip

			nameWithAddress += address

			parameters.put('hospitalAddress', address)
			parameters.put('hospitalAddress2', address2)
			parameters.put('hospitalEmail', hospitalConfigService?.hospitalInfo?.email)
			parameters.put('hospitalName', nameWithAddress?:"")
			parameters.put('hospitalContactNo', hospitalConfigService?.hospitalInfo?.telNo?:"")

		}
		
		//printing
		try {
			def jrprint = JasperFillManager.fillReport(res.inputStream, parameters, dataSource)
			
			def pdfExporter = new JRPdfExporter()
			
			def outputStreamExporterOutput = new SimpleOutputStreamExporterOutput(os)
			
			pdfExporter.setExporterInput(new SimpleExporterInput(jrprint))
			pdfExporter.setExporterOutput(outputStreamExporterOutput)
			def configuration = new SimplePdfExporterConfiguration()
			pdfExporter.setConfiguration(configuration)
			pdfExporter.exportReport()
			
		} catch (JRException e) {
			e.printStackTrace()
		} catch (IOException e) {
			e.printStackTrace()
		}
		
		if (bytearray != null)
			IOUtils.closeQuietly(bytearray)
		//end
		
		def data = os.toByteArray()
		def params = new LinkedMultiValueMap<String, String>()
		//params.add("Content-Disposition", "inline;filename=Discharge-Instruction-of-\"" + caseDto?.patient?.fullName + "\".pdf")
		params.add("Content-Disposition", "inline;filename=Stockcard-Report-of-\"" + item?.descLong + "\".pdf")
		return new ResponseEntity(data, params, HttpStatus.OK)
	}
	
	@RequestMapping(value = ['/onhand_report/{id}/{date}'], produces = ['application/pdf'])
	ResponseEntity<byte[]> onhand_report(@PathVariable('id') String depId, @PathVariable('date') String date) {
		//query
		def items = inventoryResource.getOnhandReportByDate(UUID.fromString(depId), date, '')
		def dept = departmentRepository.findById(UUID.fromString(depId)).get()
		
		def res = applicationContext?.getResource("classpath:/reports/inventory/onhand_report.jasper")
		def bytearray = new ByteArrayInputStream()
		def os = new ByteArrayOutputStream()
		def parameters = [:] as Map<String, Object>
		def logo = applicationContext?.getResource("classpath:/reports/logo.png")
		
		DateTimeFormatter dateFormat =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
		def dto = new OnHandHeader(
				date: date,
				department: dept.departmentName,
		)
		def gson = new Gson()
		def dataSourceByteArray = new ByteArrayInputStream(gson.toJson(dto).bytes)
		def dataSource = new JsonDataSource(dataSourceByteArray)
		
		if (logo.exists()) {
			parameters.put("logo", logo?.getURL())
		}
		
		if (items) {
			parameters.put('items', new JRBeanCollectionDataSource(items))
		}

		if(hospitalConfigService?.hospitalInfo){
			String nameWithAddress = ""
			String address = ""
			String address2 = ""

			address +=  hospitalConfigService?.hospitalInfo?.address
			address +=  hospitalConfigService?.hospitalInfo?.addressLine2

			if(hospitalConfigService?.hospitalInfo?.hospitalName) nameWithAddress += hospitalConfigService?.hospitalInfo?.hospitalName + " "
			if(hospitalConfigService?.hospitalInfo?.city) address2 += hospitalConfigService?.hospitalInfo?.city + ", "
			if(hospitalConfigService?.hospitalInfo?.street) address2 += hospitalConfigService?.hospitalInfo?.street + ", "
			if(hospitalConfigService?.hospitalInfo?.zip) address2 += hospitalConfigService?.hospitalInfo?.zip

			nameWithAddress += address

			parameters.put('hospitalAddress', address)
			parameters.put('hospitalAddress2', address2)
			parameters.put('hospitalEmail', hospitalConfigService?.hospitalInfo?.email)
			parameters.put('hospitalName', nameWithAddress?:"")
			parameters.put('hospitalContactNo', hospitalConfigService?.hospitalInfo?.telNo?:"")

		}
		
		//printing
		try {
			def jrprint = JasperFillManager.fillReport(res.inputStream, parameters, dataSource)
			
			def pdfExporter = new JRPdfExporter()
			
			def outputStreamExporterOutput = new SimpleOutputStreamExporterOutput(os)
			
			pdfExporter.setExporterInput(new SimpleExporterInput(jrprint))
			pdfExporter.setExporterOutput(outputStreamExporterOutput)
			def configuration = new SimplePdfExporterConfiguration()
			pdfExporter.setConfiguration(configuration)
			pdfExporter.exportReport()
			
		} catch (JRException e) {
			e.printStackTrace()
		} catch (IOException e) {
			e.printStackTrace()
		}
		
		if (bytearray != null)
			IOUtils.closeQuietly(bytearray)
		//end
		
		def data = os.toByteArray()
		def params = new LinkedMultiValueMap<String, String>()
		params.add("Content-Disposition", "inline;filename=OnHandReport-of-\"" + dept?.departmentName + "\".pdf")
		return new ResponseEntity(data, params, HttpStatus.OK)
	}

	
	@RequestMapping(value = ['/physical/{id}/{date}'], produces = ['application/pdf'])
	ResponseEntity<byte[]> physicalReport(@PathVariable('id') String depId, @PathVariable('date') String date) {
		//query
		LocalDate dateC = LocalDate.parse(date)
		Instant instant = dateC.atStartOfDay(ZoneId.of("UTC")).toInstant()
		def items = physicalCountRepository.getMonthlyCountByDateAndDep(instant, UUID.fromString(depId), "").sort {
			it.item.descLong
		}
		def dept = departmentRepository.findById(UUID.fromString(depId)).get()
		
		def res = applicationContext?.getResource("classpath:/reports/inventory/physical_report.jasper")
		def bytearray = new ByteArrayInputStream()
		def os = new ByteArrayOutputStream()
		def parameters = [:] as Map<String, Object>
		def logo = applicationContext?.getResource("classpath:/reports/logo.png")
		def itemsDto = new ArrayList<PhysicalReport>()
		
		DateTimeFormatter dateFormat =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
		def dto = new OnHandHeader(
				date: dateFormat.format(dateC),
				department: dept.departmentName,
		)
		def gson = new Gson()
		def dataSourceByteArray = new ByteArrayInputStream(gson.toJson(dto).bytes)
		def dataSource = new JsonDataSource(dataSourceByteArray)
		
		if (items) {
			items.each {
				it ->
					def qty = physicalCountRepository.getMonthlyCount(it.id)
					def itemDto = new PhysicalReport(
							descLong: it.item.descLong,
							unitOfPurchase: it.item.unit_of_purchase.unitDescription,
							unitOfUsage: it.item.unit_of_usage.unitDescription,
							itemCategory: it.item.item_category.categoryDescription,
							expiryDate: it.expiration_date.toString() == 'null' ? null : it.expiration_date.toString(),
							onHand: it.onHand,
							physicalCount: qty,
							variance: it.variance,
							unitCost: it.unitCost,
							totalCost: qty * it.unitCost,
					)
					itemsDto.add(itemDto)
			}
		}
		
		if (logo.exists()) {
			parameters.put("logo", logo?.getURL())
		}
		
		if (itemsDto) {
			parameters.put('items', new JRBeanCollectionDataSource(itemsDto))
		}

		if(hospitalConfigService?.hospitalInfo){
			String nameWithAddress = ""
			String address = ""
			String address2 = ""

			address +=  hospitalConfigService?.hospitalInfo?.address
			address +=  hospitalConfigService?.hospitalInfo?.addressLine2

			if(hospitalConfigService?.hospitalInfo?.hospitalName) nameWithAddress += hospitalConfigService?.hospitalInfo?.hospitalName + " "
			if(hospitalConfigService?.hospitalInfo?.city) address2 += hospitalConfigService?.hospitalInfo?.city + ", "
			if(hospitalConfigService?.hospitalInfo?.street) address2 += hospitalConfigService?.hospitalInfo?.street + ", "
			if(hospitalConfigService?.hospitalInfo?.zip) address2 += hospitalConfigService?.hospitalInfo?.zip

			nameWithAddress += address

			parameters.put('hospitalAddress', address)
			parameters.put('hospitalAddress2', address2)
			parameters.put('hospitalEmail', hospitalConfigService?.hospitalInfo?.email)
			parameters.put('hospitalName', nameWithAddress?:"")
			parameters.put('hospitalContactNo', hospitalConfigService?.hospitalInfo?.telNo?:"")

		}
		
		//printing
		try {
			def jrprint = JasperFillManager.fillReport(res.inputStream, parameters, dataSource)
			
			def pdfExporter = new JRPdfExporter()
			
			def outputStreamExporterOutput = new SimpleOutputStreamExporterOutput(os)
			
			pdfExporter.setExporterInput(new SimpleExporterInput(jrprint))
			pdfExporter.setExporterOutput(outputStreamExporterOutput)
			def configuration = new SimplePdfExporterConfiguration()
			pdfExporter.setConfiguration(configuration)
			pdfExporter.exportReport()
			
		} catch (JRException e) {
			e.printStackTrace()
		} catch (IOException e) {
			e.printStackTrace()
		}
		
		if (bytearray != null)
			IOUtils.closeQuietly(bytearray)
		//end
		
		def data = os.toByteArray()
		def params = new LinkedMultiValueMap<String, String>()
		params.add("Content-Disposition", "inline;filename=PhysicalReport-of-\"" + dept?.departmentName + "\".pdf")
		return new ResponseEntity(data, params, HttpStatus.OK)
	}

	@RequestMapping(value = ['/physical/{id}'], produces = ['application/pdf'])
	ResponseEntity<byte[]> physicalReportById(@PathVariable('id') String id) {
		//query
		def parent = servicePhysicalTransaction.physicalTransactionById(UUID.fromString(id))
		def items = servicePhysicalCountView.getPhysicalItemById(UUID.fromString(id)).sort {
			it.descLong
		}

		def dept = parent.department

		def res = applicationContext?.getResource("classpath:/reports/inventory/physical_report.jasper")
		def bytearray = new ByteArrayInputStream()
		def os = new ByteArrayOutputStream()
		def parameters = [:] as Map<String, Object>
		def logo = applicationContext?.getResource("classpath:/reports/logo.png")
		def itemsDto = new ArrayList<PhysicalReport>()

		DateTimeFormatter dateFormat =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
		def dto = new OnHandHeader(
				date: dateFormat.format(parent.transDate),
				department: dept.departmentName,
		)
		def gson = new Gson()
		def dataSourceByteArray = new ByteArrayInputStream(gson.toJson(dto).bytes)
		def dataSource = new JsonDataSource(dataSourceByteArray)

		if (items) {
			items.each {
				it ->
					def itemDto = new PhysicalReport(
							descLong: it.descLong,
							unitOfPurchase: it.unit_of_purchase,
							unitOfUsage: it.unit_of_usage,
							itemCategory: it.category_description,
							expiryDate:  it.expiration_date.toString() == 'null' ? null : it.expiration_date.toString(),
							onHand: it.onHand,
							physicalCount: it.monthlyCount,
							variance: it.variance,
							unitCost: it.unitCost,
							totalCost: it.monthlyCount * it.unitCost,
					)
					itemsDto.add(itemDto)
			}
		}

		if (logo.exists()) {
			parameters.put("logo", logo?.getURL())
		}

		if (itemsDto) {
			parameters.put('items', new JRBeanCollectionDataSource(itemsDto))
		}

		if(hospitalConfigService?.hospitalInfo){
			String nameWithAddress = ""
			String address = ""
			String address2 = ""

			address +=  hospitalConfigService?.hospitalInfo?.address
			address +=  hospitalConfigService?.hospitalInfo?.addressLine2

			if(hospitalConfigService?.hospitalInfo?.hospitalName) nameWithAddress += hospitalConfigService?.hospitalInfo?.hospitalName + " "
			if(hospitalConfigService?.hospitalInfo?.city) address2 += hospitalConfigService?.hospitalInfo?.city + ", "
			if(hospitalConfigService?.hospitalInfo?.street) address2 += hospitalConfigService?.hospitalInfo?.street + ", "
			if(hospitalConfigService?.hospitalInfo?.zip) address2 += hospitalConfigService?.hospitalInfo?.zip

			nameWithAddress += address

			parameters.put('hospitalAddress', address)
			parameters.put('hospitalAddress2', address2)
			parameters.put('hospitalEmail', hospitalConfigService?.hospitalInfo?.email)
			parameters.put('hospitalName', nameWithAddress?:"")
			parameters.put('hospitalContactNo', hospitalConfigService?.hospitalInfo?.telNo?:"")

		}

		//printing
		try {
			def jrprint = JasperFillManager.fillReport(res.inputStream, parameters, dataSource)

			def pdfExporter = new JRPdfExporter()

			def outputStreamExporterOutput = new SimpleOutputStreamExporterOutput(os)

			pdfExporter.setExporterInput(new SimpleExporterInput(jrprint))
			pdfExporter.setExporterOutput(outputStreamExporterOutput)
			def configuration = new SimplePdfExporterConfiguration()
			pdfExporter.setConfiguration(configuration)
			pdfExporter.exportReport()

		} catch (JRException e) {
			e.printStackTrace()
		} catch (IOException e) {
			e.printStackTrace()
		}

		if (bytearray != null)
			IOUtils.closeQuietly(bytearray)
		//end

		def data = os.toByteArray()
		def params = new LinkedMultiValueMap<String, String>()
		params.add("Content-Disposition", "inline;filename=PhysicalReport-of-\"" + dept?.departmentName + "\".pdf")
		return new ResponseEntity(data, params, HttpStatus.OK)
	}
	
	@RequestMapping(value = ['/return_sup/{id}'], produces = ['application/pdf'])
	ResponseEntity<byte[]> returnReport(@PathVariable('id') UUID id) {
		
		ReturnSupplier returnSupplierDetails = returnSupplierRepository.findById(id).get()
		List<ReturnSupplierItem> returnSupplierItem = returnSupplierItemRepository.findItemsByReturnSupplierId(id)
		Employee emp = employeeRepository.findByUsername(returnSupplierDetails?.createdBy).first()
		
		def res = applicationContext?.getResource("classpath:/reports/inventory/return_sup_report.jasper")
		def bytearray = new ByteArrayInputStream()
		def os = new ByteArrayOutputStream()
		def parameters = [:] as Map<String, Object>
		def logo = applicationContext?.getResource("classpath:/reports/logo.png")
		def itemsDto = new ArrayList<ReturnSuppItemReportDto>()
		
		DateTimeFormatter dateFormat =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
		
		def dto = new ReturnSuppReportDto(
				date: dateFormat.format(Instant.now()),
				rts: returnSupplierDetails?.rtsNo,
				supplierCode: returnSupplierDetails?.supplier?.supplierCode,
				supplierName: returnSupplierDetails?.supplier?.supplierFullname,
				returnBy: emp?.fullName,
				returnDate: dateFormat.format(returnSupplierDetails?.returnDate),
				receivedBy: returnSupplierDetails?.received_by,
				receivedDate: dateFormat.format(returnSupplierDetails?.receivedRefDate)
		)
		
		def gson = new Gson()
		def dataSourceByteArray = new ByteArrayInputStream(gson.toJson(dto).bytes)
		def dataSource = new JsonDataSource(dataSourceByteArray)
		
		if (returnSupplierItem) {
			returnSupplierItem.each {
				it ->
					
					def itemDto = new ReturnSuppItemReportDto(
							stockCode: it.item.sku,
							itemDesc: it.item.descLong,
							uom: it.item.unit_of_usage.unitDescription,
							quantityReturn: it.returnQty,
							reasonForReturn: it.return_remarks
					)
					itemsDto.add(itemDto)
			}
		}
		
		if (logo.exists()) {
			parameters.put("logo", logo?.getURL())
		}
		
		if (itemsDto) {
			parameters.put('items', new JRBeanCollectionDataSource(itemsDto))
		}

		if(hospitalConfigService?.hospitalInfo){
			String nameWithAddress = ""
			String address = ""
			String address2 = ""

			address +=  hospitalConfigService?.hospitalInfo?.address
			address +=  hospitalConfigService?.hospitalInfo?.addressLine2

			if(hospitalConfigService?.hospitalInfo?.hospitalName) nameWithAddress += hospitalConfigService?.hospitalInfo?.hospitalName + " "
			if(hospitalConfigService?.hospitalInfo?.city) address2 += hospitalConfigService?.hospitalInfo?.city + ", "
			if(hospitalConfigService?.hospitalInfo?.street) address2 += hospitalConfigService?.hospitalInfo?.street + ", "
			if(hospitalConfigService?.hospitalInfo?.zip) address2 += hospitalConfigService?.hospitalInfo?.zip

			nameWithAddress += address

			parameters.put('hospitalAddress', address)
			parameters.put('hospitalAddress2', address2)
			parameters.put('hospitalEmail', hospitalConfigService?.hospitalInfo?.email)
			parameters.put('hospitalName', nameWithAddress?:"")
			parameters.put('hospitalContactNo', hospitalConfigService?.hospitalInfo?.telNo?:"")

		}
		
		//printing
		try {
			def jrprint = JasperFillManager.fillReport(res.inputStream, parameters, dataSource)
			
			def pdfExporter = new JRPdfExporter()
			
			def outputStreamExporterOutput = new SimpleOutputStreamExporterOutput(os)
			
			pdfExporter.setExporterInput(new SimpleExporterInput(jrprint))
			pdfExporter.setExporterOutput(outputStreamExporterOutput)
			def configuration = new SimplePdfExporterConfiguration()
			pdfExporter.setConfiguration(configuration)
			pdfExporter.exportReport()
			
		} catch (JRException e) {
			e.printStackTrace()
		} catch (IOException e) {
			e.printStackTrace()
		}
		
		if (bytearray != null)
			IOUtils.closeQuietly(bytearray)
		//end
		
		def data = os.toByteArray()
		def params = new LinkedMultiValueMap<String, String>()
		//params.add("Content-Disposition", "inline;filename=Discharge-Instruction-of-\"" + caseDto?.patient?.fullName + "\".pdf")
		params.add("Content-Disposition", "inline;filename=RETURN-SUPPLIER-Report-of-\"" + returnSupplierDetails?.rtsNo + "\".pdf")
		return new ResponseEntity(data, params, HttpStatus.OK)
	}

	@RequestMapping(value = ['/issue_report/{id}'], produces = ['application/pdf'])
	ResponseEntity<byte[]> issueReport(@PathVariable('id') UUID id) {


		DepartmentStockIssue parent = departmentStockIssueRepository.findById(id).get()
		List<DepartmentStockIssueItems> items = departmentStockIssueItemRepository.findItemsByIssue(id).sort {
			it.item.descLong
		}
		String issueBy = parent.issued_by.fullName
		String receivedBy = parent.claimed_by.fullName

		def res = applicationContext?.getResource("classpath:/reports/inventory/stock_transfer.jasper")
		def bytearray = new ByteArrayInputStream()
		def os = new ByteArrayOutputStream()
		def parameters = [:] as Map<String, Object>
		def logo = applicationContext?.getResource("classpath:/reports/logo.png")
		def itemsDto = new ArrayList<STSReportItemDto>()

		DateTimeFormatter dateFormat =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())

		def dto = new STSReportDto(
				date: dateFormat.format(parent?.issueDate),
				stsNo: parent?.issueNo,
				issuing_dep: parent?.issueFrom?.departmentName,
				receiving_dep: parent?.issueTo?.departmentName,
				ref_no: parent?.request?.requestNo ?: "N/A",
				issuedBy: issueBy,
				receivedBy: receivedBy,

		)
		def gson = new Gson()
		def dataSourceByteArray = new ByteArrayInputStream(gson.toJson(dto).bytes)
		def dataSource = new JsonDataSource(dataSourceByteArray)

		if (items) {
			items.each {
				it ->
					BigDecimal total = it.issueQty * (it.unitCost ?: BigDecimal.ZERO)
					def itemDto = new STSReportItemDto(
							code: it.item.itemCode ?: "",
							description: it.item.descLong ?: "",
							uom: it.item?.unit_of_usage?.unitDescription ?: "",
							request: it.requestedQty,
							issued: it.issueQty,
							unitCost: it.unitCost,
							total: total,

					)
					itemsDto.add(itemDto)
			}
		}


		if (logo.exists()) {
			parameters.put("logo", logo?.getURL())
		}

		if (itemsDto) {
			parameters.put('items', new JRBeanCollectionDataSource(itemsDto))
		}

		if(hospitalConfigService?.hospitalInfo){
			String nameWithAddress = ""
			String address = ""
			String address2 = ""

			address +=  hospitalConfigService?.hospitalInfo?.address
			address +=  hospitalConfigService?.hospitalInfo?.addressLine2

			if(hospitalConfigService?.hospitalInfo?.hospitalName) nameWithAddress += hospitalConfigService?.hospitalInfo?.hospitalName + " "
			if(hospitalConfigService?.hospitalInfo?.city) address2 += hospitalConfigService?.hospitalInfo?.city + ", "
			if(hospitalConfigService?.hospitalInfo?.street) address2 += hospitalConfigService?.hospitalInfo?.street + ", "
			if(hospitalConfigService?.hospitalInfo?.zip) address2 += hospitalConfigService?.hospitalInfo?.zip

			nameWithAddress += address

			parameters.put('hospitalAddress', address)
			parameters.put('hospitalAddress2', address2)
			parameters.put('hospitalEmail', hospitalConfigService?.hospitalInfo?.email)
			parameters.put('hospitalName', nameWithAddress?:"")
			parameters.put('hospitalContactNo', hospitalConfigService?.hospitalInfo?.telNo?:"")

		}

		//printing
		try {
			def jrprint = JasperFillManager.fillReport(res.inputStream, parameters, dataSource)

			def pdfExporter = new JRPdfExporter()

			def outputStreamExporterOutput = new SimpleOutputStreamExporterOutput(os)

			pdfExporter.setExporterInput(new SimpleExporterInput(jrprint))
			pdfExporter.setExporterOutput(outputStreamExporterOutput)
			def configuration = new SimplePdfExporterConfiguration()
			pdfExporter.setConfiguration(configuration)
			pdfExporter.exportReport()

		} catch (JRException e) {
			e.printStackTrace()
		} catch (IOException e) {
			e.printStackTrace()
		}

		if (bytearray != null)
			IOUtils.closeQuietly(bytearray)
		//end

		def data = os.toByteArray()
		def params = new LinkedMultiValueMap<String, String>()
		//params.add("Content-Disposition", "inline;filename=Discharge-Instruction-of-\"" + caseDto?.patient?.fullName + "\".pdf")
		params.add("Content-Disposition", "inline;filename=STS-Report-of-\"" + parent?.requestNo + "\".pdf")
		return new ResponseEntity(data, params, HttpStatus.OK)
	}
}
