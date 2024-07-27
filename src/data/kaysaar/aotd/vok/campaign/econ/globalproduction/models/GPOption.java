package data.kaysaar.aotd.vok.campaign.econ.globalproduction.models;


import com.fs.starfarer.api.Global;

public class GPOption {
   transient GPSpec spec;
    public boolean isDiscovered;
    public GPSpec.ProductionType type;
    public GPOption(GPSpec spec , boolean isDiscovered,GPSpec.ProductionType productionType){
        this.spec = spec;
        this.isDiscovered = isDiscovered;
        this. type = productionType;
    }

    public GPSpec getSpec() {

        return this.spec;
    }


}
