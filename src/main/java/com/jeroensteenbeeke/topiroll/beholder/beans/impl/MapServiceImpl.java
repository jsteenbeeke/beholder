package com.jeroensteenbeeke.topiroll.beholder.beans.impl;

import java.awt.Dimension;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jeroensteenbeeke.hyperion.util.ImageUtil;
import com.jeroensteenbeeke.hyperion.util.TypedActionResult;
import com.jeroensteenbeeke.topiroll.beholder.beans.MapService;
import com.jeroensteenbeeke.topiroll.beholder.dao.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.*;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.FogOfWarGroupVisibilityFilter;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.FogOfWarShapeVisibilityFilter;
import com.jeroensteenbeeke.topiroll.beholder.util.Calculations;

@Component
@Scope(value = "request")
class MapServiceImpl implements MapService {
	private static final int MAXIMUM_RENDERABLE_AREA = 268435456;

	@Autowired
	private ScaledMapDAO mapDAO;

	@Autowired
	private MapViewDAO viewDAO;

	@Autowired
	private FogOfWarShapeDAO shapeDAO;

	@Autowired
	private FogOfWarGroupDAO groupDAO;

	@Autowired
	private FogOfWarGroupVisibilityDAO groupVisibilityDAO;

	@Autowired
	private FogOfWarShapeVisibilityDAO shapeVisibilityDAO;
	
	@Autowired
	private TokenDefinitionDAO tokenDefinitionDAO;

	@Override
	public TypedActionResult<ScaledMap> createMap(BeholderUser user, String name, int squareSize,
			byte[] data) {
		Dimension dimension = ImageUtil.getImageDimensions(data);
		
		List<MapView> views = user.getViews();
		
		
		for (MapView view: views) {
			double factor = Calculations.scale(squareSize).toResolution(view.toResolution()).onScreenWithDiagonalSize(view.getScreenDiagonalInInches());
			double w = factor * dimension.getWidth();
			double h = factor * dimension.getHeight();
			int area = (int) (w * h);
			
			if (area > MAXIMUM_RENDERABLE_AREA) {
				return TypedActionResult.fail("Map too large to render. Size on view '%s', render area %d exceeds maximum area of %d", view.getIdentifier(), area, MAXIMUM_RENDERABLE_AREA);
			}
		}
		
		
		ScaledMap map = new ScaledMap();
		map.setData(data);
		map.setName(name);
		map.setSquareSize(squareSize);
		map.setOwner(user);
		map.setBasicHeight((int) dimension.getHeight());
		map.setBasicWidth((int) dimension.getWidth());
		mapDAO.save(map);

		return TypedActionResult.ok(map);
	}

	@Override
	public void selectMap(MapView view, ScaledMap map) {
		view.setSelectedMap(map);
		viewDAO.update(view);
	}

	@Override
	public void unselectMap(MapView view) {
		view.setSelectedMap(null);
		viewDAO.update(view);
	}

	@Override
	public void addFogOfWarCircle(ScaledMap map, int radius, int offsetX,
			int offsetY) {
		FogOfWarCircle circle = new FogOfWarCircle();
		circle.setMap(map);
		circle.setOffsetX(offsetX);
		circle.setOffsetY(offsetY);
		circle.setRadius(radius);
		shapeDAO.save(circle);

	}

	@Override
	public void addFogOfWarRect(ScaledMap map, int width, int height,
			int offsetX, int offsetY) {
		FogOfWarRect rect = new FogOfWarRect();
		rect.setMap(map);
		rect.setHeight(height);
		rect.setWidth(width);
		rect.setOffsetX(offsetX);
		rect.setOffsetY(offsetY);
		shapeDAO.save(rect);
	}

	@Override
	public TypedActionResult<FogOfWarGroup> createGroup(ScaledMap map,
			String name, List<FogOfWarShape> shapes) {
		if (shapes.isEmpty()) {
			return TypedActionResult.fail("No shapes selected");
		}

		FogOfWarGroup group = new FogOfWarGroup();
		group.setMap(map);
		group.setName(name);
		groupDAO.save(group);

		shapes.forEach(shape -> {
			shape.setGroup(group);
			shapeDAO.update(shape);
		});

		return TypedActionResult.ok(group);
	}

	@Override
	public TypedActionResult<FogOfWarGroup> editGroup(FogOfWarGroup group,
			String name, List<FogOfWarShape> keep, List<FogOfWarShape> remove) {
		if (keep.isEmpty()) {
			return TypedActionResult.fail("No shapes selected");
		}

		
		group.setName(name);
		groupDAO.update(group);

		keep.forEach(shape -> {
			shape.setGroup(group);
			shapeDAO.update(shape);
		});
		
		remove.forEach(shape -> {
			shape.setGroup(null);
			shapeDAO.update(shape);
		});

		return TypedActionResult.ok(group);
	}
	
	@Override
	public void setGroupVisibility(MapView view, FogOfWarGroup group,
			VisibilityStatus status) {
		FogOfWarGroupVisibilityFilter filter = new FogOfWarGroupVisibilityFilter();
		filter.view().set(view);
		filter.group().set(group);

		FogOfWarGroupVisibility visibility = groupVisibilityDAO
				.getUniqueByFilter(filter);

		if (visibility == null) {
			visibility = new FogOfWarGroupVisibility();
			visibility.setGroup(group);
			visibility.setView(view);
			visibility.setStatus(status);
			groupVisibilityDAO.save(visibility);
		} else {
			visibility.setStatus(status);
			groupVisibilityDAO.update(visibility);
		}
	}

	@Override
	public void setShapeVisibility(MapView view, FogOfWarShape shape,
			VisibilityStatus status) {
		FogOfWarShapeVisibilityFilter filter = new FogOfWarShapeVisibilityFilter();
		filter.view().set(view);
		filter.shape().set(shape);

		FogOfWarShapeVisibility visibility = shapeVisibilityDAO
				.getUniqueByFilter(filter);

		if (visibility == null) {
			visibility = new FogOfWarShapeVisibility();
			visibility.setShape(shape);
			visibility.setView(view);
			visibility.setStatus(status);
			shapeVisibilityDAO.save(visibility);
		} else {
			visibility.setStatus(status);
			shapeVisibilityDAO.update(visibility);
		}
	}
	
	@Override
	public TokenDefinition createToken(BeholderUser user, String name, int diameter,
			byte[] image) {
		TokenDefinition def = new TokenDefinition();
		def.setImageData(image);
		def.setOwner(user);
		def.setDiameterInSquares(diameter);
		def.setName(name);
		
		tokenDefinitionDAO.save(def);
		
		return def;
	}

	@Override
	public void ungroup(FogOfWarGroup group) {
		group.getShapes().forEach(s -> {
			s.setGroup(null);
			shapeDAO.update(s);
		});
		
		group.getVisibilities().forEach(groupVisibilityDAO::delete);
		
		groupDAO.delete(group);
		
	}

}
