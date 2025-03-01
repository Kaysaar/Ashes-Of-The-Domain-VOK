package data.kaysaar.aotd.vok.ui.customprod;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignUIAPI;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.listeners.CampaignInputListener;
import com.fs.starfarer.api.campaign.listeners.CampaignUIRenderingListener;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.ui.customprod.components.UIData;
import data.kaysaar.aotd.vok.listeners.CoreUiInterceptor;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.TrapezoidButtonDetector;
import org.lazywizard.lazylib.ui.FontException;

import java.util.List;

public class GpProductionButtonRenderer implements CampaignUIRenderingListener, CampaignInputListener {

    transient SpriteAPI buttonHide = Global.getSettings().getSprite("ui_campaign_components", "gp_button_hide");
    transient SpriteAPI buttonHideHighlighted = Global.getSettings().getSprite("ui_campaign_components", "gp_button_hide_highlight");
    transient TrapezoidButtonDetector detector = new TrapezoidButtonDetector();
    float height = buttonHide.getHeight();

    public GpProductionButtonRenderer() throws FontException {
    }


    @Override
    public void renderInUICoordsBelowUI(ViewportAPI viewport) {

    }

    @Override
    public void renderInUICoordsAboveUIBelowTooltips(ViewportAPI viewport) {

    }




    @Override
    public void renderInUICoordsAboveUIAndTooltips(ViewportAPI viewport) {
        if(!GPManager.isEnabled)return;
        final CampaignUIAPI campaignUI = Global.getSector().getCampaignUI();
        if (campaignUI.isShowingDialog() || campaignUI.isShowingMenu() || campaignUI.getCurrentCoreTab() != null)
            return;
        if(!AoTDMisc.isPLayerHavingHeavyIndustry())return;
        float x1=0;
        float x = x1;
        float y = (Global.getSettings().getScreenHeight() - 300) + height;
        if (detector.determineIfHoversOverButton(x, y, x + buttonHide.getWidth(), y - 11, x, y - height, x + buttonHide.getWidth(), y - height + 11, Global.getSettings().getMouseX(), (float) (Global.getSettings().getMouseY()))) {
            buttonHideHighlighted.setHeight(height);
            buttonHideHighlighted.render(x1, Global.getSettings().getScreenHeight() - 300);
        } else {
            buttonHide.setHeight(height);
            buttonHide.render(x1, Global.getSettings().getScreenHeight() - 300);
        }

    }
    @Override
    public int getListenerInputPriority() {
        return 1400;
    }

    @Override
    public void processCampaignInputPreCore(java.util.List<InputEventAPI> events) {
        if(!GPManager.isEnabled)return;
        if (buttonHide == null) return;
        if (buttonHideHighlighted == null) return;
        if(!AoTDMisc.isPLayerHavingHeavyIndustry())return;
        for (InputEventAPI event : events) {
            float x = 1;
            float y = (Global.getSettings().getScreenHeight() - 300) + height;
            if (event.isLMBDownEvent()&&!event.isConsumed()) {
                if (detector.determineIfHoversOverButton(x, y, x + buttonHide.getWidth(), y - 11, x, y - height, x + buttonHide.getWidth(), y - height + 11, Global.getSettings().getMouseX(), (float) (Global.getSettings().getMouseY()))) {
                    Global.getSoundPlayer().playUISound("ui_button_pressed", 1f, 1f);
                    NidavelirMainPanelPlugin.isShowingUI = true;
                    if(Global.getSector().getListenerManager().hasListenerOfClass(CoreUiInterceptor.class)){
                        Global.getSector().getCampaignUI().showInteractionDialog(new HolderDialog(CoreUITabId.OUTPOSTS,null), null);
                        Global.getSector().getCampaignUI().getCurrentInteractionDialog().showCustomVisualDialog(UIData.WIDTH, UIData.HEIGHT, new NidavelirMainPanelDelegate(new NidavelirMainPanelPlugin(false, CoreUITabId.OUTPOSTS, null), Global.getSector().getCampaignUI().getCurrentInteractionDialog()));
                    }
                    else{
                        Global.getSector().getCampaignUI().showInteractionDialog(new NidavelirMainPanelDP(),null);
                    }
                    event.consume();
                    break;
                }

            }


        }
    }

    @Override
    public void processCampaignInputPreFleetControl(java.util.List<InputEventAPI> events) {

    }

    @Override
    public void processCampaignInputPostCore(List<InputEventAPI> events) {

    }
}
