package com.hisd3.hismk2.domain.inventory

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*

import javax.persistence.*
import java.time.Instant

@javax.persistence.Entity
@javax.persistence.Table(schema = "inventory", name = "receiving_report_items")
@SQLDelete(sql = "UPDATE inventory.receiving_report_items SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class ReceivingReportItem extends AbstractAuditingEntity {
	
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
	@JoinColumn(name = "receiving_report", referencedColumnName = "id")
	ReceivingReport receivingReport
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item", referencedColumnName = "id")
	Item item
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ref_poitem", referencedColumnName = "id")
	PurchaseOrderItems refPoItem
	
	@GraphQLQuery
	@Column(name = "rec_qty", columnDefinition = 'int')
	Integer receiveQty
	
	@GraphQLQuery
	@Column(name = "rec_unit_cost", columnDefinition = 'numeric')
	BigDecimal receiveUnitCost

	@GraphQLQuery
	@Column(name = "rec_inventory_cost", columnDefinition = 'numeric')
	BigDecimal recInventoryCost
	
	@GraphQLQuery
	@Column(name = "rec_disc_cost", columnDefinition = 'numeric')
	BigDecimal receiveDiscountCost
	
	@GraphQLQuery
	@Column(name = "expiration_date", columnDefinition = 'date')
	Instant expirationDate

	@GraphQLQuery
	@Column(name = "lot_no", columnDefinition = 'date')
	String lotNo
	
	@GraphQLQuery
	@Column(name = "total_amount", columnDefinition = 'numeric')
	BigDecimal totalAmount
	
	@GraphQLQuery
	@Column(name = "input_tax", columnDefinition = 'numeric')
	BigDecimal inputTax
	
	@GraphQLQuery
	@Column(name = "net_amount", columnDefinition = 'numeric')
	BigDecimal netAmount
	
	@GraphQLQuery
	@Column(name = "is_tax", columnDefinition = 'bool')
	Boolean isTax
	
	@GraphQLQuery
	@Column(name = "is_fg", columnDefinition = 'bool')
	Boolean isFg
	
	@GraphQLQuery
	@Column(name = "is_discount", columnDefinition = 'bool')
	Boolean isDiscount
	
	@GraphQLQuery
	@Column(name = "is_completed", columnDefinition = 'bool')
	Boolean isCompleted
	
	@GraphQLQuery
	@Column(name = "is_partial", columnDefinition = 'bool')
	Boolean isPartial
	
	@GraphQLQuery
	@Column(name = "is_posted", columnDefinition = 'bool')
	Boolean isPosted

	@GraphQLQuery(name = "discountRate")
	@Transient
	BigDecimal discountRate
	BigDecimal getDiscountRate() {
		def discountRate = BigDecimal.ZERO
		if(totalAmount){
			def lprice = totalAmount;
			def sprice = totalAmount - receiveDiscountCost;
			discountRate = ((lprice - sprice) / lprice) * 100;
		}
		discountRate
	}

	@GraphQLQuery(name = "unitMeasurement")
	@Transient
	String getUnitMeasurement() {
		return "${item.unit_of_purchase?.unitDescription} (${item.item_conversion} ${item.unit_of_usage?.unitDescription})"
	}

	@GraphQLQuery(name = "uou")
	@Transient
	String getUou() {
		return "${item.unit_of_usage?.unitDescription}"
	}
}
