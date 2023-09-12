package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.inventory.Supplier
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*

import javax.persistence.*
import java.time.Instant

@javax.persistence.Entity
@javax.persistence.Table(name = "wtx_consolidated", schema = "accounting")
@SQLDelete(sql = "UPDATE accounting.wtx_consolidated SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class Wtx2307Consolidated extends AbstractAuditingEntity implements Serializable {

	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@GraphQLQuery
	@Column(name = "ref_no", columnDefinition = "varchar")
	String refNo

	@GraphQLQuery
	@Column(name = "date_from", columnDefinition = "date")
	Instant dateFrom

	@GraphQLQuery
	@Column(name = "date_to", columnDefinition = "date")
	Instant dateTo
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "supplier", referencedColumnName = "id")
	Supplier supplier

	@GraphQLQuery
	@Column(name = "remarks", columnDefinition = "varchar")
	String remarks

}

