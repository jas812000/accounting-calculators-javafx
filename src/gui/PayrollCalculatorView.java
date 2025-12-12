package gui;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.UnaryOperator;

/**
 * Payroll calculator screen.
 *
 * Supports two input modes:
 * - Hourly pay (rate + hours in period)
 * - Annual pay (annual salary)
 *
 * Users select a pay schedule (weekly/bi-weekly/monthly) and the system computes
 * gross pay, FICA, and net pay. Federal/other deductions are placeholders for
 * future expansion.
 */
public final class PayrollCalculatorView extends VBox {

    public PayrollCalculatorView(Runnable onBack) {
        setSpacing(18);
        setPadding(new Insets(32));
        setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Payroll Calculator");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-font-family: 'Arial';");

        // --- Pay type selection ---
        RadioButton hourlyRadio = new RadioButton("Hourly Pay");
        RadioButton annualRadio = new RadioButton("Annual Pay");
        ToggleGroup payTypeGroup = new ToggleGroup();
        hourlyRadio.setToggleGroup(payTypeGroup);
        annualRadio.setToggleGroup(payTypeGroup);
        hourlyRadio.setSelected(true);

        HBox payTypeRow = new HBox(16, hourlyRadio, annualRadio);
        payTypeRow.setAlignment(Pos.CENTER);

        // --- Pay schedule selection ---
        RadioButton weekly   = new RadioButton("Weekly");
        RadioButton biWeekly = new RadioButton("Bi-weekly");
        RadioButton monthly  = new RadioButton("Monthly");
        ToggleGroup scheduleGroup = new ToggleGroup();
        weekly.setToggleGroup(scheduleGroup);
        biWeekly.setToggleGroup(scheduleGroup);
        monthly.setToggleGroup(scheduleGroup);
        weekly.setSelected(true);

        HBox scheduleRow = new HBox(16, new Label("Pay schedule:"), weekly, biWeekly, monthly);
        scheduleRow.setAlignment(Pos.CENTER);

        // --- Inputs ---
        TextField hourlyRate = new TextField();
        hourlyRate.setPromptText("Hourly pay");
        TextField hoursInPeriod = new TextField();
        hoursInPeriod.setPromptText("Hours this pay period");
        hourlyRate.setPrefWidth(180);
        hoursInPeriod.setPrefWidth(220);

        TextField annualSalaryField = new TextField();
        annualSalaryField.setPromptText("Annual pay amount");
        annualSalaryField.setPrefWidth(220);

        // Numeric formatter: digits + optional decimal, max 2 decimals.
        UnaryOperator<TextFormatter.Change> moneyFilter = ch -> {
            String s = ch.getControlNewText();
            if (s.isEmpty()) return ch;
            if (!s.matches("\\d*(\\.\\d*)?")) return null;
            int dot = s.indexOf('.');
            if (dot >= 0 && s.length() - dot - 1 > 2) return null;
            return ch;
        };
        hourlyRate.setTextFormatter(new TextFormatter<>(moneyFilter));
        hoursInPeriod.setTextFormatter(new TextFormatter<>(moneyFilter));
        annualSalaryField.setTextFormatter(new TextFormatter<>(moneyFilter));

        HBox hourlyInputsRow = new HBox(12, hourlyRate, hoursInPeriod);
        hourlyInputsRow.setAlignment(Pos.CENTER);

        HBox annualInputRow = new HBox(12, annualSalaryField);
        annualInputRow.setAlignment(Pos.CENTER);

        Button calcBtn = new Button("Calculate");
        Button backBtn = new Button("Back to Main Menu");
        backBtn.setOnAction(e -> { if (onBack != null) onBack.run(); });

        // --- Results ---
        Label resultsHeader = new Label("Results");
        resultsHeader.setStyle("-fx-font-weight: bold;");

        Label rHourly  = new Label("Hourly Pay: —");
        Label rAnnual  = new Label("Annual Pay (Gross): —");
        Label rSched   = new Label("Pay Schedule: —");
        Label rHours   = new Label("Hours in Period: —");
        Label rGross   = new Label("Gross Pay: —");
        Label rFed     = new Label("Federal: —");
        Label rFica    = new Label("FICA (SS + Medicare): —");
        Label rOther   = new Label("Other Deductions: —");
        Label rNet     = new Label("Net Pay: —");

        VBox resultsBox = new VBox(6,
                resultsHeader, rHourly, rAnnual, rSched, rHours, rGross,
                new Label("Deductions:"), rFed, rFica, rOther, rNet
        );
        resultsBox.setPadding(new Insets(12));
        resultsBox.setStyle("""
                -fx-border-color: #ccc;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                -fx-background-color: #fafafa;
                """);
        resultsBox.setMaxWidth(460);

        // Show/hide relevant input row based on pay type.
        Runnable updateVisibility = () -> {
            boolean hourly = hourlyRadio.isSelected();

            scheduleRow.setVisible(true);
            scheduleRow.setManaged(true);

            hourlyInputsRow.setVisible(hourly);
            hourlyInputsRow.setManaged(hourly);

            annualInputRow.setVisible(!hourly);
            annualInputRow.setManaged(!hourly);
        };

