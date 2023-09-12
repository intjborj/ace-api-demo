package com.hisd3.hismk2.domain.address

import com.hisd3.hismk2.domain.annotations.UpperCase
import io.leangen.graphql.annotations.GraphQLQuery
import org.apache.commons.lang3.StringUtils

import javax.persistence.*

@Entity
@Table(schema = "public", name = "countries")
class Country {

    @GraphQLQuery
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id

    @GraphQLQuery
    @UpperCase
    @Column(name = "country", columnDefinition = "varchar")
    String country

    @Transient
    String getCountryName() {
        return StringUtils.defaultString(country.toUpperCase())
    }

    @GraphQLQuery
    @UpperCase
    @Column(name = "shortname", columnDefinition = "varchar")
    String shortName
}
