package lp.frontend;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import lp.Manager;
import lp.business.dto.Episode;
import lp.service.DialogService;
import lp.service.FileService;
import lp.serviceimpl.DialogServiceImpl;
import lp.serviceimpl.FileServiceImpl;

import java.io.File;
import java.util.Optional;

public class StartingDialog extends Application {

    private final static DialogService dialogService = DialogServiceImpl.getInstance();

    private Manager manager = Manager.getInstance();
    private static String title;
    private static String message;

    private final FileService fileService = FileServiceImpl.getInstance();

    public static String getTitle() {
        return title;
    }

    public static void setTitle(String title) {
        StartingDialog.title = title;
    }

    public static String getMessage() {
        return message;
    }

    public static void setMessage(String message) {
        StartingDialog.message = message;
    }

    @Override
    public void start(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(TextEnum.INTRODUCE_DIALOG_TITLE.getText());
        alert.setHeaderText(TextEnum.INTRODUCE_DIALOG_TEXT.getText());
        alert.show();
        final String[] code = {new String(), TextEnum.INTRODUCE_DIALOG_LOADING_FROM_FILE_SYSTEM_CODE.getText(), TextEnum.INTRODUCE_DIALOG_LOADING_FILE_CODE.getText()};
        alert.getDialogPane().setOnKeyPressed(evt -> {
            code[0] += evt.getCode().toString();
            if (code[0].contains(code[1])) {
                alert.close();
                TextInputDialog textInputDialog = new TextInputDialog();
                textInputDialog.setTitle(getTitle());
                textInputDialog.getEditor().setText(TextEnum.DEFAULT_ROOT_PATH.getText());
                Optional<String> input;
                String nextProblem = new String();
                do {
                    textInputDialog.setHeaderText(getMessage() + nextProblem);
                    input = textInputDialog.showAndWait();
                    if (textInputDialog.getEditor().getText().isEmpty()) {
                        nextProblem = TextEnum.ADDITIONAL_TEXT_EMPTY_STRING.getText();
                    } else if (input.isPresent() && !new File(input.get()).exists()) {
                        nextProblem = TextEnum.ADDITIONAL_TEXT_FILE_NOT_FOUND.getText();
                    } else if (!input.isPresent()) {
                        System.exit(0);
                    }
                } while (textInputDialog.getEditor().getText().isEmpty() || !new File(textInputDialog.getEditor().getText()).exists());
                Episode episode = fileService.getEpisodeObjectFromFileSystem(textInputDialog.getEditor().getText());
                fileService.writeDataToJSON(TextEnum.IMPORT_FILE.getText(), episode);
                manager.setImportedEpisode(fileService.loadJSON(TextEnum.IMPORT_FILE.getText(), Episode.class));
                try {
                    MainAPP.class.newInstance().start(new Stage());
                } catch (Exception exp) {
                    dialogService.useErrorDialog(exp);
                }
            } else if (code[0].contains(code[2])) {
                alert.close();
                String path = dialogService.useTextInputDialog(TextEnum.LOAD_FILE_TITLE.getText(), TextEnum.LOAD_FILE_MESSAGE.getText(), TextEnum.EXPORT_FILE.getText() + TextEnum.EXPORT_FILE_SUFFIX.getText());
                if (path != null) {
                    String exportedFilePath = new File(path).getAbsolutePath();
                    manager.setImportedEpisode(fileService.loadJSON(exportedFilePath, Episode.class));
                    manager.setNoteText(fileService.getNoteFromJSON(exportedFilePath));
                    try {
                        MainAPP.class.newInstance().start(new Stage());
                    } catch (Exception exp) {
                        dialogService.useErrorDialog(exp);
                    }
                }
            }
        });
    }
}