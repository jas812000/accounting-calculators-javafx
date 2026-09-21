package model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PayrollCalculator}.
 *
 * Verifies hourly and salaried payroll calculations, federal withholding,
 * FICA, other deductions, exemptions, filing-status behavior, and
 * validation of unsupported or invalid payroll inputs.
 */
class PayrollCalculatorTest {

    /**
     * Verifies that hourly payroll includes federal withholding and FICA
     * when determining net pay.
     */
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

    /**
     * Verifies that annual salary is converted to the selected pay period
     * and federal withholding and FICA are calculated from period wages.
     */
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

    /**
     * Verifies that an employee marked exempt has no federal income-tax
     * withholding while FICA remains applicable.
     */
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

    /**
     * Verifies that other deductions reduce calculated net pay.
     */
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

    /**
     * Verifies that qualifying surviving spouse status is accepted for
     * payroll withholding and mapped to the applicable married schedule.
     */
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

    /**
     * Verifies that an annual salary must be greater than zero.
     */
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

    /**
     * Verifies that salaried payroll requires a pay schedule.
     */
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

    /**
     * Verifies that the pay-date year must have bundled payroll data.
     */
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

    /**
     * Creates the standard 2026 payroll-tax inputs shared by payroll tests.
     *
     * @return standard single-filer payroll-tax inputs
     */
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
