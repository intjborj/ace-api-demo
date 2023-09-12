package com.hisd3.hismk2.domain.billing

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where

import javax.persistence.*

@Entity
@Table(name = "investor_attachments", schema = "billing")
@SQLDelete(sql = "UPDATE billing.investor_attachments SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class InvestorAttachment extends AbstractAuditingEntity implements Serializable {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="investor", referencedColumnName = "id")
	Investor investor

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="dependent", referencedColumnName = "id")
	InvestorDependent dependent
	
	@GraphQLQuery
	@Column(name = "filename", columnDefinition = "varchar")
	String filename
	
	@GraphQLQuery
	@Column(name = "description", columnDefinition = "varchar")
	String description

	@GraphQLQuery
	@Column(name = "title", columnDefinition = "varchar")
	String title

	@GraphQLQuery
	@Column(name = "mimetype")
	String mimetype

	@GraphQLQuery
	@Column(name = "url_path")
	String urlPath

	@GraphQLQuery
	@Column(name = "hide")
	Boolean hide = false

}
