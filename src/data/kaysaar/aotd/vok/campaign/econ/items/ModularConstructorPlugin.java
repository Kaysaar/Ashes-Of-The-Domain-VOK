package data.kaysaar.aotd.vok.campaign.econ.items;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.CargoTransferHandlerAPI;
import com.fs.starfarer.api.campaign.SpecialItemSpecAPI;
import com.fs.starfarer.api.campaign.impl.items.BaseSpecialItemPlugin;
import com.fs.starfarer.api.campaign.impl.items.BlueprintProviderItem;
import com.fs.starfarer.api.campaign.impl.items.GenericSpecialItemPlugin;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.ui.P;
import org.json.JSONException;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ModularConstructorPlugin extends GenericSpecialItemPlugin {

    protected IndustrySpecAPI industrySpecAPI;

    @Override
    public void init(CargoStackAPI stack) {
        super.init(stack);
        industrySpecAPI = Global.getSettings().getIndustrySpec(this.getSpec().getId().split("_")[2]);
    }

    @Override
    public void render(float x, float y, float w, float h, float alphaMult,
                       float glowMult, SpecialItemRendererAPI renderer) {
        if(industrySpecAPI!=null){
            float cx = x + w / 2f;
            float cy = y + h / 2f;

            float blX = cx - 34f;
            float blY = cy + 17f;

            float tlX = cx - 34f;
            float tlY = cy + 30f;

            float trX = cx - 5;
            float trY = cy + 30f;

            float brX = cx - 5f;
            float brY = cy + 17;

            SpriteAPI sprite = Global.getSettings().getSprite(industrySpecAPI.getImageName());


            float mult = 1f;

            sprite.setAlphaMult(alphaMult * mult);
            sprite.setNormalBlend();
            sprite.renderWithCorners(blX, blY, tlX, tlY, trX, trY, brX, brY);

            if (glowMult > 0) {
                sprite.setAlphaMult(alphaMult * glowMult * 0.5f * mult);
                sprite.setAdditiveBlend();
                sprite.renderWithCorners(blX, blY, tlX, tlY, trX, trY, brX, brY);
            }

            renderer.renderScanlinesWithCorners(blX, blY, tlX, tlY, trX, trY, brX, brY, alphaMult, false);
        }

    }

    @Override
    public String getName() {
        return super.getName();
    }
    public static String retrieveNameForReq(String industryId){
        for (SpecialItemSpecAPI allSpecialItemSpec : Global.getSettings().getAllSpecialItemSpecs()) {
            if(allSpecialItemSpec.getId().equals("modular_constructor_"+industryId)){
                return allSpecialItemSpec.getName();
            }
        }
        return "";
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, CargoTransferHandlerAPI transferHandler, Object stackSource) {
        super.createTooltip(tooltip, expanded, transferHandler, stackSource);
        tooltip.addSectionHeading("Constructor capabilities:", Alignment.MID, 10f);
        if(industrySpecAPI!=null){
            tooltip.addPara("This constructor is capable of upgrading %s into %s.", 10f, Color.ORANGE, retriveUpgradeFrom(),retrieveIndustries(this.industrySpecAPI) );
            tooltip.addPara("Urgent! After industry's upgrade is started, this item is irreversibly consumed!", Misc.getNegativeHighlightColor(),10f);
        }
        else{
            tooltip.addPara("This constructor's data rack is empty. We can insert our current data into it, to suit our needs.", Misc.getTooltipTitleAndLightHighlightColor(),10f);
        }
        addCostLabel(tooltip, 10f, transferHandler, stackSource);

    }


    public String retrieveTypeOfConstructor() {
        if (industrySpecAPI != null) {
            for (IndustrySpecAPI allIndustrySpec : Global.getSettings().getAllIndustrySpecs()) {
                String branch = "";
                boolean consumes = false;
                for (String tag : allIndustrySpec.getTags()) {
                    if (tag.contains("consumes")) {
                            consumes = true;
                    }
                    if (tag.contains("aotd")) {
                        branch = tag.split("_")[1].trim();
                    }
                }
                if (!branch.isEmpty() && consumes) {
                    return Global.getSettings().getIndustrySpec(branch).getId();
                }
            }

        }
        return "";
    }

    @Override
    public String resolveDropParamsToSpecificItemData(String params, Random random) throws JSONException {
        return super.resolveDropParamsToSpecificItemData(params, random);
    }

    public String retriveUpgradeFrom() {
        if (industrySpecAPI != null) {
            return industrySpecAPI.getName();
        }
        return "";
    }
    public static  String retrieveIndustries(IndustrySpecAPI industrySpecAPI) {
        if (industrySpecAPI != null) {
            String all = "";
            ArrayList<String>industries = new ArrayList<>();
            for (IndustrySpecAPI allIndustrySpec : Global.getSettings().getAllIndustrySpecs()) {
                String branch = "";
                boolean consumes = false;
                int i = 0;
                for (String tag : allIndustrySpec.getTags()) {
                    if (tag.contains("consumes")) {
                        if (tag.contains(industrySpecAPI.getId())) {
                            industries.add( allIndustrySpec.getName());
                        }
                    }
                }

            }
            int i =0;
            for (String industry : industries) {
                if(i+1>=industries.size()){
                    all += industry;
                }
                else{
                    all += industry + ", ";
                }

                i++;
            }
            return all;
        }
        return "";
    }
}
