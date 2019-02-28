import javafx.application.Application;
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

    @Override
    public void start(Stage stage) throws InterruptedException, IOException {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label label = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        Label label2 = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        Scene scene = new Scene(new StackPane(label, label2), 640, 480);
        stage.setScene(scene);
        stage.show();
        label.setText("as");


        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("org/codingspiderfox/juglylauncher/MainWindow.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("ABC");
        stage.setScene(new Scene(root1));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}