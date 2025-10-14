package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.bifrost.ui.dialog;

import com.fs.starfarer.api.campaign.SectorEntityToken;
import org.lwjgl.util.vector.Vector2f;

public class BifrostLocationData {
    public SectorEntityToken center;
    public float radius;
    public Vector2f locationOfGate;
    public float angle;

    public BifrostLocationData(SectorEntityToken center, float radius, Vector2f locationOfGate, float angle) {
        this.center = center;
        this.radius = radius;
        this.locationOfGate = locationOfGate;
        this.angle = angle;

    }
}
