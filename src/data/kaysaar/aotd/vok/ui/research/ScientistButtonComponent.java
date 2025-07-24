package data.kaysaar.aotd.vok.ui.research;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.scientist.models.ScientistPerson;
import data.kaysaar.aotd.vok.ui.basecomps.ButtonComponent;
import data.kaysaar.aotd.vok.ui.basecomps.ImageViewer;
import data.kaysaar.aotd.vok.ui.basecomps.LabelComponent;
import data.kaysaar.aotd.vok.ui.scientist.HeadOfResearchDialog;

public class ScientistButtonComponent extends ButtonComponent {
    public ScientistPerson currentScientist;
    public HeadOfResearchShowcase siblingDialog;
    LabelComponent component;
    ImageViewer viewer;
    public void setSiblingDialog(HeadOfResearchShowcase siblingDialog) {
        this.siblingDialog = siblingDialog;
    }

    public ScientistButtonComponent(float width, float height) {
        super(width, height);
        this.currentScientist = AoTDMainResearchManager.getInstance().getManagerForPlayer().currentHeadOfCouncil;
        component = new LabelComponent(Fonts.ORBITRON_20AA,20,"Head of R&D - None", Misc.getTextColor().brighter(),width*4,30);
        if(currentScientist!=null){
            String imageName = currentScientist.getScientistPerson().getPortraitSprite();
            component.setText("Head of R&D - "+currentScientist.getScientistPerson().getNameString());
            viewer = new ImageViewer(width,height,imageName);
        }
        else{
            String imageName = Global.getSettings().getSpriteName("misc","default_portrait");
            viewer = new ImageViewer(width,height,imageName);

        }
        addComponent(viewer,0,0);
        addComponent(component,((width/2)-(component.getTextWidth()/2)),-22);
        this.setClickable(!AoTDMainResearchManager.getInstance().getManagerForPlayer().getResearchCouncil().isEmpty());
    }

    @Override
    public void clearUI() {
        super.clearUI();
        removeComponent(viewer);
        removeComponent(component);
        this.currentScientist = AoTDMainResearchManager.getInstance().getManagerForPlayer().currentHeadOfCouncil;
        component = new LabelComponent(Fonts.ORBITRON_20AA,20,"Head of R&D - None", Misc.getTextColor().brighter(),getPanelOfButton().getPosition().getWidth()*4,30);
        if(currentScientist!=null){
            String imageName = currentScientist.getScientistPerson().getPortraitSprite();
            component.setText("Head of R&D - "+currentScientist.getScientistPerson().getNameString());
            viewer = new ImageViewer(getPanelOfButton().getPosition().getWidth(),getPanelOfButton().getPosition().getHeight(),imageName);
        }
        else{
            String imageName = Global.getSettings().getSpriteName("misc","default_portrait");
            viewer = new ImageViewer(getPanelOfButton().getPosition().getWidth(),getPanelOfButton().getPosition().getHeight(),imageName);

        }
        addComponent(viewer,0,0);
        addComponent(component,((getPanelOfButton().getPosition().getWidth()/2)-(component.getTextWidth()/2)),-22);
        this.setClickable(!AoTDMainResearchManager.getInstance().getManagerForPlayer().getResearchCouncil().isEmpty());
    }

    @Override
    public void performActionOnClick(boolean isRightClick) {
        if(!isRightClick){
            AoTDMisc.initPopUpDialog(new HeadOfResearchDialog("Choose Head of R&D",siblingDialog,this),1000,700);
        }
    }
}
