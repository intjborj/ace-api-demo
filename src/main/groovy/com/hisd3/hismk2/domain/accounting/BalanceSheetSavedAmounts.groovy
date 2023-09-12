package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.billing.CompanyAccount
import com.hisd3.hismk2.domain.types.AutoIntegrateable
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

//import org.joda.time.DateTime

import javax.persistence.*
import java.sql.Date

@Entity
@Table(schema = "accounting", name = "balance_sheet_saved_amounts")
class BalanceSheetSavedAmounts extends AbstractAuditingEntity implements Serializable{

	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@GraphQLQuery
	@Column(name = "year")
	String year

	@GraphQLQuery
	@Column(name = "retained_earnings")
	BigDecimal retained_earnings

	@GraphQLQuery
	@Column(name = "net_profit")
	BigDecimal netProfit

}
