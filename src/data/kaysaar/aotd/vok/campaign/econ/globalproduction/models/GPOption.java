package data.kaysaar.aotd.vok.campaign.econ.globalproduction.models;


public class GPOption {
    public GPSpec spec;
    public boolean isDiscovered;
    public void updateSpec(){
        for (GPSpec GPSpec : GPManager.getInstance().getSpecs()) {
            if(GPSpec.getIdOfItemProduced().isEmpty())continue;
            if(GPSpec.getIdOfItemProduced().equals(spec.getIdOfItemProduced())){
                spec = GPSpec;
                break;
            }
        }
    }
    public GPOption(GPSpec spec , boolean isDiscovered){
        this.spec = spec;
        this.isDiscovered = isDiscovered;
    }

    public GPSpec getSpec() {
        return spec;
    }

    public void setDiscovered(boolean discovered) {
        isDiscovered = discovered;
    }

    public boolean isDiscovered() {
        return isDiscovered;
    }

}
