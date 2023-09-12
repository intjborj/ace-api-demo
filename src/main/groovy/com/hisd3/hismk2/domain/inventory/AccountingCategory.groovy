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
@Table(schema = "inventory", name = "accounting_categories")
@SQLDelete(sql = "UPDATE inventory.accounting_categories SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class AccountingCategory extends AbstractAuditingEntity implements Subaccountable{
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "category_code", columnDefinition = "varchar")
	String categoryCode
	
	@GraphQLQuery
	@Column(name = "category_description", columnDefinition = "varchar")
	String categoryDescription

	@GraphQLQuery
	@Column(name = "is_fixed_asset", columnDefinition = "boolean")
	Boolean isFixedAsset

	@GraphQLQuery
	@Column(name = "is_active", columnDefinition = "boolean")
	Boolean isActive

	@GraphQLQuery
	@Column(name = "departments", columnDefinition = "varchar")
	String departments

	@GraphQLQuery
	@Column(name = "include_department", columnDefinition = "boolean")
	Boolean includeDepartments

	@GraphQLQuery
	@Column(name = "mother_accounts", columnDefinition = "varchar")
	String motherAccounts

	@GraphQLQuery
	@Column(name = "account_type", columnDefinition = "varchar")
	String accountType

	@GraphQLQuery
	@Column(name = "source_column", columnDefinition = "varchar")
	String sourceColumn

	@Transient
	List<UUID> getSelectedDepartments() {
		if(departments){
			def list = departments.split(',').collect{UUID.fromString(it)}
			return list
		}else {
			return null
		}
	}

	@Transient
	List<UUID> getMotherAccountsList() {
		if(motherAccounts){
			def list = motherAccounts.split(',').collect{UUID.fromString(it)}
			return list
		}else {
			return null
		}
	}

	@Override
	String getDomain() {
		return AccountingCategory.class.name
	}

	@Override
	String getCode() {
		return categoryCode
	}

	@Override
	String getDescription() {
		return categoryDescription
	}

	@Override
	List<UUID> getDepartment() {
		return selectedDepartments
	}

	@Override
	CoaConfig getConfig() {
		new CoaConfig(show: isActive, showDepartments: includeDepartments, motherAccounts: motherAccountsList)
	}
}
