package data.kaysaar.aotd.vok.campaign.econ.stablestructures.ui;

import ashlib.data.plugins.misc.AshMisc;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.tot.stablestructures.AoTDStableStructureImpl;
import data.kaysaar.aotd.tot.stablestructures.BaseStableStructureOptionProvider;
import data.kaysaar.aotd.tot.ui.starsystems.stuctures.widget.StableStructureWidget;
import data.kaysaar.aotd.vok.campaign.econ.stablestructures.impl.GnajidIndustrialStationImpl;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class GnajidAICOptionProvider extends BaseStableStructureOptionProvider {
    public static final String DEPLOY_AIC = "gnajid_deploy_aic";

    @Override
    public List<StructureOptionData> getStructureOptions(AoTDStableStructureImpl structure) {
        if (!(structure instanceof GnajidIndustrialStationImpl gnajid)) return Collections.emptyList();
        StructureOptionData option = new StructureOptionData("Deploy AIC...", DEPLOY_AIC, gnajid, this);
        option.color = Color.cyan;
        option.enabled = gnajid.canDeployAIC() && !gnajid.getEligibleMarkets().isEmpty();
        if (gnajid.getCooldownDaysRemaining() > 0) {
            option.text = "Deploy AIC (" + gnajid.getCooldownDaysRemaining() + " days)";
        }
        return Collections.singletonList(option);
    }

    @Override
    public void optionSelected(StructureOptionData opt, StableStructureWidget widget) {
        if (!DEPLOY_AIC.equals(opt.id) || !(opt.structure instanceof GnajidIndustrialStationImpl gnajid)) return;
        if (!gnajid.canDeployAIC() || gnajid.getEligibleMarkets().isEmpty()) return;
        AshMisc.initPopUpDialog(new GnajidAICDeployDialog(gnajid, widget), 660f, 500f);
    }

    @Override
    public void createTooltip(StructureOptionData opt, TooltipMakerAPI tooltip, float width) {
        if (!(opt.structure instanceof GnajidIndustrialStationImpl station)) return;
        tooltip.addPara("Deploy an Advanced Industrial Controller to a selected player colony. " +
                "Each controller grants +20% industry construction speed, +50% synergy efficiency and -10% commodity demand.", 0f);
        tooltip.addPara("Targets must be in the station's star system or connected to it through active Bifrost gates.",
                Misc.getGrayColor(), 5f);
        if (!station.isFunctional() || station.isBuilding()) {
            tooltip.addPara("The station must be fully operational before deployment.",
                    Misc.getNegativeHighlightColor(), 5f);
        } else if (station.getCooldownDaysRemaining() > 0) {
            tooltip.addPara("Next deployment available in %s days.", 5f,
                    Misc.getNegativeHighlightColor(), String.valueOf(station.getCooldownDaysRemaining()));
        } else if (station.getEligibleMarkets().isEmpty()) {
            tooltip.addPara("No eligible player colonies are available for deployment.",
                    Misc.getNegativeHighlightColor(), 5f);
        } else {
            tooltip.addPara("Ready to deploy to %s available colonies.", 5f,
                    Misc.getPositiveHighlightColor(), String.valueOf(station.getEligibleMarkets().size()));
        }
    }
}
