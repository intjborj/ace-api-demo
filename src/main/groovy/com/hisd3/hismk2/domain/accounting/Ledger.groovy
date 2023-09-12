package com.hisd3.hismk2.domain.accounting


/*
https://github.com/vladmihalcea/hibernate-types
https://vladmihalcea.com/how-to-map-json-objects-using-generic-hibernate-types/
https://vladmihalcea.com/how-to-store-schema-less-eav-entity-attribute-value-data-using-json-and-hibernate/
https://www.postgresql.org/docs/9.4/datatype-json.html
https://www.postgresqltutorial.com/postgresql-json/
 */
import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import com.hisd3.hismk2.graphqlservices.accounting.ChartOfAccountGenerate
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*

import javax.persistence.*

@javax.persistence.Entity
@javax.persistence.Table(name = "ledger", schema = "accounting")
class Ledger extends AbstractAuditingEntity implements Serializable {
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "header", referencedColumnName = "id")
	HeaderLedger header

	@GraphQLQuery
	@Type(type = "jsonb")
	@Column(name="journal_account",columnDefinition = "jsonb")
	ChartOfAccountGenerate journalAccount


	@GraphQLQuery
	@Column(name = "particulars", columnDefinition = "varchar")
	@UpperCase
	String particulars
	
	@GraphQLQuery
	@Column(name = "debit", columnDefinition = "numeric")
	@UpperCase
	BigDecimal debit
	
	@GraphQLQuery
	@Column(name = "credit", columnDefinition = "numeric")
	@UpperCase
	BigDecimal credit

	/* Removed transfer to header
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(schema = "accounting", name = "ledger_details",
			joinColumns = [@JoinColumn(name = "general_ledger")])
	@MapKeyColumn(name = "field_name")
	@Column(name = "field_value")
	@BatchSize(size = 20)
	Map<String, String> details = [:]*/

    @Transient
	BigDecimal totalAppliedOr

}

