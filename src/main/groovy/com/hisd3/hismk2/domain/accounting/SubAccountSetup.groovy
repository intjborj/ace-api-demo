package com.hisd3.hismk2.domain.accounting

import groovy.json.JsonSlurper
import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import com.hisd3.hismk2.rest.dto.PrItems
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*

enum SubAccountType {
    INCOME, //  A/R Income Transaction Types
    EXPENSE, // AP Expense Transaction Types
    ADJUSTMENTS, // Debit and Credit Adjustments
    REVENUEITEMS, // Revenue Items
    OTHERPAYMENTS, // Other Payment Types,
    PETTYCASH, // Petty Cash Transaction Types
    QUANTITYADJUSTMENTS, // Quantity Adjustment Types
    ASSETCLASS, // Asset Classification
    OTHERENTITIES, // Other Entities
}

enum JournalPlacement {
    DEBIT,
    CREDIT
}
@Entity
@Table(name = "subaccount_setup", schema = "accounting")
class SubAccountSetup extends AbstractAuditingEntity implements Serializable {
    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "description", columnDefinition = "varchar")
    @UpperCase
    String description

    @GraphQLQuery
    @Column(name = "subaccount_code", columnDefinition = "varchar")
    @UpperCase
    String subaccountCode

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subaccount_parent", referencedColumnName = "id")
    SubAccountSetup subaccountParent

    @GraphQLQuery
    @Enumerated(EnumType.STRING)
    @Column(name = "subaccount_type", columnDefinition = "varchar")
    SubAccountType subaccountType

    @GraphQLQuery
    @Enumerated(EnumType.STRING)
    @Column(name = "journal_placement", columnDefinition = "varchar")
    JournalPlacement journalPlacement


    @OneToMany(fetch = FetchType.EAGER, mappedBy = "subAccount",cascade = [CascadeType.ALL],orphanRemoval = true)
    List<MotherAccount> motherAccounts = []

    @GraphQLQuery
    @Column(name = "include_department", columnDefinition = "bool")
    Boolean includeDepartment

    @GraphQLQuery
    @Column(name = "attr_beginning_balance", columnDefinition = "bool")
    Boolean attrBeginningBalance

    @GraphQLQuery
    @Column(name = "attr_credit_memo_adj", columnDefinition = "bool")
    Boolean attrCreditMemoAdj

    @GraphQLQuery
    @Column(name = "attr_accrual_of_income", columnDefinition = "bool")
    Boolean attrAccrualOfIncome

    @GraphQLQuery
    @Column(name = "attr_non_trade_cash_receipts", columnDefinition = "bool")
    Boolean attrNonTradeCashReceipts

    @GraphQLQuery
    @Column(name = "attr_include_posting_accrued_income_multiple_customer", columnDefinition = "bool")
    Boolean attrIncludePostingAccruedIncomeMultipleCustomer

    @GraphQLQuery
    @Column(name = "attr_vatable", columnDefinition = "bool")
    Boolean attrVatable


    @GraphQLQuery
    @Column(name = "attr_inactive", columnDefinition = "bool")
    Boolean attrInactive

    @GraphQLQuery
    @Column(name = "attr_expense_account", columnDefinition = "bool")
    Boolean attrExpenseAccount

    @GraphQLQuery
    @Column(name = "attr_debit_memo_adjustment", columnDefinition = "bool")
    Boolean attrDebitMemoAdjustment

    @GraphQLQuery
    @Column(name = "attr_accrual_expense", columnDefinition = "bool")
    Boolean attrAccrualExpense

    @GraphQLQuery
    @Column(name = "source_domain", columnDefinition = "varchar")
    String sourceDomain


    @GraphQLQuery
    @Column(name = "category", columnDefinition = "varchar")
    String category

    @GraphQLQuery
    @Column(name = "require_remarks", columnDefinition = "bool")
    Boolean requireRemarks

    @GraphQLQuery
    @Column(name = "attached_value", columnDefinition = "numeric")
    BigDecimal attachedValue

    @GraphQLQuery
    @Column(name = "department_includes", columnDefinition = "varchar")
    String departmentIncludes

    @Transient
    List<UUID> getSelectedDepartments() {
        if(departmentIncludes){
            def list = departmentIncludes.split(',').collect{UUID.fromString(it)}
            return list
        }else {
            return null
        }
    }

}
