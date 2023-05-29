package lp.frontend;

import javafx.application.Application;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import lp.service.FileService;
import lp.serviceimpl.FileServiceImpl;

import java.io.File;
import java.util.Map;
import java.util.Optional;

public class Dialogs extends Application {

    private static String title;
    private static String message;

    public static void setTitle(String title) {
        Dialogs.title = title;
    }

    public static String getTitle() {
        return title;
    }

    public static void setMessage(String message) {
        Dialogs.message = message;
    }

    public static String getMessage() {
        return message;
    }

    private final FileService fileService = FileServiceImpl.getInstance();

    @Override
    public void start(Stage stage) throws Exception {
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
        fileService.setDataForJSON(textInputDialog.getEditor().getText(), TextEnum.IMPORT_FILE.getText());
        Map<String, Map<String, Map>> episode = fileService.loadJSON(TextEnum.IMPORT_FILE.getText(), Map.class);
        StartApp.setImportedEpisode(episode);
        StartApp.class.newInstance().start(new Stage());
    }
}
