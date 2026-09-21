package model;

/**
 * Canonical set of expense categories supported by the Expenses calculator.
 *
 * Categories are used for consistent labeling in the UI and reporting.
 */
public enum ExpenseCategory {
    TRANSPORTATION("Transportation"),
    UTILITIES_AND_COMMUNICATION("Utilities & Communication"),
    DINING_MEALS("Dining & Meals"),
    DEBT_REPAYMENT("Debt Repayment"),
    PROFESSIONAL_SERVICES("Professional Services"),
    SUBSCRIPTIONS("Subscriptions"),
    HEALTH_MEDICAL("Health & Medical"),
    HOUSEHOLD_SUPPLIES("Household Supplies"),
    PERSONAL_CARE("Personal Care"),
    ENTERTAINMENT("Entertainment"),
    SAVINGS_INVESTMENT("Savings & Investment"),
    MISCELLANEOUS("Miscellaneous"),
    HOUSING("Housing"),
    GROCERIES("Groceries"),
    INSURANCE("Insurance"),
    PET_CARE("Pet Care"),
    AFFIRM("Affirm");

    private final String displayName;

    ExpenseCategory(String displayName) {
        this.displayName = displayName;
    }

    /** Human-friendly label for UI controls (ComboBox, TableView, etc.). */
    public String getDisplayName() {
        return displayName;
    }

    /** Default string representation for display contexts. */
    @Override
    public String toString() {
        return displayName;
    }
}
