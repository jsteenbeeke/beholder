package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.preparation;

import com.jeroensteenbeeke.hyperion.solstice.data.IByFunctionModel;
import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.hyperion.webcomponents.core.form.choice.LambdaRenderer;
import com.jeroensteenbeeke.topiroll.beholder.dao.CompendiumEntryDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.Campaign;
import com.jeroensteenbeeke.topiroll.beholder.entities.CompendiumEntry;
import com.jeroensteenbeeke.topiroll.beholder.util.compendium.Compendium;
import com.jeroensteenbeeke.topiroll.beholder.web.model.CampaignsModel;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.AuthenticatedPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import javax.inject.Inject;

public class CompendiumEditorPage extends AuthenticatedPage {
	private static final long serialVersionUID = 7287344433060871122L;
	@Inject
	private CompendiumEntryDAO entryDAO;

	public CompendiumEditorPage(CompendiumEntry entry) {
		super(entry.isSaved() ? entry.getTitle() : "New compendium entry");

		add(new Link<Void>("back") {
			private static final long serialVersionUID = 8110920677336998708L;

			@Override
			public void onClick() {
				setResponsePage(new PrepareCompendiumPage());
			}
		});

		final TextField<String> title = new TextField<>("title");

		IModel<Campaign> campaignSelectionModel = entry.getCampaign() != null ? ModelMaker.wrap(entry.getCampaign()) :  ModelMaker.wrap(Campaign.class);
		final DropDownChoice<Campaign> campaignChoice = new DropDownChoice<>(
			"campaign", campaignSelectionModel, new CampaignsModel(),
			LambdaRenderer.of(Campaign::getName));
		campaignChoice.setRequired(true);
		campaignChoice.setNullValid(true);

		final TextArea<String> editor = new TextArea<>("body");
		final Label preview = new Label("preview", new LoadableDetachableModel<String>() {
			private static final long serialVersionUID = 7181063826075933047L;

			@Override
			protected String load() {
				String input = editor.getModelObject();
				return input == null || input.isEmpty() ? "": Compendium.textToHtml(input).getText();
			}
		});
		preview.setEscapeModelStrings(false);
		editor.add(new AjaxFormComponentUpdatingBehavior("keydown") {
			private static final long serialVersionUID = 5215260020943053881L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(preview);
			}
		});

		Form<CompendiumEntry> editorForm = new Form<CompendiumEntry>("editorForm", new CompoundPropertyModel<>(ModelMaker.wrap(entry))) {
			private static final long serialVersionUID = -6269752739914161681L;

			@Override
			protected void onSubmit() {
				CompendiumEntry e = getModelObject();

				if (e.isSaved()) {
					entryDAO.update(e);
				} else {
					e.setAuthor(getUser());
					entryDAO.save(e);
				}

				setResponsePage(new PrepareCompendiumPage());
			}
		};


		preview.setOutputMarkupId(true);

		editorForm.add(preview);
		editorForm.add(editor);
		editorForm.add(title);
		editorForm.add(campaignChoice);
		add(editorForm);

		add(new SubmitLink("submit", editorForm));
	}
}
