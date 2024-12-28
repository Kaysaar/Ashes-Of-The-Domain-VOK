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


    public MarketDialog(String headerTitle, MarketAPI market,Object overview) {
        super(headerTitle);
        this.market = market;
        this.overview = overview;
    }

    @Override
    public void createContentForDialog(TooltipMakerAPI tooltip, float width) {
        CustomPanelAPI panel =  Global.getSettings().createCustom(width,400,null);
        IndustryTable table = new IndustryTable(630,400,panel,true,0,0,market);
        table.createTable();
        tooltip.addCustom(panel,5f);
    }

    @Override
    public void applyConfirmScript() {
        market.addIndustry(Industries.BATTLESTATION_HIGH);
        ReflectionUtilis.invokeMethod("recreateWithEconUpdate",overview);
    }
}
