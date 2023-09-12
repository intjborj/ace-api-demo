package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import com.hisd3.hismk2.graphqlservices.accounting.ChartOfAccountGenerate
import com.hisd3.hismk2.graphqlservices.accounting.CoaComponentContainer
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.Basic
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name="saved_reports", schema = "accounting")
class SavedReports extends AbstractAuditingEntity implements Serializable {

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name="report_no",columnDefinition = "varchar")
    String reportNo

    @GraphQLQuery
    @Column(name="start_date",columnDefinition = "date")
    Date startDate

    @GraphQLQuery
    @Column(name="end_date",columnDefinition = "date")
    Date endDate

    @UpperCase
    @GraphQLQuery
    @Column(name="report_type",columnDefinition = "varchar")
    String reportType

    @GraphQLQuery
    @Column(name="reference",columnDefinition = "varchar")
    String reference

    @GraphQLQuery
    @Column(name="description",columnDefinition = "varchar")
    String description

    @UpperCase
    @GraphQLQuery
    @Column(name="group_type",columnDefinition = "varchar")
    String groupType

    @GraphQLQuery
    @Type(type = "jsonb")
    @Column(name="journal_accounts",columnDefinition = "jsonb")
    List<ChartOfAccountGenerate> journalAccounts

    @GraphQLQuery
    @Column(name="amount",columnDefinition = "numeric")
    BigDecimal amount

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "json_file", columnDefinition = "bytea")
    Byte[] jsonFile = []
}
