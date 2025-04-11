package data.kaysaar.aotd.vok.scripts.misc;

    public class TrapezoidButtonDetector {
    public boolean determineIfHoversOverButton(
            float topLeftX, float topLeftY,
            float topRightX, float topRightY,
            float botLeftX, float botLeftY,
            float botRightX, float botRightY,
            float mouseX, float mouseY
    ) {
        // Split the trapezoid into two triangles and check each one
        return pointInTriangle(mouseX, mouseY, topLeftX, topLeftY, botLeftX, botLeftY, botRightX, botRightY)
                || pointInTriangle(mouseX, mouseY, topLeftX, topLeftY, topRightX, topRightY, botRightX, botRightY);
    }

    private boolean pointInTriangle(float px, float py, float ax, float ay, float bx, float by, float cx, float cy) {
        float v0x = cx - ax;
        float v0y = cy - ay;
        float v1x = bx - ax;
        float v1y = by - ay;
        float v2x = px - ax;
        float v2y = py - ay;

        float dot00 = v0x * v0x + v0y * v0y;
        float dot01 = v0x * v1x + v0y * v1y;
        float dot02 = v0x * v2x + v0y * v2y;
        float dot11 = v1x * v1x + v1y * v1y;
        float dot12 = v1x * v2x + v1y * v2y;

        float invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
        float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
        float v = (dot00 * dot12 - dot01 * dot02) * invDenom;

        return (u >= 0) && (v >= 0) && (u + v < 1);
    }
}
