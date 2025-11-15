package data.kaysaar.aotd.vok.ui.scientist;

import ashlib.data.plugins.ui.models.BasePopUpDialog;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.scientist.models.ScientistPerson;
import data.kaysaar.aotd.vok.ui.research.HeadOfResearchShowcase;
import data.kaysaar.aotd.vok.ui.research.ScientistButtonComponent;

import java.util.ArrayList;

public class HeadOfResearchDialog extends BasePopUpDialog {
    public HeadOfResearchDialog(String headerTitle,HeadOfResearchShowcase headOfResearchShowcase,ScientistButtonComponent component) {
        super(headerTitle);
        this.showcase = headOfResearchShowcase;
        this.buttonComponent = component;
    }
    ArrayList<ScientistButton>buttons = new ArrayList<>();
    ScientistButton currButton = null;
    HeadOfResearchShowcase showcase;
    ScientistButtonComponent buttonComponent;
    @Override
    public void createContentForDialog(TooltipMakerAPI tooltip, float width) {
        ArrayList<ScientistPerson>personArrayList =  AoTDMainResearchManager.getInstance().getManagerForPlayer().researchCouncil;
        personArrayList.sort((a, b) -> {
            boolean aIsHead = AoTDMainResearchManager.getInstance().getManagerForPlayer().isHeadOfResearch(a);
            boolean bIsHead = AoTDMainResearchManager.getInstance().getManagerForPlayer().isHeadOfResearch(b);
            return Boolean.compare(bIsHead, aIsHead); // head (true) comes first
        });

        for (ScientistPerson scientistPerson :personArrayList) {
            buttons.add(new ScientistButton(scientistPerson,width-10));
        }
        buttons.forEach(button -> {tooltip.addCustom(button.getMainPanel(),5f);});


    }

    @Override
    public void applyConfirmScript() {
        if(currButton!=null){
            AoTDMainResearchManager.getInstance().getManagerForPlayer().currentHeadOfCouncil = currButton.person;
            showcase.createUI();
            buttonComponent.clearUI();
            AoTDMainResearchManager.getInstance().getManagerForPlayer().executeResearchCouncilAdvance(0f);
        }

        super.applyConfirmScript();
    }

    @Override
    public void onExit() {
        buttons.clear();
        super.onExit();
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        for (ScientistButton button : buttons) {
            button.button.unhighlight();
            if(button.button.isChecked()){
                button.button.setChecked(false);
                currButton = button;
                break;
            }
        }
        if(currButton!=null){
            currButton.button.highlight();
        }
    }
}
