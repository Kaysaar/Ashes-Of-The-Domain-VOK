package data.kaysaar.aotd.vok.scripts.specialprojects.projects;

import ashlib.data.plugins.misc.AshMisc;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PersistentUIDataAPI;
import com.fs.starfarer.api.impl.SharedUnlockData;
import com.fs.starfarer.api.impl.campaign.ids.Abilities;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.rulecmd.AddAbility;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.misc.AoTDCompoundUIInMarketScript;
import data.kaysaar.aotd.vok.scripts.misc.AoTDCompoundUIScript;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProject;

import java.util.ArrayList;
import java.util.List;

import static data.kaysaar.aotd.vok.scripts.misc.AoTDFuelConsumptionScript.addAbility;

public class CompoundSpecialProject extends AoTDSpecialProject {
    @Override
    public void createRewardSection(TooltipMakerAPI tooltip, float width) {
        tooltip.addPara("Gain access to produce new commodity : Compound, that can enhance efficiency of fuel.", Misc.getPositiveHighlightColor(),5f);
        tooltip.addPara("Each Fuel Refinery will have a supply of compound equal to half of the market size.", Misc.getPositiveHighlightColor(),5f);
    }
    @Override
    public boolean checkIfProjectShouldUnlock() {
        return AoTDMainResearchManager.getInstance().isResearchedForPlayer(AoTDTechIds.ANTIMATTER_SYNTHESIS)&& AoTDMisc.isPLayerHavingIndustry(AoTDIndustries.FUEL_REFINERY);
    }

    @Override
    public Object grantReward() {
        Global.getSector().getMemory().set("$aotd_compound_unlocked",true);
        addAbility("aotd_compound_toggle");
        SharedUnlockData.get().reportPlayerAwareOfCommodity("compound", true);
        Global.getSector().getPlayerFaction().getProduction().getGatheringPoint().getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addCommodity("compound",250);
        Global.getSector().addTransientScript(new AoTDCompoundUIScript());
        Global.getSector().addTransientScript(new AoTDCompoundUIInMarketScript());
        return null;
    }
}
