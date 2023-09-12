package com.hisd3.hismk2.repository.eventhandlers

import com.hisd3.hismk2.dao.price_tier.PriceTierDetailDao
import com.hisd3.hismk2.domain.ancillary.OrderSlipItem
import com.hisd3.hismk2.domain.ancillary.RfFees
import com.hisd3.hismk2.domain.ancillary.Service
import com.hisd3.hismk2.domain.billing.BillingItem
import com.hisd3.hismk2.domain.billing.BillingItemType
import com.hisd3.hismk2.domain.billing.ItemPriceControl
import com.hisd3.hismk2.domain.billing.PriceTierDetail
import com.hisd3.hismk2.domain.billing.PriceTierModifier
import com.hisd3.hismk2.domain.billing.ServicePriceControl
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.hrm.Shift
import com.hisd3.hismk2.domain.inventory.CashBasis
import com.hisd3.hismk2.domain.inventory.CashBasisItem
import com.hisd3.hismk2.domain.inventory.StockRequest
import com.hisd3.hismk2.domain.inventory.StockRequestItem
import com.hisd3.hismk2.domain.inventory.SupplierType
import com.hisd3.hismk2.domain.pms.*
import com.hisd3.hismk2.domain.referential.DohPosition
import com.hisd3.hismk2.graphqlservices.billing.BillingItemServices
import com.hisd3.hismk2.graphqlservices.billing.BillingService
import com.hisd3.hismk2.graphqlservices.billing.RfDetails
import com.hisd3.hismk2.repository.ancillary.RfFeesRepository
import com.hisd3.hismk2.repository.ancillary.ServiceRepository
import com.hisd3.hismk2.repository.billing.BillingItemRepository
import com.hisd3.hismk2.repository.billing.ItemPriceControlRepository
import com.hisd3.hismk2.repository.billing.PriceTierDetailRepository
import com.hisd3.hismk2.repository.billing.PriceTierModifierRepository
import com.hisd3.hismk2.repository.billing.ServicePriceControlRepository
import com.hisd3.hismk2.repository.hospital_config.HospitalInfoRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.inventory.CashBasisItemRepository
import com.hisd3.hismk2.repository.inventory.CashBasisRepository
import com.hisd3.hismk2.repository.inventory.StockRequestItemRepository
import com.hisd3.hismk2.repository.pms.*
import com.hisd3.hismk2.repository.referential.DohPositionRepository
import com.hisd3.hismk2.repository.referential.DohServiceTypeRepository
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import com.hisd3.hismk2.services.InventoryLedgService
import com.hisd3.hismk2.services.NotificationService
import groovy.json.JsonBuilder
import groovy.transform.TypeChecked
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.core.annotation.*

import javax.transaction.Transactional
import java.math.RoundingMode
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@TypeChecked
@RepositoryEventHandler
@Transactional
class EventHandler {

    @Autowired
    private GeneratorService generatorService

    @Autowired
    private EmployeeRepository employeeRepository

    @Autowired
    PriceTierDetailRepository priceTierDetailRepository

    @Autowired
    InventoryLedgService inventoryLedgService

    @Autowired
    CaseRepository caseRepository

    @Autowired
    PatientRepository patientRepository

    @Autowired
    HospitalInfoRepository hospitalInfoRepository

    @Autowired
    DohServiceTypeRepository dohServiceTypeRepository

    @Autowired
    StockRequestItemRepository stockRequestItemRepository

    @Autowired
    ManagingPhysicianRepository managingPhysicianRepository

    @Autowired
    DohPositionRepository dohPositionRepository

    @Autowired
    ServicePriceControlRepository servicePriceControlRepository

    @Autowired
    ItemPriceControlRepository itemPriceControlRepository

    @Autowired
    PriceTierModifierRepository priceTierModifierRepository

    @Autowired
    BillingService billingService


    @Autowired
    PriceTierDetailDao priceTierDetailDao

    @Autowired
    BillingItemServices billingItemServices

    @Autowired
    NotificationService notificationService

    @Autowired
    DoctorOrderItemRepository doctorOrderItemRepository

    @Autowired
    MedicationRepository medicationRepository

