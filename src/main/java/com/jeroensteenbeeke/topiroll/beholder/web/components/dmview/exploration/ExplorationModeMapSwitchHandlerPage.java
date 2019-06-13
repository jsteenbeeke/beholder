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
package com.jeroensteenbeeke.topiroll.beholder.web.components.dmview.exploration;

import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.FogOfWarGroupDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.MapViewDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.ScaledMapDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.FogOfWarGroupFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.ScaledMapFilter;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.AuthenticatedPage;
import com.jeroensteenbeeke.topiroll.beholder.web.pages.dungeonmaster.exploration.ExplorationControllerPage;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.inject.Inject;

public class ExplorationModeMapSwitchHandlerPage extends AuthenticatedPage {

	private static final long serialVersionUID = -7800933386967525745L;
	@Inject
	private FogOfWarGroupDAO groupDAO;

	@Inject
	private MapViewDAO viewDAO;

	@Inject
	private ScaledMapDAO mapDAO;

	@Inject
	private MapService mapService;

	public ExplorationModeMapSwitchHandlerPage(PageParameters parameters) {
		super("Map transition");

		BeholderUser user = getUser();

		Long groupId = parameters.get("group").toOptionalLong();

		ScaledMapFilter mapFilter = new ScaledMapFilter();
		mapFilter.owner(user);

		if (groupId != null) {
			FogOfWarGroupFilter filter = new FogOfWarGroupFilter();
			filter.id().equalTo(groupId);
			filter.map().byFilter(mapFilter);

			groupDAO.findByFilter(filter).forEach(group -> {
				MapView view = viewDAO.load(parameters.get("view").toLong()).getOrElseThrow(IllegalArgumentException::new);

				mapService.selectMapAndSetFocus(view, group);

				throw new RestartResponseAtInterceptPageException(new ExplorationControllerPage(view, group));
			});
		} else {
			Long mapId = parameters.get("map").toLong();

			mapFilter.id(mapId);

			mapDAO.getUniqueByFilter(mapFilter).forEach(map -> {
				MapView view = viewDAO.load(parameters.get("view").toLong()).getOrElseThrow(IllegalArgumentException::new);

				mapService.selectMap(view, map);

				throw new RestartResponseAtInterceptPageException(new ExplorationControllerPage(view));

			});

		}
	}
}
