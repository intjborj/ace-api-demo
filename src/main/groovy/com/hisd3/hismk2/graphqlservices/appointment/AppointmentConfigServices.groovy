package com.hisd3.hismk2.graphqlservices.appointment

import com.hisd3.hismk2.domain.appointment.AppointmentConfig
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@GraphQLApi
class AppointmentConfigServices extends AbstractDaoService<AppointmentConfig> {

	@Autowired
	GeneratorService generatorService


	AppointmentConfigServices() {
		super(AppointmentConfig.class)
	}
	
	@GraphQLQuery(name = "configById")
	AppointmentConfig configById(
			@GraphQLArgument(name = "id") UUID id
	) {
		if(id){
			findOne(id)
		}else{
			return null;
		}

	}
	
	@GraphQLQuery(name = "configAllList", description = "config List")
	List<AppointmentConfig> configAllList() {
		findAll().sort { it.createdDate }
	}

	@GraphQLQuery(name = "configTimeActive", description = "config List")
	List<AppointmentConfig> configTimeActive(@GraphQLArgument(name = "status") Boolean status) {
		createQuery("Select c from AppointmentConfig c where c.status = :status",
				[status: status]).resultList.sort { it.timeStart }
	}

	@GraphQLQuery(name = "configListPage")
	Page<AppointmentConfig> configListPage(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		String query = '''Select c from AppointmentConfig c where
						lower(c.code) like lower(concat('%',:filter,'%'))'''

		String countQuery = '''Select count(c) from AppointmentConfig c where
							lower(c.code) like lower(concat('%',:filter,'%'))'''

		Map<String, Object> params = new HashMap<>()
		params.put('filter', filter)

		query += ''' ORDER BY c.timeStart'''

		getPageable(query, countQuery, page, size, params)
	}
	
	//mutation
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertConfig")
	AppointmentConfig upsertConfig(
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "id") UUID id
	) {
		
		upsertFromMap(id, fields, { AppointmentConfig entity, boolean forInsert ->
			if(forInsert){
				entity.code = generatorService.getNextValue(GeneratorType.TNO, {
					return "T-" + StringUtils.leftPad(it.toString(), 6, "0")
				})
			}
		})
	}
	
}
