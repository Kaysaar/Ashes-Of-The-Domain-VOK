package data.kaysaar.aotd.vok.ui.research;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.PopUpUI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ProgressBarComponent;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.plugins.AoDUtilis;
import data.kaysaar.aotd.vok.plugins.AoTDSettingsManager;
import data.kaysaar.aotd.vok.scripts.research.AoTDFactionResearchManager;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOption;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchRewardType;
import data.kaysaar.aotd.vok.ui.customprod.components.UILinesRenderer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static data.kaysaar.aotd.vok.plugins.AoTDVokModPlugin.clearListenersFromTemporaryMarket;

public class ResearchInfoUI extends PopUpUI {
    ResearchOption option;
    CustomPanelAPI mainPanel;
    ButtonAPI currButton;
    String currentCommand;
    float lastYPos;
    double multiplier = AoTDSettingsManager.getFloatValue(AoTDSettingsManager.AOTD_RESEARCH_SPEED_MULTIPLIER);
    ResearchZoomPanel zoomPanel;
    float iconsize = 120;

    public ResearchInfoUI(ResearchOption option, ResearchZoomPanel zoomPanel) {
        this.option = option;
        this.zoomPanel = zoomPanel;
    }


    @Override
    public void createUI(CustomPanelAPI panelAPI) {
        createUIMockup(panelAPI);
        panelAPI.addComponent(mainPanel).inTL(0, 0);
    }

    @Override
    public float createUIMockup(CustomPanelAPI panelAPI) {
        mainPanel = panelAPI.createCustomPanel(panelAPI.getPosition().getWidth(), panelAPI.getPosition().getHeight(), null);

        createImageSection(mainPanel);
        createNameOfTechSection(mainPanel);
        CustomPanelAPI progress = getProgressionBarTooltip(mainPanel);
        float lastY = 0;
        mainPanel.addComponent(progress).inTL(0, iconsize - 15);
        lastY += iconsize - 15 + progress.getPosition().getHeight() + 30;
        CustomPanelAPI widget = createWidgetForIndustries(mainPanel);
        TooltipMakerAPI tooltip = mainPanel.createUIElement(mainPanel.getPosition().getWidth(), 20, false);
        tooltip.addSectionHeading("Unlocks", Alignment.MID, 0f);
        mainPanel.addUIElement(tooltip).inTL(0, lastY);
        lastY += 20;
        if (widget != null) {
            mainPanel.addComponent(widget).inTL(0, lastY);
            lastY += widget.getPosition().getHeight() + 5;
        }
        CustomPanelAPI widget2 = createSectionForSpecialEffects(mainPanel);
        if (widget2 != null) {
            mainPanel.addComponent(widget2).inTL(0, lastY);
            lastY += widget2.getPosition().getHeight() + 5;
        }
        tooltip = mainPanel.createUIElement(mainPanel.getPosition().getWidth(), 20, false);
        if (option.otherReq != null || !option.ReqTechsToResearchFirst.isEmpty() || !option.ReqItemsToResearchFirst.isEmpty()) {
            tooltip.addSectionHeading("Requirements", Alignment.MID, 0f);
            mainPanel.addUIElement(tooltip).inTL(0, lastY);
            lastY += 25;
            if (!option.ReqTechsToResearchFirst.isEmpty()) {
                CustomPanelAPI comp = createSectionForReqResearch(mainPanel);
                mainPanel.addComponent(comp).inTL(0, lastY);
                lastY += comp.getPosition().getHeight() + 5;
            }
            if (!option.ReqItemsToResearchFirst.isEmpty()) {
                CustomPanelAPI comp = createSectionForReqResearchItem(mainPanel);
                mainPanel.addComponent(comp).inTL(0, lastY);
                lastY += comp.getPosition().getHeight() + 10;
            }
            if (option.otherReq != null) {
                CustomPanelAPI comp = createSectionForOtherReq(mainPanel);
                mainPanel.addComponent(comp).inTL(0, lastY);
                lastY += comp.getPosition().getHeight() + 5;
            }

        }
        CustomPanelAPI panelAPI1 = createButtonForResearchOrQueue(mainPanel);
        mainPanel.addComponent(panelAPI1).inTL(0, lastY);
        lastY += panelAPI1.getPosition().getHeight() + 5;
        return lastY;


    }
    public void resetUI() {
        currButton = null;
        getPanelToInfluence().removeComponent(mainPanel);
        mainPanel = null;
        createUI(getPanelToInfluence());
        if (zoomPanel != null) {
            zoomPanel.refresh();
        }
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        if (currButton.isChecked()) {
            currButton.setChecked(false);
            String data = (String) currButton.getCustomData();
            String[] splitted = data.split(":");
            if (splitted[0].equals("research")) {
                AoTDMainResearchManager.getInstance().getManagerForPlayer().pickResearchFocus(option.Id);
                AoTDMainResearchManager.getInstance().getManagerForPlayer().notifyFactionBeginResearch(option);
                Global.getSoundPlayer().playUISound("aotd_research_started", 1f, 1f);
                resetUI();
                this.zoomPanel.plugin.resetCurrentResearch();
                return;
            }
            if (splitted[0].equals("queue_add")) {
                AoTDMainResearchManager.getInstance().getManagerForPlayer().getQueueManager().addToQueue(option.Id);
                Global.getSoundPlayer().playUISound("aotd_research_started", 1f, 1f);
                resetUI();
                return;
            }
            if (splitted[0].equals("queue_remove")) {
                AoTDMainResearchManager.getInstance().getManagerForPlayer().getQueueManager().removeFromQueue(option.Id);
                resetUI();
                return;
            }
            if (splitted[0].equals("stop")) {
                AoTDFactionResearchManager manager = AoTDMainResearchManager.getInstance().getManagerForPlayer();
                manager.setCurrentFocus(null);
                if (!manager.getQueueManager().getQueuedResearchOptions().isEmpty()) {
                    manager.setCurrentFocus(AoTDMainResearchManager.getInstance().getManagerForPlayer().getQueueManager().removeFromTop().Id);
                }
                this.zoomPanel.plugin.resetCurrentResearch();
                resetUI();
                return;
            }
        }
    }

