package data.kaysaar.aotd.vok.ui.basecomps.holograms;

import ashlib.data.plugins.info.WeaponInfoGenerator;
import ashlib.data.plugins.rendering.ShipRenderer;
import ashlib.data.plugins.rendering.WeaponSpriteRenderer;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.util.Pair;

import java.awt.*;

public class WeaponHologram implements HologramViewerObjectRendererAPI {

    WeaponSpriteRenderer renderer;
    String weaponSpec;

    public WeaponHologram(String spec){
        this.weaponSpec = spec;
    }
    @Override
    public void init(CustomPanelAPI panelOfRendering) {
        Pair<CustomPanelAPI, WeaponSpriteRenderer> rendererPair= WeaponInfoGenerator.getImageOfWeapon(Global.getSettings().getWeaponSpec(weaponSpec),panelOfRendering.getPosition().getWidth());

        panelOfRendering.addComponent(rendererPair.one).inTL(0,0);
        renderer= rendererPair.two;
        renderer.setOverlayColor(Color.CYAN);
    }

    @Override
    public void render(float alphaMult,float centerX,float centeryY){
        renderer.render(alphaMult);
    }
}
