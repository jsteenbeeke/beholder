package com.jeroensteenbeeke.topiroll.beholder.web.components.mapcontrol;

import com.jeroensteenbeeke.topiroll.beholder.entities.ScaledMap;
import org.apache.wicket.ajax.AjaxRequestTarget;

public interface IClickListener {
    void onClick(AjaxRequestTarget target, ScaledMap map, int x, int y);
}
