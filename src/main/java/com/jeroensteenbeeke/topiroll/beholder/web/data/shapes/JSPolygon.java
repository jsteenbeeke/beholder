package com.jeroensteenbeeke.topiroll.beholder.web.data.shapes;

import java.util.List;

public class JSPolygon implements JSShape {
	private List<XY> points;
	
	@Override
	public String getType() {
		return "polygon";
	}

	public List<XY> getPoints() {
		return points;
	}

	public void setPoints(List<XY> points) {
		this.points = points;
	}
	
	

}
