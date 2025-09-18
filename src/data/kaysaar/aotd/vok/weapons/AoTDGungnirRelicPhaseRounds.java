package data.kaysaar.aotd.vok.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.EmpArcEntityAPI;
import com.fs.starfarer.api.combat.OnFireEffectPlugin;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class AoTDGungnirRelicPhaseRounds implements OnHitEffectPlugin,OnFireEffectPlugin {

    private static final float EMP_DAMAGE_MULTIPLIER = 2f;
    public static final float PIERCE_DAMAGE_MULTIPLIER = 0.3f;

    public static final float PEN_EXPLOSIONS = 8;

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target,
                      Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult,
                      CombatEngineAPI engine) {

        if (target instanceof ShipAPI) {
            ShipAPI targetShip = (ShipAPI) target;

            if (shieldHit && targetShip.getShield() != null && targetShip.getShield().isWithinArc(point)) {
                float originalEmpDamage = projectile.getEmpAmount();
                float boostedEmpDamage = originalEmpDamage * EMP_DAMAGE_MULTIPLIER;

                targetShip.getFluxTracker().increaseFlux(boostedEmpDamage, true);

                if (projectile.getSource() != null) {
                    EmpArcEntityAPI.EmpArcParams params = new EmpArcEntityAPI.EmpArcParams();
                    params.maxZigZagMult = 2f;
                    params.glowAlphaMult = 0.9f;
                    for (int i = 0; i < 7; i++) {
                        float angle = projectile.getFacing() - 180f;
                        float rand = 90f;
                        angle = MathUtils.getRandomNumberInRange(1,2) > 1 ? (angle += rand) : (angle -= rand);

                        Vector2f arctarget = MathUtils.getPoint(point,200f,angle);
                        EmpArcEntityAPI arc = engine.spawnEmpArc(
                                projectile.getSource(),
                                point,
                                new SimpleEntity(arctarget),
                                target,
                                DamageType.ENERGY,
                                0f,
                                0f,
                                1000f,
                                "tachyon_lance_emp_impact",
                                20f,
                                new Color(94, 131, 255, 255),
                                new Color(255, 255, 255, 255),
                                params
                        );
                    }
                }
            }
            if (!shieldHit) {
                doPierceScript(projectile, targetShip, point);
            }
        }
    }
    //After initial hit will do proj DMG type and EMP dmg * pierce dmg multiplier
    public void doPierceScript(DamagingProjectileAPI proj, ShipAPI target, Vector2f point) {
        CombatEngineAPI engine = Global.getCombatEngine();

        float hullDamage = proj.getDamageAmount() * PIERCE_DAMAGE_MULTIPLIER;
        float empDamage = proj.getEmpAmount() * PIERCE_DAMAGE_MULTIPLIER;

        if (target != null) {
            if (proj != null) {
                float facingangle = proj.getFacing();
                Vector2f projloc = proj.getLocation();

                //project line and get other end of the ship
                Vector2f lineEnd = MathUtils.getPoint(point,9999f,proj.getFacing());
                Vector2f collisionPoint = CollisionUtils.getCollisionPoint(lineEnd,point,target);
                if (collisionPoint == null) return;

                for (int i = 0; i < PEN_EXPLOSIONS; i++) {
                    float radius = MathUtils.getDistance(point,collisionPoint) * (i / PEN_EXPLOSIONS);
                    Vector2f splodeloc = MathUtils.getPoint(point,radius,facingangle);
                    engine.spawnDamagingExplosion(createExplosionSpec(),proj.getSource(),splodeloc);

                    engine.applyDamage(
                            target,
                            point,
                            hullDamage,
                            proj.getDamageType(),
                            empDamage,
                            false,
                            false,
                            proj.getSource()
                    );

                    if (proj.getSource() != null) {
                        EmpArcEntityAPI.EmpArcParams params = new EmpArcEntityAPI.EmpArcParams();
                        params.glowSizeMult = 0.8f;
                        params.segmentLengthMult = 2f;
                        params.flickerRateMult = 1f;
                        params.minFadeOutMult = 1.4f;
                        EmpArcEntityAPI arc = engine.spawnEmpArc(
                                proj.getSource(),
                                splodeloc,
                                new SimpleEntity(splodeloc),
                                target,
                                DamageType.ENERGY,
                                50f,
                                empDamage,
                                target.getCollisionRadius(),
                                "tachyon_lance_emp_impact",
                                30f,
                                new Color(255, 200, 50, 255),
                                new Color(255, 255, 255, 255),
                                params
                        );
                    }
                }
                if (proj.getWeapon() != null) {
                    engine.spawnProjectile(proj.getSource(), proj.getWeapon(), proj.getWeapon().getId(), MathUtils.getPoint(collisionPoint, 10f, facingangle), facingangle, target.getVelocity());
                }
            }
        }
    }
    public DamagingExplosionSpec createExplosionSpec() {
        float damage = 0f;
        DamagingExplosionSpec spec = new DamagingExplosionSpec(
                0.1f, // duration
                75f, // radius
                50f, // coreRadius
                damage, // maxDamage
                damage / 2f, // minDamage
                CollisionClass.PROJECTILE_FF, // collisionClass
                CollisionClass.PROJECTILE_FIGHTER, // collisionClassByFighter
                5f, // particleSizeMin
                7f, // particleSizeRange
                0.8f, // particleDuration
                200, // particleCount
                new Color(255, 68, 35,255), // particleColor
                new Color(253, 157, 60, 255)  // explosionColor
        );
        spec.setUseDetailedExplosion(true);
        spec.setDetailedExplosionFlashRadius(50f);
        spec.setDetailedExplosionFlashDuration(0.2f);
        return spec;
    }

    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        ArrayList<ShipAPI> target = findPhaseTarget(projectile, weapon, engine);
        if(weapon.getEffectPlugin() instanceof  AoTDRelicEffect effect){
            effect.initReload();
        }
        if(target == null) return;
        for (ShipAPI shipAPI : target) {
            engine.addPlugin(new AoTDGungnirRelicPhaseHit(projectile, shipAPI));
        }
        engine.addPlugin(new AoTDGungnirRelicProjCorrector(projectile));

    }

    public ArrayList<ShipAPI> findPhaseTarget(DamagingProjectileAPI proj, WeaponAPI weapon, CombatEngineAPI engine){
        float range = weapon.getRange() + 100f;
        Vector2f from = proj.getLocation();
        ArrayList<ShipAPI>ships = new ArrayList<>();
        Iterator<ShipAPI> iter = engine.getShips().iterator();
        int owner = weapon.getShip().getOwner();
        float minScore = Float.MAX_VALUE;

        while (iter.hasNext()){
            boolean phaseHit = false;

            ShipAPI otherShip = iter.next();
            if (otherShip.getOwner() == owner) continue;
            if (otherShip.isHulk()) continue;
            if (otherShip.getPhaseCloak() == null)continue;
            if (otherShip.isPhased()) phaseHit = true;
            if (!otherShip.isTargetable()) continue;
            if (!phaseHit && otherShip.getCollisionClass() == CollisionClass.NONE) continue;

//            float radius = Misc.getTargetingRadius(from, otherShip, false);
//            float dist = Misc.getDistance(from, otherShip.getLocation()) - radius;

            float dist = MathUtils.getDistance(weapon.getLocation(),otherShip.getLocation()) - otherShip.getCollisionRadius();
            float expectedAngle = VectorUtils.getAngle(weapon.getLocation(),otherShip.getLocation());
            float actualAngle = weapon.getCurrAngle();
            float diff = Math.abs(expectedAngle - actualAngle);

            if (dist > range) continue;
            if (diff > 20f) continue;
//            if (!Misc.isInArc(weapon.getCurrAngle(), 5f, from, otherShip.getLocation())) continue;
            ships.add(otherShip);
        }
        return ships;
    }
}