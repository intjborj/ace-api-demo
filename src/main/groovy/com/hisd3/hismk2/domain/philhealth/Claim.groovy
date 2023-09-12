package com.hisd3.hismk2.domain.philhealth;

import com.hisd3.hismk2.domain.AbstractAuditingEntity;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where;

import javax.persistence.*

@Entity
@Table(name = "claims", schema = "philhealth")
@SQLDelete(sql = "UPDATE philhealth.claims SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class Claim extends AbstractAuditingEntity implements Serializable {
    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "batch_no", columnDefinition = "varchar")
    String batchNo


    @GraphQLQuery
    @Column(name = "ticket_no", columnDefinition = "varchar")
    String ticketNo


}
