package data.kaysaar.aotd.vok.ui.template.wip;

/** Maps UI currScale ∈ [minScale..1] to CombatViewport viewMult (smaller = zoomed-in). */
public final class ViewMultMapper {
    private static final float EPS = 1e-12f;

    public enum Neutral {
        AT_MIN, AT_MAX, AT_GEOMETRIC_MEAN
    }

    /** p = 1 for linear-in-log behavior; raise p>1 to make zoom feel steeper, p<1 gentler. */
    public static float map(float currScale, float minScale, float maxScale,
                            Neutral neutral, float p) {
        float s = Math.max(EPS, currScale);
        float ms = Math.max(EPS, minScale);
        float xs = Math.max(EPS, maxScale);

        float s0; // neutral scale where viewMult = 1
        switch (neutral) {
            case AT_MIN:  s0 = ms; break;
            case AT_MAX:  s0 = xs; break;
            default:      s0 = (float) Math.sqrt(ms * xs); break; // geometric mean
        }

        // Unbounded reciprocal mapping (no clamping):
        return (float) Math.pow(s0 / s, Math.max(EPS, p));
    }

    public static float map(float currScale, float minScale, float maxScale, Neutral neutral) {
        return map(currScale, minScale, maxScale, neutral, 1f);
    }
}

