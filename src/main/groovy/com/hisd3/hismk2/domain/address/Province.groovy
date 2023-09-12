package com.hisd3.hismk2.domain.address

import com.hisd3.hismk2.domain.annotations.UpperCase
import io.leangen.graphql.annotations.GraphQLQuery
import org.apache.commons.lang3.StringUtils

import javax.persistence.*

@Entity
@Table(schema = "public", name = "provinces")
class Province {

    @GraphQLQuery
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @GraphQLQuery
    @UpperCase
    @Column(name = "name", columnDefinition = "varchar")
    String name

    @Transient
    String getProvinceName() {
        return StringUtils.defaultString(name.toUpperCase())
    }

}
