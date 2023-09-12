package com.hisd3.hismk2.graphqlservices.hrm

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.Permission
import com.hisd3.hismk2.domain.User
import com.hisd3.hismk2.domain.hrm.DepartmentSchedule
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.hrm.EmployeeSchedule
import com.hisd3.hismk2.domain.hrm.ScheduleLock
import com.hisd3.hismk2.domain.hrm.dto.EmployeeDto
import com.hisd3.hismk2.domain.hrm.dto.EmployeeScheduleDto
import com.hisd3.hismk2.graphqlservices.hrm.dtotransformer.EmployeeDtoTransformer
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.hrm.EmployeeScheduleRepository
import com.hisd3.hismk2.repository.hrm.ScheduleLockRepository
import com.hisd3.hismk2.security.SecurityUtils
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.hibernate.jpa.QueryHints
import org.hibernate.query.Query
import org.hibernate.transform.ResultTransformer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@TypeChecked
@Component
@GraphQLApi
class EmployeeScheduleService {

    @Autowired
    private EmployeeScheduleRepository employeeScheduleRepository

    @Autowired
    private EmployeeRepository employeeRepository

    @Autowired
    private DepartmentRepository departmentRepository

    @Autowired
    private ScheduleLockRepository scheduleLockRepository

    @Autowired
    private ScheduleLockService scheduleLockService

    @Autowired
    private UserRepository userRepository

    @Autowired
    private ObjectMapper objectMapper

    @PersistenceContext
    private EntityManager entityManager

    //================================Query================================\\

    @GraphQLQuery(name = "getEmployeeScheduleLegend", description = "Get all possible legends in the give time range.")
    List<EmployeeScheduleDto> getEmployeeScheduleLegend(
            @GraphQLArgument(name = "department") UUID department,
            @GraphQLArgument(name = "startDate") Instant startDate,
            @GraphQLArgument(name = "endDate") Instant endDate
    ) {
        if (!startDate || !endDate) throw new RuntimeException("Failed to get the legends")
        List<EmployeeScheduleDto> employeeScheduleDto = entityManager.createQuery("""
                Select distinct 
                    es.color as e_s_color, 
                    es.title as e_s_title, 
                    es.label as e_s_label
                    from Employee e
                    left outer join e.employeeSchedule es
                    join e.department d
                where
                    es.dateTimeStartRaw >= :startDate
                    and es.dateTimeStartRaw <= :endDate
                    and d.id = :department
                    and upper(es.label) != 'R'
                    and es.title is not null
                    and es.isOvertime is not true
                    and (es.isCustom is not true or es.isCustom is null)
                    and es.isLeave is not true

                order by es.title
            """)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setParameter("department", department)
                .unwrap(Query.class)
                .setResultTransformer(new ResultTransformer() {
                    @Override
                    Object transformTuple(Object[] tuple, String[] aliases) {
                        return new EmployeeScheduleDto(
                                tuple[0] as String,
                                tuple[1] as String,
                                tuple[2] as String
                        )
                    }

                    @Override
                    List transformList(List collection) {
                        return collection
                    }
                })
                .getResultList()
        return employeeScheduleDto
    }

