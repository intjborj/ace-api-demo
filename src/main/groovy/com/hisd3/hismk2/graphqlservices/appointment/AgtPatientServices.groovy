package com.hisd3.hismk2.graphqlservices.appointment

import com.hisd3.hismk2.domain.appointment.AgtPatient
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
import java.time.Duration

@Service
@GraphQLApi
class AgtPatientServices extends AbstractDaoService<AgtPatient> {

	@Autowired
	GeneratorService generatorService


	AgtPatientServices() {
		super(AgtPatient.class)
	}
	
	@GraphQLQuery(name = "agtPatientById")
	AgtPatient agtPatientById(
			@GraphQLArgument(name = "id") UUID id
	) {
		if(id){
			findOne(id)
		}else{
			return null;
		}

	}
	
	@GraphQLQuery(name = "agtPatientAll")
	List<AgtPatient> agtPatientList() {
		findAll().sort { it.fullName }
	}

//	@GraphQLQuery(name = "configTimeActive", description = "config List")
//	List<AgtPatient> configTimeActive(@GraphQLArgument(name = "status") Boolean status) {
//		createQuery("Select c from AgtPatient c where c.status = :status",
//				[status: status]).resultList.sort { it.timeStart }
//	}

	@GraphQLQuery(name = "agtPatientPage")
	Page<AgtPatient> agtPatientPage(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		String query = '''Select p from AgtPatient p where
						lower(p.fullName) like lower(concat('%',:filter,'%'))'''

		String countQuery = '''Select count(p) from AgtPatient p where
							lower(p.fullName) like lower(concat('%',:filter,'%'))'''

		Map<String, Object> params = new HashMap<>()
		params.put('filter', filter)

		query += ''' ORDER BY p.fullName'''

		getPageable(query, countQuery, page, size, params)
	}

	@GraphQLQuery(name = "getPatient")
	AgtPatient getPatient(
			@GraphQLArgument(name = "email") String email,
			@GraphQLArgument(name = "password") String password
	) {
		createQuery("Select c from AgtPatient c where (c.emailAddress = :email or c.contactNo = :email) and c.secretKey = :password",
				[email: email, password: password]).resultList.find()
	}

	@GraphQLQuery(name = "checkPatientDuplicate")
	Integer checkPatientDuplicate(
			@GraphQLArgument(name = "email") String email,
			@GraphQLArgument(name = "contact") String contact
	) {
		def count = createCountQuery("Select count(s) from AgtPatient s where s.emailAddress = '${email}' or s.contactNo = '${contact}'").resultList
		return count[0];
	}
	
	//mutation
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "agtPatientUpsert")
	AgtPatient agtPatientUpsert(
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "id") UUID id
	) {
		upsertFromMap(id, fields, { AgtPatient entity, boolean forInsert ->
			if(entity.dob) {
				entity.dob = entity.dob.plusDays(1);
			}
		})
	}

	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "agtResetPassword")
	AgtPatient agtResetPassword(
			@GraphQLArgument(name = "id") UUID id
	) {
		def up = findOne(id)
		up.secretKey = "password123"
		save(up)
	}
	
}
