package lp.serviceimpl;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import lp.frontend.StartingDialog;
import lp.frontend.TextEnum;
import lp.service.DialogService;

import java.util.Optional;

public class DialogServiceImpl implements DialogService {

    //=======================SINGLETON=========================
    private static DialogServiceImpl dialogService;

    public static DialogServiceImpl getInstance() {
        if (dialogService == null) {
            dialogService = new DialogServiceImpl();
        }
        return dialogService;
    }

    private DialogServiceImpl() {

    }
    //=======================SINGLETON=========================


    //=======================ATTRIBUTES========================
    private Alert dialog;
    //=======================ATTRIBUTES========================


    //=======================METHODS===========================
    @Override
    public void initTextInputDialog(String title, String message) {
        StartingDialog.setTitle(title);
        StartingDialog.setMessage(message);
        javafx.application.Application.launch(StartingDialog.class);
    }

    @Override
    public void useInformationDialog(String title, String message) {
        setDialog(Alert.AlertType.INFORMATION, title, message);
        dialog.showAndWait();
    }

    @Override
    public void useErrorDialog(Exception exp) {
        exp.printStackTrace();
        StringBuilder stringBuilder = new StringBuilder(exp + "\n\n");
        for (StackTraceElement stackTraceElement : exp.getStackTrace()) {
            stringBuilder.append(stackTraceElement).append("\n");
        }
        setDialog(Alert.AlertType.ERROR, TextEnum.ERROR_TITLE.getText(), TextEnum.ERROR_MESSAGE.getText());
        dialog.getDialogPane().setContent(new TextArea(stringBuilder.toString()));
        dialog.showAndWait();
    }
    //=======================METHODS===========================


    //=======================RETURN METHODS====================
    @Override
    public String useConfirmDialog(String title, String message) {
        setDialog(Alert.AlertType.CONFIRMATION, title, message);
        dialog.getButtonTypes().clear();
        dialog.getButtonTypes().addAll(
                new ButtonType(TextEnum.YES_TEXT.getText()),
                new ButtonType(TextEnum.NO_TEXT.getText()));
        Optional<ButtonType> result = dialog.showAndWait();
        return result.map(ButtonType::getText).orElse(null);
    }

    @Override
    public String useTextInputDialog(String title, String message, String promptText) {
        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setTitle(title);
        textInputDialog.setHeaderText(message);
        textInputDialog.getEditor().setText(promptText);
        Optional<String> input;
        do {
            input = textInputDialog.showAndWait();
            if (input.isEmpty()) {
                return null;
            }
        } while (textInputDialog.getEditor().getText().isEmpty());
        return input.get();
    }

    @Override
    public Alert useDialog(String title, String message, Alert.AlertType type) {
        setDialog(type, title, message);
        dialog.show();
        return dialog;
    }

    //=======================RETURN METHODS====================


    //=======================PRIVATE METHODS===================
    private void setDialog(Alert.AlertType type, String title, String message) {
        dialog = new Alert(type);
        dialog.getButtonTypes().clear();
        dialog.getButtonTypes().addAll(new ButtonType(TextEnum.OK_TEXT.getText()));
        dialog.setAlertType(type);
        dialog.setTitle(title);
        dialog.setHeaderText(message);
    }
    //=======================PRIVATE METHODS===================
}
