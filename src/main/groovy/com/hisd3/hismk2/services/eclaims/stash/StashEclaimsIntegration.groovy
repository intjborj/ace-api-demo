package com.hisd3.hismk2.services.eclaims.stash

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.billing.BillingItem
import com.hisd3.hismk2.domain.billing.BillingItemStatus
import com.hisd3.hismk2.domain.billing.BillingItemType
import com.hisd3.hismk2.domain.eclaims.EclaimsIntegrationSetting
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.graphqlservices.billing.BillingService
import com.hisd3.hismk2.repository.eclaims.EClaimsIntegration
import com.hisd3.hismk2.repository.eclaims.EcProviderRepository
import com.hisd3.hismk2.repository.hospital_config.ComlogikSettingRepository
import com.hisd3.hismk2.repository.inventory.ItemRepository
import com.hisd3.hismk2.repository.pms.CaseRepository
import com.hisd3.hismk2.repository.pms.MedicationRepository
import com.hisd3.hismk2.repository.pms.PatientOwnMedicineRepository
import com.hisd3.hismk2.rest.dto.ComlogikItemDto
import com.hisd3.hismk2.services.eclaims.generalservices.EcIntegrationAccountService
import com.squareup.okhttp.*
import groovy.json.JsonSlurper
import groovy.transform.TypeChecked
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import org.json.JSONArray
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

import java.text.DecimalFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Service("StashEclaimsIntegration")
@TypeChecked
@Component
@GraphQLApi
class StashEclaimsIntegration implements EClaimsIntegration{


    String eclaimProvider = "Stash"


    @Autowired
    ObjectMapper objectMapper

    @Autowired
    EcIntegrationAccountService ecIntegrationAccountService

    @Autowired
    EcProviderRepository ecProviderRepository

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

    @Override
     integrationAuth(Map<String, Object> fields, UUID id) {
        EclaimsIntegrationSetting provSetting = ecProviderRepository.findByProvider(eclaimProvider)


        String  json =  new JSONObject(fields);


        OkHttpClient client = new OkHttpClient()

        MediaType mediaType = MediaType.parse("application/json;charset=UTF-8")
        RequestBody body = RequestBody.create(mediaType, json ?: "")
        def request = new com.squareup.okhttp.Request.Builder()
                .url(provSetting.host + "/api/v1/account/auth")
                .method("POST", body)
                .addHeader("Content-Type", "application/json;charset=UTF-8")
                .build()
        Response response = client.newCall(request).execute()

        Object respObj = new JsonSlurper().parseText(response.body().string())

        return respObj["data"]["token"]
    }

    @Override
     searchCaseRates(Map<String, Object> fields) {
        EclaimsIntegrationSetting provSetting = ecProviderRepository.findByProvider(eclaimProvider)
        def cr = objectMapper.convertValue(fields, CaseRateDto)

        OkHttpClient client = new OkHttpClient()
        HttpUrl.Builder httpBuilder = HttpUrl.parse(provSetting.host + "/api/client/philhealth/caserate/").newBuilder()
        httpBuilder.addQueryParameter("icd", cr.icd)
        httpBuilder.addQueryParameter("rvs", cr.rvs)
        httpBuilder.addQueryParameter("description", cr.description)

        def request = new com.squareup.okhttp.Request.Builder()
                .url(httpBuilder.build())
                .method("GET", null)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "Bearer " + fields.token.toString())
                .build()
        Response response = client.newCall(request).execute()

        Object respObj = new JsonSlurper().parseText(response.body().string())

