package model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FederalWithholdingCalculatorTest {

    private final FederalWithholdingCalculator calculator =
            new FederalWithholdingCalculator();

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

    @Test
    void rejectsNullInputs() {
        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculate(null)
        );
    }

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
