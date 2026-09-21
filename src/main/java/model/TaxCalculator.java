package model;

import repository.TaxTableRepository;

import java.util.List;
import java.util.Map;

/**
 * Calculates annual federal income tax using progressive tax schedules
 * loaded from bundled year-specific JSON resources.
 */
public final class TaxCalculator {

    private static final TaxTableRepository TAX_TABLE_REPOSITORY =
            new TaxTableRepository();

    private TaxCalculator() {
    }

    /**
     * Calculates annual federal income tax for the supplied inputs.
     *
     * @param inputs tax year, filing status, and taxable income
     * @return calculated federal income tax result
     */
    public static TaxResult calculate(TaxInputs inputs) {
        if (inputs == null) {
            throw new IllegalArgumentException("inputs are required");
        }

        double federalTax = federal(
                inputs.year(),
                inputs.status(),
                inputs.taxableIncome()
        );

        return new TaxResult(
                inputs.year(),
                inputs.status(),
                inputs.taxableIncome(),
                federalTax
        );
    }

    /**
     * Calculates federal income tax using the progressive schedule for
     * the requested year and filing status.
     *
     * @param year tax year
     * @param status filing status
     * @param taxableIncome taxable income
     * @return calculated federal income tax
     */
    public static double federal(
            int year,
            FilingStatus status,
            double taxableIncome
    ) {
        if (status == null) {
            throw new IllegalArgumentException("status is required");
        }

        if (taxableIncome < 0) {
            throw new IllegalArgumentException(
                    "taxableIncome must be >= 0"
            );
        }

        TaxYearData taxYearData = TAX_TABLE_REPOSITORY.load(year);
        String scheduleName = resolveScheduleName(taxYearData, status);

        List<TaxBracket> brackets =
                taxYearData.schedules().get(scheduleName);

        if (brackets == null || brackets.isEmpty()) {
            throw new IllegalArgumentException(
                    "No tax schedule found for filing status: " + status
            );
        }

        return calculateProgressiveTax(taxableIncome, brackets);
    }

    private static String resolveScheduleName(
            TaxYearData taxYearData,
            FilingStatus status
    ) {
        String statusName = status.name();
        Map<String, String> mappings =
                taxYearData.filingStatusMappings();

        return mappings.getOrDefault(statusName, statusName);
    }

    private static double calculateProgressiveTax(
            double taxableIncome,
            List<TaxBracket> brackets
    ) {
        double tax = 0.0;

        for (TaxBracket bracket : brackets) {
            if (taxableIncome <= bracket.minimum()) {
                break;
            }

            double upperBound = bracket.maximum() == null
                    ? taxableIncome
                    : Math.min(taxableIncome, bracket.maximum());

            double taxableAmount = upperBound - bracket.minimum();

            if (taxableAmount > 0) {
                tax += taxableAmount * bracket.rate();
            }

            if (bracket.maximum() == null
                    || taxableIncome <= bracket.maximum()) {
                break;
            }
        }

        return tax;
    }
}
