package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.preparation;

import com.jeroensteenbeeke.hyperion.heinlein.web.pages.entity.BSEntityFormPage;
import com.jeroensteenbeeke.topiroll.beholder.beans.impl.ImageResource;
import com.jeroensteenbeeke.topiroll.beholder.entities.Campaign;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.AbstractPageTest;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.*;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

public class CampaignsTest extends AbstractPageTest {
	@Test
	public void test_campaign_visibility() {
		login();

		navigate_to_campaign_page();
		add_campaign_a();
		add_campaign_b();
		activate_first_campaign();

		navigate_back_to_overview();
		navigate_to_maps_page();
		add_folder_a();
		add_folder_b();
		add_map_a();
		add_map_b();

		navigate_back_to_overview();
		navigate_to_portraits_page();
		set_top_portrait_to_active_campaign();
		set_middle_portrait_to_inactive_campaign();

		navigate_back_to_overview();
		navigate_to_playlists_page();
		set_top_playlist_to_active_campaign();
		set_bottom_playlist_to_inactive_campaign();

		navigate_back_to_overview();
		navigate_to_compendium_page();
		create_compendium_item_for_active_campaign();
		create_compendium_item_for_inactive_campaign();

		navigate_back_to_overview();
		navigate_to_tokens_page();
		set_token_to_active_campaign();
		set_token_to_inactive_campaign();

		navigate_back_to_overview();
		navigate_to_exploration_mode();

	}

	private void navigate_to_campaign_page() {
		wicketTester.startPage(OverviewPage.class);

		wicketTester.clickLink("prepare");
		wicketTester.assertRenderedPage(PrepareSessionPage.class);

		wicketTester.clickLink("campaigns");
		wicketTester.assertRenderedPage(CampaignsPage.class);
	}

	private void add_campaign_a() {
		wicketTester.clickLink("add");
		wicketTester.assertRenderedPage(BSEntityFormPage.class);

		wicketTester.getComponentFromLastRenderedPage(
			"entityForm:fields:0:componentPanel:text")
			.setDefaultModelObject("Campaign A");
		wicketTester.clickLink("submit");

		wicketTester.assertRenderedPage(CampaignsPage.class);
		wicketTester.assertLabel("campaigns:1:name", "Campaign A");
	}

	private void add_campaign_b() {
		wicketTester.clickLink("add");
		wicketTester.assertRenderedPage(BSEntityFormPage.class);

		wicketTester.getComponentFromLastRenderedPage(
			"entityForm:fields:0:componentPanel:text")
			.setDefaultModelObject("Campaign B");
		wicketTester.clickLink("submit");

		wicketTester.assertRenderedPage(CampaignsPage.class);
		wicketTester.assertLabel("campaigns:1:name", "Campaign A");
		wicketTester.assertLabel("campaigns:2:name", "Campaign B");
	}

	private void activate_first_campaign() {
		wicketTester.clickLink("campaigns:1:active:link");
		wicketTester.assertRenderedPage(CampaignsPage.class);

		wicketTester.assertLabel("campaigns:1:active", "Active");
	}

	private void navigate_back_to_overview() {
		wicketTester.clickLink("back");
		wicketTester.assertRenderedPage(PrepareSessionPage.class);
	}

	private void navigate_to_maps_page() {
		wicketTester.clickLink("maps");
		wicketTester.assertRenderedPage(PrepareMapsPage.class);
	}

	private void add_folder_a() {
		wicketTester.clickLink("addfolder");
		wicketTester.assertRenderedPage(BSEntityFormPage.class);

		wicketTester.getComponentFromLastRenderedPage(
			"entityForm:fields:0:componentPanel:text")
			.setDefaultModelObject("Folder A");

		@SuppressWarnings("unchecked") DropDownChoice<Campaign> dropdown = (DropDownChoice<Campaign>) wicketTester
			.getComponentFromLastRenderedPage(
				"entityForm:fields:1:componentPanel:dropdown");
		dropdown.setModelObject(dropdown.getChoices().get(0));

		wicketTester.clickLink("submit");

		wicketTester.assertRenderedPage(ViewFolderPage.class);
		wicketTester.clickLink("back");

		wicketTester.assertRenderedPage(PrepareMapsPage.class);
		wicketTester.assertFeedback("feedback",
			"Only showing folders and maps that are tied to the currently active campaign (Campaign A) or not campaign-specific");
		wicketTester.assertLabel("maps:folders:1:campaign", "Campaign A");
	}

