package data.kaysaar.aotd.vok.scripts.specialprojects.trade;

import ashlib.data.plugins.misc.AshMisc;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.tot.scripts.trade.contracts.AoTDTradeContract;
import data.kaysaar.aotd.vok.campaign.econ.produciton.order.AoTDProductionOrderData;
import data.kaysaar.aotd.vok.campaign.econ.produciton.order.AoTDProductionOrderSnapshot;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.specialprojects.BlackSiteProjectManager;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProject;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProjectStage;

import java.awt.*;
import java.util.Map;

public class BlackSiteTradeContract extends AoTDTradeContract {
    AoTDSpecialProject currentProject;

    public BlackSiteTradeContract(AoTDSpecialProject project) {
        super(project.getProjectSpec().getId(), null, Factions.PLAYER, Integer.MAX_VALUE);
        this.currentProject = project;
    }
    public void reApplyChanges() {
        getContractData().clear();
        for (Map.Entry<String, Integer> entry : AoTDMisc.getOrderedResourceMap(currentProject.getGpCostFromStages()).entrySet()) {
            addContractData(entry.getKey(),entry.getValue(),0f);
        }

        runCleanUp();
    }

    @Override
    public Color getContractTypeColor() {
        return Global.getSector().getFaction(Factions.PIRATES).getBaseUIColor();
    }

    @Override
    public String getContractType() {
        return "Data not found";
    }

    @Override
    public String getContractTypeId() {
        return "black_site_proj";
    }

    @Override
    public String getSubTypeOfContractString() {
        return currentProject.getNameOverride();
    }
    @Override
    public void printCustomSection(TooltipMakerAPI tooltip, float width) {
        tooltip.addPara(
                "Every month this contract attempts to consume the required resources for the project to continue.",
                3f
        );
        tooltip.addPara(
                "The speed at which the stage is completed depends on the amount of resources delivered. If some resources are missing, progress will be slower. If any required resource is completely missing (0 delivered), the stage will not progress.",
                3f
        );

    }

    @Override
    public boolean hasCustomEffectSection() {
        return true;
    }

    @Override
    public boolean canEditContract() {
        return false;
    }

    @Override
    public boolean canTerminateContract() {
        return false;
    }

    @Override
    public boolean canFreezeContract() {
        return true;
    }
    @Override
    public void executeMonthEndForCommodity(int delivered, String commodityId) {
        if (delivered <= 0) return;

        int remaining = delivered;

        for (String order : currentProject.getCurrentlyAttemptedStages()) {
            AoTDSpecialProjectStage stage = currentProject.getStage(order);
            int taken = stage.takeResources(remaining, commodityId);
            remaining -= taken;

            if (remaining <= 0) {
                break;
            }
        }
    }

    @Override
    public void executeMonthEnd(float percentageOfEntireContractMet) {
        contractData.clear();
        reApplyChanges();
    }

    @Override
    public boolean isExpired() {
        return !BlackSiteProjectManager.getInstance().isCurrentOnGoing(this.currentProject);
    }
}
