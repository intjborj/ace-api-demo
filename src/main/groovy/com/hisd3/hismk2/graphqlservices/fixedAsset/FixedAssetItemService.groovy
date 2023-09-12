package com.hisd3.hismk2.graphqlservices.fixedAsset


import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.fixed_assets.FixedAssetDepreciation
import com.hisd3.hismk2.domain.fixed_assets.FixedAssetTransfer
import com.hisd3.hismk2.domain.fixed_assets.FixedAssets
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.fixedAsset.FixedAssetTransferRepository
import com.hisd3.hismk2.repository.inventory.ItemRepository
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import org.apache.tomcat.jni.Local
import org.springframework.data.domain.Example
import org.springframework.data.domain.Page
import com.hisd3.hismk2.domain.fixed_assets.FixedAssetItem
import com.hisd3.hismk2.repository.fixedAsset.FixedAssetItemRepository
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

import java.sql.Timestamp
import java.time.LocalDateTime


class BulkFixedAssetItemParams {
    UUID id
    Map<String, Object> fields
}

@Component
@GraphQLApi
class FixedAssetItemService extends AbstractDaoService<FixedAssetItem> {

    FixedAssetItemService() {
        super(FixedAssetItem.class)
    }

    @Autowired
    FixedAssetItemRepository fixedAssetItemRepository

    @Autowired
    FixedAssetTransferRepository fixedAssetTransferRepository

    @Autowired
    ItemRepository itemRepository

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    DepartmentRepository departmentRepository

    @Autowired
    FixedAssetService fixedAssetService

    @Autowired
    FixedAssetsDepreciationService fixedAssetsDepreciationService

    //===================QUERIES=======================\\

