package data.kaysaar.aotd.vok.campaign.econ.globalproduction.models;

import com.fs.starfarer.api.Global;
import kaysaar.aotd_question_of_loyalty.data.intel.AoTDCommIntelPlugin;
import com.fs.starfarer.api.util.Misc;
import kaysaar.aotd_question_of_loyalty.data.tags.AoTDRankTags;


public class GpKnowledgeRepository {
    public static boolean isKnownByPlayer(GPOption option) {
        if (Global.getSettings().isDevMode()) return true;
        if (option.getSpec().getType().equals(GPSpec.ProductionType.SHIP)) {
            boolean knows = Global.getSector().getPlayerFaction().knowsShip(option.getSpec().getShipHullSpecAPI().getHullId());
            if (Global.getSettings().getModManager().isModEnabled("aotd_qol") && Misc.getCommissionFaction() != null) {
                boolean factionKnows = Misc.getCommissionFaction().knowsShip(option.getSpec().getShipHullSpecAPI().getHullId());
                boolean haveTag = AoTDCommIntelPlugin.get().getCurrentRankData().hasTag(AoTDRankTags.ACCESS_TO_FACTION_BLUEPRINTS);
                return knows || (factionKnows && haveTag);
            }
            return knows;
        }
        if (option.getSpec().getType().equals(GPSpec.ProductionType.WEAPON)) {
            boolean knows = Global.getSector().getPlayerFaction().knowsWeapon(option.getSpec().getWeaponSpec().getWeaponId());
            if (Global.getSettings().getModManager().isModEnabled("aotd_qol") && Misc.getCommissionFaction() != null) {
                boolean factionKnows = Misc.getCommissionFaction().knowsWeapon(option.getSpec().getWeaponSpec().getWeaponId());
                boolean haveTag = AoTDCommIntelPlugin.get().getCurrentRankData().hasTag(AoTDRankTags.ACCESS_TO_FACTION_BLUEPRINTS);
                return knows || (factionKnows && haveTag);
            }
            return knows;
        }
        if (option.getSpec().getType().equals(GPSpec.ProductionType.FIGHTER)) {
            boolean knows = Global.getSector().getPlayerFaction().knowsFighter(option.getSpec().getWingSpecAPI().getId());
            if (Global.getSettings().getModManager().isModEnabled("aotd_qol") && Misc.getCommissionFaction() != null) {
                boolean factionKnows = Misc.getCommissionFaction().knowsFighter(option.getSpec().getWingSpecAPI().getId());
                boolean haveTag = AoTDCommIntelPlugin.get().getCurrentRankData().hasTag(AoTDRankTags.ACCESS_TO_FACTION_BLUEPRINTS);
                return knows || (factionKnows && haveTag);
            }
            return knows;
        }
        return true;
    }

}
