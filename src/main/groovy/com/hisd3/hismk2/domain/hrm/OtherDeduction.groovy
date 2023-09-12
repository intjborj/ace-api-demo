package com.hisd3.hismk2.domain.hrm

import com.hisd3.hismk2.domain.AbstractAuditingEntity

import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where

import javax.persistence.*

@Entity
@Table(schema = "hrm", name = "other_deductions")
@SQLDelete(sql = "UPDATE hrm.other_deductions SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class OtherDeduction extends AbstractAuditingEntity implements Serializable {

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "employee_other_deductions",
            schema = "hrm",
            joinColumns = @JoinColumn(name = "other_deduction", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "employee", referencedColumnName = "id")
    )
    Set<Employee> employees

    @Column(name = "title", columnDefinition = "varchar")
    String title

    @Column(name = "identifier", columnDefinition = "varchar")
    String identifier

    @Column(name = "amount", columnDefinition = "numeric(15,2)")
    BigDecimal amount

    @Column(name = "active", columnDefinition = "bool")
    Boolean active


}
