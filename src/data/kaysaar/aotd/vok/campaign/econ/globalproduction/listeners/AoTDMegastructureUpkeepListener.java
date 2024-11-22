package data.kaysaar.aotd.vok.campaign.econ.globalproduction.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MonthlyReport;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;

public class AoTDMegastructureUpkeepListener implements EconomyTickListener {
    public static String MegaNodeID = "node_id_megastuctures_";

    @Override
    public void reportEconomyTick(int iterIndex) {
        if (GPManager.getInstance().getMegastructures().isEmpty()) return;
        float numIter = Global.getSettings().getFloat("economyIterPerMonth");
        float f = 1f / numIter;

        //CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        MonthlyReport report = SharedData.getData().getCurrentReport();

        MonthlyReport.FDNode fleetNode = report.getNode(MegaNodeID);
        fleetNode.name = "Megastructrures";
        fleetNode.custom = MegaNodeID;
        fleetNode.tooltipCreator = report.getMonthlyReportTooltip();
        for (final GPBaseMegastructure megastructure : GPManager.getInstance().getMegastructures()) {
            float stipend = megastructure.getUpkeep();
            String id = "";
            if (megastructure.getEntityTiedTo() != null) {
                id = megastructure.getEntityTiedTo().getId();
            } else {
                id = megastructure.getSpec().getMegastructureID();
            }
            MonthlyReport.FDNode stipendNode = report.getNode(fleetNode, "node_id_megastructure" + id);
            stipendNode.upkeep += stipend * f;

            if (stipendNode.name == null) {
                stipendNode.name = megastructure.getName();
                if (megastructure.getEntityTiedTo() != null) {
                    stipendNode.name += " : " + megastructure.getEntityTiedTo().getStarSystem().getName();
                }
                stipendNode.icon = megastructure.getIcon();
                stipendNode.tooltipCreator = new TooltipMakerAPI.TooltipCreator() {
                    public boolean isTooltipExpandable(Object tooltipParam) {
                        return false;
                    }

                    public float getTooltipWidth(Object tooltipParam) {
                        return 450;
                    }

                    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                        tooltip.addPara("Monthly upkeep of " + megastructure.getName(), 0f);
                    }
                };
            }
        }


    }

    @Override
    public void reportEconomyMonthEnd() {

    }

}
