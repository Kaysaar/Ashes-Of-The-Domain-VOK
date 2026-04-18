package data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.ui.bifrost.gatebuilding;

import ashlib.data.plugins.ui.models.BasePopUpDialog;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.tot.plugins.AoTDCommodityEconSpecManager;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.sections.bifrost.BifrostSection;

import java.awt.*;

public class BifrostGateActivationDialog extends BasePopUpDialog {
    BifrostSection section;
    public BifrostGateActivationDialog(String headerTitle, BifrostSection section) {
        super(headerTitle);
        this.section = section;
    }

    @Override
    public void createContentForDialog(TooltipMakerAPI tooltip, float width) {
        tooltip.setParaInsigniaLarge();
        if(!section.isDisabled()){
            tooltip.addPara("De-activation of the gate will result in decrease of monthly upkeep to %s and decrease of purified transplutonics resource upkeep to %s, but it won't be usable until it is activated again!",5f, Color.ORANGE, Misc.getDGSCredits(section.getUpkeep(false)/2),"0");
        }
        else{

            tooltip.addPara("Activation of the gate will result in a monthly upkeep of %s and increase of purified transplutonics resource upkeep to %s",5f,Color.ORANGE, Misc.getDGSCredits(section.getUpkeep(false)*2),""+     AoTDCommodityEconSpecManager.getCargoAmountFromSupplyOrDemand(5,true, AoTDCommodities.PURIFIED_TRANSPLUTONICS));
        }
    }

    @Override
    public void applyConfirmScript() {
        section.setDisabled(!section.isDisabled());

    }
}
