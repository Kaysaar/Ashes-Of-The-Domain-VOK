package data.kaysaar.aotd.vok.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lwjgl.util.vector.ReadableVector2f;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

import static data.kaysaar.aotd.vok.plugins.ReflectionUtilis.getFloatFieldNameMatchingValue;


public class AoTDShadowLanceVFXEffect implements EveryFrameWeaponEffectPlugin {

    private final IntervalUtil intervalALT = new IntervalUtil(0.015f, 0.015f);
    private final IntervalUtil interval = new IntervalUtil(0.07f, 0.1f);
    private final IntervalUtil particle = new IntervalUtil(0.025f, 0.05f);
    private static final Color OVERDRIVE_COLOR = new Color(255, 50, 50, 5);
    private static final Color ENGINE_COLOR = new Color(255, 20, 5);
    float CHARGEUP_PARTICLE_ANGLE_SPREAD = 150f;
    float CHARGEUP_PARTICLE_BRIGHTNESS = 1f;
    float CHARGEUP_PARTICLE_DURATION = 0.5f;
    private boolean hasFired = false;
    float charge = 0f;
    float fluxCurr = 0;
    private static float RATE_PER_SECOND = 2000f;

    private boolean didIt = false;
    private boolean didItOnce = false;
    private boolean commencedSucking = false;

    private IntervalUtil intervalEMP = new IntervalUtil(0.05f, 0.2f);
    private transient String nameOfVariable = "";
    private transient String nameOfBeamStatusField = "";
    private transient String nameOfChargeUpFieldInBeamStatus = "";

    private static String shadowlanceIcon = "graphics/icons/hud/aotd_shadowlance_icon.png";

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        // Null check
        if (engine.isPaused() || weapon == null || amount < 0) {
            //Added amount <0 because in Refit Screen this script is being initalized with amount being max negative float
            return;
        }

        ShipAPI ship = weapon.getShip();

        if (weapon.getCooldownRemaining() > 1f) {
            if (weapon.getShip() == Global.getCombatEngine().getPlayerShip()) {
                Global.getCombatEngine().maintainStatusForPlayerShip(
                        weapon.toString(),
                        shadowlanceIcon,
                        "Shadowlance: Recharging",
                        Misc.getRoundedValue(weapon.getCooldownRemaining()) + " secs before firing again",
                        false
                );
            }
        }

        if (!didItOnce) {
            weapon.ensureClonedSpec();
            Object tracker = ReflectionUtilis.invokeMethodWithAutoProjection("getWeaponTracker", weapon);
            nameOfVariable = getFloatFieldNameMatchingValue(tracker, 1.7f);
            nameOfBeamStatusField = ReflectionUtilis.findFieldWithMatchingCtor(tracker);
            Object beamStatusField = ReflectionUtilis.getPrivateVariable(nameOfBeamStatusField, tracker);
            nameOfChargeUpFieldInBeamStatus = getFloatFieldNameMatchingValue(beamStatusField, 1);
            didItOnce = true;
        }
        if (weapon.isFiring()) {

            if (charge != 0.5f) {
                if (!didIt) {
                    // this overrides the beam duration
                    fluxCurr = 0;
                    Object tracker = ReflectionUtilis.invokeMethodWithAutoProjection("getWeaponTracker", weapon);
                    Object beamStatusField = ReflectionUtilis.getPrivateVariable(nameOfBeamStatusField, tracker);
                    float flux = ship.getCurrFlux();
                    fluxCurr = flux;
                    float seconds = Math.max(1, flux / RATE_PER_SECOND);
                    ReflectionUtilis.setPrivateVariableFromSuperclass(nameOfVariable, tracker, seconds * 2);
                    ReflectionUtilis.setPrivateVariableFromSuperclass(nameOfChargeUpFieldInBeamStatus, beamStatusField, seconds);
                    didIt = true;
                }
                if (hasFired || charge >= 0.99f){
                    // Disable the shields or phase
                    weapon.getShip().setDefenseDisabled(true);
                    if( weapon.getShip().getFluxTracker().getCurrFlux() <0){
                        weapon.getShip().getFluxTracker().increaseFlux(15,true);
                    }
                    if (weapon.getShip() == Global.getCombatEngine().getPlayerShip()) {
                        Global.getCombatEngine().maintainStatusForPlayerShip(
                                weapon.getShip(),
                                shadowlanceIcon,
                                "Shadowlance: Currently firing",
                                "Defensive systems and weapons are disabled",
                                true
                        );
                    }
                }
                for (WeaponAPI weaponID : weapon.getShip().getAllWeapons()) {
                    // Of fucking course, exclude the weapon itself otherwise IT WILL NOT WORK
                    if (weaponID.getId().equals("aotd_shadowlance")) continue;
                    // WHY WOULD YOU INCLUDE THE DECORATIVES FOR THIS? EXCLUDE THEM TOO
                    if (weaponID.isDecorative()) continue;
                    // Then disable the weapons
                    weaponID.setForceDisabled(true);
                    // Try to see if you can change the vfx of disabled weapons smoke
                    // Wait... maybe not, I can't or- it's too fucking complex for such minor details
                }
                if (charge < 0.95f) {
                    if (weapon.getShip() == Global.getCombatEngine().getPlayerShip()) {
                        Global.getCombatEngine().maintainStatusForPlayerShip(
                                weapon.getShip(),
                                shadowlanceIcon,
                                "Shadowlance: Currently firing",
                                "Weapons are disabled",
                                true
                        );
                    }
                }
            }
        }
        else {
            for (WeaponAPI weaponID : weapon.getShip().getAllWeapons()) {
                // Re-enables the weapons
                weaponID.setForceDisabled(false);
            }
            // Re-enables the defense system
            weapon.getShip().setDefenseDisabled(false);
            didIt = false;
        }
        /// ///////////////////////////////////////////////////////////

