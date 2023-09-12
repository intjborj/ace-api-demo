package com.hisd3.hismk2.domain.hospital_config

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.inventory.DepartmentStockIssue
import com.hisd3.hismk2.domain.inventory.Inventory
import com.hisd3.hismk2.domain.inventory.Item
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*

import javax.persistence.*

@javax.persistence.Entity
@javax.persistence.Table(schema = "hospital_configuration", name = "default_admission_items")
class DefaultAdmissionItems extends AbstractAuditingEntity implements Serializable {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "item_id", referencedColumnName = "id")
    Item item

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "department_id", referencedColumnName = "id")
	Department department

	@GraphQLQuery
	@Column(name = "quantity")
	BigDecimal quantity

	@GraphQLQuery
	@Column(name = "inventory_id")
	UUID inventory

	@GraphQLQuery
	@Column(name = "refused_admission_kit_item")
	Boolean refusedAdmissionKitItem

	@GraphQLQuery
	@Column(name = "admission_kit_type")
	String admissionKitType


}
