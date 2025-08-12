package data.kaysaar.aotd.vok.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.impl.combat.dweller.ShroudedMantleHullmod;
import data.kaysaar.aotd.vok.Ids.AoTDItems;

public class AoTDShroudedMantleHullmod extends ShroudedMantleHullmod {
    @Override
    public CargoStackAPI getRequiredItem() {
        CargoStackAPI stack = Global.getSettings().createCargoStack(CargoAPI.CargoItemType.SPECIAL, new SpecialItemData(AoTDItems.TENEBRIUM_CELL,null),null);
        return stack;
    }
}
