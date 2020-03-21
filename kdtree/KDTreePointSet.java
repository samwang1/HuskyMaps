package kdtree;

import java.util.Iterator;
import java.util.List;

public class KDTreePointSet implements PointSet {
    private KDNode overallRoot;

    /**
     * Instantiates a new KDTree with the given points.
     * @param points a non-null, non-empty list of points to include
     *               (makes a defensive copy of points, so changes to the list
     *               after construction don't affect the point set)
     */
    public KDTreePointSet(List<Point> points) {
        Iterator<Point> itr = points.iterator();
        while (itr.hasNext()) {
            overallRoot = treeBuilder(overallRoot, itr.next(), 0);
        }
    }

    private KDNode treeBuilder(KDNode root, Point p, int level) { //took out curLevel param
        if (root == null) { // base case
            return new KDNode(null, null, p);
        } else { // need to compare
            if (level % 2 == 0) { // check X's
                double nodeX = root.getX();
                double myX = p.x();
                if (myX > nodeX) {
                    root.right = treeBuilder(root.right, p, level + 1);
                } else {
                    root.left = treeBuilder(root.left, p, level + 1);
                }
            } else { // level % 2 == 1
                double nodeY = root.getY();
                double myY = p.y();
                if (myY > nodeY) {
                    root.right = treeBuilder(root.right, p, level + 1);
                } else {
                    root.left = treeBuilder(root.left, p, level + 1);
                }
            }
            return root;
        }
    }

    /**
     * Returns the point in this set closest to (x, y) in (usually) O(log N) time,
     * where N is the number of points in this set.
     */
    @Override
    public Point nearest(double x, double y) {
        Point query = new Point(x, y);
        //double bestDistance = overallRoot.point.distanceSquaredTo(x, y);

        KDNode ans = nearestHelper(overallRoot, query, overallRoot, 0);
        return ans.point;
    }

    private KDNode nearestHelper(KDNode root, Point query, KDNode globalBest, int level) {
        if (root != null) {
            // current node closer to query than globalBest (i)
            if (root.point.distanceSquaredTo(query) < globalBest.point.distanceSquaredTo(query)) {
                globalBest = root;
            }

            double rootX = root.getX();
            double rootY = root.getY();

            // go down good right/left branches
            if (level % 2 == 0 && query.x() > rootX || level % 2 == 1 && query.y() > rootY) {
                globalBest = nearestHelper(root.right, query, globalBest, level + 1);
            } else if (level % 2 == 0 && query.x() <= rootX || level % 2 == 1 && query.y() <= rootY) {
                globalBest = nearestHelper(root.left, query, globalBest, level + 1);
            }

            // CHECKING BAD BRANCH
            if (level % 2 == 0) {
                if (Math.pow((query.x() - rootX), 2) < globalBest.point.distanceSquaredTo(query)) {
                    if (query.x() > rootX) {
                        globalBest = nearestHelper(root.left, query, globalBest, level + 1);
                    } else { // query.x() <= rootX
                        globalBest = nearestHelper(root.right, query, globalBest, level + 1);
                    }
                }
            } else { // level % 2 == 1
                if (Math.pow((query.y() - rootY), 2) < globalBest.point.distanceSquaredTo(query)) {
                    if (query.y() > rootY) {
                        globalBest = nearestHelper(root.left, query, globalBest, level + 1);
                    } else { // query.y() <= rootY
                        globalBest = nearestHelper(root.right, query, globalBest, level + 1);
                    }
                }
            }
        }
        return globalBest;
    }

    private class KDNode {
        private Point point;
        private KDNode left;
        private KDNode right;

        KDNode(KDNode left, KDNode right, Point p) {
            this.left = left;
            this.right = right;
            this.point = p;
        }

        public double getX() {
            return point.x();
        }

        public double getY() {
            return point.y();
        }
    }
}
