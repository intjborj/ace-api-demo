package com.hisd3.hismk2.graphqlservices.bundy

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.hrm.*
import com.hisd3.hismk2.domain.hrm.enums.EmployeeAttendanceMethod
import com.hisd3.hismk2.graphqlservices.hrm.BiometricDeviceService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.hrm.EmployeeAttendanceRepository
import com.hisd3.hismk2.repository.hrm.EmployeeBiometricConfigRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.squareup.okhttp.*
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import org.hibernate.jpa.QueryHints
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import javax.persistence.EntityManager
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@Component
@GraphQLApi
class BundyClockService {

    @Autowired
    BiometricDeviceService biometricDeviceService

    @Autowired
    EmployeeBiometricConfigRepository empBiometricConfigRepository

    @Autowired
    EmployeeAttendanceRepository empAttendanceRepository

    @Autowired
    EmployeeRepository employeeRepository

    @Autowired
    EntityManager entityManager

    @Autowired
    ObjectMapper objectMapper

    //================================QUERY================================\\

    String getSession(BiometricDevice device, OkHttpClient client) {

        // check if the session is valid
        String loginLink = HttpUrl.parse("""http://${device.ipAddress}/csl/check""")

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded")

        def form = new ArrayList<NameValuePair>(2)
        form.add(new BasicNameValuePair("username", device.deviceUsername))
        form.add(new BasicNameValuePair("userpwd", device.devicePassword))

        RequestBody loginBody = RequestBody.create(mediaType, contentBuilder(form))
        def loginRequest = new com.squareup.okhttp.Request.Builder()
                .url(loginLink)
                .post(loginBody)
        if (device.session)
            loginRequest = loginRequest.addHeader("Cookie", device.session)
        loginRequest = loginRequest.build()


        Response loginResponse = client.newCall(loginRequest).execute()
        String loginResponseBody = loginResponse.body().string()
        Element script = Jsoup.parse(loginResponseBody).selectFirst("script")

        Instant timeNow = Instant.now()
        long timeDiff = 0
        if (device.sessionAddedAt)
            timeDiff = Duration.between(timeNow, device.sessionAddedAt).toHours()
        if ((!device.session || (device.sessionAddedAt && timeDiff > 4 && timeDiff)) || script) {

            HttpUrl parsedUrl = HttpUrl.parse("""http://${device.ipAddress}/""")

            def request = new com.squareup.okhttp.Request.Builder()
                    .url(parsedUrl)
                    .get()
                    .build()

            Response response = client.newCall(request).execute()

            if (response.successful) {
                List<String> Cookielist = response.headers().values("Set-Cookie")
                String cookies = ""

                Cookielist.each {
                    cookies += it.split(';')[0]
                }
                device.session = cookies
                device.sessionAddedAt = Instant.now()
                biometricDeviceService.save(device)
                return device.session
            } else return null
        } else return device.session
    }

    @GraphQLMutation(name = "ping_device")
    GraphQLRetVal<String> pingDevice(@GraphQLArgument(name = "id") UUID id) {
        try {
            BiometricDevice device = biometricDeviceService.findOne(id)

            OkHttpClient client = new OkHttpClient()

            HttpUrl parsedUrl = HttpUrl.parse("""http://${device.ipAddress}/""")

            def request = new com.squareup.okhttp.Request.Builder()
                    .url(parsedUrl)
                    .get()
                    .build()

            Response response = client.newCall(request).execute()

            if (response.successful) {
                return new GraphQLRetVal<String>("", true, "Connection Success!")
            } else {
                return new GraphQLRetVal<String>("", false, response.body().string())
            }

        } catch (Exception e) {
            return new GraphQLRetVal<String>("", false, e.message)
        }
    }

