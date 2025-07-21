package data.kaysaar.aotd.vok.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.combat.dweller.ShroudedMantleHullmod;

public class AoTDShroudedMantleHullmod extends ShroudedMantleHullmod {
    @Override
    public CargoStackAPI getRequiredItem() {
        CargoStackAPI stack = Global.getSettings().createCargoStack(CargoAPI.CargoItemType.RESOURCES, Commodities.ALPHA_CORE,null);
        stack.add(10);
        return stack;
    }
}
