package com.hisd3.hismk2.domain.billing

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import com.hisd3.hismk2.domain.types.Subaccountable
import com.hisd3.hismk2.graphqlservices.billing.CompanyAccountServices
import com.hisd3.hismk2.rest.dto.CoaConfig
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type
import org.springframework.beans.factory.annotation.Autowired

import javax.persistence.*

@Entity
@Table(name = "companyaccounts", schema = "billing")
class CompanyAccount extends AbstractAuditingEntity implements Serializable, Subaccountable {

	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "companyaccountid", columnDefinition = "varchar")
	@UpperCase
	String companyaccountId
	
	@GraphQLQuery
	@Column(name = "companyname", columnDefinition = "varchar")
	@UpperCase
	String companyname
	
	@GraphQLQuery
	@Column(name = "referenceno", columnDefinition = "varchar")
	@UpperCase
	String referenceno
	
	@GraphQLQuery
	@Column(name = "companyfulladdress", columnDefinition = "varchar")
	@UpperCase
	String companyFullAddress
	
	@GraphQLQuery
	@Column(name = "contactno", columnDefinition = "varchar")
	@UpperCase
	String contactno
	
	@GraphQLQuery
	@Column(name = "contactperson", columnDefinition = "varchar")
	@UpperCase
	String contactPerson
	
	@GraphQLQuery
	@Column(name = "remarks", columnDefinition = "varchar")
	@UpperCase
	String remarks
	
	@GraphQLQuery
	@Column(name = "tag", columnDefinition = "varchar")
	@UpperCase
	String tag
	
	@GraphQLQuery
	@Column(name = "hide_in_soa", columnDefinition = "boolean")
	Boolean hideInSoa

	@GraphQLQuery
	@Column(name = "direct_to_ar", columnDefinition = "boolean")
	Boolean directToAr

	@GraphQLQuery
	@Column(name = "phil_health", columnDefinition = "boolean")
	Boolean philHealth

	@GraphQLQuery
	@Column(name = "promissory_note", columnDefinition = "boolean")
	Boolean promissoryNote

	@GraphQLQuery
	@Column(name = "validation_source", columnDefinition = "varchar")
	@UpperCase
	String validationSource

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "company_type", referencedColumnName = "id")
	CompanyType companyType

	@GraphQLQuery
	@Formula("(Select coalesce(b.code,'') from billing.company_type b where b.id=company_type)")
	String compTypeCode

	@GraphQLQuery
	@Formula("(Select coalesce(upper(b.description),'') from billing.company_type b where b.id=company_type)")
	String compTypeDescription


	@Override
	String getDomain() {
		return CompanyAccount.class.name
	}

    @Override
    String getCode() {
        return companyaccountId
    }

    @Override
    String getDescription() {
        return companyname
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