	private void add_folder_b() {
		wicketTester.clickLink("addfolder");
		wicketTester.assertRenderedPage(BSEntityFormPage.class);

		wicketTester.getComponentFromLastRenderedPage(
			"entityForm:fields:0:componentPanel:text")
			.setDefaultModelObject("Folder B");

		@SuppressWarnings("unchecked") DropDownChoice<Campaign> dropdown = (DropDownChoice<Campaign>) wicketTester
			.getComponentFromLastRenderedPage(
				"entityForm:fields:1:componentPanel:dropdown");
		dropdown.setModelObject(dropdown.getChoices().get(1));

		wicketTester.clickLink("submit");

		wicketTester.assertRenderedPage(ViewFolderPage.class);
		wicketTester.clickLink("back");

		wicketTester.assertRenderedPage(PrepareMapsPage.class);
		wicketTester.assertFeedback("feedback",
			"Only showing folders and maps that are tied to the currently active campaign (Campaign A) or not campaign-specific");
		wicketTester.assertLabel("maps:folders:1:campaign", "Campaign A");
		wicketTester.assertNotExists("maps:folders:2");
	}

	private void add_map_a() {
		wicketTester.clickLink("addmap");
		wicketTester.assertRenderedPage(UploadMapStep1Page.class);

		File temple = new File(ImageResource.importImage("temple.jpg"));

		FormTester formTester = wicketTester.newFormTester("uploadForm");
		formTester.setFile("file", temple, "image/jpeg");

		wicketTester.clickLink("submit");

		wicketTester.assertRenderedPage(UploadMapStep2Page.class);
		wicketTester.getComponentFromLastRenderedPage("configureForm:name")
			.setDefaultModelObject("Map A");

		@SuppressWarnings("unchecked") DropDownChoice<Campaign> dropdown = (DropDownChoice<Campaign>) wicketTester
			.getComponentFromLastRenderedPage("configureForm:campaign");
		dropdown.setModelObject(dropdown.getChoices().get(0));
		wicketTester.clickLink("submit");

		wicketTester.assertRenderedPage(ViewMapPage.class);
		wicketTester.clickLink("back");

		wicketTester.assertRenderedPage(PrepareMapsPage.class);
		wicketTester.assertFeedback("feedback",
			"Only showing folders and maps that are tied to the currently active campaign (Campaign A) or not campaign-specific");
		wicketTester.assertLabel("maps:folders:1:campaign", "Campaign A");
		wicketTester.assertNotExists("maps:folders:2");

		wicketTester.assertLabel("maps:maps:1:campaign", "-");
		wicketTester.assertLabel("maps:maps:2:campaign", "-");
		wicketTester.assertLabel("maps:maps:3:campaign", "Campaign A");
		wicketTester.assertNotExists("maps:maps:4");

	}

	private void add_map_b() {
		wicketTester.clickLink("addmap");
		wicketTester.assertRenderedPage(UploadMapStep1Page.class);

		File temple = new File(ImageResource.importImage("temple.jpg"));

		FormTester formTester = wicketTester.newFormTester("uploadForm");
		formTester.setFile("file", temple, "image/jpeg");

		wicketTester.clickLink("submit");

		wicketTester.assertRenderedPage(UploadMapStep2Page.class);
		wicketTester.getComponentFromLastRenderedPage("configureForm:name")
			.setDefaultModelObject("Map B");

		@SuppressWarnings("unchecked") DropDownChoice<Campaign> dropdown = (DropDownChoice<Campaign>) wicketTester
			.getComponentFromLastRenderedPage("configureForm:campaign");
		dropdown.setModelObject(dropdown.getChoices().get(1));
		wicketTester.clickLink("submit");

		wicketTester.assertRenderedPage(ViewMapPage.class);
		wicketTester.clickLink("back");

		wicketTester.assertRenderedPage(PrepareMapsPage.class);
		wicketTester.assertFeedback("feedback",
			"Only showing folders and maps that are tied to the currently active campaign (Campaign A) or not campaign-specific");
		wicketTester.assertLabel("maps:folders:1:campaign", "Campaign A");
		wicketTester.assertNotExists("maps:folders:2");

		wicketTester.assertLabel("maps:maps:1:campaign", "-");
		wicketTester.assertLabel("maps:maps:2:campaign", "-");
		wicketTester.assertLabel("maps:maps:3:campaign", "Campaign A");
		wicketTester.assertNotExists("maps:maps:4");

	}

