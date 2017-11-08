package com.jeroensteenbeeke.topiroll.beholder.entities.visitor;

import com.jeroensteenbeeke.topiroll.beholder.entities.CircleMarker;
import com.jeroensteenbeeke.topiroll.beholder.entities.ConeMarker;
import com.jeroensteenbeeke.topiroll.beholder.entities.CubeMarker;
import com.jeroensteenbeeke.topiroll.beholder.entities.LineMarker;

import javax.annotation.Nonnull;
import java.io.Serializable;

public interface AreaMarkerVisitor<R> extends Serializable {
	R visit(@Nonnull CircleMarker marker);

	R visit(@Nonnull ConeMarker marker);

	R visit(@Nonnull CubeMarker marker);

	R visit(@Nonnull LineMarker marker);
}
