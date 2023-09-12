package com.hisd3.hismk2.rest

import com.hisd3.hismk2.utils.SOAPConnector
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ph.gov.doh.uhmistrn.ahsr.webservice.index.*

@RestController
class DohResource {
	
	@Autowired
	SOAPConnector soapConnector
	
	@RequestMapping(value = "/getDataTable", produces = "text/xml")
	String getDataTable(
			@RequestParam("hfhudcode") String hfhudcode,
			@RequestParam("reportingyear") String reportingyear,
			@RequestParam("table") String table
	) {
		
		GetDataTable request = new GetDataTable()
		request.hfhudcode = hfhudcode
		request.reportingyear = reportingyear
		request.table = table
		
		GetDataTableResponse response =
				(GetDataTableResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/getDataTable", request)
		return response.return
	}
	
	@RequestMapping(value = "/genInfoClassification", produces = "text/xml")
	String genInfoClassification(
			@RequestParam("hfhudcode") String hfhudcode,
			@RequestParam("servicecapability") String servicecapability,
			@RequestParam("general") String general,
			@RequestParam("specialty") String specialty,
			@RequestParam("specialtyspecify") String specialtyspecify,
			@RequestParam("traumacapability") String traumacapability,
			@RequestParam("natureofownership") String natureofownership,
			@RequestParam("government") String government,
			@RequestParam("national") String national,
			@RequestParam("local") String local,
			@RequestParam("private") String privated,
			@RequestParam("reportingyear") String reportingyear,
			@RequestParam("ownershipothers") String ownershipothers
	) {
		GenInfoClassification request = new GenInfoClassification()
		request.hfhudcode = hfhudcode
		request.servicecapability = servicecapability
		request.general = general
		request.specialty = specialty
		request.specialtyspecify = specialtyspecify
		request.traumacapability = traumacapability
		request.natureofownership = natureofownership
		request.government = government
		request.national = national
		request.local = local
		request.private = privated
		request.reportingyear = reportingyear
		request.ownershipothers = ownershipothers
		
		GenInfoClassificationResponse response =
				(GenInfoClassificationResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/genInfoClassification", request)
		return response.return
	}
	
	@RequestMapping(value = "/genInfoQualityManagement", produces = "text/xml")
	String genInfoQualityManagement(
			@RequestParam("hfhudcode") String hfhudcode,
			@RequestParam("qualitymgmttype") String qualitymgmttype,
			@RequestParam("description") String description,
			@RequestParam("certifyingbody") String certifyingbody,
			@RequestParam("philhealthaccreditation") String philhealthaccreditation,
			@RequestParam("validityfrom") String validityfrom,
			@RequestParam("validityto") String validityto,
			@RequestParam("reportingyear") String reportingyear
	) {
		GenInfoQualityManagement request = new GenInfoQualityManagement()
		request.hfhudcode = hfhudcode
		request.qualitymgmttype = qualitymgmttype
		request.description = description
		request.certifyingbody = certifyingbody
		request.philhealthaccreditation = philhealthaccreditation
		request.validityfrom = validityfrom
		request.validityto = validityto
		request.reportingyear = reportingyear
		
		GenInfoQualityManagementResponse response =
				(GenInfoQualityManagementResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/genInfoQualityManagement", request)
		return response.return
	}
	
	@RequestMapping(value = "/genInfoBedCapacity", produces = "text/xml")
	String genInfoBedCapacity(
			@RequestParam("hfhudcode") String hfhudcode,
			@RequestParam("abc") String abc,
			@RequestParam("implementingbeds") String implementingbeds,
			@RequestParam("bor") String bor,
			@RequestParam("reportingyear") String reportingyear
	) {
		GenInfoBedCapacity request = new GenInfoBedCapacity()
		request.hfhudcode = hfhudcode
		request.abc = abc
		request.implementingbeds = implementingbeds
		request.bor = bor
		request.reportingyear = reportingyear
		
		GenInfoBedCapacityResponse response =
				(GenInfoBedCapacityResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/genInfoBedCapacity", request)
		return response.return
	}
	
	@RequestMapping(method = [RequestMethod.POST], value = "/hospOptSummaryOfPatients", produces = "text/xml")
	String hospOptSummaryOfPatients(
			@RequestParam("hfhudcode") String hfhudcode,
			@RequestParam("totalinpatients") String totalinpatients,
			@RequestParam("totalnewborn") String totalnewborn,
			@RequestParam("totaldischarges") String totaldischarges,
			@RequestParam("totalpad") String totalpad,
			@RequestParam("totalibd") String totalibd,
			@RequestParam("totalinpatienttransto") String totalinpatienttransto,
			@RequestParam("totalinpatienttransfrom") String totalinpatienttransfrom,
			@RequestParam("totalpatientsremaining") String totalpatientsremaining,
			@RequestParam("reportingyear") String reportingyear
	
	) {
		HospOptSummaryOfPatients request = new HospOptSummaryOfPatients()
		request.hfhudcode = hfhudcode
		request.totalinpatients = totalinpatients
		request.totalnewborn = totalnewborn
		request.totaldischarges = totaldischarges
		request.totalpad = totalpad
		request.totalibd = totalibd
		request.totalinpatienttransto = totalinpatienttransto
		request.totalinpatienttransfrom = totalinpatienttransfrom
		request.totalpatientsremaining = totalpatientsremaining
		request.reportingyear = reportingyear
		
		HospOptSummaryOfPatientsResponse response =
				(HospOptSummaryOfPatientsResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/hospOptSummaryOfPatients", request)
		return response.return
	}
	
	@RequestMapping(method = [RequestMethod.POST], value = "/hospOptDischargesSpecialty", produces = "text/xml")
	String hospOptDischargesSpecialty(
			@RequestParam("hfhudcode") String hfhudcode,
			@RequestParam("typeofservice") String typeofservice,
			@RequestParam("nopatients") String nopatients,
			@RequestParam("totallengthstay") String totallengthstay,
			@RequestParam("nppay") String nppay,
			@RequestParam("nphservicecharity") String nphservicecharity,
			@RequestParam("nphtotal") String nphtotal,
			@RequestParam("phpay") String phpay,
			@RequestParam("phservice") String phservice,
			@RequestParam("phtotal") String phtotal,
			@RequestParam("hmo") String hmo,
			@RequestParam("owwa") String owwa,
			@RequestParam("recoveredimproved") String recoveredimproved,
			@RequestParam("transferred") String transferred,
			@RequestParam("hama") String hama,
			@RequestParam("absconded") String absconded,
			@RequestParam("unimproved") String unimproved,
			@RequestParam("deathsbelow48") String deathsbelow48,
			@RequestParam("deathsover48") String deathsover48,
			@RequestParam("totaldeaths") String totaldeaths,
			@RequestParam("totaldischarges") String totaldischarges,
			@RequestParam("remarks") String remarks,
			@RequestParam("reportingyear") String reportingyear
	) {
		HospOptDischargesSpecialty request = new HospOptDischargesSpecialty()
		request.hfhudcode = hfhudcode
		request.typeofservice = typeofservice
		request.nopatients = nopatients
		request.totallengthstay = totallengthstay
		request.nppay = nppay
		request.nphservicecharity = nphservicecharity
		request.nphtotal = nphtotal
		request.phpay = phpay
		request.phservice = phservice
		request.phtotal = phtotal
		request.hmo = hmo
		request.owwa = owwa
		request.recoveredimproved = recoveredimproved
		request.transferred = transferred
		request.hama = hama
		request.absconded = absconded
		request.unimproved = unimproved
		request.deathsbelow48 = deathsbelow48
		request.deathsover48 = deathsover48
		request.totaldeaths = totaldeaths
		request.totaldischarges = totaldischarges
		request.remarks = remarks
		request.reportingyear = reportingyear
		
		HospOptDischargesSpecialtyResponse response =
				(HospOptDischargesSpecialtyResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/hospOptDischargesSpecialty", request)
		return response.return
	}
	
	@RequestMapping(method = [RequestMethod.POST], value = "/hospOptDischargesSpecialtyOthers", produces = "text/xml")
	String hospOptDischargesSpecialtyOthers(
			@RequestParam("hfhudcode") String hfhudcode,
			@RequestParam("othertypeofservicespecify") String othertypeofservicespecify,
			@RequestParam("nopatients") String nopatients,
			@RequestParam("totallengthstay") String totallengthstay,
			@RequestParam("nppay") String nppay,
			@RequestParam("nphservicecharity") String nphservicecharity,
			@RequestParam("nphtotal") String nphtotal,
			@RequestParam("phpay") String phpay,
			@RequestParam("phservice") String phservice,
			@RequestParam("phtotal") String phtotal,
			@RequestParam("hmo") String hmo,
			@RequestParam("owwa") String owwa,
			@RequestParam("recoveredimproved") String recoveredimproved,
			@RequestParam("transferred") String transferred,
			@RequestParam("hama") String hama,
			@RequestParam("absconded") String absconded,
			@RequestParam("unimproved") String unimproved,
			@RequestParam("deathsbelow48") String deathsbelow48,
			@RequestParam("deathsover48") String deathsover48,
			@RequestParam("totaldeaths") String totaldeaths,
			@RequestParam("totaldischarges") String totaldischarges,
			@RequestParam("remarks") String remarks,
			@RequestParam("reportingyear") String reportingyear
	) {
		HospOptDischargesSpecialtyOthers request = new HospOptDischargesSpecialtyOthers()
		request.hfhudcode = hfhudcode
		request.othertypeofservicespecify = othertypeofservicespecify
		request.nopatients = nopatients
		request.totallengthstay = totallengthstay
		request.nppay = nppay
		request.nphservicecharity = nphservicecharity
		request.nphtotal = nphtotal
		request.phpay = phpay
		request.phservice = phservice
		request.phtotal = phtotal
		request.hmo = hmo
		request.owwa = owwa
		request.recoveredimproved = recoveredimproved
		request.transferred = transferred
		request.hama = hama
		request.absconded = absconded
		request.unimproved = unimproved
		request.deathsbelow48 = deathsbelow48
		request.deathsover48 = deathsover48
		request.totaldeaths = totaldeaths
		request.totaldischarges = totaldischarges
		request.remarks = remarks
		request.reportingyear = reportingyear
		
		HospOptDischargesSpecialtyOthersResponse response =
				(HospOptDischargesSpecialtyOthersResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/hospOptDischargesSpecialtyOthers", request)
		return response.return
		
	}
	
	@RequestMapping(method = [RequestMethod.POST], value = "/hospOptDischargesMorbidity", produces = "text/xml")
	String hospOptDischargesMorbidity(
			@RequestParam("hfhudcode") String hfhudcode,
			@RequestParam("icd10desc") String icd10desc,
			@RequestParam("munder1") String munder1,
			@RequestParam("funder1") String funder1,
			@RequestParam("m1to4") String m1to4,
			@RequestParam("f1to4") String f1to4,
			@RequestParam("m5to9") String m5to9,
			@RequestParam("f5to9") String f5to9,
			@RequestParam("m10to14") String m10to14,
			@RequestParam("f10to14") String f10to14,
			@RequestParam("m15to19") String m15to19,
			@RequestParam("f15to19") String f15to19,
			@RequestParam("m20to24") String m20to24,
			@RequestParam("f20to24") String f20to24,
			@RequestParam("m25to29") String m25to29,
			@RequestParam("f25to29") String f25to29,
			@RequestParam("m30to34") String m30to34,
			@RequestParam("f30to34") String f30to34,
			@RequestParam("m35to39") String m35to39,
			@RequestParam("f35to39") String f35to39,
			@RequestParam("m40to44") String m40to44,
			@RequestParam("f40to44") String f40to44,
			@RequestParam("m45to49") String m45to49,
			@RequestParam("f45to49") String f45to49,
			@RequestParam("m50to54") String m50to54,
			@RequestParam("f50to54") String f50to54,
			@RequestParam("m55to59") String m55to59,
			@RequestParam("f55to59") String f55to59,
			@RequestParam("m60to64") String m60to64,
			@RequestParam("f60to64") String f60to64,
			@RequestParam("m65to69") String m65to69,
			@RequestParam("f65to69") String f65to69,
			@RequestParam("m70over") String m70over,
			@RequestParam("f70over") String f70over,
			@RequestParam("msubtotal") String msubtotal,
			@RequestParam("fsubtotal") String fsubtotal,
			@RequestParam("grandtotal") String grandtotal,
			@RequestParam("icd10code") String icd10code,
			@RequestParam("icd10category") String icd10category,
			@RequestParam("reportingyear") String reportingyear
	) {
		HospOptDischargesMorbidity request = new HospOptDischargesMorbidity()
		request.hfhudcode = hfhudcode
		request.icd10Desc = icd10desc
		request.munder1 = munder1
		request.funder1 = funder1
		request.m1To4 = m1to4
		request.f1To4 = f1to4
		request.m5To9 = m5to9
		request.f5To9 = f5to9
		request.m10To14 = m10to14
		request.f10To14 = f10to14
		request.m15To19 = m15to19
		request.f15To19 = f15to19
		request.m20To24 = m20to24
		request.f20To24 = f20to24
		request.m25To29 = m25to29
		request.f25To29 = f25to29
		request.m30To34 = m30to34
		request.f30To34 = f30to34
		request.m35To39 = m35to39
		request.f35To39 = f35to39
		request.m40To44 = m40to44
		request.f40To44 = f40to44
		request.m45To49 = m45to49
		request.f45To49 = f45to49
		request.m50To54 = m50to54
		request.f50To54 = f50to54
		request.m55To59 = m55to59
		request.f55To59 = f55to59
		request.m60To64 = m60to64
		request.f60To64 = f60to64
		request.m65To69 = m65to69
		request.f65To69 = f65to69
		request.m70Over = m70over
		request.f70Over = f70over
		request.msubtotal = msubtotal
		request.fsubtotal = fsubtotal
		request.grandtotal = grandtotal
		request.icd10Code = icd10code
		request.icd10Category = icd10category
		request.reportingyear = reportingyear
		
		HospOptDischargesMorbidityResponse response =
				(HospOptDischargesMorbidityResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/hospOptDischargesMorbidity", request)
		return response.return
	}
	
	@RequestMapping(method = [RequestMethod.POST], value = "/hospOptDischargesNumberDeliveries", produces = "text/xml")
	String hospOptDischargesNumberDeliveries(
			@RequestParam("hfhudcode") String hfhudcode,
			@RequestParam("totalifdelivery") String totalifdelivery,
			@RequestParam("totallbvdelivery") String totallbvdelivery,
			@RequestParam("totallbcdelivery") String totallbcdelivery,
			@RequestParam("totalotherdelivery") String totalotherdelivery,
			@RequestParam("reportingyear") String reportingyear
	) {
		HospOptDischargesNumberDeliveries request = new HospOptDischargesNumberDeliveries()
		request.hfhudcode = hfhudcode
		request.totalifdelivery = totalifdelivery
		request.totallbvdelivery = totallbvdelivery
		request.totallbcdelivery = totallbcdelivery
		request.totalotherdelivery = totalotherdelivery
		request.reportingyear = reportingyear
		
		HospOptDischargesNumberDeliveriesResponse response =
				(HospOptDischargesNumberDeliveriesResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/hospOptDischargesNumberDeliveries", request)
		return response.return
		
	}
	
	@RequestMapping(method = [RequestMethod.POST], value = "/hospOptDischargesOPV", produces = "text/xml")
	String hospOptDischargesOPV(
			@RequestParam("hfhudcode") String hfhudcode,
			@RequestParam("newpatient") String newpatient,
			@RequestParam("revisit") String revisit,
			@RequestParam("adult") String adult,
			@RequestParam("pediatric") String pediatric,
			@RequestParam("adultgeneralmedicine") String adultgeneralmedicine,
			@RequestParam("specialtynonsurgical") String specialtynonsurgical,
			@RequestParam("surgical") String surgical,
			@RequestParam("antenatal") String antenatal,
			@RequestParam("postnatal") String postnatal,
			@RequestParam("reportingyear") String reportingyear
	) {
		HospOptDischargesOPV request = new HospOptDischargesOPV()
		request.hfhudcode = hfhudcode
		request.newpatient = newpatient
		request.revisit = revisit
		request.adult = adult
		request.pediatric = pediatric
		request.adultgeneralmedicine = adultgeneralmedicine
		request.specialtynonsurgical = specialtynonsurgical
		request.surgical = surgical
		request.antenatal = antenatal
		request.postnatal = postnatal
		request.reportingyear = reportingyear
		
		HospOptDischargesOPVResponse response =
				(HospOptDischargesOPVResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/hospOptDischargesOPV", request)
		return response.return
	}
	
	@RequestMapping(method = [RequestMethod.POST], value = "/hospOptDischargesOPD", produces = "text/xml")
	String hospOptDischargesOPD(
			@RequestParam("hfhudcode") String hfhudcode,
			@RequestParam("opdconsultations") String opdconsultations,
			@RequestParam("number") String number,
			@RequestParam("icd10code") String icd10code,
			@RequestParam("icd10category") String icd10category,
			@RequestParam("reportingyear") String reportingyear
	) {
		HospOptDischargesOPD request = new HospOptDischargesOPD()
		request.hfhudcode = hfhudcode
		request.opdconsultations = opdconsultations
		request.number = number
		request.icd10Code = icd10code
		request.icd10Category = icd10category
		request.reportingyear = reportingyear
		
		HospOptDischargesOPDResponse response =
				(HospOptDischargesOPDResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/hospOptDischargesOPD", request)
		return response.return
	}
	
	@RequestMapping(method = [RequestMethod.POST], value = "/hospOptDischargesER", produces = "text/xml")
	String hospOptDischargesER(
			@RequestParam("hfhudcode") String hfhudcode,
			@RequestParam("erconsultations") String erconsultations,
			@RequestParam("number") String number,
			@RequestParam("icd10code") String icd10code,
			@RequestParam("icd10category") String icd10category,
			@RequestParam("reportingyear") String reportingyear
	) {
		HospOptDischargesER request = new HospOptDischargesER()
		request.hfhudcode = hfhudcode
		request.erconsultations = erconsultations
		request.number = number
		request.icd10Code = icd10code
		request.icd10Category = icd10category
		request.reportingyear = reportingyear
		
		HospOptDischargesERResponse response =
				(HospOptDischargesERResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/hospOptDischargesER", request)
		return response.return
	}
	
	@RequestMapping(method = [RequestMethod.POST], value = "/hospOptDischargesTesting", produces = "text/xml")
	String hospOptDischargesTesting(
			@RequestParam("hfhudcode") String hfhudcode,
			@RequestParam("testinggroup") String testinggroup,
			@RequestParam("testing") String testing,
			@RequestParam("number") String number,
			@RequestParam("reportingyear") String reportingyear
	) {
		HospOptDischargesTesting request = new HospOptDischargesTesting()
		request.hfhudcode = hfhudcode
		request.testinggroup = testinggroup
		request.testing = testing
		request.number = number
		request.reportingyear = reportingyear
		
		HospOptDischargesTestingResponse response =
				(HospOptDischargesTestingResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/hospOptDischargesTesting", request)
		return response.return
	}
	
	@RequestMapping(method = [RequestMethod.POST], value = "/hospOptDischargesEV", produces = "text/xml")
	String hospOptDischargesEV(
			@RequestParam("hfhudcode") String hfhudcode,
			@RequestParam("emergencyvisits") String emergencyvisits,
			@RequestParam("emergencyvisitsadult") String emergencyvisitsadult,
			@RequestParam("emergencyvisitspediatric") String emergencyvisitspediatric,
			@RequestParam("evfromfacilitytoanother") String evfromfacilitytoanother,
			@RequestParam("reportingyear") String reportingyear
	) {
		HospOptDischargesEV request = new HospOptDischargesEV()
		request.hfhudcode = hfhudcode
		request.emergencyvisits = emergencyvisits
		request.emergencyvisitsadult = emergencyvisitsadult
		request.emergencyvisitspediatric = emergencyvisitspediatric
		request.evfromfacilitytoanother = evfromfacilitytoanother
		request.reportingyear = reportingyear
		
		HospOptDischargesEVResponse response =
				(HospOptDischargesEVResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/hospOptDischargesEV", request)
		return response.return
	}
	
	@RequestMapping(method = [RequestMethod.POST], value = "/hospitalOperationsDeaths", produces = "text/xml")
	String hospitalOperationsDeaths(
			@RequestParam("hfhudcode") String hfhudcode,
			@RequestParam("totaldeaths") String totaldeaths,
			@RequestParam("totaldeaths48down") String totaldeaths48down,
			@RequestParam("totaldeaths48up") String totaldeaths48up,
			@RequestParam("totalerdeaths") String totalerdeaths,
			@RequestParam("totaldoa") String totaldoa,
			@RequestParam("totalstillbirths") String totalstillbirths,
			@RequestParam("totalneonataldeaths") String totalneonataldeaths,
			@RequestParam("totalmaternaldeaths") String totalmaternaldeaths,
			@RequestParam("totaldeathsnewborn") String totaldeathsnewborn,
			@RequestParam("totaldischargedeaths") String totaldischargedeaths,
			@RequestParam("grossdeathrate") String grossdeathrate,
			@RequestParam("ndrnumerator") String ndrnumerator,
			@RequestParam("ndrdenominator") String ndrdenominator,
			@RequestParam("netdeathrate") String netdeathrate,
			@RequestParam("reportingyear") String reportingyear
	) {
		HospitalOperationsDeaths request = new HospitalOperationsDeaths()
		request.hfhudcode = hfhudcode
		request.totaldeaths = totaldeaths
		request.totaldeaths48Down = totaldeaths48down
		request.totaldeaths48Up = totaldeaths48up
		request.totalerdeaths = totalerdeaths
		request.totaldoa = totaldoa
		request.totalstillbirths = totalstillbirths
		request.totalneonataldeaths = totalneonataldeaths
		request.totalmaternaldeaths = totalmaternaldeaths
		request.totaldeathsnewborn = totaldeathsnewborn
		request.totaldischargedeaths = totaldischargedeaths
		request.grossdeathrate = grossdeathrate
		request.ndrnumerator = ndrdenominator
		request.ndrdenominator = ndrdenominator
		request.netdeathrate = netdeathrate
		request.reportingyear = reportingyear
		
		HospitalOperationsDeathsResponse response =
				(HospitalOperationsDeathsResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/hospitalOperationsDeaths", request)
		return response.return
	}
	
	@RequestMapping(method = [RequestMethod.POST], value = "/hospitalOperationsMortalityDeaths", produces = "text/xml")
	String hospitalOperationsMortalityDeaths(
			@RequestParam("hfhudcode") String hfhudcode,
			@RequestParam("icd10desc") String icd10desc,
			@RequestParam("munder1") String munder1,
			@RequestParam("funder1") String funder1,
			@RequestParam("m1to4") String m1to4,
			@RequestParam("f1to4") String f1to4,
			@RequestParam("m5to9") String m5to9,
			@RequestParam("f5to9") String f5to9,
			@RequestParam("m10to14") String m10to14,
			@RequestParam("f10to14") String f10to14,
			@RequestParam("m15to19") String m15to19,
			@RequestParam("f15to19") String f15to19,
			@RequestParam("m20to24") String m20to24,
			@RequestParam("f20to24") String f20to24,
			@RequestParam("m25to29") String m25to29,
			@RequestParam("f25to29") String f25to29,
			@RequestParam("m30to34") String m30to34,
			@RequestParam("f30to34") String f30to34,
			@RequestParam("m35to39") String m35to39,
			@RequestParam("f35to39") String f35to39,
			@RequestParam("m40to44") String m40to44,
			@RequestParam("f40to44") String f40to44,
			@RequestParam("m45to49") String m45to49,
			@RequestParam("f45to49") String f45to49,
			@RequestParam("m50to54") String m50to54,
			@RequestParam("f50to54") String f50to54,
			@RequestParam("m55to59") String m55to59,
			@RequestParam("f55to59") String f55to59,
			@RequestParam("m60to64") String m60to64,
			@RequestParam("f60to64") String f60to64,
			@RequestParam("m65to69") String m65to69,
			@RequestParam("f65to69") String f65to69,
			@RequestParam("m70over") String m70over,
			@RequestParam("f70over") String f70over,
			@RequestParam("msubtotal") String msubtotal,
			@RequestParam("fsubtotal") String fsubtotal,
			@RequestParam("grandtotal") String grandtotal,
			@RequestParam("icd10code") String icd10code,
			@RequestParam("icd10category") String icd10category,
			@RequestParam("reportingyear") String reportingyear
	) {
		HospitalOperationsMortalityDeaths request = new HospitalOperationsMortalityDeaths()
		request.hfhudcode = hfhudcode
		request.icd10Desc = icd10desc
		request.munder1 = munder1
		request.funder1 = funder1
		request.m1To4 = m1to4
		request.f1To4 = f1to4
		request.m5To9 = m5to9
		request.m10To14 = m10to14
		request.f10To14 = f10to14
		request.m15To19 = m15to19
		request.f15To19 = f15to19
		request.m20To24 = m20to24
		request.f20To24 = f20to24
		request.m25To29 = m25to29
		request.f25To29 = f25to29
		request.m30To34 = m30to34
		request.f30To34 = f30to34
		request.m35To39 = m35to39
		request.f35To39 = f35to39
		request.m40To44 = m40to44
		request.f40To44 = f40to44
		request.m45To49 = m45to49
		request.f45To49 = f45to49
		request.m50To54 = m50to54
		request.f50To54 = f50to54
		request.m55To59 = m55to59
		request.f55To59 = f55to59
		request.m60To64 = m60to64
		request.f60To64 = f60to64
		request.m65To69 = m65to69
		request.f65To69 = f65to69
		request.m70Over = m70over
		request.f70Over = f70over
		request.msubtotal = msubtotal
		request.fsubtotal = fsubtotal
		request.grandtotal = grandtotal
		request.icd10Code = icd10code
		request.icd10Category = icd10category
		request.reportingyear = reportingyear
		
		HospitalOperationsMortalityDeathsResponse response =
				(HospitalOperationsMortalityDeathsResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/hospitalOperationsMortalityDeaths", request)
		return response.return
	}
	
	@RequestMapping(method = [RequestMethod.POST], value = "/hospitalOperationsHAI", produces = "text/xml")
	String hospitalOperationsHAI(
			@RequestParam("hfhudcode") String hfhudcode,
			@RequestParam("numhai") String numhai,
			@RequestParam("numdischarges") String numdischarges,
			@RequestParam("infectionrate") String infectionrate,
			@RequestParam("patientnumvap") String patientnumvap,
			@RequestParam("totalventilatordays") String totalventilatordays,
			@RequestParam("resultvap") String resultvap,
			@RequestParam("patientnumbsi") String patientnumbsi,
			@RequestParam("totalnumcentralline") String totalnumcentralline,
			@RequestParam("resultbsi") String resultbsi,
			@RequestParam("patientnumuti") String patientnumuti,
			@RequestParam("totalcatheterdays") String totalcatheterdays,
			@RequestParam("resultuti") String resultuti,
			@RequestParam("numssi") String numssi,
			@RequestParam("totalproceduresdone") String totalproceduresdone,
			@RequestParam("resultssi") String resultssi,
			@RequestParam("reportingyear") String reportingyear
	) {
		HospitalOperationsHAI request = new HospitalOperationsHAI()
		request.hfhudcode = hfhudcode
		request.numhai = numhai
		request.numdischarges = numdischarges
		request.infectionrate = infectionrate
		request.patientnumvap = patientnumvap
		request.totalventilatordays = totalventilatordays
		request.resultvap = resultvap
		request.patientnumbsi = patientnumbsi
		request.totalnumcentralline = totalnumcentralline
		request.resultbsi = resultbsi
		request.patientnumuti = patientnumuti
		request.totalcatheterdays = totalcatheterdays
		request.resultuti = resultuti
		request.numssi = numssi
		request.totalproceduresdone = totalproceduresdone
		request.resultssi = resultssi
		request.reportingyear = reportingyear
		
		HospitalOperationsHAIResponse response =
				(HospitalOperationsHAIResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/hospitalOperationsHAI", request)
		return response.return
	}
	
	@RequestMapping(method = [RequestMethod.POST], value = "/hospitalOperationsMajorOpt", produces = "text/xml")
	String hospitalOperationsMajorOpt(
			@RequestParam("hfhudcode") String hfhudcode,
			@RequestParam("operationcode") String operationcode,
			@RequestParam("surgicaloperation") String surgicaloperation,
			@RequestParam("number") String number,
			@RequestParam("reportingyear") String reportingyear
	) {
		HospitalOperationsMajorOpt request = new HospitalOperationsMajorOpt()
		request.hfhudcode = hfhudcode
		request.operationcode = operationcode
		request.surgicaloperation = surgicaloperation
		request.number = number
		request.reportingyear = reportingyear
		
		HospitalOperationsMajorOptResponse response =
				(HospitalOperationsMajorOptResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/hospitalOperationsMajorOpt", request)
		return response.return
	}
	
	@RequestMapping(method = [RequestMethod.POST], value = "/hospitalOperationsMinorOpt", produces = "text/xml")
	String hospitalOperationsMinorOpt(
			@RequestParam("hfhudcode") String hfhudcode,
			@RequestParam("operationcode") String operationcode,
			@RequestParam("surgicaloperation") String surgicaloperation,
			@RequestParam("number") String number,
			@RequestParam("reportingyear") String reportingyear
	
	) {
		HospitalOperationsMinorOpt request = new HospitalOperationsMinorOpt()
		request.hfhudcode = hfhudcode
		request.operationcode = operationcode
		request.surgicaloperation = surgicaloperation
		request.number = number
		request.reportingyear = reportingyear
		
		HospitalOperationsMinorOptResponse response =
				(HospitalOperationsMinorOptResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/hospitalOperationsMinorOpt", request)
		return response.return
	}
	
	@RequestMapping(method = [RequestMethod.POST], value = "/staffingPattern", produces = "text/xml")
	String staffingPattern(
			@RequestParam("hfhudcode") String hfhudcode,
			@RequestParam("professiondesignation") String professiondesignation,
			@RequestParam("specialtyboardcertified") String specialtyboardcertified,
			@RequestParam("fulltime40permanent") String fulltime40permanent,
			@RequestParam("fulltime40contractual") String fulltime40contractual,
			@RequestParam("parttimepermanent") String parttimepermanent,
			@RequestParam("parttimecontractual") String parttimecontractual,
			@RequestParam("activerotatingaffiliate") String activerotatingaffiliate,
			@RequestParam("outsourced") String outsourced,
			@RequestParam("reportingyear") String reportingyear
	) {
		StaffingPattern request = new StaffingPattern()
		request.hfhudcode = hfhudcode
		request.professiondesignation = professiondesignation
		request.specialtyboardcertified = professiondesignation
		request.fulltime40Permanent = fulltime40permanent
		request.fulltime40Contractual = fulltime40contractual
		request.parttimepermanent = parttimepermanent
		request.parttimecontractual = parttimecontractual
		request.activerotatingaffiliate = activerotatingaffiliate
		request.outsourced = outsourced
		request.reportingyear = reportingyear
		
		StaffingPatternResponse response =
				(StaffingPatternResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/staffingPattern", request)
		return response.return
	}
	
	@RequestMapping(method = [RequestMethod.POST], value = "/staffingPatternOthers", produces = "text/xml")
	String staffingPatternOthers(
			@RequestParam("hfhudcode") String hfhudcode,
			@RequestParam("parent") String parent,
			@RequestParam("professiondesignation") String professiondesignation,
			@RequestParam("specialtyboardcertified") String specialtyboardcertified,
			@RequestParam("fulltime40permanent") String fulltime40permanent,
			@RequestParam("fulltime40contractual") String fulltime40contractual,
			@RequestParam("parttimepermanent") String parttimepermanent,
			@RequestParam("parttimecontractual") String parttimecontractual,
			@RequestParam("activerotatingaffiliate") String activerotatingaffiliate,
			@RequestParam("outsourced") String outsourced,
			@RequestParam("reportingyear") String reportingyear
	) {
		StaffingPatternOthers request = new StaffingPatternOthers()
		request.hfhudcode = hfhudcode
		request.parent = parent
		request.professiondesignation = professiondesignation
		request.specialtyboardcertified = specialtyboardcertified
		request.fulltime40Permanent = fulltime40permanent
		request.fulltime40Contractual = fulltime40contractual
		request.parttimepermanent = parttimepermanent
		request.parttimecontractual = parttimecontractual
		request.activerotatingaffiliate = activerotatingaffiliate
		request.outsourced = outsourced
		request.reportingyear = reportingyear
		
		StaffingPatternOthersResponse response =
				(StaffingPatternOthersResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/staffingPatternOthers", request)
		return response.return
	}
	
	@RequestMapping(method = [RequestMethod.POST], value = "/expenses", produces = "text/xml")
	String expenses(
			@RequestParam("hfhudcode") String hfhudcode,
			@RequestParam("salarieswages") String salarieswages,
			@RequestParam("employeebenefits") String employeebenefits,
			@RequestParam("allowances") String allowances,
			@RequestParam("totalps") String totalps,
			@RequestParam("totalamountmedicine") String totalamountmedicine,
			@RequestParam("totalamountmedicalsupplies") String totalamountmedicalsupplies,
			@RequestParam("totalamountutilities") String totalamountutilities,
			@RequestParam("totalamountnonmedicalservice") String totalamountnonmedicalservice,
			@RequestParam("totalmooe") String totalmooe,
			@RequestParam("amountinfrastructure") String amountinfrastructure,
			@RequestParam("amountequipment") String amountequipment,
			@RequestParam("totalco") String totalco,
			@RequestParam("grandtotal") String grandtotal,
			@RequestParam("reportingyear") String reportingyear
	) {
		Expenses request = new Expenses()
		request.hfhudcode = hfhudcode
		request.salarieswages = salarieswages
		request.employeebenefits = employeebenefits
		request.allowances = allowances
		request.totalps = totalps
		request.totalamountmedicine = totalamountmedicine
		request.totalamountmedicalsupplies = totalamountmedicalsupplies
		request.totalamountutilities = totalamountutilities
		request.totalamountnonmedicalservice = totalamountnonmedicalservice
		request.totalmooe = totalmooe
		request.amountinfrastructure = amountinfrastructure
		request.amountequipment = amountequipment
		request.totalco = totalco
		request.grandtotal = grandtotal
		request.reportingyear = reportingyear
		
		ExpensesResponse response =
				(ExpensesResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/expenses", request)
		return response.return
	}
	
	@RequestMapping(method = [RequestMethod.POST], value = "/revenues", produces = "text/xml")
	String revenues(
			@RequestParam("hfhudcode") String hfhudcode,
			@RequestParam("amountfromdoh") String amountfromdoh,
			@RequestParam("amountfromlgu") String amountfromlgu,
			@RequestParam("amountfromdonor") String amountfromdonor,
			@RequestParam("amountfromprivateorg") String amountfromprivateorg,
			@RequestParam("amountfromphilhealth") String amountfromphilhealth,
			@RequestParam("amountfrompatient") String amountfrompatient,
			@RequestParam("amountfromreimbursement") String amountfromreimbursement,
			@RequestParam("amountfromothersources") String amountfromothersources,
			@RequestParam("grandtotal") String grandtotal,
			@RequestParam("reportingyear") String reportingyear
	) {
		Revenues request = new Revenues()
		request.hfhudcode = hfhudcode
		request.amountfromdoh = amountfromdoh
		request.amountfromlgu = amountfromlgu
		request.amountfromdonor = amountfromdonor
		request.amountfromprivateorg = amountfromprivateorg
		request.amountfromphilhealth = amountfromphilhealth
		request.amountfrompatient = amountfrompatient
		request.amountfromreimbursement = amountfromreimbursement
		request.amountfromothersources = amountfromothersources
		request.grandtotal = grandtotal
		request.reportingyear = reportingyear
		
		RevenuesResponse response =
				(RevenuesResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/revenues", request)
		return response.return
		
	}
	
}
