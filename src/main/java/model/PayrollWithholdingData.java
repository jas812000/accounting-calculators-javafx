package model;

import java.util.List;
import java.util.Map;

/**
 * IRS Publication 15-T data for one withholding year.
 *
 * Standard schedules apply when Form W-4 Step 2 is not checked.
 * Step-2 schedules apply when that checkbox is checked.
 *
 * @param taxYear withholding year
 * @param payPeriodsPerYear supported payroll frequencies
 * @param standardSchedules standard annual Percentage Method schedules
 * @param step2Schedules Step 2 checkbox annual Percentage Method schedules
 */
public record PayrollWithholdingData(
        int taxYear,
        Map<String, Integer> payPeriodsPerYear,
        Map<String, List<WithholdingBracket>> standardSchedules,
        Map<String, List<WithholdingBracket>> step2Schedules
) {
    public PayrollWithholdingData {
        if (taxYear <= 0) {
            throw new IllegalArgumentException("taxYear must be > 0");
        }

        if (payPeriodsPerYear == null || payPeriodsPerYear.isEmpty()) {
            throw new IllegalArgumentException(
                    "payPeriodsPerYear is required"
            );
        }

        if (standardSchedules == null || standardSchedules.isEmpty()) {
            throw new IllegalArgumentException(
                    "standardSchedules are required"
            );
        }

        if (step2Schedules == null || step2Schedules.isEmpty()) {
            throw new IllegalArgumentException(
                    "step2Schedules are required"
            );
        }
    }
}
