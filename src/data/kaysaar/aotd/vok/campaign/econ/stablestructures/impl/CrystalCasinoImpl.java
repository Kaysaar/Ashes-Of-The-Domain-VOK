package data.kaysaar.aotd.vok.campaign.econ.stablestructures.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.tot.stablestructures.AoTDStableStructureImpl;
import data.kaysaar.aotd.tot.ui.commoditypanel.AoTDCommodityShortPanelCombined;
import data.kaysaar.aotd.tot.stablestructures.AoTDStableStructureSpecManager;
import data.kaysaar.aotd.tot.ui.starsystems.stuctures.map.StructureLocationPicker;
import data.kaysaar.aotd.vok.campaign.econ.industry.CrystalCasinoPlanetaryIndustry;
import data.kaysaar.aotd.vok.campaign.econ.stablestructures.ui.CrystalCasinoPlanetPicker;
import org.lwjgl.util.vector.Vector2f;

import java.util.LinkedHashMap;

public class CrystalCasinoImpl extends AoTDStableStructureImpl {
    public static final String CASINO_ID = "aotd_crystal_casino";
    public static final String PLANETARY_INDUSTRY_ID = "aotd_crystal_casino_estate";

    protected PlanetAPI targetPlanet;

    @Override
    public void setCurrentlyAssignedToken(SectorEntityToken token) {
        super.setCurrentlyAssignedToken(token);
        // Construction creates the scaffold in orbit before assigning this implementation.
        // The finished entity inherits the same orbit; retain the planet through the handoff.
        if (token.getOrbitFocus() instanceof PlanetAPI planet) targetPlanet = planet;
    }

    public PlanetAPI getTargetPlanet() {
        return targetPlanet;
    }

    public MarketAPI getTargetMarket() {
        SectorEntityToken entity = getCurrentlyTiedEntity();
        if (entity != null && CASINO_ID.equals(entity.getCustomEntityType()) && entity.getMarket() != null) {
            return entity.getMarket();
        }
        return targetPlanet == null ? null : targetPlanet.getMarket();
    }

    public boolean isHostOperational() {
        SectorEntityToken entity = getCurrentlyTiedEntity();
        MarketAPI market = getTargetMarket();
        return entity != null && !entity.isExpired() && CASINO_ID.equals(entity.getCustomEntityType())
                && isCurrentImplementation() && !isBuilding() && isFunctional()
                && market != null && market.isInEconomy() && market.hasCondition(Conditions.HABITABLE)
                && market.getFaction() != null && entity.getFaction() != null
                && market.getFactionId().equals(entity.getFaction().getId())
                && market.getConnectedEntities().contains(entity);
    }

    public boolean hasAnotherCasino(FactionAPI faction) {
        if (faction == null) return false;
        for (StarSystemAPI system : Global.getSector().getStarSystems()) {
            for (SectorEntityToken token : system.getAllEntities()) {
                if (token == currentlyAssignedToken || token.isExpired() || token.getFaction() == null
                        || !faction.getId().equals(token.getFaction().getId())) continue;
                Object value = token.getMemoryWithoutUpdate().get(AoTDStableStructureSpecManager.memKeyInEntities);
                if (value instanceof CrystalCasinoImpl && CASINO_ID.equals(((CrystalCasinoImpl) value).id)) return true;
                if (CASINO_ID.equals(token.getCustomEntityType())) return true;
            }
        }
        return false;
    }

    public boolean canBuildOnPlanet(PlanetAPI planet) {
        if (planet == null || planet.isStar() || planet.isExpired() || planet.getStarSystem() == null) return false;
        MarketAPI market = planet.getMarket();
        if (market == null || !market.isInEconomy() || !market.hasCondition(Conditions.HABITABLE)
                || market.getFaction() == null || !market.getFaction().isPlayerFaction()) return false;
        return !hasAnotherCasino(market.getFaction());
    }

    @Override
    public boolean isAvailableToBuild(StarSystemAPI system, FactionAPI faction) {
        if (system == null || faction == null || !faction.isPlayerFaction() || hasAnotherCasino(faction)) return false;
        for (PlanetAPI planet : system.getPlanets()) {
            if (canBuildOnPlanet(planet)) return true;
        }
        return false;
    }

    @Override
    public String getUnavailableReason(StarSystemAPI system, FactionAPI faction) {
        if (faction == null || !faction.isPlayerFaction()) return "Requires a player-controlled star system.";
        if (hasAnotherCasino(faction)) return "Only one Crystal Casino may be constructed by your faction.";
        return "Requires a player-owned habitable world in this star system.";
    }

    @Override
    public StructureLocationPicker createLocationPicker(float width, float height, StarSystemAPI system) {
        return new CrystalCasinoPlanetPicker(width, height, system, this);
    }

    public float getOrbitRadius(PlanetAPI planet) {
        float radius = planet.getRadius() + 320f;
        for (PlanetAPI other : planet.getStarSystem().getPlanets()) {
            if (other.getOrbitFocus() == planet) {
                radius = Math.max(radius, other.getCircularOrbitRadius() + other.getRadius() + 100f);
            }
        }
        return radius;
    }

    public Vector2f getPlacementLocation(PlanetAPI planet) {
        float radius = getOrbitRadius(planet);
        float angle = (float) Math.toRadians(planet.getCircularOrbitAngle());
        return new Vector2f(planet.getLocation().x + (float) Math.cos(angle) * radius,
                planet.getLocation().y + (float) Math.sin(angle) * radius);
    }

    // The station is visual; the planetary industry is the sole owner of its effects and AI core.
    @Override
    public void apply() {
    }