    public CustomPanelAPI createButtonForResearchOrQueue(CustomPanelAPI originPanel) {
        CustomPanelAPI other = originPanel.createCustomPanel(originPanel.getPosition().getWidth(), 30, null);
        TooltipMakerAPI tooltip = other.createUIElement(originPanel.getPosition().getWidth(), 30, false);
        if (AoTDMainResearchManager.getInstance().getManagerForPlayer().getCurrentFocus() == null) {
            currButton = tooltip.addButton("Start Research", "research:" + option.getSpec().getId(), 200, 30, 0f);
        } else {
            if (AoTDMainResearchManager.getInstance().getManagerForPlayer().getCurrentFocus().getSpec().getId().equals(option.getSpec().getId())) {
                currButton = tooltip.addButton("Stop Research", "stop:" + option.getSpec().getId(), 200, 30, 0f);

            } else {
                if (!AoTDMainResearchManager.getInstance().getManagerForPlayer().getQueueManager().isInQueue(option.getSpec().getId())) {
                    currButton = tooltip.addButton("Queue Research", "queue_add:" + option.getSpec().getId(), 200, 30, 0f);

                } else {
                    currButton = tooltip.addButton("Remove from Queue", "queue_remove:" + option.getSpec().getId(), 200, 30, 0f);

                }


            }
        }
        currButton.setEnabled(AoTDMainResearchManager.getInstance().getManagerForPlayer().canResearch(option.Id, false));
        currButton.getPosition().inTL(originPanel.getPosition().getWidth() - 205, 0);
        other.addUIElement(tooltip).inTL(0, 0);
        return other;

    }

    public void createImageSection(CustomPanelAPI originPanel) {
        String iconId = option.getSpec().getIconId();
        UILinesRenderer renderer = new UILinesRenderer(0f);
        CustomPanelAPI imagePanel = originPanel.createCustomPanel(iconsize, iconsize, null);
        TooltipMakerAPI tooltip = imagePanel.createUIElement(iconsize, iconsize, false);
        tooltip.addImage(Global.getSettings().getSpriteName("ui_icons_tech_tree", iconId), iconsize, iconsize, 0);
        imagePanel.addUIElement(tooltip).inTL(0, 0);
        originPanel.addComponent(imagePanel).inTL(-10, -5);

    }

