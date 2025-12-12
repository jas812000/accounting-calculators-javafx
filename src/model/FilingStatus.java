package model;

/**
 * Filing status options used by the tax calculator.
 *
 * Values align with IRS filing categories and are used to select
 * the correct tax bracket table for a given tax year.
 */
public enum FilingStatus {
    SINGLE("Single"),
    MARRIED_FILING_JOINTLY("Married Filing Jointly"),
    MARRIED_FILING_SEPARATELY("Married Filing Separately"),
    HEAD_OF_HOUSEHOLD("Head of Household"),
    QUALIFYING_SURVIVING_SPOUSE("Qualifying Surviving Spouse"),
    ESTATES_AND_TRUSTS("Estates and Trusts");

    private final String display;

    FilingStatus(String display) { this.display = display; }

    /** UI-facing label. */
    public String displayName() { return display; }

    @Override public String toString() { return display; }

    /**
     * Parses a UI label back into an enum.
     *
     * @param s display label (must match a known filing status)
     * @return the corresponding FilingStatus
     */
    public static FilingStatus fromDisplay(String s) {
        for (FilingStatus fs : values()) if (fs.display.equals(s)) return fs;
        throw new IllegalArgumentException("Unknown filing status: " + s);
    }
}
