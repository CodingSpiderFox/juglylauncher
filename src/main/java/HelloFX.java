import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class HelloFX extends Application {

    private static Stage primaryStageObj;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStageObj = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/FrmMain.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle("uglylauncher");
        stage.setScene(new Scene(root));
        stage.show();

        stage.setOnCloseRequest(e -> Platform.exit());
    }

    public static void main(String[] args) {
        launch();
    }


    public static Stage getPrimaryStage() {
        return primaryStageObj;
    }

}