package data.kaysaar.aotd.vok.ui.research;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.Fonts;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.PopUpUI;
import data.kaysaar.aotd.vok.ui.basecomps.ButtonComponent;
import data.kaysaar.aotd.vok.ui.basecomps.LabelComponent;

import java.awt.*;

import static data.kaysaar.aotd.vok.misc.AoTDMisc.placePopUpUI;
import static data.kaysaar.aotd.vok.misc.AoTDMisc.placePopUpUIUnder;

public class TechTreeModButton extends ButtonComponent {
    String modID;
    AoTDResearchNewPlugin plugin;
    public boolean isDropDown;
    ModDropDown modList;
    LabelComponent component;

    public TechTreeModButton(float width, float height, String modID, AoTDResearchNewPlugin referencePlugin, boolean isDropDown, ModDropDown modList) {

        super(width, height);
        this.isDropDown = isDropDown;
        this.plugin = referencePlugin;
        this.modID = modID;
        this.modList = modList;
        component = (new LabelComponent(Fonts.ORBITRON_20AA, 20, Global.getSettings().getModManager().getModSpec(modID).getName(), Color.cyan, width - 10, height / 3));
        addComponent(component, ((width / 2) - (component.getTextWidth() / 2)), (height / 2) - (component.draw.getHeight() / 2));
        alphaBG = 0.2f;
    }

    @Override
    public void performActionOnClick(boolean isRightClick) {
        if (isDropDown) {
            PopUpUI ui = new ModDropDown(modID, plugin);
            plugin.blockButtonsFromHover();
            placePopUpUIUnder(ui, componentPanel, 500, 150);
        } else {
            if (modList != null) {
                modList.currId = modID;
                modList.forceDismiss();
            }
        }
    }
    public void updateModButton(String id){
        this.modID = id;
        removeComponent(component);
        component.clearUI();
        component = (new LabelComponent(Fonts.ORBITRON_20AA, 20, Global.getSettings().getModManager().getModSpec(modID).getName(), Color.cyan, originalWidth - 10, originalHeight / 3));
        addComponent(component, ((originalWidth / 2) - (component.getTextWidth() / 2)), (originalHeight / 2) - (component.draw.getHeight() / 2));

    }
}
