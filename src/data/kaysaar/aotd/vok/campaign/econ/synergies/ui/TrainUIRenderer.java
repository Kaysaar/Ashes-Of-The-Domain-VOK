package data.kaysaar.aotd.vok.campaign.econ.synergies.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.BaseIndustrySynergy;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.ui.basecomps.ExtendUIPanelPlugin;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.*;

import static org.lwjgl.opengl.GL11.GL_BLEND;

public class TrainUIRenderer implements ExtendUIPanelPlugin {
    CustomPanelAPI mainPanel;
    HashMap<String, UIComponentAPI> references;
    UIComponentAPI main;
    UIComponentAPI centerOfRows;
    UIPanelAPI insertTo;
    MarketAPI market;
    ArrayList<BaseIndustrySynergy> synergiesPresent;
    BaseIndustrySynergy currentSynergy;
    private transient SpriteAPI lineSprite = Global.getSettings().getSprite("rendering", "GlitchSquare");

    private Train activeTrain = null;
    private final Random rng = new Random();
    CustomPanelAPI canva;

    private static class Trip {
        final UIComponentAPI start, end;
        final int wagons;
        final Color color;
        final BaseIndustrySynergy synergy;
        Trip(UIComponentAPI s, UIComponentAPI e, int w, Color c, BaseIndustrySynergy syn) {
            start = s; end = e; wagons = w; color = c; synergy = syn;
        }
    }
    private final ArrayList<Trip> tripQueue = new ArrayList<>();

    public TrainUIRenderer(UIPanelAPI main, HashMap<String, UIComponentAPI> references,
                           UIPanelAPI insertTo, UIPanelAPI centerOfRows, ArrayList<BaseIndustrySynergy> synergies) {
        this.mainPanel = Global.getSettings().createCustom(1, 1, this);
        this.references = references;
        this.main = main;
        this.centerOfRows = centerOfRows;
        insertTo.addComponent(mainPanel);
        this.insertTo = insertTo;
        canva = Global.getSettings().createCustom(Global.getSettings().getScreenWidth(), Global.getSettings().getScreenHeight(), null);
        insertTo.addComponent(canva).setLocation(0, 0);
        this.synergiesPresent = synergies;
        ReflectionUtilis.invokeMethodWithAutoProjection("sendToBottomWithinItself", insertTo, mainPanel);
    }

    @Override
    public CustomPanelAPI getMainPanel() { return mainPanel; }

    @Override
    public void createUI() { }

    @Override
    public void positionChanged(PositionAPI position) { }

    @Override
    public void renderBelow(float alphaMult) {
        if (main == null || references == null || references.isEmpty()) return;

        PositionAPI mainPos = main.getPosition();
        float mainX = mainPos.getCenterX();
        float mainY = mainPos.getCenterY() + 5f;
        float mainHalfHeight = mainPos.getHeight() / 2f;

        lineSprite.setColor(Color.CYAN);
        lineSprite.setNormalBlend();
        lineSprite.setAlphaMult(alphaMult);

        for (UIComponentAPI ref : references.values()) {
            PositionAPI refPos = ref.getPosition();
            float refX = refPos.getCenterX();
            float refY = refPos.getCenterY() + 5f;
            float refHalfHeight = refPos.getHeight() / 2f;
            float OffsetY = mainY - refY;

            if (OffsetY != 0) {
                float step1Y = mainY + mainHalfHeight;
                float leg1 = mainHalfHeight;
                lineSprite.setSize(leg1, 3f);
                lineSprite.setAngle(90f);
                lineSprite.renderAtCenter(mainX - 1, mainY - refHalfHeight + getSecondValueForY());

                float leg2 = centerOfRows.getPosition().getX() - main.getPosition().getCenterX();
                lineSprite.setSize(Math.abs(leg2), 3f);
                lineSprite.setAngle(0f);
                if (leg2 < 0) {
                    lineSprite.render(mainX + (leg2), mainY - refHalfHeight - getValueForY());
                } else {
                    lineSprite.render(mainX, mainY - refHalfHeight - getValueForY());
                }

                if (OffsetY < 0) {
                    lineSprite.setSize(3f, Math.abs(OffsetY)+1);
                    lineSprite.setAngle(0f);
                    lineSprite.render(mainX + (leg2), mainY - refHalfHeight - getValueForY());
                } else {
                    lineSprite.setSize(3f, Math.abs(OffsetY)+1);
                    lineSprite.setAngle(0f);
                    lineSprite.render(mainX + (leg2), mainY - refHalfHeight - getValueForY() - OffsetY);
                }

                leg2 = centerOfRows.getPosition().getX() - ref.getPosition().getCenterX();
                lineSprite.setSize(Math.abs(leg2), 3f);
                if (leg2 < 0) {
                    lineSprite.render(refX + (leg2), mainY - refHalfHeight - getValueForY() - (OffsetY));
                } else {
                    lineSprite.render(refX, mainY - refHalfHeight - getValueForY() - (OffsetY));
                }
                lineSprite.setSize(leg1, 3f);
                lineSprite.setAngle(90f);
                lineSprite.renderAtCenter(refX - 1, refY - refHalfHeight + getSecondValueForY());
            } else {
                float step1Y = mainY + mainHalfHeight;
                float leg1 = mainHalfHeight;
                lineSprite.setSize(leg1, 3f);
                lineSprite.setAngle(90f);
                lineSprite.renderAtCenter(mainX - 1, mainY - refHalfHeight + getSecondValueForY());
                float leg2 = ref.getPosition().getCenterX() - mainX;
                lineSprite.setAngle(0f);
                lineSprite.setSize(Math.abs(leg2), 3f);
                if (leg2 < 0) {
                    lineSprite.render(mainX + (leg2), mainY - refHalfHeight - getValueForY());
                } else {
                    lineSprite.render(mainX, mainY - refHalfHeight - getValueForY());
                }
                lineSprite.setSize(leg1, 3f);
                lineSprite.setAngle(90f);
                lineSprite.renderAtCenter(refX - 1, refY - refHalfHeight + getSecondValueForY());
            }
        }


    }

