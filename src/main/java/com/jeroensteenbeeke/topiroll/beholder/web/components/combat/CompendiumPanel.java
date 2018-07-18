package com.jeroensteenbeeke.topiroll.beholder.web.components.combat;

import com.jeroensteenbeeke.hyperion.heinlein.web.components.AjaxIconLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.GlyphIcon;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.CompendiumService;
import com.jeroensteenbeeke.topiroll.beholder.entities.CompendiumEntry;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;

import javax.inject.Inject;
import java.util.List;

public class CompendiumPanel extends CombatModePanel<MapView> {
	@Inject
	private CompendiumService compendiumService;

	private ListView<CompendiumEntry> searchResults;

	private WebMarkupContainer searchResultsContainer;

	private Label article;

	public CompendiumPanel(String id, MapView view, CombatModeCallback callback) {
		super(id, ModelMaker.wrap(view));

		final TextField<String> queryField = new TextField<>("query", Model.of(""));
		queryField.setOutputMarkupId(true);

		Form<TokenInstance> form = new Form<TokenInstance>("form") {
			@Override
			protected void onSubmit() {
				String query = queryField.getModelObject();

				if (query != null && query.length() >= 3) {
					List<CompendiumEntry> entries = compendiumService.performSearch(query);

					searchResults.setModel(ModelMaker.wrapList(entries));
					article.setDefaultModel(Model.of(""));

				}
			}
		};

		queryField.add(new AjaxFormSubmitBehavior(form, "keyup") {


			@Override
			protected void onAfterSubmit(AjaxRequestTarget target) {
				super.onAfterSubmit(target);

				target.add(article, searchResultsContainer);
			}
		});

		form.add(queryField);

		add(form);

		add(article = new Label("article", Model.of("")));
		article.setEscapeModelStrings(false);
		article.setOutputMarkupId(true);

		add(searchResultsContainer = new WebMarkupContainer("results"));
		searchResultsContainer.setOutputMarkupPlaceholderTag(true);
		searchResultsContainer.add(searchResults = new ListView<CompendiumEntry>("options", new ListModel<>()) {
			@Override
			protected void populateItem(ListItem<CompendiumEntry> item) {
				CompendiumEntry entry = item.getModelObject();

				item.add(new Label("title", entry.getTitle()));
				item.add(new Label("path", entry.getOriginalPath()));
				item.add(new AjaxIconLink<CompendiumEntry>("view", item.getModel(), GlyphIcon.check) {
					@Override
					public void onClick(AjaxRequestTarget target) {
						article.setDefaultModel(Model.of(getModelObject().getBody()));
						searchResultsContainer.setVisible(false);
						queryField.setVisible(false);

						target.add(article, searchResultsContainer, queryField);

					}
				});
			}
		});

	}
}
