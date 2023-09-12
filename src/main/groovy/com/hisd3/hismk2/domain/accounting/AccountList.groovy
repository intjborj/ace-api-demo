package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*

import javax.persistence.*

@javax.persistence.Entity
@javax.persistence.Table(name = "account_list", schema = "accounting")
@SQLDelete(sql = "UPDATE accounting.account_list SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class AccountList extends AbstractAuditingEntity implements Serializable {
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "parent", referencedColumnName = "id")
	AccountConfig parent
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "debit_account", referencedColumnName = "id")
	ChartOfAccount debitAccount
	
	@GraphQLQuery
	@Column(name = "debit_tag", columnDefinition = "varchar")
	@UpperCase
	String debitTag
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "credit_account", referencedColumnName = "id")
	ChartOfAccount creditAccount
	
	@GraphQLQuery
	@Column(name = "credit_tag", columnDefinition = "varchar")
	@UpperCase
	String creditTag
	
	@GraphQLQuery
	@Column(name = "status", columnDefinition = "int")
	@UpperCase
	Integer status
}

