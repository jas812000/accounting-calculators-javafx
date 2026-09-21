package gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * Final application screen displayed when the user chooses to exit.
 *
 * <p>Provides a friendly closing message while allowing the user either
 * to return to the main menu or close the application.</p>
 */
public final class ExitView extends VBox {

    /**
     * Constructs the exit screen.
     *
     * @param onBack callback used to return to the main menu
     */
    public ExitView(Runnable onBack) {
        setAlignment(Pos.CENTER);
        setPadding(new Insets(50));

        Label titleLabel =
                new Label("Thank you for using\nAccounting Calculators");
        titleLabel.getStyleClass().add("exit-title");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(520);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setTextAlignment(TextAlignment.CENTER);

        Label messageLabel =
                new Label("We hope to see you again soon.");
        messageLabel.getStyleClass().add("app-subtitle");

        Button backButton = new Button("Back to Main Menu");
        backButton.setPrefSize(260, 48);
        backButton.getStyleClass().add("secondary-button");
        backButton.setOnAction(event -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        Button closeButton = new Button("Close Application");
        closeButton.setPrefSize(260, 48);
        closeButton.getStyleClass().add("exit-button");
        closeButton.setOnAction(event -> Platform.exit());

        VBox card = new VBox(
                22,
                titleLabel,
                messageLabel,
                backButton,
                closeButton
        );
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(620);
        card.setPadding(new Insets(48));
        card.getStyleClass().add("card");

        VBox.setMargin(
                backButton,
                new Insets(18, 0, 0, 0)
        );

        getChildren().add(card);
    }

    /**
     * Displays the exit screen on the supplied application stage.
     *
     * @param stage primary application stage
     * @param onBack callback used to return to the main menu
     */
    public static void show(Stage stage, Runnable onBack) {
        ExitView view = new ExitView(onBack);

        Scene scene = new Scene(view, 900, 650);
        scene.getStylesheets().add(WelcomeStage.STYLESHEET);

        stage.setScene(scene);
        stage.show();
    }
}