	private void navigate_to_portraits_page() {
		wicketTester.clickLink("portraits");
		wicketTester.assertRenderedPage(PreparePortraitsPage.class);
		wicketTester.assertFeedback("feedback",
			"Only showing portraits that are tied to the currently active campaign (Campaign A) or not campaign-specific");
		wicketTester.assertLabel("portraits:1:campaign", "-");
		wicketTester.assertLabel("portraits:2:campaign", "-");
		wicketTester.assertLabel("portraits:3:campaign", "-");
		wicketTester.assertLabel("portraits:4:campaign", "-");
		wicketTester.assertLabel("portraits:5:campaign", "-");
		wicketTester.assertLabel("portraits:6:campaign", "-");
		wicketTester.assertNotExists("portraits:7:campaign");
	}

	private void set_top_portrait_to_active_campaign() {
		wicketTester.clickLink("portraits:1:edit:link");
		wicketTester.assertRenderedPage(BSEntityFormPage.class);

		@SuppressWarnings("unchecked") DropDownChoice<Campaign> dropdown = (DropDownChoice<Campaign>) wicketTester
			.getComponentFromLastRenderedPage(
				"entityForm:fields:1:componentPanel:dropdown");
		dropdown.setModelObject(dropdown.getChoices().get(0));
		wicketTester.clickLink("submit");

		wicketTester.assertRenderedPage(PreparePortraitsPage.class);
		wicketTester.assertFeedback("feedback",
			"Only showing portraits that are tied to the currently active campaign (Campaign A) or not campaign-specific");
		wicketTester.assertLabel("portraits:1:campaign", "Campaign A");
		wicketTester.assertLabel("portraits:2:campaign", "-");
		wicketTester.assertLabel("portraits:3:campaign", "-");
		wicketTester.assertLabel("portraits:4:campaign", "-");
		wicketTester.assertLabel("portraits:5:campaign", "-");
		wicketTester.assertLabel("portraits:6:campaign", "-");
		wicketTester.assertNotExists("portraits:7:campaign");

	}

	private void set_middle_portrait_to_inactive_campaign() {
		wicketTester.clickLink("portraits:3:edit:link");
		wicketTester.assertRenderedPage(BSEntityFormPage.class);

		@SuppressWarnings("unchecked") DropDownChoice<Campaign> dropdown = (DropDownChoice<Campaign>) wicketTester
			.getComponentFromLastRenderedPage(
				"entityForm:fields:1:componentPanel:dropdown");
		dropdown.setModelObject(dropdown.getChoices().get(1));
		wicketTester.clickLink("submit");

		wicketTester.assertRenderedPage(PreparePortraitsPage.class);
		wicketTester.assertFeedback("feedback",
			"Only showing portraits that are tied to the currently active campaign (Campaign A) or not campaign-specific");
		wicketTester.assertLabel("portraits:1:campaign", "Campaign A");
		wicketTester.assertLabel("portraits:2:campaign", "-");
		wicketTester.assertLabel("portraits:3:campaign", "-");
		wicketTester.assertLabel("portraits:4:campaign", "-");
		wicketTester.assertLabel("portraits:5:campaign", "-");
		wicketTester.assertNotExists("portraits:6:campaign");
	}

	private void navigate_to_playlists_page() {
		wicketTester.clickLink("playlists");
		wicketTester.assertRenderedPage(PrepareMusicPage.class);

		wicketTester.assertFeedback("feedback",
			"Only showing playlists that are tied to the currently active campaign (Campaign A) or not campaign-specific");

		wicketTester.assertLabel("playlists:1:campaign", "-");
		wicketTester.assertLabel("playlists:2:campaign", "-");
		wicketTester.assertNotExists("playlists:3");
	}

