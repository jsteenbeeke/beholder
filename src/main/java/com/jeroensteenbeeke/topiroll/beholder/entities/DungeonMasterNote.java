package com.jeroensteenbeeke.topiroll.beholder.entities;

import com.jeroensteenbeeke.hyperion.data.BaseDomainObject;
import java.io.Serializable;
import javax.annotation.Nonnull;
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

    @Nonnull
    public ScaledMap getMap() {
        return map;
    }

    public void setMap(@Nonnull ScaledMap map) {
        this.map = map;
    }

    @Nonnull
    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(@Nonnull int offsetX) {
        this.offsetX = offsetX;
    }

    @Nonnull
    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(@Nonnull int offsetY) {
        this.offsetY = offsetY;
    }

    @Nonnull
    public String getNote() {
        return note;
    }

    public void setNote(@Nonnull String note) {
        this.note = note;
    }
}
