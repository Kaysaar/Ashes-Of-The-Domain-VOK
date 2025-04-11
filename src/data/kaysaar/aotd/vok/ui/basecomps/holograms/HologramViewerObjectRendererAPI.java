package data.kaysaar.aotd.vok.ui.basecomps.holograms;

import com.fs.starfarer.api.ui.CustomPanelAPI;

public interface HologramViewerObjectRendererAPI {
    public void init(CustomPanelAPI panelOfRendering);
    public void render(float alphaMult,float centerX,float centeryY);

}
