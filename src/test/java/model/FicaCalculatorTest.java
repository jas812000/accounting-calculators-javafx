package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FicaCalculatorTest {

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
