package model;

import repository.PayrollTaxRepository;

/**
 * Calculates employee FICA withholding for one paycheck.
 *
 * Rates and annual thresholds are loaded from bundled payroll-tax data
 * selected by the year in which the wages are paid.
 */
public final class FicaCalculator {

    private static final PayrollTaxRepository REPOSITORY =
            new PayrollTaxRepository();

    private FicaCalculator() {}

    public static FicaResult calculate(
            double currentWages,
            double yearToDateWages,
            int taxYear
    ) {
        if (currentWages < 0) {
            throw new IllegalArgumentException(
                    "currentWages must be >= 0"
            );
        }

        if (yearToDateWages < 0) {
            throw new IllegalArgumentException(
                    "yearToDateWages must be >= 0"
            );
        }

        PayrollTaxData data = REPOSITORY.load(taxYear);

        double remainingSocialSecurityWages =
                Math.max(
                        0,
                        data.socialSecurityWageBase()
                                - yearToDateWages
                );

        double socialSecurityTaxableWages =
                Math.min(
                        currentWages,
                        remainingSocialSecurityWages
                );

        double socialSecurity =
                socialSecurityTaxableWages
                        * data.socialSecurityRate();

        double medicare =
                currentWages * data.medicareRate();

        double wagesBeforeAdditionalMedicare =
                Math.max(
                        0,
                        data.additionalMedicareThreshold()
                                - yearToDateWages
                );

        double additionalMedicareTaxableWages =
                Math.max(
                        0,
                        currentWages
                                - wagesBeforeAdditionalMedicare
                );

        double additionalMedicare =
                additionalMedicareTaxableWages
                        * data.additionalMedicareRate();

        return new FicaResult(
                socialSecurity,
                medicare,
                additionalMedicare
        );
    }
}
