package com.hisd3.hismk2.domain.doh

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.Type

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table


@Entity
@Table(schema = "doh",name = "test_procedure_type")
class TestProcedureType extends AbstractAuditingEntity implements Serializable {

    @Id
    @GraphQLQuery
    @Column(name = "code")
    Integer code

    @GraphQLQuery
    @Column(name = "description")
    String description

    @GraphQLQuery
    @Column(name = "group_code")
    Integer groupCode

}
