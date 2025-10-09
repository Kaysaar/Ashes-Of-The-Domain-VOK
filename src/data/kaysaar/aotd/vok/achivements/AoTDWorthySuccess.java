package data.kaysaar.aotd.vok.achivements;

import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt.HypershuntMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.NidavelirComplexMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.pluto.PlutoMegastructure;

public class AoTDWorthySuccess extends AoTDBaseMegastructureAchievement {
    public AoTDWorthySuccess() {
        classes.add(HypershuntMegastructure.class);
        classes.add(NidavelirComplexMegastructure.class);
        classes.add(PlutoMegastructure.class);
    }
}
