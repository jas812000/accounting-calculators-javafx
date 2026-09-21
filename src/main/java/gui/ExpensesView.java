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
 * <p>Allows users to add categorized expense records into an in-memory
 * table, remove items, and view the current total. This view focuses on
 * input validation and consistent formatting rather than persistence.</p>
 */
public class ExpensesView extends VBox {

    /** In-memory expense list backing the table. */
    private final ObservableList<Expense> expenses =
            FXCollections.observableArrayList();

    /** Label showing the current total for the session. */
    private final Label totalLabel =
            new Label("Total: $0.00");

    /**
     * Constructs the expenses screen.
     *
     * @param onBack callback used to return to the main menu
     */
    public ExpensesView(Runnable onBack) {
        setSpacing(24);
        setPadding(new Insets(40));
        setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Expenses Calculator");
        title.getStyleClass().add("app-title");

        // --- Expense entry form ---

        ComboBox<ExpenseCategory> categoryDropdown =
                new ComboBox<>();
        categoryDropdown.getItems().addAll(
                ExpenseCategory.values()
        );
        categoryDropdown.setPromptText(
                "Select expense category"
        );
        categoryDropdown.setPrefWidth(320);

        TextField descriptionInput = new TextField();
        descriptionInput.setPromptText(
                "Expense description (optional)"
        );
        descriptionInput.setPrefWidth(520);

        TextField amountInput = new TextField();
        amountInput.setPromptText("Amount");
        amountInput.setPrefColumnCount(12);
        amountInput.setPrefWidth(280);
        amountInput.setMaxWidth(280);
        HBox.setHgrow(amountInput, Priority.NEVER);

        UnaryOperator<TextFormatter.Change> amountFilter =
                change -> {
                    String newText =
                            change.getControlNewText();

                    if (newText.isEmpty()) {
                        return change;
                    }

                    if (!newText.matches("\\d*(\\.\\d*)?")) {
                        return null;
                    }

                    int dot = newText.indexOf('.');

                    if (dot >= 0
                            && newText.length() - dot - 1 > 2) {
                        return null;
                    }

                    return change;
                };

        amountInput.setTextFormatter(
                new TextFormatter<>(amountFilter)
        );

        DatePicker dueDatePicker = new DatePicker();
        dueDatePicker.setPromptText("Due date (optional)");
        dueDatePicker.setPrefWidth(280);

        Button addBtn = new Button("Add Expense");
        addBtn.setMinWidth(180);
        addBtn.setPrefWidth(180);
        addBtn.getStyleClass().add("action-button");

        addBtn.disableProperty().bind(
                categoryDropdown.getSelectionModel()
                        .selectedItemProperty()
                        .isNull()
                        .or(
                                Bindings.createBooleanBinding(
                                        () -> {
                                            String text =
                                                    amountInput.getText();

                                            return text == null
                                                    || text.isBlank()
                                                    || !text.matches(
                                                    "\\d+(\\.\\d{1,2})?"
                                            );
                                        },
                                        amountInput.textProperty()
                                )
                        )
        );

        addBtn.setOnAction(event -> {
            String description =
                    descriptionInput.getText();

            String amountText =
                    amountInput.getText().trim();

            try {
                double amount =
                        Double.parseDouble(amountText);

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

                categoryDropdown
                        .getSelectionModel()
                        .clearSelection();

                categoryDropdown.requestFocus();
            } catch (IllegalArgumentException exception) {
                Alert alert =
                        new Alert(Alert.AlertType.ERROR);

                alert.setTitle("Invalid Expense");
                alert.setHeaderText(null);
                alert.setContentText(
                        exception.getMessage()
                );
                alert.showAndWait();

                amountInput.requestFocus();
                amountInput.selectAll();
            }
        });

        HBox amountRow =
                new HBox(14, amountInput, addBtn);
        amountRow.setAlignment(Pos.CENTER_LEFT);
        amountRow.setFillHeight(false);

        VBox form = new VBox(
                14,
                categoryDropdown,
                descriptionInput,
                amountRow,
                dueDatePicker
        );
        form.setAlignment(Pos.CENTER_LEFT);
        form.setMaxWidth(700);
        form.setPadding(new Insets(24));
        form.getStyleClass().add("card");

        // --- Expense table ---

        TableView<Expense> table =
                new TableView<>(expenses);

        table.setPrefHeight(340);
        table.setMaxWidth(900);

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS
        );

        TableColumn<Expense, String> categoryColumn =
                new TableColumn<>("Category");

        categoryColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue()
                                .getCategory()
                                .toString()
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
                                : cell.getValue()
                                .getDueDate()
                                .toString()
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

        // --- Actions and total ---

        Button removeBtn =
                new Button("Remove Selected");
        removeBtn.getStyleClass().add("secondary-button");

        removeBtn.setOnAction(event -> {
            Expense selected =
                    table.getSelectionModel()
                            .getSelectedItem();

            if (selected != null) {
                expenses.remove(selected);
                updateTotal();
            }
        });

        Button backBtn =
                new Button("Back to Main Menu");
        backBtn.getStyleClass().add("secondary-button");

        backBtn.setOnAction(event -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        totalLabel.getStyleClass().add("result-total");

        HBox actions = new HBox(
                16,
                removeBtn,
                totalLabel,
                backBtn
        );
        actions.setAlignment(Pos.CENTER_LEFT);

        VBox content = new VBox(
                22,
                form,
                table,
                actions
        );
        content.setAlignment(Pos.TOP_CENTER);
        content.setMaxWidth(900);

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
