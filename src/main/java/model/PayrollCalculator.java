package model;

/**
 * Stateless payroll calculations.
 *
 * Produces derived payroll values from either hourly inputs (rate + hours)
 * or annual salary inputs, applying a simplified FICA deduction.
 *
 * Notes / simplifications:
 * - FICA is modeled as a flat rate (SS + Medicare) and does not include
 *   the SS wage base cap or additional Medicare thresholds.
 * - Federal withholding and other deductions are placeholders.
 */
public final class PayrollCalculator {

    // Employee FICA rate (Social Security 6.2% + Medicare 1.45%) – simplified
    private static final double FICA_RATE = 0.0765;

    private PayrollCalculator() {}

    /**
     * Calculates payroll for hourly employees for a single pay period.
     *
     * @param in hourly payroll inputs (validated by the record constructor)
     * @return computed payroll result for the period
     */
    public static PayrollResult calculateFromHourly(PayrollInputs in) {
        double gross   = gross(in.hourlyRate(), in.hoursInPeriod());
        double fica    = fica(gross);
        double federal = 0.0; // TODO: implement real withholding logic
        double other   = 0.0; // TODO: support user-defined deductions
        double net     = gross - fica - federal - other;

        return new PayrollResult(
                in.hourlyRate(),
                in.hoursInPeriod(),
                in.schedule(),
                gross, federal, fica, other, net
        );
    }

    /**
     * Calculates payroll from an annual salary by converting to per-period gross pay.
     *
     * @param annualSalary annual salary (gross)
     * @param schedule pay schedule to convert salary to pay-period gross
     * @return computed payroll result for the selected schedule
     */
    public static PayrollResult calculateFromAnnual(double annualSalary, PaySchedule schedule) {
        double gross;
        double hoursInPeriod;
        double hourlyRate;

        // Convert annual salary to gross pay per schedule.
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
                hoursInPeriod = 173.33; // Approx 2080 / 12
            }
            default -> throw new IllegalStateException("Unexpected schedule: " + schedule);
        }

        // Convert annual salary to approximate hourly rate using standard 2080-hour year.
        hourlyRate = annualSalary / 2080.0;

        double fica    = fica(gross);
        double federal = 0.0; // TODO: implement real withholding logic
        double other   = 0.0; // TODO: user-defined deductions
        double net     = gross - fica - federal - other;

        return new PayrollResult(
                hourlyRate, hoursInPeriod,
                schedule, gross, federal,
                fica, other, net
        );
    }

    /** Gross pay is the base computation for hourly wages. */
    public static double gross(double hourlyRate, double hoursInPeriod) {
        return hourlyRate * hoursInPeriod;
    }

    /** Simplified FICA calculation. */
    public static double fica(double gross) {
        return gross * FICA_RATE;
    }
}