    @GraphQLQuery(name = "getFixedAssetItems", description = "Get all active fixed asset items")
    GraphQLRetVal<Page<FixedAssetItem>> getFixedAssetItems(
            @GraphQLArgument(name = "filter") String filter = '',
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "pageSize") Integer pageSize,
            @GraphQLArgument(name = "department") List<UUID> department = [],
            @GraphQLArgument(name = "status") String status = "ACTIVE"
    ) {
        Page<FixedAssetItem> fixedAssetItems = Page.empty();

        if (department.size() > 0) {
            fixedAssetItems = fixedAssetItemRepository.getFixedAssetItemsByDept(filter, status, department, PageRequest.of(page, pageSize, Sort.Direction.DESC, "item.descLong"))
        } else {
            fixedAssetItems = fixedAssetItemRepository.getFixedAssetItems(filter, status, PageRequest.of(page, pageSize, Sort.Direction.DESC, "item.descLong"))
        }

        return new GraphQLRetVal<Page<FixedAssetItem>>(fixedAssetItems, true, "")
    }

    //===================MUTATIONS=======================\\

    @GraphQLMutation(name = "transferBulkFixedAssets")
    GraphQLRetVal<Boolean> transferBulkFixedAssets(@GraphQLArgument(name = "itemIds") List<UUID> itemIds,
                                                   @GraphQLArgument(name = "departmentId") UUID departmentId) {
        try {
            itemIds.each {
                def item = fixedAssetItemRepository.findById(it).get()
                def sourceDept = item.department
                item.department = departmentRepository.findById(departmentId).get()
                fixedAssetItemRepository.save(item)

                def transfer = new FixedAssetTransfer()
                transfer.fixedAssetItem = item
                transfer.sourceDept = sourceDept
                transfer.destinationDept = departmentRepository.findById(departmentId).get()
                fixedAssetTransferRepository.save(transfer)
            }
            return new GraphQLRetVal<Boolean>(null, true, 'Success')

        }
        catch (e) {
            return new GraphQLRetVal<Boolean>(false, false, e.message)
        }
    }

    @GraphQLMutation(name = "upsertBulkFixedAssetItem", description = "Add Multiple Items")
    GraphQLRetVal<List<FixedAssetItem>> upsertBulkFixedAssetItem(
            @GraphQLArgument(name = "fields") List<Map<String, Object>> fields
    ) {
        try{
            List<FixedAssetItem> listToInsert = [];
            if (fields.size() > 0) {

                // Upsert Process:
                // 1. Receive an array of fixed-asset-items
                // 1. Create a fixed-asset parent and add the return to a `fixedAsset` variable.
                // 2. Create a record of each item <- use the newly created `fixedAsset` variable as a parent.
                // 3. Side Effect: save to fixed-asset-bulk-transfers table
                // 4. Side Effect: save to fixed-asset-bulk-depreciate table
                // then return.

                // fields : [
                //  {
                //      item: { id: " item id here " }
                //      department : { id: " department id here" }
                //      serialNo : "32131313",
                //      etc...
                //  },
                //  {
                //      item: { id: " item id here " }
                //      department : { id: " department id here" }
                //      serialNo : "32131313",
                //      etc...
                //   }
                //  ]

                FixedAssets fixedAsset = new FixedAssets()
                def newFixedAsset = fixedAssetService.save(fixedAsset)
                fields.each {
                    asset ->
                        upsertFromObjectMapper(null,asset){
                            it,status ->
                            it.item = itemRepository.findById(it.item.id).get()
                            it.department = departmentRepository.findById(it.department.id).get()
                            it.fixedAssets = newFixedAsset
                            it.status = 'ACTIVE'
                            save(it)

                            FixedAssetDepreciation fixedAssetDepreciation = new FixedAssetDepreciation()
                            fixedAssetDepreciation.fixedAssetItem = it
                            fixedAssetDepreciation.cost = it.latestCost
                            fixedAssetDepreciation.unitOfTime = it.unitOfTime
                            fixedAssetDepreciation.estSalvageValue = it.latestEstSalvageValue
                            fixedAssetDepreciation.estUsefulLife = it.latestEstUsefulLife
                            fixedAssetDepreciation.depreciationDateStart = Calendar.getInstance().getTime().toInstant()
                            fixedAssetsDepreciationService.save(fixedAssetDepreciation)

                            FixedAssetTransfer fixedAssetTransfer = new FixedAssetTransfer()
                            fixedAssetTransfer.fixedAssetItem = it
                            fixedAssetTransfer.destinationDept = it.department
                            fixedAssetTransfer.sourceDept = it.department
                            fixedAssetTransferRepository.save(fixedAssetTransfer)

                            listToInsert.push(it)
                        }
                        // Save dept to bulk-transfer-fixed-asset
                        // Save cost, eul, esv to bulk-depreciate-fixed-asset
                }
            }
            return new GraphQLRetVal<List<FixedAssetItem>>(listToInsert, true, "Successfully Saved")

        }catch(e){
            return new GraphQLRetVal<List<FixedAssetItem>>([], false, e.message)
        }
    }

    @GraphQLMutation(name = "upsertFixedAssetItem", description = "Add Fixed Asset Item")
    GraphQLRetVal<FixedAssetItem> upsertFixedAssetItem(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    ) {
        try {
            if (id) {
                def fixedAssetItem = fixedAssetItemRepository.findById(id).get()
                if (fixedAssetItem) {
                    objectMapper.updateValue(fixedAssetItem, fields)
                    def fixedAssetItems = fixedAssetItemRepository.save(fixedAssetItem)
                    return new GraphQLRetVal<FixedAssetItem>(fixedAssetItems, true, "Successfully Update")
                } else {
                    return new GraphQLRetVal<FixedAssetItem>(new FixedAssetItem(), false, "No Record Found")
                }
            } else {
                FixedAssetItem fixedAssetItem = objectMapper.convertValue(fields, FixedAssetItem)
                def fixedAssetItems = fixedAssetItemRepository.save(fixedAssetItem)
                return new GraphQLRetVal<FixedAssetItem>(fixedAssetItems, true, "Successfully saved fixed Asset Items")
            }
        } catch (e) {
            return new GraphQLRetVal<FixedAssetItem>(new FixedAssetItem(), false, e.message)
        }
    }

    @GraphQLMutation(name = "removeFixedAssetItem", description = "Delete Fixed Asset Item")
    GraphQLRetVal<Boolean> removeFixedAssetItem(
            @GraphQLArgument(name = "id") UUID id
    ) {
        try {
            if (id) {
                FixedAssetItem obj = fixedAssetItemRepository.findById(id).get()
                def removeItem = fixedAssetItemRepository.delete(obj)

                return new GraphQLRetVal<Boolean>(removeItem, true, 'Successfully Deleted')
            }
            return new GraphQLRetVal<Boolean>(false, false, "No ID Found")
        } catch (e) {
            return new GraphQLRetVal<Boolean>(false, false, e.message)
        }
    }
}
