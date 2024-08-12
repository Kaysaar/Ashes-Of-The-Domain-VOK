package data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.loading.FormationType;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import com.fs.starfarer.combat.entities.Ship;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPOption;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPOrder;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPSpec;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.NidavelirMainPanelPlugin;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.misc.fighterinfo.FighterIconRenderer;
import data.kaysaar.aotd.vok.misc.fighterinfo.FighterInfoGenerator;
import data.kaysaar.aotd.vok.misc.shipinfo.ShipInfoGenerator;
import data.kaysaar.aotd.vok.misc.shipinfo.ShipRenderer;
import data.kaysaar.aotd.vok.misc.weaponinfo.WeaponInfoGenerator;
import data.kaysaar.aotd.vok.misc.weaponinfo.WeaponSpriteRenderer;


import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class UIData {
    public static final float WIDTH = Global.getSettings().getScreenWidth()-20;
    public static final float HEIGHT = Global.getSettings().getScreenHeight()-20;
    public static final float HEIGHT_OF_OPTIONS = HEIGHT*0.6f;
    public static final float WIDTH_OF_OPTIONS = WIDTH*0.65f;
    public static final float WIDTH_OF_ORDERS = WIDTH*0.35f-30f;
    public static final float WIDTH_OF_ORDERS_PANELS = WIDTH_OF_ORDERS+1;
    public static final float WIDTH_OF_NAMES_ORDER =WIDTH_OF_ORDERS_PANELS*0.3f;
    public static final float WIDTH_OF_NAMES_QTY =WIDTH_OF_ORDERS_PANELS*0.1f;
    public static final float WIDTH_OF_NAMES_COST =WIDTH_OF_ORDERS_PANELS*0.3f;
    public static final float WIDTH_OF_NAMES_DAYS =WIDTH_OF_ORDERS_PANELS*0.1f;
    public static final float WIDTH_OF_NAMES_GP =WIDTH_OF_ORDERS_PANELS*0.2f-5f;
    public static final float HEIGHT_OF_BUTTONS = 40f;
    public static final float WIDTH_OF_NAME = WIDTH_OF_OPTIONS*0.26f;
    public static final float WIDTH_OF_BUILD_TIME = WIDTH_OF_OPTIONS*0.08f;
    public static final float WIDTH_OF_SIZE = WIDTH_OF_OPTIONS*0.08f;
    public static final float WIDTH_OF_TYPE = WIDTH_OF_OPTIONS*0.08f;
    public static final float WIDTH_OF_DESIGN_TYPE = WIDTH_OF_OPTIONS*0.20f;
    public static final float WIDTH_OF_CREDIT_COST = WIDTH_OF_OPTIONS*0.15f;
    public static final float WIDTH_OF_GP = WIDTH_OF_OPTIONS*0.15f-5f;


    public static UiPackage getShipOption(GPOption option){
        FactionAPI faction = Global.getSector().getPlayerFaction();
        Color base = faction.getBaseUIColor();
        Color bright = faction.getBrightUIColor();
        Color bg = faction.getDarkUIColor();
        CustomPanelAPI panel = Global.getSettings().createCustom(WIDTH_OF_OPTIONS-5,HEIGHT_OF_BUTTONS,null);
        TooltipMakerAPI mainTooltip = panel.createUIElement(WIDTH_OF_OPTIONS-5,HEIGHT_OF_BUTTONS,false);
        ButtonAPI button = mainTooltip.addAreaCheckbox("",option,base,bg,bright,WIDTH_OF_OPTIONS-5,HEIGHT_OF_BUTTONS,0f);
        button.getPosition().inTL(0,0);

        Pair<CustomPanelAPI, ShipRenderer> panelImage = ShipInfoGenerator.getShipImage(option.getSpec().getShipHullSpecAPI(),30,null);
        LabelAPI name = mainTooltip.addPara(option.getSpec().getShipHullSpecAPI().getHullName(),0f, Misc.getTooltipTitleAndLightHighlightColor());
        name.autoSizeToWidth(WIDTH_OF_NAME-35);
        name.getPosition().inTL(35, getyPad(name));
        float days = option.getSpec().days/GPManager.getInstance().getProductionSpeedBonus().getModifiedValue();
        if(days<=1)days=1;
        LabelAPI buildTime = mainTooltip.addPara(AoTDMisc.convertDaysToString((int)days),0f);
        String variantId = null;
        for (String allVariantId : Global.getSettings().getAllVariantIds()) {
            if(allVariantId.contains(option.getSpec().getIdOfItemProduced())){
                variantId=allVariantId;
                break;
            }
        }
        CampaignFleetAPI fleet = Global.getFactory().createEmptyFleet(Global.getSector().getPlayerFaction().getId(),"test",false);

         FleetMemberAPI memberAPI = Global.getFactory().createFleetMember(FleetMemberType.SHIP,Global.getSettings().createEmptyVariant(variantId,option.getSpec().getShipHullSpecAPI()));
        fleet.getCargo().addCrew((int) memberAPI.getMinCrew());
        fleet.getCargo().addSupplies(memberAPI.getCargoCapacity()-10);
        fleet.getCargo().addFuel(memberAPI.getFuelCapacity());
         fleet.getFleetData().addFleetMember(memberAPI);
        memberAPI.getRepairTracker().setCR(70);
        memberAPI.getRepairTracker().computeRepairednessFraction();
        final FleetMemberAPI member = memberAPI;
        LabelAPI size = mainTooltip.addPara(Misc.getHullSizeStr(option.getSpec().getShipHullSpecAPI().getHullSize()),0f);
        LabelAPI type = mainTooltip.addPara(AoTDMisc.getType(option.getSpec().getShipHullSpecAPI()),0f);
        LabelAPI designType =mainTooltip.addPara(option.getSpec().getShipHullSpecAPI().getManufacturer(),Misc.getDesignTypeColor(option.getSpec().getShipHullSpecAPI().getManufacturer()),0f);
        LabelAPI credits = mainTooltip.addPara(Misc.getDGSCredits(option.getSpec().getCredistCost()),0f, Color.ORANGE);
        buildTime.getPosition().inTL(getxPad(buildTime, getCenter(WIDTH_OF_NAME,WIDTH_OF_BUILD_TIME)),getyPad(size));
        size.getPosition().inTL(getxPad(size, getCenter(WIDTH_OF_NAME+WIDTH_OF_BUILD_TIME,WIDTH_OF_SIZE)),getyPad(size));
        type.getPosition().inTL(getxPad(type, getCenter(WIDTH_OF_NAME+WIDTH_OF_BUILD_TIME+WIDTH_OF_SIZE,WIDTH_OF_TYPE)),getyPad(type));
        designType.getPosition().inTL(getxPad(designType, getCenter(WIDTH_OF_NAME+WIDTH_OF_BUILD_TIME+WIDTH_OF_SIZE+WIDTH_OF_TYPE,WIDTH_OF_DESIGN_TYPE)),getyPad(designType));
        credits.getPosition().inTL(getxPad(credits, getCenter(WIDTH_OF_NAME+WIDTH_OF_BUILD_TIME+WIDTH_OF_TYPE+WIDTH_OF_SIZE+WIDTH_OF_DESIGN_TYPE,WIDTH_OF_CREDIT_COST)),getyPad(credits));
        CustomPanelAPI panelImg = getGPCostPanel(WIDTH_OF_GP,HEIGHT_OF_BUTTONS,option.getSpec());
        mainTooltip.addCustomDoNotSetPosition(panelImg).getPosition().setLocation(0,0).inTL(WIDTH_OF_NAME+WIDTH_OF_BUILD_TIME+WIDTH_OF_TYPE+WIDTH_OF_SIZE+WIDTH_OF_DESIGN_TYPE+WIDTH_OF_CREDIT_COST,0);
        mainTooltip.addCustom(panelImage.one,5f).getPosition().inTL(0,4);
        final CustomPanelAPI panelAPIs = ShipInfoGenerator.getShipImage(option.getSpec().getShipHullSpecAPI(),250,null).one;
        mainTooltip.addTooltipToPrevious(new TooltipMakerAPI.TooltipCreator() {
            @Override
            public boolean isTooltipExpandable(Object tooltipParam) {
                return false;
            }

            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return 1000;
            }

            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                ShipInfoGenerator.generate(tooltip,member,null,panelAPIs,990);
            }
        }, TooltipMakerAPI.TooltipLocation.BELOW);
        panel.addUIElement(mainTooltip).inTL(-5,0);
        return new UiPackage(panel,panelImage.two,option,button);

    }
    public static UiPackage getWeaponOption(final GPOption option){
        FactionAPI faction = Global.getSector().getPlayerFaction();
        Color base = faction.getBaseUIColor();
        Color bright = faction.getBrightUIColor();
        Color bg = faction.getDarkUIColor();
        CustomPanelAPI panel = Global.getSettings().createCustom(WIDTH_OF_OPTIONS-5,HEIGHT_OF_BUTTONS,null);
        TooltipMakerAPI mainTooltip = panel.createUIElement(WIDTH_OF_OPTIONS-5,HEIGHT_OF_BUTTONS,false);
        ButtonAPI button = mainTooltip.addAreaCheckbox("",option,base,bg,bright,WIDTH_OF_OPTIONS-5,HEIGHT_OF_BUTTONS,0f);
        button.getPosition().inTL(0,0);

        Pair<CustomPanelAPI, WeaponSpriteRenderer> panelImage = WeaponInfoGenerator.getImageOfWeapon(option.getSpec().getWeaponSpec(),30);
        LabelAPI name = mainTooltip.addPara(option.getSpec().getWeaponSpec().getWeaponName(),0f, Misc.getTooltipTitleAndLightHighlightColor());
        name.autoSizeToWidth(WIDTH_OF_NAME-35);
        float days = option.getSpec().days/GPManager.getInstance().getProductionSpeedBonus().getModifiedValue();
        if(days<=1)days=1;
        name.getPosition().inTL(35, getyPad(name));
        LabelAPI buildTime = mainTooltip.addPara(AoTDMisc.convertDaysToString((int)days),0f);
        String variantId = null;
        for (String allVariantId : Global.getSettings().getAllVariantIds()) {
            if(allVariantId.contains(option.getSpec().getIdOfItemProduced())){
                variantId=allVariantId;
                break;
            }
        }

        LabelAPI size = mainTooltip.addPara(option.getSpec().getWeaponSpec().getSize().getDisplayName(),0f);
        LabelAPI type = mainTooltip.addPara(option.getSpec().getWeaponSpec().getType().getDisplayName(),0f);
        LabelAPI designType =mainTooltip.addPara(option.getSpec().getWeaponSpec().getManufacturer(),Misc.getDesignTypeColor(option.getSpec().getWeaponSpec().getManufacturer()),0f);
        LabelAPI credits = mainTooltip.addPara(Misc.getDGSCredits(option.getSpec().getCredistCost()),0f, Color.ORANGE);
        buildTime.getPosition().inTL(getxPad(buildTime, getCenter(WIDTH_OF_NAME,WIDTH_OF_BUILD_TIME)),getyPad(size));
        size.getPosition().inTL(getxPad(size, getCenter(WIDTH_OF_NAME+WIDTH_OF_BUILD_TIME,WIDTH_OF_SIZE)),getyPad(size));
        type.getPosition().inTL(getxPad(type, getCenter(WIDTH_OF_NAME+WIDTH_OF_BUILD_TIME+WIDTH_OF_SIZE,WIDTH_OF_TYPE)),getyPad(type));
        designType.getPosition().inTL(getxPad(designType, getCenter(WIDTH_OF_NAME+WIDTH_OF_BUILD_TIME+WIDTH_OF_SIZE+WIDTH_OF_TYPE,WIDTH_OF_DESIGN_TYPE)),getyPad(designType));
        credits.getPosition().inTL(getxPad(credits, getCenter(WIDTH_OF_NAME+WIDTH_OF_BUILD_TIME+WIDTH_OF_TYPE+WIDTH_OF_SIZE+WIDTH_OF_DESIGN_TYPE,WIDTH_OF_CREDIT_COST)),getyPad(credits));
        CustomPanelAPI panelImg = getGPCostPanel(WIDTH_OF_GP,HEIGHT_OF_BUTTONS,option.getSpec());
        mainTooltip.addCustomDoNotSetPosition(panelImg).getPosition().setLocation(0,0).inTL(WIDTH_OF_NAME+WIDTH_OF_BUILD_TIME+WIDTH_OF_TYPE+WIDTH_OF_SIZE+WIDTH_OF_DESIGN_TYPE+WIDTH_OF_CREDIT_COST,0);
        mainTooltip.addCustom(panelImage.one,5f).getPosition().inTL(0,8);
        mainTooltip.addTooltipToPrevious(new TooltipMakerAPI.TooltipCreator() {
            @Override
            public boolean isTooltipExpandable(Object tooltipParam) {
                return false;
            }

            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return 400;
            }

            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                WeaponInfoGenerator.generate(tooltip,option.getSpec().getWeaponSpec(),getTooltipWidth(tooltipParam));
            }
        }, TooltipMakerAPI.TooltipLocation.BELOW);
        panel.addUIElement(mainTooltip).inTL(-5,0);
        return new UiPackage(panel,panelImage.two,option,button);

    }
    public static UiPackage getWingOption(final GPOption option){
        FactionAPI faction = Global.getSector().getPlayerFaction();
        Color base = faction.getBaseUIColor();
        Color bright = faction.getBrightUIColor();
        Color bg = faction.getDarkUIColor();
        CustomPanelAPI panel = Global.getSettings().createCustom(WIDTH_OF_OPTIONS-5,HEIGHT_OF_BUTTONS,null);
        TooltipMakerAPI mainTooltip = panel.createUIElement(WIDTH_OF_OPTIONS-5,HEIGHT_OF_BUTTONS,false);
        ButtonAPI button = mainTooltip.addAreaCheckbox("",option,base,bg,bright,WIDTH_OF_OPTIONS-5,HEIGHT_OF_BUTTONS,0f);
        button.getPosition().inTL(0,0);

        Pair<CustomPanelAPI, FighterIconRenderer> panelImage = FighterInfoGenerator.createFormationPanel(option.getSpec().getWingSpecAPI(), FormationType.BOX,30,option.getSpec().getWingSpecAPI().getNumFighters());
        LabelAPI name = mainTooltip.addPara(option.getSpec().getWingSpecAPI().getWingName(),0f, Misc.getTooltipTitleAndLightHighlightColor());
        name.autoSizeToWidth(WIDTH_OF_NAME-35);
        name.getPosition().inTL(45, getyPad(name));
        float days = option.getSpec().days/GPManager.getInstance().getProductionSpeedBonus().getModifiedValue();
        if(days<=1)days=1;

        LabelAPI buildTime = mainTooltip.addPara(AoTDMisc.convertDaysToString((int)days),0f);

        LabelAPI type = mainTooltip.addPara(option.getSpec().getWingSpecAPI().getRoleDesc(),0f);
        LabelAPI designType =mainTooltip.addPara(option.getSpec().getWingSpecAPI().getVariant().getHullSpec().getManufacturer(), Misc.getDesignTypeColor(option.getSpec().getWingSpecAPI().getVariant().getHullSpec().getManufacturer()),0f);
        LabelAPI credits = mainTooltip.addPara(Misc.getDGSCredits(option.getSpec().getCredistCost()),0f, Color.ORANGE);
        buildTime.getPosition().inTL(getxPad(buildTime, getCenter(WIDTH_OF_NAME,WIDTH_OF_BUILD_TIME)),getyPad(type));
        type.getPosition().inTL(getxPad(type, getCenter(WIDTH_OF_NAME+WIDTH_OF_BUILD_TIME,WIDTH_OF_TYPE+WIDTH_OF_SIZE)),getyPad(type));
        designType.getPosition().inTL(getxPad(designType, getCenter(WIDTH_OF_NAME+WIDTH_OF_BUILD_TIME+WIDTH_OF_SIZE+WIDTH_OF_TYPE,WIDTH_OF_DESIGN_TYPE)),getyPad(designType));
        credits.getPosition().inTL(getxPad(credits, getCenter(WIDTH_OF_NAME+WIDTH_OF_BUILD_TIME+WIDTH_OF_TYPE+WIDTH_OF_SIZE+WIDTH_OF_DESIGN_TYPE,WIDTH_OF_CREDIT_COST)),getyPad(credits));
        CustomPanelAPI panelImg = getGPCostPanel(WIDTH_OF_GP,HEIGHT_OF_BUTTONS,option.getSpec());
        mainTooltip.addCustomDoNotSetPosition(panelImg).getPosition().setLocation(0,0).inTL(WIDTH_OF_NAME+WIDTH_OF_BUILD_TIME+WIDTH_OF_TYPE+WIDTH_OF_SIZE+WIDTH_OF_DESIGN_TYPE+WIDTH_OF_CREDIT_COST,0);
        mainTooltip.addCustom(panelImage.one,5f).getPosition().inTL(5,4);
        mainTooltip.addTooltipToPrevious(new TooltipMakerAPI.TooltipCreator() {
            @Override
            public boolean isTooltipExpandable(Object tooltipParam) {
                return false;
            }

            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return 400;
            }

            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                FighterInfoGenerator.generate(tooltip,option.getSpec().getWingSpecAPI(), getTooltipWidth(tooltipParam));
            }
        }, TooltipMakerAPI.TooltipLocation.BELOW);
        panel.addUIElement(mainTooltip).inTL(-5,0);
        return new UiPackage(panel,panelImage.two,option,button);

    }
    public static Pair<CustomPanelAPI,ButtonAPI>getOrderPanel(GPOrder order){

        CustomPanelAPI panel = Global.getSettings().createCustom(WIDTH_OF_ORDERS_PANELS,HEIGHT_OF_BUTTONS,null);
        TooltipMakerAPI tooltip = panel.createUIElement(WIDTH_OF_ORDERS_PANELS,HEIGHT_OF_BUTTONS,false);
        CustomPanelAPI imagePanel = null;
        LabelAPI name = null;
        LabelAPI qty;
        LabelAPI creditCost;
        LabelAPI days;
        ButtonAPI button = tooltip.addAreaCheckbox("",order, NidavelirMainPanelPlugin.base,NidavelirMainPanelPlugin.bg,NidavelirMainPanelPlugin.bright,panel.getPosition().getWidth(),40,0f);
        button.getPosition().inTL(0,0);
        if(order.getSpecFromClass().getType()== GPSpec.ProductionType.SHIP){
            imagePanel = ShipInfoGenerator.getShipImage(order.getSpecFromClass().getShipHullSpecAPI(),30,null).one;
            name = tooltip.addPara(order.getSpecFromClass().getShipHullSpecAPI().getHullName(),0f);
            tooltip.addCustom(imagePanel,5f).getPosition().setLocation(0,0).inTL(2,4);
        }
        if(order.getSpecFromClass().getType()== GPSpec.ProductionType.WEAPON){
            imagePanel = WeaponInfoGenerator.getImageOfWeapon(order.getSpecFromClass().getWeaponSpec(),30).one;
            name = tooltip.addPara(order.getSpecFromClass().getWeaponSpec().getWeaponName(),0f);
            tooltip.addCustom(imagePanel,5f).getPosition().setLocation(0,0).inTL(2,8);
        }
        if(order.getSpecFromClass().getType()== GPSpec.ProductionType.FIGHTER){
            name = tooltip.addPara(order.getSpecFromClass().getWingSpecAPI().getWingName(),0f);
            imagePanel = FighterInfoGenerator.createFormationPanel(order.getSpecFromClass().getWingSpecAPI(), FormationType.BOX,24,order.getSpecFromClass().getWingSpecAPI().getNumFighters()).one;
            tooltip.addCustom(imagePanel,5f).getPosition().setLocation(0,0).inTL(3,6);
        }

        final GPSpec spec = order.getSpecFromClass();
        tooltip.addTooltipToPrevious(new TooltipMakerAPI.TooltipCreator() {
            @Override
            public boolean isTooltipExpandable(Object tooltipParam) {
                return true;
            }

            @Override
            public float getTooltipWidth(Object tooltipParam) {
              if(spec.getType().equals(GPSpec.ProductionType.SHIP)){
                  return 950f;
              }
              else{
                  return 400f;
              }
            }

            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                if(spec.getType().equals(GPSpec.ProductionType.SHIP)){
                    String variantId = null;
                    for (String allVariantId : Global.getSettings().getAllVariantIds()) {
                        if(allVariantId.contains(spec.getIdOfItemProduced())){
                            variantId=allVariantId;
                            break;
                        }
                    }
                    final CustomPanelAPI panelAPIs = ShipInfoGenerator.getShipImage(spec.getShipHullSpecAPI(),250,null).one;
                    CampaignFleetAPI fleet = Global.getFactory().createEmptyFleet(Global.getSector().getPlayerFaction().getId(),"test",false);

                    FleetMemberAPI memberAPI = Global.getFactory().createFleetMember(FleetMemberType.SHIP,Global.getSettings().createEmptyVariant(variantId,spec.getShipHullSpecAPI()));
                    fleet.getCargo().addCrew((int) memberAPI.getMinCrew());
                    fleet.getCargo().addSupplies(memberAPI.getCargoCapacity()-10);
                    fleet.getCargo().addFuel(memberAPI.getFuelCapacity());
                    fleet.getFleetData().addFleetMember(memberAPI);
                    memberAPI.getRepairTracker().setCR(70);
                    memberAPI.getRepairTracker().computeRepairednessFraction();
                    ShipInfoGenerator.generate(tooltip,memberAPI,null,panelAPIs,getTooltipWidth(tooltipParam));
                    fleet.deflate();
                }
                if(spec.getType()== GPSpec.ProductionType.WEAPON){
                    WeaponInfoGenerator.generate(tooltip,spec.getWeaponSpec(),getTooltipWidth(tooltipParam));
                }
                if(spec.getType()== GPSpec.ProductionType.FIGHTER){
                    FighterInfoGenerator.generate(tooltip,spec.getWingSpecAPI(),getTooltipWidth(tooltipParam));

                }
            }
        }, TooltipMakerAPI.TooltipLocation.BELOW,false);

        qty = tooltip.addPara(""+order.getAmountToProduce(), Misc.getTooltipTitleAndLightHighlightColor(),0f);
        creditCost = tooltip.addPara(Misc.getDGSCredits(order.getSpecFromClass().getCredistCost()),Color.ORANGE,0f);
        int daysINt = (int) order.getDaysTillOrderFinished();
        if(daysINt<=1)daysINt=1;
        days = tooltip.addPara(""+daysINt,0f);
        name.computeTextHeight(name.getText());
       PositionAPI pos =  name.autoSizeToWidth(WIDTH_OF_NAMES_ORDER-35);
        name.computeTextHeight(name.getText());
        name.getPosition().inTL(35, HEIGHT_OF_BUTTONS/2-pos.getHeight()/2);
        float beingX = WIDTH_OF_NAMES_ORDER;

        qty.getPosition().inTL(getxPad(qty,getCenter(beingX,WIDTH_OF_NAMES_QTY)),getyPad(qty));
        beingX+=WIDTH_OF_NAMES_QTY;
        days.getPosition().inTL(getxPad(days,getCenter(beingX,WIDTH_OF_NAMES_DAYS)),getyPad(days));
        beingX+=WIDTH_OF_NAMES_DAYS;
        creditCost.getPosition().inTL(getxPad(creditCost,getCenter(beingX,WIDTH_OF_NAMES_COST)),getyPad(creditCost));
        beingX+=WIDTH_OF_NAMES_COST;
        CustomPanelAPI panelImg = getGPCostPanel(WIDTH_OF_NAMES_GP,HEIGHT_OF_BUTTONS,order.getSpecFromClass());
        tooltip.addCustom(panelImg,5f).getPosition().setLocation(0,0).inTL(beingX,0);
        panel.addUIElement(tooltip).inTL(-5,0);
        return new Pair<>(panel,button);

    }
    private static float getCenter(float beginX, float width) {;
        float endX = beginX+width;
        float widthOfSection = endX-beginX;
        float center = beginX+widthOfSection/2;
        return center;
    }

    private static float getxPad(LabelAPI buildTime, float center) {
        return center - (buildTime.computeTextWidth(buildTime.getText()) / 2);
    }

    private static float getyPad(LabelAPI name) {
        return (HEIGHT_OF_BUTTONS / 2) - (name.computeTextHeight(name.getText()) / 2);
    }
    public static ArrayList<RowData> calculateAmountOfRows(float widthOfRow, LinkedHashMap<String,Integer>designs, float xPadding){
        ArrayList<RowData>data = new ArrayList<>();
        float currentX = 0;
        float rows = 0;
        RowData daten = new RowData(rows,new LinkedHashMap<String, Integer>());
        LabelAPI dummy = Global.getSettings().createLabel("", Fonts.DEFAULT_SMALL);
        for (Map.Entry<String,Integer> entry:designs.entrySet()) {
            if(entry.getValue()==0)continue;
            String txt = entry.getKey()+"("+entry.getValue()+")";
            float widthOfButton = dummy.computeTextWidth(txt)+30;
            currentX+=widthOfButton;
            if(currentX>widthOfRow){
                currentX=widthOfButton;
                rows++;
                data.add(daten);
                daten = new RowData(rows,new LinkedHashMap<String, Integer>());
            }
            daten.stringsInRow.put(txt, (int) widthOfButton);
            currentX+=xPadding;
        }
        data.add(daten);
        return data;
    }
    public static CustomPanelAPI getGPCostPanel(float totalWidth,float height, GPSpec option){
        CustomPanelAPI panel = Global.getSettings().createCustom(totalWidth,height,null);
        TooltipMakerAPI tooltip = panel.createUIElement(totalWidth,height,false);
        float size = option.getSupplyCost().size();
        float iconSize = height/2;
        float padding = 2f;
        float totalSize = 0;
        LabelAPI test = Global.getSettings().createLabel("",Fonts.DEFAULT_SMALL);
        for (Map.Entry<String, Integer> entry : option.getSupplyCost().entrySet()) {
            totalSize+=iconSize+test.computeTextWidth(entry.getValue()+"");

        }
        totalSize+=padding*(size-1);
        float beginX = (totalWidth-totalSize)/2;
        for (Map.Entry<String, Integer> entry : option.getSupplyCost().entrySet()) {
            tooltip.addImage(Global.getSettings().getCommoditySpec(entry.getKey()).getIconName(),iconSize,iconSize,0f);
            tooltip.getPrev().getPosition().inTL(beginX,iconSize/2);
            LabelAPI label = tooltip.addPara("x"+entry.getValue(),Color.ORANGE,0f);
            label.getPosition().inTL(beginX+iconSize,(height/2)-(label.computeTextHeight(label.getText())/2));
            beginX+=iconSize+label.computeTextWidth(label.getText())+padding;
        }
        panel.addUIElement(tooltip).inTL(-5,0);
        return panel;


    }
    public static CustomPanelAPI getGPCostPanelSpecialProjStage(float totalWidth,float height, GPSpec option,int stage){
        CustomPanelAPI panel = Global.getSettings().createCustom(totalWidth,height,null);
        TooltipMakerAPI tooltip = panel.createUIElement(totalWidth,height,false);
        float iconSize = height/2;
        float padding = 2f;
        float beginX = 0f;
        for (Map.Entry<String, Integer> entry : option.getStageSupplyCost().get(stage).entrySet()) {
            tooltip.addImage(Global.getSettings().getCommoditySpec(entry.getKey()).getIconName(),iconSize,iconSize,0f);
            tooltip.getPrev().getPosition().inTL(beginX,iconSize/2);
            LabelAPI label = tooltip.addPara("x"+entry.getValue(),Color.ORANGE,0f);
            label.getPosition().inTL(beginX+iconSize,(height/2)-(label.computeTextHeight(label.getText())/2));
            beginX+=iconSize+label.computeTextWidth(label.getText())+padding;
        }
        panel.addUIElement(tooltip).inTL(-5,0);
        return panel;


    }
}
