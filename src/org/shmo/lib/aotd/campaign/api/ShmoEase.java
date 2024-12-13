package org.shmo.lib.aotd.campaign.api;

public class ShmoEase {
    public static float outBounce(float t) {
        t = Math.max(0f, Math.min(t, 1f));
        final float n1 = 7.5625f;
        final float d1 = 2.75f;

        if (t < 1f / d1) {
            return n1 * t * t;
        } else if (t < 2f / d1) {
            return n1 * (t -= 1.5f / d1) * t + 0.75f;
        } else if (t < 2.5f / d1) {
            return n1 * (t -= 2.25f / d1) * t + 0.9375f;
        } else {
            return n1 * (t -= 2.625f / d1) * t + 0.984375f;
        }
    }

    public static float inBounce(float t) {
        return 1f - outBounce(1f - t);
    }

    public static float inOutBounce(float t) {
        return t < 0.5f
                ? (1f - outBounce(1f - 2f * t)) / 2f
                : (1f + outBounce(2f * t - 1f)) / 2f;
    }
}
