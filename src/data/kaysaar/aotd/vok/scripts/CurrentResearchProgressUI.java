package data.kaysaar.aotd.vok.scripts;

import com.fs.starfarer.C;
import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignUIAPI;
import com.fs.starfarer.api.campaign.listeners.CampaignInputListener;
import com.fs.starfarer.api.campaign.listeners.CampaignUIRenderingListener;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.plugins.AoDUtilis;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.ui.AoTDResearchUIDP;
import org.lazywizard.lazylib.ui.FontException;
import org.lazywizard.lazylib.ui.LazyFont;

import java.awt.*;
import java.util.List;


public class CurrentResearchProgressUI implements CampaignUIRenderingListener, EveryFrameScript, CampaignInputListener {
    private SpriteAPI sprite = Global.getSettings().getSprite("ui_campaign_components", "tech_panel");
    private SpriteAPI spriteOfCurrentlyResearched;

    public LazyFont loader = LazyFont.loadFont(Fonts.ORBITRON_24AABOLD);
    public LazyFont.DrawableString techString;
    public LazyFont.DrawableString buttonString;
    public LazyFont.DrawableString progressionString;
    private SpriteAPI buttonHide = Global.getSettings().getSprite("ui_campaign_components", "tech_button_hide");
    private SpriteAPI buttonHideHighlighted = Global.getSettings().getSprite("ui_campaign_components", "tech_hide_button_highlighted");
    private SpriteAPI buttonTech = Global.getSettings().getSprite("ui_campaign_components", "tech_button");
    private SpriteAPI buttonTechHighlighted = Global.getSettings().getSprite("ui_campaign_components", "tech_button_highlighted");
    private TrapezoidButtonDetector detector = new TrapezoidButtonDetector();
    private SpriteAPI progressionBarFull = Global.getSettings().getSprite("ui_campaign_components", "tech_progression");
    ;
    private SpriteAPI progressionBarChanged = Global.getSettings().getSprite("ui_campaign_components", "tech_progression");
    ;
    boolean isHidden = true;

    public CurrentResearchProgressUI() throws FontException {
    }

    @Override
    public boolean isDone() {

        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }

    @Override
    public void advance(float amount) {

    }

    @Override
    public void renderInUICoordsBelowUI(ViewportAPI viewport) {

    }

    @Override
    public void renderInUICoordsAboveUIBelowTooltips(ViewportAPI viewport) {

    }

