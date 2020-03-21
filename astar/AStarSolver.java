package astar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.princeton.cs.algs4.Stopwatch;

/**
 * @see ShortestPathsSolver for more method documentation
 */
public class AStarSolver<Vertex> implements ShortestPathsSolver<Vertex> {
    private SolverOutcome outcome;
    private double solutionWeight;
    private List<Vertex> solution;
    private double timeSpent;
    private HashMap<Vertex, Double> distTo;
    private HashMap<Vertex, Vertex> edgeTo; // <current vertex, previous vertex>
    private int statesExplored;

    /**
     * Immediately solves and stores the result of running memory optimized A*
     * search, computing everything necessary for all other methods to return
     * their results in constant time. The timeout is given in seconds.
     */
    public AStarSolver(AStarGraph<Vertex> input, Vertex start, Vertex end, double timeout) {
        // initialize variables
        distTo = new HashMap<>();
        edgeTo = new HashMap<>();
        solution = new ArrayList<>();
        statesExplored = 0;
        outcome = SolverOutcome.UNSOLVABLE;

        Stopwatch sw = new Stopwatch();
        TreeMapMinPQ<Vertex> pQ = new TreeMapMinPQ<>();
        pQ.add(start, input.estimatedDistanceToGoal(start, end)); // add starting node
        distTo.put(start, 0.0); // distTo starting node = 0.
        edgeTo.put(start, null); // no edgeTo to starting node.

        while (!pQ.isEmpty()) {
            if (sw.elapsedTime() > timeout) {
                timeSpent = sw.elapsedTime();
                outcome = SolverOutcome.UNSOLVABLE;
                break;
            }
            Vertex v = pQ.removeSmallest();
            List<WeightedEdge<Vertex>> neighborEdges = input.neighbors(v);
            statesExplored++;

            if (v.equals(end)) {
                outcome = SolverOutcome.SOLVED;
                solution = backtrack(edgeTo, end);
                solutionWeight = distTo.get(end);
                timeSpent = sw.elapsedTime();
                break;
            }
            for (WeightedEdge<Vertex> e : neighborEdges) { // neighbors of current vertex v
                double potentialDist = distTo.get(v) + e.weight();
                if (pQ.contains(e.to())) { //distTo.containsKey(e.to())

                    if (potentialDist < distTo.get(e.to())) { // update distTo and PQ if found better dist
                        distTo.put(e.to(), potentialDist);
                        edgeTo.put(e.to(), e.from());
                        pQ.changePriority(e.to(), potentialDist + input.estimatedDistanceToGoal(e.to(), end));
                    }
                } else if (distTo.containsKey(e.to())) { // not in pQ but in distTo

                    if (potentialDist < distTo.get(e.to())) { // update distTo and PQ if found better dist
                        distTo.put(e.to(), potentialDist);
                        edgeTo.put(e.to(), e.from());
                        pQ.add(e.to(), potentialDist + input.estimatedDistanceToGoal(e.to(), end));
                    }
                } else { // doesnt yet exist in pQ (distTo)
                    distTo.put(e.to(), distTo.get(e.from()) + e.weight());
                    edgeTo.put(e.to(), e.from());
                    pQ.add(e.to(), distTo.get(e.to()) + input.estimatedDistanceToGoal(e.to(), end));
                }
            }
        }
    }

    private List<Vertex> backtrack(HashMap<Vertex, Vertex> edges, Vertex end) {
        List<Vertex> ans = new ArrayList<>();
        Vertex cur = end;
        while (cur != null) { // keep going while cur hasn't hit source node
            ans.add(0, cur);
            cur = edges.get(cur); // predecessor of cur
        }
        return ans;
    }

    @Override
    public SolverOutcome outcome() {
        return outcome;
    }

    @Override
    public List<Vertex> solution() {
        return solution;
    }

    @Override
    public double solutionWeight() {
        return solutionWeight;
    }

    /** The total number of priority queue removeSmallest operations. */
    @Override
    public int numStatesExplored() {
        return statesExplored;
    }

    @Override
    public double explorationTime() {
        return timeSpent;
    }
}
