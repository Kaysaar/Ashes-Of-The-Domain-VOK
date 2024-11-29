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
            tooltip.addPara("De-activation of gate will result in lowering monthly upkeep to %s and lowering resource upkeep of purified transplutonics to %s, but it won't be usable untill activated again!",5f, Color.ORANGE, Misc.getDGSCredits(section.getUpkeep(false)/2),"0");
        }
        else{
            tooltip.addPara("Activation of gate will result in monthly upkeep of %s and increasing resource upkeep of purified transplutonics to %s",5f,Color.ORANGE, Misc.getDGSCredits(section.getUpkeep(false)*2),"10");
        }
    }

    @Override
    public void applyConfirmScript() {
        section.setDisabled(!section.isDisabled());
        menu.resetSection(section.getName());

    }
}
