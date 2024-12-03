package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt;

import com.fs.starfarer.api.campaign.CustomCampaignEntityAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.aotd_entities.HypershuntReciverEntityPlugin;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt.sections.WormholeGenerator;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt.ui.HypershuntUI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.GPIndividualMegastructreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.GPMegasturcutreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;

public class HypershuntMegastrcutre extends GPBaseMegastructure {
    @Override
    public GPIndividualMegastructreMenu createUIPlugin(CustomPanelAPI parentPanel, GPMegasturcutreMenu menu) {
        return new HypershuntUI(this,parentPanel, menu);
    }
    public static boolean isWithinRangeOfAtLeastOneHypershunt(SectorEntityToken target){
        for (GPBaseMegastructure megastructure : GPManager.getInstance().getMegastructuresBasedOnClass(HypershuntMegastrcutre.class)) {
            for (GPMegaStructureSection megaStructureSection : megastructure.getMegaStructureSections()) {
                if(megaStructureSection instanceof WormholeGenerator){
                    float rangeMax = ((WormholeGenerator) megaStructureSection).getCalculatedRange();
                    float currRange = Misc.getDistanceLY(megastructure.getEntityTiedTo(),target);
                    if(currRange<=rangeMax){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public static boolean isWithinReciverSystem(SectorEntityToken target){
        for (CustomCampaignEntityAPI customEntity : target.getStarSystem().getCustomEntities()) {
            if(customEntity.hasTag("aotd_hypershunt_reciver")){
                HypershuntReciverEntityPlugin plugin = (HypershuntReciverEntityPlugin) customEntity.getCustomPlugin();
                if(plugin.isWithinRange()){
                    return true;
                }
            }
        }
        return false;
    }

}