    @Autowired
    ServiceRepository serviceRepository

    @Autowired
    CashBasisRepository cashBasisRepository

    @Autowired
    CashBasisItemRepository cashBasisItemRepository

    @Autowired
    RfFeesRepository rfFeesRepository

    @Autowired
    BillingItemRepository billingItemRepository

    @Autowired
    NurseNoteRepository nurseNoteRepository

    @HandleBeforeCreate
    handleBeforeCreatePatient(Patient patient) {
        if (!patient.patientNo) {
            patient.patientNo = generatorService?.getNextValue(GeneratorType.PATIENT_NO, { i ->
                StringUtils.leftPad(i.toString(), 6, "0")
            })
        }
    }

    @HandleAfterCreate
    handleAfterCreateSupplierType(SupplierType supplierType) {
        generatorService.getNextValue(GeneratorType.SUPPLIER_SUB_ACCOUNT_CODE)
    }

    @HandleBeforeCreate
    handleBeforeCreateCase(Case patientCase) {
        if (!patientCase.caseNo) {
            patientCase.caseNo = generatorService?.getNextValue(GeneratorType.CASE_NO, { i ->
                StringUtils.leftPad(i.toString(), 6, "0")
            })
        }
    }

    @HandleBeforeSave
    handleBeforeSaveCase(Case patientCase) {
        if (patientCase.mayGoHomeDatetime != null || patientCase.mayGoHomeDatetime == '') {
            patientCase.mayGoHomeDatetime = Instant.now()
        } else {
            patientCase.mayGoHomeDatetime = null
        }

        if (patientCase?.serviceType) {
            def dohSt = dohServiceTypeRepository.getDOHServiceTypesByDesc(patientCase.serviceType)
            patientCase.serviceType = patientCase.serviceType.toUpperCase()
            patientCase.serviceCode = dohSt?.tscode ? dohSt.tscode : 500
        }

        //intercept possible primary DX bug from frontend
        //if(patientCase.icdDiagnosis)
    }

    @HandleBeforeCreate
    handleBeforeCreateService(Service service) {
        if (!service.hidden)
            service.hidden = false

        if (!service.description)
            service.description = service.serviceName

        if (!service.category)
            service.category = service.department.parentDepartment ? service.department.parentDepartment.departmentName : service.department.departmentName
    }

    @HandleAfterSave
    handleCaseAfterSave(Case patientCase) {
        if (patientCase.dischargeCondition == "EXPIRED") {
            List<String> filters = ['MEDICAL RECORDS', 'BILLING', 'ADMITTING']
            notificationService.notifyGroups(filters, "Patient Status Notification", "Patient with Patient Id : " + patientCase.patient.patientNo + " has EXPIRED", "")
        }

        if (patientCase.attendingPhysician) {
            def physicians = managingPhysicianRepository.getManagingPhysiciansByCase(patientCase.id)
            //convert all others to false.
            List<ManagingPhysician> mp = []

            if (physicians.any { it.employee.id == patientCase.attendingPhysician.id }) {
                physicians.each { ManagingPhysician entry ->

                    if (entry.employee.id == patientCase.attendingPhysician.id) {
                        entry.isMain = true
                    } else {
                        entry.isMain = false
                    }

//					if( && !entry.isMain){
//						entry.isMain = true
//					}else {
//						entry.isMain = false
//					}
                    managingPhysicianRepository.save(entry)
                }
            } else {
                if (physicians.any { it.isMain }) {
                    physicians.eachWithIndex { ManagingPhysician entry, int i ->
                        entry.isMain = false
                        managingPhysicianRepository.save(entry)
                    }
                }
                def managing = new ManagingPhysician(
                        position: "PHYSICIAN",
                        employee: patientCase.attendingPhysician,
                        parentCase: patientCase,
                        isMain: true
                )

                managingPhysicianRepository.save(managing)
            }

        }
    }

    @HandleBeforeCreate
    handleBeforeCreateShift(Shift shift) {
        if (!shift.moveBackDays)
            shift.moveBackDays = 0
    }

