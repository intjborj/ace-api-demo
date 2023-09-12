package com.hisd3.hismk2.domain.cashiering

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*

import javax.persistence.*
import java.time.Instant

@javax.persistence.Entity
@javax.persistence.Table(name = "shifting", schema = "cashiering")
class Shifting extends AbstractAuditingEntity implements Serializable {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "shiftno", columnDefinition = "varchar")
	String shiftno
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cdctr", referencedColumnName = "id")
	Cdctr cdctr
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cashier", referencedColumnName = "id")
	CashierTerminal terminal
	
	@GraphQLQuery
	@Column(name = "active", columnDefinition = "boolean")
	Boolean active
	
	@GraphQLQuery
	@Column(name = "startshift", columnDefinition = "timestamp")
	Instant startshift
	
	@GraphQLQuery
	@Column(name = "endshift", columnDefinition = "timestamp")
	Instant endshift
	
	@GraphQLQuery
	@Column(name = "acknowledgedate", columnDefinition = "timestamp")
	Instant acknowledgedate
	
	@GraphQLQuery
	@Column(name = "acknowledgeby", columnDefinition = "varchar")
	String acknowledgeby
	
	@GraphQLQuery
	@Column(name = "acknowledged", columnDefinition = "boolean")
	Boolean acknowledged
	
	@GraphQLQuery
	@Column(name = "overage_shortage", columnDefinition = "numeric")
	BigDecimal overageShortage
	
	@GraphQLQuery
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(schema = "cashiering", name = "shifting_details",
			joinColumns = [@JoinColumn(name = "shifting")])
	@MapKeyColumn(name = "field_name")
	@Column(name = "field_value")
	@BatchSize(size = 20)
	Map<String, String> details = [:]
	
	@GraphQLQuery
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "shift")
	@javax.persistence.OrderBy("ornumber")
	List<PaymentTracker> payments = []
	
}





