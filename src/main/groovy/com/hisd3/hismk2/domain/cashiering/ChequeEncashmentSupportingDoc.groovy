package com.hisd3.hismk2.domain.cashiering

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import com.hisd3.hismk2.services.AES
import groovy.json.JsonOutput
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*
import java.beans.Transient

@Entity
@Table(name = "cheque_encashment_suppfiles", schema = "cashiering")
class ChequeEncashmentSupportingDoc extends AbstractAuditingEntity implements Serializable {

	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cheque_encashment", referencedColumnName = "id")
    ChequeEncashment chequeEncashment
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "reference_no", columnDefinition = "varchar")
	String referenceNo
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "filename", columnDefinition = "varchar")
	String filename
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "description", columnDefinition = "varchar")
	String description
	
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "attachment", columnDefinition = "bytea")
	Byte[] attachment = []
	
	@Transient
	String getAttachmentUrl() {
		if (id) {
			def json = JsonOutput.toJson(["class": ChequeEncashmentSupportingDoc.name, "id": id.toString(), "columnName": "attachment", "filename": filename])

			URLEncoder.encode(AES.encrypt(json), "UTF-8")
		} else {
			null
		}

	}
}
