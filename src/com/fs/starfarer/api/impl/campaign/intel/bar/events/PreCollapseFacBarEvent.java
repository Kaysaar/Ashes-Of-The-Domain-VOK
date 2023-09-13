package com.fs.starfarer.api.impl.campaign.intel.bar.events;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.historian.HistorianBackstory;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.historian.HistorianData;
import com.fs.starfarer.api.util.Misc;

import java.util.Map;
import java.util.Random;

public class PreCollapseFacBarEvent extends BaseBarEvent {

    public static enum OptionId {
        GREETING,
        GREETNG_CONTINUE_1,
        WHAT_DO_YOU_HAVE,
        MORE_INFO,
        PRICE,
        END_CONVERSATION,
    }

    protected long seed;
    protected transient Random random;
    protected transient HistorianBackstory.HistorianBackstoryInfo backstory = null;
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
        return "Go over to "+person.getManOrWoman()+" with old Tripad and ask what "+person.getHeOrShe()+" wants";
    }

    @Override
    public void init(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
        super.init(dialog, memoryMap);


        dialog.getVisualPanel().showPersonInfo(person, true, true);
        optionSelected(null, OptionId.GREETING);


    }

    public void optionSelected(String optionText, Object optionData) {
//        if (optionData == OptionId.GREETING) {
//            text.addPara("\"Ain't nothing, I'm just reading my mail,\" " + getHeOrShe() +
//                    " growls back. Then laughs, and taps " + getHisOrHer() + " temple. " +
//                    "\"Swear I'll never get proper used to these things.\"");
//            options.addOption("Your attention? What do you mean? ", BeyondVeilBarEvent.OptionId.INTRUIGED);
//            options.addOption("Suggest " + getHeOrShe() + " mistook you for someone else.", BeyondVeilBarEvent.OptionId.LEAVE);
//        }
//        if (optionData == OptionId.GREETNG_CONTINUE_1) {
//            text.addPara("\"Oh, nothing. Let's just say you caught my attention\"");
//            options.addOption("Your attention? What do you mean? ", BeyondVeilBarEvent.OptionId.INTRUIGED);
//            options.addOption("Suggest " + getHeOrShe() + " mistook you for someone else.", BeyondVeilBarEvent.OptionId.LEAVE);
//        }
//        if (optionData == OptionId.WHAT_DO_YOU_HAVE) {
//            text.addPara("\"Oh, nothing. Let's just say you caught my attention\"");
//            options.addOption("Your attention? What do you mean? ", BeyondVeilBarEvent.OptionId.INTRUIGED);
//            options.addOption("Suggest " + getHeOrShe() + " mistook you for someone else.", BeyondVeilBarEvent.OptionId.LEAVE);
//        }
//        if (optionData == OptionId.MORE_INFO) {
//            text.addPara("\"Oh, nothing. Let's just say you caught my attention\"");
//            options.addOption("Your attention? What do you mean? ", BeyondVeilBarEvent.OptionId.INTRUIGED);
//            options.addOption("Suggest " + getHeOrShe() + " mistook you for someone else.", BeyondVeilBarEvent.OptionId.LEAVE);
//        }
//        if (optionData == OptionId.PRICE) {
//            text.addPara("\"Oh, nothing. Let's just say you caught my attention\"");
//            options.addOption("Your attention? What do you mean? ", BeyondVeilBarEvent.OptionId.INTRUIGED);
//            options.addOption("Suggest " + getHeOrShe() + " mistook you for someone else.", BeyondVeilBarEvent.OptionId.LEAVE);
//        }
//        if (optionData == OptionId.END_CONVERSATION) {
//            text.addPara("\"Oh, nothing. Let's just say you caught my attention\"");
//            options.addOption("Your attention? What do you mean? ", BeyondVeilBarEvent.OptionId.INTRUIGED);
//            options.addOption("Suggest " + getHeOrShe() + " mistook you for someone else.", BeyondVeilBarEvent.OptionId.LEAVE);
//        }

    }

}
