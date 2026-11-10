package data.kaysaar.aotd.vok.ui.research;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.PositionAPI;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.ui.basecomps.ResizableComponent;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;


public class ResearchTreeConnector extends ResizableComponent {

    // Card and rendering dimensions.
    private static final float CARD_WIDTH = AoTDUiComp.WIDTH_OF_TECH_PANEL;
    private static final float CARD_HEIGHT = AoTDUiComp.HEIGHT_OF_TECH_PANEL;
    private static final float LINE_WIDTH_PX = 1.5f;
    private static final float LINE_ALPHA = 0.80f;
    private static final float COLOR_CHANNEL_MAX = 255f;
    private static final Color RESEARCHED_COLOR = new Color(210, 138, 7);

    // Hard routing constraints (world coordinates).
    private static final float CARD_INTERIOR_INSET = 0.35f;
    private static final float CARD_CLEARANCE = 12f;
    private static final float MIN_WIRE_SEPARATION = 12f;
    private static final float PORT_STUB = 22f;
    private static final float MAX_DIAGONAL_RISE = 140f;
    private static final float CHAMFER_SIZE = 9f;
    private static final float CHAMFER_FRACTION = 0.22f;

    // Port placement and candidate search.
    private static final float MAX_PORT_SPACING = 16f;
    private static final float PORT_SPREAD = 92f;
    private static final float PORT_ALIGNMENT_EPSILON = 0.01f;
    private static final float MIN_DIAGONAL_RISE = 0.5f;
    private static final int DIAGONAL_SAMPLES = 9;
    private static final int TRACK_SAMPLES = 9;
    private static final float TRACK_CARD_MARGIN = 3f;
    private static final float TRACK_BYPASS_OFFSET = 30f;
    private static final float TRACK_ORDER_EPSILON = 0.5f;
    private static final float SHORT_BYPASS_OFFSET = 35f;
    private static final float LONG_BYPASS_OFFSET = 50f;
    private static final float[] SHORT_BYPASS_X_OFFSETS = {30f, 60f, 100f};

    // Candidate scoring. Values deliberately retain the old router's priorities.
    private static final float LENGTH_WEIGHT = 0.06f;
    private static final float VERTICAL_SEGMENT_COST = 40f;
    private static final float VERTICAL_COUNT_COST = 240f;
    private static final float DIAGONAL_SEGMENT_COST = 50f;
    private static final float BACKTRACK_COST = 120f;
    private static final float TURN_COST = 65f;
    private static final float DIRECTION_EPSILON = 0.05f;

    // Fallback grid search (used only if simpler routes fail).
    private static final float GRID_SIZE = 24f;
    private static final float GRID_X_MARGIN = 220f;
    private static final float GRID_Y_MARGIN = 340f;
    private static final long MAX_GRID_CELLS = 125_000L;
    private static final int MAX_SEARCH_VISITS = 55_000;
    private static final int DIRECTION_COUNT = 4;
    private static final int UNSET_DIRECTION = DIRECTION_COUNT;
    private static final int SEARCH_STATES_PER_CELL = DIRECTION_COUNT + 1;
    private static final int[] DIRECTION_X = {1, 0, -1, 0};
    private static final int[] DIRECTION_Y = {0, 1, 0, -1};
    private static final float GRID_STEP_COST = GRID_SIZE;
    private static final float VERTICAL_GRID_COST = 65f;
    private static final float REVERSE_GRID_COST = 65f;
    private static final float GRID_TURN_COST = 550f;
    private static final float SEARCH_STALE_EPSILON = 0.001f;

    // Numerical tolerances for geometry and path simplification.
    private static final float POINT_EPSILON = 0.01f;
    private static final float COLLINEAR_EPSILON = 0.02f;
    private static final float DIAGONAL_COMPONENT_EPSILON = 0.03f;
    private static final float DIAGONAL_SLOPE_EPSILON = 0.04f;
    private static final float ENDPOINT_EPSILON = 0.1f;
    private static final double INTERSECTION_EPSILON = 0.0001;
    private static final float SEGMENT_BOUNDS_EPSILON = 0.001f;
    private static final double MIN_SEGMENT_LENGTH_SQUARED = 0.0001;

    private final ResearchZoomPanel tree;
    private final List<Node> nodes = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();
    private final List<Line> reservedLines = new ArrayList<>();

    public ResearchTreeConnector(ResearchZoomPanel tree, Map<ResearchPanelComponent, float[]> nodePositions) {
        this.tree = tree;
        componentPanel = Global.getSettings().createCustom(1f, 1f, this);
        build(nodePositions);
    }

