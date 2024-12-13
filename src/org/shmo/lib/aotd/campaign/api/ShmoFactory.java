package org.shmo.lib.aotd.campaign.api;

import org.json.JSONArray;
import org.json.JSONObject;
import org.shmo.lib.aotd.campaign.impl.BaseShmoCustomLaser;
import org.shmo.lib.aotd.campaign.impl.BaseShmoCustomLaserSpec;
import org.shmo.lib.aotd.campaign.impl.BaseShmoStateMachine;

import java.awt.*;

public class ShmoFactory {
    public static ShmoCustomLaserSpec createCustomLaserSpec() {
        return new BaseShmoCustomLaserSpec();
    }

    public static ShmoCustomLaserSpec cloneCustomLaserSpec(ShmoCustomLaserSpec spec) {
        if (spec == null)
            return null;
        return new BaseShmoCustomLaserSpec(spec);
    }

    public static ShmoCustomLaserSpec createCustomLaserSpec(JSONObject jsonObject) {
        if (jsonObject == null)
            return null;
        try {
            final String id = jsonObject.optString("id", null);
            final String coreSprite = jsonObject.optString("coreSprite", null);
            final String fringeSprite = jsonObject.optString("fringeSprite", null);
            final float beamWidth = (float)jsonObject.optDouble("beamWidth", -1);
            final Color coreColor;
            final Color fringeColor;
            final float coreAlphaMult = (float)jsonObject.optDouble("coreAlphaMult", -1);
            final float fringeAlphaMult = (float)jsonObject.optDouble("fringeAlphaMult", -1);
            final float animationSpeed = (float)jsonObject.optDouble("animationSpeed", -1);
            final float fadeInTime = (float)jsonObject.optDouble("fadeInTime", -1);
            final float fadeOutTime = (float)jsonObject.optDouble("fadeOutTime", -1);

            final JSONArray coreColorArray = jsonObject.getJSONArray("coreColor");
            if (coreColorArray != null && coreColorArray.length() >= 3) {
                coreColor = new Color(
                        coreColorArray.optInt(0, 255),
                        coreColorArray.optInt(1, 255),
                        coreColorArray.optInt(2, 255),
                        coreColorArray.length() > 3 ? (coreColorArray.optInt(3, 255)) : 255
                );
            } else {
                coreColor = null;
            }

            final JSONArray fringeColorArray = jsonObject.getJSONArray("fringeColor");
            if (fringeColorArray != null && fringeColorArray.length() >= 3) {
                fringeColor = new Color(
                        fringeColorArray.optInt(0, 255),
                        fringeColorArray.optInt(1, 255),
                        fringeColorArray.optInt(2, 255),
                        fringeColorArray.length() > 3 ? (fringeColorArray.optInt(3, 255)) : 255
                );
            } else {
                fringeColor = null;
            }

            final ShmoCustomLaserSpec spec = createCustomLaserSpec();
            spec.setId(id);
            spec.setCoreSprite(coreSprite);
            spec.setFringeSprite(fringeSprite);
            spec.setCoreWidth(beamWidth);
            spec.setCoreColor(coreColor);
            spec.setFringeColor(fringeColor);
            spec.setCoreAlphaMult(coreAlphaMult);
            spec.setFringeAlphaMult(fringeAlphaMult);
            spec.setAnimationSpeed(animationSpeed);
            spec.setFadeInTime(fadeInTime);
            spec.setFadeOutTime(fadeOutTime);

            return spec;

        } catch (Exception ignored) {}

        return null;
    }

    public static ShmoCustomLaser createCustomLaser(ShmoCustomLaserSpec spec) {
        final BaseShmoCustomLaser laserRenderer = new BaseShmoCustomLaser();
        laserRenderer.setSpec(spec);
        return laserRenderer;
    }

    public static ShmoStateMachine createStateMachine() {
        return new BaseShmoStateMachine();
    }
}
