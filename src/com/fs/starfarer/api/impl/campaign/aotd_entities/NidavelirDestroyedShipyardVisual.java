package com.fs.starfarer.api.impl.campaign.aotd_entities;

import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;

public class NidavelirDestroyedShipyardVisual extends NidavelirShipyardVisual {
    @Override
    public void init(SectorEntityToken entity, Object pluginParams) {
        super.init(entity, pluginParams);
    }

    @Override
    public boolean isDestroyed() {
        return true;
    }
}
