package data.kaysaar.aotd.vok.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.SoundAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.util.FaderUtil;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.awt.*;

public class AoTDRelicEffect implements EveryFrameWeaponEffectPlugin {
    private static final Color BASE_GOLD = new Color(255, 215, 0, 255);
    private static final Color GOLD_ACCENT = new Color(255, 230, 100, 255);
    private static final Color DARK_GOLD = new Color(205, 173, 0, 255);
    private static final Color GOLD_PARTICLE = new Color(255, 215, 0, 200);
    private static final Color GOLD_PARTICLE_TRANSPARENT = new Color(255, 215, 0, 150);
    private static final Color GOLD_ARC = new Color(255, 200, 50, 255);

    private static final String PARTICLE_EFFECT_1 = "graphics/fx/ParticleEffect1.png";
    private static final String PARTICLE_EFFECT_2 = "graphics/fx/ParticleEffect2.png";

    private static String gungnirIcon = "graphics/icons/hud/Gungnir_icon.png";

    private boolean fired = false;
    private float lastChargeLevel = 0.0F;
    private SoundAPI chargeSound;

    //forced weapon cooldown
    private boolean hasFired = false;
    private float charge = 0f;
    private FaderUtil fader = new FaderUtil(1f,5f);
    public float reloadTime = 25;
    public float timeSpent;
    public transient boolean reloading= false;

    public  void initReload(){
        timeSpent = 0;
        reloading  = true;
    }

    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (engine.isPaused() || weapon == null || amount < 0) {
            //Added amount <0 because in Refit Screen this script is being initalized with amount being max negative float
            return;
        }
        float chargeLevel = weapon.getChargeLevel();
        Vector2f muzzleLocation = weapon.getFirePoint(0);
        if(weapon.getCooldownRemaining()>0){
            chargeLevel = 0;
        }
        if(reloading){
            timeSpent+=amount;
            if(timeSpent>reloadTime){
                timeSpent = 0;
                reloading = false;
                weapon.setForceDisabled(false);
            }
        }

        //From AoTDShadowLanceVFX
        if (reloading) {
            weapon.setForceDisabled(true);
            if (weapon.getShip() == Global.getCombatEngine().getPlayerShip()) {
                Global.getCombatEngine().maintainStatusForPlayerShip(
                        weapon.toString(),
                        gungnirIcon,
                        "Gungnir: Recharging",
                        Misc.getRoundedValue(Math.max(0,(this.reloadTime-timeSpent))) + " seconds remaining", // Purple Nebula's
//                        Misc.getRoundedValue(weapon.getCooldownRemaining()) + " secs before firing again", // Mayu's
                        false
                );
            }
        }

//        if (weapon.isFiring()) {
//            charge = weapon.getChargeLevel();
//            if (charge == 1f){
//                hasFired = true;
//            }
//        }else{
//            charge = 0f;
//        }
//        if (weapon.getGlowSpriteAPI().getAlphaMult() == 0){
//            hasFired = false;
//        }
//        if (hasFired && weapon.getGlowSpriteAPI().getAlphaMult() > 0.1f){
//            fader.fadeOut();
//            fader.advance(amount);
//            weapon.setGlowAmount(fader.getBrightness(),weapon.getSpec().getGlowColor());
//            weapon.getGlowSpriteAPI().setAlphaMult(fader.getBrightness());
//        }

        if (weapon.isFiring()) {
            ShipAPI source = weapon.getShip();
            if (source.getOwner() == 0 && Math.random() < (5.0f * amount) && !source.isStationModule() && !source.isStation()) {
                for(WeaponAPI other : source.getAllWeapons()) {
                    if (other != weapon && other.isFiring() && other.getId().contentEquals(weapon.getId())) {
                        engine.spawnEmpArc(source, muzzleLocation, source, source, DamageType.ENERGY, 50.0F, 200.0F, 150.0F, null,
                                MathUtils.getRandomNumberInRange(10.0F, 20.0F), GOLD_ACCENT, DARK_GOLD);
                        break;
                    }
                }
            }
        }

