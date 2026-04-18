package data.kaysaar.aotd.vok.ui.onhover;

import ashlib.data.plugins.info.FighterInfoGenerator;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.impl.campaign.shared.WormholeManager;
import com.fs.starfarer.api.loading.Description;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.produciton.specs.AoTDProductionSpec;

import java.util.Set;


public class ProducitonHoverInfo implements TooltipMakerAPI.TooltipCreator {

    AoTDProductionSpec spec;
    int index =-1;
    Set<String> weapons;
    boolean embraced = false;
    boolean currentMode = false;
    public ProducitonHoverInfo(AoTDProductionSpec spec) {
        this.spec = spec;
        if(spec.getProductionType().equals(AoTDProductionSpec.AoTDProductionSpecType.FIGHTER)){
            weapons = FighterInfoGenerator.getFighterWeapons((FighterWingSpecAPI) spec.getUnderlyingSpec());
        }
    }
    @Override
    public boolean isTooltipExpandable(Object tooltipParam) {
        return true;
    }

    @Override
    public float getTooltipWidth(Object tooltipParam) {
        if (spec.getProductionType().equals(AoTDProductionSpec.AoTDProductionSpecType.SHIP)) {
            return 990f;
        } else {
            return 400f;
        }
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
        if (spec.getProductionType() == AoTDProductionSpec.AoTDProductionSpecType.SPECIAL_ITEM) {
            // Edge-case fix regarding Wormhole Anchor item requiring data when showing tooltip
            final CargoStackAPI stack;
            if (spec.getId().equals("wormhole_anchor")) {
                WormholeManager.WormholeItemData itemData = new WormholeManager.WormholeItemData("standard", "unknown", "Unknown");
                stack = Global.getFactory().createCargoStack(CargoAPI.CargoItemType.SPECIAL, new SpecialItemData(spec.getId(), itemData.toJsonStr()), null);

            }
            else {
                stack = Global.getFactory().createCargoStack(CargoAPI.CargoItemType.SPECIAL, new SpecialItemData(spec.getId(), null), null);
            }
            stack.getPlugin().createTooltip(tooltip, expanded, null, null);

        }
        if (spec.getProductionType() == AoTDProductionSpec.AoTDProductionSpecType.COMMODITY_ITEM) {
            tooltip.addTitle(spec.getName());
            tooltip.addPara(Global.getSettings().getDescription(spec.getId(), Description.Type.RESOURCE).getText1FirstPara(),10f);

        }
    }
}