    // -------------------------------------------------------------------------
    // Graph construction and port assignment
    // -------------------------------------------------------------------------

    private void build(Map<ResearchPanelComponent, float[]> positions) {
        if (positions == null || positions.isEmpty()) return;

        Map<String, Node> lookup = buildNodes(positions);
        buildEdges(lookup);
        assignAllPorts();
        routeAllEdges();
    }

    private Map<String, Node> buildNodes(Map<ResearchPanelComponent, float[]> positions) {
        Map<String, Node> lookup = new HashMap<>();
        List<Map.Entry<ResearchPanelComponent, float[]>> entries = new ArrayList<>(positions.entrySet());

        // Technology IDs, not HashMap iteration order, determine routing order.
        entries.removeIf(entry -> entry.getKey() == null || entry.getKey().spec == null
                || entry.getValue() == null || entry.getValue().length < 2);
        entries.sort(Comparator.comparing(entry -> entry.getKey().spec.getId()));

        for (Map.Entry<ResearchPanelComponent, float[]> entry : entries) {
            float[] position = entry.getValue();
            Node node = new Node(entry.getKey(), position[0], position[1]);
            nodes.add(node);
            lookup.put(node.id(), node);
        }
        return lookup;
    }

    private void buildEdges(Map<String, Node> lookup) {
        for (Node child : nodes) {
            List<String> requirements = child.panel.spec.getReqTechsToResearchFirst();
            if (requirements == null) continue;

            List<String> sortedRequirements = new ArrayList<>();
            for (String id : requirements) {
                if (id != null && !id.isEmpty()) sortedRequirements.add(id);
            }
            Collections.sort(sortedRequirements);

            for (String parentId : sortedRequirements) {
                Node parent = lookup.get(parentId);
                if (parent == null || parent == child || alreadyConnected(parent, child)) continue;

                Edge edge = new Edge(parent, child);
                edges.add(edge);
                parent.outgoing.add(edge);
                child.incoming.add(edge);
            }
        }
    }

    private static boolean alreadyConnected(Node parent, Node child) {
        for (Edge edge : child.incoming) {
            if (edge.parent == parent) return true;
        }
        return false;
    }

    private void assignAllPorts() {
        for (Node node : nodes) {
            node.outgoing.sort(Comparator.comparingDouble((Edge edge) -> edge.child.centerY())
                    .thenComparing(edge -> edge.child.id()));
            node.incoming.sort(Comparator.comparingDouble((Edge edge) -> edge.parent.centerY())
                    .thenComparing(edge -> edge.parent.id()));

            assignPorts(node, node.outgoing, true);
            assignPorts(node, node.incoming, false);
        }
    }

    private static void assignPorts(Node node, List<Edge> connected, boolean outgoing) {
        if (connected.isEmpty()) return;

        int alignedIndex = -1;
        for (int i = 0; i < connected.size(); i++) {
            Node other = outgoing ? connected.get(i).child : connected.get(i).parent;
            if (Math.abs(other.centerY() - node.centerY()) <= PORT_ALIGNMENT_EPSILON) {
                alignedIndex = i;
                break;
            }
        }

        float spacing = Math.min(MAX_PORT_SPACING,
                PORT_SPREAD / Math.max(1, connected.size() - 1));
        for (int i = 0; i < connected.size(); i++) {
            // An aligned connection keeps the center port; its siblings fan around it.
            float offset = alignedIndex >= 0
                    ? (i - alignedIndex) * spacing
                    : (i - (connected.size() - 1) / 2f) * spacing;

            if (outgoing) connected.get(i).startY = node.centerY() + offset;
            else connected.get(i).endY = node.centerY() + offset;
        }
    }

    private void routeAllEdges() {
        // An unobstructed same-row connection must be routed before branches.
        edges.sort(Comparator.comparingInt((Edge edge) -> isDirectHorizontal(edge) ? 0 : 1)
                .thenComparingDouble(edge -> Math.abs(edge.child.centerY() - edge.parent.centerY()))
                .thenComparing(edge -> edge.parent.id())
                .thenComparing(edge -> edge.child.id()));

        for (Edge edge : edges) {
            route(edge);
            if (edge.path.size() < 2) {
                // Fixed positions can make a planar route impossible. Never draw an overlap.
                System.err.println("VoK tech tree: no disjoint connector route: "
                        + edge.parent.id() + " -> " + edge.child.id());
                continue;
            }

            edge.drawPath.addAll(beveled(edge.path));
            reserve(edge.drawPath);
        }
    }

