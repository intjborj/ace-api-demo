package com.hisd3.hismk2.repository.ancillary

import com.hisd3.hismk2.domain.ancillary.RfFees
import com.hisd3.hismk2.domain.ancillary.Service
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface RfFeesRepository extends JpaRepository<RfFees, UUID> {

    @Query(value = '''Select rf from RfFees rf where rf.service.id=:serviceId and rf.doctor.id=:empId''')
    List<RfFees> searchMatch(@Param("serviceId") UUID serviceId,@Param("empId") UUID empId)


    @Query(value = """Select rf from RfFees rf where lower(rf.service.serviceName) like lower(concat('%',:filter,'%') ) 
			""",
            countQuery = """Select count(rf) from RfFees rf where lower(rf.service.serviceName) like lower(concat('%',:filter,'%')) 
			""")
    Page<RfFees> searchRfPageable(@Param("filter")String filter,
                                  Pageable pageable)

    @Query(value = '''Select rf from RfFees rf where lower(rf.service.serviceName) like lower(concat('%',:category,'%'))
            and (cast(rf.service.department.parentDepartment.id as string) like concat('%',:department,'%') or cast(rf.service.department.id as string) like concat('%',:department,'%') )
            and cast(rf.doctor.id as string) like concat('%',:empId,'%')
			''',
            countQuery = '''Select count(rf) from RfFees rf where lower(rf.service.serviceName) like lower(concat('%',:category,'%'))
            and (cast(rf.service.department.parentDepartment.id as string) like concat('%',:department,'%') or cast(rf.service.department.id as string) like concat('%',:department,'%') )
            and cast(rf.doctor.id as string) like concat('%',:empId,'%')
			'''
    )

    Page<RfFees> searchFilteredRfPageable(@Param("category") String category,@Param("empId") String empId, @Param("department")String department,Pageable pageable)
}