	private void set_top_playlist_to_active_campaign() {
		wicketTester.clickLink("playlists:1:edit:link");
		wicketTester.assertRenderedPage(BSEntityFormPage.class);

		@SuppressWarnings("unchecked") DropDownChoice<Campaign> dropdown = (DropDownChoice<Campaign>) wicketTester
			.getComponentFromLastRenderedPage(
				"entityForm:fields:2:componentPanel:dropdown");
		dropdown.setModelObject(dropdown.getChoices().get(0));

		wicketTester.clickLink("submit");
		wicketTester.assertRenderedPage(PrepareMusicPage.class);

		wicketTester.assertFeedback("feedback",
			"Only showing playlists that are tied to the currently active campaign (Campaign A) or not campaign-specific");

		wicketTester.assertLabel("playlists:1:campaign", "-");
		wicketTester.assertLabel("playlists:2:campaign", "Campaign A");
		wicketTester.assertNotExists("playlists:3");
	}

	private void set_bottom_playlist_to_inactive_campaign() {
		wicketTester.clickLink("playlists:1:edit:link");
		wicketTester.assertRenderedPage(BSEntityFormPage.class);

		@SuppressWarnings("unchecked") DropDownChoice<Campaign> dropdown = (DropDownChoice<Campaign>) wicketTester
			.getComponentFromLastRenderedPage(
				"entityForm:fields:2:componentPanel:dropdown");
		dropdown.setModelObject(dropdown.getChoices().get(1));

		wicketTester.clickLink("submit");
		wicketTester.assertRenderedPage(PrepareMusicPage.class);

		wicketTester.assertFeedback("feedback",
			"Only showing playlists that are tied to the currently active campaign (Campaign A) or not campaign-specific");

		wicketTester.assertLabel("playlists:1:campaign", "Campaign A");
		wicketTester.assertNotExists("playlists:2");
	}

	private void navigate_to_compendium_page() {
		wicketTester.clickLink("compendium");
		wicketTester.assertRenderedPage(PrepareCompendiumPage.class);
	}

	private void create_compendium_item_for_active_campaign() {
		wicketTester.clickLink("addentry");
		wicketTester.assertRenderedPage(CompendiumEditorPage.class);

		FormTester formTester = wicketTester.newFormTester("editorForm");
		formTester.setValue("title", "Compendium Entry A");
		formTester.setValue("body", "Compendium Entry A");

		@SuppressWarnings("unchecked") DropDownChoice<Campaign> dropdown = (DropDownChoice<Campaign>) wicketTester
			.getComponentFromLastRenderedPage(
				"editorForm:campaign");
		dropdown.setModelObject(dropdown.getChoices().get(0));

		wicketTester.clickLink("submit");

		wicketTester.assertRenderedPage(PrepareCompendiumPage.class);
		wicketTester.assertLabel("entries:1:title", "Compendium Entry A");
		wicketTester.assertLabel("entries:1:campaign", "Campaign A");
		wicketTester.assertNotExists("entries:2");
	}

	private void create_compendium_item_for_inactive_campaign() {
		wicketTester.clickLink("addentry");
		wicketTester.assertRenderedPage(CompendiumEditorPage.class);

		FormTester formTester = wicketTester.newFormTester("editorForm");
		formTester.setValue("title", "Compendium Entry A");
		formTester.setValue("body", "Compendium Entry A");

		@SuppressWarnings("unchecked") DropDownChoice<Campaign> dropdown = (DropDownChoice<Campaign>) wicketTester
			.getComponentFromLastRenderedPage(
				"editorForm:campaign");
		dropdown.setModelObject(dropdown.getChoices().get(1));

		wicketTester.clickLink("submit");

		wicketTester.assertRenderedPage(PrepareCompendiumPage.class);

		wicketTester.assertLabel("entries:1:title", "Compendium Entry A");
		wicketTester.assertLabel("entries:1:campaign", "Campaign A");
		wicketTester.assertNotExists("entries:2");
	}

	private void navigate_to_tokens_page() {
		wicketTester.clickLink("tokens");
		wicketTester.assertRenderedPage(PrepareTokensPage.class);
	}

	private void set_token_to_active_campaign() {
	}

	private void set_token_to_inactive_campaign() {
	}

	private void navigate_to_exploration_mode() {
	}

}
