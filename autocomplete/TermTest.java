package autocomplete;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TermTest {
    @Test
    public void testSimpleCompareTo() {
        Term a = new Term("autocomplete", 0);
        Term b = new Term("me", 0);
        assertTrue(a.compareTo(b) < 0); // "autocomplete" < "me"
    }

    @Test
    public void testCompareToReverse() {
        Term first = new Term("yes", 10);
        Term second = new Term("no", 5);
        assertTrue(first.compareToByReverseWeightOrder(second) < 0); //first < second
        assertFalse(second.compareToByReverseWeightOrder(first) < 0);
    }

    @Test
    public void testCompareToPrefix() {
        Term a = new Term("autocomplete", 0);
        Term b = new Term("me", 0);
        assertTrue(a.compareToByPrefixOrder(b, 4) < 0);

    }
}
