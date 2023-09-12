package com.hisd3.hismk2.repository.base

import com.hisd3.hismk2.dao.base.AbstractJpaDao
import com.hisd3.hismk2.dao.base.IGenericDao
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Scope
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
class GenericJpaDao<T extends Serializable>
		extends AbstractJpaDao<T> implements IGenericDao<T> {

}
