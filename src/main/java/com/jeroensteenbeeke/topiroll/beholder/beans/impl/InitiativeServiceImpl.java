/**
 * This file is part of Beholder
 * (C) 2016 Jeroen Steenbeeke
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.beans.impl;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.jeroensteenbeeke.topiroll.beholder.dao.InitiativeParticipantConditionDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.InitiativeParticipantConditionFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.jeroensteenbeeke.topiroll.beholder.BeholderRegistry;
import com.jeroensteenbeeke.topiroll.beholder.beans.InitiativeService;
import com.jeroensteenbeeke.topiroll.beholder.dao.InitiativeParticipantDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.MapViewDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.InitiativeParticipantFilter;

import javax.annotation.Nonnull;

@Component
public class InitiativeServiceImpl implements InitiativeService {
	private static final Random DICEMASTER = new Random();

	@Autowired
	private MapViewDAO mapViewDAO;

	@Autowired
	private InitiativeParticipantDAO participantDAO;

	@Autowired
	private InitiativeParticipantConditionDAO conditionDAO;

	@Override
	public void hideInitiative(@Nonnull MapView view) {
		view.setInitiativePosition(null);
		mapViewDAO.update(view);

		BeholderRegistry.instance.sendToView(view.getId(),
				view.getInitiativeJS());
	}

	@Override
	public void showInitiative(@Nonnull MapView view, @Nonnull InitiativeLocation location) {
		view.setInitiativePosition(location);
		mapViewDAO.update(view);

		BeholderRegistry.instance.sendToView(view.getId(),
				view.getInitiativeJS());
	}

	@Override
	public void addInitiative(@Nonnull MapView view, @Nonnull String name, int score,
							  @Nonnull InitiativeType type) {

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
	public void setViewInitiativeMargin(@Nonnull MapView view, @Nonnull Integer margin) {
		view.setInitiativeMargin(margin);
		mapViewDAO.update(view);

		BeholderRegistry.instance.sendToView(view.getId(),
				view.getInitiativeJS());
	}

	@Override
	public void reroll(@Nonnull MapView view) {
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
		view.getInitiativeParticipants().forEach(i -> participantScores.put(i.getTotal(), i));

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
	public void removeParticipant(@Nonnull InitiativeParticipant participant) {

		removeConditionsFromParticipant(participant);

		MapView view = participant.getView();
		participantDAO.delete(participant);

		determineOverrideOrder(view);

		BeholderRegistry.instance.sendToView(view.getId(),
				view.getInitiativeJS());
	}

	@Override
	public void setParticipantTotal(@Nonnull InitiativeParticipant participant, int total) {
		participant.setTotal(total);
		participantDAO.update(participant);

		MapView view = participant.getView();

		BeholderRegistry.instance.sendToView(view.getId(),
				view.getInitiativeJS());
	}

	@Override
	public boolean canMoveUp(@Nonnull InitiativeParticipant participant) {
		MapView view = participant.getView();

		int partTotal = Optional.ofNullable(participant.getTotal()).orElse(0);
		int partOverride = Optional.ofNullable(participant.getOrderOverride()).orElse(partTotal);

		return view.getInitiativeParticipants().stream()
				.filter(p -> Objects.equals(p.getTotal(),
						participant.getTotal()))
				.filter(p -> p.getScore() == participant.getScore())
				.filter(p -> !p.equals(participant))
				.anyMatch(p -> {
					int total = Optional.ofNullable(p.getTotal()).orElse(0);
					int override = Optional.ofNullable(p.getOrderOverride()).orElse(total);

					return override < partOverride;
				});
	}

	@Override
	public boolean canMoveDown(@Nonnull InitiativeParticipant participant) {
		MapView view = participant.getView();

		int partTotal = Optional.ofNullable(participant.getTotal()).orElse(0);
		int partOverride = Optional.ofNullable(participant.getOrderOverride()).orElse(partTotal);

		return view.getInitiativeParticipants().stream()
				.filter(p -> Objects.equals(p.getTotal(),
						participant.getTotal()))
				.filter(p -> p.getScore() == participant.getScore())
				.filter(p -> !p.equals(participant))
				.anyMatch(p -> {
					int total = Optional.ofNullable(p.getTotal()).orElse(0);
					int override = Optional.ofNullable(p.getOrderOverride()).orElse(total);

					return override < partOverride;
				});
	}

	@Override
	public void moveUp(@Nonnull InitiativeParticipant participant) {
		setOrderOverride(participant, Optional.ofNullable(participant.getOrderOverride()).orElse(0) - 1);

	}

	@Override
	public void moveDown(@Nonnull InitiativeParticipant participant) {
		setOrderOverride(participant, Optional.ofNullable(participant.getOrderOverride()).orElse(0) + 1);

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
	public void select(@Nonnull InitiativeParticipant participant) {
		MapView view = participant.getView();

		view.getInitiativeParticipants().forEach(i -> {
			i.setSelected(false);
			participantDAO.update(i);
		});

		participant.setSelected(true);
		participantDAO.update(participant);

		InitiativeParticipantConditionFilter filter = new InitiativeParticipantConditionFilter();
		filter.participant(participant);

		Set<InitiativeParticipantCondition> toDelete = new HashSet<>();
		Set<InitiativeParticipantCondition> toUpdate = new HashSet<>();

		for (InitiativeParticipantCondition condition : conditionDAO.findByFilter(filter)) {
			Integer turnsRemaining = condition.getTurnsRemaining();

			if (turnsRemaining != null) {
				turnsRemaining--;

				if (turnsRemaining <= 0) {
					toDelete.add(condition);
				} else {
					condition.setTurnsRemaining(turnsRemaining);
					toUpdate.add(condition);
				}
			}
		}

		toUpdate.forEach(conditionDAO::update);
		toDelete.forEach(conditionDAO::delete);

		participantDAO.flush();

		BeholderRegistry.instance.sendToView(view.getId(),
				view.getInitiativeJS());
	}

	@Override
	public void selectNext(@Nonnull MapView view) {
		List<InitiativeParticipant> participants = view
				.getInitiativeParticipants().stream().sorted(MapView.INITIATIVE_ORDER)
				.collect(Collectors.toList());

		if (participants.isEmpty()) {
			return;
		}

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

	@Override
	public void markAsPlayer(@Nonnull InitiativeParticipant participant) {
		participant.setPlayer(true);
		participantDAO.update(participant);
	}

	@Override
	public void markAsNonPlayer(@Nonnull InitiativeParticipant participant) {
		participant.setPlayer(false);
		participantDAO.update(participant);
	}

	@Override
	public void clearNonPlayers(@Nonnull MapView view) {
		Set<InitiativeParticipant> toDelete = view.getInitiativeParticipants().stream().filter(i -> !i.isPlayer()).collect(Collectors.toSet());

		toDelete.forEach(this::removeConditionsFromParticipant);
		toDelete.forEach(participantDAO::delete);
		toDelete.forEach(view.getInitiativeParticipants()::remove);
	}

	private void removeConditionsFromParticipant(InitiativeParticipant participant) {
		InitiativeParticipantConditionFilter filter = new InitiativeParticipantConditionFilter();
		filter.participant(participant);

		Set<InitiativeParticipantCondition> conditionsToDelete = new HashSet<>();

		for (InitiativeParticipantCondition condition : conditionDAO.findByFilter(filter)) {
			conditionsToDelete.add(condition);
		}

		conditionsToDelete.forEach(conditionDAO::delete);
	}

	@Override
	public void createCondition(InitiativeParticipant participant, String description, Integer turnsRemaining) {
		InitiativeParticipantCondition condition = new InitiativeParticipantCondition();
		condition.setParticipant(participant);
		condition.setDescription(description);
		condition.setTurnsRemaining(turnsRemaining);

		conditionDAO.save(condition);
		conditionDAO.flush();
	}

	@Override
	public void updateCondition(InitiativeParticipantCondition condition, String description, Integer turnsRemaining) {
		condition.setDescription(description);
		condition.setTurnsRemaining(turnsRemaining);

		conditionDAO.update(condition);
		conditionDAO.flush();
	}
}
