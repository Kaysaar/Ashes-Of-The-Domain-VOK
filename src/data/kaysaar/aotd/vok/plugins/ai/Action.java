package data.kaysaar.aotd.vok.plugins.ai;

public enum Action {
    BUILD(0), UPGRADE(1), DEMOLISH(2);

    private int index;

    Action(int index) {
        this.index = index;
    }

    public static Action getActionByIndex(int index) {
        for (Action action : values()) {
            if (action.index == index) {
                return action;
            }
        }
        throw new IllegalArgumentException("Invalid action index: " + index);
    }
}
