package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.preparation;

import com.jeroensteenbeeke.hyperion.heinlein.web.components.BootstrapPagingNavigator;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.GlyphIcon;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.IconLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BSEntityFormPage;
import com.jeroensteenbeeke.hyperion.heinlein.web.pages.BSEntityPageSettings;
import com.jeroensteenbeeke.hyperion.solstice.data.FilterDataProvider;
import com.jeroensteenbeeke.lux.ActionResult;
import com.jeroensteenbeeke.topiroll.beholder.beans.AmazonS3Service;
import com.jeroensteenbeeke.topiroll.beholder.dao.TokenDefinitionDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenDefinition;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.TokenDefinitionFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.AuthenticatedPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.PrepareSessionPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.UploadTokenStep1Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;

import javax.inject.Inject;

public class PrepareTokensPage extends AuthenticatedPage {

	private static final long serialVersionUID = 1L;


	@Inject
	private TokenDefinitionDAO tokenDAO;

	@Inject
	private AmazonS3Service amazon;

	public PrepareTokensPage() {
		super("Prepare tokens");

		TokenDefinitionFilter tokenFilter = new TokenDefinitionFilter();
		tokenFilter.owner().set(getUser());
		tokenFilter.name().orderBy(true);

		DataView<TokenDefinition> tokenView = new DataView<TokenDefinition>(
				"tokens", FilterDataProvider.of(tokenFilter, tokenDAO)) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<TokenDefinition> item) {
				TokenDefinition definition = item.getModelObject();

				item.add(new Label("name", definition.getName()));
				item.add(
						new Label("size", String.format("%d squares (diameter)",
								definition.getDiameterInSquares())));
				item.add(new ContextImage("thumb",
						definition.getImageUrl()));
				item.add(new IconLink<TokenDefinition>("edit", item.getModel(),
						GlyphIcon.edit) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						TokenDefinition tokenDefinition = getModelObject();

						BSEntityPageSettings<TokenDefinition> settings =
								tokenDefinition.getInstances().isEmpty() ?
										edit(tokenDefinition).onPage("Edit Token")
												.using(tokenDAO) :
										edit(tokenDefinition).onPage("Edit Token").withoutDelete()
												.using(tokenDAO);
						setResponsePage(new BSEntityFormPage<TokenDefinition>(
								settings) {

							private static final long serialVersionUID = 1L;

							@Override
							protected ActionResult onBeforeDelete(TokenDefinition entity) {
								if (entity.getAmazonKey() != null) {
									return amazon.removeImage(entity.getAmazonKey());
								}

								return ActionResult.ok();
							}

							@Override
							protected void onDeleted() {
								setResponsePage(new PrepareSessionPage());
							}

							@Override
							protected void onSaved(TokenDefinition entity) {
								setResponsePage(new PrepareSessionPage());

							}

							@Override
							protected void onCancel(TokenDefinition entity) {
								setResponsePage(new PrepareSessionPage());
							}

						});

					}
				});

			}

		};

		tokenView.setItemsPerPage(25);
		add(tokenView);
		add(new BootstrapPagingNavigator("tokennav", tokenView));

		add(new Link<TokenDefinition>("addtoken") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new UploadTokenStep1Page());

			}
		});

		add(new Link<Void>("back") {
			@Override
			public void onClick() {
				setResponsePage(new PrepareSessionPage());
			}
		});

	}
}
