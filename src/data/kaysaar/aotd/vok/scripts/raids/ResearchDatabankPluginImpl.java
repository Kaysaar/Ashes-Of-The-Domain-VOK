package data.kaysaar.aotd.vok.scripts.raids;

import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.graid.BaseGroundRaidObjectivePluginImpl;

import java.util.Random;

public class ResearchDatabankPluginImpl extends BaseGroundRaidObjectivePluginImpl {
    public ResearchDatabankPluginImpl(MarketAPI market, String id) {
        super(market, id);
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
