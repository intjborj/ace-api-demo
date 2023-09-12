package com.hisd3.hismk2.graphqlservices.cashiering

import com.hisd3.hismk2.domain.cashiering.ChequeEncashment
import com.hisd3.hismk2.domain.cashiering.ChequeEncashmentSupportingDoc
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.services.EntityObjectMapperService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.ArrayUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

import javax.transaction.Transactional

@Transactional(rollbackOn = [Exception.class])
@Component
@GraphQLApi
class ChequeEncashmentSupportDocServices extends AbstractDaoService<ChequeEncashmentSupportingDoc> {

    ChequeEncashmentSupportDocServices() {
        super(ChequeEncashmentSupportingDoc.class)
    }

    @Autowired
    EntityObjectMapperService entityObjectMapperService


    @Transactional
    @GraphQLMutation
    ChequeEncashmentSupportingDoc addChequeEncashmentSuppDoc(
            @GraphQLArgument(name="cESupportingDocu") CESupportingDocu cESupportingDocu,
            @GraphQLArgument(name = "attachment") MultipartFile attachment,
            @GraphQLArgument(name="chequeEncashment") ChequeEncashment chequeEncashment

    ){
        ChequeEncashmentSupportingDoc suppDoc = new ChequeEncashmentSupportingDoc()
        if (attachment && !attachment.empty) {
            suppDoc.referenceNo = cESupportingDocu['referenceNo']
            suppDoc.description = cESupportingDocu['description']
            suppDoc.chequeEncashment = chequeEncashment
            suppDoc.filename = attachment.originalFilename
            suppDoc.attachment = ArrayUtils.toObject(IOUtils.toByteArray(attachment.inputStream))
            save(suppDoc)
        }
        return  suppDoc
    }

}
