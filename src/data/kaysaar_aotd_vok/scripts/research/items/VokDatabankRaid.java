package data.kaysaar_aotd_vok.scripts.research.items;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.graid.BaseGroundRaidObjectivePluginImpl;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.MarketCMD;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.Random;

public class VokDatabankRaid extends BaseGroundRaidObjectivePluginImpl {
    protected String data;

    public VokDatabankRaid(MarketAPI market, String id,String data,Industry industry ) {
        super(market, id);
        this.data = data;
        this.id=id;
        setSource(industry);
    }

    @Override
    public void setSource(Industry source) {
        super.setSource(source);
        MarketCMD.RaidDangerLevel level = getDangerLevel();
        int marines = level.marineTokens;
        if (source != null) {
            marines = source.adjustMarineTokensToRaidItem(id, data, marines);
        }
        setMarinesRequired(marines);
    }

    public String getData() {
        return data;
    }
    protected float getItemPriceMult() {
        return Global.getSettings().getFloat("blueprintPriceOriginalItemMult");
    }

    public void setData(String data) {
        this.data = data;
    }

    public float getQuantity(int marines) {
        return 1;
    }

    public int getValue(int marines) {
        IndustrySpecAPI industrySpec = Global.getSettings().getIndustrySpec(data);
        return (int)( industrySpec.getCost() * getItemPriceMult());
    }

    public int getCargoSpaceNeeded() {
        return (int) getItemSpec().getCargoSpace();
    }

    public int getFuelSpaceNeeded() {
        return 0;
    }

    public int getProjectedCreditsValue() {
        return (int) getItemSpec().getBasePrice();
    }

    public int getDeficitCaused() {
        return 0;
    }

    public SpecialItemSpecAPI getItemSpec() {
        return Global.getSettings().getSpecialItemSpec(id);
    }

    public MarketCMD.RaidDangerLevel getDangerLevel() {
        MarketCMD.RaidDangerLevel level = getItemSpec().getBaseDanger();
        if (source != null) {
            level = source.adjustItemDangerLevel(id, null, level);
        }
        return level;
    }

    public float getQuantitySortValue() {
        SpecialItemSpecAPI spec = getItemSpec();
        float add = 0;
        if (spec != null) {
            add = spec.getOrder();
        }
        return QUANTITY_SORT_TIER_2 + add + 1000;
    }

    public String getName() {
        IndustrySpecAPI specAPI = Global.getSettings().getIndustrySpec(data);
        return specAPI.getName()+" VOK Databank";
    }

    public CargoStackAPI getStackForIcon() {
        CargoStackAPI stack = Global.getFactory().createCargoStack(CargoAPI.CargoItemType.SPECIAL,
                new SpecialItemData(id, data),null);
        return stack;
    }

    public int performRaid(CargoAPI loot, Random random, float lootMult, TextPanelAPI text) {
        if (marinesAssigned <= 0) return 0;

        if (source != null) {
            source.setSpecialItem(null);
        }
        loot.addSpecial(new SpecialItemData(id, data), 1);
        market.getMemoryWithoutUpdate().unset("$aotd_vok_databank");

        int xpGained = (int) (1 * getItemSpec().getBasePrice() * XP_GAIN_VALUE_MULT);
        return xpGained;
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

        //Description desc = Global.getSettings().getDescription(id, Type.RESOURCE);

        t.addPara(getItemSpec().getDescFirstPara(), 0f);

        t.addPara("Base value: %s per unit", opad, h, Misc.getDGSCredits(getValue(0)));
    }
}
