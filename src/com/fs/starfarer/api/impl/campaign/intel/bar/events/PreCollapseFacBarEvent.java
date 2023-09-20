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

import java.awt.*;
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

    protected String getPrompt() {
        return "A veteran spacer with some ornamental pre-Collapse relics scattered like ancient wards all over their clothes " + person.getHeOrShe() +
                "casually scrolls through an odd looking TriPad that looks more dated than anyone in this bar. Sturdy enough to outlive everyone in the bar as well.";
    }


    protected String getOptionText() {
        return "Sit next to scavenger with an old TriPad and listen to "
                + person.getHisOrHer() + " stories about pre-Collapse relics.";
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
            text.addPara("Anyone who spends enough time in the endless ruins of the Sector periphery would look and sound odd, and this scavenger is no exception." +
                    "You're sure that most of the weird relics "
                    + person.getHeOrShe() + " demonstrated to you are just IFF identification equipment to avoid trouble from automated systems,"+
                    "yet there is something arcane in how they operate and talk about it. After a few minutes of showing you rather useless but rare items,  what you presume was a way for scavenger to establish" + person.getHisOrHer()
                    + " credibility, they offer to tell you about recently failed expedition and, what’s more importantly, its target.");
            options.addOption("Ask  "+ person.getHimOrHer()+" about the lost expedition that was looking for Domain tech.", OptionId.GREETNG_CONTINUE_1);
            options.addOption("Thank  "+ person.getHimOrHer()+" for insights on Domain tech and politely end conversation.", OptionId.END_CONVERSATION_START);
            return;
        }
        if (option == OptionId.GREETNG_CONTINUE_1) {
            text.addPara("\"Robust and experienced people, all veterans.\" " + person.getHeOrShe()
                    + " said with a much more serious expression now. \"Wasn't just some dumb meat from the Core. " +
                    "Fleet went dark after embarking from the last resupply point and we received no signal from them for a few cycles now. "+
                    "The group that sent them decided not to bother with more expeditions.\"");
            options.addOption("Ask "+ person.getHimOrHer()+"about the expedition goal.",OptionId.WHAT_DO_YOU_HAVE);

            return;
        }
        if (option == OptionId.WHAT_DO_YOU_HAVE) {
            text.addPara("\"And as for the expedition target…\" Scavenger just showed you a few orbital scan pictures on" + person.getHisOrHer() +
                    " TriPad. Several Domain structures, in pristine condition. No, not just structures. It's a research facility. Scavenger smiles as "
                    + person.getHeOrShe() + "sees you recognizing the real value of this information. It won’t be cheap for you, that’s for sure.");
            options.addOption("Tell the scavenger that you need to hear a price before making any decisions.", OptionId.PRICE);

            return;
        }
        if (option == OptionId.PRICE) {
            String money = Misc.getDGSCredits(100000);
            text.addPara("With a few taps on TriPad " + person.getHeOrShe() + "just pull up an already filled out contract for %s credits. You just need to confirm it with your own.", Color.ORANGE,""+money);
            options.addOption("Agree to pay the price and get coordinates for the Domain research facility.", OptionId.END_CONVERSATION);
            options.addOption("Decide not to follow footsteps of lost expedition, and decline the offer.", OptionId.END_CONVERSATION_START);

            if (Global.getSector().getPlayerFleet().getCargo().getCredits().get() < 100000) {
                options.setEnabled(OptionId.END_CONVERSATION, false);
            }
            else{
                options.setEnabled(OptionId.END_CONVERSATION, true);
            }
            return;
        }
        if (option == OptionId.END_CONVERSATION) {
            String money = Misc.getDGSCredits(100000);
            text.addPara("Lost "+money,Misc.getNegativeHighlightColor());
            text.addPara("\"Excellent. And good luck, starfarer.\" Your TriPad pings and you see the navigation information on "+targetPlanet.getName()+" in "+targetPlanet.getName()+" system. With that, "
                    + person.getHeOrShe() + " gets up and walks away from a bar without finishing" + person.getHisOrHer() + " drink. "
                    + "In a few seconds, you are left alone with an image of the planet on your TriPad.");
            Global.getSector().getPlayerFleet().getCargo().getCredits().subtract(100000);
            String icon = Global.getSettings().getSpriteName("intel", "red_planet");
            Set<String> tags = new LinkedHashSet<String>();
            tags.add(Tags.INTEL_MISSIONS);

            dialog.getVisualPanel().showMapMarker(targetPlanet.getStarSystem().getCenter(),
                    "Destination: " + targetPlanet.getStarSystem().getName(), Misc.getBasePlayerColor(),
                    true, icon, null, tags);

            options.addOption("Finish your drink and leave with the information on the ancient laboratory that already got one scavenger fleet killed.", OptionId.LEAVE);
            ArrayList<PlanetAPI> planets = (ArrayList<PlanetAPI>) Global.getSector().getPersistentData().get(AoDCoreModPlugin.preCollapseFacList);
            planets.remove(targetPlanet);
            addIntel();
            Global.getSector().getPersistentData().put(AoDCoreModPlugin.preCollapseFacList, planets);
            return;

        }
        if (option == OptionId.END_CONVERSATION_START) {
            text.addPara("\"The choice is yours. Should you change your mind, I will be here, but not for long. "+
                    "A lot of people would like to buy this kind of information, with creds or blood. They just don't know this, yet.\"");
            options.addOption("Take your leave.", OptionId.LEAVE);
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

