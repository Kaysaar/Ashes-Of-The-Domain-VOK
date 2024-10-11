package data.kaysaar.aotd.vok.misc.shipinfo;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;

import com.fs.starfarer.api.loading.Description;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.loading.WeaponSlotAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.UILinesRenderer;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.misc.fighterinfo.FighterInfo;
import data.kaysaar.aotd.vok.misc.fighterinfo.FighterInfoRepo;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static data.kaysaar.aotd.vok.misc.AoTDMisc.getColorForSecondText;
import static data.kaysaar.aotd.vok.misc.AoTDMisc.getStringFromStatFloat;
import static java.awt.Color.cyan;

public class ShipInfoGenerator {
    public static void generate(TooltipMakerAPI tooltip, FleetMemberAPI ship, String variantIdOverride, CustomPanelAPI shipImage, float widthOfTooltip) {

        tooltip.addSectionHeading(ship.getHullSpec().getNameWithDesignationWithDashClass(), Alignment.MID, 0f);

        //Ship description and sprite section
        CustomPanelAPI panelAPI = Global.getSettings().createCustom(140, 140, null);
        TooltipMakerAPI tooltipOfShipSprite = panelAPI.createUIElement(140, 140, false);
        UILinesRenderer renderer = new UILinesRenderer(10);
        CustomPanelAPI textPanel = Global.getSettings().createCustom(tooltip.getWidthSoFar() - 15, 200, renderer);
        TooltipMakerAPI tooltipOfShipDescriptionTest = textPanel.createUIElement(tooltip.getWidthSoFar() - 15, 210, true);

        CustomPanelAPI container = Global.getSettings().createCustom(690, 200, null);
        tooltipOfShipDescriptionTest.setParaFont(Fonts.INSIGNIA_LARGE);
        LabelAPI labelAPI = tooltipOfShipDescriptionTest.addPara(Global.getSettings().getDescription(ship.getHullSpec().getDescriptionId(), Description.Type.SHIP).getText1FirstPara()+"\ns", 10f);
        TooltipMakerAPI tooltipOfShipDescription = textPanel.createUIElement(tooltip.getWidthSoFar() - 15, labelAPI.getPosition().getHeight() + 15, false);
        textPanel.getPosition().setSize(textPanel.getPosition().getWidth(), labelAPI.getPosition().getHeight() + 45);
        LabelAPI designLabel = Misc.addDesignTypePara(tooltipOfShipDescription, ship.getHullSpec().getManufacturer(), 10f);

        tooltipOfShipDescription.setParaFont(Fonts.INSIGNIA_LARGE);
        tooltipOfShipDescription.addPara(Global.getSettings().getDescription(ship.getHullSpec().getDescriptionId(), Description.Type.SHIP).getText1FirstPara(), 15f);
        tooltipOfShipDescription.setParaFont(Fonts.DEFAULT_SMALL);
        LabelAPI combat = tooltipOfShipDescription.addPara("Combat readiness: %s", 5f, Misc.getGrayColor(), Misc.getPositiveHighlightColor(), "70%");
        combat.getPosition().inTL(textPanel.getPosition().getWidth() - combat.computeTextWidth(combat.getText()) - 10, -designLabel.getPosition().getY() - combat.getPosition().getHeight());


        panelAPI.addComponent(shipImage).inTL(0, 0);
        CustomPanelAPI panelOfLogistics = generateLogisticalDataPanel(ship);
        panelAPI.addUIElement(tooltipOfShipSprite).inTL(0, 0);
        textPanel.addUIElement(tooltipOfShipDescription).inTL(0, 0);
        float xCord = 750;
        float maxXCord = widthOfTooltip;

        float widthLeft = maxXCord - xCord;
        float widthOfPanel = panelAPI.getPosition().getWidth();
        float leftSpace = widthOfPanel - widthOfPanel;

        container.addComponent(panelAPI).inTL(xCord + leftSpace / 2, 30);

        container.addComponent(panelOfLogistics).inTL(-12, 0);

        tooltip.addCustom(container, 5f);
        //Ship data Section
        CustomPanelAPI panelOfOtherInfo = generateOtherInfo(ship,705,120,false);
        tooltip.addCustom(panelOfOtherInfo, 15f);

        tooltip.addCustom(textPanel, 5f);
        ArrayList<CustomPanelAPI> renderes = new ArrayList<>();
        renderes.add(textPanel);
        renderer.setPanels(renderes);
        tooltip.addSpacer(10f);

    }

