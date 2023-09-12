package com.hisd3.hismk2.domain

import com.hisd3.hismk2.domain.ancillary.AncillaryConfig
import com.hisd3.hismk2.domain.hrm.DepartmentSchedule
import com.hisd3.hismk2.domain.inventory.DepartmentItem
import com.hisd3.hismk2.domain.types.CodeAndDescable
import com.hisd3.hismk2.domain.types.JaversResolvable
import com.hisd3.hismk2.rest.dto.CoaConfig
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*

import javax.persistence.*

@javax.persistence.Entity
@javax.persistence.Table(name = "departments", schema = "public")
//@Where(clause = "deleted <> true or deleted is  null ")
class Department extends AbstractAuditingEntity implements JaversResolvable , CodeAndDescable{

	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@GraphQLQuery
	@Column(name = "department_code", columnDefinition = "varchar")
	String departmentCode

	@GraphQLQuery
	@Column(name = "department_name", columnDefinition = "varchar")
	String departmentName

	@GraphQLQuery
	@Column(name = "department_desc", columnDefinition = "varchar")
	String departmentDesc

	@GraphQLQuery
	@Column(name = "group_category", columnDefinition = "varchar")
	String groupCategory

	@GraphQLQuery
	@Column(name = "department_head", columnDefinition = "uuid default null")
	UUID departmentHead

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "parentDepartment", referencedColumnName = "id")
	Department parentDepartment

	@GraphQLQuery
	@Column(name = "special_area", columnDefinition = "boolean default false")
	Boolean specialArea

	@GraphQLQuery
	@Column(name = "cost_center", columnDefinition = "boolean default false")
	Boolean costCenter

	@GraphQLQuery
	@Column(name = "revenue_center", columnDefinition = "boolean default false")
	Boolean revenueCenter

	@GraphQLQuery
	@Column(name = "sub_department", columnDefinition = "boolean default false")
	Boolean subDepartment

	@GraphQLQuery
	@Column(name = "deleted", columnDefinition = "boolean default false")
	Boolean deleted

	@GraphQLQuery
	@Column(name = "has_rooms", columnDefinition = "boolean")
	Boolean hasRooms

	@GraphQLQuery
	@Column(name = "has_patients", columnDefinition = "boolean")
	Boolean hasPatients

	@GraphQLQuery
	@Column(name = "can_clear_patient_discharge", columnDefinition = "boolean")
	Boolean canClearPatientDischarge

	@GraphQLQuery
	@Column(name = "can_receive_items", columnDefinition = "boolean")
	Boolean canReceiveItems

	@GraphQLQuery
	@Column(name = "can_purchase_items", columnDefinition = "boolean")
	Boolean canPurchaseItems

	@GraphQLQuery
	@Column(name = "deprecated", columnDefinition = "boolean")
	Boolean deprecated

	@GraphQLQuery
	@Column(name = "medication_stock_request", columnDefinition = "boolean")
	Boolean medicationStockRequest

	@GraphQLQuery
	@Column(name = "alternate_id_prefix", columnDefinition = "varchar")
	String idPrefix

	@GraphQLQuery
	@Column(name = "revenue_tag", columnDefinition = "varchar")
	String revenueTag

	@GraphQLQuery
	@Column(name = "events_to_notify", columnDefinition = "text")
	String eventsToNotify

	@GraphQLQuery
	@Column(name = "is_default_clearing_department", columnDefinition = "boolean")
	Boolean isDefaultClearingDepartment

	@GraphQLQuery
	@Column(name = "has_special_price_tier", columnDefinition = "boolean")
	Boolean hasSpecialPriceTier

	@NotFound(action = NotFoundAction.IGNORE)
	@GraphQLQuery
	//@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "parentDepartment",fetch = FetchType.EAGER)
	List<Department> children

	@NotFound(action = NotFoundAction.IGNORE)
	@GraphQLQuery
	//@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
	List<DepartmentItem> departmentItems

	@NotFound(action = NotFoundAction.IGNORE)
	@GraphQLQuery
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "integrated_", referencedColumnName = "id")
	AncillaryConfig ancillaryConfig

	@GraphQLQuery
	@Column(name = "has_diagnostic", columnDefinition = "boolean")
	Boolean hasDiagnostics

	@GraphQLQuery
	@Column(name = "hide_accounting", columnDefinition = "boolean")
	Boolean hideAccounting

	@GraphQLQuery
	@OneToMany(mappedBy = "department")
	List<DepartmentSchedule> workSchedule


	@Override
	String resolveEntityForJavers() {
		return departmentName
	}

	@Override
	String getCode() {
		return departmentCode
	}

	@Override
	String getDescription() {
		return departmentName
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
