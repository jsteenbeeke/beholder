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
import com.jeroensteenbeeke.topiroll.beholder.util.Calculations;
import java.awt.Dimension;
import java.io.Serializable;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import jakarta.persistence.*;
import org.apache.wicket.model.IModel;

@Entity
public class ScaledMap extends BaseDomainObject implements AmazonStored {

    private static final long serialVersionUID = 1L;

    private static final int MAX_PREVIEW_SIZE = 640;

    @Id
    @SequenceGenerator(
            sequenceName = "SEQ_ID_ScaledMap",
            name = "ScaledMap",
            initialValue = 1,
            allocationSize = 1)
    @GeneratedValue(generator = "ScaledMap", strategy = GenerationType.SEQUENCE)
    @Access(value = AccessType.PROPERTY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "campaign")
    private Campaign campaign;

    @Column(nullable = true, name = "amazon_key")
    private String amazonKey;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "folder")
    private MapFolder folder;

    @OneToMany(mappedBy = "map", fetch = FetchType.LAZY)
    private List<TokenInstance> tokens = new ArrayList<>();

    @Column(nullable = false)
    private int basicHeight;

    @Column(nullable = false)
    private int basicWidth;

    @OneToMany(mappedBy = "map", fetch = FetchType.LAZY)
    private List<FogOfWarGroup> groups = new ArrayList<>();

    @OneToMany(mappedBy = "map", fetch = FetchType.LAZY)
    private List<FogOfWarShape> fogOfWarShapes = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner")
    private BeholderUser owner;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int squareSize;

    @OneToMany(mappedBy = "selectedMap", fetch = FetchType.LAZY)
    private List<MapView> selectedBy = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "map")
    private List<DungeonMasterNote> notes = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(@NotNull Long id) {
        this.id = id;
    }

    @Override
    public final Serializable getDomainObjectId() {
        return getId();
    }

    @NotNull
    public List<MapView> getSelectedBy() {
        return selectedBy;
    }

    public void setSelectedBy(@NotNull List<MapView> selectedBy) {
        this.selectedBy = selectedBy;
    }

    public int getSquareSize() {
        return squareSize;
    }

    public void setSquareSize(int squareSize) {
        this.squareSize = squareSize;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public BeholderUser getOwner() {
        return owner;
    }

    public void setOwner(@NotNull BeholderUser owner) {
        this.owner = owner;
    }

    @NotNull
    public List<FogOfWarShape> getFogOfWarShapes() {
        return fogOfWarShapes;
    }

    public void setFogOfWarShapes(@NotNull List<FogOfWarShape> fogOfWarShapes) {
        this.fogOfWarShapes = fogOfWarShapes;
    }

    @NotNull
    public List<FogOfWarGroup> getGroups() {
        return groups;
    }

    public void setGroups(@NotNull List<FogOfWarGroup> groups) {
        this.groups = groups;
    }

    @Transient
    public double getPreviewFactor() {
        int w = (int) getBasicWidth();
        double factor = 1.0;

        while (w > MAX_PREVIEW_SIZE) {
            w = (int) (w * 0.9);
            factor = 0.9 * factor;
        }

        return factor;
    }

    @Transient
    public Dimension getPreviewDimension() {

        return new Dimension(
                (int) (getPreviewFactor() * getBasicWidth()),
                (int) (getPreviewFactor() * getBasicHeight()));
    }

    @Transient
    public Dimension getDisplayDimension(MapView mapView) {
        double factor = getDisplayFactor(mapView);

        return new Dimension((int) (getBasicWidth() * factor), (int) (getBasicHeight() * factor));
    }

    @Transient
    public double getDisplayFactor(MapView mapView) {
        return Calculations.scale(getSquareSize())
                .toResolution(mapView.toResolution())
                .onScreenWithDiagonalSize(mapView.getScreenDiagonalInInches());
    }

    @NotNull
    public int getBasicWidth() {
        return basicWidth;
    }

    public void setBasicWidth(@NotNull int basicWidth) {
        this.basicWidth = basicWidth;
    }

    @NotNull
    public int getBasicHeight() {
        return basicHeight;
    }

    public void setBasicHeight(@NotNull int basicHeight) {
        this.basicHeight = basicHeight;
    }

    @NotNull
    public List<TokenInstance> getTokens() {
        return tokens;
    }

    public void setTokens(@NotNull List<TokenInstance> tokens) {
        this.tokens = tokens;
    }

    @Nullable
    public MapFolder getFolder() {
        return folder;
    }

    public void setFolder(@Nullable MapFolder folder) {
        this.folder = folder;
    }

    public List<FogOfWarShape> getAllShapes() {
        Map<Long, FogOfWarShape> shapes =
                getFogOfWarShapes().stream()
                        .collect(Collectors.toMap(FogOfWarShape::getId, Function.identity()));
        getGroups().stream()
                .flatMap(g -> g.getShapes().stream())
                .filter(s -> !shapes.containsKey(s.getId()))
                .forEach(s -> shapes.put(s.getId(), s));
        return new ArrayList<>(shapes.values());
    }

    @Nullable
    @Override
    public String getAmazonKey() {
        return amazonKey;
    }

    public void setAmazonKey(@Nullable String amazonKey) {
        this.amazonKey = amazonKey;
    }

    public String getNameWithFolders() {
        if (folder != null) {
            return String.format("%s \\ %s", folder.getNameWithParents(), getName());
        }

        return getName();
    }

    @Nullable
    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(@Nullable Campaign campaign) {
        this.campaign = campaign;
    }

    @NotNull
    public List<DungeonMasterNote> getNotes() {
        return notes;
    }

    public void setNotes(@NotNull List<DungeonMasterNote> notes) {
        this.notes = notes;
    }
}
