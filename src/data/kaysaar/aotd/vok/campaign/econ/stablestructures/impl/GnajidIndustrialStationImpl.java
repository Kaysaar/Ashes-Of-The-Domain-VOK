package data.kaysaar.aotd.vok.campaign.econ.stablestructures.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.Industry.AICoreDescriptionMode;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.tot.stablestructures.AoTDStableStructureAdvanceScript;
import data.kaysaar.aotd.tot.stablestructures.AoTDStableStructureImpl;
import data.kaysaar.aotd.tot.stablestructures.AoTDStableStructureSpecManager;
import data.kaysaar.aotd.tot.ui.starsystems.stuctures.map.ChooseOrbitForBuildingMap;
import data.kaysaar.aotd.tot.ui.starsystems.stuctures.map.StructureLocationPicker;
import data.kaysaar.aotd.vok.campaign.econ.conditions.GnajidAICCondition;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.scripts.BifrostMegastructureManager;
import data.kaysaar.aotd.vok.campaign.econ.stablestructures.ui.GnajidAICOptionProvider;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GnajidIndustrialStationImpl extends AoTDStableStructureImpl {
    public static final String STATION_ID = "aotd_gnajid_industrial_station";
    public static final String GLOBAL_OWNER_KEY = "$aotd_gnajid_industrial_station_owner";
    public static final int DEPLOY_COOLDOWN_DAYS = 90;
    public static final float REPAIR_RANGE = 2500f;
    public static final float HULL_REPAIR_PER_DAY = 0.02f;
    public static final float ARMOR_REPAIR_PER_DAY = 0.01f;
    public static final float CR_REPAIR_PER_DAY = 0.01f;

    public static final int GAMMA_DEPLOY_COOLDOWN_DAYS = 80;
    public static final int BETA_DEPLOY_COOLDOWN_DAYS = 70;
    public static final int ALPHA_DEPLOY_COOLDOWN_DAYS = 60;

    public static final float GAMMA_REPAIR_RANGE_MULT = 1.2f;
    public static final float BETA_REPAIR_RANGE_MULT = 1.5f;
    public static final float ALPHA_REPAIR_RANGE_MULT = 2f;

    public static final float GAMMA_REPAIR_SPEED_MULT = 1.25f;
    public static final float BETA_REPAIR_SPEED_MULT = 1.5f;
    public static final float ALPHA_REPAIR_SPEED_MULT = 2f;

    private static final String NO_STATION = "none";
    protected float deploymentCooldownDays;

    public static SectorEntityToken findExistingStation() {
        if (Global.getSector() == null) return null;

        Object stored = Global.getSector().getPersistentData().get(GLOBAL_OWNER_KEY);
        if (stored instanceof SectorEntityToken token && isExistingStation(token)) return token;
        if (NO_STATION.equals(stored)) return null;

        // Rebuild a missing/stale reservation when loading an older save.
        for (StarSystemAPI system : AoTDStableStructureAdvanceScript.getColonizedSystems()) {
            for (AoTDStableStructureImpl structure : AoTDStableStructureAdvanceScript.getStructures(system)) {
                if (!(structure instanceof GnajidIndustrialStationImpl)) continue;
                if (structure.isCurrentImplementation()) {
                    SectorEntityToken token = structure.getCurrentlyTiedEntity();
                    Global.getSector().getPersistentData().put(GLOBAL_OWNER_KEY, token);
                    return token;
                }
            }
        }

        Global.getSector().getPersistentData().put(GLOBAL_OWNER_KEY, NO_STATION);
        return null;
    }

    private static boolean isExistingStation(SectorEntityToken token) {
        if (token == null || token.isExpired() || token.getMemoryWithoutUpdate() == null) return false;
        Object value = token.getMemoryWithoutUpdate().get(AoTDStableStructureSpecManager.memKeyInEntities);
        return value instanceof GnajidIndustrialStationImpl && ((GnajidIndustrialStationImpl) value).isCurrentImplementation();
    }

    public static void registerOptions() {
        if (Global.getSector() == null) return;
        if (!Global.getSector().getListenerManager().hasListenerOfClass(GnajidAICOptionProvider.class)) {
            Global.getSector().getListenerManager().addListener(new GnajidAICOptionProvider(), true);
        }
    }

    @Override
    public void setCurrentlyAssignedToken(SectorEntityToken token) {
        super.setCurrentlyAssignedToken(token);
        Global.getSector().getPersistentData().put(GLOBAL_OWNER_KEY, token);
        registerOptions();
    }

    @Override
    public boolean isAvailableToBuild(StarSystemAPI system, FactionAPI faction) {
        if (system == null || faction == null || !faction.isPlayerFaction()) return false;
        SectorEntityToken existing = findExistingStation();
        return existing == null || existing == currentlyAssignedToken;
    }

    @Override
    public String getUnavailableReason(StarSystemAPI system, FactionAPI faction) {
        if (faction == null || !faction.isPlayerFaction()) return "Requires a player-controlled star system.";
        if (findExistingStation() != null) return "Only one Gnajid Industrial Station may exist at a time.";
        return super.getUnavailableReason(system, faction);
    }

    @Override
    public StructureLocationPicker createLocationPicker(
            float width, float height, StarSystemAPI system) {

        ChooseOrbitForBuildingMap picker =
                new ChooseOrbitForBuildingMap(width, height, system, id);
        picker.getOrbitComponent().setSpriteAngleOffsetDeg(90f);
        return picker;
    }

    @Override
    public boolean canMountAICores() {
        return true;
    }

    public int getDeploymentCooldownDays(String coreId) {
        if (Commodities.GAMMA_CORE.equals(coreId)) return GAMMA_DEPLOY_COOLDOWN_DAYS;
        if (Commodities.BETA_CORE.equals(coreId)) return BETA_DEPLOY_COOLDOWN_DAYS;
        if (Commodities.ALPHA_CORE.equals(coreId)) return ALPHA_DEPLOY_COOLDOWN_DAYS;
        return DEPLOY_COOLDOWN_DAYS;
    }

    public int getDeploymentCooldownDays() {
        return getDeploymentCooldownDays(getAICoreId());
    }

    public float getRepairRangeMult(String coreId) {
        if (Commodities.GAMMA_CORE.equals(coreId)) return GAMMA_REPAIR_RANGE_MULT;
        if (Commodities.BETA_CORE.equals(coreId)) return BETA_REPAIR_RANGE_MULT;
        if (Commodities.ALPHA_CORE.equals(coreId)) return ALPHA_REPAIR_RANGE_MULT;
        return 1f;
    }

    public float getRepairSpeedMult(String coreId) {
        if (Commodities.GAMMA_CORE.equals(coreId)) return GAMMA_REPAIR_SPEED_MULT;
        if (Commodities.BETA_CORE.equals(coreId)) return BETA_REPAIR_SPEED_MULT;
        if (Commodities.ALPHA_CORE.equals(coreId)) return ALPHA_REPAIR_SPEED_MULT;
        return 1f;
    }

    public float getRepairRange() {
        return REPAIR_RANGE * getRepairRangeMult(getAICoreId());
    }

    public float getHullRepairPerDay() {
        return HULL_REPAIR_PER_DAY * getRepairSpeedMult(getAICoreId());
    }

    public float getArmorRepairPerDay() {
        return ARMOR_REPAIR_PER_DAY * getRepairSpeedMult(getAICoreId());
    }

    public float getCRRepairPerDay() {
        return CR_REPAIR_PER_DAY * getRepairSpeedMult(getAICoreId());
    }

    @Override
    public void setAICoreId(String coreId) {
        int previousCooldown = getDeploymentCooldownDays();
        super.setAICoreId(coreId);
        if (deploymentCooldownDays > 0f) {
            // Preserve time already served when the assigned core is changed.
            deploymentCooldownDays = Math.max(0f, deploymentCooldownDays + getDeploymentCooldownDays() - previousCooldown);
        }
    }

    public int getCooldownDaysRemaining() {
        return Math.max(0, (int) Math.ceil(deploymentCooldownDays));
    }

    public boolean canDeployAIC() {
        return isCurrentImplementation() && isPlayerOwned() && isFunctional() && !isBuilding()
                && deploymentCooldownDays <= 0f;
    }

    public boolean canReachMarket(MarketAPI market) {
        if (market == null || market.getStarSystem() == null || currentlyAssignedToken == null) return false;
        if (!market.isInEconomy() || market.getFaction() == null || !market.getFaction().isPlayerFaction()) return false;
        StarSystemAPI from = currentlyAssignedToken.getStarSystem();
        StarSystemAPI to = market.getStarSystem();
        if (from == null) return false;
        if (from == to) return true;
        return BifrostMegastructureManager.getInstance().getMegastructure().areStarSystemsConnected(from, to);
    }

    public boolean canDeployTo(MarketAPI market) {
        return canDeployAIC() && canReachMarket(market) && !market.hasCondition(GnajidAICCondition.CONDITION_ID);
    }

    public List<MarketAPI> getEligibleMarkets() {
        List<MarketAPI> markets = new ArrayList<>();
        if (Global.getSector() == null || currentlyAssignedToken == null) return markets;
        for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
            if (canReachMarket(market) && !market.hasCondition(GnajidAICCondition.CONDITION_ID)) {
                markets.add(market);
            }
        }
        markets.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        return markets;
    }

    public boolean deployAIC(MarketAPI market) {
        if (!canDeployTo(market)) return false;
        market.addCondition(GnajidAICCondition.CONDITION_ID);
        deploymentCooldownDays = getDeploymentCooldownDays();
        if (currentlyAssignedToken != null && currentlyAssignedToken.getStarSystem() != null) {
            Global.getSector().getCampaignUI().addMessage("Advanced Industrial Controller deployed to " + market.getName(),
                    Misc.getPositiveHighlightColor());
        }
        return true;
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        if (Global.getSector() == null || Global.getSector().isPaused() || amount <= 0f || !isCurrentImplementation()) return;
        registerOptions();
        float days = Global.getSector().getClock().convertToDays(amount);
        if (days <= 0f) return;
        deploymentCooldownDays = Math.max(0f, deploymentCooldownDays - days);
        if (isFunctional() && !isBuilding() && isPlayerOwned()) repairNearbyPlayerFleet(days);
    }

    private void repairNearbyPlayerFleet(float days) {
        CampaignFleetAPI player = Global.getSector().getPlayerFleet();
        if (player == null || currentlyAssignedToken == null || player.getContainingLocation() != currentlyAssignedToken.getContainingLocation()) return;
        if (Misc.getDistance(player.getLocation(), currentlyAssignedToken.getLocation()) > getRepairRange()) return;

        boolean repaired = false;
        for (FleetMemberAPI member : player.getFleetData().getMembersListCopy()) {
            if (!member.canBeRepaired() || member.isMothballed()) continue;
            if (member.getStatus().getHullFraction() < 1f) {
                member.getStatus().setHullFraction(Math.min(1f,
                        member.getStatus().getHullFraction() + getHullRepairPerDay() * days));
                repaired = true;
            }
            if (member.getStatus().getArmorDamageTaken() > 0f) {
                member.getStatus().repairArmorAllCells(getArmorRepairPerDay() * days);
                repaired = true;
            }
            float cr = member.getRepairTracker().getCR();
            float maxCR = member.getRepairTracker().getMaxCR();
            if (cr < maxCR) {
                member.getRepairTracker().setCR(Math.min(maxCR, cr + getCRRepairPerDay() * days));
                repaired = true;
            }
        }
        if (repaired) player.getFleetData().setSyncNeeded();
    }

    private void clearStationReservation() {
        if (Global.getSector() == null) return;
        if (Global.getSector().getPersistentData().get(GLOBAL_OWNER_KEY) == currentlyAssignedToken) {
            Global.getSector().getPersistentData().put(GLOBAL_OWNER_KEY, NO_STATION);
        }
    }

    @Override
    public void cancelBuilding() {
        super.cancelBuilding();
        clearStationReservation();
    }

    @Override
    public void notifyRemoved() {
        clearStationReservation();
        super.notifyRemoved();
    }

    private void addCoreRepairDescription(TooltipMakerAPI tooltip, AICoreDescriptionMode mode,
                                          String coreName, String rangeBonus, String repairBonus, String coreId) {
        tooltip.addPara(
                getAICoreDescriptionPrefix(coreName, mode)
                        + "Extends repair range by %s to %s campaign units, increases hull, armor and CR repair rates by %s, "
                        + "and reduces AIC deployment cooldown to %s days.",
                0f, Misc.getHighlightColor(),
                rangeBonus,
                Misc.getWithDGS(Math.round(REPAIR_RANGE * getRepairRangeMult(coreId))),
                repairBonus,
                String.valueOf(getDeploymentCooldownDays(coreId))
        );
    }

    @Override
    public void addGammaCoreDescription(TooltipMakerAPI tooltip, AICoreDescriptionMode mode) {
        addCoreRepairDescription(tooltip, mode, "Gamma-level AI core", "+20%", "+25%", Commodities.GAMMA_CORE);
    }

    @Override
    public void addBetaCoreDescription(TooltipMakerAPI tooltip, AICoreDescriptionMode mode) {
        addCoreRepairDescription(tooltip, mode, "Beta-level AI core", "+50%", "+50%", Commodities.BETA_CORE);
    }

    @Override
    public void addAlphaCoreDescription(TooltipMakerAPI tooltip, AICoreDescriptionMode mode) {
        addCoreRepairDescription(tooltip, mode, "Alpha-level AI core", "+100%", "+100%", Commodities.ALPHA_CORE);
    }

    private String formatRepairPercent(float amount) {
        return String.format(Locale.ROOT, "%.2f%%", amount * 100f);
    }

    @Override
    public void createTooltipForEffectSection(TooltipMakerAPI tooltip, AoTDStableStructureTooltipMode mode) {
        super.createTooltipForEffectSection(tooltip, mode);
        tooltip.addPara("Operates a Domain-era industrial coordination center capable of deploying %s to player colonies.",
                3f, Misc.getHighlightColor(), "Advanced Industrial Controllers (AICs)");
        tooltip.addPara("Each controller provides %s industry construction speed, %s industry synergy efficiency, and %s commodity demand.",
                5f, Misc.getHighlightColor(), "+20%", "+50%", "-10%");
        tooltip.addPara("Deploy one controller every %s days. Targets can be in this star system, or in any other star system linked to it through an active Bifrost network.",
                5f, Misc.getHighlightColor(), String.valueOf(getDeploymentCooldownDays()));
        tooltip.addPara("Station repair docks restore %s hull, %s armor, and %s combat readiness per day to the nearby player fleet, within %s campaign units.",
                5f, Misc.getHighlightColor(), formatRepairPercent(getHullRepairPerDay()),
                formatRepairPercent(getArmorRepairPerDay()), formatRepairPercent(getCRRepairPerDay()),
                Misc.getWithDGS(Math.round(getRepairRange())));

        if (mode != AoTDStableStructureTooltipMode.PANEL || !isCurrentImplementation()) return;
        if (isBuilding()) {
            tooltip.addPara("Deployment and fleet repairs become available after construction is complete.", Misc.getGrayColor(), 5f);
        } else if (!isFunctional()) {
            tooltip.addPara("Station services are suspended until the station becomes operational.", Misc.getNegativeHighlightColor(), 5f);
        } else if (getCooldownDaysRemaining() > 0) {
            tooltip.addPara("AIC deployment cooldown: %s days remaining.", 5f,
                    Misc.getHighlightColor(), String.valueOf(getCooldownDaysRemaining()));
        } else {
            tooltip.addPara("AIC deployment: %s. Use the structure's action menu to select a colony.",
                    5f, Misc.getPositiveHighlightColor(), "Ready");
        }
    }
}
