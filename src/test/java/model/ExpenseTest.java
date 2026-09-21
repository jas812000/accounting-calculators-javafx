package model;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Expense} model.
 *
 * Verifies expense creation, optional-field handling, amount validation,
 * description normalization, and display formatting.
 */
class ExpenseTest {

    /**
     * Verifies that a valid expense preserves all supplied values.
     */
    @Test
    void createsExpenseWithValidValues() {
        LocalDate dueDate = LocalDate.of(2026, 10, 1);

        Expense expense = new Expense(
                ExpenseCategory.UTILITIES_AND_COMMUNICATION,
                "Electric bill",
                125.50,
                dueDate
        );

        assertEquals(
                ExpenseCategory.UTILITIES_AND_COMMUNICATION,
                expense.getCategory()
        );
        assertEquals("Electric bill", expense.getDescription());
        assertEquals(125.50, expense.getAmount(), 0.001);
        assertEquals(dueDate, expense.getDueDate());
    }

    /**
     * Verifies that description and due date may be omitted.
     */
    @Test
    void allowsOptionalDescriptionAndDueDate() {
        Expense expense = new Expense(
                ExpenseCategory.GROCERIES,
                null,
                75.25,
                null
        );

        assertNull(expense.getDescription());
        assertNull(expense.getDueDate());
    }

    /**
     * Verifies that a blank description is normalized to null.
     */
    @Test
    void normalizesBlankDescriptionToNull() {
        Expense expense = new Expense(
                ExpenseCategory.MISCELLANEOUS,
                "   ",
                10.00,
                null
        );

        assertNull(expense.getDescription());
    }

    /**
     * Verifies that surrounding whitespace is removed from descriptions.
     */
    @Test
    void trimsDescription() {
        Expense expense = new Expense(
                ExpenseCategory.PET_CARE,
                "  Dog food  ",
                45.00,
                null
        );

        assertEquals("Dog food", expense.getDescription());
    }

    /**
     * Verifies that an expense cannot be created without a category.
     */
    @Test
    void rejectsNullCategory() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Expense(null, "Test", 10.00, null)
        );

        assertEquals(
                "Category cannot be null",
                exception.getMessage()
        );
    }

    /**
     * Verifies that zero is not accepted as an expense amount.
     */
    @Test
    void rejectsZeroAmount() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Expense(
                        ExpenseCategory.GROCERIES,
                        "Test",
                        0.00,
                        null
                )
        );
    }

    /**
     * Verifies that negative expense amounts are rejected.
     */
    @Test
    void rejectsNegativeAmount() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Expense(
                        ExpenseCategory.GROCERIES,
                        "Test",
                        -1.00,
                        null
                )
        );
    }

    /**
     * Verifies that non-finite numeric values cannot be used as amounts.
     */
    @Test
    void rejectsNonFiniteAmount() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Expense(
                        ExpenseCategory.GROCERIES,
                        "Test",
                        Double.NaN,
                        null
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Expense(
                        ExpenseCategory.GROCERIES,
                        "Test",
                        Double.POSITIVE_INFINITY,
                        null
                )
        );
    }

    /**
     * Verifies display formatting when all optional fields are populated.
     */
    @Test
    void formatsCompleteExpenseForDisplay() {
        Expense expense = new Expense(
                ExpenseCategory.PET_CARE,
                "Dog food",
                45.50,
                LocalDate.of(2026, 10, 1)
        );

        assertEquals(
                "PET_CARE | $45.50 | Description: Dog food | Due: 2026-10-01",
                expense.toString()
        );
    }

    /**
     * Verifies that absent optional fields are omitted from display output.
     */
    @Test
    void omitsOptionalFieldsFromDisplay() {
        Expense expense = new Expense(
                ExpenseCategory.GROCERIES,
                null,
                75.00,
                null
        );

        assertEquals(
                "GROCERIES | $75",
                expense.toString()
        );
    }
}
