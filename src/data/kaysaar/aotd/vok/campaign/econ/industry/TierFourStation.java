package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MutableCommodityQuantity;
import com.fs.starfarer.api.campaign.listeners.ListenerUtil;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.impl.SharedUnlockData;
import com.fs.starfarer.api.impl.campaign.DebugFlags;
import com.fs.starfarer.api.impl.campaign.econ.impl.OrbitalStation;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Strings;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.codex.CodexDataV2;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.IconRenderMode;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.GPUIMisc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;

import java.awt.*;
import java.util.LinkedHashMap;

public class TierFourStation extends OrbitalStation {
    public static LinkedHashMap<String,Integer>costMap = new LinkedHashMap<>();
    static {
        costMap.put(AoTDCommodities.REFINED_METAL,150);
        costMap.put(AoTDCommodities.PURIFIED_TRANSPLUTONICS,50);
        costMap.put(AoTDCommodities.ADVANCED_COMPONENTS,100);
        costMap.put(AoTDCommodities.DOMAIN_GRADE_MACHINERY,150);
    }
    @Override
    public void advance(float amount) {
        boolean disrupted = isDisrupted();
        if (!disrupted && wasDisrupted) {
            disruptionFinished();
        }
        wasDisrupted = disrupted;

//		if (disrupted) {
//			//if (DebugFlags.COLONY_DEBUG) {
//				String key = getDisruptedKey();
//				market.getMemoryWithoutUpdate().unset(key);
//			//}
//		}

        if (building && !disrupted) {
            float days = Global.getSector().getClock().convertToDays(amount);
            if(isUpgrading()){
            days*= GPManager.getInstance().getTotalPenaltyFromResources(AoTDCommodities.PURIFIED_TRANSPLUTONICS,AoTDCommodities.DOMAIN_GRADE_MACHINERY,AoTDCommodities.ADVANCED_COMPONENTS,AoTDCommodities.REFINED_METAL);
            }
            //DebugFlags.COLONY_DEBUG = true;
            if (DebugFlags.COLONY_DEBUG) {
                days *= 100f;
            }
            buildProgress += days;

            if (buildProgress >= buildTime) {
                finishBuildingOrUpgrading();
            }
        }

        if (Global.getSector().getEconomy().isSimMode()) return;


        if (stationEntity == null) {
            spawnStation();
        }

        if (stationFleet != null) {
            stationFleet.setAI(null);
            if (stationFleet.getOrbit() == null && stationEntity != null) {
                stationFleet.setCircularOrbit(stationEntity, 0, 0, 100);
            }
            if(this.getSpec().hasTag("starcitadel")){
                stationFleet.getMemoryWithoutUpdate().set(Misc.DANGER_LEVEL_OVERRIDE,10);
            }
        }

    }

    public String getBuildOrUpgradeProgressText() {
//		float f = buildProgress / spec.getBuildTime();
//		return "" + (int) Math.round(f * 100f) + "%";
        if (isUpgrading()) {
            //return "" + (int) Math.round(Misc.getMarketSizeProgress(market) * 100f) + "%";
            return "Upgrade Progress : " + Misc.getRoundedValue(getBuildOrUpgradeProgress() * 100f) + "%";
        }

        return super.getBuildOrUpgradeProgressText();
    }

    @Override
    protected void addPostUpkeepSection(TooltipMakerAPI tooltip, IndustryTooltipMode mode) {
        super.addPostUpkeepSection(tooltip, mode);
        if(mode.equals(IndustryTooltipMode.NORMAL)&&isUpgrading()){
            tooltip.addSectionHeading("Grand Project (Global Production)", Alignment.MID,5f);
            tooltip.addPara("Due to intensive work required with upgrade of this station, massive resources are needed for it's construction.",Misc.getTooltipTitleAndLightHighlightColor(),3f);
            tooltip.addCustom(GPUIMisc.createResourcePanelForSmallTooltip(tooltip.getWidthSoFar()+10,30,30,costMap,null),5f);
        }
        if(mode.equals(IndustryTooltipMode.UPGRADE)&&this.getSpec().hasTag("starcitadel")){
            tooltip.addSectionHeading("Grand Project (Global Production)", Alignment.MID,5f);
            tooltip.addPara("Due to intensive work required with upgrade of this station, massive resources are needed for it's construction.",Misc.getTooltipTitleAndLightHighlightColor(),3f);
            tooltip.addCustom(GPUIMisc.createResourcePanelForSmallTooltip(tooltip.getWidthSoFar()+10,30,30,costMap,null),5f);
        }
    }

