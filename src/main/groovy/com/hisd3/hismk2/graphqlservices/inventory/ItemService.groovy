package com.hisd3.hismk2.graphqlservices.inventory

import com.hisd3.hismk2.dao.price_tier.PriceTierDetailDao
import com.hisd3.hismk2.domain.billing.PriceTierDetail
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.domain.inventory.Markup
import com.hisd3.hismk2.repository.billing.PriceTierDetailRepository
import com.hisd3.hismk2.repository.inventory.ItemRepository
import com.hisd3.hismk2.repository.inventory.MarkupRepository
import com.hisd3.hismk2.rest.dto.ItemPriceList
import com.hisd3.hismk2.rest.dto.QueryErrorException
import groovy.transform.Canonical
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.hibernate.query.NativeQuery
import org.hibernate.transform.Transformers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import javax.persistence.EntityManager
import javax.swing.text.html.parser.Entity

@Canonical
class ItemDto {
	String id
	String descLong
	BigDecimal actualUnitCost
	BigDecimal item_markup
	Boolean vatable
	Boolean active
}

@Component
@GraphQLApi
@TypeChecked
class ItemService {
	
	@Autowired
	ItemRepository itemRepository
	
	@Autowired
	MarkupRepository markupRepository
	
	@Autowired
	PriceTierDetailRepository priceTierDetailRepository
	
	@Autowired
	PriceTierDetailDao priceTierDetailDao

	@Autowired
	EntityManager entityManager
	
	@GraphQLQuery(name = "items", description = "List of Items")
	List<Item> findAll() {
		return itemRepository.findAll().sort { it.descLong }
	}
	
	@GraphQLQuery(name = "itemsFilter", description = "List of Items")
	List<Item> findAllByGroupAndCat(@GraphQLArgument(name = "group") UUID group, @GraphQLArgument(name = "category") String category) {
		List<Item> result = null
		if (group && !category) {
			result = itemRepository.itemsFilterByGroup(group).sort { it.descLong }
		} else if (group && category) {
			String[] el = category.split(",")
			List<UUID> cat = new ArrayList<UUID>()
			el.each {
				it ->
					cat.add(UUID.fromString(it))
			}
			result = itemRepository.itemsFilterByCategory(group, cat).sort { it.descLong }
		} else {
			result = itemRepository.findAll().sort { it.descLong }
		}
		return result
	}
	
