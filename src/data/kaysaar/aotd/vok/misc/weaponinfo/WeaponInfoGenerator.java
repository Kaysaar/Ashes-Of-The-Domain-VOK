package data.kaysaar.aotd.vok.misc.weaponinfo;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.loading.*;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.misc.shipinfo.StatNumberUIPackage;

import java.awt.*;
import java.util.ArrayList;

public class WeaponInfoGenerator {
    public static void generate(TooltipMakerAPI tooltip, WeaponSpecAPI weapon, float widthOfTooltip) {
        tooltip.addTitle(weapon.getWeaponName());
        Misc.addDesignTypePara(tooltip, weapon.getManufacturer(), 10f);
        Description desc = Global.getSettings().getDescription(weapon.getWeaponId(), Description.Type.WEAPON);
        if (desc.hasText1()) {
            tooltip.addPara(desc.getText1FirstPara(), 10f);
        }
        if (desc.hasText2()) {
            tooltip.addPara(desc.getText2(), Misc.getGrayColor(), 2f);
        }
        tooltip.addSectionHeading("Primary Data", Alignment.MID, 10f);
        CustomPanelAPI firstRow = Global.getSettings().createCustom(widthOfTooltip, 100, null);
        WeaponSpriteRenderer renderer = new WeaponSpriteRenderer(weapon, 80, 0);
        CustomPanelAPI imageRow = firstRow.createCustomPanel(100, 100, renderer);
        float widthOfText = widthOfTooltip - 95;
        CustomPanelAPI dataRow = firstRow.createCustomPanel(widthOfText, 100, null);
        TooltipMakerAPI dataRowTooltip = dataRow.createUIElement(widthOfText, 100, false);
        TooltipMakerAPI dataRowTooltipDummy = dataRow.createUIElement(widthOfText, 10, true);
        getFirstRow(dataRowTooltipDummy, weapon, 4f);
        firstRow.getPosition().setSize(widthOfText, dataRowTooltipDummy.getHeightSoFar());
        ArrayList<StatNumberUIPackage> data = getFirstRow(dataRowTooltip, weapon, 4f);
        for (StatNumberUIPackage datum : data) {
            datum.placeLabelToParent(dataRowTooltip);
        }
        dataRow.addUIElement(dataRowTooltip).inTL(-5, 0);
        firstRow.addComponent(imageRow).inTL(0, 0);
        firstRow.addComponent(dataRow).inTL(105, 0);
        tooltip.addCustom(firstRow, 5f);
        renderer.setAnchor(imageRow);
        CustomPanelAPI damageSection = Global.getSettings().createCustom(widthOfTooltip, 80, null);
        CustomPanelAPI damageSectionTrue = Global.getSettings().createCustom(widthOfText, 80, null);
        TooltipMakerAPI dummy = damageSectionTrue.createUIElement(widthOfText, 10, true);
        getDamageRow(dummy, weapon, 4f);
        float height = dummy.getHeightSoFar();
        TooltipMakerAPI damageSectionTooltip = damageSectionTrue.createUIElement(widthOfText, dummy.getHeightSoFar(), false);
        damageSection.getPosition().setSize(damageSection.getPosition().getWidth(), height);
        damageSectionTrue.getPosition().setSize(damageSectionTrue.getPosition().getWidth(), height);
        for (StatNumberUIPackage statNumberUIPackage : getDamageRow(damageSectionTooltip, weapon, 4f)) {
            statNumberUIPackage.placeLabelToParent(damageSectionTooltip);
        }
        damageSectionTrue.addUIElement(damageSectionTooltip).inTL(-5, 0);
        damageSection.addComponent(damageSectionTrue).inTL(105, 0);
        tooltip.addCustom(damageSection, 10f);

        CustomPanelAPI fluxSectionHolder = Global.getSettings().createCustom(widthOfTooltip, 100, null);
        CustomPanelAPI trueFluxSectionHolder = Global.getSettings().createCustom(widthOfText, 100, null);
        TooltipMakerAPI dummyTolltip = trueFluxSectionHolder.createUIElement(widthOfText, 10, true);
        getFluxRow(dummyTolltip, weapon, 4f);
        fluxSectionHolder.getPosition().setSize(widthOfTooltip, dummyTolltip.getHeightSoFar());
        TooltipMakerAPI tooltipOfFlux = trueFluxSectionHolder.createUIElement(widthOfText, dummyTolltip.getHeightSoFar(), false);
        for (StatNumberUIPackage statNumberUIPackage : getFluxRow(tooltipOfFlux, weapon, 4f)) {
            statNumberUIPackage.placeLabelToParent(tooltipOfFlux);
        }

        trueFluxSectionHolder.addUIElement(tooltipOfFlux).inTL(-5, 0);
        fluxSectionHolder.addComponent(trueFluxSectionHolder).inTL(105, 0);
        tooltip.addCustom(fluxSectionHolder, 10f);
        if(weapon.getCustomPrimary()!=null&&!weapon.getCustomPrimary().isEmpty()){

            if(weapon.getCustomPrimaryHL()!=null&&!weapon.getCustomPrimaryHL().isEmpty()){
                String[]splitted = weapon.getCustomPrimaryHL().split("\\|");
                for (String s : splitted) {
                    s.trim();
                }
                tooltip.addPara(weapon.getCustomPrimary(),10f,Color.ORANGE,splitted);
            }
            else{
                tooltip.addPara(weapon.getCustomPrimary(),10f);
            }
        }
        tooltip.addSpacer(10f);
        tooltip.addSectionHeading("Auxiliary data", Alignment.MID, 5f);
        CustomPanelAPI damageTypeInfoHolder = Global.getSettings().createCustom(widthOfTooltip, 50, null);
        CustomPanelAPI damageTypeParaHolder = damageTypeInfoHolder.createCustomPanel(widthOfText, 50, null);
        TooltipMakerAPI tooltipOfParaHolder = damageTypeParaHolder.createUIElement(widthOfText, 50, false);
        for (StatNumberUIPackage statNumberUIPackage : getDamageTypeRow(tooltipOfParaHolder, weapon, 4f)) {
            statNumberUIPackage.placeLabelToParent(tooltipOfParaHolder);
        }
        CustomPanelAPI damageTypeImage = getImageOfDamage(85, weapon.getDamageType(), damageTypeInfoHolder);
        damageTypeInfoHolder.addComponent(damageTypeImage).inTL(0, 0);
        damageTypeParaHolder.addUIElement(tooltipOfParaHolder).inTL(-5, 0);
        damageTypeInfoHolder.addComponent(damageTypeParaHolder).inTL(105, 0);
        tooltip.addCustom(damageTypeInfoHolder, 5f);

        CustomPanelAPI accuracy_tackingInfoFiller = Global.getSettings().createCustom(widthOfTooltip, 100, null);
        CustomPanelAPI holderOfAccAndTracking = accuracy_tackingInfoFiller.createCustomPanel(widthOfText, 100, null);
        dummy = accuracy_tackingInfoFiller.createUIElement(widthOfText, 60, true);
        getInfoRowBasedOnWeaponType(dummy, weapon, 4f);

        if (dummy.getHeightSoFar() > 1) {
            accuracy_tackingInfoFiller.getPosition().setSize(accuracy_tackingInfoFiller.getPosition().getWidth(), dummy.getHeightSoFar());
            TooltipMakerAPI tooltipOfHolder = holderOfAccAndTracking.createUIElement(widthOfText, dummy.getHeightSoFar(), false);
            for (StatNumberUIPackage statNumberUIPackage : getInfoRowBasedOnWeaponType(tooltipOfHolder, weapon, 4f)) {
                statNumberUIPackage.placeLabelToParent(tooltipOfHolder);
            }
            holderOfAccAndTracking.addUIElement(tooltipOfHolder).inTL(-5, 0);
            accuracy_tackingInfoFiller.addComponent(holderOfAccAndTracking).inTL(105, 0);
            tooltip.addCustom(accuracy_tackingInfoFiller, 15f);
        }
        CustomPanelAPI ammoInfoFiller = Global.getSettings().createCustom(widthOfTooltip, 100, null);
        CustomPanelAPI holderOfAmmo = ammoInfoFiller.createCustomPanel(widthOfText, 100, null);
        dummy = ammoInfoFiller.createUIElement(widthOfText, 60, true);
        getAmmoInfo(dummy, weapon, 4f);

        if (dummy.getHeightSoFar() > 1) {
            ammoInfoFiller.getPosition().setSize(accuracy_tackingInfoFiller.getPosition().getWidth(), dummy.getHeightSoFar());
            TooltipMakerAPI tooltipOfHolder = holderOfAmmo.createUIElement(widthOfText, dummy.getHeightSoFar(), false);
            for (StatNumberUIPackage statNumberUIPackage : getAmmoInfo(tooltipOfHolder, weapon, 4f)) {
                statNumberUIPackage.placeLabelToParent(tooltipOfHolder);
            }
            holderOfAmmo.addUIElement(tooltipOfHolder).inTL(-5, 0);
            ammoInfoFiller.addComponent(holderOfAmmo).inTL(105, 0);

            tooltip.addCustom(ammoInfoFiller, 15f);
        }

        CustomPanelAPI otherInfoHolder = Global.getSettings().createCustom(widthOfTooltip, 100, null);
        CustomPanelAPI otherInfo = otherInfoHolder.createCustomPanel(widthOfText, 100, null);
        dummy = otherInfo.createUIElement(widthOfText, 60, true);
        getDelayRow(dummy, weapon, 4f);
        if (dummy.getHeightSoFar() > 1) {
            otherInfoHolder.getPosition().setSize(accuracy_tackingInfoFiller.getPosition().getWidth(), dummy.getHeightSoFar());
            TooltipMakerAPI tooltipOfHolder = otherInfo.createUIElement(widthOfText, dummy.getHeightSoFar(), false);
            for (StatNumberUIPackage statNumberUIPackage : getDelayRow(tooltipOfHolder, weapon, 4f)) {
                statNumberUIPackage.placeLabelToParent(tooltipOfHolder);
            }
            otherInfo.addUIElement(tooltipOfHolder).inTL(-5, 0);
            otherInfoHolder.addComponent(otherInfo).inTL(105, 0);
            tooltip.addCustom(otherInfoHolder, 15f);
        }
        if(weapon.getCustomAncillary()!=null&&!weapon.getCustomAncillary().isEmpty()){
            if(weapon.getCustomAncillaryHL()!=null&&!weapon.getCustomAncillaryHL().isEmpty()){
                String[]splitted = weapon.getCustomAncillaryHL().split("\\|");
                for (String s : splitted) {
                    s.trim();
                }
                tooltip.addPara(weapon.getCustomAncillary(),10,Color.ORANGE,splitted);
            }
            else{
                tooltip.addPara(weapon.getCustomAncillary(),15f);
            }
        }
    }

