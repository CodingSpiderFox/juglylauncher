package org.codingspiderfox.juglylauncher.accountmanager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class FrmUserAccountsController {
    @FXML
    public Button btnAdd;
    @FXML
    public Button btnDelete;
    @FXML
    public Button btnClose;
    @FXML
    public Button btnStandard;


    public void handleAddButton(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../../../../resources/fxml/FrmAddUser.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("ABC");
        stage.setScene(new Scene(root1));
        stage.show();
    }

    public void handleDeleteButton(ActionEvent actionEvent) {
    }

    public void handleCloseButton(ActionEvent actionEvent) {
    }

    public void handleStandardButton(ActionEvent actionEvent) {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}
