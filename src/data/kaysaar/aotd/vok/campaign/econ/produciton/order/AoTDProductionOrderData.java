package data.kaysaar.aotd.vok.campaign.econ.produciton.order;

import ashlib.data.plugins.misc.AshMisc;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MonthlyReport;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.fleets.DefaultFleetInflaterParams;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import com.fs.starfarer.api.loading.VariantSource;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.produciton.listeners.AoTDProductionListenerUtils;
import data.kaysaar.aotd.vok.campaign.econ.produciton.manager.AoTDProductionManager;
import data.kaysaar.aotd.vok.campaign.econ.produciton.specs.AoTDProductionSpec;
import data.kaysaar.aotd.vok.campaign.econ.produciton.specs.AoTDProductionSpecManager;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.fs.starfarer.api.util.Misc.random;

public class AoTDProductionOrderData {

    public AoTDProductionSpec.AoTDProductionSpecType getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public AoTDProductionSpec getSpec() {
        return AoTDProductionSpecManager.getSpecsBasedOnType(type).get(id);
    }

    public LinkedHashMap<String, Integer> reqResourcesPerUnit = new LinkedHashMap<>();
    public LinkedHashMap<String, Integer> deliveredResources = new LinkedHashMap<>();

    public AoTDProductionSpec.AoTDProductionSpecType type;
    public String id;

    public int amountToProduce = 1;
    public float daysPerUnit;
    public int unitsRewarded = 0;
    public int activeUnits = 0;
    public float activeDays = 0f;
    public String uniqueId;

    public String getUniqueId() {
        if(!AshMisc.isStringValid(uniqueId)){
            uniqueId = Misc.genUID();
        }
        return uniqueId;
    }

    public boolean hasStartedWorkOnThis = false;
    int moneyPerFinishedUnit;

    public int getMoneyPerFinishedUnit() {
        return moneyPerFinishedUnit;
    }

    public int getMoneyForAllUnits() {
        return amountToProduce * moneyPerFinishedUnit;
    }

    public AoTDProductionOrderData(String id, AoTDProductionSpec spec) {
        this(id, spec, 1);
    }

    public AoTDProductionOrderData(String id, AoTDProductionSpec spec, int amountToProduce) {
        this.id = id;
        if (spec != null) {
            this.type = spec.type;
            this.amountToProduce = Math.max(0, amountToProduce);
            this.daysPerUnit = spec.getDaysToBeCreated();
            this.moneyPerFinishedUnit = spec.getProductionCost();
            this.reqResourcesPerUnit.putAll(spec.getMapOfResourcesNeeded());
        }
    }

    public void setAmount(int amountToProduce) {
        this.amountToProduce = Math.max(0, amountToProduce);
        reconcileStateAfterAmountChange();
    }

    public int getRemainingUnits() {
        return Math.max(0, amountToProduce - unitsRewarded);
    }

    /**
     * Only already rewarded units are fully locked in.
     * Active units may still be canceled by reducing the order amount.
     */
    public int getMinimumLockedAmount() {
        return Math.max(0, unitsRewarded);
    }

    public void reconcileStateAfterAmountChange() {
        int locked = getMinimumLockedAmount();

        if (amountToProduce < locked) {
            amountToProduce = locked;
        }

        if (unitsRewarded > amountToProduce) {
            unitsRewarded = amountToProduce;
        }

        int maxActiveAllowed = Math.max(0, amountToProduce - unitsRewarded);
        if (activeUnits > maxActiveAllowed) {
            activeUnits = maxActiveAllowed;
        }

        if (activeUnits <= 0) {
            activeUnits = 0;
            activeDays = 0f;
        }

        for (Map.Entry<String, Integer> entry : reqResourcesPerUnit.entrySet()) {
            String commodityId = entry.getKey();
            int requiredPerUnit = entry.getValue();
            if (requiredPerUnit <= 0) continue;

            int maxAllowed = requiredPerUnit * amountToProduce;
            int delivered = deliveredResources.getOrDefault(commodityId, 0);

            if (delivered > maxAllowed) {
                deliveredResources.put(commodityId, maxAllowed);
            }
        }

        deliveredResources.entrySet().removeIf(e -> e.getValue() == null || e.getValue() <= 0);
    }

