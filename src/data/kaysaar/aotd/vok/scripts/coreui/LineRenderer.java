package data.kaysaar.aotd.vok.scripts.coreui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.ui.basecomps.ExtendUIPanelPlugin;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class LineRenderer implements ExtendUIPanelPlugin {
    CustomPanelAPI mainPanel;
    HashMap<String, UIComponentAPI> references;
    UIComponentAPI main;
    UIComponentAPI centerOfRows;
    UIPanelAPI insertTo;

    private transient SpriteAPI lineSprite = Global.getSettings().getSprite("rendering", "GlitchSquare");

    // --- Train System (single train at a time) ---
    private Train activeTrain = null;
    private final Random rng = new Random();
    CustomPanelAPI canva;
    public LineRenderer(UIPanelAPI main, HashMap<String, UIComponentAPI> references,
                        UIPanelAPI insertTo, UIPanelAPI centerOfRows) {
        this.mainPanel = Global.getSettings().createCustom(1, 1, this);
        this.references = references;
        this.main = main;
        this.centerOfRows = centerOfRows;
        insertTo.addComponent(mainPanel);
        this.insertTo = insertTo;
        canva = Global.getSettings().createCustom(Global.getSettings().getScreenWidth(),Global.getSettings().getScreenHeight(), null);

        insertTo.addComponent(canva).setLocation(0,0);
        ReflectionUtilis.invokeMethodWithAutoProjection("sendToBottomWithinItself", insertTo, mainPanel);
    }

    @Override
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    @Override
    public void createUI() {
    }

    @Override
    public void positionChanged(PositionAPI position) {
    }

    @Override
    public void renderBelow(float alphaMult) {
        if (main == null || references == null || references.isEmpty()) return;

        PositionAPI mainPos = main.getPosition();
        float mainX = mainPos.getCenterX();
        float mainY = mainPos.getCenterY() + 5f;
        float mainHalfHeight = mainPos.getHeight() / 2f;

        lineSprite.setColor(Color.CYAN);
        lineSprite.setAdditiveBlend();
        lineSprite.setAlphaMult(alphaMult);

        for (UIComponentAPI ref : references.values()) {
            PositionAPI refPos = ref.getPosition();
            float refX = refPos.getCenterX();
            float refY = refPos.getCenterY() + 5f;
            float refHalfHeight = refPos.getHeight() / 2f;
            float OffsetY = mainY - refY;

            // --- CYAN LINES (UNCHANGED) ---
            if (OffsetY != 0) {
                float step1Y = mainY + mainHalfHeight;
                float leg1 = mainHalfHeight;
                lineSprite.setSize(leg1, 3f);
                lineSprite.setAngle(90f);
                lineSprite.renderAtCenter(mainX - 1, mainY - refHalfHeight + 9);

                float leg2 = centerOfRows.getPosition().getX() - main.getPosition().getCenterX();
                lineSprite.setSize(Math.abs(leg2), 3f);
                lineSprite.setAngle(0f);
                if (leg2 < 0) {
                    lineSprite.render(mainX + (leg2), mainY - refHalfHeight - 19);

                } else {
                    lineSprite.render(mainX, mainY - refHalfHeight - 19);

                }

                if (OffsetY < 0) {
                    lineSprite.setSize(3f, Math.abs(OffsetY));
                    lineSprite.setAngle(0f);
                    lineSprite.render(mainX + (leg2), mainY - refHalfHeight - 19);
                } else {
                    lineSprite.setSize(3f, Math.abs(OffsetY));
                    lineSprite.setAngle(0f);
                    lineSprite.render(mainX + (leg2), mainY - refHalfHeight - 19 - OffsetY);
                }


                leg2 = centerOfRows.getPosition().getX() - ref.getPosition().getCenterX();
                lineSprite.setSize(Math.abs(leg2), 3f);

                if (leg2 < 0) {
                    lineSprite.render(refX + (leg2), mainY - refHalfHeight - 19 - (OffsetY));

                } else {
                    lineSprite.render(refX, mainY - refHalfHeight - 19 - (OffsetY));

                }
                lineSprite.setSize(leg1, 3f);
                lineSprite.setAngle(90f);
                lineSprite.renderAtCenter(refX - 1, refY - refHalfHeight + 9);
            } else {
                float step1Y = mainY + mainHalfHeight;
                float leg1 = mainHalfHeight;
                lineSprite.setSize(leg1, 3f);
                lineSprite.setAngle(90f);
                lineSprite.renderAtCenter(mainX - 1, mainY - refHalfHeight + 9);
                float leg2 = ref.getPosition().getCenterX() - mainX;
                lineSprite.setAngle(0f);
                lineSprite.setSize(Math.abs(leg2), 3f);
                if (leg2 < 0) {
                    lineSprite.render(mainX + (leg2), mainY - refHalfHeight - 19);

                } else {
                    lineSprite.render(mainX, mainY - refHalfHeight - 19);

                }
                lineSprite.setSize(leg1, 3f);
                lineSprite.setAngle(90f);
                lineSprite.renderAtCenter(refX - 1, refY - refHalfHeight + 9);
            }
        }

        // --- TRAIN RENDER (drawn in same pass to stay under other UI) ---
        if (activeTrain != null) {
            activeTrain.render(alphaMult);
        }
    }

    @Override
    public void render(float alphaMult) {
    }

    @Override
    public void advance(float amount) {
        // spawn a single train if none is active
        if (activeTrain == null && references != null && references.size() >= 2&&main!=null) {
            List<UIComponentAPI> inds = new ArrayList<>(references.values());
            UIComponentAPI start;

            UIComponentAPI end;
            boolean fromMain = Misc.random.nextBoolean();
            if (fromMain) {
                start = main;
                end = inds.get(rng.nextInt(inds.size()));
            } else {
                start = inds.get(rng.nextInt(inds.size()));;
                end = main;
            }
            if(start!=null&&end!=null){
                activeTrain = new Train(start, end, fromMain);
            }

        }

        if (activeTrain != null) {
            activeTrain.advance(amount);
            if (activeTrain.finished) {
                activeTrain = null; // remove, next tick may spawn a new random train
            }
        }
    }

    @Override
    public void processInput(List<InputEventAPI> events) {
    }

    @Override
    public void buttonPressed(Object buttonId) {
    }

    // =========================
    // =======  TRAIN  =========
    // =========================
    private class Train {
        // endpoints (industries)
        final UIComponentAPI start, end;

        // anchors that follow scroll (1x1 CustomPanels placed into insertTo)
        final List<CustomPanelAPI> anchors = new ArrayList<>();

        // polyline distances for constant-speed motion
        final List<Float> segLen = new ArrayList<>();
        final List<Float> segCum = new ArrayList<>();
        float totalLen = 0f;

        // motion
        float s = 0f;                   // current distance along path
        final float speed = 70f;       // px/sec
        final int wagons = 4;
        final float wagonSpacing = 12f; // px between wagons
        final float wagonW = 7f;
        final float wagonH = 7f;
        boolean isFromMain;
        boolean finished = false;
        final SpriteAPI wagonSprite = Global.getSettings().getSprite("rendering", "GlitchSquare");

        Train(UIComponentAPI start, UIComponentAPI end, boolean fromMain) {
            this.start = start;
            this.end = end;
            isFromMain = fromMain;
            wagonSprite.setColor(Color.YELLOW);
            wagonSprite.setAdditiveBlend();
            buildAnchorsAndLengths();
        }

        // Build anchors at the exact corners used by the cyan rails.
        // NOTE: UI placement uses inTL(x, yTopLeft), while render coords are bottom-left.
        private void buildAnchorsAndLengths() {

            PositionAPI mainPos, refPos;
                mainPos = start.getPosition();
                refPos = end.getPosition();

            float mainX = mainPos.getCenterX();
            float mainY = mainPos.getCenterY() + 5f;
            float mainHalfHeight = mainPos.getHeight() / 2f;
            float refX = refPos.getCenterX();
            float refY = refPos.getCenterY() + 5f;
            float refHalfHeight = refPos.getHeight() / 2f;
            float OffsetY = mainY - refY;

            // Same-row special case uses one shared midY
            if (Math.abs(OffsetY) == 0) {
                float leg2 = refPos.getCenterX() - mainX;
                addAnchorTLFromRender(mainX-1, mainY);          // start center
                addAnchorTLFromRender(mainX-1, (mainY-(refHalfHeight) - 17));   // vertical down to rail
                addAnchorTLFromRender(mainX-1 + leg2, (mainY-(refHalfHeight) -17) );  // horizontal to end column
                addAnchorTLFromRender(refX-1, refY);          // vertical up to end center
            } else {
                float leg2 = centerOfRows.getPosition().getX() - mainX
                ;
                if (leg2 < 0) {
//                    lineSprite.render(mainX + (leg2), mainY - refHalfHeight - 19);

                } else {
//                    lineSprite.render(mainX, mainY - refHalfHeight - 19);

                }

                addAnchorTLFromRender(mainX-1, mainY);          // start center
                addAnchorTLFromRender(mainX-1, mainY - refHalfHeight - 17);   // vertical down to rail
                addAnchorTLFromRender(mainX + (leg2)+1, mainY - refHalfHeight - 17); // horizontal to junction (centerOfRows X)
                addAnchorTLFromRender(mainX + (leg2)+1, mainY - refHalfHeight - 17 - OffsetY);   // vertical to target row's rail
                leg2 = centerOfRows.getPosition().getX() - refPos.getCenterX();



                addAnchorTLFromRender(refX + (leg2)+1, mainY - refHalfHeight - 17 - (OffsetY));
                addAnchorTLFromRender(refX-1, mainY - refHalfHeight - 17 - (OffsetY));   // horizontal to end column
                addAnchorTLFromRender(refX-1, refY);          // vertical up to end center
            }

            // precompute segment lengths & cumulative distances for constant-speed motion
            totalLen = 0f;
            segCum.clear();
            segLen.clear();
            if (anchors.size() >= 2) {
                segCum.add(0f);
                for (int i = 0; i < anchors.size() - 1; i++) {
                    PositionAPI a = anchors.get(i).getPosition();
                    PositionAPI b = anchors.get(i + 1).getPosition();
                    float ax = a.getCenterX();
                    float ay = a.getCenterY();
                    float bx = b.getCenterX();
                    float by = b.getCenterY();
                    float len = (float) Math.hypot(bx - ax, by - ay);
                    // guard against zero-length segments
                    if (len < 0.0001f) len = 0.0001f;
                    segLen.add(len);
                    totalLen += len;
                    segCum.add(totalLen);
                }
            }
        }

        // Converts render-space coords (origin bottom-left) to inTL placement (origin top-left),
        // then places a 1x1 anchor panel there so it follows scrolling automatically.
        private void addAnchorTLFromRender(float renderX, float renderY) {
            CustomPanelAPI p = Global.getSettings().createCustom(1, 1, null);
            insertTo.addComponent(p);
            float h = Global.getSettings().getScreenHeight()-(insertTo.getPosition().getY()+insertTo.getPosition().getHeight());
            float relativeY = Global.getSettings().getScreenHeight()-renderY-h;
            p.getPosition().inTL(renderX-insertTo.getPosition().getX(), relativeY);
            anchors.add(p);
        }

        void advance(float amount) {
            if (finished || anchors.size() < 2) return;

            s += speed * amount;

            // cleanup when past the end
            if (s > totalLen + wagonSpacing) {
                finished = true;
                for (CustomPanelAPI p : anchors) {
                    insertTo.removeComponent(p);
                }
                anchors.clear();
            }
        }

        void render(float alphaMult) {
            if (finished || anchors.size() < 2) return;

            wagonSprite.setAlphaMult(alphaMult);

            // draw head + wagons spaced behind along the polyline
            for (int i = 0; i < wagons; i++) {
                float d = s - i * wagonSpacing;
                if (d < 0) continue;
                if (d > totalLen) d = totalLen;

                // find segment for distance d
                int segIdx = findSegmentForDistance(d);
                if (segIdx < 0) continue;

                float segStart = segCum.get(segIdx);
                float segLength = segLen.get(segIdx);
                float t = (segLength <= 0f) ? 0f : (d - segStart) / segLength;

                PositionAPI a = anchors.get(segIdx).getPosition();
                PositionAPI b = anchors.get(segIdx + 1).getPosition();
                float ax = a.getCenterX();
                float ay = a.getCenterY();
                float bx = b.getCenterX();
                float by = b.getCenterY();

                float x = ax + (bx - ax) * t;
                float y = ay + (by - ay) * t;

                float angle = (float) Math.toDegrees(Math.atan2(by - ay, bx - ax));

                wagonSprite.setAdditiveBlend();
                wagonSprite.setColor(Color.YELLOW);
                wagonSprite.setSize(wagonW, wagonH); // 5x8 rectangle (wagon)
                wagonSprite.setAngle(angle);
                wagonSprite.renderAtCenter(x, y);
            }
        }

        private int findSegmentForDistance(float d) {
            // segCum holds 0, len1, len1+len2, ...
            // find lowest index i such that segCum[i+1] >= d
            int n = segLen.size();
            for (int i = 0; i < n; i++) {
                if (segCum.get(i + 1) >= d) return i;
            }
            return n - 1;
        }
    }
}
