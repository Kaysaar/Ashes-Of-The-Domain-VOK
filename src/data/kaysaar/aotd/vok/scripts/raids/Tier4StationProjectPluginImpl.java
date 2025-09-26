package data.kaysaar.aotd.vok.scripts.raids;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.graid.BaseGroundRaidObjectivePluginImpl;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.MarketCMD;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.Random;

public class Tier4StationProjectPluginImpl extends BaseGroundRaidObjectivePluginImpl {
    String data;
    String faction;
    public Tier4StationProjectPluginImpl(MarketAPI market, String id,String stationID) {
        super(market, id);
        setSource(null);
        MarketCMD.RaidDangerLevel level = getDangerLevel();
        int marines = level.marineTokens;
        setMarinesRequired(marines);
        this.data =stationID;
        this.faction = market.getFaction().getDisplayName();

    }

    @Override
    public float getQuantity(int marines) {
        return 1;
    }

    @Override
    public int getValue(int marines) {
        CargoStackAPI stack = Global.getFactory().createCargoStack(CargoAPI.CargoItemType.SPECIAL,
                new SpecialItemData(getId(), getData()), null);
        return stack.getBaseValuePerUnit();
    }

    @Override
    public MarketCMD.RaidDangerLevel getDangerLevel() {
        return MarketCMD.RaidDangerLevel.EXTREME;
    }

    @Override
    public CargoStackAPI getStackForIcon() {
        CargoStackAPI stack = Global.getFactory().createCargoStack(CargoAPI.CargoItemType.SPECIAL,
                new SpecialItemData(getId(), getData()), null);
        return stack;
    }

    public String getData() {
        return data;
    }

    @Override
    public float getQuantitySortValue() {
        return QUANTITY_SORT_TIER_3;
    }

    @Override
    public int getProjectedCreditsValue() {
        CargoStackAPI stack = Global.getFactory().createCargoStack(CargoAPI.CargoItemType.SPECIAL,
                new SpecialItemData(getId(), getData()), null);
        return stack.getBaseValuePerUnit();
    }

    @Override
    public String getName() {
        CargoStackAPI stack = Global.getFactory().createCargoStack(CargoAPI.CargoItemType.SPECIAL,
                new SpecialItemData(getId(), getData()), null);
        return stack.getDisplayName();
    }

    public void setMarinesRequired(int marines) {
        marines = Math.min(MarketCMD.MAX_MARINE_TOKENS, marines);
        if (marines < 0) marines = 0;
        marinesRequired = marines;
    }
    @Override
    public boolean hasTooltip() {
        return true;
    }

    @Override
    public void createTooltip(TooltipMakerAPI t, boolean expanded) {
        float opad = 10f;
        float pad = 3f;
        Color h = Misc.getHighlightColor();
        Color bad = Misc.getNegativeHighlightColor();
        Color good = Misc.getPositiveHighlightColor();
        CargoStackAPI stack = Global.getFactory().createCargoStack(CargoAPI.CargoItemType.SPECIAL,
                new SpecialItemData(getId(), getData()), null);
        //Description desc = Global.getSettings().getDescription(id, Type.RESOURCE);
        stack.getPlugin().createTooltip(t,false,null,null);
    }
    @Override
    public int performRaid(CargoAPI loot, Random random, float lootMult, TextPanelAPI text) {
        market.getMemory().removeAllRequired("$aotd_tier_4_bp_key");
        loot.addSpecial(new SpecialItemData(getId(), getData()), 1);
        int xpGained = (int) (90000);
        return xpGained;
    }
}
