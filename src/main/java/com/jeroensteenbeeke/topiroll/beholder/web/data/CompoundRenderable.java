package com.jeroensteenbeeke.topiroll.beholder.web.data;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class CompoundRenderable implements JSRenderable {
	private List<JSRenderable> renderables;

	public CompoundRenderable() {
	}

	public CompoundRenderable(JSRenderable... renderables) {
		this.renderables = ImmutableList.copyOf(renderables);
	}

	public List<JSRenderable> getRenderables() {
		return renderables;
	}

	public void setRenderables(List<JSRenderable> renderables) {
		this.renderables = renderables;
	}

	@Override
	public String getType() {
		return "compound";
	}
}
