package data.kaysaar.aotd.vok.ui.buildingmenu;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import com.fs.starfarer.api.util.Misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IndustryDropDownButton extends DropDownButton{
    IndustrySpecAPI mainSpec;
    ArrayList<IndustrySpecAPI>subSpecs;
    MarketAPI market;
    public IndustryDropDownButton(UITableImpl tableOfReference, float width, float height, float maxWidth, float maxHeight, IndustrySpecAPI mainspec, ArrayList<IndustrySpecAPI> subSpecs, MarketAPI market) {
        super(tableOfReference, width, height, maxWidth, maxHeight,subSpecs!=null&&!subSpecs.isEmpty());
        this.mainSpec = mainspec;
        this.subSpecs  = subSpecs;
        this.market = market;
        createUI();
    }
    public List<IndustrySpecAPI> getSpecs(){
        if(droppableMode){
            return subSpecs;
        }
        return Collections.singletonList(mainSpec);
    }
    @Override
    public void createUIContent() {
        if (buttons == null) {
            buttons = new ArrayList<>();
            if(droppableMode){
                for (IndustrySpecAPI subSpec : subSpecs) {
                    IndustryButton button = new IndustryButton(width-30,height,subSpec,10f,market, Misc.getDarkPlayerColor(),Misc.getDarkPlayerColor(),Misc.getDarkPlayerColor());
                    button.initializeUI();
                    buttons.add(button);
                }
            }
            if(droppableMode){
                mainButton = new IndustryButton(width-20,height,mainSpec,0f,market,Misc.getDarkHighlightColor(),Misc.getDarkPlayerColor(),Misc.getDarkHighlightColor());

            }
            else{
                mainButton = new IndustryButton(width-20,height,mainSpec,0f,market, Misc.getDarkPlayerColor(),Misc.getDarkPlayerColor(),Misc.getDarkPlayerColor());

            }
            mainButton.initializeUI();
        }
        if(!isDropped){
            tooltipOfImpl.addCustom(mainButton.getPanel(),5f).getPosition().inTL(0,0);
        }
        else{
            tooltipOfImpl.addCustom(mainButton.getPanel(),5f).getPosition().inTL(0,0);
            float currY = mainButton.getPanel().getPosition().getHeight()+2;
            for (CustomButton button : buttons) {
                IndustryButton bt = (IndustryButton) button;
                IndustryTable tb = (IndustryTable) tableOfReference;
                if(tb.cantPlaceSubIndustry(bt))continue;
                tooltipOfImpl.addCustom(button.getPanel(),0f).getPosition().inTL(button.indent,currY);
                currY+=button.getPanel().getPosition().getHeight()+2;
            }
            tooltipOfImpl.getPosition().setSize(width,currY);
            panelOfImpl.getPosition().setSize(width,currY);
            mainPanel.getPosition().setSize(width,currY);
        }

    }

}
