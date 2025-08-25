package data.kaysaar.aotd.vok.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.awt.*;

public class CommandingPresence extends BaseHullMod {

    private static final float SHIELD_UNFURL_RATE_BONUS = 100f; // +100%
    private static final float VENT_SPEED_BONUS = 15f; // +15%
    private static final float HARD_FLUX_EFFECTIVENESS = 5f; // +5%
    private static final float SOFT_FLUX_EFFECTIVENESS = 10f; // +10%

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        // +100% shield unfurl rate
        stats.getShieldUnfoldRateMult().modifyPercent(id, SHIELD_UNFURL_RATE_BONUS);

        // +15% faster vent speed
        stats.getVentRateMult().modifyPercent(id, VENT_SPEED_BONUS);

        // +5% shield effectiveness vs hard flux
        stats.getHardFluxDissipationFraction().modifyFlat(id, HARD_FLUX_EFFECTIVENESS * 0.01f);

        // +10% shield effectiveness vs soft flux
        stats.getFluxDissipation().modifyPercent(id, SOFT_FLUX_EFFECTIVENESS);
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        // Optional: Add visual effects if desired
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return Math.round(SHIELD_UNFURL_RATE_BONUS) + "%";
        if (index == 1) return Math.round(VENT_SPEED_BONUS) + "%";
        if (index == 2) return Math.round(HARD_FLUX_EFFECTIVENESS) + "%";
        if (index == 3) return Math.round(SOFT_FLUX_EFFECTIVENESS) + "%";
        return null;
    }

    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float pad = 10f;
        Color orange = new Color(255, 165, 0, 255);
        Color highlight = new Color(255, 200, 100, 255);

        tooltip.addPara("Commanding Presence enhances shield performance with:", pad);

        tooltip.addPara(
                "• %s faster shield deployment",
                3f,
                highlight,
                Math.round(SHIELD_UNFURL_RATE_BONUS) + "%"
        );

        tooltip.addPara(
                "• %s increased vent speed",
                3f,
                highlight,
                Math.round(VENT_SPEED_BONUS) + "%"
        );

        tooltip.addPara(
                "• %s improved hard flux dissipation",
                3f,
                highlight,
                Math.round(HARD_FLUX_EFFECTIVENESS) + "%"
        );

        tooltip.addPara(
                "• %s improved soft flux dissipation",
                3f,
                highlight,
                Math.round(SOFT_FLUX_EFFECTIVENESS) + "%"
        );

        tooltip.addPara("The imposing aura of the unyeilding, unbreakable Citadel bolsters shield systems through sheer force of will.", pad, orange, "unyeilding, unbreakable Citadel", "imposing aura");
    }

    @Override
    public Color getBorderColor() {
        return new Color(255, 140, 0, 255); // Dark orange border
    }

    @Override
    public Color getNameColor() {
        return new Color(255, 165, 0, 255); // Orange text color
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        // Only applicable to low-tech ships
        return ship.getHullSpec().getManufacturer().equals("Low Tech") ||
                ship.getHullSpec().getHullId().contains("lowtech") ||
                ship.getHullSpec().getHullName().toLowerCase().contains("low tech");
    }

    @Override
    public String getUnapplicableReason(ShipAPI ship) {
        return "Can only be installed on Low Tech ships";
    }
}