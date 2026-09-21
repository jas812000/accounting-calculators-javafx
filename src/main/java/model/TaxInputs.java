package model;

/**
 * Input parameters for tax calculations.
 *
 * gross is treated as the taxable base in this simplified implementation
 * (i.e., does not include deductions, exemptions, or adjustments).
 */
public record TaxInputs(int year, FilingStatus status, double gross) {
    public TaxInputs {
        if (year <= 0) throw new IllegalArgumentException("year must be > 0");
        if (status == null) throw new IllegalArgumentException("status is required");
        if (gross < 0) throw new IllegalArgumentException("gross must be ≥ 0");
    }
}
