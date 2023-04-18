/*
 * This file is part of Beholder
 * Copyright (C) 2016 - 2023 Jeroen Steenbeeke
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
package com.jeroensteenbeeke.topiroll.beholder.entities;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;
import java.io.Serializable;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

@Entity
public class DungeonMasterNote extends BaseDomainObject implements Serializable {
    @SequenceGenerator(
            allocationSize = 1,
            initialValue = 1,
            name = "DungeonMasterNote",
            sequenceName = "SEQ_ID_DungeonMasterNote")
    @Access(value = AccessType.PROPERTY)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DungeonMasterNote")
    @Id
    private Long id;

    private static final long serialVersionUID = 1L;

    @JoinColumn(name = "map")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ScaledMap map;

    @Column(nullable = false)
    private int offsetX;

    @Column(nullable = false)
    private int offsetY;

    @Column(nullable = false)
    private String note;

    @Column(nullable = true)
    private String color;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public final Serializable getDomainObjectId() {
        return getId();
    }

    @NotNull
    public ScaledMap getMap() {
        return map;
    }

    public void setMap(@NotNull ScaledMap map) {
        this.map = map;
    }

    @NotNull
    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(@NotNull int offsetX) {
        this.offsetX = offsetX;
    }

    @NotNull
    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(@NotNull int offsetY) {
        this.offsetY = offsetY;
    }

    @NotNull
    public String getNote() {
        return note;
    }

    public void setNote(@NotNull String note) {
        this.note = note;
    }

    @Nullable
    public String getColor() {
        return color;
    }

    @NotNull
    public Optional<String> color() {
        return Optional.ofNullable(color);
    }

    public void setColor(@Nullable String color) {
        this.color = color;
    }
}
