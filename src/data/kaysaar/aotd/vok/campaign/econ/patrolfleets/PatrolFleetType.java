package data.kaysaar.aotd.vok.campaign.econ.patrolfleets;

public class PatrolFleetType {
        public enum PatrolType{
        SMALL,
        MEDIUM,
        LARGE,
        MERCENARY,
        AUTOMATED
    }
    public static int getAPPoints(PatrolType patrolType){
        if(patrolType == PatrolType.SMALL){
            return 50;
        }
        if(patrolType == PatrolType.MEDIUM){
            return 250;
        }
        if(patrolType == PatrolType.LARGE){
            return 250;
        }
        if(patrolType == PatrolType.MERCENARY){
            return 250;
        }
        if(patrolType == PatrolType.AUTOMATED){
            return 250;
        }
        return 0;
    }
    public static int getMaxDPPoints(PatrolType patrolType){
        if(patrolType == PatrolType.SMALL){
            return 50;
        }
        if(patrolType == PatrolType.MEDIUM){
            return 350;
        }
        if(patrolType == PatrolType.LARGE){
            return 500;
        }
        if(patrolType == PatrolType.MERCENARY){
            return 300;
        }
        if(patrolType == PatrolType.AUTOMATED){
            return 450;
        }
        return 0;
    }
    public static String getNameOfFleetType(PatrolType patrolType){
        if(patrolType == PatrolType.SMALL){
            return "Squadron";
        }
        if(patrolType == PatrolType.MEDIUM){
            return "Flotilla";
        }
        if(patrolType == PatrolType.LARGE){
            return "Armada";
        }
        if(patrolType == PatrolType.MERCENARY){
            return "Mercenary";
        }
        if(patrolType == PatrolType.AUTOMATED){
            return "Automated";
        }
        return "";
    }
}
