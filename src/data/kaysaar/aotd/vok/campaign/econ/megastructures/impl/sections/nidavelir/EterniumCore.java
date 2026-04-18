package data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.sections.nidavelir;

import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.tot.plugins.AoTDCommodityEconSpecManager;
import data.kaysaar.aotd.tot.ui.commoditypanel.AoTDCommodityShortPanelCombined;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class EterniumCore extends BaseNidavelirSection {
    public int percentageUpkeepModifierPerManpower = 5;
    @Override
    public void printEffectSectionPerManpowerPoint(int manpowerAssigned, TooltipMakerAPI tl) {
        int total = percentageUpkeepModifierPerManpower*manpowerAssigned;
        tl.addPara("Increase black site project progression speed by %s", 5f, Color.ORANGE,total+"%");

    }
    public int getDeficitIndex(){
        return 5;
    }
}