    public static ArrayList<StatNumberUIPackage> getFirstRow(TooltipMakerAPI tooltip, WeaponSpecAPI spec, float padding) {
        ArrayList<StatNumberUIPackage> packages = new ArrayList<>();
        String text1 = spec.getPrimaryRoleStr();
        String text2 = spec.getSize().getDisplayName() + ", " + spec.getMountType().getDisplayName();

        String text3 = AoTDMisc.getNumberString(spec.getOrdnancePointCost(null));
        packages.add(new StatNumberUIPackage(tooltip.addPara("Primary role", 5f), text1, null, Color.ORANGE, null));
        packages.add(new StatNumberUIPackage(tooltip.addPara("Mount type", padding), text2, null, Color.ORANGE, null));
        if (spec.getMountType().equals(WeaponAPI.WeaponType.HYBRID) || spec.getMountType().equals(WeaponAPI.WeaponType.SYNERGY) || spec.getMountType().equals(WeaponAPI.WeaponType.COMPOSITE)) {
            String additionalText1 = getMountTypeString(spec);
            String additionalText2 = countsAsString(spec);
            packages.add(new StatNumberUIPackage(tooltip.addPara(" ", padding), additionalText1, null, Color.ORANGE, null));
            packages.add(new StatNumberUIPackage(tooltip.addPara(" ", padding), additionalText2, null, Color.ORANGE, null));
        }
        packages.add(new StatNumberUIPackage(tooltip.addPara("Ordnance points", padding), text3, null, Color.ORANGE, null));
        return packages;
    }

