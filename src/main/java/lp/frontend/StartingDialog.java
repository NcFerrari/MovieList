package lp.frontend;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import lp.Manager;
import lp.business.dto.Episode;
import lp.service.DialogService;
import lp.service.FileService;
import lp.serviceimpl.DialogServiceImpl;
import lp.serviceimpl.FileServiceImpl;

import java.io.File;

public class StartingDialog extends Application {

    //=======================STATIC ACCESS METHODS=============
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
    //=======================STATIC ACCESS METHODS=============

    //=======================ATTRIBUTES========================
    private final DialogService dialogService = DialogServiceImpl.getInstance();
    private final String[] code = {
            "",
            TextEnum.INTRODUCE_DIALOG_LOADING_FROM_FILE_SYSTEM_CODE.getText(),
            TextEnum.INTRODUCE_DIALOG_LOADING_FILE_CODE.getText()};
    private final FileService fileService = FileServiceImpl.getInstance();
    private final Manager manager = Manager.getInstance();

    private static String title;
    private static String message;
    //=======================ATTRIBUTES========================

    //=======================METHODS===========================

    /**
     * To use keyPressed event for dialog it's needed to start it as dialog.show()!
     * Not dialog.showAndWait()!
     * <p>
     * Stage is not used for now.
     */
    @Override
    public void start(Stage stage) {
        Alert alert = dialogService.useDialog(
                TextEnum.INTRODUCE_DIALOG_TITLE.getText(),
                TextEnum.INTRODUCE_DIALOG_TEXT.getText(),
                Alert.AlertType.WARNING);
        alert.getDialogPane().setOnKeyPressed(evt -> {
            code[0] += evt.getCode().toString();
            if (code[0].contains(code[1])) {
                alert.close();
                loadFilesFromSystem();
            } else if (code[0].contains(code[2])) {
                alert.close();
                loadJSONFile();
            }
        });
    }
    //=======================METHODS===========================


    //=======================RETURN METHODS====================
    //=======================RETURN METHODS====================


    //=======================PRIVATE METHODS===================
    private void loadFilesFromSystem() {
        String answer;
        String nextProblem = "";
        do {
            answer = dialogService.useTextInputDialog(
                    getTitle(),
                    getMessage() + nextProblem,
                    TextEnum.DEFAULT_ROOT_PATH.getText());
            if (answer == null) {
                nextProblem = TextEnum.ADDITIONAL_TEXT_EMPTY_STRING.getText();
            } else if (!new File(answer).exists()) {
                nextProblem = TextEnum.ADDITIONAL_TEXT_FILE_NOT_FOUND.getText();
            }
        } while (answer == null || !new File(answer).exists());

        Episode episode = fileService.getEpisodeObjectFromFileSystem(answer);
        fileService.writeDataToJSON(TextEnum.IMPORT_FILE.getText(), episode);
        manager.setImportedEpisode(fileService.loadJSON(TextEnum.IMPORT_FILE.getText(), Episode.class));
        runMainApplication();
    }

    private void loadJSONFile() {
        String path = dialogService.useTextInputDialog(
                TextEnum.LOAD_FILE_TITLE.getText(),
                TextEnum.LOAD_FILE_MESSAGE.getText(),
                TextEnum.EXPORT_FILE.getText() + TextEnum.EXPORT_FILE_SUFFIX.getText());
        if (path != null) {
            String exportedFilePath = new File(path).getAbsolutePath();
            manager.setImportedEpisode(fileService.loadJSON(exportedFilePath, Episode.class));
            manager.setNoteText(fileService.getNoteFromJSON(exportedFilePath));
            manager.loadData();
            runMainApplication();
        }
    }

    private void runMainApplication() {
        try {
            MainAPP.class.getDeclaredConstructor().newInstance().start(new Stage());
        } catch (Exception exp) {
            dialogService.useErrorDialog(exp);
        }
    }
    //=======================PRIVATE METHODS===================
}