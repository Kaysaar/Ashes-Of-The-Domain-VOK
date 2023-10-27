package data.kaysaar_aotd_vok.scripts.research.items;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.CargoTransferHandlerAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.campaign.impl.items.BaseSpecialItemPlugin;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class VoKDatabank extends BaseSpecialItemPlugin{
    protected IndustrySpecAPI industry;
    public String industryId;
    public VoKDatabankType type;

    @Override
    public void init(CargoStackAPI stack) {
        super.init(stack);
        type = getType(itemId);
        industryId = stack.getSpecialDataIfSpecial().getData();
        industry = Global.getSettings().getIndustrySpec(industryId);

    }
    @Override
    public void render(float x, float y, float w, float h, float alphaMult,
                       float glowMult, SpecialItemRendererAPI renderer) {
            if(industryId!=null){
                SpriteAPI sprite = Global.getSettings().getSprite(industry.getImageName());
                float dim = 30f;

                Color bgColor = Global.getSector().getPlayerFaction().getDarkUIColor();
                bgColor = Misc.setAlpha(bgColor, 255);
                float pad = 10f;

                y = y + dim + pad;
                x = x + w - dim - pad;

                float blX = x-20;
                float blY = y - dim;
                float tlX = x-20;
                float tlY = y;
                float trX = x + dim;
                float trY = y;
                float brX = x + dim;
                float brY = y - dim;

                renderer.renderBGWithCorners(bgColor, blX - 1, blY - 1, tlX - 1, tlY + 1, trX + 1, trY + 1, brX + 1, brY - 1, 1f, 0f, false);
                sprite.renderWithCorners(blX, blY, tlX, tlY, trX, trY, brX, brY);
            }


    }

    @Override
    public int getPrice(MarketAPI market, SubmarketAPI submarket) {
        if (industry != null) {
            float base = super.getPrice(market, submarket);
            return (int)(base + industry.getCost() * getItemPriceMult()/(float)(type.ordinal()+1));
        }
        return super.getPrice(market, submarket);
    }

    @Override
    public String getName() {
        if (industry != null) {
            return industry.getName() + " VOK Databank";
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
        tooltip.addSectionHeading("Industry Info", Alignment.MID,10f);
        tooltip.addPara(industry.getDesc(), opad);
        tooltip.addSectionHeading("Databank Status", Alignment.MID,10f);
        tooltip.addPara("This databank conditions is : %s\nBecause of this, " +
                "it gives %s research speed towards %s",10f,Color.ORANGE,""+getTypeAsString(type),""+getPercentFromType(type),""+industry.getName());


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
    public VoKDatabankType getType(String type){
        if(type.equals("aotd_vok_databank_pristine")){
            return VoKDatabankType.PRISTINE;
        }
        if(type.equals("aotd_vok_databank_decayed")){
            return VoKDatabankType.DECAYED;
        }
        if(type.equals("aotd_vok_databank_damaged")){
            return VoKDatabankType.DESTROYED;
        }
        return VoKDatabankType.DESTROYED;
    }
    public String getTypeAsString(VoKDatabankType type){
        if(type.equals(VoKDatabankType.DECAYED)){
            return "Decayed";
        }
        if(type.equals(VoKDatabankType.PRISTINE)){
            return "Pristine";
        }
        return "Destroyed";
    }
    public String getPercentFromType(VoKDatabankType type){
        if(type.equals(VoKDatabankType.DECAYED)){
            return "0%";
        }
        if(type.equals(VoKDatabankType.PRISTINE)){
            return "50%";
        }
        return "-50%";
    }

}
