package data.kaysaar.aotd.vok.weapons;

import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import org.lwjgl.util.vector.Vector2f;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import java.awt.Color;

public class AoTDGungnirRelicPhaseRounds implements OnHitEffectPlugin {

    private static final float EMP_DAMAGE_MULTIPLIER = 3f;
    private static final float PIERCE_DAMAGE_MULTIPLIER = 0.7f;

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
                    engine.spawnEmpArc(
                            projectile.getSource(),
                            point,
                            targetShip,
                            targetShip,
                            com.fs.starfarer.api.combat.DamageType.ENERGY,
                            0f,
                            boostedEmpDamage,
                            1000f,
                            "tachyon_lance_emp_impact",
                            20f,
                            new Color(125, 155, 255, 255),
                            new Color(255, 255, 255, 255)
                    );
                }
            }

            float hullDamage = projectile.getDamageAmount() * PIERCE_DAMAGE_MULTIPLIER;
            float empDamage = projectile.getEmpAmount() * PIERCE_DAMAGE_MULTIPLIER;

            engine.applyDamage(
                    targetShip,
                    point,
                    hullDamage,
                    projectile.getDamageType(),
                    empDamage,
                    false,
                    false,
                    projectile.getSource()
            );

            engine.addHitParticle(
                    point,
                    new Vector2f(),
                    100f,
                    1f,
                    0.3f,
                    new Color(200, 100, 255, 255)
            );
        }
    }
}