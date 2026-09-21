package gui;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.UnaryOperator;

import model.FilingStatus;
import model.TaxInputs;
import model.TaxResult;
import model.TaxCalculator;

/**
 * Tax calculator screen.
 *
 * Collects tax year, filing status, and gross pay amount, then calculates:
 * - FICA (simplified)
 * - Federal income tax (via year-specific tax table calculator)
 * - Net amount (gross - deductions)
 *
 * This view is UI-only; calculation logic lives in the model layer.
 */
public final class TaxCalculatorView extends VBox {

    public TaxCalculatorView(Runnable onBack) {
        setSpacing(18);
        setPadding(new Insets(32));
        setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Tax Calculator");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-font-family: 'Arial';");

        ComboBox<Integer> yearDropdown = new ComboBox<>();
        yearDropdown.getItems().addAll(2025); // extend as needed
        yearDropdown.setPromptText("Select tax year");

        ComboBox<FilingStatus> filingStatusDropdown = new ComboBox<>();
        filingStatusDropdown.getItems().addAll(
                FilingStatus.SINGLE,
                FilingStatus.MARRIED_FILING_JOINTLY,
                FilingStatus.MARRIED_FILING_SEPARATELY,
                FilingStatus.HEAD_OF_HOUSEHOLD,
                FilingStatus.QUALIFYING_SURVIVING_SPOUSE,
                FilingStatus.ESTATES_AND_TRUSTS
        );
        filingStatusDropdown.setPromptText("Select filing status");

        TextField amountInput = new TextField();
        amountInput.setPromptText("Gross Pay Amount");

        // Allow only numbers with up to 2 decimals.
        UnaryOperator<TextFormatter.Change> amountFilter = change -> {
            String txt = change.getControlNewText();
            if (txt.isEmpty()) return change;
            if (!txt.matches("\\d*(\\.\\d*)?")) return null;
            int dot = txt.indexOf('.');
            if (dot >= 0 && txt.length() - dot - 1 > 2) return null;
            return change;
        };
        amountInput.setTextFormatter(new TextFormatter<>(amountFilter));

        Button calcBtn = new Button("Calculate");

        // Disable until year chosen, filing status chosen, and amount is valid money format.
        BooleanBinding amountInvalid = Bindings.createBooleanBinding(
                () -> {
                    String t = amountInput.getText();
                    return t == null || t.isBlank() || !t.matches("\\d+(\\.\\d{1,2})?");
                },
                amountInput.textProperty()
        );
        calcBtn.disableProperty().bind(
                yearDropdown.getSelectionModel().selectedItemProperty().isNull()
                        .or(filingStatusDropdown.getSelectionModel().selectedItemProperty().isNull())
                        .or(amountInvalid)
        );

        Label result = new Label();
        result.setWrapText(true);

        calcBtn.setOnAction(e -> {
            int year = yearDropdown.getValue();
            FilingStatus status = filingStatusDropdown.getValue();
            double gross = Double.parseDouble(amountInput.getText().trim());

            TaxInputs inputs = new TaxInputs(year, status, gross);
            TaxResult out = TaxCalculator.calculate(inputs);

            NumberFormat cf = NumberFormat.getCurrencyInstance(Locale.US);
            result.setText(
                    "Year: "          + out.year() + "\n" +
                    "Filing Status: " + out.status().displayName() + "\n" +
                    "Gross: "         + cf.format(out.gross()) + "\n" +
                    "FICA: "          + cf.format(out.fica()) + "\n" +
                    "Federal: "       + cf.format(out.federal()) + "\n" +
                    "Net: "           + cf.format(out.net())
            );
        });

        Button backBtn = new Button("Back to Main Menu");
        backBtn.setOnAction(e -> { if (onBack != null) onBack.run(); });

        // Consistent control widths.
        double controlWidth = 240;
        yearDropdown.setMaxWidth(controlWidth);
        filingStatusDropdown.setMaxWidth(controlWidth);
        amountInput.setMaxWidth(controlWidth);
        calcBtn.setMaxWidth(controlWidth);
        backBtn.setMaxWidth(controlWidth);

        VBox content = new VBox(12, yearDropdown, filingStatusDropdown, amountInput, calcBtn, backBtn, result);
        content.setAlignment(Pos.CENTER);

        getChildren().addAll(title, content);
    }

    /** Helper to display this view on a Stage. */
    public static void show(Stage stage, Runnable onBack) {
        TaxCalculatorView view = new TaxCalculatorView(onBack);
        stage.setScene(new Scene(view, 800, 600));
        stage.show();
    }
}
