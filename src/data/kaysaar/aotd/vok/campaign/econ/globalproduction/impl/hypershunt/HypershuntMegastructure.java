package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt;

import com.fs.starfarer.api.campaign.CustomCampaignEntityAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.aotd_entities.HypershuntReceiverEntityPlugin;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt.sections.WormholeGenerator;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt.ui.HypershuntUI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.GPIndividualMegastructreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.GPMegastructureMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;

import java.awt.*;

public class HypershuntMegastructure extends GPBaseMegastructure {
    @Override
    public GPIndividualMegastructreMenu createUIPlugin(CustomPanelAPI parentPanel, GPMegastructureMenu menu) {
        return new HypershuntUI(this,parentPanel, menu);
    }
    public static boolean isWithinRangeOfAtLeastOneHypershunt(SectorEntityToken target){
        for (GPBaseMegastructure megastructure : GPManager.getInstance().getMegastructuresBasedOnClass(HypershuntMegastructure.class)) {
            for (GPMegaStructureSection megaStructureSection : megastructure.getMegaStructureSections()) {
                if(megaStructureSection instanceof WormholeGenerator){
                    float rangeMax = ((WormholeGenerator) megaStructureSection).getCalculatedRange();
                    float currRange = Misc.getDistanceLY(megastructure.getEntityTiedTo(),target);
                    if(currRange<=rangeMax){
                        return megastructure.getSectionById("coronal_collector").isRestored;
                    }
                }
            }
        }
        return false;
    }
    @Override
    public void createAdditionalInfoForMega(TooltipMakerAPI tooltip) {
        tooltip.addSectionHeading("Hypershunt Receivers", Alignment.MID, 5f);
        tooltip.addPara("To distribute power of hypershunt, a receiver is needed to be built. It can be built in systems within effective range of Hypershunt and only on Stable Point. Will provide bonuses to all colonies located in system, where receiver is.",Misc.getTooltipTitleAndLightHighlightColor(),5f);
        tooltip.addPara(getSectionById("coronal_collector").getName()+" must be restored in order for Hypershunt to work!", Color.ORANGE,5f);

    }
    public static boolean isWithinReceiverSystem(SectorEntityToken target){
        for (CustomCampaignEntityAPI customEntity : target.getStarSystem().getCustomEntities()) {
            if(customEntity.hasTag("aotd_hypershunt_receiver")){
                HypershuntReceiverEntityPlugin plugin = (HypershuntReceiverEntityPlugin) customEntity.getCustomPlugin();
                if(plugin.isWithinRange()){
                    return true;
                }
            }
        }
        return false;
    }

}