    @GraphQLQuery(name = "get_attlog")
    GraphQLRetVal<List<AttLogDto>> getAttLog(
            @GraphQLArgument(name = 'id') UUID id,
            @GraphQLArgument(name = "daterange") Map<String, String> daterange,
            @GraphQLArgument(name = "ids") Map<String, List<String>> ids
    ) {

        BiometricDevice device = biometricDeviceService.findOne(id)

        if (device == null) return new GraphQLRetVal<List<AttLogDto>>([], false, "No device found!")

        OkHttpClient client = new OkHttpClient()

        String cookies = this.getSession(device, client)

        if (cookies) {

            String username = device.deviceUsername
            String password = device.devicePassword

            def form = new ArrayList<NameValuePair>(2)
            def requestParams = new ArrayList<NameValuePair>(2)

            daterange.each {
                key, value ->
                    requestParams.add(new BasicNameValuePair(key, value as String))
            }

            List<Integer> idNos = entityManager.createQuery(
                    "Select p.biometricNo from Employee p  where p.biometricNo is not null", Integer.class
            ).setHint(QueryHints.HINT_READONLY, true).resultList

            if (ids) {
                ids.each {
                    key, value ->
                        if (value.size() != 0) {
                            value.each {
                                requestParams.add(new BasicNameValuePair(key, it))
                            }
                        }
                }
            } else {
                idNos.each {
                    requestParams.add(new BasicNameValuePair('uid', it as String))
                }
            }


            form.add(new BasicNameValuePair('username', username ?: ""))
            form.add(new BasicNameValuePair('userpwd', password ?: ""))

            String loginLink = HttpUrl.parse("""http://${device.ipAddress}/csl/check""")

            String queryLink = HttpUrl.parse("""http://${device.ipAddress}/csl/query?action=run""")


            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded")
            RequestBody body = RequestBody.create(mediaType, contentBuilder(form) ?: "")
            RequestBody param = RequestBody.create(mediaType, contentBuilder(requestParams) ?: "")

            def loginRequest = new com.squareup.okhttp.Request.Builder()
                    .url(loginLink)
                    .post(body)
                    .addHeader("Cookie", cookies)
                    .build()

            Response response2 = client.newCall(loginRequest).execute()

            if (response2.successful) {

                def queryRequest = new com.squareup.okhttp.Request.Builder()
                        .url(queryLink)
                        .post(param)
                        .addHeader("Cookie", cookies)
                        .build()

                Response response3 = client.newCall(queryRequest).execute()

                String html = response3.body().string()
                Element table = Jsoup.parse(html).select('table').get(0)
                Elements rows = table.select('tr')

                List<AttLogDto> logs = new ArrayList<>()

                rows.each {
                    row ->

                        AttLogDto log = new AttLogDto()

                        Elements cols = row.select('td')

                        log.date = cols.get(0).text()
                        log.idno = cols.get(1).text()
                        log.name = cols.get(2).text()
                        log.time = cols.get(3).text()
                        log.status = cols.get(4).text()
                        log.verification = cols.get(5).text() == "Finger" ? EmployeeAttendanceMethod.FINGER.toString() : null

                        logs << log
                }

                return new GraphQLRetVal<List<AttLogDto>>(logs.drop(1), true)
            }

        } else {
            return new GraphQLRetVal<List<AttLogDto>>([], false, response.body().string())
        }


    }

