package com.hisd3.hismk2.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import com.vladmihalcea.hibernate.type.json.JsonStringType
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
import org.javers.core.metamodel.annotation.DiffIgnore
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

import javax.persistence.Column
import javax.persistence.EntityListeners
import javax.persistence.MappedSuperclass
import java.time.Instant


@TypeDefs([
		@TypeDef(name = "json", typeClass = JsonStringType.class),
		@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
 ]
)
@MappedSuperclass
@EntityListeners(AuditingEntityListener)
class AbstractAuditingEntity {


	@DiffIgnore
	@GraphQLQuery
	@CreatedBy
	@Column(name = "created_by", nullable = false, length = 50, updatable = false)
	@JsonIgnore
	String createdBy

	@DiffIgnore
	@GraphQLQuery
	@CreatedDate
	@Column(name = "created_date", nullable = false)
	Instant createdDate

	@DiffIgnore
	@GraphQLQuery
	@LastModifiedBy
	@Column(name = "last_modified_by", length = 50)
	@JsonIgnore
	String lastModifiedBy

	@DiffIgnore
	@GraphQLQuery
	@LastModifiedDate
	@Column(name = "last_modified_date")
	@JsonIgnore
	Instant lastModifiedDate
}
