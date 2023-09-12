package com.hisd3.hismk2.services


import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
@TypeChecked
class DepartmentCategoryService {
	@Autowired
	JdbcTemplate jdbcTemplate
	
	List<String> allcategory() {
		String sql = "SELECT DISTINCT group_category FROM public.departments"
		List<Map<String, Object>> result = jdbcTemplate.queryForList(sql)
		List<String> forReturn = new ArrayList<>()
		for (Map<String, Object> it in result) {
			String z = it.get("group_category") as String
			forReturn.add(z)
		}
		
		return forReturn
	}
}
