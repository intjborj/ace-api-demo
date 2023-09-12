package com.hisd3.hismk2.graphqlservices.fixedAsset

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.fixed_assets.FixedAssetItem
import com.hisd3.hismk2.domain.fixed_assets.FixedAssetTransfer
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.fixedAsset.FixedAssetTransferRepository
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
@GraphQLApi
class FixedAssetTransferService extends AbstractDaoService<FixedAssetTransfer> {

    FixedAssetTransferService(){
        super(FixedAssetTransfer.class)
    }

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    FixedAssetTransferRepository fixedAssetTransferRepository

    @Autowired
    DepartmentRepository departmentRepository

    @Autowired
    FixedAssetItemService fixedAssetItemService

    //===================== Queries ========================\\
    @GraphQLQuery(name = "getFixedAssetTransfer", description = "get fixed asset transfer" )
    GraphQLRetVal <List<FixedAssetTransfer>> getFixedAssetTransfer(
            @GraphQLArgument(name = "id") UUID id
    ){
        try{
            List<FixedAssetTransfer> transfers = fixedAssetTransferRepository.getFixedAssetItemTransfer(id).sort{it.createdDate}
            return new GraphQLRetVal<List<FixedAssetTransfer>>(transfers, true, "Successfully")
        }catch(e){
            return new GraphQLRetVal<List<FixedAssetTransfer>>(null, false, e.message)
        }
    }


    //===================== Mutations =========================\\

    @GraphQLMutation(name = "upsertFixedAssetTransfer", description = "Add Fixed Asset Transfer")
    GraphQLRetVal<FixedAssetTransfer> upsertFixedAssetTransfer(
            @GraphQLArgument(name ="id") UUID id,
            @GraphQLArgument(name ="fields") Map<String, Object> fields
    ){
       try{
           // fields :
           //  {
           //      fixedAssetItem: { id: " fixedAssetItem id here " }
           //      etc...
           //  },
           if(fields) {
               upsertFromObjectMapper(id, fields) {
                   it, forInsert ->
                       Department destination = departmentRepository.findById(it.destinationDept.id).get()
                       Department source = departmentRepository.findById(it.sourceDept.id).get()

                       FixedAssetItem fixedAssetItem = fixedAssetItemService.findOne(it.fixedAssetItem.id)
                       fixedAssetItem.department = destination
                       fixedAssetItemService.save(fixedAssetItem)

                       it.destinationDept = destination
                       it.sourceDept = source
                       save(it)

                       if (forInsert)
                           return new GraphQLRetVal<FixedAssetTransfer>(it, true, "Successfully Saved")
                       else
                           return new GraphQLRetVal<FixedAssetTransfer>(it, true, "Successfully Updated")

               }
           }
           return new GraphQLRetVal<FixedAssetTransfer>(new FixedAssetTransfer(), false, "No Record Found")

       }catch(e){
            return new GraphQLRetVal<FixedAssetTransfer>(new FixedAssetTransfer(), false, e.message)
       }
    }

    @GraphQLMutation(name = "removeFixedTransfer", description = "Delete Fixed Asset Transfer")
    GraphQLRetVal<Boolean> removeFixedTransfer(
            @GraphQLArgument(name ="id") UUID id
    ) {
        try {
            if (id) {
                FixedAssetTransfer obj = findOne(id)
                def removeTransfer = delete(obj)

                return new GraphQLRetVal<Boolean>(removeTransfer, true, 'Successfully Deleted')
            }
            return new GraphQLRetVal<Boolean>(false, false, "No ID Found")
        } catch (e) {
            return new GraphQLRetVal<Boolean>(false, false, e.message)
        }
    }

}
