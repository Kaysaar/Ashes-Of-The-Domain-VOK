package data.kaysaar.aotd.vok.scripts.specialprojects.projects.shroud;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProject;
import org.lazywizard.lazylib.MathUtils;

public class ShroudUnderstanding extends AoTDSpecialProject {
    @Override
    public void createRewardSection(TooltipMakerAPI tooltip, float width) {
        tooltip.addPara("Completing this project will yield greater understanding about this \"thing\", which can result in further projects.", Misc.getTooltipTitleAndLightHighlightColor(),5f);
    }

    @Override
    public boolean checkIfProjectShouldUnlock() {
        return Global.getSector().getPlayerFleet().getCargo().getQuantity(CargoAPI.CargoItemType.SPECIAL, new SpecialItemData("aotd_shrouded_substrate",null))>0;
    }

    @Override
    public Object grantReward() {
        ShroudProjectMisc.setLevelOfUnderstanding(1);
        ShroudProjectMisc.setCooldownBetweenProjects(MathUtils.getRandomNumberInRange(10,20));
        return super.grantReward();
    }
}
