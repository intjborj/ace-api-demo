package com.hisd3.hismk2.graphqlservices.address

import com.hisd3.hismk2.domain.address.Barangay
import com.hisd3.hismk2.domain.address.City
import com.hisd3.hismk2.domain.address.Country
import com.hisd3.hismk2.domain.address.Municipality
import com.hisd3.hismk2.domain.address.Province
import com.hisd3.hismk2.domain.address.ProvinceState
import com.hisd3.hismk2.repository.address.BarangayRepository
import com.hisd3.hismk2.repository.address.CityRepository
import com.hisd3.hismk2.repository.address.CountryRepository
import com.hisd3.hismk2.repository.address.MunicipalityRepository
import com.hisd3.hismk2.repository.address.ProvinceRepository
import com.hisd3.hismk2.repository.address.ProvinceStateRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class AddressServices {

    @Autowired
    CountryRepository countryRepository

    @Autowired
    ProvinceRepository provinceRepository

    @Autowired
    CityRepository  cityRepository

    @Autowired
    MunicipalityRepository municipalityRepository

    @Autowired
    ProvinceStateRepository  provinceStateRepository

    @Autowired
    BarangayRepository barangayRepository


    @GraphQLQuery(name = "countries", description = "Search all countries")
    List<Country> getAllCountries(
//            @GraphQLArgument(name = "filter") String filter
    ) {
        countryRepository.findAll()
    }

    @GraphQLQuery(name = "countriesFilter", description = "Search all countries")
    List<Country> countriesFilter(
            @GraphQLArgument(name = "filter") String filter
    ) {
        countryRepository.searchCountryByFilter(filter)
    }

    @GraphQLQuery(name = "provinces", description = "Search all provinces")
    List<Province> getAllProvinces(
    ) {
        provinceRepository.findAll()
    }

    @GraphQLQuery(name = "stateProvinces", description = "Search all provinces")
    List<ProvinceState> getAllStateProvince(
    ) {
        provinceStateRepository.findAll()
    }

    @GraphQLQuery(name = "provincesFilter", description = "Search all provinces")
    List<Province> provincesFilter(
            @GraphQLArgument(name = "filter") String filter
    ) {
        provinceRepository.searchProvinceByFilter(filter)
    }

    @GraphQLQuery(name = "cities", description = "Search all cities")
    List<City> getCities(
            @GraphQLArgument(name = "province") String province
    ) {
        if(province){
            cityRepository.searchCities(province)
        }else{
            return null
        }

    }

    @GraphQLQuery(name = "municipalities", description = "Search all cities")
    List<Municipality> getMunicipalities(
            @GraphQLArgument(name = "filter") String filter
    ) {
        municipalityRepository.searchMunicipalityByProvince(filter)

    }

    @GraphQLQuery(name = "barangays", description = "Search all barangays")
    List<Barangay> getBarangays(
            @GraphQLArgument(name = "filter") String filter
    ) {
        barangayRepository.searchMunicipalityByProvince(filter)

    }
}
