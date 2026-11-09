package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.campaign.econ.MarketImmigrationModifier;
import com.fs.starfarer.api.campaign.econ.Industry.AICoreDescriptionMode;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.population.PopulationComposition;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.tot.industries.AoTDBaseIndustry;
import data.kaysaar.aotd.tot.plugins.AoTDBaseDemSupCalc;
import data.kaysaar.aotd.tot.plugins.AoTDCommodityEconSpecManager;
import com.fs.starfarer.api.combat.MutableStat;
import data.kaysaar.aotd.tot.stablestructures.AoTDStableStructureSpecManager;
import data.kaysaar.aotd.vok.campaign.econ.stablestructures.impl.CrystalCasinoImpl;


public class CrystalCasinoPlanetaryIndustry extends AoTDBaseIndustry implements MarketImmigrationModifier {
    public static final float ACCESSIBILITY_BONUS = 0.05f;
    public static final float POPULATION_GROWTH_BONUS = 10f;
    public static final int MONTHLY_LUXURY_GOODS = 2800;
    public static final int MONTHLY_DRUGS = 1100;
    public static final float INCOME_PER_VISITOR_WEIGHT = 180f;
    public static final int BASE_MONTHLY_TOURISM = 6000;
    public static final int MAX_BASE_TOURISM = 180000;
    public static final String TOURISM_INCOME_MOD = "aotd_crystal_casino_tourism";
    public static final String INCOME_MULT_MOD = "aotd_crystal_casino_market_income_mult";

    // The market's connected entity set is the only source of the Casino link.
    // This deliberately does not cache the implementation or the construction scaffold.
    public SectorEntityToken getCasinoEntity() {
        if (market == null) return null;
        for (SectorEntityToken entity : market.getConnectedEntities()) {
            if (entity != null && !entity.isExpired()
                    && CrystalCasinoImpl.CASINO_ID.equals(entity.getCustomEntityType())) return entity;
        }
        return null;
    }

    public SectorEntityToken getCasinoToken() {
        return getCasinoEntity();
    }

    public CrystalCasinoImpl getCasino() {
        SectorEntityToken entity = getCasinoEntity();
        if (entity == null) return null;
        Object value = entity.getMemoryWithoutUpdate().get(AoTDStableStructureSpecManager.memKeyInEntities);
        return value instanceof CrystalCasinoImpl ? (CrystalCasinoImpl) value : null;
    }

    public boolean isCasinoOperational() {
        SectorEntityToken entity = getCasinoEntity();
        return entity != null && market != null && market.isInEconomy() && isFunctional()
                && market.hasCondition(Conditions.HABITABLE)
                && entity.getFaction() != null && entity.getFaction().getId().equals(market.getFactionId());
    }

    public float getCoreEffectiveness() {
        String core = getAICoreId();
        if (Commodities.GAMMA_CORE.equals(core)) return 1.15f;
        if (Commodities.BETA_CORE.equals(core)) return 1.30f;
        if (Commodities.ALPHA_CORE.equals(core)) return 1.50f;
        return 1f;
    }

    // Use normal industry demand. ToT converts demand levels into cargo units, so
    // convert the intended monthly cargo requirement back into vanilla demand levels.
    public int getDemandUnitsForCargo(String commodityId, int cargoAmount) {
        if (market == null || cargoAmount <= 0) return 0;
        AoTDBaseDemSupCalc calculator = AoTDCommodityEconSpecManager.getEconSpec(commodityId).getCalculationScript();
        int demandUnits = Math.max(1, calculator.convertRawUnitsToDemand(cargoAmount, market, commodityId));
        int representedCargo = calculator.getRawUnitsFromDemand(new MutableStat(demandUnits), market, commodityId, this);
        if (representedCargo < cargoAmount) demandUnits++;
        return demandUnits;
    }

    public int getLuxuryGoodsDemand() {
        return getDemandUnitsForCargo(Commodities.LUXURY_GOODS, MONTHLY_LUXURY_GOODS);
    }

    public int getDrugsDemand() {
        return getDemandUnitsForCargo(Commodities.DRUGS, MONTHLY_DRUGS);
    }

    public float getCommodityFulfillment(String commodityId) {
        if (!isCasinoOperational() || getDemand(commodityId).getQuantity().getModifiedInt() <= 0) return 0f;
        return Math.max(0f, Math.min(1f, getDeficitMult(commodityId)));
    }

