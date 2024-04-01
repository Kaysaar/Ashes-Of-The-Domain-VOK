package data.kaysaar.aotd.vok.scripts.research;

import com.fs.starfarer.api.Global;
import data.kaysaar.aotd.vok.models.ResearchOption;

import java.util.ArrayList;

public class ResearchQueueManager {

    public ArrayList<ResearchOption> queuedResearchOptions = new ArrayList<>();
    public String factionId;
    public ResearchQueueManager (String factionId){
        this.factionId = factionId;
    }
    public boolean addToQueue(String idOfResearchToAdd) {
        for (ResearchOption researchOption : AoTDMainResearchManager.getInstance().getSpecificFactionManager(Global.getSector().getFaction(factionId)).getResearchRepoOfFaction()) {
            if (researchOption.Id.equals(idOfResearchToAdd) && !queuedResearchOptions.contains(researchOption)) {
                queuedResearchOptions.add(researchOption);
                return true;
            }
        }
        return false;
    }

    public boolean removeFromQueue(String idOfResearchToRemove) {
        for (int i = 0; i < queuedResearchOptions.size(); i++) {
            if (queuedResearchOptions.get(i).Id.equals(idOfResearchToRemove)) {
                queuedResearchOptions.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean moveToTop(String idOfResearchToMove) {
        int targetIndex = -1;
        for (int i = 0; i < queuedResearchOptions.size(); i++) {
            if (queuedResearchOptions.get(i).Id.equals(idOfResearchToMove)) {
                targetIndex = i;
                break;
            }
        }
        if (targetIndex != -1) {
            ResearchOption targetOption = queuedResearchOptions.remove(targetIndex);
            queuedResearchOptions.add(0, targetOption);
            // Shift last item to the position of the moved item
            if (targetIndex < queuedResearchOptions.size() - 1) {
                ResearchOption lastItem = queuedResearchOptions.remove(queuedResearchOptions.size() - 1);
                queuedResearchOptions.add(targetIndex, lastItem);
            }
            return true;
        }
        return false;
    }

    public boolean moveToBottom(String idOfResearchToMove) {
        int targetIndex = -1;
        for (int i = 0; i < queuedResearchOptions.size(); i++) {
            if (queuedResearchOptions.get(i).Id.equals(idOfResearchToMove)) {
                targetIndex = i;
                break;
            }
        }
        if (targetIndex != -1) {
            ResearchOption targetOption = queuedResearchOptions.remove(targetIndex);
            queuedResearchOptions.add(targetOption);
            // Shift first item to the position of the moved item if not moving the first one
            if (targetIndex > 0) {
                ResearchOption firstItem = queuedResearchOptions.remove(0);
                queuedResearchOptions.add(targetIndex - 1, firstItem);
            }
            return true;
        }
        return false;
    }

    public boolean moveUp(String idOfResearchToMove) {
        if (queuedResearchOptions.size() <= 1) return false;

        for (int i = 0; i < queuedResearchOptions.size(); i++) {
            if (queuedResearchOptions.get(i).Id.equals(idOfResearchToMove)) {
                if (i == 0) { // If it's the first item, move it to the last position
                    ResearchOption temp = queuedResearchOptions.remove(0);
                    queuedResearchOptions.add(temp);
                } else {
                    // Swap the current item with the one before it
                    ResearchOption temp = queuedResearchOptions.get(i);
                    queuedResearchOptions.set(i, queuedResearchOptions.get(i - 1));
                    queuedResearchOptions.set(i - 1, temp);
                }
                return true;
            }
        }
        return false;
    }

    public boolean moveDown(String idOfResearchToMove) {
        if (queuedResearchOptions.size() <= 1) return false;

        for (int i = 0; i < queuedResearchOptions.size(); i++) {
            if (queuedResearchOptions.get(i).Id.equals(idOfResearchToMove)) {
                if (i == queuedResearchOptions.size() - 1) { // If it's the last item, move it to the first position
                    ResearchOption temp = queuedResearchOptions.remove(queuedResearchOptions.size() - 1);
                    queuedResearchOptions.add(0, temp);
                } else {
                    // Swap the current item with the one after it
                    ResearchOption temp = queuedResearchOptions.get(i);
                    queuedResearchOptions.set(i, queuedResearchOptions.get(i + 1));
                    queuedResearchOptions.set(i + 1, temp);
                }
                return true;
            }
        }
        return false;
    }
    public ResearchOption removeFromTop() {
        if (!queuedResearchOptions.isEmpty()) {
            return queuedResearchOptions.remove(0);
        }
        return null;
    }
    public boolean isInQueue(String idOfResearchToCheck) {
        for (ResearchOption researchOption : queuedResearchOptions) {
            if (researchOption.Id.equals(idOfResearchToCheck)) {
                return true;
            }
        }
        return false;
    }
    public ArrayList<ResearchOption> getQueuedResearchOptions() {
        return queuedResearchOptions;
    }


}