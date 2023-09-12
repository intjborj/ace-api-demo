package com.hisd3.hismk2.domain.inventory

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*

import javax.persistence.*

@javax.persistence.Entity
@javax.persistence.Table(schema = "inventory", name = "item_categories")
@SQLDelete(sql = "UPDATE inventory.item_categories SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class ItemCategory extends AbstractAuditingEntity implements Serializable{
	
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
	@JoinColumn(name = "fk_item_group", referencedColumnName = "id")
	ItemGroup itemGroup
	
	@GraphQLQuery
	@Column(name = "category_code")
	String categoryCode
	
	@GraphQLQuery
	@Column(name = "category_description")
	String categoryDescription
	
	@GraphQLQuery
	@Column(name = "is_active")
	Boolean isActive
}
