package data.kaysaar.aotd.vok.scripts.research.scientist.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MonthlyReport;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

public class ScientistUpkeepListener implements EconomyTickListener {
    @Override
    public void reportEconomyTick(int iterIndex) {
        if(!AoTDMainResearchManager.getInstance().getManagerForPlayer().getResearchCouncil().isEmpty()){

            MonthlyReport.FDNode node = getMonthlyReportNode();
            AoTDMainResearchManager.getInstance().getManagerForPlayer().getResearchCouncil().forEach(x->{
                float numIter = Global.getSettings().getFloat("economyIterPerMonth");
                float f = 1f / numIter;
                node.upkeep+=x.getMonthlySalary()*f;
            });
        }

    }

    @Override
    public void reportEconomyMonthEnd() {
    }
    public MonthlyReport.FDNode getMonthlyReportNode() {
        MonthlyReport report = SharedData.getData().getCurrentReport();
        MonthlyReport.FDNode marketsNode = report.getNode(MonthlyReport.OUTPOSTS);
        if (marketsNode.name == null) {
            marketsNode.name = "Colonies";
            marketsNode.custom = MonthlyReport.OUTPOSTS;
            marketsNode.tooltipCreator = report.getMonthlyReportTooltip();
        }

        MonthlyReport.FDNode paymentNode = report.getNode(marketsNode, "scientist_payment");
        paymentNode.name = "R&D Team";
        //paymentNode.upkeep += payment;
        paymentNode.icon = Global.getSettings().getSpriteName("income_report", "generic_expense");

        if (paymentNode.tooltipCreator == null) {
            paymentNode.tooltipCreator = new TooltipMakerAPI.TooltipCreator() {
                public boolean isTooltipExpandable(Object tooltipParam) {
                    return false;
                }
                public float getTooltipWidth(Object tooltipParam) {
                    return 450;
                }
                public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                    tooltip.addPara("Monthly expenses for your R&D team.", 0f);
                }
            };
        }

        return paymentNode;
    }
}
