package model;

/**
 * Output values from tax computation.
 *
 * Includes:
 * - gross input amount
 * - computed FICA (simplified)
 * - computed federal tax (table-based)
 * - net amount after deductions
 */
public record TaxResult(
        int year,
        FilingStatus status,
        double gross,
        double fica,
        double federal,
        double net
) {
    // Record provides immutable storage; behavior can be added later if needed.
}
