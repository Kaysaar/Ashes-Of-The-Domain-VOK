package data.kaysaar_aotd_vok.scripts.campaign.econ.conditions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.econ.impl.HeavyIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.util.Pair;
import data.Ids.AodCommodities;


public class IcDemmand  extends BaseMarketConditionPlugin {
    //Thans to SirHartley for providing ResourceCondition plugin, which i was inspired of
    public static String COMMODITY_COND = "IcDemmand";
    @Override
    public void apply(String id) {
        super.apply(id);
        applyElectronics();
    }
    @Override
    public void unapply(String id) {
        super.unapply(id);
        for (Industry ind : market.getIndustries()) {
            unapplyElectronicsDemand((BaseIndustry) ind);
        }

    }
    public void applyElectronics() {
        int size = market.getSize();

        for (Industry ind : market.getIndustries()) {
            if (ind instanceof HeavyIndustry) {
                int demand = size-3;
                applyCommodityDemandToIndustry((BaseIndustry) ind, demand);

            }

            if (!Global.getSettings().getModManager().isModEnabled("shadow_ships")) continue;

        }

    }
    public void unapplyElectronicsDemand(BaseIndustry ind) {
        ind.supply(AodCommodities.ELECTRONICS, 0, "");
        ind.demand(AodCommodities.ELECTRONICS, 0, "");
    }
    public void applyCommodityDemandToIndustry(BaseIndustry ind, int demand){
        if (demand == 0) return;

        ind.demand(AodCommodities.ELECTRONICS, demand);

        ind.getDemand(AodCommodities.ELECTRONICS).getQuantity().unmodify(getModId());
        if (ind.getSpecialItem() != null) {
            if (ind.getSpecialItem().getId().equals(Items.CORRUPTED_NANOFORGE)) {
                ind.getDemand(AodCommodities.ELECTRONICS).getQuantity().modifyFlat(getModId(), -1, "Corrupted Nanoforge");
            }

            if (ind.getSpecialItem().getId().equals(Items.PRISTINE_NANOFORGE)) {
                ind.getDemand(AodCommodities.ELECTRONICS).getQuantity().modifyFlat(getModId(), -2, "Pristine Nanoforge");
            }
        }

        Pair<String, Integer> deficit = ind.getMaxDeficit(AodCommodities.ELECTRONICS);
        int maxDeficit = market.getSize()-3; // missing ship parts do not affect the output much, they just reduce quality.
        if (deficit.two > maxDeficit) deficit.two = maxDeficit;

        applyDeficitToIndustry(ind, 4, deficit,
                Commodities.HAND_WEAPONS,Commodities.HEAVY_MACHINERY);
    }
    public void applyDeficitToIndustry(Industry ind, int index, Pair<String, Integer> deficit, String... commodities){
        for (String commodity : commodities) {
            if (!ind.getSupply(commodity).getQuantity().isUnmodified()) {
                ind.supply(String.valueOf(index), commodity, -(Integer) deficit.two, BaseIndustry.getDeficitText((String) deficit.one));
            }
        }
    }

    @Override
    public boolean showIcon() {
        return false;
    }

    public String getModId() {
        return condition.getId();
    }
    public static void applyRessourceCond(MarketAPI marketAPI) {
        if (marketAPI.isInEconomy() && !marketAPI.hasCondition(COMMODITY_COND)){
            marketAPI.addCondition(COMMODITY_COND);
        }
    }
}
