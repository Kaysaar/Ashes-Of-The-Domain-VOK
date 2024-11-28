package data.kaysaar.aotd.vok.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.SpecialItemSpecAPI;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.InstallableIndustryItemPlugin;
import com.fs.starfarer.api.impl.campaign.econ.impl.BoostIndustryInstallableItemEffect;
import com.fs.starfarer.api.impl.campaign.econ.impl.ItemEffectsRepo;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.campaign.econ.items.ModularConstructorPlugin;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.fs.starfarer.api.impl.campaign.econ.impl.ItemEffectsRepo.PRISTINE_NANOFORGE_PROD;
import static com.fs.starfarer.api.impl.campaign.econ.impl.ItemEffectsRepo.PRISTINE_NANOFORGE_QUALITY_BONUS;

public class AoTDSpecialItemRepo {
    public void putInfoForSpecialItems() {
        ItemEffectsRepo.ITEM_EFFECTS.put(Items.MANTLE_BORE, new BoostIndustryInstallableItemEffect(
                Items.MANTLE_BORE, ItemEffectsRepo.MANTLE_BORE_MINING_BONUS, 0) {
            protected void addItemDescriptionImpl(Industry industry, TooltipMakerAPI text, SpecialItemData data,
                                                  InstallableIndustryItemPlugin.InstallableItemDescriptionMode mode, String pre, float pad) {
                List<String> commodities = new ArrayList<String>();
                for (String curr : ItemEffectsRepo.MANTLE_BORE_COMMODITIES) {
                    CommoditySpecAPI c = Global.getSettings().getCommoditySpec(curr);
                    commodities.add(c.getName().toLowerCase());
                }
                text.addPara(pre + "Increases " + Misc.getAndJoined(commodities) + " production by %s units.",
                        pad, Misc.getHighlightColor(),
                        "" + ItemEffectsRepo.MANTLE_BORE_MINING_BONUS);
            }


            @Override
            public String[] getSimpleReqs(Industry industry) {
                return new String[]{"does not have extreme weather","is not a gas giant"};
            }

            @Override
            public List<String> getRequirements(Industry industry) {
                return super.getRequirements(industry);
            }
        });

        ItemEffectsRepo.ITEM_EFFECTS.put("omega_processor", new BoostIndustryInstallableItemEffect(
                "omega_processor", 0, 0) {
            protected void addItemDescriptionImpl(Industry industry, TooltipMakerAPI text, SpecialItemData data,
                                                  InstallableIndustryItemPlugin.InstallableItemDescriptionMode mode, String pre, float pad) {
                List<String> commodities = new ArrayList<String>();
                for (String curr : ItemEffectsRepo.MANTLE_BORE_COMMODITIES) {
                    CommoditySpecAPI c = Global.getSettings().getCommoditySpec(curr);
                    commodities.add(c.getName().toLowerCase());
                }
                text.addPara(pre + "Unlocks the Experimental tier of Tech Tree",
                        pad);
            }
        });
        ItemEffectsRepo.ITEM_EFFECTS.put("turing_engine", new BoostIndustryInstallableItemEffect(
                "turing_engine", 0, 0) {
            protected void addItemDescriptionImpl(Industry industry, TooltipMakerAPI text, SpecialItemData data,
                                                  InstallableIndustryItemPlugin.InstallableItemDescriptionMode mode, String pre, float pad) {
                List<String> commodities = new ArrayList<String>();
                text.addPara("Increase production of advanced components by %s",5f,Color.ORANGE,""+6);
                text.addPara( "Gain ability to produce AI cores.",
                        pad);
            }

            @Override
            public void apply(Industry industry) {
                industry.getSupply(AoTDCommodities.ADVANCED_COMPONENTS).getQuantity().modifyFlat("aotd_turing",6);
            }

            @Override
            public void unapply(Industry industry) {
                industry.getSupply(AoTDCommodities.ADVANCED_COMPONENTS).getQuantity().unmodifyFlat("aotd_turing");
            }
        });
        ItemEffectsRepo.CORONAL_TAP_RANGE = "Coronal Network Center in 10 LY radius, 50 LY if Wormhole Stabilizer has been repaired.";
        ItemEffectsRepo.CORONAL_TAP_INDUSTRIES = 0;
        ItemEffectsRepo.CORONAL_TAP_TRANSPLUTONICS = 0;
        ItemEffectsRepo.ITEM_EFFECTS.put(Items.CORONAL_PORTAL, new BoostIndustryInstallableItemEffect(
                Items.CORONAL_PORTAL, ItemEffectsRepo.CORONAL_TAP_TRANSPLUTONICS, 0) {
            protected void addItemDescriptionImpl(Industry industry, TooltipMakerAPI text, SpecialItemData data,
                                                  InstallableIndustryItemPlugin.InstallableItemDescriptionMode mode, String pre, float pad) {
                text.addPara(pre + "Increases the maximum number of industries for all colonies in the system by %s, and increases production of all industries in the system by %s units when demand for " +
                                "%s units of transplutonics is fully met.",
                        pad, Misc.getHighlightColor(),
                        "" + (int) 1, "" + (int) 1,
                        "" + (int) 10);
            }

        });
        Global.getSettings().getSpecialItemSpec(Items.CORONAL_PORTAL).setParams("coronal_pylon");
    }

    public void setSpecialItemNewIndustries(String specialItemID, String listOfAdditionalIndustries) {
        SpecialItemSpecAPI spec = Global.getSettings().getSpecialItemSpec(specialItemID);
        String prevParams = spec.getParams();
        if (prevParams.contains(listOfAdditionalIndustries)) return;
        spec.setParams(prevParams + "," + listOfAdditionalIndustries);
    }
}
