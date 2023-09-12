package com.hisd3.hismk2.repository.ancillary

import com.hisd3.hismk2.domain.ancillary.PackageContent
import com.hisd3.hismk2.domain.ancillary.PanelContent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PackageContentRepository extends JpaRepository<PackageContent, UUID> {

    @Query(
            value = """ select c from PackageContent c where c.parent.id = :id"""
    )
    List<PackageContent> findByContentParentService(@Param("id") UUID id)


}
