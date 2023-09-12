package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*

import javax.persistence.*

@javax.persistence.Entity
@javax.persistence.Table(name = "account_config", schema = "accounting")
class AccountConfig extends AbstractAuditingEntity implements Serializable {
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "trans_type", referencedColumnName = "id")
	TransactionType transactionType
	
	@GraphQLQuery
	@Column(name = "doc_type", columnDefinition = "varchar")
	@UpperCase
	String docType
	
	@GraphQLQuery
	@Column(name = "book", columnDefinition = "varchar")
	@UpperCase
	String book
	
	@NotFound(action = NotFoundAction.IGNORE)
	@GraphQLQuery
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "parent")
	List<AccountList> accountLists
	
}

