package huskymaps;

import astar.AStarGraph;
import astar.WeightedEdge;
import autocomplete.BinaryRangeSearch;
import autocomplete.Term;
import kdtree.KDTreePointSet;
//import kdtree.NaivePointSet;
import kdtree.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
//import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static huskymaps.utils.Spatial.greatCircleDistance;
import static huskymaps.utils.Spatial.projectToX;
import static huskymaps.utils.Spatial.projectToY;

public class StreetMapGraph implements AStarGraph<Long> {
    private Map<Long, Node> nodes = new HashMap<>();
    private Map<Long, Set<WeightedEdge<Long>>> neighbors = new HashMap<>();
    private KDTreePointSet kd;

    private BinaryRangeSearch brs;
    private Map<String, List<Long>> wordToID;
    private Map<Point, Long> pointToID;

    public StreetMapGraph(String filename) {
        OSMGraphHandler.initializeFromXML(this, filename); // adds nodes to nodes map
        //System.out.println(nodes);
        List<Point> pts = new ArrayList<>();
        pointToID = new HashMap<>();
        for (Long l : nodes.keySet()) {
            Node n = nodes.get(l);
            if (isNavigable(n)) { // vertices that has neighbors
                double lon = lon(l);
                double lat = lat(l);
                Point p = new Point(projectToX(lon, lat), projectToY(lon, lat));
                pts.add(p);
                pointToID.put(p, n.id);
            }
        }
        //System.out.println(pts); // debug
        kd = new KDTreePointSet(pts);

        // autocomplete
        List<Term> t = new ArrayList<>();
        wordToID = new HashMap<>();
        // make Term[] to put into BRS
        for (long l : nodes.keySet()) {
            Node n = nodes.get(l);

            String query = n.name();
            long weight = nodes.get(l).importance;

            if (query != null && weight > 0) {
                //System.out.println(query + " " + weight);
                Term term = new Term(query, weight);
                t.add(term);

                if (wordToID.containsKey(query)) { // if already exists, e.g. "Starbucks"
                    wordToID.get(query).add(n.id);
                    //System.out.println(query + " ADDED " + wordToID.get(query));
                } else { // word doesn't yet exist
                    List ids = new ArrayList<>();
                    ids.add(n.id);
                    wordToID.put(query, ids);
                    //System.out.println(query + " ADDED " + ids);
                }

            }
        }
        Term[] terms = t.toArray(Term[]::new);
        brs = new BinaryRangeSearch(terms);
    }

    /**
     * Returns the vertex closest to the given longitude and latitude.
     * @param lat The target latitude.
     * @param lon The target longitude.
     * @return The id of the node in the graph closest to the target.
     */
    public long closest(double lat, double lon) {
        double x = projectToX(lon, lat);
        double y = projectToY(lon, lat);
        Point query = new Point(x, y);

        System.out.println(x + " " + y);
        System.out.println(kd.nearest(x, y));
        System.out.println(getNode(pointToID.get(kd.nearest(x, y))));

        //System.out.println(nodes.get(pointToID.get(query)));

        // Use x and y, not lon and lat, when working with Point instances

        Point nearest = kd.nearest(x, y);
        return pointToID.get(nearest);
    }

    /**
     * In linear time, collect all the names of OSM locations that prefix-match the query string.
     * @param prefix Prefix string to be searched for. Could be any case, with our without
     *               punctuation.
     * @return A <code>List</code> of full names of locations matching the <code>prefix</code>.
     */
    public List<String> getLocationsByPrefix(String prefix) {
        // matching autocomplete results
        Term[] matches = brs.allMatches(prefix);
        Set<String> uniques = new HashSet<>();

        // return matches as Strings
        for (Term term : matches) {
            String query = term.query();
            //System.out.println(query);
            uniques.add(query);
        }

        List<String> answer = new ArrayList<>(uniques);
        return answer;
    }

    /**
     * Collect all locations that match a cleaned <code>locationName</code>, and return
     * information about each node that matches.
     * @param locationName A full name of a location searched for.
     * @return A list of locations whose name matches the <code>locationName</code>.
     */
    public List<Node> getLocations(String locationName) {
        List<Node> ans = new ArrayList<>();
        List<String> matches = getLocationsByPrefix(locationName);
        for (String s : matches) {
            //System.out.println(s);
            if (s.equals(locationName)) { // makes sure it's a full word
                List<Long> ids = wordToID.get(locationName);
                for (int i = 0; i < ids.size(); i++) { // get all instances. e.g. all "Starbucks"
                    Long id = ids.get(i);
                    ans.add(nodes.get(id));
                }
            }
        }
        return ans;
    }

    /** Returns a list of outgoing edges for V. Assumes V exists in this graph. */
    @Override
    public List<WeightedEdge<Long>> neighbors(Long v) {
        return new ArrayList<>(neighbors.get(v));
    }

    /**
     * Returns the great-circle distance between S and GOAL. Assumes
     * S and GOAL exist in this graph.
     */
    @Override
    public double estimatedDistanceToGoal(Long s, Long goal) {
        Node sNode = nodes.get(s);
        Node goalNode = nodes.get(goal);
        return greatCircleDistance(sNode.lon(), goalNode.lon(), sNode.lat(), goalNode.lat());
    }

    /** Returns a set of my vertices. Altering this set does not alter this graph. */
    public Set<Long> vertices() {
        return new HashSet<>(nodes.keySet());
    }

    /** Adds an edge to this graph if it doesn't already exist, using distance as the weight. */
    public void addWeightedEdge(long from, long to, String name) {
        if (nodes.containsKey(from) && nodes.containsKey(to)) {
            Node fromNode = nodes.get(from);
            Node toNode = nodes.get(to);
            double weight = greatCircleDistance(fromNode.lon(), toNode.lon(), fromNode.lat(), toNode.lat());
            neighbors.get(from).add(new WeightedEdge<>(from, to, weight, name));
        }
    }

    /** Adds an edge to this graph if it doesn't already exist. */
    public void addWeightedEdge(long from, long to, double weight, String name) {
        if (nodes.containsKey(from) && nodes.containsKey(to)) {
            neighbors.get(from).add(new WeightedEdge<>(from, to, weight, name));
        }
    }

    /** Adds an edge to this graph if it doesn't already exist. */
    public void addWeightedEdge(WeightedEdge<Long> edge) {
        if (nodes.containsKey(edge.from()) && nodes.containsKey(edge.to())) {
            neighbors.get(edge.from()).add(edge);
        }
    }

    /** Checks if a vertex has 0 out-degree from graph. */
    private boolean isNavigable(Node node) {
        return !neighbors.get(node.id()).isEmpty();
    }

    /**
     * Gets the latitude of a vertex.
     * @param v The id of the vertex.
     * @return The latitude of the vertex.
     */
    public double lat(long v) {
        if (!nodes.containsKey(v)) {
            return 0.0;
        }
        return nodes.get(v).lat();
    }

    /**
     * Gets the longitude of a vertex.
     * @param v The id of the vertex.
     * @return The longitude of the vertex.
     */
    public double lon(long v) {
        if (!nodes.containsKey(v)) {
            return 0.0;
        }
        return nodes.get(v).lon();
    }

    /** Adds a node to this graph, if it doesn't yet exist. */
    void addNode(Node node) {
        if (!nodes.containsKey(node.id())) {
            nodes.put(node.id(), node);
            neighbors.put(node.id(), new HashSet<>());
        }
    }

    Node getNode(long id) {
        return nodes.get(id);
    }

    Node.Builder nodeBuilder() {
        return new Node.Builder();
    }
}
