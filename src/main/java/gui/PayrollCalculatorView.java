package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.FilingStatus;
import model.PaySchedule;
import model.PayrollCalculator;
import model.PayrollInputs;
import model.PayrollResult;
import model.PayrollTaxInputs;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;
import java.util.function.UnaryOperator;

/**
 * Payroll calculator screen.
 *
 * <p>Supports hourly and salaried payroll calculations with federal
 * income tax withholding based on a modern Form W-4 and the bundled
 * withholding rules. The pay date determines which supported
 * withholding year is used.</p>
 */
public final class PayrollCalculatorView extends VBox {

    /**
     * Constructs the payroll calculator screen.
     *
     * @param onBack callback used to return to the main menu
     */
    public PayrollCalculatorView(Runnable onBack) {
        setSpacing(24);
        setPadding(new Insets(40));
        setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Payroll Calculator");
        title.getStyleClass().add("app-title");

        // --- Pay type ---

        RadioButton hourlyRadio = new RadioButton("Hourly Pay");
        RadioButton annualRadio = new RadioButton("Annual Pay");

        ToggleGroup payTypeGroup = new ToggleGroup();
        hourlyRadio.setToggleGroup(payTypeGroup);
        annualRadio.setToggleGroup(payTypeGroup);
        hourlyRadio.setSelected(true);

        HBox payTypeRow =
                new HBox(28, hourlyRadio, annualRadio);
        payTypeRow.setAlignment(Pos.CENTER);

        // --- Pay schedule ---

        ComboBox<PaySchedule> scheduleDropdown = new ComboBox<>();
        scheduleDropdown.getItems().addAll(PaySchedule.values());
        scheduleDropdown.setValue(PaySchedule.BI_WEEKLY);
        scheduleDropdown.setPrefWidth(280);

        scheduleDropdown.setConverter(
                new javafx.util.StringConverter<>() {
                    @Override
                    public String toString(PaySchedule schedule) {
                        return schedule == null
                                ? ""
                                : schedule.displayName();
                    }

                    @Override
                    public PaySchedule fromString(String value) {
                        return null;
                    }
                }
        );

        HBox scheduleRow =
                new HBox(
                        16,
                        new Label("Pay schedule:"),
                        scheduleDropdown
                );
        scheduleRow.setAlignment(Pos.CENTER);

        // --- Compensation inputs ---

        TextField hourlyRate = moneyField("Hourly rate");
        TextField hoursInPeriod = moneyField(
                "Hours this pay period"
        );
        TextField annualSalaryField = moneyField(
                "Annual salary"
        );

        HBox hourlyInputsRow =
                new HBox(16, hourlyRate, hoursInPeriod);
        hourlyInputsRow.setAlignment(Pos.CENTER);

        HBox annualInputRow =
                new HBox(16, annualSalaryField);
        annualInputRow.setAlignment(Pos.CENTER);

        // --- Payroll / Form W-4 inputs ---

        Label taxHeader =
                new Label("Federal Withholding Information");
        taxHeader.getStyleClass().add("section-title");

        DatePicker payDate = new DatePicker(LocalDate.now());
        payDate.setPrefWidth(280);

        ComboBox<FilingStatus> filingStatus =
                new ComboBox<>();

        filingStatus.getItems().addAll(
                FilingStatus.SINGLE,
                FilingStatus.MARRIED_FILING_JOINTLY,
                FilingStatus.MARRIED_FILING_SEPARATELY,
                FilingStatus.HEAD_OF_HOUSEHOLD,
                FilingStatus.QUALIFYING_SURVIVING_SPOUSE
        );
        filingStatus.setValue(FilingStatus.SINGLE);
        filingStatus.setPrefWidth(280);

        CheckBox step2Checked = new CheckBox(
                "W-4 Step 2: Multiple jobs / spouse works"
        );

        TextField step3Credits =
                moneyField("W-4 Step 3 credits");

        TextField step4aOtherIncome =
                moneyField("W-4 Step 4(a) other income");

        TextField step4bDeductions =
                moneyField("W-4 Step 4(b) deductions");

        TextField step4cAdditional =
                moneyField(
                        "W-4 Step 4(c) additional withholding"
                );

        CheckBox exemptFederal = new CheckBox(
                "Exempt from federal income tax withholding"
        );

        TextField otherDeductions =
                moneyField("Other deductions this pay period");

        TextField yearToDateWages =
                moneyField("YTD wages before this paycheck");

        GridPane taxGrid = new GridPane();
        taxGrid.setHgap(20);
        taxGrid.setVgap(14);
        taxGrid.setAlignment(Pos.CENTER);

        taxGrid.add(new Label("Pay date:"), 0, 0);
        taxGrid.add(payDate, 1, 0);

        taxGrid.add(new Label("W-4 filing status:"), 0, 1);
        taxGrid.add(filingStatus, 1, 1);

        taxGrid.add(step2Checked, 0, 2, 2, 1);

        taxGrid.add(new Label("Step 3 credits:"), 0, 3);
        taxGrid.add(step3Credits, 1, 3);

        taxGrid.add(
                new Label("Step 4(a) other income:"),
                0,
                4
        );
        taxGrid.add(step4aOtherIncome, 1, 4);

        taxGrid.add(
                new Label("Step 4(b) deductions:"),
                0,
                5
        );
        taxGrid.add(step4bDeductions, 1, 5);

        taxGrid.add(
                new Label(
                        "Step 4(c) additional withholding:"
                ),
                0,
                6
        );
        taxGrid.add(step4cAdditional, 1, 6);

        taxGrid.add(exemptFederal, 0, 7, 2, 1);

        taxGrid.add(
                new Label("Other deductions:"),
                0,
                8
        );
        taxGrid.add(otherDeductions, 1, 8);

        taxGrid.add(
                new Label(
                        "YTD wages before this paycheck:"
                ),
                0,
                9
        );
        taxGrid.add(yearToDateWages, 1, 9);

        VBox taxBox =
                new VBox(18, taxHeader, taxGrid);
        taxBox.setAlignment(Pos.CENTER);
        taxBox.setPadding(new Insets(24));
        taxBox.setMaxWidth(720);
        taxBox.getStyleClass().add("card");

        // --- Actions ---

        Button calcBtn = new Button("Calculate");
        calcBtn.setPrefWidth(280);
        calcBtn.getStyleClass().add("action-button");

        Button backBtn = new Button("Back to Main Menu");
        backBtn.setPrefWidth(280);
        backBtn.getStyleClass().add("secondary-button");

        backBtn.setOnAction(event -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        // --- Results ---

        Label resultsHeader = new Label("Results");
        resultsHeader.getStyleClass().add("section-title");

        Label rHourly = new Label("Hourly Pay: —");
        Label rAnnual = new Label("Annual Pay (Gross): —");
        Label rSched = new Label("Pay Schedule: —");
        Label rHours = new Label("Hours in Period: —");
        Label rGross = new Label("Gross Pay: —");
        Label rFed = new Label("Federal Withholding: —");
        Label rFica =
                new Label("FICA (SS + Medicare): —");
        Label rOther =
                new Label("Other Deductions: —");
        Label rNet = new Label("Net Pay: —");
        rNet.getStyleClass().add("result-total");

        VBox resultsBox = new VBox(
                8,
                resultsHeader,
                rHourly,
                rAnnual,
                rSched,
                rHours,
                rGross,
                new Label("Deductions:"),
                rFed,
                rFica,
                rOther,
                rNet
        );
        resultsBox.setPadding(new Insets(24));
        resultsBox.setMaxWidth(720);
        resultsBox.getStyleClass().add("result-card");

        // --- Pay-type visibility ---

        Runnable updateVisibility = () -> {
            boolean hourly = hourlyRadio.isSelected();

            hourlyInputsRow.setVisible(hourly);
            hourlyInputsRow.setManaged(hourly);

            annualInputRow.setVisible(!hourly);
            annualInputRow.setManaged(!hourly);
        };

        Runnable resetCompensationFields = () -> {
            hourlyRate.clear();
            hoursInPeriod.clear();
            annualSalaryField.clear();

            clearResults(
                    rHourly,
                    rAnnual,
                    rSched,
                    rHours,
                    rGross,
                    rFed,
                    rFica,
                    rOther,
                    rNet
            );
        };

        updateVisibility.run();

        hourlyRadio.selectedProperty().addListener(
                (observable, oldValue, newValue) -> {
                    updateVisibility.run();
                    resetCompensationFields.run();
                }
        );

        annualRadio.selectedProperty().addListener(
                (observable, oldValue, newValue) -> {
                    updateVisibility.run();
                    resetCompensationFields.run();
                }
        );

        // --- Calculate ---

        calcBtn.setOnAction(event -> {
            try {
                PaySchedule schedule =
                        scheduleDropdown.getValue();

                PayrollTaxInputs taxInputs =
                        new PayrollTaxInputs(
                                payDate.getValue(),
                                filingStatus.getValue(),
                                step2Checked.isSelected(),
                                amount(step3Credits),
                                amount(step4aOtherIncome),
                                amount(step4bDeductions),
                                amount(step4cAdditional),
                                exemptFederal.isSelected(),
                                amount(otherDeductions),
                                amount(yearToDateWages)
                        );

                PayrollResult result;

                if (hourlyRadio.isSelected()) {
                    if (hourlyRate.getText().isBlank()
                            || hoursInPeriod.getText().isBlank()) {
                        throw new IllegalArgumentException(
                                "Hourly rate and hours worked "
                                        + "are required."
                        );
                    }

                    PayrollInputs payrollInputs =
                            new PayrollInputs(
                                    amount(hourlyRate),
                                    amount(hoursInPeriod),
                                    schedule
                            );

                    result =
                            PayrollCalculator.calculateFromHourly(
                                    payrollInputs,
                                    taxInputs
                            );
                } else {
                    if (annualSalaryField.getText().isBlank()) {
                        throw new IllegalArgumentException(
                                "Annual salary is required."
                        );
                    }

                    result =
                            PayrollCalculator.calculateFromAnnual(
                                    amount(annualSalaryField),
                                    schedule,
                                    taxInputs
                            );
                }

                displayResult(
                        result,
                        rHourly,
                        rAnnual,
                        rSched,
                        rHours,
                        rGross,
                        rFed,
                        rFica,
                        rOther,
                        rNet
                );
            } catch (IllegalArgumentException exception) {
                showInputError(exception.getMessage());
            }
        });

        VBox page = new VBox(
                24,
                title,
                payTypeRow,
                scheduleRow,
                hourlyInputsRow,
                annualInputRow,
                taxBox,
                calcBtn,
                resultsBox,
                backBtn
        );
        page.setAlignment(Pos.TOP_CENTER);
        page.setPadding(new Insets(0, 20, 30, 20));

        ScrollPane scrollPane = new ScrollPane(page);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);

        getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
    }

