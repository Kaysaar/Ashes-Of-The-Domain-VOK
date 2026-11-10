package data.kaysaar.aotd.vok.ui.research;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.PositionAPI;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOptionEra;
import data.kaysaar.aotd.vok.ui.basecomps.ResizableComponent;
import org.lazywizard.lazylib.ui.FontException;
import org.lazywizard.lazylib.ui.LazyFont;
import org.lwjgl.opengl.GL11;

import java.awt.Color;


public class TechTreeEraArea extends ResizableComponent {
    public static final float HEADER_TO_TECH_GAP_WORLD = 14f;
    public static final float HEADER_HEIGHT_WORLD = 84f;
    public static final float TITLE_SIZE_WORLD = 64f;

    public static final float HEADER_HEIGHT_PX = HEADER_HEIGHT_WORLD;
    public static final float HEADER_TO_TECH_GAP_PX = HEADER_TO_TECH_GAP_WORLD;

    private static final float TITLE_LEFT_WORLD = 14f;
    private static final float TITLE_TOP_WORLD = 6f;
    private static final float BACKGROUND_ALPHA = 0.12f;

    private final ResearchOptionEra era;
    private transient SpriteAPI fill;
    private transient LazyFont.DrawableString title;

    public TechTreeEraArea(ResearchOptionEra era, float width, float height) {
        this(era, width, height, 80f);
    }

    // Retained for compatibility with ResearchZoomPanel's existing call site.
    public TechTreeEraArea(ResearchOptionEra era, float width, float height, float headerSpaceWorld) {
        this.era = era;
        componentPanel = Global.getSettings().createCustom(width, height, this);
    }

    public void setZoomRange(float minZoom, float maxZoom) {
        // No min/max interpolation: all header geometry uses the same scale.
    }

    @Override
    public void renderBelow(float alphaMult) {
        if (alphaMult <= 0f) return;
        ensureFill();

        PositionAPI position = componentPanel.getPosition();
        float x = position.getX();
        float y = position.getY();
        float width = position.getWidth();
        float height = position.getHeight();

        fill.setNormalBlend();
        fill.setColor(era.getColor());
        fill.setAngle(0f);
        drawSpriteRect(x, y, width + 1f, height, alphaMult * BACKGROUND_ALPHA);
        // As before: a 1-pixel separator at the same alpha as the background.
        drawSpriteRect(x, y, 1f, height, alphaMult * BACKGROUND_ALPHA);
        resetFill();
    }


    @Override
    public void render(float alphaMult) {
        PositionAPI position = componentPanel.getPosition();
        float top = position.getY() + position.getHeight();
        drawAreaHeader(position.getX(), top, position.getWidth(), scale, alphaMult);
    }


    public void drawViewportHeader(float x, float top, float width, float zoom, float alphaMult) {
        // Compatibility no-op; no parent/zoom-panel changes are required.
    }

    private void drawAreaHeader(float x, float top, float width, float zoom, float alphaMult) {
        if (alphaMult <= 0f || width <= 0f || zoom <= 0f) return;
        ensureTitle();

        float headerHeight = HEADER_HEIGHT_WORLD * zoom;
        drawScreenSpaceHeader(x, top - headerHeight, width,
                headerHeight, alphaMult * BACKGROUND_ALPHA);
        drawTopAccent(x, top, width, zoom, alphaMult);

        float titleSize = TITLE_SIZE_WORLD * zoom;
        title.setFontSize(titleSize);
        // Do not refit the heading against its zoom-dependent rectangle.
        // A dynamic max width previously caused it to wrap/change height.
        title.setMaxWidth(Math.max(10000f, width));
        Color c = era.getColor();
        float opacity = Math.max(0f, Math.min(1f, alphaMult));
        title.setBaseColor(new Color(c.getRed() / 255f, c.getGreen() / 255f,
                c.getBlue() / 255f, opacity));
        title.draw(x + TITLE_LEFT_WORLD * zoom, top - TITLE_TOP_WORLD * zoom);
    }

    private void drawScreenSpaceHeader(float x, float y, float w, float h, float opacity) {
        if (w <= 0f || h <= 0f || opacity <= 0f) return;
        Color c = era.getColor();
        float alpha = Math.max(0f, Math.min(1f, opacity));

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_CURRENT_BIT);
        try {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(c.getRed() / 255f, c.getGreen() / 255f,
                    c.getBlue() / 255f, alpha);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2f(x, y);
            GL11.glVertex2f(x + w, y);
            GL11.glVertex2f(x + w, y + h);
            GL11.glVertex2f(x, y + h);
            GL11.glEnd();
        } finally {
            GL11.glPopAttrib();
        }
    }


    private void drawTopAccent(float x, float top, float width, float zoom, float alphaMult) {
        if (width <= 0f || alphaMult <= 0f) return;
        Color c = era.getColor();
        float r = c.getRed() / 255f;
        float g = c.getGreen() / 255f;
        float b = c.getBlue() / 255f;
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_CURRENT_BIT);
        try {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            // A short, low-alpha fade directly below the highlight.
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glColor4f(r, g, b, alphaMult * 0.14f);
            GL11.glVertex2f(x, top - 1f * zoom);
            GL11.glVertex2f(x + width, top - 1f * zoom);
            GL11.glColor4f(r, g, b, 0f);
            GL11.glVertex2f(x + width, top - 7f * zoom);
            GL11.glVertex2f(x, top - 7f * zoom);
            GL11.glEnd();
            GL11.glColor4f(r, g, b, Math.min(1f, alphaMult * 0.38f));
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2f(x, top - 1f * zoom);
            GL11.glVertex2f(x + width, top - 1f * zoom);
            GL11.glVertex2f(x + width, top);
            GL11.glVertex2f(x, top);
            GL11.glEnd();
        } finally {
            GL11.glPopAttrib();
        }
    }

    private void ensureTitle() {
        if (title != null) return;
        try {
            LazyFont font = LazyFont.FontLoader.loadFont(Fonts.ORBITRON_20AABOLD);
            title = font.createText(era.toString(), era.getColor(), TITLE_SIZE_WORLD);
            title.setBlendSrc(GL11.GL_SRC_ALPHA);
            title.setBlendDest(GL11.GL_ONE_MINUS_SRC_ALPHA);
        } catch (FontException e) {
            throw new RuntimeException("Unable to render research era heading", e);
        }
    }

    private void ensureFill() {
        if (fill == null) fill = Global.getSettings().getSprite("rendering", "GlitchSquare");
    }

    private void drawSpriteRect(float x, float y, float width, float height, float alpha) {
        if (width <= 0f || height <= 0f) return;
        fill.setSize(width, height);
        fill.setAlphaMult(alpha);
        fill.render(x, y);
    }

    private void resetFill() {
        fill.setColor(Color.WHITE);
        fill.setAlphaMult(1f);
    }
}
