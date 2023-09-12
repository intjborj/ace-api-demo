package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import com.hisd3.hismk2.domain.inventory.Supplier
import com.hisd3.hismk2.domain.types.AutoIntegrateable
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where

import javax.persistence.*
import java.time.Instant

@javax.persistence.Entity
@javax.persistence.Table(name = "wtx_2307", schema = "accounting")
@SQLDelete(sql = "UPDATE accounting.wtx_2307 SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class Wtx2307 extends AbstractAuditingEntity implements Serializable {

	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@GraphQLQuery
	@Column(name = "ref_id", columnDefinition = "varchar")
	UUID refId

	@GraphQLQuery
	@Column(name = "ref_no", columnDefinition = "varchar")
	String refNo

	@GraphQLQuery
	@Column(name = "wtx_date", columnDefinition = "date")
	Instant wtxDate
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "supplier", referencedColumnName = "id")
	Supplier supplier

	@GraphQLQuery
	@Column(name = "type", columnDefinition = "varchar")
	String type

	@GraphQLQuery
	@Column(name = "gross", columnDefinition = "numeric")
	BigDecimal gross

	@GraphQLQuery
	@Column(name = "vat_amount", columnDefinition = "numeric")
	BigDecimal vatAmount

	@GraphQLQuery
	@Column(name = "net_vat", columnDefinition = "numeric")
	BigDecimal netVat

	@GraphQLQuery
	@Column(name = "ewt_amount", columnDefinition = "numeric")
	BigDecimal ewtAmount

	@GraphQLQuery
	@Column(name = "process", columnDefinition = "bool")
	Boolean process

	@GraphQLQuery
	@Column(name = "wtx_consolidated", columnDefinition = "uuid")
	UUID wtxConsolidated

	
}

