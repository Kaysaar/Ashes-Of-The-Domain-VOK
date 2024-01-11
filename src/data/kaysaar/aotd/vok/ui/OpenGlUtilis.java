package data.kaysaar.aotd.vok.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class OpenGlUtilis {
    public void drawProgressionBar(PositionAPI pos, float alphaMult, Color uiColor, float progress) {
        if (uiColor == null) return;

        float x = pos.getX() - 3;
        float y = pos.getY();
        float w = pos.getWidth() + 6;
        float h = pos.getHeight();
        GL11.glColor4f(uiColor.getRed() / 255f, uiColor.getGreen() / 255f, uiColor.getBlue() / 255f,
                uiColor.getAlpha() / 255f * alphaMult * 23f);
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glRectf(x, y, x + w * progress, y + h);
        GL11.glPopMatrix();
    }
    void drawPanelBorder(CustomPanelAPI p) {
        GL11.glBegin(GL11.GL_LINE_LOOP);
        float x = p.getPosition().getX() - 5;
        float y = p.getPosition().getY();
        float w = p.getPosition().getWidth() + 10;
        float h = p.getPosition().getHeight();
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x + w, y);
        GL11.glVertex2f(x + w, y + h);
        GL11.glVertex2f(x, y + h);
        GL11.glEnd();
    }
    void drawPanelBorderPercentage(CustomPanelAPI p,float delta) {
        GL11.glBegin(GL11.GL_LINE_LOOP);
        float x = p.getPosition().getX() - 5;
        float y = p.getPosition().getY();
        float w = p.getPosition().getWidth();
        float h = p.getPosition().getHeight();
        w*=delta;
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x + w, y);
        GL11.glVertex2f(x + w, y + h);
        GL11.glVertex2f(x, y + h);
        GL11.glEnd();
    }
    void drawTopPanelBorder(CustomPanelAPI p, Color color,float alphaMult) {
        GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alphaMult);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        float x = p.getPosition().getX() - 5;
        float y = p.getPosition().getY();
        float w = p.getPosition().getWidth() + 10;
        float h = p.getPosition().getHeight();
        GL11.glVertex2f(x, y+h);
        GL11.glVertex2f(x + w, y+h);
        GL11.glEnd();
    }
    public  void drawHexagon(float x, float y, float width, float height) {
        // Assuming (x, y) are the top-left coordinates of the rectangle
        float hexRadius = Math.min(width, height) / 2.0f;

        GL11.glBegin(GL11.GL_POLYGON);

        for (int i = 0; i < 6; i++) {
            float angleRad = (float) (2.0 * Math.PI * i / 6.0);
            float xPos = x + width / 2.0f + hexRadius * (float) Math.cos(angleRad);
            float yPos = y + height / 2.0f + hexRadius * (float) Math.sin(angleRad);
            GL11.glVertex2f(xPos, yPos);
        }

        GL11.glEnd();
    }
    public void drawCircle( float centerX, float centerY, float radius) {
        glBegin(GL_TRIANGLE_FAN);
        glVertex2f(centerX, centerY); // Center of the circle
        for (int i = 0; i <= 360; i += 5) {
            double angle = Math.toRadians(i);
            float x = centerX + (float) (Math.cos(angle) * radius);
            float y = centerY + (float) (Math.sin(angle) * radius);
            glVertex2f(x, y);
        }
        glEnd();
    }
    public void drawCircleLine( float centerX, float centerY, float radius) {
        GL11.glBegin(GL11.GL_LINE_LOOP);
        for (int i = 0; i <= 360; i += 1) {
            double angle = Math.toRadians(i);
            float x = centerX + (float) (Math.cos(angle) * radius);
            float y = centerY + (float) (Math.sin(angle) * radius);
            GL11.glVertex2f(x, y);
        }
        GL11.glEnd();
    }
    void drawMainPanelBorder(CustomPanelAPI p,float width, float height) {
        GL11.glBegin(GL11.GL_LINE_LOOP);
        float x = p.getPosition().getX() - 2;
        float y = p.getPosition().getY() - 4;
        float w = width + 3;
        float h = height + 8;
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x + w, y);
        GL11.glVertex2f(x + w, y + h);
        GL11.glVertex2f(x, y + h);
        GL11.glEnd();
    }
    void drawMainPanelBorderSecondLayer(CustomPanelAPI p,float width, float height) {
        GL11.glBegin(GL11.GL_LINE_LOOP);
        float x = p.getPosition().getX() - 4;
        float y = p.getPosition().getY() - 2;
        float w = width + 7;
        float h = height + 4;
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x + w, y);
        GL11.glVertex2f(x + w, y + h);
        GL11.glVertex2f(x, y + h);
        GL11.glEnd();
    }
    void drawPanelBorder(PositionAPI p,boolean isResearched,float alphaMult,Color uiColor) {
        if(isResearched){
            GL11.glColor4f( uiColor.getRed()/ 255f, uiColor.getGreen() / 255f, uiColor.getBlue() / 255f,
                    uiColor.getAlpha() / 255f * alphaMult * 23f);
        }
        GL11.glBegin(GL11.GL_LINE_LOOP);
        float x = p.getX() + 5;
        float y = p.getY();
        float w = p.getWidth();
        float h = p.getHeight();
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x + w, y);
        GL11.glVertex2f(x + w, y + h);
        GL11.glVertex2f(x, y + h);
        GL11.glEnd();
    }
    void drawmask(CustomPanelAPI p) {
        GL11.glBegin(GL_QUADS);
        float x = p.getPosition().getX() - 6;
        float y = p.getPosition().getY();
        float w = p.getPosition().getWidth() + 11;
        float h = p.getPosition().getHeight();
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x + w, y);
        GL11.glVertex2f(x + w, y + h);
        GL11.glVertex2f(x, y + h);
        GL11.glEnd();
    }
    void drawTechLine(PositionAPI p1, PositionAPI p2, float correctorY) {
        glBegin(GL_LINE_LOOP);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

        if (p1.getCenterY() == p2.getCenterY()) {
            glVertex2f(p1.getCenterX() + p1.getWidth() / 2 + 5, p1.getCenterY());
            glVertex2f((p2.getCenterX() - p2.getWidth() / 2) + 5, p2.getCenterY());
            glEnd();
        } else {
            float distance = (p2.getCenterX() - p2.getWidth() / 2 - 5) - (p1.getCenterX() + p1.getWidth() / 2 + 5);

            glVertex2f(p1.getCenterX() + p1.getWidth() / 2 + 5, p1.getCenterY());
            glVertex2f(p1.getCenterX() + p1.getWidth() / 2 + 5 + 20, p1.getCenterY());
            glEnd();

            glBegin(GL_LINE_LOOP);
            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
            glVertex2f(p1.getCenterX() + p1.getWidth() / 2 + 5 + 20, p1.getCenterY());
            glVertex2f(p1.getCenterX() + p1.getWidth() / 2 + 5 + 20, p2.getCenterY() + correctorY);
            glEnd();

            glBegin(GL_LINE_LOOP);
            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
            glVertex2f(p1.getCenterX() + p1.getWidth() / 2 + 5 + 20, p2.getCenterY() + correctorY);
            glVertex2f((p2.getCenterX() - p2.getWidth() / 2) + 5, p2.getCenterY() + correctorY);
            glEnd();
        }

    }

}
