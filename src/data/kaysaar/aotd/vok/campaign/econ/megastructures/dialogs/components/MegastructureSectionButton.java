package data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.components;

import ashlib.data.plugins.ui.models.resizable.ButtonComponent;
import ashlib.data.plugins.ui.models.resizable.ImageViewer;
import com.fs.starfarer.api.Global;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureScript;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureSection;

import java.awt.*;

public class MegastructureSectionButton extends ButtonComponent {
    BaseMegastructureSection section;

    public MegastructureSectionButton(float width, float height,BaseMegastructureSection section,boolean clickable) {
        super(width, height);
        this.section = section;
        ImageViewer viewer = new ImageViewer(width,height,section.getImagePath());
        addComponent(viewer,0,0);
        if(!section.isRestored()){
            ImageViewer viewer1 = new ImageViewer(width,height, Global.getSettings().getSpriteName("rendering","rust_layer"));
            viewer1.setColorOverlay(new Color(183,65,14));
            viewer1.setAlphaMult(0.3f);
            addComponent(viewer1,0,0);
        }
        if(!clickable){
            setClickable(false);
            setOverrideHighlight(true);
        }
    }

    public MegastructureSectionButton(float width, float height, BaseMegastructureScript section) {
        super(width, height);
        ImageViewer viewer = new ImageViewer(width,height,section.getCurrentImage());
        addComponent(viewer,0,0);
        setClickable(false);
        setOverrideHighlight(true);
    }
}