    @GraphQLQuery(name = "get_biometrics_attlog")
    GraphQLRetVal<List<AttLogDto>> getBiometricsAttLog(
            @GraphQLArgument(name = "daterange") Map<String, String> daterange
    ) {

//        BiometricDevice device = biometricDeviceService.findOne(id)
        List<BiometricDevice> devices = biometricDeviceService.findAll()
        List<AttLogDto> finalLogs = []

        if (devices.size() == 0) return new GraphQLRetVal<List<AttLogDto>>([], false, "No device found!")
        OkHttpClient client = new OkHttpClient()
        client.setReadTimeout(5, TimeUnit.MINUTES)
        client.setConnectTimeout(3, TimeUnit.MINUTES)

        devices.each { device ->


            String cookies = this.getSession(device, client)
            if (cookies) {

                String username = device.deviceUsername
                String password = device.devicePassword

                def form = new ArrayList<NameValuePair>(2)
                def requestParams = new ArrayList<NameValuePair>(2)

                daterange.each {
                    key, value ->
                        requestParams.add(new BasicNameValuePair(key, value as String))
                }

                List<Integer> idNos = entityManager.createQuery(
                        "Select p.biometricNo from Employee p  where p.biometricNo is not null", Integer.class
                ).setHint(QueryHints.HINT_READONLY, true).resultList


                form.add(new BasicNameValuePair('username', username ?: ""))
                form.add(new BasicNameValuePair('userpwd', password ?: ""))

                String loginLink = HttpUrl.parse("""http://${device.ipAddress}/csl/check""")

                String queryLink = HttpUrl.parse("""http://${device.ipAddress}/csl/query?action=run""")
                String userQueryLink = HttpUrl.parse("""http://${device.ipAddress}/csl/user?first=1&last=4000000000000""")
                // purposely made long just to ensure we get all users


                MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded")
                RequestBody body = RequestBody.create(mediaType, contentBuilder(form) ?: "")

                def loginRequest = new com.squareup.okhttp.Request.Builder()
                        .url(loginLink)
                        .post(body)
                        .addHeader("Cookie", cookies)
                        .build()

                Response response2 = client.newCall(loginRequest).execute()

                if (response2.successful) {

                    // request the users to get the uid from the device
                    List<String> userUIDS = []
                    def userRequest = new com.squareup.okhttp.Request.Builder()
                            .url(userQueryLink)
                            .get()
                            .addHeader("Cookie", cookies)
                            .build()
                    Response userResponse = client.newCall(userRequest).execute()
                    String userHtml = userResponse.body().string()
                    Element userContainer = Jsoup.parse(userHtml).select('div#cc').get(0)
                    Element userTable = userContainer.select('table').get(0)

                    Elements userRows = userTable.select('tr')
                    userRows.each {
                        row ->
                            String uid = row.select('td > input').get(0).attributes().get("value")
                            userUIDS << uid
                    }

                    userUIDS.each {
                        value -> requestParams.add(new BasicNameValuePair("uid", value))
                    }
//                List<AttLogDto> logs1 = new ArrayList<>()
//                return new GraphQLRetVal<List<AttLogDto>>(logs1, true, "hehe")
                    MediaType mediaType2 = MediaType.parse("text/html; charset=utf-8")
                    RequestBody param = RequestBody.create(mediaType2, contentBuilder(requestParams) ?: "")

                    def queryRequest = new com.squareup.okhttp.Request.Builder()
                            .url(queryLink)
                            .post(param)
                            .addHeader("Cookie", cookies)
                            .build()

                    Response response3 = client.newCall(queryRequest).execute()

                    byte[] testHtml = response3.body().bytes()
//                    byte[] html2 = response3.body().bytes()
//
                    String html = new String(testHtml, StandardCharsets.ISO_8859_1);

                    Element table = Jsoup.parse(html).select('table').get(0)
                    Elements rows = table.select('tr')

                    List<AttLogDto> logs = new ArrayList<>()

                    rows.each {
                        row ->

                            AttLogDto log = new AttLogDto()
                            String offset = LocalDateTime.now().atZone(ZoneId.systemDefault()).getOffset().getId().replaceAll("Z", "+00:00")
                            DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME

                            Elements cols = row.select('td')
                            String name = cols.get(2).text()
                            byte[] bytes = name.getBytes(StandardCharsets.UTF_8);
                            String test = new String(bytes, StandardCharsets.UTF_8);
                            if (name) {
                                List<Employee> employeeResult = entityManager
                                        .createQuery("""
                                                Select e from Employee e 
                                                where 
                                                    upper(concat(e.lastName , coalesce(' ' || nullif(e.firstName,'') , ''), coalesce(' ' || nullif(e.middleName,'') , ''), coalesce(' ' || nullif(nameSuffix,'') , ''))) 
                                                    like concat(upper(:empName),'%')
                                                    and e.isActive = TRUE
                                        """, Employee.class)
                                        .setParameter("empName", name)
                                        .getResultList()
                                Employee employee = employeeResult[0]
                                if (name == "UEHARA HIRONORI OLAGUIR") {
                                    String test2 = "hell"
                                }
                                if (employee) {
                                    log.date = cols.get(0).text()
                                    log.idno = cols.get(1).text()
                                    log.name = employee.fullName
                                    log.time = cols.get(3).text()
                                    log.status = cols.get(4).text()
                                    log.verification = cols.get(5).text()
                                    log.employeeId = employee.id
                                    log.deviceName = device.deviceName
                                    log.department = employee.department?.departmentName ?: null
                                    log.departmentId = employee.department?.id ?: null
//                                log.dateTime = Instant.parse("${log.date}T${log.time}+${ZoneOffset.systemDefault().getId().replaceAll("Z", "+00:00")}")

                                    log.dateTime = LocalDateTime.parse("${log.date} ${log.time}", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).atZone(ZoneId.systemDefault()).toInstant()
//                                    ZonedDateTime zdtInstanceAtOffset = ZonedDateTime.parse("${log.date}T${log.time}${offset}", DATE_TIME_FORMATTER);
//                                    log.dateTime = zdtInstanceAtOffset.toInstant()
                                    logs << log
                                }
                            }
                    }

                    finalLogs += logs
//                    return new GraphQLRetVal<List<AttLogDto>>(logs, true)
                }

            } else {
                return new GraphQLRetVal<List<AttLogDto>>([], false, "One of the device was unresponsive")
            }

        }

        finalLogs.sort({ it.dateTime })
        return new GraphQLRetVal<List<AttLogDto>>(finalLogs, true)
    }


