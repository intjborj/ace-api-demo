package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "fiscals", schema = "accounting")
class Fiscal extends AbstractAuditingEntity implements Serializable {

	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@GraphQLQuery
	@Column(name = "fiscal_id", columnDefinition = "varchar")
	@UpperCase
	String fiscalId

	@GraphQLQuery
	@Column(name = "from_date", columnDefinition = "date")
	@UpperCase
	LocalDate fromDate

	@GraphQLQuery
	@Column(name = "to_date", columnDefinition = "date")
	@UpperCase
	LocalDate toDate

	@GraphQLQuery
	@Column(name = "remarks", columnDefinition = "varchar")
	@UpperCase
	String remarks

	@GraphQLQuery
	@Column(name = "active", columnDefinition = "boolean")
	@UpperCase
	Boolean active

	@GraphQLQuery
	@Column(name = "lock_january", columnDefinition = "boolean")
	Boolean lockJanuary

	@GraphQLQuery
	@Column(name = "lock_february", columnDefinition = "boolean")
	Boolean lockFebruary

	@GraphQLQuery
	@Column(name = "lock_march", columnDefinition = "boolean")
	Boolean lockMarch


	@GraphQLQuery
	@Column(name = "lock_april", columnDefinition = "boolean")
	Boolean lockApril


	@GraphQLQuery
	@Column(name = "lock_may", columnDefinition = "boolean")
	Boolean lockMay

	@GraphQLQuery
	@Column(name = "lock_june", columnDefinition = "boolean")
	Boolean lockJune


	@GraphQLQuery
	@Column(name = "lock_july", columnDefinition = "boolean")
	Boolean lockJuly

	@GraphQLQuery
	@Column(name = "lock_august", columnDefinition = "boolean")
	Boolean lockAugust

	@GraphQLQuery
	@Column(name = "lock_september", columnDefinition = "boolean")
	Boolean lockSeptember

	@GraphQLQuery
	@Column(name = "lock_october", columnDefinition = "boolean")
	Boolean lockOctober

	@GraphQLQuery
	@Column(name = "lock_november", columnDefinition = "boolean")
	Boolean lockNovember

	@GraphQLQuery
	@Column(name = "lock_december", columnDefinition = "boolean")
	Boolean lockDecember
}
