package com.hisd3.hismk2.repository.fixedAsset

import com.hisd3.hismk2.domain.fixed_assets.FixedAssetTransfer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FixedAssetTransferRepository extends JpaRepository<FixedAssetTransfer, UUID> {
    @Query(value = """ Select t from FixedAssetTransfer t where t.fixedAssetItem.id = :id """)
    List<FixedAssetTransfer> getFixedAssetItemTransfer(@Param("id") UUID id)
}