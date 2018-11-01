package com.jeroensteenbeeke.topiroll.beholder.web.components.combat;

import com.jeroensteenbeeke.hyperion.heinlein.web.components.AjaxIconLink;
import com.jeroensteenbeeke.hyperion.heinlein.web.components.GlyphIcon;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.CompendiumService;
import com.jeroensteenbeeke.topiroll.beholder.entities.CompendiumEntry;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.PinnedCompendiumEntry;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
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

public class CompendiumPanel extends CombatModePanel<CompendiumEntry> {
	private final AjaxLink<CompendiumEntry> unpinLink;

	private final AjaxLink<CompendiumEntry> pinLink;

	@Inject
	private CompendiumService compendiumService;

	private ListView<CompendiumEntry> searchResults;

	private WebMarkupContainer searchResultsContainer;

	private Label article;

	public CompendiumPanel(String id, CompendiumEntry entry, CombatModeCallback callback) {
		super(id, entry != null ? ModelMaker.wrap(entry, true) : ModelMaker.wrap(CompendiumEntry.class));

		final TextField<String> queryField = new TextField<>("query", Model.of(""));
		queryField.setOutputMarkupId(true);

		Form<TokenInstance> form = new Form<TokenInstance>("form") {
			@Override
			protected void onSubmit() {
				String query = queryField.getModelObject();

				if (query != null && query.length() >= 2) {
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
		form.setVisible(entry == null);
		add(form);

		add(article = new Label("article", Model.of(entry != null ? entry.getBody() : "")));
		article.setEscapeModelStrings(false);
		article.setOutputMarkupId(true);

		add(pinLink = new AjaxLink<CompendiumEntry>("pin", getModel()) {
			@Override
			public void onClick(AjaxRequestTarget target) {
				compendiumService.pinEntry(BeholderSession.get().getUser(), getModelObject());

				unpinLink.setVisible(true);
				pinLink.setVisible(false);

				target.add(pinLink, unpinLink);

				callback.refreshMenus(target);
			}
		});
		pinLink.setVisible(entry != null &&
				entry.getPinnedBy().stream().map(PinnedCompendiumEntry::getPinnedBy).noneMatch(pb -> pb.equals(BeholderSession.get().getUser())));
		pinLink.setOutputMarkupPlaceholderTag(true);

		add(unpinLink = new AjaxLink<CompendiumEntry>("unpin", getModel()) {
			@Override
			public void onClick(AjaxRequestTarget target) {
				compendiumService.unpinEntry(BeholderSession.get().getUser(), getModelObject());

				unpinLink.setVisible(false);
				pinLink.setVisible(true);

				target.add(pinLink, unpinLink);

				callback.refreshMenus(target);
			}
		});
		unpinLink.setVisible(entry != null &&
				entry.getPinnedBy().stream().map(PinnedCompendiumEntry::getPinnedBy).anyMatch(pb -> pb.equals(BeholderSession.get().getUser())));
		unpinLink.setOutputMarkupPlaceholderTag(true);


		add(searchResultsContainer = new WebMarkupContainer("results"));
		searchResultsContainer.setOutputMarkupPlaceholderTag(true);
		searchResultsContainer.setVisible(entry == null);
		searchResultsContainer.add(searchResults = new ListView<CompendiumEntry>("options", new ListModel<>()) {
			@Override
			protected void populateItem(ListItem<CompendiumEntry> item) {
				CompendiumEntry entry = item.getModelObject();

				item.add(new Label("title", entry.getTitle()));
				item.add(new Label("path", entry.getOriginalPath()));
				item.add(new AjaxIconLink<CompendiumEntry>("view", item.getModel(), GlyphIcon.check) {
					@Override
					public void onClick(AjaxRequestTarget target) {
						CompendiumEntry entry = item.getModelObject();
						CompendiumPanel.this.setModelObject(entry);
						article.setDefaultModel(Model.of(entry.getBody()));
						searchResultsContainer.setVisible(false);
						queryField.setVisible(false);

						pinLink.setVisible(entry.getPinnedBy().stream().map(PinnedCompendiumEntry::getPinnedBy).noneMatch(pb -> pb.equals(BeholderSession.get().getUser())));
						unpinLink.setVisible(entry.getPinnedBy().stream().map(PinnedCompendiumEntry::getPinnedBy).anyMatch(pb -> pb.equals(BeholderSession.get().getUser())));

						target.add(article, searchResultsContainer, queryField, pinLink, unpinLink);

					}
				});
			}
		});

	}
}
