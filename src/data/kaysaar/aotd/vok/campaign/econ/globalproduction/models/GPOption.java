package data.kaysaar.aotd.vok.campaign.econ.globalproduction.models;


import com.fs.starfarer.api.Global;

public class GPOption {
    protected String specID;
    public boolean isDiscovered;
    public GPSpec.ProductionType type;
    public GPOption(String spec , boolean isDiscovered,GPSpec.ProductionType productionType){
        this.specID = spec;
        this.isDiscovered = isDiscovered;
        this. type = productionType;
    }

    public GPSpec getSpec() {
        for (GPSpec spec : GPManager.getInstance().getSpecs()) {
            if(spec.getProjectId().equals(this.specID)&&spec.getType()==this.type){
                return spec;
            }
        }
        return null;
    }


}
