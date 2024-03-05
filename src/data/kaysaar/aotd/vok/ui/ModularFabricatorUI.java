package data.kaysaar.aotd.vok.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import com.fs.starfarer.ui.P;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.SMSpecialItem;
import data.kaysaar.aotd.vok.campaign.econ.industry.ModulaProgramotoria;
import data.kaysaar.aotd.vok.campaign.econ.industry.StellaManufactorium;
import data.kaysaar.aotd.vok.campaign.econ.items.ModularConstructorRepo;
import data.kaysaar.aotd.vok.plugins.AoDUtilis;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModularFabricatorUI implements CustomDialogDelegate {
    public static final float WIDTH = 600f;
    public static final float HEIGHT = Global.getSettings().getScreenHeight() - 300f;
    public static final float ENTRY_HEIGHT = 500; //MUST be even
    public static final float ENTRY_WIDTH = WIDTH - 5f; //MUST be even
    public static final float CONTENT_HEIGHT = 80;
    public static final float PROGRAMMING_TIME = 30f;

    public Industry industry;
    public List<ButtonAPI> buttons = new ArrayList<>();
    private String selected;

    public ModularFabricatorUI(Industry industry) {
        this.industry = industry;
    }


    public void createCustomDialog(CustomPanelAPI panel, CustomDialogCallback callback) {
        TooltipMakerAPI panelTooltip = panel.createUIElement(WIDTH, HEIGHT, true);
        panelTooltip.addSectionHeading("Select programming for Constructor", Alignment.MID, 0f);
        String aiId = industry.getAICoreId();
        float opad = 10f;
        float spad = 2f;

        buttons.clear();
        Color baseColor = Misc.getButtonTextColor();
        Color bgColour = Misc.getDarkPlayerColor();
        Color brightColor = Misc.getBrightPlayerColor();
        for (Map.Entry<String, String> specItemToProduce : ModularConstructorRepo.constructorRepo.entrySet()) {
            CustomPanelAPI helper1 = panel.createCustomPanel(ENTRY_WIDTH, ENTRY_HEIGHT + 2f, new ButtonReportingCustomPanel(this));
            TooltipMakerAPI helper;
            SpecialItemSpecAPI upgrdSpec = Global.getSettings().getSpecialItemSpec(specItemToProduce.getKey());
            MarketAPI marketAPI = industry.getMarket();
            int credits = (int) Global.getSector().getPlayerFleet().getCargo().getCredits().get();

            Color color = industry.getMarket().getFaction().getBaseUIColor();
            Color dark = industry.getMarket().getFaction().getDarkUIColor();
            Color gray = Misc.getGrayColor();
            Color highlight = Misc.getHighlightColor();
            Color bad = Misc.getNegativeHighlightColor();
            String spriteName = upgrdSpec.getIconName();
            SpriteAPI sprite = Global.getSettings().getSprite(spriteName);
            boolean enabled = true;
            float aspectRatio = sprite.getWidth() / sprite.getHeight();
            float adjustedWidth = CONTENT_HEIGHT * aspectRatio;
            float defaultPadding = (ENTRY_HEIGHT - CONTENT_HEIGHT) / 2;

// That section is for calculation custom height for section - it is bad looking af, basically here i create replica of what i want to implement to calculate height of what i want to show, its just duplication of code
            String fullTitle = upgrdSpec.getName();
            helper = helper1.createUIElement(ENTRY_WIDTH - adjustedWidth - (3 * opad), CONTENT_HEIGHT, false);
            helper.addSectionHeading(" " + fullTitle, baseColor, brightColor, Alignment.LMID, 0f);
            LabelAPI labelDesc = helper.addPara(upgrdSpec.getDesc(), opad);
            labelDesc.autoSizeToWidth(ENTRY_WIDTH - adjustedWidth - (3 * opad));
            helper.addSectionHeading("Items required", color, dark, Alignment.MID, opad);
            helper.addImage(Global.getSettings().getSpecialItemSpec("modular_constructor_empty").getIconName(), 60, 60, 10f);
            if (!AoTDMainResearchManager.getInstance().isResearchedForPlayer(AoTDTechIds.MEGA_ASSEMBLY_SYSTEMS)) {
                helper.addImage(Global.getSettings().getSpecialItemSpec("modular_constructor_empty").getIconName(), 60, 60, 10f);
            }
            helper.addPara("It takes :" + PROGRAMMING_TIME + " days to produce", Color.ORANGE, 5f);

            LabelAPI label1;

            CustomPanelAPI subIndustryButtonPanel = panel.createCustomPanel(ENTRY_WIDTH, helper.getHeightSoFar() + 30f, new ButtonReportingCustomPanel(this));
            TooltipMakerAPI anchor = subIndustryButtonPanel.createUIElement(ENTRY_WIDTH, helper.getHeightSoFar() + 20, false);

            ButtonAPI areaCheckbox = anchor.addAreaCheckbox("", upgrdSpec.getId(), baseColor, bgColour, brightColor, //new Color(255,255,255,0)
                    ENTRY_WIDTH,
                    helper.getHeightSoFar() + 20,
                    0f,
                    true);

            subIndustryButtonPanel.addUIElement(anchor).inTL(-opad, 0f); //if we don't -opad it kinda does it by its own, no clue why
            anchor = subIndustryButtonPanel.createUIElement(adjustedWidth, helper.getHeightSoFar() + 20, false);
            anchor.addImage(spriteName, adjustedWidth, CONTENT_HEIGHT, 0f);
            subIndustryButtonPanel.addUIElement(anchor).inTL(5, 10);

            TooltipMakerAPI lastPos = anchor;
            CargoAPI storage = marketAPI.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo();
            anchor = subIndustryButtonPanel.createUIElement(ENTRY_WIDTH - adjustedWidth - (3 * opad), CONTENT_HEIGHT, false);
            anchor.addSectionHeading(" " + fullTitle, Alignment.LMID, 0f);
            LabelAPI labelDesc1 = anchor.addPara(upgrdSpec.getDesc(), opad);
            labelDesc1.autoSizeToWidth(ENTRY_WIDTH - adjustedWidth - (3 * opad));
            LabelAPI label;
            anchor.addSectionHeading("Items required", color, dark, Alignment.MID, opad);
            subIndustryButtonPanel.createUIElement(ENTRY_WIDTH, 45, false);
            CustomPanelAPI panelhelper = subIndustryButtonPanel.createCustomPanel(300, 60, null);
            TooltipMakerAPI tooltipMakerAPI = panelhelper.createUIElement(ENTRY_WIDTH, 60, false);
            TooltipMakerAPI labelTooltip = panelhelper.createUIElement(ENTRY_WIDTH, 60, false);
            LabelAPI labelAPI1 = null;
            if (!AoTDMainResearchManager.getInstance().getManagerForPlayer().haveResearched(AoTDTechIds.MEGA_ASSEMBLY_SYSTEMS)) {
                tooltipMakerAPI.addImage(Global.getSettings().getSpecialItemSpec(specItemToProduce.getValue()).getIconName(), 60, 60, 10f);
                labelAPI1 = labelTooltip.addPara(Global.getSettings().getSpecialItemSpec(specItemToProduce.getValue()).getName() + " : " + 1, 10f);

                if (storage.getQuantity(CargoAPI.CargoItemType.SPECIAL, new SpecialItemData(specItemToProduce.getValue(), ModularConstructorRepo.constructorRepo.get(specItemToProduce.getValue()))) >= 1) {
                    labelAPI1.setColor(Misc.getPositiveHighlightColor());
                } else {
                    labelAPI1.setColor(Misc.getNegativeHighlightColor());
                    labelAPI1.setText(labelAPI1.getText()+" None was found in local storage");
                    enabled = false;
                }
                panelhelper.addUIElement(tooltipMakerAPI).inTL(-10, -20);
                panelhelper.addUIElement(labelTooltip).inTL(60, 5);
                anchor.addCustom(panelhelper, 10f);
            }


            panelhelper = subIndustryButtonPanel.createCustomPanel(300, 60, null);
            tooltipMakerAPI = panelhelper.createUIElement(ENTRY_WIDTH, 60, false);
            labelTooltip = panelhelper.createUIElement(ENTRY_WIDTH, 60, false);
            labelAPI1 = null;
            tooltipMakerAPI.addImage(Global.getSettings().getSpecialItemSpec(ModularConstructorRepo.CONSTRUCTOR_EMPTY).getIconName(), 60, 60, 10f);
            labelAPI1 = labelTooltip.addPara(Global.getSettings().getSpecialItemSpec(ModularConstructorRepo.CONSTRUCTOR_EMPTY).getName() + " : " + 1, 10f);

            if (storage.getQuantity(CargoAPI.CargoItemType.SPECIAL, new SpecialItemData(ModularConstructorRepo.CONSTRUCTOR_EMPTY, null)) >= 1) {
                labelAPI1.setColor(Misc.getPositiveHighlightColor());
            } else {
                labelAPI1.setColor(Misc.getNegativeHighlightColor());
                labelAPI1.setText(labelAPI1.getText()+" None was found in local storage");

                enabled = false;
            }
            panelhelper.addUIElement(tooltipMakerAPI).inTL(-10, -20);
            panelhelper.addUIElement(labelTooltip).inTL(60, 5);
            anchor.addCustom(panelhelper, 10f);
            areaCheckbox.setEnabled(enabled);
            anchor.addPara("It will require around %s to program constructor", 10f, Color.ORANGE, "" + PROGRAMMING_TIME);
            subIndustryButtonPanel.addUIElement(anchor).inTL(5 + adjustedWidth + 15, 5);
            panelTooltip.addCustom(subIndustryButtonPanel, 0f);
            buttons.add(areaCheckbox);


            buttons.add(areaCheckbox);
        }

        panel.addUIElement(panelTooltip).inTL(0.0F, 0.0F);
    }

    private String getDayOrDays(int buildTime) {
        if (buildTime == 1) {
            return "day";
        } else {
            return "days";
        }
    }

    public boolean hasCancelButton() {
        return true;
    }

    public String getConfirmText() {
        return "Confirm";
    }

    public String getCancelText() {
        return "Cancel";
    }

    public void customDialogConfirm() {
        for (ButtonAPI button : buttons) {
            if (button.isChecked()) {
                String founded = retrieveSecondCost((String) button.getCustomData());
                CargoAPI cargoAPI = industry.getMarket().getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo();
                if (founded != null) {
                    cargoAPI.removeItems(CargoAPI.CargoItemType.SPECIAL, new SpecialItemData(founded, null), 1);

                }
                cargoAPI.removeItems(CargoAPI.CargoItemType.SPECIAL, new SpecialItemData(ModularConstructorRepo.CONSTRUCTOR_EMPTY, null), 1);
                ModulaProgramotoria painAndSuffering = (ModulaProgramotoria)industry;
                painAndSuffering.mapOfProduction = new Pair<>((String)button.getCustomData(),PROGRAMMING_TIME);
                Global.getSoundPlayer().playUISound("ui_upgrade_industry", 1, 1);
            }

        }

    }

    public String retrieveSecondCost(String constructorID) {
        if (AoTDMainResearchManager.getInstance().isResearchedForPlayer(AoTDTechIds.MEGA_ASSEMBLY_SYSTEMS)) {
            return null;
        }
        return ModularConstructorRepo.constructorRepo.get(constructorID);

    }

    public void customDialogCancel() {
    }


    public CustomUIPanelPlugin getCustomPanelPlugin() {
        return null;
    }

    public void reportButtonPressed(Object id) {
        if (id instanceof String) {
            selected = (String) id;
        }

        for (ButtonAPI button : buttons) {
            if (button.isChecked() && button.getCustomData() != id) button.setChecked(false);
        }
    }

    public static class ButtonReportingCustomPanel extends BaseCustomUIPanelPlugin {
        public ModularFabricatorUI delegate;

        public ButtonReportingCustomPanel(ModularFabricatorUI delegate) {
            this.delegate = delegate;
        }

        @Override
        public void buttonPressed(Object buttonId) {
            super.buttonPressed(buttonId);
            delegate.reportButtonPressed(buttonId);
        }
    }
}