    public static CustomPanelAPI generateOtherInfo(FleetMemberAPI ship, float width, float height,boolean isFighter) {
        CustomPanelAPI otherInfoPanel = Global.getSettings().createCustom(width, height, null);
        TooltipMakerAPI nameOfLabelsTooltip = otherInfoPanel.createUIElement(100, height, false);
        TooltipMakerAPI otherInfoTooltip = otherInfoPanel.createUIElement(width-120, height, false);
        LabelAPI systemLoc =null;
        if(ship.getHullSpec().getShipSystemId()!=null&&!ship.getHullSpec().getShipSystemId().isEmpty()){
            ShipSystemSpecAPI systemAPI = Global.getSettings().getShipSystemSpec(ship.getHullSpec().getShipSystemId());
            systemLoc= otherInfoTooltip.addPara(systemAPI.getName(), Color.ORANGE, 0f);;
            Description description = Global.getSettings().getDescription(systemAPI.getId(), Description.Type.SHIP_SYSTEM);
            if (description.hasText3()) {
                otherInfoTooltip.addPara(description.getText3(), 3f);
            } else {
                otherInfoTooltip.addPara(description.getText1(), 3f);
            }
        }
        else{
            systemLoc=otherInfoTooltip.addPara("None",3f);
        }


        String mounts = "";
        ArrayList<WeaponSlotAPI> energyWeapons = new ArrayList<>();
        ArrayList<WeaponSlotAPI> missileWeapons = new ArrayList<>();
        ArrayList<WeaponSlotAPI> ballisticWeapons = new ArrayList<>();
        ArrayList<WeaponSlotAPI> launchBay = new ArrayList<>();
        ArrayList<WeaponSlotAPI> universal = new ArrayList<>();
        ArrayList<WeaponSlotAPI> hybrid = new ArrayList<>();
        ArrayList<WeaponSlotAPI> synergy = new ArrayList<>();
        ArrayList<WeaponSlotAPI> composite = new ArrayList<>();
        ArrayList<WeaponSlotAPI> built_in = new ArrayList<>();
        for (WeaponSlotAPI weaponSlotAPI : ship.getHullSpec().getAllWeaponSlotsCopy()) {
            switch (weaponSlotAPI.getWeaponType()) {
                case BALLISTIC:
                    ballisticWeapons.add(weaponSlotAPI);
                    break;
                case ENERGY:
                    energyWeapons.add(weaponSlotAPI);
                    break;
                case MISSILE:
                    missileWeapons.add(weaponSlotAPI);
                    break;
                case LAUNCH_BAY:
                    launchBay.add(weaponSlotAPI);
                    break;
                case UNIVERSAL:
                    universal.add(weaponSlotAPI);
                    break;
                case HYBRID:
                    hybrid.add(weaponSlotAPI);
                    break;
                case SYNERGY:
                    synergy.add(weaponSlotAPI);
                    break;
                case COMPOSITE:
                    composite.add(weaponSlotAPI);
                    break;
                case BUILT_IN:
                    built_in.add(weaponSlotAPI);
                    break;
                default:
                    // Handle any other cases if needed
                    break;
            }
        }
        HashMap<WeaponAPI.WeaponSize, Integer> mapOfSizesEnergy = new HashMap<>();
        HashMap<WeaponAPI.WeaponSize, Integer> mapOfSizesMissile = new HashMap<>();
        HashMap<WeaponAPI.WeaponSize, Integer> mapOfSizesBallistic = new HashMap<>();
        HashMap<WeaponAPI.WeaponSize, Integer> mapOfSizesLaunchBay = new HashMap<>();
        HashMap<WeaponAPI.WeaponSize, Integer> mapOfSizesUniversal = new HashMap<>();
        HashMap<WeaponAPI.WeaponSize, Integer> mapOfSizesHybrid = new HashMap<>();
        HashMap<WeaponAPI.WeaponSize, Integer> mapOfSizesSynergy = new HashMap<>();
        HashMap<WeaponAPI.WeaponSize, Integer> mapOfSizesComposite = new HashMap<>();

        populateWeaponSizeMap(energyWeapons, mapOfSizesEnergy);
        populateWeaponSizeMap(missileWeapons, mapOfSizesMissile);
        populateWeaponSizeMap(ballisticWeapons, mapOfSizesBallistic);
        populateWeaponSizeMap(launchBay, mapOfSizesLaunchBay);
        populateWeaponSizeMap(universal, mapOfSizesUniversal);
        populateWeaponSizeMap(hybrid, mapOfSizesHybrid);
        populateWeaponSizeMap(synergy, mapOfSizesSynergy);
        populateWeaponSizeMap(composite, mapOfSizesComposite);

        ArrayList<Pair<String, String>> energyPairs = buildWeaponSizePairs(mapOfSizesEnergy, "Energy");
        ArrayList<Pair<String, String>> missilePairs = buildWeaponSizePairs(mapOfSizesMissile, "Missile");
        ArrayList<Pair<String, String>> ballisticPairs = buildWeaponSizePairs(mapOfSizesBallistic, "Ballistic");
        ArrayList<Pair<String, String>> launchBayPairs = buildWeaponSizePairs(mapOfSizesLaunchBay, "Launch Bay");
        ArrayList<Pair<String, String>> universalPairs = buildWeaponSizePairs(mapOfSizesUniversal, "Universal");
        ArrayList<Pair<String, String>> hybridPairs = buildWeaponSizePairs(mapOfSizesHybrid, "Hybrid");
        ArrayList<Pair<String, String>> synergyPairs = buildWeaponSizePairs(mapOfSizesSynergy, "Synergy");
        ArrayList<Pair<String, String>> compositePairs = buildWeaponSizePairs(mapOfSizesComposite, "Composite");
        LabelAPI mountsLabel=null;
        if(!isFighter){
            mountsLabel    = combineAndAddToTooltip(otherInfoTooltip, energyPairs, missilePairs, ballisticPairs, launchBayPairs, universalPairs, hybridPairs, synergyPairs, compositePairs);
        }

        LabelAPI armanantsLabel = processShipData(ship.getHullSpec(), otherInfoTooltip,isFighter);

        StringBuilder entries = new StringBuilder();
        ArrayList<String> hullmodNames = new ArrayList<>();
        int i = 0;
        for (String compositePair : ship.getVariant().getHullMods()) {
            if (!Global.getSettings().getHullModSpec(compositePair).isHiddenEverywhere()) {
                if(i<ship.getVariant().getHullMods().size()-1){
                    entries.append("%s, ");
                }
                else{
                    entries.append("%s ");
                }
                hullmodNames.add(Global.getSettings().getHullModSpec(compositePair).getDisplayName());
            }
            i++;

        }
        LabelAPI hullmodsLabel = null;
        if(!hullmodNames.isEmpty()){
            hullmodsLabel=  otherInfoTooltip.addPara(entries.toString(), 3f, Misc.getTextColor(), Misc.getTextColor(), hullmodNames.toArray(new String[0]));
        }
        else{
            hullmodsLabel=  otherInfoTooltip.addPara("None",3f);
        }
        nameOfLabelsTooltip.addPara("System:", 0f).getPosition().inTL(0, -systemLoc.getPosition().getY() - systemLoc.getPosition().getHeight());
        if(!isFighter){
            nameOfLabelsTooltip.addPara("Mounts:", 0f).getPosition().inTL(0, -mountsLabel.getPosition().getY() - mountsLabel.getPosition().getHeight());

        }
        nameOfLabelsTooltip.addPara("Armaments:", 0f).getPosition().inTL(0, -armanantsLabel.getPosition().getY() - armanantsLabel.getPosition().getHeight());


        nameOfLabelsTooltip.addPara("Hull mods:", 0f).getPosition().inTL(0, -hullmodsLabel.getPosition().getY() - hullmodsLabel.getPosition().getHeight());

        otherInfoPanel.getPosition().setSize(otherInfoPanel.getPosition().getWidth(), -hullmodsLabel.getPosition().getY() + 10);
        otherInfoPanel.addUIElement(nameOfLabelsTooltip).inTL(-2, 0);
        otherInfoPanel.addUIElement(otherInfoTooltip).inTL(120, 0);

        return otherInfoPanel;
    }

    static CustomPanelAPI generateLogisticalDataPanel(FleetMemberAPI ship) {
        ship.getVariant().clear();
        ship.getRepairTracker().setCR(70);
        CustomPanelAPI panelOfLogisticMain = Global.getSettings().createCustom(895, 185, null);
        TooltipMakerAPI tooltipOfLogisticSub = panelOfLogisticMain.createUIElement(505, 185, false);
        TooltipMakerAPI tooltipOfCombatPerfomance = panelOfLogisticMain.createUIElement(245, 185, false);


        Color colorBase = Global.getSector().getPlayerFaction().getBaseUIColor();
        Color darkUIColor = Global.getSector().getPlayerFaction().getDarkUIColor();
        tooltipOfLogisticSub.addSectionHeading("Logistical Data", colorBase, darkUIColor, Alignment.MID, tooltipOfLogisticSub.getWidthSoFar() - 2, 0f).getPosition().inTL(7, 0);
        tooltipOfCombatPerfomance.addSectionHeading("Combat Performance", colorBase, darkUIColor, Alignment.MID, tooltipOfCombatPerfomance.getWidthSoFar() - 15, 0f).getPosition().inTL(5, 0);
        CustomPanelAPI crewAndCRinfo = Global.getSettings().createCustom(250, 185, null);
        TooltipMakerAPI tooltipFirstRowLogistic = crewAndCRinfo.createUIElement(250, 185, false);

        CustomPanelAPI logisticInfo = Global.getSettings().createCustom(240, 185, null);
        TooltipMakerAPI tooltipSecondRowLogistic = logisticInfo.createUIElement(240, 185, false);
        ;


        ArrayList<StatNumberUIPackage> firstColumnLogisticData = getFirstRowLogistics(ship, tooltipFirstRowLogistic);
        ArrayList<StatNumberUIPackage> secondColumnLogisticData = getSecondRowOfLogistics(ship, tooltipSecondRowLogistic);
        ArrayList<StatNumberUIPackage> combatData = getCombatData(ship, tooltipOfCombatPerfomance);
        for (StatNumberUIPackage firstColumnLogisticDatum : firstColumnLogisticData) {
            firstColumnLogisticDatum.placeLabelToParent(tooltipFirstRowLogistic);
        }
        for (StatNumberUIPackage secondColumnLogisticDatum : secondColumnLogisticData) {
            secondColumnLogisticDatum.placeLabelToParent(tooltipSecondRowLogistic);
        }
        for (StatNumberUIPackage combatDaten : combatData) {
            combatDaten.placeLabelToParent(tooltipOfCombatPerfomance);
        }
        crewAndCRinfo.addUIElement(tooltipFirstRowLogistic).inTL(0, 0);
        logisticInfo.addUIElement(tooltipSecondRowLogistic).inTL(0, 0);
        tooltipOfLogisticSub.addCustom(crewAndCRinfo, 0f);
        tooltipOfLogisticSub.addCustomDoNotSetPosition(logisticInfo).getPosition().setLocation(0, 0).inTL(270, -crewAndCRinfo.getPosition().getY() - crewAndCRinfo.getPosition().getHeight() + 2f);

        panelOfLogisticMain.addUIElement(tooltipOfLogisticSub).inTL(0, 0);
        panelOfLogisticMain.addUIElement(tooltipOfCombatPerfomance).inTL(506, 0);


        return panelOfLogisticMain;
    }

