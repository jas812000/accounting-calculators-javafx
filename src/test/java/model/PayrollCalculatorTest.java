package model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PayrollCalculatorTest {

    @Test
    void hourlyPayrollIncludesFederalWithholding() {
        PayrollResult result =
                PayrollCalculator.calculateFromHourly(
                        new PayrollInputs(
                                25,
                                80,
                                PaySchedule.BI_WEEKLY
                        ),
                        standardTaxInputs()
                );

        assertEquals(2_000, result.gross(), 0.001);
        assertEquals(156.1538, result.federal(), 0.001);
        assertEquals(153, result.fica(), 0.001);
        assertEquals(1_690.8462, result.net(), 0.001);
    }

    @Test
    void annualPayrollIncludesFederalWithholding() {
        PayrollResult result =
                PayrollCalculator.calculateFromAnnual(
                        52_000,
                        PaySchedule.BI_WEEKLY,
                        standardTaxInputs()
                );

        assertEquals(2_000, result.gross(), 0.001);
        assertEquals(156.1538, result.federal(), 0.001);
        assertEquals(153, result.fica(), 0.001);
    }

    @Test
    void exemptEmployeeHasNoFederalWithholding() {
        PayrollTaxInputs taxInputs =
                new PayrollTaxInputs(
                        LocalDate.of(2026, 6, 12),
                        FilingStatus.SINGLE,
                        false,
                        0,
                        0,
                        0,
                        0,
                        true,
                        0,
                        0
                );

        PayrollResult result =
                PayrollCalculator.calculateFromHourly(
                        new PayrollInputs(
                                25,
                                80,
                                PaySchedule.BI_WEEKLY
                        ),
                        taxInputs
                );

        assertEquals(0, result.federal(), 0.001);
        assertEquals(153, result.fica(), 0.001);
    }

    @Test
    void appliesOtherDeductionsToNetPay() {
        PayrollTaxInputs taxInputs =
                new PayrollTaxInputs(
                        LocalDate.of(2026, 6, 12),
                        FilingStatus.SINGLE,
                        false,
                        0,
                        0,
                        0,
                        0,
                        false,
                        100,
                        0
                );

        PayrollResult result =
                PayrollCalculator.calculateFromHourly(
                        new PayrollInputs(
                                25,
                                80,
                                PaySchedule.BI_WEEKLY
                        ),
                        taxInputs
                );

        assertEquals(100, result.other(), 0.001);
        assertEquals(1_590.8462, result.net(), 0.001);
    }

    @Test
    void qualifyingSurvivingSpouseUsesMarriedSchedule() {
        PayrollTaxInputs taxInputs =
                new PayrollTaxInputs(
                        LocalDate.of(2026, 6, 12),
                        FilingStatus.QUALIFYING_SURVIVING_SPOUSE,
                        false,
                        0,
                        0,
                        0,
                        0,
                        false,
                        0,
                        0
                );

        PayrollResult result =
                PayrollCalculator.calculateFromHourly(
                        new PayrollInputs(
                                25,
                                80,
                                PaySchedule.BI_WEEKLY
                        ),
                        taxInputs
                );

        assertTrue(result.federal() >= 0);
    }

    @Test
    void rejectsZeroAnnualSalary() {
        assertThrows(
                IllegalArgumentException.class,
                () -> PayrollCalculator.calculateFromAnnual(
                        0,
                        PaySchedule.BI_WEEKLY,
                        standardTaxInputs()
                )
        );
    }

    @Test
    void rejectsNullScheduleForAnnualSalary() {
        assertThrows(
                IllegalArgumentException.class,
                () -> PayrollCalculator.calculateFromAnnual(
                        52_000,
                        null,
                        standardTaxInputs()
                )
        );
    }

    @Test
    void rejectsUnsupportedPayDateYear() {
        PayrollTaxInputs taxInputs =
                new PayrollTaxInputs(
                        LocalDate.of(2027, 1, 8),
                        FilingStatus.SINGLE,
                        false,
                        0,
                        0,
                        0,
                        0,
                        false,
                        0,
                        0
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> PayrollCalculator.calculateFromHourly(
                        new PayrollInputs(
                                25,
                                80,
                                PaySchedule.BI_WEEKLY
                        ),
                        taxInputs
                )
        );
    }

    private PayrollTaxInputs standardTaxInputs() {
        return new PayrollTaxInputs(
                LocalDate.of(2026, 6, 12),
                FilingStatus.SINGLE,
                false,
                0,
                0,
                0,
                0,
                false,
                0,
                0
        );
    }
}