    @HandleAfterCreate
    handleAfterCreateNurseNote(NurseNote note) {
        List<ManagingPhysician> list = managingPhysicianRepository.getManagingStaffByCase(note.parentCase.id, note.employee.id)

        if (!list) {
            managingPhysicianRepository.save(new ManagingPhysician(
                    null,
                    note.parentCase,
                    note.employee,
                    'STAFF'
            ))
        }
    }

    @HandleBeforeCreate
    handleBeforeCreateVitals(VitalSign vitalSign) {
        vitalSign?.cbs = vitalSign?.cbs?.replace(' ', '')
        vitalSign?.diastolic = vitalSign?.diastolic?.replace(' ', '')
        vitalSign?.fetalHr = vitalSign?.fetalHr?.replace(' ', '')
        vitalSign?.oxygenSaturation = vitalSign?.oxygenSaturation?.replace(' ', '')
        vitalSign?.painScore = vitalSign?.painScore?.replace(' ', '')
        vitalSign?.pulseRate = vitalSign?.pulseRate?.replace(' ', '')
        vitalSign?.respiratoryRate = vitalSign?.respiratoryRate?.replace(' ', '')
        vitalSign?.systolic = vitalSign?.systolic?.replace(' ', '')
        vitalSign?.temperature = vitalSign?.temperature?.replace(' ', '')
        vitalSign?.weight = vitalSign?.weight?.replace(' ', '')
    }

    @HandleBeforeSave
    handleBeforeSaveVitals(VitalSign vitalSign) {
        vitalSign?.cbs = vitalSign?.cbs?.replace(' ', '')
        vitalSign?.diastolic = vitalSign?.diastolic?.replace(' ', '')
        vitalSign?.fetalHr = vitalSign?.fetalHr?.replace(' ', '')
        vitalSign?.oxygenSaturation = vitalSign?.oxygenSaturation?.replace(' ', '')
        vitalSign?.painScore = vitalSign?.painScore?.replace(' ', '')
        vitalSign?.pulseRate = vitalSign?.pulseRate?.replace(' ', '')
        vitalSign?.respiratoryRate = vitalSign?.respiratoryRate?.replace(' ', '')
        vitalSign?.systolic = vitalSign?.systolic?.replace(' ', '')
        vitalSign?.temperature = vitalSign?.temperature?.replace(' ', '')
        vitalSign?.weight = vitalSign?.weight?.replace(' ', '')
    }

