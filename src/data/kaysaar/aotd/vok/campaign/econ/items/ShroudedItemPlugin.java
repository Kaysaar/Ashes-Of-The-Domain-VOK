package data.kaysaar.aotd.vok.campaign.econ.items;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoTransferHandlerAPI;
import com.fs.starfarer.api.campaign.impl.items.BaseSpecialItemPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.specialprojects.projects.shroud.ShroudProjectMisc;

import java.awt.*;

public class ShroudedItemPlugin extends BaseSpecialItemPlugin {


    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, CargoTransferHandlerAPI transferHandler, Object stackSource) {
        float pad = 3f;
        float opad = 10f;
        float small = 5f;
        Color h = Misc.getHighlightColor();
        Color g = Misc.getGrayColor();
        Color b = Misc.getButtonTextColor();
        b = Misc.getPositiveHighlightColor();

        if (!Global.CODEX_TOOLTIP_MODE) {
            tooltip.addTitle(getName());
        } else {
            tooltip.addSpacer(-opad);
        }

        String design = getDesignType();
        if (design != null) {
            Misc.addDesignTypePara(tooltip, design, 10f);
        }

        if (!spec.getDesc().isEmpty()) {
            if (Global.CODEX_TOOLTIP_MODE) {
                tooltip.setParaSmallInsignia();
            }
            tooltip.addPara(spec.getDesc(), Misc.getTextColor(), opad);
        }

        addCostLabel(tooltip, opad, transferHandler, stackSource);
        if (!Global.CODEX_TOOLTIP_MODE) {
            if(ShroudProjectMisc.getLevelOfUnderstanding()<=0){
                if (!AoTDMisc.getPlayerFactionMarkets().isEmpty()) {
                    tooltip.addPara("We currently have little understanding of those \"things\". We need to bring those to our R&D team first!", Misc.getTooltipTitleAndLightHighlightColor(), 3f);

                } else {
                    tooltip.addPara("We currently have little understanding of those \"things\". We need to establish our own R&D team, together with our faction and resources to further study it.", Misc.getTooltipTitleAndLightHighlightColor(), 3f);

                }
            }


        } else {
            ShroudProjectMisc.updateCommodityInfo();
        }
    }

    @Override
    public float getTooltipWidth() {
        return super.getTooltipWidth();
    }

    @Override
    public boolean isTooltipExpandable() {
        return false;
    }

}
