package com.jeroensteenbeeke.topiroll.beholder.beans;

import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeLocation;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeParticipant;
import com.jeroensteenbeeke.topiroll.beholder.entities.InitiativeType;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;

public interface InitiativeService {
	void hideInitiative(MapView view);
	
	void showInitiative(MapView view, InitiativeLocation location);
	
	void addInitiative(MapView view, String name, int score, InitiativeType type);

	void reroll(MapView view);
	
	void removeParticipant(InitiativeParticipant participant);
}