    public int getMaxUnitsFromResources() {
        int maxUnits = Integer.MAX_VALUE;

        for (Map.Entry<String, Integer> entry : reqResourcesPerUnit.entrySet()) {
            String commodityId = entry.getKey();
            int requiredPerUnit = entry.getValue();
            if (requiredPerUnit <= 0) continue;

            int delivered = deliveredResources.getOrDefault(commodityId, 0);
            int unitsFromThisResource = delivered / requiredPerUnit;
            maxUnits = Math.min(maxUnits, unitsFromThisResource);
        }

        if (maxUnits == Integer.MAX_VALUE) {
            return amountToProduce;
        }

        return Math.min(amountToProduce, maxUnits);
    }

    public int getUnitsAvailableToStart() {
        int resourcedUnits = getMaxUnitsFromResources();
        int alreadyCommitted = unitsRewarded + activeUnits;
        return Math.max(0, Math.min(amountToProduce, resourcedUnits) - alreadyCommitted);
    }

    public int takeResources(int available, String commodityId) {
        if (available <= 0) return 0;

        Integer requiredPerUnit = reqResourcesPerUnit.get(commodityId);
        if (requiredPerUnit == null || requiredPerUnit <= 0) return 0;

        int totalRequired = requiredPerUnit * amountToProduce;
        int alreadyDelivered = deliveredResources.getOrDefault(commodityId, 0);
        int remainingNeeded = totalRequired - alreadyDelivered;

        if (remainingNeeded <= 0) return 0;

        int taken = Math.min(available, remainingNeeded);
        deliveredResources.put(commodityId, alreadyDelivered + taken);
        hasStartedWorkOnThis = true;

        return taken;
    }

    public LinkedHashMap<String, Integer> getReqResources() {
        LinkedHashMap<String, Integer> remaining = new LinkedHashMap<>();

        for (Map.Entry<String, Integer> entry : reqResourcesPerUnit.entrySet()) {
            String commodityId = entry.getKey();
            int requiredPerUnit = entry.getValue();

            int totalRequired = requiredPerUnit * amountToProduce;
            int delivered = deliveredResources.getOrDefault(commodityId, 0);
            int left = totalRequired - delivered;

            if (left > 0) {
                remaining.put(commodityId, left);
            }
        }

        return remaining;
    }

    public void tryStartWave() {
        if (activeUnits > 0) return;

        int canStart = getUnitsAvailableToStart();
        if (canStart <= 0) return;

        activeUnits = canStart;
        activeDays = 0f;
        hasStartedWorkOnThis = true;
    }

    public void advanceProduction(float amount) {
        tryStartWave();

        if (activeUnits <= 0) return;

        activeDays += (Global.getSector().getClock().convertToDays(amount)* AoTDProductionManager.getInstance().getSpeedStatForOrder(this).getModifiedValue());

        if (daysPerUnit <= 0f || activeDays >= daysPerUnit) {
            addReward(activeUnits);
            unitsRewarded += activeUnits;
            activeUnits = 0;
            activeDays = 0f;
            tryStartWave();
        }
    }

    public boolean isCompleted() {
        return unitsRewarded >= amountToProduce;
    }

    public float getWaveProgress() {
        if (activeUnits <= 0) return 0f;
        if (daysPerUnit <= 0f) return 1f;
        return Math.min(1f, activeDays / daysPerUnit);
    }

    public void addReward(int amount) {
        if (amount <= 0) return;

        MarketAPI gatheringPoint = Global.getSector().getPlayerFaction().getProduction().getGatheringPoint();
        if (gatheringPoint == null) return;

        CargoAPI local = Misc.getStorageCargo(gatheringPoint);
        MonthlyReport.FDNode node = getMonthlyReportNodeForOrder();
        if(node.custom instanceof Integer inter){
            node.custom = amount+inter;
        }
        else{
            node.custom =(Integer)amount;
        }
        Integer curr = (Integer) node.custom;
        node.name = getSpec().getName()+" x "+curr;
        node.upkeep +=getSpec().getProductionCost()*amount;
        node.icon = Global.getSettings().getSpriteName("income_report", "generic_expense");
        if (type == AoTDProductionSpec.AoTDProductionSpecType.WEAPON) {
            local.addWeapons(id, amount);
            return;
        }

        if (type == AoTDProductionSpec.AoTDProductionSpecType.COMMODITY_ITEM) {
            local.addCommodity(id, amount);
            return;
        }

        if (type == AoTDProductionSpec.AoTDProductionSpecType.SPECIAL_ITEM) {
            local.addSpecial(new SpecialItemData(id, null), amount);
            return;
        }

        if (type == AoTDProductionSpec.AoTDProductionSpecType.FIGHTER) {
            local.addFighters(id, amount);
            return;
        }

        if (type == AoTDProductionSpec.AoTDProductionSpecType.SHIP) {
            float quality = -1f;
            for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
                if (!market.isPlayerOwned()) continue;

                float currQuality = market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).computeEffective(0f);
                currQuality += market.getStats().getDynamic().getMod(Stats.FLEET_QUALITY_MOD).computeEffective(0f);
                quality = Math.max(quality, currQuality);
            }

