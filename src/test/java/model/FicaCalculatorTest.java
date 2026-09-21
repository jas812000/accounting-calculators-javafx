package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link FicaCalculator}.
 *
 * Verifies Social Security, Medicare, and Additional Medicare withholding,
 * including wage-base and threshold boundaries and invalid input handling.
 */
class FicaCalculatorTest {

    /**
     * Verifies normal 2026 employee FICA withholding below all wage thresholds.
     */
    @Test
    void calculatesNormal2026Fica() {
        FicaResult result =
                FicaCalculator.calculate(
                        2_000,
                        0,
                        2026
                );

        assertEquals(124, result.socialSecurity(), 0.001);
        assertEquals(29, result.medicare(), 0.001);
        assertEquals(0, result.additionalMedicare(), 0.001);
        assertEquals(153, result.total(), 0.001);
    }

    /**
     * Verifies that Social Security withholding applies only to wages
     * remaining below the annual wage base.
     */
    @Test
    void socialSecurityStopsAt2026WageBase() {
        FicaResult result =
                FicaCalculator.calculate(
                        2_000,
                        184_000,
                        2026
                );

        assertEquals(31, result.socialSecurity(), 0.001);
        assertEquals(29, result.medicare(), 0.001);
    }

    /**
     * Verifies that no Social Security tax is withheld after the annual
     * wage base has already been reached.
     */
    @Test
    void noSocialSecurityAfterWageBaseReached() {
        FicaResult result =
                FicaCalculator.calculate(
                        2_000,
                        184_500,
                        2026
                );

        assertEquals(0, result.socialSecurity(), 0.001);
        assertEquals(29, result.medicare(), 0.001);
    }

    /**
     * Verifies that regular Medicare withholding has no annual wage-base cap.
     */
    @Test
    void medicareHasNoWageBaseLimit() {
        FicaResult result =
                FicaCalculator.calculate(
                        10_000,
                        300_000,
                        2026
                );

        assertEquals(145, result.medicare(), 0.001);
    }

    /**
     * Verifies Additional Medicare withholding when a paycheck crosses
     * the applicable year-to-date wage threshold.
     */
    @Test
    void additionalMedicareStartsWhenPaycheckCrossesThreshold() {
        FicaResult result =
                FicaCalculator.calculate(
                        2_000,
                        199_000,
                        2026
                );

        assertEquals(9, result.additionalMedicare(), 0.001);
    }

    /**
     * Verifies that the entire current paycheck amount above the threshold
     * is subject to Additional Medicare withholding once prior wages have
     * already reached that threshold.
     */
    @Test
    void entirePaycheckGetsAdditionalMedicareAfterThreshold() {
        FicaResult result =
                FicaCalculator.calculate(
                        2_000,
                        200_000,
                        2026
                );

        assertEquals(18, result.additionalMedicare(), 0.001);
    }

    /**
     * Verifies that FICA calculation rejects years without bundled payroll-tax data.
     */
    @Test
    void rejectsUnsupportedFutureYear() {
        assertThrows(
                IllegalArgumentException.class,
                () -> FicaCalculator.calculate(
                        2_000,
                        0,
                        2027
                )
        );
    }

    /**
     * Verifies that current-period and year-to-date wages cannot be negative.
     */
    @Test
    void rejectsNegativeWages() {
        assertThrows(
                IllegalArgumentException.class,
                () -> FicaCalculator.calculate(
                        -1,
                        0,
                        2026
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> FicaCalculator.calculate(
                        1,
                        -1,
                        2026
                )
        );
    }
}
