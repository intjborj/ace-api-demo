package com.hisd3.hismk2.domain.ancillary

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where

import javax.persistence.*
import java.time.Instant

@TypeChecked
@Entity
@Table(schema = "ancillary", name = "notes")
@SQLDelete(sql = "UPDATE notes SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class AncillaryNote extends AbstractAuditingEntity {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "domain_name", columnDefinition = "varchar")
	String domainName
	
	@GraphQLQuery
	@Column(name = "pk_id", columnDefinition = "uuid")
	UUID pkId
	
	@GraphQLQuery
	@Column(name = "details", columnDefinition = "varchar")
	String details
	
	@GraphQLQuery
	@Column(name = "deleted", columnDefinition = "boolean")
	Boolean deleted
	
	@Transient
	Instant getCreated() {
		return createdDate
	}
	
}
