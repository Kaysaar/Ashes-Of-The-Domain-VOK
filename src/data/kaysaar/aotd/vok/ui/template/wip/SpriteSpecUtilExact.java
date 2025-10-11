package data.kaysaar.aotd.vok.ui.template.wip;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.loading.WeaponSlotAPI;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import org.lwjgl.util.vector.Vector2f;

import java.util.List;
import java.util.Objects;

public final class SpriteSpecUtilExact {

    private SpriteSpecUtilExact() {}

    /** Clean replacement for the obfuscated sprite spec. */
    public static final class SpriteSpec {
        public final String id;
        public final float width;
        public final float height;
        public final float centerX;
        public final float centerY;

        public SpriteSpec(String id, float width, float height, float centerX, float centerY) {
            this.id = id;
            this.width = width;
            this.height = height;
            this.centerX = centerX;
            this.centerY = centerY;
        }
    }
    public static Vector2f methodRandom(float var0, float var1, float var2, float var3) {
        float var4 = var0 / var1;
        float var5 = var3 * var4;
        float var6 = var3;
        if (var5 > var2) {
            var5 = var2;
            var6 = var2 / var4;
        }

        return new Vector2f(var5, var6);
    }
    /** EXACT vanilla-equivalent of getSizeWithModules(), but for ShipAPI. */
    public static SpriteSpec computeSizeWithModules(ShipAPI ship) {
        Objects.requireNonNull(ship, "ship");

        // --- Base hull sprite (W/H/CX/CY) ---
        SpriteAPI base = ship.getSpriteAPI();
        if (base == null && ship.getHullSpec() != null) {
            // Fallback – should rarely be needed
            String spriteName = ship.getHullSpec().getSpriteName();
            if (spriteName != null) base = Global.getSettings().getSprite(spriteName);
        }
        if (base == null) return new SpriteSpec(ship.getId(), 0f, 0f, 0f, 0f);

        final float baseW  = base.getWidth();
        final float baseH  = base.getHeight();
        final float baseCX = base.getCenterX();
        final float baseCY = base.getCenterY();

        // No modules -> return base spec
        List<ShipAPI> children = ship.getChildModulesCopy();
        if (ship.getVariant().getModuleSlots().isEmpty()) {
            return new SpriteSpec(ship.getId(), baseW, baseH, baseCX, baseCY);
        }

        // Initial extents in the "parent-center" frame: min = -center, max = W - center
        float minX = -baseCX;
        float minY = -baseCY;
        float maxX =  baseW - baseCX;
        float maxY =  baseH - baseCY;

        // Save the initial extents (vanilla uses these to compute deltas)
        final float initMinX = minX;
        final float initMinY = minY;
        final float initMaxX = maxX;
        final float initMaxY = maxY;

        // --- Expand with each child module (EXACT logic) ---
        for (String s : ship.getVariant().getModuleSlots()) {
            WeaponSlotAPI child = ship.getVariant().getSlot(s);
            ShipVariantAPI spec = (ShipVariantAPI) ReflectionUtilis.invokeMethodWithAutoProjection("getModuleVariant",ship.getVariant(),child.getId());

            // Module sprite (for half-extents)


            SpriteAPI mod = Global.getCombatEngine().createFXDrone(spec).getSpriteAPI();
            if (mod == null) continue;

            final float mW = mod.getWidth();
            final float mH = mod.getHeight();
            float halfW = mW * 0.5f;
            float halfH = mH * 0.5f;

            // Slot on the parent this module is attached to
            WeaponSlotAPI slot = child;
            if (slot == null) continue;

            // Anchor from module hull spec (may be null) – must be fetched via reflection
            Vector2f moduleAnchor=   spec.getHullSpec().getModuleAnchor();


            // EXACT vanilla: position via slot.computeRelativePosition(90f, anchor)
            // (anchor affects position ONLY – not extents)
            Vector2f p = (Vector2f) ReflectionUtilis.invokeMethodWithAutoProjection("computeRelativePosition",slot, 90.0f, moduleAnchor);
            if (p == null) {
                // Fallback if reflection fails: vanilla uses computeRelativePosition; location is the next best thing
                Vector2f loc = slot.getLocation();
                p = (loc != null) ? new Vector2f(loc) : new Vector2f();
            }

            // EXTENTS: if slot angle != 0, use square with radius = max(halfW, halfH)
            float extentX = halfW;
            float extentY = halfH;
            if (Math.abs(slot.getAngle()) > 0.0001f) {
                float r = Math.max(halfW, halfH);
                extentX = r;
                extentY = r;
            }

            // Expand bounds around p
            float cand;

            cand = p.x + extentX; if (cand > maxX) maxX = cand;
            cand = p.x - extentX; if (cand < minX) minX = cand;
            cand = p.y + extentY; if (cand > maxY) maxY = cand;
            cand = p.y - extentY; if (cand < minY) minY = cand;
        }

        // --- EXACT vanilla-style finish using deltas (matches decompiled structure) ---
        final float dxMin = initMinX - minX; // (oldMin - newMin)
        final float dxMax = maxX - initMaxX; // (newMax - oldMax)
        final float dyMin = initMinY - minY;
        final float dyMax = maxY - initMaxY;

        // Vanilla had a scale "var27" that ends up 1.0f, and an offset "var28" that ends up 0.0f
        final float scale = 1.0f;
        final float offs  = 0.0f;

        final float outW  = (initMaxX - initMinX) + (dxMin + dxMax) * scale + offs; // == (maxX - minX)
        final float outH  = (initMaxY - initMinY) + (dyMin + dyMax) * scale + offs; // == (maxY - minY)
        final float outCX = baseCX + dxMin; // == -minX
        final float outCY = baseCY + dyMin; // == -minY

        return new SpriteSpec(ship.getId(), outW, outH, outCX, outCY);
    }

    // ===== Reflection helpers (EXACT behavior hooks) =====


}
