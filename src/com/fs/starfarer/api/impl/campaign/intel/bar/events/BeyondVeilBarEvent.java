package com.fs.starfarer.api.impl.campaign.intel.bar.events;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class BeyondVeilBarEvent extends BaseBarEventWithPerson {
    public static enum OptionId {
        INIT,
        INTRUIGED,
        CONTINUE_1,
        CONTINUE_2,
        WHERE_WAS_SYSTEM,
        LEAVE,

    }

    public BeyondVeilBarEvent() {
        super();

    }


    public static PlanetAPI getTargetPlanet() {
        return (PlanetAPI) Global.getSector().getPersistentData().get("$aotd_v_planet");
    }

    public boolean shouldShowAtMarket(MarketAPI market) {
        if (!super.shouldShowAtMarket(market)) return false;

        if( Global.getSector().getMemory().is("$aotd_veil_done",true)){
            return false;
        }
        if (market.getFactionId().equals(Factions.LUDDIC_CHURCH) ||
                market.getFactionId().equals(Factions.LUDDIC_PATH)) {
            return false;
        }

        if (getTargetPlanet() == null) return false;

        if (Global.getSector().getIntelManager().hasIntelOfClass(BeyondVeilIntel.class)) {
            return false;
        }
        if (Global.getSector().getMemory().is("$aotd_veil_accepted", true)) {
            return false;
        }
        return true;

    }


    @Override
    protected void regen(MarketAPI market) {
        if (this.market == market) return;
        super.regen(market);

        if (person.getGender() == FullName.Gender.MALE) {
            person.setPortraitSprite(Global.getSettings().getSpriteName("intel", "old_spacer_male"));
        } else {
            person.setPortraitSprite(Global.getSettings().getSpriteName("intel", "old_spacer_female"));
        }


    }

    @Override
    public void addPromptAndOption(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
        super.addPromptAndOption(dialog, memoryMap);

        regen(dialog.getInteractionTarget().getMarket());

        TextPanelAPI text = dialog.getTextPanel();
        text.addPara("A rough-looking veteran spacer with strange cybernetic augmentations seems to stare at you for while " +
                "it's a little unnerving.");

//		Color c = Misc.getHighlightColor();
//		c = Misc.getHighlightedOptionColor();

        dialog.getOptionPanel().addOption("Ask this stranger why is staring at you ", this,
                 Color.ORANGE,null);
    }


    @Override
    public void init(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
        super.init(dialog, memoryMap);

        done = false;

        dialog.getVisualPanel().showPersonInfo(person, true);

        optionSelected(null, OptionId.INIT);
    }

    @Override
    public void optionSelected(String optionText, Object optionData) {
        if (!(optionData instanceof OptionId)) {
            return;
        }
        OptionId option = (OptionId) optionData;

        OptionPanelAPI options = dialog.getOptionPanel();
        TextPanelAPI text = dialog.getTextPanel();
        options.clearOptions();

//		continue
//		> Offer to apologize for the misunderstanding by buying $himOrHer a drink.
//
//		exit
//		> Suggest that they read the manual then leave.

        switch (option) {
            case INIT:
                text.addPara("\"Oh, nothing. Let's just say you caught my attention\"");
                options.addOption("Your attention? What do you mean? ", OptionId.INTRUIGED);
                options.addOption("Suggest " + getHeOrShe() + " mistook you for someone else.", OptionId.LEAVE);
                break;
            case INTRUIGED:
                text.addPara("\"Look, space isn't that big a place you can't hear about " + Global.getSector().getPlayerPerson().getName().getFullName() +
                        "\"\n\" Believe it or not, some people even started calling you the \"Seeker of Knowledge\". Your deeds haven't gone unnoticed.\"");

                options.addOption("What does that mean?", OptionId.CONTINUE_1);
                break;
            case CONTINUE_1:
                text.addPara("\"Plenty of people out there keeping tabs on you, what you've been doing as of late. Some look for signs of danger, threats, that sort of thing, and if there's nothing they just move on; others, " +
                        "like me, look deeper and think you might be the right person for the kind of valuable intel I possess.\"");
                text.addPara(getHeOrShe() + " leans back, a glance cast to their uniform's insignia before continuing");

                options.addOption("Continue", OptionId.CONTINUE_2);
                break;
            case CONTINUE_2:
                text.addPara("Back in the olden days, I was part of an Hegemony Expeditionary Fleet. We had only one task, investigate some strange signals coming from a planet far off the Core Worlds. Simple job. On paper, that is." +
                        "It was supposed to be a simple investigation, but when we got there it turned into wholesale slaughter. An entire fleet of strange ships, the likes of which I'd never seen, appeared out of nowhere and annihilated the entire Expedition. I'm the only one who survived. " +
                        "To this day I wonder if Command actually knew what kind of danger they were sending us into, or they didn't and that's the result. Don't know which one is worse, frankly.");


                options.addOption("\"Where was this planet located?\"", OptionId.WHERE_WAS_SYSTEM);
                break;
            case WHERE_WAS_SYSTEM:
                text.addPara(getHeOrShe() + " slides the tri-pad across the table, a blinking set of coordinates pointing in a direction well outside the Core\nll the data I have is right in here. This is the system where we got ambushed.");
                String icon = Global.getSettings().getSpriteName("intel", "red_planet");
                Set<String> tags = new LinkedHashSet<String>();
                tags.add(Tags.INTEL_MISSIONS);

                dialog.getVisualPanel().showMapMarker(getTargetPlanet().getStarSystem().getCenter(),
                        "Destination: " + getTargetPlanet().getStarSystem().getName(), Misc.getBasePlayerColor(),
                        true, icon, null, tags);

                options.addOption("Leave", OptionId.LEAVE);
                BarEventManager.getInstance().notifyWasInteractedWith(this);
                addIntel();
                Global.getSector().getMemory().set("$aotd_veil_accepted", true);
                break;
            case LEAVE:
                noContinue = true;
                done = true;
                break;
        }
    }


    protected void addIntel() {
        CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
        TextPanelAPI text = dialog.getTextPanel();

        PlanetAPI planet = getTargetPlanet();
        boolean success = false;
        if (planet != null) {
            BeyondVeilIntel intel = new BeyondVeilIntel(planet, this);
            //intel.setImportant(true);
            Global.getSector().getIntelManager().addIntel(intel, false, text);
        }

        if (!success) {
            text.addPara("For a minute there, you were caught by the story, but you now see that following up " +
                    "on it would be a fool's errand.");
        }
    }

    @Override
    protected String getPersonFaction() {
        return Factions.INDEPENDENT;
    }

    @Override
    protected String getPersonRank() {
        return Ranks.SPACE_SAILOR;
    }

    @Override
    protected String getPersonPost() {
        return Ranks.CITIZEN;
    }

    @Override
    protected String getPersonPortrait() {
        return null;
    }

    @Override
    protected FullName.Gender getPersonGender() {
        return FullName.Gender.ANY;
    }


}
