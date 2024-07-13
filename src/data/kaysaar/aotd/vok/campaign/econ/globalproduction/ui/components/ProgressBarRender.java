package data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.NidavelirMainPanelPlugin;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.util.ArrayList;
import java.util.List;

public class ProgressBarRender implements CustomUIPanelPlugin {
    transient SpriteAPI spriteToRender = Global.getSettings().getSprite("rendering","GlitchSquare");
    transient CustomPanelAPI panelAPI;
    public transient ArrayList<ButtonAPI>buttons = new ArrayList<>();

    float seperator = 10f;



    public void setPanelAPI(CustomPanelAPI panelAPI) {
        this.panelAPI = panelAPI;
    }

    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {

        if(panelAPI!=null){
            AoTDMisc.startStencil(panelAPI,1);
            int i =1;
            for (ButtonAPI button : buttons) {
                if(i>buttons.size()-1)break;
                ButtonAPI secondButtonPos = buttons.get(i);
                float distance = button.getPosition().getY()-secondButtonPos.getPosition().getY()-secondButtonPos.getPosition().getHeight();
                spriteToRender.setSize(5,distance);
                spriteToRender.setColor(NidavelirMainPanelPlugin.bg);
                spriteToRender.render(button.getPosition().getCenterX()-2,button.getPosition().getY()-distance);
                i++;
            }
            AoTDMisc.endStencil();

        }

    }

    @Override
    public void render(float alphaMult) {

    }

    @Override
    public void advance(float amount) {

    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
