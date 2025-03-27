package data.kaysaar.aotd.vok.campaign.econ.patrolfleets.inflaters;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.DModManager;
import com.fs.starfarer.api.impl.campaign.fleets.DefaultFleetInflater;
import com.fs.starfarer.api.impl.campaign.fleets.DefaultFleetInflaterParams;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.loading.VariantSource;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.plugins.AutofitPlugin;
import com.fs.starfarer.api.plugins.impl.CoreAutofitPlugin;
import com.fs.starfarer.api.util.ListMap;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;

import java.util.*;

public class AoTDFleetInflater extends DefaultFleetInflater {
    public AoTDFleetInflater(DefaultFleetInflaterParams p) {
        super(p);
    }

    @Override
    public void inflate(CampaignFleetAPI fleet) {
        Random random = new Random();
        //p.seed = null;
        if (p.seed != null) random = new Random(p.seed);

        //p.quality = 2f;

        //random = new Random();


        Random dmodRandom = new Random();
        if (p.seed != null) dmodRandom = Misc.getRandom(p.seed, 5);

        CoreAutofitPlugin auto = new CoreAutofitPlugin(fleet.getCommander());
        auto.setRandom(random);

        boolean upgrade = random.nextFloat() < Math.min(0.1f + p.quality * 0.5f, 0.5f);
        auto.setChecked(CoreAutofitPlugin.UPGRADE, upgrade);

        //auto.setChecked(CoreAutofitPlugin.RANDOMIZE, true);
        //auto.getOptions().get(4).checked = true; // upgrade

        this.fleet = fleet;
        this.faction = fleet.getFaction();
        if (p.factionId != null) {
            this.faction = Global.getSector().getFaction(p.factionId);
        }

        //this.faction = Global.getSector().getFaction(Factions.HEGEMONY);

        hullmods = new ArrayList<String>(faction.getKnownHullMods());

//		fighters = new ArrayList<AvailableFighter>();
//		for (String wingId : faction.getKnownFighters()) {
//			fighters.add(new AvailableFighterImpl(wingId, 1000));
//		}

        SortedWeapons nonPriorityWeapons = new SortedWeapons();
        SortedWeapons priorityWeapons = new SortedWeapons();


        Set<String> weaponCategories = new LinkedHashSet<String>();
        for (String weaponId : faction.getKnownWeapons()) {
            if (!faction.isWeaponKnownAt(weaponId, p.timestamp)) continue;

            WeaponSpecAPI spec = Global.getSettings().getWeaponSpec(weaponId);
            //if (mode == ShipPickMode.IMPORTED && !spec.hasTag(Items.TAG_BASE_BP)) continue;

            if (spec == null) {
                throw new RuntimeException("Weapon with spec id [" + weaponId + "] not found");
            }

            int tier = spec.getTier();
            String cat = spec.getAutofitCategory();

            if (isPriority(spec)) {
                List<AutofitPlugin.AvailableWeapon> list = priorityWeapons.getWeapons(tier).getWeapons(cat).getWeapons(spec.getSize());
                list.add(new AvailableWeaponImpl(spec, 1000));
            } else {
                List<AutofitPlugin.AvailableWeapon> list = nonPriorityWeapons.getWeapons(tier).getWeapons(cat).getWeapons(spec.getSize());
                list.add(new AvailableWeaponImpl(spec, 1000));
            }
            weaponCategories.add(cat);
        }

        ListMap<AutofitPlugin.AvailableFighter> nonPriorityFighters = new ListMap<AutofitPlugin.AvailableFighter>();
        ListMap<AutofitPlugin.AvailableFighter> priorityFighters = new ListMap<AutofitPlugin.AvailableFighter>();
        Set<String> fighterCategories = new LinkedHashSet<String>();
        for (String wingId : faction.getKnownFighters()) {
            if (!faction.isFighterKnownAt(wingId, p.timestamp)) continue;

            FighterWingSpecAPI spec = Global.getSettings().getFighterWingSpec(wingId);
            if (spec == null) {
                throw new RuntimeException("Fighter wing with spec id [" + wingId + "] not found");
            }

            //if (mode == ShipPickMode.IMPORTED && !spec.hasTag(Items.TAG_BASE_BP)) continue;
            //int tier = spec.getTier();
            String cat = spec.getAutofitCategory();
//			if (cat == null) {
//				System.out.println("wfewfwe");
//			}
            if (isPriority(spec)) {
                priorityFighters.add(cat, new AvailableFighterImpl(spec, 1000));
            } else {
                nonPriorityFighters.add(cat, new AvailableFighterImpl(spec, 1000));
            }
            fighterCategories.add(cat);
        }


        //float averageDmods = (1f - quality) / Global.getSettings().getFloat("qualityPerDMod");
        float averageDmods = getAverageDmodsForQuality(p.quality);

        //System.out.println("Quality: " + quality + ", Average: " + averageDmods);

        boolean forceAutofit = fleet.getMemoryWithoutUpdate().getBoolean(MemFlags.MEMORY_KEY_FORCE_AUTOFIT_ON_NO_AUTOFIT_SHIPS);
        int memberIndex = 0;
        for (FleetMemberAPI member : fleet.getFleetData().getMembersListCopy()) {

            if (!forceAutofit && member.getHullSpec().hasTag(Tags.TAG_NO_AUTOFIT)) {
                continue;
            }
            if (!forceAutofit && member.getVariant() != null && member.getVariant().hasTag(Tags.TAG_NO_AUTOFIT)) {
                continue;
            }

            if (!faction.isPlayerFaction()) {
                if (!forceAutofit && member.getHullSpec().hasTag(Tags.TAG_NO_AUTOFIT_UNLESS_PLAYER)) {
                    continue;
                }
                if (!forceAutofit && member.getVariant() != null && member.getVariant().hasTag(Tags.TAG_NO_AUTOFIT_UNLESS_PLAYER)) {
                    continue;
                }
            }

            // need this so that when reinflating a fleet that lost members, the members reinflate consistently
            if (p.seed != null) {
                int extra = member.getShipName().hashCode();
                random = new Random(p.seed * extra);
                auto.setRandom(random);
                dmodRandom = Misc.getRandom(p.seed * extra, 5);
            }

            List<WeaponAPI.WeaponSize> sizes = new ArrayList<WeaponAPI.WeaponSize>();
            sizes.add(WeaponAPI.WeaponSize.SMALL);
            sizes.add(WeaponAPI.WeaponSize.MEDIUM);
            sizes.add(WeaponAPI.WeaponSize.LARGE);

            weapons = new ArrayList<AutofitPlugin.AvailableWeapon>();
            for (String cat : weaponCategories) {
                for (WeaponAPI.WeaponSize size : sizes) {
                    boolean foundSome = false;
                    for (int tier = 0; tier < 4; tier++) {
                        float p = getTierProbability(tier, this.p.quality);
                        if (this.p.allWeapons != null && this.p.allWeapons) {
                            p = 1f;
                        }

                        List<AutofitPlugin.AvailableWeapon> priority = priorityWeapons.getWeapons(tier).getWeapons(cat).getWeapons(size);
                        List<AutofitPlugin.AvailableWeapon> nonPriority = nonPriorityWeapons.getWeapons(tier).getWeapons(cat).getWeapons(size);

                        if (!foundSome) {
                            p = 1f;
                        }

                        boolean tierAvailable = random.nextFloat() < p;
                        if (!tierAvailable && foundSome) continue;
                        //if (random.nextFloat() >= p) continue;

                        int num = 2;
                        switch (size) {
                            case LARGE: num = 2; break;
                            case MEDIUM: num = 2; break;
                            case SMALL: num = 2; break;
                        }
//						if (!tierAvailable) {
//							num = 1;
//						}

                        if (this.p.allWeapons != null && this.p.allWeapons) {
                            num = 500;
                        }

                        Set<Integer> picks = makePicks(num, priority.size(), random);
                        for (Integer index : picks) {
                            AutofitPlugin.AvailableWeapon w = priority.get(index);
                            weapons.add(w);
                            foundSome = true;
                        }

                        num -= picks.size();
                        if (num > 0) {
                            picks = makePicks(num, nonPriority.size(), random);
                            for (Integer index : picks) {
                                AutofitPlugin.AvailableWeapon w = nonPriority.get(index);
                                weapons.add(w);
                                foundSome = true;
                            }
                        }
                    }
                }
            }

            fighters = new ArrayList<AutofitPlugin.AvailableFighter>();
            for (String cat : fighterCategories) {
                List<AutofitPlugin.AvailableFighter> priority = priorityFighters.get(cat);

                boolean madePriorityPicks = false;
                if (priority != null) {
                    int num = random.nextInt(2) + 1;
                    if (this.p.allWeapons != null && this.p.allWeapons) {
                        num = 100;
                    }

                    Set<Integer> picks = makePicks(num, priority.size(), random);
                    for (Integer index : picks) {
                        AutofitPlugin.AvailableFighter f = priority.get(index);
                        fighters.add(f);
                        madePriorityPicks = true;
                    }
                }

                if (!madePriorityPicks) {
                    int num = random.nextInt(2) + 1;
                    if (this.p.allWeapons != null && this.p.allWeapons) {
                        num = 100;
                    }

                    List<AutofitPlugin.AvailableFighter> nonPriority = nonPriorityFighters.get(cat);
                    Set<Integer> picks = makePicks(num, nonPriority.size(), random);
                    for (Integer index : picks) {
                        AutofitPlugin.AvailableFighter f = nonPriority.get(index);
                        fighters.add(f);
                    }
                }
            }


            ShipVariantAPI target = member.getVariant();
            if (target.getOriginalVariant() != null) {
                // needed if inflating the same fleet repeatedly to pick up weapon availability changes etc
                target = Global.getSettings().getVariant(target.getOriginalVariant());
            }

            if (faction.isPlayerFaction()) {
                if (random.nextFloat() < GOAL_VARIANT_PROBABILITY) {
                    List<ShipVariantAPI> targets = Global.getSector().getAutofitVariants().getTargetVariants(member.getHullId());
                    WeightedRandomPicker<ShipVariantAPI> alts = new WeightedRandomPicker<ShipVariantAPI>(random);
                    for (ShipVariantAPI curr : targets) {
                        if (curr.getHullSpec().getHullId().equals(target.getHullSpec().getHullId())) {
                            alts.add(curr);
                        }
                    }
                    if (!alts.isEmpty()) {
                        target = alts.pick();
                    }
                }
            }


            currVariant = Global.getSettings().createEmptyVariant(fleet.getId() + "_" + memberIndex, target.getHullSpec());
            currMember = member;

            if (target.isStockVariant()) {
                currVariant.setOriginalVariant(target.getHullVariantId());
            }

            float rProb = faction.getDoctrine().getAutofitRandomizeProbability();
            if (p.rProb != null) rProb = p.rProb;
            boolean randomize = random.nextFloat() < rProb;
            if (member.isStation()) randomize = false;
            auto.setChecked(CoreAutofitPlugin.RANDOMIZE, randomize);

            memberIndex++;

            int maxSmods = 0;
            if (p.averageSMods != null && !member.isCivilian()) {
                maxSmods = getMaxSMods(currVariant, p.averageSMods, dmodRandom) - currVariant.getSMods().size();
            }
            auto.doFit(currVariant, target, maxSmods, this);
            currVariant.setSource(VariantSource.REFIT);
            member.setVariant(currVariant, false, false);
            int index =0;
            for (String fittedWing : member.getVariant().getFittedWings()) {
                member.getVariant().setWingId(index,null);
                index++;
            }

            //int dmods = (int) Math.round(averageDmods + dmodRandom.nextFloat() * 2f - 1f);
//			int dmods = (int) Math.round(averageDmods + dmodRandom.nextFloat() * 3f - 2f);
//			if (dmods > 5) dmods = 5;
//			int dmodsAlready = DModManager.getNumDMods(currVariant);
//			dmods -= dmodsAlready;
//			if (dmods > 0) {
//				DModManager.setDHull(currVariant);
//				DModManager.addDMods(member, true, dmods, dmodRandom);
//			}

            if (!currMember.isStation()) {
                int addDmods = getNumDModsToAdd(currVariant, averageDmods, dmodRandom);
                if (addDmods > 0) {
                    DModManager.setDHull(currVariant);
                    DModManager.addDMods(member, true, addDmods, dmodRandom);
                }
            }
        }


        fleet.getFleetData().setSyncNeeded();
        fleet.getFleetData().syncIfNeeded();

        // handled in the method that calls inflate()
        //ListenerUtil.reportFleetInflated(fleet, this);
    }

}
