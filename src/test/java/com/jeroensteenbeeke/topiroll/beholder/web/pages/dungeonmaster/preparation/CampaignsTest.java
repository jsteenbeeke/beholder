/**
 * This file is part of Beholder
 * (C) 2016-2019 Jeroen Steenbeeke
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
package com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.preparation;

import com.jeroensteenbeeke.hyperion.heinlein.web.pages.entity.BSEntityFormPage;
import com.jeroensteenbeeke.topiroll.beholder.beans.impl.ImageResource;
import com.jeroensteenbeeke.topiroll.beholder.entities.Campaign;
import com.jeroensteenbeeke.topiroll.beholder.entities.TokenDefinition;
import com.jeroensteenbeeke.topiroll.beholder.web.components.OnClickBehavior;
import com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.CompendiumWindow;
import com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.CreateTokenWindow;
import com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.exploration.MapSelectWindow;
import com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.exploration.PortraitsWindow;
import com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.exploration.YoutubePlaylistWindow;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.AbstractPageTest;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.*;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.play.StatefulMapControllerPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.play.combat.CombatControllerPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.play.exploration.ExplorationControllerPage;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

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
		navigate_to_maps_page();
		add_tokens_to_map();

		navigate_back_to_overview();
		navigate_to_exploration_mode();
		check_playlist_window();
		check_compendium_window("preview:explorationNavigator:compendium");
		check_portraits_window();
		check_maps_window();
		check_token_window("preview:dragdrop:preview_body:reveal:newtoken", ExplorationControllerPage.class);

		navigate_to_combat_mode();
		check_compendium_window("preview:dragdrop:preview_body:combatNavigator:compendium");
		check_token_window("preview:dragdrop:preview_body:mapOptions:newtoken", CombatControllerPage.class);
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

		wicketTester.assertLabel("tokens:1:campaign", "-");
		wicketTester.assertLabel("tokens:2:campaign", "-");
		wicketTester.assertLabel("tokens:3:campaign", "-");
		wicketTester.assertLabel("tokens:4:campaign", "-");
		wicketTester.assertNotExists("tokens:5");
	}

	private void set_token_to_active_campaign() {
		wicketTester.clickLink("tokens:1:edit:link");
		wicketTester.assertRenderedPage(BSEntityFormPage.class);

		@SuppressWarnings("unchecked") DropDownChoice<Campaign> dropdown = (DropDownChoice<Campaign>) wicketTester
			.getComponentFromLastRenderedPage(
				"entityForm:fields:1:componentPanel:dropdown");
		dropdown.setModelObject(dropdown.getChoices().get(0));
		wicketTester.clickLink("submit");
		wicketTester.assertRenderedPage(PrepareTokensPage.class);

		wicketTester.assertLabel("tokens:1:campaign", "Campaign A");
		wicketTester.assertLabel("tokens:2:campaign", "-");
		wicketTester.assertLabel("tokens:3:campaign", "-");
		wicketTester.assertLabel("tokens:4:campaign", "-");
		wicketTester.assertNotExists("tokens:5");
	}

	private void set_token_to_inactive_campaign() {
		wicketTester.clickLink("tokens:3:edit:link");
		wicketTester.assertRenderedPage(BSEntityFormPage.class);

		@SuppressWarnings("unchecked") DropDownChoice<Campaign> dropdown = (DropDownChoice<Campaign>) wicketTester
			.getComponentFromLastRenderedPage(
				"entityForm:fields:1:componentPanel:dropdown");
		dropdown.setModelObject(dropdown.getChoices().get(1));
		wicketTester.clickLink("submit");
		wicketTester.assertRenderedPage(PrepareTokensPage.class);

		wicketTester.assertLabel("tokens:1:campaign", "Campaign A");
		wicketTester.assertLabel("tokens:2:campaign", "-");
		wicketTester.assertLabel("tokens:3:campaign", "-");
		wicketTester.assertNotExists("tokens:4");
	}

	private void add_tokens_to_map() {
		wicketTester.clickLink("maps:maps:1:view:link");
		wicketTester.assertRenderedPage(ViewMapPage.class);

		wicketTester.clickLink("addtokens");
		wicketTester.assertRenderedPage(AddTokenInstance1Page.class);

		@SuppressWarnings("unchecked")
		DropDownChoice<TokenDefinition> tokenSelect = (DropDownChoice<TokenDefinition>) wicketTester.getComponentFromLastRenderedPage("configureForm:token");
		Set<Campaign> campaigns = tokenSelect
			.getChoices()
			.stream()
			.map(TokenDefinition::getCampaign)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());

		assertThat(campaigns.size(), equalTo(1));

		wicketTester.clickLink("back");
		wicketTester.assertRenderedPage(ViewMapPage.class);

		wicketTester.clickLink("back");
		wicketTester.assertRenderedPage(PrepareMapsPage.class);
	}


	private void navigate_to_exploration_mode() {
		wicketTester.clickLink("navbar:run");
		wicketTester.assertRenderedPage(RunSessionPage.class);

		wicketTester.clickLink("views:1:exploration");
		wicketTester.assertRenderedPage(ExplorationControllerPage.class);

	}

	private void check_playlist_window() {

		wicketTester.clickLink("preview:explorationNavigator:playlists", true);
		wicketTester.assertComponentOnAjaxResponse(StatefulMapControllerPage.MODAL_ID);

		Component window = wicketTester.getComponentFromLastRenderedPage(StatefulMapControllerPage.MODAL_ID);
		assertThat(window, instanceOf(YoutubePlaylistWindow.class));
		wicketTester.assertLabel("modal:playlists:1:name", "Battle Music");
		wicketTester.assertNotExists("modal:playlists:2");
	}

	private void check_compendium_window(String path) {

		wicketTester.clickLink(path, true);
		wicketTester.assertComponentOnAjaxResponse(StatefulMapControllerPage.MODAL_ID);

		Component window = wicketTester.getComponentFromLastRenderedPage(StatefulMapControllerPage.MODAL_ID);
		assertThat(window, instanceOf(CompendiumWindow.class));

		FormTester formTester = wicketTester.newFormTester("modal:form");
		formTester.setValue("query", "Compendium Entry");

		wicketTester.executeAjaxEvent("modal:form:query", "keyup");

		wicketTester.assertLabel("modal:results:options:0:title", "Compendium Entry A");
		wicketTester.assertNotExists("modal:results:options:1");
	}

	private void check_portraits_window() {
		wicketTester.clickLink("preview:explorationNavigator:portraits", true);
		wicketTester.assertComponentOnAjaxResponse(StatefulMapControllerPage.MODAL_ID);

		Component window = wicketTester.getComponentFromLastRenderedPage(StatefulMapControllerPage.MODAL_ID);
		assertThat(window, instanceOf(PortraitsWindow.class));


		wicketTester.assertLabel("modal:container:portraits:1:name", "Portrait 0");
		wicketTester.assertLabel("modal:container:portraits:2:name", "Portrait 1");
		wicketTester.assertLabel("modal:container:portraits:3:name", "Portrait 3");
		wicketTester.assertLabel("modal:container:portraits:4:name", "Portrait 4");
		wicketTester.assertLabel("modal:container:portraits:5:name", "Portrait 5");
		wicketTester.assertNotExists("modal:container:portraits:6:name");
	}

	private void check_maps_window() {
		wicketTester.clickLink("preview:explorationNavigator:mapselect", true);
		wicketTester.assertComponentOnAjaxResponse(StatefulMapControllerPage.MODAL_ID);

		Component window = wicketTester.getComponentFromLastRenderedPage(StatefulMapControllerPage.MODAL_ID);
		assertThat(window, instanceOf(MapSelectWindow.class));

		wicketTester.assertLabel("modal:folders:0:foldername", "Folder A");
		wicketTester.assertNotExists("modal:folders:1");

		wicketTester.clickLink("modal:rootmaps:1:select");

	}

	private void check_token_window(String path, Class<? extends Page> expectedPage) {
		Page page = wicketTester.getLastRenderedPage();

		assertThat(page, instanceOf(expectedPage));

		List<OnClickBehavior.OnClickAjaxBehavior> onClickBehaviors = wicketTester
			.getComponentFromLastRenderedPage("preview")
			.getBehaviors(OnClickBehavior.OnClickAjaxBehavior.class);

		onClickBehaviors.forEach(wicketTester::executeBehavior);

		wicketTester.clickLink(path, true);
		wicketTester.assertComponentOnAjaxResponse(StatefulMapControllerPage.MODAL_ID);

		Component window = wicketTester.getComponentFromLastRenderedPage(StatefulMapControllerPage.MODAL_ID);
		assertThat(window, instanceOf(CreateTokenWindow.class));

		Component type = window.get("form:type");
		assertThat(type, instanceOf(DropDownChoice.class));

		@SuppressWarnings("unchecked")
		DropDownChoice<TokenDefinition> tokenSelect = (DropDownChoice<TokenDefinition>) type;
		Set<Campaign> campaigns = tokenSelect
			.getChoices()
			.stream()
			.map(TokenDefinition::getCampaign)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());

		assertThat(campaigns.size(), equalTo(1));
	}

	private void navigate_to_combat_mode() {
		wicketTester.clickLink("preview:dragdrop:preview_body:explorationNavigator:combat");
		wicketTester.assertRenderedPage(CombatControllerPage.class);

		wicketTester.debugComponentTrees();
	}
}
