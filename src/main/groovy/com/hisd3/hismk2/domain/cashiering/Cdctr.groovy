package com.hisd3.hismk2.domain.cashiering

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant

@Entity
@Table(name = "cdctr", schema = "cashiering")
class Cdctr extends AbstractAuditingEntity implements Serializable {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "totalcollection", columnDefinition = "numeric")
	BigDecimal totalcollection
	
	@GraphQLQuery
	@Column(name = "receivedby", columnDefinition = "varchar")
	@UpperCase
	String receivedby
	
	@GraphQLQuery
	@Column(name = "received_datetime", columnDefinition = "timestamp")
	@UpperCase
	Instant received_datetime

	@GraphQLQuery
	@Column(name = "voided_by", columnDefinition = "varchar")
	@UpperCase
	String voidedBy

	@GraphQLQuery
	@Column(name = "voided_datetime", columnDefinition = "timestamp")
	@UpperCase
	Instant voidedDatetime
	
	@GraphQLQuery
	@Column(name = "recno", columnDefinition = "varchar")
	@UpperCase
	String recno

	@GraphQLQuery
	@Column(name = "status", columnDefinition = "varchar")
	@UpperCase
	String status = "ACTIVE";
	
	@OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], mappedBy = "cdctr")
	List<Shifting> shiftings = []


	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "collection", referencedColumnName = "id")
	CollectionDeposit collection

	
}
