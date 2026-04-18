package data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.sections.nidavelir;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.aotd_entities.NidavelirShipyardVisual;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.tot.plugins.AoTDCommodityEconSpecManager;
import data.kaysaar.aotd.tot.ui.commoditypanel.AoTDCommodityShortPanelCombined;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.scripts.NidavelirMegastructure;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class NexusCore extends BaseNidavelirSection {
    int percentageUpkeepModifierPerManpower = 5;
    public boolean shouldGiveGungnir = false;

    @Override
    public void createEffectSection(TooltipMakerAPI tooltipMakerAPI, boolean isForMainView) {
        super.createEffectSection(tooltipMakerAPI, isForMainView);
    }

    @Override
    public LinkedHashMap<String, Integer> getDemandMap() {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        map.put(Commodities.METALS, 5);
        map.put(Commodities.RARE_METALS, 12);
        map.put(AoTDCommodities.REFINED_METAL, 6);
        map.put(AoTDCommodities.PURIFIED_TRANSPLUTONICS, 3);
        return map;
    }

    @Override
    public LinkedHashMap<String, Integer> getProductionMap() {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        map.put(Commodities.HEAVY_MACHINERY, 15);
        map.put(AoTDCommodities.DOMAIN_GRADE_MACHINERY,10);
        map.put(AoTDCommodities.ADVANCED_COMPONENTS, 12);
        return map;
    }

    @Override
    public void applySectionOnIndustry(BaseIndustry ind) {
        super.applySectionOnIndustry(ind);
        float total = 1;
        float reductionStep = 0.05f;
        float totalREd = reductionStep*getManpowerUsed();
        total-=totalREd;
        String effect = "Assigned Manpower";
        if(isAutomated){
            effect = "Section Automation";
        }
        ind.getUpkeep().modifyMult(this.getSpec().getId()+"_red",total,getName()+"- "+effect);
    }
    public int getDeficitIndex(){
        return 6;
    }
    @Override
    public void unApplySectionOnIndustry(BaseIndustry ind) {
        super.unApplySectionOnIndustry(ind);
        ind.getUpkeep().unmodifyMult(this.getSpec().getId()+"_red");
    }

    @Override
    public void applyOnRestoration() {
        NidavelirMegastructure nid = (NidavelirMegastructure) getMegastructureTiedTo();
        SectorEntityToken token = nid.getVisual().getEntity();
        nid.getVisual().isVanising = true;
        nid.getVisual().seconds = 1;
        nid.getVisual().elapsed = 0;
        Misc.fadeAndExpire(token);
        nid.setVisual(null);
        NidavelirShipyardVisual visual = (NidavelirShipyardVisual) nid.getEntityTiedTo().getStarSystem().addCustomEntity(null, "Nid", "nid_shipyards", null).getCustomPlugin();
        visual.trueInit("aotd_nidavelir", "aotd_nidavelir_shadow", (PlanetAPI) nid.getEntityTiedTo());
        visual.waitingTime = 0.8f;
        nid.setVisual(visual);
        if (Global.getSector().getPlayerFaction().getProduction().getGatheringPoint() != null) {
            MarketAPI market = Global.getSector().getPlayerFaction().getProduction().getGatheringPoint();
            market.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addWeapons("gungnir_railgun", 1);
            Global.getSector().getCampaignUI().addMessage("We have found an ancient weapon within Nidavelir, it has already been transported to " + market.getName());

        } else {
            MarketAPI market = Misc.getPlayerMarkets(true).get(0);
            market.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addWeapons("gungnir_railgun", 1);
            Global.getSector().getCampaignUI().addMessage("We have found an ancient weapon within Nidavelir, it has already been transported to " + market.getName());

        }
    }



    @Override
    public void printEffectSectionPerManpowerPoint(int manpowerAssigned,TooltipMakerAPI tl) {
        int total = percentageUpkeepModifierPerManpower*manpowerAssigned;
        tl.addPara("Reduce upkeep of megastructure by %s", 5f, Color.ORANGE, total+"%");
    }

    @Override
    public void createEffectExplanationSectionInSubSection(TooltipMakerAPI tl) {
        if (!isRestored) {
            tl.addPara("For every manpower point assigned:", 5f);
            printEffectSectionPerManpowerPoint(1,tl);

            tl.addSectionHeading("Production per manpower point", Alignment.MID, 5f);
            float width = tl.getWidthSoFar();
            LinkedHashMap<String,Integer>supply = new LinkedHashMap<>();
            LinkedHashMap<String,Integer>demand = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> entry : getProductionMap().entrySet()) {
                supply.put(entry.getKey(), AoTDCommodityEconSpecManager.getCargoAmountFromSupplyOrDemand(entry.getValue(),false,entry.getKey()));
            }
            for (Map.Entry<String, Integer> entry : getDemandMap().entrySet()) {
                demand.put(entry.getKey(), AoTDCommodityEconSpecManager.getCargoAmountFromSupplyOrDemand(entry.getValue(),true,entry.getKey()));

            }
            AoTDCommodityShortPanelCombined combined = new AoTDCommodityShortPanelCombined(width - 10, 3, supply);
            tl.addCustom(combined.getMainPanel(), 3f);
            tl.addSectionHeading("Demand per manpower point", Alignment.MID, 5f);
            combined = new AoTDCommodityShortPanelCombined(width - 10, 3, demand);
            tl.addCustom(combined.getMainPanel(), 3f);
        }
        else{
            int manpower = getManpowerUsed();
            if(this.isAutomated()){
                tl.addPara("Section automated!",Color.cyan,5f);
            }
            else{
                tl.addPara("Currently assigned manpower : %s",5f,Color.ORANGE,""+getManpowerUsed());
            }
            printEffectSectionPerManpowerPoint(getManpowerUsed(),tl);

            float width = tl.getWidthSoFar();
            LinkedHashMap<String,Integer>supply = new LinkedHashMap<>();
            LinkedHashMap<String,Integer>demand = new LinkedHashMap<>();
            tl.addSectionHeading("Section Production", Alignment.MID, 5f);
            for (Map.Entry<String, Integer> entry : getProductionMap().entrySet()) {
                supply.put(entry.getKey(), AoTDCommodityEconSpecManager.getCargoAmountFromSupplyOrDemand(entry.getValue()*manpower,false,entry.getKey()));
            }
            for (Map.Entry<String, Integer> entry : getDemandMap().entrySet()) {
                demand.put(entry.getKey(), AoTDCommodityEconSpecManager.getCargoAmountFromSupplyOrDemand(entry.getValue()*manpower,true,entry.getKey()));

            }
            AoTDCommodityShortPanelCombined combined = new AoTDCommodityShortPanelCombined(width - 10, 3, supply);
            tl.addCustom(combined.getMainPanel(), 3f);
            tl.addSectionHeading("Section Demand", Alignment.MID, 5f);
            combined = new AoTDCommodityShortPanelCombined(width - 10, 3, demand);
            tl.addCustom(combined.getMainPanel(), 3f);
        }

    }

}
