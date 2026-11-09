package data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import data.kaysaar.aotd.tot.stablestructures.AoTDStableStructureConstruction;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.aotd_entities.BiFrostGateEntity;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.tot.stablestructures.AoTDStableStructureImpl;
import data.kaysaar.aotd.tot.stablestructures.AoTDStableStructureSpecManager;
import data.kaysaar.aotd.tot.stablestructures.AoTDStableStructureConstructionContract;
import data.kaysaar.aotd.tot.scripts.trade.contracts.AoTDTradeContractManager;
import data.kaysaar.aotd.tot.ui.starsystems.stuctures.map.StructureLocationPicker;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.MegastructureStatManager;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.sections.bifrost.BifrostSection;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.ui.bifrost.gatebuilding.ChooseOrbitForBifrost;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.tradecontracts.BaseRestorationContract;

import java.util.LinkedHashMap;
import org.lwjgl.util.vector.Vector2f;

public class BifrostSystemStructure extends AoTDStableStructureImpl {
    public static void restoreExistingGates() {
        BifrostMegastructure mega = BifrostMegastructureManager.getInstance().getMegastructure();
        for (StarSystemAPI system : Global.getSector().getStarSystems()) {
            for (SectorEntityToken token : system.getAllEntities()) {
                Object value = token.getMemoryWithoutUpdate().get(AoTDStableStructureSpecManager.memKeyInEntities);
                if (value instanceof BifrostSystemStructure site && site.isBuilding()) site.addConstructionToNetwork();
            }
        }
        for (BifrostSection existing : mega.getBuiltSections()) {
            attachExistingGate(existing.getGateTiedTo());
            mega.updateIsolation(existing.getStarSystemAPI());
        }
    }

    @Override
    public boolean showWhenUnavailable(StarSystemAPI systemAPI, FactionAPI factionAttemptingToBuildIt) {
        return false;
    }

    public static void attachExistingGate(SectorEntityToken gate) {
        if (gate == null || gate.isExpired()) return;
        if (!gate.getMemoryWithoutUpdate().contains(AoTDStableStructureSpecManager.memKeyInEntities)) {
            BifrostSystemStructure impl = new BifrostSystemStructure();
            impl.init("bifrost_gate");
            impl.setCurrentlyAssignedToken(gate);
            gate.getMemoryWithoutUpdate().set(AoTDStableStructureConstruction.USES_STABLE_LOCATION,false);
            AoTDStableStructureConstruction.requestUIRefresh(gate.getStarSystem());
        }
    }


    @Override
    public StructureLocationPicker createLocationPicker(float width, float height, StarSystemAPI system) {
        return new BifrostLocationPicker(width,height,system);
    }

    @Override
    public boolean isAvailableToBuild(StarSystemAPI system, FactionAPI faction) {
        return getUnavailableReason(system,faction) == null;
    }

    @Override
    public String getUnavailableReason(StarSystemAPI system, FactionAPI faction) {
        if (system == null || faction == null || !faction.isPlayerFaction()) return "Requires a player-controlled star system.";
        if (!BiFrostGateEntity.canUseBifrostGates()) return "Requires Bifrost Gate research and a Bifrost Control Center.";
        boolean hasColony = false;
        for (MarketAPI market : Misc.getPlayerMarkets(true)) {
            if (market.getStarSystem() == system) hasColony = true;
        }
        if (!hasColony) return "Requires a player colony in this star system.";
        for (BifrostSection section : BifrostMegastructureManager.getInstance().getMegastructure().getSections()) {
            if (section.getStarSystemAPI() == system) return "This system already has a Bifrost Gate or one under construction.";
        }
        for (SectorEntityToken token : system.getAllEntities()) {
            if (token == currentlyAssignedToken || token.isExpired()) continue;
            Object impl = token.getMemoryWithoutUpdate().get(AoTDStableStructureSpecManager.memKeyInEntities);
            if (token.hasTag("bifrost") || impl instanceof BifrostSystemStructure) {
                return "This system already has a Bifrost Gate or one under construction.";
            }
        }
        return null;
    }

