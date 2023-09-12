package com.hisd3.hismk2.graphqlservices.doh


import com.hisd3.hismk2.domain.doh.DischargeMobidity
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.views.doh.MorbidityByAgeView
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.doh.DischargeMobidityRepository
import com.hisd3.hismk2.repository.pms.CaseRepository
import com.hisd3.hismk2.repository.referential.DohIcdCodeRepository
import com.hisd3.hismk2.rest.dto.DohIcdCodeDto
import com.hisd3.hismk2.rest.dto.HospitalDischargeMorbidityDto
import com.hisd3.hismk2.utils.SOAPConnector
import groovy.json.JsonSlurper
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Component
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesMorbidity
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesMorbidityResponse

import java.sql.ResultSet
import java.sql.SQLException
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId

@Component
@GraphQLApi
class HospitalDischargeMorbidityService extends AbstractDaoService<MorbidityByAgeView> {

    HospitalDischargeMorbidityService() {
        super(MorbidityByAgeView.class)
    }

    @Autowired
    CaseRepository caseRepository

    @Autowired
    SOAPConnector soapConnector

    @Autowired
    DischargeMobidityRepository dischargeMobidityRepository

    @Autowired
    HospitalConfigService hospitalConfigService

    @Autowired
    JdbcTemplate jdbcTemplate

    @Autowired
    DohIcdCodeRepository dohIcdCodeRepository