            quality -= Global.getSector().getFaction(Factions.PLAYER).getDoctrine().getShipQualityContribution();
            quality += 4f * Global.getSettings().getFloat("doctrineFleetQualityPerPoint");

            for (int i = 0; i < amount; i++) {
                CampaignFleetAPI ships = Global.getFactory().createEmptyFleet(Factions.PLAYER, "temp", true);
                ships.setCommander(Global.getSector().getPlayerPerson());
                FleetMemberAPI member =  ships.getFleetData().addFleetMember(AoTDMisc.getVaraint(Global.getSettings().getHullSpec(id)));
                member.getVariant().setSource(VariantSource.REFIT);
                DefaultFleetInflaterParams p = new DefaultFleetInflaterParams();
                p.quality = quality;
                p.mode = FactionAPI.ShipPickMode.PRIORITY_THEN_ALL;
                p.persistent = false;
                p.seed = random.nextLong();
                p.timestamp = null;

                FleetInflater inflater = Misc.getInflater(ships, p);
                ships.setInflater(inflater);
                inflater.inflate(ships);

                ships.setInflated(true);
                ships.setInflater(null);

                for (FleetMemberAPI fleetMemberAPI : ships.getFleetData().getMembersListCopy()) {
                    fleetMemberAPI.getVariant().clear();
                    AoTDProductionListenerUtils.onShipProductionFinished(fleetMemberAPI);
                    local.getMothballedShips().addFleetMember(fleetMemberAPI);
                }

                ships.getFleetData().clear();
                ships.getMembersWithFightersCopy().clear();
                ships.despawn();
            }
        }

    }

    public void consumeSpecialItemsIfNeeded() {
        for (Map.Entry<String, Integer> entry : getReqResources().entrySet()) {
            if (AoTDProductionOrderSnapshot.isItemSpecial(entry.getKey())) {
                int taken = AoTDMisc.eatItems(
                        entry.getKey(),
                        entry.getValue(),
                        Submarkets.SUBMARKET_STORAGE,
                        Misc.getPlayerMarkets(true)
                );
                takeResources(taken, entry.getKey());
            }
        }
    }
    public MonthlyReport.FDNode getMonthlyReportNodeForOrder() {
        MonthlyReport report = SharedData.getData().getCurrentReport();
        MonthlyReport.FDNode marketsNode = report.getNode(MonthlyReport.OUTPOSTS);
        if (marketsNode.name == null) {
            marketsNode.name = "Colonies";
            marketsNode.custom = MonthlyReport.OUTPOSTS;
            marketsNode.tooltipCreator = report.getMonthlyReportTooltip();
        }

        MonthlyReport.FDNode paymentNode = report.getNode(marketsNode, "production");
        paymentNode.name = "Custom Production";
        //paymentNode.upkeep += payment;
        paymentNode.icon = Global.getSettings().getSpriteName("income_report", "production");
        if (paymentNode.tooltipCreator == null) {
            paymentNode.tooltipCreator = new TooltipMakerAPI.TooltipCreator() {
                public boolean isTooltipExpandable(Object tooltipParam) {
                    return false;
                }
                public float getTooltipWidth(Object tooltipParam) {
                    return 450;
                }
                public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                    tooltip.addPara("Monthly expenses due to production orders you have placed.", 0f);
                }
            };
        }
        MonthlyReport.FDNode nodeCommodityEach = report.getNode(paymentNode,getUniqueId());


        return nodeCommodityEach;
    }
}