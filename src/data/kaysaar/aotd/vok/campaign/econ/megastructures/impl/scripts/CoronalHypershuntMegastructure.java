package data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.scripts;

import com.fs.starfarer.api.campaign.CustomCampaignEntityAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.aotd_entities.HypershuntReceiverEntityPlugin;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.sections.hypershunt.WormholeGenerator;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureScript;

import java.awt.*;

public class CoronalHypershuntMegastructure extends BaseMegastructureScript {
    public static boolean isWithinRangeOfAtLeastOneHypershunt(SectorEntityToken target){
        for (MarketAPI playerMarket : Misc.getPlayerMarkets(true)) {
            if(playerMarket.hasCondition("aotd_coronal_market_cond")){
                CoronalHypershuntMegastructure megastructure = (CoronalHypershuntMegastructure) BaseMegastructureScript.getInstanceOfScriptFromEntityIfPresent(playerMarket.getPrimaryEntity(),"coronal_hypershunt");
                WormholeGenerator generator = megastructure.getSectionById("coronal_wormhole",WormholeGenerator.class);
                float rangeMax = generator.getCalculatedRange();
                float currRange = Misc.getDistanceLY(megastructure.getEntityTiedTo(),target);
                if(currRange<=rangeMax){
                    return megastructure.getSectionById("coronal_collector").isRestored;
                }
            }
        }
        return false;
    }

    @Override
    public boolean doesHaveCustomSectionForTooltip() {
        return true;
    }
    @Override
    public void printCustomSection(TooltipMakerAPI tooltip) {
        tooltip.addSectionHeading("Hypershunt Receivers", Alignment.MID, 5f);
        tooltip.addPara("To distribute power of a hypershunt, a receiver needs to be built. It can be build in systems within effective range of a Hypershunt and only on a Stable Point. Will provide bonuses to all colonies located in the same system.",Misc.getTooltipTitleAndLightHighlightColor(),5f);
        tooltip.addPara(getSectionById("coronal_collector").getName()+" must be restored in order for the Hypershunt to work!", Color.ORANGE,5f);


    }
    public static boolean isWithinReceiverSystem(StarSystemAPI target){
        for (CustomCampaignEntityAPI customEntity : target.getCustomEntities()) {
            if(customEntity.hasTag("aotd_hypershunt_receiver")){
                HypershuntReceiverEntityPlugin plugin = (HypershuntReceiverEntityPlugin) customEntity.getCustomPlugin();
                if(plugin.isWithinRange()){
                    return true;
                }
            }
        }
        return false;
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
