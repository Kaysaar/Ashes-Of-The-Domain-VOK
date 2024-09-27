package data.kaysaar.aotd.vok.ui.components;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOption;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchRewardType;
import data.kaysaar.aotd.vok.plugins.AoTDSettingsManager;
import data.kaysaar.aotd.vok.scripts.research.AoTDFactionResearchManager;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class TechTreeResearchOptionPanel extends UiPanel {
    public ResearchOption TechToResearch;
    ButtonAPI currentButton;
    PositionAPI coordinates;
    CustomPanelAPI buttonPanel;
    CustomPanelAPI trueProgressionBar;


    public HashMap<String,CustomPanelAPI> hexagons = new HashMap<>();
    public float x = 0;
    public float y = 0;

    @Override
    public void createUI(float x, float y) {
        ButtonAPI buttonForCheckbox ;
        buttonPanel = panel.createCustomPanel(AoTDUiComp.WIDTH_OF_TECH_PANEL - 1, AoTDUiComp.HEIGHT_OF_TECH_PANEL - 1, null);
        TooltipMakerAPI vTT = buttonPanel.createUIElement(AoTDUiComp.WIDTH_OF_TECH_PANEL, AoTDUiComp.HEIGHT_OF_TECH_PANEL, false);
        trueProgressionBar = buttonPanel.createCustomPanel(AoTDUiComp.WIDTH_OF_TECH_PANEL - 10, (AoTDUiComp.HEIGHT_OF_TECH_PANEL * 0.05f) + 10, null);
        if (AoTDMainResearchManager.getInstance().getManagerForPlayer().haveResearched(TechToResearch.Id)) {
            buttonForCheckbox = vTT.addAreaCheckbox("", TechToResearch.Id, Misc.getBrightPlayerColor(), Misc.getTooltipTitleAndLightHighlightColor(), Misc.getBrightPlayerColor(), AoTDUiComp.WIDTH_OF_TECH_PANEL, AoTDUiComp.HEIGHT_OF_TECH_PANEL, 0);

        } else if (AoTDMainResearchManager.getInstance().getManagerForPlayer().canResearch(TechToResearch.Id, false)) {
            buttonForCheckbox = vTT.addAreaCheckbox("", TechToResearch.Id, Misc.getPositiveHighlightColor(), Misc.getDarkHighlightColor(), Misc.getTooltipTitleAndLightHighlightColor(), AoTDUiComp.WIDTH_OF_TECH_PANEL, AoTDUiComp.HEIGHT_OF_TECH_PANEL, 0);

        } else {
            buttonForCheckbox = vTT.addAreaCheckbox("", TechToResearch.Id, Misc.getNegativeHighlightColor(), Misc.getDarkHighlightColor(), Misc.getTooltipTitleAndLightHighlightColor(), AoTDUiComp.WIDTH_OF_TECH_PANEL, AoTDUiComp.HEIGHT_OF_TECH_PANEL, 0);
        }
        buttonForCheckbox.setEnabled(false);
        buttonForCheckbox.setClickable(false);
        buttonForCheckbox.highlight();
        LabelAPI title = vTT.addPara(TechToResearch.Name, Color.ORANGE, 10f);
        title.getPosition().inTL(AoTDUiComp.WIDTH_OF_TECH_PANEL - 5 - title.computeTextWidth(title.getText()), 7);
        if(TechToResearch.isResearched){
            vTT.addImage(Global.getSettings().getSpriteName("ui_icons_tech_tree", "researched"), 20, 20, 10f);
            vTT.getPrev().getPosition().inTL(AoTDUiComp.WIDTH_OF_TECH_PANEL - 30 - title.computeTextWidth(title.getText()), 5);
        }


            vTT.addImage(Global.getSettings().getSpriteName("ui_icons_tech_tree", TechToResearch.getSpec().getIconId()), 70, 70, 20f);
            vTT.getPrev().getPosition().inTL(5, 20);

        double multiplier = AoTDSettingsManager.getIntValue(AoTDSettingsManager.AOTD_RESEARCH_SPEED_MULTIPLIER);

        vTT.addTooltipToPrevious(new TooltipMakerAPI.TooltipCreator() {
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
                double multiplier =AoTDSettingsManager.getFloatValue(AoTDSettingsManager.AOTD_RESEARCH_SPEED_MULTIPLIER);

                tooltip.addSectionHeading("Technology Name", Alignment.MID, 10f);
                tooltip.addPara(TechToResearch.Name, 10f);
                tooltip.addSectionHeading("Time required to finish researching", Alignment.MID, 10f);

                float defaultDays = (TechToResearch.TimeToResearch*(float) multiplier);

                float days = defaultDays - TechToResearch.daysSpentOnResearching-(defaultDays*(AoTDMainResearchManager.BONUS_PER_RESEARACH_FAC*(AoTDMainResearchManager.getInstance().getManagerForPlayerFaction().getAmountOfResearchFacilities()-1)));
                String d = " days";
                if (days <= 1) {
                    d = " day";
                }
                //TODO - commision faction pickup or player faction
                AoTDFactionResearchManager manager = AoTDMainResearchManager.getInstance().getManagerForPlayer();
                if (!TechToResearch.isResearched()) {
                    tooltip.addPara((int) days + d + " to finish researching.", Misc.getTooltipTitleAndLightHighlightColor(), 10f);
                } else {
                    tooltip.addPara("Research complete!", Misc.getPositiveHighlightColor(), 10f);
                }

                tooltip.addSectionHeading("Unlocks", Alignment.MID, 10f);
                HashMap<ResearchRewardType, Boolean> haveThat = new HashMap<>();
                for (Map.Entry<String, ResearchRewardType> entry : TechToResearch.Rewards.entrySet()) {
                    haveThat.put(entry.getValue(), true);
                }
                for (ResearchRewardType researchRewardType : haveThat.keySet()) {
                    pickHeaderByReward(researchRewardType, tooltip);
                    for (Map.Entry<String, ResearchRewardType> entry : TechToResearch.Rewards.entrySet()) {
                        if (entry.getValue() == researchRewardType) {
                            createInfoFromType(entry.getValue(), entry.getKey(), tooltip);
                        }
                    }
                }
                if (!TechToResearch.ReqTechsToResearchFirst.isEmpty() || (TechToResearch.ReqItemsToResearchFirst != null && !TechToResearch.ReqItemsToResearchFirst.isEmpty())) {
                    tooltip.addSectionHeading("Requirements", Alignment.MID, 10f);
                }
                if (!TechToResearch.ReqTechsToResearchFirst.isEmpty()) {
                    tooltip.setParaInsigniaLarge();
                    tooltip.addPara("Research", Color.ORANGE, 10f);
                    tooltip.setParaFontDefault();

                }
                for (String s : TechToResearch.ReqTechsToResearchFirst) {
                    if (s.equals("none")) continue;
                    if (manager.haveResearched(s)) {
                        tooltip.addPara(manager.findNameOfTech(s).Name, Misc.getPositiveHighlightColor(), 10f);
                    } else {
                        tooltip.addPara(manager.findNameOfTech(s).Name, Misc.getNegativeHighlightColor(), 10f);
                    }

                }

                if (TechToResearch.ReqItemsToResearchFirst != null && !TechToResearch.ReqItemsToResearchFirst.isEmpty()) {
                    tooltip.setParaInsigniaLarge();
                    LabelAPI title = tooltip.addPara("Items", Color.ORANGE, 10f);
                    tooltip.setParaFontDefault();

                    for (Map.Entry<String, Integer> entry : TechToResearch.ReqItemsToResearchFirst.entrySet()) {
                        CustomPanelAPI panel = getCustomPanelAPI(manager, entry);
                        if (panel == null) continue;
                        tooltip.addCustom(panel, 10f);
                    }

                }
                if (TechToResearch.otherReq != null) {
                    tooltip.setParaInsigniaLarge();
                    tooltip.addPara("Other", Color.ORANGE, 10f);
                    tooltip.setParaFontDefault();
                    if (!TechToResearch.metOtherReq) {
                        tooltip.addPara(TechToResearch.otherReq.two + "\n", Misc.getNegativeHighlightColor(), 10f);
                    } else {
                        tooltip.addPara(TechToResearch.otherReq.two + "\n", Misc.getPositiveHighlightColor(), 10f);
                    }

                }

            }
        }, TooltipMakerAPI.TooltipLocation.RIGHT);
        int beginx = 65;
        int beginy = 36;
        hexagons.clear();
        for (final Map.Entry<String, ResearchRewardType> rewardsEntry : TechToResearch.Rewards.entrySet()) {
            CustomPanelAPI dummyPanel = Global.getSettings().createCustom(40, 40, null);
            TooltipMakerAPI tooltipMakerAPI=  dummyPanel.createUIElement(40,40,false);

            String imagename;
            if(isImageExisting(rewardsEntry.getKey())){
                imagename = Global.getSettings().getSpriteName("ui_icons_tech_tree",rewardsEntry.getKey()+"_sub");

            }//K
            else{
                imagename = Global.getSettings().getSpriteName("ui_icons_tech_tree","special");
            }
          tooltipMakerAPI.addImage(imagename,40,40,0f);
          dummyPanel.addUIElement(tooltipMakerAPI).inTL(0,0);
            vTT.addCustom(dummyPanel, 0f).getPosition().inTL(beginx, beginy);
            if (rewardsEntry.getValue().equals(ResearchRewardType.INDUSTRY)){
                vTT.addTooltipToPrevious(new TooltipMakerAPI.TooltipCreator() {
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
                        MarketAPI marketAPI = initalizeMarket();Industry cover = marketAPI.getIndustry("dummy_industry");
                        marketAPI.addIndustry(Industries.POPULATION);
                        marketAPI.addIndustry(rewardsEntry.getKey());
                        marketAPI.reapplyConditions();
                        Industry ind = marketAPI.getIndustry(rewardsEntry.getKey());
                        TooltipMakerAPI tooltipMakerAPI = panel.createUIElement(100,100,true);
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
            if (rewardsEntry.getValue().equals(ResearchRewardType.MODIFIER)){
                vTT.addTooltipToPrevious(new TooltipMakerAPI.TooltipCreator() {
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
                        pickNormalHeaderByReward(rewardsEntry.getValue(), tooltip);
                        for (Map.Entry<String, ResearchRewardType> entry : TechToResearch.Rewards.entrySet()) {
                            if (entry.getValue() == rewardsEntry.getValue()) {
                                createInfoFromType(entry.getValue(), entry.getKey(), tooltip);
                            }
                        }
                    }
                }, TooltipMakerAPI.TooltipLocation.RIGHT);
            }
            hexagons.put(rewardsEntry.getKey(),dummyPanel);

            beginx+=35;
        }

        float defaultDays = (TechToResearch.TimeToResearch*(float) multiplier);

        float days = defaultDays - TechToResearch.daysSpentOnResearching-(defaultDays*(AoTDMainResearchManager.BONUS_PER_RESEARACH_FAC*(AoTDMainResearchManager.getInstance().getManagerForPlayerFaction().getAmountOfResearchFacilities()-1)));
        String d = " days";
        if (days <= 1) {
            d = " day";
        }
        d += " to finish researching";
        if (AoTDMainResearchManager.getInstance().getManagerForPlayer().getCurrentFocus() != null && AoTDMainResearchManager.getInstance().getManagerForPlayer().getCurrentFocus().
                Id.equals(TechToResearch.Id)) {
            LabelAPI labelAPI = vTT.addPara((int) (days) + d, Misc.getBasePlayerColor(), 10f);
            labelAPI.getPosition().inTL((AoTDUiComp.WIDTH_OF_TECH_PANEL - 1) / 2 - (labelAPI.computeTextWidth(labelAPI.getText())) / 2, 112);


        }
        if (TechToResearch.isResearched) {
            LabelAPI labelAPI = vTT.addPara("Research complete!", Misc.getBasePlayerColor(), 10f);
            labelAPI.getPosition().inTL((AoTDUiComp.WIDTH_OF_TECH_PANEL - 1) / 2 - (labelAPI.computeTextWidth(labelAPI.getText())) / 2, 112);

        }
        if(AoTDMainResearchManager.getInstance().getManagerForPlayer().getCurrentFocus()==null){
            currentButton = vTT.addButton("Research","research:"+TechToResearch.Id,110,25,10f);
        }
        else{
            if(AoTDMainResearchManager.getInstance().getManagerForPlayer().getCurrentFocus().getSpec().getId().equals(TechToResearch.Id)){
                currentButton = vTT.addButton("Stop","stop:"+TechToResearch.Id,110,25,10f);
            }
            else{
                currentButton = vTT.addButton("Queue","queue:"+TechToResearch.Id,110,25,10f);

            }
        }
        currentButton.setEnabled(AoTDMainResearchManager.getInstance().getManagerForPlayer().canResearch(TechToResearch.Id,false)&&!AoTDMainResearchManager.getInstance().getManagerForPlayer().getQueueManager().isInQueue(TechToResearch.Id));

        currentButton.getPosition().inTL(AoTDUiComp.WIDTH_OF_TECH_PANEL-115,AoTDUiComp.HEIGHT_OF_TECH_PANEL-50);

        buttonPanel.addUIElement(vTT).inTL(0, -1);
        buttonPanel.addComponent(trueProgressionBar).inTL(10, (AoTDUiComp.HEIGHT_OF_TECH_PANEL * 0.95f) - 13);
        tooltip.addComponent(buttonPanel).inTL(x, y);
        coordinates = buttonPanel.getPosition();
        this.x = x;
        this.y = y;
    }

    @Nullable
    private CustomPanelAPI getCustomPanelAPI(final AoTDFactionResearchManager manager, final Map.Entry<String, Integer> entry) {
        if(entry.getValue()==0) return null;
        CustomPanelAPI panel = mainPanel.createCustomPanel(400, 60, null);
        TooltipMakerAPI tooltipMakerAPI = panel.createUIElement(60, 60, false);
        TooltipMakerAPI labelTooltip = panel.createUIElement(320, 60, false);
        LabelAPI labelAPI1 = null;

        if (Global.getSettings().getCommoditySpec(entry.getKey()) != null) {
            tooltipMakerAPI.addImage(Global.getSettings().getCommoditySpec(entry.getKey()).getIconName(), 60, 60, 10f);
            labelAPI1 = labelTooltip.addPara(Global.getSettings().getCommoditySpec(entry.getKey()).getName() + " : " + entry.getValue(),10f);
            labelTooltip.addPara("You have %s located in Research Storages", 10f,Color.ORANGE,""+(int) manager.retrieveAmountOfItems(entry.getKey()));

        }
        if (Global.getSettings().getSpecialItemSpec(entry.getKey()) != null) {
            tooltipMakerAPI.addImage(Global.getSettings().getSpecialItemSpec(entry.getKey()).getIconName(), 60, 60, 10f);
            labelAPI1 = labelTooltip.addPara(Global.getSettings().getSpecialItemSpec(entry.getKey()).getName() + " : " + entry.getValue(),10f);
            labelTooltip.addPara("You have %s located in Research Storages", 10f,Color.ORANGE,""+(int) manager.retrieveAmountOfItems(entry.getKey()));

        }
        if (manager.haveMetReqForItem(entry.getKey(), entry.getValue()) || manager.getResearchOptionFromRepo(TechToResearch.Id).havePaidForResearch) {
            labelAPI1.setColor(Misc.getPositiveHighlightColor());

        } else {
            labelAPI1.setColor(Misc.getNegativeHighlightColor());
        }
        if (TechToResearch.isResearched) {
            labelAPI1.setColor(Misc.getPositiveHighlightColor());
        }
        labelAPI1.autoSizeToWidth(320);
        panel.addUIElement(tooltipMakerAPI).inTL(-10, -20);
        panel.addUIElement(labelTooltip).inTL(60, -14);
        return panel;
    }

    public TechTreeResearchOptionPanel(ResearchOption res) {
        TechToResearch = res;
    }

    public void setTechToResearch(ResearchOption researchOption) {
        this.TechToResearch = researchOption;
    }

    public ButtonAPI getCurrentButton() {
        return this.currentButton;
    }

    public PositionAPI getCoordinates() {
        return coordinates;
    }

    public CustomPanelAPI getProgressionBar() {
        return trueProgressionBar;
    }

    public void reset() {
        tooltip.removeComponent(buttonPanel);
        createUI(this.x, this.y);
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
    public boolean isImageExisting(String imageName){
        try {
            Global.getSettings().getSpriteName("ui_icons_tech_tree",imageName+"_sub");
        }
        catch (RuntimeException exception){
            return false;
        }
        return true;

    }
}
