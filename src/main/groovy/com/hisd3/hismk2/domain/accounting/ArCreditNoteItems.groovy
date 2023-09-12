package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.Department
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*

@Entity
@Table(schema = "accounting", name = "ar_credit_note_items")
class ArCreditNoteItems extends AbstractAuditingEntity implements Serializable {

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "credit_note_no")
    String creditNoteNo

    @GraphQLQuery
    @Column(name = "record_no" , unique = true)
    String recordNo

    @GraphQLQuery
    @Column(name = "ar_invoice_item_record_no" )
    String arInvoiceItemRecordNo

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "ar_invoice_item_id", referencedColumnName = "id")
    ArInvoiceItems arInvoiceItem

    @GraphQLQuery
    @Column(name = "ar_invoice_no")
    String arInvoiceNo

    @GraphQLQuery
    @Column(name = "ar_invoice" )
    UUID arInvoiceId

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "ar_credit_note_id", referencedColumnName = "id")
    ArCreditNote arCreditNote

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
    @Column(name = "discount_percentage")
    BigDecimal discountPercentage

    @GraphQLQuery
    @Column(name = "total_cwt_amount")
    BigDecimal cwtAmount

    @GraphQLQuery
    @Column(name = "is_CWT")
    Boolean isCWT

    @GraphQLQuery
    @Column(name = "total_vat_amount")
    BigDecimal vatAmount

    @GraphQLQuery
    @Column(name = "is_vatable")
    Boolean isVatable

    @GraphQLQuery
    @Column(name = "discount_amount")
    BigDecimal discountAmount

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
    @Column(name = "claims_item")
    Boolean claimsItem

    @GraphQLQuery
    @Column(name = "patient_name")
    String patient_name

    @GraphQLQuery
    @Column(name = "approval_code")
    String approval_code

    @GraphQLQuery
    @Column(name = "patient_id")
    UUID patient_id

    @GraphQLQuery
    @Column(name = "pf_name")
    String pf_name

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "recipient_customer", referencedColumnName = "id")
    ArCustomers recipientCustomer

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "discount_department", referencedColumnName = "id")
    Department discountDepartment

    @GraphQLQuery
    @Column(name = "pf_id")
    UUID pf_id

    @GraphQLQuery
    @Column(name = "recipient_invoice")
    UUID recipientInvoice

    @GraphQLQuery
    @Column(name = "reference")
    String reference

    @GraphQLQuery
    @Column(name = "status")
    String status


}
