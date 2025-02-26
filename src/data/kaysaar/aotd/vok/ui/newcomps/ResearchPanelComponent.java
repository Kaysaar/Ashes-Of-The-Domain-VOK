package data.kaysaar.aotd.vok.ui.newcomps;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.PopUpUI;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOption;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOptionSpec;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchRewardType;
import data.kaysaar.aotd.vok.ui.newcomps.basecomponents.ButtonComponent;
import data.kaysaar.aotd.vok.ui.newcomps.basecomponents.ImageViewer;
import data.kaysaar.aotd.vok.ui.newcomps.basecomponents.LabelComponent;

import java.awt.*;
import java.util.Map;

import static data.kaysaar.aotd.vok.misc.AoTDMisc.placePopUpUI;

public class ResearchPanelComponent extends ButtonComponent {
    public ResearchOptionSpec spec;
    LabelComponent queueText;
    ResearchZoomPanel panel;
    public ResearchPanelComponent(float width, float height, ResearchOptionSpec spec ,ResearchZoomPanel panel){
        super(width, height);
        this.spec = spec;
        this.panel = panel;
        ImageViewer mainIcon = new ImageViewer(100,100, Global.getSettings().getSpriteName("ui_icons_tech_tree",spec.IconId));
        addComponent(mainIcon,5,10);
        LabelComponent title = new LabelComponent(Fonts.ORBITRON_20AABOLD,18,spec.getName(), Color.ORANGE,width-70,100);
        addComponent(title,width-title.getTextWidth()-10,5);
        queueText = new LabelComponent(Fonts.ORBITRON_20AABOLD,20,"", Misc.getBrightPlayerColor(),width-80,100);
        addComponent(queueText,width-30,height-22);
        refresh();
        int currX = 110;
        int currY = 35;
        int size =60;
        int xSpacing = -5;
        for (Map.Entry<String, ResearchRewardType> entry : spec.getRewards().entrySet()) {
            ImageViewer viewer = new ImageViewer(size,size,AoTDMisc.getImagePathForTechIcon(entry.getKey()));
            addComponent(viewer,currX,currY);
            currX += xSpacing+size;
        }



    }
    public void refresh(){
        int i =1;
        boolean found = false;
        for (ResearchOption queuedResearchOption : AoTDMainResearchManager.getInstance().getManagerForPlayer().getQueueManager().getQueuedResearchOptions()) {
            if(queuedResearchOption.getSpec().getId().equals(spec.getId())){
                queueText.setText(i+".");
                found = true;
                break;
            }
            i++;
        }
        if(!found){
            queueText.setText(" ");
        }
        if(AoTDMainResearchManager.getInstance().getManagerForPlayer().canResearch(spec.getId(),false)){
            setColorOfBorder(Misc.getPositiveHighlightColor());
        }
        else{
            boolean researchedAll = true;
            for (String s : spec.getReqTechsToResearchFirst()) {
                if(AoTDMainResearchManager.getInstance().isResearchedForPlayer(s)){
                    researchedAll = false;
                    break;
                }
            }
            if(!researchedAll){
                setColorOfBorder(Misc.getNegativeHighlightColor());
            }
        }
        if(AoTDMainResearchManager.getInstance().isResearchedForPlayer(spec.getId())){
            setColorOfBorder(Color.ORANGE);
        }
        if(AoTDMainResearchManager.getInstance().getManagerForPlayer().getCurrentFocus()!=null&&AoTDMainResearchManager.getInstance().getManagerForPlayer().getCurrentFocus().getSpec().getId().equals(spec.getId())){
            setColorOfBorder(Color.cyan);
        }
        if(AoTDMainResearchManager.getInstance().getManagerForPlayer().getQueueManager().isInQueue(spec.getId())){
            setColorOfBorder(Color.cyan.darker());
        }
    }


    @Override
    public void performActionOnClick() {
        PopUpUI ui = new ResearchInfoUI(AoTDMainResearchManager.getInstance().getManagerForPlayer().getResearchOptionFromRepo(spec.getId()),panel);
        placePopUpUI(ui, componentPanel,410,660);
    }
}
