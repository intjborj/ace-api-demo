package com.hisd3.hismk2.domain.referential

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.sun.org.apache.xpath.internal.operations.Bool
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*

@Entity
@Table(schema = "referential", name = "doh_positions")
class DohPosition extends AbstractAuditingEntity {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "poscode", columnDefinition = "numeric")
	Integer poscode
	
	@GraphQLQuery
	@Column(name = "postdesc", columnDefinition = "varchar")
	String postdesc
	
	@GraphQLQuery
	@Column(name = "postype", columnDefinition = "numeric")
	Integer postype
	
	@GraphQLQuery
	@Column(name = "poscode_parent", columnDefinition = "numeric")
	Integer poscodeParent

	@GraphQLQuery
	@Column(name = "is_others", columnDefinition = "boolean")
	Boolean isOthers

	@GraphQLQuery
	@Column(name = "is_active", columnDefinition = "bool")
	Boolean isActive

}
