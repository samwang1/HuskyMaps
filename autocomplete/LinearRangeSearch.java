package autocomplete;
import java.util.ArrayList;
import java.util.List;

public class LinearRangeSearch implements Autocomplete {
    private Term[] terms;


    /**
     * Validates and stores the given array of terms.
     * Assumes that the given array will not be used externally afterwards (and thus may directly
     * store and mutate it).
     * @throws IllegalArgumentException if terms is null or contains null
     */
    public LinearRangeSearch(Term[] terms) {
        if (terms == null) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < terms.length; i++) {
            if (terms[i] == null) {
                throw new IllegalArgumentException();
            }
        }
        this.terms = terms;
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
        for (int i = 0; i < terms.length; i++) {
            String query = terms[i].query();
            if (query.startsWith(prefix)) {
                Long weight = terms[i].weight();
                Term t = new Term(query, weight);
                l.add(t);
            }
        }
        l.sort(TermComparators.byReverseWeightOrder());
        return l.toArray(Term[]::new);
    }
}

