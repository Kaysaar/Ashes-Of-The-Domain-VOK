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

        // Purple Nebula's Code Regarding AI ==================================
        ShipAPI enemyShip = Misc.findClosestShipEnemyOf(
                ship,
                ship.getLocation(),
                ShipAPI.HullSize.CRUISER,
                weapon.getRange(),
                true
        );

        // Part of AI behavior - Purple Nebula
        if (enemyShip != null && ship.getShipAI() != null) {
            // Set target to the closest enemy ship of Cruiser-size or bigger
            ship.setShipTarget(enemyShip);
            ship.getShipAI().setTargetOverride(enemyShip);
            // Control angle of ship towards enemy ship
            float desiredAngle = Misc.getAngleInDegrees(ship.getLocation(),enemyShip.getLocation());
            boolean withinAngleCheck = ship.getFacing() <= desiredAngle+5f && ship.getFacing() >= desiredAngle-5f;

//            if (withinAngleCheck) {
//                if (ship.getFacing() > desiredAngle) ship.setAngularVelocity(ship.getAngularVelocity()-1);
//                else if (ship.getFacing() < desiredAngle) ship.setAngularVelocity(ship.getAngularVelocity()+1);
//                else return;
//            }
            if (withinAngleCheck) {
                if (weapon.getCurrAngle() > desiredAngle) weapon.setCurrAngle(weapon.getCurrAngle()-0.07f);
                else if (weapon.getCurrAngle() < desiredAngle) weapon.setCurrAngle(weapon.getCurrAngle()+0.07f);
                else return;
            }
        }
//        for (WeaponAPI wapi : Global.getCombatEngine().getPlayerShip().getAllWeapons()) {
//            if (!wapi.getId().equals("aotd_shadowlance")) continue;
//            Console.showMessage("Current angle: "+wapi.getCurrAngle());
//            break;
//        }

        ShipwideAIFlags aiFlags = ship.getAIFlags();
        aiFlags.unsetFlag(ShipwideAIFlags.AIFlags.BACK_OFF_MIN_RANGE);
        aiFlags.removeFlag(ShipwideAIFlags.AIFlags.BACK_OFF_MIN_RANGE);
        aiFlags.unsetFlag(ShipwideAIFlags.AIFlags.BACKING_OFF);
        aiFlags.removeFlag(ShipwideAIFlags.AIFlags.BACKING_OFF);
        aiFlags.unsetFlag(ShipwideAIFlags.AIFlags.BACK_OFF_MAX_RANGE);
        aiFlags.removeFlag(ShipwideAIFlags.AIFlags.BACK_OFF_MAX_RANGE);
        aiFlags.unsetFlag(ShipwideAIFlags.AIFlags.BACK_OFF);
        aiFlags.removeFlag(ShipwideAIFlags.AIFlags.BACK_OFF);
        aiFlags.setFlag(ShipwideAIFlags.AIFlags.DO_NOT_BACK_OFF);