    public static ArrayList<StatNumberUIPackage> getDamageTypeRow(TooltipMakerAPI tooltip, WeaponSpecAPI spec, float padding) {
        ArrayList<StatNumberUIPackage> packages = new ArrayList<>();
        String text1 = spec.getDamageType().getDisplayName();
        if (spec.isBeam()) {
            text1 += "(Beam)";
        }
        packages.add(new StatNumberUIPackage(tooltip.addPara("Damage type", padding), text1, null, Color.ORANGE, null));

        text1 = spec.getDamageType().getDescription();
        if (spec.isBeam()) {
            text1 += "(no hard flux)";
        }
        packages.add(new StatNumberUIPackage(tooltip.addPara(" ", padding), text1, null, Color.ORANGE, null));

        return packages;

    }
    public static ArrayList<StatNumberUIPackage> getDelayRow(TooltipMakerAPI tooltip, WeaponSpecAPI spec, float padding) {
        ArrayList<StatNumberUIPackage> packages = new ArrayList<>();
        String text1;
        float truePad = 0f;
        if(spec.getTurretFireOffsets().size()>0){
            if(spec.getTurretFireOffsets().size()>1){
                text1 = AoTDMisc.getNumberString(spec.getBurstSize());
                packages.add(new StatNumberUIPackage(tooltip.addPara("Burst size", truePad), text1, null, Color.ORANGE, null));
                truePad = padding;
            }

            float delay = spec.getDerivedStats().getBurstFireDuration();
            if(spec.isInterruptibleBurst()&&spec instanceof ProjectileWeaponSpecAPI){
                delay = ((ProjectileWeaponSpecAPI) spec).getRefireDelay();
            } else if (spec instanceof ProjectileWeaponSpecAPI) {
                delay+=((ProjectileWeaponSpecAPI) spec).getRefireDelay();
            }
            if(spec instanceof BeamWeaponSpecAPI){
                delay = ((BeamWeaponSpecAPI) spec).getBurstCooldown()+ spec.getBurstDuration()+ ((BeamWeaponSpecAPI) spec).getChargeupTime()+ ((BeamWeaponSpecAPI) spec).getChargedownTime();
            }
            text1 = AoTDMisc.getNumberString(delay);
            packages.add(new StatNumberUIPackage(tooltip.addPara("Refire delay", truePad), text1, null, Color.ORANGE, null));
        }



        return packages;

    }
    public static ArrayList<StatNumberUIPackage> getInfoRowBasedOnWeaponType(TooltipMakerAPI tooltip, WeaponSpecAPI spec, float padding) {
        ArrayList<StatNumberUIPackage> packages = new ArrayList<>();
        String text1 = spec.getSpeedStr();
        float truePad = 0f;
        if(text1==null){
            if(spec.getProjectileSpec() instanceof MissileSpecAPI){
                text1=((MissileSpecAPI) spec.getProjectileSpec()).getSpeedDisplayName();
            }
        }
        if (text1 != null && !text1.isEmpty()&&!text1.equals("None")) {
            packages.add(new StatNumberUIPackage(tooltip.addPara("Speed", truePad), text1, null, Color.ORANGE, null));
            truePad=padding;
        }

         text1 = spec.getTrackingStr();

        if(text1==null){
            if(spec.getProjectileSpec() instanceof MissileSpecAPI){
                ((MissileSpecAPI) spec.getProjectileSpec()).getManeuverabilityDisplayName();
            }
        }
        if (text1 != null && !text1.isEmpty()&&!text1.equals("None")) {
            packages.add(new StatNumberUIPackage(tooltip.addPara("Tracking", truePad), text1, null, Color.ORANGE, null));
            truePad = padding;
        }
        text1 = spec.getAccuracyStr();
        if(text1==null){
            if(spec.getProjectileSpec() instanceof MissileSpecAPI){

            }
            else{
                text1 = getAccuracyDisplayName(spec.getMaxSpread());
                if(spec.isBeam()){
                    text1 = "Perfect";
                }
            }

        }
        if (text1 != null && !text1.isEmpty()&&!text1.equals("None")) {
            packages.add(new StatNumberUIPackage(tooltip.addPara("Accuracy", truePad), text1, null, Color.ORANGE, null));
            truePad = padding;
        }

        text1 = spec.getTurnRateStr();
        if(text1==null){
            if(spec.getProjectileSpec() instanceof MissileSpecAPI){

            }
            else {
                text1 = getTurnRateDisplayName(spec.getTurnRate());
            }

        }

        if (text1 != null && !text1.isEmpty()&&!text1.equals("None")) {
            packages.add(new StatNumberUIPackage(tooltip.addPara("Turn rate", truePad), text1, null, Color.ORANGE, null));
            truePad = padding;
        }




        return packages;

    }
    public static ArrayList<StatNumberUIPackage> getAmmoInfo(TooltipMakerAPI tooltip, WeaponSpecAPI spec, float padding) {
        ArrayList<StatNumberUIPackage> packages = new ArrayList<>();

            if(!spec.usesAmmo()){
                return packages;
            }
            else{
                String text1 = null;
                String text2 = null;
                String text3 = null;
                text1= AoTDMisc.getNumberString(spec.getMaxAmmo());
                text2 = AoTDMisc.getNumberString(spec.getReloadSize()/spec.getAmmoPerSecond());
                text3 = AoTDMisc.getNumberString(spec.getReloadSize());
                if(spec.getAmmoPerSecond()>0f){
                    if(spec.getType()== WeaponAPI.WeaponType.ENERGY){
                        packages.add(new StatNumberUIPackage(tooltip.addPara("Max charges", padding), text1, null, Color.ORANGE, null));
                        packages.add(new StatNumberUIPackage(tooltip.addPara("Seconds / recharge", padding), text2, null, Color.ORANGE, null));
                        packages.add(new StatNumberUIPackage(tooltip.addPara("Charges gained", padding), text3, null, Color.ORANGE, null));
                    }
                    else{
                        packages.add(new StatNumberUIPackage(tooltip.addPara("Max ammo", padding), text1, null, Color.ORANGE, null));
                        packages.add(new StatNumberUIPackage(tooltip.addPara("Seconds / reload", padding), text2, null, Color.ORANGE, null));
                        packages.add(new StatNumberUIPackage(tooltip.addPara("Reload size", padding), text3, null, Color.ORANGE, null));
                    }
                }
                else{
                    if(spec.isBeam()){
                        packages.add(new StatNumberUIPackage(tooltip.addPara("Charges", padding), text1, null, Color.ORANGE, null));

                    }
                    else{
                        packages.add(new StatNumberUIPackage(tooltip.addPara("Ammo", padding), text1, null, Color.ORANGE, null));

                    }
                }

            }


        return packages;

    }
    public static ArrayList<StatNumberUIPackage> getDamageRow(TooltipMakerAPI tooltip, WeaponSpecAPI spec, float padding) {
        ArrayList<StatNumberUIPackage> packages = new ArrayList<>();
        WeaponAPI.DerivedWeaponStatsAPI weaponStatsAPI = spec.getDerivedStats();

        String text1 = AoTDMisc.getNumberString(spec.getMaxRange()) + "";
        weaponStatsAPI.getSustainedDps();
        String text2 = AoTDMisc.getNumberString(weaponStatsAPI.getDamagePerShot()) + "";
        BeamWeaponSpecAPI specBeam = null;
        float delayBeam = 1f;
        if (spec.getBurstSize() > 1) {
            text2 = AoTDMisc.getNumberString(weaponStatsAPI.getDamagePerShot()) + "x" + spec.getBurstSize();
        }
        if (spec.isBeam()) {
            specBeam = (BeamWeaponSpecAPI) spec;
            delayBeam = specBeam.getBurstCooldown() + specBeam.getBurstDuration() + specBeam.getBeamChargedownTime() + specBeam.getBeamChargeupTime();
            text2 = (int) Math.round(weaponStatsAPI.getDps() * delayBeam) + "";
        }

        packages.add(new StatNumberUIPackage(tooltip.addPara("Range", 0f), text1, null, Color.ORANGE, null));
        packages.add(new StatNumberUIPackage(tooltip.addPara("Damage", padding), text2, null, Color.ORANGE, null));
        if (!spec.isNoDPSInTooltip()) {
            String text3 = (int) weaponStatsAPI.getDps() + "";
            if (weaponStatsAPI.getDps()!=weaponStatsAPI.getSustainedDps()) {
                text3 = AoTDMisc.getNumberString(weaponStatsAPI.getDps()) + "(" + AoTDMisc.getNumberString(weaponStatsAPI.getSustainedDps()) + ")";
                packages.add(new StatNumberUIPackage(tooltip.addPara("Damage / second(sustained)", padding), text3, null, Color.ORANGE, null));

            } else {
                packages.add(new StatNumberUIPackage(tooltip.addPara("Damage / second", padding), text3, null, Color.ORANGE, null));

            }
        }
        if (weaponStatsAPI.getEmpPerShot() > 0) {
            String text3 = (int) Math.round(weaponStatsAPI.getEmpPerShot() * delayBeam) + "";
            if (spec.usesAmmo() && spec.getMaxAmmo() > 1) {
                text3 = (int) Math.round(weaponStatsAPI.getEmpPerShot() * delayBeam) + "(" + (int) spec.getMaxAmmo() + ")";
            }
            packages.add(new StatNumberUIPackage(tooltip.addPara("Emp damage", padding), text3, null, Color.ORANGE, null));
        }
        if (weaponStatsAPI.getEmpPerSecond() > 0 && spec.isBeam()) {
            String text3 = Math.round(weaponStatsAPI.getEmpPerSecond() * delayBeam) + "";
            packages.add(new StatNumberUIPackage(tooltip.addPara("Emp damage", padding), text3, null, Color.ORANGE, null));
        }


        return packages;
    }

