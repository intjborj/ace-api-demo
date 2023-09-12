package com.hisd3.hismk2.domain.inventory

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.types.Subaccountable
import com.hisd3.hismk2.rest.dto.CoaConfig
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where

import javax.persistence.*

@Entity
@Table(schema = "inventory", name = "supplier_types")
@SQLDelete(sql = "UPDATE inventory.supplier_types SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class SupplierType extends AbstractAuditingEntity implements Subaccountable {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "supplier_type_code")
	String supplierTypeCode
	
	@GraphQLQuery
	@Column(name = "sup_sub_account_code")
	String subAccountCode
	
	@GraphQLQuery
	@Column(name = "supplier_type_description")
	String supplierTypeDesc
	
	@GraphQLQuery
	@Column(name = "sup_ewt_rate")
	Integer supEwtRate
	
	@GraphQLQuery
	@Column(name = "is_active")
	Boolean isActive

	@Override
	String getDomain() {
		return SupplierType.class.name
	}

	@Override
	String getCode() {
		return subAccountCode
	}

	@Override
	String getDescription() {
		return supplierTypeDesc
	}

	@Override
	List<UUID> getDepartment() {
		return null
	}

	@Override
	CoaConfig getConfig() {
		new CoaConfig(show: true, showDepartments: true)
	}
	
}
