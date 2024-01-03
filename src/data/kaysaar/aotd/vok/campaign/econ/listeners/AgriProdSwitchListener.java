package data.kaysaar.aotd.vok.campaign.econ.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCustomDialogDelegate;
import com.fs.starfarer.api.campaign.CustomDialogDelegate;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MutableCommodityQuantity;
import com.fs.starfarer.api.campaign.listeners.BaseIndustryOptionProvider;
import com.fs.starfarer.api.campaign.listeners.DialogCreatorUI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Strings;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;

import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.Ids.AoTDConditions;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.Ids.AoTDSwitches;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AgriProdSwitchListener extends BaseIndustryOptionProvider {

    public IndustrySpecAPI getIndustrySpec(String industryId) {
        for (IndustrySpecAPI indSpec : Global.getSettings().getAllIndustrySpecs()) {
            if (indSpec.getId().equals(industryId)) {
                return indSpec;

            }
        }
        return null;
    }

    public boolean isIndustry(String id) {
        return getIndustrySpec(id).hasTag(Industries.TAG_INDUSTRY);
    }

    public boolean isStructure(String id) {
        return getIndustrySpec(id).hasTag(Industries.TAG_STRUCTURE);
    }

    private void switchToolTip(IndustryOptionData opt, TooltipMakerAPI tooltip) {
        float pad = 3f;
        float opad = 10f;
        MarketAPI marketAPI = opt.ind.getMarket();
        IndustrySpecAPI originalInd = opt.ind.getSpec();
        Industry switchInd = opt.ind;


        FactionAPI faction = opt.ind.getMarket().getFaction();
        Color color = faction.getBaseUIColor();
        Color dark = faction.getDarkUIColor();
        Color grid = faction.getGridUIColor();
        Color bright = faction.getBrightUIColor();
        boolean arti = opt.ind.getId().equals(AoTDIndustries.ARTISANAL_FARMING);
        boolean sub = opt.ind.getId().equals(AoTDIndustries.SUBSIDISED_FARMING);
        boolean farm = opt.ind.getId().equals(Industries.FARMING);
        Color gray = Misc.getGrayColor();
        Color highlight = Misc.getHighlightColor();
        Color bad = Misc.getNegativeHighlightColor();
        String type = "";
        if (isIndustry(opt.ind.getId())) type = " - Industry";
        if (isStructure(opt.ind.getId())) type = " - Structure";
        tooltip.addTitle(opt.ind.getCurrentName() + type, color);
        tooltip.addPara(originalInd.getDesc(), 10f);
        int credits = (int) Global.getSector().getPlayerFleet().getCargo().getCredits().get();
        String creditsStr = Misc.getDGSCredits(credits);
        int cost = (int) (originalInd.getCost() / 10);
        String costStr = Misc.getDGSCredits(cost);

        int days = (int) 15;
        String daysStr = "days";
        if (days == 1) daysStr = "day";

        LabelAPI label = null;
        label = tooltip.addPara("%s and %s " + daysStr + " to upgrade. You have %s.", opad,
                highlight, costStr, "" + days, creditsStr);

        label.setHighlight(costStr, "" + days, creditsStr);
        if (credits >= cost) {
            label.setHighlightColors(highlight, highlight, highlight);
        } else {
            label.setHighlightColors(bad, highlight, highlight);
        }
        int upkeep = switchInd.getUpkeep().getModifiedInt();
        tooltip.addPara("Monthly upkeep: %s", opad, highlight, Misc.getDGSCredits(upkeep));
        tooltip.addStatModGrid(250, 65, 10, pad, switchInd.getUpkeep(), true, new TooltipMakerAPI.StatModValueGetter() {
            public String getPercentValue(MutableStat.StatMod mod) {
                return null;
            }

            public String getMultValue(MutableStat.StatMod mod) {
                return null;
            }

            public Color getModColor(MutableStat.StatMod mod) {
                return null;
            }

            public String getFlatValue(MutableStat.StatMod mod) {
                return Misc.getWithDGS(mod.value) + Strings.C;
            }
        });


        float maxIconsPerRow = 10f;
        boolean hasDemand = false;
        for (MutableCommodityQuantity curr : switchInd.getAllDemand()) {
            int qty = curr.getQuantity().getModifiedInt();
            if (qty <= 0) continue;
            hasDemand = true;
            break;
        }
        boolean hasSupply = false;
        for (MutableCommodityQuantity curr : switchInd.getAllSupply()) {
            int qty = curr.getQuantity().getModifiedInt();
            if (qty <= 0) continue;
            hasSupply = true;
            break;
        }
        boolean hadBioticsFocus = marketAPI.hasCondition(AoTDConditions.SWITCH_BIOTICS);
        boolean hadReciFocus = marketAPI.hasCondition(AoTDConditions.SWITCH_RECITIFICATES);
        boolean hadFoodFocus = marketAPI.hasCondition(AoTDConditions.SWITCH_FOOD);

        marketAPI.reapplyConditions();
        switchInd.reapply();

        tooltip.addSectionHeading("Production", color, dark, Alignment.MID, opad);
        tooltip.beginIconGroup();
        tooltip.setIconSpacingMedium();
        float icons = 0;
        int biggest = 5;
        for (MutableCommodityQuantity curr : switchInd.getAllSupply()) {
            int qty = curr.getQuantity().getModifiedInt();
            //if (qty <= 0) continue;

            if (qty >= biggest) {
                biggest = qty;
            }

        }

        if (farm) {
            if (opt.id.equals(AoTDConditions.SWITCH_BIOTICS)) {
                tooltip.addIcons(marketAPI.getCommodityData(AoTDCommodities.BIOTICS), biggest, IconRenderMode.NORMAL);
                tooltip.addIcons(marketAPI.getCommodityData(Commodities.FOOD), (int) (biggest * 0.5f), IconRenderMode.NORMAL);
                tooltip.addIcons(marketAPI.getCommodityData(AoTDCommodities.RECITIFICATES), (int) (biggest * 0.5f), IconRenderMode.NORMAL);
            }
            if (opt.id.equals(AoTDSwitches.SWITCH_FOOD)) {
                tooltip.addIcons(marketAPI.getCommodityData(Commodities.FOOD), biggest, IconRenderMode.NORMAL);
                tooltip.addIcons(marketAPI.getCommodityData(AoTDCommodities.BIOTICS), (int) (biggest * 0.5f), IconRenderMode.NORMAL);
                tooltip.addIcons(marketAPI.getCommodityData(AoTDCommodities.RECITIFICATES), (int) (biggest * 0.5f), IconRenderMode.NORMAL);
            }
            if (opt.id.equals(AoTDSwitches.SWITCH_RECTIFICATES)) {
                tooltip.addIcons(marketAPI.getCommodityData(AoTDCommodities.RECITIFICATES), biggest, IconRenderMode.NORMAL);
                tooltip.addIcons(marketAPI.getCommodityData(Commodities.FOOD), (int) (biggest * 0.5f), IconRenderMode.NORMAL);
                tooltip.addIcons(marketAPI.getCommodityData(AoTDCommodities.BIOTICS), (int) (biggest * 0.5f), IconRenderMode.NORMAL);

            }
        }
        if (sub) {
            if (opt.id.equals(AoTDSwitches.SWITCH_BIOTICS)) {
                tooltip.addIcons(marketAPI.getCommodityData(AoTDCommodities.BIOTICS), biggest, IconRenderMode.NORMAL);
                tooltip.addIcons(marketAPI.getCommodityData(Commodities.FOOD), (int) (biggest * 0.5f), IconRenderMode.NORMAL);
                tooltip.addIcons(marketAPI.getCommodityData(AoTDCommodities.RECITIFICATES), (int) (biggest * 0.5f), IconRenderMode.NORMAL);
            }
            if (opt.id.equals(AoTDSwitches.SWITCH_FOOD)) {
                tooltip.addIcons(marketAPI.getCommodityData(Commodities.FOOD), biggest, IconRenderMode.NORMAL);
                tooltip.addIcons(marketAPI.getCommodityData(AoTDCommodities.BIOTICS), (int) (biggest * 0.5f), IconRenderMode.NORMAL);
                tooltip.addIcons(marketAPI.getCommodityData(AoTDCommodities.RECITIFICATES), (int) (biggest * 0.5f), IconRenderMode.NORMAL);
            }
            if (opt.id.equals(AoTDSwitches.SWITCH_RECTIFICATES)) {
                tooltip.addIcons(marketAPI.getCommodityData(AoTDCommodities.RECITIFICATES), biggest, IconRenderMode.NORMAL);
                tooltip.addIcons(marketAPI.getCommodityData(Commodities.FOOD), (int) (biggest * 0.5f), IconRenderMode.NORMAL);
                tooltip.addIcons(marketAPI.getCommodityData(AoTDCommodities.BIOTICS), (int) (biggest * 0.5f), IconRenderMode.NORMAL);

            }
        }
        if (arti) {
            if (opt.id.equals(AoTDSwitches.SWITCH_BIOTICS)) {
                tooltip.addIcons(marketAPI.getCommodityData(AoTDCommodities.BIOTICS), biggest, IconRenderMode.NORMAL);
                tooltip.addIcons(marketAPI.getCommodityData(Commodities.FOOD), (int) (biggest * 0.5f), IconRenderMode.NORMAL);
                tooltip.addIcons(marketAPI.getCommodityData(AoTDCommodities.RECITIFICATES), (int) (biggest * 0.5f), IconRenderMode.NORMAL);
                tooltip.addIcons(marketAPI.getCommodityData(Commodities.LUXURY_GOODS), biggest, IconRenderMode.NORMAL);
            }
            if (opt.id.equals(AoTDSwitches.SWITCH_FOOD)) {
                tooltip.addIcons(marketAPI.getCommodityData(Commodities.FOOD), biggest, IconRenderMode.NORMAL);
                tooltip.addIcons(marketAPI.getCommodityData(AoTDCommodities.BIOTICS), (int) (biggest * 0.5f), IconRenderMode.NORMAL);
                tooltip.addIcons(marketAPI.getCommodityData(AoTDCommodities.RECITIFICATES), (int) (biggest * 0.5f), IconRenderMode.NORMAL);
                tooltip.addIcons(marketAPI.getCommodityData(Commodities.LUXURY_GOODS), biggest, IconRenderMode.NORMAL);
            }
            if (opt.id.equals(AoTDSwitches.SWITCH_RECTIFICATES)) {
                tooltip.addIcons(marketAPI.getCommodityData(AoTDCommodities.RECITIFICATES), biggest, IconRenderMode.NORMAL);
                tooltip.addIcons(marketAPI.getCommodityData(Commodities.FOOD), (int) (biggest * 0.5f), IconRenderMode.NORMAL);
                tooltip.addIcons(marketAPI.getCommodityData(AoTDCommodities.BIOTICS), (int) (biggest * 0.5f), IconRenderMode.NORMAL);
                tooltip.addIcons(marketAPI.getCommodityData(Commodities.LUXURY_GOODS), biggest, IconRenderMode.NORMAL);
            }
        }


    int rows = (int) Math.ceil(icons / maxIconsPerRow);
    rows =3;
            tooltip.addIconGroup(32,rows,opad);


        if(hasDemand)

    {
        tooltip.addSectionHeading("Demand & effects", color, dark, Alignment.MID, opad);
        tooltip.beginIconGroup();
        tooltip.setIconSpacingMedium();
        icons = 0;
        for (MutableCommodityQuantity curr : switchInd.getAllDemand()) {
            int qty = curr.getQuantity().getModifiedInt();
            if (qty <= 0) continue;

            CommodityOnMarketAPI com = marketAPI.getCommodityData(curr.getCommodityId());
            int available = com.getAvailable();

            int normal = Math.min(available, qty);
            int red = Math.max(0, qty - available);

            if (normal > 0) {
                tooltip.addIcons(com, normal, IconRenderMode.NORMAL);
            }
            if (red > 0) {
                tooltip.addIcons(com, red, IconRenderMode.DIM_RED);
            }
            icons += normal + Math.max(0, red);
        }
        rows = 1;
        tooltip.addIconGroup(32, rows, opad);
        tooltip.addPara("*Shown production and demand values are already adjusted based on current market size and local conditions.", gray, opad);
    }

}

    @Override
    public List<IndustryOptionData> getIndustryOptions(Industry ind) {
        if (isUnsuitable(ind, false)) return null;
        List<IndustryOptionData> result = new ArrayList<IndustryOptionData>();
        IndustryOptionData opt;

        if (ind.getId().equals(Industries.FARMING) || ind.getId().equals(AoTDIndustries.ARTISANAL_FARMING) || ind.getId().equals(AoTDIndustries.SUBSIDISED_FARMING)) {
            if (ind.isUpgrading()) {
                return null;
            }
            if (ind.getMarket().hasCondition(AoTDConditions.SWITCH_RECITIFICATES)) {
                opt = new IndustryOptionData("Switch Production to Biotics", AoTDSwitches.SWITCH_BIOTICS, ind, this);
                opt.color = Color.ORANGE;
                result.add(opt);
                opt = new IndustryOptionData("Switch Production to Food", AoTDSwitches.SWITCH_FOOD, ind, this);
                opt.color = Color.ORANGE;
                result.add(opt);
            } else if (ind.getMarket().hasCondition(AoTDConditions.SWITCH_BIOTICS)) {
                opt = new IndustryOptionData("Switch Production to Rectificates", AoTDSwitches.SWITCH_RECTIFICATES, ind, this);
                opt.color = Color.ORANGE;
                result.add(opt);
                opt = new IndustryOptionData("Switch Production to Food", AoTDSwitches.SWITCH_FOOD, ind, this);
                opt.color = Color.ORANGE;
                result.add(opt);
            } else {
                opt = new IndustryOptionData("Switch Production to Rectificates", AoTDSwitches.SWITCH_RECTIFICATES, ind, this);
                opt.color = Color.ORANGE;
                result.add(opt);
                opt = new IndustryOptionData("Switch Production to Biotics", AoTDSwitches.SWITCH_BIOTICS, ind, this);
                opt.color = Color.ORANGE;
                result.add(opt);

            }

        }


        if (!result.isEmpty()) {
            return result;
        }
        return null;

    }

    @Override
    public void createTooltip(IndustryOptionData opt, TooltipMakerAPI tooltip, float width) {
        switchToolTip(opt, tooltip);

    }

    @Override
    public void optionSelected(final IndustryOptionData opt, DialogCreatorUI ui) {
        CustomDialogDelegate delegate = new BaseCustomDialogDelegate() {
            @Override
            public void createCustomDialog(CustomPanelAPI panel, CustomDialogCallback callback) {
                float opad = 10f;
                FactionAPI faction = opt.ind.getMarket().getFaction();
                Color color = faction.getBaseUIColor();
                Color dark = faction.getDarkUIColor();
                Color grid = faction.getGridUIColor();
                Color bright = faction.getBrightUIColor();

                Color gray = Misc.getGrayColor();
                Color highlight = Misc.getHighlightColor();
                Color bad = Misc.getNegativeHighlightColor();
                TooltipMakerAPI info = panel.createUIElement(600, 100, false);
                info.setParaInsigniaLarge();
                info.addSpacer(2f);
                int credits = (int) Global.getSector().getPlayerFleet().getCargo().getCredits().get();
                String creditsStr = Misc.getDGSCredits(credits);
                int cost = (int) opt.ind.getSpec().getCost() / 10;
                String costStr = Misc.getDGSCredits(cost);

                int days = 15;
                String daysStr = "days";
                if (days == 1) daysStr = "day";

                LabelAPI label = null;
                if (opt.id.equals(AoTDSwitches.SWITCH_BIOTICS)) {
                    label = info.addPara("Switching production to Biotics will cost %s and take %s " + daysStr + " to upgrade.\n\nYou have %s.", opad,
                            highlight, costStr, "" + days, creditsStr);
                } else if (opt.id.equals(AoTDSwitches.SWITCH_RECTIFICATES)) {
                    label = info.addPara("Switching production to Rectificates will cost %s and take %s " + daysStr + " to upgrade.\n\nYou have %s.", opad,
                            highlight, costStr, "" + days, creditsStr);
                } else {
                    label = info.addPara("Switching production to Food will cost %s and take %s " + daysStr + " to upgrade.\n\nYou have %s.", opad,
                            highlight, costStr, "" + days, creditsStr);
                }


                label.setHighlight(costStr, "" + days, creditsStr);
                if (credits >= cost) {
                    label.setHighlightColors(highlight, highlight, highlight);
                } else {
                    label.setHighlightColors(bad, highlight, highlight);
                }

                panel.addUIElement(info).inTL(0, 0);
            }

            @Override
            public boolean hasCancelButton() {
                return true;
            }

            @Override
            public void customDialogConfirm() {
                MarketAPI market = opt.ind.getMarket();
                market.addIndustry(AoTDIndustries.AGRI_SWITCH);
                Industry inBetween = market.getIndustry(AoTDIndustries.AGRI_SWITCH);
                inBetween.getSpec().setName(opt.ind.getCurrentName());
                inBetween.getSpec().setDesc(opt.ind.getSpec().getDesc());
                inBetween.getSpec().setImageName(opt.ind.getSpec().getImageName());
                inBetween.setSpecialItem(opt.ind.getSpecialItem());
                inBetween.setImproved(opt.ind.isImproved());
                inBetween.setAICoreId(opt.ind.getAICoreId());
                inBetween.getSpec().setUpgrade(inBetween.getId());
                inBetween.startUpgrading();
                if (opt.id.equals(AoTDSwitches.SWITCH_FOOD)) {
                    if (market.hasCondition(AoTDConditions.SWITCH_RECITIFICATES)) {
                        market.removeCondition(AoTDConditions.SWITCH_RECITIFICATES);
                    }
                    if (market.hasCondition(AoTDConditions.SWITCH_BIOTICS)) {
                        market.removeCondition(AoTDConditions.SWITCH_BIOTICS);
                    }
                    if (!market.hasCondition(AoTDConditions.SWITCH_FOOD)) {
                        market.addCondition(AoTDConditions.SWITCH_FOOD);
                    }
                }
                if (opt.id.equals(AoTDSwitches.SWITCH_BIOTICS)) {
                    if (market.hasCondition(AoTDConditions.SWITCH_RECITIFICATES)) {
                        market.removeCondition(AoTDConditions.SWITCH_RECITIFICATES);
                    }
                    market.addCondition(AoTDConditions.SWITCH_BIOTICS);
                }
                if (opt.id.equals(AoTDSwitches.SWITCH_RECTIFICATES)) {
                    if (market.hasCondition(AoTDConditions.SWITCH_BIOTICS)) {
                        market.removeCondition(AoTDConditions.SWITCH_BIOTICS);
                    }
                    market.addCondition(AoTDConditions.SWITCH_RECITIFICATES);
                }
                market.removeIndustry(opt.ind.getId(), null, false);
            }

            @Override
            public void customDialogCancel() {

            }
        };
        ui.showDialog(600, 150, delegate);

    }


    @Override
    public void addToIndustryTooltip(Industry ind, Industry.IndustryTooltipMode mode, TooltipMakerAPI tooltip, float width, boolean expanded) {
    }

}
