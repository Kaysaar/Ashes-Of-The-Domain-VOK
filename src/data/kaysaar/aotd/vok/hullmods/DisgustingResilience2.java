package data.kaysaar.aotd.vok.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.DamageTakenModifier;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;
import java.awt.Color;

public class DisgustingResilience2 extends BaseHullMod {

    // =================================
    //            CONSTANTS
    // =================================
    private static final float BALLISTIC_RICOCHET_CHANCE = 0.9f;
    private static final float MISSILE_DEFLECT_CHANCE = 0.25f;
    private static final float BALLISTIC_DAMAGE_REDUCTION = 0.45f;
    private static final float PROJECTILE_SPEED_RETENTION = 0.2f;
    private static final float MISSILE_DEFLECT_ANGLE_RANGE = 45f;
    private static final float MIN_VECTOR_LENGTH = 0.001f;

    // Grey/White color scheme
    private static final Color ORANGE_HIGHLIGHT = new Color(255, 165, 0, 255);
    private static final Color DARK_ORANGE_BORDER = new Color(255, 140, 0, 255);
    private static final Color RICOCHEET_COLOR = new Color(200, 200, 200, 255); // Grey-white
    private static final Color DEFLECT_COLOR = new Color(220, 220, 220, 200); // Light Grey-white

    // First define the listener class
    private static class DisgustinglyResilientDamageListener implements DamageTakenModifier {
        @Override
        public String modifyDamageTaken(Object param,
                                        CombatEntityAPI target,
                                        DamageAPI damage,
                                        Vector2f point,
                                        boolean shieldHit) {

            if (shieldHit || !(target instanceof ShipAPI) || !(param instanceof DamagingProjectileAPI)) {
                return null;
            }

            ShipAPI ship = (ShipAPI) target;
            DamagingProjectileAPI projectile = (DamagingProjectileAPI) param;
            if(projectile.getWeapon().getSpec().hasTag("always_pierce"))return null;
            boolean isMissile = projectile instanceof MissileAPI;
            boolean isBallistic = projectile.getWeapon() != null &&
                    projectile.getWeapon().getSpec().getType() == WeaponAPI.WeaponType.BALLISTIC;

            if (!isMissile && !isBallistic) {
                return null;
            }

            float chance = isMissile ? MISSILE_DEFLECT_CHANCE : BALLISTIC_RICOCHET_CHANCE;
            if (Math.random() > chance) {
                return null;
            }

            // Calculate surface normal at impact point for more accurate reflection
            Vector2f surfaceNormal = calculateSurfaceNormal(ship, point, projectile.getLocation());

            if (surfaceNormal == null) {
                // Fallback to center-based reflection if normal calculation fails
                surfaceNormal = VectorUtils.getDirectionalVector(point, ship.getLocation());
                if (safeNormalize(surfaceNormal) == null) {
                    return null; // Can't calculate normal, skip deflection
                }
            }

            // Calculate reflection angle based on surface normal (true physics reflection)
            Vector2f incomingDirection = VectorUtils.getDirectionalVector(projectile.getLocation(), point);
            Vector2f safeIncoming = safeNormalize(incomingDirection);
            if (safeIncoming == null) {
                return null; // Can't calculate incoming direction, skip deflection
            }

            float reflectionAngle = calculateReflectionAngle(safeIncoming, surfaceNormal);
            Vector2f reflectedDirection = VectorUtils.rotate(safeIncoming, reflectionAngle);

            if (isMissile) {
                // For missiles, add some randomness to deflection
                float randomDeflection = MathUtils.getRandomNumberInRange(-MISSILE_DEFLECT_ANGLE_RANGE, MISSILE_DEFLECT_ANGLE_RANGE);
                reflectedDirection = VectorUtils.rotate(reflectedDirection, randomDeflection);
                damage.setDamage(0f);
                spawnDeflectEffect(point, reflectedDirection);
                return "disgustingly_resilient_missile_deflect";
            } else {
                // For ballistic projectiles, apply proper reflection physics
                Vector2f newVelocity = new Vector2f(reflectedDirection);
                float originalSpeed = projectile.getVelocity().length();
                newVelocity.scale(originalSpeed * PROJECTILE_SPEED_RETENTION);
                projectile.getVelocity().set(newVelocity);

                // Update projectile facing to match new direction
                float newFacing = VectorUtils.getFacing(reflectedDirection);
                projectile.setFacing(newFacing);

                damage.setDamage(damage.getDamage() * (1f - BALLISTIC_DAMAGE_REDUCTION));
                spawnRicochetEffect(point, reflectedDirection);
                return "disgustingly_resilient_ballistic_ricochet";
            }
        }

        /**
         * Safe vector normalization with zero-length check
         */
        private Vector2f safeNormalize(Vector2f vector) {
            if (vector == null) return null;
            float length = vector.length();
            if (length < MIN_VECTOR_LENGTH) {
                return null;
            }
            vector.normalise();
            return vector;
        }

        /**
         * Calculate surface normal at impact point
         */
        private Vector2f calculateSurfaceNormal(ShipAPI ship, Vector2f impactPoint, Vector2f projectileOrigin) {
            Vector2f shipCenter = ship.getLocation();

            // Calculate direction from ship center to impact point
            Vector2f centerToImpact = VectorUtils.getDirectionalVector(shipCenter, impactPoint);
            Vector2f normalizedCenterToImpact = safeNormalize(centerToImpact);
            if (normalizedCenterToImpact == null) {
                return new Vector2f(0, 1); // Default upward normal
            }

            // Use the ship's facing to determine approximate surface orientation
            float shipFacing = ship.getFacing();

            // Simple approximation: use the direction from center to impact as normal
            // This works well for roughly circular ships
            Vector2f estimatedNormal = new Vector2f(normalizedCenterToImpact);

            return estimatedNormal;
        }

