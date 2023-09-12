package com.hisd3.hismk2.graphqlservices

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.bms.Room
import com.hisd3.hismk2.domain.inventory.DepartmentItem
import com.hisd3.hismk2.domain.types.Subaccountable
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.bms.RoomRepository
import com.hisd3.hismk2.rest.dto.CoaConfig
import com.hisd3.hismk2.services.GeneratorService
import groovy.transform.Canonical
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLContext
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.data.domain.Page
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Canonical
class DepartmentData {
    UUID id
    String code
    String departmentName
}

@Canonical
class DepartmentTreeItem {
    DepartmentData data
    List<DepartmentTreeItem> children = []
}

@TypeChecked
@Component
@GraphQLApi
class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository

    @Autowired
    private RoomRepository roomRepository

    @Autowired
    GeneratorService generatorService

    @Autowired
    ObjectMapper objectMapper

    @PersistenceContext
    EntityManager entityManager

    //============== All Queries ====================
    def recursiveTraverse(Department department, List<DepartmentTreeItem> nodes) {

        List<DepartmentTreeItem> children = []
        def data = new DepartmentData(department.id, department.departmentCode, department.departmentCode + "-" + department.departmentName)

        if (department.departmentItems) {
            def sorted = department.children.toSorted { a, b ->
                a.code <=> b.code
            }
            sorted.each {
                recursiveTraverse(it, children)
            }
        }


        nodes << new DepartmentTreeItem(data, children)
    }

    @GraphQLQuery(name = "getDepartmentTree", description = "Get All Tree Departments")
    Object getDepartmentTree() {
        List<DepartmentTreeItem> result = []

        def parents = departmentRepository.getParentDepartments().toSorted {
            a, b ->
                a.code <=> b.code
        }

        parents.each {
            recursiveTraverse(it, result)
        }

        result
    }

    String generatePrefixParentDepartment(Department parentDepartment) {
        String code = parentDepartment.departmentCode

        if (parentDepartment.parentDepartment)
            return generatePrefixParentDepartment(parentDepartment.parentDepartment) + code

        return code

    }

    @GraphQLQuery(name = "departments", description = "Get All Departments")
    List<Department> findAll() {
        departmentRepository.findAll().sort { it.departmentName }
    }


	//------------------ accounting ni for includes departments----------------------------
	List<Subaccountable> findAllSortedByCodeAndFlatten(List<UUID> depIds) {

        List<Subaccountable> results = []


        def depts = entityManager.createQuery("Select d from Department d  left join fetch d.children ch left join fetch d.parentDepartment parent where (d.deleted is null or d.deleted = false) and (d.hideAccounting is null or d.hideAccounting = false)",
                Department.class
        ).resultList.toSorted { a, b -> a.departmentCode <=> b.departmentCode }


		def query = departmentRepository.getDepartmentsFlatten()
		if(depIds){
			query = departmentRepository.getDepartmentsFlattenWithIds(depIds)
		}

		 depts =  query.toSorted { a,b -> a.departmentCode <=> b.departmentCode }

        // filter only those who have on parentDepartment
        // get only the leaf
        def leafs = depts.findAll {
            it.children.size() == 0
        }

        def parents = depts.findAll {
            it.children.size() > 0
        }

        leafs.each { dept ->

            results << new Subaccountable() {
                @Override
                String getCode() {
                    return generatePrefixParentDepartment(dept)
                }

                @Override
                String getDescription() {
                    return dept.description
                }

                @Override
                UUID getId() {
                    return dept.id
                }

                @Override
                String getDomain() {
                    return Department.class.name
                }


				@Override
				List<UUID> getDepartment() {
					return null
				}

				@Override
				CoaConfig getConfig() {
					new CoaConfig(show: true, showDepartments: true)
				}

			}
		}

        parents.each { dept ->


            results << new Subaccountable() {
                @Override
                String getCode() {
                    return dept.code
                }

                @Override
                String getDescription() {
                    return dept.description
                }

                @Override
                UUID getId() {
                    return dept.id
                }


				@Override
				String getDomain() {
					return Department.class.name
				}

				@Override
				List<UUID> getDepartment() {
					return null
				}

				@Override
				CoaConfig getConfig() {
					new CoaConfig(show: true, showDepartments: true)
				}
			}
		}


        Set<String> weanduplicate = []


        return results.findAll {
            if (!weanduplicate.contains(it.code)) {
                weanduplicate.add(it.code)
                return true
            }
            return false

        }.toSorted { a, b -> a.code <=> b.code }
    }

    @GraphQLQuery(name = "department", description = "Get Department By Id")
    Department findById(@GraphQLArgument(name = "id") UUID id) {
        return departmentRepository.findById(id).get()
    }

    @GraphQLQuery(name = "departmentPage", description = "Get Department By Id")
    Page<Department> departmentPage(@GraphQLArgument(name = "filter") String filter,
                                    @GraphQLArgument(name = "page") Integer page,
                                    @GraphQLArgument(name = "pageSize") Integer pageSize) {
        return departmentRepository.departmentPage(filter,
                new PageRequest(page, pageSize, Sort.Direction.DESC, "departmentName"))
    }


    @GraphQLQuery(name = "departmentsByFilter", description = "Get Departments by filter")
    List<Department> departmentsByFilter(@GraphQLArgument(name = "filter") String filter) {
        departmentRepository.departmentsByFilter(filter).sort { it.departmentName }
    }

    @GraphQLQuery(name = "departmentsWithDiagnostics", description = "Get Departments with diagnostics")
    List<Department> departmentsWithDiagnostics() {
        departmentRepository.departmentsWithDiagnostics().sort { it.departmentName }
    }
