package data.kaysaar.aotd.vok.ui.research;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.research.scientist.models.ScientistAPI;
import data.kaysaar.aotd.vok.ui.basecomps.ButtonComponent;
import data.kaysaar.aotd.vok.ui.basecomps.ImageViewer;
import data.kaysaar.aotd.vok.ui.basecomps.LabelComponent;

public class ScientistButtonComponent extends ButtonComponent {
    public ScientistAPI currentScientist;
    public ScientistButtonComponent(float width, float height, ScientistAPI scientist) {
        super(width, height);
        this.currentScientist = scientist;
        LabelComponent component = new LabelComponent(Fonts.ORBITRON_20AA,20,"Head of R&D - None", Misc.getTextColor().brighter(),width*4,30);

        if(scientist!=null){
            String imageName = scientist.getScientistPerson().getPortraitSprite();
            component.setText("Head of R&D - "+scientist.getScientistPerson().getNameString());
            addComponent(new ImageViewer(width,height,imageName),0,0);
        }
        else{
            String imageName = Global.getSettings().getSpriteName("misc","default_portrait");
            addComponent(new ImageViewer(width,height,imageName),0,0);
        }
        addComponent(component,((width/2)-(component.getTextWidth()/2)),-22);
        this.setClickable(false);
    }

}
