package data.kaysaar.aotd.vok.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.SpecialItemSpecAPI;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.InstallableIndustryItemPlugin;
import com.fs.starfarer.api.impl.campaign.econ.impl.BoostIndustryInstallableItemEffect;
import com.fs.starfarer.api.impl.campaign.econ.impl.ItemEffectsRepo;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.Ids.AoTDItems;
import data.kaysaar.aotd.vok.campaign.econ.industry.MiscHiddenIndustry;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AoTDSpecialItemRepo {
    public void putInfoForSpecialItems() {

        ItemEffectsRepo.ITEM_EFFECTS.put(AoTDItems.HYPERDIMENSIONAL_PROCESSOR, new BoostIndustryInstallableItemEffect(
                AoTDItems.HYPERDIMENSIONAL_PROCESSOR, 0, 0) {
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
        ItemEffectsRepo.ITEM_EFFECTS.put(AoTDItems.TURING_ENGINE, new BoostIndustryInstallableItemEffect(
                AoTDItems.TURING_ENGINE, 0, 0) {
            protected void addItemDescriptionImpl(Industry industry, TooltipMakerAPI text, SpecialItemData data,
                                                  InstallableIndustryItemPlugin.InstallableItemDescriptionMode mode, String pre, float pad) {
                List<String> commodities = new ArrayList<String>();
                text.addPara("Increase production of advanced components by %s",5f,Color.ORANGE,""+6);
                text.addPara( "Gain the ability to produce AI cores.",
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

            }

        });
        ItemEffectsRepo.ITEM_EFFECTS.put(AoTDItems.TENEBRIUM_NANOFORGE, new BoostIndustryInstallableItemEffect(
                AoTDItems.TENEBRIUM_NANOFORGE, 6, 0) {
            public void apply(Industry industry) {
                super.apply(industry);
                industry.getMarket().getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD)
                        .modifyFlat("nanoforge", 0.8f, Misc.ucFirst(spec.getName().toLowerCase()));
                MiscHiddenIndustry.getInstance(industry.getMarket()).getPatherInterestManipulator().modifyFlat("tenebrium_nanoforge",12,"Abyss-Tech");

            }
            public void unapply(Industry industry) {
                super.unapply(industry);
                industry.getMarket().getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).unmodifyFlat("nanoforge");
                MiscHiddenIndustry.getInstance(industry.getMarket()).getPatherInterestManipulator().unmodifyFlat("tenebrium_nanoforge");

            }
            protected void addItemDescriptionImpl(Industry industry, TooltipMakerAPI text, SpecialItemData data,
                                                  InstallableIndustryItemPlugin.InstallableItemDescriptionMode mode, String pre, float pad) {
                String heavyIndustry ="";
                if(industry!=null) {
                    heavyIndustry = industry.getCurrentName()+" ";
                }
                if (mode == InstallableIndustryItemPlugin.InstallableItemDescriptionMode.MANAGE_ITEM_DIALOG_LIST) {
                    heavyIndustry = "";
                }
                text.addPara(pre + "Increases ship and weapon production quality by %s. " +
                                "Increases " + heavyIndustry + "production by %s units." +
                                "Causes very high Pather interest.",
                        pad, Misc.getHighlightColor(),
                        "" + (int) Math.round(0.8f * 100f) + "%",
                        "" + (int) 6);
            }

        });
        ItemEffectsRepo.ITEM_EFFECTS.put(AoTDItems.TENEBRIUM_SYNCHROTRON, new BoostIndustryInstallableItemEffect(
                AoTDItems.TENEBRIUM_SYNCHROTRON, 0, 0) {
            public void apply(Industry industry) {
                super.apply(industry);
                MiscHiddenIndustry.getInstance(industry.getMarket()).getPatherInterestManipulator().modifyFlat("tenebrium_synchotron",18,"Abyss-Tech");
                if(industry.getSupply(Commodities.FUEL).getQuantity().getModifiedInt()>0){
                    industry.getSupply(Commodities.FUEL).getQuantity().modifyFlat("tenebrium_synchotron",6,"Tenebrium Synchrotron");
                }
                if(industry.getSupply(AoTDCommodities.COMPOUND).getQuantity().getModifiedInt()>0){
                    industry.getSupply(AoTDCommodities.COMPOUND).getQuantity().modifyFlat("tenebrium_synchotron",4,"Tenebrium Synchrotron");
                }
            }
            public void unapply(Industry industry) {
                super.unapply(industry);
                MiscHiddenIndustry.getInstance(industry.getMarket()).getPatherInterestManipulator().unmodifyFlat("tenebrium_synchotron");
                industry.getSupply(Commodities.FUEL).getQuantity().unmodifyFlat("tenebrium_synchotron");
                industry.getSupply(AoTDCommodities.COMPOUND).getQuantity().unmodifyFlat("tenebrium_synchotron");

            }
            protected void addItemDescriptionImpl(Industry industry, TooltipMakerAPI text, SpecialItemData data,
                                                  InstallableIndustryItemPlugin.InstallableItemDescriptionMode mode, String pre, float pad) {

                text.addPara(pre  +
                                "Increases "  + "production of %s by %s units." +
                                "Increases "  + "production of %s by %s units." +
                                "Causes very high Pather interest.",
                        pad, Misc.getHighlightColor(),
                        "Fuel",
                        "" + (int) 6,"Compound",""+4);
            }

        });
        ItemEffectsRepo.ITEM_EFFECTS.put(AoTDItems.TENEBRIUM_ATMOSPHERIC_DRIVE, new BoostIndustryInstallableItemEffect(
                AoTDItems.TENEBRIUM_ATMOSPHERIC_DRIVE, 3, 0) {
            public void apply(Industry industry) {
                super.apply(industry);
                MiscHiddenIndustry.getInstance(industry.getMarket()).getPatherInterestManipulator().modifyFlat(AoTDItems.TENEBRIUM_ATMOSPHERIC_DRIVE,25,"Abyss-Tech");
                industry.getMarket().suppressCondition(Conditions.EXTREME_WEATHER);
            }
            public void unapply(Industry industry) {
                super.unapply(industry);
                MiscHiddenIndustry.getInstance(industry.getMarket()).getPatherInterestManipulator().unmodifyFlat(AoTDItems.TENEBRIUM_ATMOSPHERIC_DRIVE);
                industry.getMarket().unsuppressCondition(Conditions.EXTREME_WEATHER);
            }
            protected void addItemDescriptionImpl(Industry industry, TooltipMakerAPI text, SpecialItemData data,
                                                  InstallableIndustryItemPlugin.InstallableItemDescriptionMode mode, String pre, float pad) {

                String heavyIndustry ="";
                if(industry!=null) {
                    heavyIndustry = industry.getCurrentName()+" ";
                }
                if (mode == InstallableIndustryItemPlugin.InstallableItemDescriptionMode.MANAGE_ITEM_DIALOG_LIST) {
                    heavyIndustry = "";
                }
                text.addPara("Increases " + heavyIndustry + "production by %s units." +
                                "Causes very high Pather interest."+
                                "Counters the effects of %s",
                        pad, Misc.getHighlightColor(),
                        "3", "Extreme Weather");


            }

        });
        ItemEffectsRepo.ITEM_EFFECTS.put(AoTDItems.TENEBRIUM_CATALYTIC_CORE, new BoostIndustryInstallableItemEffect(
                AoTDItems.TENEBRIUM_CATALYTIC_CORE, 3, 0) {

            public void apply(Industry industry) {
                super.apply(industry);
                MiscHiddenIndustry.getInstance(industry.getMarket()).getPatherInterestManipulator().modifyFlat(AoTDItems.TENEBRIUM_ATMOSPHERIC_DRIVE,20,"Abyss-Tech");

            }
            public void unapply(Industry industry) {
                super.unapply(industry);
                MiscHiddenIndustry.getInstance(industry.getMarket()).getPatherInterestManipulator().unmodifyFlat(AoTDItems.TENEBRIUM_ATMOSPHERIC_DRIVE);

            }
            protected void addItemDescriptionImpl(Industry industry, TooltipMakerAPI text, SpecialItemData data,
                                                  InstallableIndustryItemPlugin.InstallableItemDescriptionMode mode, String pre, float pad) {

                String heavyIndustry ="";
                if(industry!=null) {
                    heavyIndustry = industry.getCurrentName()+" ";
                }
                if (mode == InstallableIndustryItemPlugin.InstallableItemDescriptionMode.MANAGE_ITEM_DIALOG_LIST) {
                    heavyIndustry = "";
                }
                text.addPara("Increases " + heavyIndustry + "production by %s units." +
                                "Causes very high Pather interest.",
                        pad, Misc.getHighlightColor(),
                        "3");


            }

        });
    }

    public void setSpecialItemNewIndustries(String specialItemID, String listOfAdditionalIndustries) {
        SpecialItemSpecAPI spec = Global.getSettings().getSpecialItemSpec(specialItemID);
        String prevParams = spec.getParams();
        if (prevParams.contains(listOfAdditionalIndustries)) return;
        spec.setParams(prevParams + "," + listOfAdditionalIndustries);
    }
    public void absoluteSetItemParams(String specialItemID, String replacement) {
        SpecialItemSpecAPI spec = Global.getSettings().getSpecialItemSpec(specialItemID);
        spec.setParams(replacement);
    }
}
