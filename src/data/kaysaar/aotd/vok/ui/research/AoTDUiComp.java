package data.kaysaar.aotd.vok.ui.research;

import java.awt.*;

public interface AoTDUiComp {
    public void createUI();
    public void createUI(float x , float y);

    public void placeTooltip(float x, float y);


    public void placeSubPanel(float x, float y);
    public static float WIDTH_OF_TECH_PANEL = 400;
    public static float HEIGHT_OF_TECH_PANEL = 130;
    public static float SEPERATOR_OF_PANELS = 50;
    public static float SEPERATOR_OF_COLUMNS= SEPERATOR_OF_PANELS+170;
    public void render(Color colorOfRender, float alphamult);
}
