package data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.sections.nidavelir;

import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.tot.plugins.AoTDCommodityEconSpecManager;
import data.kaysaar.aotd.tot.ui.commoditypanel.AoTDCommodityShortPanelCombined;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class BulwarkFoundry extends BaseNidavelirSection {

    public int percentageUpkeepModifierPerManpower = 5;

    @Override
    public void printEffectSectionPerManpowerPoint(int manpowerAssigned, TooltipMakerAPI tl) {
        int total = percentageUpkeepModifierPerManpower*manpowerAssigned;
        tl.addPara("Increase production speed of %s and %s by %s", 5f, Color.ORANGE, "Cruisers","Capitals",total+"%");
        tl.addPara("All produced ships have built-in %s and %s",3f,Color.ORANGE,"Flux Distributor","Flux Coil Adjunct");
    }
    public int getDeficitIndex(){
        return 4;
    }
}
