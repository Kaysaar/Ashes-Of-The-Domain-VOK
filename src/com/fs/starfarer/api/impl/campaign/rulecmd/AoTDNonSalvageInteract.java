package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl;

import java.util.ArrayList;

public class AoTDNonSalvageInteract extends FleetInteractionDialogPluginImpl {
    public AoTDNonSalvageInteract(FIDConfig params) {
        this.config = params;

        if (origFlagship == null) {
            origFlagship = Global.getSector().getPlayerFleet().getFlagship();
        }
        if (origCaptains.isEmpty()) {
            for (FleetMemberAPI member : Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy()) {
                origCaptains.put(member, member.getCaptain());
            }
            membersInOrderPreEncounter = new ArrayList<FleetMemberAPI>(Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy());
     ;
        }
        context = new AoTDNonSalvageContext();
    }
}
