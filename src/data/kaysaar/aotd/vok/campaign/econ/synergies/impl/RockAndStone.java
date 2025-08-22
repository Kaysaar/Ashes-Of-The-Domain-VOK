package data.kaysaar.aotd.vok.campaign.econ.synergies.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.impl.items.BlueprintProviderItem;
import com.fs.starfarer.api.campaign.impl.items.ModSpecItemPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.procgen.SalvageEntityGenDataSpec;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.SalvageEntity;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.BaseIndustrySynergy;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static com.fs.starfarer.api.impl.campaign.econ.impl.TechMining.getTechMiningRuinSizeModifier;
import static data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc.getTechMiningMult;

public class RockAndStone extends BaseIndustrySynergy {
    @Override
    public boolean canShowSynergyInUI(MarketAPI market) {
        return IndustrySynergiesMisc.didMarketMetTechCriteria(market, AoTDTechIds.EXO_SKELETONS);
    }

    @Override
    public boolean doesSynergyMetReq(MarketAPI market) {

        return  canShowSynergyInUI(market) && IndustrySynergiesMisc.isIndustryFunctionalAndExisting(market, Industries.TECHMINING)&&IndustrySynergiesMisc.isAtLeastOneIndustryFunctionalFromList(market,Industries.MINING,AoTDIndustries.MINING_MEGAPLEX);
    }

    @Override
    public String getSynergyName() {
        return "Rock and Stone";
    }

    @Override
    public void populateListForSynergies(HashSet<String> industries, MarketAPI market) {
        industries.add(Industries.TECHMINING);
        industries.add(Industries.MINING);
        industries.add(AoTDIndustries.MINING_MEGAPLEX);

    }

    @Override
    public void printEffectsImpl(TooltipMakerAPI tooltip, Color base, Color highLight, float efficiency, MarketAPI market) {
        float percent = 0.1f*efficiency;
        float bPercent = 0.3f*efficiency;
        tooltip.addPara("If %s is functional : Increase chance to find rare items by %s  ",3f,base,highLight,"Mining", AoTDMisc.getPercentageString(percent));
        tooltip.addPara("If %s is functional : Increase chance to find rare items by %s ",3f,base,highLight,"Mining Megaplex", AoTDMisc.getPercentageString(bPercent));
    }

    @Override
    public void printReqImpl(TooltipMakerAPI tooltip, MarketAPI market, Color base, Color highLight) {
        ArrayList<String>ids = new ArrayList<>();
        ids.add(Industries.MINING);
        ids.add(AoTDIndustries.MINING_MEGAPLEX);

        tooltip.addPara("%s and %s are required to be functional! ",3f,base,highLight,
                IndustrySynergiesMisc.getIndustryName(market,Industries.TECHMINING),
                IndustrySynergiesMisc.getIndustriesListed(ids,market)
               );
    }

    @Override
    public void apply(float efficiencyPercent, MarketAPI market) {


    }

    @Override
    public void unapply(MarketAPI market) {


    }



    @Override
    public Color getColorForWagons(String industry) {
        return new Color(243, 196, 78);
    }

    @Override
    public CargoAPI generateCargoForGatheringPoint(MarketAPI market, Random random) {
        if(!doesSynergyMetReq(market)){
            return super.generateCargoForGatheringPoint(market, random);
        }

        float mult = getTechMiningMult( market);
        float decay = Global.getSettings().getFloat("techMiningDecay");
        float base = getTechMiningRuinSizeModifier(market);
        int chances = 1;
        if(market.hasIndustry(AoTDIndustries.MINING_MEGAPLEX)){
            chances =3;
        }


        base *= IndustrySynergiesMisc.getEffectivenessMult(market);


        java.util.List<SalvageEntityGenDataSpec.DropData> dropRandom = new ArrayList<SalvageEntityGenDataSpec.DropData>();
        java.util.List<SalvageEntityGenDataSpec.DropData> dropValue = new ArrayList<SalvageEntityGenDataSpec.DropData>();

        SalvageEntityGenDataSpec.DropData d = new SalvageEntityGenDataSpec.DropData();
        d.chances = chances;
        d.group = "blueprints_low";
        //d.addCustom("item_:{tags:[single_bp], p:{tags:[rare_bp]}}", 1f);
        dropRandom.add(d);

        d = new SalvageEntityGenDataSpec.DropData();
        d.chances = chances;
        d.group = "rare_tech_low";
        d.valueMult = 0.1f;
        dropRandom.add(d);


        CargoAPI result = SalvageEntity.generateSalvage(random, 1f, 1f, base * mult, 1f, dropValue, dropRandom);

        FactionAPI pf = Global.getSector().getPlayerFaction();
        OUTER: for (CargoStackAPI stack : result.getStacksCopy()) {
            if (stack.getPlugin() instanceof BlueprintProviderItem) {
                BlueprintProviderItem bp = (BlueprintProviderItem) stack.getPlugin();
                List<String> list = bp.getProvidedShips();
                if (list != null) {
                    for (String id : list) {
                        if (!pf.knowsShip(id)) continue OUTER;
                    }
                }

                list = bp.getProvidedWeapons();
                if (list != null) {
                    for (String id : list) {
                        if (!pf.knowsWeapon(id)) continue OUTER;
                    }
                }

                list = bp.getProvidedFighters();
                if (list != null) {
                    for (String id : list) {
                        if (!pf.knowsFighter(id)) continue OUTER;
                    }
                }

                list = bp.getProvidedIndustries();
                if (list != null) {
                    for (String id : list) {
                        if (!pf.knowsIndustry(id)) continue OUTER;
                    }
                }
                result.removeStack(stack);
            } else if (stack.getPlugin() instanceof ModSpecItemPlugin) {
                ModSpecItemPlugin mod = (ModSpecItemPlugin) stack.getPlugin();
                if (!pf.knowsHullMod(mod.getModId())) continue OUTER;
                result.removeStack(stack);
            }
        }

        //result.addMothballedShip(FleetMemberType.SHIP, "hermes_d_Hull", null);

        return result;
    }

    @Override
    public int getAmountOfWagonsForUI(String industry) {
        return 4;
    }
}
