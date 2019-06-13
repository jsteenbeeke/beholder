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
package com.jeroensteenbeeke.topiroll.beholder.web.components;

import com.jeroensteenbeeke.hyperion.solstice.data.ModelMaker;
import com.jeroensteenbeeke.topiroll.beholder.entities.AreaMarker;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.danekja.java.util.function.serializable.SerializableBiFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

public class MarkerStyleModel<T extends AreaMarker> extends
		LoadableDetachableModel<String> {

	private static final long serialVersionUID = 8242970985335144489L;
	private final IModel<T> markerModel;

    private final double factor;

    private SerializableBiFunction<T, Double, Long> width;

    private SerializableBiFunction<T, Double, Long> height;

    private SerializableBiFunction<T, Double, Long> x;

    private SerializableBiFunction<T, Double, Long> y;

    private SerializableBiFunction<T, Double, Double> opacity;

    private SerializableBiFunction<T, Double, Long> borderRadiusPercent;

    private SerializableBiFunction<T, Double, Long> borderRadiusPX;

    private SerializableBiFunction<T, Double, String> backgroundColor;

    private SerializableBiFunction<T, Double, String> transform;

    private SerializableBiFunction<T, Double, String> borderTop;

    private SerializableBiFunction<T, Double, String> borderLeft;

    private SerializableBiFunction<T, Double, String> borderRight;

    private SerializableBiFunction<T, Double, String> borderBottom;


    public MarkerStyleModel(T marker, double factor) {
        this.markerModel = ModelMaker.wrap(marker);
        this.factor = factor;
    }

    public MarkerStyleModel<T> setWidth(
            SerializableBiFunction<T, Double, Long> width) {
        this.width = width;
        return this;
    }

    public MarkerStyleModel<T> setHeight(
            SerializableBiFunction<T, Double, Long> height) {
        this.height = height;
        return this;
    }

    public MarkerStyleModel<T> setX(
            SerializableBiFunction<T, Double, Long> x) {
        this.x = x;
        return this;
    }

    public MarkerStyleModel<T> setY(
            SerializableBiFunction<T, Double, Long> y) {
        this.y = y;
        return this;
    }

    public MarkerStyleModel<T> setBorderRadiusPercent(
            SerializableBiFunction<T, Double, Long> borderRadiusPercent) {
        this.borderRadiusPercent = borderRadiusPercent;
        return this;
    }

    public MarkerStyleModel<T> setBorderRadiusPX(
            SerializableBiFunction<T, Double, Long> borderRadiusPX) {
        this.borderRadiusPX = borderRadiusPX;
        return this;
    }

    public MarkerStyleModel<T> setOpacity(
            SerializableBiFunction<T, Double, Double> opacity) {
        this.opacity = opacity;
        return this;
    }

    public MarkerStyleModel<T> setBackgroundColor(
            SerializableBiFunction<T, Double, String> backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public MarkerStyleModel<T> setTransform(
            SerializableBiFunction<T, Double, String> transform) {
        this.transform = transform;
        return this;
    }

    public MarkerStyleModel<T> setBorderTop(
            SerializableBiFunction<T, Double, String> borderTop) {
        this.borderTop = borderTop;
        return this;
    }

    public MarkerStyleModel<T> setBorderLeft(
            SerializableBiFunction<T, Double, String> borderLeft) {
        this.borderLeft = borderLeft;
        return this;
    }

    public MarkerStyleModel<T> setBorderRight(
            SerializableBiFunction<T, Double, String> borderRight) {
        this.borderRight = borderRight;
        return this;
    }

    public MarkerStyleModel<T> setBorderBottom(
            SerializableBiFunction<T, Double, String> borderBottom) {
        this.borderBottom = borderBottom;
        return this;
    }

    @Override
    protected String load() {
        T marker = markerModel.getObject();
        StringBuilder sb = new StringBuilder();
        sb.append("position: absolute;");
        apply(sb, factor, marker, "width", width, null, "px");
        apply(sb, factor, marker, "height", height, null, "px");
        apply(sb, factor, marker, "left", x, null, "px");
        apply(sb, factor, marker, "top", y, null, "px");
        apply(sb, factor, marker, "border-radius", borderRadiusPercent, null, "%");
        apply(sb, factor, marker, "border-radius", borderRadiusPX, null, "px");
        apply(sb, factor, marker, "opacity", opacity, null, null);
        apply(sb, factor, marker, "border-top", borderTop, null, null);
        apply(sb, factor, marker, "border-left", borderLeft, null, null);
        apply(sb, factor, marker, "border-right", borderRight, null, null);
        apply(sb, factor, marker, "border-bottom", borderBottom, null, null);
        apply(sb, factor, marker, "background-color", backgroundColor, "#", null);
        apply(sb, factor, marker, "transform", transform, null, null);

        return sb.toString();
    }

    private <U extends Serializable> void apply(
            @Nonnull
                    StringBuilder builder, double factor,
            @Nonnull
                    T marker,
            @Nonnull
                    String field,
            @Nullable
                    SerializableBiFunction<T, Double, U> input,
            @Nullable
                    String prefix,
            @Nullable
                    String unit

    ) {
        if (input != null) {
            U value = input.apply(marker, factor);
            builder.append(field).append(": ");
            if (prefix != null && !value.toString().startsWith(prefix)) {
                builder.append(prefix);
            }
            builder.append(value);
            if (unit != null) {
                builder.append(unit);
            }
            builder.append("; ");
        }
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        markerModel.detach();
    }

}
