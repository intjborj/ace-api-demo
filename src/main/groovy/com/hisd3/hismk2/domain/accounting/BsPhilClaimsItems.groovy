package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.billing.Billing
import com.hisd3.hismk2.domain.billing.BillingItem
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.pms.Patient
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*

@Entity
@Table(schema = "accounting", name = "bs_phil_claims_items")
class BsPhilClaimsItems extends AbstractAuditingEntity implements Serializable {

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bs_claim_id", referencedColumnName = "id")
    BsPhilClaims bsPhilClaims

    @GraphQLQuery
    @Column(name = "billing_id")
    UUID billing

    @GraphQLQuery
    @Column(name = "patient_id")
    UUID patient

    @GraphQLQuery
    @Column(name = "case_id")
    UUID patientCase

    @GraphQLQuery
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_item_id", referencedColumnName = "id")
    BillingItem billingItem

    @GraphQLQuery
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receivable_id", referencedColumnName = "id")
    AccountReceivable accountReceivable

    @GraphQLQuery
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receivable_item_id", referencedColumnName = "id")
    AccountReceivableItems accountReceivableItems

    @GraphQLQuery
    @Column(name = "type")
    String type

    @GraphQLQuery
    @Column(name = "amount")
    BigDecimal amount

}
