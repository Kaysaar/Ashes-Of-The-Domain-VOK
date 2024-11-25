package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.bifrost;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.bifrost.sections.BifrostSection;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.bifrost.ui.BifrostUI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.GPIndividualMegastructreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.GPMegasturcutreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class BifrostMega extends GPBaseMegastructure {
    public static LinkedHashMap<String, Integer> bifrostGateCost = new LinkedHashMap<>();

    static {
        bifrostGateCost.put(AoTDCommodities.REFINED_METAL, 200);
        bifrostGateCost.put(AoTDCommodities.DOMAIN_GRADE_MACHINERY, 100);
    }

    @Override
    public void createAdditionalInfoForMega(TooltipMakerAPI tooltip) {
        tooltip.addSectionHeading("Current effects", Alignment.MID,5f);
        tooltip.addPara("Accessibility bonus to all colonies with Bifrost gate in system : %s",5f, Color.ORANGE,(getTotalAccessibility()*100)+"%");
    }

    public static SectorEntityToken spawnGate(MarketAPI market) {
        SectorEntityToken primary = market.getPrimaryEntity();
        float orbitRadius = primary.getRadius() + 150.0F;
        SectorEntityToken test = market.getContainingLocation().addCustomEntity((String) null, market.getName() + " Bifrost Gate", "bifrost_gate", market.getFactionId());
        test.setCircularOrbitWithSpin(primary, (float) Math.random() * 360.0F, orbitRadius, orbitRadius / 10.0F, 5.0F, 5.0F);
        market.getConnectedEntities().add(test);
        test.setMarket(market);
        test.setDiscoverable(false);
        market.addCondition("bifrost_removal");
        test.getMemory().set("$used", false);
        test.getMemory().set("$cooldown", 0f);
        test.getMemory().set("$supplied", true);
        return test;
    }

    @Override
    public void trueInit(String specId, SectorEntityToken entityTiedTo) {
        this.specId = specId;
        this.entityTiedTo = entityTiedTo;
        this.megaStructureSections = new ArrayList<>();
        wasInitalized = true;
    }

    public void addNewBifrostGate(StarSystemAPI systemAPI) {
        BifrostSection section = (BifrostSection) GPManager.getInstance().getMegaSectionSpecFromList("bifrost_section").getScript();
        section.init(this, false);
        section.setStarSystemAPI(systemAPI);
        section.startReconstruction();

        this.megaStructureSections.add(section);
    }

    public ArrayList<BifrostSection> getSections() {
        ArrayList<BifrostSection> section = new ArrayList<>();
        for (GPMegaStructureSection megaStructureSection : megaStructureSections) {
            section.add((BifrostSection) megaStructureSection);
        }
        return section;
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);

        float totality = getTotalAccessibility();

        if(getSections().size()>=2){
            for (MarketAPI playerMarket : Misc.getPlayerMarkets(true)) {
                playerMarket.getAccessibilityMod().modifyFlat("aotd_bifrost", totality,"Bifrost Network");
            }
        }
        else{
            for (MarketAPI playerMarket : Misc.getPlayerMarkets(true)) {
                playerMarket.getAccessibilityMod().unmodifyFlat("aotd_bifrost");
            }
        }


    }

    public float getTotalAccessibility() {
        float totality = 0f;
        for (BifrostSection section : getSections()) {
            float totalBonus = 0f;

            for (MarketAPI market : Global.getSector().getEconomy().getMarkets(section.getStarSystemAPI())) {
                if(!market.getFaction().isPlayerFaction()||!market.isPlayerOwned())continue;
                if (market.getAccessibilityMod().getFlatBonus() >= 0) {
                    totalBonus += market.getAccessibilityMod().getFlatBonus();

                }
                if (market.getAccessibilityMod().getFlatBonus("aotd_bifrost") != null) {
                    totalBonus -= market.getAccessibilityMod().getFlatBonus("aotd_bifrost").getValue();

                }
            }
            totalBonus /= 10f;
            totality += totalBonus;

        }
        totality*=getPenaltyFromManager();
        return totality;
    }

    public void removeBifrostGate(BifrostSection section) {
        if (section.getGateTiedTo() != null) {
            Misc.fadeAndExpire(section.getGateTiedTo());
        }
        section.setGateTiedTo(null);
        section.aboutToGetRemoved();
        megaStructureSections.remove(section);

    }

    @Override
    public GPIndividualMegastructreMenu createUIPlugin(CustomPanelAPI parentPanel, GPMegasturcutreMenu menu) {
        return new BifrostUI(this, parentPanel, menu);
    }
}
