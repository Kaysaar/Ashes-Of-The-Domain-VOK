package data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components;

import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GpSpecialProjectData;

public class SpecialProjectButtonData {
    public enum ACTION{
        CANCEL,
        START
    }
    public ACTION actionOfButton;
    public GpSpecialProjectData specialProject;
    public SpecialProjectButtonData(GpSpecialProjectData specialProject){
        if(specialProject.isMainFocus()){
            actionOfButton = ACTION.CANCEL;
        }
        else{
            actionOfButton = ACTION.START;
        }
        this.specialProject = specialProject;
    }

    public ACTION getActionOfButton() {
        return actionOfButton;
    }

    public GpSpecialProjectData getSpecialProject() {
        return specialProject;
    }
    public String getNameForButton(){
        if(actionOfButton==ACTION.CANCEL){
           return  "Pause project";
        }
        if(actionOfButton==ACTION.START&& getSpecialProject().isFinished()){
           return  "Restart project";
        }
        return "Start project";
    }
}
