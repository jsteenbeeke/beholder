package com.jeroensteenbeeke.topiroll.beholder.beans.impl;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jeroensteenbeeke.hyperion.solstice.api.Any;
import com.jeroensteenbeeke.topiroll.beholder.beans.IMapRenderer;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapRenderers;

@Component
class MapRenderersImpl implements MapRenderers {
	@Autowired
	private Any<IMapRenderer> renderers;
	
	@Override
	public List<IMapRenderer> getRenderers() {
		return renderers.stream().sorted(Comparator.comparing(IMapRenderer::getPriority)).collect(Collectors.toList());
	}
}
