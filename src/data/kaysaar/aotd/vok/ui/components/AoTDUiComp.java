package data.kaysaar.aotd.vok.ui.components;

import java.awt.*;

public interface AoTDUiComp {
    public void createUI();
    public void createUI(float x , float y);

    public void placeTooltip(float x, float y);


    public void placeSubPanel(float x, float y);
    public static float WIDTH_OF_TECH_PANEL = 290;
    public static float HEIGHT_OF_TECH_PANEL = 130;
    public static float SEPERATOR_OF_PANELS = 80;
    public static float SEPERATOR_OF_COLUMNS= SEPERATOR_OF_PANELS+30;
    public void render(Color colorOfRender, float alphamult);
}
