package com.hisd3.hismk2.domain.hrm


import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.hrm.enums.JobTitleStatus
import io.leangen.graphql.annotations.GraphQLQuery
import liquibase.structure.core.PrimaryKey
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where

import javax.persistence.*
import java.time.Instant

@Entity
@Table(schema = "hrm", name = "job_title")
@SQLDelete(sql = "UPDATE hrm.job_title SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class JobTitle extends AbstractAuditingEntity implements Serializable {

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id


    @GraphQLQuery
    @Column(name = "value", columnDefinition = "varchar")
    String value

    @GraphQLQuery
    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "varchar")
    JobTitleStatus status


}