    public static ArrayList<StatNumberUIPackage> getFluxRow(TooltipMakerAPI tooltip, WeaponSpecAPI spec, float padding) {
        ShipHullSpecAPI specShip = Global.getSettings().getHullSpec("dem_drone");
        ShipVariantAPI v = Global.getSettings().createEmptyVariant("dem_drone", specShip);
        ShipAPI shipAPI = Global.getCombatEngine().createFXDrone(v);
        WeaponAPI weapon = Global.getCombatEngine().createFakeWeapon(shipAPI, spec.getWeaponId());

        ArrayList<StatNumberUIPackage> packages = new ArrayList<>();
        WeaponAPI.DerivedWeaponStatsAPI weaponStatsAPI = spec.getDerivedStats();
        String text1 = "";
        if (weaponStatsAPI.getSustainedFluxPerSecond() == 0 && weaponStatsAPI.getFluxPerDam() == 0 && weaponStatsAPI.getFluxPerSecond() == 0 && weapon.getFluxCostToFire() == 0) {
            boolean ammo = spec.usesAmmo() && spec.getAmmoPerSecond() <= 0.0F;
            boolean susFlux = spec.getDerivedStats().getSustainedFluxPerSecond()>0f;
            if(!susFlux||ammo){
                String am_ch = "ammo";
                if(spec.getDamageType()==DamageType.ENERGY&&spec.getType()!= WeaponAPI.WeaponType.MISSILE){
                    am_ch = "charges";
                }
                am_ch = am_ch+ " (" + spec.getMaxAmmo() + ")";
                if(!ammo){
                    packages.add(new StatNumberUIPackage(tooltip.addPara(" ", padding), "No flux cost to fire", null, Color.ORANGE, null));
                }
                else if (susFlux){
                    packages.add(new StatNumberUIPackage(tooltip.addPara(" ", padding), "Limited "+am_ch, null, Color.ORANGE, null));

                }
                else{
                    packages.add(new StatNumberUIPackage(tooltip.addPara(" ", padding), "No flux cost to fire, limited " + am_ch, null, Color.ORANGE, null));

                }
            }
            return packages;
        }
        if (!spec.isNoDPSInTooltip()) {
            if (weaponStatsAPI.getDps()!=weaponStatsAPI.getSustainedDps()) {
                text1 = AoTDMisc.getNumberString(weaponStatsAPI.getFluxPerSecond()) + "(" + AoTDMisc.getNumberString(weaponStatsAPI.getSustainedFluxPerSecond()) + ")";
                packages.add(new StatNumberUIPackage(tooltip.addPara("Flux / second(sustained)", padding), text1, null, Color.ORANGE, null));
            } else   {
                text1 = AoTDMisc.getNumberString(weaponStatsAPI.getFluxPerSecond()) + "";
                packages.add(new StatNumberUIPackage(tooltip.addPara("Flux / second", padding), text1, null, Color.ORANGE, null));
            }
        }


        if (!spec.isBeam()) {
            text1 = AoTDMisc.getNumberString(weapon.getFluxCostToFire() / spec.getBurstSize()) + "";
            packages.add(new StatNumberUIPackage(tooltip.addPara("Flux / shot", padding), text1, null, Color.ORANGE, null));
        }
        if (weaponStatsAPI.getFluxPerDam() > 0) {
            text1 = AoTDMisc.getNumberString(weaponStatsAPI.getFluxPerDam());
            if (weaponStatsAPI.getEmpPerShot() > 0 || weaponStatsAPI.getEmpPerSecond() > 0) {
                packages.add(new StatNumberUIPackage(tooltip.addPara("Flux / non-EMP damage", padding), text1, null, Color.ORANGE, null));

            } else {
                packages.add(new StatNumberUIPackage(tooltip.addPara("Flux / damage", padding), text1, null, Color.ORANGE, null));

            }
        }


        return packages;
    }

