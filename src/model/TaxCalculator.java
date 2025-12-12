package model;

import TaxTables.TaxTableCalculator2025;

/**
 * Stateless tax calculations.
 *
 * Produces FICA, federal tax, and net amounts from gross pay.
 *
 * Notes / simplifications:
 * - FICA is modeled as a flat rate and does not enforce the SS wage base cap
 *   or additional Medicare thresholds.
 * - Federal tax uses year-specific tax tables; currently implemented for 2025.
 */
public final class TaxCalculator {
    private static final double FICA_RATE = 0.0765; // 6.2% + 1.45% (simplified)

    private TaxCalculator() {}

    /**
     * Computes tax outputs for the provided input.
     *
     * @param in tax inputs (year, status, gross)
     * @return TaxResult including fica, federal, and net
     */
    public static TaxResult calculate(TaxInputs in) {
        double fica    = fica(in.gross());
        double federal = federal(in.year(), in.status(), in.gross());
        double net     = in.gross() - fica - federal;

        return new TaxResult(in.year(), in.status(), in.gross(), fica, federal, net);
    }

    /** Simplified employee FICA. */
    public static double fica(double gross) {
        return gross * FICA_RATE;
    }

    /**
     * Federal tax computation via year-specific tables.
     *
     * @param year tax year
     * @param status filing status
     * @param gross gross income used as taxable base in this simplified model
     * @return computed federal tax, or 0 if the year is unsupported
     */
    public static double federal(int year, FilingStatus status, double gross) {
        if (year == 2025) {
            return new TaxTableCalculator2025(status, gross).calculateTax();
        }
        // Unsupported tax years return 0 to avoid misleading partial calculations.
        return 0.0;
    }
}