    @GraphQLQuery(name = "hospOptDischargesMorbidity")
    HospitalDischargeMorbidityDto hospOptDischargesMorbidity(@GraphQLArgument(name = 'icdRvsCode') String icdRvsCode, @GraphQLArgument(name = "year") Integer year) {
        List<Case> caseList = caseRepository.findAll()
        List<HospitalDischargeMorbidityDto> morbidityDtoList = new ArrayList<>()
        HospitalDischargeMorbidityDto dto = new HospitalDischargeMorbidityDto()
        def jsonSlurper = new JsonSlurper()

        caseList.findAll {
            it.admissionDatetime != null && it.admissionDatetime.atZone(ZoneId.systemDefault()).getYear() == year
        }.eachWithIndex { Case entry, int i ->
            Integer age = Period.between(entry.patient.dob, LocalDate.now()).years

            if (StringUtils.equalsAnyIgnoreCase(icdRvsCode, 'all')) {
                if (StringUtils.equalsIgnoreCase(entry.patient.gender, 'male')) {

                    if (age < 1) {
                        dto.maleUnder1++
                    } else if (age >= 1 && age <= 4) {
                        dto.male14++
                    } else if (age >= 5 && age <= 9) {
                        dto.male59++
                    } else if (age >= 10 && age <= 14) {
                        dto.male1014++
                    } else if (age >= 15 && age <= 19) {
                        dto.male1519++
                    } else if (age >= 20 && age <= 24) {
                        dto.male2024++
                    } else if (age >= 25 && age <= 29) {
                        dto.male2529++
                    } else if (age >= 30 && age <= 34) {
                        dto.male3034++
                    } else if (age >= 35 && age <= 39) {
                        dto.male3539++
                    } else if (age >= 40 && age <= 44) {
                        dto.male4044 += 1
                    } else if (age >= 45 && age <= 49) {
                        dto.male4549++
                    } else if (age >= 50 && age <= 59) {
                        dto.male5054++
                    } else if (age >= 55 && age <= 59) {
                        dto.male5559++
                    } else if (age >= 60 && age <= 64) {
                        dto.male6064++
                    } else if (age >= 65 && age <= 69) {
                        dto.male6569++
                    } else if (age >= 70) {
                        dto.male70Over++
                    }

                    dto.maleSubtotal++


                } else {

                    if (age < 1) {
                        dto.femaleUnder1++
                    } else if (age >= 1 && age <= 4) {
                        dto.female14++
                    } else if (age >= 5 && age <= 9) {
                        dto.female59++
                    } else if (age >= 10 && age <= 14) {
                        dto.female1014++
                    } else if (age >= 15 && age <= 19) {
                        dto.female1519++
                    } else if (age >= 20 && age <= 24) {
                        dto.female2024++

                    } else if (age >= 25 && age <= 29) {
                        dto.female2529++
                    } else if (age >= 30 && age <= 34) {
                        dto.female3034++
                    } else if (age >= 35 && age <= 39) {
                        dto.female3539++
                    } else if (age >= 40 && age <= 44) {
                        dto.female4044++
                    } else if (age >= 45 && age <= 49) {
                        dto.female4549++
                    } else if (age >= 50 && age <= 54) {
                        dto.female5054++
                    } else if (age >= 55 && age <= 59) {
                        dto.female5559++
                    } else if (age >= 60 && age <= 64) {
                        dto.female6064++
                    } else if (age >= 65 && age <= 69) {
                        dto.female6569++
                    } else if (age >= 70) {
                        dto.female70Over++
                    }

                    dto.femaleSubtotal++
                }
            } else if (StringUtils.isNotBlank(entry.primaryDx)) {

                def primary = jsonSlurper.parseText(entry.primaryDx) as Map<String, Object>

                if ((primary.containsKey("diagnosisCode") && primary["diagnosisCode"] == icdRvsCode) || (primary.containsKey("rvsCode") && primary["rvsCode"] == icdRvsCode)) {

                    if (StringUtils.equalsIgnoreCase(entry.patient.gender, 'male')) {

                        if (age < 1) {
                            dto.maleUnder1++
                        } else if (age >= 1 && age <= 4) {
                            dto.male14++
                        } else if (age >= 5 && age <= 9) {
                            dto.male59++
                        } else if (age >= 10 && age <= 14) {
                            dto.male1014++
                        } else if (age >= 15 && age <= 19) {
                            dto.male1519++
                        } else if (age >= 20 && age <= 24) {
                            dto.male2024++
                        } else if (age >= 25 && age <= 29) {
                            dto.male2529++
                        } else if (age >= 30 && age <= 34) {
                            dto.male3034++
                        } else if (age >= 35 && age <= 39) {
                            dto.male3539++
                        } else if (age >= 40 && age <= 44) {
                            dto.male4044 += 1
                        } else if (age >= 45 && age <= 49) {
                            dto.male4549++
                        } else if (age >= 50 && age <= 59) {
                            dto.male5054++
                        } else if (age >= 55 && age <= 59) {
                            dto.male5559++
                        } else if (age >= 60 && age <= 64) {
                            dto.male6064++
                        } else if (age >= 65 && age <= 69) {
                            dto.male6569++
                        } else if (age >= 70) {
                            dto.male70Over++
                        }

                        dto.maleSubtotal++


                    } else {

                        if (age < 1) {
                            dto.femaleUnder1++
                        } else if (age >= 1 && age <= 4) {
                            dto.female14++
                        } else if (age >= 5 && age <= 9) {
                            dto.female59++
                        } else if (age >= 10 && age <= 14) {
                            dto.female1014++
                        } else if (age >= 15 && age <= 19) {
                            dto.female1519++
                        } else if (age >= 20 && age <= 24) {
                            dto.female2024++

                        } else if (age >= 25 && age <= 29) {
                            dto.female2529++
                        } else if (age >= 30 && age <= 34) {
                            dto.female3034++
                        } else if (age >= 35 && age <= 39) {
                            dto.female3539++
                        } else if (age >= 40 && age <= 44) {
                            dto.female4044++
                        } else if (age >= 45 && age <= 49) {
                            dto.female4549++
                        } else if (age >= 50 && age <= 54) {
                            dto.female5054++
                        } else if (age >= 55 && age <= 59) {
                            dto.female5559++
                        } else if (age >= 60 && age <= 64) {
                            dto.female6064++
                        } else if (age >= 65 && age <= 69) {
                            dto.female6569++
                        } else if (age >= 70) {
                            dto.female70Over++
                        }

                        dto.femaleSubtotal++
                    }
                }

            }
        }

        dto.grandTotal = dto.maleSubtotal + dto.femaleSubtotal

        return dto
    }

