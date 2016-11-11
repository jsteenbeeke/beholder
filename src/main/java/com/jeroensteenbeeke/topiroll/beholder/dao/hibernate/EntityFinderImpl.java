package com.jeroensteenbeeke.topiroll.beholder.dao.hibernate;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jeroensteenbeeke.hyperion.data.BaseEntityFinder;
import com.jeroensteenbeeke.hyperion.data.DomainObject;

@Component("EntityFinder")
@Transactional(propagation = Propagation.NESTED, readOnly = true)
@Scope("request")
class EntityFinderImpl implements BaseEntityFinder {
	@PersistenceContext(type=PersistenceContextType.TRANSACTION)
	private EntityManager entityManager;

		@Override
	public <T extends DomainObject> T getEntity(Class<T> entityClass,
			Serializable id) {
		return (T) entityManager.find(entityClass, id);
	}
}