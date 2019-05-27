package com.jeroensteenbeeke.topiroll.beholder.web.components.dmview;

import com.jeroensteenbeeke.hyperion.heinlein.web.components.AjaxIconLink;
import com.jeroensteenbeeke.hyperion.icons.fontawesome.FontAwesome;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.beans.CompendiumService;
import com.jeroensteenbeeke.topiroll.beholder.dao.PinnedCompendiumEntryDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.CompendiumEntry;
import com.jeroensteenbeeke.topiroll.beholder.entities.PinnedCompendiumEntry;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenInstance;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.PinnedCompendiumEntryFilter;
import com.jeroensteenbeeke.topiroll.beholder.util.compendium.Compendium;
import com.jeroensteenbeeke.topiroll.beholder.web.BeholderSession;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewCallback;
import com.jeroensteenbeeke.topiroll.beholder.web.components.DMViewPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;

import javax.inject.Inject;
import java.util.List;

public class CompendiumWindow extends DMViewPanel<CompendiumEntry> {
	private static final long serialVersionUID = -8850815391817528946L;

	private final AjaxLink<CompendiumEntry> unpinLink;

	private final AjaxLink<CompendiumEntry> pinLink;

	@Inject
	private CompendiumService compendiumService;

	@Inject
	private PinnedCompendiumEntryDAO pinnedCompendiumEntryDAO;

	private ListView<CompendiumEntry> searchResults;

	private WebMarkupContainer searchResultsContainer;

	private Label article;

	public CompendiumWindow(String id, CompendiumEntry entry, DMViewCallback callback) {
		super(id, entry != null ? ModelMaker.wrap(entry, true) : ModelMaker.wrap(CompendiumEntry.class));
		setOutputMarkupId(true);

		final TextField<String> queryField = new TextField<>("query", Model.of(""));
		queryField.setOutputMarkupId(true);

		Form<TokenInstance> form = new Form<TokenInstance>("form") {
			private static final long serialVersionUID = -3259438171822347137L;

			@Override
			protected void onSubmit() {
				String query = queryField.getModelObject();

				if (query != null && query.length() >= 2) {
					List<CompendiumEntry> entries = compendiumService.performSearch(BeholderSession.get().getUser(), query);

					searchResults.setModel(ModelMaker.wrapList(entries));
					article.setDefaultModel(Model.of(""));

				}
			}
		};

		queryField.add(new AjaxFormSubmitBehavior(form, "keyup") {


			private static final long serialVersionUID = -5642292261275099960L;

			@Override
			protected void onAfterSubmit(AjaxRequestTarget target) {
				super.onAfterSubmit(target);

				target.add(article, searchResultsContainer);
			}
		});

		form.add(queryField);
		form.setVisible(entry == null);
		add(form);

		add(article = new Label("article", new ArticleModel()));
		article.setEscapeModelStrings(false);
		article.setOutputMarkupId(true);

		add(pinLink = new AjaxLink<>("pin", getModel()) {
			private static final long serialVersionUID = -2109978219691009998L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				compendiumService.pinEntry(BeholderSession.get().getUser(),
					getModelObject());

				unpinLink.setVisible(true);
				pinLink.setVisible(false);

				target.add(pinLink, unpinLink);

				callback.refreshMenus(target);
			}

			@Override
			protected void onConfigure() {
				super.onConfigure();

				CompendiumEntry e = CompendiumWindow.this.getModelObject();

				if (e != null) {
					PinnedCompendiumEntryFilter filter = new PinnedCompendiumEntryFilter();
					filter.entry(e);
					filter.pinnedBy(BeholderSession.get().getUser());

					setVisible(pinnedCompendiumEntryDAO.countByFilter(filter) == 0);
				} else {
					setVisible(false);
				}
			}
		});
		pinLink.setOutputMarkupPlaceholderTag(true);

		add(unpinLink = new AjaxLink<>("unpin", getModel()) {
			private static final long serialVersionUID = -7792768458461434418L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				compendiumService.unpinEntry(BeholderSession.get().getUser(),
					getModelObject());

				unpinLink.setVisible(false);
				pinLink.setVisible(true);

				target.add(pinLink, unpinLink);

				callback.refreshMenus(target);
			}

			@Override
			protected void onConfigure() {
				super.onConfigure();

				CompendiumEntry e = CompendiumWindow.this.getModelObject();

				if (e != null) {
					PinnedCompendiumEntryFilter filter = new PinnedCompendiumEntryFilter();
					filter.entry(e);
					filter.pinnedBy(BeholderSession.get().getUser());

					setVisible(pinnedCompendiumEntryDAO.countByFilter(filter) > 0);
				} else {
					setVisible(false);
				}
			}
		});
		unpinLink.setOutputMarkupPlaceholderTag(true);

		add(searchResultsContainer = new WebMarkupContainer("results"));
		searchResultsContainer.setOutputMarkupPlaceholderTag(true);
		searchResultsContainer.setVisible(entry == null);
		searchResultsContainer.add(searchResults = new ListView<>("options", new ListModel<>()) {
			private static final long serialVersionUID = 4606249780770838168L;

			@Override
			protected void populateItem(ListItem<CompendiumEntry> item) {
				CompendiumEntry entry = item.getModelObject();

				item.add(new Label("title", entry.getTitle()));
				item.add(new Label("path", entry.getOriginalPath() != null ?
					entry.getOriginalPath() :
					"-"));
				item.add(new AjaxIconLink<>("view", item.getModel(),
					FontAwesome.check) {
					private static final long serialVersionUID = 9153225937487202796L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						CompendiumEntry entry = item.getModelObject();
						CompendiumWindow.this.setModelObject(entry);
						CompendiumWindow.this.detach();

						article.setDefaultModel(new ArticleModel());

						searchResultsContainer.setVisible(false);
						queryField.setVisible(false);

						pinLink.setVisible(entry.getPinnedBy().stream().map(PinnedCompendiumEntry::getPinnedBy).noneMatch(
							pb -> pb.equals(BeholderSession.get().getUser())));
						unpinLink.setVisible(entry.getPinnedBy().stream().map(PinnedCompendiumEntry::getPinnedBy).anyMatch(
							pb -> pb.equals(BeholderSession.get().getUser())));

						target.add(article, searchResultsContainer, queryField,
							pinLink, unpinLink);

					}
				});
			}
		});

	}

	private class ArticleModel extends LoadableDetachableModel<String> {
		private static final long serialVersionUID = -4502747446370774883L;

		@Override
		protected String load() {
			CompendiumEntry compendiumEntry = CompendiumWindow.this.getModelObject();

			if (compendiumEntry == null) {
				return "";
			} else {
				if (compendiumEntry.getAuthor() == null) {
					return compendiumEntry.getBody();
				} else {
					return Compendium.textToHtml(compendiumEntry.getBody()).getText();
				}
			}
		}

	}
}
