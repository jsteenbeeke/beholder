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

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.jeroensteenbeeke.topiroll.beholder.BeholderRegistry;
import com.jeroensteenbeeke.topiroll.beholder.beans.InitiativeService;
import com.jeroensteenbeeke.topiroll.beholder.dao.InitiativeParticipantDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.MapViewDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeLocation;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeParticipant;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeType;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.InitiativeParticipantFilter;

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

		BeholderRegistry.instance.sendToView(view.getId(),
				view.getInitiativeJS());
	}

	@Override
	public void showInitiative(MapView view, InitiativeLocation location) {
		view.setInitiativePosition(location);
		mapViewDAO.update(view);

		BeholderRegistry.instance.sendToView(view.getId(),
				view.getInitiativeJS());
	}

	@Override
	public void addInitiative(MapView view, String name, int score,
			InitiativeType type) {

		InitiativeParticipant participant = new InitiativeParticipant();
		participant.setName(name);
		participant.setScore(score);
		participant.setTotal(type.determine(DICEMASTER, score));
		participant.setView(view);
		participant.setInitiativeType(type);

		participantDAO.save(participant);

		determineOverrideOrder(view);

		BeholderRegistry.instance.sendToView(view.getId(),
				view.getInitiativeJS());
	}

	@Override
	public void setViewInitiativeMargin(MapView view, Integer margin) {
		view.setInitiativeMargin(margin);
		mapViewDAO.update(view);

		BeholderRegistry.instance.sendToView(view.getId(),
				view.getInitiativeJS());
	}

	@Override
	public void reroll(MapView view) {
		view.getInitiativeParticipants().forEach(i -> {
			i.setTotal(
					i.getInitiativeType().determine(DICEMASTER, i.getScore()));
			i.setSelected(false);
			participantDAO.update(i);

		});

		determineOverrideOrder(view);

		selectNext(view);
	}

	private void determineOverrideOrder(MapView view) {
		Multimap<Integer, InitiativeParticipant> participantScores = LinkedHashMultimap
				.create();
		view.getInitiativeParticipants().forEach(i -> {
			participantScores.put(i.getTotal(), i);
		});

		for (Entry<Integer, Collection<InitiativeParticipant>> entry : participantScores
				.asMap().entrySet()) {
			if (entry.getValue().size() > 1) {
				AtomicInteger i = new AtomicInteger(0);
				entry.getValue().stream().sorted((a, b) -> {
					int c = Integer.compare(b.getScore(), a.getScore());
					if (c == 0) {
						c = a.getName().compareTo(b.getName());
					}

					return c;
				}).forEachOrdered(p -> {
					p.setOrderOverride(i.getAndIncrement());
					participantDAO.update(p);
				});
			} else {
				entry.getValue().forEach(p -> {
					p.setOrderOverride(null);
					participantDAO.update(p);
				});
			}

		}
	}

	@Override
	public void removeParticipant(InitiativeParticipant participant) {
		MapView view = participant.getView();
		participantDAO.delete(participant);

		determineOverrideOrder(view);

		BeholderRegistry.instance.sendToView(view.getId(),
				view.getInitiativeJS());
	}

	@Override
	public void setParticipantTotal(InitiativeParticipant participant, int total) {
		participant.setTotal(total);
		participantDAO.update(participant);

		MapView view = participant.getView();

		BeholderRegistry.instance.sendToView(view.getId(),
				view.getInitiativeJS());
	}

	@Override
	public boolean canMoveUp(InitiativeParticipant participant) {
		MapView view = participant.getView();

		return view.getInitiativeParticipants().stream()
				.filter(p -> Objects.equals(p.getTotal(),
						participant.getTotal()))
				.filter(p -> p.getScore() == participant.getScore())
				.filter(p -> !p.equals(participant)).anyMatch(p -> p
						.getOrderOverride() < participant.getOrderOverride());
	}

	@Override
	public boolean canMoveDown(InitiativeParticipant participant) {
		MapView view = participant.getView();

		return view.getInitiativeParticipants().stream()
				.filter(p -> Objects.equals(p.getTotal(),
						participant.getTotal()))
				.filter(p -> p.getScore() == participant.getScore())
				.filter(p -> !p.equals(participant)).anyMatch(p -> p
						.getOrderOverride() > participant.getOrderOverride());
	}

	@Override
	public void moveUp(InitiativeParticipant participant) {
		setOrderOverride(participant, participant.getOrderOverride() - 1);

	}

	@Override
	public void moveDown(InitiativeParticipant participant) {
		setOrderOverride(participant, participant.getOrderOverride() + 1);

	}

	private void setOrderOverride(InitiativeParticipant participant,
			int orderOverride) {
		InitiativeParticipantFilter filter = new InitiativeParticipantFilter();
		MapView view = participant.getView();
		filter.view().set(view);
		filter.orderOverride().equalTo(orderOverride);

		participantDAO.findByFilter(filter).forEach(p -> {
			p.setOrderOverride(participant.getOrderOverride());
			participantDAO.update(p);
		});

		participant.setOrderOverride(orderOverride);
		participantDAO.update(participant);

		BeholderRegistry.instance.sendToView(view.getId(),
				view.getInitiativeJS());
	}

	@Override
	public void select(InitiativeParticipant participant) {
		MapView view = participant.getView();

		view.getInitiativeParticipants().forEach(i -> {
			i.setSelected(false);
			participantDAO.update(i);
		});

		participant.setSelected(true);
		participantDAO.update(participant);

		BeholderRegistry.instance.sendToView(view.getId(),
				view.getInitiativeJS());
	}

	@Override
	public void selectNext(MapView view) {
		List<InitiativeParticipant> participants = view
				.getInitiativeParticipants().stream().sorted(MapView.INITIATIVE_ORDER)
				.collect(Collectors.toList());
		int selected = -1;
		int i = 0;

		for (InitiativeParticipant participant : participants) {
			if (participant.isSelected()) {
				selected = i;
				break;
			}
			i++;
		}

		int next = (selected + 1) % participants.size();

		select(participants.get(next));
	}

}
