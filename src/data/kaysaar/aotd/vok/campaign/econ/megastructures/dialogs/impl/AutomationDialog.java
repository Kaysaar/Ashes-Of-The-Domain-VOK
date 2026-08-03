package data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.impl;

import ashlib.data.plugins.ui.models.BasePopUpDialog;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.components.SectionShowcaseSection;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.sections.nidavelir.BaseNidavelirSection;

public class AutomationDialog extends BasePopUpDialog {
    BaseNidavelirSection section;
    SectionShowcaseSection menu;

    public AutomationDialog(String headerTitle, BaseNidavelirSection section, SectionShowcaseSection menu) {
        super(headerTitle);
        this.section = section;
        this.menu = menu;
    }

    @Override
    public void createContentForDialog(TooltipMakerAPI tooltip, float width) {
        if(!section.isAutomated){
            tooltip.setParaInsigniaLarge();
            tooltip.addPara("Automation of this section, will result in the following bonuses:",5f);
            section.printEffectSectionPerManpowerPoint(2,tooltip);
            tooltip.addPara("Note! We won't be able to use manpower points on this section after automation!", Misc.getNegativeHighlightColor(),5f);
            section.printEffects(tooltip,2,true);
        }
        else{
            tooltip.setParaInsigniaLarge();
            tooltip.addPara("De-automation of this section will result in ",5f);
            section.printEffectSectionPerManpowerPoint(0,tooltip);
            tooltip.addPara("We will be able to use manpower points again in this section!", Misc.getPositiveHighlightColor(),5f);
            section.printEffects(tooltip,0,true);
        }

    }

    @Override
    public void applyConfirmScript() {
        section.setAutomated(!section.isAutomated());
        menu.createUI();
    }
}
