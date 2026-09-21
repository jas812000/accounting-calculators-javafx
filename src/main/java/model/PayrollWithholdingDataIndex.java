package model;

import java.util.List;

/**
 * Describes the payroll-withholding years available to the application.
 *
 * @param supportedYears years backed by bundled IRS Publication 15-T data
 */
public record PayrollWithholdingDataIndex(
        List<Integer> supportedYears
) {
    public PayrollWithholdingDataIndex {
        if (supportedYears == null || supportedYears.isEmpty()) {
            throw new IllegalArgumentException(
                    "supportedYears are required"
            );
        }

        supportedYears = List.copyOf(supportedYears);
    }
}