    @Override
    public LinkedHashMap<String, Integer> getBuildCostResources() {
        LinkedHashMap<String,Integer> resources = new LinkedHashMap<>();
        float mult = MegastructureStatManager.getInstance().getMegastructureResourceCostMult().getModifiedValue();
        getSpec().getTotalResourcesNeeded().forEach((key,value)->resources.put(key,Math.round(value*mult)));
        return resources;
    }

    @Override
    public boolean startBuilding(String target) {
        SectorEntityToken focus = currentlyAssignedToken.getOrbitFocus();
        if (!(focus instanceof PlanetAPI planet)) return false;
        float radius = currentlyAssignedToken.getCircularOrbitRadius();
        float min = planet.getRadius() * (planet.isStar() || planet.isBlackHole() ? 2f : 1.5f);
        float max = planet.isStar() || planet.isBlackHole() ? 25000f : planet.getRadius() * 3f;
        if (radius < min - 0.1f || radius > max + 0.1f) return false;
        if (!super.startBuilding(target)) return false;
        AoTDTradeContractManager.getInstance().removeContract(getUniqueIdForContract());
        AoTDTradeContractManager.getInstance().addContract(new AoTDStableStructureConstructionContract(this) {
            @Override
            public float getCut(String commodityId) {
                return BaseRestorationContract.MARGIN_OF_COMMODITY_PRICE;
            }

            @Override
            public boolean isContractFrozen() {
                return !BiFrostGateEntity.canUseBifrostGates() || super.isContractFrozen();
            }
        });
        addConstructionToNetwork();
        return true;
    }

    public BifrostSection getNetworkSection() {
        for (BifrostSection section : BifrostMegastructureManager.getInstance().getMegastructure().getSections()) {
            if (section.constructionSite == this || section.getGateTiedTo() == currentlyAssignedToken) return section;
        }
        return null;
    }

    public void addConstructionToNetwork() {
        if (getNetworkSection() != null) return;
        BifrostMegastructure mega = BifrostMegastructureManager.getInstance().getMegastructure();
        BifrostSection section = (BifrostSection) mega.getSectionSpec().getScript();
        section.init(mega,false,mega.getSectionSpec().getId());
        section.setStarSystemAPI(currentlyAssignedToken.getStarSystem());
        section.constructionSite = this;
        section.isRestoring = true;
        mega.getMegaStructureSections().add(section);
    }

    @Override
    public void cancelBuilding() {
        BifrostSection section = getNetworkSection();
        if (section != null) BifrostMegastructureManager.getInstance().getMegastructure().removeBifrostGate(section);
        super.cancelBuilding();
    }

    @Override
    public void advance(float amount) {
        if (isBuilding() && !BiFrostGateEntity.canUseBifrostGates()) return;
        super.advance(amount);
    }

    @Override
    public boolean canBeDisrupted() {
        return false;
    }

    @Override
    protected void buildingFinished() {
        SectorEntityToken gate = currentlyAssignedToken;
        SectorEntityToken focus = gate.getOrbitFocus();
        float radius = gate.getCircularOrbitRadius();
        gate.setCircularOrbitWithSpin(focus,gate.getCircularOrbitAngle(),radius,radius/10f,5f,5f);
        gate.setName(focus.getName()+" Bifrost Gate");
        gate.setDiscoverable(false);
        gate.getMemoryWithoutUpdate().set("$used",false);
        gate.getMemoryWithoutUpdate().set("$cooldown",0f);
        gate.getMemoryWithoutUpdate().set("$supplied",true);
        if (focus.getMarket() != null && !focus.getMarket().getConnectedEntities().contains(gate)) {
            focus.getMarket().getConnectedEntities().add(gate);
        }
        BifrostMegastructure mega = BifrostMegastructureManager.getInstance().getMegastructure();
        BifrostSection section = getNetworkSection();
        if (section == null) {
            section = (BifrostSection) mega.getSectionSpec().getScript();
            mega.getMegaStructureSections().add(section);
        }
        section.init(mega,true,mega.getSectionSpec().getId());
        section.constructionSite = null;
        section.isRestoring = false;
        section.setStarSystemAPI(gate.getStarSystem());
        section.setGateTiedTo(gate);
        mega.updateIsolation(gate.getStarSystem());
    }

