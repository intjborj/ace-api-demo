package com.hisd3.hismk2.domain.billing

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.CollectionTable
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MapKeyColumn
import javax.persistence.OneToOne
import javax.persistence.Table
import javax.persistence.Transient
import java.time.Instant

@Deprecated
//@Entity
//@Table(name = "billing_item_cash", schema = "billing")
class BillingItemCash extends AbstractAuditingEntity implements Serializable {

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing", referencedColumnName = "id")
    Billing billing


    @GraphQLQuery
    @Column(name = "description", columnDefinition = "varchar")
    String description



    @GraphQLQuery
    @Column(name = "qty", columnDefinition = "decimal")
    Integer qty

    @Enumerated(EnumType.STRING)
    @GraphQLQuery
    @Column(name = "status", columnDefinition = "varchar")
    BillingItemStatus status


    @GraphQLQuery
    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", columnDefinition = "varchar")
    BillingItemType itemType



    @GraphQLQuery
    @Column(name = "debit", columnDefinition = "numeric")
    BigDecimal debit

    @GraphQLQuery
    @Column(name = "credit", columnDefinition = "numeric")
    BigDecimal credit


    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pricing_tier", referencedColumnName = "id")
    PriceTierDetail priceTierDetail

    @GraphQLQuery
    @Column(name = "transaction_date")
    Instant transactionDate = Instant.now()


    @GraphQLQuery
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(schema = "billing", name = "billing_item_details",
            joinColumns = [@JoinColumn(name = "billingitem")])
    @MapKeyColumn(name = "field_name")
    @Column(name = "field_value")
    @BatchSize(size = 20)
    Map<String, String> details = [:]

    @GraphQLQuery(name = "subtotal")
    @Transient
    BigDecimal getSubTotal() {
        def b = (debit ?: BigDecimal.ZERO) - (credit ?: BigDecimal.ZERO)
        b * qty
    }

    @GraphQLQuery(name = "entrydate")
    @Transient
    Instant getEntryDate() {
        transactionDate
    }

    @GraphQLQuery
    @Transient
    String getLastTouch() {
        lastModifiedBy ?: ("createdBy" ?: "")
    }



    @GraphQLQuery
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(schema = "billing", name = "billingitems_amountdetails",
            joinColumns = [@JoinColumn(name = "billingitem")])
    @MapKeyColumn(name = "billingitemsid")
    @Column(name = "amount")
    @BatchSize(size = 20)
    Map<String, BigDecimal> amountdetails = [:]




    @GraphQLQuery
    @Column(name = "tagged_cash_allowed_by")
    String taggedCashAllowedBy

    @GraphQLQuery
    @Column(name = "tagged_or_number")
    String taggedOrNumber

    @GraphQLQuery
    @Transient
    String orderSlipItemNo


    @GraphQLQuery
    @Column(name = "payment_recomp", columnDefinition = "boolean")
    Boolean paymentRecomp

    @GraphQLQuery
    @Column(name = "pf_payment_recomp", columnDefinition = "boolean")
    Boolean pfPaymentRecomp


    // use in Recomp
    @Transient
    BigDecimal tmpBalance



}
