package data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.sections.nidavelir;

import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.tot.plugins.AoTDCommodityEconSpecManager;
import data.kaysaar.aotd.tot.ui.commoditypanel.AoTDCommodityShortPanelCombined;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class VanguardForge extends BaseNidavelirSection {
    public int percentageUpkeepModifierPerManpower = 5;
    @Override
    public void printEffectSectionPerManpowerPoint(int manpowerAssigned, TooltipMakerAPI tl) {
        int total = percentageUpkeepModifierPerManpower*manpowerAssigned;
        tl.addPara("Increase production speed of %s and %s by %s", 5f, Color.ORANGE, "Frigates","Destroyers",total+"%");
        tl.addPara("All produced ships have increased amount of available S-mods by %s",3f,Color.ORANGE,"1");
    }
    public int getDeficitIndex(){
        return 7;
    }
}
