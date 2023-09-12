package com.hisd3.hismk2.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.address.Country
import com.hisd3.hismk2.domain.address.Province
import com.hisd3.hismk2.domain.appointment.AgtPatient
import com.hisd3.hismk2.domain.billing.BillingItemType
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.graphqlservices.address.AddressServices
import com.hisd3.hismk2.graphqlservices.appointment.AgtPatientServices
import com.hisd3.hismk2.graphqlservices.appointment.AppointmentScheduleServices
import com.hisd3.hismk2.graphqlservices.appointment.AppointmentScheduleTimeServices
import com.hisd3.hismk2.graphqlservices.appointment.AppointmentServices
import com.hisd3.hismk2.graphqlservices.billing.BillingItemServices
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetValAppointment
import com.hisd3.hismk2.rest.dto.PatientInfo
import com.hisd3.hismk2.rest.dto.RestAppointment
import com.hisd3.hismk2.rest.dto.ScheduleAppDate
import com.hisd3.hismk2.rest.dto.ScheduleAppTime
import com.hisd3.hismk2.services.DepartmentCategoryService
import com.hisd3.hismk2.services.NotificationService
import com.hisd3.hismk2.utils.SOAPConnector
import com.sun.xml.messaging.saaj.SOAPExceptionImpl
import groovy.transform.Canonical
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.context.support.RequestHandledEvent
import ph.gov.doh.uhmistrn.ahsr.webservice.index.GetDataTable
import ph.gov.doh.uhmistrn.ahsr.webservice.index.GetDataTableResponse

import javax.persistence.EntityManager
import javax.persistence.NoResultException
import javax.servlet.http.HttpSession

@Canonical
class CitySelect{
    String label
    String value
}

@RestController
class CommonResource {

    @Autowired
    NotificationService notificationService

    @Autowired
    SOAPConnector soapConnector

    @Autowired
    DepartmentCategoryService departmentCategoryService

    @Autowired
    EntityManager entityManager

    @Autowired
    BillingItemServices billingItemServices

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    AgtPatientServices agtPatientServices

    @Autowired
    AddressServices addressServices

    @Autowired
    AppointmentServices appointmentServices

    @Autowired
    AppointmentScheduleServices appointmentScheduleServices

    @Autowired
    AppointmentScheduleTimeServices appointmentScheduleTimeServices

    /*
    ANNOTATION_PAYMENTS_GROUPS
    ANNOTATION_NOTIFICATION_GROUPS
    http://localhost:8080/testbillingannotation?billingId=5ab3ea62-5dcb-489c-842a-e5bbfdcf74a1&billingItemType=ANNOTATION_PAYMENTS_GROUPS&description=PAYMENT_OR11211&amount=10000
     */
    @RequestMapping("/testbillingannotation")
    ResponseEntity<String> testbillingannotation(
            @RequestParam UUID billingId,
            @RequestParam String billingItemType,
            @RequestParam String description,
            @RequestParam BigDecimal amount
    ) {

        billingItemServices.addAnnotations(billingId,
                BillingItemType.valueOf(billingItemType),
                description,
                amount
        )

        return ResponseEntity.ok("OK")
    }
    @RequestMapping("/testbundyloginnofication")
    ResponseEntity<String> testnotificaion(
            @RequestBody Map<String, Object> fields
    ) {
        String message = ""

        if(fields.sEnrollNumber){


            def attState = ""

            if(fields.iAttState as Integer == 0){

                attState = "CHECKED IN"

            } else if (fields.iAttState == 1) {

                attState = "CHECKED OUT"

            }else if(fields.iAttState == 4) {

                attState = "OVERTIMED IN"
            }else if (fields.iAttState == 5)  {

                attState = "OVERTIMED OUT"

            }

            try {

                def employee = entityManager.createQuery("select e from Employee e where e.biometricNo = ${fields.sEnrollNumber as Integer}", Employee.class).setMaxResults(1).singleResult

                message = "Employee ${employee.fullName} has ${attState}!"

            } catch (NoResultException e) {

                message = "Someone just ${attState} with Biometric ID: ${fields.sEnrollNumber} but not registered in our System."

            }

        }

        notificationService.notifyUsersByRoles(['ROLE_ADMIN'], "Biometric Notification", message,"#")

        return ResponseEntity.ok("OK")

    }

