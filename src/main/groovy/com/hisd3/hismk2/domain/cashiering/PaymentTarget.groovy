package com.hisd3.hismk2.domain.cashiering

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.accounting.ChartOfAccount
import com.hisd3.hismk2.domain.annotations.UpperCase
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*

import javax.persistence.*

@javax.persistence.Entity
@javax.persistence.Table(name = "payment_target", schema = "cashiering")
class PaymentTarget extends AbstractAuditingEntity implements Serializable {
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@Deprecated
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chart_of_account", referencedColumnName = "id")
	ChartOfAccount chartOfAccount
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_tracker", referencedColumnName = "id")
	PaymentTracker paymentTracker
	
	@GraphQLQuery
	@Column(name = "amount", columnDefinition = "numeric")
	@UpperCase
	BigDecimal amount


	@GraphQLQuery
	@UpperCase
	@Column(name = "journal_code", columnDefinition = "varchar")
	String journalCode
	
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(schema = "cashiering", name = "payment_target_details",
			joinColumns = [@JoinColumn(name = "payment_target")])
	@MapKeyColumn(name = "field_name")
	@Column(name = "field_value")
	@BatchSize(size = 20)
	Map<String, String> details = [:]
}
