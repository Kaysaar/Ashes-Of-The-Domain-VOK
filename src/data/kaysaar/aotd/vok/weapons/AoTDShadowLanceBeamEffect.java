package data.kaysaar.aotd.vok.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.EmpArcEntityAPI.EmpArcParams;
import com.fs.starfarer.api.impl.combat.NegativeExplosionVisual.NEParams;
import com.fs.starfarer.api.impl.combat.RiftCascadeMineExplosion;
import com.fs.starfarer.api.impl.combat.dweller.DarkenedGazeSystemScript;
import com.fs.starfarer.api.impl.combat.dweller.RiftLightningEffect;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AoTDShadowLanceBeamEffect implements BeamEffectPlugin {

	public static float RIFT_DAMAGE = 200f; // happens approximately every 1.25 seconds; ~160 dps per beam
	public static float DAMAGE_MULT_NORMAL_WEAPON = 0.5f;
	protected IntervalUtil fireInterval = new IntervalUtil(0.2f, 0.3f);
	private IntervalUtil RiftLightningBullshit = new IntervalUtil(0.1f, 1f);
	private IntervalUtil BeamInterval = new IntervalUtil(0.1f, 0.2f);
	protected boolean hadDamageTargetPrev = false;
	protected boolean lengthChangedPrev = false;
	protected float sinceRiftSpawn = 0f;
	protected Vector2f prevTo = null;
	protected Vector2f prevFrom = null;
	boolean spawnedExplosion = false;
	float countdownOne = 1f;

	// Flux Spiker
	private static final float TARGET_RANGE = 500f;
	private static final float FLUXRAISE_MULT = 1.5f;
	private boolean triggered = false;
	final List<ShipAPI> OBJECTS = new ArrayList();
	final List<ShipAPI> FTR = new ArrayList();
	final Vector2f point = new Vector2f();
	float EMP_DAMAGE = 0f;
	
	public AoTDShadowLanceBeamEffect() {
		fireInterval.randomize();
		BeamInterval.randomize();
	}

	@Override
	public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
		if (beam.getSource() == null || beam.getWeapon() == null) {
			return;
		}

		// If this shit doesn't work then I don't fucking know anymore
		float overlevel = 1f;

		BeamInterval.advance(amount);
		if (BeamInterval.intervalElapsed()) {
			overlevel = countdownOne += 1 * amount;

			beam.setCoreColor(
					new Color(255, 255, 255,
							AoTDShadowLanceBeamEffect.clamp255((int) (250 * Math.sqrt(overlevel)))));

			beam.setFringeColor(
					new Color(
							beam.getFringeColor().getRed(), beam.getFringeColor().getGreen(), beam.getFringeColor().getBlue(),
							AoTDShadowLanceBeamEffect.clamp255((int) (250 * Math.sqrt(overlevel)))));
		}

		boolean normalWeaponMode = !beam.getSource().hasTag(DarkenedGazeSystemScript.DARKENED_GAZE_SYSTEM_TAG);
		boolean primary = beam.getWeapon().getCustom() == DarkenedGazeSystemScript.DARKENED_GAZE_PRIMARY_WEAPON_TAG;
		if (normalWeaponMode) primary = true;
		
		sinceRiftSpawn += amount;
		
		float maxRange = beam.getWeapon().getRange();
		Vector2f from = beam.getFrom();
		Vector2f to = beam.getRayEndPrevFrame();
		Vector2f to2 = beam.getTo();
		float dist = Misc.getDistance(from, to);
		float dist2 = Misc.getDistance(from, to2);

		if (dist2 < dist) {
			to = to2;
			dist = dist2;
		}

		boolean hasDamageTarget = beam.getDamageTarget() != null;
		boolean lengthChanged = prevTo == null || Math.abs(Misc.getDistance(prevFrom, prevTo) - Misc.getDistance(from, to)) > 2f;
		
		boolean forceRiftSpawn = (hasDamageTarget && !hadDamageTargetPrev) || (!lengthChanged && lengthChangedPrev);

		if (!primary) {
			forceRiftSpawn = false;
		}
		
		lengthChangedPrev = lengthChanged;
		hadDamageTargetPrev = hasDamageTarget;
		prevFrom = new Vector2f(from);
		prevTo = new Vector2f(to);

		fireInterval.advance(amount);
		if (fireInterval.intervalElapsed() || forceRiftSpawn) {
			if (beam.getDamageTarget() == null && dist < maxRange * 0.9f) {
				return;
			}
			if (beam.getBrightness() < 1) {
				return;
			}
			
			Color color = RiftLightningEffect.RIFT_LIGHTNING_COLOR;
			
			spawnedExplosion = false;
			float maxTimeWithoutExplosion = 1f;
			if (normalWeaponMode) {
				maxTimeWithoutExplosion = 0.5f;
			}
			if ((float) Math.random() > 0.8f || forceRiftSpawn || (primary && sinceRiftSpawn > maxTimeWithoutExplosion)) {
				DamagingProjectileAPI explosion = engine.spawnDamagingExplosion(
								createExplosionSpec(normalWeaponMode ? DAMAGE_MULT_NORMAL_WEAPON : 1f),
								beam.getSource(), to
				);

				float distFactor = 0f;

				if (dist > 500f) {
					distFactor = (dist - 500f) / 1500f;
					if (distFactor < 0f) distFactor = 0f;
					if (distFactor > 1f) distFactor = 1f;
				}
				
				float sizeAdd = 5f * distFactor;
				float baseSize = 15f;
				if (normalWeaponMode) {
					baseSize *= 0.5f;
					sizeAdd = 0f; // beam is not getting wider at end, so don't increase explosion size
				}
				
				NEParams p = RiftCascadeMineExplosion.createStandardRiftParams(
											color, baseSize + sizeAdd
				);
				p.noiseMult = 6f;
				p.thickness = 25f;
				p.fadeOut = 0.5f;
				p.spawnHitGlowAt = 1f;
				p.additiveBlend = true;
				p.blackColor = Color.white;
				p.underglow = null;
				p.withNegativeParticles = false;
				p.withHitGlow = false;
				p.fadeIn = 0f;
				
				RiftCascadeMineExplosion.spawnStandardRift(explosion, p);
				
				spawnedExplosion = true;
				sinceRiftSpawn = 0f;
			}
		}

		RiftLightningBullshit.advance(amount);
		if (RiftLightningBullshit.intervalElapsed()) {
			Color color = RiftLightningEffect.RIFT_LIGHTNING_COLOR;

			if (dist > 100f && ((float) Math.random() > 0.5f || (normalWeaponMode && spawnedExplosion))) {
				//if (dist > 100f && spawnedExplosion) {
				EmpArcParams params = new EmpArcParams();
				params.segmentLengthMult = 8f;
				params.zigZagReductionFactor = 0.15f;
				params.fadeOutDist = 100f; //50f
				params.minFadeOutMult = 10f;
				params.flickerRateMult = 0.5f;

				float fraction = Math.min(0.33f, 300f / dist);
				params.brightSpotFullFraction = fraction;
				params.brightSpotFadeFraction = fraction;

				float arcSpeed = RiftLightningEffect.RIFT_LIGHTNING_SPEED;
				params.movementDurOverride = Math.max(0.05f, dist / arcSpeed);

				ShipAPI ship = beam.getSource();
				EmpArcEntityAPI arc = (EmpArcEntityAPI) engine.spawnEmpArcVisual(
						from,
						ship,
						to,
						ship,
						80f, // thickness
						color,
						new Color(255, 255, 255, 255),
						params
				);
				arc.setCoreWidthOverride(40f);
				arc.setRenderGlowAtStart(false);
				arc.setFadedOutAtStart(true);
				arc.setSingleFlickerMode(true);

				Vector2f pt = Vector2f.add(from, to, new Vector2f());
				pt.scale(0.5f);

				Global.getSoundPlayer().playSound("abyssal_glare_lightning", 1f, 1f, pt, new Vector2f());
			}
		}
		
		if (normalWeaponMode) {
			Vector2f pt = Vector2f.add(from, to, new Vector2f());
			pt.scale(0.5f);
			Global.getSoundPlayer().playLoop("abyssal_glare_loop", 
											 beam.getSource(), 1f, beam.getBrightness(),
											 pt, beam.getSource().getVelocity());
		}
		else if (primary) {
			Vector2f pt = Vector2f.add(from, to, new Vector2f());
			pt.scale(0.5f);
			Global.getSoundPlayer().playLoop("darkened_gaze_loop", 
					 beam.getSource(), 1f, beam.getBrightness(),
					 pt, beam.getSource().getVelocity());
		}

		// Spike the target's flux
		final ShipAPI Ship = beam.getSource();
		EMP_DAMAGE = beam.getWeapon().getDamage().getDamage() * 0.50f;
		beam.getDamage().setForceHardFlux(true);
		if (beam.getWeapon().getChargeLevel() == 1 && beam.getDamageTarget() != null && !triggered) {
			triggered = true;
			point.setX(beam.getTo().getX());
			point.setY(beam.getTo().getY());
			for (final ShipAPI Target : CombatUtils.getShipsWithinRange(point, TARGET_RANGE)) {
				if (Target != Ship && Target.isAlive() && Target != beam.getDamageTarget() && !Target.isAlly() && !Target.isFighter() && !Target.isStationModule() && Target.getOriginalOwner() != Ship.getOriginalOwner()) {
					OBJECTS.add(Target);
				}
				// calculate a number to raise target flux by
				final float fluxmult = beam.getWeapon().getDamage().getDamage() * FLUXRAISE_MULT;
				// get target max flux level
				final float maxflux = Target.getMaxFlux();
				//Check that the target can handle the flux; if so, raise target ship flux on hull hit
				if (maxflux > (fluxmult * 2f)) { //1.5f
					// Less harsh and more banal
					//Target.getFluxTracker().increaseFlux(fluxmult, true);
					// Now raise the target's flux level by 30%
					Target.getFluxTracker().increaseFlux(maxflux * 0.3f, true);
				}
			}
			for (final ShipAPI Fighter : CombatUtils.getShipsWithinRange(point, TARGET_RANGE)) {
				if (Fighter.isFighter() && Fighter.isAlive() && !Fighter.isAlly() && Fighter.getOriginalOwner() != Ship.getOriginalOwner()) {
					FTR.add(Fighter);
				}
				Collections.shuffle(OBJECTS);
				Collections.shuffle(FTR);
			}
		}
	}
	
	public DamagingExplosionSpec createExplosionSpec(float damageMult) {
		float damage = RIFT_DAMAGE * damageMult;
		DamagingExplosionSpec spec = new DamagingExplosionSpec(
				0.1f, // duration
				75f, // radius
				50f, // coreRadius
				damage, // maxDamage
				damage / 2f, // minDamage
				CollisionClass.PROJECTILE_FF, // collisionClass
				CollisionClass.PROJECTILE_FIGHTER, // collisionClassByFighter
				3f, // particleSizeMin
				3f, // particleSizeRange
				0.5f, // particleDuration
				0, // particleCount
				new Color(255,255,255,0), // particleColor
				new Color(255,100,100,0)  // explosionColor
				);

		spec.setDamageType(DamageType.ENERGY);
		spec.setUseDetailedExplosion(false);
		spec.setSoundSetId("abyssal_glare_explosion");
		spec.setSoundVolume(damageMult);
		return spec;		
	}

	public static int clamp255(int x) {
		return Math.max(240, Math.min(255, x));
	}

}