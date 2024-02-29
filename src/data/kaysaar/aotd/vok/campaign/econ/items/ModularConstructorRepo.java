package data.kaysaar.aotd.vok.campaign.econ.items;

import com.fs.starfarer.api.impl.campaign.ids.Items;

import java.util.ArrayList;

public class ModularConstructorRepo {
    public static ArrayList<String>constructorTypes = new ArrayList<>();
    static {
        constructorTypes.add(Items.PRISTINE_NANOFORGE);
        constructorTypes.add(Items.SYNCHROTRON);
        constructorTypes.add(Items.MANTLE_BORE);
        constructorTypes.add(Items.CATALYTIC_CORE);

    }
}
