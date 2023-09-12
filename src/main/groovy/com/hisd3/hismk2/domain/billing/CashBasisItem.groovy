package com.hisd3.hismk2.domain.billing

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.Column
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Transient


@javax.persistence.Entity
@javax.persistence.Table(name = "medsupply_cashbasis", schema = "billing")
class CashBasisItem extends AbstractAuditingEntity implements Serializable {

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
    @Column(name = "code", columnDefinition = "varchar")
    String code

    @GraphQLQuery
    @Column(name = "data", columnDefinition = "varchar")
    String data

    /*
     {
    inventoryId: item.supply.id,
    quantity: item.quantity || 0
     }
     */

    /*

 com/hisd3/hismk2/graphqlservices/billing/BillingItemServices.groovy:1316

    def quantity = it.getOrDefault("quantity", 0) as Integer
				def itemId = it.getOrDefault("itemId", "") as String
				def targetDepartment = it.getOrDefault("targetDepartment", "") as String
				def returnedBillingItemId = it.getOrDefault("returnedBillingItemId", "") as String
				def cashBasisItemId = it.getOrDefault("cashBasisItemId",null) as UUID


com/hisd3/hismk2/graphqlservices/billing/BillingItemServices.groovy:1503
   def quantity = it.getOrDefault("quantity", 0) as Integer
				def inventoryId = it.getOrDefault("inventoryId", "") as String
				def itemId = it.getOrDefault("itemId", "") as String
				def returnedBillingItemId = it.getOrDefault("returnedBillingItemId", "") as String
     */

    @GraphQLQuery
    @Column(name = "type", columnDefinition = "varchar")
    String type

    @GraphQLQuery
    @Column(name = "`department_id`", columnDefinition = "uuid")
    UUID departmentId

    @GraphQLQuery
    @Transient
    List<Map<String,String>> expandDetails = []


    @GraphQLQuery
    @Column(name = "processed", columnDefinition = "boolean")
    Boolean processed

}
