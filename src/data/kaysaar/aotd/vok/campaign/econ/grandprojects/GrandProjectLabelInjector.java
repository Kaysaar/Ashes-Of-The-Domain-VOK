package data.kaysaar.aotd.vok.campaign.econ.grandprojects;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.kaysaar.aotd.vok.scripts.coreui.listeners.CargoPanelContextUI;
import data.kaysaar.aotd.vok.scripts.coreui.listeners.IndustryPanelContextUI;
import data.kaysaar.aotd.vok.scripts.coreui.listeners.MarketUIListener;
import data.kaysaar.aotd.vok.ui.basecomps.LabelWithHighlight;

import java.awt.*;

public class GrandProjectLabelInjector implements MarketUIListener {
    @Override
    public void onMarketOverviewDiscovered(IndustryPanelContextUI ctx) {
        MarketAPI market = ctx.market;
        UIPanelAPI panelOfOtherInfo = ctx.panelOfOtherInfo;
        UIPanelAPI mainColonyPanel = ctx.mainColonyPanel;
        for (UIComponentAPI componentAPI : ReflectionUtilis.getChildrenCopy(mainColonyPanel)) {
            if (componentAPI instanceof CustomPanelAPI panel && panel.getPlugin() instanceof LabelWithHighlight highlight) {
                if (highlight.getID().equals("label_grand_project")) return;
            }

        }
        LabelWithHighlight label = new LabelWithHighlight(300, 100, "label_grand_project");
        int number = market.getIndustries().stream().filter(x->x.getSpec().hasTag("grand_project")).toList().size();
        label.addLabelHighlighted("Grand Projects: %s / %s", Fonts.INSIGNIA_LARGE, Misc.getGrayColor(), Color.ORANGE, Alignment.TL, 0f, ""+number, "2");
        label.setCreator(new TooltipMakerAPI.TooltipCreator() {
            @Override
            public boolean isTooltipExpandable(Object tooltipParam) {
                return false;
            }

            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return 400;
            }

            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addPara("Grand projects are unique structures, that are step above usual infrastructure or industry, providing unique bonuses or enormous production capabilities.",3f);
                tooltip.addPara("Given they are enormous undertaking, those structures will usually require Global Production capabilities!",Misc.getTooltipTitleAndLightHighlightColor(),5f);
                tooltip.addPara("For every 2 Industry slots, you gain one additional Grand Project slot.",Misc.getTooltipTitleAndLightHighlightColor(),10f);
                tooltip.addPara("Exceeding limit of Grand Project's will result in heavy income penalties!",Misc.getNegativeHighlightColor(),3f);

            }
        });
        label.createUI();
        mainColonyPanel.addComponent(label.getMainPanel()).inTL(500, 585);
    }

    @Override
    public void onSubmarketCargoCreated(CargoPanelContextUI ctx) {

    }
}
