package data.kaysaar.aotd.vok.campaign.econ.items;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.CargoTransferHandlerAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.campaign.impl.items.BaseSpecialItemPlugin;
import com.fs.starfarer.api.campaign.impl.items.ShipBlueprintItemPlugin;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.loading.Description;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SpecialBlueprintPlugin extends ShipBlueprintItemPlugin {
    protected String memoryKeyToSet;
    @Override
    public void init(@NotNull CargoStackAPI stack) {
        this.stack = stack;
        String[]splitted =new String[2];
        String  raw = stack.getSpecialDataIfSpecial().getData();
        splitted = raw.split(":");
        ship = Global.getSettings().getHullSpec(splitted[0]);
        memoryKeyToSet = splitted[1];
    }
    @Override
    public boolean shouldRemoveOnRightClickAction() {
        String memKey = getMemoryKeyToSet();
        return !Global.getSector().getPlayerFaction().getMemory().is(memKey,true);
    }
    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, CargoTransferHandlerAPI transferHandler, Object stackSource) {
        float opad = 10f;

        tooltip.addTitle(getName());

        String design = getDesignType();
        Misc.addDesignTypePara(tooltip, design, opad);

        if (!spec.getDesc().isEmpty()) {
            Color c = Misc.getGrayColor();;
            tooltip.addPara(spec.getDesc(), c, opad);
        }



        float pad = 3f;
        float small = 5f;
        Color h = Misc.getHighlightColor();
        Color g = Misc.getGrayColor();
        Color b = Misc.getButtonTextColor();
        b = Misc.getPositiveHighlightColor();


        String memkey = getMemoryKeyToSet();
        boolean known = Global.getSector().getPlayerFaction().getMemory().is(memkey,true);
        String hullId = getHullId();
        List<String> hulls = new ArrayList<String>();
        hulls.add(hullId);
        addShipList(tooltip, "Ship hulls:", hulls, 1, opad);
        Description desc = Global.getSettings().getDescription(ship.getDescriptionId(), Description.Type.SHIP);

        String prefix = "";
        if (ship.getDescriptionPrefix() != null) {
            prefix = ship.getDescriptionPrefix() + "\n\n";
        }
        tooltip.addPara(prefix + desc.getText1FirstPara(), opad);

        addCostLabel(tooltip, opad, transferHandler, stackSource);

        if (known) {
            tooltip.addPara("Already known", g, opad);
        } else {
            tooltip.addPara("Right-click to learn", b, opad);
        }
    }
    @Override
    public void render(float x, float y, float w, float h, float alphaMult,
                       float glowMult, SpecialItemRendererAPI renderer) {
        float cx = x + w/2f;
        float cy = y + h/2f;

        float blX = cx - 30f;
        float blY = cy - 15f;
        float tlX = cx - 20f;
        float tlY = cy + 26f;
        float trX = cx + 23f;
        float trY = cy + 26f;
        float brX = cx + 15f;
        float brY = cy - 18f;

        String hullId = getHullId();

        String memkey = getMemoryKeyToSet();
        boolean known = Global.getSector().getPlayerFaction().getMemory().is(memkey,true);

        float mult = 1f;
        //if (known) mult = 0.5f;

        Color bgColor = Global.getSector().getPlayerFaction().getDarkUIColor();
        bgColor = Misc.setAlpha(bgColor, 255);

        //float b = Global.getSector().getCampaignUI().getSharedFader().getBrightness() * 0.25f;
        renderer.renderBGWithCorners(bgColor, blX, blY, tlX, tlY, trX, trY, brX, brY,
                alphaMult * mult, glowMult * 0.5f * mult, false);
        renderer.renderShipWithCorners(hullId, null, blX, blY, tlX, tlY, trX, trY, brX, brY,
                alphaMult * mult, glowMult * 0.5f * mult, !known);


        SpriteAPI overlay = Global.getSettings().getSprite("ui", "bpOverlayShip");
        overlay.setColor(Color.green);
        overlay.setColor(Global.getSector().getPlayerFaction().getBrightUIColor());
        overlay.setAlphaMult(alphaMult);
        overlay.setNormalBlend();
        renderer.renderScanlinesWithCorners(blX, blY, tlX, tlY, trX, trY, brX, brY, alphaMult, false);


        if (known) {
            renderer.renderBGWithCorners(Color.black, blX, blY, tlX, tlY, trX, trY, brX, brY,
                    alphaMult * 0.5f, 0f, false);
        }


        overlay.renderWithCorners(blX, blY, tlX, tlY, trX, trY, brX, brY);
    }

    @Override
    public int getPrice(MarketAPI market, SubmarketAPI submarket) {
        return super.getPrice(market, submarket);
    }

    @Override
    public void performRightClickAction() {
        String memKey = getMemoryKeyToSet();

        if (Global.getSector().getPlayerFaction().getMemory().is(memKey,true)) {
            Global.getSector().getCampaignUI().getMessageDisplay().addMessage(
                    "" + ship.getNameWithDesignationWithDashClass() + ": blueprint already known");//,
        } else {
            Global.getSoundPlayer().playUISound("ui_acquired_blueprint", 1, 1);
            Global.getSector().getPlayerFaction().getMemory().set(memKey,true);
            Global.getSector().getCampaignUI().getMessageDisplay().addMessage(
                    "Acquired blueprint: " + ship.getNameWithDesignationWithDashClass() + "");//,

        }
    }
    public String getHullId(){
        String[]splitted;
        String  raw = stack.getSpecialDataIfSpecial().getData();
        splitted = raw.split(":");
        return splitted[0];
    }
    public String getMemoryKeyToSet(){
        String[]splitted;
        String  raw = stack.getSpecialDataIfSpecial().getData();
        splitted = raw.split(":");
        return splitted[1];
    }
}
