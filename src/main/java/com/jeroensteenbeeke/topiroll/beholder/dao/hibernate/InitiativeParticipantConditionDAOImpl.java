package com.jeroensteenbeeke.topiroll.beholder.dao.hibernate;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeParticipantCondition;
import com.jeroensteenbeeke.topiroll.beholder.dao.InitiativeParticipantConditionDAO;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Scope;

	@Repository
	@Scope(value="request")  
class InitiativeParticipantConditionDAOImpl extends HibernateDAO<InitiativeParticipantCondition> implements InitiativeParticipantConditionDAO {

}
