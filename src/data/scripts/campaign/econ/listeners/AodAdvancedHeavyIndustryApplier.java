package data.scripts.campaign.econ.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.FleetInflater;
import com.fs.starfarer.api.campaign.listeners.FleetInflationListener;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import data.plugins.AoDUtilis;

import java.util.ArrayList;
import java.util.Collections;

public class AodAdvancedHeavyIndustryApplier implements FleetInflationListener {

    ArrayList<String> SHullmods = new ArrayList<>();

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
    @Override
    public void reportFleetInflated(CampaignFleetAPI fleet, FleetInflater inflater) {
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
        if (fleet.isPlayerFleet()) return;
        if (!AoDUtilis.isFactionPossesingTriTachyonShipyards(fleet.getFaction())) return;

        for (FleetMemberAPI fleetMemberAPI : fleet.getMembersWithFightersCopy()) {
            if(fleetMemberAPI.isFighterWing())continue;
            if(fleetMemberAPI.isStation())continue;
            if(fleetMemberAPI.isCivilian())continue;
            if(fleetMemberAPI.isMothballed())continue;

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
        return getRandomNumber(0,3);
    }
}
