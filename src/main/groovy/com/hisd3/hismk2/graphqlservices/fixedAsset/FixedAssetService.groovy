package com.hisd3.hismk2.graphqlservices.fixedAsset

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.fixed_assets.FixedAssets
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.domain.inventory.ReceivingReport
import com.hisd3.hismk2.domain.inventory.UnitMeasurement
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.types.GraphQLResVal
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.fixedAsset.FixedAssetRepository
import com.hisd3.hismk2.repository.inventory.ItemCategoryRepository
import com.hisd3.hismk2.repository.inventory.ItemGroupRepository
import com.hisd3.hismk2.repository.inventory.ItemRepository
import com.hisd3.hismk2.repository.inventory.ReceivingReportRepository
import com.hisd3.hismk2.repository.inventory.UnitMeasurementRepository
import com.hisd3.hismk2.services.EntityObjectMapperService
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.utils.Formatter
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.apache.http.client.fluent.Form
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Component
@GraphQLApi
class FixedAssetService extends  AbstractDaoService<FixedAssets> {

    FixedAssetService() {
        super(FixedAssets.class)
    }

    private Formatter fmt;

    @Autowired
    ItemRepository itemRepository

    @Autowired
    ItemCategoryRepository itemCategoryRepository

    @Autowired
    ItemGroupRepository itemGroupRepository

    @Autowired
    UnitMeasurementRepository unitMeasurementRepository

    @Autowired
    ReceivingReportRepository receivingReportRepository

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    FixedAssetRepository fixedAssetRepository

    @Autowired
    GeneratorService generatorService

    @Autowired
    EntityObjectMapperService entityObjectMapperService

    //========================== Queries ===========================\\

    @GraphQLQuery(name="getFixedAssetsFromItems")
    Page<Item> getFixedAssetsFromItems(
            @GraphQLArgument(name="search") String search,
            @GraphQLArgument(name="category") List<UUID> category = [],
            @GraphQLArgument(name="page") Integer page,
            @GraphQLArgument(name="size") Integer size
    ){
        try{
            if(category.size() > 0)
                return itemRepository.fixedAssetsItemsWithCategoryPageable(search,category,new PageRequest(page,size, Sort.Direction.ASC,"descLong"))
            else
                return itemRepository.fixedAssetsItemsPageable(search,new PageRequest(page,size, Sort.Direction.ASC,"descLong"))
        }
        catch(ignored) {
            return Page.empty()
        }
    }


    //==================================== Mutation ========================================\\

    @GraphQLMutation( name = "upsertFixedAsset", description = "Add Fixed Asset")
    GraphQLRetVal<FixedAssets> upsertFixedAsset(
            @GraphQLArgument(name ="id") UUID id,
            @GraphQLArgument(name ="fields") Map<String, Object> fields
    ){
        try{
            if(id){
                def fixedAsset = findOne(id)
                if(fixedAsset){
                    objectMapper.updateValue(fixedAsset, fields)
                    def fixedAssets = save(fixedAsset)
                    return new GraphQLRetVal<FixedAssets>(fixedAssets, true, "Successfully Updated")
                }else{
                    return new GraphQLRetVal<FixedAssets>(new FixedAssets(), false, "No Record Found")
                }
            }else{
                def fixedAsset = rawUpsertFixedAsset(fields);
                return new GraphQLRetVal<FixedAssets>(fixedAsset, true, "Successfully Saved")
            }
        }catch(e){
            return new GraphQLRetVal<FixedAssets>(new FixedAssets(), false, e.message)
        }
    }

    def rawUpsertFixedAsset() {
        def fixedAsset = findOne("003ab1a6-a15c-4b65-8a68-f6560ca629c3" as UUID)
        fixedAsset.createdDate = Instant.now();
        return fixedAssetRepository.save(fixedAsset);
    }

    @GraphQLMutation(name = "upsertItemAsFixedAsset")
    GraphQLRetVal<Item> upsertItemAsFixedAsset(
            @GraphQLArgument(name="id") UUID id,
            @GraphQLArgument(name = "fields") Map<String,Object> fields
    ){
        try{
            if(id) {
                def item = itemRepository.findById(id).get()

                if(item && item.fixAsset) {
                    entityObjectMapperService.updateFromMap(item,fields)
                    def itemObject = itemRepository.save(item)
                    return new GraphQLRetVal<Item>(itemObject, true, 'Successfully saved.')
                }
                else
                    return new GraphQLRetVal<Item>(new Item(), true, 'No records found.')
            }
            else{
                Item item = new Item()
                entityObjectMapperService.updateFromMap(item,fields)
                item.fixAsset = true
                def itemObject = itemRepository.save(item)
                return new  GraphQLRetVal<Item>(itemObject,true,'Successfully saved.')
            }
        }
        catch (e){
            return new  GraphQLRetVal<Item>(null,false,e.message)
        }
    }

    Item updateAssetValues(Map<String,Object> fields, Item item) {
        def itemCategory = itemCategoryRepository.findById(UUID.fromString(fields.get("item_category") as String)).get();
        def itemGroup = itemGroupRepository.findById(UUID.fromString(fields.get("item_group") as String)).get();
        def unitOfPurchase = unitMeasurementRepository.findById(UUID.fromString(fields.get("unit_of_purchase") as String)).get();
        def unitOfUsage = unitMeasurementRepository.findById(UUID.fromString(fields.get("unit_of_usage") as String)).get();

        item.brand = fields.get("brand") as String;
        item.descLong = fields.get("descLong") as String;
        item.dimensions = fields.get("dimensions") as String;
        item.sku = fields.get("sku") as String;
        item.stockCode = fields.get("stockCode") as String;
        item.item_dfs = fields.get("item_dfs") as String;
        item.fixAsset = true

        item.item_category = itemCategory;
        item.item_group = itemGroup;
        item.unit_of_usage = unitOfUsage;
        item.unit_of_purchase = unitOfPurchase;

        return item;
    }

    @GraphQLMutation(name = "deleteItemFromMasterfile")
    GraphQLRetVal<Boolean> deleteItemFromMasterFile(@GraphQLArgument(name="id") UUID id){
        try{
            if(id)
                itemRepository.deleteById(id)
            else
                return new  GraphQLRetVal<Boolean>(false,false,'Invalid parameter.')

            return new  GraphQLRetVal<Boolean>(true,true,'Successfully deleted.')
        }
        catch (e){
            return new  GraphQLRetVal<Boolean>(false,false,e.message)
        }
    }


    @GraphQLQuery(name = "viewNextPOCodeByType")
    GraphQLResVal<String> viewNextPOCodeByType(
            @GraphQLArgument(name="poDate") String poDate,
            @GraphQLArgument(name="type") String type
    ){
        try{
            String year = LocalDate.parse(poDate).format(DateTimeFormatter.ofPattern("yyyy"))
            String poCode = generatorService.getNextGeneratorFeatPrefix("manual_e_${year}"){
                it ->
                    if(type.equalsIgnoreCase('FIXED_ASSETS'))
                        return "PO-FA-${year}${StringUtils.leftPad(it.toString() ,5,"0")}"
                    if(type.equalsIgnoreCase('INVENTORY'))
                        return "PO-INV-${year}${StringUtils.leftPad(it.toString() ,5,"0")}"
            }
            return new GraphQLResVal<String>(poCode,true,'Success.')
        }
        catch (e){
            return new GraphQLResVal<String>('',true,e.message)
        }
    }
}