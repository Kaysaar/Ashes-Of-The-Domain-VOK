package data.kaysaar.aotd.vok.ui.basecomps.holograms;

import com.fs.starfarer.api.ui.CustomPanelAPI;

import java.awt.*;

public interface HologramViewerObjectRendererAPI {
    public void init(CustomPanelAPI panelOfRendering);
    public void render(float alphaMult,float centerX,float centeryY);
    default void renderMask(float centerX, float centerY) {
        render(1f, centerX, centerY);
    }
    public void setColor(Color color);
}
