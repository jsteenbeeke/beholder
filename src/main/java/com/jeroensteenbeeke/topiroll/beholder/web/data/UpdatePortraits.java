package com.jeroensteenbeeke.topiroll.beholder.web.data;

import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;

import java.util.List;
import java.util.stream.Collectors;

public class UpdatePortraits implements JSRenderable {
	public UpdatePortraits() {
	}

	public UpdatePortraits(MapView view) {
		this.portraits = view.getPortraitVisibilities().stream().map(JSPortrait::new).collect(Collectors.toList());
	}

	private List<JSPortrait> portraits;

	public List<JSPortrait> getPortraits() {
		return portraits;
	}

	public void setPortraits(List<JSPortrait> portraits) {
		this.portraits = portraits;
	}

	@Override
	public String getType() {
		return "portraits";
	}
}
