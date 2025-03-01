package data.kaysaar.aotd.vok.campaign.econ.listeners;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.listeners.DialogCreatorUI;
import com.fs.starfarer.api.campaign.listeners.IndustryOptionProvider;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.util.List;

public class NidavelirIndustryOptionProvider implements IndustryOptionProvider {
    public static Object NIDAVELIR = new Object();
    @Override
    public List<IndustryOptionData> getIndustryOptions(Industry ind) {
      return null;
    }

    @Override
    public void createTooltip(IndustryOptionData opt, TooltipMakerAPI tooltip, float width) {

    }

    @Override
    public void optionSelected(IndustryOptionData opt, DialogCreatorUI ui) {


    }

    @Override
    public void addToIndustryTooltip(Industry ind, Industry.IndustryTooltipMode mode, TooltipMakerAPI tooltip, float width, boolean expanded) {
//        if(ind instanceof HeavyIndustry){
//            if (ind.getSupply(Commodities.SHIPS).getQuantity().getFlatStatMod(MarketProductionNode.source)!=null) {
//                Color color = ind.getMarket().getFaction().getBaseUIColor();
//                Color dark = ind.getMarket().getFaction().getDarkUIColor();
//                Color grid = ind.getMarket().getFaction().getGridUIColor();
//                Color bright = ind.getMarket().getFaction().getBrightUIColor();
//
//                Color gray = Misc.getGrayColor();
//                Color highlight = Misc.getHighlightColor();
//                Color bad = Misc.getNegativeHighlightColor();
//                float maxIconsPerRow = 10f;
//                float opad = 10f;
//                int rows;
//                tooltip.addSectionHeading("Global Production Contribution", color, dark, Alignment.MID, opad);
//                tooltip.beginIconGroup();
//                tooltip.setIconSpacingMedium();
//                ReflectionUtilis reflection  = new ReflectionUtilis();
//                float icons = 0;
//                Map<String, MutableCommodityQuantity> retrivedRawSupply = (Map<String, MutableCommodityQuantity>) reflection.getPrivateVariableFromSuperClass("supply",ind);
//                for (MutableCommodityQuantity curr :retrivedRawSupply.values()) {
//                    if(curr.getQuantity().getFlatStatMod(MarketProductionNode.source)==null)continue;
//
//                    int qty = (int) -curr.getQuantity().getFlatStatMod(MarketProductionNode.source).value;
//                    //if (qty <= 0) continue;
//                    int normal = qty;
//                    if (normal > 0) {
//                        tooltip.addIcons(ind.getMarket().getCommodityData(curr.getCommodityId()), normal, IconRenderMode.NORMAL);
//                    }
//
//                    icons += normal;
//
//                    rows = (int) Math.ceil(icons / maxIconsPerRow);
//                }
//
//
//                rows = 3;
//                tooltip.addIconGroup(32, rows, opad);
//        }
//    }
    }
}
