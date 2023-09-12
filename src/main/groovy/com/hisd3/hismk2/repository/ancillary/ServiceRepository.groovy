package com.hisd3.hismk2.repository.ancillary

import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.ancillary.Service
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ServiceRepository extends JpaRepository<Service, UUID> {

	@Query(value = '''Select s from Service s where (s.hidden = false)''')
	List<Service> listAll()

	@Query(value = '''Select s from Service s where (s.hidden = false) and
            (
            	(lower(s.serviceName) like lower(concat('%',:filter,'%')) or
            	lower(s.serviceCode) like lower(concat('%',:filter,'%'))) or (
            		lower(s.department.departmentName) like lower(concat('%',:filter,'%'))
            	)
            )
			''')
	List<Service> searchlist(@Param("filter") String filter)

	@Query(value = '''Select s from Service s where (s.hidden = false and s.available = true) and
            (
            	(lower(s.serviceName) like lower(concat('%',:filter,'%')) or
            	lower(s.serviceCode) like lower(concat('%',:filter,'%'))) or (
            		lower(s.department.departmentName) like lower(concat('%',:filter,'%'))
            	)
            )
			''')
	List<Service> searchlistForDoctorsOrders(@Param("filter") String filter)

	@Query(value = '''Select s from Service s where (s.hidden = false) and
            (lower(s.serviceName) like lower(concat('%',:filter,'%') )or
            lower(s.serviceCode) like lower(concat('%',:filter,'%')))
			''',
			countQuery = '''Select count(s) from Service s where (s.hidden = false and s.available = true) and
            (lower(s.serviceName) like lower(concat('%',:filter,'%') )or
            lower(s.serviceCode) like lower(concat('%',:filter,'%')))
			'''
	)
	Page<Service> searchlist(@Param("filter") String filter, Pageable pageable)

	@Query(value = '''Select s from Service s where (s.hidden = false) and
            (lower(s.serviceName) like lower(concat('%',:filter,'%') )or
            lower(s.serviceCode) like lower(concat('%',:filter,'%')))
			''',
			countQuery = '''Select count(s) from Service s where (s.hidden = false) and
            (lower(s.serviceName) like lower(concat('%',:filter,'%') )or
            lower(s.serviceCode) like lower(concat('%',:filter,'%')))
			'''
	)
	Page<Service> searchlistPageable(@Param("filter") String filter, Pageable pageable)

	@Query(value = '''Select s from Service s where ((s.hidden = false) and
            (lower(s.serviceName) like lower(concat('%',:filter,'%')) or lower(s.serviceCode) like lower(concat('%',:filter,'%')))) and
            s.basePrice between :from and :to
			''',
			countQuery = '''Select count(s) from Service s where (s.hidden = false and
            (lower(s.serviceName) like lower(concat('%',:filter,'%')) or lower(s.serviceCode) like lower(concat('%',:filter,'%')))) and
            s.basePrice between :from and :to
			'''
	)
	Page<Service> searchlistPageableByCostRange(
			@Param("filter") String filter,
			@Param("from") BigDecimal from,
			@Param("to") BigDecimal to,
			Pageable pageable)

	@Query(value = '''Select s from Service s where lower(s.serviceName) like lower(concat('%',:filter,'%')) and  (s.hidden = false)
		and (s.department.id= :department or s.department.parentDepartment.id =:department) ''')
	List<Service> searchlistByDepartment(@Param("department") UUID department, @Param("filter") String filter)

	@Query(value = '''Select s from Service s where lower(s.serviceName) like lower(concat('%',:filter,'%')) and  (s.hidden = false)
		and (s.department.id= :department or s.department.parentDepartment.id =:department) ''',
			countQuery = '''Select count(s) from Service s where lower(s.serviceName) like lower(concat('%',:filter,'%')) and  (s.hidden = false)
		and (s.department.id= :department or s.department.parentDepartment.id =:department) '''
	)
	Page<Service> searchlistByDepartment(@Param("department") UUID department, @Param("filter") String filter, Pageable pageable)

	@Query(value = '''Select s from Service s where lower(s.serviceName) like lower(concat('%',:filter,'%')) and  (s.hidden = false)
		and (s.department.id= :department or s.department.parentDepartment.id =:department) ''',
			countQuery = '''Select count(s) from Service s where lower(s.serviceName) like lower(concat('%',:filter,'%')) and  (s.hidden = false)
		and (s.department.id= :department or s.department.parentDepartment.id =:department) '''
	)
	Page<Service> searchlistByDepartmentPage(@Param("department") UUID department, @Param("filter") String filter, Pageable pageable)

	@Query(value = '''Select s from Service s where (lower(s.serviceName) like lower(concat('%',:filter,'%')) and  (s.hidden = false)
		and (s.department.id= :department or s.department.parentDepartment.id =:department)) and s.basePrice between :from and :to''',
			countQuery = '''Select count(s) from Service s where (lower(s.serviceName) like lower(concat('%',:filter,'%')) and  (s.hidden = false)
		and (s.department.id= :department or s.department.parentDepartment.id =:department)) and s.basePrice between :from and :to'''
	)
	Page<Service> searchlistByDepartmentPageByCostRange(
			@Param("department") UUID department,
			@Param("filter") String filter,
			@Param("from") BigDecimal from,
			@Param("to") BigDecimal to,
			Pageable pageable)

	@Query(value = '''Select s from Service s where  lower(s.serviceName) like lower(concat('%',:filter,'%')) and  (s.hidden = false)
					and (s.department.id= :department or s.department.parentDepartment.id =:department) ''',
			countQuery = """
        Select s from Service s where s.department.id= :department or s.department.parentDepartment.id =:department and lower(s.serviceName) like lower(concat('%',:filter,'%'))
   """)

	Page<Service> searchlistByDepartmentPageable(@Param("department") UUID department, @Param("filter") String filter,
	                                             Pageable pageable)


	@Query(value = '''
  	  Select s from Service s where lower(s.department.groupCategory)  like lower(concat('%',:groupCategory,'%'))
  	   and (s.hidden = false)
  	   and (lower(s.serviceCode) like lower(concat('%',:filter,'%'))
  	    or
  	    lower(s.serviceName) like lower(concat('%',:filter,'%'))
  	    or
  	    lower(s.processCode) like lower(concat('%',:filter,'%'))
  	   )
  	    and s.department = (CASE s.genericService WHEN true THEN  s.department else :departmentUser  END)   
      ''',
			countQuery = '''
		 Select count(s) from Service s where lower(s.department.groupCategory)  like lower(concat('%',:groupCategory,'%'))
		  and (s.hidden = false)
		  and
			   (lower(s.serviceCode) like lower(concat('%',:filter,'%'))
				or
				lower(s.serviceName) like lower(concat('%',:filter,'%'))
				or
				lower(s.processCode) like lower(concat('%',:filter,'%'))
			   )
			    and s.department = (CASE s.genericService WHEN true THEN  s.department else :departmentUser  END)   
    ''')
	Page<Service> getServicesForTaggedServicesDept(@Param("filter") String filter,
												   @Param("groupCategory") String groupCategory,
												   @Param("departmentUser") Department departmentUser,
												   Pageable pageable)

	@Query(value = '''
  	  Select s from Service s where lower(s.department.groupCategory)  like lower(concat('%',:groupCategory,'%'))
  	   and (s.hidden = false)
  	   and (lower(s.serviceCode) like lower(concat('%',:filter,'%'))
  	    or
  	    lower(s.serviceName) like lower(concat('%',:filter,'%'))
  	    or
  	    lower(s.processCode) like lower(concat('%',:filter,'%'))
  	   )
  	    and s.department = (CASE s.genericService WHEN true THEN  s.department else :departmentUser  END) and s.isLifeSupport = true  
      ''',
			countQuery = '''
		 Select count(s) from Service s where lower(s.department.groupCategory)  like lower(concat('%',:groupCategory,'%'))
		  and (s.hidden = false)
		  and
			   (lower(s.serviceCode) like lower(concat('%',:filter,'%'))
				or
				lower(s.serviceName) like lower(concat('%',:filter,'%'))
				or
				lower(s.processCode) like lower(concat('%',:filter,'%'))
			   )
			    and s.department = (CASE s.genericService WHEN true THEN  s.department else :departmentUser  END)  and s.isLifeSupport = true  
    ''')
	Page<Service> getServicesForTaggedServicesDeptCreditLimit(@Param("filter") String filter,
												   @Param("groupCategory") String groupCategory,
												   @Param("departmentUser") Department departmentUser,
												   Pageable pageable)



	@Query(value = '''
  	  Select s from Service s where lower(s.department.groupCategory)  like lower(concat('%',:groupCategory,'%'))
  	   and (s.hidden = false)
  	   and (lower(s.serviceCode) like lower(concat('%',:filter,'%'))
  	    or
  	    lower(s.serviceName) like lower(concat('%',:filter,'%'))
  	    or
  	    lower(s.processCode) like lower(concat('%',:filter,'%'))
  	   )
      ''',
			countQuery = '''
		 Select count(s) from Service s where lower(s.department.groupCategory)  like lower(concat('%',:groupCategory,'%'))
		  and (s.hidden = false)
		  and
			   (lower(s.serviceCode) like lower(concat('%',:filter,'%'))
				or
				lower(s.serviceName) like lower(concat('%',:filter,'%'))
				or
				lower(s.processCode) like lower(concat('%',:filter,'%'))
			   )
    ''')
	Page<Service> getServicesForTaggedServices(@Param("filter") String filter,
	                                           @Param("groupCategory") String groupCategory,
	                                           Pageable pageable)

	@Query(value = '''
  	  Select s from Service s where
  	   (lower(s.serviceCode) like lower(concat('%',:filter,'%'))
  	    or
  	    lower(s.serviceName) like lower(concat('%',:filter,'%'))
  	    or
  	    lower(s.processCode) like lower(concat('%',:filter,'%'))
  	   ) and s.hidden = false
      ''',
			countQuery = '''
		 Select count(s) from Service s where
			   (lower(s.serviceCode) like lower(concat('%',:filter,'%'))
				or
				lower(s.serviceName) like lower(concat('%',:filter,'%'))
				or
				lower(s.processCode) like lower(concat('%',:filter,'%'))
			   ) and s.hidden = false
    ''')
	Page<Service> getServicesFiltered(@Param("filter") String filter,
	                                  Pageable pageable)

	@Query(value = '''Select s from Service s where (s.hidden = false and
            (lower(s.serviceName) like lower(concat('%',:filter,'%') )or
            lower(s.serviceCode) like lower(concat('%',:filter,'%')))) and s.basePrice between :from and :to
			''')
	List<Service> searchlistByCostRange(@Param("filter") String filter,
	                                    @Param("from") BigDecimal from,
	                                    @Param("to") BigDecimal to)

	@Query(value = '''Select s from Service s where s.processCode=:processCode and (s.hidden = false) ''')
	List<Service> serviceByProcessCode(@Param("processCode") String processCode)

	@Query(value = '''Select s from Service s where (lower(s.serviceName) like lower(concat('%',:filter,'%')) and  (s.hidden = false)
		and (s.department.id= :department or s.department.parentDepartment.id =:department)) and s.basePrice between :from and :to''')
	List<Service> searchlistByDepartmentByCostRange(
			@Param("department") UUID department,
			@Param("filter") String filter,
			@Param("from") BigDecimal from,
			@Param("to") BigDecimal to)

	@Query(value = '''Select s from Service s where (s.department.departmentName= :department or s.department.parentDepartment.departmentName =:department) ''')

	List<Service> searchByDepartment(@Param("department") String department)

	@Query(value = '''Select s from Service s where lower(s.serviceName) like lower(concat('%',:filter,'%')) and  (s.hidden = false)
		and s.department.id= :department ''')
	List<Service> searchlistByDepartmentAndCategory(@Param("department") UUID department,@Param("filter") String filter)
}
