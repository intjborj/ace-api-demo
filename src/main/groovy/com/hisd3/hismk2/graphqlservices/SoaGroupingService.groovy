package com.hisd3.hismk2.graphqlservices


import com.hisd3.hismk2.domain.billing.SoaGrouping
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.billing.BillingItemServices
import com.hisd3.hismk2.graphqlservices.billing.BillingService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@GraphQLApi
class SoaGroupingService  extends AbstractDaoService<SoaGrouping> {
    SoaGroupingService() {
        super(SoaGrouping.class)
    }
    @Autowired
    BillingService billingService
    @Autowired
    BillingItemServices billingItemServices

    @GraphQLQuery(name = "getSoaGroupingsById")
     SoaGrouping getSoaGroupingsById(@GraphQLArgument(name = "groupId") UUID groupId){

        return findOne(groupId)
    }

    @GraphQLQuery(name = "getSoaGroupingsbyBillingId")
    List<SoaGrouping> getSoaGroupingsbyBillingId(@GraphQLArgument(name = "billingId") UUID billingId){

        createQuery("from SoaGrouping sg where sg.billing=:billing",[
                billing: billingService.findOne(billingId)
        ]
        ).resultList
    }
    @GraphQLMutation(name = "addSoaGroup")
    Boolean addSoaGroup(
            @GraphQLArgument(name = "billingId") UUID billingId,
            @GraphQLArgument(name = "name") String name
    ) {
        def newGroupName = new SoaGrouping()
        newGroupName.billing = billingService.findOne(billingId)
        newGroupName.groupName = StringUtils.upperCase(name)
        save(newGroupName)
        true
    }

    @GraphQLMutation(name = "deleteSoaGroup")
    Boolean deleteSoaGroup(
            @GraphQLArgument(name = "soagroupingId") UUID soagroupingId
    ) {
        def group = findOne(soagroupingId)
        delete(group)
        true
    }



    @GraphQLMutation(name = "addSoaGroupItem")
    Boolean addSoaGroupItem(
            @GraphQLArgument(name = "billingItemIds") List<UUID> billingItemIds,
            @GraphQLArgument(name = "soagroupingId") UUID soagroupingId
    ) {
        def group = findOne(soagroupingId)
        def bis = billingItemServices.billingItemByIds(billingItemIds)

        bis.each { bi->
            bi.soaGrouping = group
            billingItemServices.save(bi)
        }

        true
    }

    @GraphQLMutation(name = "removeSoaGroupItem")
    Boolean removeSoaGroupItem(
            @GraphQLArgument(name = "billingItemId") UUID billingItemId
    ) {

        def bi = billingItemServices.findOne(billingItemId)
        bi.soaGrouping = null
        billingItemServices.save(bi)
        true
    }

}
