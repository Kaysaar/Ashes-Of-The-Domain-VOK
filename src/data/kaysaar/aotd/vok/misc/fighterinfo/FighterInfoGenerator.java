package data.kaysaar.aotd.vok.misc.fighterinfo;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.loading.*;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.misc.shipinfo.ShipRenderer;
import data.kaysaar.aotd.vok.misc.shipinfo.StatNumberUIPackage;


import java.awt.*;
import java.util.ArrayList;

import static data.kaysaar.aotd.vok.misc.shipinfo.ShipInfoGenerator.generateOtherInfo;


public class FighterInfoGenerator {
    public static void generate(TooltipMakerAPI tooltip, FighterWingSpecAPI wing, float widthOfTooltip) {
        tooltip.addTitle(wing.getWingName());
        Misc.addDesignTypePara(tooltip,wing.getVariant().getHullSpec().getManufacturer(),10f);
        Description descrp = Global.getSettings().getDescription(wing.getVariant().getHullSpec().getHullId(), Description.Type.SHIP);
        tooltip.addPara(descrp.getText1(),10f);
        tooltip.addSectionHeading("Technical Data",Alignment.MID,10f);
        CustomPanelAPI firstRowOfData = Global.getSettings().createCustom(widthOfTooltip,70,null);
        TooltipMakerAPI tooltipOfFirstRow =firstRowOfData.createUIElement(widthOfTooltip+18,70,false);
        CustomPanelAPI secondRowOfData = Global.getSettings().createCustom(widthOfTooltip,40,null);
        TooltipMakerAPI secondTooltipOfRow =secondRowOfData.createUIElement(widthOfTooltip+18,40,false);
        CustomPanelAPI thirdRowOfData = Global.getSettings().createCustom(widthOfTooltip,60,null);
        TooltipMakerAPI tooltipOfThirdRow =thirdRowOfData.createUIElement(widthOfTooltip+18,60,false);
        for (StatNumberUIPackage fighterStat : getFighterStatsFirstRow(wing,tooltipOfFirstRow)) {
            fighterStat.placeLabelToParent(tooltipOfFirstRow);
        }
        for (StatNumberUIPackage fighterStat : getFighterStatsSecondRow(wing,secondTooltipOfRow)) {
            fighterStat.placeLabelToParent(secondTooltipOfRow);
        }
        for (StatNumberUIPackage fighterStat : getFighterStatsThirdRow(wing,tooltipOfThirdRow)) {
            fighterStat.placeLabelToParent(tooltipOfThirdRow);
        }
        firstRowOfData.addUIElement(tooltipOfFirstRow).inTL(-5,0);
        secondRowOfData.addUIElement(secondTooltipOfRow).inTL(-5,0);
        thirdRowOfData.addUIElement(tooltipOfThirdRow).inTL(-5,0);
        tooltip.addCustom(firstRowOfData,5f);
        tooltip.addSpacer(10f);
        tooltip.addCustom(secondRowOfData,5f);
        tooltip.addSpacer(10f);
        tooltip.addCustom(thirdRowOfData,5f);
        FleetMemberAPI ship = Global.getFactory().createFleetMember(FleetMemberType.FIGHTER_WING,wing.getId());
        ship.getHullSpec().getHullId();
        CustomPanelAPI panelOfOtherInfo = generateOtherInfo(ship,widthOfTooltip,60,true);
        tooltip.addCustom(panelOfOtherInfo,5f);
        tooltip.addSectionHeading("Replacing Combat Losses",Alignment.MID,5f);
        tooltip.addPara("The replacement rate gradually goes down while the carrier's fighter wings are on average not at full strength, and is slowly regained otherwise. It goes down more slowly while fighters are ordered to regroup.",10f);
        tooltip.addPara("Fighters that return to a carrier - for example, after a bombing run - are relaunched quickly and do not require the full replacement time.",10f);
        tooltip.addSpacer(3f);
    }