    @Override
    public void createTooltip(IndustryTooltipMode mode, TooltipMakerAPI tooltip, boolean expanded) {

        if (getSpec() != null && getSpec().hasTag(Tags.CODEX_UNLOCKABLE)) {
            SharedUnlockData.get().reportPlayerAwareOfIndustry(getSpec().getId(), true);
        }
        tooltip.setCodexEntryId(CodexDataV2.getIndustryEntryId(getSpec().getId()));

        currTooltipMode = mode;

        float pad = 3f;
        float opad = 10f;

        FactionAPI faction = market.getFaction();
        Color color = faction.getBaseUIColor();
        Color dark = faction.getDarkUIColor();
        Color grid = faction.getGridUIColor();
        Color bright = faction.getBrightUIColor();

        Color gray = Misc.getGrayColor();
        Color highlight = Misc.getHighlightColor();
        Color bad = Misc.getNegativeHighlightColor();


        MarketAPI copy = market.clone();
        // the copy is a shallow copy and its conditions point to the original market
        // so, make it share the suppressed conditions list, too, otherwise
        // e.g. SolarArray will suppress conditions in the original market and the copy will still apply them
        copy.setSuppressedConditions(market.getSuppressedConditions());
        copy.setRetainSuppressedConditionsSetWhenEmpty(true);
        market.setRetainSuppressedConditionsSetWhenEmpty(true);
        MarketAPI orig = market;

        //int numBeforeAdd = Misc.getNumIndustries(market);

        market = copy;
        boolean needToAddIndustry = !market.hasIndustry(getId());
        //addDialogMode = true;
        if (needToAddIndustry) market.getIndustries().add(this);

        if (mode != IndustryTooltipMode.NORMAL) {
            market.clearCommodities();
            for (CommodityOnMarketAPI curr : market.getAllCommodities()) {
                curr.getAvailableStat().setBaseValue(100);
            }
        }

//		if (addDialogMode) {
//			market.reapplyConditions();
//			apply();
//		}
        market.reapplyConditions();
        reapply();

        String type = "";
        if (isIndustry()) type = " - Industry";
        if (isStructure()) type = " - Structure";
        // For future content
//        if(this.getSpec().hasTag("grand_project")){
//            type = " - Grand Project";
//        }

        tooltip.addTitle(getCurrentName() + type, color);

        String desc = spec.getDesc();
        String override = getDescriptionOverride();
        if (override != null) {
            desc = override;
        }
        desc = Global.getSector().getRules().performTokenReplacement(null, desc, market.getPrimaryEntity(), null);

        tooltip.addPara(desc, opad);

//		Industry inProgress = Misc.getCurrentlyBeingConstructed(market);
//		if ((mode == IndustryTooltipMode.ADD_INDUSTRY && inProgress != null) ||
//				(mode == IndustryTooltipMode.UPGRADE && inProgress != null)) {
//			//tooltip.addPara("Another project (" + inProgress.getCurrentName() + ") in progress", bad, opad);
//			//tooltip.addPara("Already building: " + inProgress.getCurrentName() + "", bad, opad);
//			tooltip.addPara("Another construction in progress: " + inProgress.getCurrentName() + "", bad, opad);
//		}

        //tooltip.addPara("Type: %s", opad, gray, highlight, type);
        if (isIndustry() && (mode == IndustryTooltipMode.ADD_INDUSTRY ||
                mode == IndustryTooltipMode.UPGRADE ||
                mode == IndustryTooltipMode.DOWNGRADE)
        ) {

            int num = Misc.getNumIndustries(market);
            int max = Misc.getMaxIndustries(market);


            // during the creation of the tooltip, the market has both the current industry
            // and the upgrade/downgrade. So if this upgrade/downgrade counts as an industry, it'd count double if
            // the current one is also an industry. Thus reduce num by 1 if that's the case.
            if (isIndustry()) {
                if (mode == IndustryTooltipMode.UPGRADE) {
                    for (Industry curr : market.getIndustries()) {
                        if (getSpec().getId().equals(curr.getSpec().getUpgrade())) {
                            if (curr.isIndustry()) {
                                num--;
                            }
                            break;
                        }
                    }
                } else if (mode == IndustryTooltipMode.DOWNGRADE) {
                    for (Industry curr : market.getIndustries()) {
                        if (getSpec().getId().equals(curr.getSpec().getDowngrade())) {
                            if (curr.isIndustry()) {
                                num--;
                            }
                            break;
                        }
                    }
                }
            }

            Color c = gray;
            c = Misc.getTextColor();
            Color h1 = highlight;
            Color h2 = highlight;
            if (num > max) {// || (num >= max && mode == IndustryTooltipMode.ADD_INDUSTRY)) {
                //c = bad;
                h1 = bad;
                num--;

                tooltip.addPara("Maximum number of industries reached", bad, opad);
            }
            //tooltip.addPara("Maximum of %s industries on a colony of this size. Currently: %s.",
//			LabelAPI label = tooltip.addPara("Maximum industries for a colony of this size: %s. Industries: %s. ",
//					opad, c, h1, "" + max, "" + num);
//			label.setHighlightColors(h2, h1);
        }



        addRightAfterDescriptionSection(tooltip, mode);

        if (isDisrupted()) {
            int left = (int) getDisruptedDays();
            if (left < 1) left = 1;
            String days = "days";
            if (left == 1) days = "day";

            tooltip.addPara("Operations disrupted! %s " + days + " until return to normal function.",
                    opad, Misc.getNegativeHighlightColor(), highlight, "" + left);
        }

        if (DebugFlags.COLONY_DEBUG || market.isPlayerOwned()) {
            if (mode == IndustryTooltipMode.NORMAL) {
                if (getSpec().getUpgrade() != null && !isBuilding()) {
                    tooltip.addPara("Click to manage or upgrade", Misc.getPositiveHighlightColor(), opad);
                } else {
                    tooltip.addPara("Click to manage", Misc.getPositiveHighlightColor(), opad);
                }
                //tooltip.addPara("Click to manage", market.getFaction().getBrightUIColor(), opad);
            }
        }

        if (mode == IndustryTooltipMode.QUEUED) {
            tooltip.addPara("Click to remove or adjust position in queue", Misc.getPositiveHighlightColor(), opad);
            tooltip.addPara("Currently queued for construction. Does not have any impact on the colony.", opad);

            int left = (int) (getSpec().getBuildTime());
            if (left < 1) left = 1;
            String days = "days";
            if (left == 1) days = "day";
            tooltip.addPara("Requires %s " + days + " to build.", opad, highlight, "" + left);

            //return;
        } else if (!isFunctional() && mode == IndustryTooltipMode.NORMAL && !isDisrupted()) {
            tooltip.addPara("Currently under construction and not producing anything or providing other benefits.", opad);

            int left = (int) (buildTime - buildProgress);
            if (left < 1) left = 1;
            String days = "days";
            if (left == 1) days = "day";
            tooltip.addPara("Requires %s more " + days + " to finish building.", opad, highlight, "" + left);
        }


        if (!isAvailableToBuild() &&
                (mode == IndustryTooltipMode.ADD_INDUSTRY ||
                        mode == IndustryTooltipMode.UPGRADE ||
                        mode == IndustryTooltipMode.DOWNGRADE)) {
            String reason = getUnavailableReason();
            if (reason != null) {
                tooltip.addPara(reason, bad, opad);
            }
        }

        boolean category = getSpec().hasTag(Industries.TAG_PARENT);

        if (!category) {
            int credits = (int) Global.getSector().getPlayerFleet().getCargo().getCredits().get();
            String creditsStr = Misc.getDGSCredits(credits);
            if (mode == IndustryTooltipMode.UPGRADE || mode == IndustryTooltipMode.ADD_INDUSTRY) {
                int cost = (int) getBuildCost();
                String costStr = Misc.getDGSCredits(cost);

                int days = (int) getBuildTime();
                String daysStr = "days";
                if (days == 1) daysStr = "day";

                LabelAPI label = null;
                if (mode == IndustryTooltipMode.UPGRADE) {
                    label = tooltip.addPara("%s and %s " + daysStr + " to upgrade. You have %s.", opad,
                            highlight, costStr, "" + days, creditsStr);
                } else {
                    label = tooltip.addPara("%s and %s " + daysStr + " to build. You have %s.", opad,
                            highlight, costStr, "" + days, creditsStr);
                }
                label.setHighlight(costStr, "" + days, creditsStr);
                if (credits >= cost) {
                    label.setHighlightColors(highlight, highlight, highlight);
                } else {
                    label.setHighlightColors(bad, highlight, highlight);
                }
            } else if (mode == IndustryTooltipMode.DOWNGRADE) {
                if (getSpec().getUpgrade() != null) {
                    float refundFraction = Global.getSettings().getFloat("industryRefundFraction");

                    //int cost = (int) (getBuildCost() * refundFraction);
                    IndustrySpecAPI spec = Global.getSettings().getIndustrySpec(getSpec().getUpgrade());
                    int cost = (int) (spec.getCost() * refundFraction);
                    String refundStr = Misc.getDGSCredits(cost);

                    tooltip.addPara("%s refunded for downgrade.", opad, highlight, refundStr);
                }
            }


            addPostDescriptionSection(tooltip, mode);

            if (!getIncome().isUnmodified()) {
                int income = getIncome().getModifiedInt();
                tooltip.addPara("Monthly income: %s", opad, highlight, Misc.getDGSCredits(income));
                tooltip.addStatModGrid(300, 65, 10, pad, getIncome(), true, new TooltipMakerAPI.StatModValueGetter() {
                    public String getPercentValue(MutableStat.StatMod mod) {return null;}
                    public String getMultValue(MutableStat.StatMod mod) {return null;}
                    public Color getModColor(MutableStat.StatMod mod) {return null;}
                    public String getFlatValue(MutableStat.StatMod mod) {
                        return Misc.getWithDGS(mod.value) + Strings.C;
                    }
                });
            }

            if (!getUpkeep().isUnmodified()) {
                int upkeep = getUpkeep().getModifiedInt();
                tooltip.addPara("Monthly upkeep: %s", opad, highlight, Misc.getDGSCredits(upkeep));
                tooltip.addStatModGrid(300, 65, 10, pad, getUpkeep(), true, new TooltipMakerAPI.StatModValueGetter() {
                    public String getPercentValue(MutableStat.StatMod mod) {return null;}
                    public String getMultValue(MutableStat.StatMod mod) {return null;}
                    public Color getModColor(MutableStat.StatMod mod) {return null;}
                    public String getFlatValue(MutableStat.StatMod mod) {
                        return Misc.getWithDGS(mod.value) + Strings.C;
                    }
                });
            }

            addPostUpkeepSection(tooltip, mode);

            boolean hasSupply = false;
            for (MutableCommodityQuantity curr : supply.values()) {
                int qty = curr.getQuantity().getModifiedInt();
                if (qty <= 0) continue;
                hasSupply = true;
                break;
            }
            boolean hasDemand = false;
            for (MutableCommodityQuantity curr : demand.values()) {
                int qty = curr.getQuantity().getModifiedInt();
                if (qty <= 0) continue;
                hasDemand = true;
                break;
            }

            float maxIconsPerRow = 10f;
            if (hasSupply) {
                tooltip.addSectionHeading("Production", color, dark, Alignment.MID, opad);
                tooltip.beginIconGroup();
                tooltip.setIconSpacingMedium();
                float icons = 0;
                for (MutableCommodityQuantity curr : supply.values()) {
                    int qty = curr.getQuantity().getModifiedInt();
                    //if (qty <= 0) continue;

                    int normal = qty;
                    if (normal > 0) {
                        tooltip.addIcons(market.getCommodityData(curr.getCommodityId()), normal, IconRenderMode.NORMAL);
                    }

                    int plus = 0;
                    int minus = 0;
                    for (MutableStat.StatMod mod : curr.getQuantity().getFlatMods().values()) {
                        if (mod.value > 0) {
                            plus += (int) mod.value;
                        } else if (mod.desc != null && mod.desc.contains("shortage")) {
                            minus += (int) Math.abs(mod.value);
                        }
                    }
                    minus = Math.min(minus, plus);
                    if (minus > 0 && mode == IndustryTooltipMode.NORMAL) {
                        tooltip.addIcons(market.getCommodityData(curr.getCommodityId()), minus, IconRenderMode.DIM_RED);
                    }
                    icons += normal + Math.max(0, minus);
                }
                int rows = (int) Math.ceil(icons / maxIconsPerRow);
                rows = 3;
                tooltip.addIconGroup(32, rows, opad);


            }
//			else if (!isFunctional() && mode == IndustryTooltipMode.NORMAL) {
//				tooltip.addPara("Currently under construction and not producing anything or providing other benefits.", opad);
//			}

            addPostSupplySection(tooltip, hasSupply, mode);

            if (hasDemand || hasPostDemandSection(hasDemand, mode)) {
                tooltip.addSectionHeading("Demand & effects", color, dark, Alignment.MID, opad);
            }
            if (hasDemand) {
                tooltip.beginIconGroup();
                tooltip.setIconSpacingMedium();
                float icons = 0;
                for (MutableCommodityQuantity curr : demand.values()) {
                    int qty = curr.getQuantity().getModifiedInt();
                    if (qty <= 0) continue;

                    CommodityOnMarketAPI com = orig.getCommodityData(curr.getCommodityId());
                    int available = com.getAvailable();

                    int normal = Math.min(available, qty);
                    int red = Math.max(0, qty - available);

                    if (mode != IndustryTooltipMode.NORMAL) {
                        normal = qty;
                        red = 0;
                    }
                    if (normal > 0) {
                        tooltip.addIcons(com, normal, IconRenderMode.NORMAL);
                    }
                    if (red > 0) {
                        tooltip.addIcons(com, red, IconRenderMode.DIM_RED);
                    }
                    icons += normal + Math.max(0, red);
                }
                int rows = (int) Math.ceil(icons / maxIconsPerRow);
                rows = 3;
                rows = 1;
                tooltip.addIconGroup(32, rows, opad);
            }

            addPostDemandSection(tooltip, hasDemand, mode);

            if (!needToAddIndustry) {
                //addAICoreSection(tooltip, AICoreDescriptionMode.TOOLTIP);
                addInstalledItemsSection(mode, tooltip, expanded);
                addImprovedSection(mode, tooltip, expanded);

                ListenerUtil.addToIndustryTooltip(this, mode, tooltip, getTooltipWidth(), expanded);
            }

            tooltip.addPara("*Shown production and demand values are already adjusted based on current market size and local conditions.", gray, opad);
        }

        if (needToAddIndustry) {
            unapply();
            market.getIndustries().remove(this);
        }
        market = orig;
        market.setRetainSuppressedConditionsSetWhenEmpty(null);
        if (!needToAddIndustry) {
            reapply();
        }
        market.reapplyConditions();
    }



    @Override
    public boolean isAvailableToBuild() {
        if(this.getSpec().hasTag("starcitadel")){
            if(market.isPlayerOwned()){
                return Global.getSector().getPlayerFaction().knowsIndustry(this.getSpec().getId());
            }
            return market.getFaction().knowsIndustry(this.getSpec().getId());
        }
        return super.isAvailableToBuild();
    }

    @Override
    public boolean showWhenUnavailable() {
        if(this.getSpec().hasTag("starcitadel")){
            return false;
        }
        return super.showWhenUnavailable();
    }

}