        if (chargeLevel > this.lastChargeLevel && weapon.isFiring()) {
            float duration = 0.8F * (1.0F - chargeLevel);
            float fadeIn = duration * 0.6F;
            float fadeOut = duration * 0.4F;

            for(int i = 0; (float)i < 200.0F * amount; ++i) {
                Vector2f point = MathUtils.getPointOnCircumference(muzzleLocation,
                        MathUtils.getRandomNumberInRange(80.0F, 100.0F) * (2.0F - chargeLevel),
                        (float) (Math.random() * 360f));
                Vector2f vel = Vector2f.sub(muzzleLocation, point, new Vector2f());
                vel.scale(1.0F / duration);

                String particleTexture = Math.random() > 0.5 ? PARTICLE_EFFECT_1 : PARTICLE_EFFECT_2;
                SpriteAPI particleSprite = null;

                try {
                    particleSprite = Global.getSettings().getSprite(particleTexture);
                } catch (Exception e) {
                    particleSprite = Global.getSettings().getSprite("fx", "particle_glow");
                }

                Color endColor = Misc.interpolateColor(GOLD_PARTICLE_TRANSPARENT, GOLD_ACCENT, chargeLevel);
                Color particleColor = Misc.interpolateColor(GOLD_PARTICLE, endColor, (float) Math.random());

                int alpha = (int) (255 * (0.5f + chargeLevel * 0.5f));
                particleColor = new Color(particleColor.getRed(), particleColor.getGreen(), particleColor.getBlue(), alpha);

                MagicRender.battlespace(
                        particleSprite,
                        point,
                        vel,
                        new Vector2f(MathUtils.getRandomNumberInRange(10.0F, 15.0F),
                                MathUtils.getRandomNumberInRange(10.0F, 15.0F)),
                        new Vector2f(MathUtils.getRandomNumberInRange(-2.0F, 0.0F),
                                MathUtils.getRandomNumberInRange(-2.0F, 0.0F)),
                        VectorUtils.getFacing(vel),
                        MathUtils.getRandomNumberInRange(-90f, 90f),
                        particleColor,
                        true,
                        0.0f,
                        fadeIn,
                        fadeOut
                );

                engine.addHitParticle(point, vel,
                        MathUtils.getRandomNumberInRange(8.0F, 12.0F),
                        1.0f,
                        fadeIn + fadeOut,
                        particleColor);
            }

            try {
                SpriteAPI glowSprite = Global.getSettings().getSprite(PARTICLE_EFFECT_1);
                MagicRender.battlespace(
                        glowSprite,
                        muzzleLocation,
                        new Vector2f(),
                        new Vector2f(200.0F * chargeLevel, 200.0F * chargeLevel),
                        new Vector2f(),
                        0f,
                        0f,
                        new Color(BASE_GOLD.getRed(), BASE_GOLD.getGreen(), BASE_GOLD.getBlue(),
                                (int)(200 * chargeLevel)),
                        true,
                        0.0f,
                        0.1f,
                        amount * 3.0F
                );
            } catch (Exception e) {
                engine.addSmoothParticle(muzzleLocation, new Vector2f(), 200.0F * chargeLevel, 1.0F, 0.5F, amount * 3.0F,
                        new Color(BASE_GOLD.getRed(), BASE_GOLD.getGreen(), BASE_GOLD.getBlue(),
                                (int)(200 * chargeLevel)));
            }

            if (!this.fired) {
                this.fired = true;
                this.chargeSound = Global.getSoundPlayer().playSound("RelicCharge", 1.0F, 1.0F, muzzleLocation, new Vector2f());

            } else {
                if (Math.random() < (7.0F * amount)) {
                    for(int i = 0; i < (int)(Math.random() * 2) + 1; ++i) {
                        float radius = 50.0F + MathUtils.getRandomNumberInRange(150.0F, 200.0F) * (1.0F - chargeLevel);
                        Vector2f spawnVector = AoTDCombatUtils.getRandomPointInShipCollisionBounds(weapon.getShip());
                        float width = MathUtils.getRandomNumberInRange(4.0F, 10.0F);

                        try {
                            SpriteAPI empSprite = Global.getSettings().getSprite(PARTICLE_EFFECT_2);
                            MagicRender.battlespace(
                                    empSprite,
                                    spawnVector,
                                    new Vector2f(),
                                    new Vector2f(width * 3f, width * 3f),
                                    new Vector2f(),
                                    0f,
                                    0f,
                                    GOLD_ARC,
                                    true,
                                    0.0f,
                                    0.1f,
                                    0.3f
                            );
                        } catch (Exception e) {
                        }

                        engine.spawnEmpArcVisual(muzzleLocation, weapon.getShip(), spawnVector, null, width,
                                GOLD_ARC, Color.white);
                    }
                }
            }
        } else {
            this.fired = false;
            this.lastChargeLevel = 0.0F;
            if (this.chargeSound != null) {
                this.chargeSound.stop();
            }

            this.chargeSound = null;
        }

        this.lastChargeLevel = chargeLevel;
    }
}