package data.kaysaar.aotd.vok.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MutableCommodityQuantity;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.ItemEffectsRepo;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.plugins.AoDUtilis;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;


import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class UpgradeListUI implements CustomDialogDelegate {


    //This ui has been "Yoinked" by permission of SirHartley as it based on  his code, to which I thank you a lot for sharing ^^
    public static final float WIDTH = 600f;
    public static final float HEIGHT = Global.getSettings().getScreenHeight() - 300f;
    public static final float ENTRY_HEIGHT = 500; //MUST be even
    public static final float ENTRY_WIDTH = WIDTH - 5f; //MUST be even
    public static final float CONTENT_HEIGHT = 80;

    static ArrayList<Pair<String,String>> industries = new ArrayList<>();
    static {
        industries.add(new Pair<>(Industries.AQUACULTURE,"aotd_tech_aquatic_agriculture"));
        industries.add(new Pair<>(Industries.FARMING,"aotd_tech_agriculture"));
        industries.add(new Pair<>(Industries.MINING,"aotd_tech_exosceletons"));
        industries.add(new Pair<>(Industries.REFINING,"aotd_tech_nanometal"));
        industries.add(new Pair<>(Industries.HEAVYINDUSTRY,"aotd_tech_hull_manufacture"));
        industries.add(new Pair<>(Industries.ORBITALWORKS,"aotd_tech_orbital_assembly"));
        industries.add(new Pair<>(Industries.LIGHTINDUSTRY,"aotd_tech_aquatic_agriculture"));


    }
    public Industry industry;
    public List<ButtonAPI> buttons = new ArrayList<>();
    public List<String> upgrades = new ArrayList<>();
    public  String selected = null;
    public UpgradeListUI(Industry industry,ArrayList<String>upgrades) {
        this.industry = industry;
        this.upgrades = upgrades;
    }

    public boolean isIndustry(String id) {
        return getIndustrySpec(id).hasTag(Industries.TAG_INDUSTRY);
    }


    public String helperIndustryStructureOrIndustry(String id) {
        if (isIndustry(id)) {
            return " - Industry";
        }
        return "- Structure";
    }


    int handleFarming(MarketAPI market) {
        int quantity = market.getSize();
        if (market.hasCondition(Conditions.FARMLAND_BOUNTIFUL)) {
            quantity += 2;
        }
        if (market.hasCondition(Conditions.FARMLAND_RICH)) {
            quantity += 1;
        }
        if (market.hasCondition(Conditions.FARMLAND_POOR)) {
            quantity -= 1;
        }
        if (market.hasCondition(Conditions.SOLAR_ARRAY)) {
            quantity += 2;
        }
        return quantity;
    }


    public boolean isCryovolcanicOrFrozen(MarketAPI market) {
        if (market == null) return false;
        boolean isCryovolcanicOrFrozen = false;

        if (market.getPlanetEntity() != null) {
            if (market.getPlanetEntity().getTypeId().equals("frozen") || market.getPlanetEntity().getTypeId().equals("cryovolcanic") || market.getPlanetEntity().getTypeId().equals("frozen1")) {
                isCryovolcanicOrFrozen = true;
            }
        }

        return isCryovolcanicOrFrozen;
    }


    public IndustrySpecAPI getIndustrySpec(String industryId) {
        for (IndustrySpecAPI indSpec : Global.getSettings().getAllIndustrySpecs()) {
            if (indSpec.getId().equals(industryId)) {
                return indSpec;

            }
        }
        return null;
    }

    public void createCustomDialog(CustomPanelAPI panel, CustomDialogCallback callback) {
        TooltipMakerAPI panelTooltip = panel.createUIElement(WIDTH, HEIGHT, true);
        panelTooltip.addSectionHeading("Select an Upgrade", Alignment.MID, 0f);

        float opad = 10f;
        float spad = 2f;

        buttons.clear();

        for (String industryId : upgrades) {
            CustomPanelAPI helper1 = panel.createCustomPanel(ENTRY_WIDTH, ENTRY_HEIGHT + 2f, new ButtonReportingCustomPanel(this));
            TooltipMakerAPI helper;
            IndustrySpecAPI upgrdSpec = getIndustrySpec(industryId);
            float cost = getIndustrySpec(industryId).getCost();

            boolean canAfford = Global.getSector().getPlayerFleet().getCargo().getCredits().get() >= cost;

            MarketAPI marketAPI = industry.getMarket();
            MarketAPI copy = marketAPI.clone();
            MarketAPI orig = marketAPI;
            marketAPI = copy;
            IndustrySpecAPI upgrd = getIndustrySpec(industryId);
            marketAPI.addIndustry(upgrd.getId());
            Industry upgrdInd = marketAPI.getIndustry(upgrd.getId());
            if(!upgrdInd.showWhenUnavailable()&&!upgrdInd.isAvailableToBuild()){
                marketAPI.removeIndustry(upgrdInd.getId(), null, false);
                marketAPI.reapplyIndustries();
                continue;
            }

            List<MutableCommodityQuantity> dem = upgrdInd.getAllDemand();
            for (MutableCommodityQuantity mutableCommodityQuantity : dem) {
                upgrdInd.getDemand(mutableCommodityQuantity.getCommodityId()).getQuantity().modifyMult("uis", 0);

            }
            marketAPI.reapplyIndustries();

            List<MutableCommodityQuantity> sup = upgrdInd.getAllSupply();

            marketAPI.reapplyIndustries();
            boolean isAvailableToBuild = upgrdInd.isAvailableToBuild();
            if (upgrdInd.getId().equals(Industries.FARMING)) {
                if ((AoDUtilis.getFoodQuantityBonus(upgrdInd.getMarket())) >= -1) {
                    isAvailableToBuild = true;
                }

            }

            boolean hasSupply = false;

            for (MutableCommodityQuantity curr : sup) {
                int qty = (int) curr.getQuantity().getModifiedInt();
                if (marketAPI.getAdmin().getStats().hasSkill(Skills.INDUSTRIAL_PLANNING)) {
                    qty -= 1;
                }

                if (qty <= 0) continue;
                hasSupply = true;
                break;
            }
            for (MutableCommodityQuantity mutableCommodityQuantity : dem) {
                upgrdInd.getDemand(mutableCommodityQuantity.getCommodityId()).getQuantity().unmodifyMult("uis");

            }
            boolean hasDemand = false;
            for (MutableCommodityQuantity curr : dem) {
                int qty = (int) curr.getQuantity().getModifiedInt();
                if (qty <= 0) continue;
                hasDemand = true;
                break;
            }


            if (upgrdInd.getId().equals(Industries.FARMING) || upgrdInd.getId().equals(Industries.AQUACULTURE) || upgrdInd.getId().equals(Industries.MINING)) {
                hasSupply = true;
            }


            Color baseColor = Misc.getButtonTextColor();
            Color bgColour = Misc.getDarkPlayerColor();
            Color brightColor = Misc.getBrightPlayerColor();

            if (!canAfford || !isAvailableToBuild) {

                baseColor = Color.darkGray;
                bgColour = Color.lightGray;
                brightColor = Color.gray;
            }

            int credits = (int) Global.getSector().getPlayerFleet().getCargo().getCredits().get();

            Color color = industry.getMarket().getFaction().getBaseUIColor();
            Color dark = industry.getMarket().getFaction().getDarkUIColor();
            Color gray = Misc.getGrayColor();
            Color highlight = Misc.getHighlightColor();
            Color bad = Misc.getNegativeHighlightColor();

            String creditsStr = Misc.getDGSCredits(credits);
            String costStr = Misc.getDGSCredits(cost);
            String spriteName = upgrdSpec.getImageName();
            SpriteAPI sprite = Global.getSettings().getSprite(spriteName);

            float aspectRatio = sprite.getWidth() / sprite.getHeight();
            float adjustedWidth = CONTENT_HEIGHT * aspectRatio;
            float defaultPadding = (ENTRY_HEIGHT - CONTENT_HEIGHT) / 2;

            int days = (int) upgrdSpec.getBuildTime();
            String daysStr = "days";
            if (days == 1) daysStr = "day";
// That section is for calculation custom height for section - it is bad looking af, basically here i create replica of what i want to implement to calculate height of what i want to show, its just duplication of code
            String fullTitle = upgrdSpec.getName() + helperIndustryStructureOrIndustry(upgrdSpec.getId());
            helper = helper1.createUIElement(ENTRY_WIDTH - adjustedWidth - (3 * opad), CONTENT_HEIGHT, false);
            if (canAfford && isAvailableToBuild) helper.addSectionHeading(" " + fullTitle, Alignment.LMID, 0f);
            else {

                helper.addSectionHeading(" " + fullTitle, Color.WHITE, brightColor, Alignment.LMID, 0f);
            }
            LabelAPI labelDesc = helper.addPara(upgrdSpec.getDesc(), opad);
            labelDesc.autoSizeToWidth(ENTRY_WIDTH - adjustedWidth - (3 * opad));
            LabelAPI label1;
            label1 = helper.addPara("%s and %s " + daysStr + " to upgrade. You have %s.", opad,
                    highlight, costStr, "" + days, creditsStr);

            label1.setHighlight(costStr, "" + days, creditsStr);
            if (credits >= cost) {
                label1.setHighlightColors(highlight, highlight, highlight);
            } else {
                label1.setHighlightColors(bad, highlight, highlight);
            }
            int upkeep = upgrdInd.getUpkeep().getModifiedInt();
            if (!upgrdInd.getIncome().isUnmodified()) {
                int income = upgrdInd.getIncome().getModifiedInt();
                helper.addPara("Monthly income: %s", opad, highlight, Misc.getDGSCredits(income));
                helper.addStatModGrid(250, 65, 10, 10, upgrdInd.getIncome(), true, new TooltipMakerAPI.StatModValueGetter() {
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
            }
            helper.addPara("Monthly upkeep: %s", opad, highlight, Misc.getDGSCredits(upkeep));
            helper.addStatModGrid(250, 65, 10, 10f, upgrdInd.getUpkeep(), true, new TooltipMakerAPI.StatModValueGetter() {

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
            for (MutableCommodityQuantity mutableCommodityQuantity : dem) {
                upgrdInd.getDemand(mutableCommodityQuantity.getCommodityId()).getQuantity().modifyMult("uis", 0);

            }

            if (hasSupply) {
                helper.addSectionHeading("Production", color, dark, Alignment.MID, opad);
                helper.beginIconGroup();
                helper.setIconSpacingMedium();
                float icons = 0;
                for (MutableCommodityQuantity curr : upgrdInd.getAllSupply()) {
                    int qty = (int) curr.getQuantity().getModifiedInt();
                    //if (qty <= 0) continue

                    int normal = qty;

                    if (normal > 0) {
                        helper.addIcons(marketAPI.getCommodityData(curr.getCommodityId()), normal, IconRenderMode.NORMAL);
                    }

                }
                if (upgrdInd.getId().equals(Industries.FARMING)) {
                    helper.addIcons(marketAPI.getCommodityData(Commodities.FOOD), handleFarming(marketAPI), IconRenderMode.NORMAL);
                } else if (upgrdInd.getId().equals(Industries.AQUACULTURE)) {
                    helper.addIcons(marketAPI.getCommodityData(Commodities.FOOD), marketAPI.getSize(), IconRenderMode.NORMAL);
                } else if (upgrdInd.getId().equals(Industries.MINING)) {
                    if (AoDUtilis.getNormalOreAmount(marketAPI) >= -1) {
                        helper.addIcons(marketAPI.getCommodityData(Commodities.ORE), marketAPI.getSize() + AoDUtilis.getNormalOreAmount(marketAPI), IconRenderMode.NORMAL);
                    }
                    if (AoDUtilis.getRareOreAmount(marketAPI) >= -1) {
                        helper.addIcons(marketAPI.getCommodityData(Commodities.RARE_ORE), marketAPI.getSize() + AoDUtilis.getRareOreAmount(marketAPI) - 2, IconRenderMode.NORMAL);
                    }
                    if (AoDUtilis.getOrganicsAmount(marketAPI) >= -1) {
                        helper.addIcons(marketAPI.getCommodityData(Commodities.ORGANICS), marketAPI.getSize() + AoDUtilis.getOrganicsAmount(marketAPI), IconRenderMode.NORMAL);
                    }
                    if (AoDUtilis.getVolatilesAmount(marketAPI) >= -1) {
                        helper.addIcons(marketAPI.getCommodityData(Commodities.VOLATILES), marketAPI.getSize() + AoDUtilis.getVolatilesAmount(marketAPI) - 2, IconRenderMode.NORMAL);
                    }
                    if (isCryovolcanicOrFrozen(marketAPI)) {
                        helper.addIcons(marketAPI.getCommodityData(AoTDCommodities.WATER), marketAPI.getSize() - 2, IconRenderMode.NORMAL);
                    }


                }
                int rows = (int) Math.ceil(icons / maxIconsPerRow);
                rows = 3;
                helper.addIconGroup(32, rows, opad);
            }
            for (MutableCommodityQuantity mutableCommodityQuantity : dem) {
                upgrdInd.getDemand(mutableCommodityQuantity.getCommodityId()).getQuantity().unmodifyMult("uis");

            }
            if (hasDemand) {

                helper.addSectionHeading("Demand & effects", color, dark, Alignment.MID, opad);
                helper.beginIconGroup();
                helper.setIconSpacingMedium();
                float icons = 0;
                for (MutableCommodityQuantity curr : dem) {
                    int qty = (int) upgrdInd.getDemand(curr.getCommodityId()).getQuantity().getModifiedInt();
                    if (qty <= 0) continue;

                    CommodityOnMarketAPI com = marketAPI.getCommodityData(curr.getCommodityId());
                    int available = com.getAvailable();

                    int normal = Math.min(available, qty);
                    int red = Math.max(0, qty - available);
                    helper.addIcons(com, normal, IconRenderMode.NORMAL);


                }

                int rows = (int) Math.ceil(icons / maxIconsPerRow);
                rows = 1;

                helper.addIconGroup(32, rows, opad);


            }
            helper.addPara("*Shown production and demand values are already adjusted based on current market size and local conditions, showing total production and demands of this industry on the market.", gray, opad);
// That section is for calculation custom height for section
            if (!isAvailableToBuild) {
                helper.addPara(upgrdInd.getUnavailableReason(), Color.RED, opad);
            }
            helper.addPara(upgrdInd.getUnavailableReason(), Misc.getNegativeHighlightColor(), opad);
            if (upgrdInd.getSpec().getTags().contains("consumes")) {

                if(AoTDMainResearchManager.getInstance().getManagerForPlayer().haveResearched(AoTDTechIds.MEGA_ASSEMBLY_SYSTEMS)){
                    helper.addPara("Mega Assembly Systems have been researched! Item won't be consumed.", Misc.getPositiveHighlightColor(), opad);
                }
                else{
                    helper.addPara("Urgent! After industry's upgrade is started, this item is irreversibly consumed!", Misc.getHighlightColor(), opad);
                }
            }


            CustomPanelAPI subIndustryButtonPanel = panel.createCustomPanel(ENTRY_WIDTH, helper.getHeightSoFar() + 30f, new ButtonReportingCustomPanel(this));
            TooltipMakerAPI anchor = subIndustryButtonPanel.createUIElement(ENTRY_WIDTH, helper.getHeightSoFar() + 20, false);

            ButtonAPI areaCheckbox = anchor.addAreaCheckbox("", upgrdSpec.getId(), baseColor, bgColour, brightColor, //new Color(255,255,255,0)
                    ENTRY_WIDTH,
                    helper.getHeightSoFar() + 20,
                    0f,
                    true);

            areaCheckbox.setChecked(selected == upgrdSpec.getId());
            areaCheckbox.setEnabled(canAfford && isAvailableToBuild);
            for (String tag : upgrdInd.getSpec().getTags()) {
                if(tag.contains("consumes")){
                    String[] splited = tag.split(":");
                    areaCheckbox.setEnabled(canAfford && isAvailableToBuild&&AoDUtilis.checkForItemBeingInstalled(marketAPI,splited[1],splited[2]));
                    break;
                }
            }
            subIndustryButtonPanel.addUIElement(anchor).inTL(-opad, 0f); //if we don't -opad it kinda does it by its own, no clue why


            anchor = subIndustryButtonPanel.createUIElement(adjustedWidth, helper.getHeightSoFar() + 20, false);
            anchor.addImage(spriteName, adjustedWidth, CONTENT_HEIGHT, 0f);
            subIndustryButtonPanel.addUIElement(anchor).inTL(5, 10);

            TooltipMakerAPI lastPos = anchor;

            anchor = subIndustryButtonPanel.createUIElement(ENTRY_WIDTH - adjustedWidth - (3 * opad), CONTENT_HEIGHT, false);
            if (canAfford && isAvailableToBuild) anchor.addSectionHeading(" " + fullTitle, Alignment.LMID, 0f);
            else anchor.addSectionHeading(" " + fullTitle, Color.WHITE, brightColor, Alignment.LMID, 0f);
            LabelAPI labelDesc1 = anchor.addPara(upgrdSpec.getDesc(), opad);
            labelDesc1.autoSizeToWidth(ENTRY_WIDTH - adjustedWidth - (3 * opad));
            LabelAPI label;
            label = anchor.addPara("%s and %s " + daysStr + " to upgrade. You have %s.", opad,
                    highlight, costStr, "" + days, creditsStr);

            label.setHighlight(costStr, "" + days, creditsStr);
            if (credits >= cost) {
                label.setHighlightColors(highlight, highlight, highlight);
            } else {
                label.setHighlightColors(bad, highlight, highlight);
            }


            if (!upgrdInd.getIncome().isUnmodified()) {
                int income = upgrdInd.getIncome().getModifiedInt();
                anchor.addPara("Monthly income: %s", opad, highlight, Misc.getDGSCredits(income));
                anchor.addStatModGrid(250, 65, 10, 10, upgrdInd.getIncome(), true, new TooltipMakerAPI.StatModValueGetter() {
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
            }
            anchor.addPara("Monthly upkeep: %s", opad, highlight, Misc.getDGSCredits(upkeep));
            anchor.addStatModGrid(250, 65, 10, 10f, upgrdInd.getUpkeep(), true, new TooltipMakerAPI.StatModValueGetter() {

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


            for (MutableCommodityQuantity mutableCommodityQuantity : dem) {
                upgrdInd.getDemand(mutableCommodityQuantity.getCommodityId()).getQuantity().modifyMult("uis", 0);

            }
            marketAPI.reapplyIndustries();
            if (hasSupply) {
                anchor.addSectionHeading("Production", color, dark, Alignment.MID, opad);
                anchor.beginIconGroup();
                anchor.setIconSpacingMedium();
                float icons = 0;

                for (MutableCommodityQuantity curr : sup) {

                    int qty = (int) upgrdInd.getSupply(curr.getCommodityId()).getQuantity().getModifiedInt();
                    //if (qty <= 0) continue
                    if (marketAPI.getAdmin().getStats().hasSkill(Skills.INDUSTRIAL_PLANNING)) {
                        qty -= 1;
                    }
                    int normal = qty;

                    if (normal > 0) {
                        anchor.addIcons(marketAPI.getCommodityData(curr.getCommodityId()), normal, IconRenderMode.NORMAL);
                    }

                }
                if (upgrdInd.getId().equals(Industries.FARMING)) {
                    anchor.addIcons(marketAPI.getCommodityData(Commodities.FOOD), handleFarming(marketAPI), IconRenderMode.NORMAL);
                } else if (upgrdInd.getId().equals(Industries.AQUACULTURE)) {
                    anchor.addIcons(marketAPI.getCommodityData(Commodities.FOOD), marketAPI.getSize(), IconRenderMode.NORMAL);
                } else if (upgrdInd.getId().equals(Industries.MINING)) {
                    if (AoDUtilis.getNormalOreAmount(marketAPI) >= -1) {
                        anchor.addIcons(marketAPI.getCommodityData(Commodities.ORE), (marketAPI.getSize() + AoDUtilis.getNormalOreAmount(marketAPI)), IconRenderMode.NORMAL);
                    }
                    if (AoDUtilis.getRareOreAmount(marketAPI) >= -1) {
                        anchor.addIcons(marketAPI.getCommodityData(Commodities.RARE_ORE), (marketAPI.getSize() + AoDUtilis.getRareOreAmount(marketAPI)) - 2, IconRenderMode.NORMAL);
                    }
                    if (AoDUtilis.getOrganicsAmount(marketAPI) >= -1) {
                        anchor.addIcons(marketAPI.getCommodityData(Commodities.ORGANICS), (marketAPI.getSize() + AoDUtilis.getOrganicsAmount(marketAPI)), IconRenderMode.NORMAL);
                    }
                    if (AoDUtilis.getVolatilesAmount(marketAPI) >= -1) {
                        anchor.addIcons(marketAPI.getCommodityData(Commodities.VOLATILES), marketAPI.getSize() + AoDUtilis.getVolatilesAmount(marketAPI) - 2, IconRenderMode.NORMAL);
                    }
                    if (isCryovolcanicOrFrozen(marketAPI)) {
                        anchor.addIcons(marketAPI.getCommodityData(AoTDCommodities.WATER), marketAPI.getSize() - 2, IconRenderMode.NORMAL);
                    }

                }
                int rows = (int) Math.ceil(icons / maxIconsPerRow);
                rows = 3;
                anchor.addIconGroup(32, rows, opad);
            }
            for (MutableCommodityQuantity mutableCommodityQuantity : dem) {
                upgrdInd.getDemand(mutableCommodityQuantity.getCommodityId()).getQuantity().unmodifyMult("uis");

            }

            if (hasDemand) {
                anchor.addSectionHeading("Demand & effects", color, dark, Alignment.MID, opad);
                anchor.beginIconGroup();
                anchor.setIconSpacingMedium();
                float icons = 0;
                for (MutableCommodityQuantity curr : dem) {
                    int qty = curr.getQuantity().getModifiedInt();
                    if (qty <= 0) continue;

                    CommodityOnMarketAPI com = marketAPI.getCommodityData(curr.getCommodityId());
                    anchor.addIcons(com, qty, IconRenderMode.NORMAL);


                }

                int rows = (int) Math.ceil(icons / maxIconsPerRow);
                rows = 1;

                anchor.addIconGroup(32, rows, opad);

            }
            anchor.addPara("*Shown production and demand values are already adjusted based on current market size and local conditions.", gray, opad);
            if (!isAvailableToBuild) {
                anchor.addPara(upgrdInd.getUnavailableReason(), Misc.getNegativeHighlightColor(), opad);
            }
            for (String tag : upgrdInd.getSpec().getTags()) {
                if(tag.contains("consumes")){
                    String[] splited = tag.split(":");
                    if(!AoDUtilis.checkForItemBeingInstalled(marketAPI,splited[1],splited[2])){
                        anchor.addPara(AoDUtilis.consumeReq(splited[1]), Misc.getNegativeHighlightColor(), opad);
                    }
                    break;
                }
            }
            if (upgrdInd.getSpec().getTags().contains("consumes")) {
                if(AoTDMainResearchManager.getInstance().getManagerForPlayer().haveResearched(AoTDTechIds.MEGA_ASSEMBLY_SYSTEMS)){
                    anchor.addPara("Mega Assembly Systems have been researched! Item won't be consumed.", Misc.getPositiveHighlightColor(), opad);
                }
                else{
                    anchor.addPara("Urgent! After industry's upgrade is started, this item is irreversibly consumed!", Misc.getHighlightColor(), opad);
                }
            }
            subIndustryButtonPanel.addUIElement(anchor).inTL(5 + adjustedWidth + 15, 5);

            panelTooltip.addCustom(subIndustryButtonPanel, 0f);
            buttons.add(areaCheckbox);

            marketAPI.removeIndustry(upgrdInd.getId(), null, false);
            marketAPI.reapplyIndustries();
        }

        panel.addUIElement(panelTooltip).inTL(0.0F, 0.0F);
    }

    private String getDayOrDays(int buildTime) {
        if (buildTime == 1) {
            return "day";
        } else {
            return "days";
        }
    }

    public boolean hasCancelButton() {
        return true;
    }

    public String getConfirmText() {
        return "Confirm";
    }

    public String getCancelText() {
        return "Cancel";
    }

    public void customDialogConfirm() {
        if (selected == null) return;
        industry.getSpec().setUpgrade(selected);
        SpecialItemData specItem = industry.getSpecialItem();
        for (String tag : getIndustrySpec(selected).getTags()) {
            if(tag.contains("consumes")){
                if (specItem != null) {
                    industry.setSpecialItem(null);
                }
            }
        }
        industry.startUpgrading();
        industry.getSpec().setUpgrade(null);

        float cost = getIndustrySpec(selected).getCost();
        Global.getSector().getPlayerFleet().getCargo().getCredits().subtract(cost);
        Global.getSoundPlayer().playUISound("ui_upgrade_industry", 1, 1);


    }

    public void customDialogCancel() {
    }


    public CustomUIPanelPlugin getCustomPanelPlugin() {
        return null;
    }

    public void reportButtonPressed(Object id) {
        if (id instanceof String) {
           selected = (String) id;
        }

        for (ButtonAPI button : buttons) {
            if (button.isChecked() && button.getCustomData() != id) button.setChecked(false);
        }
    }

    public static class ButtonReportingCustomPanel extends BaseCustomUIPanelPlugin {
        public UpgradeListUI delegate;

        public ButtonReportingCustomPanel(UpgradeListUI delegate) {
            this.delegate = delegate;
        }

        @Override
        public void buttonPressed(Object buttonId) {
            super.buttonPressed(buttonId);
            delegate.reportButtonPressed(buttonId);
        }
    }
}
