package data.kaysaar.aotd.vok.scripts.research.models;

import java.awt.Color;

public enum ResearchOptionEra {
    BASIC("Basic", new Color(135, 164, 181)),
    SOPHISTICATED("Sophisticated", new Color(80, 180, 160)),
    ADVANCED("Advanced", new Color(80, 145, 230)),
    UNIQUE("Unique", new Color(175, 110, 220)),
    PRE_COLLAPSE("Pre-collapse", new Color(225, 170, 70)),
    EXPERIMENTAL("Experimental", new Color(215, 80, 100));

    private final String displayName;
    private final Color color;

    ResearchOptionEra(String displayName, Color color) {
        this.displayName = displayName;
        this.color = color;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public Color getColor() {
        return color;
    }
}
