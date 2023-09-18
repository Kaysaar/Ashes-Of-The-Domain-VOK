package com.fs.starfarer.api.impl.campaign.intel.bar.events;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.historian.HistorianBackstory;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.historian.HistorianData;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.combat.entities.terrain.Planet;
import data.plugins.AoDCoreModPlugin;

import java.util.*;

public class PreCollapseFacBarEvent extends BaseBarEvent {

    public static enum OptionId {
        GREETING,
        GREETNG_CONTINUE_1,
        WHAT_DO_YOU_HAVE,
        MORE_INFO,
        PRICE,
        END_CONVERSATION,
        END_CONVERSATION_START,
        LEAVE
    }

    protected long seed;
    protected PlanetAPI targetPlanet;

    protected transient Random random;

    protected MarketAPI market = null;
    protected PersonAPI person = null;

    public PreCollapseFacBarEvent() {
        super();
        seed = Misc.random.nextLong();
        person = Global.getSector().getFaction(Factions.INDEPENDENT).createRandomPerson();
    }

    protected void regen(MarketAPI market) {
        //if (this.market == market) return;
        this.market = market;
        done = false;

        random = new Random(seed + market.getId().hashCode());
//		offers = HistorianData.getInstance().getOffers(random, dialog);
    }

    @Override
    public void addPromptAndOption(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
        super.addPromptAndOption(dialog, memoryMap);

        regen(dialog.getInteractionTarget().getMarket());

        TextPanelAPI text = dialog.getTextPanel();
        text.addPara(getPrompt());

        dialog.getOptionPanel().addOption(getOptionText(), this);
        //dialog.setOptionColor(this, Misc.getStoryOptionColor());

    }

    //TODO Rewrite here dialog as this is from historaion
    protected String getPrompt() {
        return "An old veteran sits alone at a table, scrolling thorugh, what seems to be a very old Tripad, which does not match anything, that has been seen in Sector";
    }

    protected String getOptionText() {
        return "Go over to " + person.getManOrWoman() + " with old Tripad and ask what " + person.getHeOrShe() + " wants";
    }

    @Override
    public void init(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
        super.init(dialog, memoryMap);

        ArrayList<PlanetAPI> preCollapsePlanets = (ArrayList<PlanetAPI>) Global.getSector().getPersistentData().get(AoDCoreModPlugin.preCollapseFacList);
        Collections.shuffle(preCollapsePlanets);
        if (!preCollapsePlanets.isEmpty()) {
            targetPlanet = preCollapsePlanets.get(0);
        }

        dialog.getVisualPanel().showPersonInfo(person, true, true);
        optionSelected(null, OptionId.GREETING);


    }

    @Override
    public boolean shouldShowAtMarket(MarketAPI market) {
        if(!super.shouldShowAtMarket(market))return false;
        ArrayList<PlanetAPI> preCollapsePlanets = (ArrayList<PlanetAPI>) Global.getSector().getPersistentData().get(AoDCoreModPlugin.preCollapseFacList);
        if (Global.getSector().getIntelManager().hasIntelOfClass(PreCollapseFacIntel.class)) {
            return false;
        }
        return !preCollapsePlanets.isEmpty();
    }

    public void optionSelected(String optionText, Object optionData) {
        if (!(optionData instanceof PreCollapseFacBarEvent.OptionId)) {
            return;
        }
        OptionId option = (PreCollapseFacBarEvent.OptionId) optionData;

        OptionPanelAPI options = dialog.getOptionPanel();
        TextPanelAPI text = dialog.getTextPanel();
        options.clearOptions();

        if (option == OptionId.GREETING) {
            text.addPara("Small Introduction");
            options.addOption("Option 1 : Listen", OptionId.GREETNG_CONTINUE_1);
            options.addOption("Option 2 : Ignore", OptionId.END_CONVERSATION_START);
            return;
        }
        if (option == OptionId.GREETNG_CONTINUE_1) {
            text.addPara("Story about failed expedition");
            options.addOption("Your attention? What do you mean? ", OptionId.WHAT_DO_YOU_HAVE);
            return;
        }
        if (option == OptionId.WHAT_DO_YOU_HAVE) {
            text.addPara("Moment where Pre Collapse Fac is mentioned");
            options.addOption("Pre Collapse fac ? ", OptionId.MORE_INFO);
            return;
        }
        if (option == OptionId.MORE_INFO) {
            //Show planet
            text.addPara("More Info about what could be there");
            options.addOption("Where i can find it ? ", OptionId.PRICE);
            return;
        }
        if (option == OptionId.PRICE) {

            text.addPara("Price : Money 100k ");
            options.addOption("Yes", OptionId.END_CONVERSATION);
            options.addOption("No ", OptionId.END_CONVERSATION_START);
            if (Global.getSector().getPlayerFleet().getCargo().getCredits().get() < 100000) {
                options.setEnabled(OptionId.END_CONVERSATION, false);
            }
            else{
                options.setEnabled(OptionId.END_CONVERSATION, true);
            }
            return;
        }
        if (option == OptionId.END_CONVERSATION) {
            text.addPara("Telling location and showing location");
            Global.getSector().getPlayerFleet().getCargo().getCredits().subtract(100000);
            String icon = Global.getSettings().getSpriteName("intel", "red_planet");
            Set<String> tags = new LinkedHashSet<String>();
            tags.add(Tags.INTEL_MISSIONS);

            dialog.getVisualPanel().showMapMarker(targetPlanet.getStarSystem().getCenter(),
                    "Destination: " + targetPlanet.getStarSystem().getName(), Misc.getBasePlayerColor(),
                    true, icon, null, tags);

            options.addOption("Goooood", OptionId.LEAVE);
            ArrayList<PlanetAPI> planets = (ArrayList<PlanetAPI>) Global.getSector().getPersistentData().get(AoDCoreModPlugin.preCollapseFacList);
            planets.remove(targetPlanet);
            addIntel();
            Global.getSector().getPersistentData().put(AoDCoreModPlugin.preCollapseFacList, planets);
            return;

        }
        if (option == OptionId.END_CONVERSATION_START) {
            text.addPara("Text about maybe next time starfarer, a bit of dissapointment");
            options.addOption("Essa", OptionId.LEAVE);
            return;
        }
        if (option == OptionId.LEAVE) {
            noContinue = true;
            done = true;

        }

    }

    protected void addIntel() {

        TextPanelAPI text = dialog.getTextPanel();

        PlanetAPI planet = targetPlanet;
        if (planet != null) {
            PreCollapseFacIntel intel = new PreCollapseFacIntel(planet, this);
            intel.setImportant(true);
            Global.getSector().getIntelManager().addIntel(intel, false, text);
        }


    }

}

