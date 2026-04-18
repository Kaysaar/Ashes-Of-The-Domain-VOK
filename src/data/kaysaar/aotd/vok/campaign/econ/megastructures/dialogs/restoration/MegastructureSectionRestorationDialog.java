package data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.restoration;

import ashlib.data.plugins.ui.models.BasePopUpDialog;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.tot.ui.commoditypanel.AoTDCommodityShortPanelCombined;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.base.BaseMegastructureDialogContent;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureSection;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.tradecontracts.BaseRestorationContract;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class MegastructureSectionRestorationDialog extends BasePopUpDialog {
    BaseMegastructureSection section;
    BaseMegastructureDialogContent content;
    boolean isPause;
    public MegastructureSectionRestorationDialog(BaseMegastructureSection section, BaseMegastructureDialogContent content,boolean isPause,String headerTitle) {
        super(headerTitle);
        this.section = section;
        this.content = content;
        this.isPause = isPause;
    }

    @Override
    public void createContentForDialog(TooltipMakerAPI tooltip, float width) {

        tooltip.setParaFont(Fonts.INSIGNIA_LARGE);
        if(!isPause){
            ArrayList<Pair<String,Integer>>pairs = new ArrayList<>();
            LinkedHashMap<String,Integer>costs  = AoTDMisc.getOrderedResourceMap(section.getMonthlyResNeeded());
            for (Map.Entry<String, Integer> entry : costs.entrySet()) {
                Pair<String,Integer>res = new Pair<>(entry.getKey(),entry.getValue());
                pairs.add(res);
            }
            int cost = BaseRestorationContract.getCreditsWorthOfResources(costs);
            tooltip.setParaFont(Fonts.INSIGNIA_LARGE);
            tooltip.addPara("Restoration of %s will require our faction to issue state-mandated contract, which will require us around %s credits monthly and following resources:",2f,Color.ORANGE,section.getName(),Misc.getDGSCredits(cost));
            tooltip.addSectionHeading("Resource Cost", Alignment.MID,10f);
            AoTDCommodityShortPanelCombined panel = new AoTDCommodityShortPanelCombined(width,5,pairs);
            tooltip.addCustom(panel.getMainPanel(),5f);
            tooltip.addPara("If resource criteria are met, we should be able to finish this project in %s",10f,Color.ORANGE, AoTDMisc.convertDaysToString((int) section.getDaysLeft()));
        }
        else {

            tooltip.addPara(
                    "Restoration of %s will be paused. No additional resources will be consumed, but work will continue using already delivered resources until the end of the current month.",
                    2f,
                    Color.ORANGE,
                    section.getName()
            );
            tooltip.addPara(
                    "This will also result in freezing state-mandated contract for restoration of this section.",
                    2f,
                    Color.ORANGE,
                    section.getName()
            );

        }
    }

    @Override
    public void applyConfirmScript() {
        if(isPause){
            this.section.stopRestoration();
        }
        else{
            this.section.startRestoration();
        }

        this.content.getSection().createUI();
        this.content.getMegastructureViewSection().createUI();
    }
}
