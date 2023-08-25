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

            String petId = stack.getSpecialDataIfSpecial().getData();

            if (petId == null) return;

            String imageName = Global.getSettings().getIndustrySpec(industryId).getImageName();
            SpriteAPI sprite = Global.getSettings().getSprite(imageName);
            float dim = 30f;

            Color bgColor = Global.getSector().getPlayerFaction().getDarkUIColor();
            bgColor = Misc.setAlpha(bgColor, 255);
            float pad = 10f;

            y = y + dim + pad;
            x = x + w - dim - pad;

            float blX = x;
            float blY = y - dim;
            float tlX = x;
            float tlY = y;
            float trX = x + dim;
            float trY = y;
            float brX = x + dim;
            float brY = y - dim;

            renderer.renderBGWithCorners(bgColor, blX - 1, blY - 1, tlX - 1, tlY + 1, trX + 1, trY + 1, brX + 1, brY - 1, 1f, 0f, false);
            sprite.renderWithCorners(blX, blY, tlX, tlY, trX, trY, brX, brY);
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
            return industry.getName() + "VOK Databank";
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
       return false;
    }

}
