package data.kaysaar.aotd.vok.campaign.econ.synergies.ui;

import ashlib.data.plugins.misc.AshMisc;
import com.fs.graphics.util.Fader;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.BaseIndustrySynergy;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.IndustrySynergiesManager;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.IndustrySynergySourceAPI;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.kaysaar.aotd.vok.ui.basecomps.ExtendedUIPanelPlugin;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class SynergyInfoMarket implements ExtendedUIPanelPlugin {
    CustomPanelAPI mainPanel, contentPanel;
    ButtonAPI buttonToCheckSynergies;
    MarketAPI tiedMarket;
    LabelAPI title, number, percent;

    public SynergyInfoMarket(MarketAPI tiedMarket) {
        mainPanel = Global.getSettings().createCustom(180, 40, this);
        this.tiedMarket = tiedMarket;
        createUI();
    }

    @Override
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    @Override
    public void createUI() {
        if (contentPanel != null) {
            mainPanel.removeComponent(contentPanel);
        }
        contentPanel = Global.getSettings().createCustom(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight(), null);
        TooltipMakerAPI tooltip = contentPanel.createUIElement(contentPanel.getPosition().getWidth(), 300, false);
        List<BaseIndustrySynergy> synergies = IndustrySynergiesManager.getInstance().getSynergyScriptsValidForMarketInUI(tiedMarket);
        tooltip.setParaFont("graphics/fonts/orbitron12condensed.fnt");
        title = tooltip.addPara("Synergies", tiedMarket.getFaction().getBaseUIColor(), 4f);
        title.setAlignment(Alignment.MID);
        title.setHighlightOnMouseover(true);
        tooltip.setParaFont("graphics/fonts/insignia25LTaa.fnt");
        String size = "";
        final String percent =  AoTDMisc.getPercentageString(IndustrySynergiesManager.getInstance().calculateEfficiency(tiedMarket));
        if (synergies.isEmpty()) {
            size = "--- " + " (" + percent+")";
        } else {
            size = synergies.size() + " (" + percent+")";
        }
        number = tooltip.addPara(size, tiedMarket.getFaction().getBrightUIColor(), 2f);
        number.setAlignment(Alignment.MID);
        number.setHighlightOnMouseover(true);

        ReflectionUtilis.invokeMethodWithAutoProjection("setAdditiveColor", number, (Color) null);
        buttonToCheckSynergies = tooltip.addAreaCheckbox("Show Synergy List", null, tiedMarket.getFaction().getBaseUIColor(), tiedMarket.getFaction().getDarkUIColor(), tiedMarket.getFaction().getBrightUIColor(), contentPanel.getPosition().getWidth(), 20, 16f);
        buttonToCheckSynergies.getPosition().setXAlignOffset(-5);
        if (!tiedMarket.getFaction().isPlayerFaction() && !tiedMarket.isPlayerOwned()) {
            buttonToCheckSynergies.setEnabled(false);
        }
        contentPanel.addUIElement(tooltip).inTL(0, 0);
        mainPanel.addComponent(contentPanel).inTL(0, -4);
        boolean finalNotAvailable = false;
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
                tooltip.addPara("With a well established logistical network, certain industries and structures can receive bonuses when built on the same planet.", 5f);
                tooltip.addSectionHeading("Efficiency", Alignment.MID, 5f);
                tooltip.addPara("Synergies are directly affected by efficiency of the planet's logistics. More efficient logistics will result in higher synergy bonuses.", 3f);
                tooltip.addPara("Currently this planet's efficiency is %s.", 3f, Color.ORANGE, percent);
                tooltip.addPara("The lowest efficiency value is %s and can't drop below that.",3f,Color.ORANGE,"0%");
                tooltip.setBulletedListMode(BaseIntelPlugin.BULLET);
                for (Map.Entry<String, IndustrySynergySourceAPI> entry : IndustrySynergiesManager.getInstance().getSourcesOfSynergy().entrySet()) {
                    if(tiedMarket.hasIndustry(entry.getValue().getId())&&entry.getValue().calculateEfficiencyFromIndustry(tiedMarket.getIndustry(entry.getValue().getId()),true)!=0){
                        entry.getValue().addToTooltipForInfo(tiedMarket.getIndustry(entry.getValue().getId()),tooltip);
                    }
                }
                tooltip.setBulletedListMode(null);

                tooltip.addSectionHeading("Synergies on " + tiedMarket.getName(), Alignment.MID, 5f);
                if (synergies.isEmpty()) {
                    tooltip.addPara("This planet has no synergies active. You can see possible synergies in the %s.", 3f, Color.ORANGE, "Synergy List");
                } else {
                    tooltip.addPara("This planet has %s synergies active. You can see their effect in the %s.", 3f, Color.ORANGE, "" + synergies.size(), "Synergy List");

                }


            }
        }, contentPanel, TooltipMakerAPI.TooltipLocation.RIGHT, false);
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
        if (buttonToCheckSynergies != null && buttonToCheckSynergies.isChecked()) {
            buttonToCheckSynergies.setChecked(false);
            SynergyInfoPopUp nid = new SynergyInfoPopUp(tiedMarket);
            AshMisc.placePopUpUI(nid, buttonToCheckSynergies, 730, 500);
        }
    }

    @Override
    public void processInput(List<InputEventAPI> events) {
        if (title != null && number != null) {
            Fader fader = (Fader) ReflectionUtilis.invokeMethodWithAutoProjection("getMouseoverFader", title);
            Fader fader2 = (Fader) ReflectionUtilis.invokeMethodWithAutoProjection("getMouseoverFader", number);
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
