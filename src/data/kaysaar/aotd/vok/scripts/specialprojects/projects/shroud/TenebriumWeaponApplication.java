package data.kaysaar.aotd.vok.scripts.specialprojects.projects.shroud;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.util.ArrayList;

public class TenebriumWeaponApplication extends ShroudBasedProject {
    public static ArrayList<String> TENEBRIUM_WEAPONS = new ArrayList<>();

    static {
        TENEBRIUM_WEAPONS.add("pseudoparticle_jet");
        TENEBRIUM_WEAPONS.add("assaying_rift");
        TENEBRIUM_WEAPONS.add("rift_lightning");
        TENEBRIUM_WEAPONS.add("abyssal_glare");
        TENEBRIUM_WEAPONS.add("inimical_emanation");

    }
    @Override
    public void createRewardSection(TooltipMakerAPI tooltip, float width) {
        tooltip.addPara("Unlock new line-up of powerful weapons, that will be using Tenebrium cells!", Misc.getPositiveHighlightColor(),5f);
    }
    @Override
    public int getRequiredShroudExpertLevel() {
        return 3;
    }

    @Override
    public Object grantReward() {
        TENEBRIUM_WEAPONS.forEach(x-> Global.getSector().getPlayerFaction().addKnownWeapon(x,true));
        return super.grantReward();
    }
}