	@GraphQLQuery(name = "itemsFilterPageable", description = "List of Items")
	Page<Item> findAllByGroupAndCatPageable(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "group") UUID group,
			@GraphQLArgument(name = "category") String category,
			@GraphQLArgument(name = "page") Integer page, // zero based
			@GraphQLArgument(name = "size") Integer pageSize) {
		Page<Item> result = null
		if (group && !category) {
			result = itemRepository.itemsByFilterPagedGroup(filter, group, new PageRequest(page, pageSize, Sort.Direction.ASC, "descLong"))
		} else if (group && category) {
			String[] el = category.split(",")
			List<UUID> cat = new ArrayList<UUID>()
			el.each {
				it ->
					cat.add(UUID.fromString(it))
			}

			result = itemRepository.itemsByFilterPagedCategory(filter, group, cat, new PageRequest(page, pageSize, Sort.Direction.ASC, "descLong"))
		} else {
			result = itemRepository.itemsByFilterPaged(filter, new PageRequest(page, pageSize, Sort.Direction.ASC, "descLong"))
		}
		return result
	}

	@GraphQLQuery(name = "itemsPageFilter", description = "List of Items")
	Page<Item> itemsPageFilter(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "group") UUID group,
			@GraphQLArgument(name = "category") String[] category,
			@GraphQLArgument(name = "page") Integer page, // zero based
			@GraphQLArgument(name = "size") Integer pageSize) {
		Page<Item> result = null
		if (group && !category) {
			result = itemRepository.itemsByFilterPagedGroup(filter, group, new PageRequest(page, pageSize, Sort.Direction.ASC, "descLong"))
		} else if (group && category) {
			List<UUID> cat = new ArrayList<UUID>()
			category.each {
				it ->
					cat.add(UUID.fromString(it))
			}
			result = itemRepository.itemsByFilterPagedCategory(filter, group, cat, new PageRequest(page, pageSize, Sort.Direction.ASC, "descLong"))
		} else {
			result = itemRepository.itemsByFilterPaged(filter, new PageRequest(page, pageSize, Sort.Direction.ASC, "descLong"))
		}
		return result
	}
	
	@GraphQLQuery(name = "markupList", description = "List of Markups Items")
	List<Markup> markupList(@GraphQLArgument(name = "filter") String filter, @GraphQLArgument(name = "group") UUID group, @GraphQLArgument(name = "category") String[] category) {
		if (group && !category) {
			return markupRepository.activeMarkupAndGroup(filter, group)
		} else if (group && category) {
			List<UUID> cat = new ArrayList<UUID>()
			category.each {
				it ->
					cat.add(UUID.fromString(it))
			}
			return markupRepository.activeMarkupAndCat(filter, group, cat).sort { it.descLong }
		} else {
			return markupRepository.activeMarkup(filter).sort { it.descLong }
		}
	}
	
	@GraphQLQuery(name = "markupListPageable", description = "List of Markups Items")
	Page<Markup> markupListPageable(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "pageSize") Integer pageSize
	) {
		return markupRepository.activeMarkupPageable(filter, new PageRequest(page, pageSize, Sort.Direction.ASC, "descLong"))
	}
	
	@GraphQLQuery(name = "itemsPageable", description = "List of Items")
	Page<Item> findAllPageable(@GraphQLArgument(name = "page") Integer page,
	                           @GraphQLArgument(name = "pageSize") Integer pageSize
	
	) {
		return itemRepository.findAll(PageRequest.of(page, pageSize))
	}
	
	@GraphQLQuery(name = "itemsByFilter", description = "List of Items")
	List<Item> findByFilter(@GraphQLArgument(name = "filter") String filter) {
		return itemRepository.itemsByFilter(filter).sort { it.descLong }
	}
	
	@GraphQLQuery(name = "itemsByFilterLimited", description = "List of Items Limit 10")
	List<Item> findByFilterLimited(@GraphQLArgument(name = "filter") String filter) {
		return itemRepository.itemsByFilter(filter).sort { it.descLong }
	}
	
	@GraphQLQuery(name = "medicines", description = "List of Medicines")
	List<Item> findAllMedicines() {
		return itemRepository.findAllMedicines().sort { it.descLong }
	}
	
	@GraphQLQuery(name = "filterMedicines", description = "List of Medicines")
	List<Item> filterAllMedicines(@GraphQLArgument(name = "filter") String filter) {
		return itemRepository.filterAllMedicines(filter).sort { it.descLong }
	}
	
	@GraphQLQuery(name = "supplies", description = "List of Supplies")
	List<Item> findAllSupplies() {
		return itemRepository.findAllSupplies().sort { it.descLong }
	}
	
	@GraphQLQuery(name = "nonFluidMedicines", description = "List of non-fluid Medicines")
	List<Item> findAllNonFluidMedicinesMedicines() {
		return itemRepository.findAllNonFluidMedicines().sort { it.descLong }
	}
	
	@GraphQLQuery(name = "filterNonFluidMedicines", description = "List of non-fluid Medicines")
	List<Item> filterAllNonFluidMedicinesMedicines(@GraphQLArgument(name = "filter") String filter) {
		return itemRepository.filterAllNonFluidMedicines(filter).sort { it.descLong }
	}
	
	@GraphQLQuery(name = "fluidMedicines", description = "List of fluid Medicines")
	List<Item> findAllFluidMedicines() {
		return itemRepository.findAllFluidMedicines().sort { it.descLong }
	}
	
	@GraphQLQuery(name = "filterFluidMedicines", description = "List of fluid Medicines")
	List<Item> filterAllFluidMedicines(@GraphQLArgument(name = "filter") String filter) {
		return itemRepository.filterAllFluidMedicines(filter).sort { it.descLong }
	}
	
	@GraphQLQuery(name = "gasItems", description = "List of fluid Medicines")
	List<Item> findAllGasItems() {
		return itemRepository.findAllGasItems().sort { it.descLong }
	}
	
	@GraphQLQuery(name = "filterAllGasItems", description = "List of Gas items")
	List<Item> filterAllGasItems(@GraphQLArgument(name = "filter") String filter) {
		return itemRepository.filterAllGasItems(filter).sort { it.descLong }
	}
	
	@GraphQLQuery(name = "item", description = "Get Item By Id")
	Item findById(@GraphQLArgument(name = "id") UUID id) {
		def result = new Item()
		if (id) {
			result = itemRepository.findById(id).get()
		}
		return result
	}
	
	@GraphQLQuery(name = "activeItemsFilter", description = "List of Active Items")
	List<Item> activeItems(@GraphQLArgument(name = "filter") String filter) {
		return itemRepository.activeItems(filter).sort { it.descLong }
	}
	
	@GraphQLQuery(name = "productionItems", description = "List of Production Items")
	Set<Item> productionItems() {
		return itemRepository.findAllProductionItem()
	}
	
	@GraphQLQuery(name = "itemsByFilterAndIsNotProductionPageable", description = "List of Production Items")
	Page<Item> itemsByFilterAndIsNotProductionPageable(@GraphQLArgument(name = "page") Integer page, // zero based
	                                                   @GraphQLArgument(name = "pageSize") Integer pageSize,
	                                                   @GraphQLArgument(name = "filter") String filter) {
		
		return itemRepository.itemsByFilterAndIsNotProductionPageable(filter, new PageRequest(page, pageSize))
	}
	
	@GraphQLQuery(name = "itemsByFilterOnly", description = "List of Production Items")
	Page<Item> itemsByFilterOnly(@GraphQLArgument(name = "page") Integer page, // zero based
	                             @GraphQLArgument(name = "size") Integer pageSize,
	                             @GraphQLArgument(name = "filter") String filter) {
		
		return itemRepository.itemsByFilterPaged(filter, new PageRequest(page, pageSize, Sort.Direction.ASC, "descLong"))
	}
	
	//priceTierList
	@GraphQLQuery(name = "itemPriceList", description = "List of Supplies/Medicine Price List")
	List<ItemPriceList> itemPriceList(@GraphQLArgument(name = "tierId") UUID tierId, @GraphQLArgument(name = "filter") String filter) {
		List<Markup> item = markupRepository.activeMarkup(filter).sort { it.descLong }
		PriceTierDetail tierDetail = priceTierDetailRepository.getPriceTierDetailsById(tierId)
		List<ItemPriceList> returnData = []
		
		if (tierDetail) {
			item.each {
				it ->
					BigDecimal initialPrice = it.actualUnitCost + it.actualUnitCost * (it.item_markup / 100)
					if (it.isMedicine) {
						if (tierDetail.isVatable) {
							BigDecimal vat = initialPrice * (tierDetail.vatRate / 100)
							BigDecimal sellingPriceVat = initialPrice + vat
							returnData.add(new ItemPriceList(it, vat, sellingPriceVat))
						} else {
							returnData.add(new ItemPriceList(it, 0.00, initialPrice))
						}
					} else {
						if (tierDetail.isVatable) {
							BigDecimal vat = initialPrice * (tierDetail.vatRate / 100)
							BigDecimal sellingPriceVat = initialPrice + vat
							returnData.add(new ItemPriceList(it, vat, sellingPriceVat))
						} else {
							returnData.add(new ItemPriceList(it, 0.00, initialPrice))
						}
					}
			}
		}
		return returnData
	}
	
	//mutation //
	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "massUpdateMarkup", description = "insert BEG")
	Item massUpdateMarkup(
			@GraphQLArgument(name = "groupId") UUID groupId,
			@GraphQLArgument(name = "category") List<UUID> category,
			@GraphQLArgument(name = "markup") BigDecimal markup
	) {
		Item update = new Item()
		//get items by group
		def items = itemRepository.itemsFilterByGroup(groupId)
		if(category){
			items = itemRepository.itemsFilterByCategory(groupId, category)
		}

		try {
			if (items) {
				items.each {
					it ->
						def data = itemRepository.findById(it.id).get()
						if (!data.markupLock) {
							data.item_markup = markup
							update = itemRepository.save(data)
						}
				}
			}
		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}
		return update
	}


 	List<ItemDto> itemsByCostRange(
			BigDecimal fromUnitCost,
			BigDecimal toUnitCost,
			Boolean medicine,
			String filter
	){
		return entityManager.createNativeQuery("""
				select 
						cast(i.id as text) as id,
						desc_long as "descLong",
						base_price as "actualUnitCost",
						item_markup,
						vatable,
						active
				from inventory.item i 
				where 
			 	(lower(i.desc_long) like lower(concat('%',:filter,'%')) or
			  	lower(i.sku) like lower(concat('%',:filter,'%'))) and
				i.is_medicine = :medicine
				and i.base_price between :from and :to
				order by i.desc_long
		""")
				.setParameter('from',fromUnitCost)
				.setParameter('to',toUnitCost)
				.setParameter('medicine',medicine)
				.setParameter('filter',filter)
				.unwrap(NativeQuery.class)
				.setResultTransformer(Transformers.aliasToBean(ItemDto.class))
				.getResultList();
	}

	List<ItemDto> itemsByCostRangeMedicalSupplies(
			BigDecimal fromUnitCost,
			BigDecimal toUnitCost,
			String filter
	){
		return entityManager.createNativeQuery("""
				select 
						cast(i.id as text) as id,
						desc_long as "descLong",
						base_price as "actualUnitCost",
						item_markup,
						vatable,
						active
				from inventory.item as i 
				left join inventory.item_categories as c on c.id = i.item_category 
				where
				(lower(i.desc_long) like lower(concat('%',:filter,'%')) or
			  	lower(i.sku) like lower(concat('%',:filter,'%'))) and
				(i.is_medicine = false) and (i.base_price BETWEEN :from AND :to)
				and (c.category_description = 'MEDICAL SUPPLY')
				order by i.desc_long
		""")
				.setParameter('from',fromUnitCost)
				.setParameter('to',toUnitCost)
				.setParameter('filter',filter)
				.unwrap(NativeQuery.class)
				.setResultTransformer(Transformers.aliasToBean(ItemDto.class))
				.getResultList();
	}

	List<ItemDto> itemsByCostRange(
			BigDecimal fromUnitCost,
			BigDecimal toUnitCost,
			String filter
	){
		return entityManager.createNativeQuery("""
				select 
						cast(i.id as text) as id,
						desc_long as "descLong",
						base_price as "actualUnitCost",
						item_markup,
						vatable,
						active
				from inventory.item i 
			 	where
			 	(lower(i.desc_long) like lower(concat('%',:filter,'%')) or
			  	lower(i.sku) like lower(concat('%',:filter,'%'))) and
			 	i.base_price between :from and :to
			 	order by i.desc_long
		""")
				.setParameter('from',fromUnitCost)
				.setParameter('to',toUnitCost)
				.setParameter('filter',filter)
				.unwrap(NativeQuery.class)
				.setResultTransformer(Transformers.aliasToBean(ItemDto.class))
				.getResultList();
	}


	List<ItemDto> allItemsByType(
			Boolean medicine,
			String filter
	){
		return entityManager.createNativeQuery("""
				select 
						cast(i.id as text) as id,
						desc_long as "descLong",
						base_price as "actualUnitCost",
						item_markup,
						vatable,
						active
				from inventory.item i 
			 	where
			 	(lower(i.desc_long) like lower(concat('%',:filter,'%')) or
			  	lower(i.sku) like lower(concat('%',:filter,'%'))) and
			 	i.is_medicine = :medicine
			 	order by i.desc_long
		""")
				.setParameter('medicine',medicine)
				.setParameter('filter',filter)
				.unwrap(NativeQuery.class)
				.setResultTransformer(Transformers.aliasToBean(ItemDto.class))
				.getResultList();
	}

	List<ItemDto> allItemsMedicalSuppliesByType(
			String filter
	){
		return entityManager.createNativeQuery("""
				select 
						cast(i.id as text) as id,
						desc_long as "descLong",
						base_price as "actualUnitCost",
						item_markup,
						vatable,
						active
				from inventory.item i 
				left join inventory.item_categories as c on c.id = i.item_category 
			 	where (i.is_medicine = false) and
			 	(lower(i.desc_long) like lower(concat('%',:filter,'%')) or
			  	lower(i.sku) like lower(concat('%',:filter,'%'))) and
				and (c.category_description = 'MEDICAL SUPPLY')
				order by i.desc_long
		""")
				.setParameter('filter',filter)
				.unwrap(NativeQuery.class)
				.setResultTransformer(Transformers.aliasToBean(ItemDto.class))
				.getResultList();
	}

	List<ItemDto> allItemsNativeQuery(
			String filter
	){
		return entityManager.createNativeQuery("""
				select 
						cast(i.id as text) as id,
						desc_long as "descLong",
						base_price as "actualUnitCost",
						item_markup,
						vatable,
						active
				from inventory.item i
				where
				(lower(i.desc_long) like lower(concat('%',:filter,'%')) or
			  	lower(i.sku) like lower(concat('%',:filter,'%')))
				order by i.desc_long
		""")
				.setParameter('filter',filter)
				.unwrap(NativeQuery.class)
				.setResultTransformer(Transformers.aliasToBean(ItemDto.class))
				.getResultList();
	}

}
