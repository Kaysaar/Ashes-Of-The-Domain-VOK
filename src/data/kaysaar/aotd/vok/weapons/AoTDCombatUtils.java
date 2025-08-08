package data.kaysaar.aotd.vok.weapons;


import com.fs.starfarer.api.combat.BoundsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class AoTDCombatUtils
{
    private static final Random random = new Random();

    /**
     * Checks whether a world-space point is within the collision bounds of the ship.
     */
    public static boolean isPointInShipBounds(ShipAPI ship, Vector2f point) {
        BoundsAPI bounds = ship.getExactBounds();
        if (bounds == null || !ship.isAlive()) return false;

        bounds.update(ship.getLocation(), ship.getFacing());

        List<Vector2f> polygon = new ArrayList<>();
        for (BoundsAPI.SegmentAPI segment : bounds.getSegments())
        {
            polygon.add(segment.getP1());
        }

        int crossings = 0;
        int count = polygon.size();
        for (int i = 0; i < count; i++)
        {
            Vector2f a = polygon.get(i);
            Vector2f b = polygon.get((i + 1) % count);

            if (((a.y > point.y) != (b.y > point.y)) &&
                    (point.x < (b.x - a.x) * (point.y - a.y) / ((b.y - a.y) + 1e-6f) + a.x))
            {
                crossings++;
            }
        }

        return (crossings % 2 == 1);
    }

    /**
     * Generates a random point within the ship's collision polygon using fan triangulation.
     */
    public static Vector2f getRandomPointInShipCollisionBounds(ShipAPI ship) {
        BoundsAPI bounds = ship.getExactBounds();
        if (bounds == null || !ship.isAlive()) return null;

        bounds.update(ship.getLocation(), ship.getFacing());

        List<Vector2f> polygon = new ArrayList<>();
        for (BoundsAPI.SegmentAPI segment : bounds.getSegments())
        {
            polygon.add(new Vector2f(segment.getP1()));
        }

        if (polygon.size() < 3) return null;

        // Triangulate with fan method and shuffle triangles to randomize bias
        List<Triangle> triangles = new ArrayList<>();
        Vector2f origin = polygon.get(0);
        for (int i = 1; i < polygon.size() - 1; i++)
        {
            triangles.add(new Triangle(origin, polygon.get(i), polygon.get(i + 1)));
        }

        Collections.shuffle(triangles, random); // Shuffle to improve spatial randomness

        // Recalculate cumulative areas after shuffle
        List<Float> cumulativeAreas = new ArrayList<>();
        float totalArea = 0f;
        for (Triangle tri : triangles)
        {
            totalArea += tri.getArea();
            cumulativeAreas.add(totalArea);
        }

        if (totalArea == 0f) return new Vector2f(origin);

        float r = random.nextFloat() * totalArea;
        int chosen = 0;
        for (int i = 0; i < cumulativeAreas.size(); i++)
        {
            if (r <= cumulativeAreas.get(i))
            {
                chosen = i;
                break;
            }
        }

        return triangles.get(chosen).getRandomPointInside();
    }


    /**
     * Private helper class for triangle geometry and random point sampling.
     */
    private static class Triangle {
        private final Vector2f a, b, c;

        public Triangle(Vector2f a, Vector2f b, Vector2f c)
        {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        public float getArea()
        {
            return 0.5f * Math.abs(
                    (a.x * (b.y - c.y)) +
                            (b.x * (c.y - a.y)) +
                            (c.x * (a.y - b.y)));
        }

        public Vector2f getRandomPointInside()
        {
            float u = random.nextFloat();
            float v = random.nextFloat();
            if (u + v > 1f)
            {
                u = 1f - u;
                v = 1f - v;
            }

            float x = a.x + u * (b.x - a.x) + v * (c.x - a.x);
            float y = a.y + u * (b.y - a.y) + v * (c.y - a.y);
            return new Vector2f(x, y);
        }
    }
}