    private boolean isDirectHorizontal(Edge edge) {
        if (Math.abs(edge.parent.centerY() - edge.child.centerY()) > PORT_ALIGNMENT_EPSILON
                || edge.child.x <= edge.parent.right()) return false;
        return valid(Arrays.asList(new Point(edge.parent.right(), edge.parent.centerY()),
                new Point(edge.child.x, edge.child.centerY())), edge);
    }

    private void reserve(List<Point> path) {
        for (int i = 1; i < path.size(); i++) {
            reservedLines.add(new Line(path.get(i - 1), path.get(i)));
        }
    }

    // -------------------------------------------------------------------------
    // Route candidates: straight, diagonal, single trunk, then detours
    // -------------------------------------------------------------------------

    private void route(Edge edge) {
        edge.path.clear();
        Point from = new Point(edge.parent.right(), edge.startY);
        Point to = new Point(edge.child.x, edge.endY);
        float distanceX = to.x - from.x;

        if (sameRow(edge) && distanceX > 0f) {
            Candidate direct = consider(null, Arrays.asList(
                    new Point(from.x, edge.parent.centerY()),
                    new Point(to.x, edge.child.centerY())), edge);
            if (direct != null) {
                edge.path.addAll(direct.points);
                return;
            }
        }

        Candidate best = null;
        if (Math.abs(from.y - to.y) < PORT_ALIGNMENT_EPSILON && distanceX > 0f) {
            best = consider(best, Arrays.asList(from, to), edge);
        }

        best = tryDiagonalRoutes(best, from, to, distanceX, edge);
        if (distanceX > 2f * PORT_STUB) {
            best = tryTrunkRoutes(best, from, to, edge);
        }
        if (best == null && distanceX <= 2f * PORT_STUB) {
            best = tryShortDistanceBypasses(from, to, edge);
        }
        if (best == null) {
            best = consider(null, orthogonalFallback(from, to), edge);
        }
        if (best != null) edge.path.addAll(best.points);
    }

    private static boolean sameRow(Edge edge) {
        return Math.abs(edge.parent.centerY() - edge.child.centerY()) < PORT_ALIGNMENT_EPSILON;
    }

    private Candidate tryDiagonalRoutes(Candidate best, Point from, Point to,
                                        float distanceX, Edge edge) {
        float rise = Math.abs(to.y - from.y);
        if (distanceX <= 2f * PORT_STUB + rise
                || rise <= MIN_DIAGONAL_RISE || rise > MAX_DIAGONAL_RISE) return best;

        float first = from.x + PORT_STUB;
        float last = to.x - PORT_STUB - rise;
        for (int i = 0; i < DIAGONAL_SAMPLES; i++) {
            float x = first + (last - first) * i / (DIAGONAL_SAMPLES - 1f);
            best = consider(best, Arrays.asList(from, new Point(x, from.y),
                    new Point(x + rise, to.y), to), edge);
        }
        return best;
    }

    private Candidate tryTrunkRoutes(Candidate best, Point from, Point to, Edge edge) {
        List<Float> xs = xTracks(from.x + PORT_STUB, to.x - PORT_STUB);
        for (float x : xs) {
            best = consider(best, Arrays.asList(from, new Point(x, from.y),
                    new Point(x, to.y), to), edge);
        }

        // Two vertical trunks are only considered if all simpler candidates failed.
        if (best != null) return best;
        for (float y : yTracks(from.y, to.y)) {
            for (float left : xs) {
                for (float right : xs) {
                    if (right <= left + TRACK_ORDER_EPSILON) continue;
                    best = consider(best, twoTrunkPath(from, to, left, right, y), edge);
                }
            }
        }
        return best;
    }

    private Candidate tryShortDistanceBypasses(Point from, Point to, Edge edge) {
        float minTop = Math.min(edge.parent.y, edge.child.y);
        float maxBottom = Math.max(edge.parent.bottom(), edge.child.bottom());
        float[] ys = {
                minTop - SHORT_BYPASS_OFFSET,
                maxBottom + SHORT_BYPASS_OFFSET,
                minTop - CARD_HEIGHT - LONG_BYPASS_OFFSET,
                maxBottom + CARD_HEIGHT + LONG_BYPASS_OFFSET
        };

        Candidate best = null;
        for (float y : ys) {
            for (float leftOffset : SHORT_BYPASS_X_OFFSETS) {
                for (float rightOffset : SHORT_BYPASS_X_OFFSETS) {
                    best = consider(best, twoTrunkPath(from, to,
                            from.x + leftOffset, to.x - rightOffset, y), edge);
                }
            }
        }
        return best;
    }

    private static List<Point> twoTrunkPath(Point from, Point to,
                                            float left, float right, float y) {
        return Arrays.asList(from, new Point(left, from.y), new Point(left, y),
                new Point(right, y), new Point(right, to.y), to);
    }

