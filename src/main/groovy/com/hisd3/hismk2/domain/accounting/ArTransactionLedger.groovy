package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.billing.CompanyAccount
import com.hisd3.hismk2.domain.types.AutoIntegrateable
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant


@Entity
@Table(schema = "accounting", name = "ar_transaction_ledger")
class ArTransactionLedger extends AbstractAuditingEntity implements Serializable {

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "record_no", unique = true)
    String recordNo

    @GraphQLQuery
    @Column(name = "doc_no", unique = true)
    String docNo

    @GraphQLQuery
    @Column(name = "doc_type")
    String docType

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "ar_customers", referencedColumnName = "id")
    ArCustomers arCustomer

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "ar_invoice_id", referencedColumnName = "id")
    ArInvoice arInvoice

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "ar_credit_note_id", referencedColumnName = "id")
    ArCreditNote arCreditNote

    @GraphQLQuery
    @Column(name = "ar_payment_id")
    UUID arPaymentId

    @GraphQLQuery
    @Column(name = "ledger_date")
    Instant ledgerDate

    @GraphQLQuery
    @Column(name = "doc_date")
    Date docDate

    @GraphQLQuery
    @Column(name = "total_cwt_amount")
    BigDecimal totalCwtAmount

    @GraphQLQuery
    @Column(name = "total_vat_amount")
    BigDecimal totalVatAmount

    @GraphQLQuery
    @Column(name = "total_hci_amount")
    BigDecimal totalHCIAmount

    @GraphQLQuery
    @Column(name = "total_pf_amount")
    BigDecimal totalPFAmount

    @GraphQLQuery
    @Column(name = "total_amount_due")
    BigDecimal totalAmountDue

    @GraphQLQuery
    @Column(name = "remaining_hci_balance")
    BigDecimal remainingHCIBalance

    @GraphQLQuery
    @Column(name = "remaining_pf_balance")
    BigDecimal remainingPFBalance

    @GraphQLQuery
    @Column(name = "remaining_balance")
    BigDecimal remainingBalance

}
