package model;

import java.time.LocalDate;

/**
 * Employee tax and deduction information needed for one payroll calculation.
 *
 * Federal withholding uses a 2020-or-later Form W-4 and IRS Publication 15-T.
 * FICA uses year-to-date wages before the current paycheck so annual Social
 * Security and Additional Medicare thresholds can be applied correctly.
 *
 * @param payDate date wages are paid; determines the payroll tax year
 * @param filingStatus filing status selected on Form W-4
 * @param step2Checked whether the Form W-4 Step 2 checkbox is selected
 * @param step3Credits annual credits from Form W-4 Step 3
 * @param step4aOtherIncome annual other income from Form W-4 Step 4(a)
 * @param step4bDeductions annual deductions from Form W-4 Step 4(b)
 * @param step4cAdditionalWithholding additional withholding per pay period
 * @param exemptFromFederalWithholding whether the employee claimed exemption
 * @param otherDeductions other employee deductions for this pay period
 * @param yearToDateWages wages paid earlier in the same calendar year,
 *                        excluding the current paycheck
 */
public record PayrollTaxInputs(
        LocalDate payDate,
        FilingStatus filingStatus,
        boolean step2Checked,
        double step3Credits,
        double step4aOtherIncome,
        double step4bDeductions,
        double step4cAdditionalWithholding,
        boolean exemptFromFederalWithholding,
        double otherDeductions,
        double yearToDateWages
) {
    public PayrollTaxInputs {
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

        if (step3Credits < 0
                || step4aOtherIncome < 0
                || step4bDeductions < 0
                || step4cAdditionalWithholding < 0
                || otherDeductions < 0
                || yearToDateWages < 0) {
            throw new IllegalArgumentException(
                    "Payroll tax amounts must be >= 0"
            );
        }
    }
}
