package data.kaysaar.aotd.vok.campaign.econ.synergies.impl.synergies;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.BaseIndustrySynergy;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.awt.*;
import java.util.HashSet;

public class CoreToForge extends BaseIndustrySynergy {
    @Override
    public String getIdForEffects() {
        return "ore_to_core";
    }

    @Override
    public boolean canShowSynergyInUI(MarketAPI market) {
        return IndustrySynergiesMisc.didMarketMetTechCriteria(market, AoTDTechIds.NANOMETAL_FUSION_SYNTHESIS, AoTDTechIds.BASE_SHIP_HULL_ASSEMBLY);

    }

    @Override
    public boolean doesSynergyMetReq(MarketAPI market) {
        boolean tech = IndustrySynergiesMisc.didMarketMetTechCriteria(market, AoTDTechIds.NANOMETAL_FUSION_SYNTHESIS, AoTDTechIds.BASE_SHIP_HULL_ASSEMBLY);
        return tech && IndustrySynergiesMisc.isAtLeastOneIndustryFunctionalFromListIncludingUpgrades(market, Industries.REFINING) && IndustrySynergiesMisc.isAtLeastOneIndustryFunctionalFromListIncludingUpgrades(market,Industries.HEAVYINDUSTRY);
    }

    @Override
    public String getSynergyName() {
        return "Core-To-Forge";
    }

    @Override
    public void populateListForSynergies(HashSet<String> industries, MarketAPI market) {
        industries.addAll( IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.REFINING));
        industries.addAll( IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.HEAVYINDUSTRY));
    }

    @Override
    public void printEffectsImpl(TooltipMakerAPI tooltip, Color base, Color highLight, float efficiency, MarketAPI market) {
        int baseValue  = (int) (1*efficiency);
        float effi = 0.15f*efficiency;
        tooltip.addPara("Increase production of %s by %s",3f,base,highLight,IndustrySynergiesMisc.getIndustriesListed(IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.HEAVYINDUSTRY),market),baseValue+"");
        tooltip.addPara("Increase ship quality by %s",3f,base,highLight, AoTDMisc.getPercentageString(effi));

    }

    @Override
    public void printReqImpl(TooltipMakerAPI tooltip, MarketAPI market, Color base, Color highLight) {
        tooltip.addPara("%s and %s are required to be functional! ", 3f, base, highLight,
                "Refining or one of it's upgrades",
                "Heavy Industry or one of it's upgrades");
    }

    @Override
    public void apply(float efficiencyPercent, MarketAPI market) {
        for (String id : IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.HEAVYINDUSTRY)) {
            if(market.hasIndustry(id)) {
                market.getIndustry(id).getSupplyBonusFromOther().modifyFlat(getIdForEffects(),1,"Core To Forge");
                market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).modifyFlat(getIdForEffects(), 0.15f*efficiencyPercent, "Core To Forge");
            }
        }

    }


    @Override
    public void unapply(MarketAPI market) {
        for (String id : IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.HEAVYINDUSTRY)) {
            if(market.hasIndustry(id)) {
                market.getIndustry(id).getSupplyBonusFromOther().unmodifyFlat(getIdForEffects());
            }
        }
        market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).unmodifyFlat(getIdForEffects());


    }
    @Override
    public Color getColorForWagons(String industry) {
        return new Color(85, 0, 0,255);
    }

    @Override
    public int getAmountOfWagonsForUI(String industry) {
        return 6;
    }
}
