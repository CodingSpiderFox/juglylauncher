package org.codingspiderfox.juglylauncher;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    private Stage primaryStage;

    @FXML
    public Button btnSave;

    @FXML
    public Button btnCancel;

    @FXML
    public Menu mnAccountManagement;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Button btn = new Button();
        btn.setText("Account management");
        btn.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {
                handleAccountManagementAction(event);
            }
        });
        mnAccountManagement.setGraphic(btn);
        mnAccountManagement.setText("");

    }

    public void handleSaveButton(ActionEvent actionEvent) {
    }

    @FXML
    public void handleCancelButton(ActionEvent actionEvent) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public void handleQuitAction(ActionEvent actionEvent) {
        Platform.exit();
    }

    @FXML
    public void handleAccountManagementAction(ActionEvent event) {
        System.out.println("test");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/FrmUserAccounts.fxml"));
        Scene newScene = null;
        try {
            newScene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage inputStage = new Stage();
        inputStage.initOwner(primaryStage);
        inputStage.setScene(newScene);
        inputStage.show();
    }
}