    @Override
    public void renderInUICoordsAboveUIAndTooltips(ViewportAPI viewport) {
        final CampaignUIAPI campaignUI = Global.getSector().getCampaignUI();
        if (campaignUI.isShowingDialog() || campaignUI.isShowingMenu() || campaignUI.getCurrentCoreTab() != null)
            return;
        if (AoTDMainResearchManager.getInstance().getManagerForPlayer().howManyFacilitiesFactionControlls() <= 0)
            return;

        int x1 = 0;
        if (!isHidden) {
            sprite.render(x1, Global.getSettings().getScreenHeight() - 200);
            float x = sprite.getWidth()-2;
            float y = (Global.getSettings().getScreenHeight() - 200) + sprite.getHeight();
            if (techString == null) {
                techString = loader.createText();
            }
            if (buttonString == null) {
                buttonString = loader.createText();
            }
            if (progressionString == null) {
                progressionString = loader.createText();
            }

            if (detector.determineIfHoversOverButton(x, y, x + buttonHide.getWidth(), y - 11, x, y - sprite.getHeight(), x + buttonHide.getWidth(), y - sprite.getHeight() + 11, Global.getSettings().getMouseX(), (float) (Global.getSettings().getMouseY()))) {
                buttonHideHighlighted.setHeight(sprite.getHeight());
                buttonHideHighlighted.render(sprite.getWidth()-2, Global.getSettings().getScreenHeight() - 200);
            } else {
                buttonHide.setHeight(sprite.getHeight());
                buttonHide.render(sprite.getWidth()-2, Global.getSettings().getScreenHeight() - 200);
            }
            if (AoTDMainResearchManager.getInstance().getManagerForPlayer().getCurrentFocus() != null) {
                spriteOfCurrentlyResearched = Global.getSettings().getSprite("ui_icons_tech_tree", AoTDMainResearchManager.getInstance().getManagerForPlayer().getCurrentFocus().getSpec().getIconId());
                spriteOfCurrentlyResearched.setWidth(80);
                spriteOfCurrentlyResearched.setHeight(80);
                spriteOfCurrentlyResearched.render(8, Global.getSettings().getScreenHeight() - 197);

                techString.setText("Researching : " + AoTDMainResearchManager.getInstance().getManagerForPlayer().getCurrentFocus().getSpec().getName());
                techString.setMaxWidth(sprite.getWidth() - 98);
                techString.setFontSize(12);
                techString.setBaseColor(Misc.getTextColor());
                techString.draw(95, Global.getSettings().getScreenHeight() - 125);
                progressionBarChanged.setWidth(progressionBarFull.getWidth() * AoDUtilis.calculatePercentOfProgression(AoTDMainResearchManager.getInstance().getManagerForPlayer().getCurrentFocus()));
                progressionBarChanged.render(9, y - sprite.getHeight() + 2);
            } else {
                techString.setText("Currently nothing is being\nresearched!");
                techString.setFontSize(12);
                techString.setBaseColor(Misc.getTextColor());
                techString.draw(95, Global.getSettings().getScreenHeight() - 125);
            }

            buttonTech.setWidth(170);
            buttonTech.setHeight(20);
            buttonTechHighlighted.setWidth(170);
            buttonTechHighlighted.setHeight(20);
            float buttonXBeginning = 95;
            float buttonYBeginning = Global.getSettings().getScreenHeight() - 186 + buttonTech.getHeight();
            if (detector.determineIfHoversOverButton(buttonXBeginning, buttonYBeginning, buttonXBeginning + 170, buttonYBeginning, buttonXBeginning, buttonYBeginning - 20, buttonXBeginning + 170, buttonYBeginning - 20, Global.getSettings().getMouseX(), Global.getSettings().getMouseY())) {
                buttonTechHighlighted.render(95, Global.getSettings().getScreenHeight() - 186);

            } else {
                buttonTech.render(95, Global.getSettings().getScreenHeight() - 186);

            }

            buttonString.setText("Access Tech Tree");
            buttonString.setFontSize(12);
            buttonString.setBaseColor(Misc.getTextColor());
            buttonString.draw(130, Global.getSettings().getScreenHeight() - 170);
            if(AoTDMainResearchManager.getInstance().getManagerForPlayer().getCurrentFocus()!=null){
                progressionString.setText((int) (AoDUtilis.calculatePercentOfProgression(AoTDMainResearchManager.getInstance().getManagerForPlayer().getCurrentFocus()) * 100) + "%");
                progressionString.setFontSize(10);
                progressionString.setBaseColor(new Color(31, 32, 33));
                progressionString.draw(125, Global.getSettings().getScreenHeight() - 189);
            }


        } else {
            float x = x1;
            float y = (Global.getSettings().getScreenHeight() - 200) + sprite.getHeight();
            if (detector.determineIfHoversOverButton(x, y, x + buttonHide.getWidth(), y - 11, x, y - sprite.getHeight(), x + buttonHide.getWidth(), y - sprite.getHeight() + 11, Global.getSettings().getMouseX(), (float) (Global.getSettings().getMouseY()))) {
                buttonHideHighlighted.setHeight(sprite.getHeight());
                buttonHideHighlighted.render(x1, Global.getSettings().getScreenHeight() - 200);
            } else {
                buttonHide.setHeight(sprite.getHeight());
                buttonHide.render(x1, Global.getSettings().getScreenHeight() - 200);
            }
        }


    }

    @Override
    public int getListenerInputPriority() {
        return 1200;
    }

    @Override
    public void processCampaignInputPreCore(List<InputEventAPI> events) {

    }

    @Override
    public void processCampaignInputPreFleetControl(List<InputEventAPI> events) {
        if (sprite == null) return;
        if (buttonTech == null) return;
        if (buttonTechHighlighted == null) return;
        for (InputEventAPI event : events) {
            float x = sprite.getWidth()-2;
            float y = (Global.getSettings().getScreenHeight() - 200) + sprite.getHeight();
            if (isHidden) {
                x = 1;
            }
            if (event.isLMBDownEvent()) {
                if (detector.determineIfHoversOverButton(x, y, x + buttonHide.getWidth(), y - 11, x, y - sprite.getHeight(), x + buttonHide.getWidth(), y - sprite.getHeight() + 11, Global.getSettings().getMouseX(), (float) (Global.getSettings().getMouseY()))) {
                    Global.getSoundPlayer().playUISound("ui_button_pressed", 1f, 1f);
                    isHidden = !isHidden;
                    event.consume();
                    break;
                }
                float buttonXBeginning = 95;
                float buttonYBeginning = Global.getSettings().getScreenHeight() - 166;
                if (detector.determineIfHoversOverButton(buttonXBeginning, buttonYBeginning, buttonXBeginning + 170, buttonYBeginning, buttonXBeginning, buttonYBeginning - 20, buttonXBeginning + 170, buttonYBeginning - 20, Global.getSettings().getMouseX(), Global.getSettings().getMouseY())) {
                    Global.getSoundPlayer().playUISound("ui_button_pressed", 1f, 1f);
                    Global.getSector().getCampaignUI().showInteractionDialog(new AoTDResearchUIDP(), null);
                    event.consume();
                }
            }


        }
    }

    @Override
    public void processCampaignInputPostCore(List<InputEventAPI> events) {

    }

}
