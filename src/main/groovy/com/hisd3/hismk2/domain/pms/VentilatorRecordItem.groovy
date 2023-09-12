package com.hisd3.hismk2.domain.pms

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.action.internal.OrphanRemovalAction
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant

@Entity
@Table(schema = "pms", name = "ventilator_record_items")
class VentilatorRecordItem extends AbstractAuditingEntity {

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ventilator_record", referencedColumnName = "id")
    VentilatorRecord ventilatorRecord

    @GraphQLQuery
    @Column(name = "fi02", columnDefinition = "int")
    Integer fi02

    @GraphQLQuery
    @Column(name = "peep", columnDefinition = "int")
    Integer peep

    @GraphQLQuery
    @Column(name = "sputum_character", columnDefinition = "varchar")
    String sputumCharacter

    @GraphQLQuery
    @Column(name = "sputum_character_remarks", columnDefinition = "varchar")
    String sputumCharacterRemarks

    @GraphQLQuery
    @Column(name = "sputum_result", columnDefinition = "varchar")
    String sputumResult

    @GraphQLQuery
    @Column(name = "sputum_result_remarks", columnDefinition = "varchar")
    String sputumResultRemarks
}
