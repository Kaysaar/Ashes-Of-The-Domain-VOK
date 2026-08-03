package data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.scripts;

import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.MusicPlayerPluginImpl;
import com.fs.starfarer.api.impl.campaign.aotd_entities.PlutoMiningStation;
import com.fs.starfarer.api.impl.campaign.procgen.themes.MiscellaneousThemeGenerator;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.sections.pluto.OpticCommandNexus;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureScript;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureSection;

import java.awt.*;

public class PlutoMegastructure extends BaseMegastructureScript {
    SectorEntityToken existingStation = null;

    @Override
    public void trueInit(String specId, SectorEntityToken entityTiedTo, MarketAPI marketTiedTo) {
        super.trueInit(specId, entityTiedTo, marketTiedTo);
        boolean found = false;
        for (SectorEntityToken connectedEntity : marketTiedTo.getConnectedEntities()) {
            if (connectedEntity.getCustomEntitySpec() != null && connectedEntity.getCustomEntitySpec().getId().equals("aotd_pluto_station")) {
                existingStation = connectedEntity;
                found = true;
                break;

            }
        }
        if (!found) {
            if (existingStation == null) {
                SectorEntityToken token = entityTiedTo.getMarket().getStarSystem().addCustomEntity("aotd_pluto_station", "Pluto Mining Station", "aotd_pluto_station", tiedMarket.getFactionId());
                float angle = entityTiedTo.getCircularOrbitAngle();
                float period = entityTiedTo.getCircularOrbitPeriod(); // 270 : height
                token.setCircularOrbitPointingDown(entityTiedTo, angle, entityTiedTo.getRadius() + 270 + 70, period);
                token.getMemoryWithoutUpdate().set(MusicPlayerPluginImpl.MUSIC_SET_MEM_KEY, "aotd_mega");
                MiscellaneousThemeGenerator.makeDiscoverable(token, 40000, 3000f);
                marketTiedTo.getConnectedEntities().add(token);
                existingStation = token;
            } else {
                marketTiedTo.getConnectedEntities().add(existingStation);
            }

        }

    }

    public OpticCommandNexus getLaserSection() {
        return getSectionById("pluto_ocn", OpticCommandNexus.class);
    }

    public PlutoMiningStation getStation() {
        return (PlutoMiningStation) existingStation.getCustomPlugin();
    }

    @Override
    public SectorEntityToken getEntityTiedTo() {
        return existingStation;
    }

    @Override
    public boolean doesHaveCustomSectionForTooltip() {
        return true;
    }

    @Override
    public void printCustomSection(TooltipMakerAPI tooltip) {
        tooltip.addSectionHeading("Laser Strength", Alignment.MID, 5f);
        OpticCommandNexus nexus = getSectionById("pluto_ocn", OpticCommandNexus.class);
        if (nexus.isRestored) {
            if (nexus.getCurrentMagnitude() == 0f) {
                tooltip.addPara("Currently laser is dormant. Access %s section to start it!", 3f, Color.ORANGE, nexus.getName());
            } else {
                String percentage = ((int) (nexus.getCurrentMagnitude() * 10)) + "%";
                tooltip.addPara("Currently laser is operational at %s capacity.", 3f, Color.ORANGE, percentage);

            }
        } else {
            tooltip.addPara("Currently megastructure is fully unusable, as long as %s section is damaged. Before we start restoration of any other section, this one must be restored first!", 3f, Color.ORANGE, nexus.getName());

        }

    }
    @Override
    public Industry getIndustryTiedToMegastructureIfPresent() {
        if(tiedMarket==null)return null;
        return tiedMarket.getIndustries().stream().filter(x->x.getSpec().getId().equals("pluto_station")).findFirst().orElse(null);
    }
}
