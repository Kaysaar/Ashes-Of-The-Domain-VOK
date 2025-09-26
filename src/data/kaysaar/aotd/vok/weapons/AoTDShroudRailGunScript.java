package data.kaysaar.aotd.vok.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.Iterator;

import static org.lazywizard.lazylib.MathUtils.getRandomNumberInRange;

public class AoTDShroudRailGunScript implements OnFireEffectPlugin, OnHitEffectPlugin, EveryFrameWeaponEffectPlugin {

    private final int arcOffsetMin = -50;
    private final int arcOffsetMax = 50;

    public static Color RIFT_LIGHTNING_COLOR = new Color(150,0,50,255);
    public static float glow = 0f;
    public static Vector2f velocity = new Vector2f();
    CombatEngineAPI engine = Global.getCombatEngine();
    IntervalUtil timer = new IntervalUtil(0.5f, 0.5f);
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {

        glow = weapon.getChargeLevel();
        velocity = weapon.getShip().getVelocity();
        timer.advance(0.1f);

        if (timer.intervalElapsed()) {
            Iterator projiter = engine.getProjectiles().iterator();
            while (projiter.hasNext()) {
                DamagingProjectileAPI proj = (DamagingProjectileAPI) projiter.next();
                if (proj.getWeapon() == weapon) {
                    Vector2f empSourceLocation = new Vector2f(
                            proj.getLocation().x + getRandomNumberInRange(arcOffsetMax, arcOffsetMin),
                            proj.getLocation().y + getRandomNumberInRange(arcOffsetMax, arcOffsetMin)
                    );
                    Vector2f projSourceLocation = new Vector2f(
                            proj.getLocation().x + getRandomNumberInRange(arcOffsetMax, arcOffsetMin),
                            proj.getLocation().y + getRandomNumberInRange(arcOffsetMax, arcOffsetMin)
                    );
                    engine.spawnEmpArcVisual(
                            projSourceLocation,
                            new SimpleEntity(projSourceLocation),
                            empSourceLocation,
                            new SimpleEntity(empSourceLocation),
                            getRandomNumberInRange(10, 3),
                            RIFT_LIGHTNING_COLOR,
                            new Color(0, 0, 0, 255)
                    );
                    spawnCloudVFX(empSourceLocation);
                    spawnCloudVFX(projSourceLocation);
                }
            }
        }
    }

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {

    }

    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        for (int i = 0; i < 6; i++){
            Vector2f loc = MathUtils.getRandomPointInCone(weapon.getLocation(), 50, weapon.getCurrAngle()-195, weapon.getCurrAngle()-175);
            spawnCloudVFX(loc);
        }
    }

    public void spawnCloudVFX(Vector2f loc){
        engine.addNebulaParticle(loc, velocity, 30, 1.5f, 0.2f, 0.6f, 2f, RIFT_LIGHTNING_COLOR);
    }
}
