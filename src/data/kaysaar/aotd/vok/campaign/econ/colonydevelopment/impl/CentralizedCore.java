package data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.impl;

import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.intel.bases.LuddicPathBaseManager;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.models.BaseColonyDevelopment;
import data.kaysaar.aotd.vok.campaign.econ.industry.MiscHiddenIndustry;

import java.awt.*;

public class CentralizedCore extends BaseColonyDevelopment {
    @Override
    public String getName() {
        return "Centralized Urban Core";
    }
    @Override
    public void generateDescriptionSection(MarketAPI market, TooltipMakerAPI tooltip) {
        tooltip.addPara(
                "This model refines the old Eridani-Utopia corporate doctrine, built for efficient colonization and resource control. Instead of many settlements, one major city handles administration, industry, and population.",
                5f
        );

        tooltip.addPara(
                "Smaller automated outposts surround the Core, focused on extraction and logistics. Resources flow inward through fixed routes, keeping costs low but control absolute. Efficiency is high — at the expense of local growth and autonomy.",
                3f
        );
    }
    @Override
    public void apply(MarketAPI market) {
        int size = market.getSize();
        for (Industry industry : market.getIndustries()) {
            IndustrySpecAPI spec = industry.getSpec();
            if(!spec.hasTag("grounddefenses")&&!spec.hasTag("military")&&!spec.hasTag("command")&&!spec.hasTag("patrol")){
                industry.getUpkeep().modifyMult("aotd_centralized",0.8f,getName());
            }
            if(DistributedRegionalNetwork.extractiveOperationIndustries.contains(industry.getSpec().getId())){
                industry.getSupplyBonusFromOther().modifyFlat("aotd_centralized",-Math.min(3,size-2),getName());
            }
            else{
                industry.getSupplyBonusFromOther().modifyFlat("aotd_centralized",Math.min(2,size/3),getName());

            }
        }
        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).modifyMult("aotd_centralized",1.5f,getName());
        MiscHiddenIndustry.getInstance(market).getPatherInterestManipulator().unmodifyFlat("aotd_centralized");
        float intrest = LuddicPathBaseManager.getLuddicPathMarketInterest(market);

        MiscHiddenIndustry.getInstance(market).getPatherInterestManipulator().modifyFlat("aotd_centralized",intrest*0.1f,getName());
    }

    @Override
    public void unapply(MarketAPI market) {
        market.getIndustries().forEach(x->{
            x.getUpkeep().unmodifyMult("aotd_centralized");
            x.getSupplyBonusFromOther().unmodifyFlat("aotd_centralized");
        });
        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodifyMult("aotd_centralized");
        MiscHiddenIndustry.getInstance(market).getPatherInterestManipulator().unmodifyFlat("aotd_centralized");
    }
    @Override
    public void generateEffects(MarketAPI market, TooltipMakerAPI tooltip, String fontForSections) {
        tooltip.setParaFont(fontForSections);
        tooltip.addPara("Urban Utilities", Misc.getTooltipTitleAndLightHighlightColor(),5f);
        tooltip.setParaFont(Fonts.DEFAULT_SMALL);
        tooltip.addPara("Reduces maintenance upkeep by %s, except for military and resource-dependent structures.",3f,Color.ORANGE,"20%");
        tooltip.addPara("Increase synergy efficiency by %s.",3f,Color.ORANGE,"15%");
        tooltip.setParaFont(fontForSections);
        tooltip.addPara("Central Planning", Misc.getTooltipTitleAndLightHighlightColor(),5f);
        tooltip.setParaFont(Fonts.DEFAULT_SMALL);
        tooltip.addPara("All industries, that don't specialised in resource extraction will get up to additional %s production bonus, depending on colony size",3f,Misc.getPositiveHighlightColor(),"2");
        tooltip.addPara("Industries specializing in resource extraction will get their supply lowered by %s, depending on colony size ",3f,Misc.getNegativeHighlightColor(),"3");
        tooltip.setParaFont(fontForSections);
        tooltip.addPara("City Fortress", Misc.getTooltipTitleAndLightHighlightColor(),5f);
        tooltip.setParaFont(Fonts.DEFAULT_SMALL);
        tooltip.addPara("Increase ground defence by %s. %s",3f,new Color[]{Color.ORANGE,Misc.getNegativeHighlightColor()},"1.5x","Pather bases are easier to establish, due to little presence on planet.");

    }

    @Override
    public boolean canBeAppliedOnMarket(MarketAPI market) {
        SectorEntityToken token = market.getPrimaryEntity();
        if(token instanceof PlanetAPI planet){
            return !planet.isGasGiant();
        }
        return true;
    }


}