    public void createNameOfTechSection(CustomPanelAPI originPanel) {
        float width = originPanel.getPosition().getWidth() - iconsize;
        CustomPanelAPI imagePanel = originPanel.createCustomPanel(width, iconsize - 10, null);
        TooltipMakerAPI tooltip = imagePanel.createUIElement(width, iconsize - 10, false);
        tooltip.setTitleOrbitronVeryLarge();
        tooltip.addTitle(option.getSpec().getName());
        imagePanel.addUIElement(tooltip).inTL(0, 0);
        originPanel.addComponent(imagePanel).inTL(iconsize - 10, 5);

    }

    public CustomPanelAPI createIndustryWidgetForUI(String indId) {
        UILinesRenderer renderer = new UILinesRenderer(0f);
        CustomPanelAPI panelAPI = Global.getSettings().createCustom(190, 95, renderer);
        renderer.setPanel(panelAPI);
        TooltipMakerAPI tooltip = panelAPI.createUIElement(195, 95, false);
        tooltip.addImage(Global.getSettings().getIndustrySpec(indId).getImageName(), 190, 95, 0f);
        tooltip.addTitle(Global.getSettings().getIndustrySpec(indId).getName()).getPosition().inTL(5, -20);
        panelAPI.addUIElement(tooltip).inTL(-5, 0);
        return panelAPI;
    }

    public CustomPanelAPI getProgressionBarTooltip(CustomPanelAPI originPanel) {
        CustomPanelAPI progression = originPanel.createCustomPanel(originPanel.getPosition().getWidth(), 50, null);
        ProgressBarComponent component = new ProgressBarComponent(progression.getPosition().getWidth() - 10, 21, option.getPercentageProgress()/100f, Misc.getDarkPlayerColor().brighter().brighter());
        TooltipMakerAPI tooltip = progression.createUIElement(originPanel.getPosition().getWidth(), 50, false);
        int defaultDays = (int) AoDUtilis.getDaysFromResearch(option);

        tooltip.addPara("This technology takes %s to research", 5f, Color.ORANGE, AoTDMisc.convertDaysToString(defaultDays));
        if (option.isResearched) {
            tooltip.addPara("Current research progress :%s", 5f, Color.ORANGE, "Finished");
        } else {
            AoTDFactionResearchManager manager = AoTDMainResearchManager.getInstance().getManagerForPlayerFaction();
            if (manager.getCurrentFocus() != null && manager.getCurrentFocus().getSpec().getId().equals(option.getSpec().getId())) {
                tooltip.addPara("Current research progress : %s ( %s left )", 5f, Color.ORANGE, option.getPercentageProgress() + "%", AoTDMisc.convertDaysToString((int) AoDUtilis.getDaysFromResearch(option)));

            } else {
                tooltip.addPara("Current research progress : %s", 5f, Color.ORANGE, option.getPercentageProgress() + "%");

            }

        }
        tooltip.addCustom(component.getRenderingPanel(), 5f);
        progression.addUIElement(tooltip).inTL(0, 0);
        return progression;
    }

