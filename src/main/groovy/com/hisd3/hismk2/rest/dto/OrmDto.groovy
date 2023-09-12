package com.hisd3.hismk2.rest.dto

class OrmDto {
	MsgHeader header
	PatientDTO patient
	AddressDTO address
	PatientVisit visits
	OrderDTO order
	Extras extras
}

class MsgHeader {
	String messageType = ""// ORM_O01"
	String messageTriggerEvent = ""//001"
	String hospitalName = ""//ACEMCBHL"
	String sendingFacility = ""//ACEMCBHL"
	String messageControlId = ""//"EST0002"
	String sendingApplication = ""//HISD3MIDDLEWARE"
	String receivingApplication = ""//LIS"
}

class PatientDTO {
	String patientId = ""//PTEST00001"
	String alternateId = ""//ALTERNATE0001"
	String patientNo = ""//PTEST00001"
	String firstName = ""//JOHN"
	String lastName = ""//DOE"
	String middleName = ""//MIDDLENAME"
	String extensionName = ""//JR"
	String gender = ""//M"
	String birthDate = ""//DateTime.now().toString("yyyyMMddHHmmss").toString()
	String citizenship = ""//JAVAS"
	String maritalStatus = ""//M"
	String religion = ""
	String fullName = lastName + "," + firstName + middleName + extensionName
}

class AddressDTO {
	String countryCode = ""//+63"
	String city = ""//TAGBILARAN CITY"
	String country = ""//PHL"
	String addressLine = ""//0368 Carlos P. Garcia East Avenue"
	String province = ""//BOHOL"
	String zip = ""//6300"
}

class PatientVisit {
	String patientClass = ""//I"
	String assignedLocation = ""//PRIVATE"
	String room = ""//503B"
	String bed = ""//1"
	String admissionType = ""//C"
	String visitNumber = ""//TESTCASE0001"
	String admissionDateTime = ""//DateTime.now().toString("yyyyMMddHHmmss").toString()
	String referringPhysicianId = ""//EMP12345"
	String referringPhysicianName = ""//JUAN de LUNA"
	String admittingPhysicianId = ""//EMP12345"
	String admittingPhysicianName = ""//JUAN de LUNA"
}

class OrderDTO {
	String orderControl = "NW"
	String placeOrderNumber = "" // carestream accession
	String fileOrderNumber = ""  // carestream accession
	String orderDateTime = ""
	String enteringOrganization = ""//ACE-MC-BHL"
	String modalityType = ""//D"X"
	String scheduleDateTime = ""//DateTime.now().toString("yyyyMMddHHmmss")
	String observationDate = "" //DateTime.now().toString("yyyyMMddHHmmss")
	String priority = ""
	ArrayList<OrderItem> itemList
}

class OrderItem {
	//needs to populate obr-16 requesting physician
	String identifier = ""//DICGR0051"
	String serviceName = ""//X RAY-CHEST - AP (ADULT)"
	String priority = ""//STAT"
}

class Extras {
	Boolean tcp = false
	String ipAddress = ""
	String port = 0
	String smbUrl = ""
	String integratedFacilities = ""//LIS'
	String userLogin = ""
	String passLogin = ""
}
