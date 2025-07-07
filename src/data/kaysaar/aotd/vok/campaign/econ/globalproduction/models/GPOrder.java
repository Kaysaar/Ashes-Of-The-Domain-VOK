package data.kaysaar.aotd.vok.campaign.econ.globalproduction.models;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.fleets.DefaultFleetInflaterParams;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.util.HashMap;
import java.util.LinkedHashMap;

import static com.fs.starfarer.api.util.Misc.random;

public class GPOrder implements Cloneable {
    int amountToProduce;
    int alreadyProduced;
    float dummyCounter;
    float daysSpentDoingOrder;// between 0 and 1
    boolean contributingToOrder;
    int atOnce = 1;
    LinkedHashMap<String, Float> progressMap = new LinkedHashMap<>();

    public float getDaysSpentDoingOrder() {
        return daysSpentDoingOrder;
    }

    public GPOrder cloneOrder() {
        try {
            return (GPOrder) clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    String specId;

    public int getAmountToProduce() {
        return amountToProduce - alreadyProduced;
    }

    public float getDaysTillOrderFinished() {
        float baseDays = getSpecFromClass().days * getBonus();
        if (baseDays <= 1) baseDays = 1; // Base days can't be less than 1

        // Penalized days remaining (with penalty applied)
        float effectiveDaysRemaining = baseDays - (daysSpentDoingOrder);

        return effectiveDaysRemaining > 0 ? effectiveDaysRemaining : 0;
    }

    public float getProgressPercentage() {
        float baseDays = getSpecFromClass().days * getBonus();
        if (baseDays <= 1) baseDays = 1;

        // Calculate progress using penalized time
        float progressPercentage = daysSpentDoingOrder / (baseDays);

        // Ensure the progress does not exceed 100%
        return Math.min(progressPercentage, 1.0f);
    }

    public float getDaysForLabel() {


        float baseDays = getSpecFromClass().days * getBonus();
        if (baseDays <= 1) baseDays = 1;
        float toReturn = (baseDays / penalty) - (baseDays / penalty) * getProgressPercentage();
        return toReturn;
    }

    public float getBonus() {
        float bonus = 1f;
        if (this.getSpecFromClass().getType().equals(GPSpec.ProductionType.SHIP)) {
            if (this.getSpecFromClass().getShipHullSpecAPI().getHullSize().equals(ShipAPI.HullSize.CAPITAL_SHIP) || this.getSpecFromClass().getShipHullSpecAPI().getHullSize().equals(ShipAPI.HullSize.CRUISER)) {
                bonus = GPManager.getInstance().getCruiserCapitalSpeed().getModifiedValue();
            } else {
                bonus = GPManager.getInstance().getFrigateDestroyerSpeed().getModifiedValue();

            }
        }
        return bonus;
    }

    HashMap<String, Integer> assignedResources = new HashMap<>();
    HashMap<String, Integer> resourcesGet = new HashMap<>();

    public boolean canProceed() {
        if (isAboutToBeRemoved()) return false;
        // Check if the obtained resources meet or exceed the required resources
        for (String s : getSpecFromClass().getSupplyCost().keySet()) {
            if (GPManager.getInstance().getTotalResources().get(s) <= 0) {
                return false;
            }
        }
        // If all required resources meet or exceed the required amounts, return true
        return true;
    }

    public int getAmountOfItemsProduced() {
        int amount = atOnce;
        return Math.min(amount, getAmountToProduce());
    }

    public GPOrder(String specID, int amountToProduce) {
        this.specId = specID;
        updateAmountToProduce(amountToProduce);
        assignedResources = getSpec(specID).supplyCost;
        contributingToOrder = true;
    }

    public void updateResourceCost() {
        assignedResources = getSpecFromClass().supplyCost;
    }

    public static GPSpec getSpec(String id) {
        for (GPSpec spec : GPManager.getInstance().getSpecs()) {
            if (spec.getProjectId().equals(id)) {
                return spec;
            }
        }
        return null;
    }

    public GPSpec getSpecFromClass() {
        for (GPSpec spec : GPManager.getInstance().getSpecs()) {
            if (spec.getProjectId().equals(this.specId)) {
                return spec;
            }

        }
        return null;
    }

    public void setContributingToOrder(boolean contributingToOrder) {
        this.contributingToOrder = contributingToOrder;
    }

    public boolean isAboutToBeRemoved() {
        return amountToProduce <= 0;
    }

    public boolean isCountingToContribution() {
        return contributingToOrder && amountToProduce > 0;
    }

    public void updateAmountToProduce(int newValue) {
        this.amountToProduce = newValue;
        if (this.atOnce > this.amountToProduce) {
            this.atOnce = amountToProduce;
        }
    }

    public boolean haveMetQuota() {
        return alreadyProduced >= amountToProduce;
    }

    public void setAtOnce(int atOnce) {
        if (atOnce <= 1) {
            atOnce = 1;
        }
        if (atOnce > getAmountToProduce()) {
            atOnce = getAmountToProduce();
        }
        this.atOnce = atOnce;
    }

    float penalty;

    public void setPenalty(float penalty) {
        this.penalty = penalty;
    }

    public int getAtOnce() {
        return atOnce;
    }

    public HashMap<String, Integer> getReqResources() {
        return getSpecFromClass().supplyCost;
    }

    public void setDaysSpentDoingOrder(float daysSpentDoingOrder) {
        this.daysSpentDoingOrder = daysSpentDoingOrder;
    }

    public void advance(float amount) {
        daysSpentDoingOrder += Global.getSector().getClock().convertToDays(amount) * penalty;
        if (getDaysTillOrderFinished() <= 0) {
            int produced = getAmountOfItemsProduced();
            amountToProduce -= getAmountOfItemsProduced();
            FactionAPI pf = Global.getSector().getPlayerFaction();
            FactionProductionAPI prod = pf.getProduction();


            MarketAPI gatheringPoint = prod.getGatheringPoint();
            if (gatheringPoint == null) return;

            //CargoAPI local = Misc.getLocalResourcesCargo(gatheringPoint);
            CargoAPI local = Misc.getStorageCargo(gatheringPoint);
            if (getSpecFromClass().type == GPSpec.ProductionType.WEAPON) {
                local.addWeapons(getSpecFromClass().getIdOfItemProduced(), produced);
                GpHistory.reportPlayerProducedStuff(this.getSpecFromClass(),null,produced);
            }
            if (getSpecFromClass().type == GPSpec.ProductionType.AICORE) {
                local.addCommodity(getSpecFromClass().getAiCoreSpecAPI().getId(), produced);
                GpHistory.reportPlayerProducedStuff(this.getSpecFromClass(),null,produced);
            }
            if (getSpecFromClass().type == GPSpec.ProductionType.ITEM) {
                local.addSpecial(new SpecialItemData(getSpecFromClass().getItemSpecAPI().getId(), null), produced);
                GpHistory.reportPlayerProducedStuff(this.getSpecFromClass(),null,produced);
            }
            if (getSpecFromClass().type == GPSpec.ProductionType.SHIP) {
                float quality = -1f;
                for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
                    if (!market.isPlayerOwned()) continue;
                    //quality = Math.max(quality, ShipQuality.getShipQuality(market, Factions.PLAYER));
                    float currQuality = market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).computeEffective(0f);
                    currQuality += market.getStats().getDynamic().getMod(Stats.FLEET_QUALITY_MOD).computeEffective(0f);
                    quality = Math.max(quality, currQuality);
                }
                quality -= Global.getSector().getFaction(Factions.PLAYER).getDoctrine().getShipQualityContribution();
                quality += 4f * Global.getSettings().getFloat("doctrineFleetQualityPerPoint");
                CampaignFleetAPI ships = Global.getFactory().createEmptyFleet(Factions.PLAYER, "temp", true);
                ships.setCommander(Global.getSector().getPlayerPerson());
                for (int i = 0; i < produced; i++) {
                    FleetMemberAPI member = ships.getFleetData().addFleetMember(AoTDMisc.getVaraint(getSpecFromClass().getShipHullSpecAPI()));

                    member.getVariant().clear();
                }
                DefaultFleetInflaterParams p = new DefaultFleetInflaterParams();
                p.quality = quality;
                p.mode = FactionAPI.ShipPickMode.PRIORITY_THEN_ALL;
                p.persistent = false;
                p.seed = random.nextLong();
                p.timestamp = null;
                FleetInflater inflater = Misc.getInflater(ships, p);
                ships.setInflater(inflater);
                inflater.inflate(ships);
                GpHistory.reportPlayerProducedStuff(this.getSpecFromClass(),ships,produced);
                for (FleetMemberAPI fleetMemberAPI : ships.getFleetData().getMembersListCopy()) {
                    fleetMemberAPI.getVariant().clear();
                    local.getMothballedShips().addFleetMember(fleetMemberAPI);
                }

                ships.despawn();

            }
            if (getSpecFromClass().type == GPSpec.ProductionType.FIGHTER) {
                local.addFighters(getSpecFromClass().getIdOfItemProduced(), produced);
            }
            if (!isAboutToBeRemoved()) {
                daysSpentDoingOrder = 0;
            }
        }
    }


}
