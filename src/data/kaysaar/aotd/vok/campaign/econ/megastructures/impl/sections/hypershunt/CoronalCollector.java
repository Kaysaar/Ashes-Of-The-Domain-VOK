package data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.sections.hypershunt;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureSection;

import java.awt.*;

public class CoronalCollector extends BaseMegastructureSection {
    @Override
    public void createEffectExplanationSectionInSubSection(TooltipMakerAPI tl) {
        tl.addSectionHeading("Solar Lance", Alignment.MID,5f);
        if(!isRestored){
            tl.addPara("Once restored, allows Hypershunt to collect energy of it's orbiting star!",5f);
        }
        else{
            tl.addPara("Collector operational!", Misc.getPositiveHighlightColor(),5f);
        }
        tl.addPara("Allows building a Hypershunt Receiver in a stable location within the effective range of the nearest Hypershunt, that gives unique effects to markets and megastructures.",5f);

    }

    @Override
    public float getUpkeepOfSection() {
        return 25000;
    }

    @Override
    public void applySectionOnIndustry(BaseIndustry ind) {
        if(isRestored){
            ind.getUpkeep().modifyFlat("upkeep_def",getUpkeepOfSection(),getName());
        }
    }

    @Override
    public void addToEffectSectionMain(TooltipMakerAPI tl) {
        super.addToEffectSectionMain(tl);
    }

    @Override
    public void applyOnRestoration() {
        this.getMegastructureTiedTo().getEntityTiedTo().getMemory().set("$usable",true);
    }
}
