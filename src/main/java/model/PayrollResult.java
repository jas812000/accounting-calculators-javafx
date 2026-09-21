package model;

/**
 * Output values produced by payroll calculations for a pay period.
 *
 * Federal and "other" deductions are included for forward compatibility,
 * even if currently implemented as placeholders in the calculator.
 */
public record PayrollResult(
        double hourlyRate,
        double hoursInPeriod,
        PaySchedule schedule,
        double gross,
        double federal,
        double fica,
        double other,
        double net
) {

    /**
     * Derives an annualized pay estimate using a standard 2080-hour work year.
     * This provides a consistent baseline regardless of selected pay schedule.
     */
    public double annualPay(){
        return hourlyRate * 2080;
    }
}
