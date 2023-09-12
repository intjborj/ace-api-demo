package com.hisd3.hismk2.rest

import com.google.gson.Gson
import com.hisd3.hismk2.domain.address.Barangay
import com.hisd3.hismk2.domain.address.Country
import com.hisd3.hismk2.domain.address.Municipality
import com.hisd3.hismk2.domain.address.ProvinceState
import com.hisd3.hismk2.domain.address.Region
import com.hisd3.hismk2.repository.address.BarangayRepository
import com.hisd3.hismk2.repository.address.CountryRepository
import com.hisd3.hismk2.repository.address.MunicipalityRepository
import com.hisd3.hismk2.repository.address.ProvinceStateRepository
import com.hisd3.hismk2.repository.address.RegionRepository
import com.hisd3.hismk2.rest.dto.PatientBasicDto
import com.hisd3.hismk2.utils.RequestDumpUtil
import org.apache.http.HttpEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.params.HttpProtocolParams
import org.apache.http.util.EntityUtils
import org.json.JSONArray
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.user.SimpUser
import org.springframework.messaging.simp.user.SimpUserRegistry
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/public")
class PublicResource {


    @Autowired
    SimpUserRegistry simpUserRegistry

    @Autowired
    JdbcTemplate jdbcTemplate

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate

    @Autowired
    PasswordEncoder passwordEncoder

    @Autowired
    CountryRepository countryRepository

    @Autowired
    RegionRepository regionRepository

    @Autowired
    ProvinceStateRepository provinceStateRepository

    @Autowired
    MunicipalityRepository municipalityRepository

    @Autowired
    BarangayRepository barangayRepository
/*
    @RequestMapping(value = "/passwordEncoder", produces = ["text/plain"] )
    String passwordEncoder(@RequestParam String password) {

        return passwordEncoder.encode(password)

    }*/
    @RequestMapping(value = "/remoteIp", produces = ["text/plain"] )
    String remoteIp(HttpServletRequest request) {

        def sb = new StringBuilder()
        RequestDumpUtil.dumpRequest(sb,request)
        sb.append("====\n")
        RequestDumpUtil.dumpRequestHeader(sb,request)
        sb.append("====\n")
        RequestDumpUtil.dumpRequestParameter(sb,request)
        sb.append("====\n")
        RequestDumpUtil.dumpRequestSessionAttribute(sb,request)

        return sb.toString()
    }
    @RequestMapping(value = "/getUsers" )
    List<String> getUsers(HttpServletRequest request) {

        List<String> forRet = new ArrayList<>();
        for(SimpUser simpUser in simpUserRegistry.users)
        {
            forRet.add(simpUser.name)
        }
        return forRet
    }
    @RequestMapping(value = "/testCompare" )
    List<String> testCompareTo(HttpServletRequest request) {
        String input = "Bayocboc"
        String comparison1 = "BAYOCBOC GREGG"
        String comparison2 = "BAYOCBOK GREGG"
        String comparison3 = "limaad"
        String comparison4 = "elopre"
        String comparison5 = "baayocbocz"
        String comparison6 = "baayocboc"
        String comparison7 = "bayocbo"
        String comparison8 = "boyocboc"
        println input.compareToIgnoreCase(comparison1)
        println input.compareToIgnoreCase(comparison2)
        println input.compareToIgnoreCase(comparison3)
        println input.compareToIgnoreCase(comparison4)
        println input.compareToIgnoreCase(comparison5)
        println input.compareToIgnoreCase(comparison6)
        println input.compareToIgnoreCase(comparison7)
        println input.compareToIgnoreCase(comparison8)
    }

