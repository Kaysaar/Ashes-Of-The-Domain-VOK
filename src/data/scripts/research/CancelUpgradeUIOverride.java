package data.scripts.research;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCustomDialogDelegate;
import com.fs.starfarer.api.campaign.CustomDialogDelegate;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.BaseIndustryOptionProvider;
import com.fs.starfarer.api.campaign.listeners.DialogCreatorUI;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.Ids.AoDConditions;
import data.Ids.AoDIndustries;
import data.Ids.AoDSwitches;
import data.plugins.AoDUtilis;
import data.scripts.campaign.econ.conditions.IndUpgradeCondition;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CancelUpgradeUIOverride extends BaseIndustryOptionProvider {
    public static Object AOTD_DOWNGRADE = new Object();

    public List<IndustryOptionData> getIndustryOptions(Industry ind) {
        ArrayList<IndustryOptionData> result = new ArrayList<>();

        for (ResearchOption option : AoDUtilis.getResearchAPI().getAllResearchOptions()) {
            IndustrySpecAPI specAPI = Global.getSettings().getIndustrySpec(option.industryId);
            if (option != null && option.hasDowngrade && !specAPI.hasTag("starter") && ind.isUpgrading() && option.downgradeId.equals(ind.getId())) {
                IndustryOptionData opt = new IndustryOptionData("Cancel upgrade", AOTD_DOWNGRADE, ind, this);
                result.add(opt);
                return result;
            }
        }

        return null;
    }


    public void createTooltip(IndustryOptionData opt, TooltipMakerAPI tooltip, float width) {
        IndUpgradeCondition condition = (IndUpgradeCondition) opt.ind.getMarket().getFirstCondition("AodIndUpgrade").getPlugin();
        IndustrySpecAPI upgrdInd = Global.getSettings().getIndustrySpec(condition.currUpgradesOnPlanet.get(opt.ind.getId()));

        String costStr = Misc.getDGSCredits(upgrdInd.getCost());
        if (opt.ind.getBuildOrUpgradeProgress() != 0.0f) {
            costStr = Misc.getDGSCredits(upgrdInd.getCost() * 0.4f);
        }
        tooltip.addPara("Currently upgrading to : " + upgrdInd.getName() + ". Cancel upgrade for a %s refund\n", 10f, Color.ORANGE, "" + costStr);

    }

    public void optionSelected(final IndustryOptionData opt, DialogCreatorUI ui) {
        CustomDialogDelegate delegate = new BaseCustomDialogDelegate() {
            @Override
            public void createCustomDialog(CustomPanelAPI panel, CustomDialogCallback callback) {
                float opad = 10f;
                IndUpgradeCondition condition = (IndUpgradeCondition) opt.ind.getMarket().getFirstCondition("AodIndUpgrade").getPlugin();
                Color highlight = Misc.getHighlightColor();
                TooltipMakerAPI info = panel.createUIElement(600, 100, false);
                info.setParaInsigniaLarge();
                info.addSpacer(2f);
                IndustrySpecAPI upgrdInd = Global.getSettings().getIndustrySpec(condition.currUpgradesOnPlanet.get(opt.ind.getId()));


                if (opt.ind.getBuildOrUpgradeProgress() == 0.0f) {
                    int cost = (int) upgrdInd.getCost();
                    String costStr = Misc.getDGSCredits(cost);
                    info.addPara("Cancelling the upgrade of " + opt.ind.getCurrentName() + " will refund you the full upgrade cost of %s and it will take effect immediately", opad,
                            highlight, "" + costStr);
                } else {
                    int cost = (int) (upgrdInd.getCost() * 0.4f);
                    String costStr = Misc.getDGSCredits(cost);
                    info.addPara("Cancelling the in-progress upgrade of " + opt.ind.getCurrentName() + " will refund you %s of the upgrade cost, or %s and it will take effect immediately", opad,
                            highlight, "40%", costStr);
                }


                panel.addUIElement(info).inTL(0, 0);
            }

            @Override
            public boolean hasCancelButton() {
                return true;
            }

            @Override
            public void customDialogConfirm() {
                IndUpgradeCondition condition = (IndUpgradeCondition) opt.ind.getMarket().getFirstCondition("AodIndUpgrade").getPlugin();

                IndustrySpecAPI upgrdInd = Global.getSettings().getIndustrySpec(condition.currUpgradesOnPlanet.get(opt.ind.getId()));

                boolean fullRefund = opt.ind.getBuildOrUpgradeProgress() == 0.0f;
                if (fullRefund) {
                    Global.getSector().getPlayerFleet().getCargo().getCredits().add(upgrdInd.getCost());
                } else {
                    Global.getSector().getPlayerFleet().getCargo().getCredits().add(upgrdInd.getCost() * 0.4f);

                }
                opt.ind.cancelUpgrade();

            }

            @Override
            public void customDialogCancel() {

            }
        };
        ui.showDialog(600, 125, delegate);
    }

    public void addToIndustryTooltip(Industry ind, Industry.IndustryTooltipMode mode, TooltipMakerAPI tooltip, float width, boolean expanded) {

    }
}
