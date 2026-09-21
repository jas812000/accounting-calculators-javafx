package model;

/**
 * One row from an IRS Publication 15-T annual Percentage Method schedule.
 *
 * @param minimum inclusive lower bound
 * @param maximum exclusive upper bound, or null for the highest bracket
 * @param baseWithholding withholding accumulated before this bracket
 * @param rate rate applied to wages exceeding the bracket minimum
 */
public record WithholdingBracket(
        double minimum,
        Double maximum,
        double baseWithholding,
        double rate
) {
    public WithholdingBracket {
        if (minimum < 0 || baseWithholding < 0) {
            throw new IllegalArgumentException(
                    "Bracket amounts must be >= 0"
            );
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