    @HandleBeforeSave
    handleBeforeSaveStockRequestItem(StockRequestItem str) { //insert ni sya sa billing after prepared qty
        try {
            if (str.stockRequest.status == "CLAIMABLE" || str.stockRequest.status == "SENT") {
                if (!str.billedToPatient) {
                    str.billedToPatient = str.preparedQty > 0
                    str.item //Kani nga item icharge
                    str.preparedQty // Quantity Prepared
                    str.stockRequest.requestedDepartment // dire ang pharmacy department nakabutang
                    //ANI SA IINSERT PAG CHARGE SIR. AKO RAY BALHIN IF MUINGON SILA ICHARGE DAYUN AFTER MA PREPARE BISAG WAPA ICLAIM

                    def activeBilling = billingService.activeBilling(str.stockRequest.patientCase)

                    // if no activing billing... no charges are made
                    //  New Active Billing auto to prevent errors
                    //billed only if qty is greater than zero
                    if(str.preparedQty > 0){
                        if (activeBilling) {
                            str.stockRequest
                            def item = [
                                    "quantity"        : str?.preparedQty?.toInteger() ?: 1,
                                    "itemId"          : str.item.id,
                                    "targetDepartment": str.stockRequest.requestedDepartment.id.toString()

                            ]

                            def values = []
                            values << item

                            billingItemServices.addBillingItem(
                                    activeBilling.id,
                                    BillingItemType.MEDICINES,
                                    values,
                                    null,
                                    null,
                                    null,
                                    true
                            )
                        } else {
                            //  New Active Billing auto from Ancillary

                            activeBilling = billingService.createBilling(str.stockRequest.patient.id, str.stockRequest.patientCase.id)

                            def item = [
                                    "quantity"        : str?.preparedQty?.toInteger() ?: 1,
                                    "itemId"          : str.item.id,
                                    "targetDepartment": str.stockRequest.requestedDepartment.id.toString()

                            ]

                            def values = []
                            values << item

                            billingItemServices.addBillingItem(
                                    activeBilling.id,
                                    BillingItemType.MEDICINES,
                                    values,
                                    null,
                                    null,
                                    null,
                                    true
                            )

                        }
                        //
                        inventoryLedgService.InventoryCharge(str.stockRequest.requestedDepartment.id,
                                str.item.id,
                                str.stockRequest.stockRequestNo,
                                "cs",
                                str.preparedQty as Integer,
                                activeBilling.id,
                        null)
                    }

                }
                /*else if(str.forCashPayment && !StringUtils.isEmpty(str.billingItemNo) && str.billingItemNo==null)
                {
                    println("Cash basis medication prepared.")
                }*/
            } else if (str.stockRequest.status == "CLAIMED") {
                if (str?.medication) {
                    if (str?.medication?.onhand != null) {
                        str?.medication?.onhand += str?.preparedQty
                    } else {
                        str?.medication?.onhand = 0.0
                        str?.medication?.onhand += str?.preparedQty
                    }
                }
            } else if (str.forCashPayment && str.stockRequest.status == "REQUESTED") {
                CashBasis cb = cashBasisRepository.getPendingCashBasisByCase(str.stockRequest.patientCase.id).first()
                PriceTierDetail priceTierDetail = priceTierDetailDao.getDetail(str.stockRequest.patientCase.id);
                CashBasisItem cbi = new CashBasisItem()
                cbi.item = str.item
                cbi.cashBasis = cb
                cbi.price = priceTierDetailDao.getItemPrice(priceTierDetail.id, str.item.id)
                cbi.type = "medicines"
                cbi.sriNumber = str.id.toString()
                cbi.quantity = str.preparedQty
                cashBasisItemRepository.save(cbi)
            }
        }
        catch (Exception e) {
            throw e;
        }

    }

    @HandleBeforeSave
    handleBeforeSaveCashBasis(CashBasis cashBasis) {
        if (cashBasis.status == "POSTED") {

            def activeBilling = billingService.activeBilling(cashBasis.patientCase)
            cashBasis.cashBasisItem.each {
                if (activeBilling) {

                    def item = [
                            "quantity"        : it?.quantity,
                            "itemId"          : it?.item.id,
                            "targetDepartment": it?.cashBasis?.department?.id.toString(),
                            "cashBasisItemId" : it?.id
                    ]

                    def values = []
                    values << item

                    billingItemServices.addBillingItemForCashBasis(
                            activeBilling.id,
                            BillingItemType.MEDICINES,
                            values
                    )
                }
            }
        } else if (cashBasis.status == "DISPENSED") {
            cashBasis.cashBasisItem.each {
                if (it?.sriNumber != null || !it?.sriNumber.isEmpty()) {
                    StockRequestItem item = stockRequestItemRepository.getOne(UUID.fromString(it?.sriNumber))
                    item.billingItemNo = it?.billingItemNo
                    item.billedToPatient = true
                    inventoryLedgService.InventoryCharge(item?.stockRequest.requestedDepartment.id,
                            item?.item.id,
                            item?.stockRequest.stockRequestNo,
                            "cs",
                            item?.preparedQty as Integer,
                            null, null)
                }

            }
        }
    }

    @HandleAfterCreate
    handleAfterCreateAdministration(Administration administration) {
        if (administration.medication.doctorsOrderItemId) {
            DoctorOrderItem item = doctorOrderItemRepository.findById(administration.medication.doctorsOrderItemId).get()
            item.status = "ADMINISTERED"
            doctorOrderItemRepository.save(item)
        }
    }

    @HandleBeforeSave
    handleBeforeSaveEmployee(Employee employee) {
        if (employee.positionType) {
            DohPosition position = dohPositionRepository.getDOHPositionByPostDesc(employee.positionType).find()
            employee.positionCode = position.poscode
        }
    }


