package com.hisd3.hismk2.services.eclaims.generalservices

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.billing.BillingItem
import com.hisd3.hismk2.domain.billing.BillingItemStatus
import com.hisd3.hismk2.domain.billing.BillingItemType
import com.hisd3.hismk2.domain.eclaims.EclaimsCaseRef
import com.hisd3.hismk2.domain.hospital_config.ComlogikSetting
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.billing.BillingService
import com.hisd3.hismk2.repository.hospital_config.ComlogikSettingRepository
import com.hisd3.hismk2.repository.inventory.ItemRepository
import com.hisd3.hismk2.repository.pms.CaseRepository
import com.hisd3.hismk2.repository.pms.MedicationRepository
import com.hisd3.hismk2.repository.pms.PatientOwnMedicineRepository
import com.hisd3.hismk2.rest.dto.ComlogikItemDto
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.services.eclaims.stash.EcMedsCont
import com.hisd3.hismk2.services.eclaims.stash.EcStructuredMeds
import com.hisd3.hismk2.services.eclaims.stash.StashItemDto
import com.squareup.okhttp.HttpUrl
import com.squareup.okhttp.MediaType
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.RequestBody
import com.squareup.okhttp.Response
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import org.json.JSONArray
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import java.text.DecimalFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Component
@TypeChecked
class EcHISClaimsMedicine{

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    ComlogikSettingRepository comlogikSettingRepository

    @Autowired
    MedicationRepository medicationRepository

    @Autowired
    PatientOwnMedicineRepository patientOwnMedicineRepository

    @Autowired
    CaseRepository caseRepository

    @Autowired
    BillingService billingService

    @Autowired
    ItemRepository itemRepository

    Object getHISClaimsMedicines( UUID caseId, UUID billingId, Boolean includePatientMeds ){
//        List<ComlogikSetting> comlogikSetting = comlogikSettingRepository.findAll()
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

            def listComlogikDto = new ArrayList<StashItemDto>()
            def listComlogikDto2 = new ArrayList<StashItemDto>()
            def subTotal = 0.0

            fillteredMeds.each { entry ->
                if (entry.details.containsKey('ITEMID')) {
                    def invItem = itemRepository.findById(UUID.fromString(entry.details['ITEMID'])).get()
                    StashItemDto itemDto = new StashItemDto()
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
                        itemDto.medPnf = invItem.pnf
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

                        StashItemDto dto = new StashItemDto()

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
                            dto.philhealthQuantity = entry.qty_onhand ? entry.qty_onhand.toInteger() : 1
//							dto.philhealthQuantity = entry.qty_onhand.toInteger()
                            dto.philhealthActualUnitPrice = 0
                            if (entry.entry_datetime != null) {
                                dto.philhealthPurchaseDate = dateFormat.format(entry.entry_datetime)
                            } else {
                                dto.philhealthPurchaseDate = ""
                            }
                            dto.philhealthPreparation = entry.description
                            dto.medPnf = ""

                            listComlogikDto2.add(dto)
                        } else {
                            exist.philhealthQuantity += entry.qty_onhand.toInteger()
                        }


                }

            }


            listComlogikDto.addAll(listComlogikDto2)


            listComlogikDto.findAll { it.philhealthQuantity != 0 }.each { entry ->
//                EcStructuredMeds med = new EcStructuredMeds()
                def med = new JSONObject()
                def medDto = meds.find {
                    it.medicine.id == entry.id
                }



//Test
                med.put('id', 0)
                med.put("drugcode", entry.philhealthDrugCodePhic?:"")
                med.put("medicinedesc", entry.philhealthGenericName ?: "")
                med.put("medPnf", entry.medPnf ?: "")

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

                //Test
//                med.id = 0
//                med.drugcode = entry.philhealthDrugCodePhic?:""
//                med.medicinedesc = entry.philhealthGenericName ?: ""
//
//
//
//
//                if(medDto){
//                    med.route =  medDto.route?:""
//                    med.freq = medDto.frequency?:""
//                }else {
//                    med.route =  "N/A"
//                    med.freq = ""
//                }
//
//                med.purchasedate = entry.philhealthPurchaseDate ?: ""
//                med.qty = entry.philhealthQuantity ?: 0
//
//                if (entry.philhealthActualUnitPrice) {
//                    med.price =  entry.philhealthActualUnitPrice
//                } else {
//                    med.price = "0.00"
////                    med.put("price", "0.00")
//                }


                formsArry.put(med)
            }
        }

        String finalResp
        def outMeds = new JSONArray()
//        def outMeds = new JSONArray()
        if (formsArry) {

            for(def i in 0..formsArry.length()-1){
                def  dto = formsArry.getJSONObject(i)
//				TESTING
//                def buildDto =  new JSONObject( dto )

//                ObjectMapper mapper = new ObjectMapper();
//                EcStructuredMeds usr = mapper.readValue(dto.toString(), EcStructuredMeds.class);

//                outMeds.put(usr)
//                println(usr)
                 outMeds.put(dto)
//                println(dto)
//

//				println(formsArry)
//				TESTING

                //				TESTING
            }

        }

        println(outMeds)

        return  outMeds

    }



}
