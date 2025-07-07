package data.kaysaar.aotd.vok.campaign.econ.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.FleetInflater;
import com.fs.starfarer.api.campaign.listeners.FleetInflationListener;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import data.kaysaar.aotd.vok.plugins.AoDUtilis;

import java.util.ArrayList;
import java.util.Collections;

public class AodAdvancedHeavyIndustryApplier implements FleetInflationListener {

    static ArrayList<String> SHullmods = new ArrayList<>();

    static {
        SHullmods.add(HullMods.HEAVYARMOR);
        SHullmods.add(HullMods.MISSLERACKS);
        SHullmods.add(HullMods.UNSTABLE_INJECTOR);
        SHullmods.add(HullMods.EXTENDED_SHIELDS);
        SHullmods.add(HullMods.ACCELERATED_SHIELDS);
        SHullmods.add(HullMods.ECCM);
        SHullmods.add(HullMods.ARMOREDWEAPONS);
        SHullmods.add(HullMods.HARDENED_SUBSYSTEMS);
        SHullmods.add(HullMods.FLUXBREAKERS);
        SHullmods.add(HullMods.STABILIZEDSHIELDEMITTER);
        SHullmods.add(HullMods.AUTOREPAIR);
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    @Override
    public void reportFleetInflated(CampaignFleetAPI fleet, FleetInflater inflater) {

        if (fleet.isPlayerFleet()) return;
        if (fleet.getFleetData().getCommander().equals(Global.getSector().getPlayerPerson())) return;
        if (AoDUtilis.getTTShipyard(fleet.getFaction()) == null) return;

        for (FleetMemberAPI fleetMemberAPI : fleet.getMembersWithFightersCopy()) {
            if (fleetMemberAPI.isFighterWing()) continue;
            if (fleetMemberAPI.isStation()) continue;
            if (fleetMemberAPI.isCivilian()) continue;
            if (fleetMemberAPI.isMothballed()) continue;
            int SModsAmount = determineAmountofSmods(fleet.getFaction());
            fleetMemberAPI.getStats().getDynamic().getMod(Stats.MAX_PERMANENT_HULLMODS_MOD).modifyFlat("Advanced Heavy Industry", SModsAmount);
            Collections.shuffle(SHullmods);
            for (int i = 0; i < SModsAmount; i++) {
                String variant = null;
                for (String sHullmod : SHullmods) {
                    if (!fleetMemberAPI.getVariant().hasHullMod(sHullmod)) {
                        variant = sHullmod;
                        break;
                    }
                }


                if (variant != null) {
                    fleetMemberAPI.getVariant().addPermaMod(variant, true);
                }
            }
            fleetMemberAPI.updateStats();


        }


    }


    public int determineAmountofSmods(FactionAPI factionAPI) {
        BaseIndustry ind = AoDUtilis.getTTShipyard(factionAPI);

        if (ind != null && ind.getAICoreId() != null && ind.getAICoreId().equals(Commodities.ALPHA_CORE)) {
            return getRandomNumber(2, 4);
        }
        return getRandomNumber(1, 3);
    }
}