    @GraphQLMutation(name = "postHospitalDichargeMorbidity")
    GraphQLRetVal<String> postHospitalDichargeMorbidity(@GraphQLArgument(name = 'fields') Map<String, Object> fields) {

        try {
            HospOptDischargesMorbidity request = new HospOptDischargesMorbidity()

            request.hfhudcode = hospitalConfigService.hospitalInfo.hfhudcode ?: ""
            request.icd10Desc = fields.get('icd10Desc') as String
            request.munder1 = fields.get('maleUnder1') as Integer
            request.funder1 = fields.get('femaleUnder1') as Integer
            request.m1To4 = fields.get('male14') as Integer
            request.f1To4 = fields.get('female14') as Integer
            request.m5To9 = fields.get('male59') as Integer
            request.f5To9 = fields.get('female59') as Integer
            request.m10To14 = fields.get('male1014') as Integer
            request.f10To14 = fields.get('female1014') as Integer
            request.m15To19 = fields.get('male1519') as Integer
            request.f15To19 = fields.get('female1519') as Integer
            request.m20To24 = fields.get('male2024') as Integer
            request.f20To24 = fields.get('female2024') as Integer
            request.m25To29 = fields.get('male2529') as Integer
            request.f25To29 = fields.get('female2529') as Integer
            request.m30To34 = fields.get('male3034') as Integer
            request.f30To34 = fields.get('female3034') as Integer
            request.m35To39 = fields.get('male3539') as Integer
            request.f35To39 = fields.get('female3539') as Integer
            request.m40To44 = fields.get('male4044') as Integer
            request.f40To44 = fields.get('female4044') as Integer
            request.m45To49 = fields.get('male4549') as Integer
            request.f45To49 = fields.get('female4549') as Integer
            request.m50To54 = fields.get('male5054') as Integer
            request.f50To54 = fields.get('female5054') as Integer
            request.m55To59 = fields.get('male5559') as Integer
            request.f55To59 = fields.get('female5559') as Integer
            request.m60To64 = fields.get('male6064') as Integer
            request.f60To64 = fields.get('female6064') as Integer
            request.m65To69 = fields.get('male5669') as Integer
            request.f65To69 = fields.get('female6569') as Integer
            request.m70Over = fields.get('male70Over') as Integer
            request.f70Over = fields.get('female70Over') as Integer
            request.msubtotal = fields.get('maleSubtotal') as Integer
            request.fsubtotal = fields.get('femaleSubtotal') as Integer
            request.grandtotal = fields.get('grandTotal') as Integer
            request.icd10Code = fields.get('icd10Code') as String
            request.icd10Category = fields.get('diagnosisCategory') as String
            request.reportingyear = fields.get('reportingYear') as Integer

            HospOptDischargesMorbidityResponse response =
                    (HospOptDischargesMorbidityResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/hospOptDischargesMorbidity", request)

            UUID id = UUID.fromString(fields.get('id') as String)
            DischargeMobidity dto = dischargeMobidityRepository.getOne(id)
            dto.dohResponse = response.return

            dischargeMobidityRepository.save(dto)

            return new GraphQLRetVal<String>(response.return, true)
        } catch (Exception e) {
            return new GraphQLRetVal<String>(e.message, false)

        }
    }

    @GraphQLQuery(name = "dohIcd10Custom")
    List<DohIcdCodeDto> dohIcd10Custom() {
        def items = jdbcTemplate.queryForList("SELECT  icdcode, longname FROM pms.morbidity_by_age_view limit 10")
        List<DohIcdCodeDto> dtoList = new ArrayList<>()
        items.each {
            DohIcdCodeDto dto = new DohIcdCodeDto()
            dto.icdCode = it.icdcode
            dto.icdDesc = it.longname
            dtoList << dto
        }

        return dtoList
    }

    @GraphQLQuery(name = "hospOptDischargesMorbidityV2")
    HospitalDischargeMorbidityDto hospOptDischargesMorbidityV2(@GraphQLArgument(name = 'icdRvsCode') String icdRvsCode, @GraphQLArgument(name = "year") Integer year) {
        def parameters = [:]
        int currentYear = Calendar.getInstance().get(Calendar.YEAR)
        List<MorbidityByAgeView> morbidityByAgeViews
        HospitalDischargeMorbidityDto dto = new HospitalDischargeMorbidityDto()

        if (StringUtils.isBlank(icdRvsCode) || icdRvsCode == 'all') {
            parameters.put('year', year ?: currentYear)
            morbidityByAgeViews = createQuery('select m from MorbidityByAgeView m where m.reportingYear = :year', parameters).resultList
        } else {
            def dohIcdCode = jdbcTemplate.query(
                    """SELECT icd10_code, icd10_desc, icd10_cat, id, doh_icd_cat FROM referential.doh_icd_codes where icd10_code = '${
                        icdRvsCode
                    }'"""
                    , new RowMapper<Map<String, Object>>() {
                @Override
                Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
                    def mapper = [:]
                    mapper.put('icd10Category', rs.getString('icd10_cat'))
                    mapper.put('icd10Code', rs.getString('icd10_code'))
                    mapper.put('icd10Desc', rs.getString('icd10_desc'))
                    return mapper
                }
            })

            if (dohIcdCode) {
                dto.diagnosisCategory = dohIcdCode.icd10Category
            }
            parameters.put('icdCode', icdRvsCode)
            parameters.put('year', year ?: currentYear)
            morbidityByAgeViews = createQuery("select m from MorbidityByAgeView m where m.icd10Code = :icdCode and m.reportingYear = :year", parameters).resultList
        }


        morbidityByAgeViews.each {
            item ->
                if(icdRvsCode != "all" && icdRvsCode != "" && icdRvsCode != null){
                    dto.icd10Code = item.icd10Code
                    dto.icd10Desc = item.longname
                }
                dto.reportingYear = year ?: currentYear
                dto.maleUnder1 += item.munder1 ?: 0
                dto.femaleUnder1 += item.funder1 ?: 0
                dto.male14 += item.m1to4
                dto.female14 += item.f1to4
                dto.male59 += item.m5to9
                dto.female59 += item.f5to9
                dto.male1014 += item.m10to14
                dto.female1014 += item.f10to14
                dto.male1519 += item.m15to19
                dto.female1519 += item.f15to19
                dto.male2024 += item.m20to24
                dto.female2024 += item.f20to24
                dto.male2529 += item.m25to29
                dto.female2529 += item.f25to29
                dto.male3034 += item.m30to34
                dto.female3034 += item.f30to34
                dto.male3539 += item.m35to39
                dto.female3539 += item.f35to39
                dto.male4044 += item.m40to44
                dto.female4044 += item.f40to44
                dto.male4549 += item.m45to49
                dto.female4549 += item.f45to49
                dto.male5054 += item.m50to54
                dto.female5054 += item.f50to54
                dto.male5559 += item.m55to59
                dto.female5559 += item.f55to59
                dto.male6064 += item.m60to64
                dto.female6064 += item.f60to64
                dto.male6569 += item.m65to69
                dto.female6569 += item.f65to69
                dto.male70Over += item.m70over ?: 0
                dto.female70Over += item.f70over ?: 0
                dto.maleSubtotal += item.msubtotal
                dto.femaleSubtotal += item.fsubtotal
        }

        dto.grandTotal = dto.maleSubtotal + dto.femaleSubtotal


        return dto
    }
}
