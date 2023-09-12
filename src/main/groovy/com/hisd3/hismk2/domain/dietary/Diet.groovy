package com.hisd3.hismk2.domain.dietary

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.types.JaversResolvable
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*

@Entity
@Table(name = "diets", schema = "dietary")
class Diet extends AbstractAuditingEntity implements JaversResolvable {
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@Column(name = "diet_name", columnDefinition = "varchar")
	String dietName
	
	@Column(name = "diet_description", columnDefinition = "varchar")
	String dietDescription
	
	@Column(name = "color", columnDefinition = "varchar")
	String dietColor

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dietary_category", referencedColumnName = "id")
	DietCategory dietCategory

	@Override
	String resolveEntityForJavers() {
		return dietName
	}
}
