package com.hisd3.hismk2.domain.inventory

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.annotations.UpperCase
import com.hisd3.hismk2.domain.hrm.Employee
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where

import javax.persistence.*
import java.time.Instant

@Entity
@Table(schema = "inventory", name = "beginning_transactions")
@SQLDelete(sql = "UPDATE inventory.beginning_transactions SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class BeginningTransaction extends AbstractAuditingEntity implements Serializable {

	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@GraphQLQuery
	@Column(name = "trans_no")
	String transNo

	@GraphQLQuery
	@Column(name = "trans_date")
	Instant transDate

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "department", referencedColumnName = "id")
	Department department

	@GraphQLQuery
	@Column(name = "remarks")
	String remarksNotes

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trans_by", referencedColumnName = "id")
	Employee transBy

	@GraphQLQuery
	@Column(name = "status")
	@UpperCase
	String status

	@GraphQLQuery
	@Column(name = "acct_type", columnDefinition = 'uuid')
	UUID accType

	@GraphQLQuery
	@Column(name = "posted", columnDefinition = "bool")
	@UpperCase
	Boolean posted

	@GraphQLQuery
	@Column(name = "posted_by", columnDefinition = "varchar")
	@UpperCase
	String postedBy

	@GraphQLQuery
	@Column(name = "posted_ledger")
	UUID postedLedger
}
