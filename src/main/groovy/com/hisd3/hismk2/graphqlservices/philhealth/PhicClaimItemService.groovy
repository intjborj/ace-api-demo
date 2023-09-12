package com.hisd3.hismk2.graphqlservices.philhealth

import com.hisd3.hismk2.domain.philhealth.Claim
import com.hisd3.hismk2.domain.philhealth.ClaimItem
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
@GraphQLApi
class PhicClaimItemService extends AbstractDaoService<ClaimItem> {

    PhicClaimItemService(){
        super(ClaimItem.class);
    }

    @GraphQLQuery(name = 'claimItems')
    List<ClaimItem> claimItems(@GraphQLArgument(name = 'id') UUID id){

        //language=HQL
        String query = "select i from ClaimItem i where i.claims.id = :id"

        return createQuery(query, ["id":id]).resultList
    }

    @GraphQLQuery(name = 'claimItemsByClaimPageable')
    Page<ClaimItem> claimItemsByClaimPageable(
            @GraphQLArgument(name = 'ids') List<UUID> ids,
            @GraphQLArgument(name = 'patient') String patient,
            @GraphQLArgument(name = 'page') Integer page,
            @GraphQLArgument(name = 'pageSize') Integer pageSize
    ){
        //language=HQL
        String query = '''select i from ClaimItem i  where i.id is not null'''

        //language=HQL
        String countQuery = '''select count(i) from ClaimItem i where i.id is not null'''

        Map<String, Object> params = [:]


        if(ids){
             query += " and i.claims.id in :ids"

            countQuery += " and i.claims.id in :ids"

            params.put('ids', ids)

        }

        if(patient){
            query += ''' and (lower(i.parentCase.patient.fullName) like lower(concat('%', :patient, '%')) or 
                                   (lower(i.parentCase.patient.lastName) like lower(concat('%', :patient, '%'))
                                    or lower(i.parentCase.patient.firstName) like lower(concat('%', :patient, '%'))))'''

            countQuery += ''' and (lower(i.parentCase.patient.fullName) like lower(concat('%', :patient, '%')) or 
                                   (lower(i.parentCase.patient.lastName) like lower(concat('%', :patient, '%'))
                                    or lower(i.parentCase.patient.firstName) like lower(concat('%', :patient, '%'))))'''

            params.put('patient', patient)

        }


        return getPageable(query,countQuery, page,pageSize,params)


    }

    @GraphQLMutation(name = 'postDeleteClaimItem')
    GraphQLRetVal<ClaimItem> postDeleteClaimItem(@GraphQLArgument(name = 'id') UUID id){
        try {
            ClaimItem claimItem = findOne(id)
            if(claimItem.trackingNo){
                return new GraphQLRetVal<ClaimItem>(claimItem, false, 'Already have tracking #.')

            }else{
                delete(claimItem)
                return new GraphQLRetVal<ClaimItem>(claimItem, true, 'Successfully Delete!')
            }

        } catch(Exception e){
            return new GraphQLRetVal<ClaimItem>(new ClaimItem(), false, e.message)
        }
    }


}
