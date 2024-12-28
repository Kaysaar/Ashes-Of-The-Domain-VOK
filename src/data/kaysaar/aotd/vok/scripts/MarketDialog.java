package data.kaysaar.aotd.vok.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs.BasePopUpDialog;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.kaysaar.aotd.vok.ui.buildingmenu.IndustryTable;

public class MarketDialog extends BasePopUpDialog {
    MarketAPI market;
    Object overview;
    IndustryTable table;

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
         table = new IndustryTable(630,height,panel,true,0,0,market);
        table.createTable();
        tooltip.addCustom(panel,5f).getPosition().inTL(0,0);
    }

    @Override
    public void applyConfirmScript() {
        market.addIndustry(Industries.BATTLESTATION_HIGH);
        ReflectionUtilis.invokeMethod("recreateWithEconUpdate",overview);
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        if(table!=null){
            table.advance(amount);
        }

    }
}
