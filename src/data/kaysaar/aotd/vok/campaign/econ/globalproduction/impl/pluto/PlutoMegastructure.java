package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.pluto;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.pluto.sections.OpticCommandNexus;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.pluto.sections.PlutoForgeSection;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.pluto.ui.PlutoUI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.GPIndividualMegastructreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.GPMegasturcutreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;
import data.kaysaar.aotd.vok.campaign.econ.industry.BaseMegastructureIndustry;

import java.awt.*;
import java.util.ArrayList;

public class PlutoMegastructure extends GPBaseMegastructure {
    public static String memKeyToPlanet = "$aotd_pluto_planet";
    public MarketAPI getMarketTiedTo() {
        return entityTiedTo.getMarket();
    }
    public static ArrayList<String>resourcesProduced = new ArrayList<>();
    static {
        resourcesProduced.add(Commodities.METALS);
        resourcesProduced.add(Commodities.RARE_METALS);
    }

    @Override
    public GPIndividualMegastructreMenu createUIPlugin(CustomPanelAPI parentPanel, GPMegasturcutreMenu menu) {
        return new PlutoUI(this,parentPanel,menu);
    }

    @Override
    public String getIndustryIfIfPresent() {
        return "pluto_station";
    }

    @Override
    public void trueInit(String specId, SectorEntityToken entityTiedTo) {
        super.trueInit(specId, entityTiedTo);
        isPlanetaryMegastructure = true;
        if(Global.getSettings().isDevMode()){
            entityTiedTo.getStarSystem().setBaseName("Pluto");
        }

    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        MarketAPI marketAPI = getMarketTiedTo();
        if(marketAPI!=null&&marketAPI.getFaction()!=null) {
            if(marketAPI.getFaction().isPlayerFaction()||marketAPI.isPlayerOwned()){
                BaseMegastructureIndustry ind = (BaseMegastructureIndustry) marketAPI.getIndustry("pluto_station");
                if(ind ==null){
                    marketAPI.addIndustry("pluto_station");
                    ind = (BaseMegastructureIndustry) marketAPI.getIndustry("pluto_station");
                }
                if(ind.getMegastructure()==null){
                    ind.setMegastructure(this);
                }

            }
        }
    }

    @Override
    public void applySupplyToIndustryFirst(Industry ind) {
         int ore = getLaserSection().getAvailableOresAmount(Commodities.ORE);
         int rore= getLaserSection().getAvailableOresAmount(Commodities.RARE_ORE);
         int access = (int) Math.floor((ind.getMarket().getAccessibilityMod().getFlatBonus()*10));
         ore = (int) Math.min(ore,access);
         rore = (int) Math.min(rore,access);

         ind.supply("aotd_pluto",Commodities.ORE,ore,"Megastructure");
         ind.supply("aotd_pluto",Commodities.RARE_ORE,rore,"Megastructure");

        for (PlutoForgeSection consumerSection : getConsumerSections()) {
            for (String s : resourcesProduced) {
                if(consumerSection.getAmountOfResources(s)>0){
                    int amount = consumerSection.getAmountOfResources(s);
                    amount = Math.min(amount,access);
                    ind.supply("aotd_pluto",s,amount,"Megastructure");
                }
            }
        }
    }

    @Override
    public void unapplySupplyToIndustry(Industry ind) {
        ind.supply("aotd_pluto",Commodities.ORE,0,"Megastructure");
        ind.supply("aotd_pluto",Commodities.RARE_ORE,0,"Megastructure");
        for (String s : resourcesProduced) {
            ind.supply("aotd_pluto",s,0,"Megastructure");

        }
    }

    @Override
    public void createAdditionalInfoToButton(TooltipMakerAPI tooltipMakerAPI) {
        TooltipMakerAPI tooltip =  tooltipMakerAPI.beginSubTooltip(tooltipMakerAPI.getWidthSoFar());
        getLaserSection().createTooltipForOreMiningLite(tooltip);
        tooltipMakerAPI.addCustom(tooltip,5f);
        tooltipMakerAPI.setHeightSoFar(tooltipMakerAPI.getHeightSoFar()+tooltip.getHeightSoFar());

    }
    public OpticCommandNexus getLaserSection(){
        return (OpticCommandNexus) getSectionById("pluto_ocn");
    }
    public int getAvailableResources(int produced,String key){
        int have = produced;
        for (PlutoForgeSection consumerSection : getConsumerSections()) {
            int cur = consumerSection.getAssignedResources(key);
            have -= cur;
        }
        if(have<0){
            for (PlutoForgeSection consumerSection : getConsumerSections()) {
                consumerSection.updateResourceDesignated(key,0);
            }
            have = produced;
        }
        return have;
    }
    public ArrayList<PlutoForgeSection>getConsumerSections(){
        ArrayList<PlutoForgeSection>sections = new ArrayList<>();
        for (GPMegaStructureSection megaStructureSection : getMegaStructureSections()) {
            if(megaStructureSection instanceof PlutoForgeSection){
                sections.add((PlutoForgeSection) megaStructureSection);
            }
        }
        return sections;
    }
    public int getAvailableResourcesDirectly(String key){
        return getLaserSection().getAvailableOresAmount(key);
    }
    @Override
    public void createAdditionalInfoForMega(TooltipMakerAPI tooltip) {
        super.createAdditionalInfoForMega(tooltip);
        tooltip.addSectionHeading("Current effects", Alignment.MID,5f);
        Pair<String,Color> pair = getLaserStatus();
        tooltip.addPara("Current laser status : %s",5f,pair.two,pair.one);
        tooltip.addSectionHeading("Accessibility selling power",Alignment.MID,5f);
        int access = (int) Math.floor((getMarketTiedTo().getAccessibilityMod().getFlatBonus()*10));
        tooltip.addPara("Maximum amount of %s units of supply can sold to %s accessibility",5f,Color.ORANGE,access+"",(int)(getMarketTiedTo().getAccessibilityMod().getFlatBonus()*100)+"%");
        getLaserSection().createTooltipForOreMining(tooltip);

    }
    public Pair<String,Color> getLaserStatus(){
        if(!getLaserSection().isRestored){
            return new Pair<>("damaged, requires repairs", Misc.getNegativeHighlightColor());
        }
        else{
            if(getLaserSection().isFiringLaser()){
                return new Pair<>("operational: laser magnitude: "+getLaserSection().getCurrentMagnitude()*10+"%", Misc.getPositiveHighlightColor());
            }
            else{
                return new Pair<>("laser turned off", Misc.getTooltipTitleAndLightHighlightColor());
            }
        }
    }

}
