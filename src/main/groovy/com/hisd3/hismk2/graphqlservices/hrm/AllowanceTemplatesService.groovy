package com.hisd3.hismk2.graphqlservices.hrm

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.hrm.Allowance
import com.hisd3.hismk2.domain.hrm.AllowanceTemplate
import com.hisd3.hismk2.domain.hrm.AllowanceTemplateItem
import com.hisd3.hismk2.domain.hrm.AllowanceTemplateItemsId
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.types.GraphQLResVal
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.hrm.AllowanceRepository
import com.hisd3.hismk2.repository.hrm.AllowanceTemplateItemsRepository
import com.hisd3.hismk2.repository.hrm.AllowanceTemplatesRepository
import com.hisd3.hismk2.rest.dto.QueryErrorException
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import java.util.stream.Collectors

@Component
@GraphQLApi
class AllowanceTemplatesService extends AbstractDaoService<AllowanceTemplate> {

    AllowanceTemplatesService() {
        super(AllowanceTemplate.class)
    }

    @Autowired
    AllowanceTemplatesRepository allowanceTemplatesRepository

    @Autowired
    AllowanceTemplateItemsRepository allowanceTemplateItemsRepository

    @Autowired
    AllowanceRepository allowanceRepository

    @Autowired
    ObjectMapper objectMapper

    @GraphQLMutation(name = "deleteAllowanceTemplates")
    GraphQLResVal<Boolean> deleteAllowanceTemplates(
            @GraphQLArgument(name = "id") UUID id
    ) {
        try {
            if (id) {
                allowanceTemplatesRepository.deleteById(id)
                return new GraphQLResVal<Boolean>(true, true, 'Successfully Deleted Allowance Template.')
            }
            return new GraphQLResVal<Boolean>(false, false, 'Failed to Delete Allowance Template')
        } catch (e) {
            return new GraphQLResVal<Boolean>(false, false, e.message)
        }
    }


    @GraphQLQuery(name = 'getActiveAllowanceTemplates', description = 'list of all active allowance templates where total > 1')
    List<AllowanceTemplate> getActiveAllowanceTemplates() {
        return allowanceTemplatesRepository.findActiveWithTotal()
    }