    private List<Float> xTracks(float left, float right) {
        List<Float> tracks = new ArrayList<>();
        if (right < left) return tracks;

        tracks.add((left + right) * 0.5f);
        for (int i = 0; i < TRACK_SAMPLES; i++) {
            tracks.add(left + (right - left) * (i + 0.5f) / TRACK_SAMPLES);
        }
        for (Node node : nodes) {
            addIfBetween(tracks, node.x - CARD_CLEARANCE - TRACK_CARD_MARGIN, left, right);
            addIfBetween(tracks, node.right() + CARD_CLEARANCE + TRACK_CARD_MARGIN, left, right);
        }
        return tracks;
    }

    private List<Float> yTracks(float start, float end) {
        List<Float> tracks = new ArrayList<>();
        float low = Math.min(start, end);
        float high = Math.max(start, end);

        tracks.add((low + high) / 2f);
        tracks.add(low - TRACK_BYPASS_OFFSET);
        tracks.add(high + TRACK_BYPASS_OFFSET);
        for (Node node : nodes) {
            addIfBetween(tracks, node.y - CARD_CLEARANCE - TRACK_CARD_MARGIN, low, high);
            addIfBetween(tracks, node.bottom() + CARD_CLEARANCE + TRACK_CARD_MARGIN, low, high);
        }
        tracks.add(low - CARD_HEIGHT - 2f * CARD_CLEARANCE);
        tracks.add(high + CARD_HEIGHT + 2f * CARD_CLEARANCE);
        return tracks;
    }

    private static void addIfBetween(List<Float> tracks, float value, float low, float high) {
        if (value > low && value < high) tracks.add(value);
    }

    private Candidate consider(Candidate best, List<Point> proposed, Edge edge) {
        if (proposed == null || proposed.size() < 2) return best;

        List<Point> clean = simplify(proposed);
        if (!valid(clean, edge)) return best;

        List<Point> drawn = beveled(clean);
        if (!valid(drawn, edge) || intersectsItself(drawn) || conflictsWithReserved(drawn)) {
            return best;
        }

        float score = score(clean);
        return best == null || score < best.score ? new Candidate(clean, score) : best;
    }

    private static float score(List<Point> path) {
        float score = 0f;
        int verticals = 0;
        int turns = Math.max(0, path.size() - 2);
        for (int i = 1; i < path.size(); i++) {
            Point a = path.get(i - 1);
            Point b = path.get(i);
            float dx = b.x - a.x;
            float dy = b.y - a.y;

            score += (float) Math.hypot(dx, dy) * LENGTH_WEIGHT;
            if (Math.abs(dx) < DIRECTION_EPSILON) {
                verticals++;
                score += VERTICAL_SEGMENT_COST;
            }
            if (Math.abs(dx) > DIRECTION_EPSILON && Math.abs(dy) > DIRECTION_EPSILON) {
                score += DIAGONAL_SEGMENT_COST;
            }
            if (dx < -DIRECTION_EPSILON) score += BACKTRACK_COST;
        }
        score += verticals * VERTICAL_COUNT_COST + turns * TURN_COST;
        return score;
    }

    // -------------------------------------------------------------------------
    // Route validation and geometry
    // -------------------------------------------------------------------------

    private boolean valid(List<Point> path, Edge edge) {
        if (path == null || path.size() < 2) return false;

        Point start = path.get(0);
        Point second = path.get(1);
        Point beforeEnd = path.get(path.size() - 2);
        Point end = path.get(path.size() - 1);
        if (Math.abs(start.x - edge.parent.right()) > ENDPOINT_EPSILON
                || Math.abs(end.x - edge.child.x) > ENDPOINT_EPSILON) return false;
        if (second.x < start.x + DIRECTION_EPSILON
                || beforeEnd.x > end.x - DIRECTION_EPSILON) return false;

        for (int i = 1; i < path.size(); i++) {
            Point a = path.get(i - 1);
            Point b = path.get(i);
            float dx = b.x - a.x;
            float dy = b.y - a.y;
            if (Math.abs(dx) < POINT_EPSILON && Math.abs(dy) < POINT_EPSILON) continue;
            if (!isOrthogonalOr45Degree(dx, dy)) return false;

            for (Node node : nodes) {
                float inset = node == edge.parent || node == edge.child
                        ? CARD_INTERIOR_INSET : -CARD_CLEARANCE;
                Rectangle2D.Float bounds = new Rectangle2D.Float(node.x + inset,
                        node.y + inset, CARD_WIDTH - 2f * inset, CARD_HEIGHT - 2f * inset);
                if (bounds.intersectsLine(a.x, a.y, b.x, b.y)) return false;
            }
        }
        return true;
    }

