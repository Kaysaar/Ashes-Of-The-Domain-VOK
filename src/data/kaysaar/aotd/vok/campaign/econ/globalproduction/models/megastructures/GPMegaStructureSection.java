package data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.ui.CustomPanelAPI;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class GPMegaStructureSection {
    public String specID;
    public GPBaseMegastructure megastructureTiedTo;
    public GPMegaStructureSection(){

    }

    public void init(GPBaseMegastructure megastructureTiedTo,boolean isRestored){
        this.megastructureTiedTo = megastructureTiedTo;
        this.isRestored = isRestored;
    }
    public float progressOfRestoration;
    boolean  isRestored;
    boolean isRestoring;
    public float penaltyFromLackOfResources;
    public void setPenaltyFromLackOfResources(float penaltyFromLackOfResources) {
        this.penaltyFromLackOfResources = penaltyFromLackOfResources;
    }
    boolean isAboutToBeRemoved = false;
    public void apply(){
        if(!isAboutToBeRemoved){
            if(isRestored){
                applyEffectOfSection();
            }
        }


    }
    public int getUpkeep(){
        return 1;
    }

    public GpMegaStructureSectionsSpec getSpec() {
        return null;
    }

    public void advance(float amount){
        apply();
        if(isRestoring){
            progressOfRestoration+= Global.getSector().getClock().convertToDays(amount)*penaltyFromLackOfResources;
            if(progressOfRestoration>=getSpec().daysForRenovation){
                isRestoring = false;
                isRestored = true;
            }
        }
    }
    public void unapply(){
        unapplyEffectOfSection();
    }
    public void aboutToGetRemoved(){
        this.isAboutToBeRemoved = true;
        unapplyEffectOfSection();
    }
    public void applyEffectOfSection(){

    }
    public void unapplyEffectOfSection(){

    }

    public LinkedHashMap<String,String>generateButtons(){
        LinkedHashMap<String,String> buttons = new LinkedHashMap<>();
        return buttons;
    }
    public CustomPanelAPI createPanelDialogWhenOptionPressed(CustomPanelAPI panelOfInsertion,String optionID){
        return null;
    }
    public IntelInfoPlugin notifyAboutCompletion(){
        return null;
    }



}
