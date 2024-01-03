package data.kaysaar.aotd.vok.scripts.research;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.Script;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;
import com.fs.starfarer.api.impl.campaign.fleets.RouteLocationCalculator;
import com.fs.starfarer.api.impl.campaign.fleets.RouteManager;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.intel.ResearchExpeditionIntel;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RouteFleetAssignmentAI;

public class ResearchFleetRouteManager extends RouteFleetAssignmentAI implements FleetActionTextProvider, FleetEventListener {
    public SectorEntityToken target;
    public static int counter = 6;

    public ResearchFleetRouteManager(CampaignFleetAPI fleet, RouteManager.RouteData route, SectorEntityToken target) {

        super(fleet, route);
        this.target = target;
        giveInitialAssignments();
    }

    @Override
    protected void giveInitialAssignments() {
        if (target == null) return;
        RouteManager.RouteSegment current = route.getCurrent();
        SectorEntityToken source = route.getMarket().getPrimaryEntity();
        RouteLocationCalculator.TaskInterval[] intervals = new RouteLocationCalculator.TaskInterval[]{
                RouteLocationCalculator.TaskInterval.days(3f + (float) Math.random() * 3f),
                RouteLocationCalculator.TaskInterval.travel(),
                RouteLocationCalculator.TaskInterval.days(30f + (float) Math.random() * 30f),
                RouteLocationCalculator.TaskInterval.travel(),
                RouteLocationCalculator.TaskInterval.remaining(1f),
        };
        RouteLocationCalculator.computeIntervalsAndSetLocation(fleet, current.elapsed, current.daysMax,
                false, intervals,
                source, source, target, target, source, source);
        fleet.clearAssignments();
        fleet.addEventListener(this);
        if (intervals[0].value > 0 && !fleet.getFaction().getId().contains("luddic")) {
            fleet.addAssignment(FleetAssignment.ORBIT_PASSIVE, source, intervals[0].value, "Preparing for cleansing", false, null, null);
        }
        if (intervals[0].value > 0 && fleet.getFaction().getId().contains("luddic")) {
            fleet.addAssignment(FleetAssignment.ORBIT_PASSIVE, source, intervals[0].value, "Preparing for expedition", false, null, null);
        }
        if (intervals[1].value > 0 && fleet.getFaction().getId().contains("luddic")) {
            fleet.addAssignment(FleetAssignment.GO_TO_LOCATION, target, RouteLocationCalculator.getTravelDays(source, target), "Searching Moloch's infection", false, null, null);
        }
        if (intervals[1].value > 0 && !fleet.getFaction().getId().contains("luddic")) {
            fleet.addAssignment(FleetAssignment.GO_TO_LOCATION, target, RouteLocationCalculator.getTravelDays(source, target), "Searching for Lost Knowledge", false, null, null);
        }
        if (intervals[2].value > 0 && !fleet.getFaction().getId().contains("luddic")) {
            fleet.addAssignment(FleetAssignment.ORBIT_AGGRESSIVE, target, intervals[2].value, "Mining for lost technology", false, new Script() {
                @Override
                public void run() {
                    fleet.getMemoryWithoutUpdate().set(MemFlags.FLEET_IGNORES_OTHER_FLEETS, false);
                    fleet.getMemoryWithoutUpdate().set(MemFlags.FLEET_FIGHT_TO_THE_LAST, true);
                    target.getMemory().set("$aotd_r_expedition_present",true);

                }
            }, new Script() {
                @Override
                public void run() {
                    fleet.getCargo().addCommodity("research_databank", 15);
                    target.getMarket().getMemory().set("$aotd_failed_pre_collapse", true);
                    target.getMemory().unset("$aotd_r_expedition_present");
                }
            });
        }
        if (intervals[2].value > 0 && fleet.getFaction().getId().contains("luddic")) {
            fleet.addAssignment(FleetAssignment.ORBIT_AGGRESSIVE, target, intervals[2].value, "Preparing for Saturation Bombardment", false, new Script() {
                @Override
                public void run() {
                    fleet.getMemoryWithoutUpdate().set(MemFlags.FLEET_IGNORES_OTHER_FLEETS, false);
                    fleet.getMemoryWithoutUpdate().set(MemFlags.FLEET_FIGHT_TO_THE_LAST, true);
                    target.getMemory().set("$aotd_r_expedition_present",true);

                }
            }, new Script() {
                @Override
                public void run() {
                    fleet.getCargo().addCommodity("research_databank", 15);
                    target.getMarket().getMemory().set("$aotd_failed_pre_collapse", true);
                    target.getMemory().unset("$aotd_r_expedition_present");
                }
            });
        }
        if (intervals[3].value > 0) {
            fleet.addAssignment(FleetAssignment.GO_TO_LOCATION, source, RouteLocationCalculator.getTravelDays(target, source), "Returning to " + route.getMarket(), new Script() {
                @Override
                public void run() {
                    for (IntelInfoPlugin intelInfoPlugin : Global.getSector().getIntelManager().getIntel(ResearchExpeditionIntel.class)) {
                        if(((ResearchExpeditionIntel)intelInfoPlugin).idOfIntel.split("_")[1].equals(fleet.getFaction().getId())){
                            ((ResearchExpeditionIntel)intelInfoPlugin).finished=true;
                        }
                    }
                    AoTDMainResearchManager.getInstance().getSpecificFactionManager(fleet.getFaction()).setAICounter(counter);

                }
            });

        }
        fleet.addAssignment(FleetAssignment.ORBIT_PASSIVE, source, intervals[4].value, "Orbiting");
        fleet.addAssignment(FleetAssignment.GO_TO_LOCATION_AND_DESPAWN, source, 10f,
                "Stand down", goNextScript(current));
        fleet.getAI().setActionTextProvider(this);


    }

    @Override
    public void advance(float amount) {
//		if (fleet.isInCurrentLocation() &&
//				Misc.getDistance(Global.getSector().getPlayerFleet(), fleet) < fleet.getRadius()) {
//			System.out.println("ewfwefwe");
//		}
        super.advance(amount);
        checkCapture(amount);
        checkBuild(amount);
    }

    @Override
    public String getActionText(CampaignFleetAPI fleet) {
        return null;
    }

    @Override
    public void reportFleetDespawnedToListener(CampaignFleetAPI fleet, CampaignEventListener.FleetDespawnReason reason, Object param) {
        target.getMemory().unset("$aotd_r_expedition_present");
    }

    @Override
    public void reportBattleOccurred(CampaignFleetAPI fleet, CampaignFleetAPI primaryWinner, BattleAPI battle) {

    }
}