    public static ArrayList<StatNumberUIPackage> getFighterStatsFirstRow(FighterWingSpecAPI wing, TooltipMakerAPI tooltipMakerAPI){
        ArrayList<StatNumberUIPackage> firstColumnLogisticData = new ArrayList<>();
        String text1 = wing.getRoleDesc();
        firstColumnLogisticData.add(new StatNumberUIPackage(tooltipMakerAPI.addPara("Primary role", 3f), text1, null,Color.ORANGE, null));
        text1 = (int)wing.getOpCost(null)+"";
        firstColumnLogisticData.add(new StatNumberUIPackage(tooltipMakerAPI.addPara("Ordnance points", 3f), text1, null,Color.ORANGE, null));
        text1 = String.valueOf((int)wing.getVariant().getHullSpec().getMinCrew());
        firstColumnLogisticData.add(new StatNumberUIPackage(tooltipMakerAPI.addPara("Crew per fighter", 3f), text1, null,Color.ORANGE, null));
        text1 = (int)wing.getRange()+"";
        String rangeText = "Maximum engagement range";
        if(wing.getRole().equals(WingRole.SUPPORT) ){
            rangeText = "Maximum support range";
        }
        firstColumnLogisticData.add(new StatNumberUIPackage(tooltipMakerAPI.addPara(rangeText, 3f), text1, null,Color.ORANGE, null));

        return firstColumnLogisticData;
    }
    public static ArrayList<StatNumberUIPackage> getFighterStatsSecondRow(FighterWingSpecAPI wing, TooltipMakerAPI tooltipMakerAPI){
        ArrayList<StatNumberUIPackage> columnData = new ArrayList<>();
        String text1 = (int)wing.getNumFighters()+"";
        columnData.add(new StatNumberUIPackage(tooltipMakerAPI.addPara("Fighters in wing", 3f), text1, null,Color.ORANGE, null));
        text1 = (int)wing.getRefitTime()+"";
        columnData.add(new StatNumberUIPackage(tooltipMakerAPI.addPara("Base replacement time(seconds)", 3f), text1, null,Color.ORANGE, null));
        return columnData;
    }
    public static ArrayList<StatNumberUIPackage> getFighterStatsThirdRow(FighterWingSpecAPI wing, TooltipMakerAPI tooltipMakerAPI){
        ArrayList<StatNumberUIPackage> columnData = new ArrayList<>();
        FleetMemberAPI fleetMemberAPI = Global.getFactory().createFleetMember(FleetMemberType.FIGHTER_WING,wing.getId());
        String text1 = (int)wing.getVariant().getHullSpec().getHitpoints()+"";
        columnData.add(new StatNumberUIPackage(tooltipMakerAPI.addPara("Hull integrity", 3f), text1, null,Color.ORANGE, null));

         text1 = (int)wing.getVariant().getHullSpec().getArmorRating()+"";
        columnData.add(new StatNumberUIPackage(tooltipMakerAPI.addPara("Armor rating", 3f), text1, null,Color.ORANGE, null));
        text1 = (int)fleetMemberAPI.getStats().getMaxSpeed().getBaseValue()+"";
        columnData.add(new StatNumberUIPackage(tooltipMakerAPI.addPara("Top speed", 3f), text1, null,Color.ORANGE, null));
        return columnData;
    }
    public static Pair<CustomPanelAPI, FighterIconRenderer> createFormationPanel(FighterWingSpecAPI wing, FormationType formation, int iconSize, int amountOfWings) {
        FighterIconRenderer renderer = new FighterIconRenderer(wing);
        CustomPanelAPI panel = Global.getSettings().createCustom(iconSize, iconSize, renderer);
        SpriteAPI shipSprite = Global.getSettings().getSprite(wing.getVariant().getHullSpec().getSpriteName());
        ArrayList<CustomPanelAPI>panels = generateRectanglePattern(panel,amountOfWings);
        shipSprite.setAlphaMult(0f);
        float originalWidth = shipSprite.getWidth();
        float originalHeight = shipSprite.getHeight();
        float trueIconSize = panels.get(0).getPosition().getWidth();
        renderer.setTester(panel);


        // Get the original width and height of the sprite


        // Calculate the aspect ratio
        float aspectRatio = originalWidth / originalHeight;

        // Variables for the new width and height
        float newWidth, newHeight;

        // Determine which dimension to resize to fit the icon size
        if (originalWidth <= trueIconSize && originalHeight <= trueIconSize) {
            newWidth = originalWidth;
            newHeight = originalHeight;
        } else {
            if (originalWidth > originalHeight) {
                // Width is the larger dimension
                newWidth = trueIconSize;
                newHeight = trueIconSize / aspectRatio;
            } else {
                // Height is the larger dimension or they are equal
                newHeight = trueIconSize;
                newWidth = trueIconSize * aspectRatio;
            }
        }
        if (aspectRatio >= 1) {
            if (originalWidth > originalHeight) {
                // Width is the larger dimension
                newWidth = trueIconSize * 0.8f;
                newHeight = trueIconSize * 0.8f / aspectRatio;
            } else {
                // Height is the larger dimension or they are equal
                newHeight = trueIconSize * 0.8f;
                newWidth = trueIconSize * 0.8f * aspectRatio;
            }
        }
        renderer.setDimensions((newHeight/originalHeight));
        if(wing.getNumFighters()==1){
            renderer.setDimensions((newHeight/originalHeight)*0.8f);
        }
        renderer.addPanels(panels);
        return new Pair<>(panel,renderer);
    }
    public static ArrayList<CustomPanelAPI>generateRectanglePattern(CustomPanelAPI parent,int amount){
        //width == height
        float panelWidth = parent.getPosition().getWidth();
        float iconsize = panelWidth;
        float centerY = panelWidth/2;
        ArrayList<CustomPanelAPI>panels = new ArrayList<>();
        if(amount==1){
            CustomPanelAPI subPanel = parent.createCustomPanel(iconsize,iconsize,null);
            panels.add(subPanel);
            parent.addComponent(subPanel).inTL(0,0);

        }
        if(amount==2){
            iconsize = parent.getPosition().getWidth()/2;
            CustomPanelAPI subPanel = parent.createCustomPanel(iconsize,iconsize,null);
            CustomPanelAPI subPanel2 = parent.createCustomPanel(iconsize,iconsize,null);
            panels.add(subPanel);
            panels.add(subPanel2);
            parent.addComponent(subPanel).inTL(0,centerY-iconsize/2);
            parent.addComponent(subPanel2).inTL(iconsize,centerY-iconsize/2);
        }
        if(amount==3){
            iconsize = parent.getPosition().getWidth()/3;
            CustomPanelAPI subPanel = parent.createCustomPanel(iconsize,iconsize,null);
            CustomPanelAPI subPanel2 = parent.createCustomPanel(iconsize,iconsize,null);
            CustomPanelAPI subPanel3 = parent.createCustomPanel(iconsize,iconsize,null);
            panels.add(subPanel);
            panels.add(subPanel2);
            panels.add(subPanel3);

            parent.addComponent(subPanel).inTL(0,centerY-iconsize/2);
            parent.addComponent(subPanel2).inTL(iconsize,centerY-iconsize/2);
            parent.addComponent(subPanel3).inTL(iconsize*2,centerY-iconsize/2);
        }
        if(amount==4){
            iconsize = parent.getPosition().getWidth()/2;

            CustomPanelAPI subPanel = parent.createCustomPanel(iconsize,iconsize,null);
            CustomPanelAPI subPanel2 = parent.createCustomPanel(iconsize,iconsize,null);
            CustomPanelAPI subPanel3 = parent.createCustomPanel(iconsize,iconsize,null);
            CustomPanelAPI subPanel4 = parent.createCustomPanel(iconsize,iconsize,null);

            panels.add(subPanel);
            panels.add(subPanel2);
            panels.add(subPanel3);
            panels.add(subPanel4);
            parent.addComponent(subPanel).inTL(0,0);
            parent.addComponent(subPanel2).inTL(iconsize,0);
            parent.addComponent(subPanel3).inTL(0,iconsize);
            parent.addComponent(subPanel4).inTL(iconsize,iconsize);
        }
        if(amount==5){
            iconsize = parent.getPosition().getWidth()/3;

            CustomPanelAPI subPanel = parent.createCustomPanel(iconsize,iconsize,null);
            CustomPanelAPI subPanel2 = parent.createCustomPanel(iconsize,iconsize,null);
            CustomPanelAPI subPanel3 = parent.createCustomPanel(iconsize,iconsize,null);
            CustomPanelAPI subPanel4 = parent.createCustomPanel(iconsize,iconsize,null);
            CustomPanelAPI subPanel5 = parent.createCustomPanel(iconsize,iconsize,null);
            panels.add(subPanel);
            panels.add(subPanel2);
            panels.add(subPanel3);
            panels.add(subPanel4);
            panels.add(subPanel5);
            parent.addComponent(subPanel).inTL(0,centerY-iconsize);
            parent.addComponent(subPanel2).inTL(iconsize,centerY-iconsize);
            parent.addComponent(subPanel3).inTL(iconsize*2,centerY-iconsize);
            float spacer = iconsize/2;

            parent.addComponent(subPanel4).inTL(spacer,centerY);
            parent.addComponent(subPanel5).inTL(spacer*3,centerY);

        }
        if(amount>=6){
            iconsize = parent.getPosition().getWidth()/3;

            CustomPanelAPI subPanel = parent.createCustomPanel(iconsize,iconsize,null);
            CustomPanelAPI subPanel2 = parent.createCustomPanel(iconsize,iconsize,null);
            CustomPanelAPI subPanel3 = parent.createCustomPanel(iconsize,iconsize,null);
            CustomPanelAPI subPanel4 = parent.createCustomPanel(iconsize,iconsize,null);
            CustomPanelAPI subPanel5 = parent.createCustomPanel(iconsize,iconsize,null);
            CustomPanelAPI subPanel6 = parent.createCustomPanel(iconsize,iconsize,null);
            panels.add(subPanel);
            panels.add(subPanel2);
            panels.add(subPanel3);
            panels.add(subPanel4);
            panels.add(subPanel5);
            panels.add(subPanel6);
            parent.addComponent(subPanel).inTL(0,centerY-iconsize);
            parent.addComponent(subPanel2).inTL(iconsize,centerY-iconsize);
            parent.addComponent(subPanel3).inTL(iconsize*2,centerY-iconsize);
            parent.addComponent(subPanel4).inTL(0,centerY);
            parent.addComponent(subPanel5).inTL(iconsize,centerY);
            parent.addComponent(subPanel6).inTL(iconsize*2,centerY);

        }
        return panels;

    }
    @Deprecated
    public static ArrayList<CustomPanelAPI>generateVFormPattern(FighterWingSpecAPI wing,CustomPanelAPI parent,int amount,float iconSize){
        //I failed if you wanna render fighters in wings good luck, I aint doing this shit
        ArrayList<CustomPanelAPI>subParents= new ArrayList<>();
        if(amount==1){
            CustomPanelAPI subParent = parent.createCustomPanel(iconSize,iconSize,null);
            parent.addComponent(subParent).inTL(0,0);
            subParents.add(parent);
            return subParents;
        }
        if(amount==2){
            float iconSizePerOne = iconSize;
            CustomPanelAPI subParentChecker = parent.createCustomPanel(iconSize,iconSize,null);
            float x1Begin = parent.getPosition().getWidth()/4;
            float x1End= x1Begin+iconSize;
            float x2Begin = parent.getPosition().getWidth()/2;
            float x2End = x2Begin+iconSize;
            if (x2End>iconSize){

            }
        }
        if(amount==3){

        }
        if(amount==4){

        }
        if(amount==5){

        }
        if(amount>=6){

        }
        return null;
    }



}
