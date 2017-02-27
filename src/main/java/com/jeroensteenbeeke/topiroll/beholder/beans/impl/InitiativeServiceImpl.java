package com.jeroensteenbeeke.topiroll.beholder.beans.impl;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jeroensteenbeeke.topiroll.beholder.BeholderRegistry;
import com.jeroensteenbeeke.topiroll.beholder.beans.InitiativeService;
import com.jeroensteenbeeke.topiroll.beholder.dao.InitiativeParticipantDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.MapViewDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeLocation;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeParticipant;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeType;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;

@Component
public class InitiativeServiceImpl implements InitiativeService {
	private static final Random DICEMASTER = new Random();

	@Autowired
	private MapViewDAO mapViewDAO;
	
	@Autowired
	private InitiativeParticipantDAO participantDAO;
	
	@Override
	public void hideInitiative(MapView view) {
		view.setInitiativePosition(null);
		mapViewDAO.update(view);
		
		BeholderRegistry.instance.sendToView(view.getId(), view.getInitiativeJS());
	}

	@Override
	public void showInitiative(MapView view, InitiativeLocation location) {
		view.setInitiativePosition(location);
		mapViewDAO.update(view);
		
		BeholderRegistry.instance.sendToView(view.getId(), view.getInitiativeJS());
	}

	@Override
	public void addInitiative(MapView view, String name, int score, InitiativeType type) {
		
		InitiativeParticipant participant = new InitiativeParticipant();
		participant.setName(name);
		participant.setScore(score);
		participant.setTotal(type.determine(DICEMASTER, score));
		participant.setView(view);
		participant.setInitiativeType(type);
		
		participantDAO.save(participant);
		view.getInitiativeParticipants().add(participant);
		
		BeholderRegistry.instance.sendToView(view.getId(), view.getInitiativeJS());
	}
	
	@Override
	public void reroll(MapView view) {
		view.getInitiativeParticipants().forEach(i -> {
			i.setTotal(i.getInitiativeType().determine(DICEMASTER, i.getScore()));
			participantDAO.update(i);
		});
		
		BeholderRegistry.instance.sendToView(view.getId(), view.getInitiativeJS());
	}
	
	@Override
	public void removeParticipant(InitiativeParticipant participant) {
		MapView view = participant.getView();
		participantDAO.delete(participant);
		
		BeholderRegistry.instance.sendToView(view.getId(), view.getInitiativeJS());
	}
	
	
}
