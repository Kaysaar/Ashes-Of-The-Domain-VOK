package data.kaysaar.aotd.vok.campaign.econ.grandprojects;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.tot.grandwonders.GrandWonderManager;
import data.kaysaar.aotd.tot.grandwonders.GrandWonderTypeSpecAPI;

import java.awt.*;

public class BiosphereType  implements GrandWonderTypeSpecAPI {
    @Override
    public String getId() {
        return "biosphere";
    }

    @Override
    public String getName() {
        return "Biosphere";
    }

    @Override
    public Color getColor() {
        return Misc.getPositiveHighlightColor().darker();
    }

    @Override
    public boolean showTypeSeparate() {
        return true;
    }

    @Override
    public boolean canBuildAdditionalWonderOfType(String s, MarketAPI marketAPI) {
        return GrandWonderManager.getInstance().getBuiltSoFar(s)<getMaxAmountOfWonderOfType(s,marketAPI);
    }

    @Override
    public int getMaxAmountOfWonderOfType(String s, MarketAPI marketAPI) {
        return 1;
    }

    @Override
    public void createTooltipForTypeOfWonder(TooltipMakerAPI tooltipMakerAPI, MarketAPI marketAPI) {

    }
    @Override
    public      boolean isUniqueViaCategory(){
        return false;
    }
}
