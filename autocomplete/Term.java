package autocomplete;

public class Term implements Comparable<Term> {
    private String query;
    private long weight;

    /**
     * Initializes a term with the given query string and weight.
     * @throws IllegalArgumentException if query is null or weight is negative
     */
    public Term(String query, long weight) {
        if (query == null || weight < 0) {
            throw new IllegalArgumentException();
        }
        this.query = query;
        this.weight = weight;
    }

    /**
     * Compares the two terms in lexicographic order by query.
     * @throws NullPointerException if the specified object is null
     */
    public int compareTo(Term that) {
        if (that == null) {
            throw new NullPointerException();
        }
        return this.query.compareTo(that.query);
    }

    /** Compares to another term, in descending order by weight. */
    public int compareToByReverseWeightOrder(Term that) {
        if (this.weight < that.weight) { // this comes after that
            return 1;
        } else if (this.weight == that.weight){  // this and that are equal weight
            return 0;
        } else { // this comes before that
            return -1;
        }
    }

    /**
     * Compares to another term in lexicographic order, but using only the first r characters
     * of each query. If r is greater than the length of any term's query, compares using the
     * term's full query.
     * @throws IllegalArgumentException if r < 0
     */
    public int compareToByPrefixOrder(Term that, int r) {
        if (r < 0) {
            throw new IllegalArgumentException();
        }
        return this.queryPrefix(r).compareTo(that.queryPrefix(r));
    }

    /** Returns this term's query. */
    public String query() {
        return query;
    }

    @Override
    public String toString() {
        return "Term{" +
                "query='" + query + '\'' +
                ", weight=" + weight +
                '}';
    }

    /**
     * Returns the first r characters of this query.
     * If r is greater than the length of the query, returns the entire query.
     * @throws IllegalArgumentException if r < 0
     */
    public String queryPrefix(int r) {
        if (r < 0) {
            throw new IllegalArgumentException();
        }
        if (r > query.length()) {
            return query;
        }
        return query.substring(0, r);
    }

    /** Returns this term's weight. */
    public long weight() {
        return weight;
    }
}