    public CustomPanelAPI createWidgetForIndustries(CustomPanelAPI originPanel) {
        ArrayList<String> industryIds = new ArrayList<>();
        for (Map.Entry<String, ResearchRewardType> entry : option.Rewards.entrySet()) {
            if (entry.getValue().equals(ResearchRewardType.INDUSTRY)) industryIds.add(entry.getKey());
        }
        if (industryIds.isEmpty()) {
            return null;
        }
        int totalHeight;
        int heightOfComponent = 115;
        int pad = 5;
        boolean ignoreLastOne = false;
        int rows = industryIds.size() / 2;
        if (industryIds.size() % 2 != 0) {
            rows++;
            ignoreLastOne = true;
        }
        int pads = rows - 1;
        totalHeight = rows * heightOfComponent + pads * pad;
        CustomPanelAPI panelAPI = originPanel.createCustomPanel(originPanel.getPosition().getWidth(), heightOfComponent + 5, null);
        TooltipMakerAPI tooltip = panelAPI.createUIElement(originPanel.getPosition().getWidth(), heightOfComponent + 5, true);
        if (ignoreLastOne) {
            float lastY = 20;
            float lastX = 5;
            for (int i = 0; i < industryIds.size() - 1; i++) {
                CustomPanelAPI panel2 = createIndustryWidgetForUI(industryIds.get(i));
                tooltip.addCustom(panel2, 0f).getPosition().inTL(lastX, lastY);
                final String inds = industryIds.get(i);
                createOnHoverTooltipForIndustry(tooltip, inds, panel2);
                lastX += 210;
                tooltip.setHeightSoFar(lastY + heightOfComponent);
                if (lastX >= originPanel.getPosition().getWidth()) {
                    lastX = 5;
                    lastY += heightOfComponent + pad;
                }

            }
            float center = originPanel.getPosition().getWidth() / 2;
            CustomPanelAPI panel2 = createIndustryWidgetForUI(industryIds.get(industryIds.size() - 1));
            tooltip.addCustom(panel2, 0f).getPosition().inTL(center - panel2.getPosition().getWidth() / 2, lastY);
            final String inds = industryIds.get(industryIds.size() - 1);
            createOnHoverTooltipForIndustry(tooltip, inds, panel2);
        } else {
            float lastY = 20;
            float lastX = 5;
            for (int i = 0; i < industryIds.size(); i++) {
                CustomPanelAPI panel2 = createIndustryWidgetForUI(industryIds.get(i));
                tooltip.addCustom(panel2, 0f).getPosition().inTL(lastX, lastY);
                final String inds = industryIds.get(i);
                createOnHoverTooltipForIndustry(tooltip, inds, panel2);
                lastX += 210;
                tooltip.setHeightSoFar(lastY + heightOfComponent);
                if (lastX >= originPanel.getPosition().getWidth()) {
                    lastX = 5;
                    lastY += heightOfComponent + pad;
                }

            }
        }
        panelAPI.addUIElement(tooltip).inTL(0, 0);
        return panelAPI;


    }

