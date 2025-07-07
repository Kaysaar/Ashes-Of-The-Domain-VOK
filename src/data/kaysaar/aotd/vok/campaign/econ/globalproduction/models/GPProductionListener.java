package data.kaysaar.aotd.vok.campaign.econ.globalproduction.models;

public interface GPProductionListener {
    void reportPlayerProducedStuff(GPSpec spec, Object param,int amount);
}
