package autocomplete;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class LinearRangeSearchTest {
    @Test
    public void testSimpleExample() {
        Term[] moreTerms = new Term[] {
                new Term("hello", 0),
                new Term("world", 0),
                new Term("welcome", 0),
                new Term("to", 0),
                new Term("autocomplete", 0),
                new Term("me", 0)
        };
        LinearRangeSearch lrs = new LinearRangeSearch(moreTerms);
        Term[] expected = new Term[]{new Term("autocomplete", 0)};
        assertTermsEqual(expected, lrs.allMatches("auto"));
    }

    private void assertTermsEqual(Term[] expected, Term[] actual) {
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            Term e = expected[i];
            Term a = actual[i];
            assertEquals(e.query(), a.query());
            assertEquals(e.weight(), a.weight());
        }
    }
}