    private static int getSecondValueForY() {
        return getValueForY()-22;
    }

    private static int getValueForY() {
        return 25;
    }

    @Override
    public void render(float alphaMult) {
        if (activeTrain != null) activeTrain.render(alphaMult);
    }

    @Override
    public void advance(float amount) {
        if (activeTrain == null) {
            if (tripQueue.isEmpty()) rebuildTripQueue();

            if (!tripQueue.isEmpty()) {
                Trip t = tripQueue.remove(0);
                currentSynergy = t.synergy;
                activeTrain = new Train(t.start, t.end, t.wagons, t.color);
                for (int j = 0; j < activeTrain.getJunctionCount(); j++) {
                    activeTrain.setTurn(j, 0.5f, 0f);
                }
            }
        }

        if (activeTrain != null) {
            activeTrain.advance(amount);
            if (activeTrain.finished) activeTrain = null;
        }
    }

    @Override
    public void processInput(List<InputEventAPI> events) { }

    @Override
    public void buttonPressed(Object buttonId) { }

    private void rebuildTripQueue() {
        tripQueue.clear();

        if (synergiesPresent != null && !synergiesPresent.isEmpty()) {
            ArrayList<BaseIndustrySynergy> order = new ArrayList<>(synergiesPresent);
            Collections.shuffle(order, rng);

            for (BaseIndustrySynergy syn : order) {
                ArrayList<String> ids = new ArrayList<>(syn.getIndustriesForSynergy());
                if (ids.isEmpty()) continue;
                Collections.shuffle(ids, rng);

                for (String id : ids) {
                    UIComponentAPI comp = references.get(id);
                    if (comp == null) continue;
                    if (main == null) continue;
                    int wagons = Math.max(1, syn.getAmountOfWagonsForUI(id));
                    Color color = syn.getColorForWagons(id) != null ? syn.getColorForWagons(id) : Color.ORANGE;
                    tripQueue.add(new Trip(main, comp, wagons, color, syn));
                    tripQueue.add(new Trip(comp, main, wagons, color, syn));
                }
            }
        }

        if (tripQueue.isEmpty() && references != null && !references.isEmpty() && main != null) {
            ArrayList<UIComponentAPI> inds = new ArrayList<>(references.values());
            Collections.shuffle(inds, rng);
            for (UIComponentAPI comp : inds) {
                tripQueue.add(new Trip(main, comp, 4, Color.YELLOW, null));
                tripQueue.add(new Trip(comp, main, 4, Color.YELLOW, null));
            }
        }
    }