    @HandleAfterSave
    handleAfterSaveDo(DoctorOrderItem doctorOrderItem) {
        if (doctorOrderItem.discontinuedDoItemRef) {
            List<Medication> medication = medicationRepository.getMedicationsWhereDoItemId(doctorOrderItem.discontinuedDoItemRef)
            if (medication.size() > 0) {
                def med1 = medication.first()
                if (doctorOrderItemRepository.findById(doctorOrderItem.discontinuedDoItemRef).get().discontinuedDatetime != null) {
                    med1.discontinuedDatetime = Instant.now()
                    medicationRepository.save(med1)
                }
            }
        }
    }

    @HandleBeforeSave
    handleBeforeSaveStockRequest(StockRequest stockRequest) {
        if (stockRequest.status == "CLAIMABLE" || stockRequest.status == "SENT") {

            stockRequest.claimedByDatetime = LocalDateTime.now().toInstant(ZoneOffset.UTC)
            if (stockRequest.claimedBy == null) {
                Employee e = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()
                stockRequest.claimedBy = e
            }
            List<StockRequestItem> stockRequestItems = stockRequestItemRepository.getSRItemsBySRId(stockRequest.id)
            for (StockRequestItem str in stockRequestItems) {

            }
        }

        if (stockRequest.status == "CLAIMABLE") {
            Employee e = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()
            stockRequest.preparedBy = e
            stockRequest.preparedByDatetime = LocalDateTime.now().toInstant(ZoneOffset.UTC)
            notificationService.notifySentClaimableStockRequest(stockRequest)

        } else if (stockRequest.status == "SENT") {
            if (stockRequest.preparedBy == null) {
                Employee e = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()
                if (stockRequest.preparedBy == null) {
                    stockRequest.preparedBy = e
                    stockRequest.preparedByDatetime = LocalDateTime.now().toInstant(ZoneOffset.UTC)
                }
                stockRequest.dispensedBy = e
                stockRequest.dispensedByDatetime = LocalDateTime.now().toInstant(ZoneOffset.UTC)
            }
            notificationService.notifySentClaimableStockRequest(stockRequest)
        } else if (stockRequest.status == "CLAIMED") {
            List<StockRequestItem> stockRequestItems = stockRequestItemRepository.getSRItemsBySRId(stockRequest.id)
            for (StockRequestItem str in stockRequestItems) {

                if (str?.medication?.onhand != null) {
                    str?.medication?.onhand += str.preparedQty
                } else {
                    str?.medication?.onhand = str.preparedQty
                }
                stockRequestItemRepository.save(str)
            }
        }
    }

    @HandleAfterSave
    handleMedicationAfterSave(StockRequest stockRequest) {
//		if (stockRequest.status == "CLAIMABLE") {
//			println("MUST NOTIFY THAT REQUEST IS CLAIMABLE")
//			for (StockRequestItem stockRequestItem in stockRequest.stockRequestItems) {
//				if (stockRequestItem.itemReferenceId != null) {
//					//deduct on inventory
//				} else {
//					println("Item has no inventory reference id and cannot charge or deduct on inventory : " + stockRequestItem.itemDescription)
//				}
//			}
//		}
//		if (stockRequest.status == "CLAIMED") {
//			println("MUST CHARGE MEDCS")
//		}
    }

    @HandleBeforeCreate
    handleBeforeCreateStockRequest(StockRequest stockRequest) {
        if (!stockRequest.stockRequestNo) {

            //Generate stock request
            stockRequest.stockRequestNo = generatorService?.getNextValue(GeneratorType.STOCK_REQUEST_NO, { i ->
                StringUtils.leftPad(i.toString(), 6, "0")
            })

            //Set initial stock request status
            stockRequest.status = "REQUESTED"
        }
    }

    @HandleBeforeCreate
    handleBeforeCreateO2Administration(O2Administration o2Administration) {
        println o2Administration
    }

    @HandleBeforeCreate
    handleBeforeCreateAdministration(Administration administration) {
        if (administration.medication.onhand != null)
            administration.medication.onhand--
    }

    @HandleBeforeDelete
    handleBeforeDeleteAdministration(Administration administration) {
        if (administration.medication.onhand != null)
            administration.medication.onhand++
    }