    @RequestMapping(value = "/soapTest", produces = "text/xml")
    String soapTest() {
        try {
            GetDataTable request = new GetDataTable()
            request.hfhudcode = "DOH000000000000000"
            request.reportingyear = "2020"
            request.table = "genInfoClassification"
            GetDataTableResponse response =
                    (GetDataTableResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/getDataTable", request)
            println("Got Response As below ========= : ")
            println("Name : " + response.return)

            return response.return
        } catch (SOAPExceptionImpl e) {
            println(e.message)
        }

    }

    @RequestMapping("/ping")
    String ping() {
        "PONG"
    }

    @RequestMapping("/test")
    String test() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(false)
        println "Last Accessed "+ session.getLastAccessedTime()
        println "Current Time "+ System.currentTimeMillis();
        session.getLastAccessedTime()
    }

    @RequestMapping("/")
    String index() {
        "WELCOME TO HISMK2 GraphQL Server."
    }

    @RequestMapping("/serverTime")
    String serverTime() {
        System.currentTimeMillis()
    }

    @RequestMapping("/categories")
    List<String> cat() {
        return departmentCategoryService.allcategory()
    }

    //THIS FUNCTION IS REQUEST LISTENER, IT DOES LISTEN TO REQUESTS THAT ARE CONSIDERED "USER ACTIVITY" TO MONITOR INACTIVE USERS AND WARN FOR LOGOUT
    @EventListener
    void handleEvent (RequestHandledEvent e) {
        String url = e.getAt("requestUrl").toString()
        Boolean exemptedURL = url.contains("serverTime") || url.contains("favicon") || url.contains("websocket") || url.contains("checkExpiry")
    }

    //wilson common resource
    @RequestMapping("/user/register")
    GraphQLRetValAppointment<Boolean> register(
            @RequestBody Map<String, Object> fields
    ) {
        def p = objectMapper.convertValue(fields, AgtPatient.class)
        def result = new GraphQLRetValAppointment<Boolean>(true,true,
                "Register Success",
                "")
        def check = agtPatientServices.checkPatientDuplicate(p.emailAddress, p.contactNo)
        if(!check){
            agtPatientServices.agtPatientUpsert(fields, null)
        }else{
            result = new GraphQLRetValAppointment<Boolean>(false,false,
                    "Email address or Contact number already taken",
                    "")
        }
        return result
    }

    @RequestMapping("/user/auth")
    AgtPatient register(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ) {
        return agtPatientServices.getPatient(username, password)
    }

    @RequestMapping("/user/patient")
    AgtPatient getPatient(
            @RequestParam("id") UUID id
    ) {
        return agtPatientServices.agtPatientById(id)
    }

    @RequestMapping("/user/countries")
    List<Country> countries(@RequestParam("filter") String filter) {
        return addressServices.countriesFilter(filter)
    }

    @RequestMapping("/user/province")
    List<Province> province(@RequestParam("filter") String filter) {
        return addressServices.provincesFilter(filter)
    }

    @RequestMapping("/user/city")
    List<CitySelect> getCities(@RequestParam("province") String province) {
        def city =  addressServices.getCities(province)
        def result = new ArrayList<CitySelect>();
        city.each {
            result.push(new CitySelect(
                    label: it.name.toUpperCase(),
                    value: it.name.toUpperCase()
            ))
        }
        return result
    }

    @RequestMapping("/user/upsertInfo")
    AgtPatient upsertInfo(
            @RequestBody Map<String, Object> fields
    ) {
        def id = UUID.fromString(fields['id'] as String)
        agtPatientServices.agtPatientUpsert(fields, id)
    }

