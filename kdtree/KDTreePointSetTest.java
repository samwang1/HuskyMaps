package kdtree;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KDTreePointSetTest {

    @Test
    public void testConstructor() {
        List<Point> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Point p = new Point(i, i);
            list.add(p);
        }
        PointSet ps = new KDTreePointSet(list);
    }

    @Test
    public void testSimpleNearest() {
        Point p1 = new Point(1.1, 2.2); // Point with x = 1.1, y = 2.2
        Point p2 = new Point(3.3, 4.4);
        Point p3 = new Point(-2.9, 4.2);
        Point p4 = new Point(-5, 4);
        Point p5 = new Point(1, 4.5);
        Point p6 = new Point(2.5, 3.1);
        Point p7 = new Point(6.2, 7.3);
        PointSet ans = new NaivePointSet(List.of(p1, p2, p3, p4, p5, p6, p7));
        PointSet test = new KDTreePointSet(List.of(p1, p2, p3, p4, p5, p6, p7));

        Point naive = ans.nearest(2.5, 3.1);
        Point kd = test.nearest(2.5, 3.1);

        double naiveD = naive.distanceSquaredTo(1, 1);
        double kdD = kd.distanceSquaredTo(1, 1);

        Assert.assertSame(naive, kd);
    }

    @Test
    public void testSpindlyNearest() {
        List<Point> points = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            Point p = new Point(i, i);
            points.add(p);
        }

        PointSet ans = new NaivePointSet(points);
        PointSet test = new KDTreePointSet(points);

        Point naive = ans.nearest(999, 999);
        Point kd = test.nearest(999, 999);

        Assert.assertSame(naive, kd);
    }

    @Test
    public void test3Nearest() {
        Point p1 = new Point(188.3541835438, -730.3409314820);
        Point p2 = new Point(-36.8399405401, -815.8883292022);
        Point p3 = new Point(818.3340334168, 699.3291524571);

        PointSet ans = new NaivePointSet(List.of(p1, p2, p3));
        PointSet test = new KDTreePointSet(List.of(p1, p2, p3));

        Point naive = ans.nearest(188, -730);
        Point kd = test.nearest(188, -730);

        Assert.assertSame(naive, kd);
    }

    @Test
    public void testRandom() {
        List<Point> list = new ArrayList<>();
        Random r = new Random(3456456);
        for (int i = 0; i < 10000; i++) {
            double x = r.nextDouble();
            double y = r.nextDouble();
            list.add(new Point(x, y));
        }
        // list.add(new Point(1, 1));
        // list.add(new Point(3, 4));
        // list.add(new Point(-5, 1));
        // list.add(new Point(2, 7));
        // list.add(new Point(-2, 10));

        PointSet act = new KDTreePointSet(list);
        PointSet exp = new NaivePointSet(list);

        for (int i = 0; i < 10000; i++) {
            double x = r.nextDouble();
            double y = r.nextDouble();

            Assert.assertSame(exp.nearest(x, y), act.nearest(x, y));
        }
    }
}
