package com.hisd3.hismk2.graphqlservices.pms

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.User
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.pms.PatientCaseView
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.pms.CaseRepository
import com.hisd3.hismk2.repository.pms.PatientCaseViewRepository
import com.hisd3.hismk2.repository.pms.PatientRepository
import com.hisd3.hismk2.rest.dto.MatchPatientResultDto
import com.hisd3.hismk2.security.SecurityUtils
import com.intuit.fuzzymatcher.component.MatchService
import com.intuit.fuzzymatcher.domain.Document
import com.intuit.fuzzymatcher.domain.Element
import com.intuit.fuzzymatcher.domain.ElementType
import com.intuit.fuzzymatcher.domain.Match
import com.lowagie.text.PageSize
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

import org.springframework.data.domain.PageRequest
import org.xmlsoap.schemas.soap.encoding.Int
import org.xmlsoap.schemas.soap.encoding.Time

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.TypedQuery
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.stream.Collectors

@Component
@GraphQLApi
class PatientCaseViewService extends AbstractDaoService<PatientCaseView> {

    PatientCaseViewService() {
        super(PatientCaseView.class)
    }

    @Autowired
    PatientCaseViewRepository patientCaseViewRepository

    @Autowired
    CaseRepository caseRepository

    @Autowired
    PatientRepository patientRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    EmployeeRepository employeeRepository

    @PersistenceContext
    EntityManager entityManager

    @Autowired
    private JdbcTemplate jdbcTemplate

    @Autowired
    ObjectMapper objectMapper


    @GraphQLQuery(name = "searchMatchingIdentity", description = '''Search by
 breaking down full name''')
    List<PatientCaseView> searchMatchingIdentity(
            @GraphQLArgument(name = 'lastName') String lastName,
            @GraphQLArgument(name = 'firstName') String firstName,
            @GraphQLArgument(name = 'middleName') String middleName
    ) {

        if (lastName.isEmpty() && firstName.isEmpty() && middleName.isEmpty())
            return new ArrayList<PatientCaseView>();

        String jpql = "SELECT p FROM PatientCaseView p " +
                "WHERE LOWER(p.lastName) LIKE LOWER(CONCAT('%',:lastName,'%'))" +
                " AND LOWER(p.firstName) LIKE LOWER(CONCAT('%',:firstName," +
                "'%'))" +
                " AND LOWER(p.middleName) LIKE LOWER(CONCAT('%',:middleName," +
                "'%'))";

        TypedQuery<PatientCaseView> query = entityManager.createQuery(jpql, PatientCaseView.
                class);
        query.setParameter("firstName", firstName);
        query.setParameter("lastName", lastName);
        query.setParameter("middleName", middleName);
        query.setMaxResults(10);

        List<PatientCaseView> results = query.getResultList();

        return results;
    }