//        if (ship.getShipAI() != null) {
//
//
//            if (ship.getCurrFlux() < (ship.getMaxFlux()*0.8f)) {
//                weapon.setForceNoFireOneFrame(true);
//                WeaponGroupAPI wgApi = ship.getWeaponGroupFor(weapon);
//                wgApi.toggleOff();
//
//            } else {
//                ShipAPI enemyShip = Misc.findClosestShipEnemyOf(
//                        ship,
//                        ship.getLocation(),
//                        ShipAPI.HullSize.CRUISER,
//                        weapon.getRange(),
//                        true
//                );
//                Vector2f targeting = MathUtils.getPoint(ship.getLocation(),weapon.getRange(),ship.getFacing());
//                if (enemyShip != null) {
//                    targeting = AIUtils.getBestInterceptPoint(
//                            weapon.getFirePoint(0), weapon.getProjectileSpeed(),
//                            enemyShip.getLocation(),enemyShip.getVelocity()
//                    );
//
//                }
//
//                ship.setShipTarget(enemyShip);
//                ship.getShipAI().setTargetOverride(enemyShip);
//                if (enemyShip != null && ship.getShipTarget() == enemyShip) {
//
//                    float desiredAngle = Misc.getAngleInDegrees(ship.getLocation(),enemyShip.getLocation());
//                    float distanceToTarget = Misc.getDistance(ship.getLocation(),enemyShip.getLocation());
//                    boolean withinAngleCheck = ship.getFacing() <= desiredAngle+5f && ship.getFacing() >= desiredAngle-5f;
////                    if (ship.getFacing() != desiredAngle) {
//                    WeaponGroupAPI wgApi = ship.getWeaponGroupFor(weapon);
//                    if (withinAngleCheck) {
//                        if (distanceToTarget < weapon.getRange()) {
//                            wgApi.toggleOn();
//                            weapon.setForceNoFireOneFrame(false);
//                            weapon.setForceFireOneFrame(true);
//                            weapon.repair();
//                        }
//                    } else {
//                        wgApi.toggleOff();
//                        weapon.setForceNoFireOneFrame(true);
//                        weapon.setForceFireOneFrame(false);
//                        weapon.disable(false);
//                    }
//
////                    ShipAPI playerShip = Global.getCombatEngine().getPlayerShip();
////                    Console.showMessage("Player ship X: "+playerShip.getLocation().getX());
////                    Console.showMessage("Player ship Y: "+playerShip.getLocation().getY());
////                    Console.showMessage("Enemy ship X: "+playerShip.getShipTarget().getLocation().getX());
////                    Console.showMessage("Enemy ship Y: "+playerShip.getShipTarget().getLocation().getY());
//////                    float angle = (float) Math.toDegrees(Math.atan2(
//////                            playerShip.getShipTarget().getLocation().getY() - playerShip.getLocation().getY(),
//////                            playerShip.getShipTarget().getLocation().getX() - playerShip.getLocation().getX())
//////                    );
//////                    Console.showMessage("Desired angle: "+angle);
////                    Console.showMessage("Desired angle: "+Misc.getAngleInDegrees(playerShip.getLocation(),playerShip.getShipTarget().getLocation()));
//////                    playerShip.setFacing(angle);
//
//                }
//            }
//
//        }
        // ====================================================================

        // Mayu's Code regarding VFX Effect
        if (weapon.getCooldownRemaining() > 0.1f) {
            if (weapon.getShip() == Global.getCombatEngine().getPlayerShip()) {
                Global.getCombatEngine().maintainStatusForPlayerShip(
                        weapon.toString(),
                        shadowlanceIcon,
                        "Shadowlance: Recharging",
                        Misc.getRoundedValue(weapon.getCooldownRemaining()) + " seconds remaining", // Purple Nebula's
//                        Misc.getRoundedValue(weapon.getCooldownRemaining()) + " secs before firing again", // Mayu's
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
        // The overrider for the duration of firing beam
        // Does it work? I don't fuckin know
        // Set all weapons and defense system to be disabled
        if (weapon.isFiring()) {

            if (hasFired || charge >= 0.99f){
                // Disable the shields or phase
                weapon.getShip().setDefenseDisabled(true);
                weapon.getShip().setShipSystemDisabled(true);
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

                if(hasFired||charge>=0.98f){
                    if(!commencedSucking){
                        commencedSucking = true;
                        Global.getSoundPlayer().playSound("shadowlance_on_fire"
                                ,0.9f,  1,
                                weapon.getLocation(), weapon.getShip().getVelocity());
                    }
                }
                if (hasFired || charge >= 0.99f){
                    // Disable the shields or phase
                    if(hasFired){
                        Global.getSoundPlayer().playLoop("shadowlance_fire",
                                weapon.getShip(),0.7f,  0.8f,
                                weapon.getLocation(), weapon.getShip().getVelocity());
                    }
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
                if (weaponID.getId().equals("aotd_shadowlance")  && ship.getShipAI() != null) {
                    if (weapon.getShip().getCurrFlux() < weapon.getShip().getMaxFlux()*0.8f) {
//                if (weapon.getCooldownRemaining() > 0) return;
//                weapon.setRemainingCooldownTo(2f);
                        weapon.setForceDisabled(true);
                    } else {
                        if (enemyShip != null) {
                            float desiredAngle = Misc.getAngleInDegrees(ship.getLocation(),enemyShip.getLocation());
                            float distanceToTarget = Misc.getDistance(ship.getLocation(),enemyShip.getLocation());
                            boolean withinAngleCheck = ship.getFacing() <= desiredAngle+5f && ship.getFacing() >= desiredAngle-5f;
                            boolean withinRangeCheck = distanceToTarget < weapon.getRange();
                            if (withinAngleCheck && withinRangeCheck) {
//                    if (weapon.getCooldownRemaining() > 2f) weapon.setRemainingCooldownTo(0);
                                weapon.setForceDisabled(false);
                            }
                        }
                    }
                }
                else {
                    // Re-enables the weapons
                    weaponID.setForceDisabled(false);
                }
            }
            // Re-enables the defense system
            weapon.getShip().setShipSystemDisabled(false);
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
                if(!weapon.getSlot().isHardpoint()){
                    TURRET_OFFSET-=15;
                }

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
                        Global.getSoundPlayer().playLoop("shadowlance_charge",
                                weapon.getShip(), Math.max(0.3f,charge),  Math.max(0.9f,charge),
                                weapon.getLocation(), weapon.getShip().getVelocity());
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
                else {
                    // OG Flux Remover
//                    if (0.01f < FluxRemover) { //                        if (ship.getHardFluxLevel() + 0.01f < FluxRemover) {
//                        Global.getCombatEngine().maintainStatusForPlayerShip(
//                                weapon.getShip() + "TOOLTIP_2",
//                                Global.getSettings().getSpriteName("tooltips", "grimoire_deepstrike"),
//                                "AOTD TEST WEAPON: " + Misc.getRoundedValue(FluxRemover) + "%",
//                                "Currently discharging built up flux...",
//                                false
//                        );
//                        if (FluxRemover > 0.75f) {
//                            ship.getFluxTracker().decreaseFlux(25f);
//                        }
//                        else {
//                            ship.getFluxTracker().decreaseFlux(25f * FluxRemover);
//                        }
//                    }
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
            commencedSucking= false;
            charge =0f;
        }
    }

}