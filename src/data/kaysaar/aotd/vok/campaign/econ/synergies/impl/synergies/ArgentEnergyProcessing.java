package data.kaysaar.aotd.vok.campaign.econ.synergies.impl.synergies;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.campaign.econ.industry.MiscHiddenIndustry;
import data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.BaseIndustrySynergy;
import data.kaysaar.aotd.vok.scripts.specialprojects.BlackSiteProjectManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;

import static data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc.getIndustriesListed;

public class ArgentEnergyProcessing extends BaseIndustrySynergy {
    @Override
    public String getSynergyName() {
        return "Argent Energy Processing";
    }

    @Override
    public boolean canShowSynergyInUI(MarketAPI market) {
        return  BlackSiteProjectManager.getInstance().getProject("aotd_tenebrium_proj").checkIfProjectWasCompleted();
    }

    @Override
    public boolean doesSynergyMetReq(MarketAPI market) {
        return canShowSynergyInUI(market)&& IndustrySynergiesMisc.isIndustryFunctionalAndExistingIncludingUpgrades(market, Industries.FUELPROD, AoTDIndustries.EXOMATTER_PROCESSING);
    }

    @Override
    public void populateListForSynergies(HashSet<String> industries, MarketAPI market) {
        industries.addAll(IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.FUELPROD));
        industries.add(AoTDIndustries.EXOMATTER_PROCESSING);
    }

    @Override
    public void printReqImpl(TooltipMakerAPI tooltip, MarketAPI market, Color base, Color highLight) {
        ArrayList<String> ids = IndustrySynergiesMisc.getIdsOfTreeFromIndustry(AoTDIndustries.EXOMATTER_PROCESSING);
        ArrayList<String> fuelIds = IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.FUELPROD);
        tooltip.addPara("Requires %s and %s on the same planet.", 3f, base, highLight,
                getIndustriesListed(ids,market),
                getIndustriesListed(fuelIds,market));
    }

    @Override
    public void printEffectsImpl(TooltipMakerAPI tooltip, Color base, Color highLight, float efficiency, MarketAPI market) {
        tooltip.addPara("Increases the production of %s by %s.",3f,base,highLight,IndustrySynergiesMisc.getIndustriesListed(IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.FUELPROD),market),2+"");
        tooltip.addPara("Heavily increases Pather interest.", Misc.getNegativeHighlightColor(), 3f);
    }

    @Override
    public void apply(float efficiencyPercent, MarketAPI market) {
        for (String str : IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.FUELPROD)) {
            if(market.hasIndustry(str)){
                market.getIndustry(str).getSupplyBonusFromOther().modifyFlat(getIdForEffects(),2,getSynergyName());
            }
        }
    }

    @Override
    public void unapply(MarketAPI market) {
        for (String str : IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.FUELPROD)) {
            if(market.hasIndustry(str)){
                market.getIndustry(str).getSupplyBonusFromOther().unmodifyFlat(getIdForEffects());
            }
        }
    }

    @Override
    public Color getColorForWagons(String industry) {
        return new Color(157, 54, 54);
    }
    @Override
    public void advance(MarketAPI market, float amount,boolean aboutToRemove) {
        if(this.doesSynergyMetTotalReq(market)&&!aboutToRemove) {
            MiscHiddenIndustry.getInstance(market).getPatherInterestManipulator().modifyFlat(getIdForEffects(),10,getSynergyName());
        }
        else{
            MiscHiddenIndustry.getInstance(market).getPatherInterestManipulator().unmodifyFlat(getIdForEffects());

        }
    }
    @Override
    public int getAmountOfWagonsForUI(String industry) {
        return 3;
    }
}
