package com.hisd3.hismk2.domain.inventory

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant
import java.time.LocalDateTime

@Entity
@Table(schema = "inventory", name = "po_items_with_monitoring")
class PoItemMonitoring extends AbstractAuditingEntity implements Serializable{

    @GraphQLQuery
    @Id
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item", referencedColumnName = "id")
    Item item

    @GraphQLQuery
    @Column(name = "quantity", columnDefinition = "numeric")
    Integer quantity

    @GraphQLQuery
    @Column(name = "qty_in_small", columnDefinition = "int")
    Integer qtyInSmall

    @GraphQLQuery
    @Column(name = "pr_nos", columnDefinition = "varchar")
    String prNos

    @GraphQLQuery
    @Column(name = "desc_long", columnDefinition = "varchar")
    String descLong

    @GraphQLQuery
    @Column(name = "po_number", columnDefinition = "varchar")
    String poNumber

    @GraphQLQuery
    @Column(name = "supplier_fullname", columnDefinition = "varchar")
    String supplierFullName

    @GraphQLQuery
    @Column(name = "supplier", columnDefinition = "varchar")
    UUID supplier

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order", referencedColumnName = "id")
    PurchaseOrder purchaseOrder

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiving_report", referencedColumnName = "id")
    ReceivingReport receivingReport

    @GraphQLQuery
    @Column(name = "supplier_last_price", columnDefinition = "numeric")
    BigDecimal supplierLastPrice

    @GraphQLQuery
    @Column(name = "delivery_balance", columnDefinition = "int")
    Integer delBalance

    @GraphQLQuery
    @Column(name = "delivered_qty", columnDefinition = "int")
    Integer delQty

    @GraphQLQuery
    @Column(name = "delivery_status", columnDefinition = "int")
    Integer deliveryStatus //-- 0: for delivery 1: partial delivery 2: completed

    @GraphQLQuery
    @Column(name = "type", columnDefinition = "varchar")
    String type

    @GraphQLQuery
    @Column(name = "type_text", columnDefinition = "varchar")
    String type_text

    @Transient
    String getUnitMeasurement() {
        return "${item.unit_of_purchase?.unitDescription} (${item.item_conversion} ${item.unit_of_usage?.unitDescription})"
    }

}
