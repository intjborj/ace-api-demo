package com.hisd3.hismk2.graphqlservices.fixedAsset

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.fixed_assets.FixedAssetDepreciation
import com.hisd3.hismk2.domain.fixed_assets.FixedAssetItem
import com.hisd3.hismk2.graphqlservices.types.GraphQLResVal
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.fixedAsset.FixedAssetDepreciationRepository
import com.hisd3.hismk2.repository.fixedAsset.FixedAssetItemRepository
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService


@Component
@GraphQLApi
class FixedAssetsDepreciationService extends AbstractDaoService<FixedAssetDepreciation> {

    FixedAssetsDepreciationService(){
       super(FixedAssetDepreciation.class)
   }
    @Autowired
    ObjectMapper objectMapper

    @Autowired
    FixedAssetDepreciationRepository fixedAssetDepreciationRepository

    @Autowired
    FixedAssetItemService fixedAssetItemService


    //====================== Queries =========================\\
    @GraphQLQuery(name = "getFixedAssetDepreciation", description = "Get depreciation of a fixed item.")
    GraphQLRetVal <List<FixedAssetDepreciation>> getFixedAssetDepreciation(
            @GraphQLArgument(name = "id") UUID id
    ){
        try{
            List<FixedAssetDepreciation> depreciation = fixedAssetDepreciationRepository.getByFixedAssetItemId(id).sort{it.createdDate}
            return new GraphQLRetVal <List<FixedAssetDepreciation>>(depreciation, true, "Successfully")
        }catch(e){
            return new GraphQLRetVal <List<FixedAssetDepreciation>>(null, false, e.message)
        }
    }

    //====================== Mutations =========================\\
    @GraphQLMutation(name = "upsertFixedAssetDepreciation", description = "Add Fixed Asset Depreciation")
    GraphQLResVal<FixedAssetDepreciation> upsertFixedAssetDepreciation(
            @GraphQLArgument(name ="id") UUID id,
            @GraphQLArgument(name ="fields") Map<String, Object> fields
    ){
        try{
            // fields :
            //  {
            //      fixedAssetItem: { id: " fixedAssetItem id here " }
            //      etc...
            //  },
            if(fields){
                upsertFromObjectMapper(id,fields){
                    it,forInsert ->
                        FixedAssetItem fixedAssetItem = fixedAssetItemService.findOne(it.fixedAssetItem.id)
                        it.fixedAssetItem = fixedAssetItem
                        save(it)
                        if(forInsert)
                            return new GraphQLResVal<FixedAssetDepreciation>(it, true, "Successfully Saved")
                        else
                            return new GraphQLResVal<FixedAssetDepreciation>(it, true, "Successfully Updated")
                }
            }
            return new GraphQLResVal<FixedAssetDepreciation>(new FixedAssetDepreciation(), false, "No Record Found")

        }catch(e){
            return new GraphQLResVal<FixedAssetDepreciation>(new FixedAssetDepreciation(), false, e.message)
        }
    }

    @GraphQLMutation(name = "removeFixedDepreciation")
    GraphQLRetVal<Boolean> removeFixedDepreciation(
            @GraphQLArgument(name ="id") UUID id
    ) {
        try {
            if (id) {
                FixedAssetDepreciation obj = findOne(id)
                def removeDepreciation = delete(obj)

                return new GraphQLRetVal<Boolean>(removeDepreciation, true, 'Successfully Deleted')
            }
            return new GraphQLRetVal<Boolean>(false, false, "No ID Found")
        } catch (e) {
            return new GraphQLRetVal<Boolean>(false, false, e.message)
        }
    }

    @GraphQLMutation(name ="depreciateBulkFixedAssets")
    GraphQLRetVal<List<FixedAssetDepreciation>>depreciateBulkFixedAssets(
            @GraphQLArgument(name ="ids") List<UUID> ids,
            @GraphQLArgument(name ="fields") Map<String, Object> fields

    ){
        List<FixedAssetDepreciation> list = []
        try{
            if(ids.size() > 0){
                ids.each {
                    id ->
                        fields['fixedAssetItem'] = fixedAssetItemService.findOne(id)
                        def result = upsertFixedAssetDepreciation(null,fields)
                        list.push(result.response)
                }
                return new GraphQLRetVal<List<FixedAssetDepreciation>>(list, true, "Depreciation added successfully.")
            }
            return  new GraphQLRetVal<List<FixedAssetDepreciation>>(list, true, "No data.")
        }catch(e){
            return new GraphQLRetVal<List<FixedAssetDepreciation>>(list, false, e.message)
        }
    }

}