    @GraphQLQuery(name = "patientCaseView")
    Page<PatientCaseView> findByFilters(
            @GraphQLArgument(name = 'patient') String patient,
            @GraphQLArgument(name = 'physician') UUID physician,
            @GraphQLArgument(name = 'registry') String registry,
            @GraphQLArgument(name = 'service') String service,
            @GraphQLArgument(name = 'date') Instant[] date,
            @GraphQLArgument(name = 'disposition') String disposition,
            @GraphQLArgument(name = 'condition') String condition,
            @GraphQLArgument(name = 'roomNo') String roomNo,
            @GraphQLArgument(name = 'onlyMe') Boolean onlyMe,
            @GraphQLArgument(name = 'page') Integer page,
            @GraphQLArgument(name = 'pageSize') Integer pageSize,
            @GraphQLArgument(name = 'diet') String diet
    ) {

        String query = '''select distinct p from PatientCaseView p where 
                                ((lower(p.lastName) like lower(concat('%', :patient, '%')) or 
								lower(p.firstName) like lower(concat('%', :patient, '%')) or 
								lower(p.middleName) like lower(concat('%', :patient, '%'))) or lower(p.fullName) like lower(concat('%', :patient, '%')))'''

        String countQuery = '''select distinct count(p) from PatientCaseView p where 
                                ((lower(p.lastName) like lower(concat('%', :patient, '%')) or 
								lower(p.firstName) like lower(concat('%', :patient, '%')) or 
								lower(p.middleName) like lower(concat('%', :patient, '%'))) or lower(p.fullName) like lower(concat('%', :patient, '%')))'''

        Map<String, Object> params = new HashMap<>()

        params.put('patient', patient)

        if (physician) {
            query += ''' and (p.primaryphysician.id = :physician)'''
            countQuery += ''' and (p.primaryphysician.id = :physician)'''
            params.put("physician", physician)
        }

        if (registry) {
            query += ''' and (p.registryType = :registry)'''
            countQuery += ''' and (p.registryType = :registry)'''
            params.put("registry", registry)
        }

        if (service) {
            query += ''' and (p.serviceType=:service)'''
            countQuery += ''' and (p.serviceType=:service)'''
            params.put("service", service)
        }

        if (date) {
            query += ''' and (p.admissionDatetime between :start and :end)'''
            countQuery += ''' and (p.admissionDatetime between :start and :end)'''
            params.put("start", LocalDateTime.ofInstant(date[0], ZoneOffset.UTC).toLocalDate().atStartOfDay().minusHours(8).toInstant(ZoneOffset.UTC))
            params.put("end", LocalDateTime.ofInstant(date[1], ZoneOffset.UTC).toLocalDate().atStartOfDay().plusDays(1).minusHours(8).toInstant(ZoneOffset.UTC))
        }

        if (disposition) {
            query += ''' and (p.dischargeDisposition=:disposition)'''
            countQuery += ''' and (p.dischargeDisposition=:disposition)'''
            params.put("disposition", disposition)
        }

        if (condition) {
            query += ''' and (p.dischargeCondition=:condition)'''
            countQuery += ''' and (p.dischargeCondition=:condition)'''
            params.put("condition", condition)
        }

        if (roomNo) {
            query += ''' and (p.room.roomNo=:roomNo)'''
            countQuery += ''' and (p.room.roomNo=:roomNo)'''
            params.put("roomNo", roomNo)
        }

        if (diet) {
            query += ''' and (p.diet.dietName=:diet)'''
            countQuery += ''' and (p.diet.dietName=:diet)'''
            params.put("diet", diet)
        }

        if (onlyMe) {
            User user = userRepository.findOneByLogin(SecurityUtils.currentLogin())
            Employee employee = employeeRepository.findOneByUser(user)

            query += ''' and (lower(p.managingStaffs) like lower(concat('%', :userId, '%') ) or lower(p.comanagingPhysician) like lower(concat('%', :userId, '%') ))'''
            countQuery += ''' and (lower(p.managingStaffs) like lower(concat('%', :userId, '%') ) or lower(p.comanagingPhysician) like lower(concat('%', :userId, '%') ))'''
            params.put("userId", employee.id)
        }

        return getPageable(query, countQuery, page, pageSize, params)
    }