    @GraphQLQuery(name = 'getAllowanceTemplatesByPagination', description = 'list of all Allowance Templates with pagination')
    Page<AllowanceTemplate> getAllowanceTemplatesByPagination(
            @GraphQLArgument(name = "pageSize") Integer pageSize,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "filter") String filter,
            @GraphQLArgument(name = "showActive") Boolean showActive


    ) {

        String query = "select c from AllowanceTemplate c where (lower(c.name) like lower(concat('%',:filter,'%')) )"
        String countQuery = "select count(c) from AllowanceTemplate c where (lower(c.name) like lower(concat('%',:filter,'%')))"
        Map<String, Object> params = new HashMap<>()
        params.put('filter', filter)

        if (showActive == true) {
            query += ''' and c.active = TRUE'''
            countQuery += ''' and c.active = TRUE'''

        }
        return getPageable(query, countQuery, page, pageSize, params)


    }

    @GraphQLQuery(name = 'getOneAllowanceTemplate', description = 'get one allowance Template')
    GraphQLResVal<AllowanceTemplate> getOneAllowanceTemplate(
            @GraphQLArgument(name = "id") UUID id
    ) {
        try {
            if (id) {
                AllowanceTemplate allowanceTemplate = allowanceTemplatesRepository.findById(id).get()
                allowanceTemplate.templates = allowanceTemplate.templates.sort { it.allowance.name }
                return new GraphQLResVal<AllowanceTemplate>(allowanceTemplate, true, 'Successfully Fetched Allowance Template.')
            } else
                return new GraphQLResVal<AllowanceTemplate>(new AllowanceTemplate(), true, 'Failed to Fetch Allowance Template.')
        }
        catch (e) {
            return new GraphQLResVal<AllowanceTemplate>(new AllowanceTemplate(), false, e.message)
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name = "upsertAllowanceTemplate")
    GraphQLRetVal<AllowanceTemplate> upsertAllowanceTemplate(
            @GraphQLArgument(name = "template_id") UUID templateId,
            @GraphQLArgument(name = "template_fields") Map<String, Object> templateFields,
            @GraphQLArgument(name = "item_fields") ArrayList<Map<String, Object>> itemFields
    ) {
        try {
            if (templateId) {
                def allowanceTemplate = allowanceTemplatesRepository.findById(templateId).get()

                if (allowanceTemplate) {
                    objectMapper.updateValue(allowanceTemplate, templateFields)
                    def newUpdated = allowanceTemplatesRepository.save(allowanceTemplate)
                    if (itemFields) {
                        List<UUID> newItemIds = []
                        List<AllowanceTemplateItem> itemsToRemove = []
                        itemFields.each {
                            newItemIds.add(UUID.fromString(it['allowanceId'] as String))
                        }
                        allowanceTemplate.templates.each {
                            int index = newItemIds.indexOf(it.allowance.id)
                            if (index < 0) {
                                itemsToRemove.add(it)
                            }
                        }
                        allowanceTemplate.templates.removeAll(itemsToRemove)

                        itemFields.each {
                            UUID allowanceId = UUID.fromString(it.get('allowanceId') as String)
                            def item = findById(templateId, allowanceId)
                            if (item) {
                                objectMapper.updateValue(item, it)
                                allowanceTemplateItemsRepository.save(item)
                            } else {
                                Allowance allowance = allowanceRepository.findById(allowanceId).get()
                                if (allowance) {
                                    AllowanceTemplateItem newItem = new AllowanceTemplateItem(newUpdated, allowance)
                                    objectMapper.updateValue(newItem, it)
                                    allowanceTemplateItemsRepository.save(newItem)
                                } else {
                                    return new GraphQLRetVal<AllowanceTemplate>(newUpdated, false, 'Failed to Update Allowance Template, Allowance not found.')
                                }
                            }
                        }
                    }
                    return new GraphQLRetVal<AllowanceTemplate>(newUpdated, true, 'Successfully Updated Allowance Template.')
                } else
                    return new GraphQLRetVal<AllowanceTemplate>(new AllowanceTemplate(), true, 'Failed to Update Allowance Template.')
            } else {
                AllowanceTemplate template = new AllowanceTemplate()
                objectMapper.updateValue(template, templateFields)
                template = allowanceTemplatesRepository.save(template)
                ArrayList<AllowanceTemplateItem> itemList = new ArrayList<AllowanceTemplateItem>()

                itemFields.each {
                    UUID id = UUID.fromString(it.get('allowanceId') as String)
                    Allowance allowance = allowanceRepository.findById(id).get()
                    if (allowance) {
                        AllowanceTemplateItem item = new AllowanceTemplateItem(template, allowance)
                        objectMapper.updateValue(item, it)

                        itemList.add(item)
                    } else {
                        return new GraphQLRetVal<AllowanceTemplate>(template, false, 'Failed to create Allowance Template item, Allowance not found.')
                    }
                }
                allowanceTemplateItemsRepository.saveAll(itemList)
                return new GraphQLRetVal<AllowanceTemplate>(template, true, 'Successfully created Allowance Template and Allowance Template Item.')
            }
        }
        catch (e) {
            return new GraphQLRetVal<AllowanceTemplate>(new AllowanceTemplate(), false, e.message)
        }
    }

    @GraphQLQuery(name = 'getOneTemplateItem', description = 'list of all Allowance Templates with pagination')
    AllowanceTemplateItem getOneTemplateItem(
            @GraphQLArgument(name = "templateId") UUID templateId,
            @GraphQLArgument(name = "allowanceId") UUID allowanceId
    ) {
        def itemId = new AllowanceTemplateItemsId(templateId, allowanceId)
        def item = allowanceTemplateItemsRepository.findById(itemId).get() ?: null
        return item
    }


    AllowanceTemplateItem findById(UUID templateId, UUID allowanceId) {
        def itemId = new AllowanceTemplateItemsId(templateId, allowanceId)
        try {
            def item = allowanceTemplateItemsRepository.findById(itemId).get() ?: null
        } catch (e) {
            return null
        }
    }
}