    private static boolean isOrthogonalOr45Degree(float dx, float dy) {
        return Math.abs(dx) <= DIAGONAL_COMPONENT_EPSILON
                || Math.abs(dy) <= DIAGONAL_COMPONENT_EPSILON
                || Math.abs(Math.abs(dx) - Math.abs(dy)) <= DIAGONAL_SLOPE_EPSILON;
    }

    private static List<Point> simplify(List<Point> path) {
        List<Point> clean = new ArrayList<>();
        for (Point point : path) {
            if (!clean.isEmpty() && samePoint(clean.get(clean.size() - 1), point)) continue;

            while (clean.size() >= 2) {
                Point a = clean.get(clean.size() - 2);
                Point b = clean.get(clean.size() - 1);
                float dx = b.x - a.x;
                float dy = b.y - a.y;
                float ex = point.x - b.x;
                float ey = point.y - b.y;

                if (Math.abs(dx * ey - dy * ex) < COLLINEAR_EPSILON && dx * ex + dy * ey > 0f) {
                    clean.remove(clean.size() - 1);
                } else {
                    break;
                }
            }
            clean.add(point);
        }
        return clean;
    }

    private static boolean samePoint(Point a, Point b) {
        return Math.abs(a.x - b.x) < POINT_EPSILON
                && Math.abs(a.y - b.y) < POINT_EPSILON;
    }

    private static List<Point> beveled(List<Point> path) {
        if (path.size() < 3) return path;

        List<Point> drawn = new ArrayList<>();
        drawn.add(path.get(0));
        for (int i = 1; i < path.size() - 1; i++) {
            Point previous = path.get(i - 1);
            Point corner = path.get(i);
            Point next = path.get(i + 1);
            float ax = corner.x - previous.x;
            float ay = corner.y - previous.y;
            float bx = next.x - corner.x;
            float by = next.y - corner.y;
            float firstLength = (float) Math.hypot(ax, ay);
            float nextLength = (float) Math.hypot(bx, by);

            boolean rightAngle = (Math.abs(ax) < POINT_EPSILON && Math.abs(by) < POINT_EPSILON)
                    || (Math.abs(ay) < POINT_EPSILON && Math.abs(bx) < POINT_EPSILON);
            if (!rightAngle || firstLength < CHAMFER_SIZE || nextLength < CHAMFER_SIZE) {
                drawn.add(corner);
                continue;
            }

            float cut = Math.min(CHAMFER_SIZE,
                    Math.min(firstLength, nextLength) * CHAMFER_FRACTION);
            drawn.add(new Point(corner.x - ax / firstLength * cut,
                    corner.y - ay / firstLength * cut));
            drawn.add(new Point(corner.x + bx / nextLength * cut,
                    corner.y + by / nextLength * cut));
        }
        drawn.add(path.get(path.size() - 1));
        return drawn;
    }

    private boolean conflictsWithReserved(List<Point> path) {
        for (int i = 1; i < path.size(); i++) {
            Point a = path.get(i - 1);
            Point b = path.get(i);
            for (Line line : reservedLines) {
                if (segmentsTooClose(a, b, line.a, line.b)) return true;
            }
        }
        return false;
    }

    private static boolean intersectsItself(List<Point> path) {
        for (int i = 1; i < path.size(); i++) {
            for (int j = i + 2; j < path.size(); j++) {
                if (intersectsInclusive(path.get(i - 1), path.get(i),
                        path.get(j - 1), path.get(j))) return true;
            }
        }
        return false;
    }

    private static boolean segmentsTooClose(Point a, Point b, Point c, Point d) {
        if (intersectsInclusive(a, b, c, d)) return true;
        float minDistanceSquared = MIN_WIRE_SEPARATION * MIN_WIRE_SEPARATION;
        float closestSquared = Math.min(
                Math.min(pointSegmentDistanceSquared(a, c, d),
                        pointSegmentDistanceSquared(b, c, d)),
                Math.min(pointSegmentDistanceSquared(c, a, b),
                        pointSegmentDistanceSquared(d, a, b)));
        return closestSquared < minDistanceSquared;
    }