    public static void createOnHoverTooltipForIndustry(TooltipMakerAPI tooltip, String industryId, UIComponentAPI attatchTo) {
        tooltip.addTooltipTo(new TooltipMakerAPI.TooltipCreator() {
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
                MarketAPI marketAPI = initalizeMarket();
                marketAPI.addIndustry(Industries.POPULATION);
                marketAPI.addIndustry(Industries.SPACEPORT);
                marketAPI.reapplyConditions();
                Industry ind = Global.getSettings().getIndustrySpec(industryId).getNewPluginInstance(marketAPI);
                if (ind.getId().equals(Industries.FARMING)) {
                    ind.getSupply(Commodities.FOOD).getQuantity().modifyFlat("test", 6);
                }
                if (ind.getId().equals(Industries.AQUACULTURE)) {
                    ind.getSupply(Commodities.FOOD).getQuantity().modifyFlat("test", 6);
                }
                if (ind.getId().equals(Industries.MINING)) {
                    ind.getSupply(Commodities.ORE).getQuantity().modifyFlat("test", 6);
                    ind.getSupply(Commodities.RARE_ORE).getQuantity().modifyFlat("test", 4);
                    ind.getSupply(Commodities.ORGANICS).getQuantity().modifyFlat("test", 6);
                    ind.getSupply(Commodities.VOLATILES).getQuantity().modifyFlat("test", 4);
                }

                ind.createTooltip(Industry.IndustryTooltipMode.NORMAL, tooltip, true);
                tooltip.addPara("*Size of example market is 6 and all resource conditions are set to give none of bonuses to any industry.", Misc.getTooltipTitleAndLightHighlightColor(), 3f);

                if (ind.getSpec().getDowngrade() != null) {
                    tooltip.addSectionHeading("Upgrades from ", Alignment.MID, 10f);
                    tooltip.addPara("This industry upgrades from : %s ", 10f, Color.ORANGE, "" + Global.getSettings().getIndustrySpec(ind.getSpec().getDowngrade()).getName());

                }
                clearListenersFromTemporaryMarket();

            }
        }, attatchTo, TooltipMakerAPI.TooltipLocation.RIGHT,false);
    }

    public CustomPanelAPI createSectionForSpecialEffects(CustomPanelAPI originPanel) {
        CustomPanelAPI other = originPanel.createCustomPanel(originPanel.getPosition().getWidth(), 20, null);
        TooltipMakerAPI tooltip = other.createUIElement(originPanel.getPosition().getWidth(), 20, true);
        boolean addedSth = false;
        float pad = 3f;
        for (Map.Entry<String, ResearchRewardType> entry : option.Rewards.entrySet()) {
            if (entry.getValue().equals(ResearchRewardType.MODIFIER)) {
                tooltip.addPara(entry.getKey(), Misc.getPositiveHighlightColor(), pad);
                pad = 10f;
                addedSth = true;
            }
        }
        if (addedSth) {
            other.addUIElement(tooltip).inTL(0, 0);
            return other;
        }
        return null;

    }

    @Override
    public void processInput(List<InputEventAPI> events) {
        super.processInput(events);
    }

    public CustomPanelAPI createSectionForOtherReq(CustomPanelAPI originPanel) {
        CustomPanelAPI other = originPanel.createCustomPanel(originPanel.getPosition().getWidth(), 30, null);
        TooltipMakerAPI tooltip = other.createUIElement(originPanel.getPosition().getWidth(), 30, true);
        if (option.otherReq != null) {
            if (!option.metOtherReq) {
                tooltip.addPara(option.otherReq.two, Misc.getNegativeHighlightColor(), 0f);
            } else {
                tooltip.addPara(option.otherReq.two, Misc.getPositiveHighlightColor(), 0f);
            }

        }
        other.addUIElement(tooltip).inTL(0, 0);
        return other;

    }

    public CustomPanelAPI createSectionForReqResearch(CustomPanelAPI originPanel) {
        CustomPanelAPI other = originPanel.createCustomPanel(originPanel.getPosition().getWidth(), 120, null);
        TooltipMakerAPI tooltip = other.createUIElement(originPanel.getPosition().getWidth(), 120, true);
        float pad = 0f;
        float height = 0;
        for (String s : option.ReqTechsToResearchFirst) {
            tooltip.addCustom(populateReqResearch(AoTDMainResearchManager.getInstance().getManagerForPlayerFaction(), s), pad);
            height += tooltip.getPrev().getPosition().getHeight() + pad;
            pad = 5f;
        }
        if (tooltip.getHeightSoFar() <= 120) {
            other.getPosition().setSize(other.getPosition().getWidth(), tooltip.getHeightSoFar());

        } else {
            other.getPosition().setSize(other.getPosition().getWidth(), 120);
        }


        other.addUIElement(tooltip).inTL(0, 0);
        return other;

    }

    public CustomPanelAPI createSectionForReqResearchItem(CustomPanelAPI originPanel) {
        CustomPanelAPI other = originPanel.createCustomPanel(originPanel.getPosition().getWidth(), 140, null);
        TooltipMakerAPI tooltip = other.createUIElement(originPanel.getPosition().getWidth(), 140, true);
        float pad = 10f;
        for (Map.Entry<String, Integer> entry : option.ReqItemsToResearchFirst.entrySet()) {
            CustomPanelAPI panel = populateReqItems(AoTDMainResearchManager.getInstance().getManagerForPlayerFaction(), entry);
            if (panel == null) continue;
            tooltip.addCustom(panel, pad);
        }

        other.addUIElement(tooltip).inTL(0, 0);
        if (tooltip.getHeightSoFar() <= 140) {
            other.getPosition().setSize(other.getPosition().getWidth(), tooltip.getHeightSoFar());

        } else {
            other.getPosition().setSize(other.getPosition().getWidth(), 140);
        }
        return other;

    }

    private CustomPanelAPI populateReqItems(final AoTDFactionResearchManager manager, final Map.Entry<String, Integer> entry) {
        if (entry.getValue() == 0) return null;
        CustomPanelAPI panel = Global.getSettings().createCustom(400, 60, null);
        TooltipMakerAPI tooltipMakerAPI = panel.createUIElement(60, 60, false);
        TooltipMakerAPI labelTooltip = panel.createUIElement(320, 60, false);
        LabelAPI labelAPI1 = null;

        if (Global.getSettings().getCommoditySpec(entry.getKey()) != null) {
            tooltipMakerAPI.addImage(Global.getSettings().getCommoditySpec(entry.getKey()).getIconName(), 60, 60, 10f);
            labelAPI1 = labelTooltip.addPara(Global.getSettings().getCommoditySpec(entry.getKey()).getName() + " : " + entry.getValue(), 10f);
            labelTooltip.addPara("You have %s located in Research Storages", 10f, Color.ORANGE, "" + (int) manager.retrieveAmountOfItems(entry.getKey()));

        }
        if (Global.getSettings().getSpecialItemSpec(entry.getKey()) != null) {
            tooltipMakerAPI.addImage(Global.getSettings().getSpecialItemSpec(entry.getKey()).getIconName(), 60, 60, 10f);
            labelAPI1 = labelTooltip.addPara(Global.getSettings().getSpecialItemSpec(entry.getKey()).getName() + " : " + entry.getValue(), 10f);
            labelTooltip.addPara("You have %s located in Research Storages", 10f, Color.ORANGE, "" + (int) manager.retrieveAmountOfItems(entry.getKey()));

        }
        if (manager.haveMetReqForItem(entry.getKey(), entry.getValue()) || manager.getResearchOptionFromRepo(option.Id).havePaidForResearch) {
            labelAPI1.setColor(Misc.getPositiveHighlightColor());

        } else {
            labelAPI1.setColor(Misc.getNegativeHighlightColor());
        }
        if (option.isResearched) {
            labelAPI1.setColor(Misc.getPositiveHighlightColor());
        }
        labelAPI1.autoSizeToWidth(320);
        panel.addUIElement(tooltipMakerAPI).inTL(-10, -20);
        panel.addUIElement(labelTooltip).inTL(60, -14);
        return panel;
    }

    private CustomPanelAPI populateReqResearch(final AoTDFactionResearchManager manager, final String researchID) {
        CustomPanelAPI panel = Global.getSettings().createCustom(360, 80, null);
        TooltipMakerAPI tooltipMakerAPI = panel.createUIElement(80, 80, false);
        TooltipMakerAPI labelTooltip = panel.createUIElement(300, 60, false);
        LabelAPI labelAPI1 = null;

        tooltipMakerAPI.addImage(Global.getSettings().getSpriteName("ui_icons_tech_tree", manager.getResearchOptionFromRepo(researchID).getSpec().getIconId()), 80, 80, 0);
        labelTooltip.addTitle(manager.getResearchOptionFromRepo(researchID).getSpec().getName());
        if (manager.getResearchOptionFromRepo(researchID).isResearched()) {
            labelAPI1 = labelTooltip.addPara("Already researched !", Misc.getPositiveHighlightColor(), 20f);
        } else {
            labelAPI1 = labelTooltip.addPara("Must be researched!", Misc.getNegativeHighlightColor(), 20f);
        }

        labelAPI1.autoSizeToWidth(320);
        panel.addUIElement(tooltipMakerAPI).inTL(-12, 0);
        panel.addUIElement(labelTooltip).inTL(70, 7);
        return panel;
    }

    public static MarketAPI initalizeMarket() {
        if (Global.getSector().getMemory().get("$aotd_checkout_market")!=null) {
            return (MarketAPI) Global.getSector().getMemory().get("$aotd_checkout_market");
        } else {
            MarketAPI marketToShowTooltip = Global.getFactory().createMarket("to_delete", "TEst", 6);
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
                allCommodity.getAvailableStat().addTemporaryModFlat(10000, "src", 30);
            }
            marketToShowTooltip.setUseStockpilesForShortages(true);
            Global.getSector().getMemory().set("$aotd_checkout_market", marketToShowTooltip);

            return marketToShowTooltip;
        }
    }


    @Override
    public void onExit() {
        super.onExit();
        zoomPanel.unlockButtonsFromHover();
    }

    public void clearUI() {
        getPanelToInfluence().removeComponent(mainPanel);
        createUI(getPanelToInfluence());
    }

}
