package com.hisd3.hismk2.graphqlservices.philhealth

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.philhealth.Claim
import com.hisd3.hismk2.domain.philhealth.ClaimItem
import com.hisd3.hismk2.domain.pms.Case;
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.pms.CaseRepository
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional;


@Component
@GraphQLApi
class PhicClaimService extends AbstractDaoService<Claim> {

    PhicClaimService(){
        super(Claim.class);
    }

    @Autowired
    PhicClaimItemService phicClaimItemService

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    GeneratorService generatorService

    @Autowired
    CaseRepository caseRepository

    @GraphQLQuery(name = "claim")
    Claim claim(@GraphQLArgument(name = 'id') UUID id){
        if(id){
            findOne(id)
        }
    }

    @GraphQLQuery(name = "claimsPageable")
    Page<Claim> claimsPageable(
            @GraphQLArgument(name = 'filter') String filter,
            @GraphQLArgument(name = 'page') Integer page,
            @GraphQLArgument(name = 'size') Integer size

    ){
        // language=HQL
        String query = "select c from Claim c where lower(c.batchNo) like lower(concat('%', :filter, '%'))"

        // language=HQL
        String countQuery = "select count(c) from Claim c where lower(c.batchNo) like lower(concat('%', :filter, '%'))"

        getPageable(
                query,
                countQuery,
                page,
                size,
                ['filter': filter]
        )
    }

    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name = 'postClaim')
    GraphQLRetVal<Claim> postClaim(@GraphQLArgument(name = 'id') UUID id, @GraphQLArgument(name = 'items') List<Map<String,Object>> items){
            try {
                if(id){
                    Claim claim = findOne(id)

                    if(items){
                        items.each {
                            if(!it.get('id')){
                                ClaimItem claimItem = new ClaimItem()
                                Object aCase = it.get('case') as Object
                                if(aCase['id']){
                                    Case pCase = caseRepository.getOne(UUID.fromString(aCase['id'] as String))
                                    claimItem.parentCase = pCase
                                }
                                claimItem.claims = claim

                                phicClaimItemService.save(claimItem)
                            }
                        }
                    }

                    return  new GraphQLRetVal<Claim>(claim, true,"Successfully Edited")
                }else {
                    Claim claim = new Claim()
                    claim.batchNo = generatorService?.getNextValue(GeneratorType.CLAIM_BATCHNO, {
                        return "BATCH" + StringUtils.leftPad(it.toString(), 6, "0")
                    })

                    Claim afterSave = save(claim)

                    if(items){
                        items.each {
                            if(!it.get('id')){
                                ClaimItem claimItem = new ClaimItem()
                                Object aCase = it.get('case') as Object
                                if(aCase['id']){
                                    Case pCase = caseRepository.getOne(UUID.fromString(aCase['id'] as String))
                                    claimItem.parentCase = pCase
                                }
                                claimItem.claims = afterSave

                                phicClaimItemService.save(claimItem)
                            }
                        }
                    }

                    return  new GraphQLRetVal<Claim>(afterSave, true, "Successfully Saved")
                }
            } catch (Exception e) {
                return  new GraphQLRetVal<Claim>(new Claim(), false, e.message)

            }
    }

}