/*
	@GraphQLQuery(name = "allDepartmentsByFilter", description = "Get Departments by filter")
	List<Department> departmentsByFilter(@GraphQLArgument(name = "filter") String filter) {
		departmentRepository.departmentsByFilter(filter).sort { it.departmentName }
	}
*/

    @GraphQLQuery(name = "departmentsWithRooms", description = "Get Departments with Rooms")
    List<Department> getDepartmentWithRooms() {
        departmentRepository.getDepartmentWithRooms()
    }

    @GraphQLQuery(name = "getDepartmentWithTiers", description = "Get Departments with Price Tiers")
    List<Department> getDepartmentWithTiers() {
        departmentRepository.getDepartmentWithTiers()
    }

    @GraphQLQuery(name = "parentDepartments", description = "Get Parent Departments")
    List<Department> getParentDepartments(@GraphQLArgument(name = "id") UUID id) {
        List<Department> list = departmentRepository.getParentDepartments()
        for (dpt in list) {

            for (child in dpt.children) {
                List<DepartmentItem> forRemove = new ArrayList<>()
                for (item in child.departmentItems) {
                    if (item.item.id != id)
                        forRemove.add(item)
                }
                child.departmentItems.removeAll(forRemove)
                //2nd
                for (subChild in child.children) {
                    List<DepartmentItem> forRm = new ArrayList<>()
                    for (item in subChild.departmentItems) {
                        if (item.item.id != id)
                            forRm.add(item)
                    }
                    subChild.departmentItems.removeAll(forRm)

                    //3rd
                    for (sub3rdChild in subChild.children) {
                        List<DepartmentItem> forRm4th = new ArrayList<>()
                        for (item in sub3rdChild.departmentItems) {
                            if (item.item.id != id)
                                forRm4th.add(item)
                        }
                        sub3rdChild.departmentItems.removeAll(forRm4th)
                    }
                }
            }

            List<DepartmentItem> forRemove = new ArrayList<>()
            for (item in dpt.departmentItems) {
                if (item.item.id != id)
                    forRemove.add(item)
            }
            dpt.departmentItems.removeAll(forRemove)
        }
        //departmentRepository.getParentDepartments()
        list
    }

    @GraphQLQuery(name = "getDepartmentsInId", description = "get Selected Department for assigning employee allowance")
    List<Department> getDepartmentsInId(
            @GraphQLArgument(name = "ids") ArrayList<UUID> ids = []
    ) {
        if (ids)
            return departmentRepository.findDepartmentsInIds(ids)
        else return null
    }

    @GraphQLQuery(name = "getSubDepartments", description = "Get Sub Departments")
    List<Department> getSubDepartments() {
        departmentRepository.getSubDepartments()
    }

    @GraphQLQuery(name = "subDepartments", description = "Get Parent Departments")
    List<Department> getSubDepartments(@GraphQLContext Department parentDepartment) {
        departmentRepository.getSubDepartments(parentDepartment.id)
    }

    @GraphQLQuery(name = "isDepartmentCodeUnique", description = "Check if departmentCode exists")
    Boolean findOneByDepartmentCode(@GraphQLArgument(name = "departmentCode") String departmentCode) {
        return !departmentRepository.findOneByDepartmentCode(departmentCode)
    }

    @GraphQLQuery(name = "isDepartmentNameUnique", description = "Check if departmentName exists")
    Boolean findOneByDepartmentName(@GraphQLArgument(name = "departmentName") String departmentName) {
        return !departmentRepository.findOneByDepartmentName(departmentName)
    }

    @GraphQLQuery(name = "availableDepartmentRooms", description = "Get all available Department Rooms")
    List<Room> getAvailableRoomsByDepartment(@GraphQLContext Department department) {
        return roomRepository.getAvailableRoomsByDepartment(department.id).sort { it.roomName }
    }

    @GraphQLQuery(name = "departmentRooms", description = "Get all Department Rooms")
    List<Room> getRoomsByDepartment(@GraphQLContext Department department) {
        return roomRepository.getRoomsByDepartment(department.id).sort { it.roomName }
    }

    @GraphQLQuery(name = "receivingDepartments", description = "Get all Receiving Department")
    List<Department> getReceivingDepartment() {
        departmentRepository.getReceivingDepartment()
    }

    @GraphQLQuery(name = "stockRequestDepartments", description = "Get all Department that allow request")
    List<Department> getStockRequestDepartments() {
        departmentRepository.getMedicationStockRequestDepartment()
    }

    @GraphQLQuery(name = "purchasingDepartments", description = "Get all Purchasing Department")
    List<Department> getPurchasingDepartment() {
        departmentRepository.getPurchasingDepartment()
    }

    @GraphQLQuery(name = "defaultClearingDepartments", description = "Get all Purchasing Department")
    List<Department> defaultClearingDepartments() {
        departmentRepository.defaultClearingDepartments()
    }

    //==================================Mutation ============
    @GraphQLMutation
    Department upsertDepartment(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    ) {
        if (id) {
            def department = departmentRepository.findById(id).get()
            objectMapper.updateValue(department, fields)

            return departmentRepository.save(department)
        } else {

            def department1 = objectMapper.convertValue(fields, Department)

            return departmentRepository.save(department1)
        }
    }
}
