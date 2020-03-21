package autocomplete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class  BinaryRangeSearch implements Autocomplete {
    private Term[] terms;

    /**
     * Validates and stores the given array of terms.
     * Assumes that the given array will not be used externally afterwards (and thus may directly
     * store and mutate it).
     * @throws IllegalArgumentException if terms is null or contains null
     */
    public BinaryRangeSearch(Term[] terms) {
        if (terms == null) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < terms.length; i++) {
            if (terms[i] == null) {
                throw new IllegalArgumentException();
            }
        }

        //sort terms
        this.terms = terms;
        Arrays.sort(this.terms); // 1. sort in lexicographic order
    }

    /**
     * Returns all terms that start with the given prefix, in descending order of weight.
     * @throws IllegalArgumentException if prefix is null
     */
    public Term[] allMatches(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException();
        }

        List<Term> l = new ArrayList<>();

        int firstOcc = bSearch(prefix, true);
        int lastOcc = bSearch(prefix, false);

        if (firstOcc < 0 || lastOcc < 0) { // if no words contain prefix, return empty Term[]
            return new Term[0];
        }

        for (int i = firstOcc; i <= lastOcc; i++) {
            l.add(terms[i]);
        }

        l.sort(TermComparators.byReverseWeightOrder());
        return l.toArray(Term[]::new);
    }

    private int bSearch(String prefix, boolean first) { // inspiration from https://www.youtube.com/watch?v=OE7wUUpJw6I
        int low = 0;
        int high = terms.length - 1;
        int result = -1;

        while (low <= high) {
            int mid = (low + high) / 2;
            String word = terms[mid].query();
            if (word.startsWith(prefix)) {
                result = mid;
                if (first) { // looking for first occurrence
                    high = mid - 1;
                } else { // looking for last occurrence
                    low = mid + 1;
                }
            } else if (prefix.compareTo(word) < 0) { // prefix comes before word
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return result;
    }
}
