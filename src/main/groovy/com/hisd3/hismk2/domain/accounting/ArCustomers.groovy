package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.accounting.dto.CompanyDiscountAndPenalties
import com.hisd3.hismk2.domain.types.Subaccountable
import com.hisd3.hismk2.rest.dto.CoaConfig
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

enum CustomerType {
    HMO,
    CORPORATE,
    PERSONAL,
    PROMISSORY_NOTE,
}


@Entity
@Table(schema = "accounting", name = "ar_customers")
class ArCustomers extends AbstractAuditingEntity implements Serializable, Subaccountable {

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "account_prefix")
    String accountPrefix

    @GraphQLQuery
    @Column(name = "account_no", unique = true)
    String accountNo

    @GraphQLQuery
    @Column(name = "name")
    String customerName

    @GraphQLQuery
    @Column(name = "address")
    String address

    @Enumerated(EnumType.STRING)
    @GraphQLQuery
    @Column(name = "type")
    CustomerType customerType

    @GraphQLQuery
    @Type(type = "jsonb")
    @Column(name="discount_and_penalties",columnDefinition = "jsonb")
    CompanyDiscountAndPenalties discountAndPenalties

    @GraphQLQuery
    @Column(name = "reference_id")
    UUID referenceId


    @GraphQLQuery
    @Column(name = "patient_id")
    UUID patientId

    @Override
    String getDomain() {
        return ArCustomers.class.name
    }

    @Override
    String getCode() {
        return accountNo
    }

    @Override
    String getDescription() {
        return customerName
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
