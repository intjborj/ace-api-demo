package com.hisd3.hismk2.domain.doh

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant
import java.time.LocalDate


@Entity
@Table(name = "create_rvs_account", schema = "doh")
class CreateRsvAccount extends AbstractAuditingEntity{
    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "hfhudname", columnDefinition = "varchar")
    String hfhUdName

    @GraphQLQuery
    @Column(name = "fhudaddress", columnDefinition = "varchar")
    String fhUdAddress

    @GraphQLQuery
    @Column(name = "regcode", columnDefinition = "varchar")
    String regCode

    @GraphQLQuery
    @Column(name = "provcode", columnDefinition = "varchar")
    String provCode

    @GraphQLQuery
    @Column(name = "ctymuncode", columnDefinition = "varchar")
    String ctyMunCode

    @GraphQLQuery
    @Column(name = "bgycode", columnDefinition = "varchar")
    String bgyCode

    @GraphQLQuery
    @Column(name = "fhudtelno1", columnDefinition = "varchar")
    String fhUdTelNo1

    @GraphQLQuery
    @Column(name = "fhudtelno2", columnDefinition = "varchar")
    String fhUdTelNo2

    @GraphQLQuery
    @Column(name = "fhudfaxno", columnDefinition = "varchar")
    String fhUdFaxNo

    @GraphQLQuery
    @Column(name = "fhudemail", columnDefinition = "varchar")
    String fhUdEmail

    @GraphQLQuery
    @Column(name = "headlname", columnDefinition = "varchar")
    String headlName

    @GraphQLQuery
    @Column(name = "headfname", columnDefinition = "varchar")
    String headfName

    @GraphQLQuery
    @Column(name = "headmname", columnDefinition = "varchar")
    String headmName

    @GraphQLQuery
    @Column(name = "access_key", columnDefinition = "varchar")
    String accessKey

    @GraphQLQuery
    @Column(name = "submitted_date_time", columnDefinition = "timestamp")
    Instant submittedDateTime

}
