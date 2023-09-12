package com.hisd3.hismk2.domain.hrm

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.types.JaversResolvable
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.ResultCheckStyle
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table



@Entity
@Table(schema = "hrm", name = "biometric_service_config")
@SQLDelete(sql = "UPDATE hrm.biometric_service_config SET deleted = true WHERE id = ? ", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted <> true or deleted is null")
class BiometricServiceConfig extends AbstractAuditingEntity implements JaversResolvable, Serializable {

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id


    @GraphQLQuery
    @Column(name = "ip_address", columnDefinition = "varchar")
    String ipAddress


    @GraphQLQuery
    @Column(name = "port", columnDefinition = "varchar")
    String port

    @Override
    String resolveEntityForJavers() {
        return ipAddress
    }

}
