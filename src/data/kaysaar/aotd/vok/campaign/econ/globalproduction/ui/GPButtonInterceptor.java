package data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.listeners.CampaignInputListener;
import com.fs.starfarer.api.campaign.listeners.CampaignUIRenderingListener;
import com.fs.starfarer.api.campaign.listeners.CoreUITabListener;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.UIData;
import data.kaysaar.aotd.vok.scripts.TrapezoidButtonDetector;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class GPButtonInterceptor implements CampaignUIRenderingListener, CampaignInputListener {
    @Override
    public int getListenerInputPriority() {
        return 24000;
    }

    boolean calledTwice = false;

    @Override
    public void processCampaignInputPreCore(List<InputEventAPI> events) {

        if (Global.getSector().getCampaignUI().getCurrentCoreTab() == null) return;
        if (Global.getSector().getCampaignUI().getCurrentCoreTab() == CoreUITabId.OUTPOSTS) {

            float mouseX = Global.getSettings().getMouseX();
            float mouseY = Global.getSettings().getMouseY();
            float x = 609 * Global.getSettings().getScreenScaleMult();
            float y = Global.getSettings().getScreenHeight() - 17 * Global.getSettings().getScreenScaleMult();
            float width = 205 * Global.getSettings().getScreenScaleMult();
            float height = 18 * Global.getSettings().getScreenScaleMult();

            TrapezoidButtonDetector detector = new TrapezoidButtonDetector();
            if (detector.determineIfHoversOverButton(x, y, x + width, y, x, y - height, x + width, y - height, mouseX, mouseY)) {
                for (InputEventAPI event : events) {
                    if (event.isConsumed()) continue;
                    if (event.isLMBEvent() || event.getEventValue() == Keyboard.KEY_5) {
                        if (!NidavelirMainPanelPlugin.isShowingUI) {
                            if (Global.getSector().getCampaignUI().getCurrentInteractionDialog() != null) {
                                CoreUITabId cur = Global.getSector().getCampaignUI().getCurrentCoreTab();
                                NidavelirMainPanelPlugin.isShowingUI = true;
                                Global.getSector().getCampaignUI().getCurrentInteractionDialog().getVisualPanel().closeCoreUI();
                                Global.getSoundPlayer().playUISound("ui_button_pressed", 1f, 1f);
                                Global.getSector().getCampaignUI().getCurrentInteractionDialog().showCustomVisualDialog(UIData.WIDTH, UIData.HEIGHT, new NidavelirMainPanelDelegate(new NidavelirMainPanelPlugin(false, cur, null), Global.getSector().getCampaignUI().getCurrentInteractionDialog()));
                                calledTwice = true;
                                event.consume();
                                return;
                            }

                        }
                    }
                }

            }
            calledTwice = false;
        }
    }

    @Override
    public void processCampaignInputPreFleetControl(List<InputEventAPI> events) {

    }

    @Override
    public void processCampaignInputPostCore(List<InputEventAPI> events) {

    }

    @Override
    public void renderInUICoordsBelowUI(ViewportAPI viewport) {

    }

    @Override
    public void renderInUICoordsAboveUIBelowTooltips(ViewportAPI viewport) {

    }

    @Override
    public void renderInUICoordsAboveUIAndTooltips(ViewportAPI viewport) {

    }
}