        // Reset fields whenever user switches pay type.
        Runnable resetFields = () -> {
            hourlyRate.clear();
            hoursInPeriod.clear();
            annualSalaryField.clear();

            rHourly.setText("Hourly Pay: —");
            rAnnual.setText("Annual Pay (Gross): —");
            rSched.setText("Pay Schedule: —");
            rHours.setText("Hours in Period: —");
            rGross.setText("Gross Pay: —");
            rFed.setText("Federal: —");
            rFica.setText("FICA (SS + Medicare): —");
            rOther.setText("Other Deductions: —");
            rNet.setText("Net Pay: —");
        };

        updateVisibility.run();
        hourlyRadio.selectedProperty().addListener((o, a, b) -> { updateVisibility.run(); resetFields.run(); });
        annualRadio.selectedProperty().addListener((o, a, b) -> { updateVisibility.run(); resetFields.run(); });

        // --- Calculate handler ---
        calcBtn.setOnAction(e -> {
            NumberFormat cf = NumberFormat.getCurrencyInstance(Locale.US);

            // Resolve schedule selection into enum used by the model layer.
            model.PaySchedule schedule = weekly.isSelected() ? model.PaySchedule.WEEKLY
                    : (biWeekly.isSelected() ? model.PaySchedule.BI_WEEKLY : model.PaySchedule.MONTHLY);

            if (hourlyRadio.isSelected()) {
                String rateText  = hourlyRate.getText().trim();
                String hoursText = hoursInPeriod.getText().trim();
                if (rateText.isBlank() || hoursText.isBlank()) return;

                try {
                    double rate  = Double.parseDouble(rateText);
                    double hours = Double.parseDouble(hoursText);

                    model.PayrollInputs inputs = new model.PayrollInputs(rate, hours, schedule);
                    model.PayrollResult result = model.PayrollCalculator.calculateFromHourly(inputs);

                    rHourly.setText("Hourly Pay: " + cf.format(result.hourlyRate()));
                    rAnnual.setText("Annual Pay (Gross): " + cf.format(result.annualPay()));
                    rSched.setText("Pay Schedule: " + result.schedule().displayName());
                    rHours.setText("Hours in Period: " +
                            (result.hoursInPeriod() % 1 == 0
                                    ? String.format("%.0f", result.hoursInPeriod())
                                    : String.format("%.2f", result.hoursInPeriod())));
                    rGross.setText("Gross Pay: " + cf.format(result.gross()));
                    rFed.setText("Federal: —"); // placeholder until withholding logic is implemented
                    rFica.setText("FICA (SS + Medicare): " + cf.format(result.fica()));
                    rOther.setText("Other Deductions: —"); // placeholder for additional deductions
                    rNet.setText("Net Pay: " + cf.format(result.net()) + " " + result.schedule().displayName());

                } catch (NumberFormatException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Input Error");
                    alert.setHeaderText("Invalid Hourly Pay Inputs");
                    alert.setContentText("Please enter valid numeric values for hourly rate and hours worked.");
                    alert.showAndWait();
                }

            } else {
                String salaryText = annualSalaryField.getText().trim();
                if (salaryText.isBlank()) return;

                try {
                    double annualSalary = Double.parseDouble(salaryText);
                    model.PayrollResult result = model.PayrollCalculator.calculateFromAnnual(annualSalary, schedule);

                    rHourly.setText("Hourly Pay: " + cf.format(result.hourlyRate()));
                    rAnnual.setText("Annual Pay (Gross): " + cf.format(result.annualPay()));
                    rSched.setText("Pay Schedule: " + result.schedule().displayName());
                    rHours.setText("Hours in period: " +
                            (result.hoursInPeriod() % 1 == 0
                                    ? String.format("%.0f", result.hoursInPeriod())
                                    : String.format("%.2f", result.hoursInPeriod())));
                    rGross.setText("Gross Pay: " + cf.format(result.gross()));
                    rFed.setText("Federal: —"); // placeholder
                    rFica.setText("FICA (SS + Medicare): " + cf.format(result.fica()));
                    rOther.setText("Other Deductions: —"); // placeholder
                    rNet.setText("Net Pay: " + cf.format(result.net()) + " " + result.schedule().displayName());

                } catch (NumberFormatException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Input Error");
                    alert.setHeaderText("Invalid Annual Pay Input");
                    alert.setContentText("Please enter a valid numeric value for the annual salary.");
                    alert.showAndWait();
                }
            }
        });

        VBox page = new VBox(18, title, payTypeRow, scheduleRow, hourlyInputsRow, annualInputRow, calcBtn, resultsBox, backBtn);
        page.setAlignment(Pos.TOP_CENTER);

        BorderPane root = new BorderPane(page);
        BorderPane.setAlignment(page, Pos.TOP_CENTER);

        getChildren().add(root);
    }

    /** Helper to display this view on a Stage. */
    public static void show(Stage stage, Runnable onBack) {
        PayrollCalculatorView view = new PayrollCalculatorView(onBack);
        stage.setScene(new Scene(view, 800, 600));
        stage.show();
    }
}
