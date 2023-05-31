package lp.serviceimpl;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import lp.frontend.StartingDialog;
import lp.frontend.TextEnum;
import lp.service.DialogService;

import java.util.Optional;

public class DialogServiceImpl implements DialogService {

    private static DialogServiceImpl dialogService;
    private Alert dialog;


    private DialogServiceImpl() {

    }

    public static DialogServiceImpl getInstance() {
        if (dialogService == null) {
            dialogService = new DialogServiceImpl();
        }
        return dialogService;
    }

    @Override
    public void initTextInputDialog(String title, String message) {
        StartingDialog.setTitle(title);
        StartingDialog.setMessage(message);
        javafx.application.Application.launch(StartingDialog.class);
    }

    @Override
    public Optional<ButtonType> useConfirmDialog(String title, String message) {
        setDialog(Alert.AlertType.CONFIRMATION, title, message);
        dialog.getButtonTypes().clear();
        dialog.getButtonTypes().addAll(new ButtonType(TextEnum.YES.getText()), new ButtonType(TextEnum.NO.getText()));
        return dialog.showAndWait();
    }

    @Override
    public void useInformationDialog(String title, String message) {
        setDialog(Alert.AlertType.INFORMATION, title, message);
        dialog.show();
    }

    private void setDialog(Alert.AlertType type, String title, String message) {
        if (dialog == null) {
            dialog = new Alert(type);
        }
        dialog.setAlertType(type);
        dialog.setTitle(title);
        dialog.setHeaderText(message);
    }
}
