package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.billing.CompanyAccount
import com.hisd3.hismk2.domain.billing.Discount
import com.hisd3.hismk2.domain.types.AutoIntegrateable
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*

enum AR_CREDIT_NOTE_FLAG  {
    AR_CREDIT_NOTE,
    AR_CREDIT_NOTE_TRANSFER
}

@Entity
@Table(schema = "accounting", name = "ar_credit_note")
class ArCreditNote extends AbstractAuditingEntity implements Serializable, AutoIntegrateable {

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "credit_note_no", unique = true)
    String creditNoteNo

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "ar_customers", referencedColumnName = "id")
    ArCustomers arCustomer

    @GraphQLQuery
    @Column(name = "credit_note_date")
    Date creditNoteDate

    @GraphQLQuery
    @Column(name = "discount_percentage")
    BigDecimal discountPercentage

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
    @Column(name = "credit_note_type")
    String creditNoteType

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
        return ArCreditNote.class.name
    }

    @Override
    Map<String, String> getDetails() {
        return [:]
    }

    @Transient
    String flagValue

    @Transient
    BigDecimal negativeCwtAmount
    BigDecimal getNegativeCwtAmount() {
        return -cwtAmount
    }

    @Transient
    BigDecimal negativeVatAmount
    BigDecimal getNegativeVatAmount() {
        return -vatAmount
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
    CompanyAccount transferredAccount

    @Transient
    BigDecimal totalDiscount , totalTransfer, negativeTotalAmount, totalHCICreditNote, totalPFCreditNote

    @Transient
    Department department

    @Transient
    Discount discount
}
