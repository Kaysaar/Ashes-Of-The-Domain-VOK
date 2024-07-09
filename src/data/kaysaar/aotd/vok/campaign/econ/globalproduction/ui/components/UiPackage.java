package data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components;

import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPOption;
import data.kaysaar.aotd.vok.misc.fighterinfo.FighterIconRenderer;
import data.kaysaar.aotd.vok.misc.shipinfo.ShipRenderer;
import data.kaysaar.aotd.vok.misc.weaponinfo.WeaponSpriteRenderer;


public class UiPackage {

    transient CustomPanelAPI panelPackage;
    transient ShipRenderer render;
    transient WeaponSpriteRenderer renderWep;
    transient GPOption option;
    transient FighterIconRenderer fighterIconRenderer;
    ButtonAPI button ;
    public UiPackage(CustomPanelAPI panel, FighterIconRenderer render, GPOption option,ButtonAPI button) {
        this.panelPackage = panel;
        this.fighterIconRenderer = render;
        this.option = option;
        this.button = button;

    }
    public UiPackage(CustomPanelAPI panel, ShipRenderer render, GPOption option,ButtonAPI button) {
        this.panelPackage = panel;
        this.render = render;
        this.option = option;
        this.button = button;


    }

    public UiPackage(CustomPanelAPI panel, WeaponSpriteRenderer render, GPOption option,ButtonAPI button) {
        this.panelPackage = panel;
        this.renderWep = render;
        this.option = option;
        this.button = button;


    }

    public FighterIconRenderer getFighterIconRenderer() {
        return fighterIconRenderer;
    }

    public ShipRenderer getRender() {
        return render;
    }

    public CustomPanelAPI getPanelPackage() {
        return panelPackage;
    }

    public GPOption getOption() {
        return option;
    }
}
