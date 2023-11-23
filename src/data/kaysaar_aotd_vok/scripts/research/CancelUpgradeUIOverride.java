package data.kaysaar_aotd_vok.scripts.research;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCustomDialogDelegate;
import com.fs.starfarer.api.campaign.CustomDialogDelegate;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.listeners.BaseIndustryOptionProvider;
import com.fs.starfarer.api.campaign.listeners.DialogCreatorUI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar_aotd_vok.plugins.AoDUtilis;
import data.kaysaar_aotd_vok.plugins.ReflectionUtilis;
import data.kaysaar_aotd_vok.scripts.campaign.econ.conditions.IndUpgradeCondition;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CancelUpgradeUIOverride extends BaseIndustryOptionProvider {
    public static Object AOTD_DOWNGRADE = new Object();
    ReflectionUtilis reflectionUtilis = new ReflectionUtilis();
    public List<IndustryOptionData> getIndustryOptions(Industry ind) {
        ArrayList<IndustryOptionData> result = new ArrayList<>();

        for (ResearchOption option : AoDUtilis.getResearchAPI().getAllResearchOptions()) {
            if(ind.getSpec().getUpgrade()!=null)return null;
            if(!ind.isUpgrading())return  null;
            BaseIndustry industry = (BaseIndustry) ind;
            String upgradeId = (String) reflectionUtilis.getPrivateVariableFromSuperClass("upgradeId",industry);
            if(upgradeId!=null){
                IndustryOptionData opt = new IndustryOptionData("Cancel upgrade", AOTD_DOWNGRADE, ind, this);
                result.add(opt);
                return result;
            }


        }

        return null;
    }


    public void createTooltip(IndustryOptionData opt, TooltipMakerAPI tooltip, float width) {
        String upgradeId = (String) reflectionUtilis.getPrivateVariableFromSuperClass("upgradeId",(BaseIndustry)opt.ind);
        IndustrySpecAPI upgrdInd = Global.getSettings().getIndustrySpec(upgradeId);
        String costStr = Misc.getDGSCredits(upgrdInd.getCost());
        if (opt.ind.getBuildOrUpgradeProgress() != 0.0f) {
            costStr = Misc.getDGSCredits(upgrdInd.getCost() * 0.4f);
        }
        tooltip.addPara("Currently upgrading to : " + upgrdInd.getName() + ". Cancel upgrade for a %s refund\n", 10f, Color.ORANGE, "" + costStr);

    }

    public void optionSelected(final IndustryOptionData opt, DialogCreatorUI ui) {
        final BaseIndustry industry= (BaseIndustry) opt.ind;
        CustomDialogDelegate delegate = new BaseCustomDialogDelegate() {
            @Override
            public void createCustomDialog(CustomPanelAPI panel, CustomDialogCallback callback) {
                float opad = 10f;
                Color highlight = Misc.getHighlightColor();
                TooltipMakerAPI info = panel.createUIElement(600, 100, false);
                info.setParaInsigniaLarge();
                info.addSpacer(2f);
                String upgradeId = (String) reflectionUtilis.getPrivateVariableFromSuperClass("upgradeId",industry);
                IndustrySpecAPI upgrdInd = Global.getSettings().getIndustrySpec(upgradeId);


                if (industry.getBuildOrUpgradeProgress() == 0.0f) {
                    int cost = (int) upgrdInd.getCost();
                    String costStr = Misc.getDGSCredits(cost);
                    info.addPara("Cancelling the upgrade of " + industry.getCurrentName() + " will refund you the full upgrade cost of %s and it will take effect immediately", opad,
                            highlight, "" + costStr);
                } else {
                    int cost = (int) (upgrdInd.getCost() * 0.4f);
                    String costStr = Misc.getDGSCredits(cost);
                    info.addPara("Cancelling the in-progress upgrade of " + industry.getCurrentName() + " will refund you %s of the upgrade cost, or %s and it will take effect immediately", opad,
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

                String upgradeId = (String) reflectionUtilis.getPrivateVariableFromSuperClass("upgradeId",industry);
                IndustrySpecAPI upgrdInd = Global.getSettings().getIndustrySpec(upgradeId);
                boolean fullRefund = industry.getBuildOrUpgradeProgress() == 0.0f;
                if (fullRefund) {
                    Global.getSector().getPlayerFleet().getCargo().getCredits().add(upgrdInd.getCost());
                } else {
                    Global.getSector().getPlayerFleet().getCargo().getCredits().add(upgrdInd.getCost() * 0.4f);

                }
                industry.cancelUpgrade();

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