        return respObj
    }

     @Override
     searchPatientPin(Map<String, Object> fields) {
         EclaimsIntegrationSetting provSetting = ecProviderRepository.findByProvider(eclaimProvider)
        def pdet = objectMapper.convertValue(fields, IntegPatientDetailDto)

        def form = new ArrayList<NameValuePair>(2)
        form.add(new BasicNameValuePair("birthDate", pdet.birthDate))
        form.add(new BasicNameValuePair("firstName", pdet.firstName))
        form.add(new BasicNameValuePair("lastName", pdet.lastName))
        form.add(new BasicNameValuePair("middleName", pdet.middleName))
        form.add(new BasicNameValuePair("suffix", pdet.suffix))
        form.add(new BasicNameValuePair("pin", pdet.pin))
        form.add(new BasicNameValuePair("type", pdet.type))

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded")
        String requestBody = contentBuilder(form) ?: ""
        RequestBody body = RequestBody.create(mediaType, requestBody)

        OkHttpClient client = new OkHttpClient()
        HttpUrl.Builder httpBuilder = HttpUrl.parse(provSetting.host + "/api/client/philhealth/member/pin").newBuilder()
        def request = new com.squareup.okhttp.Request.Builder()
                .url(httpBuilder.build())
                .method("POST", body)
                .addHeader("Content-Type", "application/json;charset=UTF-8")
                .addHeader("Authorization", "Bearer " + fields.token.toString())
                .build()

        Response response = client.newCall(request).execute()

        Object respObj = new JsonSlurper().parseText(response.body().string())

        return respObj

    }

    @Override
     checkDrAccreditationValidation(Map<String, Object> fields) {
        EclaimsIntegrationSetting provSetting = ecProviderRepository.findByProvider(eclaimProvider)

        def dav = objectMapper.convertValue(fields, DoctorAccreValidation)
        def form = new ArrayList<NameValuePair>(2)
        form.add(new BasicNameValuePair("doctorAccreCode", dav.doctorAccreCode))
        form.add(new BasicNameValuePair("admissionDate", dav.admissionDate))
        form.add(new BasicNameValuePair("dischargeDate", dav.dischargeDate))
        form.add(new BasicNameValuePair("showResult", dav.showResult))

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded")
        String requestBody = contentBuilder(form) ?: ""
        RequestBody body = RequestBody.create(mediaType, requestBody)

        OkHttpClient client = new OkHttpClient()
        HttpUrl.Builder httpBuilder = HttpUrl.parse(provSetting.host + "/api/client/philhealth/doctor/accredited").newBuilder()
        def request = new com.squareup.okhttp.Request.Builder()
                .url(httpBuilder.build())
                .method("POST", body)
                .addHeader("Content-Type", "multipart/form-data")
                .addHeader("Authorization", "Bearer " + fields.token.toString())
                .build()

        Response response = client.newCall(request).execute()

        Object respObj = new JsonSlurper().parseText(response.body().string())

        return respObj
    }

    @Override
    Object createNewClaim(Map<String, Object> fields) {
        EclaimsIntegrationSetting provSetting = ecProviderRepository.findByProvider(eclaimProvider)

        String  json =  new JSONObject(fields);

        OkHttpClient client = new OkHttpClient()

        def methodSend =  fields.sendType.toString() == "update" ? "PUT" : "POST";

        MediaType mediaType = MediaType.parse("application/json;charset=UTF-8")
        RequestBody body = RequestBody.create(mediaType, json ?: "")
        def request = new com.squareup.okhttp.Request.Builder()
                .url(provSetting.host + "/api/client/philhealth/claims/")
                .method(methodSend, body)
//                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + fields.token.toString())
                .build()
        Response response = client.newCall(request).execute()

        Object respObj = new JsonSlurper().parseText(response.body().string())

        return respObj
    }

    @Override
    Object updateClaim(Map<String, Object> fields) {

        EclaimsIntegrationSetting provSetting = ecProviderRepository.findByProvider(eclaimProvider)

        String  json =  new JSONObject(fields);

        OkHttpClient client = new OkHttpClient()

        MediaType mediaType = MediaType.parse("application/json;charset=UTF-8")
        RequestBody body = RequestBody.create(mediaType, json ?: "")
        def request = new com.squareup.okhttp.Request.Builder()
                .url(provSetting.host + "/api/client/philhealth/claims/")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + fields.token.toString())
                .build()
        Response response = client.newCall(request).execute()

        Object respObj = new JsonSlurper().parseText(response.body().string())

        return respObj
    }

    @Override
    Object generateCf4(Map<String, Object> fields) {

        EclaimsIntegrationSetting provSetting = ecProviderRepository.findByProvider(eclaimProvider)

//        String  json =  fields;
        JSONObject  json1 =  new JSONObject(fields);
        json1.put("uuid", fields.uuid == null ? JSONObject.NULL : fields.uuid);

        String  json =  json1;
//        String  json =  new JSONObject(fields);

        OkHttpClient client = new OkHttpClient()
        def methodSend =  fields.sendType.toString() == "update" ? "PUT" : "POST";

        MediaType mediaType = MediaType.parse("application/json;charset=UTF-8")
        RequestBody body = RequestBody.create(mediaType, json ?: "")
//        RequestBody body = RequestBody.create(mediaType, json ?: "")
        def request = new com.squareup.okhttp.Request.Builder()
                .url(provSetting.host + "/api/client/philhealth/claims/cf4")
                .method(methodSend, body)
//                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + fields.token.toString())
                .build()
        Response response = client.newCall(request).execute()

        Object respObj = new JsonSlurper().parseText(response.body().string())

        return respObj

    }

    @Override
    Object createEligibility(Map<String, Object> fields) {

        EclaimsIntegrationSetting provSetting = ecProviderRepository.findByProvider(eclaimProvider)

        def dav = objectMapper.convertValue(fields, DoctorAccreValidation)
        def pdet = objectMapper.convertValue(fields, IntegPatientDetailDto)
        def mdet = objectMapper.convertValue(fields, IntegPhilhMemnerDetailDto)
        def el = objectMapper.convertValue(fields, IntegEligibilityDto)

        def form = new ArrayList<NameValuePair>(2)
        form.add(new BasicNameValuePair("totalAmountActual", el.totalAmountActual))
        form.add(new BasicNameValuePair("totalAmountClaimed",el.totalAmountClaimed))
        form.add(new BasicNameValuePair("member.firstName", mdet.mem_firstName))
        form.add(new BasicNameValuePair("member.lastName",mdet.mem_lastName))
        form.add(new BasicNameValuePair("member.middleName", mdet.mem_middleName))
        form.add(new BasicNameValuePair("member.suffix",mdet.mem_suffix))
        form.add(new BasicNameValuePair("member.type",mdet.mem_type))
        form.add(new BasicNameValuePair("member.pin", mdet.mem_pin))
        form.add(new BasicNameValuePair("patient.lastName", pdet.lastName))
        form.add(new BasicNameValuePair("patient.firstName", pdet.firstName))
        form.add(new BasicNameValuePair("patient.birthDate", pdet.birthDate))
        form.add(new BasicNameValuePair("patient.suffix",pdet.suffix))
        form.add(new BasicNameValuePair("patient.middleName",pdet.middleName))
        form.add(new BasicNameValuePair("patient.gender",pdet.gender))
        form.add(new BasicNameValuePair("patientIs",el.patientIs))
        form.add(new BasicNameValuePair("accreditation.admissionDate",dav.admissionDate))
        form.add(new BasicNameValuePair("accreditation.dischargeDate",dav.dischargeDate))
        form.add(new BasicNameValuePair("isFinal",el.isFinal ))
        form.add(new BasicNameValuePair("isDisabled",el.isDisabled))
        form.add(new BasicNameValuePair("member.birthDate", pdet.birthDate))

//        println(form)

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded")
        String requestBody = contentBuilder(form) ?: ""
        RequestBody body = RequestBody.create(mediaType, requestBody)

        OkHttpClient client = new OkHttpClient()
        HttpUrl.Builder httpBuilder = HttpUrl.parse(provSetting.host + "/api/client/philhealth/claims/eligibility").newBuilder()
        def request = new com.squareup.okhttp.Request.Builder()
                .url(httpBuilder.build())
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "Bearer " + fields.token.toString())
                .build()

        Response response = client.newCall(request).execute()

        Object respObj = new JsonSlurper().parseText(response.body().string())
