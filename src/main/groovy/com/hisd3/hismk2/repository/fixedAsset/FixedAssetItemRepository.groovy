package com.hisd3.hismk2.repository.fixedAsset

import com.hisd3.hismk2.domain.fixed_assets.FixedAssetItem
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FixedAssetItemRepository extends  JpaRepository<FixedAssetItem, UUID> {

    @Query(value = """ 
            Select i from FixedAssetItem i where 
            (lower(i.item.descLong) like lower(concat('%',:filter,'%')) or lower(i.item.brand) like lower(concat('%',:filter,'%')))
            AND i.status = :status       
           """,
            countQuery = """
            Select count(i) from FixedAssetItem i where
            (lower(i.item.descLong) like lower(concat('%',:filter,'%')) or lower(i.item.brand) like lower(concat('%',:filter,'%')))
            AND i.status = :status                
            """
    )
    Page<FixedAssetItem> getFixedAssetItems(@Param("filter") String filter, @Param("status") String status, Pageable pageable )

    @Query(value = """ 
            Select i from FixedAssetItem i where 
            (lower(i.item.descLong) like lower(concat('%',:filter,'%')) or lower(i.item.brand) like lower(concat('%',:filter,'%')))
            AND i.status = :status       
            AND i.department.id in (:department)
           """,
            countQuery = """
            Select count(i) from FixedAssetItem i where
            (lower(i.item.descLong) like lower(concat('%',:filter,'%')) or lower(i.item.brand) like lower(concat('%',:filter,'%')))
            AND i.status = :status                
            AND i.department.id in (:department)
            """
    )
    Page<FixedAssetItem> getFixedAssetItemsByDept(@Param("filter") String filter, @Param("status") String status,@Param("department") List<UUID> department, Pageable pageable )




}