package checkers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Standard template to launch the app.
 *
 * See BoardController for the main code.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("checkers.fxml"));
        primaryStage.setTitle("Checkers");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public Main() {
    }

    public static void main(String[] args) {
        launch(args);
    }
}
