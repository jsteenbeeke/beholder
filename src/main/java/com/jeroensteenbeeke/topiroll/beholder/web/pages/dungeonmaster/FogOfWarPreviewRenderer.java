package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster;

import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarCircle;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarRect;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarTriangle;
import com.jeroensteenbeeke.topiroll.beholder.entities.visitors.FogOfWarShapeVisitor;

public class FogOfWarPreviewRenderer implements FogOfWarShapeVisitor<String> {
	@Override
	public String visit(FogOfWarCircle fogOfWarCircle) {
		return null;
	}

	@Override
	public String visit(FogOfWarRect fogOfWarRect) {
		return null;
	}

	@Override
	public String visit(FogOfWarTriangle fogOfWarTriangle) {
		return null;
	}
}
