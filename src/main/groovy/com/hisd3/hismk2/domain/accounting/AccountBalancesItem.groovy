package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.annotations.UpperCase
import com.hisd3.hismk2.graphqlservices.accounting.ChartOfAccountGenerate
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
import javax.persistence.Table
import javax.persistence.Transient

@Entity
@Table(name="account_balances_item",schema = "accounting")
class AccountBalancesItem implements Serializable{

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_balances_id", referencedColumnName = "id")
    AccountBalances accountBalances

    @GraphQLQuery
    @Type(type = "jsonb")
    @Column(name="journal_account",columnDefinition = "jsonb")
    ChartOfAccountGenerate journalAccount

    @GraphQLQuery
    @Column(name = "ledger_id", columnDefinition = "uuid")
    UUID ledgerId


    @GraphQLQuery
    @Column(name = "header", columnDefinition = "uuid")
    UUID headerId

    @GraphQLQuery
    @Column(name = "debit", columnDefinition = "numeric")
    @UpperCase
    BigDecimal debit

    @GraphQLQuery
    @Column(name = "credit", columnDefinition = "numeric")
    @UpperCase
    BigDecimal credit

}
