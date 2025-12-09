package data.kaysaar.aotd.vok.scripts.misc;

import ashlib.data.plugins.ui.models.ProgressBarComponentV2;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.PersistentUIDataAPI;
import com.fs.starfarer.api.impl.campaign.ids.Abilities;
import com.fs.starfarer.api.impl.campaign.rulecmd.AddAbility;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;

import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.ui.basecomps.ImageViewer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AoTDCompoundShowcase implements CustomUIPanelPlugin {
    CustomPanelAPI mainPanel;
    ProgressBarComponentV2 component;
    public static String memKey = "$aotd_compoun_on";
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    public AoTDCompoundShowcase(float width, float height) {
        mainPanel = Global.getSettings().createCustom(width, height, this);
        Color c = new Color(122, 36, 245);
        c = Misc.interpolateColor(c,Misc.getDarkPlayerColor().brighter(),0.45f);
        float progres = AoTDFuelConsumptionScript.getCompound(Global.getSector().getPlayerFleet().getCargo()) / Global.getSector().getPlayerFleet().getCargo().getFuel();

        String rounded = Misc.getRoundedValue(progres*100f);

        ProgressBarComponentV2 component = new ProgressBarComponentV2(width-15, height-4,rounded+"%" ,null,c, Misc.getBasePlayerColor(),progres){
            @Override
            public void influenceLabel() {
                LabelAPI label = getProgressLabel();
                float progres = AoTDFuelConsumptionScript.getCompound(Global.getSector().getPlayerFleet().getCargo()) / Global.getSector().getPlayerFleet().getCargo().getFuel();
                if (progres >= 1) progres = 1;
                String rounded = Misc.getRoundedValue(progres*100f);
                label.setText(rounded+"%");
                Color[] colors = new Color[1];
                colors[0] = new Color(201, 156, 255, 255);
                label.setHighlight(rounded+"%");
                label.setHighlightColors(colors);
            }
        };

        this.component = component;
        ImageViewer viewer = new ImageViewer(20, 20, Global.getSettings().getSpriteName("aotd_icons", "compound_info"));
        viewer.setColorOverlay(c);
        mainPanel.addComponent(viewer.getComponentPanel()).inTL(7, 10);
        mainPanel.addComponent(component.getRenderingPanel()).inTL(25, 10);
    }


    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {

    }

    @Override
    public void render(float alphaMult) {

    }

    @Override
    public void advance(float amount) {

        if (component != null) {
            float progres = AoTDFuelConsumptionScript.getCompound(Global.getSector().getPlayerFleet().getCargo()) / Global.getSector().getPlayerFleet().getCargo().getFuel();
            if (progres >= 1) progres = 1;
            String rounded = Misc.getRoundedValue(progres*100f);
            component.setBarText(rounded+"%");
            component.setProgress(progres);
        }

    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
