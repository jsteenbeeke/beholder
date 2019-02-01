package com.jeroensteenbeeke.topiroll.beholder.dao.hibernate;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.InitiativeParticipantConditionDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeParticipantCondition;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.InitiativeParticipantConditionFilter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

@Repository
@Scope(value = "request")
class InitiativeParticipantConditionDAOImpl extends HibernateDAO<InitiativeParticipantCondition, InitiativeParticipantConditionFilter> implements InitiativeParticipantConditionDAO {

}
