package data.kaysaar.aotd.vok.ui.basecomps.holograms;

import ashlib.data.plugins.info.ShipInfoGenerator;
import ashlib.data.plugins.rendering.ShipRenderer;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.util.Pair;

import java.awt.*;

public class ShipHologram implements HologramViewerObjectRendererAPI {
    ShipRenderer renderer;
    String shipSpec;

    public ShipHologram(String spec){
        this.shipSpec = spec;
    }
    @Override
    public void init(CustomPanelAPI panelOfRendering) {
        Pair<CustomPanelAPI, ShipRenderer> rendererPair= ShipInfoGenerator.getShipImageWithoutInitPanel(Global.getSettings().getHullSpec(shipSpec),panelOfRendering.getPosition().getWidth(), Color.CYAN);

        panelOfRendering.addComponent(rendererPair.one).inTL(0,0);
        renderer= rendererPair.two;
    }

    @Override
    public void render(float alphaMult,float centerX,float centeryY){
        renderer.render(alphaMult);
    }
}
