package com.hisd3.hismk2.domain.pms

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where

import javax.persistence.*
import java.time.Instant

@TypeChecked
@Entity
@Table(schema = "pms", name = "attachment")
@Where(clause = "deleted <> true or deleted is  null ")
class FileAttachment extends AbstractAuditingEntity {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "patient", referencedColumnName = "id")
	Patient patient
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "`case`", referencedColumnName = "id")
	Case patientCase
	
	@GraphQLQuery
	@Column(name = "file_name", columnDefinition = "varchar")
	String fileName
	
	@GraphQLQuery
	@Column(name = "description", columnDefinition = "varchar")
	String desc
	
	@GraphQLQuery
	@Column(name = "mimetype", columnDefinition = "varchar")
	String mimetype
	
	@GraphQLQuery
	@Column(name = "url_path", columnDefinition = "varchar")
	String urlPath
	
	@GraphQLQuery
	@Column(name = "deleted", columnDefinition = "bool")
	Boolean deleted
	
	@Transient
	Instant getCreated() {
		return createdDate
	}
}
