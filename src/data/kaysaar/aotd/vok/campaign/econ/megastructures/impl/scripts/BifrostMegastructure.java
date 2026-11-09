package data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.MegastructureSectionSpec;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.MegastructureSpecManager;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.sections.bifrost.BifrostSection;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.ui.bifrost.gatebuilding.BifrostLocationData;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureScript;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureSection;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import data.kaysaar.aotd.tot.ids.AoTDTags;

public class BifrostMegastructure extends BaseMegastructureScript {
    public static final String ISOLATION_REMOVED = "$aotd_bifrost_removed_isolation";

    public void updateIsolation(StarSystemAPI system) {
        if (system == null || !Global.getSettings().getModManager().isModEnabled("aotd_theory_of_toolbox")) return;
        boolean connected = false;
        for (BifrostSection section : getSections()) {
            SectorEntityToken gate = section.getGateTiedTo();
            if (section.getStarSystemAPI() == system && section.isRestored && !section.isDisabled()
                    && gate != null && !gate.isExpired() && gate.getContainingLocation() == system) {
                connected = true;
                break;
            }
        }
        if (connected && system.hasTag(AoTDTags.AOTD_ISOLATED_SYSTEM)) {
            system.getMemoryWithoutUpdate().set(ISOLATION_REMOVED,true);
            system.removeTag(AoTDTags.AOTD_ISOLATED_SYSTEM);
        }
        else if (!connected && system.getMemoryWithoutUpdate().getBoolean(ISOLATION_REMOVED)) {
            system.addTag(AoTDTags.AOTD_ISOLATED_SYSTEM);
            system.getMemoryWithoutUpdate().unset(ISOLATION_REMOVED);
        }
    }

