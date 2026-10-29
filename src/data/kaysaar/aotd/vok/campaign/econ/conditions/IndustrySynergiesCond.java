package data.kaysaar.aotd.vok.campaign.econ.conditions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.util.IntervalUtil;
import data.kaysaar.aotd.vok.campaign.econ.industry.MiscHiddenIndustry;
import data.kaysaar.aotd.vok.campaign.econ.synergies.impl.sources.*;
import data.kaysaar.aotd.vok.campaign.econ.synergies.impl.synergies.*;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.BaseIndustrySynergy;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.IndustrySynergiesManager;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.IndustrySynergySourceAPI;

import java.util.Set;

;

public class IndustrySynergiesCond extends BaseMarketConditionPlugin {
    private static final float SYNERGY_ADVANCE_INTERVAL_DAYS = 0.05f;
    private static final Set<Class<?>> EFFICIENCY_NEUTRAL_SYNERGIES = Set.of(AgroTourism.class, ArgentEnergyProcessing.class,
            CommissionedMaterials.class, CoreToForge.class, DeepSeaScan.class, DefensiveNetwork.class, DrumsInTheDeep.class,
            EmergencyMeasures.class, FacilitatedResearchLogistics.class, FishingResort.class, LockheedInitiative.class,
            OreToCore.class, PlausibleDeniability.class, RapidTransportation.class, RockAndStone.class, SyndicateLine.class,
            UAFAddictivePastries.class, UAFFederallyMandatedPastries.class, UAFOutsourcedDesserts.class, VolatileLine.class);
    private static final Set<Class<?>> KNOWN_SYNERGY_SOURCES = Set.of(SpaceportSource.class, WaystationSource.class,
            MaglevSource.class, HypercognitionTestSynergy.class, ColonyDevelopmentApplier.class);

    IntervalUtil util = new IntervalUtil(Global.getSector().getClock().getSecondsPerDay(), Global.getSector().getClock().getSecondsPerDay());
    private transient float pendingSynergyAmount;
    private transient float nextSynergyAdvance;
    private transient boolean synergyAdvanceStarted;

    public void apply(String id) {
        super.apply(id);
        MiscHiddenIndustry.getInstance(market);
        applyEffects();
    }

    public void applyEffects() {
        IndustrySynergiesManager manager = IndustrySynergiesManager.getInstance();
        float efficiency = 0f;
        boolean efficiencyCurrent = false;
        for (BaseIndustrySynergy synergy : manager.getSynergyScriptsValidForMarket(market)) {
            if (!efficiencyCurrent) {
                efficiency = manager.calculateEfficiency(market);
                efficiencyCurrent = onlyKnownSynergySources(manager);
            }
            synergy.apply(efficiency, market);
            efficiencyCurrent &= EFFICIENCY_NEUTRAL_SYNERGIES.contains(synergy.getClass());
        }
    }

    private static boolean onlyKnownSynergySources(IndustrySynergiesManager manager) {
        for (IndustrySynergySourceAPI source : manager.getSourcesOfSynergy().values()) {
            if (!KNOWN_SYNERGY_SOURCES.contains(source.getClass())) return false;
        }
        return true;
    }

    public void applySynergyEffect(BaseIndustrySynergy synergy) {
        synergy.apply(IndustrySynergiesManager.getInstance().calculateEfficiency(market), market);
    }

    @Override
    public void unapply(String id) {
        super.unapply(id);
        IndustrySynergiesManager.getInstance().getSynergyScripts().forEach(x -> x.unapply(market));
    }
    public static IndustrySynergiesCond getInstance(MarketAPI market) {
        if(!market.hasCondition("aotd_industry_synergies")){
            market.addCondition("aotd_industry_synergies");
        }
        return (IndustrySynergiesCond) market.getCondition("aotd_industry_synergies").getPlugin();
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        if (util == null) {
            util = new IntervalUtil(Global.getSector().getClock().getSecondsPerDay(), Global.getSector().getClock().getSecondsPerDay());
        }

        pendingSynergyAmount += amount;
        util.advance(amount);
        final boolean dayEnded = util.intervalElapsed();
        if (dayEnded || pendingSynergyAmount >= nextSynergyAdvance) {
            final float advanced = pendingSynergyAmount;
            pendingSynergyAmount = 0f;
            final float interval = SYNERGY_ADVANCE_INTERVAL_DAYS * Global.getSector().getClock().getSecondsPerDay();
            nextSynergyAdvance = synergyAdvanceStarted ? interval : interval * (0.25f + 0.75f * (float) Math.random());
            synergyAdvanceStarted = true;
            IndustrySynergiesManager.getInstance().getSynergyScripts().forEach(x -> x.advance(market, advanced, false));
        }
        if (dayEnded) {
            IndustrySynergiesManager.getInstance().getSynergyScripts().forEach(x -> x.endOfTheDay(market));
        }

    }


    public String getModId() {
        return condition.getId();
    }

    @Override
    public boolean showIcon() {
        return false;

    }

    public static void applyRessourceCond(MarketAPI marketAPI) {
        if (marketAPI.isInEconomy() && !marketAPI.hasCondition("aotd_industry_synergies")) {
            marketAPI.addCondition("aotd_industry_synergies");
        }
    }
}
