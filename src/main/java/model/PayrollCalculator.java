package model;

/**
 * Stateless payroll calculations for hourly and salaried employees.
 *
 * Gross pay is calculated from the employee's compensation inputs.
 * Federal income tax withholding is calculated using IRS Publication 15-T
 * data selected from the employee's pay date.
 *
 * Employee FICA withholding applies Social Security, Medicare, and
 * Additional Medicare rules using wages already paid during the calendar year.
 */
public final class PayrollCalculator {

    private static final FederalWithholdingCalculator
            FEDERAL_WITHHOLDING_CALCULATOR =
            new FederalWithholdingCalculator();

    private PayrollCalculator() {}

    /**
     * Calculates payroll for an hourly employee for one pay period.
     */
    public static PayrollResult calculateFromHourly(
            PayrollInputs payrollInputs,
            PayrollTaxInputs taxInputs
    ) {
        if (payrollInputs == null) {
            throw new IllegalArgumentException(
                    "payrollInputs are required"
            );
        }

        validateTaxInputs(taxInputs);

        double gross = gross(
                payrollInputs.hourlyRate(),
                payrollInputs.hoursInPeriod()
        );

        return buildResult(
                payrollInputs.hourlyRate(),
                payrollInputs.hoursInPeriod(),
                payrollInputs.schedule(),
                gross,
                taxInputs
        );
    }

    /**
     * Calculates payroll for a salaried employee for one pay period.
     */
    public static PayrollResult calculateFromAnnual(
            double annualSalary,
            PaySchedule schedule,
            PayrollTaxInputs taxInputs
    ) {
        if (annualSalary <= 0) {
            throw new IllegalArgumentException(
                    "annualSalary must be > 0"
            );
        }

        if (schedule == null) {
            throw new IllegalArgumentException(
                    "schedule is required"
            );
        }

        validateTaxInputs(taxInputs);

        double gross;
        double hoursInPeriod;

        switch (schedule) {
            case WEEKLY -> {
                gross = annualSalary / 52.0;
                hoursInPeriod = 40;
            }
            case BI_WEEKLY -> {
                gross = annualSalary / 26.0;
                hoursInPeriod = 80;
            }
            case MONTHLY -> {
                gross = annualSalary / 12.0;
                hoursInPeriod = 2080.0 / 12.0;
            }
            default -> throw new IllegalStateException(
                    "Unexpected schedule: " + schedule
            );
        }

        double hourlyRate = annualSalary / 2080.0;

        return buildResult(
                hourlyRate,
                hoursInPeriod,
                schedule,
                gross,
                taxInputs
        );
    }

    /**
     * Calculates gross hourly wages.
     */
    public static double gross(
            double hourlyRate,
            double hoursInPeriod
    ) {
        if (hourlyRate <= 0) {
            throw new IllegalArgumentException(
                    "hourlyRate must be > 0"
            );
        }

        if (hoursInPeriod < 0) {
            throw new IllegalArgumentException(
                    "hoursInPeriod must be >= 0"
            );
        }

        return hourlyRate * hoursInPeriod;
    }

    private static PayrollResult buildResult(
            double hourlyRate,
            double hoursInPeriod,
            PaySchedule schedule,
            double gross,
            PayrollTaxInputs taxInputs
    ) {
        double federal = taxInputs.exemptFromFederalWithholding()
                ? 0
                : calculateFederalWithholding(
                        gross,
                        schedule,
                        taxInputs
                );

        FicaResult ficaResult =
                FicaCalculator.calculate(
                        gross,
                        taxInputs.yearToDateWages(),
                        taxInputs.payDate().getYear()
                );

        double fica = ficaResult.total();
        double other = taxInputs.otherDeductions();

        double net =
                gross - federal - fica - other;

        return new PayrollResult(
                hourlyRate,
                hoursInPeriod,
                schedule,
                gross,
                federal,
                fica,
                other,
                net
        );
    }

    private static double calculateFederalWithholding(
            double taxableWages,
            PaySchedule schedule,
            PayrollTaxInputs taxInputs
    ) {
        PayrollWithholdingInputs withholdingInputs =
                new PayrollWithholdingInputs(
                        taxInputs.payDate(),
                        taxInputs.filingStatus(),
                        taxInputs.step2Checked(),
                        taxableWages,
                        schedule,
                        taxInputs.step3Credits(),
                        taxInputs.step4aOtherIncome(),
                        taxInputs.step4bDeductions(),
                        taxInputs.step4cAdditionalWithholding()
                );

        return FEDERAL_WITHHOLDING_CALCULATOR
                .calculate(withholdingInputs)
                .federalWithholding();
    }

    private static void validateTaxInputs(
            PayrollTaxInputs taxInputs
    ) {
        if (taxInputs == null) {
            throw new IllegalArgumentException(
                    "taxInputs are required"
            );
        }
    }
}