    @HandleBeforeCreate
    handleBeforeCreateAdministration(PatientOwnMedicineAdministration administration) {
        if (administration.patientOwnMedicine.qty_onhand != null)
            administration.patientOwnMedicine.qty_onhand--
    }

    @HandleBeforeDelete
    handleBeforeDeleteAdministration(PatientOwnMedicineAdministration administration) {
        if (administration.patientOwnMedicine.qty_onhand != null)
            administration.patientOwnMedicine.qty_onhand++
    }

    @HandleBeforeCreate
    handleBeforeCreatePatientOwnMedicine(PatientOwnMedicine patientOwnMedicine) {
        Employee e = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()
        patientOwnMedicine.employee = e
        patientOwnMedicine.entry_datetime = Instant.now()
    }

    @HandleBeforeDelete
    handleBeforeDeletePriceTierDetail(PriceTierDetail detail) {
        List<PriceTierModifier> modifierList = priceTierModifierRepository.getPriceTierModifierByTier(detail.id)
        List<ServicePriceControl> serviceList = servicePriceControlRepository.getServiceControlItemsByTier(detail.id)
        List<ItemPriceControl> itemsList = itemPriceControlRepository.getItemControlItemsByTier(detail.id)

        priceTierModifierRepository.deleteAll(modifierList)
        servicePriceControlRepository.deleteAll(serviceList)
        itemPriceControlRepository.deleteAll(itemsList)
    }

    @HandleBeforeCreate
    handleAfterCreateStockRequest(StockRequest stockRequest) {
        Case currentCase = caseRepository.getPatientActiveCase(stockRequest.patient.id)
        stockRequest.patientCase = currentCase
    }

    @HandleAfterCreate
    handleAfterSaveTransfer(Transfer transfer) {
        List<String> filters = ['MEDICAL RECORDS', 'BILLING', 'ADMITTING']
        notificationService.notifyGroups(filters, "Patient Transfer Notification", transfer.parentCase.patient.fullName + " is transfered to " + transfer.department.departmentName, "")
    }

    @HandleAfterSave
    handleAfterSaveOrderSlipItem(OrderSlipItem orderSlipItem) {
        if (orderSlipItem.reader && orderSlipItem.billing_item) {

            List<RfFees> rfFees = rfFeesRepository.searchMatch(orderSlipItem.service.id, orderSlipItem.reader.id)

            if (rfFees.size() > 0) {

                orderSlipItem.billing_item.rfFee = orderSlipItem.billing_item.debit * (rfFees[0].rfPercentage / 100)

                def o = new RfDetails()
                o.rfTableId = rfFees[0].id
                o.doctorsId = orderSlipItem.reader.id
                o.serviceId = orderSlipItem.service.id
                o.percentage = rfFees[0].rfPercentage
                orderSlipItem.billing_item.rfDetails = new JsonBuilder(o).toString()

            } else {

//				def percentage = (orderSlipItem.service.readersFee /orderSlipItem.service.basePrice)

                orderSlipItem.billing_item.rfFee = orderSlipItem.service.readersFee

                def o = new RfDetails()
                o.doctorsId = orderSlipItem.reader.id
                o.serviceId = orderSlipItem.service.id
//				o.percentage = ((orderSlipItem.service.readersFee / orderSlipItem.service.basePrice) * 100).setScale(0, RoundingMode.FLOOR)
                orderSlipItem.billing_item.rfDetails = new JsonBuilder(o).toString()
            }
            billingItemRepository.save(orderSlipItem.billing_item)
        }
        if (orderSlipItem.status == "COMPLETED") {
            Patient p = orderSlipItem.orderslip.parentCase.patient
            UUID deptID = orderSlipItem.orderslip.parentCase.department.id

            notificationService.notifyUsersOfDepartment(deptID, "Procedure Complete",
                    "Procedure " + orderSlipItem.service.serviceName + " for " + p.lastName + " " + p.firstName[0] + ". has now been completed", "")
        }
    }

    @HandleAfterSave
    handleAfterSaveService(Service services) {
        if (services.readersFee > 0) {
            def percentage = (services.readersFee / services.basePrice) * 100

            services.rfPercentage = percentage
            serviceRepository.save(services)

        }
    }
}
