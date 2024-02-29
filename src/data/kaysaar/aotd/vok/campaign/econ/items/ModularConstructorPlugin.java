package data.kaysaar.aotd.vok.campaign.econ.items;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.CargoTransferHandlerAPI;
import com.fs.starfarer.api.campaign.SpecialItemSpecAPI;
import com.fs.starfarer.api.campaign.impl.items.BaseSpecialItemPlugin;
import com.fs.starfarer.api.campaign.impl.items.BlueprintProviderItem;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.ui.P;
import org.json.JSONException;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class ModularConstructorPlugin extends BaseSpecialItemPlugin implements BlueprintProviderItem {

    protected SpecialItemSpecAPI itemSpecAPI;
    protected IndustrySpecAPI industrySpecAPI;

    @Override
    public void init(CargoStackAPI stack) {
        super.init(stack);
        itemSpecAPI = Global.getSettings().getSpecialItemSpec(stack.getSpecialDataIfSpecial().getData());
        industrySpecAPI = Global.getSettings().getIndustrySpec(retrieveTypeOfConstructor());
    }

    @Override
    public void render(float x, float y, float w, float h, float alphaMult,
                       float glowMult, SpecialItemRendererAPI renderer) {
        if(itemSpecAPI!=null){
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
        if (itemSpecAPI != null) {
            return super.getName() + " : " + Global.getSettings().getIndustrySpec(retrieveTypeOfConstructor()).getName() + " Type";
        }
        return super.getName();
    }
    public static void  setConstructorKnowledge(ModularConstructorPlugin constructor,String itemId){
        constructor.itemSpecAPI = Global.getSettings().getSpecialItemSpec(itemId);
        constructor.industrySpecAPI = Global.getSettings().getIndustrySpec(constructor.retrieveTypeOfConstructor());
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, CargoTransferHandlerAPI transferHandler, Object stackSource) {
        super.createTooltip(tooltip, expanded, transferHandler, stackSource);
        tooltip.addSectionHeading("Constructor capabilities:", Alignment.MID, 10f);
        if(itemSpecAPI!=null){
            tooltip.addPara("This constructor is capable of upgrading %s into %s", 10f, Color.ORANGE, retriveUpgradeFrom(),retrieveIndustries() );
            tooltip.addPara("Note! After start of industry's upgrade, item is irreversibly consumed!", Misc.getNegativeHighlightColor(),10f);
        }
        else{
            tooltip.addPara("This constructor's data rack is empty. We can fill it with our current data, to suit our needs", Misc.getTooltipTitleAndLightHighlightColor(),10f);
        }
        addCostLabel(tooltip, 10f, transferHandler, stackSource);

    }

    @Override
    public List<String> getProvidedShips() {
        return null;
    }

    @Override
    public List<String> getProvidedWeapons() {
        return null;
    }

    @Override
    public List<String> getProvidedFighters() {
        return null;
    }

    @Override
    public List<String> getProvidedIndustries() {
        return null;
    }

    public String retrieveTypeOfConstructor() {
        if (itemSpecAPI != null) {
            for (IndustrySpecAPI allIndustrySpec : Global.getSettings().getAllIndustrySpecs()) {
                String branch = "";
                boolean consumes = false;
                for (String tag : allIndustrySpec.getTags()) {
                    if (tag.contains("consumes")) {
                        if (tag.contains(itemSpecAPI.getId())) {
                            consumes = true;
                        }
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
        if (itemSpecAPI != null) {
            for (IndustrySpecAPI allIndustrySpec : Global.getSettings().getAllIndustrySpecs()) {
                String branch = "";
                boolean consumes = false;
                for (String tag : allIndustrySpec.getTags()) {
                    if (tag.contains("consumes")) {
                        if (tag.contains(itemSpecAPI.getId())) {
                           return Global.getSettings().getIndustrySpec(tag.split(":")[1]).getName();
                        }
                    }
                }
            }

        }
        return "";
    }
    public String retrieveIndustries() {
        if (itemSpecAPI != null) {
            String all = "";
            for (IndustrySpecAPI allIndustrySpec : Global.getSettings().getAllIndustrySpecs()) {
                String branch = "";
                boolean consumes = false;
                for (String tag : allIndustrySpec.getTags()) {
                    if (tag.contains("consumes")) {
                        if (tag.contains(itemSpecAPI.getId())) {
                            all += allIndustrySpec.getName() + " ";
                        }
                    }
                }

            }
            return all;
        }
        return "";
    }
}
