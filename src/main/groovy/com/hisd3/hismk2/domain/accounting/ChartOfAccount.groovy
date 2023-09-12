package com.hisd3.hismk2.domain.accounting

import com.fasterxml.jackson.annotation.JsonIgnore
import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant

enum AccountType {
	ASSET,
	LIABILITY,
	EQUITY,
	COST_OF_SALE,
	REVENUE,
	EXPENSE
}

enum FinancialStatementType {
	BALANCE_SHEET,
	INCOME
}

enum NormalSide {
	DEBIT,
	CREDIT
}

@Entity
@Table(name = "chart_of_accounts", schema = "accounting")
class ChartOfAccount extends AbstractAuditingEntity implements Serializable {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "account_code", columnDefinition = "varchar")
	@UpperCase
	String accountCode
	
	@GraphQLQuery
	@Column(name = "description", columnDefinition = "varchar")
	@UpperCase
	String description
	
	@GraphQLQuery
	@Formula("concat(account_code,' - ',description)")
	String fullDesc
	
	@GraphQLQuery
	@Column(name = "category", columnDefinition = "bool")
	Boolean category
	
	@GraphQLQuery
	@Column(name = "tags", columnDefinition = "varchar")
	@UpperCase
	String tags
	
	@GraphQLQuery
	@Column(name = "parent", columnDefinition = "uuid")
	UUID parent
	
	@GraphQLQuery
	@Column(name = "deprecated", columnDefinition = "bool")
	Boolean deprecated
	
	@GraphQLQuery
	@Enumerated(EnumType.STRING)
	@Column(name = "account_type", columnDefinition = "varchar")
	AccountType accountType
	
	@GraphQLQuery
	@Enumerated(EnumType.STRING)
	@Column(name = "fs_type", columnDefinition = "varchar")
	FinancialStatementType fsType
	
	@GraphQLQuery
	@Enumerated(EnumType.STRING)
	@Column(name = "normal_side", columnDefinition = "varchar")
	NormalSide normalSide
	
	@GraphQLQuery
	@Column(name = "is_contra", columnDefinition = "bool")
	Boolean isContra
	
	@JsonIgnore
	@Transient
	Instant getDateCreated() {
		return createdDate
	}
	
	@GraphQLQuery
	@Transient
	String accountTrace
}
