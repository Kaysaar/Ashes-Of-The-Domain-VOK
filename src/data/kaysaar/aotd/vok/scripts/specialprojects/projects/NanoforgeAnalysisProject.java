package data.kaysaar.aotd.vok.scripts.specialprojects.projects;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProject;
import org.lazywizard.lazylib.MathUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class NanoforgeAnalysisProject extends AoTDSpecialProject {
    boolean pristine = false;
    public static final ArrayList<String> items = new ArrayList<String>(Arrays.asList(
            "pristine_nanoforge",
            "synchrotron",
            "orbital_fusion_lamp",
            "mantle_bore",
            "catalytic_core",
            "soil_nanites",
            "biofactory_embryo",
            "fullerene_spool",
            "plasma_dynamo",
            "cryoarithmetic_engine",
            "drone_replicator",
            "dealmaker_holosuite",
            "coronal_portal"
    ));
    public String lastItemSavedID;

    @Override
    public void createRewardSection(TooltipMakerAPI tooltip, float width) {
        tooltip.addPara("50 % chance to reconfigure nanoforge to be pristine quality.", Misc.getPositiveHighlightColor(), 5f);
        tooltip.addPara("50 % chance to reconfigure nanoforge to become different item.", Misc.getPositiveHighlightColor(), 5f);

    }

    @Override
    public Object grantReward() {
        pristine = Misc.random.nextBoolean();
        MarketAPI playerMarket = Global.getSector().getPlayerFaction().getProduction().getGatheringPoint();
        if (playerMarket == null) {
            playerMarket = Misc.getPlayerMarkets(true).get(0);
        }
        if (pristine) {
            playerMarket.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addSpecial(new SpecialItemData(Items.PRISTINE_NANOFORGE, null), 1);
            lastItemSavedID = Items.PRISTINE_NANOFORGE;
        } else {
            int random = MathUtils.getRandomNumberInRange(0, items.size() - 1);
            String id = items.get(random);
            lastItemSavedID = id;
            if (playerMarket.hasSubmarket(Submarkets.SUBMARKET_STORAGE)) {
                playerMarket.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addSpecial(new SpecialItemData(id, null), 1);
            }

        }
        return null;

    }

    @Override
    public void createRewardSectionForInfo(TooltipMakerAPI tooltip, float width) {
        super.createRewardSectionForInfo(tooltip, width);
        if(pristine) {
            tooltip.addPara("By using AI algorithms we managed to reconfigure nanoforge, getting rid of it's corruption.",Misc.getPositiveHighlightColor(), 5f);
        }
        else{
            tooltip.addPara("By using AI algorithms we managed to reconfigure nanoforge, but corruption instead of being fixed, escalated, which resulted in total reconfiguration.",Misc.getPositiveHighlightColor(), 5f);
            tooltip.addPara("Gain "+Global.getSettings().getSpecialItemSpec(lastItemSavedID).getName(),5f);
        }
    }

    @Override
    public boolean checkIfProjectShouldUnlock() {
        return AoTDMainResearchManager.getInstance().isResearchedForPlayer(AoTDTechIds.CORE_DIRECTIVE_ENGINEERING);
    }
}
