package com.hisd3.hismk2.domain.philhealth

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.pms.Case
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where
import org.javers.core.metamodel.annotation.DiffIgnore

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "claim_items", schema = "philhealth")
@SQLDelete(sql = "UPDATE philhealth.claim_items SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class ClaimItem extends AbstractAuditingEntity implements Serializable {
    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @DiffIgnore
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "`case`", referencedColumnName = "id")
    Case parentCase

    @DiffIgnore
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim", referencedColumnName = "id")
    Claim claims

    @GraphQLQuery
    @Column(name = "tracking_no", columnDefinition = "varchar")
    String trackingNo

    @GraphQLQuery
    @Column(name = "total_amt_actual", columnDefinition = "numeric")
    BigDecimal totalAmtActual

    @GraphQLQuery
    @Column(name = "total_amt_claimed", columnDefinition = "numeric")
    BigDecimal totalAmtClaimed

    @GraphQLQuery
    @Column(name = "claim_type", columnDefinition = "varchar")
    String claimType
}