    private static boolean intersectsInclusive(Point a, Point b, Point c, Point d) {
        double s1 = cross(b.x - a.x, b.y - a.y, c.x - a.x, c.y - a.y);
        double s2 = cross(b.x - a.x, b.y - a.y, d.x - a.x, d.y - a.y);
        double s3 = cross(d.x - c.x, d.y - c.y, a.x - c.x, a.y - c.y);
        double s4 = cross(d.x - c.x, d.y - c.y, b.x - c.x, b.y - c.y);

        boolean crossesAB = (s1 > INTERSECTION_EPSILON && s2 < -INTERSECTION_EPSILON)
                || (s1 < -INTERSECTION_EPSILON && s2 > INTERSECTION_EPSILON);
        boolean crossesCD = (s3 > INTERSECTION_EPSILON && s4 < -INTERSECTION_EPSILON)
                || (s3 < -INTERSECTION_EPSILON && s4 > INTERSECTION_EPSILON);
        if (crossesAB && crossesCD) return true;

        return (Math.abs(s1) <= INTERSECTION_EPSILON && onSegment(c, a, b))
                || (Math.abs(s2) <= INTERSECTION_EPSILON && onSegment(d, a, b))
                || (Math.abs(s3) <= INTERSECTION_EPSILON && onSegment(a, c, d))
                || (Math.abs(s4) <= INTERSECTION_EPSILON && onSegment(b, c, d));
    }

    private static boolean onSegment(Point p, Point a, Point b) {
        return p.x >= Math.min(a.x, b.x) - SEGMENT_BOUNDS_EPSILON
                && p.x <= Math.max(a.x, b.x) + SEGMENT_BOUNDS_EPSILON
                && p.y >= Math.min(a.y, b.y) - SEGMENT_BOUNDS_EPSILON
                && p.y <= Math.max(a.y, b.y) + SEGMENT_BOUNDS_EPSILON;
    }

    private static double cross(double ax, double ay, double bx, double by) {
        return ax * by - ay * bx;
    }

    private static float pointSegmentDistanceSquared(Point point, Point a, Point b) {
        double dx = b.x - a.x;
        double dy = b.y - a.y;
        double lengthSquared = dx * dx + dy * dy;
        double t = lengthSquared < MIN_SEGMENT_LENGTH_SQUARED ? 0.0 : Math.max(0.0,
                Math.min(1.0, ((point.x - a.x) * dx + (point.y - a.y) * dy) / lengthSquared));
        double offsetX = point.x - (a.x + dx * t);
        double offsetY = point.y - (a.y + dy * t);
        return (float) (offsetX * offsetX + offsetY * offsetY);
    }

    // -------------------------------------------------------------------------
    // Orthogonal grid fallback
    // -------------------------------------------------------------------------

    private List<Point> orthogonalFallback(Point from, Point to) {
        Grid grid = new Grid(from, to);
        if (grid.cellCount() > MAX_GRID_CELLS) return Collections.emptyList();
        markBlockedCells(grid);

        int startX = grid.column(from.x + PORT_STUB);
        int endX = grid.column(to.x - PORT_STUB);
        int startY = grid.row(from.y);
        int endY = grid.row(to.y);
        if (!grid.inBounds(startX, startY) || !grid.inBounds(endX, endY)) {
            return Collections.emptyList();
        }

        int start = grid.index(startX, startY);
        int goal = grid.index(endX, endY);
        if (grid.blocked[start] || grid.blocked[goal]) return Collections.emptyList();

        List<Point> found = searchGrid(grid, start, goal);
        if (found.isEmpty()) return found;

        // Keep the ports attached to the actual cards, not to the grid points.
        List<Point> joined = new ArrayList<>();
        joined.add(from);
        joined.add(new Point(found.get(0).x, from.y));
        joined.addAll(found);
        joined.add(new Point(found.get(found.size() - 1).x, to.y));
        joined.add(to);
        return joined;
    }

    private void markBlockedCells(Grid grid) {
        for (Node node : nodes) {
            int x0 = Math.max(0, (int) Math.floor((node.x - CARD_CLEARANCE - grid.minX) / GRID_SIZE));
            int x1 = Math.min(grid.cols - 1,
                    (int) Math.ceil((node.right() + CARD_CLEARANCE - grid.minX) / GRID_SIZE));
            int y0 = Math.max(0, (int) Math.floor((node.y - CARD_CLEARANCE - grid.minY) / GRID_SIZE));
            int y1 = Math.min(grid.rows - 1,
                    (int) Math.ceil((node.bottom() + CARD_CLEARANCE - grid.minY) / GRID_SIZE));

            for (int y = y0; y <= y1; y++) {
                for (int x = x0; x <= x1; x++) {
                    Point point = grid.point(x, y);
                    if (point.x > node.x - CARD_CLEARANCE
                            && point.x < node.right() + CARD_CLEARANCE
                            && point.y > node.y - CARD_CLEARANCE
                            && point.y < node.bottom() + CARD_CLEARANCE) {
                        grid.blocked[grid.index(x, y)] = true;
                    }
                }
            }
        }
    }

