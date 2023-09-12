package com.hisd3.hismk2.domain.billing

import com.fasterxml.jackson.annotation.JsonIgnore
import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.PersistentToken
import com.hisd3.hismk2.domain.annotations.UpperCase
import com.hisd3.hismk2.domain.hrm.Employee
import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant

@Canonical
class MoreInformation implements Serializable {
    Map<String, String> title
    String value
}

@Entity
@Table(name = "investors", schema = "billing")
class Investor extends AbstractAuditingEntity {

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

//    @JsonIgnore
//    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "investor")
//    List<InvestorAttachment> attachment = [] as List


    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "investor")
    List<Subscription> subscriptions = [] as List

    @JsonIgnore
    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="referred_by", referencedColumnName = "id")
    Employee referredBy

    @GraphQLQuery
    @Column(name = "firstname", columnDefinition = "varchar")
    String firstname

    @GraphQLQuery
    @Column(name = "middlename", columnDefinition = "varchar")
    String middlename

    @GraphQLQuery
    @Column(name = "lastname", columnDefinition = "varchar")
    String lastname

    @GraphQLQuery
    @Column(name = "address", columnDefinition = "varchar")
    @UpperCase
    String address

    @GraphQLQuery
    @Column(name = "active", columnDefinition = "boolean")
    Boolean active

    @GraphQLQuery
    @Column(name = "dob", columnDefinition = "timestamp")
    Instant dob

    @GraphQLQuery
    @Column(name = "date_of_full_payment", columnDefinition = "timestamp")
    Instant dateOfFullPayment

    @GraphQLQuery
    @Column(name = "noofdaysleft", columnDefinition = "numeric")
    BigDecimal noofdaysleft

    @GraphQLQuery
    @Column(name = "investor_no", columnDefinition = "varchar")
    String investorNo

    @GraphQLQuery
    @Column(name = "version", columnDefinition = "varchar")
    String version

    @GraphQLQuery
    @Column(name = "datepaid", columnDefinition = "date")
    Instant datepaid

    @GraphQLQuery
    @Column(name = "arno", columnDefinition = "varchar")
    String arno

    @GraphQLQuery
    @Column(name = "suffix", columnDefinition = "varchar")
    @UpperCase
    String suffix

    @GraphQLQuery
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "investor", cascade = [CascadeType.ALL])
    Set<InvestorDependent> dependents = [] as Set

    @GraphQLQuery
    @Formula("concat(lastname , coalesce(', ' || nullif(firstname,'') , ''), coalesce(' ' || nullif(middlename,'') , ''))")
    String fullName

    @GraphQLQuery
    @Column(name = "tin_no", columnDefinition = "varchar")
    String tinNo

    @GraphQLQuery
    @Column(name = "stock_cert_no", columnDefinition = "varchar")
    String stockCertNo

    @GraphQLQuery
    @Column(name = "barangay", columnDefinition = "varchar")
    @UpperCase
    String barangay

    @GraphQLQuery
    @Column(name = "city", columnDefinition = "varchar")
    String city

    @GraphQLQuery
    @Column(name = "province", columnDefinition = "varchar")
    String province

    @GraphQLQuery
    @Column(name = "country", columnDefinition = "varchar")
    String country

    @GraphQLQuery
    @Column(name = "zip_code", columnDefinition = "varchar")
    @UpperCase
    String zipCode

    @GraphQLQuery
    @Column(name = "gender", columnDefinition = "varchar")
    @UpperCase
    String gender

    @GraphQLQuery
    @Column(name = "civil_status", columnDefinition = "varchar")
    String civilStatus

    @GraphQLQuery
    @Column(name = "place_of_birth", columnDefinition = "varchar")
    @UpperCase
    String placeOfBirth

    @GraphQLQuery
    @Column(name = "email_address", columnDefinition = "varchar")
    String emailAddress

    @GraphQLQuery
    @Column(name = "present_employer", columnDefinition = "varchar")
    @UpperCase
    String presentEmployer

    @GraphQLQuery
    @Column(name = "educational_attainment", columnDefinition = "varchar")
    @UpperCase
    String educationalAttainment

    @GraphQLQuery
    @Column(name = "profession", columnDefinition = "varchar")
    @UpperCase
    String profession

    @GraphQLQuery
    @Column(name = "office_address", columnDefinition = "varchar")
    @UpperCase
    String officeAddress

    @GraphQLQuery
    @Type(type = "jsonb")
    @Column(name = "contact_numbers", columnDefinition = "jsonb")
    List<MoreInformation> contactNumbers = []

    @GraphQLQuery
    @Type(type = "jsonb")
    @Column(name = "identifications", columnDefinition = "jsonb")
    List<MoreInformation> identifications = []

}
