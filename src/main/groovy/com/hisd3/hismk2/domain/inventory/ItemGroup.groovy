package com.hisd3.hismk2.domain.inventory

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.billing.PackageItem
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where

import javax.persistence.*

@Entity
@Table(schema = "inventory", name = "item_groups")
@SQLDelete(sql = "UPDATE inventory.item_groups SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class ItemGroup extends AbstractAuditingEntity {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "item_code")
	String itemCode
	
	@GraphQLQuery
	@Column(name = "item_description")
	String itemDescription
	
	@GraphQLQuery
	@Column(name = "is_active")
	Boolean isActive

	@GraphQLQuery
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "itemGroup", cascade = [CascadeType.ALL], orphanRemoval = true)
	List<ItemCategory> groupCategories
}