//        println(respObj)
        return respObj

//        return []
    }

    @Override
    Object getEligibility(Map<String, Object> fields) {
        EclaimsIntegrationSetting provSetting = ecProviderRepository.findByProvider(eclaimProvider)

        OkHttpClient client = new OkHttpClient()
        HttpUrl.Builder httpBuilder = HttpUrl.parse(provSetting.host + "/api/client/philhealth/eligibility/" + fields.id.toString()).newBuilder()

        def request = new com.squareup.okhttp.Request.Builder()
                .url(httpBuilder.build())
                .method("GET", null)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "Bearer " + fields.token.toString())
                .build()
        Response response = client.newCall(request).execute()

        Object respObj = new JsonSlurper().parseText(response.body().string())

        return respObj
    }


    @Override
    Object getClaims(Map<String, Object> fields) {
        EclaimsIntegrationSetting provSetting = ecProviderRepository.findByProvider(eclaimProvider)

        OkHttpClient client = new OkHttpClient()
        HttpUrl.Builder httpBuilder = HttpUrl.parse(provSetting.host + "/api/client/philhealth/claims/" + fields.id.toString()).newBuilder()

        def request = new com.squareup.okhttp.Request.Builder()
                .url(httpBuilder.build())
                .method("GET", null)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "Bearer " + fields.token.toString())
                .build()
        Response response = client.newCall(request).execute()

        Object respObj = new JsonSlurper().parseText(response.body().string())

        return respObj
    }

    @Override
    Object getCf4(Map<String, Object> fields) {
        EclaimsIntegrationSetting provSetting = ecProviderRepository.findByProvider(eclaimProvider)

        OkHttpClient client = new OkHttpClient()
        HttpUrl.Builder httpBuilder = HttpUrl.parse(provSetting.host + "/api/client/philhealth/claims/cf4/" + fields.id.toString()).newBuilder()

        def request = new com.squareup.okhttp.Request.Builder()
                .url(httpBuilder.build())
                .method("GET", null)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "Bearer " + fields.token.toString())
                .build()
        Response response = client.newCall(request).execute()

        Object respObj = new JsonSlurper().parseText(response.body().string())

        return respObj
    }

    String contentBuilder(ArrayList<NameValuePair> params) {

        EclaimsIntegrationSetting provSetting = ecProviderRepository.findByProvider(eclaimProvider)

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



    @Override
    Object getHISMeds(UUID caseId, UUID billingId, Boolean includePatientMeds) {
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

        def listComlogikDto = new ArrayList<StashItemDto>()


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

//            def listComlogikDto = new ArrayList<ComlogikItemDto>()
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
                        itemDto.isPatientMed = false
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
//                            dto.philhealthDrugCode = "NOMED0000000000000000000000000"
//                            dto.philhealthDrugCode = ""
                            dto.philhealthPNDFCode = ""
                            dto.philhealthGenericName = entry.medicine_name ?: ""
                            dto.philhealthBrandName = ""
                            dto.philhealthQuantity = entry.qty_onhand ? (entry.qty_onhand.toInteger() < 0 ? 1 : entry.qty_onhand.toInteger()) : 1
//							dto.philhealthQuantity = entry.qty_onhand.toInteger()
                            dto.philhealthActualUnitPrice = 0
                            if (entry.entry_datetime != null) {
                                dto.philhealthPurchaseDate = dateFormat.format(entry.entry_datetime)
                            } else {
                                dto.philhealthPurchaseDate = ""
                            }
                            dto.philhealthPreparation = entry.description
                            dto.isPatientMed = true
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
                med.put("hisId", (entry.philhealthDrugCodePhic == "" ?:""))
                med.put("drugcode", entry.philhealthDrugCodePhic?:"")
//                med.put("drugcode", entry.philhealthDrugCodePhic?:"NOMED0000000000000000000000000")
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
                med.put("qty", entry.philhealthQuantity ?: 0)
                med.put("patientMed", entry.isPatientMed ?: false)
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

//        println(outMeds)
//        Object respObj = listComlogikDto
//        Object respObj = new JsonSlurper().parseText(listComlogikDto.toString())
        Object respObj = new JsonSlurper().parseText(outMeds.toString())
        EcMedsCont newMeds = new EcMedsCont()
        newMeds.id = ""
        newMeds.meds = respObj
//

//        return  respObj
        return  newMeds
//        return  outMeds

    }

    @Override
    Object getClaimsCaseNo(Map<String, Object> fields) {
        EclaimsIntegrationSetting provSetting = ecProviderRepository.findByProvider(eclaimProvider)

        OkHttpClient client = new OkHttpClient()
        HttpUrl.Builder httpBuilder = HttpUrl.parse(provSetting.host + "/api/client/philhealth/claims/referenceId/" + fields.id.toString()).newBuilder()

        def request = new com.squareup.okhttp.Request.Builder()
                .url(httpBuilder.build())
                .method("GET", null)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "Bearer " + fields.token.toString())
                .build()
        Response response = client.newCall(request).execute()

        Object respObj = new JsonSlurper().parseText(response.body().string())

        return respObj
    }

    @Override
    Object getClaimLibrary(Map<String, Object> fields) {

        EclaimsIntegrationSetting provSetting = ecProviderRepository.findByProvider(eclaimProvider)

        OkHttpClient client = new OkHttpClient()
        HttpUrl.Builder httpBuilder = HttpUrl.parse(provSetting.host + "/library/lib_json/" + fields.id.toString()).newBuilder()

        def request = new com.squareup.okhttp.Request.Builder()
                .url(httpBuilder.build())
                .method("GET", null)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "Bearer " + fields.token.toString())
                .build()
        Response response = client.newCall(request).execute()

        Object respObj = new JsonSlurper().parseText(response.body().string())

        return respObj
    }
}
