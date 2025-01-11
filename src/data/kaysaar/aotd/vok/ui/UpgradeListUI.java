package data.kaysaar.aotd.vok.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MutableCommodityQuantity;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.ItemEffectsRepo;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.plugins.AoDUtilis;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;


import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class UpgradeListUI implements CustomDialogDelegate {


    public static final float WIDTH = 600f;
    public static final float HEIGHT = Global.getSettings().getScreenHeight() - 300f;
    public static final float ENTRY_HEIGHT = 500; //MUST be even
    public static final float ENTRY_WIDTH = WIDTH - 5f; //MUST be even
    public static final float CONTENT_HEIGHT = 80;


    public Industry industry;
    public List<ButtonAPI> buttons = new ArrayList<>();
    public List<String> upgrades = new ArrayList<>();
    public  String selected = null;
    public UpgradeListUI(Industry industry,ArrayList<String>upgrades) {
        this.industry = industry;
        this.upgrades = upgrades;
    }

    public boolean isIndustry(String id) {
        return getIndustrySpec(id).hasTag(Industries.TAG_INDUSTRY);
    }


    public String helperIndustryStructureOrIndustry(String id) {
        if (isIndustry(id)) {
            return " - Industry";
        }
        return "- Structure";
    }


    int handleFarming(MarketAPI market) {
        int quantity = market.getSize();
        if (market.hasCondition(Conditions.FARMLAND_BOUNTIFUL)) {
            quantity += 2;
        }
        if (market.hasCondition(Conditions.FARMLAND_RICH)) {
            quantity += 1;
        }
        if (market.hasCondition(Conditions.FARMLAND_POOR)) {
            quantity -= 1;
        }
        if (market.hasCondition(Conditions.SOLAR_ARRAY)) {
            quantity += 2;
        }
        return quantity;
    }


    public boolean isCryovolcanicOrFrozen(MarketAPI market) {
        if (market == null) return false;
        boolean isCryovolcanicOrFrozen = false;

        if (market.getPlanetEntity() != null) {
            if (market.getPlanetEntity().getTypeId().equals("frozen") || market.getPlanetEntity().getTypeId().equals("cryovolcanic") || market.getPlanetEntity().getTypeId().equals("frozen1")) {
                isCryovolcanicOrFrozen = true;
            }
        }

        return isCryovolcanicOrFrozen;
    }


    public IndustrySpecAPI getIndustrySpec(String industryId) {
        for (IndustrySpecAPI indSpec : Global.getSettings().getAllIndustrySpecs()) {
            if (indSpec.getId().equals(industryId)) {
                return indSpec;

            }
        }
        return null;
    }

    public void createCustomDialog(CustomPanelAPI panel, CustomDialogCallback callback) {
        TooltipMakerAPI panelTooltip = panel.createUIElement(WIDTH, HEIGHT, true);
        panelTooltip.addSectionHeading("Select an Upgrade", Alignment.MID, 0f);

        float opad = 10f;
        float spad = 2f;

        buttons.clear();

        for (String industryId : upgrades) {
            CustomPanelAPI helper1 = panel.createCustomPanel(ENTRY_WIDTH, ENTRY_HEIGHT + 2f, new ButtonReportingCustomPanel(this));

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
        if (selected == null) return;
        industry.getSpec().setUpgrade(selected);
        SpecialItemData specItem = industry.getSpecialItem();
        for (String tag : getIndustrySpec(selected).getTags()) {
            if(tag.contains("consumes")){
                if (specItem != null) {
                    industry.setSpecialItem(null);
                }
            }
        }
        industry.startUpgrading();
        industry.getSpec().setUpgrade(null);

        float cost = getIndustrySpec(selected).getCost();
        Global.getSector().getPlayerFleet().getCargo().getCredits().subtract(cost);
        Global.getSoundPlayer().playUISound("ui_upgrade_industry", 1, 1);


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
        public UpgradeListUI delegate;

        public ButtonReportingCustomPanel(UpgradeListUI delegate) {
            this.delegate = delegate;
        }

        @Override
        public void buttonPressed(Object buttonId) {
            super.buttonPressed(buttonId);
            delegate.reportButtonPressed(buttonId);
        }
    }
}
