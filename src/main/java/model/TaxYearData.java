package model;

import java.util.List;
import java.util.Map;

/**
 * Represents the federal income tax schedules and filing-status mappings
 * for one supported tax year.
 *
 * @param taxYear tax year represented by this data
 * @param schedules progressive tax brackets keyed by schedule name
 * @param filingStatusMappings filing statuses that reuse another schedule
 */
public record TaxYearData(
        int taxYear,
        Map<String, List<TaxBracket>> schedules,
        Map<String, String> filingStatusMappings
) {
    public TaxYearData {
        if (taxYear <= 0) {
            throw new IllegalArgumentException("taxYear must be > 0");
        }

        if (schedules == null || schedules.isEmpty()) {
            throw new IllegalArgumentException("schedules are required");
        }

        if (filingStatusMappings == null) {
            throw new IllegalArgumentException(
                    "filingStatusMappings are required"
            );
        }
    }
}
