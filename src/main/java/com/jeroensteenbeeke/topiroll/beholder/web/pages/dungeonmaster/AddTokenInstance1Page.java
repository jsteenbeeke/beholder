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
package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster;

import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.jeroensteenbeeke.hyperion.ducktape.web.renderer.LambdaRenderer;
import com.jeroensteenbeeke.hyperion.heinlein.web.resources.TouchPunchJavaScriptReference;
import com.jeroensteenbeeke.hyperion.meld.web.EntityEncapsulator;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.dao.TokenDefinitionDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenBorderType;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenDefinition;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.TokenDefinitionFilter;

public class AddTokenInstance1Page extends AuthenticatedPage {
	private static final long serialVersionUID = 1L;
	private final NumberTextField<Integer> hpField;

	private DropDownChoice<TokenDefinition> tokenSelect;

	private NumberTextField<Integer> amountField;

	private IModel<ScaledMap> mapModel;

	@Inject
	private TokenDefinitionDAO tokenDAO;

	public AddTokenInstance1Page(ScaledMap map) {
		super("Configure map");

		this.mapModel = ModelMaker.wrap(map);

		add(new Link<BeholderUser>("back") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				onBackButtonClicked();

			}
		});

		TokenDefinitionFilter filter = new TokenDefinitionFilter();
		filter.owner().set(getUser());
		filter.name().orderBy(true);

		List<TokenDefinition> tokens = tokenDAO.findByFilter(filter);
		tokenSelect = new DropDownChoice<TokenDefinition>("token",
				tokens.isEmpty()
						? EntityEncapsulator
								.createNullModel(TokenDefinition.class)
						: ModelMaker.wrap(tokens.get(0)),
				ModelMaker.wrapList(tokens),
				LambdaRenderer.of(TokenDefinition::getName));
		tokenSelect.setRequired(true);
		amountField = new NumberTextField<>("amount", Model.of(1));
		amountField.setRequired(true);
		amountField.setMinimum(1);

		hpField = new NumberTextField<>("hp", Model.of((Integer) null), Integer.class);
		hpField.setMinimum(0);

		Form<ScaledMap> configureForm = new Form<ScaledMap>("configureForm",
				mapModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				ScaledMap map = mapModel.getObject();
				TokenDefinition token = tokenSelect.getModelObject();
				Integer amount = amountField.getModelObject();

				setResponsePage(createSecondStepPage(map, token, 1, amount, hpField.getModelObject()));
			}

		};

		configureForm.add(tokenSelect);
		configureForm.add(amountField);
		configureForm.add(hpField);

		add(configureForm);

		add(new SubmitLink("submit", configureForm));
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);

		response.render(JavaScriptHeaderItem
				.forReference(TouchPunchJavaScriptReference.get()));
	}

	@Override
	protected void onDetach() {

		super.onDetach();

		mapModel.detach();
	}

	protected void onBackButtonClicked() {
		setResponsePage(new ViewMapPage(mapModel.getObject()));
	}

	protected AddTokenInstance2Page createSecondStepPage(ScaledMap map,
			TokenDefinition token, int current,
			int amount, Integer hp) {
		return new AddTokenInstance2Page(map, token, TokenBorderType.Enemy, current,
				amount, hp);
	}

}
