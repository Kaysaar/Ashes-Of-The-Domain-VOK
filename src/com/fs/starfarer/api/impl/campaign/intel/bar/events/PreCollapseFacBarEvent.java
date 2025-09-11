package com.fs.starfarer.api.impl.campaign.intel.bar.events;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.PCFPlanetIntel;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDMemFlags;

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
        return "A veteran spacer with some ornamental pre-Collapse relics scattered like ancient wards all over their clothes sits at a table. " + person.getHeOrShe() +
                "is casually scrolling through an odd looking TriPad that looks more dated than anything else in this bar and its construction is sturdy enough to outlive everyone in the bar as well.";
    }


    protected String getOptionText() {
        return "Sit next to scavenger with an old TriPad and listen to "
                + person.getHisOrHer() + " stories about pre-Collapse relics.";
    }

    @Override
    public void init(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
        super.init(dialog, memoryMap);

        ArrayList<PlanetAPI> preCollapsePlanets = (ArrayList<PlanetAPI>) Global.getSector().getPersistentData().get(AoTDMemFlags.preCollapseFacList);
        Collections.shuffle(preCollapsePlanets);
        for (PlanetAPI preCollapsePlanet : preCollapsePlanets) {
            if(!preCollapsePlanet.getMarket().getSurveyLevel().equals(MarketAPI.SurveyLevel.FULL)){
                targetPlanet = preCollapsePlanet;
                break;
            }

        }

        dialog.getVisualPanel().showPersonInfo(person, true, true);
        optionSelected(null, OptionId.GREETING);


    }

    @Override
    public boolean shouldShowAtMarket(MarketAPI market) {
        if(!super.shouldShowAtMarket(market)) return false;
        if(market.getMemory().is("$aotd_pre_collapse",true))return false;
        ArrayList<PlanetAPI> preCollapsePlanets = (ArrayList<PlanetAPI>) Global.getSector().getPersistentData().get(AoTDMemFlags.preCollapseFacList);
        ArrayList<PlanetAPI>validPlanets = new ArrayList<>();
        if(preCollapsePlanets!=null){
            for (PlanetAPI preCollapsePlanet : preCollapsePlanets) {
                if(PCFPlanetIntel.doesContainPlanet(preCollapsePlanet))continue;
                if(!preCollapsePlanet.getMarket().getSurveyLevel().equals(MarketAPI.SurveyLevel.FULL)){
                    validPlanets.add(preCollapsePlanet);
                }
            }
        }

        return !validPlanets.isEmpty();
    }

    public void optionSelected(String optionText, Object optionData) {
        if (!(optionData instanceof OptionId)) {
            return;
        }
        OptionId option = (OptionId) optionData;

        OptionPanelAPI options = dialog.getOptionPanel();
        TextPanelAPI text = dialog.getTextPanel();
        options.clearOptions();

        if (option == OptionId.GREETING) {
            text.addPara("Anyone who spends enough time in the endless ruins of Persean Sector's periphery would look and sound odd, and this scavenger is no exception." +
                    "You're sure that most of the weird relics "
                    + person.getHeOrShe() + " has demonstrated to you are just IFF identification equipment that was used to avoid trouble from automated systems,"+
                    "yet there is something arcane in how they operate and talk about them. After a few minutes of showing you rather useless but rare items, what you presume was a way for scavenger to establish" + person.getHisOrHer()
                    + " credibility, they offer to tell you about one of recently failed expeditions and what's more important, its target.");
            options.addOption("Ask  "+ person.getHimOrHer()+" about the lost expedition that was looking for Pre-Collapse Domain technology.", OptionId.GREETNG_CONTINUE_1);
            options.addOption("Thank  "+ person.getHimOrHer()+" for the insights on Domain technology and politely end conversation.", OptionId.END_CONVERSATION_START);
            return;
        }
        if (option == OptionId.GREETNG_CONTINUE_1) {
            text.addPara("\"Robust and experienced people, all veterans.\" " + person.getHeOrShe()
                    + " said with a much more serious expression on their face. \"They weren't just some dumb meat from the Core. " +
                    "Fleet went dark after embarking from the last resupply point and we haven't received any signs of life from them for few cycles now. "+
                    "The group that sent them decided not to bother with any more expeditions.\"");
            options.addOption("Ask "+ person.getHimOrHer()+"about the expedition's goal.",OptionId.WHAT_DO_YOU_HAVE);

            return;
        }
        if (option == OptionId.WHAT_DO_YOU_HAVE) {
            text.addPara("\"And as for the expedition's target…\" Scavenger simply shown you few orbital scans on" + person.getHisOrHer() +
                    " TriPad. Several Domain structures, all in pristine condition. On closer look no, it's not just structures. It's a whole intact research facility. Scavenger smiles as "
                    + person.getHeOrShe() + "sees you recognize the real value of this intel. It won't be cheap, that's for sure.");
            options.addOption("Tell the scavenger that you need to hear the price before making any decisions.", OptionId.PRICE);

            return;
        }
        if (option == OptionId.PRICE) {
            String money = Misc.getDGSCredits(100000);
            text.addPara("With a few taps on TriPad " + person.getHeOrShe() + " pulls up already filled out contract for %s credits. \"You just need to confirm it with your own signature.\"", Color.ORANGE,""+money);
            options.addOption("Agree to pay the price and get the coordinates for the Domain research facility.", OptionId.END_CONVERSATION);
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
            text.addPara("\"Excellent. And good luck starfarer, you will need it.\" Your TriPad pings and you see the navigation information leading to "+targetPlanet.getName()+" in "+targetPlanet.getName()+" system. With that, "
                    + person.getHeOrShe() + " gets up and walks out from the bar without finishing" + person.getHisOrHer() + " drink. "
                    + "Soon enough, you are left alone with an image of the planet on your TriPad.");
            Global.getSector().getPlayerFleet().getCargo().getCredits().subtract(100000);
            String icon = Global.getSettings().getSpriteName("intel", "red_planet");
            Set<String> tags = new LinkedHashSet<String>();
            tags.add(Tags.INTEL_MISSIONS);

            dialog.getVisualPanel().showMapMarker(targetPlanet.getStarSystem().getCenter(),
                    "Destination: " + targetPlanet.getStarSystem().getName(), Misc.getBasePlayerColor(),
                    true, icon, null, tags);

            options.addOption("Finish your drink and leave with the information on the ancient research facility that already got one scavenger fleet killed.", OptionId.LEAVE);
            ArrayList<PlanetAPI> planets = (ArrayList<PlanetAPI>) Global.getSector().getPersistentData().get(AoTDMemFlags.preCollapseFacList);
            int index =0;
            for (PlanetAPI planet : planets) {
                if(planet.getId().equals(targetPlanet.getId())){
                    planets.remove(index);
                    break;

                }
                index++;
            }
            addIntel();
            market.getMemory().set("$aotd_pre_collapse",true,200);
            Global.getSector().getPersistentData().put(AoTDMemFlags.preCollapseFacList, planets);
            return;

        }
        if (option == OptionId.END_CONVERSATION_START) {
            text.addPara("\"The choice is yours. Should you change your mind, I will be here, but not for too long. "+
                    "Many people would like to buy this kind of information, with creeds or blood. They simply don't know this yet...\"");
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
        if (planet != null&&!PCFPlanetIntel.doesContainPlanet(planet)) {
            PCFPlanetIntel intel = new PCFPlanetIntel(planet);
            Global.getSector().getIntelManager().addIntel(intel, false, text);
        }



    }

}

