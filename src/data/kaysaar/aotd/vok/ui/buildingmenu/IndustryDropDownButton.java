package data.kaysaar.aotd.vok.ui.buildingmenu;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.loading.IndustrySpecAPI;

import java.util.ArrayList;

public class IndustryDropDownButton extends DropDownButton{
    IndustrySpecAPI mainSpec;
    ArrayList<IndustrySpecAPI>subSpecs;
    MarketAPI market;
    public IndustryDropDownButton(UITableImpl tableOfReference, float width, float height, float maxWidth, float maxHeight, IndustrySpecAPI mainspec, ArrayList<IndustrySpecAPI> subSpecs, MarketAPI market) {
        super(tableOfReference, width, height, maxWidth, maxHeight);
        this.mainSpec = mainspec;
        this.subSpecs  = subSpecs;
        this.market = market;
        createUI();
    }

    @Override
    public void createUIContent() {
        if (buttons == null) {
            buttons = new ArrayList<>();
            for (IndustrySpecAPI subSpec : subSpecs) {
                IndustryButton button = new IndustryButton(width-30,height,subSpec,10f,market);
                button.initializeUI();
                buttons.add(button);
            }
            mainButton = new IndustryButton(width-20,height,mainSpec,0f,market);
            mainButton.initializeUI();
        }
        if(!isDropped){
            tooltipOfImpl.addCustom(mainButton.getPanel(),5f).getPosition().inTL(0,0);
        }
        else{
            tooltipOfImpl.addCustom(mainButton.getPanel(),5f).getPosition().inTL(0,0);
            float currY = mainButton.getPanel().getPosition().getHeight()+2;
            for (CustomButton button : buttons) {
                tooltipOfImpl.addCustom(button.getPanel(),0f).getPosition().inTL(button.indent,currY);
                currY+=button.getPanel().getPosition().getHeight()+2;
            }
            tooltipOfImpl.getPosition().setSize(width,currY);
            panelOfImpl.getPosition().setSize(width,currY);
            mainPanel.getPosition().setSize(width,currY);
        }

    }

}
