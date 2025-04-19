package data.kaysaar.aotd.vok.scripts.specialprojects.projects;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.submarkets.StoragePlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProject;
import data.scripts.ass_ModPlugin;

import java.awt.*;

public class ASSArkoshipProject extends AoTDSpecialProject {
    @Override
    public void createRewardSection(TooltipMakerAPI tooltip, float width) {
        tooltip.addPara("Gain Arkoship!", Misc.getPositiveHighlightColor(), 5f);
    }

    @Override
    public void createRewardSectionForInfo(TooltipMakerAPI tooltip, float width) {
        MarketAPI playerMarket = Global.getSector().getPlayerFaction().getProduction().getGatheringPoint();
        if (playerMarket == null) {
            playerMarket = Misc.getPlayerMarkets(true).get(0);
        }
        tooltip.addPara("Newly built Arkoship is currently orbiting "+playerMarket.getName(), Color.ORANGE,5f);
    }

    @Override
    public boolean checkIfProjectShouldUnlock() {
        GPBaseMegastructure megastructure = GPManager.getInstance().getMegastructure("aotd_nidavelir");
        return (megastructure!=null&&megastructure.getSectionById("nidavelir_nexus").isRestored&&megastructure.getSectionById("nidavelir_eternium").isRestored);
    }

    @Override
    public void grantReward() {
        MarketAPI gatheringPoint = Global.getSector().getPlayerFaction().getProduction().getGatheringPoint();
        if(gatheringPoint==null){
            gatheringPoint = Misc.getPlayerMarkets(true).get(0);
        }
        SectorEntityToken d2 = gatheringPoint.getPrimaryEntity();
        if(Global.getSettings().getModManager().isModEnabled("arthrships")){

                MarketAPI m = ass_ModPlugin.createArkoship(Global.getSector(), Misc.genUID(),d2.getOrbitFocus(), d2.getCircularOrbitRadius()+d2.getRadius()+200, d2.getCircularOrbitPeriod(), d2.getCircularOrbitAngle(), d2.getStarSystem(), "player", null);
                    m.getPrimaryEntity().addTag("ARKOSHIP_UPGRADABLE");

                m.removeSubmarket("open_market");
                m.removeSubmarket("black_market");
                m.setFactionId("player");
                SectorEntityToken e = m.getPrimaryEntity();
                e.setFaction("player");
                m.addSubmarket("local_resources");
                m.setPlayerOwned(true);
                m.setAdmin(Global.getSector().getPlayerPerson());
                m.setName("ISS Phoenix");
                m.getPrimaryEntity().setName("ISS Phoenix");
                if (m.getSubmarket("storage") == null) {
                    m.addSubmarket("storage");
                }

                ((StoragePlugin)m.getSubmarket("storage").getPlugin()).setPlayerPaidToUnlock(true);
                m.addCondition("population_3");
                m.setSize(3);
                m.getIndustry("population").startBuilding();
                m.getIndustry("spaceport").startBuilding();
                m.addCondition("population_3");
                m.setSize(3);
                if (!Misc.isPlayerFactionSetUp()) {
                    Global.getSector().addTransientScript(new EveryFrameScript() {
                        @Override
                        public boolean isDone() {
                            return false;
                        }

                        @Override
                        public boolean runWhilePaused() {
                            return false;
                        }

                        @Override
                        public void advance(float amount) {
                            if (Global.getSector().getCampaignUI().showPlayerFactionConfigDialog()) {
                                Global.getSector().setPaused(true);
                                Global.getSector().getMemoryWithoutUpdate().set("$shownFactionConfigDialog", true);
                                Global.getSector().removeTransientScript(this);
                            }
                        }
                    });
                }
            }

        }
    }

