package com.jeroensteenbeeke.topiroll.beholder.web.components.combat;

import com.jeroensteenbeeke.hyperion.ducktape.web.components.TypedPanel;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;

import javax.inject.Inject;

public class ApplyTokenDamagePanel extends CombatModePanel<TokenInstance> {
	@Inject
	private MapService mapService;

	public ApplyTokenDamagePanel(String id, TokenInstance instance) {
		super(id, ModelMaker.wrap(instance));
	}

}