    /**
     * Creates a currency-entry field that accepts up to two
     * decimal places.
     *
     * @param prompt prompt displayed when the field is empty
     * @return configured currency-entry field
     */
    private static TextField moneyField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefWidth(280);

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getControlNewText();

            if (text.isEmpty()) {
                return change;
            }

            if (!text.matches("\\d*(\\.\\d*)?")) {
                return null;
            }

            int decimal = text.indexOf('.');

            if (decimal >= 0
                    && text.length() - decimal - 1 > 2) {
                return null;
            }

            return change;
        };

        field.setTextFormatter(new TextFormatter<>(filter));

        return field;
    }

    /**
     * Parses a monetary field, treating an empty field as zero.
     *
     * @param field field to parse
     * @return parsed monetary amount
     */
    private static double amount(TextField field) {
        String value = field.getText().trim();

        if (value.isEmpty()) {
            return 0;
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Please enter valid numeric values."
            );
        }
    }

    /**
     * Displays a completed payroll calculation.
     */
    private static void displayResult(
            PayrollResult result,
            Label rHourly,
            Label rAnnual,
            Label rSched,
            Label rHours,
            Label rGross,
            Label rFed,
            Label rFica,
            Label rOther,
            Label rNet
    ) {
        NumberFormat currency =
                NumberFormat.getCurrencyInstance(Locale.US);

        rHourly.setText(
                "Hourly Pay: "
                        + currency.format(result.hourlyRate())
        );

        rAnnual.setText(
                "Annual Pay (Gross): "
                        + currency.format(result.annualPay())
        );

        rSched.setText(
                "Pay Schedule: "
                        + result.schedule().displayName()
        );

        rHours.setText(
                "Hours in Period: "
                        + formatHours(result.hoursInPeriod())
        );

        rGross.setText(
                "Gross Pay: "
                        + currency.format(result.gross())
        );

        rFed.setText(
                "Federal Withholding: "
                        + currency.format(result.federal())
        );

        rFica.setText(
                "FICA (SS + Medicare): "
                        + currency.format(result.fica())
        );

        rOther.setText(
                "Other Deductions: "
                        + currency.format(result.other())
        );

        rNet.setText(
                "Net Pay: "
                        + currency.format(result.net())
                        + " "
                        + result.schedule().displayName()
        );
    }

    /**
     * Formats hours without unnecessary trailing decimal places.
     *
     * @param hours number of hours to format
     * @return formatted hours
     */
    private static String formatHours(double hours) {
        if (hours % 1 == 0) {
            return String.format("%.0f", hours);
        }

        return String.format("%.2f", hours);
    }

    /**
     * Restores result labels to their initial empty state.
     *
     * @param labels result labels in display order
     */
    private static void clearResults(Label... labels) {
        String[] captions = {
                "Hourly Pay: —",
                "Annual Pay (Gross): —",
                "Pay Schedule: —",
                "Hours in Period: —",
                "Gross Pay: —",
                "Federal Withholding: —",
                "FICA (SS + Medicare): —",
                "Other Deductions: —",
                "Net Pay: —"
        };

        for (int i = 0; i < labels.length; i++) {
            labels[i].setText(captions[i]);
        }
    }

    /**
     * Displays a validation error for invalid payroll input.
     *
     * @param message error message to display
     */
    private static void showInputError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText("Unable to Calculate Payroll");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays the payroll calculator on the supplied stage.
     *
     * @param stage application stage
     * @param onBack action invoked when returning to the main menu
     */
    public static void show(Stage stage, Runnable onBack) {
        PayrollCalculatorView view =
                new PayrollCalculatorView(onBack);

        Scene scene = new Scene(view, 1050, 820);
        scene.getStylesheets().add(WelcomeStage.STYLESHEET);

        stage.setScene(scene);
        stage.show();
    }
}