    public static String getMountTypeString(WeaponSpecAPI weaponSpecAPI) {
        String str = "";
        if (weaponSpecAPI.getMountType().equals(WeaponAPI.WeaponType.HYBRID)) {
            str = generateResponse(WeaponAPI.WeaponType.BALLISTIC, WeaponAPI.WeaponType.ENERGY, WeaponAPI.WeaponType.HYBRID);
        }
        if (weaponSpecAPI.getMountType().equals(WeaponAPI.WeaponType.SYNERGY)) {
            str = generateResponse(WeaponAPI.WeaponType.ENERGY, WeaponAPI.WeaponType.MISSILE, WeaponAPI.WeaponType.SYNERGY);
        }
        if (weaponSpecAPI.getMountType().equals(WeaponAPI.WeaponType.COMPOSITE)) {
            str = generateResponse(WeaponAPI.WeaponType.BALLISTIC, WeaponAPI.WeaponType.MISSILE, WeaponAPI.WeaponType.COMPOSITE);
        }
        if (weaponSpecAPI.getMountType().equals(WeaponAPI.WeaponType.UNIVERSAL)) {
            str = "Can be installed in any type of slot";
        }
        return str;
    }

    public static String countsAsString(WeaponSpecAPI specAPI) {
        return "Counts as " + specAPI.getType().getDisplayName() + " for stat modifiers";
    }

