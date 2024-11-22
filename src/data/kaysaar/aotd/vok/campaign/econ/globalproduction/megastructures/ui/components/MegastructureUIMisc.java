package data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.NidavelirMainPanelPlugin;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.UILinesRenderer;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.onhover.CommodityInfo;
import data.kaysaar.aotd.vok.misc.ui.ImagePanel;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager.commodities;

public class MegastructureUIMisc {
    public static CustomPanelAPI createTitleMenu(CustomPanelAPI mainPanel, GPBaseMegastructure mega){
        float widthForUsage  = mainPanel.getPosition().getWidth()-20;
        float sectionWidth = widthForUsage/6;
        float height = 0f;
        float imageSection = sectionWidth*2;
        float mapSection = sectionWidth;
        float descriptionSection = sectionWidth*3;
        if(mega.getEntityTiedTo()==null){
            mapSection = 0;
            descriptionSection = sectionWidth*4;
        }
        CustomPanelAPI titlePanel;
        UILinesRenderer renderer = new UILinesRenderer(0f);
        titlePanel = mainPanel.createCustomPanel(mainPanel.getPosition().getWidth(),40,renderer);
        renderer.setPanel(titlePanel);
        TooltipMakerAPI imageTootlip,mapTooltip,descriptionTooltip;

        imageTootlip = titlePanel.createUIElement(sectionWidth*2,100,false);
        imageTootlip.addImage(Global.getSettings().getSpriteName("megastructureImage",mega.getSpec().getImageForMegastructure()),sectionWidth*2,0f);
        height = imageTootlip.getPrev().getPosition().getHeight();
        imageTootlip.getPosition().setSize(sectionWidth*2,height);
        titlePanel.getPosition().setSize(titlePanel.getPosition().getWidth(),height);
        if(mapSection!=0){
            mapTooltip = titlePanel.createUIElement(sectionWidth,height,false);
            mapTooltip.addSectorMap(sectionWidth,height-20,mega.getEntityTiedTo().getStarSystem(), 0f);
            titlePanel.addUIElement(mapTooltip).inTL(imageSection+descriptionSection+10,0);
        }

        descriptionTooltip = titlePanel.createUIElement(descriptionSection,height,true);
        descriptionTooltip.addPara(mega.getSpec().getDescription(),5f);

        titlePanel.addUIElement(imageTootlip).inTL(0,0);
        titlePanel.addUIElement(descriptionTooltip).inTL(imageSection+5,0);


        return titlePanel;
    }
    public static CustomPanelAPI createTitleSection(CustomPanelAPI mainPanel, GPBaseMegastructure mega,String title){
        CustomPanelAPI mainTitlePanel;
        mainTitlePanel = mainPanel.createCustomPanel(mainPanel.getPosition().getWidth(),20,null);
        TooltipMakerAPI tooltip = mainTitlePanel.createUIElement(mainTitlePanel.getPosition().getWidth(),20,false);
        tooltip.addSectionHeading(title, Alignment.MID,0f);
        mainTitlePanel.addUIElement(tooltip).inTL(0,0);
 return mainTitlePanel;

    }
    public static ButtonPackage createWidgetForSection(CustomPanelAPI mainPanel, GPMegaStructureSection section,float offsetOpt,float offsetOther){
        CustomPanelAPI sectionsPanel;
        float imageWidth,imageHeight;
        imageWidth = 250;
        imageHeight= 150;

        UILinesRenderer renderer = new UILinesRenderer(0f);
        sectionsPanel = Global.getSettings().createCustom(mainPanel.getPosition().getWidth(),imageHeight,renderer);
        renderer.setPanel(sectionsPanel);
        TooltipMakerAPI tooltipOfImage = sectionsPanel.createUIElement(imageWidth,imageHeight,false);
        LabelAPI label = tooltipOfImage.addTitle(section.getName());
        ImagePanel panelPl = new ImagePanel();
        CustomPanelAPI panelImage = sectionsPanel.createCustomPanel(imageWidth,imageHeight-20,panelPl);
        panelPl.init(panelImage,Global.getSettings().getSprite("sectionImage",section.getSpec().getIcon()));
        tooltipOfImage.addCustom(panelImage,10f);
        tooltipOfImage.getPrev().getPosition().inTL(label.getPosition().getX()-5,label.getPosition().getY()+33);
        tooltipOfImage.setTitleFont(Fonts.INSIGNIA_LARGE);
        sectionsPanel.addUIElement(tooltipOfImage).inTL(0,0);
        CustomPanelAPI panel = sectionsPanel.createCustomPanel(sectionsPanel.getPosition().getWidth()-imageWidth,imageHeight,null);
        renderer.setPanel(panel);
        TooltipMakerAPI optionTooltip,descriptionTooltip,otherInfoTooltip;
        optionTooltip = panel.createUIElement(panel.getPosition().getWidth()*0.4f-15f,imageHeight-25,true);
        otherInfoTooltip = panel.createUIElement(panel.getPosition().getWidth()*0.6f-15f,imageHeight-25,true);
//        section.createProdUpkeepInfo(otherInfoTooltip,panel.getPosition().getWidth()*0.6f);

        float pad =0f;
//        for (int i = 0; i < 3; i++) {

//        }
        ArrayList<ButtonAPI>buttons = new ArrayList<>();
        //Quick fix : awful but works
        for (Map.Entry<String, ButtonData> entry : section.generateButtons().entrySet()) {
            if(entry.getKey().equals("moreInfo")){
                TooltipMakerAPI testTooltip= otherInfoTooltip.beginSubTooltip(panel.getPosition().getWidth()*0.6f-30f);
                LabelAPI label1 = testTooltip.addPara(entry.getValue().textButton,0f);
                TooltipMakerAPI tooltipInsert = otherInfoTooltip.beginSubTooltip(panel.getPosition().getWidth()*0.6f+30f);

                float height = label1.computeTextHeight(label1.getText());
                float width = label1.computeTextWidth(label1.getText());
                float totalWidth = panel.getPosition().getWidth()*0.6f-30f;
                float neededPadding = (totalWidth-width)/2;
                ButtonAPI button = tooltipInsert.addAreaCheckbox("",entry.getValue(), NidavelirMainPanelPlugin.base,NidavelirMainPanelPlugin.bg,NidavelirMainPanelPlugin.bright,panel.getPosition().getWidth()*0.6f-30f,height+20,0);
                if(entry.getValue().getCreator()!=null){
                    tooltipInsert.addTooltipToPrevious(entry.getValue().creator, TooltipMakerAPI.TooltipLocation.BELOW,false);
                }
                button.setEnabled(entry.getValue().isButtonEnabled());
                tooltipInsert.addPara(label1.getText(),entry.getValue().getTextColor(),0f).getPosition().inTL(button.getPosition().getX()+neededPadding,-button.getPosition().getY()-button.getPosition().getHeight()+10);
                otherInfoTooltip.addCustom(tooltipInsert,0f);
                otherInfoTooltip.addSpacer(height*2+10);
                buttons.add(button);
            }
            else{
                TooltipMakerAPI testTooltip= optionTooltip.beginSubTooltip(panel.getPosition().getWidth()*0.4f-30f);
                LabelAPI label1 = testTooltip.addPara(entry.getValue().textButton,0f);
                TooltipMakerAPI tooltipInsert = optionTooltip.beginSubTooltip(panel.getPosition().getWidth()*0.4f+30f);

                float height = label1.computeTextHeight(label1.getText());
                float width = label1.computeTextWidth(label1.getText());
                float totalWidth = panel.getPosition().getWidth()*0.4f-30f;
                float neededPadding = (totalWidth-width)/2;
                ButtonAPI button = tooltipInsert.addAreaCheckbox("",entry.getValue(), NidavelirMainPanelPlugin.base,NidavelirMainPanelPlugin.bg,NidavelirMainPanelPlugin.bright,panel.getPosition().getWidth()*0.4f-30f,height+20,pad);
                if(entry.getValue().getCreator()!=null){
                    tooltipInsert.addTooltipToPrevious(entry.getValue().creator, TooltipMakerAPI.TooltipLocation.BELOW,false);
                }
                button.setEnabled(entry.getValue().isButtonEnabled());
                tooltipInsert.addPara(label1.getText(),entry.getValue().getTextColor(),0f).getPosition().inTL(button.getPosition().getX()+neededPadding,-button.getPosition().getY()-button.getPosition().getHeight()+10);
                optionTooltip.addCustom(tooltipInsert,0f);
                optionTooltip.addSpacer(height*2+10);
                buttons.add(button);

                pad= 5f;
            }

        }




        panel.addUIElement(otherInfoTooltip).inTL(panel.getPosition().getWidth()*0.4f,5);
        panel.addUIElement(optionTooltip).inTL(0,5);
        sectionsPanel.addComponent(panel).inTL(imageWidth,0);
        ButtonPackage buttonPackage = new ButtonPackage();
        buttonPackage.setTooltipOptions(optionTooltip);
        buttonPackage.setTooltipOther(otherInfoTooltip);
        buttonPackage.setButtonsPlaced(buttons);
        buttonPackage.setSection(section);
        buttonPackage.setPanelOfButtons(sectionsPanel);
        if(offsetOpt>=optionTooltip.getHeightSoFar()){
            offsetOpt = optionTooltip.getHeightSoFar()-10;
        }
        if(offsetOther>=otherInfoTooltip.getHeightSoFar()){
            offsetOther = otherInfoTooltip.getHeightSoFar()-10f;
        }
        if(optionTooltip.getExternalScroller()!=null){
            optionTooltip.getExternalScroller().setYOffset(offsetOpt);
        }
        if(otherInfoTooltip.getExternalScroller()!=null){
            otherInfoTooltip.getExternalScroller().setYOffset(offsetOther);
        }
        return buttonPackage;
    }

