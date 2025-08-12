package data.kaysaar.aotd.vok.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.combat.dweller.ShroudedThunderheadHullmod;
import data.kaysaar.aotd.vok.Ids.AoTDItems;

public class AoTDShroudedThunderHeadHullmod extends ShroudedThunderheadHullmod {
    @Override
    public CargoStackAPI getRequiredItem() {
        CargoStackAPI stack = Global.getSettings().createCargoStack(CargoAPI.CargoItemType.SPECIAL, new SpecialItemData(AoTDItems.TENEBRIUM_CELL,null),null);
        return stack;
    }
}
