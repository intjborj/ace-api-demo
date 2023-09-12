package com.hisd3.hismk2.graphqlservices.base

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.billing.Billing
import com.hisd3.hismk2.domain.types.JaversResolvable
import com.hisd3.hismk2.graphqlservices.versioning.Hisd3EntityVersionInfoDetail
import com.hisd3.hismk2.graphqlservices.versioning.Hisd3EntityVersionInfoHistory
import com.hisd3.hismk2.security.SecurityUtils
import org.javers.core.Javers
import org.javers.core.metamodel.object.CdoSnapshot
import org.javers.core.metamodel.object.InstanceId
import org.javers.core.metamodel.object.SnapshotType
import org.javers.repository.jql.QueryBuilder
import org.springframework.beans.factory.annotation.Autowired

import javax.persistence.EntityManager
import java.time.ZoneId
import java.time.format.DateTimeFormatter

abstract class AbstractDaoServiceJavers<T extends  Serializable> extends AbstractDaoService<T> {

    @Autowired
    Javers javers

    AbstractDaoServiceJavers(Class<T> classType) {
        super(classType)
    }

    @Autowired
    EntityManager entityManager

    @Override
    T save(T entity) {
        def e = super.save(entity) as T
        javers.commit(SecurityUtils.currentLogin(),e)
        return e
    }

    List<Hisd3EntityVersionInfoHistory> getSnapshotHistory(T entity,boolean  includeInitial=true) {

        List<Hisd3EntityVersionInfoHistory> result = []
        QueryBuilder jqlQuery = QueryBuilder.byInstance(entity)
        List<CdoSnapshot> snapshots = javers.findSnapshots(jqlQuery.build())




        snapshots.findAll{includeInitial || it.type != SnapshotType.INITIAL  }.each { snapShot->

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
