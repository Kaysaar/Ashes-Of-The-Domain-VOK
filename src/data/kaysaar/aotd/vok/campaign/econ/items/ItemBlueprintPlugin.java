package data.kaysaar.aotd.vok.campaign.econ.items;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.CargoTransferHandlerAPI;
import com.fs.starfarer.api.campaign.SpecialItemSpecAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.campaign.impl.items.BaseSpecialItemPlugin;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ItemBlueprintPlugin  extends BaseSpecialItemPlugin {
    transient SpecialItemSpecAPI itemSpec;

    @Override
    public void init(@NotNull CargoStackAPI stack) {
        this.stack = stack;
        String  raw = stack.getSpecialDataIfSpecial().getData();
        itemSpec = Global.getSettings().getSpecialItemSpec(raw);
    }
    @Override
    public boolean shouldRemoveOnRightClickAction() {
        return !AoTDMisc.knowsItem(itemSpec.getId(),Global.getSector().getPlayerFaction());
    }

    @Override
    public boolean hasRightClickAction() {
        return true;
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, CargoTransferHandlerAPI transferHandler, Object stackSource) {
        float opad = 10f;
        Color h = Misc.getHighlightColor();
        Color g = Misc.getGrayColor();
        Color b = Misc.getButtonTextColor();
        tooltip.addTitle(getName());

        String design = itemSpec.getManufacturer();
        Misc.addDesignTypePara(tooltip, design, opad);
        boolean known = AoTDMisc.knowsItem(itemSpec.getId(),Global.getSector().getPlayerFaction());
        if (!spec.getDesc().isEmpty()) {
            Color c = Misc.getGrayColor();;
            tooltip.addPara(spec.getDesc(), c, opad);
        }
        tooltip.addPara("Item: %s",10f,Color.ORANGE,itemSpec.getName());
        tooltip.addPara(itemSpec.getDesc(), opad);
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

        float blX = cx - 25f;
        float blY = cy - 15f;
        float tlX = cx - 20f;
        float tlY = cy + 26f;
        float trX = cx + 23f;
        float trY = cy + 26f;
        float brX = cx + 15f;
        float brY = cy - 18f;

        float blX1 = cx - 20f;
        float blY1 = cy - 12f;
        float tlX1 = cx - 16f;
        float tlY1 = cy + 20.8f;
        float trX1 = cx + 18.4f;
        float trY1 = cy + 20.8f;
        float brX1 = cx + 12f;
        float brY1 = cy - 14.4f;

        boolean known = false;

        float mult = 1f;
        //if (known) mult = 0.5f;

        Color bgColor = Global.getSector().getPlayerFaction().getDarkUIColor();
        bgColor = Misc.setAlpha(bgColor, 255);

        //float b = Global.getSector().getCampaignUI().getSharedFader().getBrightness() * 0.25f;
        renderer.renderBGWithCorners(bgColor, blX, blY, tlX, tlY, trX, trY, brX, brY,
                alphaMult * mult, glowMult * 0.5f * mult, false);
        SpriteAPI sprite = Global.getSettings().getSprite(itemSpec.getIconName());
        sprite.setAlphaMult(alphaMult * mult);
        sprite.setNormalBlend();
        sprite.renderWithCorners(blX1, blY1, tlX1, tlY1, trX1, trY1, brX1, brY1);
        if (glowMult > 0) {
            sprite.setAlphaMult(alphaMult * glowMult * 0.5f * mult);
            sprite.setAdditiveBlend();
            sprite.renderWithCorners(blX1, blY1, tlX1, tlY1, trX1, trY1, brX1, brY1);
        }
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
        int price = (int) (itemSpec.getBasePrice()*2);
        if(price>=2000000){
            price = 2000000;
        }
        return price ;
    }

    @Override
    public void performRightClickAction() {
        if (AoTDMisc.knowsItem(itemSpec.getId(),Global.getSector().getPlayerFaction())) {
            Global.getSector().getCampaignUI().getMessageDisplay().addMessage(
                    "" + itemSpec.getName() + ": blueprint already known");//,
        } else {
            Global.getSoundPlayer().playUISound("ui_acquired_blueprint", 1, 1);
            Global.getSector().getPlayerFaction().getMemory().set("$aotd"+itemSpec.getId(),true);
            Global.getSector().getCampaignUI().getMessageDisplay().addMessage(
                    "Acquired blueprint: " + itemSpec.getName() + "");//,

        }

    }
}
