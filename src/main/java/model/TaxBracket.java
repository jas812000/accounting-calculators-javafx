package model;

/**
 * Represents one progressive federal income tax bracket.
 *
 * @param minimum inclusive lower bound for the bracket
 * @param maximum exclusive upper bound, or null for the highest bracket
 * @param rate tax rate applied to income within the bracket
 */
public record TaxBracket(
        double minimum,
        Double maximum,
        double rate
) {
    public TaxBracket {
        if (minimum < 0) {
            throw new IllegalArgumentException("minimum must be >= 0");
        }

        if (maximum != null && maximum <= minimum) {
            throw new IllegalArgumentException(
                    "maximum must be greater than minimum"
            );
        }

        if (rate < 0 || rate > 1) {
            throw new IllegalArgumentException(
                    "rate must be between 0 and 1"
            );
        }
    }
}
