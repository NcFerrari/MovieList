package lp.frontend;

import javafx.application.Application;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import lp.Manager;
import lp.business.dto.Episode;
import lp.service.FileService;
import lp.serviceimpl.FileServiceImpl;

import java.io.File;
import java.util.Optional;

public class StartingDialog extends Application {

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
        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setTitle(getTitle());
        textInputDialog.getEditor().setText(TextEnum.DEFAULT_ROOT_PATH.getText());
        Optional<String> input;
        String nextProblem = "";
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
        fileService.writeDataToJSON(textInputDialog.getEditor().getText(), TextEnum.IMPORT_FILE.getText());
        manager.setImportedEpisode(fileService.loadJSON(TextEnum.IMPORT_FILE.getText(), Episode.class));
        try {
            StartApp.class.newInstance().start(new Stage());
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }
}