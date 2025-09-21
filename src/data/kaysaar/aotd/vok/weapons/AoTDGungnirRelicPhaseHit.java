package data.kaysaar.aotd.vok.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.EmpArcEntityAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.List;

public class AoTDGungnirRelicPhaseHit extends BaseEveryFrameCombatPlugin {
    private transient DamagingProjectileAPI proj;
    private transient ShipAPI target;
    private transient float hullDamage;
    private transient float empDamage;
    private float elapsed = 0;
    private float estimatedHitTime;
    public AoTDGungnirRelicPhaseHit(DamagingProjectileAPI proj, ShipAPI target){
        this.proj = proj;
        this.target = target;
        this.hullDamage = proj.getDamageAmount() * AoTDGungnirRelicPhaseRounds.PIERCE_DAMAGE_MULTIPLIER;
        this.empDamage = proj.getEmpAmount() * AoTDGungnirRelicPhaseRounds.PIERCE_DAMAGE_MULTIPLIER;
        //estimates when the proj will hit target to simulate the hit
        estimatedHitTime = MathUtils.getDistance(proj,target) /Math.min(proj.getMoveSpeed(),6000);
    }
    public void advance(float amount, List<InputEventAPI> events) {
        CombatEngineAPI engine = Global.getCombatEngine();
        if(engine.isPaused() || proj == null || target == null) return;

        elapsed += amount;
        if(elapsed >= 12f){
            cleanUp();
        }

        if(elapsed >= estimatedHitTime){
            if (!target.isPhased() && target.getCollisionClass() == CollisionClass.NONE) return;
            if (proj.getSource() != null){
                Vector2f lineStart = proj.getLocation();
                Vector2f lineEnd = MathUtils.getPoint(lineStart,9999f,proj.getFacing());
                Vector2f collisionStart = CollisionUtils.getCollisionPoint(lineStart,lineEnd,target);
                Vector2f collisionEnd = CollisionUtils.getCollisionPoint(lineEnd,lineStart,target);
                if (collisionStart == null || collisionEnd == null) return;

                float facingangle = proj.getFacing();

                for (int i = 0;i < AoTDGungnirRelicPhaseRounds.PEN_EXPLOSIONS;i++) {
                    float radius = MathUtils.getDistance(collisionStart,collisionEnd) * (i / AoTDGungnirRelicPhaseRounds.PEN_EXPLOSIONS);
                    Vector2f splodeloc = MathUtils.getPoint(collisionStart,radius,facingangle);

                    engine.spawnDamagingExplosion(createExplosionSpec(),proj.getSource(),splodeloc);
                    engine.applyDamage(target,
                            splodeloc,
                            hullDamage,
                            proj.getDamageType(),
                            empDamage,
                            false,
                            false,
                            proj.getSource());

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
            cleanUp();
        }
    }
    public void cleanUp(){
        CombatEngineAPI engine = Global.getCombatEngine();
        engine.removePlugin(this);
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
                new Color(102, 17, 193,255), // particleColor
                new Color(139, 85, 244, 255)  // explosionColor
        );
        spec.setUseDetailedExplosion(true);
        spec.setDetailedExplosionFlashRadius(50f);
        spec.setDetailedExplosionFlashDuration(0.2f);
        return spec;
    }
}
