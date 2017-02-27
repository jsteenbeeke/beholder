package com.jeroensteenbeeke.topiroll.beholder.web.data;

import java.util.List;

public class InitiativeRenderable implements JSRenderable {
	private boolean show;

	private String position;

	private List<InitiativeParticipantRenderable> participants;

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public List<InitiativeParticipantRenderable> getParticipants() {
		return participants;
	}

	public void setParticipants(
			List<InitiativeParticipantRenderable> participants) {
		this.participants = participants;
	}

	@Override
	public String getType() {
		return "initiative";
	}

}
