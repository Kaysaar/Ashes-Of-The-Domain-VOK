package data.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.CargoTransferHandlerAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.campaign.impl.items.BaseSpecialItemPlugin;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AoDDatabank extends BaseSpecialItemPlugin{
    protected IndustrySpecAPI industry;
    public String industryId;

    @Override
    public void init(CargoStackAPI stack) {
        super.init(stack);
        String [] params = spec.getParams().split(",");
        for(String param: params){
            param = param.trim();
            industryId = param;
            break;
        }
        industry = Global.getSettings().getIndustrySpec(industryId);
        stack.getSpecialDataIfSpecial().setData(industryId);
    }

    public List<String> getProvidedFighters() {
        return null;
    }

    public List<String> getProvidedShips() {
        return null;
    }

    public List<String> getProvidedWeapons() {
        return null;
    }
    public List<String> getProvidedIndustries() {
        List<String> result = new ArrayList<String>();
        result.add(industry.getId());
        return result;
    }


    @Override
    public void render(float x, float y, float w, float h, float alphaMult,
                       float glowMult, SpecialItemRendererAPI renderer) {
        float cx = x + w/2f;
        float cy = y + h/2f;

        float blX = cx -25f;
        float blY = cy -14f;
        float tlX = cx -30f;
        float tlY = cy +16f;
        float trX = cx +24f;
        float trY = cy +22f;
        float brX = cx +30f;
        float brY = cy -6f;

        SpriteAPI sprite = Global.getSettings().getSprite(industry.getImageName());

        String industryId = this.industryId;
        boolean known = Global.getSector().getPlayerFaction().knowsIndustry(industryId);

        float mult = 1f;

        sprite.setAlphaMult(alphaMult * mult);
        sprite.setNormalBlend();
        sprite.renderWithCorners(blX, blY, tlX, tlY, trX, trY, brX, brY);

        if (glowMult > 0) {
            sprite.setAlphaMult(alphaMult * glowMult * 0.5f * mult);
            sprite.setAdditiveBlend();
            sprite.renderWithCorners(blX, blY, tlX, tlY, trX, trY, brX, brY);
        }

        if (known) {
            renderer.renderBGWithCorners(Color.black, blX, blY, tlX, tlY, trX, trY, brX, brY,
                    alphaMult * 0.5f, 0f, false);
        }

        renderer.renderScanlinesWithCorners(blX, blY, tlX, tlY, trX, trY, brX, brY, alphaMult, false);
    }

    @Override
    public int getPrice(MarketAPI market, SubmarketAPI submarket) {
        if (industry != null) {
            float base = super.getPrice(market, submarket);
            return (int)(base + industry.getCost() * getItemPriceMult());
        }
        return super.getPrice(market, submarket);
    }

    @Override
    public String getName() {
        if (industry != null) {
            return industry.getName() + " Industry Databank";
        }
        return super.getName();
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, CargoTransferHandlerAPI transferHandler, Object stackSource) {
        super.createTooltip(tooltip, expanded, transferHandler, stackSource);

        float pad = 3f;
        float opad = 10f;
        float small = 5f;
        Color h = Misc.getHighlightColor();
        Color g = Misc.getGrayColor();
        Color b = Misc.getButtonTextColor();
        b = Misc.getPositiveHighlightColor();

        String industryId = stack.getSpecialDataIfSpecial().getData();
        boolean known = Global.getSector().getPlayerFaction().knowsIndustry(industryId);

        tooltip.addPara(industry.getDesc(), opad);

        addCostLabel(tooltip, opad, transferHandler, stackSource);

    }

    @Override
    public boolean hasRightClickAction() {
        return false;
    }

    @Override
    public boolean shouldRemoveOnRightClickAction() {
        String industryId = stack.getSpecialDataIfSpecial().getData();
        return !Global.getSector().getPlayerFaction().knowsIndustry(industryId);
    }

}
