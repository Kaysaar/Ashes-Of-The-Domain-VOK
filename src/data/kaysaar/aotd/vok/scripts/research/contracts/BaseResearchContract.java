package data.kaysaar.aotd.vok.scripts.research.contracts;

import java.util.ArrayList;

public class BaseResearchContract {
    public float currentProgressionOfContract;
    public boolean hasCompletedContract;
    public ResearchContractSpec spec;
    public boolean receivedPenalty;
    public ArrayList<String>prioritizedTech;
    public float penaltyModifier;

    public ResearchContractSpec getSpec() {

        return spec;
    }
}
