package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Application entry point and navigation controller for the JavaFX UI.
 *
 * <p>Provides the main menu and routes the user to the Payroll, Tax,
 * and Expenses calculators. Feature views remain responsible for their
 * own UI and data-entry behavior.</p>
 */
public class WelcomeStage extends Application {

    /** Primary application window reused when navigating between views. */
    private Stage primaryStage;

    /**
     * Initializes the application window and displays the main menu.
     *
     * @param stage primary JavaFX application stage
     */
    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        primaryStage.setTitle("Accounting App");
        showMenuScene();
        primaryStage.show();
    }

    /** Displays the main menu used to navigate between calculators. */
    private void showMenuScene() {
        Label menuLabel = new Label("Main Menu");
        menuLabel.setStyle(
                "-fx-font-size: 36px; " +
                "-fx-font-weight: bold; " +
                "-fx-font-family: 'Arial';"
        );

        Button payrollButton = new Button("Payroll Calculator");
        payrollButton.setOnAction(event -> showPayrollScene());

        Button taxCalculatorButton = new Button("Tax Calculator");
        taxCalculatorButton.setOnAction(
                event -> showTaxCalculatorScene()
        );

        Button expensesButton = new Button("Expenses Calculator");
        expensesButton.setOnAction(event -> showExpensesScene());

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(event -> exitApplication());

        VBox menuLayout = new VBox(
                20,
                menuLabel,
                payrollButton,
                taxCalculatorButton,
                expensesButton,
                exitButton
        );
        menuLayout.setPadding(new Insets(20));
        menuLayout.setStyle("-fx-alignment: center;");

        primaryStage.setScene(
                new Scene(menuLayout, 800, 600)
        );
    }

    /** Navigates to the Payroll calculator. */
    private void showPayrollScene() {
        PayrollCalculatorView.show(
                primaryStage,
                this::showMenuScene
        );
    }

    /** Navigates to the Tax calculator. */
    private void showTaxCalculatorScene() {
        TaxCalculatorView.show(
                primaryStage,
                this::showMenuScene
        );
    }

    /** Navigates to the Expenses calculator. */
    private void showExpensesScene() {
        ExpensesView view =
                new ExpensesView(this::showMenuScene);

        primaryStage.setScene(
                new Scene(view, 800, 600)
        );
    }

    /** Closes the JavaFX application. */
    private void exitApplication() {
        Platform.exit();
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args command-line arguments supplied to the application
     */
    public static void main(String[] args) {
        launch(args);
    }
}
