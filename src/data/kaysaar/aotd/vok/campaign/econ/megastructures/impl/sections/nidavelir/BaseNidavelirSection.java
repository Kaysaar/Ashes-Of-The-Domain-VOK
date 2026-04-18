package data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.sections.nidavelir;

import ashlib.data.plugins.misc.AshMisc;
import ashlib.data.plugins.ui.models.BasePopUpDialog;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CutStyle;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.tot.plugins.AoTDCommodityEconSpecManager;
import data.kaysaar.aotd.tot.ui.commoditypanel.AoTDCommodityShortPanelCombined;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.base.BaseMegastructureDialogContent;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.impl.AutomationDialog;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.slider.ManpowerManagementDialog;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.scripts.CoronalHypershuntMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.scripts.NidavelirMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureSection;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class BaseNidavelirSection extends BaseMegastructureSection {
    public int assignedManpower = 0;

    public int getAssignedManpower() {
        return assignedManpower;
    }

    @Override
    public void createEffectSection(TooltipMakerAPI tooltipMakerAPI, boolean isForMainView) {
        if (isForMainView && getManpowerUsed() == 0) return;
        super.createEffectSection(tooltipMakerAPI, isForMainView);
    }

    public void printEffects(TooltipMakerAPI tooltip, int manpowerToBeAssigned, boolean wantToAutomate) {
        LinkedHashMap<String, Integer> increase = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> demand = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> s : getProductionMap().entrySet()) {
            increase.put(s.getKey(), AoTDCommodityEconSpecManager.getCargoAmountFromSupplyOrDemand(manpowerToBeAssigned * s.getValue(), false, s.getKey()));

        }
        for (Map.Entry<String, Integer> s : getDemandMap().entrySet()) {
            demand.put(s.getKey(), AoTDCommodityEconSpecManager.getCargoAmountFromSupplyOrDemand(manpowerToBeAssigned * s.getValue(), true, s.getKey()));
        }
        tooltip.addPara("This section is going to produce this amount of resources, as long as demand is met", 5f);
        tooltip.addSectionHeading("Production", Alignment.MID, 5f);
        tooltip.addCustom(new AoTDCommodityShortPanelCombined(tooltip.getWidthSoFar(), 5, increase).getMainPanel(), 5f);
        tooltip.addSectionHeading("Demand", Alignment.MID, 5f);
        tooltip.addCustom(new AoTDCommodityShortPanelCombined(tooltip.getWidthSoFar(), 5, demand).getMainPanel(), 10f);
    }

    @Override
    public void addToEffectSectionMain(TooltipMakerAPI tl) {
        if (!isRestored) {
            tl.addPara("None", 3f);
        } else {
            if (getManpowerUsed() > 0) {
                printEffectSectionPerManpowerPoint(getManpowerUsed(), tl);
            }
        }
    }

    @Override
    public void reportButtonPressedImpl(ButtonAPI buttonAPI, BaseMegastructureDialogContent dialogContent) {
        String customData = (String) buttonAPI.getCustomData();
        if (customData != null && customData.equals("assign_manpower")) {
            NidavelirMegastructure megastructure = (NidavelirMegastructure) getMegastructureTiedTo();
            BasePopUpDialog dialog = new ManpowerManagementDialog("Manpower Management", this, dialogContent.getSection(), megastructure.getAvailableManpower());
            AshMisc.initPopUpDialog(dialog, 700, 440);
        }
        if (customData != null && customData.equals("automate_section")) {
            NidavelirMegastructure megastructure = (NidavelirMegastructure) getMegastructureTiedTo();
            BasePopUpDialog dialog = new AutomationDialog("Section Automation", this, dialogContent.getSection());
            AshMisc.initPopUpDialog(dialog, 720, 390);
        }
    }

    @Override
    public void createEffectExplanationSectionInSubSection(TooltipMakerAPI tl) {
        if (!isRestored) {
            tl.addPara("For every manpower point assigned:", 5f);
            printEffectSectionPerManpowerPoint(1, tl);
            tl.addSectionHeading("Production per manpower point", Alignment.MID, 5f);
            float width = tl.getWidthSoFar();
            LinkedHashMap<String, Integer> supply = new LinkedHashMap<>();
            LinkedHashMap<String, Integer> demand = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> entry : getProductionMap().entrySet()) {
                supply.put(entry.getKey(), AoTDCommodityEconSpecManager.getCargoAmountFromSupplyOrDemand(entry.getValue(), false, entry.getKey()));
            }
            for (Map.Entry<String, Integer> entry : getDemandMap().entrySet()) {
                demand.put(entry.getKey(), AoTDCommodityEconSpecManager.getCargoAmountFromSupplyOrDemand(entry.getValue(), true, entry.getKey()));

            }
            AoTDCommodityShortPanelCombined combined = new AoTDCommodityShortPanelCombined(width - 10, 3, supply);
            tl.addCustom(combined.getMainPanel(), 3f);
            tl.addSectionHeading("Demand per manpower point", Alignment.MID, 5f);
            combined = new AoTDCommodityShortPanelCombined(width - 10, 3, demand);
            tl.addCustom(combined.getMainPanel(), 3f);
        } else {
            int manpower = getManpowerUsed();
            if (this.isAutomated()) {
                tl.addPara("Section automated!", Color.cyan, 5f);
            } else {
                tl.addPara("Currently assigned manpower : %s", 5f, Color.ORANGE, "" + getManpowerUsed());
            }

            printEffectSectionPerManpowerPoint(manpower, tl);
            float width = tl.getWidthSoFar();
            LinkedHashMap<String, Integer> supply = new LinkedHashMap<>();
            LinkedHashMap<String, Integer> demand = new LinkedHashMap<>();
            tl.addSectionHeading("Section Production", Alignment.MID, 5f);
            for (Map.Entry<String, Integer> entry : getProductionMap().entrySet()) {
                supply.put(entry.getKey(), AoTDCommodityEconSpecManager.getCargoAmountFromSupplyOrDemand(entry.getValue() * manpower, false, entry.getKey()));
            }
            for (Map.Entry<String, Integer> entry : getDemandMap().entrySet()) {
                demand.put(entry.getKey(), AoTDCommodityEconSpecManager.getCargoAmountFromSupplyOrDemand(entry.getValue() * manpower, true, entry.getKey()));

            }
            AoTDCommodityShortPanelCombined combined = new AoTDCommodityShortPanelCombined(width - 10, 3, supply);
            tl.addCustom(combined.getMainPanel(), 3f);
            tl.addSectionHeading("Section Demand", Alignment.MID, 5f);
            combined = new AoTDCommodityShortPanelCombined(width - 10, 3, demand);
            tl.addCustom(combined.getMainPanel(), 3f);
        }

    }

    @Override
    public float getUpkeepOfSection() {
        if (isRestored) {
            return getSpec().getBaseUpkeepAfterRestoration();
        }
        return 0f;

    }

    public void printEffectSectionPerManpowerPoint(int manpowerAssigned, TooltipMakerAPI tl) {

    }

    @Override
    public LinkedHashMap<String, Integer> getDemandMap() {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        map.put(Commodities.METALS, 15);
        map.put(Commodities.RARE_METALS, 8);
        map.put(AoTDCommodities.REFINED_METAL, 10);
        map.put(AoTDCommodities.PURIFIED_TRANSPLUTONICS, 4);
        return map;
    }

    @Override
    public LinkedHashMap<String, Integer> getProductionMap() {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        map.put(Commodities.SHIPS, 25);
        map.put(Commodities.SUPPLIES, 10);
        map.put(Commodities.HAND_WEAPONS, 20);
        return map;
    }

    @Override
    public boolean canRestoreSection() {
        if (this.getSpec().getId().equals("nidavelir_nexus")) {
            return true;
        }
        return getMegastructureTiedTo().getSectionById("nidavelir_nexus").isRestored();
    }

    public int getDeficitIndex() {
        return 3;
    }

    @Override
    public void applySectionOnIndustry(BaseIndustry ind) {
        for (Map.Entry<String, Integer> entry : getProductionMap().entrySet()) {
            ind.supply(entry.getKey(), entry.getValue() * getManpowerUsed());
        }
        for (Map.Entry<String, Integer> entry : getDemandMap().entrySet()) {
            ind.demand(entry.getKey(), entry.getValue() * getManpowerUsed());
        }
        applyDeficitToProduction(getDeficitIndex(), ind, ind.getMaxDeficit(getDemandMap().keySet().toArray(new String[0])), getProductionMap().keySet().toArray(new String[0]));
        ind.getUpkeep().modifyFlat(this.getSpec().getId(), getUpkeepOfSection(), getName());
    }

    @Override
    public void unApplySectionOnIndustry(BaseIndustry ind) {
        super.unApplySectionOnIndustry(ind);
        ind.getUpkeep().unmodifyFlat(this.getSpec().getId());
    }

    public void setAssignedManpower(int assignedManpower) {
        this.assignedManpower = assignedManpower;
    }

    public boolean isAutomated;

    public boolean isAutomated() {
        return isAutomated;
    }

    public void setAutomated(boolean automated) {
        isAutomated = automated;
    }

    public int getManpowerUsed() {
        if (isAutomated) {
            return 2;
        }
        return getAssignedManpower();
    }

    @Override
    public boolean doesHaveCustomSection() {
        return true;
    }

    @Override
    public void createCustomSection(TooltipMakerAPI tooltipMakerAPI) {
        super.createCustomSection(tooltipMakerAPI);
    }

    @Override
    public void advanceImpl(float amount) {
        if (!CoronalHypershuntMegastructure.isWithinReceiverSystem(this.getMegastructureTiedTo().entityTiedTo)) {
            setAutomated(false);
        }
    }

    @Override
    public void addAdditionalButtonsForSection(ArrayList<ButtonAPI> bt, float width, float heightOfButtons, TooltipMakerAPI tooltip) {
        ButtonAPI button = tooltip.addButton("Assign Manpower", "assign_manpower", Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, CutStyle.TL_BR, width, heightOfButtons, 5f);
        bt.add(button);
        button.setEnabled(isRestored && !isAutomated());
        button = tooltip.addButton("Automate Section", "automate_section", Misc.getBasePlayerColor(), Color.cyan.darker().darker(), Alignment.MID, CutStyle.TL_BR, width, heightOfButtons, 5f);
        bt.add(button);
        button.setEnabled(isRestored && CoronalHypershuntMegastructure.isWithinRangeOfAtLeastOneHypershunt(getMegastructureTiedTo().getEntityTiedTo()));
        tooltip.addTooltipToPrevious(new TooltipMakerAPI.TooltipCreator() {
            @Override
            public boolean isTooltipExpandable(Object tooltipParam) {
                return false;
            }

            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return 300;
            }

            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                if (isAutomated) {
                    tooltip.addPara("We can deactivate the automation systems and disconnect it from %s", 5f, Color.ORANGE, "Hypershunt Receiver");

                } else {
                    tooltip.addPara("Our engineers think it is possible to link the %s directly to a %s, allowing full automation of this section, as long as the connection to a Hypershunt remains.", 5f, Color.ORANGE, getName(), "Hypershunt Receiver");

                }
            }
        }, TooltipMakerAPI.TooltipLocation.LEFT, false);
        if (isAutomated()) {
            button.setText("De-automate section");
        }
    }
}
