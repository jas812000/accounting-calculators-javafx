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
 * remove items, and view the current total. This view focuses on input
 * validation and consistent formatting rather than persistence.
 */
public class ExpensesView extends VBox {

    /** In-memory expense list backing the TableView. */
    private final ObservableList<Expense> expenses =
            FXCollections.observableArrayList();

    /** Label showing the current total for the session. */
    private final Label totalLabel = new Label("Total: $0.00");

    /**
     * Constructs the expenses screen.
     *
     * @param onBack callback to return to the main menu
     */
    public ExpensesView(Runnable onBack) {
        setSpacing(18);
        setPadding(new Insets(32));
        setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Expenses Calculator");
        title.setStyle(
                "-fx-font-size: 28px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-family: 'Arial';"
        );

        ComboBox<ExpenseCategory> categoryDropdown = new ComboBox<>();
        categoryDropdown.getItems().addAll(ExpenseCategory.values());
        categoryDropdown.setPromptText("Select expense category");

        TextField descriptionInput = new TextField();
        descriptionInput.setPromptText("Expense description (optional)");

        TextField amountInput = new TextField();
        amountInput.setPromptText("Amount");
        amountInput.setPrefColumnCount(12);
        amountInput.setPrefWidth(200);
        amountInput.setMaxWidth(200);
        HBox.setHgrow(amountInput, Priority.NEVER);

        UnaryOperator<TextFormatter.Change> amountFilter = change -> {
            String newText = change.getControlNewText();

            if (newText.isEmpty()) {
                return change;
            }

            if (!newText.matches("\\d*(\\.\\d*)?")) {
                return null;
            }

            int dot = newText.indexOf('.');
            if (dot >= 0 && newText.length() - dot - 1 > 2) {
                return null;
            }

            return change;
        };

        amountInput.setTextFormatter(new TextFormatter<>(amountFilter));

        DatePicker dueDatePicker = new DatePicker();
        dueDatePicker.setPromptText("Due date (optional)");

        Button addBtn = new Button("Add expense");
        addBtn.setMinWidth(140);
        addBtn.setPrefWidth(140);

        addBtn.disableProperty().bind(
                categoryDropdown.getSelectionModel()
                        .selectedItemProperty()
                        .isNull()
                        .or(Bindings.createBooleanBinding(
                                () -> {
                                    String text = amountInput.getText();

                                    return text == null
                                            || text.isBlank()
                                            || !text.matches("\\d+(\\.\\d{1,2})?");
                                },
                                amountInput.textProperty()
                        ))
        );

        addBtn.setStyle(
                "-fx-background-color: #4CAF50; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold;"
        );

        addBtn.setOnAction(event -> {
            String description = descriptionInput.getText();
            String amountText = amountInput.getText().trim();

            try {
                double amount = Double.parseDouble(amountText);

                Expense expense = new Expense(
                        categoryDropdown.getValue(),
                        description,
                        amount,
                        dueDatePicker.getValue()
                );

                expenses.add(expense);
                updateTotal();

                descriptionInput.clear();
                amountInput.clear();
                dueDatePicker.setValue(null);
                categoryDropdown.getSelectionModel().clearSelection();
                categoryDropdown.requestFocus();

            } catch (IllegalArgumentException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Expense");
                alert.setHeaderText(null);
                alert.setContentText(ex.getMessage());
                alert.showAndWait();

                amountInput.requestFocus();
                amountInput.selectAll();
            }
        });

        HBox amountRow = new HBox(10, amountInput, addBtn);
        amountRow.setAlignment(Pos.CENTER_LEFT);
        amountRow.setFillHeight(false);

        VBox form = new VBox(
                12,
                categoryDropdown,
                descriptionInput,
                amountRow,
                dueDatePicker
        );

        form.setAlignment(Pos.CENTER_LEFT);
        form.setMaxWidth(520);

        TableView<Expense> table = new TableView<>(expenses);
        table.setPrefHeight(280);
        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS
        );

        TableColumn<Expense, String> categoryColumn =
                new TableColumn<>("Category");

        categoryColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getCategory().toString()
                )
        );

        TableColumn<Expense, String> descriptionColumn =
                new TableColumn<>("Description");

        descriptionColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getDescription() == null
                                ? ""
                                : cell.getValue().getDescription()
                )
        );

        TableColumn<Expense, String> amountColumn =
                new TableColumn<>("Amount");

        amountColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        String.format(
                                Locale.US,
                                "$%.2f",
                                cell.getValue().getAmount()
                        )
                )
        );

        TableColumn<Expense, String> dueDateColumn =
                new TableColumn<>("Due Date");

        dueDateColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getDueDate() == null
                                ? ""
                                : cell.getValue().getDueDate().toString()
                )
        );

        table.getColumns().addAll(
                List.of(
                        categoryColumn,
                        descriptionColumn,
                        amountColumn,
                        dueDateColumn
                )
        );

        Button removeBtn = new Button("Remove selected");

        removeBtn.setOnAction(event -> {
            Expense selected =
                    table.getSelectionModel().getSelectedItem();

            if (selected != null) {
                expenses.remove(selected);
                updateTotal();
            }
        });

        Button backBtn = new Button("Back to Main Menu");

        backBtn.setOnAction(event -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        totalLabel.setStyle(
                "-fx-font-size: 16px; -fx-font-weight: bold;"
        );

        HBox actions = new HBox(
                12,
                removeBtn,
                totalLabel,
                backBtn
        );

        actions.setAlignment(Pos.CENTER_LEFT);

        VBox content = new VBox(16, form, table, actions);
        content.setAlignment(Pos.TOP_CENTER);

        getChildren().addAll(title, content);
    }

    /** Updates the displayed total whenever the expense list changes. */
    private void updateTotal() {
        NumberFormat currencyFormat =
                NumberFormat.getCurrencyInstance(Locale.US);

        double total = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        totalLabel.setText(
                "Total: " + currencyFormat.format(total)
        );
    }
}