    public static String generateResponse(WeaponAPI.WeaponType firstMount, WeaponAPI.WeaponType secondMount, WeaponAPI.WeaponType weaponMount) {
        return "Requires a " + firstMount.getDisplayName() + ", " + secondMount.getDisplayName() + ", or " + weaponMount.getDisplayName() + " slot";
    }

    public static Pair<CustomPanelAPI,WeaponSpriteRenderer> getImageOfWeapon(WeaponSpecAPI spec, float size) {
        WeaponSpriteRenderer renderer = new WeaponSpriteRenderer(spec, size, 0);
        CustomPanelAPI panelAPI = Global.getSettings().createCustom(size, size, renderer);
        renderer.setAnchor(panelAPI);
        return new Pair<>(panelAPI,renderer);
    }

    public static CustomPanelAPI getImageOfDamage(float size, DamageType type, CustomPanelAPI parentPanel) {
        CustomPanelAPI panelOfImage = parentPanel.createCustomPanel(size, size, null);
        TooltipMakerAPI tooltip = panelOfImage.createUIElement(size, size, false);
        if (type.equals(DamageType.ENERGY)) {
            tooltip.addImage(Global.getSettings().getSpriteName("ui_campaign_components", "dmg_type_ene"), size, size, 0f);
        }
        if (type.equals(DamageType.KINETIC)) {
            tooltip.addImage(Global.getSettings().getSpriteName("ui_campaign_components", "dmg_type_kin"), size, size, 0f);

        }
        if (type.equals(DamageType.FRAGMENTATION)) {
            tooltip.addImage(Global.getSettings().getSpriteName("ui_campaign_components", "dmg_type_frag"), size, size, 0f);

        }
        if (type.equals(DamageType.HIGH_EXPLOSIVE)) {
            tooltip.addImage(Global.getSettings().getSpriteName("ui_campaign_components", "dmg_type_boom"), size, size, 0f);

        }
        if (type.equals(DamageType.OTHER)) {
            tooltip.addImage(Global.getSettings().getSpriteName("ui_campaign_components", "dmg_type_other"), size, size, 0f);

        }
        panelOfImage.addUIElement(tooltip).inTL(-5, 0);
        return panelOfImage;
    }
    public static String getTurnRateDisplayName(float rate) {
        if (rate <= 0.0F) {
            return null;
        } else if (rate <= 5.0F) {
            return "Very Slow";
        } else if (rate <= 15.0F) {
            return "Slow";
        } else if (rate <= 25.0F) {
            return "Medium";
        } else if (rate <= 35.0F) {
            return "Fast";
        } else {
            return rate <= 50.0F ? "Very Fast" : "Excellent";
        }
    }
    public static String getAccuracyDisplayName(float acc) {
        if (acc <= 0.0F) {
            return "Perfect";
        } else if (acc <= 2.0F) {
            return "Excellent";
        } else if (acc <= 5.0F) {
            return "Good";
        } else if (acc <= 10.0F) {
            return "Medium";
        } else if (acc <= 15.0F) {
            return "Poor";
        } else {
            return acc <= 20.0F ? "Very Poor" : "Terrible";
        }
    }
}
