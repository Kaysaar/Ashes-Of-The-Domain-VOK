package data.kaysaar.aotd.vok.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import org.lwjgl.util.vector.Vector2f;

import java.util.List;

public class AoTDGungnirRelicProjCorrector  extends BaseEveryFrameCombatPlugin{
    private transient float originalAngularVelocity;
    private transient float facing;
    private transient Vector2f originalVelocity;
    private DamagingProjectileAPI proj;
    float elapsed = 0f;
    public float maxSpeed = 6000f;

    public AoTDGungnirRelicProjCorrector(DamagingProjectileAPI proj){
        this.proj = proj;
        originalAngularVelocity = proj.getAngularVelocity();
        this.facing = proj.getFacing();
        this.originalVelocity = new Vector2f(proj.getVelocity());
    }
    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        CombatEngineAPI engine = Global.getCombatEngine();
        if(engine.isPaused() || proj == null ) return;
        if(elapsed >= 5f){
            cleanUp();
        }
         elapsed+=engine.getElapsedInLastFrame();
        float speed = proj.getVelocity().length();
        float differnce =  speed - maxSpeed;
        if(differnce>0){
            float factor = maxSpeed / speed;
            proj.getVelocity().scale(factor);

        }

    }
    public void cleanUp(){
        CombatEngineAPI engine = Global.getCombatEngine();
        engine.removePlugin(this);
    }
}
