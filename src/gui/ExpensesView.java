package gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextFormatter;

import model.Expense;
import model.ExpenseCategory;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;

/**
 * Expenses entry and summary screen.
 *
 * Allows users to add categorized expense records into an in-memory table,
 * remove items, and compute a running total. This view focuses on input
 * validation and consistent formatting rather than persistence.
 */
public class ExpensesView extends VBox {

    /** In-memory expense list backing the TableView. */
    private final ObservableList<Expense> expenses = FXCollections.observableArrayList();

    /** Label showing the computed total for the current session. */
    private final Label totalLabel = new Label("Total: $0.00");

    /**
     * Constructs the expenses screen.
     *
     * @param onBack            callback to return to main menu
     * @param onShowCalculating callback to show a "calculating" popup (optional UX cue)
     */
    public ExpensesView(Runnable onBack, Runnable onShowCalculating) {

        // --- Layout scaffold ---
        setSpacing(18);
        setPadding(new Insets(32));
        setAlignment(Pos.TOP_CENTER);

        // --- Title ---
        Label title = new Label("Expenses Calculator");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-font-family: 'Arial';");

        // --- Inputs: category, description, amount, optional due date ---
        ComboBox<ExpenseCategory> categoryDropdown = new ComboBox<>();
        categoryDropdown.getItems().addAll(ExpenseCategory.values());
        categoryDropdown.setPromptText("Select expense category");

        TextField descriptionInput = new TextField();
        descriptionInput.setPromptText("Expense description");

        TextField amountInput = new TextField();
        amountInput.setPromptText("Amount");

        // Constrain amount field size to prevent layout stretching.
        amountInput.setPrefColumnCount(12);
        amountInput.setPrefWidth(200);
        amountInput.setMaxWidth(200);
        HBox.setHgrow(amountInput, Priority.NEVER);

        // Input filter: numbers with optional decimal, max 2 decimal places.
        UnaryOperator<TextFormatter.Change> amountFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty()) return change;                  // allow clearing
            if (!newText.matches("\\d*(\\.\\d*)?")) return null;   // digits + optional single dot
            int dot = newText.indexOf('.');
            if (dot >= 0 && newText.length() - dot - 1 > 2) return null; // max 2 decimals
            return change;
        };
        amountInput.setTextFormatter(new TextFormatter<>(amountFilter));

        DatePicker dueDatePicker = new DatePicker();
        dueDatePicker.setPromptText("Due date (optional)");

        // --- Add Expense button ---
        Button addBtn = new Button("Add expense");
        addBtn.setMinWidth(140);
        addBtn.setPrefWidth(140);

        // Disable add button until a category is selected and amount is a valid money value.
        addBtn.disableProperty().bind(
                categoryDropdown.getSelectionModel().selectedItemProperty().isNull()
                        .or(Bindings.createBooleanBinding(
                                () -> {
                                    String t = amountInput.getText();
                                    return t == null || t.isBlank() || !t.matches("\\d+(\\.\\d{1,2})?");
                                },
                                amountInput.textProperty()
                        ))
        );

        addBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");

        // Adds a new expense to the table after parsing + validation.
        addBtn.setOnAction(e -> {
            String desc = descriptionInput.getText().trim();
            String amtText = amountInput.getText().trim();
            if (desc.isEmpty() || amtText.isEmpty()) return;

            try {
                double amt = Double.parseDouble(amtText);

                // Enforce positive amounts (zero/negative entries are invalid).
                if (amt <= 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Amount");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter a positive amount greater than zero.");
                    alert.showAndWait();
                    return;
                }

                expenses.add(new Expense(
                        categoryDropdown.getValue(),
                        desc,
                        amt,
                        dueDatePicker.getValue()
                ));

                // Reset UI controls for next entry.
                descriptionInput.clear();
                amountInput.clear();
                dueDatePicker.setValue(null);
                categoryDropdown.getSelectionModel().clearSelection();
                categoryDropdown.requestFocus();

            } catch (NumberFormatException ex) {
                // User-facing error guidance for invalid numeric input.
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Input");
                alert.setHeaderText(null);
                alert.setContentText("""
                        Please enter a valid number for the amount.
                        Examples: 1200, 1200.50, 75.25
                        """);
                alert.showAndWait();

                // Speed up correction.
                amountInput.requestFocus();
                amountInput.selectAll();
            }
        });

        HBox amountRow = new HBox(10, amountInput, addBtn);
        amountRow.setAlignment(Pos.CENTER_LEFT);
        amountRow.setFillHeight(false);

        VBox form = new VBox(12, categoryDropdown, descriptionInput, amountRow, dueDatePicker);
        form.setAlignment(Pos.CENTER_LEFT);
        form.setMaxWidth(520);

        // --- TableView setup ---
        TableView<Expense> table = new TableView<>(expenses);
        table.setPrefHeight(280);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<Expense, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(
                        c.getValue().getCategory() == null ? "" : c.getValue().getCategory().toString()
                )
        );

        TableColumn<Expense, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getDescription()));

        TableColumn<Expense, String> amtCol = new TableColumn<>("Amount");
        amtCol.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(String.format("$%.2f", c.getValue().getAmount()))
        );

        TableColumn<Expense, String> dueCol = new TableColumn<>("Due Date");
        dueCol.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getDueDate() == null ? "" : c.getValue().getDueDate().toString())
        );

        table.getColumns().addAll(List.of(catCol, descCol, amtCol, dueCol));

        // --- Actions: remove, calculate, back ---
        Button removeBtn = new Button("Remove selected");
        removeBtn.setOnAction(e -> {
            Expense sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) expenses.remove(sel);
        });

        Button calculateBtn = new Button("Calculate");
        calculateBtn.setOnAction(e -> {
            NumberFormat cf = NumberFormat.getCurrencyInstance(Locale.US);
            totalLabel.setText("Total: " + cf.format(
                    expenses.stream()
                            .mapToDouble(Expense::getAmount)
                            .sum()
            ));
        });

        Button backBtn = new Button("Back to Main Menu");
        backBtn.setOnAction(e -> { if (onBack != null) onBack.run(); });

        totalLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        HBox actions = new HBox(12, calculateBtn, removeBtn, totalLabel, backBtn);
        actions.setAlignment(Pos.CENTER_LEFT);

        VBox content = new VBox(16, form, table, actions);
        content.setAlignment(Pos.TOP_CENTER);

        getChildren().addAll(title, content);
    }
}
