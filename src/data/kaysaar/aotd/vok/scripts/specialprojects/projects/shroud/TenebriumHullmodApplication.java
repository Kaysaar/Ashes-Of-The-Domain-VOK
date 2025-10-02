package data.kaysaar.aotd.vok.scripts.specialprojects.projects.shroud;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import niko_SA.augments.core.stationAugmentStore;

import java.util.ArrayList;

public class TenebriumHullmodApplication extends ShroudBasedProject {
    public static ArrayList<String> TENEBRIUM_HULLMODS = new ArrayList<>();
    public static ArrayList<String> STATION_AUGMENTS = new ArrayList<>();

    static {
        // Hullmods
        TENEBRIUM_HULLMODS.add(HullMods.SHROUDED_MANTLE);
        TENEBRIUM_HULLMODS.add("shrouded_thunderhead");
        TENEBRIUM_HULLMODS.add("shrouded_lens");

        // Station augments
        STATION_AUGMENTS.add("SA_shroudedMantle");
        STATION_AUGMENTS.add("SA_shroudedLens");
        STATION_AUGMENTS.add("SA_shroudedThunderhead");
    }

    @Override
    public void createRewardSection(TooltipMakerAPI tooltip, float width) {
        tooltip.addPara("Unlock new hullmods related to shrouded stuff!", Misc.getPositiveHighlightColor(), 5f);
    }

    @Override
    public int getRequiredShroudExpertLevel() {
        return 3;
    }

    @Override
    public Object grantReward() {
        // Unlock hullmods for player


        return null;
    }
}
