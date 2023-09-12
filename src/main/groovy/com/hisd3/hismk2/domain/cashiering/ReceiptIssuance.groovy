package com.hisd3.hismk2.domain.cashiering

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*

@Entity
@Table(name = "receiptsissuance", schema = "cashiering")
class ReceiptIssuance extends AbstractAuditingEntity implements Serializable {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "batchcode", columnDefinition = "varchar")
	String batchcode
	
	@GraphQLQuery
	@Column(name = "receipt_from", columnDefinition = "int8")
	Long receiptFrom
	
	@GraphQLQuery
	@Column(name = "receipt_to", columnDefinition = "int8")
	Long receiptTo
	
	@GraphQLQuery
	@Column(name = "arfrom", columnDefinition = "int8")
	Long arFrom
	
	@GraphQLQuery
	@Column(name = "arto", columnDefinition = "int8")
	Long arTo
	
	@GraphQLQuery
	@Column(name = "receipt_current", columnDefinition = "int8")
	Long receiptCurrent
	
	@GraphQLQuery
	@Column(name = "arcurrent", columnDefinition = "int8")
	Long arCurrent
	
	@GraphQLQuery
	@Column(name = "activebatch", columnDefinition = "bool")
	Boolean activebatch // OR Active
	
	@GraphQLQuery
	@Column(name = "aractive", columnDefinition = "bool")
	Boolean aractive // AR Active
	
	@GraphQLQuery
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "terminal", referencedColumnName = "id")
	CashierTerminal terminal
	
}
