package com.hisd3.hismk2.graphqlservices.inventory

import com.hisd3.hismk2.domain.inventory.DocumentTypes
import com.hisd3.hismk2.repository.inventory.DocumentTypeRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@GraphQLApi
@TypeChecked
class DocumentTypeService {
	
	@Autowired
	DocumentTypeRepository documentTypeRepository
	
	@GraphQLQuery(name = "documentTypeList", description = "List of Document Type")
	List<DocumentTypes> getDocumentTypes() {
		return documentTypeRepository.findAll().sort { it.createdDate }
	}
	
}
