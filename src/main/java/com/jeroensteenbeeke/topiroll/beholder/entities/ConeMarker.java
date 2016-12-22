package com.jeroensteenbeeke.topiroll.beholder.entities;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;

import org.apache.wicket.markup.html.panel.Panel;

import com.jeroensteenbeeke.topiroll.beholder.util.JSBuilder;
import com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol.markers.ConeMarkerController;

@Entity
public class ConeMarker extends AreaMarker {

	private static final long serialVersionUID = 1L;

	@Column(nullable = false)
	private int theta;

	@Nonnull
	public int getTheta() {
		return theta;
	}

	public void setTheta(@Nonnull int theta) {
		this.theta = theta;
	}

	@Override
	public void renderTo(String contextVariable, JSBuilder js, double ratio,
			int squareSize) {
		int x = (int) (ratio * getOffsetX());
		int y = (int) (ratio * getOffsetY());
		int radius = (int) (ratio * getExtent() * squareSize / 5);
		double theta = Math.toRadians((double) getTheta());

		js.__("%s.save();", contextVariable);
		js.__("%s.globalAlpha = 0.5;", contextVariable);
		js.__("%s.beginPath();", contextVariable);
		js.__("%s.arc(%d, %d, %d, %f, Math.PI / 2, false);", contextVariable, x,
				y, radius, theta);
		js.__("%s.fillStyle = '#%s';", contextVariable, getColor());
		js.__("%s.fill();", contextVariable);
		js.__("%s.restore();", contextVariable);
	}
	
	@Override
	public Panel createPanel(String id) {

		return new ConeMarkerController(id, this);
	}
	
	@Override
	public String getMarkerState() {
		return ";CONE;theta=".concat(Integer.toString(getTheta()));
	}

}