    private List<Point> searchGrid(Grid grid, int start, int goal) {
        int stateCount = grid.blocked.length * SEARCH_STATES_PER_CELL;
        float[] costs = new float[stateCount];
        int[] previous = new int[stateCount];
        Arrays.fill(costs, Float.POSITIVE_INFINITY);
        Arrays.fill(previous, -1);

        PriorityQueue<Search> queue = new PriorityQueue<>(Comparator
                .comparingDouble((Search search) -> search.cost)
                .thenComparingInt(search -> search.state));
        int initial = state(start, UNSET_DIRECTION);
        costs[initial] = 0f;
        queue.add(new Search(initial, 0f));

        int finish = -1;
        int visits = 0;
        while (!queue.isEmpty() && visits++ < MAX_SEARCH_VISITS) {
            Search current = queue.poll();
            if (current.cost > costs[current.state] + SEARCH_STALE_EPSILON) continue;

            int cell = current.state / SEARCH_STATES_PER_CELL;
            int previousDirection = current.state % SEARCH_STATES_PER_CELL;
            if (cell == goal) {
                finish = current.state;
                break;
            }

            int x = cell % grid.cols;
            int y = cell / grid.cols;
            for (int direction = 0; direction < DIRECTION_COUNT; direction++) {
                int nextX = x + DIRECTION_X[direction];
                int nextY = y + DIRECTION_Y[direction];
                if (!grid.inBounds(nextX, nextY)) continue;

                int nextCell = grid.index(nextX, nextY);
                if (grid.blocked[nextCell]) continue;
                if (conflictsWithReserved(grid.point(x, y), grid.point(nextX, nextY))) continue;

                float nextCost = current.cost + gridStepCost(previousDirection, direction);
                int nextState = state(nextCell, direction);
                if (nextCost < costs[nextState]) {
                    costs[nextState] = nextCost;
                    previous[nextState] = current.state;
                    queue.add(new Search(nextState, nextCost));
                }
            }
        }
        return reconstructGridPath(grid, finish, previous);
    }

    private static float gridStepCost(int previousDirection, int direction) {
        float cost = GRID_STEP_COST;
        if (direction == 1 || direction == 3) cost += VERTICAL_GRID_COST;
        if (direction == 2) cost += REVERSE_GRID_COST;
        if (previousDirection < DIRECTION_COUNT && previousDirection != direction) {
            cost += GRID_TURN_COST;
        }
        return cost;
    }

    private boolean conflictsWithReserved(Point a, Point b) {
        for (Line line : reservedLines) {
            if (segmentsTooClose(a, b, line.a, line.b)) return true;
        }
        return false;
    }

    private static int state(int cell, int direction) {
        return cell * SEARCH_STATES_PER_CELL + direction;
    }

    private static List<Point> reconstructGridPath(Grid grid, int finish, int[] previous) {
        if (finish < 0) return Collections.emptyList();

        List<Point> path = new ArrayList<>();
        for (int current = finish; current >= 0; current = previous[current]) {
            int cell = current / SEARCH_STATES_PER_CELL;
            path.add(grid.point(cell % grid.cols, cell / grid.cols));
        }
        Collections.reverse(path);
        return path;
    }

    // -------------------------------------------------------------------------
    // Draw only: the existing card positions are the endpoint anchors
    // -------------------------------------------------------------------------

