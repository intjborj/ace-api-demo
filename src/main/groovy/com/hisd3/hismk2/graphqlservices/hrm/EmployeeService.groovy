package com.hisd3.hismk2.graphqlservices.hrm

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.Authority
import com.hisd3.hismk2.domain.Permission
import com.hisd3.hismk2.domain.User
import com.hisd3.hismk2.domain.hrm.AddOn
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.hrm.EmployeeAttendance
import com.hisd3.hismk2.domain.hrm.EmployeeSchedule
import com.hisd3.hismk2.domain.hrm.dto.EmployeeDepartmentSalaryDto
import com.hisd3.hismk2.domain.referential.DohPosition
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.AuthorityRepository
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.PermissionRepository
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.repository.hrm.*
import com.hisd3.hismk2.repository.inventory.DepartmentStockIssueRepository

import com.hisd3.hismk2.repository.referential.DohPositionRepository
import com.hisd3.hismk2.security.HISUser
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.hibernate.jpa.QueryHints
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

@TypeChecked
@Component
@GraphQLApi
class EmployeeService {

    @Autowired
    private UserRepository userRepository

    @Autowired
    private EmployeeRepository employeeRepository

    @Autowired
    private EmployeeScheduleRepository employeeScheduleRepository

    @Autowired
    private EmployeeAttendanceRepository employeeAttendanceRepository

    @Autowired
    private DepartmentRepository departmentRepository

    @Autowired
    private DohPositionRepository dohPositionRepository

    @Autowired
    private AddOnRepository addOnRepository

    @Autowired
    private DepartmentStockIssueRepository departmentStockIssueRepository

    @Autowired
    private JobTitleRepository jobTitleRepository

    @Autowired
    JdbcTemplate jdbcTemplate

    @Autowired
    GeneratorService generatorService

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    PasswordEncoder passwordEncoder

    @Autowired
    PermissionRepository permissionRepository

    @Autowired
    AuthorityRepository authorityRepository

    @PersistenceContext
    EntityManager entityManager

    //============== All Queries ====================

    @GraphQLQuery(name = "employees", description = "Get All Employees")
    List<Employee> findAll() {
        employeeRepository.findAll().sort { it.lastName }
    }

