package data.kaysaar.aotd.vok.campaign.econ.synergies.impl.synergies;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.BaseIndustrySynergy;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;

import static data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc.getIndustriesListed;

public class UAFFederallyMandatedPastries extends BaseIndustrySynergy {
    @Override
    public boolean canShowSynergyInUI(MarketAPI market) {
        return Global.getSettings().getIndustrySpec("uaf_bakery_branch").getNewPluginInstance(market).isAvailableToBuild();
    }

    @Override
    public boolean doesSynergyMetReq(MarketAPI market) {
        return IndustrySynergiesMisc.isIndustryFunctionalAndExistingIncludingUpgrades(market,"uaf_bakery_branch", Industries.MILITARYBASE);
    }

    @Override
    public String getSynergyName() {
        return "Federally Mandated Pastries";
    }

    @Override
    public void populateListForSynergies(HashSet<String> industries, MarketAPI market) {
        industries.addAll(IndustrySynergiesMisc.getIdsOfTreeFromIndustry("uaf_bakery_branch"));
        industries.addAll(IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.MILITARYBASE));
    }

    @Override
    public void printReqImpl(TooltipMakerAPI tooltip, MarketAPI market, Color base, Color highLight) {
        ArrayList<String> ids = IndustrySynergiesMisc.getIdsOfTreeFromIndustry("uaf_bakery_branch");
        ArrayList<String> fuelIds = IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.MILITARYBASE);
        tooltip.addPara("%s and %s are required to be functional", 3f, base, highLight,
                getIndustriesListed(ids,market),
                getIndustriesListed(fuelIds,market));
    }

    @Override
    public void printEffectsImpl(TooltipMakerAPI tooltip, Color base, Color highLight, float efficiency, MarketAPI market) {
        tooltip.addPara("Increase planet defence by %s",3f,base,highLight, Misc.getRoundedValueMaxOneAfterDecimal((1+(0.5f*efficiency))));
    }

    @Override
    public void apply(float efficiencyPercent, MarketAPI market) {
        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).modifyFlat(getIdForEffects(),1+(0.5f*efficiencyPercent),getSynergyName());
    }

    @Override
    public void unapply(MarketAPI market) {
        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodifyFlat(getIdForEffects());

    }

    @Override
    public Color getColorForWagons(String industry) {
        return new Color(57, 45, 243);
    }

    @Override
    public int getAmountOfWagonsForUI(String industry) {
        return 6;
    }
}
