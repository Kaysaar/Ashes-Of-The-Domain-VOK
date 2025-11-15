package data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs;

import ashlib.data.plugins.ui.models.BasePopUpDialog;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.sections.NidavelirBaseSection;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.BaseMegastrucutreMenu;

public class AutomationDialog  extends BasePopUpDialog {
    NidavelirBaseSection section;
    BaseMegastrucutreMenu menu;

    public AutomationDialog(String headerTitle, NidavelirBaseSection section, BaseMegastrucutreMenu menu) {
        super(headerTitle);
        this.section = section;
        this.menu = menu;
    }

    @Override
    public void createContentForDialog(TooltipMakerAPI tooltip, float width) {
        if(!section.isAutomated){
            tooltip.setParaInsigniaLarge();
            tooltip.addPara("Automation of this section, will result in such bonuses:",5f);
            section.printMenu(tooltip,12,true);
            tooltip.addPara("Note! We won't be able to use manpower points on this section after automation!", Misc.getNegativeHighlightColor(),5f);
            section.printEffects(tooltip,12,true);
        }
        else{
            tooltip.setParaInsigniaLarge();
            tooltip.addPara("De-automation of this section will result in ",5f);
            section.printMenu(tooltip,0,true);
            tooltip.addPara("We will be able to use manpower points again in this section!", Misc.getPositiveHighlightColor(),5f);
            section.printEffects(tooltip,0,true);
        }

    }

    @Override
    public void applyConfirmScript() {
        section.setAutomated(!section.isAutomated());
        if(section.isAutomated){
            section.setCurrentManpowerAssigned(0);
        }
        menu.resetSection(section.getSpec().getSectionID());
    }
}
