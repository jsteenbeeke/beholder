package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.preparation;

import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.dao.CompendiumEntryDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.CompendiumEntry;
import com.jeroensteenbeeke.topiroll.beholder.util.compendium.Compendium;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.AuthenticatedPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;

import javax.inject.Inject;

public class CompendiumEditorPage extends AuthenticatedPage {
	@Inject
	private CompendiumEntryDAO entryDAO;

	public CompendiumEditorPage(CompendiumEntry entry) {
		super(entry.isSaved() ? entry.getTitle() : "New compendium entry");

		add(new Link<Void>("back") {
			@Override
			public void onClick() {
				setResponsePage(new PrepareCompendiumPage());
			}
		});

		final TextField<String> title = new TextField<>("title");
		final TextArea<String> editor = new TextArea<>("body");
		final Label preview = new Label("preview", new LoadableDetachableModel<String>() {
			@Override
			protected String load() {
				String input = editor.getModelObject();
				return input == null || input.isEmpty() ? "": Compendium.textToHtml(input).getText();
			}
		});
		preview.setEscapeModelStrings(false);
		editor.add(new AjaxFormComponentUpdatingBehavior("keydown") {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(preview);
			}
		});

		Form<CompendiumEntry> editorForm = new Form<CompendiumEntry>("editorForm", new CompoundPropertyModel<>(ModelMaker.wrap(entry))) {
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
		add(editorForm);

		add(new SubmitLink("submit", editorForm));
	}
}
