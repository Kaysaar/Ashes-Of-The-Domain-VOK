package data.kaysaar.aotd.vok.campaign.econ.items;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoTransferHandlerAPI;
import com.fs.starfarer.api.campaign.impl.items.BaseSpecialItemPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class TenebriumPlugin extends BaseSpecialItemPlugin {
    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, CargoTransferHandlerAPI transferHandler, Object stackSource, boolean useGray) {
        float opad = 10f;
        String design = getDesignType();
        if (!Global.CODEX_TOOLTIP_MODE) {
            tooltip.addTitle(getName(), Misc.getDesignTypeColor(design));
        } else {
            tooltip.addSpacer(-opad);
        }

        if(stackSource!=null) {
            Misc.addDesignTypePara(tooltip, design, opad);
        }


        if (Global.CODEX_TOOLTIP_MODE) {
            tooltip.setParaSmallInsignia();
        }
        if (!spec.getDesc().isEmpty()) {
            Color c = Misc.getTextColor();
            tooltip.addPara(spec.getDesc(), c, opad);
        }
        else{
            tooltip.addPara("No description... yet.", opad);
        }
        if(stackSource!=null) {
            addCostLabel(tooltip, opad, transferHandler, stackSource);
        }



    }
}