    @Override
    public void unapply() {
    }

    @Override
    public boolean canMountAICores() {
        return false;
    }

    public CrystalCasinoPlanetaryIndustry getLinkedIndustry() {
        MarketAPI market = getTargetMarket();
        if (market == null) return null;
        Industry industry = market.getIndustry(PLANETARY_INDUSTRY_ID);
        return industry instanceof CrystalCasinoPlanetaryIndustry ? (CrystalCasinoPlanetaryIndustry) industry : null;
    }

    @Override
    protected void buildingFinished() {
        // AoTDStableStructureImpl.finishBuildingOrUpgrading() has already called
        // replaceEntity(this, this, built) before invoking this callback.
        SectorEntityToken entity = getCurrentlyTiedEntity();
        if (entity == null || entity.isExpired() || !CASINO_ID.equals(entity.getCustomEntityType())
                || !isCurrentImplementation() || isBuilding()) {
            Global.getLogger(CrystalCasinoImpl.class).error("Crystal Casino: construction did not yield a finished entity");
            return;
        }

        PlanetAPI planet = entity.getOrbitFocus() instanceof PlanetAPI ? (PlanetAPI) entity.getOrbitFocus() : targetPlanet;
        MarketAPI market = planet == null ? null : planet.getMarket();
        if (market == null || !market.isInEconomy()) {
            Global.getLogger(CrystalCasinoImpl.class).error("Crystal Casino: completed entity has no valid host market: " + entity.getId());
            return;
        }

        targetPlanet = planet;
        // Only the finished Casino is connected. Never register the scaffold.
        entity.setMarket(market);
        market.getConnectedEntities().add(entity);
        currentAICoreID = null;

        if (!market.hasIndustry(PLANETARY_INDUSTRY_ID)) market.addIndustry(PLANETARY_INDUSTRY_ID);
        CrystalCasinoPlanetaryIndustry industry = getLinkedIndustry();
        if (industry == null) {
            Global.getLogger(CrystalCasinoImpl.class).error("Crystal Casino: linked industry was not registered on " + market.getId());
            return;
        }
        industry.reapply();
        Global.getLogger(CrystalCasinoImpl.class).info(
                "Crystal Casino registered finished entity " + entity.getId() + " on " + market.getId());
    }

    @Override
    public void notifyRemoved() {
        SectorEntityToken entity = getCurrentlyTiedEntity();
        MarketAPI market = getTargetMarket();
        if (market != null && entity != null && CASINO_ID.equals(entity.getCustomEntityType())) {
            // Detach the exact finished entity before industry unapply/removal.
            market.getConnectedEntities().remove(entity);
            if (entity.getMarket() == market) entity.setMarket(null);
            if (market.hasIndustry(PLANETARY_INDUSTRY_ID)) {
                market.removeIndustry(PLANETARY_INDUSTRY_ID, null, false);
            }
        }
        super.notifyRemoved();
    }

    protected void addCommodityDemand(TooltipMakerAPI tooltip, CrystalCasinoPlanetaryIndustry industry, boolean constructed) {
        float width = Math.max(100f, Math.min(420f, tooltip.getWidthSoFar() - 10f));
        if (constructed && industry != null) {
            tooltip.addCustom(new AoTDCommodityShortPanelCombined(width, 2, industry, true, false).getMainPanel(), 3f);
            return;
        }

        LinkedHashMap<String, Integer> demand = new LinkedHashMap<>();
        demand.put(Commodities.LUXURY_GOODS, CrystalCasinoPlanetaryIndustry.MONTHLY_LUXURY_GOODS);
        demand.put(Commodities.DRUGS, CrystalCasinoPlanetaryIndustry.MONTHLY_DRUGS);
        tooltip.addCustom(new AoTDCommodityShortPanelCombined(width, 2, demand).getMainPanel(), 3f);
    }

    @Override
    public void createTooltipForEffectSection(TooltipMakerAPI tooltip, AoTDStableStructureTooltipMode mode) {
        super.createTooltipForEffectSection(tooltip, mode);

        CrystalCasinoPlanetaryIndustry industry = getLinkedIndustry();
        boolean constructed = mode == AoTDStableStructureTooltipMode.PANEL && !isBuilding() && isCurrentImplementation();
        if (constructed && industry != null && industry.isCasinoOperational()) {
            tooltip.addPara("Monthly tourism income: %s", 5f, Misc.getHighlightColor(),
                    Misc.getDGSCredits(industry.getIncome().getModifiedInt()));
        } else {
            tooltip.addPara("Generates monthly tourism income through an industry on its host world.", 5f);
        }

        tooltip.addSectionHeading("Demand", Alignment.MID, 5f);
        addCommodityDemand(tooltip, industry, constructed);

        float effectiveness = industry == null ? 1f
                : industry.getCoreEffectiveness() * (constructed ? industry.getSupplyEffectiveness() : 1f);
        String accessibility = "+" + Misc.getRoundedValue(CrystalCasinoPlanetaryIndustry.ACCESSIBILITY_BONUS * effectiveness * 100f) + "%";
        String growth = "+" + Misc.getRoundedValue(CrystalCasinoPlanetaryIndustry.POPULATION_GROWTH_BONUS * effectiveness) + "%";
        tooltip.addPara("Grants %s accessibility and %s population growth to same-faction colonies in the system.",
                3f, Misc.getHighlightColor(), accessibility, growth);
        tooltip.addPara("Luxury penthouses allow important contacts to be invited to reside aboard the Casino.", 3f);
    }
}
