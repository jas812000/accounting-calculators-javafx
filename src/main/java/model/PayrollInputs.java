package model;

/**
 * Inputs for payroll computation.
 *
 * This record enforces minimal validation to prevent invalid calculations
 * (negative hours, missing schedule, etc.).
 */
public record PayrollInputs(
        double hourlyRate,
        double hoursInPeriod,
        PaySchedule schedule
) {
    public PayrollInputs {
        if (hourlyRate <= 0) throw new IllegalArgumentException("hourlyRate must be > 0");
        if (hoursInPeriod < 0) throw new IllegalArgumentException("hoursInPeriod must be ≥ 0");
        if (schedule == null) throw new IllegalArgumentException("schedule is required");
    }
}
