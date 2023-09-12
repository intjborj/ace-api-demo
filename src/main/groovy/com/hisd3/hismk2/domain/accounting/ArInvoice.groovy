package com.hisd3.hismk2.domain.accounting;


import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.billing.CompanyAccount
import com.hisd3.hismk2.domain.types.AutoIntegrateable;
import com.hisd3.hismk2.domain.types.Subaccountable;
import com.hisd3.hismk2.rest.dto.CoaConfig;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

enum AR_INVOICE_FLAG  {
    AR_CLAIMS_INVOICE,
    AR_PERSONAL_INVOICE
}

@Entity
@Table(schema = "accounting", name = "ar_invoice")
class ArInvoice extends AbstractAuditingEntity implements Serializable, AutoIntegrateable {

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "invoice_no", unique = true)
    String invoiceNo

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "ar_customers", referencedColumnName = "id")
    ArCustomers arCustomer

    @GraphQLQuery
    @Column(name = "due_date")
    Date dueDate

    @GraphQLQuery
    @Column(name = "invoice_date")
    Date invoiceDate

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
    @Column(name = "total_pf_amount")
    BigDecimal totalPFAmount

    @GraphQLQuery
    @Column(name = "total_amount_due")
    BigDecimal totalAmountDue

    @GraphQLQuery
    @Column(name = "total_credit_note")
    BigDecimal totalCreditNote

    @GraphQLQuery
    @Column(name = "total_payments")
    BigDecimal totalPayments

    @GraphQLQuery
    @Column(name = "invoice_type")
    String invoiceType

    @GraphQLQuery
    @Column(name = "reference")
    String reference

    @GraphQLQuery
    @Column(name = "notes")
    String notes

    @GraphQLQuery
    @Column(name = "status")
    String status

    @GraphQLQuery
    @Column(name = "ledger_id")
    UUID ledgerId



    @Override
    String getDomain() {
        return ArInvoice.class.name
    }

    @Override
    Map<String, String> getDetails() {
        return [:]
    }

    @Transient
    String flagValue

    @Transient
    BigDecimal netTotalAmount
    BigDecimal getNetTotalAmount() {
        BigDecimal credit =  totalCreditNote?:0.00
        BigDecimal payments = totalPayments?:0.00
        BigDecimal totalCredits  = payments + credit
        BigDecimal total = totalAmountDue?:0.00
        BigDecimal netTotal =  total - totalCredits
        return  netTotal
    }

    @Transient
    BigDecimal negativeTotalHCIAmount
    BigDecimal getNegativeTotalHCIAmount() {
        return -totalHCIAmount
    }

    @Transient
    BigDecimal negativeTotalPFAmount
    BigDecimal getNegativeTotalPFAmount() {
        return -totalPFAmount
    }

    @Transient
    BigDecimal negativeTotalAmountDue
    BigDecimal getNegativeTotalAmountDue() {
        return -totalAmountDue
    }

    @Transient
    BigDecimal negativeCWTAmount
    BigDecimal getNegativeCWTAmount() {
        return -cwtAmount
    }


    @Transient
    CompanyAccount companyAccount

    @Transient
    BigDecimal electricity, rental, others, unitPrice, quantity, totalAmount, negativeTotalAmount, totalHCIVat, totalHCITax
}
