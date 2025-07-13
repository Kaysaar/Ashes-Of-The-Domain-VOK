package data.kaysaar.aotd.vok.campaign.econ.items;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoTransferHandlerAPI;
import com.fs.starfarer.api.campaign.impl.items.BaseSpecialItemPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class ShroudedItemPlugin extends BaseSpecialItemPlugin {

    public static String PLAYER_CAN_MAKE_WEAPONS = "$canMakeDwellerWeapons"; // in player memory
    public static String SHROUDED_SUBSTRATE_AVAILABLE = "$shroudedSubstrateAvailable"; // in player memory

    public static boolean isPlayerCanMakeWeapons() {
        return Global.getSector().getPlayerMemoryWithoutUpdate().getBoolean(PLAYER_CAN_MAKE_WEAPONS);
    }
    public static void setPlayerCanMakeWeapons() {
        Global.getSector().getPlayerMemoryWithoutUpdate().set(PLAYER_CAN_MAKE_WEAPONS, true);
    }


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
        String [] params = spec.getParams().split(",");
        int param = Integer.parseInt(params[0]);
        if (!Global.CODEX_TOOLTIP_MODE) {
            if(Global.getSector().getPlayerMemoryWithoutUpdate().is("$aotd_shroud_r&d",true)){
                if(param==100){

                }
                if(param==400){

                }
                if(param==800){

                }
                if(param==1200){

                }
            }
            else{
                tooltip.addPara("We currently have little understanding of those \"things\". We need to bring those to our R&D team first!",Misc.getTooltipTitleAndLightHighlightColor(),3f);

            }

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
