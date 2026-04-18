package data.kaysaar.aotd.vok.campaign.econ.grandprojects;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.tot.grandwonders.GrandWonderManager;
import data.kaysaar.aotd.tot.grandwonders.GrandWonderTypeSpecAPI;

import java.awt.*;

public class SpaceTransportationType implements GrandWonderTypeSpecAPI {
    @Override
    public String getId() {
        return "space_transportation";
    }

    @Override
    public String getName() {
        return "Space Transportation";
    }

    @Override
    public Color getColor() {
        return new Color(147, 202, 255);
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