        // Set up the charging animation
        if (weapon.isFiring() && charge != 0.5f) {
            float size = 6f; // Previously 2
            for (int i = 0; i < 1; i++) {
                int barrel = i;
                float TURRET_OFFSET = weapon.getSpec().getTurretFireOffsets().get(barrel).x;
                float OFFSET_Y = weapon.getSpec().getTurretFireOffsets().get(barrel).y;

                charge = weapon.getChargeLevel();

                if (!hasFired) {
                    // Remover at the charge up
                    if (charge <= 1f) {
                        Global.getCombatEngine().maintainStatusForPlayerShip(
                                weapon.getShip() + "TOOLTIP_2",
                                shadowlanceIcon,
                                "Shadowlance",
                                "Currently discharging built up flux...",
                                false
                        );
                        ship.getFluxTracker().setCurrFlux(fluxCurr - (fluxCurr * charge));
                        ship.getFluxTracker().setHardFlux(fluxCurr - (fluxCurr * charge));
                    }

                    particle.advance(amount);
                    if (particle.intervalElapsed()) {
                        Vector2f origin = new Vector2f(weapon.getLocation());
                        Vector2f offset = new Vector2f(TURRET_OFFSET + 20f, OFFSET_Y);
                        VectorUtils.rotate(offset, weapon.getCurrAngle(), offset);
                        Vector2f.add(offset, origin, origin);
                        Vector2f vel = weapon.getShip().getVelocity();

                        // LIGHT VFX THAT POOLS IN THE SUCTION AREA, YEAH I DON'T KNOW EITHER WHY IS IT HERE BEFORE THE SUCTION CODE
                        engine.addHitParticle(
                                origin,
                                vel,
                                MathUtils.getRandomNumberInRange((float) size * 2, (float) (charge * 1.5f * 30f + size)),
                                MathUtils.getRandomNumberInRange((float) 0.5f, (float) (0.5f + charge)),
                                MathUtils.getRandomNumberInRange((float) 0.1f, (float) (0.1f + charge / 10.0f)),
                                new Color(204, 51, 0, 100)
                        );

                        // I forgot what the fuck is this
                        Vector2f particleVel = MathUtils.getRandomPointInCircle((Vector2f) new Vector2f(), (float) (35.0f * charge));
                        Vector2f particleLoc = new Vector2f();
                        Vector2f.sub((Vector2f) origin, (Vector2f) new Vector2f((ReadableVector2f) particleVel), (Vector2f) particleLoc);
                        Vector2f.add((Vector2f) vel, (Vector2f) particleVel, (Vector2f) particleVel);
                    }

                    Vector2f origin = new Vector2f(weapon.getLocation());
                    Vector2f offset = new Vector2f(TURRET_OFFSET, OFFSET_Y);
                    VectorUtils.rotate(offset, weapon.getCurrAngle(), offset);
                    Vector2f.add(offset, origin, origin);
                    // Vector2f vel = weapon.getShip().getVelocity();

                    // Set up the VFX SUCTION
                    float shipFacing = weapon.getCurrAngle();
                    Vector2f shipVelocity = ship.getVelocity();
                    interval.advance(amount);
                    float chargeLevel = weapon.getChargeLevel();
                    float muzzleChargeOffset = 20f;
                    float chargeupParticleMin = 0f;
                    float chargeupParticleMax = 0f;
                    float chargeup_particle_distance_minimum = 0f;
                    float chargeup_particle_distance_maximum = 0f;
                    chargeupParticleMin = 3f;
                    chargeupParticleMax = 7f;
                    chargeup_particle_distance_minimum = 40f;
                    chargeup_particle_distance_maximum = 80f;

                    Vector2f muzzleLocation = MathUtils.getPointOnCircumference(
                            origin,
                            muzzleChargeOffset,
                            shipFacing
                    );

                    // Location of the EMP build up
                    Vector2f locEMP = MathUtils.getPoint((Vector2f)weapon.getLocation(), (float)18.5f, (float)weapon.getCurrAngle());

                    // Set up the EMP ARC point of target that zaps everywhere 180 degrees because why the fuck not
                    ShipEngineControllerAPI.ShipEngineAPI shipengine = ship.getEngineController().getShipEngines().get(MathUtils.getRandomNumberInRange(0, ship.getEngineController().getShipEngines().size() - 1));
                    intervalALT.advance(amount);
                    intervalEMP.advance(Global.getCombatEngine().getElapsedInLastFrame());

                    if (intervalALT.intervalElapsed() && weapon.isFiring()) {
                        int particleCount = (int) (5f * chargeLevel);
                        float distance, sizeALT, angle, speed;
                        Vector2f particleVelocity;
                        for (int wow = 0; wow < particleCount; ++wow) {
                            distance = MathUtils.getRandomNumberInRange(chargeup_particle_distance_minimum, chargeup_particle_distance_maximum);
                            sizeALT = MathUtils.getRandomNumberInRange(chargeupParticleMin, chargeupParticleMax);
                            angle = MathUtils.getRandomNumberInRange(-0.5f * CHARGEUP_PARTICLE_ANGLE_SPREAD, 0.5f * CHARGEUP_PARTICLE_ANGLE_SPREAD);
                            Vector2f spawnLocation = MathUtils.getPointOnCircumference(muzzleLocation, distance, (angle + shipFacing));
                            speed = distance / CHARGEUP_PARTICLE_DURATION;
                            particleVelocity = MathUtils.getPointOnCircumference(shipVelocity, speed, 180.0f + angle + shipFacing);
                            if (charge <= 0.91f) {
                                // ACTUAL SUCTION SCRIPT THAT STOPS APPEARING BEFORE THE FIRING BECAUSE YES
                                engine.addHitParticle(
                                        spawnLocation,
                                        particleVelocity,
                                        sizeALT,
                                        CHARGEUP_PARTICLE_BRIGHTNESS * weapon.getChargeLevel(),
                                        0.75f,
                                        new Color(255, 24, 24, 255)
                                );
                            }
                        }

                        // Then put the fucking EMP script here
                        if (intervalEMP.intervalElapsed()) {
                            if (charge <= 0.99f) {
                                Global.getCombatEngine().spawnEmpArc(
                                        ship,
                                        AoTDCombatUtils.getRandomPointInShipCollisionBounds(ship),
                                        new SimpleEntity(shipengine.getLocation()),
                                        new SimpleEntity(locEMP),//new SimpleEntity(weaponLocation),
                                        DamageType.ENERGY, //Damage type
                                        0f, //Damage
                                        0f, //Emp
                                        100000f, //Max range
                                        null, //Impact sound
                                        3f, // thickness of the lightning bolt
                                        ENGINE_COLOR, //Central color
                                        OVERDRIVE_COLOR //Fringe Color
                                );
                            }
                        }
                    }
                }
                // Another part that you can put various shit into
                if (charge == 1f) {
                    // To add that IMPACT when it fires the beam at 0.9 seconds
                    // Can shockwave VFX here whenever
                    hasFired = true;
                }
            }
        } else {
            // So the whole ass animation won't repeat until it fire again
            hasFired = false;
        }
    }

}