    public float getLuxuryGoodsFulfillment() {
        return getCommodityFulfillment(Commodities.LUXURY_GOODS);
    }

    public float getDrugsFulfillment() {
        return getCommodityFulfillment(Commodities.DRUGS);
    }

    // Luxury Goods drive the Casino's appeal; Drugs are an important secondary input.
    // Without either commodity, revenues and colony bonuses drop substantially.
    public float getSupplyEffectiveness() {
        float luxury = getLuxuryGoodsFulfillment();
        float drugs = getDrugsFulfillment();
        return (0.15f + 0.85f * luxury) * (0.55f + 0.45f * drugs);
    }

    @Override
    protected void applyIndustry() {
        boolean operational = isCasinoOperational();
        demand(Commodities.LUXURY_GOODS, operational ? getLuxuryGoodsDemand() : 0);
        demand(Commodities.DRUGS, operational ? getDrugsDemand() : 0);
    }

    public int getBaseTourismIncome() {
        if (!isCasinoOperational()) return 0;
        if (!market.hasSpaceport()) return 0;
        float hostAccessibility = Math.max(0f, Math.min(1.5f, market.getAccessibilityMod().computeEffective(0f)));
        if (hostAccessibility <= 0f) return 0;
        FactionAPI owner = market.getFaction();
        float visitorWeight = 0f;
        for (MarketAPI other : Global.getSector().getEconomy().getMarketsCopy()) {
            if (other.isHidden() || !other.isInEconomy() || other.getFaction() == null
                    || !other.hasSpaceport() || other.getSize() < 3) continue;
            float accessibility = Math.max(0f, Math.min(1.5f, other.getAccessibilityMod().computeEffective(0f)));
            if (accessibility <= 0f) continue;
            float relationFactor = 1f;
            if (!other.getFactionId().equals(owner.getId())) {
                float relation = owner.getRelationship(other.getFactionId());
                if (relation <= -0.5f) continue;
                relationFactor = Math.max(0.2f, Math.min(1.3f, 0.8f + relation * 0.6f));
            }
            visitorWeight += Math.max(0, other.getSize() - 2) * accessibility * relationFactor;
        }
        float hostMultiplier = 0.5f + hostAccessibility * 0.5f;
        return Math.min(MAX_BASE_TOURISM,
                Math.max(0, Math.round((BASE_MONTHLY_TOURISM + visitorWeight * INCOME_PER_VISITOR_WEIGHT) * hostMultiplier)));
    }

    public int getTourismIncome() {
        if (!isCasinoOperational()) return 0;
        return Math.max(0, Math.round(getBaseTourismIncome() * getSupplyEffectiveness() * getCoreEffectiveness()));
    }

    @Override
    protected void applyIncomeAndUpkeep(float sizeOverride) {
        super.applyIncomeAndUpkeep(sizeOverride);
        if (!isCasinoOperational()) {
            getIncome().unmodifyFlat(TOURISM_INCOME_MOD);
            getIncome().unmodifyMult(INCOME_MULT_MOD);
            return;
        }
        // BaseIndustry's standard income stat is picked up by the colony economy automatically.
        getIncome().modifyFlatAlways(TOURISM_INCOME_MOD, getTourismIncome(), "Crystal Casino tourism");
        getIncome().modifyMultAlways(INCOME_MULT_MOD, market.getIncomeMult().getModifiedValue(),
                "Colony income multiplier");
    }

    public String getEffectsModId() {
        return getId() + "_" + (market == null ? "unknown" : market.getId());
    }

    protected void clearSystemEffects(boolean removeHostImmigration) {
        String modId = getEffectsModId();
        for (MarketAPI other : Global.getSector().getEconomy().getMarketsCopy()) {
            other.getAccessibilityMod().unmodifyFlat(modId);
            if (removeHostImmigration || other != market) other.removeTransientImmigrationModifier(this);
        }
    }

