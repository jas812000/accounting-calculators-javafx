package model;

/**
 * Pay frequency used to convert annual pay to per-period pay and
 * to label payroll results in the UI.
 */
public enum PaySchedule {
    WEEKLY, BI_WEEKLY, MONTHLY;

    /** Human-readable label for UI output. */
    public String displayName() {
        return switch (this) {
            case WEEKLY -> "Weekly";
            case BI_WEEKLY -> "Bi-Weekly";
            case MONTHLY -> "Monthly";
        };
    }
}