    @GraphQLQuery(name = "getAllEmployeeSchedule", description = "Get employees with department schedules")
    Page<EmployeeDto> getAllEmployeeSchedule(
            @GraphQLArgument(name = "department") UUID department,
            @GraphQLArgument(name = "startDate") Instant startDate,
            @GraphQLArgument(name = "endDate") Instant endDate,
            @GraphQLArgument(name = "size") Integer size,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "withChildren") Boolean withChildren,
            @GraphQLArgument(name = "filter") String filter

    ) {
        List<UUID> parentWithChildrenDepartments = []
        if (withChildren)
            parentWithChildrenDepartments = entityManager.createQuery("""
                Select d.id from Department d
                left join d.parentDepartment pd
                where d.id = :department or pd.id = :department
                """).setParameter("department", department)
                    .resultList


        List<DepartmentSchedule> schedules = []
        //prefetch schedules
        if (department == null) {
            schedules = entityManager.createQuery("""
                Select ds from DepartmentSchedule ds
                join fetch ds.department d
                where d.id is null
            """).getResultList()
        } else if (department && withChildren) {
            schedules = entityManager.createQuery("""
                Select ds from DepartmentSchedule ds
                join fetch ds.department d
                where d.id in :departments
            """).setParameter("departments", parentWithChildrenDepartments).getResultList()
        } else {
            schedules = entityManager.createQuery("""
                Select ds from DepartmentSchedule ds
                join fetch ds.department d
                where d.id = :id
            """).setParameter("id", department).getResultList()
        }

        String employeeIdQueryString

        if (department == null)
            employeeIdQueryString = """
                    Select e.id from Employee e
                    where e.department IS NULL and e.isActive IS NOT NULL and e.isActive = true
                    and lower(e.fullName) like lower(concat('%', :filter ,'%'))
                    and (e.excludePayroll IS FALSE OR e.excludePayroll IS NULL)
                    order by e.fullName"""
        else if (department && withChildren)
            employeeIdQueryString = """
                    Select e.id from Employee e
                        left join e.department d
                    where d.id in :department and e.isActive IS NOT NULL and e.isActive = true
                    and lower(e.fullName) like lower(concat('%', :filter ,'%'))
                    and (e.excludePayroll IS FALSE OR e.excludePayroll IS NULL)
                    order by e.fullName"""
        else
            employeeIdQueryString = """
                    Select e.id from Employee e
                        left join e.department d
                    where d.id = :department and e.isActive IS NOT NULL and e.isActive = true
                    and lower(e.fullName) like lower(concat('%', :filter ,'%'))
                    and (e.excludePayroll IS FALSE OR e.excludePayroll IS NULL)
                    order by e.fullName"""

        def employeeIdQuery = entityManager.createQuery(employeeIdQueryString)
                .setParameter("filter", filter)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
        def employeeIds = []
        if (department == null) {
            employeeIds = employeeIdQuery.setFirstResult(page * size)
                    .setMaxResults(size)
                    .getResultList()
        } else if (department && withChildren) {
            employeeIds = employeeIdQuery.setParameter("department", parentWithChildrenDepartments)
                    .setFirstResult(page * size)
                    .setMaxResults(size)
                    .resultList
        } else {
            employeeIds = employeeIdQuery.setParameter("department", department)
                    .setFirstResult(page * size)
                    .setMaxResults(size)
                    .getResultList()
        }

        Map<String, EmployeeDto> employees = entityManager.createQuery("""
                Select distinct 
                    e.id as e_id, 
                    e.fullName as e_full_name, 
                    d.id as d_department_of_duty_id,
                    d.departmentName as d_department_name,
                    dd.id as dd_department_of_duty_id,
                    dd.departmentName as dd_department_name,
                    es.id as e_s_id, 
                    es.dateTimeStart as e_s_date_time_start, 
                    es.timeStart as e_s_time_start, 
                    es.dateTimeStartRaw as e_s_date_time_start_raw, 
                    es.dateTimeEnd as e_s_date_time_end, 
                    es.timeEnd as e_s_time_end, 
                    es.dateTimeEndRaw as e_s_date_time_end_raw, 
                    es.color as e_s_color, 
                    es.isRestDay as e_s_is_rest_day, 
                    es.isOvertime as e_s_is_overtime, 
                    es.isLeave as e_s_is_leave, 
                    es.title as e_s_title, 
                    es.label as e_s_label, 
                    es.locked as e_s_locked,
                    es.mealBreakStart as e_s_meal_break_start,
                    es.mealBreakEnd as e_s_meal_break_end,
                    es.isCustom as e_s_is_custom,
                    es.isOIC as e_s_is_oic,
                    es.isMultiDay as e_s_is_multi_day,
                    es.withNSD as e_s_with_nsd,
                    es.withHoliday as e_s_with_holiday,
                    es.withPay as e_s_with_pay,
                    es.assignedDate as e_s_assigned_date,
                    r.id as r_request_id,
                    dd.id as dd_department_of_duty_id,
                    dd.departmentName as dd_department_name,
                    esd.id as esd_department_of_duty_id,
                    esd.departmentName as esd_department_name 
                    from Employee e
                    left outer join e.employeeSchedule es
                    left join es.request r
                    left join e.department dd
                    left join e.department d
                    left join es.department esd
                where
                    (
                    ((es.dateTimeStartRaw >= :startDate and es.dateTimeStartRaw <= :endDate ) or (es.dateTimeEndRaw >= :startDate and es.dateTimeEndRaw <= :endDate) 
                    or (es.dateTimeStartRaw <= :startDate and es.dateTimeEndRaw >= :endDate)
                    or (es.assignedDate >= :startDate and es.assignedDate <= :endDate and es.isOvertime is true)
                    ) 
                    and e.id in :employees) 
                    
                order by e.fullName, es.dateTimeStartRaw
            """)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setParameter("employees", employeeIds)
                .unwrap(Query.class)
                .setResultTransformer(new EmployeeDtoTransformer())
                .getResultStream().inject([:]) { Map<String, EmployeeDto> map, EmployeeDto it -> map << [(it.id.toString()): it] } as Map

        List<EmployeeDto> finalEmployees = []
        List<Employee> newEmployees = entityManager.createQuery("""
                Select e from Employee e
                    left join fetch e.department
                where e.id in :employees 
                order by e.fullName
            """, Employee.class)
                .setParameter("employees", employeeIds)
                .getResultList()

        newEmployees = entityManager.createQuery("""
                Select e from Employee e
                    left join fetch e.departmentOfDuty
                where e.id in :employees 
                order by e.fullName
            """, Employee.class)
                .setParameter("employees", employeeIds)
                .getResultList()

        Map<String, List<DepartmentSchedule>> departmentScheduleMap = schedules.groupBy { it.department.id.toString() }


        newEmployees.forEach({ Employee it ->
            List<DepartmentSchedule> departmentSchedule = []
            if (department && withChildren) departmentSchedule = departmentScheduleMap[it.department.id.toString()] ?: new ArrayList<DepartmentSchedule>()
            else departmentSchedule = schedules ?: new ArrayList<DepartmentSchedule>()
            if (employees.get(it.id.toString())) {
                EmployeeDto employee = employees.get(it.id.toString())
                employee.schedule = departmentSchedule
                finalEmployees.add(employee)
            } else finalEmployees.add(
                    new EmployeeDto(
                            it.id,
                            it.fullName,
                            it.department?.id,
                            it.department?.departmentName,
                            it.departmentOfDuty?.id,
                            it.departmentOfDuty?.departmentName,
                            departmentSchedule
                    )
            )
        })

        def count
        if (department == null)
            count = entityManager.createQuery("""
                Select count(e) from Employee e
                where e.department is null and e.isActive IS NOT NULL and e.isActive = true
                and lower(e.fullName) like lower(concat('%', :filter ,'%'))
                and (e.excludePayroll IS FALSE OR e.excludePayroll IS NULL)
            """, Long.class)
                    .setParameter("filter", filter)
                    .getSingleResult()
        else if (department && withChildren)
            count = entityManager.createQuery("""
                 Select count(e) from Employee e
                     left join e.department d
                 where d.id in :department and e.isActive IS NOT NULL and e.isActive = true
                 and lower(e.fullName) like lower(concat('%', :filter ,'%'))
                 and (e.excludePayroll IS FALSE OR e.excludePayroll IS NULL)
            """, Long.class)
                    .setParameter("department", parentWithChildrenDepartments)
                    .setParameter("filter", filter)
                    .getSingleResult()
        else
            count = entityManager.createQuery("""
                Select count(e) from Employee e
                left join e.department d
                where d.id = :department and e.isActive IS NOT NULL and e.isActive = true
                and lower(e.fullName) like lower(concat('%', :filter ,'%'))
                and (e.excludePayroll IS FALSE OR e.excludePayroll IS NULL)
            """, Long.class)
                    .setParameter("department", department)
                    .setParameter("filter", filter)
                    .getSingleResult()

        return new PageImpl<EmployeeDto>(finalEmployees, PageRequest.of(page, size),
                count)


    }

    @GraphQLQuery(name = "getEmployeeScheduleDetails", description = "Get the schedule of the day of employee")
    EmployeeDto getEmployeeScheduleDetails(
            @GraphQLArgument(name = "employee") UUID employee,
            @GraphQLArgument(name = "startDate") Instant startDate,
            @GraphQLArgument(name = "endDate") Instant endDate
    ) {
        if (!employee) throw new RuntimeException("Failed to get employee schedule")
        List<EmployeeDto> employeeDto = entityManager.createQuery("""
                Select  
                    e.id as e_id, 
                    e.fullName as e_full_name, 
                    d.id as d_department_of_duty_id,
                    d.departmentName as d_department_name,
                    dd.id as dd_department_of_duty_id,
                    dd.departmentName as dd_department_name,
                    es.id as e_s_id, 
                    es.dateTimeStart as e_s_date_time_start, 
                    es.timeStart as e_s_time_start, 
                    es.dateTimeStartRaw as e_s_date_time_start_raw, 
                    es.dateTimeEnd as e_s_date_time_end, 
                    es.timeEnd as e_s_time_end, 
                    es.dateTimeEndRaw as e_s_date_time_end_raw, 
                    es.color as e_s_color, 
                    es.isRestDay as e_s_is_rest_day, 
                    es.isOvertime as e_s_is_overtime, 
                    es.isLeave as e_s_is_leave, 
                    es.title as e_s_title, 
                    es.label as e_s_label, 
                    es.locked as e_s_locked,
                    es.mealBreakStart as e_s_meal_break_start,
                    es.mealBreakEnd as e_s_meal_break_end,
                    es.isCustom as e_s_is_custom,
                    es.isOIC as e_s_is_oic,
                    es.isMultiDay as e_s_is_multi_day,
                    es.withNSD as e_s_with_nsd,
                    es.withHoliday as e_s_with_holiday,
                    es.withPay as e_s_with_pay,
                    es.assignedDate as e_s_assigned_date,
                    r.id as r_request_id,  
                    esd.id as esd_department_of_duty_id,
                    esd.departmentName as esd_department_name 
                    from Employee e
                    left outer join e.employeeSchedule es
                    left join es.request r
                    left join e.department dd
                    left join e.department d
                    left join es.department esd
                where
                    (
                        (
                            (es.dateTimeStartRaw >= :startDate and es.dateTimeStartRaw <= :endDate ) 
                            or (es.dateTimeEndRaw >= :startDate and es.dateTimeEndRaw <= :endDate)
                        )  
                        or (es.assignedDate >= :startDate and es.assignedDate <= :endDate )
                    )
                    and e.id in (:employee)
                order by e.fullName, es.dateTimeStartRaw
        """).setParameter("employee", employee)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .unwrap(Query.class)
                .setResultTransformer(new EmployeeDtoTransformer())
                .getResultList()

        if (employeeDto.isEmpty()) {
            List<Employee> empList = entityManager.createQuery("""
                Select e from Employee e
                join fetch e.departmentOfDuty
                where e.id = :employee
            """, Employee.class).setParameter("employee", employee).getResultList()
            if (empList) {
                if (empList.size() > 0) {

                    Employee emp = empList.first()
                    EmployeeDto empDto = new EmployeeDto(
                            emp.id,
                            emp.fullName,
                            emp.department.id,
                            emp.department.departmentName
                    )
                    return empDto
                } else return null

            } else return null

        } else
            return employeeDto.first() as EmployeeDto
    }

    @GraphQLQuery(name = "getSchedule", description = "Get one schedule.")
    EmployeeSchedule getSchedule(@GraphQLArgument(name = "id") UUID id) {
        if (!id) throw new RuntimeException("Failed to get schedule")

        EmployeeSchedule schedule = employeeScheduleRepository.findById(id).get()
        return schedule
    }

    //================================Query================================\\

    //===============================Mutation==============================\\

    @GraphQLMutation(name = "createEmployeeSchedule", description = "Create schedule for one employee")
    GraphQLRetVal<String> createEmployeeSchedule(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "employee_id") UUID employee_id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields,
            @GraphQLArgument(name = "department") UUID departmentId
    ) {
        String username = SecurityUtils.currentLogin()
        User user = userRepository.findOneByLogin(username)
        Boolean hasPermission = user.permissions.any { Permission it -> it.name == "manage_locked_work_schedule" }

        if (!employee_id) return new GraphQLRetVal<String>("ERROR", false, "Failed to create schedule for employee.")
        if (!fields.get("isRestDay") && !departmentId) return new GraphQLRetVal<String>("ERROR", false, "Failed to create schedule for employee.")

        Instant startDate = Instant.parse(fields.get("dateTimeStartRaw") as String)
        if (!startDate) return new GraphQLRetVal<String>("ERROR", false, "Failed to create schedule for employee.")
        Map<String, ScheduleLock> lockedDates = scheduleLockService.getScheduleLock(startDate.minus(1, ChronoUnit.DAYS), startDate.plus(1, ChronoUnit.DAYS))
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM_dd_yyyy").withZone(ZoneId.systemDefault())
        String formattedDate = formatter.format(startDate)
        ScheduleLock scheduleLock = lockedDates.get(formattedDate)
        if (scheduleLock && !hasPermission)
            if (scheduleLock.isLocked)
                return new GraphQLRetVal<String>("ERROR", false, "Date is locked.")

        Employee employee = employeeRepository.findById(employee_id).get()
        if (id) {
            EmployeeSchedule schedule = employeeScheduleRepository.findById(id).get()

            schedule = objectMapper.updateValue(schedule, fields)
            if (departmentId) {
                Department department = departmentRepository.findById(departmentId).get()
                schedule.department = department
            }
            schedule.employee = employee
            employeeScheduleRepository.save(schedule)

            return new GraphQLRetVal<String>("OK", true, "Successfully updated schedule for employee.")
        } else {
            EmployeeSchedule schedule = objectMapper.convertValue(fields, EmployeeSchedule)
            schedule.employee = employee
            if (departmentId) {
                Department department = departmentRepository.findById(departmentId).get()
                schedule.department = department
            }
            employeeScheduleRepository.save(schedule)
            return new GraphQLRetVal<String>("OK", true, "Successfully created schedule for employee.")
        }
    }

    @GraphQLMutation(name = "removeEmployeeSchedule", description = "Delete the schedule of employee.")
    GraphQLRetVal<String> removeEmployeeSchedule(
            @GraphQLArgument(name = "id") UUID id
    ) {
        if (!id) return new GraphQLRetVal<String>("ERROR", false, "Failed to delete employee schedule.")
        EmployeeSchedule schedule = employeeScheduleRepository.findById(id).get()
        employeeScheduleRepository.delete(schedule)
        new GraphQLRetVal<String>("OK", true, "Successfully deleted schedule for employee.")
    }


    //===============================Mutation==============================\\

}