    @Override
    public void trueInit(String specId, SectorEntityToken entityTiedTo, MarketAPI marketTiedTo) {
        this.specId = specId;
        megaStructureSections = new ArrayList<>();
        getUniqueGenId();
        wasInitalized = true;
        wasFullyRestored = true;
    }
    public MegastructureSectionSpec getSectionSpec(){
        return MegastructureSpecManager.getSpecForSection("bifrost_section");
    }
    public LinkedHashMap<String, Integer> getTotalDemandFromActive() {
        LinkedHashMap<String, Integer> am = new LinkedHashMap<>();

        for (BifrostSection activeSection : getActiveSections()) {
            LinkedHashMap<String, Integer> demand = activeSection.getDemandForGateMaintenance();

            for (Map.Entry<String, Integer> entry : demand.entrySet()) {
                am.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }

        return am;
    }
    public boolean areStarSystemsConnected(StarSystemAPI from ,StarSystemAPI to){
        boolean foundFrom = false;
        boolean foundTo = false;
        for (BifrostSection section : getActiveSections()) {
            if(section.getStarSystemAPI().getId().equals(from.getId())){
                foundFrom = true;
            }
            if(section.getStarSystemAPI().getId().equals(to.getId())){
                foundTo = true;
            }
            if(foundFrom && foundTo){
                return true;
            }
        }
        return false;
    }
    public SectorEntityToken getSectionEntityInStarSystem(StarSystemAPI system){
        for (BifrostSection workingSection : getActiveSections()) {
            if(workingSection.getStarSystemAPI().getId().equals(system.getId())){
                return workingSection.getGateTiedTo();
            }
        }
        return null;
    }
    public ArrayList<BifrostSection> getActiveSections() {
        ArrayList<BifrostSection>sections =getSections();
        sections.removeIf(BifrostSection::isDisabled);
        sections.removeIf(x->!x.isRestored || x.getGateTiedTo() == null || x.getGateTiedTo().isExpired());
        return sections;
    }
    public ArrayList<BifrostSection> getBuiltSections() {
        ArrayList<BifrostSection> sections = new ArrayList<>();
        for (BaseMegastructureSection section : getMegaStructureSections()) {
            if(!section.isRestored)continue;
            if (section instanceof BifrostSection) {
                sections.add((BifrostSection) section);
            }
        }
        return sections;
    }
    public ArrayList<BifrostSection> getSections() {
        ArrayList<BifrostSection> sections = new ArrayList<>();
        for (BaseMegastructureSection section : getMegaStructureSections()) {
            if (section instanceof BifrostSection) {
                sections.add((BifrostSection) section);
            }
        }
        return sections;
    }
    public void addNewBifrostGate(StarSystemAPI systemAPI) {
        BifrostSection section = (BifrostSection) getSectionSpec().getScript();
        section.init(this, false,getSectionSpec().getId());
        section.setStarSystemAPI(systemAPI);
        section.startRestoration();

        this.megaStructureSections.add(section);
    }
    public void addNewBifrostGate(StarSystemAPI systemAPI, BifrostLocationData data) {
        BifrostSection section = (BifrostSection) getSectionSpec().getScript();
        section.init(this, false,getSectionSpec().getId());
        section.setStarSystemAPI(systemAPI);
        section.startRestoration();
        section.setData(data);

        this.megaStructureSections.add(section);
    }
    @Override
    public void advance(float amount) {
        for (BaseMegastructureSection megaStructureSection : new ArrayList<>(megaStructureSections)) {
            megaStructureSection.advance(amount);
        }
    }

    public float getTotalAccessibility() {
        float totality = 0f;
        for (BifrostSection section : getActiveSections()) {
                totality += section.getRawBonus();
        }

        return totality;
    }

    public void removeBifrostGate(BifrostSection section) {
        if (section.getGateTiedTo() != null) {
            SectorEntityToken gate = section.getGateTiedTo();
            SectorEntityToken focus = gate.getOrbitFocus();
            if (focus != null && focus.getMarket() != null) focus.getMarket().getConnectedEntities().remove(gate);
            Misc.fadeAndExpire(gate);
        }
        section.setGateTiedTo(null);
        section.aboutToGetRemoved();
        megaStructureSections.remove(section);
        updateIsolation(section.getStarSystemAPI());

    }
    public static SectorEntityToken spawnGate(MarketAPI market) {
        SectorEntityToken primary = market.getPrimaryEntity();
        float orbitRadius = primary.getRadius() + 150.0F;
        SectorEntityToken test = market.getContainingLocation().addCustomEntity((String) null, market.getName() + " Bifrost Gate", "bifrost_gate", market.getFactionId());
        test.setCircularOrbitWithSpin(primary, (float) Math.random() * 360.0F, orbitRadius, orbitRadius / 10.0F, 5.0F, 5.0F);
        market.getConnectedEntities().add(test);
        // test.setMarket(market);
        test.setDiscoverable(false);
        market.addCondition("bifrost_removal");
        test.getMemory().set("$used", false);
        test.getMemory().set("$cooldown", 0f);
        test.getMemory().set("$supplied", true);
        return test;
    }
    public static SectorEntityToken spawnGate(BifrostLocationData data) {
        float orbitRadius = data.radius;
        String name =data.center.getName();
        if(data.center.getMarket()!=null){
            name = data.center.getMarket().getName();
        }
        SectorEntityToken test = data.center.getStarSystem().addCustomEntity((String) null,  name+ " Bifrost Gate", "bifrost_gate", Factions.PLAYER);
        if(!data.center.getContainingLocation().getAllEntities().contains(test)){
            data.center.getContainingLocation().addEntity(test);
        }
        test.setCircularOrbitWithSpin(data.center, data.angle, orbitRadius, orbitRadius / 10.0F, 5.0F, 5.0F);
        test.setLocation(data.locationOfGate.x,data.locationOfGate.y);

        MarketAPI market = data.center.getMarket();
        if(market!=null){
            market.getConnectedEntities().add(test);
            // test.setMarket(market);

        }

        test.setDiscoverable(false);

        test.getMemory().set("$used", false);
        test.getMemory().set("$cooldown", 0f);
        test.getMemory().set("$supplied", true);

        return test;
    }
}
