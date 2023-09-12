package com.hisd3.hismk2.domain.cashiering

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.accounting.AccountReceivableItems
import com.hisd3.hismk2.domain.accounting.Bank
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.types.AutoIntegrateable
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant
@Entity
@Table(name = "cheque_encashment",schema = "cashiering")
class ChequeEncashment extends  AbstractAuditingEntity implements Serializable, AutoIntegrateable{

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid2")
    @Column(name="id",columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terminal",referencedColumnName = "id")
    CashierTerminal terminal

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift",referencedColumnName = "id")
    Shifting shifting

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank",referencedColumnName = "id")
    Bank bank

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "returned_shift_id",referencedColumnName = "id")
    Shifting returnedShifting

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "returned_personnel",referencedColumnName = "id")
    Employee returnedPersonnel

    @GraphQLQuery
    @Column(name = "record_no", columnDefinition = "varchar")
    String recordNo

    @GraphQLQuery
    @Column(name = "cheque_no", columnDefinition = "varchar")
    String chequeNo

    @GraphQLQuery
    @Column(name = "cheque_date", columnDefinition = "date")
    Date chequeDate

    @GraphQLQuery
    @Column(name = "transaction_date", columnDefinition = "timestamp")
    Instant transactionDate

    @GraphQLQuery
    @Column(name = "amount" , columnDefinition = "numeric")
    BigDecimal amount

    @GraphQLQuery
    @Column(name = "remarks", columnDefinition = "varchar")
    String remarks

    @GraphQLQuery
    @Column(name = "returned_date", columnDefinition = "timestamp")
    Instant returnedDate

    @GraphQLQuery
    @Column(name = "returned_remarks", columnDefinition = "varchar")
    String returnRemarks

    @GraphQLQuery
    @Column(name = "cleared", columnDefinition = "boolean")
    Boolean cleared

    @GraphQLQuery
    @Column(name = "denied", columnDefinition = "boolean")
    Boolean denied

    @GraphQLQuery
    @Column(name = "cleared_date", columnDefinition = "timestamp")
    Instant clearedDate

    @GraphQLQuery
    @Column(name = "denied_date", columnDefinition = "timestamp")
    Instant deniedDate

    @GraphQLQuery
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_detail", referencedColumnName = "id")
    CollectionDetail collectionDetail

    @GraphQLQuery
    @Column(name = "posted_ledger", columnDefinition = "uuid")
    UUID postedLedger

    @GraphQLQuery
    @Column(name = "return_posted_ledger", columnDefinition = "uuid")
    UUID returnPostedLedger

    @GraphQLQuery
    @OneToMany(mappedBy="chequeEncashment")
    Set<ChequeEncashmentSupportingDoc> supportingDocs
//    @OrderBy("recordNo")

//
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "chequeEncashment", cascade = [CascadeType.ALL], orphanRemoval = true)
//    List<ChequeEncashmentSupportingDoc> supportingDocs = []

    @Override
    String getDomain() {
        return ChequeEncashment.class.name

    }

    @Override
    Map<String, String> getDetails() {
        return [:]
    }

    @Transient
    String flagValue

    @Transient
    BigDecimal negativeAmount
    BigDecimal getNegativeAmount(){
        def neg = -amount
        return neg
    }

}
