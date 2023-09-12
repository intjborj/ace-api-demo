package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.Inventory
import com.hisd3.hismk2.domain.inventory.InventoryLedger
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface InventoryLedgerRepository extends JpaRepository<InventoryLedger, UUID> {
	
	@Query(value = "Select inv from Inventory inv where inv.depId = :departmentid and inv.itemId = :itemId")
	Inventory getOnHandByItem(@Param("departmentid") UUID departmentid, @Param("itemId") UUID itemId)

	@Query(value = "Select inv from Inventory inv where inv.depId = :departmentid and inv.sku = :barcode")
	List<Inventory> getItemBarcode(@Param("departmentid") UUID departmentid, @Param("barcode") String barcode)
	
	@Query(value = "Select inv from InventoryLedger inv where inv.ledgerUnitCost <= 0")
	List<InventoryLedger> getInventoryLedgerCost0()

	@Query(value = "Select inv from InventoryLedger inv where inv.referenceNo = :referenceNo")
	List<InventoryLedger> getLedgerByRefNo(@Param("referenceNo") String referenceNo)

	@Query(value = "Select inv from InventoryLedger inv where inv.id = :id")
	InventoryLedger getById(@Param("id") UUID id)

	@Query(value = "Select inv from InventoryLedger inv where inv.item.id = :itemId")
	List<InventoryLedger> getInvLedgerItemsPerId(@Param("itemId") UUID itemId)
}
