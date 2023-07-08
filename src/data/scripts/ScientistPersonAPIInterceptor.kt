package data.scripts

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.*
import com.fs.starfarer.api.campaign.econ.MarketAPI
import com.fs.starfarer.api.characters.AbilityPlugin
import com.fs.starfarer.api.characters.PersonAPI
import com.fs.starfarer.api.combat.EngagementResultAPI
import com.fs.starfarer.api.impl.campaign.intel.bar.events.ScientistAICoreBarEvent
import com.fs.starfarer.api.impl.campaign.intel.bar.events.ScientistAICoreIntel
import data.plugins.AoDUtilis
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType

class ScientistPersonAPIInterceptor: CampaignEventListener {


    override fun reportPlayerOpenedMarket(market: MarketAPI?) {

    }

    override fun reportPlayerClosedMarket(market: MarketAPI?) {
        val intel = Global.getSector().intelManager?.getFirstIntel(ScientistAICoreIntel::class.java);
        if(intel!=null){
            val person = (getPrivateVariable("event", intel) as ScientistAICoreBarEvent?)?.person
            if(person!=null){
                AoDUtilis.insertOPScientist(person);
                Global.getSector().removeListener(this);
            }
        }



    }

    override fun reportPlayerOpenedMarketAndCargoUpdated(market: MarketAPI?) {

    }

    override fun reportEncounterLootGenerated(plugin: FleetEncounterContextPlugin?, loot: CargoAPI?) {

    }

    override fun reportPlayerMarketTransaction(transaction: PlayerMarketTransaction?) {

    }

    override fun reportBattleOccurred(primaryWinner: CampaignFleetAPI?, battle: BattleAPI?) {

    }

    override fun reportBattleFinished(primaryWinner: CampaignFleetAPI?, battle: BattleAPI?) {

    }

    override fun reportPlayerEngagement(result: EngagementResultAPI?) {

    }

    override fun reportFleetDespawned(
        fleet: CampaignFleetAPI?,
        reason: CampaignEventListener.FleetDespawnReason?,
        param: Any?
    ) {

    }

    override fun reportFleetSpawned(fleet: CampaignFleetAPI?) {

    }

    override fun reportFleetReachedEntity(fleet: CampaignFleetAPI?, entity: SectorEntityToken?) {

    }

    override fun reportFleetJumped(
        fleet: CampaignFleetAPI?,
        from: SectorEntityToken?,
        to: JumpPointAPI.JumpDestination?
    ) {

    }

    override fun reportShownInteractionDialog(dialog: InteractionDialogAPI?) {

    }

    override fun reportPlayerReputationChange(faction: String?, delta: Float) {

    }

    override fun reportPlayerReputationChange(person: PersonAPI?, delta: Float) {

    }

    override fun reportPlayerActivatedAbility(ability: AbilityPlugin?, param: Any?) {

    }

    override fun reportPlayerDeactivatedAbility(ability: AbilityPlugin?, param: Any?) {

    }

    override fun reportPlayerDumpedCargo(cargo: CargoAPI?) {

    }

    override fun reportPlayerDidNotTakeCargo(cargo: CargoAPI?) {

    }

    override fun reportEconomyTick(iterIndex: Int) {

    }

    override fun reportEconomyMonthEnd() {

    }

    fun setPrivateVariable(fieldName: String, instanceToModify: Any, newValue: Any?) {
        val fieldClass = Class.forName("java.lang.reflect.Field", false, Class::class.java.classLoader)
        val setMethod = MethodHandles.lookup()
            .findVirtual(fieldClass, "set", MethodType.methodType(Void.TYPE, Any::class.java, Any::class.java))
        val getNameMethod =
            MethodHandles.lookup().findVirtual(fieldClass, "getName", MethodType.methodType(String::class.java))
        val setAcessMethod = MethodHandles.lookup().findVirtual(
            fieldClass,
            "setAccessible",
            MethodType.methodType(Void.TYPE, Boolean::class.javaPrimitiveType)
        )

        val instancesOfFields: Array<out Any> = instanceToModify.javaClass.getDeclaredFields()
        for (obj in instancesOfFields) {
            setAcessMethod.invoke(obj, true)
            val name = getNameMethod.invoke(obj)
            if (name.toString() == fieldName) {
                setMethod.invoke(obj, instanceToModify, newValue)
            }
        }
    }

    private fun getPrivateVariable(fieldName: String, instanceToGetFrom: Any): Any? {
        val fieldClass = Class.forName("java.lang.reflect.Field", false, Class::class.java.classLoader)
        val getMethod = MethodHandles.lookup()
            .findVirtual(fieldClass, "get", MethodType.methodType(Any::class.java, Any::class.java))
        val getNameMethod =
            MethodHandles.lookup().findVirtual(fieldClass, "getName", MethodType.methodType(String::class.java))
        val setAcessMethod = MethodHandles.lookup().findVirtual(
            fieldClass,
            "setAccessible",
            MethodType.methodType(Void.TYPE, Boolean::class.javaPrimitiveType)
        )

        val instancesOfFields: Array<out Any> = instanceToGetFrom.javaClass.getDeclaredFields()
        for (obj in instancesOfFields) {
            setAcessMethod.invoke(obj, true)
            val name = getNameMethod.invoke(obj)
            if (name.toString() == fieldName) {
                return getMethod.invoke(obj, instanceToGetFrom)
            }
        }
        return null
    }


}