    @GraphQLQuery(name = "get_bundy_users")
    GraphQLRetVal<BundyUserDto[]> getBundyUsers(
            @GraphQLArgument(name = "device_id") UUID deviceId
    ) {
        BundyUserDto[] dtos = []

        try {
            OkHttpClient client = new OkHttpClient()

            def bundyDeviceConfig = entityManager.createQuery("select b from BiometricServiceConfig b ", BiometricServiceConfig.class).resultList

            def bundyDevice = entityManager.find(BiometricDevice.class, deviceId)


            if (!bundyDeviceConfig) {
                return new GraphQLRetVal<BundyUserDto[]>(dtos, false, "Ooops! Please set Biometric Service Config first")

            }

            if (!bundyDevice) {
                return new GraphQLRetVal<BundyUserDto[]>(dtos, false, "Ooops! Device not found")

            }

            Request request = new Request.Builder()
                    .url("http://${bundyDeviceConfig[0].ipAddress}:${bundyDeviceConfig[0].port}/BundyClock/get-users?IpAddress=${bundyDevice.ipAddress}&Port=${bundyDevice.port}")
                    .method("GET", null)
                    .build()
            Response response = client.newCall(request).execute()


            if (response.successful) {
                def jsonString = response.body().string()

                dtos = objectMapper.readValue(jsonString, BundyUserDto[].class)

                return new GraphQLRetVal<BundyUserDto[]>(dtos, true)
            } else {
                return new GraphQLRetVal<BundyUserDto[]>(dtos, false, response.message())
            }
        } catch (Exception e) {
            return new GraphQLRetVal<BundyUserDto[]>(dtos, false, e.message + ". Please make sure the Biometric Service is Running")
        }

    }

    @GraphQLQuery(name = "get_biometric_service_config")
    GraphQLRetVal<BiometricServiceConfig> getBiometricServiceConfig() {
        BiometricServiceConfig biometricServiceConfig = new BiometricServiceConfig()

        //language=HQL
        def query = "select b from BiometricServiceConfig b order by b.createdDate asc"

        try {

            biometricServiceConfig =
                    entityManager.createQuery(query, BiometricServiceConfig.class)
                            .setFirstResult(0)
                            .setMaxResults(1)
                            .setHint(QueryHints.HINT_READONLY, true).resultList[0]


            return new GraphQLRetVal<BiometricServiceConfig>(biometricServiceConfig, true)

        } catch (Exception e) {

            return new GraphQLRetVal<BiometricServiceConfig>(biometricServiceConfig, false, e.message)

        }
    }

    @GraphQLQuery(name = "set_biometrics_uid")
    GraphQLRetVal<String> setBiometricsUid() {
        List<BiometricDevice> devices = biometricDeviceService.findAll()
        OkHttpClient client = new OkHttpClient()
        client.setConnectTimeout(5, TimeUnit.MINUTES)
        client.setReadTimeout(10, TimeUnit.MINUTES)

        devices.forEach({ device ->

            String cookies = getSession(device, client)
            if (cookies) {

                String username = device.deviceUsername
                String password = device.devicePassword

                def form = new ArrayList<NameValuePair>(2)
                def requestParams = new ArrayList<NameValuePair>(2)

                form.add(new BasicNameValuePair('username', username ?: ""))
                form.add(new BasicNameValuePair('userpwd', password ?: ""))

                String loginLink = HttpUrl.parse("""http://${device.ipAddress}/csl/check""")

                String queryLink = HttpUrl.parse("""http://${device.ipAddress}/csl/query?action=run""")
                String userQueryLink = HttpUrl.parse("""http://${device.ipAddress}/csl/user?first=1&last=4000000000000""")
                // purposely made long just to ensure we get all users


                MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded")
                RequestBody body = RequestBody.create(mediaType, contentBuilder(form) ?: "")

                def loginRequest = new com.squareup.okhttp.Request.Builder()
                        .url(loginLink)
                        .post(body)
                        .addHeader("Cookie", cookies)
                        .build()

                Response response2 = client.newCall(loginRequest).execute()

                if (response2.successful) {

                    // request the users to get the uid from the device
                    def userRequest = new com.squareup.okhttp.Request.Builder()
                            .url(userQueryLink)
                            .get()
                            .addHeader("Cookie", cookies)
                            .build()
                    Response userResponse = client.newCall(userRequest).execute()
                    String userHtml = userResponse.body().string()
                    Element userContainer = Jsoup.parse(userHtml).select('div#cc').get(0)
                    Element userTable = userContainer.select('table').get(0)

                    Elements userRows = userTable.select('tr')
                    userRows.each {
                        Element row ->
                            Elements cols = row.select('td')
                            String uid = row.select('td > input').get(0).attributes().get("value")
                            String name = cols.get(3).text()
                            List<Employee> employeeResult = entityManager
                                    .createQuery("Select e from Employee e where upper(concat(last_name , coalesce(' ' || nullif(first_name,'') , ''), coalesce(' ' || nullif(middle_name,'') , ''), coalesce(' ' || nullif(name_suffix,'') , ''))) like concat(upper(:empName),'%')", Employee.class)
                                    .setParameter("empName", name)
                                    .getResultList()
                            Employee employee = employeeResult[0]
                            if (employee) {

                                EmployeeBiometricConfig biometricConfig = new EmployeeBiometricConfig()
                                biometricConfig.employee = employee
                                biometricConfig.biometricDevice = device
                                biometricConfig.biometricNo = Integer.parseInt(uid)
                                empBiometricConfigRepository.save(biometricConfig)
                            }
                    }


                }

            } else {
                return new GraphQLRetVal<String>("ERROR", false, "One of the device was unresponsive")
            }

        })
        return new GraphQLRetVal<String>("OK", true, "Successfully matched the uids")
    }