    public static void drawRectangleFilledForTooltip(TooltipMakerAPI tooltipMakerAPI, float alphaMult, Color uiColor) {
        if (uiColor == null) return;

        float x = tooltipMakerAPI.getPosition().getX();
        float y = tooltipMakerAPI.getPosition().getY();
        float w = tooltipMakerAPI.getPosition().getWidth();
        float h = tooltipMakerAPI.getPosition().getHeight();

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(uiColor.getRed() / 255f, uiColor.getGreen() / 255f, uiColor.getBlue() / 255f,
                uiColor.getAlpha() / 255f * alphaMult* 23f);
        GL11.glRectf(x, y, x + w, y + h);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glPopMatrix();
    }
    public static CustomPanelAPI createResourcePanel(float width, float height, float iconSize, HashMap<String,Integer> costs,Color overrideColor) {
        CustomPanelAPI customPanel = Global.getSettings().createCustom(width, height, null);
        TooltipMakerAPI tooltip = customPanel.createUIElement(width, height, false);
        float totalSize = width;
        float sections = totalSize /commodities.size();
        float positions = totalSize / (commodities.size() * 4);
        float iconsize = iconSize;
        float topYImage = 0;
        LabelAPI test = Global.getSettings().createLabel("", Fonts.DEFAULT_SMALL);
        float x = positions;
        for (String commodity : GPManager.getCommodities()) {
            if(costs.get(commodity)!=null){
                tooltip.addImage(Global.getSettings().getCommoditySpec(commodity).getIconName(), iconsize, iconsize, 0f);
                UIComponentAPI image = tooltip.getPrev();
                image.getPosition().inTL(x, topYImage);
                String text = "" + costs.get(commodity);
                String text2 = text;
                Color col = Misc.getPositiveHighlightColor();
                if (costs.get(commodity) > GPManager.getInstance().getTotalResources().get(commodity)){
                    col = Misc.getNegativeHighlightColor();
                }
                if(overrideColor!=null){
                    col = overrideColor;
                }
                tooltip.addPara("%s", 0f, col, col, text).getPosition().inTL(x + iconsize + 5, (topYImage + (iconsize / 2)) - (test.computeTextHeight(text2) / 3));
                x += sections;
            }
            else{
                tooltip.addImage(Global.getSettings().getCommoditySpec(commodity).getIconName(), iconsize, iconsize, 0f);
                UIComponentAPI image = tooltip.getPrev();
                image.getPosition().inTL(x, topYImage);
                String text = "" + 0;
                String text2 = text;
                Color col = Color.ORANGE;
                tooltip.addPara("%s", 0f, col, col, text).getPosition().inTL(x + iconsize + 5, (topYImage + (iconsize / 2)) - (test.computeTextHeight(text2) / 3));
                x += sections;
            }

        }
        customPanel.addUIElement(tooltip).inTL(0, 0);
        return customPanel;
    }
    public static CustomPanelAPI createResourcePanelForSmallTooltip(float width, float height, float iconSize, HashMap<String,Integer> costs,Color overrideColor) {
        CustomPanelAPI customPanel = Global.getSettings().createCustom(width, height, null);
        TooltipMakerAPI tooltip = customPanel.createUIElement(width, height, false);
        float totalSize = width;
        float sections = totalSize /commodities.size();
        float positions = totalSize / (commodities.size() * 4);
        float iconsize = iconSize;
        float topYImage = 0;
        LabelAPI test = Global.getSettings().createLabel("", Fonts.DEFAULT_SMALL);
        float x = positions;
        ArrayList<CustomPanelAPI> panelsWithImage = new ArrayList<>();
        for (String commodity : GPManager.getCommodities()) {
            float widthTempPanel = iconsize;
            int number = 0;
            if(costs.get(commodity)!=null){
                number = costs.get(commodity);
            }
            widthTempPanel+=test.computeTextWidth(""+number+5);
            CustomPanelAPI panelTemp = Global.getSettings().createCustom(widthTempPanel,iconSize,null);
            TooltipMakerAPI tooltipMakerAPI = panelTemp.createUIElement(widthTempPanel,iconSize,false);
            tooltipMakerAPI.addImage(Global.getSettings().getCommoditySpec(commodity).getIconName(), iconsize, iconsize, 0f);
            UIComponentAPI image = tooltipMakerAPI.getPrev();
            image.getPosition().inTL(x, topYImage);
            String text = "" +number;
            String text2 = text;
            Color col = Misc.getPositiveHighlightColor();
            if (number > GPManager.getInstance().getTotalResources().get(commodity)){
                col = Misc.getNegativeHighlightColor();
            }
            if(overrideColor!=null){
                col = overrideColor;
            }
            tooltipMakerAPI.addPara("%s", 0f, Misc.getTooltipTitleAndLightHighlightColor(), col, text).getPosition().inTL(x + iconsize + 5, (topYImage + (iconsize / 2)) - (test.computeTextHeight(text2) / 3));
            panelTemp.addUIElement(tooltipMakerAPI).inTL(0, 0);
            panelsWithImage.add(panelTemp);
        }
        float lastX = 0f;
        float lastY = 0f;
        float totalWidth =0f;
        float secondRowWidth = 0f;
        float left;
        for (CustomPanelAPI panelAPI : panelsWithImage) {
            totalWidth+=panelAPI.getPosition().getWidth()+15;
        }
        left = totalWidth;
        ArrayList<CustomPanelAPI> panelsSecondRow = new ArrayList<>();
        if(totalWidth>=width){
            for (int i = panelsWithImage.size()-1; i >=0 ; i--) {
                left-=panelsWithImage.get(i).getPosition().getWidth()-15;
                panelsSecondRow.add(panelsWithImage.get(i));
                if(left<width){
                    break;
                }
                panelsWithImage.remove(i);
            }
        }
        for (CustomPanelAPI panelAPI : panelsSecondRow) {
            secondRowWidth+=panelAPI.getPosition().getWidth()+15;
        }
        float startingXFirstRow =  (width-totalWidth)/2;
        float startingXSecondRow =  (width-secondRowWidth)/2;
        if(!panelsSecondRow.isEmpty()){
            tooltip.getPosition().setSize(width,height*2+5);
            customPanel.getPosition().setSize(width,height*2+5);
        }
        for (CustomPanelAPI panelAPI : panelsWithImage) {
            tooltip.addCustom(panelAPI,0f).getPosition().inTL(startingXFirstRow,0);
            startingXFirstRow+=panelAPI.getPosition().getWidth()+5;
        }
        for (CustomPanelAPI panelAPI : panelsSecondRow) {
            tooltip.addCustom(panelAPI,0f).getPosition().inTL(startingXSecondRow,iconSize+5);
            startingXSecondRow+=panelAPI.getPosition().getWidth()+5;
        }

        customPanel.addUIElement(tooltip).inTL(0, 0);
        return customPanel;
    }
    public static CustomPanelAPI createResourcePanelForSmallTooltipCondensed(float width, float height, float iconSize, HashMap<String,Integer> costs,HashMap<String,Integer>production) {
        CustomPanelAPI customPanel = Global.getSettings().createCustom(width, height, null);
        TooltipMakerAPI tooltip = customPanel.createUIElement(width, height, false);
        float totalSize = width;
        float sections = totalSize /commodities.size();
        float positions = totalSize / (commodities.size() * 4);
        float iconsize = iconSize;
        float topYImage = 0;
        LabelAPI test = Global.getSettings().createLabel("", Fonts.DEFAULT_SMALL);
        float x = positions;
        ArrayList<CustomPanelAPI> panelsWithImage = new ArrayList<>();
        for (String commodity : GPManager.getCommodities()) {
            float widthTempPanel = iconsize;
            int number = 0;
            int cost = 0;
            int prod =0;

            if(costs.get(commodity)!=null){
                cost = costs.get(commodity);
            }
            if(production.get(commodity)!=null){
                prod = production.get(commodity);
            }
            number = prod - cost;
            String text = "" +number;
            Color col = Misc.getPositiveHighlightColor();
            if(number==0){
                col = Misc.getTooltipTitleAndLightHighlightColor();
            }
            if(number<0){
                col = Misc.getNegativeHighlightColor();
            }
            if(number>0){
                text = "+"+number;
            }
            widthTempPanel+=test.computeTextWidth(text)+5;
            CustomPanelAPI panelTemp = Global.getSettings().createCustom(widthTempPanel,iconSize,null);
            TooltipMakerAPI tooltipMakerAPI = panelTemp.createUIElement(widthTempPanel,iconSize,false);
            tooltipMakerAPI.addImage(Global.getSettings().getCommoditySpec(commodity).getIconName(), iconsize, iconsize, 0f);
            UIComponentAPI image = tooltipMakerAPI.getPrev();
            image.getPosition().inTL(x, topYImage);

            tooltipMakerAPI.addPara("%s", 0f, col, col, text).getPosition().inTL(x + iconsize + 2, (topYImage + (iconsize / 2)) - (test.computeTextHeight(text) / 3));
            panelTemp.addUIElement(tooltipMakerAPI).inTL(0, 0);
            panelsWithImage.add(panelTemp);
        }
        float lastX = 0f;
        float lastY = 0f;
        float totalWidth =0f;
        float secondRowWidth = 0f;
        float left;
        for (CustomPanelAPI panelAPI : panelsWithImage) {
            totalWidth+=panelAPI.getPosition().getWidth()+15;
        }
        left = totalWidth;
        ArrayList<CustomPanelAPI> panelsSecondRow = new ArrayList<>();
        if(totalWidth>=width){
            for (int i = panelsWithImage.size()-1; i >=0 ; i--) {
                left-=panelsWithImage.get(i).getPosition().getWidth()-15;
                panelsSecondRow.add(panelsWithImage.get(i));
                if(left<width){
                    break;
                }
                panelsWithImage.remove(i);
            }
        }
        for (CustomPanelAPI panelAPI : panelsSecondRow) {
            secondRowWidth+=panelAPI.getPosition().getWidth()+15;
        }
        float startingXFirstRow =  0;
        float startingXSecondRow =  0;
        if(!panelsSecondRow.isEmpty()){
            tooltip.getPosition().setSize(width,height*2+5);
            customPanel.getPosition().setSize(width,height*2+5);
        }
        for (CustomPanelAPI panelAPI : panelsWithImage) {
            tooltip.addCustom(panelAPI,0f).getPosition().inTL(startingXFirstRow,0);
            startingXFirstRow+=panelAPI.getPosition().getWidth()+5;
        }
        for (CustomPanelAPI panelAPI : panelsSecondRow) {
            tooltip.addCustom(panelAPI,0f).getPosition().inTL(startingXSecondRow,iconSize+5);
            startingXSecondRow+=panelAPI.getPosition().getWidth()+5;
        }

        customPanel.addUIElement(tooltip).inTL(-15, 0);
        return customPanel;
    }
    public static CustomPanelAPI createResourcePanelForSmallTooltipNotCondensed(float width, float height, float iconSize, HashMap<String,Integer> costs,HashMap<String,Integer>production) {
        CustomPanelAPI customPanel = Global.getSettings().createCustom(width, height, null);
        TooltipMakerAPI tooltip = customPanel.createUIElement(width, height, false);
        float totalSize = width;
        float sections = totalSize /commodities.size();
        float positions = totalSize / (commodities.size() * 4);
        float iconsize = iconSize;
        float topYImage = 0;
        LabelAPI test = Global.getSettings().createLabel("", Fonts.DEFAULT_SMALL);
        float x = positions;
        ArrayList<CustomPanelAPI> panelsWithImage = new ArrayList<>();
        for (String commodity : GPManager.getCommodities()) {
            float widthTempPanel = iconsize;
            int number = 0;
            int cost = 0;
            int prod =0;
            if(costs.get(commodity)!=null){
                cost = costs.get(commodity);
            }
            if(production.get(commodity)!=null){
                prod = production.get(commodity);
            }
            number = prod - cost;
            String text = "" +number;
            Color col = Misc.getPositiveHighlightColor();
            if(number==0){
                col = Misc.getTooltipTitleAndLightHighlightColor();
            }
            if(number<0){
                col = Misc.getNegativeHighlightColor();
            }
            if(number>0){
                text = "+"+number;
            }
            widthTempPanel+=test.computeTextWidth(text)+5;
            CustomPanelAPI panelTemp = Global.getSettings().createCustom(widthTempPanel,iconSize,null);
            TooltipMakerAPI tooltipMakerAPI = panelTemp.createUIElement(widthTempPanel,iconSize,false);
            tooltipMakerAPI.addImage(Global.getSettings().getCommoditySpec(commodity).getIconName(), iconsize, iconsize, 0f);
            UIComponentAPI image = tooltipMakerAPI.getPrev();
            image.getPosition().inTL(x, topYImage);

            tooltipMakerAPI.addPara("%s", 0f, col, col, text).getPosition().inTL(x + iconsize + 2, (topYImage + (iconsize / 2)) - (test.computeTextHeight(text) / 3));
            panelTemp.addUIElement(tooltipMakerAPI).inTL(0, 0);
            panelsWithImage.add(panelTemp);
        }
        float lastX = 0f;
        float lastY = 0f;
        float totalWidth =0f;
        float secondRowWidth = 0f;
        float left;
        for (CustomPanelAPI panelAPI : panelsWithImage) {
            totalWidth+=panelAPI.getPosition().getWidth()+15;
        }
        left = totalWidth;
        ArrayList<CustomPanelAPI> panelsSecondRow = new ArrayList<>();
        if(totalWidth>=width){
            for (int i = panelsWithImage.size()-1; i >=0 ; i--) {
                left-=panelsWithImage.get(i).getPosition().getWidth()-15;
                panelsSecondRow.add(panelsWithImage.get(i));
                if(left<width){
                    break;
                }
                panelsWithImage.remove(i);
            }
        }
        for (CustomPanelAPI panelAPI : panelsSecondRow) {
            secondRowWidth+=panelAPI.getPosition().getWidth()+15;
        }
        float startingXFirstRow =  0;
        float startingXSecondRow =  0;
        float leftFirstRow =  (width-totalWidth)/2;
        float leftSecondRow =  (width-secondRowWidth/2);
        float seperatoFirst,seperatorSecond;
        seperatoFirst = totalWidth/panelsWithImage.size();
        seperatorSecond = secondRowWidth/panelsSecondRow.size();
        startingXFirstRow = leftFirstRow;
        startingXSecondRow = leftSecondRow;
        if(!panelsSecondRow.isEmpty()){
            tooltip.getPosition().setSize(width,height*2+5);
            customPanel.getPosition().setSize(width,height*2+5);
        }
        for (CustomPanelAPI panelAPI : panelsWithImage) {
            tooltip.addCustom(panelAPI,0f).getPosition().inTL(startingXFirstRow,0);
            startingXFirstRow+=seperatoFirst;
        }
        for (CustomPanelAPI panelAPI : panelsSecondRow) {
            tooltip.addCustom(panelAPI,0f).getPosition().inTL(startingXSecondRow,iconSize+5);
            startingXSecondRow+=seperatorSecond;
        }

        customPanel.addUIElement(tooltip).inTL(-15, 0);
        return customPanel;
    }
}
