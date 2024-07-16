package data.kaysaar.aotd.vok.scripts.research.researchprojects;

import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.Ids.AoTDSubmarkets;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchProject;
import data.kaysaar.aotd.vok.scripts.research.models.SpecialProjectStage;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import org.lazywizard.lazylib.MathUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class NanoforgeRestoration extends ResearchProject {
    public static final ArrayList<String> items = new ArrayList<String>(Arrays.asList(
            "corrupted_nanoforge",
            "pristine_nanoforge",
            "synchrotron",
            "orbital_fusion_lamp",
            "mantle_bore",
            "catalytic_core",
            "soil_nanites",
            "biofactory_embryo",
            "fullerene_spool",
            "plasma_dynamo",
            "cryoarithmetic_engine",
            "drone_replicator",
            "dealmaker_holosuite",
            "coronal_portal"
    ));
    @Override
    public boolean haveMetReqForProjectToAppear() {
        return  AoTDMainResearchManager.getInstance().getManagerForPlayer().haveResearched("aotd_tech_streamlined_production");
    }

    @Override
    public boolean haveMetReqForProject() {
        for (MarketAPI playerMarket : Misc.getPlayerMarkets(true)) {
            if(playerMarket.hasSubmarket(AoTDSubmarkets.RESEARCH_FACILITY_MARKET)){
                float amount = playerMarket.getSubmarket(AoTDSubmarkets.RESEARCH_FACILITY_MARKET).getCargo().getQuantity(CargoAPI.CargoItemType.SPECIAL,new SpecialItemData(Items.CORRUPTED_NANOFORGE,null));
                if(amount!=0){
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void generateTooltipInfoForProject(TooltipMakerAPI tooltipMakerAPI) {
        if(haveMetReqForProject()){
            tooltipMakerAPI.addPara("We can try to restore a corrupted nanoforge.",Misc.getPositiveHighlightColor(),10f);
        }
        else{
            tooltipMakerAPI.addPara("For this project to start, we will need a corrupted nanoforge in one of our research facilities.",Misc.getNegativeHighlightColor(),10f);

        }
    }

    @Override
    public void payForProjectIfNecessary() {
        for (MarketAPI playerMarket : Misc.getPlayerMarkets(true)) {
            if(playerMarket.hasSubmarket(AoTDSubmarkets.RESEARCH_FACILITY_MARKET)){
                float amount = playerMarket.getSubmarket(AoTDSubmarkets.RESEARCH_FACILITY_MARKET).getCargo().getQuantity(CargoAPI.CargoItemType.SPECIAL,new SpecialItemData(Items.CORRUPTED_NANOFORGE,null));
                if(amount!=0){
                    playerMarket.getSubmarket(AoTDSubmarkets.RESEARCH_FACILITY_MARKET).getCargo().removeItems(CargoAPI.CargoItemType.SPECIAL,new SpecialItemData(Items.CORRUPTED_NANOFORGE,null),1);
                    break;
                }
            }
        }
    }

    @Override
    public boolean haveMetReqForOption(String optionId) {
        if(optionId.equals("recon_standard")){
            return AoTDMainResearchManager.getInstance().getManagerForPlayer().haveResearched(AoTDTechIds.DOMAIN_TYPE_MODEL_STANDARDIZATION);
        }
        if(optionId.equals("recon_alpha")){
            boolean haveAlpha = false;
            for (MarketAPI playerMarket : Misc.getPlayerMarkets(true)) {
                if(playerMarket.hasSubmarket(AoTDSubmarkets.RESEARCH_FACILITY_MARKET)){
                    if(playerMarket.getSubmarket(AoTDSubmarkets.RESEARCH_FACILITY_MARKET).getCargo().getCommodityQuantity(Commodities.ALPHA_CORE)!=0){
                        haveAlpha = true;
                    }
                }
            }
            return haveAlpha;
        }
        return true;
    }

    @Override
    public void payForOption(String optionId) {
       if(optionId.equals("recon_alpha")){
           for (MarketAPI playerMarket : Misc.getPlayerMarkets(true)) {
               if(playerMarket.hasSubmarket(AoTDSubmarkets.RESEARCH_FACILITY_MARKET)){
                   if(playerMarket.getSubmarket(AoTDSubmarkets.RESEARCH_FACILITY_MARKET).getCargo().getCommodityQuantity(Commodities.ALPHA_CORE)!=0){
                      playerMarket.getSubmarket(AoTDSubmarkets.RESEARCH_FACILITY_MARKET).getCargo().removeCommodity(Commodities.ALPHA_CORE,1);
                      break;
                   }
               }
           }
       }
    }

    @Override
    public void applyOptionResults(String optionId) {
        super.applyOptionResults(optionId);
        if(optionId.equals("override_systems")){
            currentProgress = totalDays;
            haveDoneIt = true;
            applyProjectOutcomeWhenCompleted();
        }
    }

    @Override
    public void applyProjectOutcomeWhenCompleted() {
        if(currentValueOfOptions>0&&currentValueOfOptions<100){
            for (MarketAPI playerMarket : Misc.getPlayerMarkets(true)) {
                if(playerMarket.hasSubmarket(AoTDSubmarkets.RESEARCH_FACILITY_MARKET)){
                    playerMarket.getSubmarket(AoTDSubmarkets.RESEARCH_FACILITY_MARKET).getCargo().addSpecial(new SpecialItemData(Items.PRISTINE_NANOFORGE,null),1);
                    break;
                }
            }
        }
        else if (currentValueOfOptions<=0){
            for (MarketAPI playerMarket : Misc.getPlayerMarkets(true)) {
                if(playerMarket.hasIndustry(AoTDIndustries.RESEARCH_CENTER)){
                    playerMarket.getIndustry(AoTDIndustries.RESEARCH_CENTER).setDisrupted(150);
                    break;
                }
            }
        }
        else{
            int random = MathUtils.getRandomNumberInRange(0,items.size()-1);
            String id = items.get(random);
            for (MarketAPI playerMarket : Misc.getPlayerMarkets(true)) {
                if(playerMarket.hasSubmarket(AoTDSubmarkets.RESEARCH_FACILITY_MARKET)){
                    playerMarket.getSubmarket(AoTDSubmarkets.RESEARCH_FACILITY_MARKET).getCargo().addSpecial(new SpecialItemData(id,null),1);
                    break;
                }
            }
        }
        super.applyProjectOutcomeWhenCompleted();
    }

    @Override
    public void generateTooltipForOption(String optionId, TooltipMakerAPI tooltip) {
        super.generateTooltipForOption(optionId, tooltip);
        if (optionId.equals("reset_systems")) {
            tooltip.addPara("We will reset Nanoforge's internal systems to avoid causing further damage.", Misc.getTooltipTitleAndLightHighlightColor(), 10f);
        }
        if (optionId.equals("override_systems")) {
            tooltip.addPara("We will need to override it's systems to avoid causing further damage", Misc.getTooltipTitleAndLightHighlightColor(), 10f);
        }
        if (optionId.equals("recon_standard")) {
            tooltip.addPara("We can use knowledge we have gathered and Standard Templates we have researched to reconfigure the Nanoforge.", Misc.getTooltipTitleAndLightHighlightColor(), 10f);
            tooltip.addSectionHeading("Requirements", Alignment.MID, 10f);
            tooltip.addPara("For this option you first need to research : " + AoTDMainResearchManager.getInstance().getManagerForPlayerFaction().getResearchOptionFromRepo(AoTDTechIds.DOMAIN_TYPE_MODEL_STANDARDIZATION).getSpec().getName(), Misc.getTooltipTitleAndLightHighlightColor(), 10f);
        }
        if (optionId.equals("recon_alpha")) {
            tooltip.addPara("We can use Alpha level AI Core computing power to reconfigure the Nanoforge.", Misc.getTooltipTitleAndLightHighlightColor(), 10f);
            tooltip.addSectionHeading("Requirements", Alignment.MID, 10f);
            tooltip.addPara("We need one Alpha AI Core to be in Research Facility's Storage.", Misc.getTooltipTitleAndLightHighlightColor(), 10f);

        }
        if(optionId.equals("recon_dont")){
            tooltip.addPara("We will allow Nanofroge to reconfigure it's systems on its own.", Misc.getTooltipTitleAndLightHighlightColor(), 10f);

        }
    }

    @Override
    public void generateDescriptionForCurrentResults(TooltipMakerAPI tooltipMakerAPI) {
        for (SpecialProjectStage stage : stages) {
            if (stage.chosenOption != null) {
                if (stage.chosenOption.equals("reset_systems")) {
                    tooltipMakerAPI.addPara("We have reseted Nanoforge's systems.", 10f);
                }
                if (stage.chosenOption.equals("override_systems")) {
                    tooltipMakerAPI.addPara("We have overridden Nanoforge's systems.", 10f);
                }
                if (stage.chosenOption.equals("recon_standard")) {
                    tooltipMakerAPI.addPara("By using Domain's Standard Templates, we were able to reconfigure Nanoforge's systems.", 10f);
                }
                if (stage.chosenOption.equals("recon_alpha")) {
                    tooltipMakerAPI.addPara("With usage of our Alpha AI Core computing power we hope that Nanoforge's systems can be fully reconfigured.", 10f);
                }
                if (stage.chosenOption.equals("recon_dont")) {
                    tooltipMakerAPI.addPara("Internal systems of nanoforge have not been tampered with, our scientists believe that the nanoforge should be now going through total reconfiguration process using its built-in maintenance systems.", 10f);
                }
            }
        }
     if(haveDoneIt){
         if(currentValueOfOptions<0){
             tooltipMakerAPI.addPara("Unfortunate miscalculation made when overriding the nanoforge's systems caused it to explode, disabling the Research Facility where the restoration project was being conducted. ",Misc.getNegativeHighlightColor(),10f);
             tooltipMakerAPI.addPara("One of of Research Facilities is disabled for 150 days",Misc.getNegativeHighlightColor(),10f);
             tooltipMakerAPI.addPara("Corrupted Nanoforge has been lost.",Misc.getNegativeHighlightColor(),5f);
         }

        else{
            if(currentValueOfOptions==40){
                tooltipMakerAPI.addPara("By using our Alpha level AI core we were able to restore damaged systems to their full power.",Misc.getPositiveHighlightColor(),10f);
                tooltipMakerAPI.addPara("Pristine Nanoforge obtained.",Misc.getNegativeHighlightColor(),10f);
            }
            else if (currentValueOfOptions==60){
                tooltipMakerAPI.addPara("By using our Standard Templates we were able to restore damaged systems to their full power.",Misc.getPositiveHighlightColor(),10f);
                tooltipMakerAPI.addPara("Pristine Nanoforge obtained.",Misc.getNegativeHighlightColor(),10f);
            }
            else{
                tooltipMakerAPI.addPara("We have allowed Nanoforge to reconfigure itself on its own, which resulted in Nanoforge transforming itself into one of other colony items. Our scientists say that Nanoforge's systems must've detected too much damage to internal components and in process of reconfiguration have transformed it into another, less powerful, safer industry item.",Misc.getPositiveHighlightColor(),10f);
                tooltipMakerAPI.addPara("Random colony item obtained.",Misc.getNegativeHighlightColor(),10f);

            }

        }
     }

    }
}
