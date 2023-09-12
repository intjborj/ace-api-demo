package com.hisd3.hismk2.domain.inventory

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.Department
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*

import javax.persistence.*
import java.time.Instant

@javax.persistence.Entity
@javax.persistence.Table(schema = "inventory", name = "signature_table")
@SQLDelete(sql = "UPDATE inventory.signature_table SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class Signature extends AbstractAuditingEntity implements Serializable {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "department_id", referencedColumnName = "id")
	Department department

	@GraphQLQuery
	@Column(name = "signature_type", columnDefinition = 'varchar')
	String signatureType

	@GraphQLQuery
	@Column(name = "signature_header", columnDefinition = 'varchar')
	String signatureHeader

	@GraphQLQuery
	@Column(name = "signature_person", columnDefinition = 'varchar')
	String signaturePerson

	@GraphQLQuery
	@Column(name = "signature_position", columnDefinition = 'varchar')
	String signaturePosition

	@GraphQLQuery
	@Column(name = "is_current_user", columnDefinition = 'bool')
	Boolean currentUsers

	@GraphQLQuery
	@Column(name = "sequence", columnDefinition = 'int')
	Integer sequence

}
