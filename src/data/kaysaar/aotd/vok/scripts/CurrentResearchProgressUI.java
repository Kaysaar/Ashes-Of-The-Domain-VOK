package data.kaysaar.aotd.vok.scripts;


import ashlib.data.plugins.coreui.CommandTabMemoryManager;
import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignUIAPI;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.listeners.CampaignInputListener;
import com.fs.starfarer.api.campaign.listeners.CampaignUIRenderingListener;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.plugins.AoDUtilis;
import data.kaysaar.aotd.vok.scripts.misc.TrapezoidButtonDetector;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import org.lazywizard.lazylib.ui.FontException;
import org.lazywizard.lazylib.ui.LazyFont;
import org.lwjgl.opengl.GL11;
import sidebarLib.UI.sidebarMain;

import java.awt.*;
import java.util.List;

;


public class CurrentResearchProgressUI implements CampaignUIRenderingListener, EveryFrameScript, CampaignInputListener {
    transient SpriteAPI sprite = Global.getSettings().getSprite("ui_campaign_components", "tech_panel");
    transient SpriteAPI spriteOfCurrentlyResearched;

    transient LazyFont loader = LazyFont.loadFont(Fonts.ORBITRON_24AABOLD);
    transient LazyFont.DrawableString techString;
    transient LazyFont.DrawableString buttonString;
    transient LazyFont.DrawableString progressionString;
    transient SpriteAPI buttonHide = Global.getSettings().getSprite("ui_campaign_components", "tech_button_hide");
    transient SpriteAPI buttonHideHighlighted = Global.getSettings().getSprite("ui_campaign_components", "tech_hide_button_highlighted");
    transient SpriteAPI buttonTech = Global.getSettings().getSprite("ui_campaign_components", "tech_button");
    transient SpriteAPI buttonTechHighlighted = Global.getSettings().getSprite("ui_campaign_components", "tech_button_highlighted");
    transient TrapezoidButtonDetector detector = new TrapezoidButtonDetector();
    transient SpriteAPI progressionBarFull = Global.getSettings().getSprite("ui_campaign_components", "tech_progression");
    ;
    transient SpriteAPI progressionBarChanged = Global.getSettings().getSprite("ui_campaign_components", "tech_progression");
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
        if (AoTDMainResearchManager.getInstance().getManagerForPlayer().getAmountOfResearchFacilities() <= 0)
            return;

        if(Global.getSettings().getModManager().isModEnabled("sidebar")){

        }
        int x1 = 0;
        if (!isHidden) {
            sprite.render(x1, getYForRender());
            float x = sprite.getWidth()-2;
            float y = (getYForRender()) + sprite.getHeight();
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
                buttonHideHighlighted.render(sprite.getWidth()-2, getYForRender());
            } else {
                buttonHide.setHeight(sprite.getHeight());
                buttonHide.render(sprite.getWidth()-2, getYForRender());
            }
            if (AoTDMainResearchManager.getInstance().getManagerForPlayer().getCurrentFocus() != null) {
                spriteOfCurrentlyResearched = Global.getSettings().getSprite("ui_icons_tech_tree", AoTDMainResearchManager.getInstance().getManagerForPlayer().getCurrentFocus().getSpec().getIconId());
                spriteOfCurrentlyResearched.setWidth(80);
                spriteOfCurrentlyResearched.setHeight(80);
                spriteOfCurrentlyResearched.render(8, getYForRender() +3);

                techString.setText("Researching : " + AoTDMainResearchManager.getInstance().getManagerForPlayer().getCurrentFocus().getSpec().getName());
                techString.setMaxWidth(sprite.getWidth() - 98);
                techString.setFontSize(12);
                techString.setBaseColor(Misc.getTextColor());
                techString.setBlendSrc(GL11.GL_SRC_ALPHA);
                techString.setBlendDest(GL11.GL_ONE_MINUS_SRC_ALPHA);
                techString.draw(95, getYForRender() + 75);
                progressionBarChanged.setWidth(progressionBarFull.getWidth() * AoDUtilis.calculatePercentOfProgression(AoTDMainResearchManager.getInstance().getManagerForPlayer().getCurrentFocus()));
                progressionBarChanged.render(7, y - sprite.getHeight() + 2);
            } else {
                techString.setText("Currently nothing is being\nresearched!");
                techString.setFontSize(12);
                techString.setBaseColor(Misc.getTextColor());
                techString.setBlendSrc(GL11.GL_SRC_ALPHA);
                techString.setBlendDest(GL11.GL_ONE_MINUS_SRC_ALPHA);
                techString.draw(95, getYForRender() + 75);
            }

