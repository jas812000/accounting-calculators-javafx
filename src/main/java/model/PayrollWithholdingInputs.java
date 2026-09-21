package model;

import java.time.LocalDate;

/**
 * Inputs required to calculate federal income tax withholding using
 * IRS Publication 15-T Worksheet 1A for a 2020-or-later Form W-4.
 *
 * @param payDate date wages are paid; determines the withholding-data year
 * @param filingStatus employee's Form W-4 filing status
 * @param step2Checked whether the Form W-4 Step 2 checkbox is selected
 * @param taxableWages taxable wages for the current payroll period
 * @param paySchedule payroll frequency
 * @param step3Credits annual credits entered in Form W-4 Step 3
 * @param step4aOtherIncome annual other income entered in Form W-4 Step 4(a)
 * @param step4bDeductions annual deductions entered in Form W-4 Step 4(b)
 * @param step4cAdditionalWithholding additional withholding per pay period
 */
public record PayrollWithholdingInputs(
        LocalDate payDate,
        FilingStatus filingStatus,
        boolean step2Checked,
        double taxableWages,
        PaySchedule paySchedule,
        double step3Credits,
        double step4aOtherIncome,
        double step4bDeductions,
        double step4cAdditionalWithholding
) {
    public PayrollWithholdingInputs {
        if (payDate == null) {
            throw new IllegalArgumentException("payDate is required");
        }

        if (filingStatus == null) {
            throw new IllegalArgumentException("filingStatus is required");
        }

        if (filingStatus == FilingStatus.ESTATES_AND_TRUSTS) {
            throw new IllegalArgumentException(
                    "ESTATES_AND_TRUSTS is not a Form W-4 filing status"
            );
        }

        if (paySchedule == null) {
            throw new IllegalArgumentException("paySchedule is required");
        }

        if (taxableWages < 0
                || step3Credits < 0
                || step4aOtherIncome < 0
                || step4bDeductions < 0
                || step4cAdditionalWithholding < 0) {
            throw new IllegalArgumentException(
                    "Payroll withholding amounts must be >= 0"
            );
        }
    }
}
