package lp.service;

import javafx.scene.control.Alert;

public interface DialogService {

    void initTextInputDialog(String title, String message);

    String useConfirmDialog(String title, String message);

    void useInformationDialog(String title, String message);

    Alert useDialog(String title, String message, Alert.AlertType type);

    void useErrorDialog(Exception exp);

    String useTextInputDialog(String title, String message, String promptText);
}
