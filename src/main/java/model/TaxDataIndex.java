package model;

import java.util.List;

/**
 * Describes the tax years available from the bundled tax-data resources.
 *
 * @param supportedYears tax years supported by the application
 */
public record TaxDataIndex(
        List<Integer> supportedYears
) {
    public TaxDataIndex {
        if (supportedYears == null || supportedYears.isEmpty()) {
            throw new IllegalArgumentException(
                    "supportedYears are required"
            );
        }

        supportedYears = List.copyOf(supportedYears);
    }
}
