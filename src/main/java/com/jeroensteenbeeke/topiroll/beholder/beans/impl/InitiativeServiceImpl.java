/**
 * This file is part of Beholder
 * (C) 2016 Jeroen Steenbeeke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
