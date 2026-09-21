package gui;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Application entry point and navigation controller for the JavaFX UI.
 *
 * Responsibilities:
 * - Show a short welcome screen
 * - Present a main menu
 * - Navigate to feature views (Payroll, Tax, Expenses)
 * - Display an exit screen
 *
 * Note: This class intentionally keeps routing logic in one place
 * so feature views remain focused on UI + data entry.
 */
public class WelcomeStage extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        // Initial stage setup.
        Scene scene = new Scene(new StackPane(), 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Accounting App");
        primaryStage.show();

        showWelcomeScene();
    }

    /** Shows a timed welcome screen before transitioning to the main menu. */
    private void showWelcomeScene() {
        Label welcomeLabel = new Label("Welcome to the Accounting App!!!");
        welcomeLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-font-family: 'Arial';");

        StackPane welcomePane = new StackPane(welcomeLabel);
        Scene welcomeScene = new Scene(welcomePane, 800, 600);
        primaryStage.setScene(welcomeScene);

        // Delay ensures the user sees the app "boot" state.
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> showMenuScene());
        pause.play();
    }

    /** Main menu screen for routing to calculators. */
    private void showMenuScene() {
        Label menuLabel = new Label("Main Menu");
        menuLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-font-family: 'Arial';");

        Button payrollButton = new Button("Payroll Calculator");
        payrollButton.setOnAction(event -> showPayrollScene());

        Button taxCalculatorButton = new Button("Tax Calculator");
        taxCalculatorButton.setOnAction(event -> showTaxCalculatorScene());

        Button expensesButton = new Button("Expenses Calculator");
        expensesButton.setOnAction(event -> showExpensesScene());

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(event -> showExitScene());

        VBox menuLayout = new VBox(20, menuLabel, payrollButton, taxCalculatorButton, expensesButton, exitButton);
        menuLayout.setPadding(new Insets(20));
        menuLayout.setStyle("-fx-alignment: center;");

        Scene menuScene = new Scene(menuLayout, 800, 600);
        primaryStage.setScene(menuScene);
    }

    /** Navigate to Payroll screen. */
    private void showPayrollScene() {
        PayrollCalculatorView.show(primaryStage, this::showMenuScene);
    }

    /** Navigate to Tax screen. */
    private void showTaxCalculatorScene() {
        TaxCalculatorView.show(primaryStage, this::showMenuScene);
    }

    /** Navigate to Expenses screen. */
    private void showExpensesScene() {
        ExpensesView view = new ExpensesView(this::showMenuScene);

        Scene scene = new Scene(view, 800, 600);
        primaryStage.setScene(scene);
    }

    /** Navigate to Exit screen (auto-terminates). */
    private void showExitScene() {
        ExitView.show(primaryStage);
    }

    /** Launches the JavaFX application. */
    public static void main(String[] args) {
        launch(args);
    }
}
