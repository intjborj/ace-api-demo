package com.hisd3.hismk2.domain.inventory

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.types.Subaccountable
import com.hisd3.hismk2.rest.dto.CoaConfig
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*

import javax.persistence.*

@javax.persistence.Entity
@javax.persistence.Table(schema = "inventory", name = "supplier")
@SQLDelete(sql = "UPDATE inventory.supplier SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class Supplier extends AbstractAuditingEntity implements Subaccountable {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "supplier_code")
	String supplierCode
	
	@GraphQLQuery
	@Column(name = "supplier_fullname")
	String supplierFullname
	
	@GraphQLQuery
	@Column(name = "supplier_tin")
	String supplierTin
	
	@GraphQLQuery
	@Column(name = "supplier_email")
	String supplierEmail
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_terms", referencedColumnName = "id")
	PaymentTerm paymentTerms //fk
	
	@GraphQLQuery
	@Column(name = "supplier_entity")
	String supplierEntity
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "supplier_types", referencedColumnName = "id")
	SupplierType supplierTypes //fk
	
	@GraphQLQuery
	@Column(name = "credit_limit")
	BigDecimal creditLimit
	
	@GraphQLQuery
	@Column(name = "is_vatable")
	Boolean isVatable
	
	@GraphQLQuery
	@Column(name = "is_vat_inclusive")
	Boolean isVatInclusive
	
	@GraphQLQuery
	@Column(name = "remarks")
	String remarks
	
	@GraphQLQuery
	@Column(name = "lead_time")
	Integer leadTime
	
	@GraphQLQuery
	@Column(name = "primary_address")
	String primaryAddress
	
	@GraphQLQuery
	@Column(name = "primary_telphone")
	String primaryTelphone
	
	@GraphQLQuery
	@Column(name = "primary_contactperson")
	String primaryContactPerson
	
	@GraphQLQuery
	@Column(name = "primary_fax")
	String primaryFax
	
	@GraphQLQuery
	@Column(name = "secondary_address")
	String secondaryAddress
	
	@GraphQLQuery
	@Column(name = "secondary_telphone")
	String secondaryTelphone
	
	@GraphQLQuery
	@Column(name = "secondary_contactperson")
	String secondaryContactPerson
	
	@GraphQLQuery
	@Column(name = "secondary_fax")
	String secondaryFax

	@GraphQLQuery
	@Column(name = "atc_no")
	String atcNo

	@GraphQLQuery
	@Column(name = "employee_id")
	UUID employeeId

	@GraphQLQuery
	@Column(name = "investor_id")
	UUID investorId

	@GraphQLQuery
	@Column(name = "is_active")
	Boolean isActive

	@GraphQLQuery
	@Column(name = "ewt_rate")
	BigDecimal ewtRate

	@Override
	String getDomain() {
		return Supplier.class.name
	}

	@Override
	String getCode() {
		return supplierCode
	}

	@Override
	String getDescription() {
		return supplierFullname
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
