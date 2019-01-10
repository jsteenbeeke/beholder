package com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.exploration;

import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarGroupDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.MapViewDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.ScaledMapDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.BeholderUser;
import com.jeroensteenbeeke.topiroll.beholder.entities.FogOfWarGroup;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.FogOfWarGroupFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.ScaledMapFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.AuthenticatedPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.exploration.ExplorationControllerPage;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.inject.Inject;

public class ExplorationModeMapSwitchHandlerPage extends AuthenticatedPage {

	@Inject
	private FogOfWarGroupDAO groupDAO;

	@Inject
	private MapViewDAO viewDAO;

	@Inject
	private MapService mapService;

	public ExplorationModeMapSwitchHandlerPage(PageParameters parameters) {
		super("Map transition");

		BeholderUser user = getUser();

		long groupId = parameters.get("group").toLong();

		ScaledMapFilter mapFilter = new ScaledMapFilter();
		mapFilter.owner(user);

		FogOfWarGroupFilter filter = new FogOfWarGroupFilter();
		filter.id().equalTo(groupId);
		filter.map().byFilter(mapFilter);

		groupDAO.findByFilter(filter).forEach(group -> {
			MapView view = viewDAO.load(parameters.get("view").toLong()).getOrElseThrow(IllegalArgumentException::new);

			mapService.selectMap(view, group.getMap());

			throw new RestartResponseAtInterceptPageException(new ExplorationControllerPage(view));
		});
	}
}
