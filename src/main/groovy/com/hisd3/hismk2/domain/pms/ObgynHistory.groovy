package com.hisd3.hismk2.domain.pms

import com.fasterxml.jackson.annotation.JsonManagedReference
import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.LocalDate

@Entity
@Table(schema = "pms", name = "obgyn_history")
class ObgynHistory extends AbstractAuditingEntity {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "`case`", referencedColumnName = "id")
	@JsonManagedReference
	Case aCase
	
	@GraphQLQuery
	@Column(name = "gravida", columnDefinition = "numeric")
	Integer gravida
	
	@GraphQLQuery
	@Column(name = "parturition", columnDefinition = "numeric")
	Integer parturition

	@GraphQLQuery
	@Column(name = "term", columnDefinition = "numeric")
	Integer term

	@GraphQLQuery
	@Column(name = "preterm", columnDefinition = "numeric")
	Integer preterm

	@GraphQLQuery
	@Column(name = "abortion", columnDefinition = "numeric")
	Integer abortion
	
	@GraphQLQuery
	@Column(name = "living", columnDefinition = "numeric")
	Integer living

	@GraphQLQuery
	@Column(name = "menarche", columnDefinition = "numeric")
	Integer menarche

	@GraphQLQuery
	@Column(name = "menopause", columnDefinition = "numeric")
	Integer menopause

	@GraphQLQuery
	@Column(name = "due_date", columnDefinition = "date")
	LocalDate dueDate

	@GraphQLQuery
	@Column(name = "age_of_gestation", columnDefinition = "date")
	LocalDate ageOfGestation

	@GraphQLQuery
	@Column(name = "age_of_gestation_2", columnDefinition = "int")
	Integer ageOfGestation2

	@GraphQLQuery
	@Column(name = "last_menstrual_period", columnDefinition = "date")
	LocalDate lastMenstrualPeriod
}
