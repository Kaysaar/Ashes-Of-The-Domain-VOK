package data.kaysaar.aotd.vok.ui.research;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ProgressBarComponent;
import data.kaysaar.aotd.vok.plugins.AoDUtilis;
import data.kaysaar.aotd.vok.ui.customprod.components.UILinesRenderer;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.plugins.AoTDSettingsManager;
import data.kaysaar.aotd.vok.scripts.research.AoTDFactionResearchManager;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOption;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchRewardType;
import data.kaysaar.aotd.vok.ui.basecomps.ImageViewer;
import data.kaysaar.aotd.vok.ui.basecomps.LabelComponent;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class CurrentResearchShowcase implements CustomUIPanelPlugin {
    // 450 ,160

    public CustomPanelAPI mainPanel;
    UILinesRenderer renderer = new UILinesRenderer(0f);
    AoTDResearchNewPlugin aoTDResearchNewPlugin;
    ButtonAPI buttonAPI;
    public CustomPanelAPI subPanel;
    ButtonAPI buttonAPI2;
    float width;
    float height;
    public CurrentResearchShowcase(float width, float height, AoTDResearchNewPlugin aoTDResearchNewPlugin){
        this.aoTDResearchNewPlugin = aoTDResearchNewPlugin;
        this.width = width;
        this.height = height;
        mainPanel = Global.getSettings().createCustom(width,height,this);
        subPanel = mainPanel.createCustomPanel(width,height,null);
        TooltipMakerAPI tooltip = subPanel.createUIElement(width,height,false);
        renderer.setPanel(mainPanel);
        ResearchOption option = AoTDMainResearchManager.getInstance().getManagerForPlayer().getCurrentFocus();
        if(option!=null){
            ImageViewer viewer = new ImageViewer(80,80, AoTDMisc.getTechImage(option.getSpec().getIconId()));
            tooltip.addCustom(viewer.getComponentPanel(),0f);
            LabelComponent title = new LabelComponent(Fonts.ORBITRON_20AABOLD,20,option.getSpec().getName(), Color.ORANGE,width-80,100);

            tooltip.addCustom(title.getComponentPanel(),0f).getPosition().inTL(80,5);


            int currX = 80;
            int currY = 30;
            int size =40;
            int xSpacing = 0;
            for (Map.Entry<String, ResearchRewardType> entry : option.getSpec().getRewards().entrySet()) {
                ImageViewer viewers = new ImageViewer(size,size,AoTDMisc.getImagePathForTechIcon(entry.getKey()));
                tooltip.addCustom(viewers.getComponentPanel(),0f).getPosition().inTL(currX,currY);
                currX += xSpacing+size;
            }
            double multiplier = AoTDSettingsManager.getFloatValue(AoTDSettingsManager.AOTD_RESEARCH_SPEED_MULTIPLIER);
            float defaultDays = (option.TimeToResearch*(float) multiplier);
            float days = AoDUtilis.getDaysFromResearch(option)-option.daysSpentOnResearching;
            ProgressBarComponent component = new ProgressBarComponent(width-15,25,option.getPercentageProgress()/100f, Misc.getDarkPlayerColor().brighter().brighter());

            tooltip.addCustom(component.getRenderingPanel(),0f).getPosition().inTL(10,currY+45);
            LabelAPI labelAPI = tooltip.addPara("Current progress : %s ( %s left till researched)",5f,Color.ORANGE,option.getPercentageProgress()+"%",""+AoTDMisc.convertDaysToString((int) (days)));
            labelAPI.getPosition().inTL(width/2-(labelAPI.computeTextWidth(labelAPI.getText())/2),currY+50);
            buttonAPI = tooltip.addButton("Cancel research",null,Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.NONE,((width-20)/2)-7,20,0f);
            buttonAPI2 = tooltip.addButton("Show on tree",null,Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.NONE,((width-20)/2)-7,20,0f);

            buttonAPI2.getPosition().inTL(12,currY+75);
            buttonAPI.getPosition().inTL(((width-20)/2)+20,currY+75);
        }
        subPanel.addUIElement(tooltip).inTL(-5,0);
        mainPanel.addComponent(subPanel).inTL(0,0);

    }

    public void resetUI(){
        if(this.aoTDResearchNewPlugin !=null){
            this.aoTDResearchNewPlugin.resetCurrTechTree();
        }
        mainPanel.removeComponent(subPanel);
        subPanel = mainPanel.createCustomPanel(width,height,null);
        TooltipMakerAPI tooltip = subPanel.createUIElement(width,height,false);
        renderer.setPanel(mainPanel);
        final ResearchOption option = AoTDMainResearchManager.getInstance().getManagerForPlayer().getCurrentFocus();
        if(option!=null){
            ImageViewer viewer = new ImageViewer(80,80, AoTDMisc.getTechImage(option.getSpec().getIconId()));
            tooltip.addCustom(viewer.getComponentPanel(),0f);
            LabelComponent title = new LabelComponent(Fonts.ORBITRON_20AABOLD,20,option.getSpec().getName(), Color.ORANGE,width-80,100);

            tooltip.addCustom(title.getComponentPanel(),0f).getPosition().inTL(80,5);


            int currX = 80;
            int currY = 30;
            int size =40;
            int xSpacing = 0;
            for (final Map.Entry<String, ResearchRewardType> entry : option.getSpec().getRewards().entrySet()) {
                ImageViewer viewers = new ImageViewer(size,size,AoTDMisc.getImagePathForTechIcon(entry.getKey()));
                tooltip.addCustom(viewers.getComponentPanel(),0f).getPosition().inTL(currX,currY);
                if (entry.getValue().equals(ResearchRewardType.INDUSTRY)){
                    tooltip.addTooltipToPrevious(new TooltipMakerAPI.TooltipCreator() {
                        @Override
                        public boolean isTooltipExpandable(Object tooltipParam) {
                            return true;
                        }

                        @Override
                        public float getTooltipWidth(Object tooltipParam) {
                            return 400;
                        }

                        @Override
                        public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                            MarketAPI marketAPI = initalizeMarket();
                            Industry cover = marketAPI.getIndustry("dummy_industry");
                            marketAPI.addIndustry(Industries.POPULATION);
                            marketAPI.addIndustry(entry.getKey());
                            marketAPI.reapplyConditions();
                            Industry ind = marketAPI.getIndustry(entry.getKey());
                            TooltipMakerAPI tooltipMakerAPI = Global.getSettings().createCustom(1,1,null).createUIElement(100,100,true);
                            ind.createTooltip(Industry.IndustryTooltipMode.NORMAL,tooltipMakerAPI,true);
                            marketAPI.reapplyIndustries();
                            marketAPI.reapplyConditions();
                            if(ind.getId().equals(Industries.FARMING)){
                                ind.getSupply(Commodities.FOOD).getQuantity().modifyFlat("test",6);
                            }
                            if(ind.getId().equals(Industries.AQUACULTURE)){
                                ind.getSupply(Commodities.FOOD).getQuantity().modifyFlat("test",6);
                            }
                            if(ind.getId().equals(Industries.MINING)){
                                ind.getSupply(Commodities.ORE).getQuantity().modifyFlat("test",6);
                                ind.getSupply(Commodities.RARE_ORE).getQuantity().modifyFlat("test",4);
                                ind.getSupply(Commodities.ORGANICS).getQuantity().modifyFlat("test",6);
                                ind.getSupply(Commodities.VOLATILES).getQuantity().modifyFlat("test",4);
                            }

                            ind.createTooltip(Industry.IndustryTooltipMode.NORMAL,tooltip,true);
                            if(ind.getSpec().getDowngrade()!=null){
                                tooltip.addSectionHeading("Upgrades from ",Alignment.MID,10f);
                                tooltip.addPara("This industry upgrades from : %s ",10f,Color.ORANGE,""+Global.getSettings().getIndustrySpec(ind.getSpec().getDowngrade()).getName());

                            }
                            Global.getSector().getEconomy().removeMarket(marketAPI);
                        }
                    }, TooltipMakerAPI.TooltipLocation.RIGHT);
                }
                if (entry.getValue().equals(ResearchRewardType.MODIFIER)){
                    tooltip.addTooltipToPrevious(new TooltipMakerAPI.TooltipCreator() {
                        @Override
                        public boolean isTooltipExpandable(Object tooltipParam) {
                            return true;
                        }

                        @Override
                        public float getTooltipWidth(Object tooltipParam) {
                            return 400;
                        }

                        @Override
                        public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                            pickNormalHeaderByReward(entry.getValue(), tooltip);
                            for (Map.Entry<String, ResearchRewardType> entry : option.Rewards.entrySet()) {
                                if (entry.getValue() == entry.getValue()) {
                                    createInfoFromType(entry.getValue(), entry.getKey(), tooltip);
                                }
                            }
                        }
                    }, TooltipMakerAPI.TooltipLocation.RIGHT);
                }
                currX += xSpacing+size;
            }

            ProgressBarComponent component = new ProgressBarComponent(width-15,25,option.getPercentageProgress()/100f, Misc.getDarkPlayerColor().brighter().brighter());

            tooltip.addCustom(component.getRenderingPanel(),0f).getPosition().inTL(13,currY+45);
            LabelAPI labelAPI =                 tooltip.addPara("Current progress : %s ( %s left till researched)", 5f, Color.ORANGE, option.getPercentageProgress() + "%", AoTDMisc.convertDaysToString((int) (AoDUtilis.getDaysFromResearch(option)-option.daysSpentOnResearching)));

            labelAPI.getPosition().inTL(width/2-(labelAPI.computeTextWidth(labelAPI.getText())/2),currY+50);
            buttonAPI = tooltip.addButton("Cancel research",null,Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.NONE,((width-20))-1,20,0f);

            buttonAPI.getPosition().inTL(16,currY+75);
        }
        subPanel.addUIElement(tooltip).inTL(-5,0);
        mainPanel.addComponent(subPanel).inTL(0,0);
    }
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }
    public void createInfoFromType(ResearchRewardType type, String object, TooltipMakerAPI tooltip) {
        if (type == ResearchRewardType.INDUSTRY) {
            IndustrySpecAPI specAPI = Global.getSettings().getIndustrySpec(object);
            tooltip.addPara(specAPI.getName(), Misc.getPositiveHighlightColor(), 10f);
        }
        if (type == ResearchRewardType.MODIFIER) {
            tooltip.addPara(object, Misc.getPositiveHighlightColor(), 10f);
        }
    }

    public void pickHeaderByReward(ResearchRewardType type, TooltipMakerAPI tooltip) {
        tooltip.setParaInsigniaLarge();
        if (type == ResearchRewardType.INDUSTRY) tooltip.addPara("Industries", Color.ORANGE, 10f);
        if (type == ResearchRewardType.MODIFIER) tooltip.addPara("Permanent modifiers", Color.orange, 10f);
        tooltip.setParaFontDefault();
    }
    public void pickNormalHeaderByReward(ResearchRewardType type, TooltipMakerAPI tooltip) {
        tooltip.setParaInsigniaLarge();
        if (type == ResearchRewardType.INDUSTRY) tooltip.addSectionHeading("Industries", Alignment.MID, 10f);
        if (type == ResearchRewardType.MODIFIER) tooltip.addSectionHeading("Permanent modifiers", Alignment.MID, 10f);
        tooltip.setParaFontDefault();
    }

    public MarketAPI initalizeMarket(){
        MarketAPI marketToShowTooltip = Global.getFactory().createMarket("to_delete","TEst",6);
        marketToShowTooltip.addCondition(Conditions.FARMLAND_ADEQUATE);
        marketToShowTooltip.addCondition(Conditions.ORE_MODERATE);
        marketToShowTooltip.addCondition(Conditions.RARE_ORE_MODERATE);
        marketToShowTooltip.addCondition(Conditions.ORGANICS_COMMON);
        marketToShowTooltip.addCondition(Conditions.VOLATILES_DIFFUSE);

        marketToShowTooltip.addCondition("AoDFoodDemand");
        marketToShowTooltip.addCondition(Conditions.VOLATILES_DIFFUSE);
        marketToShowTooltip.addIndustry("dummy_industry");
        marketToShowTooltip.setFactionId(Global.getSector().getPlayerFaction().getId());
        marketToShowTooltip.reapplyConditions();
        marketToShowTooltip.setFreePort(true);
        for (CommodityOnMarketAPI allCommodity : marketToShowTooltip.getAllCommodities()) {
            allCommodity.getAvailableStat().addTemporaryModFlat(10000,"src",30);
        }
        marketToShowTooltip.setUseStockpilesForShortages(true);
        return marketToShowTooltip;
    }
    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {

    }

    @Override
    public void render(float alphaMult) {
        renderer.render(alphaMult);
    }

    @Override
    public void advance(float amount) {

        if(buttonAPI!=null){
            if(buttonAPI.isChecked()){
                AoTDFactionResearchManager manager = AoTDMainResearchManager.getInstance().getManagerForPlayer();
                manager.setCurrentFocus(null);
                if (!manager.getQueueManager().getQueuedResearchOptions().isEmpty()) {
                    manager.setCurrentFocus(AoTDMainResearchManager.getInstance().getManagerForPlayer().getQueueManager().removeFromTop().Id);
                }
                resetUI();
            }
        }
    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
