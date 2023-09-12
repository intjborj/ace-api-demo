package com.hisd3.hismk2.domain.inventory

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*

import javax.persistence.*

@javax.persistence.Entity
@javax.persistence.Table(schema = "inventory", name = "physical_logs_count")
class LogsCount extends AbstractAuditingEntity implements Serializable {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "physical_count", referencedColumnName = "id")
	PhysicalCount physicalCount
	
	@GraphQLQuery
	@Column(name = "log_user")
	String user
	
	@GraphQLQuery
	@Column(name = "log_count")
	Integer count
}