            buttonTech.setWidth(170);
            buttonTech.setHeight(20);
            buttonTechHighlighted.setWidth(170);
            buttonTechHighlighted.setHeight(20);
            float buttonXBeginning = 95;
            float buttonYBeginning = getYForRender() + 14 + buttonTech.getHeight();
            if (detector.determineIfHoversOverButton(buttonXBeginning, buttonYBeginning, buttonXBeginning + 170, buttonYBeginning, buttonXBeginning, buttonYBeginning - 20, buttonXBeginning + 170, buttonYBeginning - 20, Global.getSettings().getMouseX(), Global.getSettings().getMouseY())) {
                buttonTechHighlighted.render(95, getSecondY());

            } else {
                buttonTech.render(95, getSecondY());

            }

            buttonString.setText("Access Tech Tree");
            buttonString.setFontSize(12);
            buttonString.setBaseColor(Misc.getTextColor());
            buttonString.setBlendSrc(GL11.GL_SRC_ALPHA);
            buttonString.setBlendDest(GL11.GL_ONE_MINUS_SRC_ALPHA);
            buttonString.draw(130, getYForRender() +30);
            if(AoTDMainResearchManager.getInstance().getManagerForPlayer().getCurrentFocus()!=null){
                progressionString.setText((int) (AoDUtilis.calculatePercentOfProgression(AoTDMainResearchManager.getInstance().getManagerForPlayer().getCurrentFocus()) * 100) + "%");
                progressionString.setFontSize(10);
                if((AoDUtilis.calculatePercentOfProgression(AoTDMainResearchManager.getInstance().getManagerForPlayer().getCurrentFocus()) * 100)>=50){
                    progressionString.setBaseColor(new Color(31, 32, 33));
                }
                else{
                    progressionString.setBaseColor(Misc.getTooltipTitleAndLightHighlightColor());

                }
                progressionString.setBlendSrc(GL11.GL_SRC_ALPHA);
                progressionString.setBlendDest(GL11.GL_ONE_MINUS_SRC_ALPHA);
                progressionString.draw(125, getYForRender() +11);
            }
            

        } else {
            float x = x1;
            float y = (getYForRender()) + sprite.getHeight();
            if (detector.determineIfHoversOverButton(x, y, x + buttonHide.getWidth(), y - 11, x, y - sprite.getHeight(), x + buttonHide.getWidth(), y - sprite.getHeight() + 11, Global.getSettings().getMouseX(), (float) (Global.getSettings().getMouseY()))) {
                buttonHideHighlighted.setHeight(sprite.getHeight());
                buttonHideHighlighted.render(x1, getYForRender());
            } else {
                buttonHide.setHeight(sprite.getHeight());
                buttonHide.render(x1, getYForRender());
            }
        }

    }

    private  float getSecondY() {
        return getYForRender()+ 14;
    }

    private  float getYForRender() {
        if(Global.getSettings().getModManager().isModEnabled("sidebar")){
            return sidebarMain.getSidebar().yyLoc()-sidebarMain.getSidebar().getSidebarHeight()-sidebarMain.headerHeight()-sprite.getHeight()-5;
        }
        return Global.getSettings().getScreenHeight() - 200;
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
            float y = (getYForRender()) + sprite.getHeight();
            if (isHidden) {
                x = 1;
            }
            if (event.isLMBDownEvent()&&!event.isConsumed()) {
                if (detector.determineIfHoversOverButton(x, y, x + buttonHide.getWidth(), y - 11, x, y - sprite.getHeight(), x + buttonHide.getWidth(), y - sprite.getHeight() + 11, Global.getSettings().getMouseX(), (float) (Global.getSettings().getMouseY()))) {
                    Global.getSoundPlayer().playUISound("ui_button_pressed", 1f, 1f);
                    isHidden = !isHidden;
                    event.consume();
                    break;
                }
                float buttonXBeginning = 95;
                float buttonYBeginning = getYForRender() + 14 + buttonTech.getHeight();
                if (!isHidden&&detector.determineIfHoversOverButton(buttonXBeginning, buttonYBeginning, buttonXBeginning + 170, buttonYBeginning, buttonXBeginning, buttonYBeginning - 20, buttonXBeginning + 170, buttonYBeginning - 20, Global.getSettings().getMouseX(), Global.getSettings().getMouseY())) {
                    Global.getSoundPlayer().playUISound("ui_button_pressed", 1f, 1f);
                    CommandTabMemoryManager.getInstance().setLastCheckedTab("research & production");
                    CommandTabMemoryManager.getInstance().getTabStates().put("research & production","research");
                   Global.getSector().getCampaignUI().showCoreUITab(CoreUITabId.OUTPOSTS);
                    event.consume();
                }
            }


        }
    }

    @Override
    public void processCampaignInputPostCore(List<InputEventAPI> events) {

    }

}
