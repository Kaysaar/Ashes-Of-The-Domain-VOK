package data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.GPIndividualMegastructreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.GPMegasturcutreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.BaseMegastrucutreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.ui.customprod.components.UILinesRenderer;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.MegastructureUIMisc.createResourcePanelForSmallTooltipCondensed;

public class GPBaseMegastructure {
    public String  specId;
    MutableStat operationCostMult = new MutableStat(1);
    public SectorEntityToken entityTiedTo;
    public ArrayList<GPMegaStructureSection>megaStructureSections;
    public String getIcon(){ //Icon that should be displayed in megastructures TAB
        return Global.getSettings().getSpriteName("megastructures",getSpec().getIconId());
    }
    public String getName(){ //Name that will be displayed
        return getSpec().getName();
    }
    public static String memKey = "$aotd_megastructure";
    //Note : Width will always be 400, height can be customized
    public boolean wasInitalized = false;
    public boolean isPlanetaryMegastructure = false;
    public boolean metCustomCriteria(){
        return true;
    }
    public void printCustomCriteria(InteractionDialogAPI dialog){

    }
    public String getIndustryIfIfPresent(){
        return null;
    }
    public void setPlanetaryMegastructure(boolean planetaryMegastructure) {
        isPlanetaryMegastructure = planetaryMegastructure;
    }

    public float getPenaltyFromManager(){
        return GPManager.getInstance().getTotalPenaltyFromResources(getDemand().keySet().toArray(new String[0]));
    }
    public boolean haveRecivedStoryPoint = false;
    public CustomPanelAPI createButtonSection(float width){
        UILinesRenderer renderer = new UILinesRenderer(0f);


        CustomPanelAPI panel = Global.getSettings().createCustom(width,250,null);
        renderer.setPanel(panel);
        TooltipMakerAPI tooltip  =createTooltipButton(panel,width);
        panel.getPosition().setSize(width,tooltip.getHeightSoFar()+5);
        panel.addUIElement(tooltip).inTL(-5,0);
        return panel;
    }
   public TooltipMakerAPI createTooltipButton(CustomPanelAPI panel, float width){
       UILinesRenderer renderer = new UILinesRenderer(0f);

       renderer.setPanel(panel);
       TooltipMakerAPI tooltip = panel.createUIElement(width,50,false);
       TooltipMakerAPI tooltipOfIcon = tooltip.beginSubTooltip(width);
       TooltipMakerAPI tooltipOfCosts = tooltip.beginSubTooltip(width);
       tooltipOfCosts.addPara("Monthly running cost %s",0,Color.ORANGE,Misc.getDGSCredits(getUpkeep())).getPosition().inTL(10,25);
       tooltipOfCosts.addCustom(createResourcePanelForSmallTooltipCondensed(width,20,20, getDemand(),getProduction()),5f);
       tooltipOfIcon.addImage(getIcon(),50,50,5f);
       String starSystem = "";
       if(entityTiedTo!=null){
           starSystem = " : "+entityTiedTo.getStarSystem().getName();
       }
       tooltipOfIcon.addTitle(getName()+starSystem).getPosition().inTL(60,10);
       tooltip.addCustom(tooltipOfIcon,0f);
       tooltip.addSpacer(tooltipOfIcon.getHeightSoFar());
       tooltip.addCustom(tooltipOfCosts,-25f);
       tooltip.addSpacer(tooltipOfCosts.getHeightSoFar()+25);
       createAdditionalInfoToButton(tooltip);
       return tooltip;
   }
   public void createAdditionalInfoToButton(TooltipMakerAPI tooltipMakerAPI){

   }
    public boolean isFullyRestored(){
        for (GPMegaStructureSection megaStructureSection : megaStructureSections) {
            if(!megaStructureSection.isRestored)return false;
        }
        return true;
    }
    public  HashMap<String, Integer> getDemand(){
        HashMap<String,Integer> costs = new HashMap<>();
        for (GPMegaStructureSection megaStructureSection : megaStructureSections) {
            for (Map.Entry<String, Integer> stringIntegerEntry : megaStructureSection.getGPUpkeep().entrySet()) {
                if(costs.get(stringIntegerEntry.getKey())==null){
                    costs.put(stringIntegerEntry.getKey(),stringIntegerEntry.getValue());
                }
                else{
                    costs.put(stringIntegerEntry.getKey(),costs.get(stringIntegerEntry.getKey())+stringIntegerEntry.getValue());
                }
            }
        }

        return costs;
    }
    public void createAdditionalInfoForMega(TooltipMakerAPI tooltip){

    }
    public HashMap<String,Integer>getProduction(HashMap<String,Float> penaltyMap){
        HashMap<String,Integer> production = new HashMap<>();
        for (GPMegaStructureSection megaStructureSection : megaStructureSections) {
            for (Map.Entry<String, Integer> entry : megaStructureSection.getProduction( penaltyMap).entrySet()) {
                AoTDMisc.putCommoditiesIntoMap(production,entry.getKey(),entry.getValue());
            }
        }

        return production;
    }
    public HashMap<String,Integer>getProduction(){
       return getProduction(GPManager.getInstance().getPenaltyMap());
    }
    public HashMap<String,Integer>getProductionWithoutPenalty(){
        return getProduction(new HashMap<String, Float>());
    }
    public void createTooltipInfoBeforeClaiming(InteractionDialogAPI dialogAPI){
        TooltipMakerAPI tooltip = dialogAPI.getTextPanel().beginTooltip();

        tooltip.addSectionHeading("Costs",Alignment.MID,10f);
        dialogAPI.getTextPanel().addTooltip();
        dialogAPI.getTextPanel().addPara("Monthly upkeep of %s is estimated to be around %s",Color.ORANGE,this.getName(),Misc.getDGSCredits(getUpkeep()));
        tooltip = dialogAPI.getTextPanel().beginTooltip();
        tooltip.addSectionHeading("Megastructure information",Alignment.MID,10f);
        dialogAPI.getTextPanel().addTooltip();
        String sections = "sections";
        if(megaStructureSections.size()==1){
            sections = "section";
        }
        dialogAPI.getTextPanel().addPara("This megastructure have distinct %s "+sections+", which require great restoration efforts, to bring them to their former glory",Color.ORANGE,""+megaStructureSections.size());



    }
    public float getUpkeep(){
        int total =0;
        operationCostMult.unmodify();
        for (GPMegaStructureSection megaStructureSection : megaStructureSections) {
            total+= (int) megaStructureSection.getUpkeep(false);
        }
        return total;
    }
    public GPMegaStructureSpec getSpec() {
        return GPManager.getInstance().getMegaSpecFromList(specId);
    }

