package model;

/**
 * Year-specific employee payroll tax rates and thresholds.
 */
public record PayrollTaxData(
        int taxYear,
        double socialSecurityRate,
        double socialSecurityWageBase,
        double medicareRate,
        double additionalMedicareRate,
        double additionalMedicareThreshold
) {
    public PayrollTaxData {
        if (taxYear <= 0) {
            throw new IllegalArgumentException("taxYear must be > 0");
        }

        if (socialSecurityRate < 0 || socialSecurityRate > 1) {
            throw new IllegalArgumentException(
                    "socialSecurityRate must be between 0 and 1"
            );
        }

        if (medicareRate < 0 || medicareRate > 1) {
            throw new IllegalArgumentException(
                    "medicareRate must be between 0 and 1"
            );
        }

        if (additionalMedicareRate < 0
                || additionalMedicareRate > 1) {
            throw new IllegalArgumentException(
                    "additionalMedicareRate must be between 0 and 1"
            );
        }

        if (socialSecurityWageBase <= 0) {
            throw new IllegalArgumentException(
                    "socialSecurityWageBase must be > 0"
            );
        }

        if (additionalMedicareThreshold <= 0) {
            throw new IllegalArgumentException(
                    "additionalMedicareThreshold must be > 0"
            );
        }
    }
}
