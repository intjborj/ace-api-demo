package com.hisd3.hismk2.domain.hospital_config

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.accounting.AccountReceivableItems
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where
import org.javers.core.metamodel.annotation.ShallowReference

import javax.persistence.*

@Entity
@SQLDelete(sql = "UPDATE hospital_configuration.clinics_doctors SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
@Table(schema = "hospital_configuration", name = "clinics_doctors")
class ClinicDoctor extends AbstractAuditingEntity implements Serializable {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@ShallowReference
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "room", referencedColumnName = "id")
	ClinicRoom room

	@ShallowReference
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "physician", referencedColumnName = "id")
	Physician physician
	
}