    public GPBaseMegastructure(){

    }
    //Remember to replace it with true init
    public void mockUpInit(String specID){
        this.specId = specID;
        megaStructureSections = new ArrayList<>();
        for (String sectionId : getSpec().getSectionIds()) {
            GPMegaStructureSection section = GPManager.getInstance().getMegaSectionSpecFromList(sectionId).getScript();
            section.init(this,false);
            megaStructureSections.add(section);
        }
    }
    public boolean isClaimed(){
        return GPManager.getInstance().getMegastructures().contains(this);
    }
    public void trueInit(String specId,SectorEntityToken entityTiedTo){
        this.specId = specId;
        this.entityTiedTo = entityTiedTo;
        megaStructureSections = new ArrayList<>();
        for (String sectionId : getSpec().getSectionIds()) {
            GPMegaStructureSection section = GPManager.getInstance().getMegaSectionSpecFromList(sectionId).getScript();
            section.init(this,false);
            megaStructureSections.add(section);
        }
        wasInitalized  = true;
        entityTiedTo.getMemory().set("$aotd_megastructure",this);

    }

    public boolean isHaveRecivedStoryPoint() {
        return haveRecivedStoryPoint;
    }

    public void setHaveRecivedStoryPoint(boolean haveRecivedStoryPoint) {
        this.haveRecivedStoryPoint = haveRecivedStoryPoint;
    }

    public ArrayList<GPMegaStructureSection> getMegaStructureSections() {
        return megaStructureSections;
    }

    public GPMegaStructureSection getSectionById(String sectionId){
        for (GPMegaStructureSection section : megaStructureSections) {
            if(section.getSpec().getSectionID().equals(sectionId)){
                return section;
            }
        }
        return null;
    }

    public SectorEntityToken getEntityTiedTo() {
        return entityTiedTo;
    }

    public void setEntityTiedTo(SectorEntityToken entityTiedTo) {
        this.entityTiedTo = entityTiedTo;
    }

    public void advance(float amount){
        for (GPMegaStructureSection megaStructureSection : megaStructureSections) {
            megaStructureSection.advance(amount);
        }
    }
    //This is for megastructures that have tied industry
    public void applySupplyToIndustryFirst(Industry ind){


    }
    public void applySupplyToIndustryLast(Industry ind){


    }
    public void unapplySupplyToIndustry(Industry ind){

    }
    //This is UI plugin you wanna create to handle all things in Megastrucutres tab (for each instance of megastrucutre) because it is interface extending
    //CustomUIPanelPlugin you should have there all components handled
    //clearUI is always called when switching to different megastructure while initUI initalizes UI
    public GPIndividualMegastructreMenu createUIPlugin(CustomPanelAPI parentPanel, GPMegasturcutreMenu menu) {
        return new BaseMegastrucutreMenu(this,parentPanel,menu);
    }

}
