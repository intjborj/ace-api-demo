package com.hisd3.hismk2.domain.billing


import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where

import javax.persistence.*
import java.time.Instant

enum DependentType {
	CHILD,
	SPOUSE,
	FATHER,
	MOTHER
}

@Entity
@Table(name = "investors_dependents", schema = "billing")
@SQLDelete(sql = "UPDATE billing.investors_dependents SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class InvestorDependent extends AbstractAuditingEntity implements Serializable{
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
//
//	@JsonIgnore
//	@OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "dependent")
//	List<InvestorAttachment> attachments = [] as List

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "investor_data", referencedColumnName = "id")
	Investor investor
	
	@GraphQLQuery
	@Column(name = "investorid", columnDefinition = "varchar")
	String investorId
	
	@GraphQLQuery
	@Column(name = "firstname", columnDefinition = "varchar")
	String firstname
	
	@GraphQLQuery
	@Column(name = "middlename", columnDefinition = "varchar")
	String middlename
	
	@GraphQLQuery
	@Column(name = "lastname", columnDefinition = "varchar")
	String lastname

	@GraphQLQuery
	@Column(name = "suffix", columnDefinition = "varchar")
	String suffix

	@GraphQLQuery
	@Formula("concat(lastname , coalesce(', ' || nullif(firstname,'') , ''), coalesce(' ' || nullif(middlename,'') , ''))")
	String fullName

	@GraphQLQuery
	@Column(name = "relation_investor", columnDefinition = "varchar")
	String relationToInvestor
	
	@GraphQLQuery
	@Column(name = "dob", columnDefinition = "timestamp")
	Instant dob
	
	@GraphQLQuery
	@Enumerated(value = EnumType.STRING)
	@Column(name = "type", columnDefinition = "varchar")
	DependentType type

	@GraphQLQuery
	@Column(name = "use_investor_id")
	Boolean useInvestorId
}
