package com.hisd3.hismk2.domain.bms

import io.leangen.graphql.annotations.GraphQLQuery

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(schema = "philhealth", name = "icd_codes")
class ICDCode implements Serializable {
	
	@Id
	@GraphQLQuery
	@Column(name = "diagnosis_code", columnDefinition = "varchar")
	String diagnosisCode
	
	@GraphQLQuery
	@Column(name = "short_name", columnDefinition = "varchar")
	String shortName
	
	@GraphQLQuery
	@Column(name = "long_name", columnDefinition = "varchar")
	String longName
	
	@GraphQLQuery
	@Column(name = "primary_amount1", columnDefinition = "numeric")
	BigDecimal primaryAmount1
	
	@GraphQLQuery
	@Column(name = "primary_hosp_share1", columnDefinition = "numeric")
	BigDecimal primaryHospShare1
	
	@GraphQLQuery
	@Column(name = "secondary_hosp_share", columnDefinition = "numeric")
	BigDecimal secondaryHospShare
	
	@GraphQLQuery
	@Column(name = "secondary_prof_share", columnDefinition = "numeric")
	BigDecimal secondaryProfShare
	
	@GraphQLQuery
	@Column(name = "primary_prof_share1", columnDefinition = "numeric")
	BigDecimal primaryProfShare1
	
	@GraphQLQuery
	@Column(name = "ACR_GROUPID", columnDefinition = "varchar")
	String acrGroupId
	
	@GraphQLQuery
	@Column(name = "\"EFF_DATE\"", columnDefinition = "varchar")
	String effDate
	
	@GraphQLQuery
	@Column(name = "\"EFF_END_DATE\"", columnDefinition = "varchar")
	String effEndDate
	
	@GraphQLQuery
	@Column(name = "check_facility_h1", columnDefinition = "varchar")
	String checkFacilityH1
	
	@GraphQLQuery
	@Column(name = "check_facility_h2", columnDefinition = "varchar")
	String checkFacilityH2
	
}
