package data.kaysaar.aotd.vok.ui.basecomps;

import com.fs.graphics.util.Fader;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LabelWithHighlight implements IdentifiableUIPanelPlugin {

    String id;
    private static final class Entry {
         String text;
         Alignment alignment;
         float opad;
         Color textColor;
         Color highlightColor;
         String[]highlights;
         String font;
         boolean highlighted = false;
        Entry(String text,String font,Color textColor, Alignment alignment, float opad) {
            this.text = text;
            this.textColor = textColor;
            this.alignment = alignment;
            this.opad = opad;
            this.font = font;
        }
        Entry(String text,String font,Color textColor,Color highlightColor, Alignment alignment, float opad,String... highLights) {
            highlighted =true;
            this.text = text;
            this.font = font;
            this.textColor = textColor;
            this.highlightColor = highlightColor;
            this.alignment = alignment;
            this.opad = opad;
            this.highlights = highLights;
        }
    }
    TooltipMakerAPI.TooltipCreator creator;
    private final float width;
    private final float height;

    private final List<Entry> entries = new ArrayList<>();
    private final List<LabelAPI> labels = new ArrayList<>();

    private CustomPanelAPI root;

    public void setCreator(TooltipMakerAPI.TooltipCreator creator) {
        this.creator = creator;
    }

    public LabelWithHighlight(float width, float height,
                              String id) {
        this.width = width;
        this.height = height;
        this.id = id;
    }

    /** Add a label line with alignment and top padding (in px). Call before createUI(). */
    public void addLabel(String text,String font,Color color, Alignment alignment, float opad) {
        entries.add(new Entry(text,font,color, alignment != null ? alignment : Alignment.MID, opad));
    }
    public void addLabelHighlighted(String text,String font, Color color, Color highlightColor, Alignment alignment, float opad, String... highLights) {
        entries.add(new Entry(text,font,color,highlightColor, alignment != null ? alignment : Alignment.MID, opad,highLights));
    }
    @Override
    public String getID() {
        return id;
    }

    @Override
    public CustomPanelAPI getMainPanel() {
        return root;
    }

    @Override
    public void createUI() {
        labels.clear();
        root = Global.getSettings().createCustom(width, height, this);

        // NOTE: We do NOT store TooltipMakerAPI; it's local only.
        TooltipMakerAPI ui = root.createUIElement(width, height, false);
        float maxWidth = 0;
        for (Entry e : entries) {
            ui.setParaFont(e.font);
            LabelAPI label;
            if(e.highlighted){
                label = ui.addPara(e.text,e.opad,e.textColor,e.highlightColor,e.highlights);
            }
            else{
                label = ui.addPara(e.text, e.textColor, e.opad);
            }
            label.setAlignment(e.alignment);
            // Highlight the entire text when hovered
            label.setHighlightOnMouseover(true);
            label.getPosition().setSize(label.computeTextWidth(label.getText()),label.computeTextHeight(label.getText()));
            if(maxWidth<=label.getPosition().getWidth()){
                maxWidth = label.getPosition().getWidth();
            }
            labels.add(label);
        }

        root.addUIElement(ui).inTL(0f, 0f);
        root.getPosition().setSize(maxWidth,ui.getHeightSoFar());
        ui.addTooltipTo(creator,root, TooltipMakerAPI.TooltipLocation.BELOW,false);
    }

    @Override
    public void positionChanged(PositionAPI position) { }

    @Override
    public void renderBelow(float alphaMult) { }

    @Override
    public void render(float alphaMult) { }

    @Override
    public void advance(float amount) { }

    @Override
    public void processInput(List<InputEventAPI> events) {
        // Defensive fallback to keep mouseover highlight snappy even if the engine changes.
        if (labels.isEmpty() || events == null || events.isEmpty()) return;
        boolean hovered = false;

        for (LabelAPI label : labels) {
            Fader fader = (Fader) ReflectionUtilis.invokeMethodWithAutoProjection("getMouseoverFader", label);
            if (fader == null) continue;
            fader.fadeOut();

            for (InputEventAPI e : events) {
                if (!e.isConsumed() && e.isMouseEvent() && label.getPosition().containsEvent(e)) {
                    hovered = true;
                    break;
                }
            }
        }
        if(hovered){
            for (LabelAPI label : labels) {
                Fader fader = (Fader) ReflectionUtilis.invokeMethodWithAutoProjection("getMouseoverFader", label);
                if (fader == null) continue;
                fader.fadeIn();
            }
        }
    }

    @Override
    public void buttonPressed(Object buttonId) { }
}
