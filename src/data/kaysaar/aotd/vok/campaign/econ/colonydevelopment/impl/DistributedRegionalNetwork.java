package data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.impl;

import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.intel.bases.LuddicPathBaseManager;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.models.BaseColonyDevelopment;
import data.kaysaar.aotd.vok.campaign.econ.industry.MiscHiddenIndustry;
import data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class DistributedRegionalNetwork extends BaseColonyDevelopment {
    public static LinkedHashSet<String> extractiveOperationIndustries = new LinkedHashSet<>();
    public static void addNewIndustries(ArrayList<String> industries){
        extractiveOperationIndustries.addAll(industries);
    }
    @Override
    public String getName() {
        return "Distributed Regional Network";
    }
    @Override
    public void apply(MarketAPI market) {
        int size = market.getSize();
        float val = 1.5f;
        if(IndustrySynergiesMisc.isIndustryFunctionalAndExisting(market, AoTDIndustries.MAGLEV_CENTRAL_HUB)){
            val = 1.3f;
        }
        if(market.hasIndustry(AoTDIndustries.RESORT)){
            market.getIndustry(AoTDIndustries.RESORT).getIncome().modifyMult("aotd_distributed",1.1f,getName());
        }
        for (Industry industry : market.getIndustries()) {
            industry.getUpkeep().modifyMult("aotd_distributed",val,getName());
            if(DistributedRegionalNetwork.extractiveOperationIndustries.contains(industry.getSpec().getId())){
                industry.getSupplyBonusFromOther().modifyFlat("aotd_distributed",Math.min(3,size-2),getName());
            }
            else{
                industry.getSupplyBonusFromOther().modifyFlat("aotd_distributed",-Math.min(4,size-2),getName());

            }
        }
        MiscHiddenIndustry.getInstance(market).getPatherInterestManipulator().unmodifyFlat("aotd_distributed");
        float intrest = LuddicPathBaseManager.getLuddicPathMarketInterest(market);

        MiscHiddenIndustry.getInstance(market).getPatherInterestManipulator().modifyFlat("aotd_distributed",-intrest*0.2f,getName());
    }

    @Override
    public void unapply(MarketAPI market) {
        if(market.hasIndustry(AoTDIndustries.RESORT)){
            market.getIndustry(AoTDIndustries.RESORT).getIncome().unmodifyMult("aotd_distributed");
        }
        market.getIndustries().forEach(x->{
            x.getUpkeep().unmodifyMult("aotd_distributed");
            x.getSupplyBonusFromOther().unmodifyFlat("aotd_distributed");
        });
        MiscHiddenIndustry.getInstance(market).getPatherInterestManipulator().unmodifyFlat("aotd_distributed");
    }
    @Override
    public void generateDescriptionSection(MarketAPI market, TooltipMakerAPI tooltip) {
        tooltip.addPara(
                "Though not really used in the Persean Sector due to the Domain Edict 4915-PS, the Regional Development Network plan is considered to be the genuine standard of colonization used since before the Era of the Domain.",
                5f
        );

        tooltip.addPara(
                "Inspired by the original colonization of Mars, Ganymede, and Titan back in Sol, this model was widely implemented by the Cradle Core of the Domain during its golden age of expansion. It emphasizes tight centralization and low urban density as a foundation for efficiency and control.",
                3f
        );
    }

    @Override
    public void generateEffects(MarketAPI market, TooltipMakerAPI tooltip, String fontForSections) {
        tooltip.setParaFont(fontForSections);
        tooltip.addPara("Decentralized Industrial Complex", Misc.getTooltipTitleAndLightHighlightColor(),5f);
        tooltip.setParaFont(Fonts.DEFAULT_SMALL);
        tooltip.addPara("All industries specializing in resource extraction: %s will get up to %s bonus towards production, depending on colony size",3f,Color.ORANGE, IndustrySynergiesMisc.getIndustriesListed(extractiveOperationIndustries.stream().toList(),market),"3");
        tooltip.addPara(
                "All other industries will have their output reduced by up to %s, depending on colony size.",
                3f,
                Misc.getNegativeHighlightColor(),
                "4 units"
        );
        tooltip.setParaFont(fontForSections);
        tooltip.addPara("Planetwide Transport Network", Misc.getTooltipTitleAndLightHighlightColor(),5f);
        tooltip.setParaFont(Fonts.DEFAULT_SMALL);
        tooltip.addPara(
                "Maintenance costs are increased by %s across the board on all industries, infrastructure, and facilities on this world.",
                3f,
                Misc.getNegativeHighlightColor(),
                "+50%"
        );

        tooltip.addPara(
                "If the %s is built and operational, this penalty is reduced to %s due to improved logistical efficiency.",
                3f,
                Misc.getPositiveHighlightColor(),
                "Maglev Network", "+30%"
        );
        tooltip.addPara("Resort Center receives %s income bonus!",3f,Misc.getPositiveHighlightColor(),"10%");
        tooltip.setParaFont(fontForSections);
        tooltip.addPara("Scattered Settlements", Misc.getTooltipTitleAndLightHighlightColor(),5f);
        tooltip.setParaFont(Fonts.DEFAULT_SMALL);
        tooltip.addPara("Lowers synergy efficiency by %s and lowers Pather interest significantly, due to being harder to establish a hidden base on this planet",3f,Color.ORANGE,"10%");

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