    @GraphQLQuery(name = "getAllPatientByPhysician")
    Page<PatientCaseView> getAllPatientByPhysician(
            @GraphQLArgument(name = "physician") UUID physician,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name ="pageSize") Integer pageSize

    ) {
        if(!physician){
            def currentLogin = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()
          return  patientCaseViewRepository.getAllPatientPageable(currentLogin.id, PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, 'fullName')))
        }
      return  patientCaseViewRepository.getAllPatientPageable(physician, PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, 'fullName')))
    }

    @GraphQLQuery(name = "getAllPatientByDepartment")
    Page<PatientCaseView> getAllPatientByDepartment(
            @GraphQLArgument(name = "department") UUID department,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name ="pageSize") Integer pageSize
    ){
        if(!department){
            def currentLogin = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()
            return  patientCaseViewRepository.getAllPatientByDepartment(currentLogin.id, PageRequest.of(page, pageSize,  Sort.by(Sort.Direction.DESC, 'fullName')))
        }
        return  patientCaseViewRepository.getAllPatientByDepartment(department, PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, 'fullName')))
    }


    @GraphQLQuery(name = "patientCaseViewForReport")
    Page<PatientCaseView> patientCaseViewForReport(
            @GraphQLArgument(name = 'patient') String patient,
            @GraphQLArgument(name = 'physician') UUID physician,
            @GraphQLArgument(name = 'registry') String registry,
            @GraphQLArgument(name = 'service') String service,
            @GraphQLArgument(name = 'date') Instant[] date,
            @GraphQLArgument(name = 'disposition') String disposition,
            @GraphQLArgument(name = 'condition') String condition,
            @GraphQLArgument(name = 'roomNo') String roomNo,
            @GraphQLArgument(name = 'onlyMe') Boolean onlyMe,
            @GraphQLArgument(name = 'page') Integer page,
            @GraphQLArgument(name = 'pageSize') Integer pageSize,
            @GraphQLArgument(name = 'diet') String diet
    ) {

        String query = '''select p from PatientCaseView p where 
                    (lower(p.fullName) like lower(concat('%', :patient, '%')) or 
                    (lower(p.lastName) like lower(concat('%', :patient, '%')) 
                    or lower(p.firstName) like lower(concat('%', :patient, '%'))))'''

        String countQuery = '''select count(p) from PatientCaseView p where 
                    (lower(p.fullName) like lower(concat('%', :patient, '%')) or 
                    (lower(p.lastName) like lower(concat('%', :patient, '%')) 
                    or lower(p.firstName) like lower(concat('%', :patient, '%'))))'''

        Map<String, Object> params = new HashMap<>()

        params.put('patient', patient)

        if (physician) {
            query += ''' and (p.primaryphysician.id = :physician)'''
            countQuery += ''' and (p.primaryphysician.id = :physician)'''
            params.put("physician", physician)
        }

        if (registry) {
            query += ''' and (p.registryType = :registry)'''
            countQuery += ''' and (p.registryType = :registry)'''
            params.put("registry", registry)
        }

        if (service) {
            query += ''' and (p.serviceType=:service)'''
            countQuery += ''' and (p.serviceType=:service)'''
            params.put("service", service)
        }

        if (date) {
            query += ''' and (p.admissionDatetime between :start and :end)'''
            countQuery += ''' and (p.admissionDatetime between :start and :end)'''
            params.put("start", LocalDateTime.ofInstant(date[0], ZoneOffset.UTC).toLocalDate().atStartOfDay().minusHours(8).toInstant(ZoneOffset.UTC))
            params.put("end", LocalDateTime.ofInstant(date[1], ZoneOffset.UTC).toLocalDate().atStartOfDay().plusDays(1).minusHours(8).toInstant(ZoneOffset.UTC))
        }

        if (disposition) {
            query += ''' and (p.dischargeDisposition=:disposition)'''
            countQuery += ''' and (p.dischargeDisposition=:disposition)'''
            params.put("disposition", disposition)
        }

        if (condition) {
            query += ''' and (p.dischargeCondition=:condition)'''
            countQuery += ''' and (p.dischargeCondition=:condition)'''
            params.put("condition", condition)
        }

        if (roomNo) {
            query += ''' and (p.room.roomNo=:roomNo)'''
            countQuery += ''' and (p.room.roomNo=:roomNo)'''
            params.put("roomNo", roomNo)
        }

        if (diet) {
            query += ''' and (p.diet.dietName=:diet)'''
            countQuery += ''' and (p.diet.dietName=:diet)'''
            params.put("diet", diet)
        }

        if (onlyMe) {
            User user = userRepository.findOneByLogin(SecurityUtils.currentLogin())
            Employee employee = employeeRepository.findOneByUser(user)

            query += ''' and (lower(p.managingStaffs) like lower(concat('%', :userId, '%') ) or lower(p.comanagingPhysician) like lower(concat('%', :userId, '%') ))'''
            countQuery += ''' and (lower(p.managingStaffs) like lower(concat('%', :userId, '%') ) or lower(p.comanagingPhysician) like lower(concat('%', :userId, '%') ))'''
            params.put("userId", employee.id)
        }

        Page<PatientCaseView> ret = getPageable(query, countQuery, 0, 9999999, params)
        return ret;
    }


    @GraphQLQuery(name = "patientCaseViewForReportList")
    List<PatientCaseView> patientCaseViewForReportList(
            @GraphQLArgument(name = 'patient') String patient,
            @GraphQLArgument(name = 'physician') UUID physician,
            @GraphQLArgument(name = 'registry') String registry,
            @GraphQLArgument(name = 'service') String service,
            @GraphQLArgument(name = 'date') Instant[] date,
            @GraphQLArgument(name = 'disposition') String disposition,
            @GraphQLArgument(name = 'condition') String condition,
            @GraphQLArgument(name = 'roomNo') String roomNo,
            @GraphQLArgument(name = 'onlyMe') Boolean onlyMe,
            @GraphQLArgument(name = 'page') Integer page,
            @GraphQLArgument(name = 'pageSize') Integer pageSize,
            @GraphQLArgument(name = 'diet') String diet
    ) {

        Page<PatientCaseView> ret = this.patientCaseViewForReport(patient, physician, registry, service, date, disposition, condition, roomNo, onlyMe, page, pageSize, diet)
        List<PatientCaseView> modifiableList = new ArrayList<PatientCaseView>(ret.getContent());
        modifiableList.sort { it.admissionDatetime }
        return modifiableList;
    }

    @GraphQLQuery(name = "countPatient")
    Long countPatient() {
        return getCount('''select count(p) from PatientCaseView p ''', [:])
    }

    @GraphQLQuery(name = 'patientSearchFuzzy')
    GraphQLRetVal<List<MatchPatientResultDto>> patientSearchFuzzy(@GraphQLArgument(name = 'filter') String filter) {

        List<MatchPatientResultDto> results = new ArrayList<>()


        def patientList2 = jdbcTemplate.queryForList("""select id, patient_lastname as lastName, patient_firstname as firstName, patient_middlename as middleName, concat(patient_lastname, ', ',patient_firstname, ' ', patient_middlename) as fullName 
from  pms.patient_case_view where lower(concat(patient_lastname, ' ',patient_firstname, ' ', patient_middlename)) like lower(concat('%','${filter}', '%')) order by fullName asc""")

        if (patientList2) {
            results = patientList2.stream().map {

                MatchPatientResultDto dto = new MatchPatientResultDto()

                String fullName = ""

                dto.id = it.id

                if (it.lastName) {
                    fullName += it.lastName + ", "
                }

                if (it.firstName) {
                    fullName += it.firstName + " "
                }

                if (it.middleName) {
                    fullName += it.middleName + " "
                }

                dto.fullName = fullName


                return dto

            }.collect(Collectors.toList())

        } else {

            //language=HQL
            def patientList = jdbcTemplate.queryForList('''select id, patient_lastname as lastName, patient_firstname as firstName, patient_middlename as middleName, concat(patient_lastname, ', ',patient_firstname, ' ', patient_middlename) as fullName from  pms.patient_case_view ''')

            UUID key = UUID.randomUUID()
            Document patientFilter = new Document.Builder(key.toString()).addElement(new Element.Builder<String>().setValue(filter).setType(ElementType.NAME).createElement()).createDocument()

            List<Document> documentList = patientList.stream().map { entry ->

                Set<Element> elements = []

                elements << new Element.Builder<String>().setValue(entry['id'] as String).setType(ElementType.TEXT).createElement()
                elements << new Element.Builder<String>().setValue(entry.get('fullName') as String ?: "").setType(ElementType.NAME).setThreshold(0.5).createElement()

                Document doc = new Document(entry?.id as String, elements, 1.0)

                doc.elements.stream().each { element -> element.document = doc }

                return doc

            }.collect(Collectors.toList())


            MatchService matchService = new MatchService()

            Map<String, List<Match<Document>>> result = matchService.applyMatchByDocId(patientFilter, documentList)


            result.entrySet().each {
                it ->
                    it.value.each {
                        data ->
                            if (data.result >= 0.5) {
                                MatchPatientResultDto dto = new MatchPatientResultDto()
                                dto.id = data.matchedWith.elements[0].value ?: ""
//							dto.lastName = data.matchedWith.elements[1].value?:""
//							dto.firstName = data.matchedWith.elements[2].value?:""
//							if(data.matchedWith.elements[3]){
//								dto.middleName = data.matchedWith.elements[3].value?:""
//							}
                                if (data.matchedWith.elements[1]) {
                                    dto.fullName = data.matchedWith.elements[1].value ?: ""
                                }
                                dto.threshold = data.matchedWith.threshold ?: 0.0
                                results << dto
                            }

                    }


            }

        }


        List<MatchPatientResultDto> sortedResults = results.toSorted { MatchPatientResultDto a, MatchPatientResultDto b -> a.fullName <=> b.fullName }

        if (sortedResults.size() > 10) {
            sortedResults = sortedResults.take(10)
        }


        return new GraphQLRetVal<List<MatchPatientResultDto>>(sortedResults, true, "Success")
    }

    @GraphQLQuery(name = 'patientSearchFuzzyWithOthers')
    GraphQLRetVal<List<MatchPatientResultDto>> patientSearchFuzzyWithOthers(@GraphQLArgument(name = 'fields') Map<String, Object> fields) {


        if (!fields) {
            fields = [:]
        }

        SimpleDateFormat dobFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        SimpleDateFormat dobFormat2 = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy")
        SimpleDateFormat dobFormat3 = new SimpleDateFormat("yyyy-MM-dd")

        dobFormat.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()))
        dobFormat2.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()))
        dobFormat3.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()))

        //language=HQL
        def patientList = jdbcTemplate.queryForList('''select id, patient_lastname as lastName, patient_firstname as firstName, patient_middlename as middleName, concat(patient_lastname, ', ',patient_firstname, ' ', patient_middlename) as fullName, dob from  pms.patient_case_view ''')

        UUID key = UUID.randomUUID()

        String fullName = ""


        if (fields.containsKey('lastName')) {
            fullName += "${fields.lastName}, "
        }

        if (fields.containsKey('firstName')) {
            fullName += """${fields.firstName} """
        }


        if (fields.containsKey('middleName')) {
            fullName += fields.middleName
        }


        Set<Element> elementsFilter = new ArrayList<Element>()
        elementsFilter << new Element.Builder<String>().setValue(fullName).setType(ElementType.NAME).setThreshold(0.5).createElement()
        Calendar dateDob = Calendar.getInstance()

        if (fields.dob) {
            dateDob.setTime(dobFormat.parse(fields.dob as String))
            dateDob.set(Calendar.HOUR_OF_DAY, 0)
            dateDob.set(Calendar.MINUTE, 0)
            dateDob.set(Calendar.SECOND, 0)
            dateDob.set(Calendar.MILLISECOND, 0)
            dateDob.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()))

        }

        Document docFilter = new Document.Builder(key.toString())
                .addElement(new Element.Builder<String>().setValue(fullName).setType(ElementType.NAME).createElement())
                .addElement(new Element.Builder<Date>().setValue(dateDob.getTime()).setType(ElementType.DATE).createElement())
                .createDocument()


        List<Document> documentList = patientList.stream().map { entry ->

            Set<Element> elements = new ArrayList<Element>()
            elements << new Element.Builder<String>().setValue(entry.id as String).setType(ElementType.NAME).createElement()
            elements << new Element.Builder<String>().setValue(entry.fullName as String ?: "").setType(ElementType.NAME).setThreshold(0.5).createElement()

            if (entry.dob) {
                elements << new Element.Builder<Date>().setValue(dobFormat3.parse(entry.dob as String)).setType(ElementType.DATE).createElement()
            }

            Document doc = new Document(entry.get('id') as String, elements, 0.5)

            doc.elements.stream().each { element -> element.document = doc }

            return doc

        }.collect(Collectors.toList())


        MatchService matchService = new MatchService()

        Map<String, List<Match<Document>>> result = matchService.applyMatchByDocId(docFilter, documentList)

        List<MatchPatientResultDto> results = new ArrayList<>()

        result.entrySet().each {
            it ->
                it.value.each {
                    data ->
                        if (data.result >= 0.7) {
                            MatchPatientResultDto dto = new MatchPatientResultDto()
                            dto.id = data.matchedWith.elements[0].value ?: ""
                            dto.fullName = data.matchedWith.elements[1].value ?: ""
                            if (data.matchedWith.elements[2]) {
                                dto.dob = dobFormat3.format(dobFormat2.parse(data.matchedWith.elements[2].value as String))
                            }
                            dto.threshold = data.matchedWith.threshold ?: 0.0
                            results << dto
                        }

                }


        }


        return new GraphQLRetVal<List<MatchPatientResultDto>>(results, true, "Success")
    }

}