    public void setTurnForActiveTrain(int junctionIdx, float durationSec, float extraDegrees) {
        if (activeTrain != null) activeTrain.setTurn(junctionIdx, durationSec, extraDegrees);
    }

    private class Train {
        final UIComponentAPI start, end;
        final List<CustomPanelAPI> anchors = new ArrayList<>();
        final List<Float> segLen = new ArrayList<>();
        final List<Float> segCum = new ArrayList<>();
        final List<Float> segAngle = new ArrayList<>();
        float totalLen = 0f;

        float s = 0f;
        final float speed = 110f;
        int wagons;
        final float wagonSpacing = 15f;
        final float wagonW = 10f;
        final float wagonH = 5f;
        final Color wagonColor;

        boolean finished = false;
        final SpriteAPI wagonSprite = Global.getSettings().getSprite("rendering", "GlitchSquare");

        float[] turnDuration;
        float[] turnExtraDegrees;
        boolean turning = false;
        int turningJunction = -1;
        float turnTimer = 0f;
        float turnTime = 0f;
        float turnStartAngle = 0f;
        float turnEndAngle = 0f;
        float turnX = 0f, turnY = 0f;

        Train(UIComponentAPI start, UIComponentAPI end, int wagons, Color color) {
            this.start = start;
            this.end = end;
            this.wagons = Math.max(1, wagons);
            this.wagonColor = color != null ? color : Color.YELLOW;
            wagonSprite.setColor(this.wagonColor);

            buildAnchorsAndLengths();
            turnDuration = new float[Math.max(0, segLen.size()-1)];
            turnExtraDegrees = new float[Math.max(0, segLen.size()-1)];
            for (int i = 0; i < turnDuration.length; i++) { turnDuration[i] = 0f; turnExtraDegrees[i] = 0f; }
        }

