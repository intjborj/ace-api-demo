package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.accounting.Integration
import com.hisd3.hismk2.domain.accounting.IntegrationItem
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

@Service
@GraphQLApi
class IntegrationItemServices extends  AbstractDaoService<IntegrationItem>{

    IntegrationItemServices(){
        super(IntegrationItem.class)
    }

    @GraphQLQuery(name="integrationItemsByIntegrationId")
    Page<IntegrationItem> integrationItemsByIntegrationId(
            @GraphQLArgument(name="id") UUID id,
            @GraphQLArgument(name="filter") String filter,
            @GraphQLArgument(name="page") Integer page,
            @GraphQLArgument(name="size") Integer size
    ){
        try{
            getPageable(""" Select i from IntegrationItem i where i.integration.id = :id 
                    and 
              		(
						lower(coalesce(i.sourceColumn,'')) like lower(concat('%',:filter,'%')) or
						lower(function('jsonb_extract_path_text',i.journalAccount,'subAccountName')) like lower(concat('%',:filter,'%'))
              		)  
            """,
                    """ Select count(i) from IntegrationItem i where i.integration.id = :id
                    and 
              		(
						lower(coalesce(i.sourceColumn,'')) like lower(concat('%',:filter,'%'))  or
						lower(function('jsonb_extract_path_text',i.journalAccount,'subAccountName')) like lower(concat('%',:filter,'%'))
              		) 
            """,
                    page,
                    size,
            [
                    id:id,
                    filter:filter
            ])
        }
        catch (ignored){
            return null
        }
    }
}
