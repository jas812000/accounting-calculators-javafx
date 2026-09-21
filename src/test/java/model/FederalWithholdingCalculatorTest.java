package model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link FederalWithholdingCalculator}.
 *
 * Verifies pay-date year selection, standard and Step 2 withholding
 * schedules, Form W-4 adjustments and credits, additional withholding,
 * and invalid payroll-withholding inputs.
 */
class FederalWithholdingCalculatorTest {

    private final FederalWithholdingCalculator calculator =
            new FederalWithholdingCalculator();

    /**
     * Verifies that the paycheck date determines the withholding year.
     */
    @Test
    void payDateDeterminesWithholdingYear() {
        PayrollWithholdingInputs inputs = inputs(
                LocalDate.of(2026, 12, 31),
                FilingStatus.SINGLE,
                false,
                2_000
        );

        PayrollWithholdingResult result =
                calculator.calculate(inputs);

        assertEquals(2026, result.taxYear());
    }

    /**
     * Verifies that a future pay date does not silently fall back to
     * the most recent bundled withholding year.
     */
    @Test
    void futurePayDateDoesNotFallBackTo2026() {
        PayrollWithholdingInputs inputs = inputs(
                LocalDate.of(2027, 1, 8),
                FilingStatus.SINGLE,
                false,
                2_000
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculate(inputs)
        );
    }

    /**
     * Verifies standard single-filer biweekly withholding for 2026.
     */
    @Test
    void calculatesStandardSingleBiweeklyWithholding() {
        PayrollWithholdingInputs inputs = inputs(
                LocalDate.of(2026, 6, 12),
                FilingStatus.SINGLE,
                false,
                2_000
        );

        PayrollWithholdingResult result =
                calculator.calculate(inputs);

        assertEquals(43_400, result.adjustedAnnualWages(), 0.001);
        assertEquals(4_060, result.tentativeAnnualWithholding(), 0.001);
        assertEquals(156.1538, result.federalWithholding(), 0.001);
    }

    /**
     * Verifies that Form W-4 Step 2 uses the Step 2 withholding schedule
     * without the standard withholding adjustment.
     */
    @Test
    void step2CheckboxUsesStep2ScheduleAndNoStandardAdjustment() {
        PayrollWithholdingInputs inputs =
                new PayrollWithholdingInputs(
                        LocalDate.of(2026, 6, 12),
                        FilingStatus.SINGLE,
                        true,
                        2_000,
                        PaySchedule.BI_WEEKLY,
                        0,
                        0,
                        0,
                        0
                );

        PayrollWithholdingResult result =
                calculator.calculate(inputs);

        assertEquals(52_000, result.adjustedAnnualWages(), 0.001);
        assertEquals(7_025, result.tentativeAnnualWithholding(), 0.001);
        assertEquals(270.1923, result.federalWithholding(), 0.001);
    }

    /**
     * Verifies application of Form W-4 Step 4(a) other income and
     * Step 4(b) deductions when determining adjusted annual wages.
     */
    @Test
    void appliesFormW4OtherIncomeAndDeductions() {
        PayrollWithholdingInputs inputs =
                new PayrollWithholdingInputs(
                        LocalDate.of(2026, 6, 12),
                        FilingStatus.SINGLE,
                        false,
                        2_000,
                        PaySchedule.BI_WEEKLY,
                        0,
                        5_000,
                        2_000,
                        0
                );

        PayrollWithholdingResult result =
                calculator.calculate(inputs);

        assertEquals(46_400, result.adjustedAnnualWages(), 0.001);
    }

    /**
     * Verifies that Form W-4 Step 3 credits reduce withholding on a
     * per-pay-period basis.
     */
    @Test
    void appliesStep3CreditsPerPayPeriod() {
        PayrollWithholdingInputs inputs =
                new PayrollWithholdingInputs(
                        LocalDate.of(2026, 6, 12),
                        FilingStatus.SINGLE,
                        false,
                        2_000,
                        PaySchedule.BI_WEEKLY,
                        2_600,
                        0,
                        0,
                        0
                );

        PayrollWithholdingResult result =
                calculator.calculate(inputs);

        assertEquals(56.1538, result.federalWithholding(), 0.001);
    }

    /**
     * Verifies that Form W-4 Step 4(c) additional withholding is added
     * to the calculated per-pay-period withholding.
     */
    @Test
    void appliesStep4cAdditionalWithholding() {
        PayrollWithholdingInputs inputs =
                new PayrollWithholdingInputs(
                        LocalDate.of(2026, 6, 12),
                        FilingStatus.SINGLE,
                        false,
                        2_000,
                        PaySchedule.BI_WEEKLY,
                        0,
                        0,
                        0,
                        50
                );

        PayrollWithholdingResult result =
                calculator.calculate(inputs);

        assertEquals(206.1538, result.federalWithholding(), 0.001);
    }

    /**
     * Verifies that Step 3 credits cannot reduce federal withholding below zero.
     */
    @Test
    void creditsCannotReduceWithholdingBelowZero() {
        PayrollWithholdingInputs inputs =
                new PayrollWithholdingInputs(
                        LocalDate.of(2026, 6, 12),
                        FilingStatus.SINGLE,
                        false,
                        2_000,
                        PaySchedule.BI_WEEKLY,
                        100_000,
                        0,
                        0,
                        0
                );

        PayrollWithholdingResult result =
                calculator.calculate(inputs);

        assertEquals(0, result.federalWithholding(), 0.001);
    }

    /**
     * Verifies that estates and trusts cannot be used as an employee
     * payroll-withholding filing status.
     */
    @Test
    void rejectsEstatesAndTrustsForPayroll() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PayrollWithholdingInputs(
                        LocalDate.of(2026, 6, 12),
                        FilingStatus.ESTATES_AND_TRUSTS,
                        false,
                        2_000,
                        PaySchedule.BI_WEEKLY,
                        0,
                        0,
                        0,
                        0
                )
        );
    }

    /**
     * Verifies that negative payroll amounts are rejected.
     */
    @Test
    void rejectsNegativePayrollAmounts() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PayrollWithholdingInputs(
                        LocalDate.of(2026, 6, 12),
                        FilingStatus.SINGLE,
                        false,
                        -1,
                        PaySchedule.BI_WEEKLY,
                        0,
                        0,
                        0,
                        0
                )
        );
    }

    /**
     * Verifies that the calculator rejects a null withholding input object.
     */
    @Test
    void rejectsNullInputs() {
        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculate(null)
        );
    }

    /**
     * Creates common biweekly withholding inputs for tests that do not
     * require additional Form W-4 adjustments.
     *
     * @param payDate paycheck date used to select the withholding year
     * @param filingStatus employee filing status
     * @param step2Checked whether Form W-4 Step 2 is selected
     * @param taxableWages taxable wages for the current pay period
     * @return configured payroll-withholding inputs
     */
    private PayrollWithholdingInputs inputs(
            LocalDate payDate,
            FilingStatus filingStatus,
            boolean step2Checked,
            double taxableWages
    ) {
        return new PayrollWithholdingInputs(
                payDate,
                filingStatus,
                step2Checked,
                taxableWages,
                PaySchedule.BI_WEEKLY,
                0,
                0,
                0,
                0
        );
    }
}
