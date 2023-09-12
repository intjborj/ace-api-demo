package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.OrderBy

enum SourceType{
    LINE_TYPE,
    SUB_ACCOUNT,
    MOTHER_ACCOUNT, // for those who have no Sub
    FIXED_VALUE,
    TEXT, // just caption.
    HIDDEN,
}


@javax.persistence.Entity
@javax.persistence.Table(name = "line_type", schema = "accounting")
class LineType extends AbstractAuditingEntity implements Serializable {
    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id


    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "report", referencedColumnName = "id")
    FinancialReport report

    @UpperCase
    @GraphQLQuery
    @Column(name = "caption", columnDefinition = "varchar")
    String caption

    @UpperCase
    @GraphQLQuery
    @Column(name = "code", columnDefinition = "varchar")
    String code

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_line_type", referencedColumnName = "id")
    LineType parentLineType


    @Enumerated
    @Column(name = "source_type", columnDefinition = "varchar")
    SourceType sourceType

    @UpperCase
    @GraphQLQuery
    @Column(name = "order_line", columnDefinition = "varchar")
    String orderLine



    @GraphQLQuery
    @Column(name = "fixed_value", columnDefinition = "numeric")
    BigDecimal fixedValue


    /*
     Reversed
        showMotherAccount
        showDepartment
        showSubSub
     */
    @GraphQLQuery
    @Column(name = "show_mother_account", columnDefinition = "boolean")
    Boolean showMotherAccount

    @GraphQLQuery
    @Column(name = "show_department", columnDefinition = "boolean")
    Boolean showDepartment


    @GraphQLQuery
    @Column(name = "show_sub_sub", columnDefinition = "boolean")
    Boolean showSubSub


    @GraphQLQuery
    @Column(name = "bold", columnDefinition = "bool")
    Boolean bold

    @GraphQLQuery
    @Column(name = "bold_underlined", columnDefinition = "bool")
    Boolean boldUnderlined



    @GraphQLQuery
    @OrderBy("orderLine")
    @OneToMany(mappedBy="parentLineType")
    List<LineType> children= []




    @GraphQLQuery
    @OrderBy("code")
    @OneToMany(mappedBy="linetypeParent",cascade = [CascadeType.ALL],fetch = FetchType.LAZY,orphanRemoval = true)
    List<SourceLineType> sourceLineTypes= []

    @GraphQLQuery
    @OrderBy("code")
    @OneToMany(mappedBy="lineType",cascade = [CascadeType.ALL],fetch = FetchType.LAZY,orphanRemoval = true)
    List<SourceSubAccount> sourceSubAccountList= []


    @GraphQLQuery
    @OrderBy("code")
    @OneToMany(mappedBy="lineType",cascade = [CascadeType.ALL],fetch = FetchType.LAZY,orphanRemoval = true)
    List<SourceMotherAccount> sourceMotherAccounts= []

}
