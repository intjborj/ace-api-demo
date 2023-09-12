package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.OrderBy


@javax.persistence.Entity
@javax.persistence.Table(name = "financial_report", schema = "accounting")
class FinancialReport  extends AbstractAuditingEntity implements Serializable {
    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id


    @UpperCase
    @GraphQLQuery
    @Column(name = "title", columnDefinition = "varchar")
    String title


    @UpperCase
    @GraphQLQuery
    @Column(name = "code", columnDefinition = "varchar")
    String code


    @GraphQLQuery
    @Column(name = "compare_prev_month", columnDefinition = "bool")
    Boolean comparePrevMonth


    @GraphQLQuery
    @Column(name = "can_select_department", columnDefinition = "bool")
    Boolean canSelectDepartment


    @GraphQLQuery
    @Column(name = "show_zero_amount", columnDefinition = "bool")
    Boolean showZeroAmount


    @GraphQLQuery
    @Column(name = "show_all", columnDefinition = "bool")
    Boolean showAll



    @GraphQLQuery
    @OrderBy("orderLine")
    @OneToMany(mappedBy="report",cascade = [CascadeType.ALL],fetch = FetchType.LAZY,orphanRemoval = true)
    List<LineType> lineTypes= []

    @GraphQLQuery
    @Column(name = "periodic", columnDefinition = "bool")
    Boolean periodic



}
