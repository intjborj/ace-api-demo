package com.hisd3.hismk2.services.eclaims.generalservices

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.eclaims.EclaimsCaseRef
import com.hisd3.hismk2.domain.eclaims.EclaimsIntegrationAccount
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@TypeChecked
class EcCaseReferenceService extends AbstractDaoService<EclaimsCaseRef>{

    EcCaseReferenceService() {
        super(EclaimsCaseRef.class)
    }

    @Autowired
    ObjectMapper objectMapper

    @Transactional(rollbackFor = Exception.class)
    EclaimsCaseRef UpsertEclaimCaseReference( Map<String, Object> fields, UUID id){
        upsertFromMap(id, fields, { EclaimsCaseRef entity, boolean forInsert ->})
    }



}