    @RequestMapping(value = "/getPts", produces = ["application/JSON"] )
    List<PatientBasicDto> getAllPatientDto(HttpServletRequest request){
        println request.remoteUser
        String sql = "select id,first_name as \"firstname\", last_name as \"lastname\", middle_name as \"middlename\", dob,name_suffix as \"suffix\" from pms.patients"
        List<PatientBasicDto> items = jdbcTemplate.query(sql, new BeanPropertyRowMapper(PatientBasicDto.class))
        List<PatientBasicDto> close = new ArrayList<>()





        return items
    }
    @RequestMapping(method = RequestMethod.GET, value = "/updateAddressFromGithub",produces = ["application/JSON"])
    String updateAddressFromGithub () {
        Gson gson = new Gson()


        CloseableHttpClient httpClient = HttpClients.createDefault()


        //def request = new HttpPost("http://localhost:4567/msgreceiver")

        def results = new HttpGet("https://raw.githubusercontent.com/flores-jacob/philippine-regions-provinces-cities-municipalities-barangays/master/philippine_provinces_cities_municipalities_and_barangays_2019v2.json")

        results.getParams().setParameter(HttpProtocolParams.HTTP_CONTENT_CHARSET, "UTF-8")
        results.setHeader("Accept", "application/json")
        results.setHeader("Accept-Encoding", "UTF-8")
        results.setHeader("Content-type", "application/json")
        try {
            def response = httpClient.execute(results)
            HttpEntity responseEntity = response.entity
            if (response.statusLine.statusCode == 500) {
                throw new Exception()
                httpClient.close()
            }

            Country country = countryRepository.searchCountryByFilter("Philippines").first()

            def responseString = EntityUtils.toString(responseEntity, "UTF-8")

            JSONObject region = new JSONObject(responseString)
            //JsonObject jsonObject = new JsonParser().parse(responseString).getAsJsonObject()


            region.keys().forEachRemaining{key ->
                JSONObject regions = region.get(key)

                String  regName = region.getJSONObject(key).get("region_name")
                Region regionAdd = new Region()
                List<Region> resRegion = regionRepository.searchRegionByFilter(regName)
                if(resRegion.size() == 0) {

                    regionAdd.name = regName
                    regionAdd.country = country
                    regionRepository.save(regionAdd)
                }else{
                    regionAdd = resRegion[0]
                }

                regions.keys().forEachRemaining{it ->

                    if(it == "region_name" ){
                        println(regName.toString())
                    }
                    else {
                        println(it.toString())

                        JSONObject provinces = regions.get(it)

                        provinces.keys().forEachRemaining { p ->

                            ProvinceState stateProvince = new ProvinceState()
                            List<ProvinceState> state = provinceStateRepository.searchStateByFilter(p.toString())
                            if(state.size() == 0) {

                                stateProvince.name = p
                                stateProvince.region = regionAdd
                                provinceStateRepository.save(stateProvince)
                            }
                            else {stateProvince = state[0] }

                            JSONObject province = provinces.getJSONObject(p)

                            province.keys().forEachRemaining { b ->

                                println("municipals list :" + b.toString())

                                JSONObject municipals = province.getJSONObject(b)

                                municipals.keys().forEachRemaining {m  ->

                                    println("Municipality :" + m)

                                    Municipality poblacion = new Municipality()
                                    List<Municipality> municipal = municipalityRepository.searchMunicipalityByFilter(m.toString())
                                    if(municipal.size() == 0) {
                                        poblacion.name = m
                                        poblacion.province = stateProvince
                                        municipalityRepository.save(poblacion)
                                    }else{
                                        poblacion = municipal[0]
                                    }

                                    JSONArray barangays = municipals.getJSONObject(m).getJSONArray("barangay_list")


                                    for (int i = 0; i < barangays.length(); i++) {

                                        List<Barangay> brgy = barangayRepository.searchBarangayByFilter(m.toString())
                                        if(brgy.size() == 0) {

                                            String post_id = barangays.get(i)
                                            System.out.println(post_id)

                                            Barangay barangay = new Barangay()
                                            barangay.name = post_id
                                            barangay.municipality = poblacion

                                            barangayRepository.save(barangay)

                                        }
                                    }


//
                                }

                            }

                        }
                    }

                }


            }


            httpClient.close()
            return gson.toJson(responseString)
        }
        catch (e) {
            throw e
        }
    }
}
