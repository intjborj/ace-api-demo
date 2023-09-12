package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.Column
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

enum LineTypeOperationType{
    ADD,
    SUBTRACT,
    MULTIPLICATION,
    DIVISION
}


@javax.persistence.Entity
@javax.persistence.Table(name = "source_line_type", schema = "accounting")
class SourceLineType extends AbstractAuditingEntity implements Serializable{

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @UpperCase
    @GraphQLQuery
    @Column(name = "code", columnDefinition = "varchar")
    String code

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "line_type", referencedColumnName = "id")
    LineType lineType

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "linetype_parent", referencedColumnName = "id")
    LineType linetypeParent


    @Enumerated(value = EnumType.STRING)
    @Column(name = "operation_type", columnDefinition = "varchar")
    LineTypeOperationType operationType
}