    @Override
    protected void applyIndustryEffects() {
        clearSystemEffects(false);
        if (!isCasinoOperational()) return;
        SectorEntityToken casinoEntity = getCasinoEntity();
        String factionId = market.getFactionId();
        String modId = getEffectsModId();
        float effectiveness = getSupplyEffectiveness() * getCoreEffectiveness();
        for (MarketAPI other : Global.getSector().getEconomy().getMarketsCopy()) {
            if (other.getStarSystem() != casinoEntity.getStarSystem() || !factionId.equals(other.getFactionId())) continue;
            other.getAccessibilityMod().modifyFlat(modId, ACCESSIBILITY_BONUS * effectiveness,
                    "Crystal Casino tourism infrastructure");
            // BaseIndustry already registers the host market during apply().
            if (other != market) other.addTransientImmigrationModifier(this);
        }
    }

    @Override
    protected void unapplyIndustryEffects() {
        clearSystemEffects(true);
        getIncome().unmodifyFlat(TOURISM_INCOME_MOD);
        getIncome().unmodifyMult(INCOME_MULT_MOD);
    }

    @Override
    public void modifyIncoming(MarketAPI target, PopulationComposition incoming) {
        if (!isCasinoOperational() || target == null
                || target.getStarSystem() != market.getStarSystem()
                || !target.getFactionId().equals(market.getFactionId())) return;
        incoming.getWeight().modifyPercent(getEffectsModId(),
                POPULATION_GROWTH_BONUS * getCoreEffectiveness() * getSupplyEffectiveness(),
                "Crystal Casino residential appeal");
    }

    @Override
    public void setAICoreId(String coreId) {
        if (coreId != null && !Commodities.GAMMA_CORE.equals(coreId)
                && !Commodities.BETA_CORE.equals(coreId) && !Commodities.ALPHA_CORE.equals(coreId)) return;
        if (coreId == null ? getAICoreId() == null : coreId.equals(getAICoreId())) return;
        super.setAICoreId(coreId);
        if (market != null && market.getIndustry(getId()) == this) reapply();
    }

    @Override
    public boolean canInstallAICores() {
        return true;
    }

    @Override
    public boolean isAvailableToBuild() {
        return false;
    }

    @Override
    public boolean showWhenUnavailable() {
        return false;
    }

    @Override
    public boolean showShutDown() {
        return false;
    }

    @Override
    public boolean canShutDown() {
        return false;
    }

    @Override
    public String getCanNotShutDownReason() {
        return "This industry is permanently linked to the Crystal Casino.";
    }

    @Override
    public boolean canUpgrade() {
        return false;
    }

    @Override
    public boolean canDowngrade() {
        return false;
    }

    @Override
    public boolean canBeDisrupted() {
        return false;
    }

    @Override
    public boolean canImprove() {
        return false;
    }

    @Override
    protected void addGammaCoreDescription(TooltipMakerAPI tooltip, AICoreDescriptionMode mode) {
        tooltip.addPara("Increases Casino effects by %s.", 0f, Misc.getHighlightColor(), "+15%");
    }

    @Override
    protected void addBetaCoreDescription(TooltipMakerAPI tooltip, AICoreDescriptionMode mode) {
        tooltip.addPara("Increases Casino effects by %s.", 0f, Misc.getHighlightColor(), "+30%");
    }

    @Override
    protected void addAlphaCoreDescription(TooltipMakerAPI tooltip, AICoreDescriptionMode mode) {
        tooltip.addPara("Increases Casino effects by %s.", 0f, Misc.getHighlightColor(), "+50%");
    }

    @Override
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        super.addPostDemandSection(tooltip, hasDemand, mode);
        if (!isCasinoOperational()) {
            tooltip.addPara("Crystal Casino is not operational.", Misc.getNegativeHighlightColor(), 5f);
            return;
        }

        float effectiveness = getSupplyEffectiveness() * getCoreEffectiveness();
        tooltip.addPara("Tourism efficiency: %s (Luxury Goods and Drugs fulfillment).", 5f,
                Misc.getHighlightColor(), Misc.getRoundedValue(getSupplyEffectiveness() * 100f) + "%");
        tooltip.addPara("System-wide: %s accessibility, %s population growth.", 3f,
                Misc.getHighlightColor(),
                "+" + Misc.getRoundedValue(ACCESSIBILITY_BONUS * effectiveness * 100f) + "%",
                "+" + Misc.getRoundedValue(POPULATION_GROWTH_BONUS * effectiveness) + "%");
    }
}
