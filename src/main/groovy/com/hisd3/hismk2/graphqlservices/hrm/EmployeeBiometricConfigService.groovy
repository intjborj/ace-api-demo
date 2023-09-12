package com.hisd3.hismk2.graphqlservices.hrm

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.hrm.BiometricDevice
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.hrm.EmployeeBiometricConfig
import com.hisd3.hismk2.graphqlservices.bundy.BundyClockService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.hrm.EmployeeBiometricConfigRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.squareup.okhttp.HttpUrl
import com.squareup.okhttp.MediaType
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.RequestBody
import com.squareup.okhttp.Response
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.http.message.BasicNameValuePair
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.omg.CORBA.NameValuePair
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.persistence.EntityManager


@Component
@GraphQLApi
class EmployeeBiometricConfigService {

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    EmployeeBiometricConfigRepository employeeBiometricConfigRepository

    @Autowired
    EmployeeRepository employeeRepository

    @Autowired
    BiometricDeviceService biometricDeviceService

    @Autowired
    BundyClockService bundyClockService

    @Autowired
    EntityManager entityManager

    @GraphQLQuery(name = "findAllBiometricInEmployee", description = "findAllBiometricInEmployee")
    List<EmployeeBiometricConfig>findAllBiometricInEmployee(){
        return employeeBiometricConfigRepository.findAllBiometricInEmployee()
    }

    @GraphQLQuery(name = "findAllEmployeeBiometric", description = "Search Biometric Device")
    List<EmployeeBiometricConfig> findAllEmployeeBiometric(@GraphQLArgument(name = "id") UUID id) {
        employeeBiometricConfigRepository.findAllEmployeeBiometric(id)
    }


//    String getSession(BiometricDevice device, OkHttpClient client) {
//        if (!device.session) {
//
//            HttpUrl parsedUrl = HttpUrl.parse("""http://${device.ipAddress}/""")
//
//            def request = new com.squareup.okhttp.Request.Builder()
//                    .url(parsedUrl)
//                    .get()
//                    .build()
//
//            com.squareup.okhttp.Response response = client.newCall(request).execute()
//
//            if (response.successful) {
//                List<String> Cookielist = response.headers().values("Set-Cookie")
//                String cookies = ""
//
//                Cookielist.each {
//                    cookies += it.split(';')[0]
//                }
//                device.session = cookies
//                biometricDeviceService.save(device)
//                return device.session
//            } else return null
//        } else return device.session
//    }


    @GraphQLMutation
    GraphQLRetVal<String> postEmployeeBiometric(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") List<Map<String, Object>> fields,
            @GraphQLArgument(name = "employee_id") UUID employee_id,
            @GraphQLArgument(name = "biometric_id") UUID biometric_id
    ) {
        if (!employee_id) return new GraphQLRetVal<String>("Error", false, "false")
        Employee employee = employeeRepository.findById(employee_id).get()

        if (!biometric_id) return new GraphQLRetVal<String>("Error", false, "false")
        BiometricDevice biometricDevice = biometricDeviceService.find(biometric_id)

        fields.forEach({
            EmployeeBiometricConfig employeeBiometricConfig = objectMapper.convertValue(it, EmployeeBiometricConfig)
            employeeBiometricConfig.employee = employee
            employeeBiometricConfig.biometricDevice = biometricDevice
            employeeBiometricConfigRepository.save(employeeBiometricConfig)
        })
        return new GraphQLRetVal<String>("OK", true, "Successfully Saved")
    }


    @GraphQLMutation
    GraphQLRetVal<String> deleteEmployeeBiometric(
            @GraphQLArgument(name = "id") UUID id
    ) {
        if (!id) return new GraphQLRetVal<String>("Error", false, "Failed to Delete")

        EmployeeBiometricConfig employeeBiometricConfig = employeeBiometricConfigRepository.findById(id).get()
        employeeBiometricConfigRepository.delete(employeeBiometricConfig)
        return new GraphQLRetVal<String>("Ok", true, "Successfully deleted")
    }

