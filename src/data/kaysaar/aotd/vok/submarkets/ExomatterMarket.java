package data.kaysaar.aotd.vok.submarkets;

import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.CoreUIAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.submarkets.BaseSubmarketPlugin;
import com.fs.starfarer.api.util.Highlights;

public class ExomatterMarket extends BaseSubmarketPlugin {
    public void init(SubmarketAPI submarket) {
        super.init(submarket);

    }

    public void updateCargoPrePlayerInteraction() {
    }

    public boolean showInFleetScreen() {
        return false;
    }

    public boolean showInCargoScreen() {
        return market.getFaction().isPlayerFaction()||market.isPlayerOwned();
    }


    public boolean isIllegalOnSubmarket(CargoStackAPI stack, TransferAction action) {

        return false;
    }

    @Override
    public boolean isIllegalOnSubmarket(String commodityId, TransferAction action) {
        return false;
    }

    @Override
    public boolean isIllegalOnSubmarket(FleetMemberAPI member, TransferAction action) {
        return false;
    }

    public boolean isParticipatesInEconomy() {
        return false;
    }

    public float getTariff() {
        return 0f;
    }

    @Override
    public boolean isFreeTransfer() {
        return true;
    }

    @Override
    public String getBuyVerb() {
        return "Take";
    }

    @Override
    public String getSellVerb() {
        return "Leave";
    }

    public String getIllegalTransferText(CargoStackAPI stack, TransferAction action) {

        return "Cannot be stored here.";
    }

    public boolean isEnabled(CoreUIAPI ui) {
        return true;
    }

    public OnClickAction getOnClickAction(CoreUIAPI ui) {
        return OnClickAction.OPEN_SUBMARKET;
    }

    public String getTooltipAppendix(CoreUIAPI ui) {
        return null;
    }

    public Highlights getTooltipAppendixHighlights(CoreUIAPI ui) {
        return null;
    }
}
