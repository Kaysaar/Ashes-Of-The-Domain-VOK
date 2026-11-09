package data.kaysaar.aotd.vok.campaign.econ.stablestructures.impl.view;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.tot.ids.AoTDTags;
import data.kaysaar.aotd.tot.stablestructures.AoTDStableStructureConstruction;
import data.kaysaar.aotd.tot.stablestructures.AoTDStableStructureImpl;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureScript;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureSection;

import java.awt.Color;

public abstract class BaseMegastructureSystemStructureView extends AoTDStableStructureImpl {

    protected abstract String getMegastructureId();

    protected abstract SectorEntityToken getMegastructureOwnerEntity();

    public BaseMegastructureScript getMegastructureScript() {
        SectorEntityToken entity = getMegastructureOwnerEntity();
        if (entity == null) return null;
        return BaseMegastructureScript.getInstanceOfScriptFromEntityIfPresent(entity, getMegastructureId());
    }

    @Override
    public String getSystemStructureCapacityModifierName() {
        return getName();
    }

    @Override
    public boolean isAvailableToBuild(StarSystemAPI systemAPI, FactionAPI factionAttemptingToBuildIt) {
        return false;
    }

    @Override
    public boolean showWhenUnavailable(StarSystemAPI systemAPI, FactionAPI factionAttemptingToBuildIt) {
        return false;
    }

    @Override
    public boolean canBeShutDown() {
        return false;
    }

    @Override
    public boolean canMountAICores() {
        return false;
    }

    @Override
    public boolean canMountItems() {
        return false;
    }

    @Override
    public int getSystemStructureCapacityBonus() {
        // ToT already counts objective-tagged entities before a view is attached.
        // Compensate only entities that become a system structure because of
        // this visual wrapper, preserving the number of available build slots.
        SectorEntityToken token = currentlyAssignedToken;
        if (token == null || token.hasTag(AoTDTags.AOTD_OBJECTIVE) || token.hasTag(Tags.OBJECTIVE)
                || AoTDStableStructureConstruction.SCAFFOLD.equals(token.getCustomEntityType())) return 0;
        return 1;
    }

    @Override
    public void advance(float amount) {
        // Visual proxy only; the megastructure script performs all real logic.
    }

    @Override
    public void apply() {
    }

    @Override
    public void unapply() {
    }

    @Override
    public String getName() {
        BaseMegastructureScript script = getMegastructureScript();
        if (script != null) return script.getName();
        return getSpecEntity() == null ? getMegastructureId() : getSpecEntity().getDefaultName();
    }

    @Override
    public String getImageName() {
        BaseMegastructureScript script = getMegastructureScript();
        if (script != null) return script.getCurrentImage();
        return getSpecEntity() == null ? null : getSpecEntity().getInteractionImage();
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, AoTDStableStructureTooltipMode mode) {
        StarSystemAPI system = currentlyAssignedToken == null ? null : currentlyAssignedToken.getStarSystem();
        createTooltip(tooltip, mode, system, Global.getSector().getPlayerFaction());
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, AoTDStableStructureTooltipMode mode,
                              StarSystemAPI system, FactionAPI factionAttemptingToBuildIt) {
        BaseMegastructureScript script = getMegastructureScript();
        tooltip.addTitle(getName());

        if (script == null) {
            tooltip.addPara("This entry represents an existing Domain-era megastructure and cannot be constructed or dismantled here.",
                    Misc.getGrayColor(), 5f);
            return;
        }

        if (script.getSpec() != null && script.getSpec().getDescription() != null) {
            tooltip.addPara(script.getSpec().getDescription(), 10f);
        }

        Color base = currentlyAssignedToken != null && currentlyAssignedToken.getFaction() != null
                ? currentlyAssignedToken.getFaction().getBaseUIColor() : Misc.getBasePlayerColor();
        Color dark = currentlyAssignedToken != null && currentlyAssignedToken.getFaction() != null
                ? currentlyAssignedToken.getFaction().getDarkUIColor() : Misc.getDarkPlayerColor();
        tooltip.addSectionHeading("Megastructure Status", base, dark, Alignment.MID, 10f);

        int restored = 0;
        int total = 0;
        if (script.getMegaStructureSections() != null) {
            for (BaseMegastructureSection section : script.getMegaStructureSections()) {
                if (section == null) continue;
                total++;
                if (section.isRestored()) restored++;
            }
        }

        tooltip.addPara("Restored sections: %s / %s", 5f,
                restored == total && total > 0 ? Misc.getPositiveHighlightColor() : Misc.getHighlightColor(),
                String.valueOf(restored), String.valueOf(total));

        if (script.getMegaStructureSections() != null) {
            for (BaseMegastructureSection section : script.getMegaStructureSections()) {
                if (section == null) continue;
                tooltip.addPara("%s: %s", 3f,
                        section.isRestored() ? Misc.getPositiveHighlightColor() : Misc.getNegativeHighlightColor(),
                        section.getName(), section.isRestored() ? "Restored" : "Damaged");
            }
        }

        if (script.doesHaveCustomSectionForTooltip()) {
            script.printCustomSection(tooltip);
        }
    }
}
