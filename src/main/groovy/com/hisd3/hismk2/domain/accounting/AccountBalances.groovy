package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.graphqlservices.accounting.ChartOfAccountGenerate
import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToOne
import javax.persistence.OrderBy
import javax.persistence.Table


@Entity
@Table(name="account_balances",schema = "accounting")
class AccountBalances extends AbstractAuditingEntity implements Serializable{

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name="description",columnDefinition = "varchar")
    String description

    @GraphQLQuery
    @Column(name="beg_month",columnDefinition = "int")
    Integer begMonth

    @GraphQLQuery
    @Column(name="ab_year",columnDefinition = "int")
    Integer abYear

    @GraphQLQuery
    @Column(name="start_date", columnDefinition = "date")
    Date startDate

    @GraphQLQuery
    @Column(name="end_date", columnDefinition = "date")
    Date endDate

    @GraphQLQuery
    @Column(name="ledger_id", columnDefinition = "uuid")
    UUID headerLedgerId

}