package com.hisd3.hismk2.graphqlservices.versioning

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.pms.Patient
import com.hisd3.hismk2.domain.types.JaversResolvable
import com.hisd3.hismk2.repository.pms.CaseRepository
import com.hisd3.hismk2.repository.pms.PatientRepository
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.BooleanUtils
import org.javers.core.Javers
import org.javers.core.metamodel.object.CdoSnapshot
import org.javers.core.metamodel.object.InstanceId
import org.javers.core.metamodel.object.SnapshotType
import org.javers.repository.jql.QueryBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.persistence.EntityManager
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Service
@GraphQLApi
class PatientVersioningService {

    @Autowired
    Javers javers

    @Autowired
    PatientRepository patientRepository

    @Autowired
    CaseRepository caseRepository

    @Autowired
    EntityManager entityManager


    @GraphQLQuery(name="patientSnapshotHistory")
    List<Hisd3EntityVersionInfoHistory> getPatientSnapshotHistory(@GraphQLArgument(name = "patientId") UUID patientId,
                                                                  @GraphQLArgument(name = "includeInitial")  Boolean  includeInitial) {

        List<Hisd3EntityVersionInfoHistory> result = []

        QueryBuilder jqlQuery = QueryBuilder.byInstanceId(patientId,Patient.class)

        List<CdoSnapshot> snapshots = javers.findSnapshots(jqlQuery.build())

        snapshots.findAll{ BooleanUtils.isTrue(includeInitial) || it.type != SnapshotType.INITIAL  }.each {snapShot->

            def rec  = new  Hisd3EntityVersionInfoHistory()
            rec.author = snapShot.commitMetadata.author
            rec.modifiedDateTime  = snapShot.commitMetadata.commitDateInstant

            snapShot.changed.each {property ->

                if(!(property in ["lastModifiedBy","lastModifiedDate"])){
                    Object newValue = snapShot.getPropertyValue(property)


                    if(newValue instanceof InstanceId){

                        Class targetClass = Class.forName(newValue.typeName)
                        def entityLookup = entityManager.find(targetClass, UUID.fromString(newValue.cdoId.toString()))
                        // Allows Foreign Keys to be resolvable to String... implements JaversResolvable
                        if(entityLookup instanceof JaversResolvable){
                            String resolveValue = entityLookup.resolveEntityForJavers()
                            rec.details << new Hisd3EntityVersionInfoDetail(property,resolveValue)
                        }
                        else {
                            rec.details << new Hisd3EntityVersionInfoDetail(property,newValue)
                        }
                    }
                    else {
                        // just do toString
                        rec.details << new Hisd3EntityVersionInfoDetail(property,newValue)
                    }
                }

            }

            result << rec
        }


        boolean  hasInitial = false
        snapshots.findAll {  it.type == SnapshotType.INITIAL }.each {snapShot->

            hasInitial = true
            def formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a")
            def rec  = new  Hisd3EntityVersionInfoHistory()
            rec.author = snapShot.commitMetadata.author
            rec.modifiedDateTime  = snapShot.commitMetadata.commitDateInstant

            rec.details << new Hisd3EntityVersionInfoDetail("createdDate",
                    rec.modifiedDateTime.atZone(ZoneId.systemDefault()).format(formatter))

            result << rec
        }

        if(!hasInitial){

            def entity = patientRepository.findById(patientId).get()

            if(entity instanceof AbstractAuditingEntity){
                // if it has audit information


                def formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a")
                def rec  = new  Hisd3EntityVersionInfoHistory()
                rec.author =  entity.createdBy
                rec.modifiedDateTime  =entity.createdDate

                rec.details << new Hisd3EntityVersionInfoDetail("createdDate",
                        rec.modifiedDateTime.atZone(ZoneId.systemDefault()).format(formatter))

                result << rec
            }

        }


        result
    }

    @GraphQLQuery(name="patientCaseSnapshotHistory")
    List<Hisd3EntityVersionInfoHistory> patientCaseSnapshotHistory(@GraphQLArgument(name = "caseId") UUID caseId,
                                                                   @GraphQLArgument(name = "includeInitial")  Boolean  includeInitial) {

        List<Hisd3EntityVersionInfoHistory> result = []

        QueryBuilder jqlQuery = QueryBuilder.byInstanceId(caseId, Case.class)

        List<CdoSnapshot> snapshots = javers.findSnapshots(jqlQuery.build())

        snapshots.findAll{ BooleanUtils.isTrue(includeInitial) || it.type != SnapshotType.INITIAL  }.each { snapShot->

            def rec  = new  Hisd3EntityVersionInfoHistory()
            rec.author = snapShot.commitMetadata.author
            rec.modifiedDateTime  = snapShot.commitMetadata.commitDateInstant

            snapShot.changed.each {property ->

                if(!(property in ["lastModifiedBy","lastModifiedDate"])){
                    Object newValue = snapShot.getPropertyValue(property)


                    if(newValue instanceof InstanceId){

                        Class targetClass = Class.forName(newValue.typeName)
                        def entityLookup = entityManager.find(targetClass, UUID.fromString(newValue.cdoId.toString()))
                        // Allows Foreign Keys to be resolvable to String... implements JaversResolvable
                        if(entityLookup instanceof JaversResolvable){
                            String resolveValue = entityLookup.resolveEntityForJavers()
                            rec.details << new Hisd3EntityVersionInfoDetail(property,resolveValue)
                        }
                        else {
                            rec.details << new Hisd3EntityVersionInfoDetail(property,newValue)
                        }
                    }
                    else {
                        // just do toString
                        rec.details << new Hisd3EntityVersionInfoDetail(property,newValue)
                    }

                }

            }

            result << rec
        }


        boolean  hasInitial = false
        snapshots.findAll {  it.type == SnapshotType.INITIAL }.each {snapShot->

            hasInitial = true
            def formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a")
            def rec  = new  Hisd3EntityVersionInfoHistory()
            rec.author = snapShot.commitMetadata.author
            rec.modifiedDateTime  = snapShot.commitMetadata.commitDateInstant

            rec.details << new Hisd3EntityVersionInfoDetail("createdDate",
                    rec.modifiedDateTime.atZone(ZoneId.systemDefault()).format(formatter))

            result << rec
        }

        if(!hasInitial){

            def entity = caseRepository.findById(caseId).get()

            if(entity instanceof AbstractAuditingEntity){
                // if it has audit information


                def formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a")
                def rec  = new  Hisd3EntityVersionInfoHistory()
                rec.author =  entity.createdBy
                rec.modifiedDateTime  =entity.createdDate

                rec.details << new Hisd3EntityVersionInfoDetail("createdDate",
                        rec.modifiedDateTime.atZone(ZoneId.systemDefault()).format(formatter))

                result << rec
            }

        }


        result
    }

}