    @Override
    public void notifyRemoved() {
        BifrostMegastructure mega = BifrostMegastructureManager.getInstance().getMegastructure();
        for (BifrostSection section : mega.getSections()) {
            if ((section.getGateTiedTo() == currentlyAssignedToken || section.constructionSite == this)) mega.removeBifrostGate(section);
        }
        SectorEntityToken focus = currentlyAssignedToken.getOrbitFocus();
        if (focus != null && focus.getMarket() != null) focus.getMarket().getConnectedEntities().remove(currentlyAssignedToken);
        super.notifyRemoved();
    }

    @Override
    public void createTooltipForEffectSection(TooltipMakerAPI tooltip, AoTDStableStructureTooltipMode mode) {
        tooltip.addPara("Connects this system to the Bifrost network, allowing travel between gates and contributing to the empire-wide accessibility bonus.",3f);
        tooltip.addPara("Connects %s to the rest of the Sector, allowing normal imports and exports. Removing or deactivating the gate restores their isolation.",3f,Misc.getPositiveHighlightColor(),"isolated systems");
    }
    public static class BifrostLocationPicker extends ChooseOrbitForBifrost implements StructureLocationPicker {
        SectorEntityToken lastFocus;
        boolean lastSelection;
        int lastRadius;

        public BifrostLocationPicker(float width, float height, StarSystemAPI system) {
            super(width,height,system);
        }

    @Override
    public boolean hasSelection() {
        return getOrbitComponent() != null && getOrbitComponent().isLocked()
                && getOrbitComponent().getCurrentlyCenteredAround() != null
                && !getOrbitComponent().getCurrentlyCenteredAround().getToken().isExpired()
                && getOrbitComponent().getSavedRadiusWorld() != null && getOrbitComponent().getSavedGateAngleRad() != null;
    }

    @Override
    public SectorEntityToken getSelectedEntity() {
        return hasSelection() ? getOrbitComponent().getCurrentlyCenteredAround().getToken() : null;
    }

    @Override
    public Vector2f getSelectedLocation() {
        if (!hasSelection()) return null;
        Vector2f center = getSelectedEntity().getLocation();
        float radius = getOrbitComponent().getSavedRadiusWorld();
        float angle = getOrbitComponent().getSavedGateAngleRad();
        return new Vector2f(center.x + (float) Math.cos(angle) * radius, center.y + (float) Math.sin(angle) * radius);
    }

    @Override
    public boolean consumeSelectionTextUpdate() {
        SectorEntityToken focus = getSelectedEntity();
        boolean selected = hasSelection();
        int radius = selected ? Math.round(getOrbitComponent().getSavedRadiusWorld()) : 0;
        boolean changed = focus != lastFocus || selected != lastSelection || radius != lastRadius;
        lastFocus = focus;
        lastSelection = selected;
        lastRadius = radius;
        return changed;
    }

    @Override
    public void createSelectionText(TooltipMakerAPI tooltip) {
        if (!hasSelection()) {
            tooltip.addPara("Select a planet or star, then click to place the Bifrost Gate. Press %s to clear.",0f,Misc.getHighlightColor(),"D");
        }
        else {
            tooltip.addPara("Orbit focus: %s. Radius: %s.",0f,Misc.getHighlightColor(),getSelectedEntity().getName(),""+Math.round(getOrbitComponent().getSavedRadiusWorld()));
        }
    }

    }
}
