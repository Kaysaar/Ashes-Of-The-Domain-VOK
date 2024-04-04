package data.kaysaar.aotd.vok.campaign.econ.items;

import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Items;

import java.util.ArrayList;
import java.util.HashMap;

public class ModularConstructorRepo {
    public static final String CONSTRUCTOR_TEMPLATE = "modular_constructor_";
    public static final String CONSTRUCTOR_MINING = CONSTRUCTOR_TEMPLATE+ Industries.MINING;
    public static final String CONSTRUCTOR_REFINING = CONSTRUCTOR_TEMPLATE+ Industries.REFINING;
    public static final String CONSTRUCTOR_ORBITALWORKS = CONSTRUCTOR_TEMPLATE+ Industries.ORBITALWORKS;
    public static final String CONSTRUCTOR_FUELPROD = CONSTRUCTOR_TEMPLATE+ Industries.FUELPROD;
    public static final String CONSTRUCTOR_EMPTY = CONSTRUCTOR_TEMPLATE+ "empty";
    public static HashMap<String,String>constructorRepo = new HashMap<>();
    static {
        constructorRepo.put(CONSTRUCTOR_MINING,Items.MANTLE_BORE);
        constructorRepo.put(CONSTRUCTOR_ORBITALWORKS,Items.PRISTINE_NANOFORGE);
        constructorRepo.put(CONSTRUCTOR_REFINING,Items.CATALYTIC_CORE);
        constructorRepo.put(CONSTRUCTOR_FUELPROD,Items.SYNCHROTRON);
    }
    public static HashMap<String,String>constructorRepoIndustries = new HashMap<>();
    static {
        constructorRepoIndustries.put(CONSTRUCTOR_MINING,Industries.MINING);
        constructorRepoIndustries.put(CONSTRUCTOR_ORBITALWORKS,Industries.ORBITALWORKS);
        constructorRepoIndustries.put(CONSTRUCTOR_REFINING,Industries.REFINING);
        constructorRepoIndustries.put(CONSTRUCTOR_FUELPROD,Industries.FUELPROD);
    }
}
