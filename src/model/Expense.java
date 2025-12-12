package model;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

/**
 * Immutable expense record used by the Expenses calculator.
 *
 * Each Expense includes:
 * - a required category
 * - an optional description
 * - a required monetary amount
 * - an optional due date
 *
 * This model is intentionally UI-friendly (e.g., formatted toString) while keeping
 * core fields immutable and validated at construction time.
 */
public class Expense {

    /** Required category for the expense (never null). */
    private final ExpenseCategory category;

    /** Optional free-text description (may be null or blank). */
    private final String description;

    /**
     * Required monetary amount (stored as Double to allow null checks at construction).
     * Business rules for positivity are enforced in the UI layer before object creation.
     */
    private final Double amount;

    /** Optional due date (may be null). */
    private final LocalDate dueDate;

    /**
     * Creates a new Expense record.
     *
     * @param category  required category (non-null)
     * @param description optional description (nullable)
     * @param amount    required amount (non-null)
     * @param dueDate   optional due date (nullable)
     * @throws IllegalArgumentException if category or amount is null
     */
    public Expense(ExpenseCategory category, String description, Double amount, LocalDate dueDate) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }

        this.category = category;
        this.description = description;
        this.amount = amount;
        this.dueDate = dueDate;
    }

    /** @return the expense category */
    public ExpenseCategory getCategory() {
        return category;
    }

    /** @return the description text (may be null) */
    public String getDescription() {
        return description;
    }

    /** @return the expense amount as a primitive double */
    public double getAmount() {
        return amount;
    }

    /** @return the due date (may be null) */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Returns a human-readable summary string for display and debugging.
     *
     * Formats monetary values using US currency rules and omits optional fields
     * when they are not present.
     *
     * @return formatted description of the expense
     */
    @Override
    public String toString() {
        // Currency formatter for US currency.
        NumberFormat numFormat = NumberFormat.getCurrencyInstance(Locale.US);

        // Format amount as a whole number if it has no fractional component; otherwise show 2 decimals.
        boolean isWhole = amount % 1 == 0;
        numFormat.setMinimumFractionDigits(isWhole ? 0 : 2);
        numFormat.setMaximumFractionDigits(isWhole ? 0 : 2);

        String amountStr = numFormat.format(amount);

        // Build output: CATEGORY | $amount | optional fields...
        StringBuilder sb = new StringBuilder();
        sb.append(category.name())
          .append(" | ")
          .append(amountStr);

        if (description != null && !description.isBlank()) {
            sb.append(" | Description: ").append(description.trim());
        }

        if (dueDate != null) {
            sb.append(" | Due: ").append(dueDate);
        }

        return sb.toString();
    }
}
