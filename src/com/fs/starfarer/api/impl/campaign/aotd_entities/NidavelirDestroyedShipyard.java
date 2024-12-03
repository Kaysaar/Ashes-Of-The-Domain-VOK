package com.fs.starfarer.api.impl.campaign.aotd_entities;

import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;

public class NidavelirDestroyedShipyard extends NidavelirShipyard {
    @Override
    public void init(SectorEntityToken entity, Object pluginParams) {
        super.init(entity, pluginParams);
    }

    @Override
    public void trueInit(String type, String shadowType, PlanetAPI planetTied) {
        tiedToPlanet = planetTied;
        reinitRendering(type,shadowType,true);
    }
}
