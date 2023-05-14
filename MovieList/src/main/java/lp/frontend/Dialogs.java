package lp.frontend;

import javafx.application.Application;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

public class Dialogs extends Application {

    private static String title;
    private static String message;
    private static String inputResult;

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

    public static void setInputResult(String inputResult) {
        Dialogs.inputResult = inputResult;
    }

    public static String getInputResult() {
        return inputResult;
    }

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
            } else if (!new File(input.get()).exists()) {
                nextProblem = TextEnum.ADDITIONAL_TEXT_FILE_NOT_FOUND.getText();
            }
        } while (!input.isPresent() || textInputDialog.getEditor().getText().isEmpty() || !new File(input.get()).exists());
        setInputResult(input.get());
    }
}
