package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.bifrost;

import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.bifrost.sections.BifrostSection;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.bifrost.ui.BifrostUI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.GPIndividualMegastructreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.GPMegasturcutreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;

import java.util.ArrayList;

public class BifrostMega extends GPBaseMegastructure {
    @Override
    public void trueInit(String specId, SectorEntityToken entityTiedTo) {
        this.specId = specId;
        this.entityTiedTo = entityTiedTo;
        this.megaStructureSections = new ArrayList<>();
        wasInitalized  = true;
    }
    public void addNewBifrostGate(SectorEntityToken token){
        BifrostSection section = new BifrostSection();
        section.init(this,true);
        section.setGateTiedTo(token);
        this.megaStructureSections.add(section);
    }
    public ArrayList<BifrostSection>getSections(){
        ArrayList<BifrostSection>section = new ArrayList<>();
        for (GPMegaStructureSection megaStructureSection : megaStructureSections) {
            section.add((BifrostSection) megaStructureSection);
        }
        return section;
    }
    public void removeBifrostGate(SectorEntityToken token){
        for (BifrostSection section : getSections()) {
            if(section.getGateTiedTo().getId().equals(token.getId())){
                megaStructureSections.remove(section);
                break;
            }
        }
    }

    @Override
    public GPIndividualMegastructreMenu createUIPlugin(CustomPanelAPI parentPanel, GPMegasturcutreMenu menu) {
        return new BifrostUI(this,parentPanel,menu);
    }
}