    static LabelAPI combineAndAddToTooltip(TooltipMakerAPI tooltip, ArrayList<Pair<String, String>>... pairsLists) {
        StringBuilder textBuilder = new StringBuilder();
        ArrayList<String> highlights = new ArrayList<>();

        for (ArrayList<Pair<String, String>> pairsList : pairsLists) {
            for (Pair<String, String> pair : pairsList) {
                if (textBuilder.length() > 0) {
                    textBuilder.append(", ");
                }
                textBuilder.append("%s " + pair.two);
                highlights.add(pair.one);
            }
        }

        String text = textBuilder.toString();
        if(highlights.isEmpty()){
            return tooltip.addPara("None",3f);
        }
        return tooltip.addPara(text, 3f, Color.ORANGE, highlights.toArray(new String[0]));
    }

    static HashMap<String, Integer> countBuiltInWeapons(ShipHullSpecAPI ship,boolean isFighter) {
        HashMap<String, Integer> builtInWeaponsMap = new HashMap<>();

        if(isFighter){
            return FighterInfoRepo.getFromRepo(ship.getHullId()).getWeaponMap();
        }
        else{
            for (Map.Entry<String, String> builtInWeapon : ship.getBuiltInWeapons().entrySet()) {
                String weaponID = builtInWeapon.getValue();
                if (ship.getWeaponSlot(builtInWeapon.getKey()).isDecorative()) continue;
                if (ship.getWeaponSlot(builtInWeapon.getKey()).isStationModule()) continue;
                if (builtInWeaponsMap.containsKey(weaponID)) {
                    builtInWeaponsMap.put(weaponID, builtInWeaponsMap.get(weaponID) + 1);
                } else {
                    builtInWeaponsMap.put(weaponID, 1);
                }
            }
            for (String builtInWing : ship.getBuiltInWings()) {
                String weaponID = builtInWing;
                if (builtInWeaponsMap.containsKey(weaponID)) {
                    builtInWeaponsMap.put(weaponID, builtInWeaponsMap.get(weaponID) + 1);
                } else {
                    builtInWeaponsMap.put(weaponID, 1);
                }
            }
        }


        return builtInWeaponsMap;
    }

    static public LabelAPI processShipData(ShipHullSpecAPI ship, TooltipMakerAPI tooltip,boolean isFighter) {
        // Count built-in weapons
        HashMap<String, Integer> builtInWeaponsMap = countBuiltInWeapons(ship,isFighter);

        // Assuming you want to display this information similarly
        // Build the pairs for the built-in weapons
        ArrayList<Pair<String, String>> builtInPairs = buildWeaponNamePairs(builtInWeaponsMap);

        // Combine and add to tooltip (adjust as needed)
        return combineAndAddToTooltip(tooltip, builtInPairs);
    }

    // Method to build the pairs for the built-in weapons
    static ArrayList<Pair<String, String>> buildWeaponNamePairs(HashMap<String, Integer> weaponsMap) {
        ArrayList<Pair<String, String>> pairs = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : weaponsMap.entrySet()) {
            String countString = entry.getValue() + "x";
            try {
                if (Global.getSettings().getWeaponSpec(entry.getKey()) != null) {
                    String weaponName = Global.getSettings().getWeaponSpec(entry.getKey()).getWeaponName();
                    pairs.add(new Pair<String, String>(countString, weaponName));
                }
            } catch (Exception e) {
                FighterWingSpecAPI specAPI = Global.getSettings().getFighterWingSpec(entry.getKey());

                String weaponName = specAPI.getVariant().getFullDesignationWithHullName();
                pairs.add(new Pair<String, String>(countString, weaponName));
            }


        }