        private void buildAnchorsAndLengths() {
            PositionAPI mainPos = start.getPosition();
            PositionAPI refPos  = end.getPosition();

            float mainX = mainPos.getCenterX();
            float mainY = mainPos.getCenterY() + 5f;
            float refX  = refPos.getCenterX();
            float refY  = refPos.getCenterY() + 5f;
            float refHalfHeight = refPos.getHeight() / 2f;
            float OffsetY = mainY - refY;

            if (Math.abs(OffsetY) == 0) {
                float leg2 = refPos.getCenterX() - mainX;
                addAnchorTLFromRender(mainX - 1, mainY);
                addAnchorTLFromRender(mainX - 1, (mainY - (refHalfHeight) - getTrainOffsetY()));
                addAnchorTLFromRender(mainX - 1 + leg2, (mainY - (refHalfHeight) - getTrainOffsetY()));
                addAnchorTLFromRender(refX - 1, refY);
            } else {
                float leg2 = centerOfRows.getPosition().getX() - mainX;
                addAnchorTLFromRender(mainX - 1, mainY);
                addAnchorTLFromRender(mainX - 1, mainY - refHalfHeight - getTrainOffsetY());
                addAnchorTLFromRender(mainX + (leg2) + 1, mainY - refHalfHeight - getTrainOffsetY());
                addAnchorTLFromRender(mainX + (leg2) + 1, mainY - refHalfHeight - getTrainOffsetY() - OffsetY);
                leg2 = centerOfRows.getPosition().getX() - refPos.getCenterX();
                addAnchorTLFromRender(refX + (leg2) + 1, mainY - refHalfHeight - getTrainOffsetY() - (OffsetY));
                addAnchorTLFromRender(refX - 1, mainY - refHalfHeight - getTrainOffsetY() - (OffsetY));
                addAnchorTLFromRender(refX - 1, refY);
            }

            totalLen = 0f;
            segCum.clear();
            segLen.clear();
            segAngle.clear();
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
                    if (len < 0.0001f) len = 0.0001f;
                    segLen.add(len);
                    totalLen += len;
                    segCum.add(totalLen);
                    float ang = (float) Math.toDegrees(Math.atan2(by - ay, bx - ax));
                    segAngle.add(ang);
                }
            }
        }

        private void addAnchorTLFromRender(float renderX, float renderY) {
            CustomPanelAPI p = Global.getSettings().createCustom(1, 1, null);
            insertTo.addComponent(p);
            float hOff = Global.getSettings().getScreenHeight() - (insertTo.getPosition().getY() + insertTo.getPosition().getHeight());
            float relativeY = Global.getSettings().getScreenHeight() - renderY - hOff;
            p.getPosition().inTL(renderX - insertTo.getPosition().getX(), relativeY);
            anchors.add(p);
        }

        public int getJunctionCount() { return Math.max(0, segLen.size() - 1); }

        public void setTurn(int junctionIdx, float durationSeconds, float extraDegrees) {
            if (junctionIdx < 0 || junctionIdx >= getJunctionCount()) return;
            turnDuration[junctionIdx] = Math.max(0f, durationSeconds);
            turnExtraDegrees[junctionIdx] = extraDegrees;
        }

        void advance(float amount) {
            if (finished || anchors.size() < 2) return;

            if (turning) {
                if (turnTime <= 0f) { turning = false; }
                else {
                    turnTimer += amount;
                    if (turnTimer >= turnTime) {
                        turning = false;
                        turnTimer = 0f;
                    }
                    return;
                }
            }

            s += 110f * amount;

            int segIdx = findSegmentForDistance(s);
            if (segIdx >= 0 && segIdx < segLen.size()) {
                float endOfSeg = segCum.get(segIdx + 1);
                if (s >= endOfSeg && segIdx < getJunctionCount()) {
                    float dur = turnDuration[segIdx];
                    if (dur > 0f) {
                        turning = true;
                        turningJunction = segIdx;
                        turnTime = dur;
                        turnTimer = 0f;
                        turnStartAngle = segAngle.get(segIdx);
                        float target = segAngle.get(segIdx + 1) + turnExtraDegrees[segIdx];
                        turnEndAngle = normalizeAngleLerp(turnStartAngle, target);
                        PositionAPI joint = anchors.get(segIdx + 1).getPosition();
                        turnX = joint.getCenterX();
                        turnY = joint.getCenterY();
                        s = endOfSeg;
                    }
                }
            }

            // remove only when ALL wagons have reached destination (tail passed end)
            float tailOffset = (wagons - 1) * wagonSpacing;
            if (s >= totalLen + tailOffset) {
                finished = true;
                for (CustomPanelAPI p : anchors) insertTo.removeComponent(p);
                anchors.clear();
            }
        }

        void render(float alphaMult) {
            if (finished || anchors.size() < 2) return;
            GL11.glDisable(GL_BLEND);
            wagonSprite.setAlphaMult(alphaMult);
            wagonSprite.setColor(wagonColor);

            for (int i = 0; i < wagons; i++) {
                float d = s - i * wagonSpacing;
                if (d < 0) continue;
                if (d > totalLen) d = totalLen;

                int segIdx = findSegmentForDistance(d);
                if (segIdx < 0) continue;

                float segStart = segCum.get(segIdx);
                float segLength = segLen.get(segIdx);
                float t = (segLength <= 0f) ? 0f : (d - segStart) / segLength;

                float x, y, angle;

                if (i == 0 && turning) {
                    float k = Math.min(1f, Math.max(0f, turnTimer / Math.max(0.0001f, turnTime)));
                    angle = lerpAngle(turnStartAngle, turnEndAngle, k);
                    x = turnX;
                    y = turnY;
                } else {
                    PositionAPI a = anchors.get(segIdx).getPosition();
                    PositionAPI b = anchors.get(segIdx + 1).getPosition();
                    float ax = a.getCenterX(), ay = a.getCenterY();
                    float bx = b.getCenterX(), by = b.getCenterY();
                    x = ax + (bx - ax) * t;
                    y = ay + (by - ay) * t;
                    angle = segAngle.get(segIdx);
                }

                wagonSprite.setSize(wagonW, wagonH);
                wagonSprite.setAngle(angle);
                wagonSprite.renderAtCenter(x, y);
            }
            GL11.glEnable(GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }

        private int findSegmentForDistance(float d) {
            int n = segLen.size();
            for (int i = 0; i < n; i++) {
                if (segCum.get(i + 1) >= d) return i;
            }
            return n - 1;
        }

        private float normalizeAngleLerp(float from, float to) {
            float a = (to - from) % 360f;
            if (a > 180f) a -= 360f;
            if (a < -180f) a += 360f;
            return from + a;
        }

        private float lerpAngle(float from, float to, float t) {
            float a = (to - from) % 360f;
            if (a > 180f) a -= 360f;
            if (a < -180f) a += 360f;
            return from + a * t;
        }
    }

    private  int getTrainOffsetY() {
        return getValueForY()-2;
    }
}
