package com.hisd3.hismk2.repository.pms

import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.pms.Transfer
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

import java.time.LocalDateTime

interface TransferRepository extends JpaRepository<Transfer, UUID> {
	
	@Query(value = "Select t from Transfer t order by t.entryDateTime")
	List<Transfer> searchTransfers(@Param("filter") String filter)
	
	@Query(value = '''Select t from Transfer t where
            (lower(t.parentCase.patient.lastName) like concat('%',:filter,'%') or
            lower(t.parentCase.patient.firstName) like concat('%',:filter,'%') or
            lower(t.parentCase.patient.middleName) like concat('%',:filter,'%')) and
            (t.voided = false or t.voided is null or t.voided is empty)
            ''')
	List<Transfer> searchTransfersByPatient(@Param("filter") String filter)
	
	@Query(value = "Select t from Transfer t where (t.voided is empty or t.voided is null) or t.voided = false order by t.entryDateTime")
	List<Transfer> getAllTransfers()
	
	@Query(value = "Select t from Transfer t where t.parentCase.id = :id order by t.lastModifiedDate")
	List<Transfer> getTransfersByCase(@Param("id") UUID id)

	@Query(value = "Select t from Transfer t where t.parentCase.id = :id")
	List<Transfer> getTransfersByCase(@Param("id") UUID id, Sort sort)
	
	@Query(value = "Select t from Transfer t where t.parentCase.id = :id and t.room is not null order by t.lastModifiedDate")
	List<Transfer> getTransfersByCaseWithRooms(@Param("id") UUID id)
	
	@Query(value = "Select t from Transfer t where t.parentCase.id = :caseId and t.active = true order by t.lastModifiedDate")
	List<Transfer> getCurrentActiveTransfer(@Param("caseId") UUID caseId)
	
	@Query(value = "Select t from Transfer t where t.id = :transferId and t.active = :value order by t.lastModifiedDate")
	List<Transfer> getTransferByIdAndActiveStatus(@Param("transferId") UUID transferId, @Param("value") String value)
	
	@Query(value = "Select t from Transfer t where t.entryDateTime between :fromDate and :toDate and t.registryType = :registryType order by t.entryDateTime")
	List<Transfer> getTransfersByDateRange(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate, @Param("registryType") String registryType)
	
	@Query(value = "Select distinct t.department from Transfer t where t.parentCase.id = :id")
	List<Department> transfersDepartmentsByCase(@Param("id") UUID id)

	@Query(value = '''Select t from Transfer t where
            ((lower(t.parentCase.patient.lastName) like concat('%',:filter,'%') or
            lower(t.parentCase.patient.firstName) like concat('%',:filter,'%') or
            lower(t.parentCase.patient.middleName) like concat('%',:filter,'%')) and
            (t.voided = false or t.voided is null or t.voided is empty)) and EXTRACT(YEAR FROM t.parentCase.entryDateTime) = :year
            ''')
	List<Transfer> getTransfersByFilter(@Param("filter") String filter, @Param("year") Integer  year)
}
