package com.hisd3.hismk2.domain.payroll

import com.fasterxml.jackson.annotation.JsonIgnore
import io.leangen.graphql.annotations.GraphQLQuery
import org.javers.core.metamodel.annotation.DiffIgnore
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate

import javax.persistence.Column
import javax.persistence.MappedSuperclass
import java.time.Instant

@MappedSuperclass
class AbstractFinalizeEntity {


	@DiffIgnore
	@GraphQLQuery
	@LastModifiedBy
	@Column(name = "finalized_by", length = 50)
	@JsonIgnore
	String finalizedBy

	@DiffIgnore
	@GraphQLQuery
	@LastModifiedDate
	@Column(name = "finalized_date")
	@JsonIgnore
	Instant finalizedDate
}
