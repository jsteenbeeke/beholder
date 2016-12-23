package com.jeroensteenbeeke.topiroll.beholder.entities;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;

import org.apache.wicket.markup.html.panel.Panel;

import com.jeroensteenbeeke.topiroll.beholder.util.JSBuilder;
import com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol.markers.LineMarkerController;

@Entity
public class LineMarker extends AreaMarker {

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
		int extent = (int) (ratio * getExtent() * squareSize / 5);
		double theta = Math.toRadians((double) getTheta());

		int h = (int) (extent * Math.sin(theta));
		int w = (int) (extent * Math.cos(theta));

		js.__("%s.save();", contextVariable);
		js.__("%s.moveTo(%d, %d);", contextVariable, x, y);
		js.__("%s.lineTo(%d, %d);", contextVariable, (x + w), (y + h));
		js.__("%s.closePath()", contextVariable);
		js.__("%s.strokeStyle = '#%s';", contextVariable, getColor());
		js.__("%s.strokeWidth = %f;", contextVariable, Math.max(2.0f, 4.0*ratio));
		js.__("%s.stroke();", contextVariable);
		js.__("%s.restore();", contextVariable);
		js.__("%s.strokeWidth = 0;", contextVariable);
		js.__("%s.strokeStyle = '#000000';", contextVariable);
		
	}

	@Override
	public Panel createPanel(String id) {

		return new LineMarkerController(id, this);
	}
	
	@Override
	public String getMarkerState() {
		return ";CONE;theta=".concat(Integer.toString(getTheta()));
	}
}
