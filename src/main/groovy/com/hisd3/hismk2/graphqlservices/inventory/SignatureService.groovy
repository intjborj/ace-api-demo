package com.hisd3.hismk2.graphqlservices.inventory

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.User
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.inventory.Signature
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.inventory.SignatureRepository
import com.hisd3.hismk2.rest.dto.QueryErrorException
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@GraphQLApi
@TypeChecked
class SignatureService extends AbstractDaoService<Signature> {

	SignatureService() {
		super(Signature.class)
	}
	
	@Autowired
	ObjectMapper objectMapper
	
	@Autowired
	GeneratorService generatorService
	
	@Autowired
	UserRepository userRepository
	
	@Autowired
	EmployeeRepository employeeRepository

	@Autowired
	SignatureRepository signatureRepository


	//===========mutation====================//
	@Transactional(rollbackFor = QueryErrorException.class) //
	@GraphQLMutation(name = "upsertSignature", description = "Insert/Update Signature")
	Signature upsertSignature(
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "id") UUID id
	) {

		User user = userRepository.findOneByLogin(SecurityUtils.currentLogin())
		Employee employee = employeeRepository.findOneByUser(user)
		def forInsert = objectMapper.convertValue(fields, Signature)



		try {
			if(forInsert.currentUsers){
				Signature upsert = signatureRepository.findActiveSignatureDept(employee.department.id,forInsert.signatureType)
				if(upsert)
				{
					upsert.currentUsers = false
					save(upsert)
				}
			}

			if(id){
				Signature upsert = findOne(id)

				if(forInsert.sequence != null) {
					def exisitngSeq = signatureRepository.findSignatureSequenceDept(employee.department.id, forInsert.sequence, forInsert.signatureType)

					if(exisitngSeq != null){
						Signature existUpsert = findOne(exisitngSeq.id)
						existUpsert.sequence = upsert.sequence
						save(existUpsert)
					}
				}
				else {
					Integer numberofSignature = signatureRepository.countNumberofSignature(employee.department.id, forInsert.signatureType)
					forInsert.sequence = numberofSignature + 1
				}

				upsert.signatureHeader = forInsert.signatureHeader
				upsert.signaturePerson = forInsert.signaturePerson
				upsert.signaturePosition = forInsert.signaturePosition
				upsert.currentUsers = forInsert.currentUsers
                upsert.sequence = forInsert.sequence
                save(upsert)
				return upsert

			}
			else {
				Signature upsert = new Signature()

				if(forInsert.sequence != null) {
					def exisitngSeq = signatureRepository.findSignatureSequenceDept(employee.department.id, forInsert.sequence, forInsert.signatureType)

					if(exisitngSeq != null){
						Integer numberofSignature = signatureRepository.countNumberofSignature(employee.department.id, forInsert.signatureType)
						Signature existUpsert = findOne(exisitngSeq.id)
						existUpsert.sequence = numberofSignature + 1
						save(existUpsert)
					}
				}
				else {
					Integer numberofSignature = signatureRepository.countNumberofSignature(employee.department.id, forInsert.signatureType)
					forInsert.sequence = numberofSignature + 1
				}

				upsert.department = employee.department
				upsert.signatureType = forInsert.signatureType
				upsert.signatureHeader = forInsert.signatureHeader
				upsert.signaturePerson = forInsert.signaturePerson
				upsert.signaturePosition = forInsert.signaturePosition
                upsert.currentUsers = forInsert.currentUsers
                upsert.sequence = forInsert.sequence
                save(upsert)
				return upsert
			}


		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}

	}

	@GraphQLQuery(name = "signatureList", description = "List of Signature per type")
	List<Signature> signatureList(@GraphQLArgument(name = "type") String type) {
		User user = userRepository.findOneByLogin(SecurityUtils.currentLogin())
		Employee employee = employeeRepository.findOneByUser(user)
		createQuery("Select f from Signature f where f.signatureType = :type and f.department.id = :departmentID",
						 [
								 type:type,
								 departmentID:employee.department.id,
						 ] as Map<String, Object>).resultList.sort { it.sequence }
	}

	@GraphQLQuery(name = "signatureListFilter", description = "List of Signature per type")
	List<Signature> signatureListFilter(@GraphQLArgument(name = "type") String type, @GraphQLArgument(name = "filter") String filter) {
		User user = userRepository.findOneByLogin(SecurityUtils.currentLogin())
		Employee employee = employeeRepository.findOneByUser(user)
		createQuery("Select f from Signature f where f.signatureType = :type and f.department.id = :departmentID and (lower(f.signaturePerson) like lower(concat('%',:filter,'%')) or lower(f.signatureHeader) like lower(concat('%',:filter,'%')))",
				[
						type:type,
						departmentID:employee.department.id,
						filter:filter,
				] as Map<String, Object>).resultList.sort { it.sequence }
	}

	@GraphQLQuery(name = "findOneSignature", description = "find signature by id")
	Signature findOneSignature(@GraphQLArgument(name = "id") UUID id) {
		return this.findOne(id)
	}
}