    @GraphQLMutation(name = "getEmployeeNo")
    GraphQLRetVal<String> getEmployeeNo(
            @GraphQLArgument(name = 'id') UUID id,
            @GraphQLArgument(name = 'employee_id') UUID employee_id,
            @GraphQLArgument(name = 'biometric_id') UUID biometric_id

    ) {
        BiometricDevice device = biometricDeviceService.findOne(biometric_id)
        Employee emp = employeeRepository.findById(employee_id).get()


        if (device == null) return new GraphQLRetVal<String>("ok", true, "successful")

        OkHttpClient client = new OkHttpClient()

//        HttpUrl parsedUrl = HttpUrl.parse("""http://${device.ipAddress}/""")
//
//        def request = new com.squareup.okhttp.Request.Builder()
//            .url(parsedUrl)
//            .get()
//            .build()
//
//      com.squareup.okhttp.Response response = client.newCall(request).execute()

        String cookies = bundyClockService.getSession(device, client)
        if (cookies) {
            String username = device.deviceUsername
            String password = device.devicePassword

            def form = new ArrayList<NameValuePair>(2)
            def requestParams = new ArrayList<NameValuePair>(2) 

            form.add(new BasicNameValuePair('username', username ?: ""))
            form.add(new BasicNameValuePair('userpwd', password ?: ""))


            String loginLink = HttpUrl.parse("""http://${device.ipAddress}/csl/check""")

            String userQueryLink = HttpUrl.parse("""http://${device.ipAddress}/csl/user?first=1&last=4000000000000""")

            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded")
            RequestBody body = RequestBody.create(mediaType, contentBuilder(form) ?: "")

            def loginRequest = new Request.Builder()
                    .url(loginLink)
                    .post(body)
                    .addHeader("Cookie", cookies)
                    .build()

            Response response = client.newCall(loginRequest).execute()

            if (response.successful) {

                // request the users to get the uid from the device
                def userRequest = new Request.Builder()
                        .url(userQueryLink)
                        .get()
                        .addHeader("Cookie", cookies)
                        .build()
                Response userResponse = client.newCall(userRequest).execute()
                String userHtml = userResponse.body().string()
                Element userContainer = Jsoup.parse(userHtml).select('div#cc').get(0)
                Element userTable = userContainer.select('table').get(0)

                Boolean hasFoundUser = false
                Elements userRows = userTable.select('tr')
                Element foundUserRow = userRows.find {
                    row ->
                        Elements cols = row.select("td")
                        String empName = cols.get(3).text()
                        String employeeFullName = ("${emp.lastName} ${emp.firstName} ${emp.middleName}").toUpperCase()
                        boolean foundUser =employeeFullName.startsWith(empName)
                        return foundUser

                }
                if(foundUserRow){
                    hasFoundUser = true
                    String uid = foundUserRow.select('td > input').get(0).attributes().get("value")
                    EmployeeBiometricConfig employeeBiometricConfig = new EmployeeBiometricConfig()
                    employeeBiometricConfig.employee = emp
                    employeeBiometricConfig.biometricDevice = device
                    employeeBiometricConfig.biometricNo = Integer.parseInt(uid)
                    employeeBiometricConfigRepository.save(employeeBiometricConfig)
                }else hasFoundUser = false

                if (hasFoundUser) return new GraphQLRetVal<String>("OK", true, "Successfully assigned user to biometric")
                else return new GraphQLRetVal<String>("ERROR", false, "Found no user in biometrics")

            }
        } else {
            return new GraphQLRetVal<String>("Ok", false, "fail")
        }
    }


    String contentBuilder(ArrayList<org.apache.http.NameValuePair> params) {
        String contentBuilder = ""

        params.eachWithIndex { org.apache.http.NameValuePair entry, int i ->
            if (i == params.size() - 1) {
                contentBuilder += entry.name + "=" + entry.value
            } else {
                contentBuilder += entry.name + "=" + entry.value + "&"
            }
        }

        return contentBuilder
    }

    @GraphQLMutation
    GraphQLRetVal<String> deleteBiometricAssign(
            @GraphQLArgument(name = "id") UUID id
    ) {
        if (id) {
            employeeBiometricConfigRepository.deleteById(id)
            return new GraphQLRetVal<String>("OK", true, "Successfully deleted biometric")
        }
        return new GraphQLRetVal<String>("ERROR", false, "Failed to delete biometric")

    }


}
