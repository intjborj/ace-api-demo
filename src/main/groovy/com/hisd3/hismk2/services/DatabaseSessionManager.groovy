package com.hisd3.hismk2.services

import org.springframework.orm.jpa.EntityManagerFactoryUtils
import org.springframework.orm.jpa.EntityManagerHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionSynchronizationManager

import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceUnit

@Service
class DatabaseSessionManager {
    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

     void bindSession() {
        if (!TransactionSynchronizationManager.hasResource(entityManagerFactory)) {
            EntityManager entityManager = entityManagerFactory.createEntityManager()
            TransactionSynchronizationManager.bindResource(entityManagerFactory, new EntityManagerHolder(entityManager))
        }
    }

     void unbindSession() {
        EntityManagerHolder emHolder = (EntityManagerHolder) TransactionSynchronizationManager
                .unbindResource(entityManagerFactory)
        EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager())
    }
}