        /**
         * Calculate reflection angle using proper physics: angle of incidence = angle of reflection
         */
        private float calculateReflectionAngle(Vector2f incomingDir, Vector2f surfaceNormal) {
            // Ensure vectors are normalized
            Vector2f safeIncoming = safeNormalize(new Vector2f(incomingDir));
            Vector2f safeNormal = safeNormalize(new Vector2f(surfaceNormal));

            if (safeIncoming == null || safeNormal == null) {
                return 180f; // Default 180 degree reflection if vectors are invalid
            }

            // Calculate reflection using formula: R = I - 2 * (I·N) * N
            float dotProduct = Vector2f.dot(safeIncoming, safeNormal);

            Vector2f reflection = new Vector2f(
                    safeIncoming.x - 2 * dotProduct * safeNormal.x,
                    safeIncoming.y - 2 * dotProduct * safeNormal.y
            );

            Vector2f safeReflection = safeNormalize(reflection);
            if (safeReflection == null) {
                return 180f; // Default reflection
            }

            return VectorUtils.getFacing(safeReflection);
        }

        private void spawnRicochetEffect(Vector2f location, Vector2f direction) {
            Vector2f safeDirection = safeNormalize(new Vector2f(direction));
            if (safeDirection == null) {
                safeDirection = new Vector2f(0, 1);
            }

            Global.getCombatEngine().spawnExplosion(
                    location,
                    new Vector2f(safeDirection.x * 50f, safeDirection.y * 50f),
                    RICOCHEET_COLOR,
                    25f, // Slightly larger explosion
                    0.3f // Longer duration
            );

            Global.getCombatEngine().addHitParticle(
                    location,
                    new Vector2f(safeDirection.x * 120f, safeDirection.y * 120f),
                    15f, // Larger particles
                    1.2f, // Brighter
                    0.4f, // Longer duration
                    new Color(230, 230, 230, 255) // Light grey particle trail
            );

            // Add some spark particles for metallic effect
            for (int i = 0; i < 5; i++) {
                Global.getCombatEngine().addHitParticle(
                        location,
                        new Vector2f(
                                safeDirection.x * (60f + (float)Math.random() * 60f),
                                safeDirection.y * (60f + (float)Math.random() * 60f)
                        ),
                        3f + (float)Math.random() * 2f,
                        1f,
                        0.2f + (float)Math.random() * 0.1f,
                        new Color(255, 255, 255, 200) // White sparks
                );
            }
        }

        private void spawnDeflectEffect(Vector2f location, Vector2f direction) {
            Vector2f safeDirection = safeNormalize(new Vector2f(direction));
            if (safeDirection == null) {
                safeDirection = new Vector2f(0, 1);
            }

            Global.getCombatEngine().spawnExplosion(
                    location,
                    new Vector2f(safeDirection.x * 40f, safeDirection.y * 40f),
                    DEFLECT_COLOR,
                    30f, // Larger explosion for missiles
                    0.35f // Longer duration
            );

            // Add additional particles for missile deflection
            for (int i = 0; i < 5; i++) {
                Global.getCombatEngine().addHitParticle(
                        location,
                        new Vector2f(
                                safeDirection.x * (80f + (float)Math.random() * 40f),
                                safeDirection.y * (80f + (float)Math.random() * 40f)
                        ),
                        8f + (float)Math.random() * 4f,
                        1f,
                        0.3f + (float)Math.random() * 0.2f,
                        new Color(240, 240, 240, 200) // Light grey particles
                );
            }

            // Add white flash particles for missile deflection
            for (int i = 0; i < 3; i++) {
                Global.getCombatEngine().addHitParticle(
                        location,
                        new Vector2f(
                                safeDirection.x * (100f + (float)Math.random() * 50f),
                                safeDirection.y * (100f + (float)Math.random() * 50f)
                        ),
                        12f + (float)Math.random() * 6f,
                        1.5f,
                        0.15f + (float)Math.random() * 0.1f,
                        new Color(255, 255, 255, 150) // White flash
                );
            }
        }
    }

    // =================================
    //          TOOLTIP
    // =================================
    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float pad = 10f;

        tooltip.addPara(
                "The Citadel has disgustingly resilient plating that provides exceptional protection. Has a %s chance of deflecting Ballistic Projectiles and a %s chance of Missiles bouncing off harmlessly; Deflected Projectiles deal %s of its original damage.",
                pad,
                ORANGE_HIGHLIGHT,
                Math.round(BALLISTIC_RICOCHET_CHANCE * 100) + "%",
                Math.round(MISSILE_DEFLECT_CHANCE * 100) + "%",
                Math.round((1f - BALLISTIC_DAMAGE_REDUCTION) * 100) + "%"
        );
    }

    @Override
    public Color getBorderColor() {
        return DARK_ORANGE_BORDER;
    }

    @Override
    public Color getNameColor() {
        return ORANGE_HIGHLIGHT;
    }

    // =================================
    //          HULLMOD METHODS
    // =================================
    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        if (!ship.hasListenerOfClass(DisgustinglyResilientDamageListener.class)) {
            ship.addListener(new DisgustinglyResilientDamageListener());
        }
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
        switch (index) {
            case 0: return Math.round(BALLISTIC_RICOCHET_CHANCE * 100) + "%";
            case 1: return Math.round(BALLISTIC_DAMAGE_REDUCTION * 100) + "%";
            case 2: return Math.round(MISSILE_DEFLECT_CHANCE * 100) + "%";
            default: return null;
        }
    }
}