package com.hisd3.hismk2.domain.address

import com.hisd3.hismk2.domain.annotations.UpperCase
import io.leangen.graphql.annotations.GraphQLQuery
import org.apache.commons.lang3.StringUtils
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction

import javax.persistence.*

@Entity
@Table(schema = "public", name = "cities")
class City {

    @GraphQLQuery
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "province_id", referencedColumnName = "id")
    Province province

    @GraphQLQuery
    @UpperCase
    @Column(name = "name", columnDefinition = "varchar")
    String name

    @Transient
    String getCityName() {
        return StringUtils.defaultString(name.toUpperCase())
    }

    @GraphQLQuery
    @UpperCase
    @Column(name = "zipcode", columnDefinition = "varchar")
    String zipCode


    @GraphQLQuery
    @UpperCase
    @Column(name = "district", columnDefinition = "varchar")
    String district
}
