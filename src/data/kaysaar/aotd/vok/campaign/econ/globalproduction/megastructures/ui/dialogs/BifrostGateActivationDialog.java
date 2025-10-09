package data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs;

import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.bifrost.sections.BifrostSection;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.BaseMegastrucutreMenu;

import java.awt.*;

public class BifrostGateActivationDialog extends BasePopUpDialog{
    BifrostSection section;
    BaseMegastrucutreMenu menu;
    public BifrostGateActivationDialog(String headerTitle,BifrostSection section,BaseMegastrucutreMenu menu) {
        super(headerTitle);
        this.section = section;
        this.menu = menu;
    }

    @Override
    public void createContentForDialog(TooltipMakerAPI tooltip, float width) {
        tooltip.setParaInsigniaLarge();
        if(!section.isDisabled()){
            tooltip.addPara("De-activation of the gate will result in decrease of monthly upkeep to %s and decrease of purified transplutonics resource upkeep to %s, but it won't be usable until it is activated again!",5f, Color.ORANGE, Misc.getDGSCredits(section.getUpkeep(false)/2),"0");
        }
        else{
            tooltip.addPara("Activation of the gate will result in a monthly upkeep of %s and increase of purified transplutonics resource upkeep to %s",5f,Color.ORANGE, Misc.getDGSCredits(section.getUpkeep(false)*2),"10");
        }
    }

    @Override
    public void applyConfirmScript() {
        section.setDisabled(!section.isDisabled());
        menu.resetSection(section.getName());

    }
}
