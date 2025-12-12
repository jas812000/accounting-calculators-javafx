package gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Exit screen view for the application.
 *
 * Displays a short "goodbye" message and then terminates the JavaFX runtime
 * and JVM after a small delay. This provides a clear user-facing exit state
 * instead of instantly closing the window.
 */
public final class ExitView extends StackPane {

    /** Constructs the exit UI with centered, styled text. */
    public ExitView() {
        Label exitLabel = new Label("Thank you for using the Accounting App.\nGood Bye!!!!");
        exitLabel.setStyle(
                "-fx-font-size: 32px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-alignment: center; " +
                "-fx-font-family: 'Arial';"
        );

        setAlignment(Pos.CENTER);
        getChildren().add(exitLabel);
    }

    /**
     * Shows this exit view on the provided Stage and then exits after 3 seconds.
     *
     * @param stage the primary application Stage to reuse
     */
    public static void show(Stage stage) {
        ExitView view = new ExitView();
        stage.setScene(new Scene(view, 800, 600));
        stage.show();

        // Delay termination so the user sees a final state.
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> {
            System.out.println("Application exited.");
            Platform.exit();   // cleanly exit JavaFX
            System.exit(0);    // ensure JVM shuts down
        });
        pause.play();
    }
}