    @GraphQLQuery(name = "getEmployeeIsActiveAndIncludeInPayroll", description = "Get all employee isActive")
    Page<Employee> getEmployeeIsActiveAndIncludeInPayroll(
            @GraphQLArgument(name = "filter") String filter,
            @GraphQLArgument(name = "option") String option,
            @GraphQLArgument(name = "department") UUID department,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "size") Integer size
    ) {
        if (department) {
            if (option == "ACTIVE_EMPLOYEE_INCLUDED_PAYROLL")
                return employeeRepository
                        .searchActiveAndIncldInPayrollEmpsAndDept(filter, department, PageRequest.of(page, size, Sort.Direction.ASC, "lastName"))
            else if (option == "EXCLUDE_PAYROLL")
                return employeeRepository.searchExcldInPayrollEmpAndDept(filter, department, new PageRequest(page, size, Sort.Direction.ASC, "lastName"))
            else if (option == "INACTIVE_EMPLOYEES")
                return employeeRepository.searchInActiveEmployeesAndDepartment(filter, department, new PageRequest(page, size, Sort.Direction.ASC, "lastName"))
            else
                return employeeRepository.searchEmployeesAndDepartment(filter, department, new PageRequest(page, size, Sort.Direction.ASC, "lastName"))
        } else {
            if (option == "ACTIVE_EMPLOYEE_INCLUDED_PAYROLL")
                return employeeRepository
                        .searchActiveAndIncldInPayrollEmps(filter, new PageRequest(page, size, Sort.Direction.ASC, "lastName"))
            else if (option == "EXCLUDE_PAYROLL")
                return employeeRepository.searchExcludedInPayrollEmployees(filter, new PageRequest(page, size, Sort.Direction.ASC, "lastName"))
            else if (option == "INACTIVE_EMPLOYEES")
                return employeeRepository.searchInActiveEmployees(filter, new PageRequest(page, size, Sort.Direction.ASC, "lastName"))
            else
                return employeeRepository.searchEmployees(filter, new PageRequest(page, size, Sort.Direction.ASC, "lastName"))
        }
    }

    @GraphQLQuery(name = "getEmployeeActiveAndIncldInPayrollAndDeptExcldUser", description = "Get all employee isActive")
    List<Employee> getEmployeeActiveAndIncldInPayrollAndDeptExcldUser(
            @GraphQLArgument(name = "filter") String filter,
            @GraphQLArgument(name = "option") String option,
            @GraphQLArgument(name = "department") UUID department,
            @GraphQLArgument(name = "excludeUser") Boolean excludeUser
    ) {

        def user = SecurityContextHolder.context.authentication.principal as HISUser

        String query = "Select e from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%'))"
        LinkedHashMap<String, Object> parameters = [:]

        if (option == "ACTIVE_EMPLOYEE_INCLUDED_PAYROLL")
            query += " and coalesce(e.isActive,TRUE) = true and coalesce(e.excludePayroll,FALSE) = FALSE"
        else if (option == "EXCLUDE_PAYROLL")
            query += " and coalesce(e.excludePayroll,FALSE) = FALSE"
        else if (option == "INACTIVE_EMPLOYEES")
            query += " and coalesce(e.isActive, TRUE) = false"

        if (department) {
            query += " and department.id = :department"
            parameters.put("department", department)
        }
        if (excludeUser) {
            Employee employee = userRepository.findOneByLogin(user.username).employee
            query += " and e.id <> :excludeUser"
            parameters.put("excludeUser", employee.id)
        }
        query += " order by e.fullName"

        def jpaQuery = entityManager.createQuery(query, Employee.class).setParameter("filter", filter)
        parameters.each { jpaQuery.setParameter(it.key, it.value) }

        return jpaQuery.resultList

    }

    @GraphQLQuery(name = "searchActiveEmployeesPageable", description = "Search employees")
    Page<Employee> searchActiveEmployeesPageable(@GraphQLArgument(name = "filter") String filter,
                                                 @GraphQLArgument(name = "showInActive") Boolean showInActive,
                                                 @GraphQLArgument(name = "department") UUID department,
                                                 @GraphQLArgument(name = "page") Integer page,
                                                 @GraphQLArgument(name = "size") Integer size) {
        if (department) {
            if (showInActive)
                return employeeRepository.searchEmployeesAndDepartment(filter, department, new PageRequest(page, size, Sort.Direction.ASC, "lastName"))
            else return employeeRepository.searchActiveEmployeesAndDepartmentPageable(filter, department, new PageRequest(page, size, Sort.Direction.ASC, "lastName"))
        } else {
            if (showInActive) return employeeRepository.searchEmployees(filter, new PageRequest(page, size, Sort.Direction.ASC, "lastName"))
            return employeeRepository.searchActiveEmployeesPageable(filter, new PageRequest(page, size, Sort.Direction.ASC, "lastName"))

        }

    }

    @GraphQLQuery(name = "getEmployeesInIdsByFilter", description = "get Selected Employees for assigning employee allowance")
    Page<Employee> getEmployeesInIdsByFilter(
            @GraphQLArgument(name = "ids") ArrayList<UUID> ids,
            @GraphQLArgument(name = "filter") String filter,
            @GraphQLArgument(name = "department") UUID department,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "size") Integer size
    ) {

        if (department) {
            return employeeRepository.findInIdsByByFullNameAndDepartment(filter, department, ids, new PageRequest(page, size, Sort.Direction.ASC, "lastName"))
        } else {
            return employeeRepository.findInIdsByFullName(filter, ids, new PageRequest(page, size, Sort.Direction.ASC, "lastName"))
        }
    }

    @GraphQLQuery(name = "searchEmployeesPageable", description = "Search employees")
    Page<Employee> searchEmployeesPageable(@GraphQLArgument(name = "filter") String filter,
                                           @GraphQLArgument(name = "department") UUID department,
                                           @GraphQLArgument(name = "page") Integer page,
                                           @GraphQLArgument(name = "size") Integer size) {
        if (department) {
            employeeRepository.searchEmployeesAndDepartment(filter, department, new PageRequest(page, size, Sort.Direction.ASC, "lastName"))
        } else {
            employeeRepository.searchEmployees(filter, new PageRequest(page, size, Sort.Direction.ASC, "lastName"))
        }

    }

    @GraphQLQuery(name = "searchEmployeesSalaryPageable", description = "Search employees salary")
    Page<EmployeeDepartmentSalaryDto> searchEmployeesSalaryPageable(@GraphQLArgument(name = "filter") String filter,
                                                                    @GraphQLArgument(name = "department") UUID department,
                                                                    @GraphQLArgument(name = "page") Integer page,
                                                                    @GraphQLArgument(name = "size") Integer size) {
        if (department) {
            employeeRepository.searchEmployeesSalaryAndDepartmentPageable(filter, department, new PageRequest(page, size, Sort.Direction.ASC, "lastName"))
        } else {
            employeeRepository.searchEmployeesSalaryPageable(filter, new PageRequest(page, size, Sort.Direction.ASC, "lastName"))
        }

    }

    @GraphQLQuery(name = "searchEmployees", description = "Search employees")
    List<Employee> searchEmployees(@GraphQLArgument(name = "filter") String filter) {
        employeeRepository.searchEmployees(filter).sort { it.lastName }
    }

    @GraphQLQuery(name = "getEmployeeAttendance", description = "Search employees")
    List<EmployeeAttendance> getEmployeeAttendance(@GraphQLArgument(name = "id") UUID id) {
        employeeAttendanceRepository.findByEmployeeID(id)
    }

    @GraphQLQuery(name = "getEmployeeSchedule", description = "Search employees")
    List<EmployeeSchedule> getEmployeeSchedule(@GraphQLArgument(name = "id") UUID id) {
        employeeScheduleRepository.findByEmployeeID(id)
    }

    @GraphQLQuery(name = "searchEmployeesByRole", description = "Search employees by role")
    List<Employee> searchEmployeesByRole(
            @GraphQLArgument(name = "role") String role,
            @GraphQLArgument(name = "filter") String filter) {
        List<Employee> empList = employeeRepository.searchEmployees(filter).sort { it.lastName }
        List<Employee> finalList = []
        empList.each { it ->

            if (it.user) {
                List<Authority> empRoles = it.user.authorities

                if (role.contains("NON_ATTENDING_PHYSICIAN")) {
                    empRoles.each { er ->
                        if (!role.contains(er["name"] as String) && !finalList.contains(it)) {
                            finalList.add(it as Employee)
                        }
                    }
                } else {
                    empRoles.each { er ->
                        if (role.contains(er["name"] as String) && !finalList.contains(it)) {
                            finalList.add(it as Employee)
                        }
                    }
                }
            }
        }

        return finalList
    }

    @GraphQLQuery(name = "searchEmployeesByAllowedCoManage", description = "Search employees by allowed to co-manage")
    List<Employee> searchEmployeesByAllowedCoManage(
            @GraphQLArgument(name = "filter") String filter) {
        return employeeRepository.searchEmployeesByAllowedCoManage(filter).sort { it.lastName }
    }

    @GraphQLQuery(name = "searchEmployeesByRoleId", description = "Search employees by role")
    List<Employee> searchEmployeesByRoleId(
            @GraphQLArgument(name = "role") String role,
            @GraphQLArgument(name = "filter") String filter) {
        List<Employee> empList = employeeRepository.searchEmployeesBySup(filter).sort { it.lastName }
        List<Employee> finalList = []
        empList.each { it ->

            if (it.user) {
                List<Authority> empRoles = it.user.authorities

                if (role.contains("NON_ATTENDING_PHYSICIAN")) {
                    empRoles.each { er ->
                        if (!role.contains(er["name"] as String) && !finalList.contains(it)) {
                            finalList.add(it as Employee)
                        }
                    }
                } else {
                    empRoles.each { er ->
                        if (role.contains(er["name"] as String) && !finalList.contains(it)) {
                            finalList.add(it as Employee)
                        }
                    }
                }
            }
        }

        return finalList
    }

    @GraphQLQuery
    Page<Employee> searchEmployeePermissionRole(
            @GraphQLArgument(name = "filter") String filter,
            @GraphQLArgument(name = "department") UUID department,
            @GraphQLArgument(name = "permissions") List<String> permissions,
            @GraphQLArgument(name = "roles") List<String> roles,
            @GraphQLArgument(name = "permissionOperation") String permissionOperation,
            @GraphQLArgument(name = "rolesOperation") String rolesOperation,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "size") Integer size
    ) {
        def authority = []
        def permission = []
        if (roles.size() > 0)
            authority = entityManager.createQuery("select a from Authority a where a.name in :list", Authority.class).setParameter("list", roles).resultList
        if (permissions.size() > 0)
            permission = entityManager.createQuery("select p from Permission p where p.name in :list", Permission.class).setParameter("list", permissions).resultList


        List<Employee> employee = []
        Long employeeCount = null

        def selectQuery = """
                SELECT DISTINCT e
                FROM Employee e
                left join fetch e.user u
                left join fetch e.departmentOfDuty dd"""
        def whereClause = """
                where 
                    lower(e.fullName) like lower(concat('%',:filter,'%'))"""

        def countQuery = """
                SELECT count(e)
                FROM Employee e
                left join e.user u
                left join e.departmentOfDuty dd"""

        LinkedHashMap<String, Object> parameters = [:]
        parameters.put("filter", filter)

        if (department) {
            selectQuery += " left join fetch e.department d"
            countQuery += " left join e.department d"
            whereClause += " and e.department.id = :department"
            parameters.put("department", department)
        }

        if (permissionOperation && permission.size() > 0) {
            if (permissionOperation == "AND") {
                whereClause += " AND (Select count(p.name) from User pu left join pu.permissions p where pu = u and p.name in :permissions) >= :p_size "
                parameters.put("permissions", permissions)
                parameters.put("p_size", permission.size().longValue())
            } else if (permissionOperation == "OR") {
                selectQuery += " left join fetch u.permissions p"
                countQuery += " left join u.permissions p"
                whereClause += " AND p in :permissions"
                parameters.put("permissions", permission)
            }
        }

        if (rolesOperation && authority.size() > 0) {
            if (rolesOperation == "AND") {
                whereClause += " AND (Select count(au.name) from User auu left join auu.authorities au where auu= u and au.name in :authority) >= :au_size"
                parameters.put("authority", roles)
                parameters.put("au_size", roles.size().longValue())
            } else if (rolesOperation == "OR") {
                selectQuery += " left join fetch u.authorities au"
                countQuery += " left join u.authorities au"
                whereClause += " AND au in :authorities"
                parameters.put("authorities", authority)
            }
        }

        def fullSelectQuery = "${selectQuery} ${whereClause}"
        def fullCountQuery = "${countQuery} ${whereClause}"

        def employeeQuery = entityManager.createQuery(fullSelectQuery, Employee.class)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
        def employeesCountQuery = entityManager.createQuery(fullCountQuery, Long.class)

        parameters.each {
            employeeQuery.setParameter(it.key, it.value)
            employeesCountQuery.setParameter(it.key, it.value)
        }

        employee = employeeQuery
                .setFirstResult(page * size)
                .setMaxResults(size)
                .resultList
        employeeCount = employeesCountQuery.singleResult


        return new PageImpl<Employee>(employee, PageRequest.of(page, size),
                employeeCount)
    }

    @GraphQLQuery(name = "employee", description = "Get Employee By Id")
    Employee findById(@GraphQLArgument(name = "id") UUID id) {

        return id ? employeeRepository.findById(id).get() : null
    }

    @GraphQLQuery(name = "employeesByDep", description = "Get Employee By Department")
    List<Employee> findEmployeesByDepartment(@GraphQLArgument(name = "id") UUID id) {

        return id ? employeeRepository.findEmployeesByDepartment(id) : null
    }


    //==============Add On =====================
    @GraphQLQuery(name = "employeeAddOns", description = "Get one Employee add-ons")
    List<AddOn> employeeAddOns(@GraphQLArgument(name = "id") UUID id) {
        if (!id) throw new RuntimeException("ID is required")
        return addOnRepository.getEmployeeAddOns(id)
    }


    //============== All Mutations ====================

    @GraphQLMutation
    @Transactional
    GraphQLRetVal<String> migrateEmployee(
            @GraphQLArgument(name = "list") List<Map<String, Object>> list
    ) {
        list.each {
            Employee emp = objectMapper.convertValue(it, Employee)
            List<Employee> foundEmployee = employeeRepository.getEmployeeByFullName(emp.fullName)
            if (foundEmployee.size() == 0)
                employeeRepository.save(emp)
        }

        return new GraphQLRetVal<String>("OK", true, "Successfully migrated employee ID")
    }


    @GraphQLMutation
    @Transactional
    GraphQLRetVal<String> migrateEmployeeId(
            @GraphQLArgument(name = "list") List<Map<String, Object>> list
    ) {
        list.each {
            String id = it.get('id')
            Optional<Employee> possibleEmployee = employeeRepository.findById(UUID.fromString(id))
            if (possibleEmployee.isPresent()) {
                Employee employee = possibleEmployee.get()
                employee.employeeId = it.get('employeeid')
                employeeRepository.save(employee)
            }
        }

        return new GraphQLRetVal<String>("OK", true, "Successfully migrated employee ID")
    }

    @GraphQLMutation
    @Transactional
    GraphQLRetVal<Employee> upsertEmployee(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields,
            @GraphQLArgument(name = "departmentId") UUID departmentId,
            @GraphQLArgument(name = "departmentOfDutyId") UUID departmentOfDutyId

    ) {

        if (id) {
            Employee employee = employeeRepository.findById(id).get()
            objectMapper.updateValue(employee, fields)
            employee.department = departmentRepository.findById(departmentId).get()

            DohPosition position = dohPositionRepository.findById(UUID.fromString(fields['position'] as String)).get()
            if (position) {
                employee.position = position.id
                employee.positionCode = position?.isOthers ? null: position.poscode
                employee.positionCodeOthers = position?.isOthers ? position.poscode: null
                employee.positionType = position?.postdesc
            }

            employee.departmentOfDuty = departmentRepository.findById(departmentOfDutyId).get()
            User user = new User()

            if (!employee.user) {
                if (fields.get("login") && fields.get("password")) {
                    user.login = fields["login"].toString().toLowerCase()
                    user.password = passwordEncoder?.encode(fields["password"] as String)
                    user.firstName = fields["firstName"]
                    user.lastName = fields["lastName"]
                    user.email = fields["login"].toString().toLowerCase() + "@hismkii.com"
                    user.activated = true
                    user.langKey = "en"
                    user = userRepository.save(user)
                }

                if (user.id) {
                    employee.user = user
                }
            }

            return new GraphQLRetVal<Employee>(employeeRepository.save(employee), true, "Employee created successfully.")
        } else {

            Employee employee = objectMapper.convertValue(fields, Employee)

            DohPosition position = dohPositionRepository.findById(UUID.fromString(fields['position'] as String)).get()
            if (position) {
                employee.position = position.id
                employee.positionCode = position?.isOthers ? null: position.poscode
                employee.positionCodeOthers = position?.isOthers ? position.poscode: null
                employee.positionType = position?.postdesc
            }

            employee.isActive = true
            employee.department = departmentRepository.findById(departmentId).get()

            employee.employeeNo = "E" + generatorService.getNextValue(GeneratorType.EMPLOYEE_NO) { Long no ->
                StringUtils.leftPad(no.toString(), 6, "0")
            }

            return new GraphQLRetVal<Employee>(employeeRepository.save(employee), true, "Employee updated successfully.")
        }


    }

    @GraphQLMutation
    @Transactional
    GraphQLRetVal<Employee> createLogin(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields

    ) {
        Employee employee = null
        User user = new User()

        employeeRepository.findById(id).ifPresent { employee = it }
        if (employee) {
            if (fields.get("login") && fields.get("password")) {
                user.login = fields["login"].toString().toLowerCase()
                user.password = passwordEncoder?.encode(fields["password"] as String)
                user.firstName = employee.firstName
                user.lastName = employee.lastName
                user.email = fields["login"].toString().toLowerCase() + "@hismkii.com"
                user.activated = true
                user.langKey = "en"
                user = userRepository.save(user)

            }

            if (user.id) {
                employee.user = user
            }
            return new GraphQLRetVal<Employee>(employee, true, "Successfully created user for employee.")
        } else {
            return new GraphQLRetVal<Employee>(null, false, "Failed to create user for employee.")
        }

    }

    @GraphQLMutation
    GraphQLRetVal<String> removeEmployeePermissionOrRole(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "type") String type,
            @GraphQLArgument(name = "name") String name
    ) {
        Employee employee = null
        employeeRepository.findEmployeeById(id).ifPresent { employee = it }
        if (!employee || !employee.user) return new GraphQLRetVal<String>(null, false, "Something went wrong: Failed to do operation.")

        def user = employee.user
        if (type == "ROLE") {
            Authority role = authorityRepository.findOneByName(name)
            user.authorities.remove(role)
            userRepository.save(user)

            return new GraphQLRetVal<String>(null, true, "Successfully removed employee role.")
        } else if (type == "PERMISSION") {
            Permission permission = permissionRepository.findOneByName(name)
            user.permissions.remove(permission)
            userRepository.save(user)
            return new GraphQLRetVal<String>(null, true, "Successfully removed employee permission.")
        } else return new GraphQLRetVal<String>(null, false, "Something went wrong: Failed to do operation.")
    }

    @GraphQLMutation
    GraphQLRetVal<String> setEmployeeRolesAndPermissions(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "roles") List<String> roles,
            @GraphQLArgument(name = "permissions") List<String> permissions
    ) {
        Employee employee = null
        employeeRepository.findEmployeeById(id).ifPresent { employee = it }
        if (!employee || !employee.user) return new GraphQLRetVal<String>(null, false, "Something went wrong: Failed to do operation.")

        if (employee.user && employee.user.id) {
            List<Authority> authorities = authorityRepository.findByNameIn(roles);
            List<Permission> access = permissionRepository.findByNameIn(permissions);

            employee.user.permissions.clear()
            employee.user.authorities.clear()
            employee.user.permissions.addAll(access)
            employee.user.authorities.addAll(authorities)

            userRepository.save(employee.user)

            return new GraphQLRetVal<String>(null, true, "Successfully update user's roles and permissions.")
        }

        return new GraphQLRetVal<String>(null, false, "Something went wrong, Please try again later.")
    }
}
