package com.fs.starfarer.api.impl.campaign;

import com.fs.graphics.util.Fader;
import com.fs.starfarer.api.campaign.CampaignEngineLayers;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.combat.CombatViewport;
import com.fs.starfarer.combat.entities.terrain.Planet;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;

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
