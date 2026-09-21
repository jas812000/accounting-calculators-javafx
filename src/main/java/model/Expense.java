package model;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

/**
 * Immutable expense record used by the Expenses calculator.
 *<p>
 * Each Expense includes:
 * - a required category
 * - an optional description
 * - a required positive monetary amount
 * - an optional due date
 *<p>
 * This model is intentionally UI-friendly (e.g., formatted toString) while keeping
 * core fields immutable and validated at construction time.
 */
public class Expense {

    /** Required category for the expense (never null). */
    private final ExpenseCategory category;

    /** Optional free-text description (may be null). */
    private final String description;

    /** Required positive monetary amount. */
    private final double amount;

    /** Optional due date (may be null). */
    private final LocalDate dueDate;

    /**
     * Creates a new Expense record.
     *
     * @param category    required category (non-null)
     * @param description optional description (nullable or blank)
     * @param amount      required positive amount
     * @param dueDate     optional due date (nullable)
     * @throws IllegalArgumentException if category is null or amount is not positive
     */
    public Expense(
            ExpenseCategory category,
            String description,
            double amount,
            LocalDate dueDate) {

        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }

        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        this.category = category;
        this.description = description == null || description.isBlank()
                ? null
                : description.trim();
        this.amount = amount;
        this.dueDate = dueDate;
    }

    /** @return the expense category */
    public ExpenseCategory getCategory() {
        return category;
    }

    /** @return the description text, or null when no description was supplied */
    public String getDescription() {
        return description;
    }

    /** @return the expense amount */
    public double getAmount() {
        return amount;
    }

    /** @return the due date, or null when no due date was supplied */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Returns a human-readable summary string for display and debugging.
     *<p>
     * Formats monetary values using US currency rules and omits optional fields
     * when they are not present.
     *<p>
     * @return formatted description of the expense
     */
    @Override
    public String toString() {
        NumberFormat numFormat = NumberFormat.getCurrencyInstance(Locale.US);

        boolean isWhole = amount % 1 == 0;
        numFormat.setMinimumFractionDigits(isWhole ? 0 : 2);
        numFormat.setMaximumFractionDigits(isWhole ? 0 : 2);

        String amountStr = numFormat.format(amount);

        StringBuilder sb = new StringBuilder();

        sb.append(category.name())
                .append(" | ")
                .append(amountStr);

        if (description != null) {
            sb.append(" | Description: ").append(description);
        }

        if (dueDate != null) {
            sb.append(" | Due: ").append(dueDate);
        }

        return sb.toString();
    }
}
