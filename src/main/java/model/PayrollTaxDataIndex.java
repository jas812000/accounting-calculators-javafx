package model;

import java.util.List;

/**
 * Manifest of payroll-tax years bundled with the application.
 */
public record PayrollTaxDataIndex(
        List<Integer> supportedYears
) {
    public PayrollTaxDataIndex {
        if (supportedYears == null || supportedYears.isEmpty()) {
            throw new IllegalArgumentException(
                    "supportedYears must not be empty"
            );
        }

        supportedYears = List.copyOf(supportedYears);
    }
}