    @RequestMapping("/user/appointment")
    List<RestAppointment> appointment(@RequestParam("id") UUID id) {
//        def id = UUID.fromString(patient)
        def ap = appointmentServices.appointmentByPatient(id)
        def appointment = new ArrayList<RestAppointment>()
        def schedAppTime = new ScheduleAppTime()
        def schedAppDate = new ScheduleAppDate()
        def patientData = new PatientInfo()
        ap.each {
            def schedTime = it.scheduleTime
            def schedDate = it.schedule
            def p = it.patient
            schedTime.each {
                schedAppTime = new ScheduleAppTime(
                        id: it.id,
                        timeId: it.schedTime.id,
                        formattedTime: it.schedTime.formattedTime,
                )
            }

            schedDate.each {
                schedAppDate = new ScheduleAppDate(
                        id: it.id,
                        scheduleDate: it.scheduleDate,
                        formattedScheduleDate: it.formattedScheduleDate,
                )
            }

            p.each {
                patientData = new PatientInfo(
                        id: it.id,
                        fullName: it.fullName,
                )
            }
            appointment.push(new RestAppointment(
                    id: it.id,
                    appNo: it.appNo,
                    purposeOfTesting: it.purposeOfTesting,
                    reasonOfTesting: it.reasonOfTesting,
                    dateValidity: it.dateValidity,
                    transportation: it.transportation,
                    airlineSeaVessel: it.airlineSeaVessel,
                    flightVesselNo: it.flightVesselNo,
                    countryDestination: it.countryDestination,
                    positiveCovidBefore: it.positiveCovidBefore,
                    informant: it.informant,
                    relationInformant: it.relationInformant,
                    informantContact: it.informantContact,
                    numberOfTest: it.numberOfTest,
                    covidUpdates: it.covidUpdates,
                    covidUpdatesList: it.covidUpdatesList,
                    outcomeCondition: it.outcomeCondition,
                    dod: it.dod,
                    immediateCause: it.immediateCause,
                    antecedentCause: it.antecedentCause,
                    underlyingCause: it.underlyingCause,
                    contributoryConditions: it.contributoryConditions,
                    patient: patientData,
                    scheduleTime: schedAppTime,
                    schedule: schedAppDate,
                    dor: it.dor,
                    orderStatus: it.orderStatus,
                    status: it.status,
            ))
        }
        return appointment
    }

    @RequestMapping("/user/listAppDate")
    List<ScheduleAppDate> listAppDate(
            @RequestParam("dateNow") String dateNow,
            @RequestParam("filter") String filter
    ) {
        def schedAppDate = new ArrayList<ScheduleAppDate>()
        def raw = appointmentScheduleServices.getScheduleDateList(dateNow, filter)
        raw.each {
            schedAppDate.push(new ScheduleAppDate(
                    id: it.id,
                    scheduleDate: it.scheduleDate,
                    formattedScheduleDate: it.formattedScheduleDate,
            ))
        }
        return schedAppDate.sort{it.scheduleDate}
    }

    @RequestMapping("/user/listAppTime")
    List<ScheduleAppTime> listAppTime(
            @RequestParam("id") UUID id
    ) {
        def schedAppTime = new ArrayList<ScheduleAppTime>()
        def raw = appointmentScheduleTimeServices.timeByScheduleActive(id)
        raw.each {
            schedAppTime.push(new ScheduleAppTime(
                    id: it.id,
                    timeId: it.schedTime.id,
                    formattedTime: it.schedTime.formattedTime,
            ))
        }
        return schedAppTime.sort{it.formattedTime}
    }

    @RequestMapping("/user/appointmentCheck")
    GraphQLRetValAppointment<Boolean> appointmentCheckUser(
            @RequestParam("id") UUID id,
            @RequestParam("type") String type
    ) {
        return appointmentServices.scheduleCheck(id, type)
    }

    @RequestMapping("/user/upsertAppInfo")
    GraphQLRetValAppointment<Boolean> upsertAppInfo(
            @RequestBody Map<String, Object> fields
    ) {
        def patient = UUID.fromString(fields['patient']['id'] as String)
        def sched = UUID.fromString(fields['schedule']['id'] as String)
        def result = new GraphQLRetValAppointment<Boolean>(true,true,"Success", "")
        def check = appointmentServices.checkAppByPatientSchedule(sched,patient)
        if(!check){
            appointmentServices.upsertAppointment(fields, null)
        }else{
            result = new GraphQLRetValAppointment<Boolean>(false,false,"You can only book an appointment once a day. Please tell the authorized personnel if you want to change your schedule.", "")
        }
        return result
    }

    @RequestMapping("/user/updateArrival")
    GraphQLRetValAppointment<Boolean> updateArrival(
            @RequestParam("id") UUID id
    ) {
        appointmentServices.appUpdateStatus(id)
        new GraphQLRetValAppointment<Boolean>(true,true,"Success", "")
    }
}
