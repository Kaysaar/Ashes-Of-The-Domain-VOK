package data.kaysaar.aotd.vok.scripts.research.researchprojects;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.ui.P;
import data.kaysaar.aotd.vok.Ids.AoTDSubmarkets;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.models.ResearchProject;
import data.kaysaar.aotd.vok.models.SpecialProjectStage;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

public class NanoforgeRestoration extends ResearchProject {

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
            tooltipMakerAPI.addPara("We will try to repair corrupted nanoforge",Misc.getPositiveHighlightColor(),10f);
        }
        else{
            tooltipMakerAPI.addPara("For this project to start, we need corrupted nanoforge in one of our research facilities!",Misc.getNegativeHighlightColor(),10f);

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
        if(currentValueOfOptions>0){
            for (MarketAPI playerMarket : Misc.getPlayerMarkets(true)) {
                if(playerMarket.hasSubmarket(AoTDSubmarkets.RESEARCH_FACILITY_MARKET)){
                    playerMarket.getSubmarket(AoTDSubmarkets.RESEARCH_FACILITY_MARKET).getCargo().addSpecial(new SpecialItemData(Items.PRISTINE_NANOFORGE,null),1);
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
            tooltip.addPara("We will reset Nanoforge internal systems to avoid further damage", Misc.getTooltipTitleAndLightHighlightColor(), 10f);
        }
        if (optionId.equals("override_systems")) {
            tooltip.addPara("We need to  override it's systems to avoid further damage", Misc.getTooltipTitleAndLightHighlightColor(), 10f);
        }
        if (optionId.equals("recon_standard")) {
            tooltip.addPara("We can use our knowledge and Standard Templates we have researched to reconfigure Nanoforge", Misc.getTooltipTitleAndLightHighlightColor(), 10f);
            tooltip.addSectionHeading("Requirements", Alignment.MID, 10f);
            tooltip.addPara("For this option you need to research : " + AoTDMainResearchManager.getInstance().getManagerForPlayerFaction().getResearchOptionFromRepo(AoTDTechIds.DOMAIN_TYPE_MODEL_STANDARDIZATION).getSpec().getName(), Misc.getTooltipTitleAndLightHighlightColor(), 10f);
        }
        if (optionId.equals("recon_alpha")) {
            tooltip.addPara("We can use Alpha level AI Core to reconfigure Nanoforge", Misc.getTooltipTitleAndLightHighlightColor(), 10f);
            tooltip.addSectionHeading("Requirements", Alignment.MID, 10f);
            tooltip.addPara("We need one Alpha AI Core to be in Research Facility Storage", Misc.getTooltipTitleAndLightHighlightColor(), 10f);

        }
    }

    @Override
    public void generateDescriptionForCurrentResults(TooltipMakerAPI tooltipMakerAPI) {
        for (SpecialProjectStage stage : stages) {
            if (stage.chosenOption != null) {
                if (stage.chosenOption.equals("reset_systems")) {
                    tooltipMakerAPI.addPara("We have rested Nanoforge systems", 10f);
                }
                if (stage.chosenOption.equals("override_systems")) {
                    tooltipMakerAPI.addPara("We have overridden Nanoforge systems", 10f);
                }
                if (stage.chosenOption.equals("recon_standard")) {
                    tooltipMakerAPI.addPara("By using Domain Standard Templates, that we have reverse engineered we were able to reconfigure system", 10f);
                }
                if (stage.chosenOption.equals("recon_alpha")) {
                    tooltipMakerAPI.addPara("With usage of Alpha AI Core we hope that systems can be fully reconfigured ", 10f);
                }
            }
        }
     if(haveDoneIt){
         if(currentValueOfOptions<0){
             tooltipMakerAPI.addPara("Unfortunate overriding nanoforge systems caused it to explode, which disabled Research Facility, where project was conducted ",Misc.getNegativeHighlightColor(),10f);
             tooltipMakerAPI.addPara("Disables one of Research Facilities for 150 days",Misc.getNegativeHighlightColor(),10f);
             tooltipMakerAPI.addPara("Losses Corrupted Nanoforge",Misc.getNegativeHighlightColor(),5f);
         }

        else{
            if(currentValueOfOptions==600){
                tooltipMakerAPI.addPara("By using Alpha level AI core we were able to restore damaged system to it's full capacity",Misc.getPositiveHighlightColor(),10f);
                tooltipMakerAPI.addPara("Gain Pristine Nanoforge",Misc.getNegativeHighlightColor(),10f);
            }
            else{
                tooltipMakerAPI.addPara("By using our Standard Templates we were able to restore damaged system to it's full capacity",Misc.getPositiveHighlightColor(),10f);
                tooltipMakerAPI.addPara("Gain Pristine Nanoforge",Misc.getNegativeHighlightColor(),10f);
            }

        }
     }

    }
}
