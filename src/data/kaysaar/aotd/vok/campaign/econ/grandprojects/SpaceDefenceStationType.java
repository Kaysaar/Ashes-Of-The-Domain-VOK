package data.kaysaar.aotd.vok.campaign.econ.grandprojects;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.tot.grandwonders.GrandWonderManager;
import data.kaysaar.aotd.tot.grandwonders.GrandWonderTypeSpecAPI;

import java.awt.*;

public class SpaceDefenceStationType implements GrandWonderTypeSpecAPI {
    @Override
    public String getId() {
        return "space_defence_station";
    }

    @Override
    public String getName() {
        return "Defence Station";
    }

    @Override
    public Color getColor() {
        return new Color(138, 47, 47);
    }

    @Override
    public boolean showTypeSeparate() {
        return true;
    }

    @Override
    public boolean canBuildAdditionalWonderOfType(String s, MarketAPI marketAPI) {
        return GrandWonderManager.getInstance().getAmountOfWondersOfSameType(this.getId())<getMaxAmountOfWonderOfType(s,marketAPI);
    }

    @Override
    public int getMaxAmountOfWonderOfType(String s, MarketAPI marketAPI) {
        return 5;
    }

    @Override
    public void createTooltipForTypeOfWonder(TooltipMakerAPI tooltipMakerAPI, MarketAPI marketAPI) {

    }
    @Override
    public      boolean isUniqueViaCategory(){
        return true;
    }
}
