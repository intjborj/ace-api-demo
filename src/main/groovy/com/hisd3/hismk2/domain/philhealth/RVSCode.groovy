package com.hisd3.hismk2.domain.philhealth

import io.leangen.graphql.annotations.GraphQLQuery

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(schema = "philhealth", name = "rvs_codes")
class RVSCode implements Serializable {
	
	@Id
	@GraphQLQuery
	@Column(name = "rvscode", columnDefinition = "varchar")
	String rvsCode
	
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
	@Column(name = "primary_prof_share1", columnDefinition = "numeric")
	BigDecimal primaryProfShare1
	
	@GraphQLQuery
	@Column(name = "secondary_hosp_share", columnDefinition = "numeric")
	BigDecimal secondaryHospShare
	
	@GraphQLQuery
	@Column(name = "secondary_prof_share", columnDefinition = "numeric")
	BigDecimal secondaryProfShare
	
	@GraphQLQuery
	@Column(name = "check_facility_h2", columnDefinition = "varchar")
	String checkFacilityH2
	
	@GraphQLQuery
	@Column(name = "eff_date", columnDefinition = "varchar")
	String effDate
	
	@GraphQLQuery
	@Column(name = "eff_end_date", columnDefinition = "varchar")
	String effEndDate
}
