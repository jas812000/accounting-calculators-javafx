package gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

    /** Shared application stylesheet. */
    public static final String STYLESHEET =
            WelcomeStage.class
                    .getResource("/styles/application.css")
                    .toExternalForm();

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

        primaryStage.setTitle("Accounting Calculators");
        showMenuScene();
        primaryStage.show();
    }

    /** Displays the main menu used to navigate between calculators. */
    private void showMenuScene() {
        Label titleLabel = new Label("Accounting Calculators");
        titleLabel.getStyleClass().add("app-title");

        Label subtitleLabel =
                new Label("Payroll, Tax & Expense Tools");
        subtitleLabel.getStyleClass().add("app-subtitle");

        Button payrollButton =
                createMenuButton("Payroll Calculator", "primary-button");
        payrollButton.setOnAction(event -> showPayrollScene());

        Button taxCalculatorButton =
                createMenuButton("Tax Calculator", "primary-button");
        taxCalculatorButton.setOnAction(
                event -> showTaxCalculatorScene()
        );

        Button expensesButton =
                createMenuButton("Expenses Calculator", "primary-button");
        expensesButton.setOnAction(event -> showExpensesScene());

        Button exitButton =
                createMenuButton("Exit", "exit-button");
        exitButton.setOnAction(event -> showExitScene());

        VBox menuCard = new VBox(
                18,
                titleLabel,
                subtitleLabel,
                payrollButton,
                taxCalculatorButton,
                expensesButton,
                exitButton
        );
        menuCard.setAlignment(Pos.CENTER);
        menuCard.setMaxWidth(500);
        menuCard.setPadding(new Insets(42));
        menuCard.getStyleClass().add("card");

        VBox.setMargin(
                payrollButton,
                new Insets(18, 0, 0, 0)
        );

        VBox root = new VBox(menuCard);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));

        Scene scene = new Scene(root, 900, 650);
        scene.getStylesheets().add(STYLESHEET);
        primaryStage.setScene(scene);
    }

    /**
     * Creates a consistently sized main-menu button.
     *
     * @param text text displayed on the button
     * @param styleClass semantic style class for the button
     * @return configured menu button
     */
    private Button createMenuButton(
            String text,
            String styleClass
    ) {
        Button button = new Button(text);
        button.setPrefSize(300, 50);
        button.getStyleClass().add(styleClass);
        return button;
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

        Scene scene = new Scene(view, 1000, 720);
        scene.getStylesheets().add(STYLESHEET);
        primaryStage.setScene(scene);
    }

    /** Navigates to the application exit screen. */
    private void showExitScene() {
        ExitView.show(primaryStage, this::showMenuScene);
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
