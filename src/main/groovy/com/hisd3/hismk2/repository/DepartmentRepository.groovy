package com.hisd3.hismk2.repository

import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.hrm.DepartmentSchedule
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface DepartmentRepository extends JpaRepository<Department, UUID> {

    @Query(value = "Select department from Department department where upper(department.departmentCode) = upper(:departmentCode)")
    Department findOneByDepartmentCode(@Param("departmentCode") String departmentCode)

    @Query(value = "Select department from Department department where upper(department.revenueTag) = upper(:revenueTag)")
    Department findOneByRevenueTag(@Param("revenueTag") String revenueTag)

    @Query(value = "Select department from Department department where upper(department.departmentName) = upper(:departmentName)")
    Department findOneByDepartmentName(@Param("departmentName") String departmentName)

/*

	@Query(value = '''Select department from Department department where upper(department.groupCategory) like upper(concat('%',:category,'%')) and
           ( upper(department.departmentName) like upper(concat('%',:filter,'%')) or
            upper(department.departmentCode) like upper(concat('%',:filter,'%')) ) ''')

	List<Department> departmentsByFilter(@Param("filter") String filter,@Param("category")String category)
*/

    @Query(value = '''Select department from Department department where
			upper(department.departmentName) like upper(concat('%',:filter,'%')) or
			upper(department.groupCategory) like upper(concat('%',:filter,'%')) or
            upper(department.departmentCode) like upper(concat('%',:filter,'%'))  ''')

    List<Department> departmentsByFilter(@Param("filter") String filter)

    @Query(value = '''Select department from Department department where
			upper(department.departmentName) like upper(concat('%',:filter,'%')) or
			upper(department.groupCategory) like upper(concat('%',:filter,'%')) or
            upper(department.departmentCode) like upper(concat('%',:filter,'%'))  ''')

    Page<Department> departmentPage(@Param("filter") String filter, Pageable pageable)

    @Query(value = '''Select department from Department department where
			department.hasDiagnostics = true  ''')

    List<Department> departmentsWithDiagnostics()

    @Query(value = '''Select department from Department department where
            upper(department.departmentName) like upper(concat('%',:name,'%'))''')
    Department findByName(@Param("name") String name)


    @Query(value = "Select department from Department department where department.hasRooms = true")
    List<Department> getDepartmentWithRooms()

    @Query(value = "Select department from Department department where (department.hasSpecialPriceTier = true and department.hasPatients = true)")
    List<Department> getDepartmentWithTiers()

    @Query(value = "Select department from Department department where department.parentDepartment is null")
    List<Department> getParentDepartments()

    @Query(value = "Select department from Department department where department.parentDepartment is not null")
    List<Department> getSubDepartments()

    @Query(value = "Select department from Department department where department.parentDepartment.id = :parentDepartment")
    List<Department> getSubDepartments(@Param("parentDepartment") UUID parentDepartment)

    @Query(value = "Select department from Department department where department.canReceiveItems = true")
    List<Department> getReceivingDepartment()

    @Query(value = "Select department from Department department where department.medicationStockRequest = true")
    List<Department> getMedicationStockRequestDepartment()

    @Query(value = "Select department from Department department where department.canPurchaseItems = true")
    List<Department> getPurchasingDepartment()

    @Query(value = "Select department from Department department where department.parentDepartment is not null and  (department.revenueCenter = true or department.parentDepartment.revenueCenter = true )  order by department.departmentCode")
    List<Department> getRevenueCenters()

    @Query(value = "select distinct group_category from department ", nativeQuery = true)
    List<String> getallCategory()

    @Query(value = "Select department from Department department where department.canClearPatientDischarge = true")
    List<Department> defaultClearingDepartments()

    @Query(value = "Select department from Department department where lower(department.departmentName) like lower(concat('%',:likes,'%'))")
    List<Department> departmentsAlike(@Param("likes") String likes)

    @Query(value = "Select department from Department department where lower(department.eventsToNotify) like lower(concat('%',:event,'%'))")
    List<Department> departmentsByEvent(@Param("event") String event)

    @Query(value = "Select department from Department department where department.hasPatients = true")
    List<Department> getPatientsDepartments()

    @Query(value = """
            Select distinct d from Department d
            left outer join fetch d.workSchedule dc
            order by d.departmentName, dc.dateTimeStartRaw 
    """)
    List<Department> getDepartmentSchedule()

    @Query(value = """
        Select distinct d from Department d
        left outer join fetch d.workSchedule dc
        where d.id = :id
        order by d.departmentName, dc.dateTimeStartRaw 
    """)
    List<Department> getOneDepartmentSchedule(@Param("id") UUID id)

    @Query(value = """
	Select distinct d from Department d
	 left join fetch d.workSchedule dc
	 where d.id = :id
	 order by dc.dateTimeStartRaw
	""")
    Department getOneDepartmentWithSchedule(@Param("id") UUID id)

    @Query(
            value = "Select e from Department e where e.id in (:ids)")
    List<Department> findDepartmentsInIds(@Param("ids") ArrayList<UUID> ids)
    
    // wilson update
    @Query(value = "Select d from Department d  left join fetch d.children ch left join fetch d.parentDepartment parent where (d.deleted is null or d.deleted = false) and (d.hideAccounting is null or d.hideAccounting = false)")
    List<Department> getDepartmentsFlatten()

    @Query(value = '''Select d from Department d  left join fetch d.children ch left join fetch d.parentDepartment parent 
            where (d.deleted is null or d.deleted = false) and (d.hideAccounting is null or d.hideAccounting = false) and d.id IN (:dep)''')
    List<Department> getDepartmentsFlattenWithIds(@Param("dep") List<UUID> dep)

    @Query(value = '''Select d from Department d where  d.id IN (:dep)''')
    List<Department> getDepartmentWithIds(@Param("dep") List<UUID> dep)
}
