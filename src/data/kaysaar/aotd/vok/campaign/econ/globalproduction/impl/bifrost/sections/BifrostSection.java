package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.bifrost.sections;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.bifrost.BifrostMega;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt.HypershuntMegastrcutre;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ButtonData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.OnHoverButtonTooltip;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ProgressBarComponent;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class BifrostSection extends GPMegaStructureSection {
    public SectorEntityToken gateTiedTo;

    public SectorEntityToken getGateTiedTo() {
        return gateTiedTo;
    }

    @Override
    public String getName() {
        return "Bifrost Gate : "+getStarSystemAPI().getBaseName();
    }

    public void setGateTiedTo(SectorEntityToken gateTiedTo) {
        this.gateTiedTo = gateTiedTo;
    }
    public StarSystemAPI starSystemAPI;

    public StarSystemAPI getStarSystemAPI() {
        return starSystemAPI;
    }
    public boolean isDisabled;

    public void setDisabled(boolean disabled) {
        isDisabled = disabled;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public void setStarSystemAPI(StarSystemAPI starSystemAPI) {
        this.starSystemAPI = starSystemAPI;
    }

    @Override
    public void addButtonsToList(LinkedHashMap<String, ButtonData> currentButtons) {
        ButtonData data1 = new ButtonData("De-activate Gate", this, true, Misc.getNegativeHighlightColor(), "deactivateGate", new OnHoverButtonTooltip(this, "deactivateGate"), "deactivateGate", this.getSpec().getSectionID());
        if(isRestored){
            if(isDisabled){
                data1.textButton = "Re-activate gate";
            }
            currentButtons.put("deactivateGate", data1);

        }
    }

    @Override
    public void applyEffectOfSection() {
        super.applyEffectOfSection();
    }

    @Override
    public void applyAdditionalGPChanges(HashMap<String, Integer> map) {
        if(isDisabled){
            map.clear();
        }
        if(isRestored && HypershuntMegastrcutre.isWithinReciverSystem(getGateTiedTo())){
            map.remove(AoTDCommodities.PURIFIED_TRANSPLUTONICS);
        }
    }

    @Override
    public LinkedHashMap<String, ButtonData> generateButtons() {
        return super.generateButtons();
    }

    @Override
    public String getPauseRestorationString() {
        return "Pause construction";
    }

    @Override
    public String getContinueRestoration() {
        return "Continue construction";
    }

    @Override
    public String getRestorationStringForDialog() {
        return "Construction";
    }

    @Override
    public String getContentForPauseRestoration() {
        return "By pausing construction efforts we wont be spending further money until project is resumed and it will free resources that is currently consuming";
    }

    @Override
    public String getRestorationString() {
        return "Begin construction";
    }

    @Override
    public void createTooltipForOtherInfoSection(TooltipMakerAPI tooltip, float width) {
        createTooltipForBenefits(tooltip);
        if (isRestoring) {
            tooltip.addPara("Build progress : %s", 5f, Color.ORANGE, (int) (getProgressPercentage() * 100) + "%");
            ProgressBarComponent component = new ProgressBarComponent(width-25,21,getProgressPercentage(), Misc.getDarkPlayerColor().brighter().brighter());
            tooltip.addCustom(component.getRenderingPanel(),5f);
        }
        if(isRestored&&getGateTiedTo()!=null) {
            tooltip.addPara("Bifrost Gate Operational !",Misc.getPositiveHighlightColor(),5f);
            int cooldown = (int) this.getGateTiedTo().getMemory().getFloat("$cooldown");
            if(!HypershuntMegastrcutre.isWithinReciverSystem(this.getGateTiedTo())){
                tooltip.addPara("Cooldown of gate %s",5f,Color.ORANGE, AoTDMisc.convertDaysToString(cooldown));
            }
            else{
                tooltip.addPara("Due to connection with Hypershunt Receiver, gate have no cooldown!",Misc.getPositiveHighlightColor(),5f);
            }


        }
        createUpkeepSection(tooltip,width);
        addPenaltyFromLackOfResourcesInfo(tooltip,width);


    }

    @Override
    public void createTooltipForBenefits(TooltipMakerAPI tooltip) {
        if (isRestored) {
            tooltip.addSectionHeading("Effects of " + this.getName(), Alignment.MID, 5f);

        } else {
            tooltip.addSectionHeading("Effects upon completion", Alignment.MID, 5f);

        }
    }

    @Override
    public void addPenaltyFromLackOfResourcesInfo(TooltipMakerAPI tooltip, float width) {
        if(isRestored){
            tooltip.addPara("Any shortage of resources required to maintain station will cause drop in Combat Readiness and will make it's regeneration much slower",5f);
        }
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        MarketAPI market = null;
        for (MarketAPI marketAPI : Global.getSector().getEconomy().getMarkets(starSystemAPI)) {
            if(marketAPI.getFaction()!=null) {
                if(marketAPI.isPlayerOwned()||(marketAPI.getFaction().isPlayerFaction())){
                    market = marketAPI;
                    break;
                }
            }

        }
        if(market==null && isRestoring){
            isRestoring = false;
            progressOfRestoration = 0f;
            BifrostMega mega = (BifrostMega) getMegastructureTiedTo();
            mega.removeBifrostGate(this);
            return;
        }

    }

    @Override
    public void applyReductionOfUpkeep() {
        if(isDisabled){
            upkeepMult.modifyMult("disabled",0.5f);
        }
    }


    @Override
    public HashMap<String, Integer> getGPUpkeep() {
        return super.getGPUpkeep();
    }
    @Override
    public void aboutToReconstructSection() {
        super.aboutToReconstructSection();
        MarketAPI market = null;
        for (MarketAPI marketAPI : Global.getSector().getEconomy().getMarkets(starSystemAPI)) {
            if(marketAPI.isPlayerOwned()||marketAPI.getFaction().isPlayerFaction()){
                market = marketAPI;
                break;
            }
        }
        if(market!=null){
            gateTiedTo = BifrostMega.spawnGate(market);
        }

    }
}
