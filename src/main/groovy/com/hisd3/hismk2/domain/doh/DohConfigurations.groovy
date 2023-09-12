package com.hisd3.hismk2.domain.doh

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant



class TotalDeathsConfig {
    Boolean includeStillBirths
    Boolean includeNeonatal
    Boolean includeMaternal
}

@Entity
@Table(name = "configurations", schema = "doh")
class DohConfiguration extends AbstractAuditingEntity{
    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Type(type = "jsonb")
    @Column(name="total_deaths_config",columnDefinition = "jsonb")
    TotalDeathsConfig totalDeathsConfig
}
