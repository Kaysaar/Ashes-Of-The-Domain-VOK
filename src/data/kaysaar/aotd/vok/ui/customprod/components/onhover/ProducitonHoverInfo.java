package data.kaysaar.aotd.vok.ui.customprod.components.onhover;

import ashlib.data.plugins.info.FighterInfoGenerator;
import ashlib.data.plugins.info.ShipInfoGenerator;
import ashlib.data.plugins.info.WeaponInfoGenerator;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.loading.Description;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPSpec;

import java.util.Set;


public class ProducitonHoverInfo implements TooltipMakerAPI.TooltipCreator {

    GPSpec spec;
    int index =-1;
    Set<String> weapons;
    boolean embraced = false;
    boolean currentMode = false;
    public ProducitonHoverInfo(GPSpec spec) {
        this.spec = spec;
        if(spec.getType().equals(GPSpec.ProductionType.FIGHTER)){
            weapons = FighterInfoGenerator.getFighterWeapons(spec.getWingSpecAPI());
        }
    }
    @Override
    public boolean isTooltipExpandable(Object tooltipParam) {
        return true;
    }

    @Override
    public float getTooltipWidth(Object tooltipParam) {
        if (spec.getType().equals(GPSpec.ProductionType.SHIP)) {
            return 990f;
        } else {
            return 400f;
        }
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
        if (spec.getType().equals(GPSpec.ProductionType.SHIP)) {
            String variantId = null;
            for (String allVariantId : Global.getSettings().getAllVariantIds()) {
                if (allVariantId.contains(spec.getIdOfItemProduced())) {
                    variantId = allVariantId;
                    break;
                }
            }
            final CustomPanelAPI panelAPIs = ShipInfoGenerator.getShipImage(spec.getShipHullSpecAPI(), 250, null).one;
            CampaignFleetAPI fleet = Global.getFactory().createEmptyFleet(Global.getSector().getPlayerFaction().getId(), "test", false);

            FleetMemberAPI memberAPI = Global.getFactory().createFleetMember(FleetMemberType.SHIP, Global.getSettings().createEmptyVariant(variantId, spec.getShipHullSpecAPI()));
            fleet.getCargo().addCrew((int) memberAPI.getMinCrew());
            fleet.getCargo().addSupplies(memberAPI.getCargoCapacity() - 10);
            fleet.getCargo().addFuel(memberAPI.getFuelCapacity());
            fleet.getFleetData().addFleetMember(memberAPI);
            memberAPI.getRepairTracker().setCR(70);
            memberAPI.getRepairTracker().computeRepairednessFraction();
            ShipInfoGenerator.generate(tooltip, memberAPI, null, panelAPIs, getTooltipWidth(tooltipParam));
            fleet.deflate();
        }
        if (spec.getType() == GPSpec.ProductionType.WEAPON) {
            WeaponInfoGenerator.generate(tooltip, spec.getWeaponSpec(), getTooltipWidth(tooltipParam));
        }
        if (spec.getType() == GPSpec.ProductionType.FIGHTER) {
            if(!embraced){
                embraced = true;
                currentMode = expanded;
            }
            else{
                if(currentMode!=expanded){
                    currentMode = expanded;
                    index++;
                    if(index>=weapons.size()){
                        index =-1;
                    }
                }
            }
            if(index==-1){
                FighterInfoGenerator.generate(tooltip, spec.getWingSpecAPI(), getTooltipWidth(tooltipParam));

            }
            else{
               String[] wep =  weapons.toArray(new String[0]);
               WeaponInfoGenerator.generate(tooltip,Global.getSettings().getWeaponSpec(wep[index]), 400f);
            }
        }
        if (spec.getType() == GPSpec.ProductionType.ITEM) {
            final CargoStackAPI stack = Global.getFactory().createCargoStack(CargoAPI.CargoItemType.SPECIAL, new SpecialItemData(spec.getItemSpecAPI().getId(), null), null);
            stack.getPlugin().createTooltip(tooltip, expanded, null, null);

        }
        if (spec.getType() == GPSpec.ProductionType.AICORE) {
            tooltip.addTitle(spec.getAiCoreSpecAPI().getName());
            tooltip.addPara(Global.getSettings().getDescription(spec.getAiCoreSpecAPI().getId(), Description.Type.RESOURCE).getText1FirstPara(),10f);

        }
    }
}