    @Override
    public void renderBelow(float alphaMult) {
        if (edges.isEmpty() || alphaMult <= 0f || tree.getPluginPanel() == null) return;

        PositionAPI canvas = tree.getPluginPanel().getPosition();
        float zoom = tree.currScale;
        float originX = canvas.getX() + tree.data.getCurrentOffsetX() * zoom;
        float originY = canvas.getY() + canvas.getHeight() + tree.data.getCurrentOffsetY() * zoom;

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT
                | GL11.GL_LINE_BIT | GL11.GL_CURRENT_BIT | GL11.GL_HINT_BIT);
        try {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
            GL11.glLineWidth(LINE_WIDTH_PX);

            for (Edge edge : edges) {
                if (edge.path.size() < 2) continue;
                Color color = getColor(edge.parent.panel);
                GL11.glColor4f(color.getRed() / COLOR_CHANNEL_MAX,
                        color.getGreen() / COLOR_CHANNEL_MAX,
                        color.getBlue() / COLOR_CHANNEL_MAX,
                        Math.min(1f, alphaMult * LINE_ALPHA));
                drawWire(edge, originX, originY, zoom);
            }
        } finally {
            GL11.glPopAttrib();
        }
    }

    private void drawWire(Edge edge, float originX, float originY, float zoom) {
        List<Point> path = edge.drawPath;
        if (path.size() < 2 || zoom <= 0f) return;

        PositionAPI source = edge.parent.panel.getComponentPanel().getPosition();
        PositionAPI target = edge.child.panel.getComponentPanel().getPosition();
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i < path.size(); i++) {
            if (i == 0) {
                float offset = (edge.startY - edge.parent.centerY()) * zoom;
                GL11.glVertex2f(source.getX() + source.getWidth(), source.getCenterY() - offset);
            } else if (i == path.size() - 1) {
                float offset = (edge.endY - edge.child.centerY()) * zoom;
                GL11.glVertex2f(target.getX(), target.getCenterY() - offset);
            } else {
                Point point = path.get(i);
                GL11.glVertex2f(originX + point.x * zoom, originY - point.y * zoom);
            }
        }
        GL11.glEnd();
    }

    private Color getColor(ResearchPanelComponent parent) {
        AoTDMainResearchManager manager = AoTDMainResearchManager.getInstance();
        Color color = Color.WHITE;
        String id = parent.spec.getId();

        if (manager.getManagerForPlayer().haveResearched(id)) color = RESEARCHED_COLOR;
        if (manager.getManagerForPlayer().getCurrentFocus() != null
                && id.equals(manager.getManagerForPlayer().getCurrentFocus().getSpec().getId())) {
            color = Color.CYAN;
        }
        if (manager.getManagerForPlayer().getQueueManager().isInQueue(id)) {
            color = Color.CYAN.darker();
        }
        return color;
    }

    // -------------------------------------------------------------------------
    // Small routing data types
    // -------------------------------------------------------------------------

    private static final class Node {
        final ResearchPanelComponent panel;
        final float x;
        final float y;
        final List<Edge> outgoing = new ArrayList<>();
        final List<Edge> incoming = new ArrayList<>();

        Node(ResearchPanelComponent panel, float x, float y) {
            this.panel = panel;
            this.x = x;
            this.y = y;
        }

        String id() { return panel.spec.getId(); }
        float right() { return x + CARD_WIDTH; }
        float bottom() { return y + CARD_HEIGHT; }
        float centerY() { return y + CARD_HEIGHT * 0.5f; }
    }

    private static final class Edge {
        final Node parent;
        final Node child;
        final List<Point> path = new ArrayList<>();
        final List<Point> drawPath = new ArrayList<>();
        float startY;
        float endY;

        Edge(Node parent, Node child) {
            this.parent = parent;
            this.child = child;
        }
    }

    private static final class Point {
        final float x;
        final float y;

        Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    private static final class Line {
        final Point a;
        final Point b;

        Line(Point a, Point b) {
            this.a = a;
            this.b = b;
        }
    }

    private static final class Candidate {
        final List<Point> points;
        final float score;

        Candidate(List<Point> points, float score) {
            this.points = points;
            this.score = score;
        }
    }

    private static final class Search {
        final int state;
        final float cost;

        Search(int state, float cost) {
            this.state = state;
            this.cost = cost;
        }
    }

    private static final class Grid {
        final float minX;
        final float minY;
        final int cols;
        final int rows;
        final boolean[] blocked;

        Grid(Point from, Point to) {
            float maxX = Math.max(from.x, to.x) + GRID_X_MARGIN;
            float maxY = Math.max(from.y, to.y) + GRID_Y_MARGIN;
            minX = (float) Math.floor((Math.min(from.x, to.x) - GRID_X_MARGIN) / GRID_SIZE)
                    * GRID_SIZE;
            minY = (float) Math.floor((Math.min(from.y, to.y) - GRID_Y_MARGIN) / GRID_SIZE)
                    * GRID_SIZE;
            cols = (int) Math.ceil((maxX - minX) / GRID_SIZE) + 1;
            rows = (int) Math.ceil((maxY - minY) / GRID_SIZE) + 1;
            blocked = cellCount() > MAX_GRID_CELLS ? null : new boolean[cols * rows];
        }

        long cellCount() { return (long) cols * rows; }
        int index(int x, int y) { return y * cols + x; }
        int column(float x) { return Math.round((x - minX) / GRID_SIZE); }
        int row(float y) { return Math.round((y - minY) / GRID_SIZE); }
        boolean inBounds(int x, int y) { return x >= 0 && x < cols && y >= 0 && y < rows; }
        Point point(int x, int y) { return new Point(minX + x * GRID_SIZE, minY + y * GRID_SIZE); }
    }
}