    String contentBuilder(ArrayList<NameValuePair> params) {
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

    //================================QUERY================================\\

    //==============================MUTATIONS==============================\\

    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name = "save_biometric_attlog")
    GraphQLRetVal<String> saveBiometricAttLogs(
//    List<EmployeeAttendanceDto> saveBiometricAttLogs(
@GraphQLArgument(name = "logs") List<AttLogDto> logs,
@GraphQLArgument(name = "startDate") Instant startDate,
@GraphQLArgument(name = "endDate") Instant endDate
    ) {

//        if (logs.size() == 0 || !startDate || !endDate) return new GraphQLRetVal<String>("ERROR", false, 'Failed to save attendance logs to the database.')
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss").withZone(ZoneId.systemDefault())
//
//        Map<String, EmployeeAttendanceDto> savedAttendances = entityManager.createQuery("""
//            Select new com.hisd3.hismk2.domain.hrm.dto.EmployeeAttendanceDto(
//                 e.original_attendance_time,  e.originalType,  e.source,  e.isManual,  emp.id
//            ) from EmployeeAttendance e
//            left join fetch e.employee emp
//            where
//                e.original_attendance_time >= :startDate and e.original_attendance_time <= :endDate
//                and e.isManual is not true
//            order by e.original_attendance_time
//        """, EmployeeAttendanceDto.class).setParameter("startDate", startDate)
//                .setParameter("endDate", endDate)
//                .getResultStream()
//                .collect(Collectors.groupingBy({
//                    EmployeeAttendanceDto d ->
//                        String dateTime = formatter.format(d.attendance_time)
//                        String key = "${dateTime}_${d.originalType}_${d.source}"
//                        return key
//                })) as Map

        List<String> savedAttendances = empAttendanceRepository
                .getEmployeeAttendance(startDate, endDate)
                .stream()
                .collect {
                    String dateTime = formatter.format(it.originalAttendanceTime)
                    return "${it.employeeId}_${dateTime}_${it.originalType}_${it.source}"
                } as List<String>

        Map<String, Employee> employees = employeeRepository.
                findByIsActiveTrueAndExcludePayrollFalse()
                .stream()
                .inject([:]) { Map<String, Employee> map, Employee it -> map << [(it.id.toString()): it] } as Map<String, Employee>

        List<EmployeeAttendance> empAttendances = []
        logs.forEach({
            it ->
                Instant logTime = LocalDateTime.parse("${it.date} ${it.time}", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).atZone(ZoneId.systemDefault()).toInstant()
                String formattedDateTime = formatter.format(logTime)
                String key = "${it.employeeId}_${formattedDateTime}_${it.status}_${it.deviceName}"
                if (!savedAttendances.any { it == key }) {
                    EmployeeAttendance employeeAttendance = new EmployeeAttendance()
                    Employee foundEmployee = employees.get(it.employeeId)
//                    Employee emp = employeeRepository.findA(UUID.fromString(it.employeeId)).get()
                    if (foundEmployee) {
                        employeeAttendance.employee = foundEmployee
                        employeeAttendance.attendance_time = logTime
                        employeeAttendance.original_attendance_time = logTime
                        employeeAttendance.type = it.status
                        employeeAttendance.originalType = it.status
                        employeeAttendance.source = it.deviceName
                        if (it.verification == "Finger")
                            employeeAttendance.method = EmployeeAttendanceMethod.FINGER
                        empAttendances.push(employeeAttendance)
                    }
                }
        })
        if (empAttendances.size() > 0)
            empAttendanceRepository.saveAll(empAttendances)

        return new GraphQLRetVal<String>("OK", true, "Successfully saved attendance logs to the database.")
    }

    //==============================MUTATIONS==============================\\

}
