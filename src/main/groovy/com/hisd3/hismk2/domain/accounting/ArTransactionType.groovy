package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.billing.CompanyAccount
import com.hisd3.hismk2.domain.cashiering.PaymentTracker
import com.sun.org.apache.xpath.internal.operations.Bool
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type
import org.springframework.data.annotation.CreatedDate

import javax.persistence.*
import java.time.Instant

@Entity
@Table(schema = "accounting", name = "ar_transaction_type")
class ArTransactionType extends AbstractAuditingEntity implements Serializable{

	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@GraphQLQuery
	@Column(name = "description")
	String description

	@ManyToOne(fetch = FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "integration", referencedColumnName = "id")
	Integration integration

	@GraphQLQuery
	@Column(name = "active")
	Boolean active



}
