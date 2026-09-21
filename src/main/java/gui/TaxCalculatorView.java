package gui;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.FilingStatus;
import model.TaxCalculator;
import model.TaxInputs;
import model.TaxResult;
import repository.TaxTableRepository;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.UnaryOperator;

/**
 * Annual federal income tax calculator screen.
 *
 * Collects a supported tax year, filing status, and taxable income,
 * then calculates federal income tax using the corresponding
 * progressive tax schedule.
 */
public final class TaxCalculatorView extends VBox {

    public TaxCalculatorView(Runnable onBack) {
        setSpacing(18);
        setPadding(new Insets(32));
        setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Federal Income Tax Calculator");
        title.setStyle(
                "-fx-font-size: 28px; " +
                "-fx-font-weight: bold; " +
                "-fx-font-family: 'Arial';"
        );

        TaxTableRepository taxTableRepository =
                new TaxTableRepository();

        ComboBox<Integer> yearDropdown = new ComboBox<>();
        yearDropdown.getItems().addAll(
                taxTableRepository.getSupportedYears()
        );
        yearDropdown.setPromptText("Select tax year");

        ComboBox<FilingStatus> filingStatusDropdown = new ComboBox<>();
        filingStatusDropdown.getItems().addAll(FilingStatus.values());
        filingStatusDropdown.setPromptText("Select filing status");

        TextField amountInput = new TextField();
        amountInput.setPromptText("Taxable Income");

        UnaryOperator<TextFormatter.Change> amountFilter = change -> {
            String text = change.getControlNewText();

            if (text.isEmpty()) {
                return change;
            }

            if (!text.matches("\\d*(\\.\\d*)?")) {
                return null;
            }

            int decimalPoint = text.indexOf('.');

            if (decimalPoint >= 0
                    && text.length() - decimalPoint - 1 > 2) {
                return null;
            }

            return change;
        };

        amountInput.setTextFormatter(new TextFormatter<>(amountFilter));

        Button calculateButton = new Button("Calculate");

        BooleanBinding amountInvalid = Bindings.createBooleanBinding(
                () -> {
                    String text = amountInput.getText();

                    return text == null
                            || text.isBlank()
                            || !text.matches("\\d+(\\.\\d{1,2})?");
                },
                amountInput.textProperty()
        );

        calculateButton.disableProperty().bind(
                yearDropdown.getSelectionModel()
                        .selectedItemProperty()
                        .isNull()
                        .or(
                                filingStatusDropdown.getSelectionModel()
                                        .selectedItemProperty()
                                        .isNull()
                        )
                        .or(amountInvalid)
        );

        Label result = new Label();
        result.setWrapText(true);

        calculateButton.setOnAction(event -> {
            int year = yearDropdown.getValue();
            FilingStatus status = filingStatusDropdown.getValue();
            double taxableIncome =
                    Double.parseDouble(amountInput.getText().trim());

            TaxInputs inputs =
                    new TaxInputs(year, status, taxableIncome);

            TaxResult output = TaxCalculator.calculate(inputs);

            NumberFormat currency =
                    NumberFormat.getCurrencyInstance(Locale.US);

            result.setText(
                    "Year: " + output.year() + "\n" +
                    "Filing Status: "
                            + output.status().displayName() + "\n" +
                    "Taxable Income: "
                            + currency.format(output.taxableIncome()) + "\n" +
                    "Federal Income Tax: "
                            + currency.format(output.federalTax())
            );
        });

        Button backButton = new Button("Back to Main Menu");
        backButton.setOnAction(event -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        double controlWidth = 240;

        yearDropdown.setMaxWidth(controlWidth);
        filingStatusDropdown.setMaxWidth(controlWidth);
        amountInput.setMaxWidth(controlWidth);
        calculateButton.setMaxWidth(controlWidth);
        backButton.setMaxWidth(controlWidth);

        VBox content = new VBox(
                12,
                yearDropdown,
                filingStatusDropdown,
                amountInput,
                calculateButton,
                backButton,
                result
        );

        content.setAlignment(Pos.CENTER);

        getChildren().addAll(title, content);
    }

    /**
     * Displays the tax calculator on the supplied stage.
     *
     * @param stage application stage
     * @param onBack action invoked when returning to the main menu
     */
    public static void show(Stage stage, Runnable onBack) {
        TaxCalculatorView view = new TaxCalculatorView(onBack);

        stage.setScene(new Scene(view, 800, 600));
        stage.show();
    }
}
