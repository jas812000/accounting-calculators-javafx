package model;

/**
 * Result of an annual federal income tax calculation.
 *
 * @param year tax year
 * @param status filing status
 * @param taxableIncome taxable income used for the calculation
 * @param federalTax calculated federal income tax
 */
public record TaxResult(
        int year,
        FilingStatus status,
        double taxableIncome,
        double federalTax
) {
}
