package model;

/**
 * Input parameters for an annual federal income tax calculation.
 *
 * @param year tax year
 * @param status filing status
 * @param taxableIncome taxable income to which the progressive tax schedule applies
 */
public record TaxInputs(
        int year,
        FilingStatus status,
        double taxableIncome
) {
    public TaxInputs {
        if (year <= 0) {
            throw new IllegalArgumentException("year must be > 0");
        }

        if (status == null) {
            throw new IllegalArgumentException("status is required");
        }

        if (taxableIncome < 0) {
            throw new IllegalArgumentException(
                    "taxableIncome must be >= 0"
            );
        }
    }
}
