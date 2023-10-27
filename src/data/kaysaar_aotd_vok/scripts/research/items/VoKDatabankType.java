package data.kaysaar_aotd_vok.scripts.research.items;

public enum VoKDatabankType {
    PRISTINE,
    DECAYED,
    DESTROYED;
    public static VoKDatabankType fromInteger(int x) {
        switch(x) {
            case 0:
                return PRISTINE;
            case 1:
                return DECAYED;
            case 2:
                return DESTROYED;
        }
        return null;
    }
}
