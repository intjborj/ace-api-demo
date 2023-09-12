package com.hisd3.hismk2.domain.fixed_assets

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
@Table(schema = "fixed_assets", name = "fixed_asset_category")
@SQLDelete(sql = "UPDATE fixed_assets.fixed_asset_category SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class FixedAssetCategory extends AbstractAuditingEntity implements Subaccountable, Serializable{
	
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
	@Column(name = "is_active", columnDefinition = "boolean")
	Boolean isActive

	@Override
	String getDomain() {
		return FixedAssetCategory.class.name
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
		return []
	}

	@Override
	CoaConfig getConfig() {
		new CoaConfig(show: isActive, showDepartments: false)
	}
}
