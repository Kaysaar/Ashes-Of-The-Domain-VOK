package com.fs.starfarer.api.impl.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.econ.AICoreAdmin;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.Set;

public class MegastructureLocationIntel extends BaseIntelPlugin{
    @Override
    public boolean isImportant() {
        return true;
    }
    private final StarSystemAPI planet;

    // Don't touch this one
    public MegastructureLocationIntel(StarSystemAPI planet) {
        this.planet = planet;

    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        Color title = getTitleColor(mode);

        // Title of the intel
        info.addPara(getName(), title, 0f);

        float initPad;
        if (mode == ListInfoMode.IN_DESC) {
            initPad = 10f;
        } else {
            initPad = 3f;
        }

        // Subtitle of the intel
        bullet(info);
        info.addPara(planet.getName(), initPad, getBulletColorForMode(mode));
        unindent(info);
    }

    @Override
    protected String getName() {
        // The text in the title of the intel
        return "Megastructure location - "+planet.getName();
    }

    @Override
    public String getSmallDescriptionTitle() {
        // The text in the title of the small description
        return planet.getName();
    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        // Create the small description here, you can place images, multiple paragraphs, anything
        info.addPara("This is a location of a megastructure", Misc.getGrayColor(), 0);
    }

    @Override
    public String getIcon() {
        // The icon of the intel
        return Global.getSettings().getSpriteName("intel", "hostilities");
    }

    // Don't touch this one
    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);
        tags.add(Tags.INTEL_IMPORTANT);
        return tags;
    }

    // Color of the intel text
    @Override
    public FactionAPI getFactionForUIColors() {
        return super.getFactionForUIColors();
    }

    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        // The location on the map of the intel
        return getPlanet().getStar();
    }

    @Override
    public boolean shouldRemoveIntel() {
        // The condition which will remove the intel
        return planet == null;
    }

    @Override
    public String getCommMessageSound() {
        // Sound when the intel pops up
        return "ui_discovered_entity";
    }

    @Override
    public IntelSortTier getSortTier() {
        // Intel tab sort tier (don't really understand this one either tbh)
        return IntelSortTier.TIER_6;
    }

    // Don't touch this one
    public StarSystemAPI getPlanet() {
        return planet;
    }
}
