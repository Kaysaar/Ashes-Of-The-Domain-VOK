package data.kaysaar.aotd.vok.campaign.econ.synergies.ui;

import ashlib.data.plugins.misc.AshMisc;
import com.fs.graphics.util.Fader;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.BaseIndustrySynergy;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.IndustrySynergiesManager;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.ui.basecomps.ExtendUIPanelPlugin;

import java.awt.*;
import java.util.List;

public class SynergyInfoMarket implements ExtendUIPanelPlugin {
    CustomPanelAPI mainPanel,contentPanel;
    ButtonAPI buttonToCheckSynergies;
    MarketAPI tiedMarket;
    LabelAPI title,number;
    public SynergyInfoMarket(MarketAPI tiedMarket) {
        mainPanel = Global.getSettings().createCustom(180,40,this);
        this.tiedMarket = tiedMarket;
        createUI();
    }
    @Override
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    @Override
    public void createUI() {
        if(contentPanel!=null){
            mainPanel.removeComponent(contentPanel);
        }
        contentPanel = Global.getSettings().createCustom(mainPanel.getPosition().getWidth(),mainPanel.getPosition().getHeight(),null);
        TooltipMakerAPI tooltip = contentPanel.createUIElement(contentPanel.getPosition().getWidth(),300,false);
        List<BaseIndustrySynergy>synergies = IndustrySynergiesManager.getInstance().getSynergyScriptsValidForMarketInUI(tiedMarket);
        tooltip.setParaFont("graphics/fonts/orbitron12condensed.fnt");
        title=tooltip.addPara("Synergies", tiedMarket.getFaction().getBaseUIColor(),4f);
        title.setAlignment(Alignment.MID);
        title.setHighlightOnMouseover(true);
        tooltip.setParaFont("graphics/fonts/insignia25LTaa.fnt");
        String size = "";
        boolean notAvailable = !IndustrySynergiesMisc.isIndustryFunctionalAndExisting(tiedMarket,"aotd_maglev");
        if(synergies.isEmpty()||notAvailable){
            size = "---";
        }
        else{
            size = ""+synergies.size();
        }
        number = tooltip.addPara(size,tiedMarket.getFaction().getBrightUIColor(),2f);
        number.setAlignment(Alignment.MID);
        number.setHighlightOnMouseover(true);
        ReflectionUtilis.invokeMethodWithAutoProjection("setAdditiveColor",number,(Color)null);
        buttonToCheckSynergies = tooltip.addAreaCheckbox("Show Synergy List",null,tiedMarket.getFaction().getBaseUIColor(),tiedMarket.getFaction().getDarkUIColor(),tiedMarket.getFaction().getBrightUIColor(),contentPanel.getPosition().getWidth(),20,16f);
        buttonToCheckSynergies.getPosition().setXAlignOffset(-5);
        buttonToCheckSynergies.setEnabled(AoTDMainResearchManager.getInstance().isAvailableForThisMarket("aotd_tech_maglev",tiedMarket));
        if(!tiedMarket.getFaction().isPlayerFaction()&&!tiedMarket.isPlayerOwned()){
            buttonToCheckSynergies.setEnabled(false);
        }
        contentPanel.addUIElement(tooltip).inTL(0,0);
        mainPanel.addComponent(contentPanel).inTL(0,-4);
        boolean finalNotAvailable = notAvailable;
        tooltip.addTooltipTo(new TooltipMakerAPI.TooltipCreator() {
            @Override
            public boolean isTooltipExpandable(Object tooltipParam) {
                return false;
            }

            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return 450f;
            }

            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addTitle("Synergies");
                tooltip.addPara("With well established logistical network on colony, certain structures/industries can benefit from each-other",5f);
                tooltip.addPara("For such benefits we must first build %s, which requires %s to be researched first!",5,Color.ORANGE,"Maglev Central Hub","Urban Throughput Systems");
                if(!buttonToCheckSynergies.isEnabled()){
                    tooltip.addPara("We need to first research %s to be able to see possible synergies!",5f,Color.ORANGE,"Urban Throughput Systems");
                }

                if(!finalNotAvailable){
                    tooltip.addSectionHeading("Synergies on "+tiedMarket.getName(),Alignment.MID,5f);
                    if(synergies.isEmpty()){
                        tooltip.addPara("Currently this market has no synergies, you can see possible synergies in %s",3f,Color.ORANGE,"Synergy List");
                    }
                    else{
                        tooltip.addPara("Currently this market benefits from %s synergies, you can see their effect in %s",3f,Color.ORANGE,""+synergies.size(),"Synergy List");

                    }
                }
                else{
                    tooltip.addPara("For synergies to apply %s must be functional!",5f,Color.ORANGE,"Maglev Central Hub");
                }
            }
        },contentPanel, TooltipMakerAPI.TooltipLocation.RIGHT,false);
    }

    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {

    }

    @Override
    public void render(float alphaMult) {

    }

    @Override
    public void advance(float amount) {
        if(buttonToCheckSynergies!=null&&buttonToCheckSynergies.isChecked()){
            buttonToCheckSynergies.setChecked(false);
            SynergyInfoPopUp nid = new SynergyInfoPopUp(tiedMarket);
            AshMisc.placePopUpUI(nid,buttonToCheckSynergies, 730,500);
        }
    }

    @Override
    public void processInput(List<InputEventAPI> events) {
        if(title!=null&&number!=null){
            Fader fader = (Fader) ReflectionUtilis.invokeMethodWithAutoProjection("getMouseoverFader",title);
            Fader fader2 = (Fader) ReflectionUtilis.invokeMethodWithAutoProjection("getMouseoverFader",number);
            fader.fadeOut();
            fader2.fadeOut();

            for (InputEventAPI event : events) {
                if (!event.isConsumed() && event.isMouseEvent() && this.contentPanel.getPosition().containsEvent(event)) {
                    fader.fadeIn();
                    fader2.fadeIn();
                    break;

                }
            }
        }


    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
