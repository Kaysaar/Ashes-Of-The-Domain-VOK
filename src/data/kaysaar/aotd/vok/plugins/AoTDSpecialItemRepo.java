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
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.items.ModularConstructorPlugin;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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
        ItemEffectsRepo.ITEM_EFFECTS.put("modular_constructor_refining", new BoostIndustryInstallableItemEffect(
                "modular_constructor_refining", 0, 0) {
            protected void addItemDescriptionImpl(Industry industry, TooltipMakerAPI text, SpecialItemData data,
                                                  InstallableIndustryItemPlugin.InstallableItemDescriptionMode mode, String pre, float pad) {
                text.addPara(pre + "Allows upgrading %s into : %s " ,10f, Color.ORANGE,
                        "Refining",ModularConstructorPlugin.retrieveIndustries(Global.getSettings().getIndustrySpec(Industries.REFINING)));
            }
        });
        ItemEffectsRepo.ITEM_EFFECTS.put("modular_constructor_mining", new BoostIndustryInstallableItemEffect(
                "modular_constructor_mining", 0, 0) {
            protected void addItemDescriptionImpl(Industry industry, TooltipMakerAPI text, SpecialItemData data,
                                                  InstallableIndustryItemPlugin.InstallableItemDescriptionMode mode, String pre, float pad) {
                text.addPara(pre + "Allows upgrading %s into : %s " ,10f, Color.ORANGE,
                        "Mining",ModularConstructorPlugin.retrieveIndustries(Global.getSettings().getIndustrySpec(Industries.MINING)));
            }
        });
        ItemEffectsRepo.ITEM_EFFECTS.put("modular_constructor_orbitalworks", new BoostIndustryInstallableItemEffect(
                "modular_constructor_orbitalworks", 0, 0) {
            protected void addItemDescriptionImpl(Industry industry, TooltipMakerAPI text, SpecialItemData data,
                                                  InstallableIndustryItemPlugin.InstallableItemDescriptionMode mode, String pre, float pad) {
                text.addPara(pre + "Allows upgrading %s into : %s " ,10f, Color.ORANGE,
                        "Orbital Works",ModularConstructorPlugin.retrieveIndustries(Global.getSettings().getIndustrySpec(Industries.ORBITALWORKS)));

            }
        });

        ItemEffectsRepo.ITEM_EFFECTS.put(Items.CATALYTIC_CORE, new BoostIndustryInstallableItemEffect(
                Items.CATALYTIC_CORE, ItemEffectsRepo.CATALYTIC_CORE_BONUS, 0) {

            protected void addItemDescriptionImpl(Industry industry, TooltipMakerAPI text, SpecialItemData data,
                                                  InstallableIndustryItemPlugin.InstallableItemDescriptionMode mode, String pre, float pad) {
                text.addPara(pre + "Increases refining's production by %s units.",
                        pad, Misc.getHighlightColor(),
                        "" + (int) ItemEffectsRepo.CATALYTIC_CORE_BONUS);
            }

            @Override
            public String[] getSimpleReqs(Industry industry) {
                return new String[]{"does not have extreme weather", "does not have extreme tectonic activity"};
            }
        });
        ItemEffectsRepo.ITEM_EFFECTS.put("modular_constructor_fuelprod", new BoostIndustryInstallableItemEffect(
                "modular_constructor_fuelprod", 0, 0) {
            protected void addItemDescriptionImpl(Industry industry, TooltipMakerAPI text, SpecialItemData data,
                                                  InstallableIndustryItemPlugin.InstallableItemDescriptionMode mode, String pre, float pad) {
                text.addPara(pre + "Allows upgrading %s into : %s " ,10f, Color.ORANGE,
                        "Fuel Production",ModularConstructorPlugin.retrieveIndustries(Global.getSettings().getIndustrySpec(Industries.ORBITALWORKS)));

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
