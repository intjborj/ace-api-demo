package com.hisd3.hismk2.repository.fixedAsset

import com.hisd3.hismk2.domain.fixed_assets.FixedAssets
import org.springframework.data.jpa.repository.JpaRepository

interface FixedAssetRepository extends JpaRepository<FixedAssets, UUID> {

}