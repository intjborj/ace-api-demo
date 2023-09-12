package com.hisd3.hismk2.repository.fixedAsset

import com.hisd3.hismk2.domain.fixed_assets.FixedAssetDepreciation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FixedAssetDepreciationRepository extends JpaRepository<FixedAssetDepreciation, UUID>{

    @Query(value = '''Select a from FixedAssetDepreciation a where
            a.fixedAssetItem.id = :id ''')
    List<FixedAssetDepreciation> getByFixedAssetItemId(@Param("id") UUID id)
}