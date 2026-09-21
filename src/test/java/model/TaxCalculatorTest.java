package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link TaxCalculator}.
 *
 * Verifies progressive federal income-tax calculations across brackets,
 * filing statuses and tax years, along with input validation and result data.
 */
class TaxCalculatorTest {

    private static final double DELTA = 0.01;

    /**
     * Verifies that zero taxable income produces zero federal income tax.
     */
    @Test
    void calculatesZeroTaxForZeroIncome() {
        assertEquals(
                0.0,
                TaxCalculator.federal(
                        2025,
                        FilingStatus.SINGLE,
                        0.0
                ),
                DELTA
        );
    }

    /**
     * Verifies tax calculation for income entirely within the first bracket.
     */
    @Test
    void calculatesTaxWithinFirstBracket() {
        assertEquals(
                1000.0,
                TaxCalculator.federal(
                        2025,
                        FilingStatus.SINGLE,
                        10_000.0
                ),
                DELTA
        );
    }

    /**
     * Verifies tax calculation exactly at the first-bracket boundary.
     */
    @Test
    void calculatesTaxAtFirstBracketBoundary() {
        assertEquals(
                1192.50,
                TaxCalculator.federal(
                        2025,
                        FilingStatus.SINGLE,
                        11_925.0
                ),
                DELTA
        );
    }

    /**
     * Verifies progressive taxation when taxable income spans multiple brackets.
     */
    @Test
    void calculatesProgressiveTaxAcrossMultipleBrackets() {
        double expected =
                (11_925.0 * 0.10)
                        + ((48_475.0 - 11_925.0) * 0.12)
                        + ((60_000.0 - 48_475.0) * 0.22);

        assertEquals(
                expected,
                TaxCalculator.federal(
                        2025,
                        FilingStatus.SINGLE,
                        60_000.0
                ),
                DELTA
        );
    }

    /**
     * Verifies that qualifying surviving spouse status uses the
     * married-filing-jointly tax schedule.
     */
    @Test
    void qualifyingSurvivingSpouseUsesMarriedFilingJointlySchedule() {
        double taxableIncome = 100_000.0;

        double survivingSpouseTax = TaxCalculator.federal(
                2025,
                FilingStatus.QUALIFYING_SURVIVING_SPOUSE,
                taxableIncome
        );

        double marriedJointlyTax = TaxCalculator.federal(
                2025,
                FilingStatus.MARRIED_FILING_JOINTLY,
                taxableIncome
        );

        assertEquals(
                marriedJointlyTax,
                survivingSpouseTax,
                DELTA
        );
    }

    /**
     * Verifies that estates and trusts use the 37 percent top bracket
     * when taxable income exceeds the applicable threshold.
     */
    @Test
    void estatesAndTrustsUseTopThirtySevenPercentBracket() {
        double expected =
                (3_150.0 * 0.10)
                        + ((11_450.0 - 3_150.0) * 0.24)
                        + ((15_650.0 - 11_450.0) * 0.35)
                        + ((20_000.0 - 15_650.0) * 0.37);

        assertEquals(
                expected,
                TaxCalculator.federal(
                        2025,
                        FilingStatus.ESTATES_AND_TRUSTS,
                        20_000.0
                ),
                DELTA
        );
    }

    /**
     * Verifies that the calculator uses the bracket data for the requested
     * tax year rather than a single hard-coded schedule.
     */
    @Test
    void calculatesUsingDifferentTaxYears() {
        double income = 50_000.0;

        double tax2020 = TaxCalculator.federal(
                2020,
                FilingStatus.SINGLE,
                income
        );

        double tax2026 = TaxCalculator.federal(
                2026,
                FilingStatus.SINGLE,
                income
        );

        double expected2020 =
                (9_875.0 * 0.10)
                        + ((40_125.0 - 9_875.0) * 0.12)
                        + ((50_000.0 - 40_125.0) * 0.22);

        double expected2026 =
                (12_400.0 * 0.10)
                        + ((50_000.0 - 12_400.0) * 0.12);

        assertEquals(expected2020, tax2020, DELTA);
        assertEquals(expected2026, tax2026, DELTA);
    }

    /**
     * Verifies that the high-level calculation returns the expected
     * tax year, filing status, taxable income, and federal tax.
     */
    @Test
    void calculateReturnsExpectedResultData() {
        TaxInputs inputs = new TaxInputs(
                2025,
                FilingStatus.SINGLE,
                10_000.0
        );

        TaxResult result = TaxCalculator.calculate(inputs);

        assertEquals(2025, result.year());
        assertEquals(FilingStatus.SINGLE, result.status());
        assertEquals(10_000.0, result.taxableIncome(), DELTA);
        assertEquals(1_000.0, result.federalTax(), DELTA);
    }

    /**
     * Verifies that negative taxable income is rejected.
     */
    @Test
    void rejectsNegativeTaxableIncome() {
        assertThrows(
                IllegalArgumentException.class,
                () -> TaxCalculator.federal(
                        2025,
                        FilingStatus.SINGLE,
                        -1.0
                )
        );
    }

    /**
     * Verifies that a filing status is required.
     */
    @Test
    void rejectsNullFilingStatus() {
        assertThrows(
                IllegalArgumentException.class,
                () -> TaxCalculator.federal(
                        2025,
                        null,
                        10_000.0
                )
        );
    }

    /**
     * Verifies that tax calculations reject years without bundled tax data.
     */
    @Test
    void rejectsUnsupportedTaxYear() {
        assertThrows(
                IllegalArgumentException.class,
                () -> TaxCalculator.federal(
                        2019,
                        FilingStatus.SINGLE,
                        10_000.0
                )
        );
    }

    /**
     * Verifies that the high-level calculator rejects a null input object.
     */
    @Test
    void rejectsNullInputs() {
        assertThrows(
                IllegalArgumentException.class,
                () -> TaxCalculator.calculate(null)
        );
    }
}
