package data.kaysaar.aotd.vok.plugins.coreui;

import ashlib.data.plugins.coreui.CommandTabListener;
import ashlib.data.plugins.coreui.CommandUIPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.industry.MiscHiddenIndustry;
import data.kaysaar.aotd.vok.scripts.ui.TechnologyCoreUI;
import data.kaysaar.aotd.vok.ui.customprod.components.UIData;
import org.lwjgl.input.Keyboard;

import static data.kaysaar.aotd.vok.misc.AoTDMisc.tryToGetButtonProd;

public class RnDTabListener implements CommandTabListener {
    @Override
    public String getNameForTab() {
        return "Research & Production";
    }

    @Override
    public String getButtonToReplace() {
        return "custom production";
    }

    @Override
    public String getButtonToBePlacedNear() {
        return "doctrine & blueprints";
    }

    @Override
    public TooltipMakerAPI.TooltipCreator getTooltipCreatorForButton() {
        return new TooltipMakerAPI.TooltipCreator() {
            @Override
            public boolean isTooltipExpandable(Object tooltipParam) {
                return false;
            }

            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return 500;
            }

            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addSectionHeading("Ashes of the Domain: Vaults of Knowledge", Alignment.MID,0f);
                tooltip.addPara("In this tab you can find the research tree to manage your technological advancements, special projects available to you, the custom order tab to produce new ships and weapons, and megastructures currently under your control.",5f);
            }
        };
    }


    @Override
    public CommandUIPlugin createPlugin() {
        return new TechnologyCoreUI(UIData.WIDTH, UIData.HEIGHT);
    }

    @Override
    public float getWidthOfButton() {
        return 240;
    }

    @Override
    public int getKeyBind() {
        return  Keyboard.KEY_5;
    }

    @Override
    public void performRecalculations(UIComponentAPI uiPanelAPI) {
        ButtonAPI button = tryToGetButtonProd("holdings");
        if(button==null){
            button = tryToGetButtonProd("colonies");
        }
        UIData.WIDTH = Global.getSettings().getScreenWidth() - button.getPosition().getX();
        UIData.HEIGHT = uiPanelAPI.getPosition().getHeight();

        UIData.recompute();
    }

    @Override
    public int getOrder() {
        return 50;
    }

    @Override
    public boolean shouldButtonBeEnabled() {
        return true;
    }

    @Override
    public void performRefresh(ButtonAPI buttonAPI) {
        if(buttonAPI.getText().toLowerCase().contains("income")||buttonAPI.getText().toLowerCase().contains("colonies")){
            MiscHiddenIndustry.clearHiddenIndustries();
        }
        else{
            for (MarketAPI playerMarket : Misc.getPlayerMarkets(true)) {
                MiscHiddenIndustry.getInstance(playerMarket);
            }
        }
    }
}
