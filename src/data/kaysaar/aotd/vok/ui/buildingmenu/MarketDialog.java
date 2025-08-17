package data.kaysaar.aotd.vok.ui.buildingmenu;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.MutableValue;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs.BasePopUpDialog;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.kaysaar.aotd.vok.scripts.misc.TrapezoidButtonDetector;
import data.kaysaar.aotd.vok.ui.customprod.NidavelirMainPanelPlugin;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.List;

public class MarketDialog extends BasePopUpDialog {
    public MarketAPI market;
    Object overview;
    public IndustryTable table;
    public IndustrySearchPanel searchPanel;
    public IntervalUtil util = null;
    public IndustryTagFilter filter;
    public IndustryShowcaseUI showcaseUI;
    public boolean dissableExit = false;
    public MarketDialog(String headerTitle, MarketAPI market,Object overview) {
        super(headerTitle);
        this.market = market;
        this.overview = overview;
    }


    @Override
    public void createUI(CustomPanelAPI panelAPI) {
        createHeaader(panelAPI);

        TooltipMakerAPI tooltip = panelAPI.createUIElement(panelAPI.getPosition().getWidth()-30,panelAPI.getPosition().getHeight()-y,false);
        createContentForDialog(tooltip,panelAPI.getPosition().getWidth()-30,panelAPI.getPosition().getHeight()-y-70);
        panelAPI.addUIElement(tooltip).inTL(x,y);
        createConfirmAndCancelSection(panelAPI);;
    }


    public void createContentForDialog(TooltipMakerAPI tooltip, float width,float height) {
        CustomPanelAPI panel =  Global.getSettings().createCustom(width,panelToInfluence.getPosition().getHeight(),null);
         table = new IndustryTable(630,height-30,panel,true,0,0,this);
         searchPanel = new IndustrySearchPanel(200,20,table);
        filter = new IndustryTagFilter(402,20,this);
        showcaseUI = new IndustryShowcaseUI(width - 640,height,market);
        table.createSections();
        table.createTable();
        showcaseUI.setCurrentSpec(Global.getSettings().getIndustrySpec(AoTDIndustries.RESEARCH_CENTER));
        IndustryInfoBottom bottom = new IndustryInfoBottom(market,width,30);
        tooltip.addCustom(panel,5f).getPosition().inTL(0,30);
        tooltip.addCustom(searchPanel.getMainPanel(),5f).getPosition().inTL(5,0);
        tooltip.addCustom(filter.getMainPanel(),5f).getPosition().inTL(210,0);
        tooltip.addCustom(showcaseUI.getMainPanel(),5f).getPosition().inTL(635,0);
        tooltip.addCustom(bottom.getMainPanel(),5f).getPosition().inTL(5,height+30);
    }
@Override
    public ButtonAPI generateConfirmButton(TooltipMakerAPI tooltip){
        ButtonAPI button = tooltip.addButton("Build","confirm", NidavelirMainPanelPlugin.base,NidavelirMainPanelPlugin.bg, Alignment.MID, CutStyle.TL_BR,160,25,0f);
        button.setShortcut(Keyboard.KEY_G,true);
        confirmButton = button;
        return button;
    }
    @Override
    public ButtonAPI generateCancelButton(TooltipMakerAPI tooltip){
        ButtonAPI button = tooltip.addButton("Dismiss","cancel", NidavelirMainPanelPlugin.base,NidavelirMainPanelPlugin.bg,Alignment.MID,CutStyle.TL_BR,buttonConfirmWidth,25,0f);
        button.setShortcut(Keyboard.KEY_A,true);
        cancelButton = button;
        return button;
    }
    @Override
    public void applyConfirmScript() {
        if(table.specToBuilt!=null){
            Industry ind = table.specToBuilt.getNewPluginInstance(market);
            int cost = (int) ind.getBuildCost();
            Misc.getCurrentlyBeingConstructed(market);
            this.market.getConstructionQueue().addToEnd(ind.getId(), cost);
            MutableValue credits = Global.getSector().getPlayerFleet().getCargo().getCredits();
            credits.subtract(cost);
            if (credits.get() <= 0.0F) {
                credits.set(0.0F);
            }
            Global.getSector().getCampaignUI().getMessageDisplay().addMessage(String.format("Spent %s", Misc.getDGSCredits((cost)),Global.getSettings().getColor("standardUIIconColor"), Misc.getDGSCredits(cost), Color.ORANGE));
            Global.getSoundPlayer().playUISound("ui_build_industry", 1, 1);


        }
        ReflectionUtilis.invokeMethod("recreateWithEconUpdate",overview);
    }

    @Override
    public void processInput(List<InputEventAPI> events) {
        for (InputEventAPI event : events) {
            if(frames>=15){
                if(event.isMouseDownEvent()&&!isDialog){
                    TrapezoidButtonDetector detector = new TrapezoidButtonDetector();
                    float xLeft = panelToInfluence.getPosition().getX();
                    float xRight = panelToInfluence.getPosition().getX()+panelToInfluence.getPosition().getWidth();
                    float yBot = panelToInfluence.getPosition().getY();
                    float yTop = panelToInfluence.getPosition().getY()+panelToInfluence.getPosition().getHeight();
                    boolean hovers = detector.determineIfHoversOverButton(xLeft,yTop,xRight,yTop,xLeft,yBot,xRight,yBot,Global.getSettings().getMouseX(),Global.getSettings().getMouseY());
                    if(!hovers){
                        ProductionUtil.getCoreUI().removeComponent(panelToInfluence);
                        event.consume();
                        onExit();
                    }
                }
                if(!event.isConsumed()&&!dissableExit){
                    if(event.getEventValue()== Keyboard.KEY_ESCAPE){
                        ProductionUtil.getCoreUI().removeComponent(panelToInfluence);
                        event.consume();
                        onExit();
                        break;
                    }
                }
            }
            event.consume();
        }



    }

    @Override
    public void onExit() {
        for (DropDownButton o : table.copyOfButtons) {
            o.clear();
        }
        for (DropDownButton o : table.dropDownButtons) {
            o.clear();
        }
        table.clearTable();
        table.activeTags.clear();
        table.specs.clear();
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        if(util!=null){
            util.advance(amount);
            if(util.intervalElapsed()){
                dissableExit =  false;
                util = null;
            }
        }
        if(table!=null){
            table.advance(amount);
            if(table.specToBuilt!=null){
                BaseIndustry ind = (BaseIndustry) table.specToBuilt.getNewPluginInstance(market);
                if(ind.isAvailableToBuild()){
                    if(!confirmButton.isEnabled()){
                        confirmButton.setEnabled(true);
                    }
                }
                else{
                    if(confirmButton.isEnabled()){
                        confirmButton.setEnabled(false);
                    }
                }
            }
            else{
                if(confirmButton.isEnabled()){
                    confirmButton.setEnabled(false);
                }
            }
        }


    }
}