        return pairs;
    }

    // Method to combine the pairs into a single tooltip.addPara call

    @NotNull
    private static ArrayList<StatNumberUIPackage> getFirstRowLogistics(FleetMemberAPI ship, TooltipMakerAPI tooltipFirstRowLogistic) {
        ArrayList<StatNumberUIPackage> firstColumnLogisticData = new ArrayList<>();
        Color lightGreen = new Color(0, 222, 158);
        MutableStat stat;
        String text1 = (int) ship.getStats().getCRPerDeploymentPercent().computeEffective(ship.getHullSpec().getCRToDeploy()) + "";
        String text2 = getStringFromStat(ship.getHullSpec().getCRToDeploy(), ship.getStats().getCRPerDeploymentPercent().computeEffective(ship.getHullSpec().getCRToDeploy()));
        Color colorOfText2 = getColorForSecondText(ship.getHullSpec().getCRToDeploy(), ship.getStats().getCRPerDeploymentPercent().computeEffective(ship.getHullSpec().getCRToDeploy()), true);

        firstColumnLogisticData.add(new StatNumberUIPackage(tooltipFirstRowLogistic.addPara("CR per deployment", 5f), text1, text2, Misc.getTooltipTitleAndLightHighlightColor(), colorOfText2));

        stat = ship.getStats().getBaseCRRecoveryRatePercentPerDay();
        text1 = stat.getModifiedInt() + "";
        text2 = getStringFromStat(stat.getBaseValue(), stat.getModifiedValue());
        colorOfText2 = getColorForSecondText(stat.getBaseValue(), stat.getModifiedValue(), false);

        firstColumnLogisticData.add(new StatNumberUIPackage(tooltipFirstRowLogistic.addPara(" Recovery rate (per day)", 3f), text1, text2, Misc.getTooltipTitleAndLightHighlightColor(), colorOfText2));

        text1 = (int) ship.getDeploymentCostSupplies() + "";
        text2 = getStringFromStat(ship.getBaseDeploymentCostSupplies(), ship.getDeploymentCostSupplies());
        colorOfText2 = getColorForSecondText(ship.getBaseDeploymentCostSupplies(), ship.getDeploymentCostSupplies(), true);

        firstColumnLogisticData.add(new StatNumberUIPackage(tooltipFirstRowLogistic.addPara(" Recovery cost (supplies)", 3f), text1, text2, Misc.getTooltipTitleAndLightHighlightColor(), colorOfText2));

        text1 = (int) ship.getDeploymentPointsCost() + "";
        text2 = getStringFromStat(ship.getUnmodifiedDeploymentPointsCost(), ship.getDeploymentPointsCost());
        colorOfText2 = getColorForSecondText(ship.getUnmodifiedDeploymentPointsCost(), ship.getDeploymentPointsCost(), true);

        firstColumnLogisticData.add(new StatNumberUIPackage(tooltipFirstRowLogistic.addPara(" Deployment points", 3f), text1, text2, cyan, colorOfText2));

        text1 = (int) ship.getStats().getPeakCRDuration().computeEffective(ship.getHullSpec().getNoCRLossTime()) + "";
        text2 = getStringFromStat(ship.getHullSpec().getNoCRLossTime(), ship.getStats().getPeakCRDuration().computeEffective(ship.getHullSpec().getNoCRLossTime()));
        colorOfText2 = getColorForSecondText(ship.getHullSpec().getNoCRLossTime(), ship.getStats().getPeakCRDuration().computeEffective(ship.getHullSpec().getNoCRLossTime()), false);

        firstColumnLogisticData.add(new StatNumberUIPackage(tooltipFirstRowLogistic.addPara("Peak performance (sec)", 3f), text1, text2, Misc.getTooltipTitleAndLightHighlightColor(), colorOfText2));
        String textHelp = (int) (ship.getStats().getMinCrewMod().computeEffective(ship.getMinCrew())) + "";

        text1 = textHelp + " / " + textHelp;

        text2 = getStringFromStat(ship.getMinCrew(), ship.getStats().getMinCrewMod().computeEffective(ship.getMinCrew()));
        colorOfText2 = getColorForSecondText(ship.getMinCrew(), ship.getStats().getMinCrewMod().computeEffective(ship.getMinCrew()), true);
        firstColumnLogisticData.add(new StatNumberUIPackage(tooltipFirstRowLogistic.addPara("Crew complement", 3f), text1, text2, lightGreen, colorOfText2));
        tooltipFirstRowLogistic.addPara("", 3f);

        text1 = Misc.getHullSizeStr(ship.getHullSpec().getHullSize());
        firstColumnLogisticData.add(new StatNumberUIPackage(tooltipFirstRowLogistic.addPara("Hull size", 3f), text1, text2, Color.ORANGE, colorOfText2));

        text1 = ship.getHullSpec().getOrdnancePoints(null) + "";

        firstColumnLogisticData.add(new StatNumberUIPackage(tooltipFirstRowLogistic.addPara("Ordnance points", 3f), text1, text2, Color.ORANGE, colorOfText2));
        return firstColumnLogisticData;
    }

    @NotNull
    private static ArrayList<StatNumberUIPackage> getSecondRowOfLogistics(FleetMemberAPI ship, TooltipMakerAPI tooltipSecondRowOfLogistics) {
        ArrayList<StatNumberUIPackage> secondColumnData = new ArrayList<>();
        Color lightGreen = new Color(0, 222, 158);
        MutableStat stat = ship.getStats().getSuppliesPerMonth();
        String text1 = stat.getModifiedInt() + "";
        String text2 = getStringFromStat(stat.getBaseValue(), stat.getModifiedValue());
        Color colorOfText2 = getColorForSecondText(stat.getBaseValue(), stat.getModifiedValue(), true);

        secondColumnData.add(new StatNumberUIPackage(tooltipSecondRowOfLogistics.addPara("Maintenance (supplies/mo)", 3f), text1, text2, Misc.getTooltipTitleAndLightHighlightColor(), colorOfText2));

        text1 = (int) ship.getStats().getCargoMod().computeEffective(ship.getCargoCapacity()) + "";
        text2 = getStringFromStat(ship.getCargoCapacity(), ship.getStats().getCargoMod().computeEffective(ship.getCargoCapacity()));
        colorOfText2 = getColorForSecondText(ship.getCargoCapacity(), ship.getStats().getCargoMod().computeEffective(ship.getCargoCapacity()), false);
        secondColumnData.add(new StatNumberUIPackage(tooltipSecondRowOfLogistics.addPara("Cargo capacity", 3f), text1, text2, new Color(200, 200, 142), colorOfText2));

        text1 = (int) (ship.getStats().getMaxCrewMod().computeEffective(ship.getMaxCrew())) + "";
        text2 = getStringFromStat(ship.getMaxCrew(), ship.getStats().getMaxCrewMod().computeEffective(ship.getMaxCrew()));
        colorOfText2 = getColorForSecondText(ship.getMaxCrew(), ship.getStats().getMaxCrewMod().computeEffective(ship.getMaxCrew()), false);

        secondColumnData.add(new StatNumberUIPackage(tooltipSecondRowOfLogistics.addPara("Maximum crew", 3f), text1, text2, lightGreen, colorOfText2));

        text1 = (int) (ship.getStats().getMinCrewMod().computeEffective(ship.getMinCrew())) + "";
        text2 = getStringFromStat(ship.getMinCrew(), ship.getStats().getMinCrewMod().computeEffective(ship.getMinCrew()));
        colorOfText2 = getColorForSecondText(ship.getMinCrew(), ship.getStats().getMinCrewMod().computeEffective(ship.getMinCrew()), true);

        secondColumnData.add(new StatNumberUIPackage(tooltipSecondRowOfLogistics.addPara("Skeleton crew required", 3f), text1, text2, lightGreen, colorOfText2));
        text1 = (int) (ship.getStats().getFuelMod().computeEffective(ship.getFuelCapacity())) + "";
        text2 = getStringFromStat(ship.getFuelCapacity(), ship.getStats().getFuelMod().computeEffective(ship.getFuelCapacity()));
        colorOfText2 = getColorForSecondText(ship.getFuelCapacity(), ship.getStats().getFuelMod().computeEffective(ship.getFuelCapacity()), false);

        secondColumnData.add(new StatNumberUIPackage(tooltipSecondRowOfLogistics.addPara("Fuel capacity", 3f), text1, text2, new Color(236, 116, 23), colorOfText2));

        stat = ship.getStats().getMaxBurnLevel();
        text1 = stat.getModifiedInt() + "";
        text2 = getStringFromStat(stat.getBaseValue(), stat.getModifiedValue());
        colorOfText2 = getColorForSecondText(stat.getBaseValue(), stat.getModifiedValue(), false);
        secondColumnData.add(new StatNumberUIPackage(tooltipSecondRowOfLogistics.addPara("Maximum burn", 3f), text1, text2, Color.ORANGE, colorOfText2));


        text1 = (int) (ship.getStats().getFuelUseMod().computeEffective(ship.getFuelUse())) + "";
        text2 = getStringFromStat(ship.getFuelUse(), ship.getStats().getFuelUseMod().computeEffective(ship.getFuelUse()));
        colorOfText2 = getColorForSecondText(ship.getFuelUse(), ship.getStats().getFuelUseMod().computeEffective(ship.getFuelUse()), true);
        secondColumnData.add(new StatNumberUIPackage(tooltipSecondRowOfLogistics.addPara("Fuel / light year, jump cost", 3f), text1, text2, Color.ORANGE, colorOfText2));


        stat = ship.getStats().getSensorProfile();
        text1 = stat.getModifiedInt() + "";
        text2 = getStringFromStat(stat.getBaseValue(), stat.getModifiedValue());
        colorOfText2 = getColorForSecondText(stat.getBaseValue(), stat.getModifiedValue(), true);
        secondColumnData.add(new StatNumberUIPackage(tooltipSecondRowOfLogistics.addPara("Sensor profile", 3f), text1, text2, Color.ORANGE, colorOfText2));

        stat = ship.getStats().getSensorStrength();
        text1 = stat.getModifiedInt() + "";
        text2 = getStringFromStat(stat.getBaseValue(), stat.getModifiedValue());
        colorOfText2 = getColorForSecondText(stat.getBaseValue(), stat.getModifiedValue(), false);
        secondColumnData.add(new StatNumberUIPackage(tooltipSecondRowOfLogistics.addPara("Sensor strength", 3f), text1, text2, Color.ORANGE, colorOfText2));
        return secondColumnData;
    }

    static ArrayList<Pair<String, String>> buildWeaponSizePairs(HashMap<WeaponAPI.WeaponSize, Integer> sizeMap, String weaponType) {
        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        if(weaponType.toLowerCase().contains("launch bay")){
            int  amount = 0;
            for (Map.Entry<WeaponAPI.WeaponSize, Integer> entry : sizeMap.entrySet()) {
                amount+=entry.getValue();
            }
            String countString =  amount + "x";
            pairs.add(new Pair<String, String>(countString, "Fighter bay"));
        }
        else{
            for (Map.Entry<WeaponAPI.WeaponSize, Integer> entry : sizeMap.entrySet()) {
                String countString = entry.getValue() + "x";
                String titleString = entry.getKey().getDisplayName() + " " + weaponType;
                pairs.add(new Pair<String, String>(countString, titleString));
            }
        }


        return pairs;
    }

    @NotNull
    private static ArrayList<StatNumberUIPackage> getCombatData(FleetMemberAPI ship, TooltipMakerAPI tooltipSecondRowOfLogistics) {
        ArrayList<StatNumberUIPackage> combatColumnData = new ArrayList<>();
        Color lightGreen = new Color(0, 222, 158);
        MutableStat stat = null;
        String text1 = (int) ship.getHullSpec().getHitpoints() + "";
        String text2 = null;
        Color colorOfText2 = null;
        combatColumnData.add(new StatNumberUIPackage(tooltipSecondRowOfLogistics.addPara("Hull integrity", 5f), text1, text2, Color.ORANGE, colorOfText2));

        text1 = AoTDMisc.getNumberString(ship.getStats().getArmorBonus().computeEffective(ship.getHullSpec().getArmorRating())) + "";

        text2 = getStringFromStat(ship.getHullSpec().getArmorRating(), ship.getStats().getArmorBonus().computeEffective(ship.getHullSpec().getArmorRating()));
        colorOfText2 = getColorForSecondText(ship.getHullSpec().getArmorRating(), ship.getStats().getArmorBonus().computeEffective(ship.getHullSpec().getArmorRating()), false);
        combatColumnData.add(new StatNumberUIPackage(tooltipSecondRowOfLogistics.addPara("Armor rating", 3f), text1, text2, Color.ORANGE, colorOfText2));
        ShieldAPI.ShieldType type = ship.getHullSpec().getShieldType();

        if (type.equals(ShieldAPI.ShieldType.NONE)) {
            combatColumnData.add(new StatNumberUIPackage(tooltipSecondRowOfLogistics.addPara("Defence", 3f), "None", null, Color.ORANGE, colorOfText2));
            for (int i = 0; i < 3; i++) {
                tooltipSecondRowOfLogistics.addPara("", 3f);
            }
        } else if (type.equals(ShieldAPI.ShieldType.PHASE)) {
            combatColumnData.add(new StatNumberUIPackage(tooltipSecondRowOfLogistics.addPara("Defence", 3f), "Phase Cloak", null, Color.ORANGE, colorOfText2));

            text1 =  AoTDMisc.getNumberString(ship.getStats().getPhaseCloakActivationCostBonus().computeEffective(ship.getHullSpec().getShieldSpec().getPhaseCost())) + "";
            text2 = getStringFromStat(ship.getHullSpec().getShieldSpec().getPhaseCost(), ship.getStats().getPhaseCloakActivationCostBonus().computeEffective(ship.getHullSpec().getShieldSpec().getPhaseCost()));
            colorOfText2 = getColorForSecondText(ship.getHullSpec().getShieldSpec().getPhaseCost(), ship.getStats().getPhaseCloakActivationCostBonus().computeEffective(ship.getHullSpec().getShieldSpec().getPhaseCost()), true);
            combatColumnData.add(new StatNumberUIPackage(tooltipSecondRowOfLogistics.addPara("Cloak activation cost", 3f), text1, text2, Color.ORANGE, colorOfText2));

            text1 = (int) ship.getStats().getPhaseCloakUpkeepCostBonus().computeEffective(ship.getHullSpec().getShieldSpec().getPhaseUpkeep()) + "";
            text2 = getStringFromStat(ship.getHullSpec().getShieldSpec().getPhaseUpkeep(), ship.getStats().getPhaseCloakUpkeepCostBonus().computeEffective(ship.getHullSpec().getShieldSpec().getPhaseCost()));
            colorOfText2 = getColorForSecondText(ship.getHullSpec().getShieldSpec().getPhaseUpkeep(), ship.getStats().getPhaseCloakUpkeepCostBonus().computeEffective(ship.getHullSpec().getShieldSpec().getPhaseCost()), true);
            combatColumnData.add(new StatNumberUIPackage(tooltipSecondRowOfLogistics.addPara("Cloak upkeep/sec", 3f), text1, text2, Color.ORANGE, colorOfText2));
            tooltipSecondRowOfLogistics.addPara("", 3f);


        } else {
            String nameOfShield = "Omni Shield";
            if (type.equals(ShieldAPI.ShieldType.FRONT)) {
                nameOfShield = "Front Shield";
            }
            combatColumnData.add(new StatNumberUIPackage(tooltipSecondRowOfLogistics.addPara("Defence", 3f), nameOfShield, null, Color.ORANGE, colorOfText2));
            ShipHullSpecAPI.ShieldSpecAPI shieldSpecAPI = ship.getHullSpec().getShieldSpec();
            text1 =  AoTDMisc.getNumberString( ship.getStats().getShieldArcBonus().computeEffective(shieldSpecAPI.getArc())) + "";
            text2 = getStringFromStat(shieldSpecAPI.getArc(), ship.getStats().getShieldArcBonus().computeEffective(shieldSpecAPI.getArc()));
            colorOfText2 = getColorForSecondText(shieldSpecAPI.getArc(), ship.getStats().getShieldArcBonus().computeEffective(shieldSpecAPI.getArc()), false);
            combatColumnData.add(new StatNumberUIPackage(tooltipSecondRowOfLogistics.addPara("Shield arc", 3f), text1, text2, Color.ORANGE, colorOfText2));

            text1 =   AoTDMisc.getNumberString(shieldSpecAPI.getUpkeepCost() * ship.getStats().getShieldUpkeepMult().getModifiedValue()) + "";
            text2 = getStringFromStat(shieldSpecAPI.getUpkeepCost(), shieldSpecAPI.getUpkeepCost() * ship.getStats().getShieldUpkeepMult().getModifiedValue());
            colorOfText2 = getColorForSecondText(shieldSpecAPI.getUpkeepCost(), shieldSpecAPI.getUpkeepCost() * ship.getStats().getShieldUpkeepMult().getModifiedValue(), true);
            combatColumnData.add(new StatNumberUIPackage(tooltipSecondRowOfLogistics.addPara("Shield upkeep/sec", 3f), text1, text2, Color.ORANGE, colorOfText2));

            text1 = AoTDMisc.getNumberString(shieldSpecAPI.getFluxPerDamageAbsorbed() * ship.getStats().getShieldDamageTakenMult().getModifiedValue());
            text2 = getStringFromStatFloat(shieldSpecAPI.getFluxPerDamageAbsorbed(), shieldSpecAPI.getFluxPerDamageAbsorbed() * ship.getStats().getShieldDamageTakenMult().getModifiedValue());
            colorOfText2 = getColorForSecondText(shieldSpecAPI.getFluxPerDamageAbsorbed(), shieldSpecAPI.getUpkeepCost() * ship.getStats().getShieldDamageTakenMult().getModifiedValue(), true);

            combatColumnData.add(new StatNumberUIPackage(tooltipSecondRowOfLogistics.addPara("Shield flux/damage", 3f), text1, text2, Color.ORANGE, colorOfText2));


        }
        stat = ship.getStats().getFluxCapacity();
        text1 = stat.getModifiedInt() + "";
        text2 = getStringFromStat(stat.getBaseValue(), stat.getModifiedValue());
        colorOfText2 = getColorForSecondText(stat.getBaseValue(), stat.getModifiedValue(), false);
        combatColumnData.add(new StatNumberUIPackage(tooltipSecondRowOfLogistics.addPara("Flux capacity", 3f), text1, text2, Color.ORANGE, colorOfText2));

        stat = ship.getStats().getFluxDissipation();
        text1 = stat.getModifiedInt() + "";
        text2 = getStringFromStat(stat.getBaseValue(), stat.getModifiedValue());
        colorOfText2 = getColorForSecondText(stat.getBaseValue(), stat.getModifiedValue(), false);
        combatColumnData.add(new StatNumberUIPackage(tooltipSecondRowOfLogistics.addPara("Flux dissipation", 3f), text1, text2, Color.ORANGE, colorOfText2));

        stat = ship.getStats().getMaxSpeed();
        text1 = stat.getModifiedInt() + "";
        text2 = getStringFromStat(stat.getBaseValue(), stat.getModifiedValue());
        colorOfText2 = getColorForSecondText(stat.getBaseValue(), stat.getModifiedValue(), false);
        combatColumnData.add(new StatNumberUIPackage(tooltipSecondRowOfLogistics.addPara("Top speed", 3f), text1, text2, Color.ORANGE, colorOfText2));
        return combatColumnData;
    }

    private static String getStringFromStat(float base, float modified) {
        int diff = (int) base - (int) modified;
        if (diff == 0) {
            return null;
        }
        if (diff < 0) {
            return "(+" + AoTDMisc.getNumberString(Math.abs(diff)) + ")";
        } else {
            return "(-" +  AoTDMisc.getNumberString(Math.abs(diff)) + ")";
        }
    }



    static void populateWeaponSizeMap(ArrayList<WeaponSlotAPI> weaponList, HashMap<WeaponAPI.WeaponSize, Integer> sizeMap) {
        for (WeaponSlotAPI weaponSpec : weaponList) {
            WeaponAPI.WeaponSize size = weaponSpec.getSlotSize();
            if (sizeMap.containsKey(size)) {
                sizeMap.put(size, sizeMap.get(size) + 1);
            } else {
                sizeMap.put(size, 1);
            }
        }
    }

    public static Pair<CustomPanelAPI,ShipRenderer> getShipImage(ShipHullSpecAPI specAPI, float iconSize,Color colorOverride) {
        ShipRenderer renderer = new ShipRenderer();
        CustomPanelAPI panelHolder = Global.getSettings().createCustom(iconSize, iconSize, renderer);
        TooltipMakerAPI tooltip = panelHolder.createUIElement(iconSize, iconSize, false);


        // Get the sprite using the spec API
        SpriteAPI shipSprite = Global.getSettings().getSprite(specAPI.getSpriteName());
        shipSprite.setAlphaMult(0f);
        float originalWidth = shipSprite.getWidth();
        float originalHeight = shipSprite.getHeight();
        // Get the original width and height of the sprite


        // Calculate the aspect ratio
        float aspectRatio = originalWidth / originalHeight;

        // Variables for the new width and height
        float newWidth, newHeight;

        // Determine which dimension to resize to fit the icon size
        if (originalWidth <= iconSize && originalHeight <= iconSize) {
            newWidth = originalWidth;
            newHeight = originalHeight;
        } else {
            if (originalWidth > originalHeight) {
                // Width is the larger dimension
                newWidth = iconSize;
                newHeight = iconSize / aspectRatio;
            } else {
                // Height is the larger dimension or they are equal
                newHeight = iconSize;
                newWidth = iconSize * aspectRatio;
            }
        }
        if (aspectRatio >= 1) {
            if (originalWidth > originalHeight) {
                // Width is the larger dimension
                newWidth = iconSize * 0.8f;
                newHeight = iconSize * 0.8f / aspectRatio;
            } else {
                // Height is the larger dimension or they are equal
                newHeight = iconSize * 0.8f;
                newWidth = iconSize * 0.8f * aspectRatio;
            }
        }
        if (specAPI.getHullSize().equals(ShipAPI.HullSize.FRIGATE)) {
            if (originalWidth > originalHeight) {
                // Width is the larger dimension
                newWidth = iconSize * 0.4f;
                newHeight = iconSize * 0.4f / aspectRatio;
            } else {
                // Height is the larger dimension or they are equal
                newHeight = iconSize * 0.4f;
                newWidth = iconSize * 0.4f * aspectRatio;
            }
        }
        if (specAPI.getHullSize().equals(ShipAPI.HullSize.FRIGATE)) {
            if (originalWidth > originalHeight) {
                // Width is the larger dimension
                newWidth = iconSize * 0.4f;
                newHeight = iconSize * 0.4f / aspectRatio;
            } else {
                // Height is the larger dimension or they are equal
                newHeight = iconSize * 0.4f;
                newWidth = iconSize * 0.4f * aspectRatio;
            }
        }
        if (specAPI.getHullSize().equals(ShipAPI.HullSize.DESTROYER)) {
            if (originalWidth > originalHeight) {
                // Width is the larger dimension
                newWidth = iconSize * 0.6f;
                newHeight = iconSize * 0.6f / aspectRatio;
            } else {
                // Height is the larger dimension or they are equal
                newHeight = iconSize * 0.6f;
                newWidth = iconSize * 0.6f * aspectRatio;
            }
        }
        if (specAPI.getHullSize().equals(ShipAPI.HullSize.CRUISER)) {
            if (originalWidth > originalHeight) {
                // Width is the larger dimension
                newWidth = iconSize * 0.8f;
                newHeight = iconSize * 0.8f / aspectRatio;
            } else {
                // Height is the larger dimension or they are equal
                newHeight = iconSize * 0.8f;
                newWidth = iconSize * 0.8f * aspectRatio;
            }
        }

        boolean modular = false;
        for (ShipHullSpecAPI.ShipTypeHints hint : specAPI.getHints()) {
            if (hint.equals(ShipHullSpecAPI.ShipTypeHints.SHIP_WITH_MODULES) || hint.equals(ShipHullSpecAPI.ShipTypeHints.STATION)) {
                modular = true;
                break;
            }
        }
        ShipRenderInfo info = ShipRenderInfoRepo.renderInfoRepo.get(specAPI.getHullId());
        if(info==null)       {
            try {
                //We sometimes miss during loading , this is to insure that we wont have blank ( at least as long as worst case scenario has not been met)
                ShipRenderInfoRepo.populateShip(specAPI);
                info = ShipRenderInfoRepo.renderInfoRepo.get(specAPI.getHullId());
            }
            catch (Exception e ){
                return new Pair<>(panelHolder,renderer);
            }
        }
        if (modular) {

            try {
                float scale = 1f;
                Pair<HashMap<CustomPanelAPI, ShipRenderInfo.Module>, CustomPanelAPI> data = (generateImageOfModularShip(specAPI, panelHolder, info, scale));
                HashMap<CustomPanelAPI, ShipRenderInfo.Module> imagePanels = new HashMap<>();
                imagePanels.putAll(data.one);

                float minX = 0;
                float maxX = 0;
                float minY = 0;
                float maxY = 0;
                for (Map.Entry<CustomPanelAPI, ShipRenderInfo.Module> imagePanel : imagePanels.entrySet()) {
                    if (imagePanel.getKey().getPosition().getX() <= minX) {
                        minX = imagePanel.getKey().getPosition().getX();
                    }
                    if (imagePanel.getKey().getPosition().getX() + imagePanel.getKey().getPosition().getWidth() >= maxX) {
                        maxX = imagePanel.getKey().getPosition().getX() + imagePanel.getKey().getPosition().getWidth();
                    }
                    if (imagePanel.getKey().getPosition().getY() + imagePanel.getKey().getPosition().getHeight() >= maxY) {
                        maxY = imagePanel.getKey().getPosition().getY() + imagePanel.getKey().getPosition().getHeight();
                    }
                    if (imagePanel.getKey().getPosition().getY() <= minY) {
                        minY = imagePanel.getKey().getPosition().getY();
                    }
                }
                float widthCombined = maxX - minX;
                float heightCombined = maxY - minY;

                aspectRatio = widthCombined / heightCombined;

                if (widthCombined <= iconSize && heightCombined <= iconSize) {
                    newWidth = widthCombined;
                    newHeight = heightCombined;
                } else {
                    if (widthCombined > heightCombined) {
                        // Width is the larger dimension
                        newWidth = iconSize;
                        newHeight = iconSize / aspectRatio;
                    } else {
                        // Height is the larger dimension or they are equal
                        newHeight = iconSize;
                        newWidth = iconSize * aspectRatio;
                    }
                }

                scale = newHeight / heightCombined;
                Pair<HashMap<CustomPanelAPI, ShipRenderInfo.Module>, CustomPanelAPI> data2 = (generateImageOfModularShipSized(specAPI, panelHolder, info, scale, newWidth, newHeight));
                panelHolder.getPosition().setSize(iconSize, iconSize);
                ;
                float remainingX = iconSize - data2.two.getPosition().getWidth();
                float remainingY = iconSize - data2.two.getPosition().getHeight();
                tooltip.addCustom(data2.two, 0f).getPosition().inTL(remainingX / 2, remainingY / 2);
                renderer.setScale(scale);
                renderer.setPartsOfShip(data2.one, data2.two);
                renderer.setCollorOverride(colorOverride);
                renderer.setStencilMaskBorder(panelHolder);

                panelHolder.addUIElement(tooltip).inTL(0, 0);
                return new Pair<>(panelHolder,renderer);
            }
            catch (Exception e ){
                return getShipImage(Global.getSettings().getHullSpec("onslaught"),iconSize,Color.red);
            }



        }

        if(info!=null){
            float remainingX = iconSize - newWidth;
            float remainingY = iconSize - newHeight;
            float scale = newWidth / originalWidth;

            ShipRenderInfo.Module centralModule = info.getCentralModule() ;
            CustomPanelAPI center = panelHolder.createCustomPanel((float) (info.width * scale), (float) (info.height * scale), null);
            panelHolder.addComponent(center).inTL(remainingX / 2, remainingY / 2);
            HashMap<CustomPanelAPI, ShipRenderInfo.Module> renderingMap = new HashMap<>();
            renderingMap.put(center, centralModule);
            renderer.setScale(scale);
            renderer.setCollorOverride(colorOverride);
            renderer.setPartsOfShip(renderingMap, center);
        }

        return new Pair<>(panelHolder,renderer);
    }
    public static Pair<CustomPanelAPI,ShipRenderer> getShipImage(ShipHullSpecAPI specAPI, float iconSize,Color colorOverride,float percentageOfShipRendered) {
        ShipRenderer renderer = new ShipRenderer();
        CustomPanelAPI panelHolder = Global.getSettings().createCustom(iconSize, iconSize, renderer);
        TooltipMakerAPI tooltip = panelHolder.createUIElement(iconSize, iconSize, false);


        // Get the sprite using the spec API
        SpriteAPI shipSprite = Global.getSettings().getSprite(specAPI.getSpriteName());
        shipSprite.setAlphaMult(0f);
        float originalWidth = shipSprite.getWidth();
        float originalHeight = shipSprite.getHeight();
        // Get the original width and height of the sprite


        // Calculate the aspect ratio
        float aspectRatio = originalWidth / originalHeight;

        // Variables for the new width and height
        float newWidth, newHeight;

        // Determine which dimension to resize to fit the icon size
        if (originalWidth <= iconSize && originalHeight <= iconSize) {
            newWidth = originalWidth;
            newHeight = originalHeight;
        } else {
            if (originalWidth > originalHeight) {
                // Width is the larger dimension
                newWidth = iconSize;
                newHeight = iconSize / aspectRatio;
            } else {
                // Height is the larger dimension or they are equal
                newHeight = iconSize;
                newWidth = iconSize * aspectRatio;
            }
        }
        if (aspectRatio >= 1) {
            if (originalWidth > originalHeight) {
                // Width is the larger dimension
                newWidth = iconSize * 0.8f;
                newHeight = iconSize * 0.8f / aspectRatio;
            } else {
                // Height is the larger dimension or they are equal
                newHeight = iconSize * 0.8f;
                newWidth = iconSize * 0.8f * aspectRatio;
            }
        }
        if (specAPI.getHullSize().equals(ShipAPI.HullSize.FRIGATE)) {
            if (originalWidth > originalHeight) {
                // Width is the larger dimension
                newWidth = iconSize * 0.4f;
                newHeight = iconSize * 0.4f / aspectRatio;
            } else {
                // Height is the larger dimension or they are equal
                newHeight = iconSize * 0.4f;
                newWidth = iconSize * 0.4f * aspectRatio;
            }
        }
        if (specAPI.getHullSize().equals(ShipAPI.HullSize.FRIGATE)) {
            if (originalWidth > originalHeight) {
                // Width is the larger dimension
                newWidth = iconSize * 0.4f;
                newHeight = iconSize * 0.4f / aspectRatio;
            } else {
                // Height is the larger dimension or they are equal
                newHeight = iconSize * 0.4f;
                newWidth = iconSize * 0.4f * aspectRatio;
            }
        }
        if (specAPI.getHullSize().equals(ShipAPI.HullSize.DESTROYER)) {
            if (originalWidth > originalHeight) {
                // Width is the larger dimension
                newWidth = iconSize * 0.6f;
                newHeight = iconSize * 0.6f / aspectRatio;
            } else {
                // Height is the larger dimension or they are equal
                newHeight = iconSize * 0.6f;
                newWidth = iconSize * 0.6f * aspectRatio;
            }
        }
        if (specAPI.getHullSize().equals(ShipAPI.HullSize.CRUISER)) {
            if (originalWidth > originalHeight) {
                // Width is the larger dimension
                newWidth = iconSize * 0.8f;
                newHeight = iconSize * 0.8f / aspectRatio;
            } else {
                // Height is the larger dimension or they are equal
                newHeight = iconSize * 0.8f;
                newWidth = iconSize * 0.8f * aspectRatio;
            }
        }

        boolean modular = false;
        for (ShipHullSpecAPI.ShipTypeHints hint : specAPI.getHints()) {
            if (hint.equals(ShipHullSpecAPI.ShipTypeHints.SHIP_WITH_MODULES) || hint.equals(ShipHullSpecAPI.ShipTypeHints.STATION)) {
                modular = true;
                break;
            }
        }
        ShipRenderInfo info = ShipRenderInfoRepo.renderInfoRepo.get(specAPI.getHullId());
        if(info==null)       {
            try {
                //We sometimes miss during loading , this is to insure that we wont have blank ( at least as long as worst case scenario has not been met)
                ShipRenderInfoRepo.populateShip(specAPI);
                info = ShipRenderInfoRepo.renderInfoRepo.get(specAPI.getHullId());
            }
            catch (Exception e ){
                return new Pair<>(panelHolder,renderer);
            }
        }
        if (modular) {

            float scale = 1f;
            Pair<HashMap<CustomPanelAPI, ShipRenderInfo.Module>, CustomPanelAPI> data = (generateImageOfModularShip(specAPI, panelHolder, info, scale));
            HashMap<CustomPanelAPI, ShipRenderInfo.Module> imagePanels = new HashMap<>();
            imagePanels.putAll(data.one);

            float minX = 0;
            float maxX = 0;
            float minY = 0;
            float maxY = 0;
            for (Map.Entry<CustomPanelAPI, ShipRenderInfo.Module> imagePanel : imagePanels.entrySet()) {
                if (imagePanel.getKey().getPosition().getX() <= minX) {
                    minX = imagePanel.getKey().getPosition().getX();
                }
                if (imagePanel.getKey().getPosition().getX() + imagePanel.getKey().getPosition().getWidth() >= maxX) {
                    maxX = imagePanel.getKey().getPosition().getX() + imagePanel.getKey().getPosition().getWidth();
                }
                if (imagePanel.getKey().getPosition().getY() + imagePanel.getKey().getPosition().getHeight() >= maxY) {
                    maxY = imagePanel.getKey().getPosition().getY() + imagePanel.getKey().getPosition().getHeight();
                }
                if (imagePanel.getKey().getPosition().getY() <= minY) {
                    minY = imagePanel.getKey().getPosition().getY();
                }
            }
            float widthCombined = maxX - minX;
            float heightCombined = maxY - minY;

            aspectRatio = widthCombined / heightCombined;

            if (widthCombined <= iconSize && heightCombined <= iconSize) {
                newWidth = widthCombined;
                newHeight = heightCombined;
            } else {
                if (widthCombined > heightCombined) {
                    // Width is the larger dimension
                    newWidth = iconSize;
                    newHeight = iconSize / aspectRatio;
                } else {
                    // Height is the larger dimension or they are equal
                    newHeight = iconSize;
                    newWidth = iconSize * aspectRatio;
                }
            }

            scale = newHeight / heightCombined;
            Pair<HashMap<CustomPanelAPI, ShipRenderInfo.Module>, CustomPanelAPI> data2 = (generateImageOfModularShipSized(specAPI, panelHolder, info, scale, newWidth, newHeight));
            panelHolder.getPosition().setSize(iconSize, iconSize);
            ;
            float remainingX = iconSize - data2.two.getPosition().getWidth();
            float remainingY = iconSize - data2.two.getPosition().getHeight();
            tooltip.addCustom(data2.two, 0f).getPosition().inTL(remainingX / 2, remainingY / 2);
            renderer.setScale(scale);
            renderer.setPartsOfShip(data2.one, data2.two);
            renderer.setCollorOverride(colorOverride);
            renderer.setStencilMaskBorder(panelHolder);
            renderer.setUsingStencil(true);
            renderer.setRenderingPercentage(percentageOfShipRendered);
            panelHolder.addUIElement(tooltip).inTL(0, 0);
            return new Pair<>(panelHolder,renderer);


        }

        if(info!=null){
            float remainingX = iconSize - newWidth;
            float remainingY = iconSize - newHeight;
            float scale = newWidth / originalWidth;

            ShipRenderInfo.Module centralModule = info.createModule(info.center, info.width, info.height, info.center, specAPI.getHullId(), -1);
            CustomPanelAPI center = panelHolder.createCustomPanel((float) (info.width * scale), (float) (info.height * scale), null);
            panelHolder.addComponent(center).inTL(remainingX / 2, remainingY / 2);
            HashMap<CustomPanelAPI, ShipRenderInfo.Module> renderingMap = new HashMap<>();
            renderingMap.put(center, centralModule);
            renderer.setScale(scale);
            renderer.setCollorOverride(colorOverride);
            renderer.setPartsOfShip(renderingMap, center);
            renderer.setStencilMaskBorder(panelHolder);
            renderer.setUsingStencil(true);
            renderer.setRenderingPercentage(percentageOfShipRendered);
        }

        return new Pair<>(panelHolder,renderer);
    }
    private static Pair<HashMap<CustomPanelAPI, ShipRenderInfo.Module>, CustomPanelAPI> generateImageOfModularShip(ShipHullSpecAPI specAPI, CustomPanelAPI panelHolder, ShipRenderInfo info, float scale) {
        CustomPanelAPI centralPanel = panelHolder.createCustomPanel((float) info.width * scale, (float) info.height * scale, null);
        float translatedY = (float) (info.height * scale - info.center.getY() * scale);


        float posX = info.center.x * scale;
        float posY = translatedY;
        ShipRenderInfo.Module centralModule = info.getCentralModule();
        HashMap<CustomPanelAPI, ShipRenderInfo.Module> mapOfRendering = new HashMap<>();
        mapOfRendering.put(centralPanel, centralModule);
        for (ShipRenderInfo.Module module : info.moduleSlotsOnOriginalShip) {
            CustomPanelAPI panel = centralPanel.createCustomPanel((float) module.width * scale, (float) module.height * scale, null);
            float translatedYMod = (float) (module.height * scale - module.center.getY() * scale);
            float posXMod = -(module.center.x) * scale;
            float posYMod = -(translatedYMod);

            float centerX = -module.slotOnOriginal.locationOnShip.getX() * scale;
            float centerY = -module.slotOnOriginal.locationOnShip.getY() * scale;
            centralPanel.addComponent(panel).inTL(centerX + posX + posXMod, centerY + posY + posYMod);

            mapOfRendering.put(panel, module);
        }
        return new Pair<>(mapOfRendering, centralPanel);
    }

    private static Pair<HashMap<CustomPanelAPI, ShipRenderInfo.Module>, CustomPanelAPI> generateImageOfModularShipSized(ShipHullSpecAPI specAPI, CustomPanelAPI panelHolder, ShipRenderInfo info, float scale, float totalWidth, float totalHeight) {
        CustomPanelAPI mainCentralPanel = panelHolder.createCustomPanel(totalWidth, totalHeight, null);
        CustomPanelAPI centralPanel = mainCentralPanel.createCustomPanel((float) info.width * scale, (float) info.height * scale, null);

        float translatedY = (float) (info.height * scale - info.center.getY() * scale);
        float posX = info.center.x * scale;
        float posY = translatedY;
        float furtherstXLeft = 0f;
        float furtherstYTop = 0f;
        float furtherstXRight = 0f;
        float furtherstYBot = 0f;

        ShipRenderInfo.Module centralModule = info.getCentralModule();
        HashMap<CustomPanelAPI, ShipRenderInfo.Module> mapOfRendering = new HashMap<>();
        mapOfRendering.put(centralPanel, centralModule);
        for (ShipRenderInfo.Module module : info.moduleSlotsOnOriginalShip) {
            CustomPanelAPI panel = centralPanel.createCustomPanel((float) module.width * scale, (float) module.height * scale, null);
            float translatedYMod = (float) (module.height * scale - module.center.getY() * scale);
            float posXMod = -(module.center.x) * scale;
            float posYMod = -(translatedYMod);

            float centerX = -module.slotOnOriginal.locationOnShip.getX() * scale;
            float centerY = -module.slotOnOriginal.locationOnShip.getY() * scale;
            if (furtherstXLeft >= centerX + posX + posXMod) {
                furtherstXLeft = centerX + posX + posXMod;
            }
            if (furtherstYTop >= centerY + posY + posYMod) {
                furtherstYTop = centerY + posY + posYMod;
            }
            if (furtherstXRight <= centerX + posX + posXMod) {
                furtherstXRight = centerX + posX + posXMod;
            }
            if (furtherstYBot <= centerY + posY + posYMod) {
                furtherstYBot = centerY + posY + posYMod;
            }

            centralPanel.addComponent(panel).inTL(centerX + posX + posXMod, centerY + posY + posYMod);

            mapOfRendering.put(panel, module);
        }
        float calculatedHeight = furtherstYBot - furtherstYTop;
        float calcuatedWidth = furtherstXRight - furtherstXLeft;

        float diffX = totalWidth - calcuatedWidth + furtherstXLeft;
        float diffY = totalHeight - calculatedHeight + furtherstYTop;
        mainCentralPanel.addComponent(centralPanel).inTL(-furtherstXLeft, -furtherstYTop);
        return new Pair<>(mapOfRendering, mainCentralPanel);
    }

}
