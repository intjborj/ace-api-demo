package com.hisd3.hismk2.graphqlservices.pms

import com.hisd3.hismk2.domain.pms.FileAttachment
import com.hisd3.hismk2.repository.pms.FileAttachmentRepository
import com.hisd3.hismk2.rest.OrderslipResource
import com.hisd3.hismk2.rest.dto.AttachmentDto
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@TypeChecked
@Component
@GraphQLApi
class FileAttachmentService {

	@Autowired
	FileAttachmentRepository fileAttachmentRepository

	@Autowired
	OrderslipResource orderslipResource

	@GraphQLQuery(name = "allAttachment", description = "Get Results by PatientId")
	List<AttachmentDto> allAttachment(@GraphQLArgument(name = "id") UUID id) {

		println "STARTED LOADING"
		List<AttachmentDto> allResults =  new ArrayList<AttachmentDto>()

		List<FileAttachment> res = fileAttachmentRepository.findByPid(id)
		println "ATTACHMENT COUNT : "+res.size()
		int x = 0;
		res.each{
			attachement ->
//				println "X : "+ x
				x++;
				try{
					AttachmentDto s = new AttachmentDto()

					s.id = attachement.id
					//s.base64 = orderslipResource.getThumbAttachment(attachement.id)
					s.urlPath= attachement.urlPath
					s.mimetype = attachement.mimetype
					s.fileName= attachement.fileName
					s.desc= attachement.desc

					allResults.add(s)
				}
				catch (Exception e)
				{
					println "ERROR in LOOP"
					e.printStackTrace()
				}

		}

		return allResults
	}
}

