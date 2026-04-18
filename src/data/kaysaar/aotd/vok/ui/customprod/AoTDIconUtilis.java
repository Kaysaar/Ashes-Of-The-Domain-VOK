package data.kaysaar.aotd.vok.ui.customprod;

import ashlib.data.plugins.info.FighterInfoGenerator;
import ashlib.data.plugins.info.ShipInfoGenerator;
import ashlib.data.plugins.info.WeaponInfoGenerator;
import ashlib.data.plugins.misc.AshMisc;
import ashlib.data.plugins.ui.models.resizable.ImageViewer;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SpecialItemSpecAPI;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.loading.FormationType;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;

import data.kaysaar.aotd.vok.campaign.econ.produciton.specs.AoTDProductionSpec.AoTDProductionSpecType;
import data.kaysaar.aotd.vok.ui.UIData;



public class AoTDIconUtilis {

    public static CustomPanelAPI getIcon(String id, AoTDProductionSpecType type, float size) {

        CustomPanelAPI container = Global.getSettings().createCustom(size, size, null);

        switch (type) {

            case FIGHTER -> {
                FighterWingSpecAPI spec = Global.getSettings().getFighterWingSpec(id);

                CustomPanelAPI fighterPanel =
                        FighterInfoGenerator.createFormationPanel(spec, FormationType.BOX, (int) size, spec.getNumFighters()).one;

                container.addComponent(fighterPanel).inTL(0, 0);

                FleetMemberAPI fleetMember =
                        Global.getFactory().createFleetMember(FleetMemberType.FIGHTER_WING, id);

               UIData.createFighterTooltip(fleetMember, spec, fighterPanel);
            }

            case SHIP -> {
                ShipHullSpecAPI hull = Global.getSettings().getHullSpec(id);

                CustomPanelAPI shipPanel =
                        ShipInfoGenerator.getShipImage(hull, size, null).one;

                container.addComponent(shipPanel).inTL(0, 0);

                FleetMemberAPI fleetMember =
                        Global.getFactory().createFleetMember(
                                FleetMemberType.SHIP,
                                AshMisc.getVaraint(hull)
                        );

                fleetMember.getRepairTracker().setCR(0.7f);
                fleetMember.getCrewComposition().addCrew(fleetMember.getMinCrew());
                fleetMember.updateStats();

                UIData.createTooltipForShip(fleetMember, shipPanel);
            }

            case WEAPON -> {
                WeaponSpecAPI spec = Global.getSettings().getWeaponSpec(id);

                CustomPanelAPI weaponPanel =
                        WeaponInfoGenerator.getImageOfWeapon(spec, size - 6f).one;

                container.addComponent(weaponPanel).inTL(1f, 3f);

                UIData.createWeaponTooltip(spec, weaponPanel);
            }

            case SPECIAL_ITEM -> {
                SpecialItemSpecAPI spec = Global.getSettings().getSpecialItemSpec(id);

                ImageViewer viewer =
                        new ImageViewer(size, size, spec.getIconName());

                container.addComponent(viewer.getComponentPanel()).inTL(0, 0);
            }

            case COMMODITY_ITEM -> {
                CommoditySpecAPI spec = Global.getSettings().getCommoditySpec(id);

                ImageViewer viewer =
                        new ImageViewer(size, size, spec.getIconName());

                container.addComponent(viewer.getComponentPanel()).inTL(0, 0);
            }
        }

        return container;
    }

}