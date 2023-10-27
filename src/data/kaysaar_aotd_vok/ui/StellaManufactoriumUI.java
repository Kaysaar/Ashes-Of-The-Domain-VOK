package data.kaysaar_aotd_vok.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar_aotd_vok.plugins.AoDUtilis;
import data.kaysaar_aotd_vok.scripts.campaign.econ.SMSpecialItem;
import data.kaysaar_aotd_vok.scripts.campaign.econ.industry.StellaManufactorium;
import data.kaysaar_aotd_vok.scripts.research.ResearchOption;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StellaManufactoriumUI implements CustomDialogDelegate {
    public static final float WIDTH = 600f;
    public static final float HEIGHT = Global.getSettings().getScreenHeight() - 300f;
    public static final float ENTRY_HEIGHT = 500; //MUST be even
    public static final float ENTRY_WIDTH = WIDTH - 5f; //MUST be even
    public static final float CONTENT_HEIGHT = 80;


    public Industry industry;
    public ResearchOption selected = null;
    public List<ButtonAPI> buttons = new ArrayList<>();
    public List<SMSpecialItem> itemsThatCanBeProduced = AoDUtilis.getSpecItemsForManufactoriumData();

    public StellaManufactoriumUI(Industry industry) {
        this.industry = industry;
    }


    public void createCustomDialog(CustomPanelAPI panel, CustomDialogCallback callback) {
        TooltipMakerAPI panelTooltip = panel.createUIElement(WIDTH, HEIGHT, true);
        panelTooltip.addSectionHeading("Select Special items to be produced", Alignment.MID, 0f);
        String aiId = industry.getAICoreId();
        float opad = 10f;
        float spad = 2f;

        buttons.clear();
        Color baseColor = Misc.getButtonTextColor();
        Color bgColour = Misc.getDarkPlayerColor();
        Color brightColor = Misc.getBrightPlayerColor();
        for (SMSpecialItem specItemToProduce : itemsThatCanBeProduced) {
            CustomPanelAPI helper1 = panel.createCustomPanel(ENTRY_WIDTH, ENTRY_HEIGHT + 2f, new ButtonReportingCustomPanel(this));
            TooltipMakerAPI helper;
            SpecialItemSpecAPI upgrdSpec = Global.getSettings().getSpecialItemSpec(specItemToProduce.id);
            MarketAPI marketAPI = industry.getMarket();
            int credits = (int) Global.getSector().getPlayerFleet().getCargo().getCredits().get();

            Color color = industry.getMarket().getFaction().getBaseUIColor();
            Color dark = industry.getMarket().getFaction().getDarkUIColor();
            Color gray = Misc.getGrayColor();
            Color highlight = Misc.getHighlightColor();
            Color bad = Misc.getNegativeHighlightColor();
            String spriteName = upgrdSpec.getIconName();
            SpriteAPI sprite = Global.getSettings().getSprite(spriteName);

            float aspectRatio = sprite.getWidth() / sprite.getHeight();
            float adjustedWidth = CONTENT_HEIGHT * aspectRatio;
            float defaultPadding = (ENTRY_HEIGHT - CONTENT_HEIGHT) / 2;

// That section is for calculation custom height for section - it is bad looking af, basically here i create replica of what i want to implement to calculate height of what i want to show, its just duplication of code
            String fullTitle = upgrdSpec.getName();
            helper = helper1.createUIElement(ENTRY_WIDTH - adjustedWidth - (3 * opad), CONTENT_HEIGHT, false);
            helper.addSectionHeading(" " + fullTitle, baseColor, brightColor, Alignment.LMID, 0f);
            LabelAPI labelDesc = helper.addPara(upgrdSpec.getDesc(), opad);
            labelDesc.autoSizeToWidth(ENTRY_WIDTH - adjustedWidth - (3 * opad));
            helper.addSectionHeading("Demand to produce item", color, dark, Alignment.MID, opad);
            helper.beginIconGroup();
            helper.setIconSpacingMedium();
            for (Map.Entry<String, Integer> itemCost : specItemToProduce.cost.entrySet()) {
                if (itemCost.getKey() == null || itemCost.getKey().isEmpty()) {
                    continue;
                }
                CommodityOnMarketAPI com = marketAPI.getCommodityData(itemCost.getKey());
                helper.addIcons(com, itemCost.getValue(), IconRenderMode.NORMAL);


            }
            helper.addIconGroup(32, 1, opad);
            helper.addPara("It takes :" + specItemToProduce.costInDays + "to produce", Color.ORANGE, 10f);

            LabelAPI label1;

            CustomPanelAPI subIndustryButtonPanel = panel.createCustomPanel(ENTRY_WIDTH, helper.getHeightSoFar() + 30f, new ButtonReportingCustomPanel(this));
            TooltipMakerAPI anchor = subIndustryButtonPanel.createUIElement(ENTRY_WIDTH, helper.getHeightSoFar() + 20, false);

            ButtonAPI areaCheckbox = anchor.addAreaCheckbox("", upgrdSpec.getId(), baseColor, bgColour, brightColor, //new Color(255,255,255,0)
                    ENTRY_WIDTH,
                    helper.getHeightSoFar() + 20,
                    0f,
                    true);

            areaCheckbox.setChecked(selected == upgrdSpec);
            subIndustryButtonPanel.addUIElement(anchor).inTL(-opad, 0f); //if we don't -opad it kinda does it by its own, no clue why
            anchor = subIndustryButtonPanel.createUIElement(adjustedWidth, helper.getHeightSoFar() + 20, false);
            anchor.addImage(spriteName, adjustedWidth, CONTENT_HEIGHT, 0f);
            subIndustryButtonPanel.addUIElement(anchor).inTL(5, 10);

            TooltipMakerAPI lastPos = anchor;

            anchor = subIndustryButtonPanel.createUIElement(ENTRY_WIDTH - adjustedWidth - (3 * opad), CONTENT_HEIGHT, false);
            anchor.addSectionHeading(" " + fullTitle, Alignment.LMID, 0f);
            LabelAPI labelDesc1 = anchor.addPara(upgrdSpec.getDesc(), opad);
            labelDesc1.autoSizeToWidth(ENTRY_WIDTH - adjustedWidth - (3 * opad));
            LabelAPI label;
            anchor.addSectionHeading("Demand to produce item", color, dark, Alignment.MID, opad);
            anchor.beginIconGroup();
            anchor.setIconSpacingMedium();
            for (Map.Entry<String, Integer> itemCost : specItemToProduce.cost.entrySet()) {
                if (itemCost.getKey() == null || itemCost.getKey().isEmpty()) {
                    continue;
                }

                CommodityOnMarketAPI com = marketAPI.getCommodityData(itemCost.getKey());
                if (aiId != null) {
                    anchor.addIcons(com, itemCost.getValue() - 1, IconRenderMode.NORMAL);
                } else {
                    anchor.addIcons(com, itemCost.getValue(), IconRenderMode.NORMAL);
                }


            }
            int rows = 1;
            anchor.addIconGroup(32, rows, opad);
            if (aiId != null && aiId.equals(Commodities.ALPHA_CORE)) {
                anchor.addPara("It takes " + (int) specItemToProduce.costInDays / 2 + " days to produce", Color.ORANGE, 10f);
            } else {
                anchor.addPara("It takes " + (int) specItemToProduce.costInDays + " days to produce", Color.ORANGE, 10f);
            }

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
        StellaManufactorium manufactorium = (StellaManufactorium) industry;
        manufactorium.unapplyDemand();
        for (ButtonAPI button : buttons) {
            if (button.isChecked()) {
                SMSpecialItem founded = findItem((String) button.getCustomData());
                manufactorium.demandForProduction.put(founded, founded.costInDays);
            }
        }
        Global.getSoundPlayer().playUISound("ui_upgrade_industry", 1, 1);


    }

    public SMSpecialItem findItem(String id) {
        for (SMSpecialItem smSpecialItem : itemsThatCanBeProduced) {
            if (smSpecialItem.id.equals(id)) {
                return smSpecialItem;
            }
        }
        return null;
    }

    public void customDialogCancel() {
    }


    public CustomUIPanelPlugin getCustomPanelPlugin() {
        return null;
    }

    public void reportButtonPressed(Object id) {
    }

    public static class ButtonReportingCustomPanel extends BaseCustomUIPanelPlugin {
        public StellaManufactoriumUI delegate;

        public ButtonReportingCustomPanel(StellaManufactoriumUI delegate) {
            this.delegate = delegate;
        }

        @Override
        public void buttonPressed(Object buttonId) {
            super.buttonPressed(buttonId);
            delegate.reportButtonPressed(buttonId);
        }
    }
}
