package data.kaysaar.aotd.vok.scripts.specialprojects.projects.shroud;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.util.ArrayList;

public class TenebriumHullmodApplication extends ShroudBasedProject {
    public static ArrayList<String> TENEBRIUM_HULLMODS = new ArrayList<>();

    static {
        TENEBRIUM_HULLMODS.add(HullMods.SHROUDED_MANTLE);
        TENEBRIUM_HULLMODS.add("shrouded_thunderhead");
        TENEBRIUM_HULLMODS.add("shrouded_lens");
    }
    @Override
    public void createRewardSection(TooltipMakerAPI tooltip, float width) {
        tooltip.addPara("Unlock new hullmods related to shrouded stuff!", Misc.getPositiveHighlightColor(),5f);
    }

    @Override
    public int getRequiredShroudExpertLevel() {
        return 3;
    }

    @Override
    public Object grantReward() {
        TENEBRIUM_HULLMODS.forEach(x-> Global.getSector().getPlayerFaction().addKnownHullMod(x));

        return null;
    }
}
