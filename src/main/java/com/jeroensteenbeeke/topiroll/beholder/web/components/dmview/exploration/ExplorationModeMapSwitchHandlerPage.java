package com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.exploration;

import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.MapViewDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.ScaledMapDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.MapView;
import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.AuthenticatedPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.exploration.ExplorationControllerPage;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.inject.Inject;

public class ExplorationModeMapSwitchHandlerPage extends AuthenticatedPage {
	@Inject
	private MapService mapService;

	@Inject
	private ScaledMapDAO mapDAO;

	@Inject
	private MapViewDAO viewDAO;

	public ExplorationModeMapSwitchHandlerPage(PageParameters parameters) {
		super("Map transition");

		ScaledMap map = mapDAO.load(parameters.get("map").toLong()).getOrElseThrow(IllegalArgumentException::new);
		MapView view = viewDAO.load(parameters.get("view").toLong()).getOrElseThrow(IllegalArgumentException::new);

		mapService.selectMap(view, map);

		throw new RestartResponseAtInterceptPageException(new ExplorationControllerPage(view));
	}
}
