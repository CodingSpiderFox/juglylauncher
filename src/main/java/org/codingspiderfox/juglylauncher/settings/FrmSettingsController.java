package org.codingspiderfox.juglylauncher.settings;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class FrmSettingsController {

    @FXML
    public Button btnCancel;
    @FXML
    public Button btnSave;

    public void handleCancelButton(ActionEvent actionEvent) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}
