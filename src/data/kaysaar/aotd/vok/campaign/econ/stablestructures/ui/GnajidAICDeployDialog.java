package data.kaysaar.aotd.vok.campaign.econ.stablestructures.ui;

import ashlib.data.plugins.ui.models.BasePopUpDialog;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.tot.ui.starsystems.stuctures.widget.StableStructureWidget;
import data.kaysaar.aotd.vok.campaign.econ.stablestructures.impl.GnajidIndustrialStationImpl;

import java.util.LinkedHashMap;
import java.util.Map;

public class GnajidAICDeployDialog extends BasePopUpDialog {
    protected GnajidIndustrialStationImpl station;
    protected StableStructureWidget widget;
    protected LinkedHashMap<ButtonAPI, MarketAPI> buttons = new LinkedHashMap<>();
    protected MarketAPI selected;

    public GnajidAICDeployDialog(GnajidIndustrialStationImpl station, StableStructureWidget widget) {
        super("Deploy Advanced Industrial Controller");
        this.station = station;
        this.widget = widget;
        confirmButtonText = "Deploy AIC";
    }

    @Override
    public void createContentForDialog(TooltipMakerAPI tooltip, float width) {
        tooltip.addPara("Select a colony to receive an Advanced Industrial Controller. " +
                "The station can deploy one controller every 90 days.", 0f);
        tooltip.addPara("Construction speed +20% | Synergy efficiency +50% | Commodity demand -10%",
                Misc.getPositiveHighlightColor(), 5f);

        if (!station.canDeployAIC()) {
            tooltip.addPara("Deployment is unavailable while the station is offline or on cooldown.",
                    Misc.getNegativeHighlightColor(), 10f);
            return;
        }
        if (station.getEligibleMarkets().isEmpty()) {
            tooltip.addPara("There are no eligible player colonies within the station's deployment network.",
                    Misc.getNegativeHighlightColor(), 10f);
            return;
        }

        float height = 295f;
        CustomPanelAPI selector = Global.getSettings().createCustom(width - 10f, height, null);
        TooltipMakerAPI list = selector.createUIElement(width - 15f, height - 8f, true);
        list.addPara("Available colonies", Misc.getBasePlayerColor(), 4f);

        for (MarketAPI market : station.getEligibleMarkets()) {
            String name = market.getName() + "  -  " + market.getStarSystem().getName();
            ButtonAPI button = list.addAreaCheckbox(name, market.getId(),
                    Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Misc.getBrightPlayerColor(),
                    width - 25f, 29f, 4f);
            buttons.put(button, market);
        }

        selector.addUIElement(list).inTL(0f, 0f);
        tooltip.addCustom(selector, 10f);
    }

    @Override
    public void advance(float amount) {
        for (Map.Entry<ButtonAPI, MarketAPI> entry : buttons.entrySet()) {
            if (!entry.getKey().isChecked() || selected == entry.getValue()) continue;
            selected = entry.getValue();
            for (Map.Entry<ButtonAPI, MarketAPI> other : buttons.entrySet()) {
                other.getKey().setChecked(other.getValue() == selected);
            }
            break;
        }
        if (selected != null) {
            boolean selectedChecked = false;
            for (Map.Entry<ButtonAPI, MarketAPI> entry : buttons.entrySet()) {
                if (entry.getValue() == selected && entry.getKey().isChecked()) {
                    selectedChecked = true;
                    break;
                }
            }
            if (!selectedChecked) selected = null;
        }
        if (confirmButton != null) {
            boolean enabled = selected != null && station.canDeployTo(selected);
            if (confirmButton.isEnabled() != enabled) confirmButton.setEnabled(enabled);
            if (!enabled && confirmButton.isChecked()) confirmButton.setChecked(false);
        }
        super.advance(amount);
    }

    @Override
    public void applyConfirmScript() {
        if (selected == null || !station.deployAIC(selected)) return;
        if (widget != null) widget.requestSystemStructuresRefresh();
    }
}
