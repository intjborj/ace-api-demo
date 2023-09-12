package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*

@Entity
@Table(schema = "accounting", name = "ar_invoice_items")
class ArInvoiceItems extends AbstractAuditingEntity implements Serializable {

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "invoice_no")
    String invoiceNo

    @GraphQLQuery
    @Column(name = "record_no" , unique = true)
    String recordNo

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "ar_invoice_id", referencedColumnName = "id")
    ArInvoice arInvoice

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "ar_customers", referencedColumnName = "id")
    ArCustomers arCustomer

    @GraphQLQuery
    @Column(name = "item_name")
    String itemName

    @GraphQLQuery
    @Column(name = "description")
    String description

    @GraphQLQuery
    @Column(name = "item_type")
    String itemType

    @GraphQLQuery
    @Column(name = "unit_price")
    BigDecimal unitPrice

    @GraphQLQuery
    @Column(name = "quantity")
    Integer quantity

    @GraphQLQuery
    @Column(name = "discount")
    BigDecimal discount

    @GraphQLQuery
    @Column(name = "discount_amount")
    BigDecimal discountAmount

    @GraphQLQuery
    @Column(name = "cwt_amount")
    BigDecimal cwtAmount

    @GraphQLQuery
    @Column(name = "is_CWT")
    Boolean isCWT

    @GraphQLQuery
    @Column(name = "vat_amount")
    BigDecimal vatAmount

    @GraphQLQuery
    @Column(name = "is_vatable")
    Boolean isVatable

    @GraphQLQuery
    @Column(name = "total_hci_amount")
    BigDecimal totalHCIAmount

    @GraphQLQuery
    @Column(name = "credit_note")
    BigDecimal creditNote

    @GraphQLQuery
    @Column(name = "payment")
    BigDecimal payment

    @GraphQLQuery
    @Column(name = "total_pf_amount")
    BigDecimal totalPFAmount

    @GraphQLQuery
    @Column(name = "total_amount_due")
    BigDecimal totalAmountDue

    @GraphQLQuery
    @Column(name = "claims_item")
    Boolean claimsItem

    @GraphQLQuery
    @Column(name = "patient_name")
    String patient_name

    @GraphQLQuery
    @Column(name = "billing_no")
    String billing_no

    @GraphQLQuery
    @Column(name = "soa_no")
    String soa_no

    @GraphQLQuery
    @Column(name = "approval_code")
    String approval_code

    @GraphQLQuery
    @Column(name = "admission_date")
    Date admission_date

    @GraphQLQuery
    @Column(name = "discharge_date")
    Date discharge_date

    @GraphQLQuery
    @Column(name = "registry_type")
    String registry_type

    @GraphQLQuery
    @Column(name = "billing_item_id")
    UUID billing_item_id

    @GraphQLQuery
    @Column(name = "billing_id")
    UUID billing_id

    @GraphQLQuery
    @Column(name = "patient_id")
    UUID patient_id

    @GraphQLQuery
    @Column(name = "case_id")
    UUID case_id

    @GraphQLQuery
    @Column(name = "pf_name")
    String pf_name

    @GraphQLQuery
    @Column(name = "pf_id")
    UUID pf_id

    @GraphQLQuery
    @Column(name = "status")
    String status

    @GraphQLQuery
    @Column(name = "reference_transfer_id")
    UUID reference_transfer_id

    @Transient
    BigDecimal netTotalAmount
    BigDecimal getNetTotalAmount() {
        BigDecimal credit =  creditNote?:0.00
        BigDecimal payments = payment?:0.00
        BigDecimal totalCredits  = payments + credit
        BigDecimal total = totalAmountDue?:0.00
        BigDecimal netTotal =  total - totalCredits
        return  netTotal
    }


    @Transient
    BigDecimal discountPercentage
}
