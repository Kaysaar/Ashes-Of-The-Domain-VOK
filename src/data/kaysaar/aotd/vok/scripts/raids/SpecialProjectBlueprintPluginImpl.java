package data.kaysaar.aotd.vok.scripts.raids;

import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.graid.BaseGroundRaidObjectivePluginImpl;

import java.util.Random;

public class SpecialProjectBlueprintPluginImpl extends BaseGroundRaidObjectivePluginImpl {
    protected String memKey;
    protected String hullId;
    public SpecialProjectBlueprintPluginImpl(MarketAPI market, String id,String memKey) {
        super(market, id);
        setSource(null);
    }

    @Override
    public float getQuantity(int marines) {
        return 0;
    }

    @Override
    public int getValue(int marines) {
        return 0;
    }

    @Override
    public float getQuantitySortValue() {
        return 0;
    }

    @Override
    public int getProjectedCreditsValue() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int performRaid(CargoAPI loot, Random random, float lootMult, TextPanelAPI text) {
        return 0;
    }
}
