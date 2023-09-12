package com.hisd3.hismk2.graphqlservices.fixedAsset

import com.hisd3.hismk2.domain.fixed_assets.FixedAssetCategory
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

@Component
@GraphQLApi
class FixedAssetCategoryService extends  AbstractDaoService<FixedAssetCategory>{

    FixedAssetCategoryService(){
        super(FixedAssetCategory.class)
    }


    @GraphQLQuery(name="fixedAssetCategoryPageable")
    Page<FixedAssetCategory> fixedAssetCategoryPageable(
            @GraphQLArgument(name="filter") String filter = '',
            @GraphQLArgument(name="page") Integer page = 0,
            @GraphQLArgument(name="size") Integer size = 10
    ){
        getPageable(
                """
              	Select c from FixedAssetCategory c where c.isActive = true
              	and 
              		(
						lower(c.categoryDescription) like lower(concat('%',:filter,'%')) or 
						lower(c.categoryCode) like lower(concat('%',:filter,'%'))
              		) 
				""",
                """
			 	Select count(c) from FixedAssetCategory c where c.isActive = true
			 	and 
              		(
						lower(c.categoryDescription) like lower(concat('%',:filter,'%')) or 
						lower(c.categoryCode) like lower(concat('%',:filter,'%'))
              		)
				""",
                page,
                size,
                [
                        filter: filter,
                ]
        )
    }
}
