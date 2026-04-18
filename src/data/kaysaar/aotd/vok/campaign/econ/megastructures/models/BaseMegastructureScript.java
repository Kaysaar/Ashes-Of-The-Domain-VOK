package data.kaysaar.aotd.vok.campaign.econ.megastructures.models;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CutStyle;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.MegastructureSpec;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.MegastructureSpecManager;
import data.kaysaar.aotd.vok.timeline.templates.MegastructureClaimEvent;
import data.kaysaar.aotd.vok.timeline.templates.MegastructureRestoredEvent;
import data.scripts.managers.AoTDFactionManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BaseMegastructureScript {
    public String specId;
    MutableStat operationCostMult = new MutableStat(1);
    public SectorEntityToken entityTiedTo;
    public ArrayList<BaseMegastructureSection> megaStructureSections;
    public String uniqueGenId = null;
    public boolean wasClaimed = false;
    public boolean wasFullyRestored =false;
    public String getUniqueGenId() {
        if (uniqueGenId == null) {
            uniqueGenId = Misc.genUID();
        }
        return uniqueGenId;
    }

    public boolean isOwnedByPLayerFaction() {

        return getEntityTiedTo().getFaction().isPlayerFaction();
    }

    public MarketAPI getTiedMarket() {
        return tiedMarket;
    }

    public String getIcon() { //Icon that should be displayed in megastructures TAB
        return Global.getSettings().getSpriteName("megastructures", getSpec().getIconId());
    }

    public String getName() { //Name that will be displayed
        return getSpec().getName();
    }

    public void createCustomEffectsTooltip(TooltipMakerAPI tooltip) {

    }

    public <T extends BaseMegastructureSection> T getSectionById(String sectionId, Class<T> clazz) {
        for (BaseMegastructureSection section : megaStructureSections) {
            if (section.getSpec().getId().equals(sectionId) && clazz.isInstance(section)) {
                return (T) section;
            }
        }
        throw new IllegalArgumentException("Section " + sectionId + " not found or wrong type: " + clazz.getSimpleName());
    }

    public static String memKey = "$aotd_megastructure_";
    //Note : Width will always be 400, height can be customized
    public boolean wasInitalized = false;
    public boolean isPlanetaryMegastructure = false;

    public boolean metCustomCriteria() {
        return true;
    }

    public String getCurrentImage() {
        return Global.getSettings().getSpriteName("megastructureImage", getSpec().getImageForMegastructure());
    }

    public boolean isInRestorationProcess() {
        boolean restoring = false;
        for (BaseMegastructureSection section : getMegaStructureSections()) {
            if (section.isRestoring) {
                restoring = true;
            }
        }
        return restoring;
    }

    public static BaseMegastructureScript getInstanceOfScriptFromEntityIfPresent(SectorEntityToken token, String megaId) {
        return (BaseMegastructureScript) token.getMemoryWithoutUpdate().get(memKey + megaId);
    }
    public static ArrayList<BaseMegastructureScript>getAllMegasFromEntity(SectorEntityToken token) {
        ArrayList<BaseMegastructureScript>scripts = new ArrayList<>();
        for (String key : token.getMemory().getKeys()) {
            if(key.contains(memKey)){
                scripts.add(getInstanceOfScriptFromEntityIfPresent(token, key));
            }
        }
        return scripts;
    }
    public Industry getIndustryTiedToMegastructureIfPresent() {
        return null;
    }

    public MarketAPI tiedMarket;

    public void printCustomCriteria(InteractionDialogAPI dialog) {

    }

    public boolean doesHaveCustomSectionForTooltip() {
        return true;
    }

    public boolean doesHaveCustomEffects() {
        return false;
    }


    public void printCustomSection(TooltipMakerAPI tooltip) {

    }

    public String getIndustryIfIfPresent() {
        return null;
    }

    public void setPlanetaryMegastructure(boolean planetaryMegastructure) {
        isPlanetaryMegastructure = planetaryMegastructure;
    }

    public boolean haveRecivedStoryPoint = false;

    public boolean isFullyRestored() {
        for (BaseMegastructureSection megaStructureSection : megaStructureSections) {
            if (!megaStructureSection.isRestored) return false;
        }
        return true;
    }

    public void createAdditionalInfoForMega(TooltipMakerAPI tooltip) {

    }


    public void createTooltipInfoBeforeClaiming(InteractionDialogAPI dialogAPI) {
        TooltipMakerAPI tooltip = dialogAPI.getTextPanel().beginTooltip();

        tooltip.addSectionHeading("Costs", Alignment.MID, 10f);
        dialogAPI.getTextPanel().addTooltip();
        dialogAPI.getTextPanel().addPara("Monthly upkeep of the %s is estimated to be around %s", Color.ORANGE, this.getName(), Misc.getDGSCredits(getUpkeep()));
        tooltip = dialogAPI.getTextPanel().beginTooltip();
        tooltip.addSectionHeading("Megastructure Information", Alignment.MID, 10f);
        dialogAPI.getTextPanel().addTooltip();
        String sections = "sections";
        String require = "require";
        if (megaStructureSections.size() == 1) {
            sections = "section";
            require = "requires";
        }
        dialogAPI.getTextPanel().addPara("This megastructure has %s distinct " + sections + ", that " + require + " great restoration efforts to bring them back to their former glory.", Color.ORANGE, "" + megaStructureSections.size());


    }

    public float getUpkeep() {
        int total = 0;
        operationCostMult.unmodify();
        for (BaseMegastructureSection megaStructureSection : megaStructureSections) {
            total += (int) megaStructureSection.getUpkeepOfSection();
        }
        return total;
    }

    public MegastructureSpec getSpec() {
        return MegastructureSpecManager.getSpecForMegastructure(specId);
    }

    //Remember to replace it with true init
    public void mockUpInit(String specID) {
        this.specId = specID;
        megaStructureSections = new ArrayList<>();
        for (String sectionId : getSpec().getSectionIds()) {
            BaseMegastructureSection section = MegastructureSpecManager.getSpecForSection(sectionId).getScript();
            section.init(this, false, sectionId);
            megaStructureSections.add(section);
        }
    }

    public void trueInit(String specId, SectorEntityToken entityTiedTo, MarketAPI marketTiedTo) {
        this.specId = specId;
        this.entityTiedTo = entityTiedTo;
        this.tiedMarket = marketTiedTo;
        megaStructureSections = new ArrayList<>();
        getUniqueGenId();
        for (String sectionId : getSpec().getSectionIds()) {
            BaseMegastructureSection section = MegastructureSpecManager.getSpecForSection(sectionId).getScript();
            section.init(this, false, sectionId);
            megaStructureSections.add(section);
        }
        wasInitalized = true;
        entityTiedTo.getMemory().set(memKey + specId, this);

    }

    public boolean isHaveRecivedStoryPoint() {
        return haveRecivedStoryPoint;
    }

    public void setHaveRecivedStoryPoint(boolean haveRecivedStoryPoint) {
        this.haveRecivedStoryPoint = haveRecivedStoryPoint;
    }

    public ArrayList<BaseMegastructureSection> getMegaStructureSections() {
        return megaStructureSections;
    }

    public List<BaseMegastructureSection> getRestoredSections() {
        return megaStructureSections.stream().filter(x -> x.isRestored).toList();
    }

    public BaseMegastructureSection getSectionById(String sectionId) {
        for (BaseMegastructureSection section : megaStructureSections) {
            if (section.getSpec().getId().equals(sectionId)) {
                return section;
            }
        }
        return null;
    }


    public SectorEntityToken getEntityTiedTo() {

        return entityTiedTo;
    }

    public void setEntityTiedTo(SectorEntityToken entityTiedTo) {
        this.entityTiedTo = entityTiedTo;
    }

    public void advance(float amount) {
        for (BaseMegastructureSection megaStructureSection : megaStructureSections) {
            megaStructureSection.advance(amount);
        }
        if (tiedMarket != null && getEntityTiedTo() != null) {
            getEntityTiedTo().setFaction(tiedMarket.getFactionId());
            if (tiedMarket.getFaction().isPlayerFaction()) {
                if (!wasClaimed) {
                    wasClaimed = true;
                    if (Global.getSettings().getModManager().isModEnabled("aotd_sop")&&!getSpec().hasTag("ignore_timeline")&&tiedMarket.getFaction().isPlayerFaction()) {
                        MegastructureClaimEvent event =new MegastructureClaimEvent(this.getSpec().getMegastructureID(), getName(), getCurrentImage());
                        event.createIntelEntryForUnlocking();
                        AoTDFactionManager.getInstance().addEventToTimeline(event);
                    }
                }
            }
        }
        if(!wasFullyRestored){
            boolean restored = true;
            for (BaseMegastructureSection megaStructureSection : megaStructureSections) {
                if(!megaStructureSection.isRestored()){
                    restored = false;
                    break;
                }
            }
            if(restored){
                wasFullyRestored = true;
                if (Global.getSettings().getModManager().isModEnabled("aotd_sop")&&!getSpec().hasTag("ignore_timeline")&&tiedMarket.getFaction().isPlayerFaction()) {
                    MegastructureRestoredEvent event =new MegastructureRestoredEvent(this.getSpec().getMegastructureID(), getName(), getCurrentImage());
                    event.createIntelEntryForUnlocking();
                    AoTDFactionManager.getInstance().addEventToTimeline(event);
                }
            }
        }


    }

    //This is for megastructures that have tied industry
    public void applySupplyToIndustryFirst(Industry ind) {


    }

    public void applySupplyToIndustryLast(Industry ind) {


    }

    public void unapplySupplyToIndustry(Industry ind) {

    }
}
